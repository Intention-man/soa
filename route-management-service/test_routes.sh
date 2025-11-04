#!/bin/bash
# Тестирование полного набора эндпоинтов Route Management Service

BASE_URL="https://localhost:28443/route-management-service/routes"
CONTENT_TYPE="application/xml"

echo "🧹 Очистка экрана..."
clear

echo "=============================="
echo "🚀 Тест: СОЗДАНИЕ новой сущности"
echo "=============================="

CREATE_BODY='<?xml version="1.0" encoding="UTF-8"?>
<RouteCreateRequest>
    <name>Route Test A</name>
    <coordinates>
        <x>123</x>
        <y>678</y>
    </coordinates>
    <fromLocation>
        <name>Moscow</name>
        <x>55</x>
        <y>37</y>
    </fromLocation>
    <toLocation>
        <name>Saint Petersburg</name>
        <x>59</x>
        <y>30</y>
    </toLocation>
    <distance>450.5</distance>
</RouteCreateRequest>'


CREATE_RESPONSE=$(curl -ks -D - -o /dev/null -X POST "$BASE_URL" \
    -H "Content-Type: $CONTENT_TYPE" \
    -d "$CREATE_BODY")

LOCATION=$(echo "$CREATE_RESPONSE" | grep -i "^Location:" | awk '{print $2}' | tr -d '\r\n')
NEW_ID=$(basename "$LOCATION")

echo "Создан Route ID из Location: $NEW_ID"

if [ -z "$NEW_ID" ]; then
  echo "❌ Ошибка: не удалось получить ID созданного маршрута"
  exit 1
fi

echo
echo "=============================="
echo "📦 Тест: ПОЛУЧЕНИЕ маршрута по ID"
echo "=============================="
curl -ks -X GET "$BASE_URL/$NEW_ID" | xmllint --format -

echo
echo "=============================="
echo "🧩 Тест: ОБНОВЛЕНИЕ маршрута"
echo "=============================="

UPDATE_BODY='<?xml version="1.0" encoding="UTF-8"?>
<RouteUpdateRequest>
    <name>Route Test A - Updated</name>
    <distance>999.9</distance>
</RouteUpdateRequest>'

RESPONSE=$(curl -ks -X PUT "$BASE_URL/$NEW_ID" \
    -H "Content-Type: $CONTENT_TYPE" \
    -d "$UPDATE_BODY")

if [ -z "$RESPONSE" ]; then
    echo "Маршрут обновлён, тело ответа пустое"
else
    echo "$RESPONSE" | xmllint --format -
fi

echo
echo "=============================="
echo "🔢 Тест: ПОЛУЧЕНИЕ всех маршрутов (страница 0, 10 элементов)"
echo "=============================="
curl -ks "$BASE_URL?page=0&size=10" | xmllint --format -

echo
echo "=============================="
echo "🔍 Тест: ФИЛЬТРЫ и СОРТИРОВКА"
echo "=============================="

echo "➡️ Поиск по подстроке 'Route'"
curl -ks "$BASE_URL?filter.name.contains=Route" | xmllint --format -

echo
echo "➡️ Фильтр по дистанции > 100"
curl -s "$BASE_URL?filter.distance.min=100" | xmllint --format -

echo
echo "➡️ Сортировка по distance ASC"
curl -ks "$BASE_URL?sort=distance,asc" | xmllint --format -

echo
echo "=============================="
echo "📊 Тест: /distance/sum"
echo "=============================="
curl -ks "$BASE_URL/distance/sum" | xmllint --format -

echo
echo "=============================="
echo "📈 Тест: /distance/group"
echo "=============================="
curl -ks "$BASE_URL/distance/group" | xmllint --format -

echo
echo "=============================="
echo "🏁 Тест: /distance/greater-than?minDistance=300"
echo "=============================="
curl -ks "$BASE_URL/distance/greater-than?minDistance=300" | xmllint --format -

echo
echo "=============================="
echo "🗑️ Тест: УДАЛЕНИЕ маршрута"
echo "=============================="
curl -ks -X DELETE "$BASE_URL/$NEW_ID" -w "\nHTTP code: %{http_code}\n"

echo
echo "=============================="
echo "✅ ВСЕ ЗАПРОСЫ ВЫПОЛНЕНЫ"
echo "=============================="
