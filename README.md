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

## 6. How to View the Artboards & Interactive Simulator

Open the interactive suite directly in your browser:
```bash
# Path to Interactive Artboard Suite
ui-artboards/index.html
```

Or view the individual 393 × 852 PNG artboards inside `artboards/`:
- `screen_00_splash.png`
- `screen_01_login.png`
- `screen_02_otp.png`
- `screen_03_home.png`
- `screen_04_catalogue.png`
- `screen_05_product.png`
- `screen_06_cart.png`
- `screen_07_address.png`
- `screen_08_checkout.png`
- `screen_09_confirmation.png`
- `screen_10_history.png`
- `screen_11_order_details.png`
- `screen_12_profile.png`
- `state_empty_cart.png`
- `state_empty_search.png`
- `state_out_of_stock.png`
- `state_network_offline.png`
