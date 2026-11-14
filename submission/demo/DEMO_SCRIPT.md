# MediCart – Live Video Demonstration Script

**Duration:** ~4 to 5 minutes  
**Target Audience:** Assessment Evaluators, Hiring Managers, Technical Reviewers  
**Author / Candidate:** Sahil Kumar Chaudhary  
**Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Tested On:** Physical Android Device (`CPH2333` running Android 13) + Node.js / Express Server  

> **Submission Notice:** This script is prepared for recording the live application walkthrough. Per assessment guidelines, no claim is made that the video is pre-recorded until the candidate completes the recording and provides the video link.

---

## Equipment & Environment Setup

1. **Backend Server:** Active on port 5000 (`cd backend && npm start`).
2. **Device Connection:** Smartphone connected over USB with USB debugging enabled.
3. **Port Forwarding:** `adb reverse tcp:5000 tcp:5000` executed so the phone accesses `http://127.0.0.1:5000/`.
4. **App Installation:** Verified `submission/apk/MediCart-debug.apk` installed.
5. **Screen Recording:** Scrcpy or device screen recorder configured at 1080p, 60fps.

---

## Step-by-Step Walkthrough Guide

### 1. App Introduction & Splash Animation (0:00 – 0:30)
- **Visual:** Launch the application from the phone's home screen. The splash screen animates with the Emerald Green shopping bag & medical cross logo.
- **Presenter Script:**
  > "Hello! Welcome to the technical demonstration of MediCart, a modern, production-grade Indian online pharmacy application built with Kotlin and Jetpack Compose, backed by a Node.js, Express, and MongoDB REST API.
  > Notice the brand identity: an elegant Emerald Green (`#087E6B`) and Warm White palette designed in accordance with Material Design 3 guidelines."

---

### 2. Mobile Login & OTP Authentication (0:30 – 1:15)
- **Visual:** Navigate from Welcome screen to Login. Enter `9876543210` under the Indian `+91` prefix. Tap "Get Verification OTP". On the OTP screen, enter the 6-digit code `487210` and tap "Verify & Proceed".
- **Presenter Script:**
  > "We enter an Indian mobile number. Tapping 'Get Verification OTP' makes a POST request to our Express backend, generating a 5-minute time-limited HMAC OTP.
  > Upon submitting `487210`, the server verifies the code in constant time using `crypto.timingSafeEqual` and issues a signed JWT Bearer token.
  > The Android client securely stores this token using Android Jetpack Security's `EncryptedSharedPreferences`, encrypted with AES-256 GCM backed by the Android hardware Keystore."

---

### 3. Home Dashboard & Delivery Address (1:15 – 1:50)
- **Visual:** Land on the Home screen. Highlight the top delivery address chip ("Indiranagar, Bengaluru - 560038"), the "Monsoon Wellness" generic medicine banner, the category grid, and the circular floating bottom navigation bar.
- **Presenter Script:**
  > "Here on the Home screen, the user immediately sees their deliverable postal PIN code.
  > Our bottom navigation bar is styled with a modern floating circular pill shape and clear active tab indicators.
  > All category data and featured medicines are fetched dynamically from the live REST API via Retrofit 2."

---

### 4. Product Catalogue, Search & Product Details (1:50 – 2:35)
- **Visual:** Tap the "Catalogue" tab. Clear or filter by category pills. Tap the search bar and type "Paracare". Tap the product card for "Cetriz-L 10mg Tablets" to open the Medicine Details screen.
- **Presenter Script:**
  > "Navigating to the Catalogue, we have 20 realistic, generic formulations.
  > Notice the Indian pharmacy domain details: MRP strikethrough, discount percentages, strip packaging dimensions, and stock indicators.
  > In the Product Details view, active ingredients, dosage warnings, storage instructions, and return policies are clearly disclosed."

---

### 5. Cart Management & Fee Engine (2:35 – 3:15)
- **Visual:** Increment Cetriz-L quantity with the stepper. Tap "Add to Cart". Open the Cart screen. Show Paracare 650mg Tablets with quantity 2 and AyurTulsi Cough Relief with quantity 1.
- **Presenter Script:**
  > "In the shopping cart, the quantity stepper enforces pharmaceutical safety compliance: users cannot order more than 5 packs of any formulation.
  > The fee breakdown dynamically computes the item total, standard delivery charge of ₹30, and hygienic packaging fee of ₹14, arriving at a total of ₹197."

---

### 6. Checkout & Idempotent Order Creation (3:15 – 3:55)
- **Visual:** Tap "Proceed to Checkout". Show the delivery address review and the simulated payment options (UPI Demo, Cash on Delivery, Card Demo). Tap "Place Order (Demo)".
- **Presenter Script:**
  > "At checkout, we review the delivery address and choose UPI simulated demo payment.
  > When we tap 'Place Order', the client attaches a unique UUID `idempotencyKey`.
  > If a network timeout or accidental double-tap occurs, the backend detects the key and returns the existing order rather than creating a duplicate or double-decrementing inventory.
  > Let's place the order!"

---

### 7. Order Confirmation & Reference Generation (3:55 – 4:15)
- **Visual:** The animated success checkmark appears with "Order Placed Successfully!", badge "BACKEND CONFIRMED", and order reference `#MC-2026-3947`.
- **Presenter Script:**
  > "Success! The order is confirmed directly by the backend with reference `#MC-2026-3947` and marked for pharmacist verification."

---

### 8. Order History & Live Delivery Timeline (4:15 – 4:40)
- **Visual:** Tap "View Order Details". Scroll through the 4-stage tracking timeline (Order Confirmed → Dispensing & Packing → Dispatched → Delivered), the immutable package contents, and the receipt breakdown. Tap back to see the full list in Order History.
- **Presenter Script:**
  > "The Order Details screen displays the real-time progress tracker and an immutable snapshot of item prices at the time of purchase.
  > In the Order History screen, we see all past orders fetched live from MongoDB."

---

### 9. Offline Resilience & Network Drop Handling (4:40 – 5:00)
- **Visual:** Enable Airplane Mode on the smartphone. Tap the Catalogue and Cart tabs; show that cached medicines and cart selections remain available. Re-enable network connectivity.
- **Presenter Script:**
  > "Finally, let's test offline resilience. With Airplane Mode on, the repository immediately falls back to our local SQLite database (`MediCartDatabaseHelper`), allowing users to browse their cart and saved catalogue without crashing or hanging.
  > Once the network reconnects, state synchronizes cleanly with the server."

---

### 10. Architecture Summary & Wrap-Up (5:00 – 5:20)
- **Visual:** Display the architecture diagram or summary slide.
- **Presenter Script:**
  > "To summarize: MediCart combines Jetpack Compose, MVVM, Retrofit 2, SQLite caching, and EncryptedSharedPreferences on Android with a secure Node.js, Express, and MongoDB backend.
  > Backed by 52 automated tests and live hardware verification on Android 13, MediCart is robust, secure, and production-ready. Thank you!"
