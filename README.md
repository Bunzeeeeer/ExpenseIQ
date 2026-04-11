# 💰 ExpenseIQ — Smart Expense Tracker for Android

> A modular, production-ready Android expense tracker built with Java, MVVM, RxJava2, Room, and Firebase Authentication.

---

## 📖 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Module Structure](#module-structure)
- [Setup & Installation](#setup--installation)
- [CI/CD Pipeline](#cicd-pipeline)
- [Database Schema](#database-schema)
- [Screenshots](#screenshots)
- [Author](#author)

---

## 🧠 Overview

**ExpenseIQ** is a modular Android expense tracker that helps users manage their personal finances. It features Firebase Authentication for secure multi-user support, real-time budget tracking, spending analytics with charts, and a unique "Command Center" home screen with financial health scoring.

Built as a personal portfolio project to demonstrate clean Android architecture, modular design, and production-grade code quality practices.

---

## ✨ Features

### 🔐 Authentication
- Email & Password registration and login via Firebase Auth
- Per-user data isolation — each user only sees their own data
- Auto-redirect to login on app launch if not authenticated
- Secure logout from the home screen

### 🏠 Home Screen (Command Center)
- **Financial Health Score** (0–100) based on budget adherence
- **Logging Streak** tracker — tracks consecutive days with expenses logged
- **Smart Alerts** — real-time budget warnings (80% and 100% thresholds)
- **Weekly Spending Chart** — bar chart of last 7 days spending
- **Money Tips** — random financial tips on every launch
- Auto-refresh on resume — always shows up-to-date data

### 📊 Dashboard
- Monthly spending hero card (total, expense count, budget left)
- Category overview grid with pastel color coding
- Spending breakdown with horizontal bar charts
- Budget overview with progress bars
- Recent expenses list (last 5)

### 💸 Expense Management
- Full CRUD — add, edit, delete expenses
- Category assignment with emoji icons
- Date picker for expense date
- Note support for each expense
- Empty state handling

### 🗂️ Category Management
- Full CRUD — add, edit, delete custom categories
- Emoji icon picker
- Hex color picker with live color preview
- Seed categories on first launch: Food, Transport, Shopping, Health, Bills, Others

### 💰 Budget Management
- Full CRUD — add, edit, delete budgets per category per month
- Month/Year picker
- Budget progress tracking
- Budget health color coding (green/yellow/red)

### 📈 Charts & Analytics
- **Pie Chart** — all-time spending by category
- **Line Chart** — daily spending over time (cubic bezier + fill)
- **Bar Chart** — monthly spending comparison
- All-time summary stats (total + transaction count)

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| Architecture | MVVM (Model-View-ViewModel) |
| Async/Reactive | RxJava2 + RxAndroid |
| Local Database | Room (SQLite) |
| Authentication | Firebase Auth (Email/Password) |
| Charts | MPAndroidChart |
| UI | Material Design 3 |
| CI/CD | GitHub Actions |
| Code Quality | SonarCloud |
| Build System | Gradle (Groovy DSL) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 36 |

---

## 🏛️ Architecture

ExpenseIQ follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns across modular layers.

```
┌─────────────────────────────────────────┐
│              UI Layer                    │
│  Activities → ViewModels → LiveData     │
├─────────────────────────────────────────┤
│           Domain Layer                   │
│  Repository Interfaces → Models         │
├─────────────────────────────────────────┤
│            Data Layer                    │
│  Room DAOs → Repository Implementations │
│  Firebase Auth → AuthRepository         │
└─────────────────────────────────────────┘
```

### Key Patterns Used
- **BaseActivity** — abstract lifecycle contract (`getLayoutId`, `initDesign`, `initViewModel`, `initObservers`)
- **Repository Pattern** — data access abstracted behind interfaces
- **RxJava2 Flowable** — reactive Room streams for real-time UI updates
- **CompositeDisposable** — automatic RxJava subscription cleanup
- **ViewModelFactory** — manual dependency injection for ViewModels

---

## 📦 Module Structure

```
ExpenseIQ/
├── app/                          → Shell, MainActivity, BottomNav
├── core/
│   ├── core-data/                → Room DB, DAOs, Repository implementations
│   ├── core-domain/              → Models, Repository interfaces
│   └── core-ui/                  → Theme, colors, BaseActivity, drawables
└── feature/
    ├── feature-auth/             → Login, Register, Firebase Auth
    ├── feature-dashboard/        → Dashboard screen
    ├── feature-expense/          → Expense + Category CRUD
    ├── feature-budget/           → Budget CRUD
    └── feature-charts/           → Analytics charts
```

### Module Dependency Graph

```
app
 ├── core-data (api)
 ├── core-domain (api)
 ├── core-ui
 ├── feature-auth
 ├── feature-dashboard
 ├── feature-expense
 ├── feature-budget
 └── feature-charts

feature-* → core-data, core-domain, core-ui
core-data → core-domain
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11
- Android SDK API 26+
- Firebase account (free Spark plan)

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/Bunzeeeeer/ExpenseIQ.git
cd ExpenseIQ
```

2. **Set up Firebase**
    - Go to [Firebase Console](https://console.firebase.google.com)
    - Create a new project named `ExpenseIQ`
    - Add an Android app with package name `com.bunzeeeeer.expenseiq`
    - Download `google-services.json` and place it in the `app/` folder
    - Enable **Email/Password** authentication in Firebase Console → Authentication → Sign-in method

3. **Open in Android Studio**
    - Open the project root folder
    - Let Gradle sync complete

4. **Run the app**
    - Connect a device or start an emulator (API 26+)
    - Click Run ▶️

---

## 🔄 CI/CD Pipeline

ExpenseIQ uses **GitHub Actions** for automated CI/CD with two checks on every PR:

### Build Check
- Assembles the release APK
- Verifies all modules compile correctly

### SonarCloud Analysis
- Static code analysis
- Security hotspot detection
- Code smell and maintainability checks
- Quality Gate enforcement

```yaml
# Triggers on:
- Pull Requests → develop
- Push → develop, main

# Branching strategy:
feature/* → develop → main (PR workflow only)
```

---

## 🗄️ Database Schema

Room database with version management and destructive migration support.

### Expenses Table
| Column | Type | Notes |
|---|---|---|
| id | INTEGER (PK) | Auto-generated |
| userId | TEXT | Firebase UID |
| title | TEXT | Expense name |
| amount | REAL | Amount in local currency |
| date | INTEGER | Unix timestamp |
| note | TEXT | Optional note |
| categoryId | INTEGER (FK) | Nullable, SET_NULL on delete |

### Budgets Table
| Column | Type | Notes |
|---|---|---|
| id | INTEGER (PK) | Auto-generated |
| userId | TEXT | Firebase UID |
| categoryId | INTEGER (FK) | Nullable, CASCADE on delete |
| limitAmount | REAL | Budget limit |
| month | INTEGER | 1–12 |
| year | INTEGER | e.g. 2026 |

### Categories Table
| Column | Type | Notes |
|---|---|---|
| id | INTEGER (PK) | Auto-generated |
| name | TEXT | Category name |
| colorHex | TEXT | Hex color string |
| icon | TEXT | Emoji icon |

> **Note:** Categories are shared across all users. Seed categories (Food, Transport, Shopping, Health, Bills, Others) are inserted on first app install.

---

## 🎨 UI Theme

ExpenseIQ uses a custom **pastel purple** Material Design 3 theme.

| Token | Value | Usage |
|---|---|---|
| Background | `#F8F6FF` | Soft lavender background |
| Primary | `#7B5EA7` | Purple accent |
| Hero Gradient | `#9B7FC7 → #7B5EA7` | Cards and headers |
| Surface | `#FFFFFF` | Card backgrounds |
| Pastel Pink | `#FFF0F5` | Food category |
| Pastel Mint | `#F0FAF5` | Transport category |
| Pastel Peach | `#FFF4EE` | Shopping category |
| Pastel Lavender | `#F3F0FF` | Health category |

---

## 🔀 Git Workflow

```
main          ← production
  └── develop ← integration
        └── feature/feature-name ← development
```

- All changes go through PRs — no direct pushes to `develop` or `main`
- PR descriptions follow a standard template
- Commit messages follow **Conventional Commits** (`feat/fix/chore/ci`)
- SonarCloud warnings are fixed before merging

---

## 👨‍💻 Author

**Lance Joshua Corcega, Claude AI for AI Assistance**

- GitHub: [@Bunzeeeeer](https://github.com/Bunzeeeeer)

---

## 📄 License

This project is for portfolio and educational purposes.

---

> Built with ☕ and a lot of HAHAHAHAHA 😄
