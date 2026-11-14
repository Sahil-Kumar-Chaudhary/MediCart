# MediCart – Technical Audit, Architecture & Quality Assurance Report

**Project Title:** MediCart Online Pharmacy Android Application & REST API  
**Author / Candidate:** Sahil Kumar Chaudhary  
**Organization / Evaluator:** ALGIONS AI LABS PRIVATE LIMITED  
**Official Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Main Branch:** `main`  
**Target Platform:** Android 13 (API Level 33) / Verified on Hardware Device: `CPH2333` (OPPO Reno7 5G)  
**Backend:** Node.js, Express.js, MongoDB (Mongoose), JWT, Zod  
**Submission Package:** `submission/`  

---

## 1. Executive Summary

This report documents the final technical quality assurance, software architecture, security audit, and live hardware verification for the **MediCart** healthcare application developed for the internship assessment at **ALGIONS AI LABS PRIVATE LIMITED**.

The solution comprises a native Android application built with modern **Kotlin** and **Jetpack Compose**, integrated via **Retrofit 2** with a production-grade **Node.js / Express REST API** and **MongoDB** database. The entire ordering lifecycle was tested end-to-end on a physical smartphone (`CPH2333` running Android 13) connected to a local backend via ADB reverse port-forwarding.

All 52 automated tests (30 Android unit/integration tests and 22 backend Jest tests) and 25 hardware interaction steps achieved a **100% pass rate**.

---

## 2. Business Problem & Domain Compliance

India's retail pharmacy sector presents distinct operational and regulatory requirements:
1. **Safety & Regulatory Dispensing Caps:** Over-the-counter and prescription medicines require strict quantity limits to prevent hoarding and abuse. MediCart enforces a regulatory limit of at most 5 packs per formulation.
2. **Batch Expiry & Withholding:** Expired pharmaceutical formulations must never be dispensed. The system actively checks batch expiry dates and disallows purchase of expired items.
3. **Transparent Indian Pricing & Currency:** All financial figures are standardized in Indian Rupees (₹) with integer-paise calculations on the backend to avoid floating-point rounding errors.
4. **Indian Address & Postal Validation:** Indian postal PIN codes are strictly validated as 6-digit numeric sequences against an active delivery radius check.
5. **Simulated Payment Sandbox:** A non-financial checkout environment (supporting UPI, Cash on Delivery, and simulated Cards) clearly communicates its demo sandbox nature without deducting real money.

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
- **Server Runtime:** Node.js v24.20.0
- **Framework:** Express.js 4.21.2
- **Database Engine:** MongoDB with Mongoose 8.9.5 ODM (supports MongoDB Atlas and in-memory server for zero-config evaluation)
- **Authentication:** JSON Web Tokens (`jsonwebtoken`) signed via HMAC-SHA256
- **OTP Generation & Verification:** Time-based HMAC OTP using `crypto.timingSafeEqual` with a 5-minute expiry
- **Request Validation:** Zod schemas
- **Security Middleware:** Helmet (HTTP security headers), CORS whitelist, and Express Rate Limiter
- **Testing:** Jest 29.7.0 and Supertest 7.0.0

---

## 4. Architectural Design

```
+-----------------------------------------------------------------------------------+
|                           Android Client (CPH2333 - Android 13)                   |
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
                                            | HTTP/REST (Port 5000 via ADB Reverse)
                                            v
+-----------------------------------------------------------------------------------+
|                              Node.js / Express Server                             |
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
                                            v
                              [MongoDB Database Engine]
```

### 4.1. Unidirectional Data Flow & State Management
Compose screens observe immutable `StateFlow` state exposed by ViewModels. User actions (e.g., adding to cart, updating quantities, placing orders) dispatch intents to the ViewModel, which coordinates with `PharmacyRepository`.

### 4.2. Repository Pattern & Offline Synchronization
`MockPharmacyRepository.kt` acts as the single source of truth:
1. **Network First:** Queries remote REST endpoints when connectivity is active.
2. **Local Cache:** Writes fetched catalogue products, addresses, and placed orders into SQLite.
3. **Offline Fallback:** If network requests fail with an `IOException`, the repository gracefully serves data from SQLite without blocking the UI.
4. **Hardware Keystore:** Authentication tokens and user metadata are encrypted via `EncryptedSharedPreferences` using master keys generated by Android's hardware Keystore provider.

---

## 5. REST API Documentation

| Method | Endpoint | Access | Purpose |
| :--- | :--- | :---: | :--- |
| `GET` | `/health` | Public | System status and service health check |
| `POST` | `/api/auth/request-otp` | Public | Initiates OTP delivery (rate limited to 10 req/15 min) |
| `POST` | `/api/auth/verify-otp` | Public | Validates 6-digit OTP and returns signed JWT Bearer token |
| `GET` | `/api/auth/me` | Bearer | Returns profile of the authenticated user |
| `GET` | `/api/products` | Public | Lists pharmacy catalogue with search and category filters |
| `GET` | `/api/products/:id` | Public | Returns comprehensive details of a specific medicine |
| `GET` | `/api/addresses` | Bearer | Retrieves delivery addresses belonging to the authenticated user |
| `POST` | `/api/addresses` | Bearer | Creates a new validated Indian delivery address |
| `PUT` | `/api/addresses/:id` | Bearer | Updates existing delivery address with ownership verification |
| `DELETE` | `/api/addresses/:id` | Bearer | Deletes delivery address |
| `POST` | `/api/orders` | Bearer | Submits order with idempotency check, price validation, and atomic stock decrement |
| `GET` | `/api/orders` | Bearer | Retrieves chronological order history for the authenticated user |
| `GET` | `/api/orders/:id` | Bearer | Returns detailed order record with timeline and immutable snapshots |

---

## 6. Order Evidence Reconciliation

### 6.1. Explanation of Historical Discrepancies
During early Phase 4/5 reporting, two discrepancies occurred:
1. **Quantity Discrepancy:** The Phase 5 text described the Paracetamol quantity being incremented to 2, but the raw JSON snapshot logged in one earlier test reflected 1 unit.
2. **Reused Order Reference:** The reference `#MC-2026-2368` was cited in multiple places with differing line-item contents.

### 6.2. Fresh Verified Orders
To ensure complete transparency and zero fabrication, fresh live orders were generated on the running Express backend and verified directly against MongoDB:

#### Primary Hardware Verified Order (`#MC-2026-3947`)
- **Order Reference:** `#MC-2026-3947`
- **MongoDB Object ID:** `6af84a37de616014ffa912c4`
- **User ID:** `6af847f7de616014ffa9127e`
- **Idempotency Key:** `4f925d49-5dd3-43fd-adac-a516ca3e2245`
- **Submission Source:** Native Android UI on physical smartphone `CPH2333`
- **Line Items:**
  1. `med_01` (Paracare 650mg Tablets) • **Quantity: 2** • Unit: ₹34.00 (3400 paise) • Line Total: ₹68.00 (6800 paise)
  2. `med_03` (AyurTulsi Cough Relief Herbal Syrup) • **Quantity: 1** • Unit: ₹85.00 (8500 paise) • Line Total: ₹85.00 (8500 paise)
- **Subtotal:** ₹153.00 (15300 paise)
- **Delivery Fee:** ₹30.00 (3000 paise)
- **Packaging Fee:** ₹14.00 (1400 paise)
- **Final Total Amount:** **₹197.00** (19700 paise)
- **Payment Method:** `UPI_DEMO`
- **Order Status:** `PROCESSING`
- **Delivery Address:** Flat 402, Block B, Shanti Vihar, 12th Main Road, HAL 2nd Stage, Bengaluru, Karnataka - 560038
- **Created At:** `2026-11-14T11:47:03.890Z`

#### Secondary API Verified Order (`#MC-2026-5796`)
- **Order Reference:** `#MC-2026-5796`
- **MongoDB Object ID:** `6af84864de616014ffa9129e`
- **Line Items:**
  1. `med_01` (Paracare 650mg Tablets) • **Quantity: 2** • Line Total: ₹68.00 (6800 paise)
  2. `med_02` (Cetriz-L 10mg Tablets) • **Quantity: 1** • Line Total: ₹22.00 (2200 paise)
- **Subtotal:** ₹90.00 (9000 paise) + Delivery ₹30.00 + Packaging ₹14.00 = **Total: ₹134.00** (13400 paise)
- **Idempotency Key:** `order_reconciled_1794656356462`
- **Created At:** `2026-11-14T11:39:16.493Z`

Both orders are confirmed stored in MongoDB and rendered in the live Android Order History list.

---

## 7. Critical Reliability & Edge Case Testing

1. **Out-of-Stock Protection:** Attempting to place an order containing an out-of-stock item (`stockCount == 0`) returns HTTP `400 Bad Request` with code `OUT_OF_STOCK`.
2. **Expired Batch Protection:** Medicines flagged as expired (`isExpired: true`) are rejected with HTTP `400 Bad Request` (`MEDICINE_EXPIRED`).
3. **Quantity Ceiling Enforcement:** Requests for more than 5 packs trigger HTTP `400 Bad Request` (`INVALID_QUANTITY`).
4. **Server-Side Price Authority:** Client-supplied prices are discarded. The server computes financial totals exclusively from verified catalogue prices in MongoDB.
5. **Multi-Item Atomic Decrement with Rollback:** In a multi-item order where a subsequent item fails stock validation, earlier decrements are rolled back using `$inc: { stockCount: +quantity }`. Zero inventory is leaked.
6. **Concurrent Duplicate Checkout Protection:** Multiple simultaneous checkout requests with the same `idempotencyKey` encounter MongoDB's unique index constraint. The winning request creates the order (HTTP 201), while concurrent collisions roll back their decrements and return the winning order (HTTP 200). Exactly 1 order is stored and stock is decremented exactly once.

---

## 8. Security & Privacy Audit

1. **Stateless JWT Authorization:** Protected endpoints require `Authorization: Bearer <token>`. Unauthenticated requests receive HTTP 401; unauthorized resource access (e.g., accessing another user's address or order) returns HTTP 403.
2. **Timing-Safe OTP:** Verification utilizes `crypto.timingSafeEqual` to thwart timing-based side-channel attacks.
3. **Production OTP Isolation:** In production mode (`NODE_ENV=production`), development OTP bypass is disabled, and cryptographically secure random codes are generated.
4. **HTTP Header Hardening:** `helmet` applies defensive headers (`nosniff`, `SAMEORIGIN`, disabling `X-Powered-By`).
5. **Rate Limiting:** Global limiter prevents volumetric denial of service; auth limiter restricts brute-force guessing.
6. **Network Security Configuration:** Android `network_security_config.xml` strictly disables cleartext traffic across public domains, whitelisting local loopback addresses (`10.0.2.2`, `127.0.0.1`) only for local testing.
7. **Synthetic Data Compliance:** Catalogue uses 100% fictional formulation names (`Paracare`, `Cetriz-L`, `AyurTulsi`, `Panto-D`, `OmniTherm`, etc.). No real commercial brand trademarks, personal phone numbers, or private addresses appear in public screenshots or reports.

---

## 9. Performance & APK Build Audit

- **Debug APK Location:** `submission/apk/MediCart-debug.apk`
- **File Size:** **16.5 MB** (17,300,474 bytes)
- **Min SDK:** API Level 24 (Android 7.0 Nougat) | **Target SDK:** API Level 34 (Android 14)
- **Release Signing Status:**
  - `MediCart-debug.apk` is signed with the standard Android debug certificate (`androiddebugkey`).
  - The `release` build type in `app/build.gradle.kts` does not include embedded keystore secrets. Production signing credentials should be injected via CI/CD environment variables (`KEYSTORE_PASSWORD`, `KEY_ALIAS`).
  - In accordance with submission instructions, we explicitly declare that **only a debug APK has been generated and verified**.

---

## 10. Automated Test Results

### 10.1. Android Client Test Suite (30 Tests)
Command: `.\gradlew.bat testDebugUnitTest`  
Result: **BUILD SUCCESSFUL** (30/30 tests passed in 41s)
- **Domain & Validation Tests (10 tests):**
  - Pricing calculation, Indian PIN validation, 5-pack quantity limits, address formatting, discount logic.
- **Retrofit & Network Acceptance Tests (20 tests):**
  - `MockWebServer` verification of token interceptors, 401 unauthorized handling, 403 forbidden checks, offline database caching, cart persistence, and order submission.

### 10.2. Backend REST API Test Suite (22 Tests)
Command: `cd backend && npm test`  
Result: **PASS** (22/22 tests passed in 6.2s)
- Health check endpoint verification
- OTP request and constant-time verification
- Protected route authorization enforcement
- Catalogue search and category filtering
- User address isolation (cross-user access forbidden)
- Order calculation, snapshot storage, and reference generation
- Empty cart, out-of-stock, and expired medicine rejection
- Quantity cap and price tampering prevention
- Idempotency key replay protection
- Atomic multi-item stock decrement and rollback
- Rapid concurrent duplicate checkout collision handling

---

## 11. Known Limitations & Future Roadmap

1. **Local Demonstration Server:** Unless deployed to a public cloud provider, the backend operates locally. Physical hardware evaluation requires `adb reverse tcp:5000 tcp:5000` over USB.
2. **Simulated Payment Gateway:** Payment flows are simulated for assessment safety; integration with live Razorpay or Cashfree SDKs is deferred to production deployment.
3. **Prescription Upload OCR:** Prescription images can be attached locally; automated AI OCR validation for scheduled drugs remains a prospective feature.
4. **Push Notifications:** Foreground state updates are handled via reactive StateFlow; FCM (Firebase Cloud Messaging) integration can be added for background order status alerts.
