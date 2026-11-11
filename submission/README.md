# MediCart – Submission Package

**Applicant / Author:** Sahil Kumar Chaudhary  
**Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Main Branch:** `main`  
**Verified On:** Physical Device `CPH2333` (Android 13)  

---

## Submission Package Contents

```
submission/
├── README.md                      <- This submission guide
├── reports/
│   └── TECHNICAL_REPORT.md        <- Comprehensive QA, security & architecture report
├── demo/
│   └── DEMO_SCRIPT.md             <- 5-minute video demonstration walkthrough script
└── screenshots/                   <- High-resolution physical hardware screenshots
    ├── physical_device_live.png                  (Login Screen)
    ├── physical_device_otp_screen.png            (OTP Verification Screen)
    ├── physical_device_home_authenticated.png    (Authenticated Home Screen)
    ├── physical_device_products2.png             (Product Catalogue)
    ├── physical_device_detail.png                (Product Detail Bottom Sheet)
    ├── physical_device_cart.png                  (Cart Summary with Fee Engine)
    ├── physical_device_checkout.png              (Checkout & Address Selection)
    ├── physical_device_order_confirmation.png    (Order Confirmed #MC-2026-2368)
    ├── physical_device_orders.png                (Order History Screen)
    └── physical_device_order_detail_view.png     (Order Tracking & Progress Timeline)
```

---

## Verified Screenshots Gallery

| Screen | Description | File |
| :--- | :--- | :--- |
| **Login** | Mobile number entry with Indian country code | `screenshots/physical_device_live.png` |
| **OTP Verification** | 6-digit code entry with auto-focus | `screenshots/physical_device_otp_screen.png` |
| **Home Screen** | Categories, delivery address pill, search | `screenshots/physical_device_home_authenticated.png` |
| **Catalogue** | Filterable medicines with stock indicators | `screenshots/physical_device_products2.png` |
| **Cart** | Quantity stepper, regulatory cap, fee calculation | `screenshots/physical_device_cart.png` |
| **Checkout** | Delivery address, payment options, totals | `screenshots/physical_device_checkout.png` |
| **Order Confirmation** | Real order reference `#MC-2026-2368` | `screenshots/physical_device_order_confirmation.png` |
| **Order History** | List of past orders with status badges | `screenshots/physical_device_orders.png` |
| **Order Details** | 4-step delivery timeline & receipt snapshot | `screenshots/physical_device_order_detail_view.png` |

---

## Build & Run Instructions

### 1. Backend Server
```bash
cd backend
npm install
npm test      # Executes 22 Jest acceptance tests
npm start     # Starts Express server on http://localhost:5000 (auto-seeded)
```

### 2. Android Client
```bash
# Unit & integration acceptance tests (30 tests)
.\gradlew.bat testDebugUnitTest

# Assemble debug APK
.\gradlew.bat assembleDebug

# Output location:
# app/build/outputs/apk/debug/app-debug.apk (19.43 MB)
```

### 3. Connect Physical Device over USB
```bash
adb reverse tcp:5000 tcp:5000
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Test Verification Summary
- **Android Unit & Domain Tests:** 10 / 10 passed
- **Android Retrofit Integration Tests:** 20 / 20 passed
- **Backend Acceptance Tests:** 22 / 22 passed
- **Hardware End-to-End Steps:** 25 / 25 verified live
- **Total:** 77 / 77 (100% Pass Rate)
