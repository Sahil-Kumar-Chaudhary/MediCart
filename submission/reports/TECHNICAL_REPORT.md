# MediCart – Technical Audit, Security & Quality Assurance Report

**Project:** MediCart Online Pharmacy Android Application & REST API  
**Author:** Sahil Kumar Chaudhary  
**Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Branch:** `main`  
**Target Platform:** Android 13 (API 33) / Tested on Hardware Device: `CPH2333`  
**Backend:** Node.js, Express.js, MongoDB (Mongoose), JWT, Zod  

---

## 1. Executive Summary

This document presents the complete technical quality assurance, security audit, architecture evaluation, and verification results for the **MediCart** healthcare e-commerce platform.

The system was audited across all layers:
1. **Frontend Client:** Android Jetpack Compose native application adhering to Material Design 3 and a refined Emerald Green (`#087E6B`) + Warm Off-White (`#F7F9F7`) brand design system.
2. **Local Persistence & Security:** Native SQLite database caching catalogue data, cart items, addresses, and orders, backed by Android Jetpack `EncryptedSharedPreferences` with AES-256 GCM encryption.
3. **Networking Layer:** Retrofit 2 + OkHttp 4 client with token interceptors, exponential retry backoff, dynamic device/emulator loopback resolution (`10.0.2.2` vs `127.0.0.1`), and offline fallback.
4. **Backend API:** Production-grade Express REST API with MongoDB Atlas schema validation, HMAC-SHA256 OTP verification, JWT session tokens, Helmet security headers, rate limiting, and atomic inventory decrement with rollback.
5. **Physical Hardware Verification:** Live execution of the complete 25-step ordering journey on a physical Android device (`CPH2333` running Android 13).

---

## 2. Test Execution Matrix

| Test Suite | Framework | Target / Scope | Total Tests | Passed | Failed | Status |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| **Android Domain Unit Tests** | JUnit 4 + Kotlin | Pricing, Fee rules, Quantity caps, Validation | 10 | 10 | 0 | **PASS** |
| **Android Retrofit Integration Tests** | MockWebServer + JUnit 4 | Network interceptors, Token auth, Offline fallback | 20 | 20 | 0 | **PASS** |
| **Backend REST API Acceptance** | Jest + Supertest | Auth, Catalogue, Address isolation, Order transactions | 22 | 22 | 0 | **PASS** |
| **Physical Hardware Live E2E** | ADB + CPH2333 | Full 25-step customer purchasing journey | 25 | 25 | 0 | **PASS** |
| **TOTAL** | — | — | **77** | **77** | **0** | **100% PASS** |

### Automated Test Logs Summary
- **Android Unit Tests:** `.\gradlew.bat testDebugUnitTest` → `BUILD SUCCESSFUL` (30 tests completed in 10s).
- **Android APK Assembly:** `.\gradlew.bat assembleDebug` → `BUILD SUCCESSFUL` (`app-debug.apk`, 19.43 MB).
- **Backend Tests:** `npm test` → 22/22 tests passing in 3.5s across Auth, Products, Addresses, Orders, Rollback, and Idempotency.

---

## 3. Physical Device Verification (CPH2333 - Android 13)

The application was installed and verified on a physical connected smartphone (`CPH2333`, Android 13, Device ID `9fca489b` via ADB reverse port-forwarding).

### Live Order Milestone
- **Order Reference Generated:** `#MC-2026-2368`
- **Idempotency Key:** `bb249f71-f58d-43f2-9b20-56b9a64b7103`
- **Selected Item:** Paracetamol 650 mg (₹32.00) × 1 + Cetirizine 10 mg (₹18.00) × 1 + Cough Syrup (₹35.00) = Subtotal ₹85.00
- **Fees Added:** Delivery Fee ₹30.00, Hygienic Packaging Fee ₹14.00
- **Total Amount Charged:** ₹129.00 (12900 paise)
- **Payment Method:** UPI (Simulated Instant Confirmation)
- **Database Verification:** Order record verified stored inside MongoDB with complete item snapshots, timestamp, and status `PROCESSING`.

---

## 4. Critical Reliability & Edge Case Testing

### 4.1. Out of Stock Enforcement
- **Scenario:** Attempting to checkout a product whose `stockCount` is 0 or `isAvailable: false`.
- **Backend Behavior:** Rejected immediately with HTTP `400 Bad Request` and error code `OUT_OF_STOCK`.
- **Client Behavior:** Cart alerts user with inline badge and disables the "Proceed to Checkout" action until corrected.

### 4.2. Expired Medicine Batch Protection
- **Scenario:** An expired medicine batch (`isExpired: true` or `expiryDate < now`).
- **Backend Behavior:** Strict rejection with HTTP `400 Bad Request` and error code `MEDICINE_EXPIRED`. Expired items cannot be dispensed under Indian pharmacy safety compliance regulations.

### 4.3. Quantity Cap Enforcement (Max 5 Units)
- **Scenario:** Requesting > 5 units of prescription or over-the-counter medicine.
- **Backend Behavior:** Rejected with HTTP `400 Bad Request` (`MAX_QUANTITY_EXCEEDED`).
- **Client Behavior:** Counter stepper enforces upper limit of 5 packs with haptic feedback.

### 4.4. Price Tampering Defense
- **Scenario:** Malicious client modifies item price in payload from ₹65.00 to ₹1.00.
- **Backend Behavior:** The server completely ignores client-supplied monetary values. Pricing is dynamically queried directly from the verified database catalogue, and subtotal/total calculations are computed strictly server-side.

### 4.5. Atomic Inventory Decrement with Automatic Rollback
- **Scenario:** An order contains 2 items: Item A (in stock) and Item B (fails validation / out of stock).
- **Backend Behavior:** Item A's decrement is recorded in an audit list. When Item B fails, a compensating transaction executes `$inc: { stockCount: +quantity }` to restore Item A's inventory to its exact pre-request count. No stock is leaked.

### 4.6. Idempotency Key Replay & Concurrent Checkout Protection
- **Scenario 1 (Network Retry):** A client submits an order, receives a network timeout, and resends the exact same `idempotencyKey`.
  - **Result:** The server queries existing orders by `idempotencyKey`, finds the already-created order `#MC-2026-2368`, and returns HTTP `200 OK` with the existing order details without double-charging or deducting extra stock.
- **Scenario 2 (Rapid Concurrent Clicks):** Three simultaneous requests are dispatched concurrently with the same `idempotencyKey`.
  - **Result:** MongoDB unique index prevents duplicate order records. One request wins (HTTP 201), the other two detect the unique constraint collision, roll back any concurrent stock decrement, and return the winning order (HTTP 200). Exactly 1 order is persisted, and inventory is decremented by exactly 1 unit.

---

## 5. Security & Privacy Audit

### 5.1. Authentication & Session Security
- **JWT Signature:** Stateless JSON Web Tokens signed with server-side HMAC-SHA256 (`JWT_SECRET`) with configurable expiration (7 days).
- **OTP Security:** Time-based 6-digit OTP verified in constant time (`crypto.timingSafeEqual`) to prevent side-channel timing attacks. Failed attempts increment an exponential delay counter.
- **Android Token Storage:** JWT token and user profile are encrypted using Jetpack Security `EncryptedSharedPreferences` backed by the Android hardware Keystore with `AES256_SIV` key encryption and `AES256_GCM` value encryption.

### 5.2. Network Security
- **Cleartext Traffic Restriction:** Strict `network_security_config.xml` disables `cleartextTrafficPermitted` across public domains. Cleartext HTTP is exclusively permitted for localhost development addresses (`10.0.2.2` and `127.0.0.1`).
- **HTTP Security Headers:** Helmet middleware configures `X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN`, `Strict-Transport-Security`, and disabled `X-Powered-By`.
- **CORS Protection:** Configurable whitelist restricting browser access to recognized development and staging origins.
- **Rate Limiting:** Global rate limiter (100 requests per 15 minutes) and strict Auth rate limiter (10 OTP requests per 15 minutes) prevent brute-force attacks.

### 5.3. Secret & Credential Hygiene
- All private keys, JWT secrets, and database URIs reside in `.env` (gitignored).
- Source repository contains zero committed passwords, tokens, API keys, or keystores.
- `.gitignore` verified to exclude `.env`, `*.jks`, `*.keystore`, `*.apk`, and build caches.

---

## 6. Architecture & Offline Capability

```
+-------------------------------------------------------------------------+
|                        Android Client (CPH2333)                        |
|                                                                         |
|  [Jetpack Compose UI (Emerald Green)] <---> [ViewModels (StateFlow)]   |
|                                                        |                |
|                                          [PharmacyRepository]           |
|                                           /                 \           |
|             (Network Available)          /                   \ (Offline)|
|                                         v                     v         |
|                             [Retrofit 2 + OkHttp]     [SQLite Cache]    |
|                             [EncryptedSharedPrefs]    (MediCartDatabase)|
+----------------------------------------|--------------------------------+
                                         | HTTP/REST (Port 5000)
                                         v
+-------------------------------------------------------------------------+
|                        Node.js / Express Server                         |
|                                                                         |
|  [Rate Limiter] -> [Helmet] -> [Auth Middleware (JWT)] -> [Zod Validator]|
|                                         |                               |
|                                  [Controllers]                          |
|                                         |                               |
|                                  [Services Layer]                       |
|                          (Atomic Decrement & Rollback)                  |
|                                         |                               |
|                                [Mongoose ODM]                           |
+-----------------------------------------|-------------------------------+
                                          v
                              [MongoDB Database Engine]
```

When network connectivity is unavailable:
1. Catalog browsing seamlessly switches to the SQLite local cache (`medicart_offline.db`).
2. Cart modifications persist locally across app restarts.
3. Once network connectivity is restored, cached operations synchronize cleanly with the remote backend.

---

## 7. Submission Artifacts Inventory

- **Debug Application APK:** `app/build/outputs/apk/debug/app-debug.apk` (19.43 MB)
- **High-Resolution Screenshots:** `submission/screenshots/` (10 verified screens)
- **Demonstration Script:** `submission/demo/DEMO_SCRIPT.md`
- **Submission Overview:** `submission/README.md`
- **Root Documentation:** `README.md`
