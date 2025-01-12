# Image Resizer APP

Az átméretezéshez használandó eszközöket az ImageResizer interfészt implementálva lehet a rendszerbe beépíteni.
Ez lehetővé teszi a különböző képkezelők használatát.

A feltölthető képek mérete nem lehet nagyobb, mint 10MB.

## Kép átméretezéséhez szükséges csomagok telepítése

### ImageMagick

#### Windows

Töltsd le és telepítsd a hivatalos oldalról (https://imagemagick.org/script/download.php)

Linux:

sudo apt-get install imagemagick # Ubuntu/Debian

sudo yum install ImageMagick # CentOS/RHEL

## Adatbázis konfigurációja

    url: jdbc:postgresql://localhost:5432/image_resizer?
        createDatabaseIfNotExist=true&serverTimezone=UTC&characterEncoding=utf8
    username: postgres
    password: test1234
    driver-class-name: org.postgresql.Driver

## API dokumentáció

Az API dokumentációja megetekinthető
a [swaggerhub-on](https://app.swaggerhub.com/apis/PARANCSPETER/image-resizer_app/1.0.0)

Az alapjául szolgáló yaml fájl megtalálható az assets/api-docs.yaml fájlban.

Továbbá a swagger annotációk a kódban is dokumentálják az API-t.

## Postman

Postman környezetben a teszteléshez használható az assets mappában található `ImageResizer.postman_collection.json`
fájl.

## Tesztek, tesztlefedettség