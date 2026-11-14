# MediCart – Official Submission Package

**Candidate / Author:** Sahil Kumar Chaudhary  
**Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Project Title:** MediCart – Android Online Pharmacy Application & REST API  
**Official Repository:** [https://github.com/Sahil-Kumar-Chaudhary/MediCart.git](https://github.com/Sahil-Kumar-Chaudhary/MediCart.git)  
**Main Branch:** `main`  
**Verified On Hardware Device:** `CPH2333` (OPPO Reno7 5G, Android 13)  

---

## 1. Directory Structure

```
submission/
├── apk/
│   └── MediCart-debug.apk             <- Clean, installable Android debug APK (16.5 MB)
├── screenshots/                       <- Verified high-resolution physical hardware screenshots
│   ├── 01_splash.png                  (Native Animated Splash Screen)
│   ├── 02_login.png                   (Mobile Authentication Screen)
│   ├── 03_home.png                    (Home Dashboard & Deliverable PIN)
│   ├── 04_catalogue.png               (Filterable Medicine Catalogue)
│   ├── 05_product_details.png         (Medicine Details & Formulations)
│   ├── 06_cart.png                    (Shopping Cart & Transparent Fees)
│   ├── 07_checkout.png                (Checkout & Address Selection)
│   ├── 08_order_confirmation.png      (Live Confirmed Order #MC-2026-3947)
│   ├── 09_order_history.png           (Order History with Reconciled Records)
│   └── 10_order_details.png           (4-Stage Delivery Tracking Timeline)
├── reports/
│   └── TECHNICAL_REPORT.md            <- Comprehensive architecture, security & QA report
├── demo/
│   └── DEMO_SCRIPT.md                 <- 5-Minute live video demonstration script
└── README.md                          <- This submission guide
```

---

## 2. Verified Hardware Screenshots Gallery

| # | Screen Name | File | Description |
| :-: | :--- | :--- | :--- |
| **01** | Splash Screen | `screenshots/01_splash.png` | Native animated splash with Emerald Green identity |
| **02** | Login | `screenshots/02_login.png` | Indian mobile authentication (`+91 9876543210`) |
| **03** | Home Dashboard | `screenshots/03_home.png` | Deliverable PIN chip, categories, floating circular bottom bar |
| **04** | Catalogue | `screenshots/04_catalogue.png` | 20 generic formulations, category chips, stock badges |
| **05** | Product Details | `screenshots/05_product_details.png` | Active ingredients, dosage warnings, return policy, Add CTA |
| **06** | Shopping Cart | `screenshots/06_cart.png` | Quantity stepper (max 5 packs), delivery fee ₹30, packaging ₹14 |
| **07** | Checkout | `screenshots/07_checkout.png` | Delivery address confirmation, simulated UPI sandbox |
| **08** | Order Confirmation | `screenshots/08_order_confirmation.png` | Live backend order confirmation reference **`#MC-2026-3947`** |
| **09** | Order History | `screenshots/09_order_history.png` | Chronological orders showing `#MC-2026-3947` and `#MC-2026-5796` |
| **10** | Order Details | `screenshots/10_order_details.png` | 4-stage tracking timeline (Dispensing & Packing) and itemized receipt |

---

## 3. Quick Run Instructions

### 3.1. Start the Backend REST API
```bash
cd backend
npm install
npm test       # Runs 22 Jest & Supertest integration tests
npm start      # Starts server on http://localhost:5000 (auto-seeded with 20 medicines)
```

### 3.2. Forward Port to Connected Android Phone
```bash
adb reverse tcp:5000 tcp:5000
```

### 3.3. Install & Run Android Client
```bash
# Run 30 Android unit and integration tests
.\gradlew.bat testDebugUnitTest

# Assemble fresh Debug APK
.\gradlew.bat assembleDebug

# Install verified APK onto attached phone
adb install -r submission/apk/MediCart-debug.apk
```

---

## 4. Reconciled Order Evidence

| Order Attribute | Primary Hardware Order | Secondary API Test Order |
| :--- | :--- | :--- |
| **Order Reference** | **`#MC-2026-3947`** | **`#MC-2026-5796`** |
| **MongoDB ID** | `6af84a37de616014ffa912c4` | `6af84864de616014ffa9129e` |
| **User ID** | `6af847f7de616014ffa9127e` | `6af847f7de616014ffa9127e` |
| **Idempotency Key** | `4f925d49-5dd3-43fd-adac-a516ca3e2245` | `order_reconciled_1794656356462` |
| **Items Ordered** | • Paracare 650mg (Qty: 2, ₹68)<br>• AyurTulsi Syrup (Qty: 1, ₹85) | • Paracare 650mg (Qty: 2, ₹68)<br>• Cetriz-L 10mg (Qty: 1, ₹22) |
| **Subtotal** | ₹153.00 (15300 paise) | ₹90.00 (9000 paise) |
| **Delivery Fee** | ₹30.00 (3000 paise) | ₹30.00 (3000 paise) |
| **Packaging Fee** | ₹14.00 (1400 paise) | ₹14.00 (1400 paise) |
| **Final Total** | **₹197.00** (19700 paise) | **₹134.00** (13400 paise) |
| **Payment Method** | `UPI_DEMO` | `UPI_DEMO` |
| **Status** | `PROCESSING` | `PROCESSING` |

---

## 5. Verification Matrix Summary

- **Android Unit & Domain Tests:** 10 / 10 passed
- **Android Retrofit & Integration Tests:** 20 / 20 passed
- **Backend Acceptance Tests:** 22 / 22 passed
- **Physical Device Interaction Steps:** 25 / 25 verified live
- **Total Verification Scope:** **77 / 77 Steps (100% Pass Rate)**
