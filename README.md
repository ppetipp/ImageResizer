# Image Resizer APP

# Telepítés

# Kép átméretezéséhez szükséges csomagok telepítése

# Adatbázis konfigurációja

    url: jdbc:postgresql://localhost:5432/image_resizer?
        createDatabaseIfNotExist=true&serverTimezone=UTC&characterEncoding=utf8
    username: postgres
    password: test1234
    driver-class-name: org.postgresql.Driver

# API dokumentáció

Az API dokumentációja megetekinthető
a [swaggerhub-on](https://app.swaggerhub.com/apis/PARANCSPETER/image-resizer_app/1.0.0)

Az alapjául szolgáló yaml fájl megtalálható az assets/api-docs.yaml fájlban.

Továbbá a swagger annotációk a kódban is dokumentálják az API-t.

# Postman

# Tesztek, tesztlefedettség