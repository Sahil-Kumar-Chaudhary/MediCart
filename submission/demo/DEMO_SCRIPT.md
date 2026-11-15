# MediCart – Live Video Demonstration Script

**Duration:** ~4 to 5 minutes  
**Target Audience:** Assessment Evaluators, Hiring Managers, Technical Reviewers  
**Author / Candidate:** Sahil Kumar Chaudhary  
**Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Tested On:** Physical Android Device (`CPH2333` running Android 13) + Live Cloud Backend (`https://medicart-backend-lmcz.onrender.com/`)  
**Database:** MongoDB Atlas Production Cluster  

> **Submission Notice on Video Status:**  
> This script is fully prepared and choreographed for the live recording walkthrough. Per assessment integrity rules, **no claim is made that the video is pre-recorded until the candidate executes the recording on the physical device and uploads the final video link**.

---

## Equipment & Environment Setup

1. **Live Cloud Backend:** Active on Render (`https://medicart-backend-lmcz.onrender.com/health` returns `status: healthy`).
2. **Device Connection:** Physical Android smartphone (`CPH2333` / OPPO Reno7 5G, Android 13) connected via USB debugging / scrcpy screen mirroring.
3. **App Installation:** Verified `submission/apk/MediCart-v2.4-live-debug.apk` installed.
4. **Network:** Pure Wi-Fi / Mobile Data (no ADB reverse port forwarding required).
5. **Screen Recording:** Scrcpy or device screen recorder configured at 1080p, 60fps with clear audio commentary.

---

## Complete 15-Stage Demonstration Walkthrough

```
+---------------------------------------------------------------------------------------------------+
| Total Timeline: ~4:30 Minutes                                                                     |
| [0:00] Splash -> [0:20] Login -> [0:45] Register -> [1:10] OTP -> [1:30] Forgot Pwd ->            |
| [1:50] Pwd Reset -> [2:10] Home/Catalog -> [2:30] Search -> [2:50] Cart -> [3:10] Checkout ->    |
| [3:30] Order Confirm -> [3:50] Order History -> [4:05] Backend -> [4:20] Security -> [4:35] End  |
+---------------------------------------------------------------------------------------------------+
```

---

### Stage 1: Splash Animation (0:00 – 0:20)
- **Visual Action:** Tap the MediCart app icon on the Android home screen. The splash screen renders with the Emerald Green circular logo, pulsating cross emblem, and the tagline *"Healthcare, Made Convenient."*.
- **Presenter Narration:**
  > "Welcome to the technical demonstration of MediCart, a modern, production-ready online pharmacy application developed for the internship assessment at ALGIONS AI LABS PRIVATE LIMITED.
  > Notice the brand identity: an elegant Emerald Green (`#087E6B`) and soft mint palette implemented strictly with Jetpack Compose and Material Design 3 guidelines."

---

### Stage 2: Phone Number & Password Login (0:20 – 0:45)
- **Visual Action:** The app lands on the Login screen. The Indian country code `+91` is fixed. Enter registered demo phone `9876543210` and password `Demo@1234`. Tap the eye icon to toggle password visibility.
- **Presenter Narration:**
  > "Our authentication system uses standard phone number and password credentials. 
  > The password field includes an interactive visibility toggle. Credentials are sent over HTTPS to our Node.js / Express backend deployed on Render, which validates the password against a salted Bcrypt hash and issues a signed JSON Web Token."

---

### Stage 3: New User Registration (0:45 – 1:10)
- **Visual Action:** Tap 'Create Account'. Fill in 'Sahil Verma', phone number `9811223344`, and enter a password. Demonstrate the dynamic password criteria checklist:
  - 8+ Characters
  - Uppercase & Lowercase letters
  - At least one number
  - At least one special symbol
  Check the Terms of Service checkbox. Tap 'Request OTP'.
- **Presenter Narration:**
  > "Let's demonstrate new user registration. As I type a strong password, our reactive state engine updates a 4-point criteria checklist in real time. 
  > Once all validation rules pass, tapping 'Request OTP' validates the inputs against backend Zod schemas and ensures the phone number is not already registered."

---

### Stage 4: OTP Verification (1:10 – 1:30)
- **Visual Action:** The OTP Verification screen appears with a 6-cell segmented digit input, phone number label, and a 60-second resend countdown timer. Enter the 6-digit verification code.
- **Presenter Narration:**
  > "The OTP screen presents a segmented 6-digit input. On the backend, OTPs are generated using a cryptographically secure random number generator and validated in constant time using `crypto.timingSafeEqual` with a 5-minute time-to-live.
  > Once verified, the account is created in MongoDB Atlas and the user is authenticated."

---

### Stage 5: Forgot Password (1:30 – 1:50)
- **Visual Action:** Return to Login, tap 'Forgot Password?'. Enter the mobile number and tap 'Send Reset OTP'.
- **Presenter Narration:**
  > "If a user forgets their password, they simply enter their phone number. 
  > To prevent account enumeration attacks, our backend responds with a generic HTTP 200 success response regardless of whether the mobile number exists in the database, protecting user privacy."

---

### Stage 6: Password Reset & Session Invalidation (1:50 – 2:10)
- **Visual Action:** Input the reset OTP, enter new password `Updated@2026`, confirm matching password, and tap 'Reset Password'.
- **Presenter Narration:**
  > "On the Set New Password screen, the user enters their new credentials. 
  > When submitted, the backend hashes the new password with Bcrypt and crucially increments the user's `tokenVersion`. This instantly revokes all existing JWT sessions across any other active devices without needing an expensive Redis cache."

---

### Stage 7: Home Dashboard & Product Catalogue (2:10 – 2:30)
- **Visual Action:** Log in and navigate to the Home screen. Highlight the deliverable PIN chip ('Indiranagar, Bengaluru - 560038'), the promotional banner, category tiles, and bottom navigation pill. Tap 'Catalogue'.
- **Presenter Narration:**
  > "Here on the Home screen, the user immediately sees their delivery location and serviceability status.
  > Navigating to the Catalogue tab, 20 generic pharmaceutical formulations are fetched directly from MongoDB Atlas via Retrofit 2, displaying MRP strikethrough prices, Indian Rupee discounts, and live stock badges."

---

### Stage 8: Product Search & Filtering (2:30 – 2:50)
- **Visual Action:** Tap the top search bar, type 'Paracare'. Filter results by category chips ('Pain Relief', 'Cold & Fever'). Tap the product card to view detailed formulation info.
- **Presenter Narration:**
  > "Our search bar provides instant debounced querying. 
  > In the Medicine Details view, Indian pharmaceutical standards are enforced: active ingredients, dosage warnings, strip packaging counts, and temperature storage instructions are prominently displayed."

---

### Stage 9: Add to Cart & Pharmaceutical Quantity Cap (2:50 – 3:10)
- **Visual Action:** Select quantity 2 and tap 'Add to Cart'. Open the Cart screen. Tap the '+' stepper up to 5, then show that attempting to add a 6th pack displays the safety advisory.
- **Presenter Narration:**
  > "In the shopping cart, MediCart enforces regulatory e-pharmacy compliance: users are strictly capped at a maximum of 5 packs per order to prevent medication abuse and unauthorized reselling.
  > The pricing engine calculates item subtotal, a flat delivery fee of ₹30, and a hygienic packaging fee of ₹14 in integer paise."

---

### Stage 10: Checkout & Simulated UPI Sandbox (3:10 – 3:30)
- **Visual Action:** Tap 'Proceed to Checkout'. Select saved address. Select payment option 'UPI Demo (Simulated)'. Tap 'Place Order'.
- **Presenter Narration:**
  > "At checkout, the user selects their delivery address and chooses the simulated UPI demo payment method.
  > When tapping 'Place Order', an idempotency key is automatically attached in the request header, guaranteeing that even on unstable networks, an order will never be duplicated."

---

### Stage 11: Order Confirmation & Backend Reference (3:30 – 3:50)
- **Visual Action:** Animated checkmark appears with 'Order Placed Successfully!' and displays the live generated order reference (e.g., `#MC-2026-3819`).
- **Presenter Narration:**
  > "The order is confirmed live by the MongoDB backend, assigning a unique business tracking reference `#MC-2026-3819`. 
  > The inventory stock is atomically decremented on the server."

---

### Stage 12: Order History & 4-Stage Live Tracking (3:50 – 4:05)
- **Visual Action:** Tap 'View Order History'. Select the order to view the 4-stage tracking timeline: Order Confirmed → Dispensing & Packing → Out for Delivery → Delivered.
- **Presenter Narration:**
  > "The Order History screen maintains an immutable record of past orders. 
  > In the details view, customers can monitor a 4-stage visual timeline tracking their prescription from pharmacy packaging to door delivery."

---

### Stage 13: Backend Architecture (4:05 – 4:20)
- **Visual Action:** Point out Render backend URL and live architecture diagram.
- **Presenter Narration:**
  > "Behind the scenes, our Node.js and Express REST API is hosted on Render with a 100% free plan. It connects to MongoDB Atlas using Mongoose ODM, with modular routes, controllers, and services separating business concerns."

---

### Stage 14: Database & Security Audit (4:20 – 4:35)
- **Visual Action:** Show security credentials in presentation slides or code snippet: EncryptedSharedPreferences, Bcrypt, Helmet headers.
- **Presenter Narration:**
  > "Security is baked into every layer:
  > Passwords are salted and hashed using Bcrypt. Mobile tokens are stored in Android Jetpack Security's `EncryptedSharedPreferences` backed by AES-256 GCM in the Android Hardware Keystore. The API is fortified with Helmet, CORS, and Express rate limiting."

---

### Stage 15: Offline Handling & SQLite Resilience (4:35 – 4:50)
- **Visual Action:** Enable Airplane Mode on the smartphone. Tap the Catalogue and Cart tabs; show cached data loads smoothly. Re-enable network.
- **Presenter Narration:**
  > "Finally, MediCart features robust offline-first resilience. When network connectivity drops, our Repository gracefully falls back to the local SQLite database (`MediCartDatabaseHelper`). 
  > The user can still review their cart and browse saved medicines without encountering a single crash. Once online, the app seamlessly reconnects.
  > Thank you! That concludes our live demonstration of MediCart."

---

## Technical Checklist for Recording

| Item | Requirement | Verification |
| :--- | :--- | :---: |
| **Resolution** | 1080 x 2400 (or 1080p landscape split-screen) | Verified |
| **Frame Rate** | 60 fps smooth recording | Verified |
| **Audio** | Clear microphone audio without background noise | Candidate to execute |
| **Network** | Physical Wi-Fi or 5G connection to Render cloud backend | Verified |
| **Demo Phone** | `9876543210` / `Demo@1234` | Verified |
| **New User Phone** | `9811223344` (or any valid 10-digit number) | Verified |
