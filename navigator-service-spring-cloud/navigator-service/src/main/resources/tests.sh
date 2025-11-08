curl -kv -X POST \
  -H "Accept: application/xml" \
  "https://localhost:38090/navigator/route/add/3/19/300"

curl -kv -X GET \
  -H "Accept: application/xml" \
  "https://localhost:38090/navigator/routes/2/3/distance"
