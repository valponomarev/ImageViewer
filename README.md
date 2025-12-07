# ImageViewer

Android приложение для просмотра изображений, построенное с использованием современных технологий и архитектурных паттернов.

## Описание

ImageViewer - это приложение для загрузки и просмотра изображений из удаленного источника. Приложение загружает список изображений с сервера, кэширует их локально и предоставляет удобный интерфейс для просмотра с возможностью масштабирования.

## Основные возможности

- Загрузка списка изображений с удаленного сервера
- Локальное кэширование изображений и данных
- Отображение изображений в виде сетки
- Просмотр изображений с возможностью масштабирования
- Автоматическая обработка ошибок и повторные попытки загрузки
- Обработка состояний отсутствия сети
- Оптимизированная загрузка превью изображений

## Архитектура

Проект использует **Clean Architecture** с модульной структурой:

### Модули

- **`app`** - Главный модуль приложения, точка входа
- **`core:domain`** - Бизнес-логика, модели и use cases
- **`core:data`** - Реализация репозиториев, API, база данных
- **`core:presentation`** - Общие UI компоненты и темы
- **`feature:images`** - Экран списка изображений
- **`feature:viewer`** - Экран просмотра изображения

### Технологический стек

- **Kotlin** - Основной язык программирования
- **Jetpack Compose** - Современный UI фреймворк
- **Hilt** - Dependency Injection
- **Room** - Локальная база данных
- **Retrofit** - HTTP клиент для сетевых запросов
- **OkHttp** - HTTP клиент с логированием
- **Coil** - Библиотека для загрузки и кэширования изображений
- **Navigation Compose** - Навигация между экранами
- **Coroutines** - Асинхронное программирование
- **KSP** - Kotlin Symbol Processing для генерации кода

## Требования

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java**: 11
- **Kotlin**: 2.0.21
- **Gradle**: 8.13.1

## Структура проекта

```
ImageViewer/
├── app/                    # Главный модуль приложения
│   ├── src/main/
│   │   ├── java/...        # MainActivity, Application
│   │   └── res/...         # Ресурсы
│   └── build.gradle.kts
├── core/
│   ├── domain/             # Доменный слой
│   │   └── src/main/java/.../domain/
│   │       ├── model/      # Модели данных
│   │       ├── repository/ # Интерфейсы репозиториев
│   │       └── usecase/    # Use cases
│   ├── data/               # Слой данных
│   │   └── src/main/java/.../data/
│   │       ├── di/         # Dependency Injection модули
│   │       ├── local/      # Room база данных
│   │       ├── remote/    # Retrofit API
│   │       ├── mapper/     # Мапперы данных
│   │       └── repository/ # Реализация репозиториев
│   └── presentation/       # Общие UI компоненты
│       └── src/main/java/.../presentation/
│           └── theme/      # Тема приложения
├── feature/
│   ├── images/             # Модуль списка изображений
│   │   └── src/main/java/.../feature/images/
│   │       ├── compose/    # Compose компоненты
│   │       ├── ImagesScreen.kt
│   │       ├── ImagesViewModel.kt
│   │       └── ImagesUiState.kt
│   └── viewer/             # Модуль просмотра изображения
│       └── src/main/java/.../feature/viewer/
│           ├── compose/    # Compose компоненты
│           ├── ImageViewerScreen.kt
│           └── ImageViewerViewModel.kt
├── gradle/
│   └── libs.versions.toml  # Версии зависимостей
├── build.gradle.kts        # Корневой build файл
└── settings.gradle.kts     # Настройки проекта
```

## Конфигурация

### API Endpoint

Приложение использует следующий endpoint для загрузки списка изображений:
- **Base URL**: `https://it-link.ru/`
- **Images File**: `https://it-link.ru/test/images.txt`

