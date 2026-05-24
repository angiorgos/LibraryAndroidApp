# LibraryAndroidApp

Μια σύγχρονη εφαρμογή Android για τη διαχείριση βιβλιοθήκης, αναπτυγμένη με **Jetpack Compose**, **Firebase** και **Room Database**.

## 📝 Περιγραφή
Η εφαρμογή επιτρέπει στους χρήστες να περιηγούνται στον κατάλογο βιβλίων, να πραγματοποιούν δανεισμούς και να ενημερώνονται για τις ημερομηνίες επιστροφής. Παράλληλα, παρέχει ένα πλήρες περιβάλλον διαχείρισης (Admin Panel) για την προσθήκη βιβλίων, παραρτημάτων και την παρακολούθηση των δανεισμών.

## ✨ Χαρακτηριστικά
- **Αυθεντικοποίηση Χρηστών:** Εγγραφή και σύνδεση μέσω Firebase Auth.
- **Διαχείριση Βιβλίων:** Προσθήκη, επεξεργασία και διαγραφή βιβλίων.
- **Διαχείριση Παραρτημάτων:** Οργάνωση βιβλίων ανά γεωγραφικό παράρτημα.
- **Σύστημα Δανεισμού:** Δυνατότητα δανεισμού βιβλίων με αυτόματη ενημέρωση διαθεσιμότητας.
- **Admin Panel:** Εξειδικευμένες οθόνες για τη διαχείριση των δεδομένων της βιβλιοθήκης.
- **Ειδοποιήσεις:** Τοπικές ειδοποιήσεις για την υπενθύμιση επιστροφών.
- **Offline Υποστήριξη:** Χρήση Room Database για τοπική αποθήκευση και γρήγορη πρόσβαση.

## 🛠️ Τεχνολογίες
- **Γλώσσα:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) με Material 3
- **Database (Τοπική):** [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- **Database (Cloud) & Auth:** [Firebase Firestore](https://firebase.google.com/docs/firestore) & [Firebase Authentication](https://firebase.google.com/docs/auth)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Navigation:** Navigation Compose
- **Concurrency:** Kotlin Coroutines & Flow

## 📂 Δομή Έργου
Η δομή του κώδικα ακολουθεί τις βέλτιστες πρακτικές του Android:

```text
app/src/main/java/com/example/project/
├── data/           # Layer δεδομένων (DAOs, Repositories, Database Providers)
├── model/          # Data classes και οντότητες (Book, Branch, Loan)
├── ui/             # Layer διεπαφής χρήστη
│   ├── navigation/ # Ρυθμίσεις πλοήγησης (NavHost)
│   ├── screens/    # Όλες οι οθόνες της εφαρμογής (Composables)
│   ├── state/      # Κλάσεις για τη διαχείριση του UI State
│   └── theme/      # Ορισμοί χρωμάτων, γραμματοσειρών και θέματος
├── util/           # Βοηθητικές κλάσεις (π.χ. NotificationHelper)
└── viewmodel/      # Business logic και σύνδεση UI με τα δεδομένα
```

## 🚀 Εγκατάσταση & Εκτέλεση
1. Κάντε clone το repository.
2. Ανοίξτε το project στο **Android Studio (Ladybug ή νεότερο)**.
3. Συνδέστε την εφαρμογή με το δικό σας **Firebase Project**:
   - Προσθέστε το αρχείο `google-services.json` στον κατάλογο `app/`.
   - Ενεργοποιήστε το Authentication και το Firestore στο Firebase Console.
4. Κάντε Build και τρέξτε την εφαρμογή σε Emulator ή φυσική συσκευή.

---
*Αναπτύχθηκε στα πλαίσια εκπαιδευτικής δραστηριότητας για τη διαχείριση βιβλιοθηκών σε περιβάλλον Android.*
