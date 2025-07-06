#!/bin/bash

# Coffee and Tea ArgoCD Deployment Script
# Автоматическое развертывание приложения через ArgoCD

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE_ARGOCD="argocd"
APP_NAME="coffee-and-tea"
GIT_REPO="${GIT_REPO:-https://github.com/your-org/coffee-and-tea.git}"
ARGOCD_SERVER="${ARGOCD_SERVER:-localhost:8080}"

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_info "Проверка предварительных требований..."
    
    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl не найден. Установите kubectl."
        exit 1
    fi
    
    # Check argocd CLI
    if ! command -v argocd &> /dev/null; then
        log_warning "argocd CLI не найден. Установка..."
        install_argocd_cli
    fi
    
    # Check helm
    if ! command -v helm &> /dev/null; then
        log_error "helm не найден. Установите Helm."
        exit 1
    fi
    
    # Check kustomize
    if ! command -v kustomize &> /dev/null; then
        log_warning "kustomize не найден. Установка..."
        install_kustomize
    fi
    
    log_success "Все предварительные требования выполнены"
}

# Install ArgoCD CLI
install_argocd_cli() {
    local os=$(uname -s | tr '[:upper:]' '[:lower:]')
    local arch=$(uname -m)
    
    case $arch in
        x86_64) arch="amd64" ;;
        armv7l) arch="arm" ;;
        aarch64) arch="arm64" ;;
    esac
    
    local version="v2.8.4"
    local url="https://github.com/argoproj/argo-cd/releases/download/${version}/argocd-${os}-${arch}"
    
    log_info "Загрузка ArgoCD CLI ${version}..."
    curl -sSL -o argocd "$url"
    chmod +x argocd
    sudo mv argocd /usr/local/bin/
    log_success "ArgoCD CLI установлен"
}

# Install Kustomize
install_kustomize() {
    curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh" | bash
    sudo mv kustomize /usr/local/bin/
    log_success "Kustomize установлен"
}

# Check if ArgoCD is installed
check_argocd_installation() {
    log_info "Проверка установки ArgoCD..."
    
    if ! kubectl get namespace "$NAMESPACE_ARGOCD" &> /dev/null; then
        log_warning "ArgoCD не установлен. Установка..."
        install_argocd
    else
        log_success "ArgoCD уже установлен"
    fi
}

# Install ArgoCD
install_argocd() {
    log_info "Установка ArgoCD..."
    
    kubectl create namespace "$NAMESPACE_ARGOCD" --dry-run=client -o yaml | kubectl apply -f -
    kubectl apply -n "$NAMESPACE_ARGOCD" -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
    
    log_info "Ожидание готовности ArgoCD..."
    kubectl wait --for=condition=available --timeout=300s deployment/argocd-server -n "$NAMESPACE_ARGOCD"
    
    log_success "ArgoCD установлен"
}

# Setup ArgoCD access
setup_argocd_access() {
    log_info "Настройка доступа к ArgoCD..."
    
    # Get initial admin password
    local admin_password=$(kubectl -n "$NAMESPACE_ARGOCD" get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
    
    # Port forward for access (in background)
    kubectl port-forward svc/argocd-server -n "$NAMESPACE_ARGOCD" 8080:443 &
    local port_forward_pid=$!
    
    sleep 5  # Wait for port forward to establish
    
    # Login to ArgoCD
    log_info "Вход в ArgoCD..."
    argocd login "$ARGOCD_SERVER" --username admin --password "$admin_password" --insecure
    
    # Kill port forward
    kill $port_forward_pid 2>/dev/null || true
    
    log_success "Настройка доступа завершена"
    log_info "Admin password: $admin_password"
}

# Add Git repository
add_git_repository() {
    log_info "Добавление Git репозитория..."
    
    if argocd repo list | grep -q "$GIT_REPO"; then
        log_info "Репозиторий уже добавлен"
    else
        argocd repo add "$GIT_REPO" --type git
        log_success "Git репозиторий добавлен"
    fi
}

# Validate ArgoCD manifests
validate_manifests() {
    log_info "Валидация ArgoCD манифестов..."
    
    # Check if manifests directory exists
    if [[ ! -d "argocd" ]]; then
        log_error "Каталог argocd не найден"
        exit 1
    fi
    
    # Validate with kubectl dry-run
    kubectl apply --dry-run=client -k argocd/
    
    # Validate individual files
    for file in argocd/projects/*.yaml argocd/applications/*.yaml; do
        if [[ -f "$file" ]]; then
            kubectl apply --dry-run=client -f "$file"
        fi
    done
    
    log_success "Валидация манифестов успешна"
}

# Deploy ArgoCD applications
deploy_applications() {
    log_info "Развертывание ArgoCD приложений..."
    
    # Apply with kustomize
    kubectl apply -k argocd/
    
    log_success "ArgoCD приложения развернуты"
}

# Wait for applications to sync
wait_for_sync() {
    local app_name="$1"
    local timeout="${2:-300}"
    
    log_info "Ожидание синхронизации приложения $app_name..."
    
    local end_time=$((SECONDS + timeout))
    while [[ $SECONDS -lt $end_time ]]; do
        local status=$(argocd app get "$app_name" -o json | jq -r '.status.sync.status' 2>/dev/null || echo "Unknown")
        local health=$(argocd app get "$app_name" -o json | jq -r '.status.health.status' 2>/dev/null || echo "Unknown")
        
        if [[ "$status" == "Synced" && "$health" == "Healthy" ]]; then
            log_success "Приложение $app_name синхронизировано и работает"
            return 0
        fi
        
        log_info "Статус: $status, Здоровье: $health. Ожидание..."
        sleep 10
    done
    
    log_error "Тайм-аут ожидания синхронизации приложения $app_name"
    return 1
}

# Check application status
check_app_status() {
    local app_name="$1"
    
    log_info "Проверка статуса приложения $app_name..."
    
    if argocd app get "$app_name" &> /dev/null; then
        argocd app get "$app_name"
        return 0
    else
        log_error "Приложение $app_name не найдено"
        return 1
    fi
}

# Sync application
sync_application() {
    local app_name="$1"
    local force="${2:-false}"
    
    log_info "Синхронизация приложения $app_name..."
    
    if [[ "$force" == "true" ]]; then
        argocd app sync "$app_name" --force
    else
        argocd app sync "$app_name"
    fi
    
    wait_for_sync "$app_name"
}

# Cleanup function
cleanup() {
    log_info "Очистка ресурсов..."
    # Kill any remaining port forwards
    pkill -f "kubectl port-forward" 2>/dev/null || true
}

# Usage information
usage() {
    cat << EOF
Использование: $0 [КОМАНДА] [ОПЦИИ]

КОМАНДЫ:
    install     Полная установка ArgoCD и развертывание приложений
    deploy      Развертывание только приложений (ArgoCD уже установлен)
    sync        Синхронизация конкретного приложения
    status      Проверка статуса приложения
    validate    Валидация манифестов без развертывания
    cleanup     Удаление всех ресурсов

ОПЦИИ:
    -h, --help              Показать справку
    -a, --app NAME          Имя приложения для операций
    -f, --force             Принудительная синхронизация
    -r, --repo URL          URL Git репозитория
    -s, --server URL        ArgoCD server URL

ПРИМЕРЫ:
    $0 install                                   # Полная установка
    $0 deploy                                    # Только развертывание
    $0 sync -a coffee-and-tea-dev               # Синхронизация dev окружения
    $0 status -a coffee-and-tea-production      # Статус production
    $0 validate                                  # Валидация манифестов

EOF
}

# Main execution
main() {
    local command=""
    local app_name=""
    local force="false"
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            install|deploy|sync|status|validate|cleanup)
                command="$1"
                shift
                ;;
            -a|--app)
                app_name="$2"
                shift 2
                ;;
            -f|--force)
                force="true"
                shift
                ;;
            -r|--repo)
                GIT_REPO="$2"
                shift 2
                ;;
            -s|--server)
                ARGOCD_SERVER="$2"
                shift 2
                ;;
            -h|--help)
                usage
                exit 0
                ;;
            *)
                log_error "Неизвестная опция: $1"
                usage
                exit 1
                ;;
        esac
    done
    
    # Setup cleanup trap
    trap cleanup EXIT
    
    # Execute command
    case "$command" in
        install)
            check_prerequisites
            check_argocd_installation
            setup_argocd_access
            add_git_repository
            validate_manifests
            deploy_applications
            
            # Sync development environment automatically
            log_info "Автоматическая синхронизация development окружения..."
            sync_application "coffee-and-tea-dev"
            ;;
            
        deploy)
            check_prerequisites
            validate_manifests
            deploy_applications
            ;;
            
        sync)
            if [[ -z "$app_name" ]]; then
                log_error "Укажите имя приложения с -a/--app"
                exit 1
            fi
            sync_application "$app_name" "$force"
            ;;
            
        status)
            if [[ -z "$app_name" ]]; then
                log_error "Укажите имя приложения с -a/--app"
                exit 1
            fi
            check_app_status "$app_name"
            ;;
            
        validate)
            validate_manifests
            ;;
            
        cleanup)
            log_warning "Удаление всех ArgoCD приложений..."
            kubectl delete -k argocd/ --ignore-not-found=true
            log_success "Очистка завершена"
            ;;
            
        "")
            log_error "Укажите команду"
            usage
            exit 1
            ;;
            
        *)
            log_error "Неизвестная команда: $command"
            usage
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"