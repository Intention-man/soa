# Изменения для лабораторной работы #4

## Обзор изменений

Route Management Service переписан с REST (JAX-RS) на SOAP (JAX-WS). Создана REST-прослойка для обратной совместимости с клиентским приложением. Настроена интеграция через Mule ESB.

## Структура проекта

### 1. Route Service (SOAP)
- **Модуль**: `route-web`
- **Эндпоинт**: `/route-web/RouteSoapService`
- **Протокол**: SOAP 1.2
- **Интерфейс**: `RouteSoapService` (JAX-WS)
- **Реализация**: `RouteSoapServiceImpl`

### 2. REST-прослойка
- **Модуль**: `route-rest-proxy`
- **Эндпоинт**: `/route-management-service/api/routes/*`
- **Протокол**: REST (JAX-RS)
- **Функция**: Вызывает SOAP-сервис и возвращает REST-ответы
- **Ресурс**: `RouteProxyResource`

### 3. Mule ESB конфигурация
- **Файл**: `mule-esb-config/route-service-integration.xml`
- **Порт**: 8081
- **Функция**: Интеграция между Navigator Service и Route Service

## Развертывание

### Route Service (EAR)
EAR архив включает:
- `route-ejb` - бизнес-логика и работа с БД
- `route-web` - SOAP-сервис
- `route-rest-proxy` - REST-прослойка

Развертывание на WildFly:
```bash
cd route-service-javaee/route-ear
mvn clean install
# Скопировать route-ear-1.0-SNAPSHOT.ear в WildFly deployments
```

### Mule ESB
1. Установить Mule ESB на Helios
2. Скопировать `mule-esb-config/route-service-integration.xml` в `apps/` директорию Mule ESB
3. Запустить Mule ESB

## API Endpoints

### SOAP Service
- WSDL: `http://localhost:8080/route-web/RouteSoapService?wsdl`
- Namespace: `http://example.com/routeservice/soap`

### REST Proxy (для клиентского приложения)
- `GET /route-management-service/api/routes` - получить список маршрутов
- `GET /route-management-service/api/routes/{id}` - получить маршрут по ID
- `POST /route-management-service/api/routes` - создать маршрут
- `PUT /route-management-service/api/routes/{id}` - обновить маршрут
- `DELETE /route-management-service/api/routes/{id}` - удалить маршрут
- `GET /route-management-service/api/routes/distance/sum` - сумма расстояний
- `GET /route-management-service/api/routes/distance/group` - группировка по расстоянию
- `GET /route-management-service/api/routes/distance/greater-than?minDistance=X` - маршруты с расстоянием больше X
- `POST /route-management-service/api/routes/add/{idFrom}/{idTo}/{distance}` - создать маршрут между локациями

## Navigator Service

Navigator Service **не изменен** и продолжает работать как раньше. Он вызывает REST-эндпоинты, которые теперь обрабатываются REST-прослойкой, которая в свою очередь вызывает SOAP-сервис.

## Переменные окружения

Для REST-прослойки:
- `SOAP_SERVICE_URL` - URL SOAP-сервиса (по умолчанию: `http://localhost:8080/route-web/RouteSoapService`)

