# Utopia: Campsite Reservation System

Utopia is an Android app for reserving campsites through a lottery. Campsite owners list a campsite with a registration period, a booking period and a number of winners. Campers (entrants) join the waitlist, and winners are drawn automatically once the registration period has ended. Admins keep the platform tidy by browsing and removing campsites, profiles and images.

The repository is called `Utopia-campsite`. It is the team project for **CMPUT 301 (Fall 2026) at the University of Alberta**, and this version is the **Project Part 3 prototype**.

## Team

| Name | GitHub |
| --- | --- |
| Aidan Dai | [fyz007](https://github.com/fyz007) |
| Joshua Lavallee | [JoshuaL05](https://github.com/JoshuaL05) |
| Zanoon Hassan | [Al-Zaytoun](https://github.com/Al-Zaytoun) |
| Shenura Mendis | [shenuramendis](https://github.com/shenuramendis) |
| Evan Pearson Maraj | [epearsonmaraj](https://github.com/epearsonmaraj) |
| Silin Chen (Kris) | [silin740](https://github.com/silin740) |
| Cyrus June Dingle | [cdingle609](https://github.com/cdingle609) |

GitHub usernames are also recorded in [doc/teams.txt](doc/teams.txt).

## Features

- **Entrants (campers)** search and filter campsites, view campsite details, join or leave a campsite lottery, view their reservations and manage their profile.
- **Campsite owners** register and update campsites, set the registration period, booking period and number of winners, and view the users booked at their campsites.
- **Admins** browse and remove campsites, profiles and images.
- There is no username or password: users are signed in anonymously with Firebase Authentication.
- Lottery winners are drawn automatically for campsites whose registration period has ended (checked when the app starts).

The [product backlog](doc/Product%20Backlog/Product%20Backlog%20-%20Project%20Part%203.md) lists every user story, marks the ones planned for Part 3 and gives the sprint each belongs to.

## Tech stack

| Area | What we use |
| --- | --- |
| Language | Kotlin 2.4.10 |
| UI | Jetpack Compose with Material 3, Navigation Compose |
| Backend | Firebase: Cloud Firestore (collections `users` and `campsites`) and Firebase Authentication |
| Images | Coil 3 |
| Build | Gradle (Kotlin DSL), Android Gradle Plugin 9.4.1 |
| Targets | Android 7.0 (API 24) and newer; compile and target SDK 37 |
| Tests | JUnit 4 for unit tests; Compose UI tests and Espresso for instrumented tests |
| CI | GitHub Actions ([android.yml](.github/workflows/android.yml)) |

## Getting started

You need Android Studio (a version that supports Android Gradle Plugin 9.4.1) with JDK 17, and the Firebase configuration file of the team's project.

1. Clone the repository:

   ```bash
   git clone https://github.com/CMPUT301F26utopia/Utopia-campsite.git
   ```

2. Put the Firebase configuration file at `app/google-services.json`. It is not committed (it is listed in `.gitignore`), and the build fails without it.
3. Open the project in Android Studio and let Gradle sync.
4. Run the `app` configuration on an emulator or a device with Android 7.0 (API 24) or newer. The device needs internet access, because the data lives in Firestore.

## Running the tests

```bash
./gradlew testDebugUnitTest          # unit tests (JUnit)
./gradlew connectedDebugAndroidTest  # UI tests, needs an emulator or a device
```

GitHub Actions builds the app and runs the same tests on every push and on pull requests into `main`. The UI tests run on an API 34 emulator there.

## Code documentation

KDoc comments for the model classes and their public methods are in the source files. To build the HTML version with Dokka, run `./gradlew :app:dokkaGeneratePublicationHtml`; the output is written to `app/build/dokka/html`.

## Project layout

```text
app/src/main/java/com/example/utopia_campsite/
├── MainActivity.kt, UtopiaNavApp.kt   entry point and navigation
├── auth/    welcome and sign-up screens
├── user/    entrant screens, User and Reservation models, UserRepository
├── owner/   owner screens, Campsites and Owner models, CampsiteRepository, lottery draw
├── admin/   admin screens for browsing and removing campsites, profiles and images
└── ui/      shared components and theme
app/src/test/          unit tests
app/src/androidTest/   instrumented (UI) tests
doc/                   project documents (see below)
```

## Documentation

| Document | Where |
| --- | --- |
| UML class diagram | [doc/UML/utopia-uml.drawio.png](doc/UML/utopia-uml.drawio.png) |
| CRC cards | [doc/CRC Cards/utopia-crc-card.drawio.png](doc/CRC%20Cards/utopia-crc-card.drawio.png) |
| Product backlog and user stories | [doc/Product Backlog](doc/Product%20Backlog/) |
| UI mockups | [doc/UI Mockup](doc/UI%20Mockup/) |
| Storyboards | [doc/storyboards](doc/storyboards/) |

The same documents are shown on the [team wiki](https://github.com/CMPUT301F26utopia/Utopia-campsite/wiki).

## Contributing

Work is done on branches and merged into `main` through pull requests.

## Third-party libraries

Firebase (Cloud Firestore, Authentication, FirebaseUI Auth), Jetpack Compose and Material 3, Navigation Compose, Coil 3 and Google Play Services Base. For testing: JUnit, Espresso and Compose UI Test. The exact versions are in [app/build.gradle.kts](app/build.gradle.kts) and [gradle/libs.versions.toml](gradle/libs.versions.toml).

## License

This project is licensed under the [Apache License 2.0](LICENSE). The full text is in the `LICENSE` file at the root of the repository.

Copyright 2026 Aidan Dai, Joshua Lavallee, Zanoon Hassan, Shenura Mendis, Evan Pearson Maraj, Silin Chen and Cyrus June Dingle.
