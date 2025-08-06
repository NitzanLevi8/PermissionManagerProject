# PermissionManagerProject

A modular Android library and demo app for managing, requesting, and logging app permissions in a clean and reusable way.  
Built as part of a learning project, this system helps developers easily integrate permission control and visibility into their apps, with real-time logging powered by Firebase.

---

## 📱 Key Features

- 🔐 Request and track Android permissions (camera, location, contacts, microphone, etc.)
- 📊 Automatically log permission changes and save them to Firebase Realtime Database
- 🕵️‍♀️ View a log of the 5 most recent permission events directly on the home screen
- 📶 Scan for available Wi-Fi networks nearby and connect to open networks
- 🧩 Modular architecture — includes reusable permission and Wi-Fi libraries for future projects

---

## 🧱 Project Structure

- **app** – Main application for demonstrating features and connecting to Firebase  
- **permissionlib** – Standalone library for handling permissions across Android apps  
- **wifilib** – Library for scanning and connecting to nearby Wi-Fi networks  

---

## ⚙️ Built With

- **Java** (100% Kotlin-free)
- **Firebase Realtime Database**
- **Android Jetpack** libraries (AppCompat, ConstraintLayout, Material)
- **Custom modular architecture**
- **Gradle Version Catalog** for clean dependency management

---

## 🚀 Getting Started

To run the project locally:

```bash
git clone https://github.com/NitzanLevi8/PermissionManagerProject.git
