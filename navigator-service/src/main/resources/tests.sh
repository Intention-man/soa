curl -v -X POST \
  -H "Accept: application/xml" \
  "https://localhost:18444/navigator-service/navigator/route/add/3/19/300"

curl -v -X GET \
  -H "Accept: application/xml" \
  "https://localhost:18444/navigator-service/navigator/routes/3/19/distance"
