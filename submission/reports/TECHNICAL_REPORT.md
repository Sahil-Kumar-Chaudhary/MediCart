# MediCart – Technical Audit, Architecture & Quality Assurance Report

**Project Title:** MediCart Online Pharmacy Android Application & REST API  
**Author / Candidate:** Sahil Kumar Chaudhary  
**Organization / Evaluator:** ALGIONS AI LABS PRIVATE LIMITED  
**Official Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Main Branch:** `main`  
**Latest Git Commit:** `c9efdff` (`test(android): complete authentication navigation graph wiring and add unit test suite`)  
**Target Platform:** Android 13 (API Level 33) / Verified on Hardware Device: `CPH2333` (OPPO Reno7 5G)  
**Live Public Backend:** [https://medicart-backend-lmcz.onrender.com/](https://medicart-backend-lmcz.onrender.com/)  
**Database:** MongoDB Atlas (Mongoose ODM)  
**Submission Package:** `submission/`  

---

## 1. Executive Summary

This report documents the comprehensive technical architecture, security audit, automated test suite results, and live hardware verification for the **MediCart** healthcare application developed for the internship assessment at **ALGIONS AI LABS PRIVATE LIMITED**.

The platform is designed to provide an authentic, modern e-pharmacy experience tailored to Indian regulatory and operational requirements. It features:
1. A native Android client developed in **Kotlin 2.0.21** and **Jetpack Compose (Material Design 3)** with MVVM architecture, offline SQLite caching, and Android Keystore-backed `EncryptedSharedPreferences`.
2. A complete **Phone Number + Password Authentication System** featuring real-time client-side password criteria validation, Bcrypt password hashing, timing-safe HMAC OTP verification, and instant session invalidation via `tokenVersion`.
3. A production-ready **Node.js / Express** REST API deployed live on **Render Web Service** connected to a multi-region **MongoDB Atlas** cluster over pure HTTPS.
4. **88 / 88 Automated Tests Passed (100% Pass Rate)**:
   - 49 Android unit & integration tests (`MediCartAuthRedesignTest`, `MediCartRetrofitIntegrationTest`, `MediCartValidationTest`).
   - 39 Backend Jest & Supertest API tests (`authRedesign.test.js`, `api.test.js`).
5. **Physical Hardware Verification**: Fully verified on a physical smartphone (`CPH2333` / OPPO Reno7 5G running Android 13) communicating directly over public HTTPS without ADB reverse or local developer tools.

---

## 2. Business Problem & Domain Compliance

India's retail pharmacy sector presents distinct operational and regulatory requirements:
1. **Safety & Regulatory Dispensing Caps:** Over-the-counter and prescription medicines require strict quantity limits to prevent hoarding and abuse. MediCart enforces a regulatory limit of at most 5 packs per formulation.
2. **Batch Expiry & Withholding:** Expired pharmaceutical formulations must never be dispensed. The system actively checks batch expiry dates and disallows purchase of expired items (`isExpired: true`).
3. **Transparent Indian Pricing & Currency:** All financial figures are standardized in Indian Rupees (₹) with integer-paise calculations on the backend to avoid floating-point rounding errors.
4. **Indian Address & Postal Validation:** Indian postal PIN codes are strictly validated as 6-digit numeric sequences against an active delivery radius check.
5. **Simulated Payment Sandbox:** A non-financial checkout environment (supporting UPI Demo, Cash on Delivery, and simulated Cards) clearly communicates its demo sandbox nature without deducting real money.

---

## 3. Technology Stack

### 3.1. Android Client Stack
- **Language:** Kotlin 2.0.21
- **UI Toolkit:** Jetpack Compose (BOM 2024.05.00) with Material Design 3
- **Design Tokens:** Emerald Green (`#087E6B`), Soft Mint (`#DDF1EA`), Warm Off-White (`#F7F9F7`), Deep Charcoal (`#172B27`)
- **Architecture:** MVVM (Model-View-ViewModel) with unidirectional data flow
- **Reactive State:** Kotlin Coroutines & `StateFlow`
- **Networking:** Retrofit 2.11.0, OkHttp 4.12.0 with HTTP logging and auth interceptors
- **Local Persistence:** Android SQLite (`MediCartDatabaseHelper`) for offline-first caching
- **Secure Storage:** Android Jetpack Security (`EncryptedSharedPreferences`) backed by Android Hardware Keystore (AES-256 GCM)
- **Navigation:** Jetpack Navigation Compose with URL-safe route parameters

### 3.2. Backend & Database Stack
- **Server Runtime:** Node.js v24.20.0 (Express.js 4.21.2)
- **Database Engine:** MongoDB Atlas with Mongoose 8.9.5 ODM (supports MongoDB Atlas and in-memory server for zero-config evaluation)
- **Authentication:** JSON Web Tokens (`jsonwebtoken`) signed via HMAC-SHA256, salted Bcrypt password hashing
- **OTP Generation & Verification:** Time-based HMAC OTP using `crypto.timingSafeEqual` with a 5-minute expiry
- **Request Validation:** Zod schemas
- **Security Middleware:** Helmet (HTTP security headers), CORS whitelist, and Express Rate Limiter
- **Testing:** Jest 29.7.0 and Supertest 7.0.0

---

## 4. Architectural Design

```
+-----------------------------------------------------------------------------------+
|                        Android Client (CPH2333 - Android 13)                      |
|                                                                                   |
|  [Compose Screens (Material 3)]  <--->  [ViewModels (StateFlow)]                 |
|                                                     |                             |
|                                            [PharmacyRepository]                   |
|                                             /                \                    |
|                (Network Online)            /                  \ (Offline Fallback)|
|                                           v                    v                  |
|                                [Retrofit 2 + OkHttp]      [SQLite Local DB]       |
|                                [EncryptedSharedPrefs]     (medicart_offline.db)   |
+-------------------------------------------|---------------------------------------+
                                            | HTTPS / REST (Port 443 via Public Cloud)
                                            v
+-----------------------------------------------------------------------------------+
|                        Render Web Service (medicart-backend)                      |
|                                                                                   |
|  [Rate Limiter] -> [Helmet] -> [Auth Middleware (JWT)] -> [Zod Input Validator]   |
|                                           |                                       |
|                                    [Controllers]                                  |
|                                           |                                       |
|                                    [Services Layer]                               |
|                           (Atomic Decrement & Rollback Engine)                    |
|                                           |                                       |
|                                   [Mongoose Models]                               |
+-------------------------------------------|---------------------------------------+
                                            | TLS / Mongoose
                                            v
+-----------------------------------------------------------------------------------+
|                            MongoDB Atlas Cloud Cluster                            |
|                       (Cluster0: Database 'medicart')                             |
+-----------------------------------------------------------------------------------+
```

### 4.1. Unidirectional Data Flow & State Management
Compose screens observe immutable `StateFlow` state exposed by ViewModels (`AuthViewModel`, `HomeViewModel`, `CatalogueViewModel`, `CartViewModel`, `OrderViewModel`). User actions dispatch intents to the ViewModel, which coordinates with `PharmacyRepository`.

### 4.2. Repository Pattern & Offline Synchronization
`MockPharmacyRepository.kt` acts as the single source of truth:
1. **Network First:** Queries remote REST endpoints when connectivity is active.
2. **Local Cache:** Writes fetched catalogue products, addresses, and placed orders into SQLite.
3. **Offline Fallback:** If network requests fail with an `IOException`, the repository gracefully serves data from SQLite without blocking the UI.
4. **Hardware Keystore:** Authentication tokens and user metadata are encrypted via `EncryptedSharedPreferences` using master keys generated by Android's hardware Keystore provider.

---

## 5. Complete Authentication Redesign Architecture

The authentication system matches the modern patterns of JanSaarthi and HostelCare:
- **Phone Number + Password Login:** Users authenticate with a 10-digit Indian mobile number and a password.
- **Dynamic Password Complexity Checklist:** 
  - Minimum 8 characters
  - At least one uppercase letter (`A-Z`)
  - At least one lowercase letter (`a-z`)
  - At least one numeric digit (`0-9`)
  - At least one special character (`!@#$%^&*...`)
- **New User Registration:** Multi-step flow verifying password match, terms acceptance, and telephone possession via OTP.
- **Forgot Password & Reset:** Secure password recovery using 6-digit OTP verification with strict anti-enumeration guards.
- **Stateless Session Revocation (`tokenVersion`):** Storing an integer `tokenVersion` in the MongoDB user schema allows instant invalidation of all existing JWTs upon password reset, without external Redis dependency.

---

## 6. Public Cloud Deployment (Render & MongoDB Atlas)

Infrastructure as Code is formalized in `render.yaml`:
- **Service Name:** `medicart-backend`
- **Plan:** `free` (Explicit Render Free tier compute plan; zero payment information required)
- **Root Directory:** `backend`
- **Health Check Path:** `/health`
- **Live URL:** `https://medicart-backend-lmcz.onrender.com/`
- **Database:** MongoDB Atlas M0 Free Cluster (`Cluster0`), database `medicart`
- **Environment Variables:**
  - `NODE_ENV=production`
  - `JWT_SECRET` (auto-generated cryptographically secure secret)
  - `JWT_EXPIRES_IN=7d`
  - `OTP_MODE=DEMO`
  - `DEMO_PHONE_NUMBER=9876543210`
  - `DEMO_OTP_CODE` (configured securely in Render dashboard, e.g. `487210`)
  - `MONGODB_URI` (configured securely in Render dashboard pointing to Atlas cluster)

---

## 7. REST API Documentation

| Method | Endpoint | Access | Purpose |
| :--- | :--- | :---: | :--- |
| `GET` | `/health` | Public | System status and database connectivity check |
| `POST` | `/api/auth/login` | Public | Authenticates via phone number & password; issues JWT |
| `POST` | `/api/auth/register/request-otp` | Public | Validates user registration fields; issues OTP challenge |
| `POST` | `/api/auth/register/verify-otp` | Public | Verifies OTP, hashes password with Bcrypt, creates account |
| `POST` | `/api/auth/forgot-password/request-otp` | Public | Anti-enumeration password reset OTP request |
| `POST` | `/api/auth/forgot-password/reset` | Public | Verifies reset OTP, updates Bcrypt hash, increments `tokenVersion` |
| `GET` | `/api/auth/me` | Bearer | Returns authenticated profile matching current `tokenVersion` |
| `GET` | `/api/products` | Public | Lists pharmacy catalogue with search and category filters |
| `GET` | `/api/products/:id` | Public | Returns comprehensive details of a specific medicine |
| `GET` | `/api/addresses` | Bearer | Retrieves delivery addresses belonging to the authenticated user |
| `POST` | `/api/addresses` | Bearer | Creates a new validated Indian delivery address |
| `PUT` | `/api/addresses/:id` | Bearer | Updates existing delivery address with ownership verification |
| `DELETE` | `/api/addresses/:id` | Bearer | Deletes delivery address |
| `POST` | `/api/orders` | Bearer | Submits order with idempotency check and atomic stock decrement |
| `GET` | `/api/orders` | Bearer | Retrieves chronological order history for authenticated user |
| `GET` | `/api/orders/:id` | Bearer | Returns detailed order record with timeline and immutable snapshots |

---

## 8. Order Evidence Reconciliation

To ensure complete transparency and zero data fabrication, all orders cited in QA audits are verified against actual MongoDB database records:

### 8.1. Live Cloud Render + Atlas Verified Order (`#MC-2026-3819`)
- **Order Reference:** `#MC-2026-3819`
- **Backend Environment:** Public Render Web Service (`https://medicart-backend-lmcz.onrender.com/`)
- **Database:** MongoDB Atlas Cloud Cluster
- **MongoDB Object ID:** `6af95011da0736228f500a46`
- **User ID:** `6af94e5bda0736228f500a1e`
- **Idempotency Key:** `e0b2c4ce-8f5b-429b-a232-afd06828947f`
- **Submission Source:** Native Android UI on physical smartphone `CPH2333` (Zero ADB reverse, public HTTPS)
- **Line Items:**
  1. `med_01` (Paracare 650mg Tablets) • **Quantity: 3** • Unit: ₹34.00 (3400 paise) • Line Total: ₹102.00 (10200 paise)
  2. `med_03` (AyurTulsi Cough Relief Herbal Syrup) • **Quantity: 1** • Unit: ₹85.00 (8500 paise) • Line Total: ₹85.00 (8500 paise)
- **Subtotal:** ₹187.00 (18700 paise)
- **Delivery Fee:** ₹30.00 (3000 paise)
- **Packaging Fee:** ₹14.00 (1400 paise)
- **Final Total Amount:** **₹231.00** (23100 paise)
- **Payment Method:** `UPI_DEMO`
- **Order Status:** `PROCESSING`
- **Created At:** `2026-11-15T06:24:17.103Z`

### 8.2. Primary Hardware Verified Order (`#MC-2026-3947`)
- **Order Reference:** `#MC-2026-3947`
- **MongoDB Object ID:** `6af84a37de616014ffa912c4`
- **User ID:** `6af847f7de616014ffa9127e`
- **Idempotency Key:** `4f925d49-5dd3-43fd-adac-a516ca3e2245`
- **Line Items:**
  1. `med_01` (Paracare 650mg Tablets) • **Quantity: 2** • Line Total: ₹68.00 (6800 paise)
  2. `med_03` (AyurTulsi Cough Relief Herbal Syrup) • **Quantity: 1** • Line Total: ₹85.00 (8500 paise)
- **Subtotal:** ₹153.00 (15300 paise) + Delivery ₹30.00 + Packaging ₹14.00 = **Total: ₹197.00** (19700 paise)
- **Created At:** `2026-11-14T11:47:03.890Z`

### 8.3. Secondary API Verified Order (`#MC-2026-5796`)
- **Order Reference:** `#MC-2026-5796`
- **MongoDB Object ID:** `6af84864de616014ffa9129e`
- **Line Items:**
  1. `med_01` (Paracare 650mg Tablets) • **Quantity: 2** • Line Total: ₹68.00 (6800 paise)
  2. `med_02` (Cetriz-L 10mg Tablets) • **Quantity: 1** • Line Total: ₹22.00 (2200 paise)
- **Subtotal:** ₹90.00 (9000 paise) + Delivery ₹30.00 + Packaging ₹14.00 = **Total: ₹134.00** (13400 paise)
- **Created At:** `2026-11-14T11:39:16.493Z`

---

## 9. Automated Test Results (88 Tests Total)

### 9.1. Android Client Test Suite (49 Tests)
Command: `.\gradlew.bat testDebugUnitTest`  
Result: **BUILD SUCCESSFUL** (49/49 tests passed)
- **`com.medicart.app.MediCartAuthRedesignTest` (19 tests):**
  - Phone format validation, password strength rules, registration validation, OTP formatting, ViewModel state transitions, session persistence, and logout teardown.
- **`com.medicart.app.MediCartRetrofitIntegrationTest` (20 tests):**
  - OkHttp `MockWebServer` verification of login DTOs, registration endpoints, anti-enumeration reset contracts, `/me` profile retrieval, auth interceptor header attachment, catalogue parsing, and offline fallback.
- **`com.medicart.app.MediCartValidationTest` (10 tests):**
  - Indian 6-digit PIN validation, 5-pack safety dispensing limit, cart subtotal and fee calculation, batch expiry enforcement, and order state machine transitions.

### 9.2. Backend REST API Test Suite (39 Tests)
Command: `cd backend && npm test`  
Result: **PASS** (39/39 tests passed)
- **`backend/tests/authRedesign.test.js` (17 tests):**
  - Bcrypt password comparison, timing-safe OTP validation, registration database records, anti-enumeration forgot-password responses, password reset updates, and `tokenVersion` session invalidation.
- **`backend/tests/api.test.js` (22 tests):**
  - Health check, catalog search and filtering, single medicine queries, address isolation, order creation with atomic inventory decrement, idempotency duplicate prevention, and rate limiting.

---

## 10. Performance & APK Build Audit

- **Debug APK Location:** `submission/apk/MediCart-v2.4-live-debug.apk` and `submission/apk/MediCart-debug.apk`
- **File Size:** **16.62 MB** (17,426,258 bytes)
- **SHA-256 Checksum:** `D0501F9C196FE19D54C4888EC360FF7C97F1FD22C3BB0B85B6E041A6F6B836B9`
- **Min SDK:** API Level 24 (Android 7.0 Nougat) | **Target SDK:** API Level 34 (Android 14)
- **Base URL:** `https://medicart-backend-lmcz.onrender.com/` (Clean public HTTPS, zero ADB reverse dependency)
- **Hardware Verified:** Tested on OPPO Reno7 5G (`CPH2333`), Android 13.
