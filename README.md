# MediCart – Android Pharmacy Application (Complete UI/UX Redesign)

![MediCart Brand](ui-artboards/screens/screen_00_splash.html)

A production-ready, realistic Android pharmacy ordering application crafted specifically for Indian consumers, adhering to **Material Design 3**, strict **8dp rhythm**, and a sophisticated **Emerald Green + Warm White** brand identity.

---

## 1. Executive Summary & Brand Transformation

The previous MediCart design suffered from a generic e-commerce blue palette (`#1E88E5`), excessive empty whitespace, diminutive typography, inconsistent product cards, and clutter. 

This redesign transitions MediCart into an authoritative, startup-grade Indian healthcare brand inspired by leading fintech minimalism and modern pharmacy logistics.

### 🎨 New Color Tokens

| Semantic Token | HEX Value | Jetpack Compose Value | Purpose |
|---|---|---|---|
| **Primary** | `#087E6B` | `Color(0xFF087E6B)` | Brand accent, primary CTAs, active tab indicator |
| **Primary Dark** | `#075B4F` | `Color(0xFF075B4F)` | Pressed states, high-contrast badges, gradients |
| **Secondary Mint** | `#DDF1EA` | `Color(0xFFDDF1EA)` | Soft pill backgrounds, container surfaces, steppers |
| **App Background** | `#F7F9F7` | `Color(0xFFF7F9F7)` | Warm off-white background ensuring low eye strain |
| **Surface** | `#FFFFFF` | `Color(0xFFFFFFFF)` | Card containers, bottom sheets, app bars |
| **Primary Text** | `#172B27` | `Color(0xFF172B27)` | Deep charcoal for legible typography |
| **Secondary Text**| `#71817B` | `Color(0xFF71817B)` | Muted grey-green for subtitles, pack details |
| **Border** | `#E5ECE8` | `Color(0xFFE5ECE8)` | Clean subtle borders, card separators |
| **Success** | `#16865B` | `Color(0xFF16865B)` | Stock confirmed, order confirmed, deliverable PIN |
| **Warning** | `#E49A32` | `Color(0xFFE49A32)` | Low stock warnings (< 3 units) |
| **Error** | `#D64B4B` | `Color(0xFFD64B4B)` | Network offline, expired batches, PIN validation |

---

## 2. Indian Market Requirements & Domain Compliance

- **Indian Currency (₹):** Standardized INR symbol across all catalogue items, cart breakdowns, and invoices.
- **Indian Mobile Authentication:** Standard `+91` prefix with 10-digit number validation.
- **6-Digit Postal PIN Code:** Real-time Indian PIN code validation ensuring delivery radius check.
- **Indian Address Formats:** Structured fields for Flat/House No., Street/Locality, Landmark, City, State, and PIN code.
- **Simulated Payment Gateway:** Explicit demo sandbox options:
  - ⚡ **UPI (Demo):** PhonePe, Google Pay, Paytm, or VPA.
  - 💵 **Cash on Delivery:** Pay on delivery via Cash or QR.
  - 💳 **Card (Demo):** RuPay, Visa, Mastercard simulated sandbox.
  - *Compliance Notice: Clearly communicates that payment is simulated for assessment verification.*
- **Bilingual Interface:** Instant language switching between English and हिन्दी (Hindi).
- **Fictional Pharmacy Catalogue:** All medicines are fictional formulations (e.g. `Paracare 650mg`, `Cetriz-L 10mg`, `Panto-D SR`, `AyurTulsi Syrup`). No misleading doctor endorsements or unsupported guarantees.

---

## 3. The 12 Application Artboards (393 × 852 dp Viewport)

Every screen is crafted as a standalone mobile artboard rendered to modern Android device specifications (393 × 852 dp reference):

1. **Screen 00: Splash Screen** – Emerald Green & Warm White identity, shopping bag + medical cross logo, tagline *"Healthcare, Made Convenient."*, and reusable adaptive launcher icon.
2. **Screen 01: Login** – Clean Indian mobile number entry (`+91`), verified generic medication guarantees, and clean Material 3 card container.
3. **Screen 02: OTP Verification** – 6-digit OTP input boxes, active focus indicator, 24-second resend countdown timer, and realistic Android numeric keypad.
4. **Screen 03: Home Dashboard** – Live deliverable PIN address chip, search bar with voice input, category grid (Pain Relief, Cough & Cold, First Aid, Vitamins), featured medicines with direct Add CTA, and fixed 4-item bottom navigation.
5. **Screen 04: Medicine Catalogue** – Real-time query search, filter chips, compact uniform product cards, INR pricing with MRP strikethrough, stock availability badges, and quantity steppers.
6. **Screen 05: Product Details** – High-res packaging showcase, active ingredient disclosures, storage guidelines, 7-day return policy, safety disclaimer (no medical dosage advice), and bottom sticky Add to Cart bar.
7. **Screen 06: Shopping Cart** – Selected items with thumbnail, quantity stepper, free delivery progress, and full transparent bill breakdown (items total, delivery fee, packaging charge, total amount).
8. **Screen 07: Delivery Address** – Indian address form with recipient name, mobile, flat number, street/locality, city, state, deliverable 6-digit PIN code check, and address tag chips (Home, Work, Other).
9. **Screen 08: Checkout** – Address confirmation, package item preview, simulated payment method selection (UPI, COD, Card), and demo sandbox verification banner.
10. **Screen 09: Order Confirmation** – Animated success checkmark, unique order reference (`#MC-2026-8942`), delivery timestamp, and backend confirmation notice. Only displays upon confirmed server creation.
11. **Screen 10: Order History** – Chronological order cards, order status badges (Processing, Delivered, Cancelled), total amounts, item summaries, and reorder actions.
12. **Screen 11: Order Details** – 4-stage visual timeline tracker (Order Confirmed → Dispensing & Packing → Dispatched → Delivered), itemized receipt, delivery address, and invoice download action.
13. **Screen 12: Profile & Settings** – Account information, saved addresses quick link, language toggle (English / हिन्दी), notification toggles, and logout.

---

## 4. Crucial Edge & Failure States Handled

1. **Empty Cart State:** Explains basket is empty with an illustration and one-tap action to browse catalogue.
2. **Empty Search Results:** Gracefully catches non-matching medicine queries with helpful search tips and category shortcuts.
3. **Loading Skeleton:** Shimmering content placeholder shapes for slow network conditions.
4. **Product Out of Stock:** Amber warning badge, disables purchase button, and provides "Notify Me" action.
5. **Product Expired:** Automatic safety withholding preventing purchase of expired batches (e.g. `ColdRelief Night Syrup - Batch Expired`).
6. **Quantity Validation:** Prevents ordering more than 5 packs per order to uphold safety standards.
7. **Invalid PIN Code:** Real-time validation rejecting non-6-digit or non-numeric PIN codes.
8. **Network Unavailable:** Red top banner and full offline state with retry button.
9. **Order Submission Failed:** Keeps cart items intact and explains backend retry path without state loss.
10. **Empty Order History:** Welcoming onboarding state for new users.

---

## 5. Jetpack Compose Architecture

```
app/src/main/java/com/medicart/app/
├── MainActivity.kt                       // ComponentActivity entry point
├── navigation/
│   ├── Screen.kt                         // Type-safe navigation routes
│   └── MediCartNavGraph.kt               // Complete NavHost graph
├── data/
│   └── MockPharmacyRepository.kt         // Simulated backend repository & catalogue
├── model/
│   ├── Product.kt                        // Medicine data model & availability logic
│   ├── CartItem.kt                       // Quantity and price calculator
│   ├── Order.kt                          // Order reference, status & timestamps
│   ├── Address.kt                        // Indian address format & PIN validator
│   ├── Category.kt                       // Pharmacy categories
│   └── User.kt                           // Language and user profile
├── ui/
│   ├── theme/
│   │   ├── Color.kt                      // Emerald Green & Warm White tokens
│   │   ├── Type.kt                       // Material 3 typography scale (min 12sp)
│   │   ├── Shape.kt                      // 8dp rhythm rounded corners
│   │   └── Theme.kt                      // MediCartTheme configuration
│   ├── components/
│   │   ├── MediTopAppBar.kt              // Reusable top bar with back navigation
│   │   ├── MediBottomNav.kt              // 4-item bottom navigation bar
│   │   ├── PrimaryButton.kt              // 48dp accessible CTA button
│   │   ├── QuantityStepper.kt            // Quantity stepper component
│   │   ├── StatusBadge.kt                // Order status badges
│   │   └── EmptyStateView.kt             // Recovery empty states
│   └── screens/
│       ├── SplashScreen.kt
│       ├── LoginScreen.kt
│       ├── OtpVerificationScreen.kt
│       ├── HomeScreen.kt
│       ├── CatalogueScreen.kt
│       ├── ProductDetailScreen.kt
│       ├── CartScreen.kt
│       ├── DeliveryAddressScreen.kt
│       ├── CheckoutScreen.kt
│       ├── OrderConfirmationScreen.kt
│       ├── OrderHistoryScreen.kt
│       ├── OrderDetailScreen.kt
│       └── ProfileScreen.kt
└── src/test/java/com/medicart/app/
    └── MediCartValidationTest.kt         // Automated validation & business rule tests
```

---

## 6. Full-Stack Architecture & Offline Synchronization

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

### Key Technical Capabilities:
- **Offline First:** Catalogue browsing and cart modifications are cached locally in SQLite (`MediCartDatabaseHelper`).
- **Hardware Encryption:** User tokens and phone numbers are encrypted with Android Jetpack Security `EncryptedSharedPreferences` backed by AES-256 GCM hardware Keystore.
- **Dynamic Networking:** Auto-detects QEMU emulator (`10.0.2.2`) vs physical Android devices (`127.0.0.1` via `adb reverse tcp:5000 tcp:5000`).
- **Atomic Order Processing:** Inventory is decremented atomically; if any item in a multi-item cart fails validation, the system rolls back all reserved stock cleanly.
- **Idempotency Protection:** Unique `idempotencyKey` prevents duplicate checkout submissions and double-billing on network retries or rapid button clicks.

---

## 7. Quality Assurance & Test Verification

| Test Layer | Framework | Coverage | Count | Status |
| :--- | :--- | :--- | :---: | :---: |
| **Android Domain Logic** | JUnit 4 + Kotlin | Pricing, Fee rules, Max qty, PIN validation | 10 | **PASS** |
| **Android Network & Retrofit** | MockWebServer + JUnit 4 | Interceptors, Token injection, Offline fallback | 20 | **PASS** |
| **Backend REST API** | Jest + Supertest | Auth, Catalogue, Address isolation, Orders, Rollback | 22 | **PASS** |
| **Physical Hardware Live E2E**| Real Device (CPH2333) | Complete 25-step purchasing journey | 25 | **PASS** |
| **TOTAL** | — | — | **77** | **100% PASS** |

- **Live Hardware Milestone:** Verified on Android 13 (`CPH2333`) creating live backend order reference **`#MC-2026-2368`** with idempotency key `bb249f71-f58d-43f2-9b20-56b9a64b7103`.

---

## 8. Build, Run & Testing Instructions

### Backend Server (Node.js & Express)
```bash
cd backend
npm install
npm test       # Run 22 Jest acceptance tests
npm start      # Launch server on http://localhost:5000 (auto-seeded with 20 Indian medicines)
```

### Android Client (Kotlin & Jetpack Compose)
```bash
# Run 30 Android unit and integration tests
.\gradlew.bat testDebugUnitTest

# Assemble clean Debug APK
.\gradlew.bat assembleDebug

# Output APK: app/build/outputs/apk/debug/app-debug.apk (19.43 MB)
```

### Physical Device Deployment
```bash
# Forward port 5000 to the connected phone
adb reverse tcp:5000 tcp:5000

# Install APK to device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 9. Submission Package & Artifacts

All formal submission materials are organized in the [`submission/`](submission/) directory:
- 📑 **Technical Report:** [`submission/reports/TECHNICAL_REPORT.md`](submission/reports/TECHNICAL_REPORT.md)
- 🎬 **Video Demo Script:** [`submission/demo/DEMO_SCRIPT.md`](submission/demo/DEMO_SCRIPT.md)
- 📱 **Physical Device Screenshots:** [`submission/screenshots/`](submission/screenshots/)
- 📦 **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk` (19.43 MB)

