#!/bin/bash
# Тестирование эндпоинтов Route Management Service

BASE_URL="http://localhost:8080/route-management-service/routes"

echo "1️⃣ Пагинация"
curl -s "${BASE_URL}?page=0&size=10" | xmllint --format -

echo "2️⃣ Сортировка по нескольким полям"
curl -s "${BASE_URL}?sort=distance,asc&sort=name,desc" | xmllint --format -

echo "3️⃣ Поиск по точному названию"
curl -s "${BASE_URL}?filter.name.equals=Route%20B" | xmllint --format -

echo "4️⃣ Поиск по подстроке"
curl -s "${BASE_URL}?filter.name.contains=Route" | xmllint --format -

echo "5️⃣ Фильтр по дистанции (равно)"
curl -s "${BASE_URL}?filter.distance.equals=123.45" | xmllint --format -

echo "6️⃣ Фильтр по дистанции (диапазон)"
curl -s "${BASE_URL}?filter.distance.min=100&filter.distance.max=500" | xmllint --format -

echo "7️⃣ Фильтр по координатам"
curl -s "${BASE_URL}?filter.coordinatesX.gt=50&filter.coordinatesY.lt=200" | xmllint --format -

echo "8️⃣ Фильтр по локациям"
curl -s "${BASE_URL}?filter.fromName.equals=Москва&filter.toName.contains=бург" | xmllint --format -

echo "9️⃣ Фильтр по дате создания"
curl -s "${BASE_URL}?filter.creationDate.gte=2024-01-01T00:00:00&filter.creationDate.lte=2024-12-31T23:59:59" | xmllint --format -

echo "✅ Все запросы выполнены."
