# Night Story 🌙✨

A magical Android app that creates bedtime stories for kids using AI.

## Features

- **AI Story Generation** — powered by Google Gemini (free API key)
- **Read Aloud** — stories read aloud using Android's built-in text-to-speech
- **Story Library** — save and browse all your generated stories
- **15 Languages** — English, Spanish, French, Arabic, Persian, and more
- **10 Story Styles** — Fairy Tale, Adventure, Funny, Space, Dinosaurs, and more
- **Secure Storage** — API keys stored with encryption

## Setup

### 1. Get a Free Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/apikey)
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Copy the key

### 2. Install the App

**Option A: Build with Android Studio**
1. Clone this repo
2. Open in Android Studio
3. Run on device/emulator

**Option B: GitHub Actions APK**
1. Push to GitHub
2. Go to Actions tab → latest build
3. Download the APK from Artifacts

### 3. Configure

1. Open the app
2. Go to **Settings** tab
3. Paste your Gemini API key
4. Choose your preferred language and story style
5. Tap **Save**

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **API:** Google Gemini (generativelanguage.googleapis.com)
- **TTS:** Android TextToSpeech engine
- **Database:** Room
- **Security:** EncryptedSharedPreferences
- **Architecture:** MVVM

## Project Structure

```
app/src/main/java/com/nightstory/app/
├── data/
│   ├── api/          # Gemini API service & models
│   ├── db/           # Room database, DAO, entities
│   └── repository/   # Story repository
├── domain/model/     # Domain models
└── ui/
    ├── home/         # Story generation screen
    ├── history/      # Story library screen
    ├── settings/     # API key & preferences screen
    ├── navigation/   # Nav graph
    └── theme/        # Colors & theme
```

## Build APK via GitHub Actions

This project includes a GitHub Actions workflow that automatically builds both debug and release APKs on every push. To use it:

1. Create a GitHub repository
2. Push this code
3. Go to **Actions** tab
4. Wait for the build to complete
5. Download APKs from **Artifacts**

## License

MIT
