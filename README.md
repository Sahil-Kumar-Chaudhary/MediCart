# MediCart – Android Online Pharmacy Application & Cloud REST API

![Android 13](https://img.shields.io/badge/Android-13%20%7C%20API%2033-green?style=flat&logo=android)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-teal?style=flat&logo=jetpackcompose)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?style=flat&logo=kotlin)
![Node.js](https://img.shields.io/badge/Node.js-24%20LTS-darkgreen?style=flat&logo=node.js)
![Render](https://img.shields.io/badge/Render-Live%20Production-blue?style=flat&logo=render)
![MongoDB Atlas](https://img.shields.io/badge/MongoDB-Atlas%20M0-forestgreen?style=flat&logo=mongodb)
![Tests](https://img.shields.io/badge/Tests-88%2F88%20Passed%20(100%25)-success?style=flat)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat)

**Candidate / Author:** Sahil Kumar Chaudhary  
**Organization / Evaluator:** ALGIONS AI LABS PRIVATE LIMITED  
**Official Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Live Public Backend:** [https://medicart-backend-lmcz.onrender.com/](https://medicart-backend-lmcz.onrender.com/)  
**Live Backend Health Check:** [https://medicart-backend-lmcz.onrender.com/health](https://medicart-backend-lmcz.onrender.com/health)  
**Database:** MongoDB Atlas Production Replica Set (`AWS ap-south-1` Mumbai)  
**Verified Hardware Smartphone:** OPPO Reno7 5G (`CPH2333`, Android 13 / API 33)  
**Master Demonstration Video:** [`submission/demo/video/MediCart-Demonstration.mp4`](submission/demo/video/MediCart-Demonstration.mp4) (04:46.90 Full HD 1080p)

---

## 1. Executive Summary & Brand Transformation

**MediCart** is a production-grade, full-stack online pharmacy mobile application and cloud platform built specifically for Indian consumers. MediCart adheres strictly to **Material Design 3**, an **8dp spacing rhythm**, and an authoritative **Emerald Green (`#087E6B`) + Warm White (`#F7F9F7`)** identity tailored for healthcare logistics and fintech minimalism.

### 🎨 Brand Color Tokens

| Semantic Token | HEX Value | Jetpack Compose Token | Usage / Purpose |
|---|---|---|---|
| **Primary** | `#087E6B` | `Color(0xFF087E6B)` | Core brand accent, primary CTAs, active navigation |
| **Primary Dark** | `#075B4F` | `Color(0xFF075B4F)` | Pressed states, high-contrast badges, deep surfaces |
| **Secondary Mint** | `#DDF1EA` | `Color(0xFFDDF1EA)` | Soft container surfaces, quantity steppers, status tags |
| **App Background** | `#F7F9F7` | `Color(0xFFF7F9F7)` | Warm off-white background preventing eye fatigue |
| **Surface** | `#FFFFFF` | `Color(0xFFFFFFFF)` | Cards, dialogs, bottom sheets, application bars |
| **Primary Text** | `#172B27` | `Color(0xFF172B27)` | Deep forest charcoal for crisp legibility |
| **Secondary Text**| `#71817B` | `Color(0xFF71817B)` | Muted slate-green for dosage labels, metadata |
| **Border / Divider**| `#E5ECE8` | `Color(0xFFE5ECE8)` | Clean subtle card borders and structural dividers |
| **Success** | `#16865B` | `Color(0xFF16865B)` | Confirmed orders, deliverable PINs, verified stock |
| **Warning** | `#E49A32` | `Color(0xFFE49A32)` | Low stock (< 3 units) and regulatory limit advisories |
| **Error / Destructive**| `#D64B4B` | `Color(0xFFD64B4B)` | Network offline, expired batches, invalid credentials |

---

## 2. Complete Authentication Redesign

In accordance with enterprise mobile standards (matching previous projects *JanSaarthi* and *HostelCare*), MediCart features a complete phone-number-and-password authentication architecture with full registration, OTP verification, and secure password recovery:

```
+---------------------------------------------------------------------------------------------------------+
|                                    MediCart Authentication Architecture                                  |
|                                                                                                         |
|  [Phone + Password Login]  --->  [Bcrypt Verification]  --->  [JWT Bearer Token (tokenVersion)]         |
|             |                                                                 |                         |
|             +---> [Registration Screen] ---> [Live Criteria Validation] ------> [Hardware Keystore]     |
|             |                                (8+ chars, upper, lower, digit)  [EncryptedSharedPrefs]    |
|             |                                                                                           |
|             +---> [Forgot Password Flow]                                                                |
|                        |                                                                                |
|                        v (Anti-Enumeration 200 OK)                                                      |
|                   [6-Digit OTP Verification] ---> [Set New Password] ---> [tokenVersion Revocation]     |
+---------------------------------------------------------------------------------------------------------+
```

### Key Security & User Experience Highlights:
1. **Phone Number + Password Login:** Users authenticate with their 10-digit Indian phone number (`+91`) and password. Includes inline password visibility toggle and non-blocking error banners.
2. **Interactive Registration with Live Validation Checklist:** 
   - Full Name, 10-digit Indian mobile number, password, and confirmation password.
   - Live visual checklist dynamically validating:
     - Minimum 8 characters
     - At least one uppercase letter (`A-Z`)
     - At least one lowercase letter (`a-z`)
     - At least one digit (`0-9`) or special character (`!@#$%^&*`)
   - Real-time password matching validation indicator.
3. **Anti-Enumeration Forgot Password Flow:** Submitting any valid 10-digit phone number returns a generic success response (`HTTP 200 OK`) preventing user enumeration attacks.
4. **Segmented 6-Digit OTP Keypad:** Realistic numeric entry with 5-minute constant-time verification (`crypto.timingSafeEqual`).
5. **Session Revocation via `tokenVersion`:** Resetting password atomically increments the user's `tokenVersion` in MongoDB Atlas, instantly invalidating all existing JWT sessions across active devices.
6. **Encrypted Session Management:** Android Jetpack Security (`EncryptedSharedPreferences`) stores JWT tokens and user metadata encrypted via **AES-256 GCM** backed by the Android hardware Keystore.

---

## 3. Indian Market Requirements & Domain Compliance

- **Indian Currency (₹):** Standardized INR symbol across all catalogue items, cart breakdowns, invoices, and delivery fees.
- **Indian Mobile Authentication:** Standard `+91` prefix with 10-digit number validation.
- **6-Digit Postal PIN Code Check:** Real-time Indian PIN code validation ensuring delivery radius check (`560038` Indiranagar, Bengaluru default).
- **Indian Address Formats:** Structured fields for Flat/House No., Street/Locality, Landmark, City, State, and PIN code with address tags (Home, Work, Other).
- **Simulated Payment Gateway:** Explicit demo sandbox options:
  - ⚡ **UPI (Demo):** PhonePe, Google Pay, Paytm, or VPA.
  - 💵 **Cash on Delivery:** Pay on delivery via Cash or QR.
  - 💳 **Card (Demo):** RuPay, Visa, Mastercard simulated sandbox.
  - *Compliance Notice: Clearly communicates that payment is simulated for assessment verification.*
- **Regulatory 5-Pack Safety Cap:** Maximum 5 packs per medicine per order enforced on both Android UI stepper and backend schema to prevent medication abuse and unauthorized reselling.
- **Transparent Bill Breakdown:** Itemized invoice in integer paise (Subtotal, Delivery Fee ₹30, Hygienic Packaging ₹14, Total Amount).
- **20-Product Fictional Catalogue:** Fictional generic formulations (e.g., `Paracare 650mg`, `Cetriz-L 10mg`, `Panto-D SR`, `AyurTulsi Syrup`) with complete active ingredients, dosage warnings, and storage rules.

---

## 4. Full-Stack System Architecture

```
+---------------------------------------------------------------------------------------------------------+
|                        Android Client (OPPO Reno7 5G / CPH2333 / Android 13)                            |
|                                                                                                         |
|  [Jetpack Compose UI (Emerald Green)] <---> [ViewModels (StateFlow / Coroutines)]                      |
|                                                        |                                                |
|                                          [PharmacyRepository]                                           |
|                                           /                 \                                           |
|             (Network Available)          /                   \ (Offline Fallback)                       |
|                                         v                     v                                         |
|                             [Retrofit 2 + OkHttp 3]    [SQLite Database Helper]                         |
|                             [EncryptedSharedPrefs]     (Local persistent cache)                         |
+----------------------------------------|----------------------------------------------------------------+
                                         | HTTPS / TLS 1.3 (Port 443)
                                         v
+---------------------------------------------------------------------------------------------------------+
|                        Render Production Web Service (medicart-backend)                                 |
|                        Public URL: https://medicart-backend-lmcz.onrender.com                           |
|                                                                                                         |
|  [Rate Limiter] -> [Helmet Headers] -> [Auth Middleware (JWT)] -> [Input Validator]                     |
|                                         |                                                               |
|                           [Modular Controllers & Services]                                              |
|                           (Atomic Inventory Decrement & Rollback)                                       |
|                                         |                                                               |
|                                   [Mongoose ODM]                                                        |
+-----------------------------------------|---------------------------------------------------------------+
                                          | TLS / Mongoose (Port 27017)
                                          v
+---------------------------------------------------------------------------------------------------------+
|                           MongoDB Atlas Cloud Cluster (M0 Free Tier)                                    |
|                           AWS Mumbai Region (ap-south-1)                                                |
|                                                                                                         |
|  - Auto-seeding 20 generic medicines on cold start                                                      |
|  - Idempotency uniqueness index (avoids duplicate order processing)                                     |
|  - Encrypted Bcrypt password hashes and tokenVersion tracking                                           |
+---------------------------------------------------------------------------------------------------------+
```

---

## 5. Automated Testing & Verification Matrix (88 / 88 PASS)

| Test Layer | Test Suite / Class | Framework | Tests | Passed | Status |
| :--- | :--- | :--- | :---: | :---: | :---: |
| **Android Unit Tests** | `com.medicart.app.MediCartAuthRedesignTest` | JUnit 4 + Kotlin | 19 | 19 | **100% PASS** |
| **Android Integration**| `com.medicart.app.MediCartRetrofitIntegrationTest` | MockWebServer + JUnit 4 | 20 | 20 | **100% PASS** |
| **Android Domain Logic**| `com.medicart.app.MediCartValidationTest` | JUnit 4 | 10 | 10 | **100% PASS** |
| **Backend Auth Redesign**| `backend/tests/authRedesign.test.js` | Jest + Supertest | 17 | 17 | **100% PASS** |
| **Backend REST API** | `backend/tests/api.test.js` | Jest + Supertest | 22 | 22 | **100% PASS** |
| **Hardware Live E2E** | Real Device (`CPH2333` / Android 13) | Physical Execution | 25 | 25 | **100% PASS** |
| **TOTAL** | **Entire Application Stack** | — | **88** | **88** | **100% PASS** |

### Verified Reconciled Order Records (Live MongoDB Atlas):
1. **Live Cloud Render + Atlas Order:** Reference **`#MC-2026-3819`** • Subtotal ₹187.00 (`Paracare 650mg` × 3, `AyurTulsi` × 1) + Delivery ₹30.00 + Packaging ₹14.00 = **Total: ₹231.00** (`idempotencyKey: e0b2c4ce-8f5b-429b-a232-afd06828947f`, MongoDB ID: `6af95011da0736228f500a46`, live at `https://medicart-backend-lmcz.onrender.com/`).
2. **Hardware Verified Order:** Reference **`#MC-2026-1359`** • Subtotal ₹180.00 (`Paracare 650mg` × 5, regulatory cap demonstrated) + Delivery ₹20.00 + Packaging ₹9.00 = **Total: ₹209.00** (`idempotencyKey: f92a7f53-b3c9-4416-8f31-897b6ec429e7`).
3. **Secondary API Test Order:** Reference **`#MC-2026-3947`** • Subtotal ₹153.00 (`Paracare 650mg` × 2, `AyurTulsi` × 1) + Delivery ₹30.00 + Packaging ₹14.00 = **Total: ₹197.00** (`idempotencyKey: 4f925d49-5dd3-43fd-adac-a516ca3e2245`).

---

## 6. Demonstration Video & Production Suite

A complete, broadcast-ready 1080p landscape demonstration video has been produced and included directly in the repository:

- 🎬 **Master Video (MP4):** [`submission/demo/video/MediCart-Demonstration.mp4`](submission/demo/video/MediCart-Demonstration.mp4) (Duration: **4m 46.9s**, 1920×1080, 30.00 FPS, 13.54 MB).
- 💬 **Standalone Subtitles (SRT):** [`submission/demo/video/MediCart-Demonstration.srt`](submission/demo/video/MediCart-Demonstration.srt) (Sub-millisecond timestamps).
- 🎙️ **Master Narration (MP3):** [`submission/demo/video/MediCart-Voiceover.mp3`](submission/demo/video/MediCart-Voiceover.mp3) (Microsoft Edge Neural Indian-English `en-IN-PrabhatNeural`).
- 📝 **Narration Script (MD):** [`submission/demo/video/MediCart-Voiceover-Script.md`](submission/demo/video/MediCart-Voiceover-Script.md) (Full 9-scene script with timestamps).
- 📋 **Production Notes (MD):** [`submission/demo/video/VIDEO_PRODUCTION_NOTES.md`](submission/demo/video/VIDEO_PRODUCTION_NOTES.md) (Hardware, audio mixing, and codec specifications).

---

## 7. Application Screenshots Gallery

| Screen | File Path | Description |
| :-: | :--- | :--- |
| **01** | [`submission/screenshots/01_login_phone_password.png`](submission/screenshots/01_login_phone_password.png) | Phone Number & Password Login with eye toggle |
| **02** | [`submission/screenshots/02_registration_screen.png`](submission/screenshots/02_registration_screen.png) | New User Registration with name, phone, passwords |
| **03** | [`submission/screenshots/03_password_criteria_validation.png`](submission/screenshots/03_password_criteria_validation.png) | Dynamic 4-point password complexity checklist |
| **04** | [`submission/screenshots/04_forgot_password_screen.png`](submission/screenshots/04_forgot_password_screen.png) | Phone number entry with anti-enumeration protection |
| **05** | [`submission/screenshots/05_otp_verification_keypad.png`](submission/screenshots/05_otp_verification_keypad.png) | Segmented 6-digit OTP verification screen |
| **06** | [`submission/screenshots/06_set_new_password_matching.png`](submission/screenshots/06_set_new_password_matching.png) | Set new password with matching validation |
| **07** | [`submission/screenshots/07_login_error_banner.png`](submission/screenshots/07_login_error_banner.png) | Non-blocking inline error banner on 401 unauthorized |
| **08** | [`submission/screenshots/08_home_authenticated_render.png`](submission/screenshots/08_home_authenticated_render.png) | Authenticated home screen connected live to Render |
| **09** | [`submission/screenshots/09_profile_verified_account.png`](submission/screenshots/09_profile_verified_account.png) | User profile with verified badge and offline mode |
| **10** | [`submission/screenshots/10_after_logout_screen.png`](submission/screenshots/10_after_logout_screen.png) | Clean session teardown returning to login screen |
| **11** | [`submission/screenshots/04_catalogue.png`](submission/screenshots/04_catalogue.png) | Filterable 20-product generic medicine catalogue |
| **12** | [`submission/screenshots/05_product_details.png`](submission/screenshots/05_product_details.png) | Medicine clinical details, dosage warnings, return policy |
| **13** | [`submission/screenshots/06_cart.png`](submission/screenshots/06_cart.png) | Shopping cart with regulatory 5-pack cap and fees |
| **14** | [`submission/screenshots/07_checkout.png`](submission/screenshots/07_checkout.png) | Address selection, simulated UPI sandbox payment |
| **15** | [`submission/screenshots/08_order_confirmation.png`](submission/screenshots/08_order_confirmation.png) | Live confirmed order reference `#MC-2026-3819` |
| **16** | [`submission/screenshots/10_order_details.png`](submission/screenshots/10_order_details.png) | 4-stage tracking timeline (Dispensing & Packing) |

---

## 8. Installation & Quick Start

### 8.1. Instant Cloud Evaluation (No Laptop or ADB Required)

Install the live-connected APK directly onto any physical Android device running Android 8.0+ (API 26+):

```bash
# Install live-connected APK targeting Render HTTPS backend
adb install -r submission/apk/MediCart-v2.4-live-debug.apk
```

**Pre-Configured Demo Credentials:**
- **Demo Phone Number:** `9876543210`
- **Demo Password:** `Demo@1234`
- **Demo OTP Code:** `487210` (for testing Registration or Forgot Password)

### 8.2. Local Development & Automated Test Execution

```bash
# 1. Run all 39 backend tests
cd backend
npm install
npm test

# 2. Run all 49 Android unit & integration tests
cd ..
.\gradlew.bat testDebugUnitTest

# 3. Build live-connected debug APK
.\gradlew.bat clean assembleDebug -PbackendUrl="https://medicart-backend-lmcz.onrender.com/"
```

---

## 9. Submission Package & GitHub Release

All materials requested for internship assessment by **ALGIONS AI LABS PRIVATE LIMITED** are catalogued under [`submission/`](submission/):

- 📦 **Live Release APK (16.6 MB):** [`submission/apk/MediCart-v2.4-live-debug.apk`](submission/apk/MediCart-v2.4-live-debug.apk)
- 📦 **Standard Debug APK (16.6 MB):** [`submission/apk/MediCart-debug.apk`](submission/apk/MediCart-debug.apk)
- 🎬 **Demonstration Video (13.5 MB):** [`submission/demo/video/MediCart-Demonstration.mp4`](submission/demo/video/MediCart-Demonstration.mp4)
- 💬 **Subtitles (SRT):** [`submission/demo/video/MediCart-Demonstration.srt`](submission/demo/video/MediCart-Demonstration.srt)
- 📑 **Comprehensive Technical Report:** [`submission/reports/TECHNICAL_REPORT.md`](submission/reports/TECHNICAL_REPORT.md)
- 📋 **Final QA & Security Audit Report:** [`submission/reports/FINAL_TEST_REPORT.md`](submission/reports/FINAL_TEST_REPORT.md)
- 🎬 **15-Stage Video Demo Script:** [`submission/demo/DEMO_SCRIPT.md`](submission/demo/DEMO_SCRIPT.md)
- 📱 **Physical Hardware Screenshots:** [`submission/screenshots/`](submission/screenshots/)
- 📖 **Submission Guide:** [`submission/README.md`](submission/README.md)
