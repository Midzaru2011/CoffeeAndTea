# Coffee and Tea Helm Chart

Helm chart для развертывания приложения Coffee and Tea с рекомендациями кофе на основе погоды в Kubernetes.

## Возможности

- ☕ **Приложение Coffee and Tea** с погодными рекомендациями кофе
- 🐘 **PostgreSQL** - встроенная база данных
- 🔄 **Apache Kafka** - обработка событий
- 🌐 **Istio Service Mesh** - управление трафиком и безопасность
- 📊 **Prometheus мониторинг** - метрики и наблюдаемость
- 🔧 **Автомасштабирование** - HPA для автоматического масштабирования
- 🛡️ **Безопасность** - SecurityContext, RBAC, NetworkPolicies

## Требования

- Kubernetes 1.19+
- Helm 3.8.0+
- Istio 1.15.0+ (опционально)
- Prometheus Operator (опционально, для мониторинга)

## Установка

### 1. Добавление репозиториев зависимостей

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

### 2. Установка зависимостей

```bash
cd coffee-and-tea
helm dependency build
```

### 3. Установка chart'а

#### Базовая установка
```bash
helm install coffee-and-tea ./coffee-and-tea \
  --namespace coffee-and-tea \
  --create-namespace
```

#### Установка с Istio
```bash
helm install coffee-and-tea ./coffee-and-tea \
  --namespace coffee-and-tea \
  --create-namespace \
  --set istio.enabled=true
```

#### Установка с внешней базой данных
```bash
helm install coffee-and-tea ./coffee-and-tea \
  --namespace coffee-and-tea \
  --create-namespace \
  --set postgresql.enabled=false \
  --set configMap.data.database.url="jdbc:postgresql://external-db:5432/coffeeandtea" \
  --set secret.data.database.password="your-password"
```

#### Установка с реальным Weather API ключом
```bash
helm install coffee-and-tea ./coffee-and-tea \
  --namespace coffee-and-tea \
  --create-namespace \
  --set externalServices.openWeatherMap.apiKey="your-openweathermap-api-key"
```

## Конфигурация

### Основные параметры

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `replicaCount` | Количество реплик приложения | `2` |
| `image.repository` | Docker образ приложения | `midzaru2011/coffeeandtea` |
| `image.tag` | Тег образа | `latest` |
| `service.type` | Тип сервиса | `ClusterIP` |
| `service.port` | Порт сервиса | `8081` |

### PostgreSQL

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `postgresql.enabled` | Включить PostgreSQL | `true` |
| `postgresql.auth.username` | Имя пользователя БД | `sasha` |
| `postgresql.auth.password` | Пароль БД | `sasha_password` |
| `postgresql.auth.database` | Имя базы данных | `CoffeeAndTea` |

### Kafka

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `kafka.enabled` | Включить Kafka | `true` |
| `kafka.broker.replicaCount` | Количество брокеров Kafka | `1` |
| `kafka.kraft.enabled` | Использовать KRaft вместо Zookeeper | `true` |

### Istio

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `istio.enabled` | Включить Istio интеграцию | `true` |
| `istio.gateway.enabled` | Создать Istio Gateway | `true` |
| `istio.virtualService.enabled` | Создать VirtualService | `true` |
| `istio.destinationRule.enabled` | Создать DestinationRule | `true` |

### Ingress

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `ingress.enabled` | Включить Ingress | `true` |
| `ingress.className` | Класс Ingress Controller | `nginx` |
| `ingress.hosts[0].host` | Хост для доступа | `coffee-and-tea.local` |

### Автомасштабирование

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `autoscaling.enabled` | Включить HPA | `false` |
| `autoscaling.minReplicas` | Минимум реплик | `2` |
| `autoscaling.maxReplicas` | Максимум реплик | `10` |
| `autoscaling.targetCPUUtilizationPercentage` | Целевая загрузка CPU | `80` |

### Мониторинг

| Параметр | Описание | Значение по умолчанию |
|----------|----------|-----------------------|
| `monitoring.enabled` | Включить мониторинг | `true` |
| `monitoring.serviceMonitor.enabled` | Создать ServiceMonitor | `true` |
| `monitoring.serviceMonitor.path` | Путь к метрикам | `/actuator/prometheus` |

## Использование

### Доступ к приложению

После установки приложение будет доступно по адресу:

- **С Istio**: `http://coffee-and-tea.local` (через Istio Gateway)
- **С Ingress**: `http://coffee-and-tea.local` (через Nginx Ingress)
- **Port-forward**: 
  ```bash
  kubectl port-forward -n coffee-and-tea svc/coffee-and-tea 8081:8081
  ```

### Основные эндпоинты

- **Главная страница**: `/`
- **Рекомендации кофе**: `/recommendations`
- **API рекомендаций**: `/recommendations/api/by-city?city=Moscow`
- **Health Check**: `/actuator/health`
- **Метрики**: `/actuator/prometheus`

### Примеры API запросов

```bash
# Получить рекомендацию кофе для Москвы
curl "http://coffee-and-tea.local/recommendations/api/by-city?city=Москва"

# Получить данные о погоде
curl "http://coffee-and-tea.local/recommendations/api/weather?city=Москва"

# Health check
curl "http://coffee-and-tea.local/actuator/health"
```

## Мониторинг и наблюдаемость

### Метрики Prometheus

Приложение экспортирует метрики в формате Prometheus:
- JVM метрики
- HTTP метрики
- Database connection pool метрики
- Custom business метрики

### Логи

Логи структурированы в JSON формате и включают:
- Request/Response логи
- Business logic логи
- Database операции
- Kafka события

### Distributed Tracing

При включенном Istio доступен distributed tracing:
- Jaeger integration
- Request flow визуализация
- Performance анализ

## Безопасность

### Pod Security

- Non-root пользователь
- Read-only root filesystem
- Dropped capabilities
- Security contexts

### Network Security

- NetworkPolicies для изоляции трафика
- TLS encryption с Istio
- Mutual TLS между сервисами

### Secrets Management

- Kubernetes Secrets для sensitive данных
- Отдельные секреты для БД, API ключей
- Encrypted storage

## Обновление

### Обновление приложения

```bash
helm upgrade coffee-and-tea ./coffee-and-tea \
  --namespace coffee-and-tea \
  --set image.tag=new-version
```

### Rolling updates

Chart настроен для rolling updates:
- MaxUnavailable: 25%
- MaxSurge: 25%
- PodDisruptionBudget: minAvailable=1

### Откат версии

```bash
helm rollback coffee-and-tea 1 --namespace coffee-and-tea
```

## Устранение неполадок

### Проверка статуса

```bash
# Статус релиза
helm status coffee-and-tea -n coffee-and-tea

# Pods статус
kubectl get pods -n coffee-and-tea

# Logs приложения
kubectl logs -f deployment/coffee-and-tea -n coffee-and-tea
```

### Отладка конфигурации

```bash
# Проверить rendered templates
helm template coffee-and-tea ./coffee-and-tea --debug

# Проверить values
helm get values coffee-and-tea -n coffee-and-tea
```

### Частые проблемы

1. **Приложение не стартует**: Проверьте доступность PostgreSQL и Kafka
2. **502 ошибки**: Проверьте readiness/liveness probes
3. **Connection refused**: Проверьте Service и EndPoints
4. **ImagePullBackOff**: Проверьте доступность Docker образа

## Удаление

```bash
# Удалить приложение
helm uninstall coffee-and-tea -n coffee-and-tea

# Удалить namespace (опционально)
kubectl delete namespace coffee-and-tea
```

## Лицензия

MIT License

## Поддержка

Для вопросов и поддержки обращайтесь к команде разработки.