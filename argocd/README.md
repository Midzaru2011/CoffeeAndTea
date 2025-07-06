# Coffee and Tea ArgoCD Configuration

Этот каталог содержит конфигурации ArgoCD для автоматического развертывания приложения Coffee and Tea в Kubernetes кластере через GitOps подход.

## Структура

```
argocd/
├── applications/          # ArgoCD Applications для разных окружений
│   ├── coffee-and-tea-dev.yaml
│   ├── coffee-and-tea-staging.yaml
│   └── coffee-and-tea-production.yaml
├── projects/             # ArgoCD Projects для управления доступом
│   └── coffee-and-tea-project.yaml
├── applicationsets/      # ApplicationSets для автоматизации
│   └── coffee-and-tea-appset.yaml
└── README.md

environments/             # Конфигурации окружений
├── development/
│   └── values.yaml
├── staging/
│   └── values.yaml
└── production/
    └── values.yaml
```

## Предварительные требования

1. **ArgoCD установлен в кластере**
   ```bash
   kubectl create namespace argocd
   kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
   ```

2. **Настроен доступ к Git репозиторию**
   ```bash
   argocd repo add https://github.com/your-org/coffee-and-tea.git --username <username> --password <token>
   ```

3. **Установлены зависимости Helm charts**
   ```bash
   helm repo add bitnami https://charts.bitnami.com/bitnami
   helm repo update
   ```

## Быстрое развертывание

### 1. Создание AppProject

```bash
kubectl apply -f argocd/projects/coffee-and-tea-project.yaml
```

### 2. Развертывание Development окружения

```bash
kubectl apply -f argocd/applications/coffee-and-tea-dev.yaml
```

### 3. Развертывание Staging окружения

```bash
kubectl apply -f argocd/applications/coffee-and-tea-staging.yaml
```

### 4. Развертывание Production окружения (ручная синхронизация)

```bash
kubectl apply -f argocd/applications/coffee-and-tea-production.yaml
# Синхронизация требует ручного подтверждения через UI или CLI
argocd app sync coffee-and-tea-production
```

## Окружения

### Development
- **Namespace**: `coffee-and-tea-dev`
- **URL**: `http://coffee-dev.local`
- **Автосинхронизация**: Включена
- **Ресурсы**: Минимальные (1 реплика, 256Mi RAM)
- **База данных**: PostgreSQL (5Gi)
- **Kafka**: Одна реплика (5Gi)
- **TLS**: Отключен
- **Istio**: Отключен

### Staging
- **Namespace**: `coffee-and-tea-staging`
- **URL**: `https://coffee-staging.example.com`
- **Автосинхронизация**: Включена
- **Ресурсы**: Средние (2 реплики, 1Gi RAM)
- **База данных**: PostgreSQL (20Gi, метрики)
- **Kafka**: 3 реплики (30Gi, метрики)
- **TLS**: Let's Encrypt staging
- **Istio**: Включен с circuit breaker

### Production
- **Namespace**: `coffee-and-tea-production`
- **URL**: `https://coffee.example.com`
- **Автосинхронизация**: Отключена (ручная синхронизация)
- **Ресурсы**: Максимальные (3-20 реплик, 2Gi RAM)
- **База данных**: PostgreSQL HA (100Gi, реплики, бэкапы)
- **Kafka**: 3 реплики HA (200Gi)
- **TLS**: Let's Encrypt production
- **Istio**: Полная конфигурация с мониторингом

## Управление доступом (RBAC)

### Роли в проекте

1. **coffee-and-tea-developer**
   - Доступ только к development окружению
   - Права: просмотр, синхронизация, логи

2. **coffee-and-tea-qa**
   - Доступ к staging окружению
   - Права: просмотр, синхронизация, действия, логи

3. **coffee-and-tea-devops**
   - Полный доступ ко всем окружениям
   - Права: все операции

### Настройка пользователей

Обновите `coffee-and-tea-project.yaml`, заменив `your-org` на название вашей организации:

```yaml
groups:
  - your-org:coffee-and-tea-developers
  - your-org:coffee-and-tea-qa
  - your-org:coffee-and-tea-devops
```

## ApplicationSet (опционально)

Для автоматического создания приложений используйте ApplicationSet:

```bash
kubectl apply -f argocd/applicationsets/coffee-and-tea-appset.yaml
```

ApplicationSet автоматически создаст приложения на основе:
- Структуры папок `environments/*/`
- Меток кластера для мультикластерного развертывания

## Мониторинг и уведомления

### Настройка уведомлений

1. Настройте ArgoCD Notifications:
   ```bash
   kubectl apply -f https://raw.githubusercontent.com/argoproj-labs/argocd-notifications/stable/manifests/install.yaml
   ```

2. Создайте ConfigMap с настройками Slack:
   ```yaml
   apiVersion: v1
   kind: ConfigMap
   metadata:
     name: argocd-notifications-cm
     namespace: argocd
   data:
     service.slack: |
       token: $slack-token
     template.app-deployed: |
       message: Application {{.app.metadata.name}} deployed to {{.app.spec.destination.namespace}}
     trigger.on-deployed: |
       - when: app.status.operationState.phase in ['Succeeded']
         send: [app-deployed]
   ```

### Метрики Prometheus

Приложения автоматически экспортируют метрики для Prometheus в staging и production окружениях.

## Безопасность

### Sync Windows

Production окружение имеет ограничения на синхронизацию:
- **Запрещено**: Воскресенье 2:00-3:00 (техническое обслуживание)
- **Разрешено**: Будние дни 9:00-17:00

### Network Policies

В production включены строгие Network Policies, ограничивающие трафик между подами.

### Secrets Management

⚠️ **Важно**: Замените все пароли и секреты на внешние системы управления секретами (Vault, External Secrets Operator).

## Команды ArgoCD CLI

### Основные операции

```bash
# Просмотр приложений
argocd app list

# Синхронизация приложения
argocd app sync coffee-and-tea-dev

# Просмотр статуса
argocd app get coffee-and-tea-production

# Просмотр логов
argocd app logs coffee-and-tea-staging

# Откат к предыдущей версии
argocd app rollback coffee-and-tea-production 123

# Принудительная синхронизация
argocd app sync coffee-and-tea-dev --force

# Удаление приложения
argocd app delete coffee-and-tea-dev
```

### Работа с проектами

```bash
# Просмотр проектов
argocd proj list

# Детали проекта
argocd proj get coffee-and-tea-project

# Добавление репозитория в проект
argocd proj add-source coffee-and-tea-project https://github.com/your-org/coffee-and-tea.git
```

## Устранение неполадок

### Проблемы с синхронизацией

1. **Проверьте статус приложения**:
   ```bash
   argocd app get coffee-and-tea-dev
   ```

2. **Просмотрите события**:
   ```bash
   kubectl get events -n coffee-and-tea-dev
   ```

3. **Проверьте логи ArgoCD**:
   ```bash
   kubectl logs -n argocd deployment/argocd-application-controller
   ```

### Проблемы с Helm

1. **Проверьте dependencies**:
   ```bash
   helm dependency list coffee-and-tea/
   ```

2. **Обновите dependencies**:
   ```bash
   helm dependency update coffee-and-tea/
   ```

3. **Валидация шаблонов**:
   ```bash
   helm template coffee-and-tea/ --values environments/development/values.yaml
   ```

### Проблемы с доступом

1. **Проверьте права пользователя**:
   ```bash
   argocd account can-i sync applications coffee-and-tea-project
   ```

2. **Проверьте проект**:
   ```bash
   argocd proj get coffee-and-tea-project
   ```

## Лучшие практики

1. **Всегда используйте фиксированные версии для production**
2. **Тестируйте изменения в development, затем staging**
3. **Используйте manual sync для production**
4. **Настройте уведомления для критичных окружений**
5. **Регулярно создавайте бэкапы конфигураций ArgoCD**
6. **Используйте external secrets для чувствительных данных**
7. **Мониторьте drift detection**

## Обновление версий

### Обычное обновление (dev/staging)
```bash
# Обновится автоматически при изменении в Git
git tag v2.1.3
git push origin v2.1.3
```

### Production обновление
```bash
# 1. Обновить конфигурацию
vim argocd/applications/coffee-and-tea-production.yaml
# Изменить targetRevision и image.tag

# 2. Применить изменения
kubectl apply -f argocd/applications/coffee-and-tea-production.yaml

# 3. Ручная синхронизация
argocd app sync coffee-and-tea-production
```

## Контакты

- **DevOps Team**: devops@example.com
- **Slack**: #coffee-and-tea-alerts
- **Documentation**: https://wiki.example.com/coffee-and-tea