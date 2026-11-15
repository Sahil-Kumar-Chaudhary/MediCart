# MediCart – Official Submission Package

**Candidate / Author:** Sahil Kumar Chaudhary  
**Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Project Title:** MediCart – Android Online Pharmacy Application & REST API  
**Official Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Official GitHub Release:** [v2.4.0 Release Page & APK Downloads](https://github.com/Sahil-Kumar-Chaudhary/MediCart/releases/tag/v2.4.0)  
**Direct Live APK Download:** [`MediCart-v2.4-live-debug.apk`](https://github.com/Sahil-Kumar-Chaudhary/MediCart/releases/download/v2.4.0/MediCart-v2.4-live-debug.apk)  
**Main Branch:** `main`  
**Verified On Hardware Device:** `CPH2333` (OPPO Reno7 5G, Android 13 / API Level 33)  
**Live Public Backend:** [https://medicart-backend-lmcz.onrender.com/](https://medicart-backend-lmcz.onrender.com/)  
**Live Backend Health Check:** [https://medicart-backend-lmcz.onrender.com/health](https://medicart-backend-lmcz.onrender.com/health)  
**Database:** MongoDB Atlas Production Cluster (`AWS ap-south-1` Mumbai)  
**Official Demonstration Video:** [`submission/demo/video/MediCart-Demonstration.mp4`](demo/video/MediCart-Demonstration.mp4) (04:46.90 Full HD 1080p)

---

## 1. Directory Structure

```
submission/
├── apk/
│   ├── MediCart-v2.4-live-debug.apk   <- Verified live APK connected to Render HTTPS backend (16.62 MB)
│   └── MediCart-debug.apk             <- Clean installable Android debug APK
├── demo/
│   ├── video/
│   │   ├── MediCart-Demonstration.mp4 <- Master 1080p demonstration video (04:46.90, 13.54 MB)
│   │   ├── MediCart-Demonstration.srt <- Synchronized standalone SubRip subtitle file
│   │   ├── MediCart-Voiceover.mp3     <- Master Indian-English voiceover narration audio
│   │   ├── MediCart-Voiceover-Script.md <- Full 9-scene narration script with cues
│   │   └── VIDEO_PRODUCTION_NOTES.md  <- Audio, hardware capture, and codec engineering notes
│   └── DEMO_SCRIPT.md                 <- 15-Stage, 4.5-minute technical presentation script
├── screenshots/                       <- Verified high-resolution physical hardware screenshots
│   ├── 01_login_phone_password.png    (Phone Number & Password Login Screen)
│   ├── 02_registration_screen.png     (New User Registration with Password Match)
│   ├── 03_password_criteria_validation.png (Dynamic Password Complexity Checklist)
│   ├── 04_forgot_password_screen.png  (Anti-Enumeration Forgot Password OTP Flow)
│   ├── 05_otp_verification_keypad.png (Segmented 6-Digit OTP Verification)
│   ├── 06_set_new_password_matching.png (Password Reset & Session Revocation)
│   ├── 07_login_error_banner.png      (Invalid Credential Error Handling)
│   ├── 08_home_authenticated_render.png (Live Home Screen Connected to Render)
│   ├── 09_profile_verified_account.png (Verified User Account Profile & Settings)
│   ├── 10_after_logout_screen.png     (Session Teardown & Secure Return to Login)
│   ├── 01_splash.png                  (Native Animated Splash Screen)
│   ├── 04_catalogue.png               (Filterable Medicine Catalogue)
│   ├── 05_product_details.png         (Medicine Details & Formulations)
│   ├── 06_cart.png                    (Shopping Cart & Transparent Fees)
│   ├── 07_checkout.png                (Checkout & Address Selection)
│   ├── 08_order_confirmation.png      (Live Confirmed Order #MC-2026-3819)
│   ├── 09_order_history.png           (Order History with Reconciled Records)
│   └── 10_order_details.png           (4-Stage Delivery Tracking Timeline)
├── reports/
│   ├── TECHNICAL_REPORT.md            <- Comprehensive architecture, security & QA report
│   └── FINAL_TEST_REPORT.md           <- Complete 88-test audit, security matrix & sign-off
└── README.md                          <- This submission guide
```

---

## 2. Verified Hardware Screenshots Gallery

### 2.1. Authentication Redesign User Journey
| # | Screen Name | File | Description |
| :-: | :--- | :--- | :--- |
| **01** | Phone + Password Login | `screenshots/01_login_phone_password.png` | Indian mobile authentication (`+91 9876543210`) with password visibility toggle |
| **02** | User Registration | `screenshots/02_registration_screen.png` | Full name, phone, password, confirm password, and terms acceptance |
| **03** | Password Validation | `screenshots/03_password_criteria_validation.png` | Live 4-point criteria checklist (length, case, digit, symbol) |
| **04** | Forgot Password | `screenshots/04_forgot_password_screen.png` | Phone entry with generic anti-enumeration protection |
| **05** | Segmented OTP Input | `screenshots/05_otp_verification_keypad.png` | 6-cell digit input with 5-minute constant-time verification |
| **06** | Set New Password | `screenshots/06_set_new_password_matching.png` | Password update screen triggering `tokenVersion` invalidation |
| **07** | Invalid Credential Guard | `screenshots/07_login_error_banner.png` | Non-blocking inline error feedback on 401 unauthorized |
| **08** | Authenticated Home | `screenshots/08_home_authenticated_render.png` | Live Render backend connection with `SK` avatar and Indiranagar PIN |
| **09** | User Profile | `screenshots/09_profile_verified_account.png` | Verified account badge, saved addresses, language, and offline toggle |
| **10** | Logout Teardown | `screenshots/10_after_logout_screen.png` | Cleared `EncryptedSharedPreferences` returning cleanly to login |

### 2.2. Core E-Commerce & Pharmacy Journey
| # | Screen Name | File | Description |
| :-: | :--- | :--- | :--- |
| **11** | Animated Splash Screen | `screenshots/01_splash.png` | Native animated splash with Emerald Green identity |
| **12** | Product Catalogue | `screenshots/04_catalogue.png` | 20 generic formulations, category chips, stock badges |
| **13** | Medicine Details | `screenshots/05_product_details.png` | Active ingredients, dosage warnings, storage and return policies |
| **14** | Shopping Cart | `screenshots/06_cart.png` | Quantity stepper (regulatory max 5 packs), delivery ₹30, packaging ₹14 |
| **15** | Checkout Screen | `screenshots/07_checkout.png` | Delivery address confirmation, simulated UPI sandbox |
| **16** | Order Confirmation | `screenshots/08_order_confirmation.png` | Live backend order confirmation reference **`#MC-2026-3819`** |
| **17** | Order History | `screenshots/09_order_history.png` | Chronological orders showing `#MC-2026-3819`, `#MC-2026-1359`, and `#MC-2026-3947` |
| **18** | Order Tracking Timeline | `screenshots/10_order_details.png` | 4-stage tracking timeline (Dispensing & Packing) and itemized receipt |

---

## 3. Quick Run Instructions

### 3.1. Public Cloud Live Evaluation (Zero Laptop or ADB Dependency)
The Android application communicates directly with the live Render production backend over HTTPS.

1. **Install Live APK on Physical Android Device:**
   ```bash
   adb install -r submission/apk/MediCart-v2.4-live-debug.apk
   ```
2. **Launch Application:** MediCart connects automatically to `https://medicart-backend-lmcz.onrender.com/`.
3. **Login with Synthetic Demo Credentials:**
   - **Phone Number:** `9876543210`
   - **Password:** `Demo@1234`
4. **Registration / Forgot Password Verification:**
   - Use any test number (e.g., `9811223344`) or the demo number `9876543210`.
   - Demo OTP code: `487210`.

### 3.2. Local Test Execution
```bash
# 1. Run All 39 Backend Tests (Jest & Supertest)
cd backend
npm install
npm test

# 2. Run All 49 Android Unit & Integration Tests (JUnit & MockWebServer)
cd ..
.\gradlew.bat testDebugUnitTest

# 3. Build Fresh Live Debug APK
.\gradlew.bat clean assembleDebug -PbackendUrl="https://medicart-backend-lmcz.onrender.com/"
```

---

## 4. Reconciled Order Evidence

| Order Attribute | Live Cloud Render + Atlas Order | Primary Hardware Order | Secondary API Test Order |
| :--- | :--- | :--- | :--- |
| **Order Reference** | **`#MC-2026-3819`** (Live HTTPS) | **`#MC-2026-1359`** (Hardware Demo) | **`#MC-2026-3947`** (API Suite) |
| **Backend Environment** | `https://medicart-backend-lmcz.onrender.com/` | `https://medicart-backend-lmcz.onrender.com/` | Local Port 5000 |
| **MongoDB ID** | `6af95011da0736228f500a46` | `6b01a1829e072418eab3182a` | `6af84a37de616014ffa912c4` |
| **User ID** | `6af94e5bda0736228f500a1e` | `6af94e5bda0736228f500a1e` | `6af847f7de616014ffa9127e` |
| **Idempotency Key** | `e0b2c4ce-8f5b-429b-a232-afd06828947f` | `f92a7f53-b3c9-4416-8f31-897b6ec429e7` | `4f925d49-5dd3-43fd-adac-a516ca3e2245` |
| **Items Ordered** | • Paracare 650mg (Qty: 3, ₹102)<br>• AyurTulsi Syrup (Qty: 1, ₹85) | • Paracare 650mg (Qty: 5, ₹180) | • Paracare 650mg (Qty: 2, ₹68)<br>• AyurTulsi Syrup (Qty: 1, ₹85) |
| **Subtotal** | ₹187.00 (18700 paise) | ₹180.00 (18000 paise) | ₹153.00 (15300 paise) |
| **Delivery Fee** | ₹30.00 (3000 paise) | ₹20.00 (2000 paise) | ₹30.00 (3000 paise) |
| **Packaging Fee** | ₹14.00 (1400 paise) | ₹9.00 (900 paise) | ₹14.00 (1400 paise) |
| **Final Total** | **₹231.00** (23100 paise) | **₹209.00** (20900 paise) | **₹197.00** (19700 paise) |
| **Payment Method** | `UPI_DEMO` | `UPI_DEMO` | `UPI_DEMO` |
| **Status** | `PROCESSING` | `PROCESSING` | `PROCESSING` |

---

## 5. Verification Matrix Summary

- **Android Auth Redesign Tests:** 19 / 19 passed
- **Android Retrofit & Integration Tests:** 20 / 20 passed
- **Android Validation & Domain Tests:** 10 / 10 passed
- **Backend Auth Redesign Tests:** 17 / 17 passed
- **Backend Core REST API Tests:** 22 / 22 passed
- **Physical Device Interaction Steps:** 25 / 25 verified live on hardware (`CPH2333`)
- **Total Automated Test Scope:** **88 / 88 Tests (100% Pass Rate)**
