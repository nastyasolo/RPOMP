# Android Java 

Учебные Android-приложения на Java 
Каждая папка — отдельный Android Studio проект.

## Содержание

| № | Папка | Что это | Стек |
|---|-------|---------|------|
| 1 | `RPOMP1` | Клиент-сервер: загрузка списка с сервера по JSON, настройки отображения, пагинация, CSV-экспорт | Volley, Gson, Glide, RecyclerView, OpenCSV |
| 2 | `RPOMP92` | Список товаров с выбором и корзиной, передача данных между Activity | ListView, BaseAdapter, Parcelable |
| 3 | `RPOMP103` | CRUD-заметки на SQLite, ViewPager с 4 вкладками (Show/Add/Del/Update) | SQLiteOpenHelper, FragmentPagerAdapter |
| 4 | `RPOMP134` | Асинхронная загрузка файла с сервера, прогресс, просмотр PDF, SharedPreferences | AsyncTask, HttpURLConnection, FileProvider, PopupWindow |
| 5 | `RPOMP145` | Выбор файла из хранилища: изображение в ImageView, аудио в MediaPlayer, видео в VideoView, управление скоростью | MediaPlayer, VideoView, SeekBar |
| 6 | `RPOMP166` | Пользовательские жесты, привязанные к действиям (фонарик, скриншот, плеер, звонок) | Android Gesture API |
| 7 | `RPOMP177` | Камера, видеоплеер, галерея с навигацией и масштабированием | Camera2 API, MediaPlayer, RecyclerView |
| 8 | `RPOMP208` | Карта с маркерами и маршрутом, сохранение между запусками | osmdroid, Play Services Location, SharedPreferences |

## Технологии

Java · Android SDK · SQLite · Volley · Gson · Glide · MediaPlayer · 
Camera2 API · osmdroid · SharedPreferences · RecyclerView · ViewPager · 
Android Gesture API

## Статус

Архив учебных Android-проектов на Java. Код сохранён в исходном виде, 
в котором сдавался. Проекты разрабатывались на разных версиях 
Android Studio и Gradle — для сборки на актуальном окружении может 
потребоваться обновление зависимостей.

## Требования

- Android Studio (актуальная версия)
- Android SDK под `compileSdk` конкретного проекта
- Эмулятор или физическое устройство
- Для сетевых проектов — локальный сервер или замена URL в коде

