# LibraryAndroidApp

A modern Android application for library management, developed with **Jetpack Compose**, **Firebase** and **Room Database**.

## Description
The application allows users to browse the book catalog, perform loans and receive updates on return dates. It also provides a complete management environment (Admin Panel) for adding books, branches and tracking loans.

## Features
- **User Authentication:** Registration and login via Firebase Auth.
- **Book Management:** Add, edit and delete books.
- **Branch Management:** Organize books by geographic branch.
- **Loan System:** Ability to borrow books with automatic availability update.
- **Admin Panel:** Specialized screens for managing library data.
- **Notifications:** Local notifications to remind of returns.
- **Offline Support:** Use of Room Database for local storage and quick access.

## Technologies
- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Database (Local):** [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- **Database (Cloud) & Auth:** [Firebase Firestore](https://firebase.google.com/docs/firestore) & [Firebase Authentication](https://firebase.google.com/docs/auth)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Navigation:** Navigation Compose
- **Concurrency:** Kotlin Coroutines & Flow

## Project Structure
The code structure follows Android best practices:

```text
app/src/main/java/com/example/project/
├── data/           # Data layer (DAOs, Repositories, Database Providers)
├── model/          # Data classes and entities (Book, Branch, Loan)
├── ui/             # User interface layer
│   ├── navigation/ # Navigation settings (NavHost)
│   ├── screens/    # All application screens (Composables)
│   ├── state/      # Classes for UI State management
│   └── theme/      # Color, font and theme definitions
├── util/           # Helper classes (e.g. NotificationHelper)
└── viewmodel/      # Business logic and UI connection with data
```

## Installation & Execution
1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or later)**.
3. Connect the application with your **Firebase Project**:
   - Add the `google-services.json` file to the `app/` directory.
   - Enable Authentication and Firestore in the Firebase Console.
4. Build and run the application on an Emulator or physical device.

---
*Developed as part of an educational activity for library management in an Android environment.*
