# Rent Truth BD — Environment Setup Guide ⚙️

This document describes how to configure your local development workspace, import environment variables securely, and compile the Rent Truth BD Android applet.

---

## 🔑 Secure Secrets Management (No Hardcoded API Keys)

Rent Truth BD respects strict secure data guidelines. The Gemini API key and other server credentials must **NEVER** be committed to Git or hardcoded in any class file.

### 1. Injected Variables (.env / Secrets Panel)
The application pulls variables at compile and runtime using the **Secrets Panel in the AI Studio UI**. These secrets are injected directly into `BuildConfig`.

Ensure you configure the following key inside the UI or your local development machine config:

```properties
GEMINI_API_KEY=AIzaSyA_your_real_gemini_api_key_here
```

### 2. Gradle Integration Settings
`.env` properties are mapped securely to the application using the Secrets Gradle Plugin declared inside `build.gradle.kts`:

```kotlin
// Build config fields are generated automatically
android {
    buildTypes {
        getByName("debug") {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
        getByName("release") {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
    }
}
```

---

## 🛠️ Developer Prerequisites

- **Java Development Kit (JDK)**: Version 17 (Required by modern Gradle tools)
- **Android Studio**: Ladybug / Koala or newer
- **Android SDK**: API 34 (Android 14) installed

---

## 🏃 Local Compilation & Running

First, clone the repository and navigate to the project root:

```bash
# Clone the repository
git clone https://github.com/aistudio/rent-truth-bd.git
cd rent-truth-bd

# Prepare .env secrets file (for local builds)
cp .env.example .env

# Verify files are formatted correctly
# On local systems, you can use ./gradlew. In the AI Studio container, always run raw 'gradle':
gradle compileDebugKotlin
```

To construct the debug APK file for testing on external devices:

```bash
# Build Debug Application Package
gradle assembleDebug
```

The compiled package will be available under:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 Validating with Robolectric Tests

To invoke the local JVM test cases alongside screenshot regression controls:

```bash
# Run unit and local integration tests
gradle :app:testDebugUnitTest

# Re-evaluate visual regressions using Roborazzi
gradle :app:verifyRoborazziDebug
```
