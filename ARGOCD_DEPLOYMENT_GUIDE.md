# 🚀 Coffee and Tea - ArgoCD Deployment Guide

Полное руководство по развертыванию приложения Coffee and Tea в Kubernetes кластере через ArgoCD и GitOps.

## 📋 Обзор

Создана полная инфраструктура для автоматического развертывания приложения Coffee and Tea с использованием:
- **ArgoCD** для GitOps развертывания
- **Helm Charts** для шаблонизации Kubernetes ресурсов  
- **Multi-environment** поддержка (dev, staging, production)
- **CI/CD** с GitHub Actions
- **Security** и мониторинг

## 🏗️ Архитектура

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GitHub Repo   │    │     ArgoCD      │    │   Kubernetes    │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │ Source Code │ │    │ │Applications │ │    │ │ Development │ │
│ │ Helm Charts │ │───▶│ │  Projects   │ │───▶│ │   Staging   │ │
│ │ArgoCD Config│ │    │ │AppSets      │ │    │ │ Production  │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📁 Структура проекта

```
├── argocd/                           # ArgoCD конфигурации
│   ├── applications/                 # ArgoCD Applications
│   │   ├── coffee-and-tea-dev.yaml
│   │   ├── coffee-and-tea-staging.yaml
│   │   └── coffee-and-tea-production.yaml
│   ├── projects/                     # ArgoCD Projects  
│   │   └── coffee-and-tea-project.yaml
│   ├── applicationsets/              # ApplicationSets
│   │   └── coffee-and-tea-appset.yaml
│   ├── kustomization.yaml           # Kustomize для удобного развертывания
│   └── README.md                    # Документация ArgoCD
│
├── environments/                     # Конфигурации окружений
│   ├── development/
│   │   └── values.yaml              # Dev настройки
│   ├── staging/  
│   │   └── values.yaml              # Staging настройки
│   └── production/
│       └── values.yaml              # Production настройки
│
├── coffee-and-tea/                   # Helm Chart (уже создан)
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
│
├── scripts/
│   └── deploy-argocd.sh             # Автоматизация развертывания
│
├── .github/workflows/
│   └── gitops.yml                   # GitHub Actions CI/CD
│
└── ARGOCD_DEPLOYMENT_GUIDE.md       # Этот файл
```

## 🚀 Быстрое развертывание

### 1. Предварительные требования

```bash
# Установка kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl && sudo mv kubectl /usr/local/bin/

# Установка Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Установка ArgoCD CLI  
curl -sSL -o argocd https://github.com/argoproj/argo-cd/releases/download/v2.8.4/argocd-linux-amd64
chmod +x argocd && sudo mv argocd /usr/local/bin/
```

### 2. Автоматическая установка

```bash
# Полная установка ArgoCD + развертывание приложений
./scripts/deploy-argocd.sh install

# Или пошагово:
./scripts/deploy-argocd.sh validate        # Валидация манифестов
./scripts/deploy-argocd.sh deploy          # Развертывание приложений
./scripts/deploy-argocd.sh sync -a coffee-and-tea-dev  # Синхронизация dev
```

### 3. Ручная установка

```bash
# 1. Установка ArgoCD
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 2. Доступ к ArgoCD UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# 3. Получение пароля admin
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d

# 4. Развертывание приложений
kubectl apply -k argocd/
```

## 🏢 Окружения

### Development Environment
- **Namespace**: `coffee-and-tea-dev`
- **URL**: `http://coffee-dev.local`
- **Автосинхронизация**: ✅ Включена
- **Ресурсы**: Минимальные (1 реплика, 256Mi RAM)
- **База данных**: PostgreSQL (5Gi)
- **Kafka**: 1 реплика (5Gi)
- **TLS**: ❌ Отключен
- **Istio**: ❌ Отключен
- **Мониторинг**: ❌ Отключен

### Staging Environment  
- **Namespace**: `coffee-and-tea-staging`
- **URL**: `https://coffee-staging.example.com`
- **Автосинхронизация**: ✅ Включена
- **Ресурсы**: Средние (2-5 реплик, 1Gi RAM)
- **База данных**: PostgreSQL HA (20Gi, метрики)
- **Kafka**: 3 реплики (30Gi, метрики)
- **TLS**: ✅ Let's Encrypt staging
- **Istio**: ✅ Circuit breaker, retries
- **Мониторинг**: ✅ Prometheus, alerting

### Production Environment
- **Namespace**: `coffee-and-tea-production`
- **URL**: `https://coffee.example.com`
- **Автосинхронизация**: ❌ Ручная синхронизация
- **Ресурсы**: Максимальные (3-20 реплик, 2Gi RAM)
- **База данных**: PostgreSQL HA + read replicas (100Gi, бэкапы)
- **Kafka**: 3 реплики HA (200Gi)
- **TLS**: ✅ Let's Encrypt production
- **Istio**: ✅ Полная конфигурация
- **Мониторинг**: ✅ Полный мониторинг + alerting

## 🔐 Безопасность и RBAC

### ArgoCD Project Roles

| Роль | Доступ | Права |
|------|--------|-------|
| `coffee-and-tea-developer` | Development | Просмотр, синхронизация, логи |
| `coffee-and-tea-qa` | Staging | Просмотр, синхронизация, действия |
| `coffee-and-tea-devops` | Все окружения | Полный доступ |

### Sync Windows
- **Production**: Запрещена синхронизация в воскресенье 2:00-3:00
- **Production**: Разрешена только в рабочие часы (9:00-17:00, пн-пт)

### Network Policies
- Строгие ограничения трафика в production
- Изоляция между namespace'ами
- Разрешен трафик только к необходимым сервисам

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
# Триггеры
develop branch   → Автоматическое развертывание в Development
main branch      → Автоматическое развертывание в Staging  
git tag v*       → Подготовка Production (ручная синхронизация)
```

### Pipeline Stages

1. **Build & Test** - Компиляция Java, тесты, безопасность
2. **Docker Build** - Сборка и публикация образов
3. **Validation** - Валидация Helm charts и ArgoCD манифестов
4. **Security Scan** - Trivy сканирование уязвимостей
5. **Deploy** - Автоматическое развертывание через ArgoCD
6. **Notify** - Уведомления в Slack

## 📊 Мониторинг

### Prometheus Metrics
- Application performance metrics
- JVM memory usage
- HTTP request rates and latency
- Database connection pools
- Kafka consumer lag

### Alerting Rules
- High CPU usage (>80%)
- High memory usage (>85%)  
- High error rate (>5%)
- Pod restart frequency
- Service unavailability

## 🛠️ Управление

### Основные команды ArgoCD

```bash
# Просмотр приложений
argocd app list

# Синхронизация
argocd app sync coffee-and-tea-dev

# Проверка статуса
argocd app get coffee-and-tea-production

# Просмотр логов
argocd app logs coffee-and-tea-staging

# Откат к предыдущей версии
argocd app rollback coffee-and-tea-production 123
```

### Обновление версий

```bash
# Development/Staging - автоматически через GitHub Actions
git push origin develop  # → развертывание в dev
git push origin main     # → развертывание в staging

# Production - через Git tags
git tag v2.1.3
git push origin v2.1.3  # → подготовка production (ручная синхронизация)
```

## 🚨 Устранение неполадок

### Проблемы с синхронизацией

```bash
# Проверка статуса приложения
argocd app get coffee-and-tea-dev

# Просмотр событий Kubernetes
kubectl get events -n coffee-and-tea-dev

# Принудительная синхронизация
argocd app sync coffee-and-tea-dev --force
```

### Проблемы с Helm

```bash
# Валидация Helm chart
helm template coffee-and-tea/ --values environments/development/values.yaml

# Обновление зависимостей
helm dependency update coffee-and-tea/
```

### Проблемы с доступом

```bash
# Проверка прав пользователя
argocd account can-i sync applications coffee-and-tea-project

# Проверка конфигурации проекта
argocd proj get coffee-and-tea-project
```

## 📈 Масштабирование

### Горизонтальное автомасштабирование
- **Development**: Отключено
- **Staging**: 2-5 реплик (CPU 70%, Memory 80%)
- **Production**: 3-20 реплик (CPU 60%, Memory 70%)

### Вертикальное масштабирование
- Автоматическая настройка лимитов ресурсов
- JVM tuning для production
- Database connection pooling

## 🔗 Полезные ссылки

- [ArgoCD Documentation](https://argo-cd.readthedocs.io/)
- [Helm Documentation](https://helm.sh/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Istio Service Mesh](https://istio.io/latest/docs/)
- [Prometheus Monitoring](https://prometheus.io/docs/)

## 📞 Поддержка

- **DevOps Team**: devops@example.com
- **Slack**: #coffee-and-tea-alerts
- **Issue Tracker**: GitHub Issues
- **Documentation**: Internal Wiki

---

## ✅ Чек-лист развертывания

- [ ] ArgoCD установлен и настроен
- [ ] Git репозиторий добавлен в ArgoCD
- [ ] AppProject создан и настроен RBAC
- [ ] Applications развернуты для всех окружений
- [ ] Development окружение работает
- [ ] Staging окружение работает  
- [ ] Production конфигурация подготовлена
- [ ] Мониторинг и alerting настроены
- [ ] Уведомления в Slack работают
- [ ] CI/CD pipeline протестирован
- [ ] Документация актуальна
- [ ] Команда обучена работе с ArgoCD

🎉 **Готово!** Ваше приложение Coffee and Tea готово к производственному использованию с полной автоматизацией через GitOps!