# google-analytics-kt

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-purple.svg)](https://kotlinlang.org)

A pure Kotlin/JVM client for the **GA4 Measurement Protocol v2**. No Firebase SDK, no Android SDK, no Google Play Services - just a direct HTTP POST to GA4's collection endpoint. Works on any JVM target.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Quick Start](#quick-start)
- [Hit Types](#hit-types)
  - [screenView](#screenview)
  - [event](#event)
  - [exception](#exception)
  - [timing](#timing)
  - [pageView](#pageview)
  - [custom](#custom)
- [Limitations](#limitations)
- [License](#license)

---

## Prerequisites

You need two values from the GA4 Admin panel:

| Value | Where to find it |
|---|---|
| **Measurement ID** (`G-XXXXXXXX`) | GA4 Admin → Data Streams → your stream → Measurement ID |
| **API Secret** | GA4 Admin → Data Streams → your stream → Measurement Protocol API secrets → Create |

> **Security:** Never commit these values to source control. Store them in `~/.gradle/gradle.properties` locally and in CI secrets for builds.

---

## Setup

**Gradle (Kotlin DSL)**
```kotlin
dependencies {
  implementation("io.github.criticalay:google-analytics-kt:1.2.1")
}
```

**Maven**
```xml
<dependency>
    <groupId>io.github.criticalay</groupId>
    <artifactId>google-analytics-kt</artifactId>
    <version>1.2.1</version>
</dependency>
```

**Android Requirement**
Add the serialization plugin to your module:
```kotlin
plugins {
    kotlin("plugin.serialization") version "[Kotlin version]"
}
```


**Others**: [Check Here](https://central.sonatype.com/artifact/io.github.criticalay/google-analytics-kt)

---

## Quick Start

Initialize once for the lifetime of your app:

```kotlin
val ga = GoogleAnalytics.builder {
    measurementId = "G-XXXXXXXX"  
    apiSecret     = "your-secret" 
    enabled       = true
    debug         = false 
}

val clientId = "user-uuid-here"
```

Fire events:

```kotlin
ga.screenView(clientId)
    .screenName("HomeScreen")
    .sendAsync()
```

Close on app exit to flush pending batches:
```kotlin
ga.close()
```

---

## Hit Types

### screenView
Maps to the GA4 `screen_view` event.
```kotlin
ga.screenView(clientId)
    .screenName("HomeScreen")   
    .appName("MyApp")          
    .appVersion("2.1.0")       
    .sendAsync()
```

### event
Maps to the GA4 `event` event. Mirrors the UA category/action/label model.
```kotlin
ga.event(clientId)
    .category("UI")             
    .action("button_tap")       
    .label("submit_button")    
    .value(1)             
    .sendAsync()
```

### exception
Maps to the GA4 `exception` event.
```kotlin
ga.exception(clientId)
    .exception(throwable, includeStack = true)
    .fatal(false)
    .sendAsync()
```

### timing
Maps to the GA4 `timing_complete` event.
```kotlin
val elapsed = System.currentTimeMillis() - start

ga.timing(clientId)
    .timingCategory("api")
    .timingName("syncCards")
    .timingValue(elapsed)       
    .timingLabel("production")  
    .sendAsync()
```

### pageView
Maps to the GA4 `page_view` event. Useful for web/desktop apps.
```kotlin
ga.pageView(clientId)
    .pageLocation("[https://myapp.com/home](https://myapp.com/home)")
    .pageTitle("Home")
    .pageReferrer("[https://myapp.com/login](https://myapp.com/login)")
    .sendAsync()
```

### custom
Records an event with a fully custom name and parameters.
*(Rules: letters/digits/underscores only, max 40 chars, start with a letter. No `ga_`, `google_`, or `firebase_` prefixes).*
```kotlin
ga.custom(clientId, "deck_studied")
    .param("deck_name", "Japanese N5")
    .param("card_count", 42)
    .param("duration_ms", 18_500L)
    .param("passed", true)
    .sendAsync()
```

---

## Limitations
- The batch buffer is in-memory only - events are lost if the app crashes before flush
- If the device is offline when flush happens, the entire batch is dropped
- `send()` returns an empty `GaResponse` when batching is enabled (no per-event response)

---

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
---

#### Special thanks to [google-analytics-java](https://github.com/mikehardy/google-analytics-java) for the inspiration and motivation behind this library's design.

