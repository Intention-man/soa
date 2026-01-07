# Mule ESB Configuration

Эта конфигурация обеспечивает интеграцию между Navigator Service и Route Service через Mule ESB.

## Установка Mule ESB на Helios

1. Скачайте Mule ESB Runtime с официального сайта
2. Распакуйте в директорию `/opt/mule-esb` или аналогичную
3. Скопируйте файл `route-service-integration.xml` в директорию `apps/` Mule ESB
4. Запустите Mule ESB: `./bin/mule start`

## Конфигурация

- **HTTP Listener**: Порт 8081 для приема запросов от Navigator Service
- **SOAP Consumer**: Подключение к SOAP-сервису Route Service на порту 8080
- **REST Proxy**: Проксирование запросов к REST-прослойке Route Service

## Endpoints

- `/routes/*` - Проксирование запросов к SOAP-сервису
- `/proxy/routes/*` - Проксирование запросов от Navigator Service к REST-прослойке

