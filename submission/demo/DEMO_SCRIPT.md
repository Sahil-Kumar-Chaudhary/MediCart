# MediCart – Live Video Demonstration Script

**Duration:** ~4 to 5 minutes  
**Target Audience:** Evaluators, Hiring Managers, Technical Reviewers  
**Presenter:** Sahil Kumar Chaudhary  
**Device:** Physical Android Smartphone (`CPH2333` running Android 13) + Node.js Backend Server  

---

## Equipment & Setup Checklist Before Recording

1. **Backend Server:** Running on port 5000 (`cd backend && npm start`).
2. **Device Connection:** Connected via USB with ADB debugging enabled.
3. **Port Forwarding:** `adb reverse tcp:5000 tcp:5000` executed so the phone connects directly to `localhost:5000`.
4. **App Installation:** `app-debug.apk` freshly installed on the device.
5. **Screen Recording:** Scrcpy or device screen recorder active with clear 1080p resolution.

---

## Walkthrough Breakdown

### Segment 1: Introduction & Brand Theme (0:00 – 0:45)
- **Visual:** Launch the app from the home screen launcher icon.
- **Presenter Script:**
  > "Hello! Welcome to the live demonstration of MediCart, a modern, production-ready Indian online pharmacy application built with Jetpack Compose, Kotlin, and a high-performance Node.js / MongoDB backend.
  > Notice the brand identity: we transitioned to a sophisticated Emerald Green (`#087E6B`) and Warm Off-White (`#F7F9F7`) palette with clean typography, rounded surfaces, and Material Design 3 guidelines."
- **Key UI Elements Highlighted:**
  - Modern circular floating bottom navigation bar.
  - Quick action category pills (Pain Relief, Cold & Cough, Personal Care, First Aid).
  - Clean promotional cards and delivery address banner.

---

### Segment 2: Authentication & Secure Onboarding (0:45 – 1:30)
- **Visual:** Navigate from Welcome screen to Login, enter `+91 9876543210`, tap "Get OTP", enter the 6-digit code `487210`, tap "Verify & Continue".
- **Presenter Script:**
  > "For user onboarding, we implemented a phone-number based OTP verification flow matching Indian market standards.
  > When I enter the phone number, the backend generates an HMAC-signed OTP code with a 5-minute expiry.
  > Upon verification, the server issues a signed JWT Bearer token, which the Android client securely stores using Jetpack Security `EncryptedSharedPreferences` backed by the Android Hardware Keystore."
- **Key UI Elements Highlighted:**
  - Responsive OTP box inputs with automatic focus advancement.
  - Resend timer and error handling for invalid codes.

---

### Segment 3: Live Catalogue, Search & Cart Management (1:30 – 2:30)
- **Visual:** Browse the product catalogue, filter by 'Pain Relief', search for 'Paracetamol', open the product detail sheet, and tap "Add to Cart". Increment quantity to 2, then navigate to the Cart screen.
- **Presenter Script:**
  > "All product data shown here is retrieved live from our Express backend via Retrofit 2.
  > Each item displays Indian pharmacy specifics: MRP discounts, strip packaging sizes, and prescription requirement tags.
  > Notice the cart stepper: it enforces regulatory safety rules, preventing anyone from ordering more than 5 packs of medication.
  > In the cart summary, the fee engine dynamically calculates item subtotal, delivery charges, and packaging fees."
- **Key UI Elements Highlighted:**
  - Live stock indicators (In Stock vs Low Stock).
  - Haptic quantity stepper with maximum limit clamp.
  - Dynamic price breakdown: Subtotal, Delivery Fee, Packaging Fee.

---

### Segment 4: Checkout, Idempotent Order Submission (2:30 – 3:30)
- **Visual:** Tap "Proceed to Checkout". Select "Indiranagar, Bengaluru" delivery address. Select UPI (Simulated Instant Payment). Tap "Place Order".
- **Presenter Script:**
  > "During checkout, the client attaches a unique UUID `idempotencyKey` to the order payload.
  > Even if a user double-taps the place order button or the connection drops midway, the server guarantees zero duplicate charges and prevents double-decrementing inventory.
  > Let's place the order!
  > Boom — Order confirmed instantly with reference `#MC-2026-2368`!"
- **Key UI Elements Highlighted:**
  - Address selection card with pin-code verification.
  - Payment method choices (UPI, Cards, Cash on Delivery).
  - High-impact animated success confirmation screen with order reference.

---

### Segment 5: Order Tracking & Live Timeline (3:30 – 4:15)
- **Visual:** Tap "View Order Details". Scroll through the order summary, item snapshots, delivery address, and the 4-step progress timeline (Ordered -> Processing -> Out for Delivery -> Delivered).
- **Presenter Script:**
  > "Navigating to Order Details, we see a complete immutable snapshot of the order, including historical item prices at the time of purchase and the full delivery address.
  > The status tracker clearly displays the current phase of the order, providing complete transparency to the customer."
- **Key UI Elements Highlighted:**
  - Step progress indicator with custom icons.
  - Itemized receipt breakdown.
  - One-tap re-order and customer support shortcuts.

---

### Segment 6: Offline Resilience & Technical Architecture (4:15 – 5:00)
- **Visual:** Briefly show airplane mode toggled on the phone; show that catalogue items and active cart are still fully browsable thanks to the local SQLite database.
- **Presenter Script:**
  > "Finally, let's talk about offline resilience. MediCart uses an offline-first architecture. The repository seamlessly falls back to a local SQLite database (`MediCartDatabaseHelper`) if the network disconnects, keeping the app fast and usable anywhere.
  > With 30 Android unit and integration tests and 22 backend acceptance tests, the codebase achieves 100% test pass rate and enterprise-grade reliability.
  > Thank you for watching!"
