# MediCart – Professional Demonstration Voiceover Script

**Project Title:** MediCart – Android Online Pharmacy Application & REST API  
**Evaluator / Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Candidate / Author:** Sahil Kumar Chaudhary  
**Target Duration:** ~4 Minutes 35 Seconds (275 Seconds)  
**Format:** English Voiceover (Indian-English Friendly Style) with Burned-In Subtitles  
**Voice Engine:** Neural TTS (`en-IN-PrabhatNeural`)  

---

## Complete Narration Breakdown by Scene

### Scene 1: Introduction (0:00 – 0:20)
*Timecode: 00:00.000 – 00:20.000 (Duration: ~20s)*  
**Visual Focus:** Professional opening branding title, animated splash screen with Emerald Green circular logo, and app launch.  
**Spoken Narration:**  
> "Welcome to the technical demonstration of MediCart, a modern, production-grade Android pharmacy application developed for the internship assessment at ALGIONS AI LABS PRIVATE LIMITED. MediCart is designed to make discovering, ordering, and managing essential medicines transparent, secure, and effortless for Indian consumers. Let us walk through the complete end-to-end experience running live on a physical Android device."

---

### Scene 2: Authentication & Security (0:20 – 0:50)
*Timecode: 00:20.000 – 00:50.000 (Duration: ~30s)*  
**Visual Focus:** Phone number and password login, password visibility toggle, new user registration form with password criteria checklist, forgot password OTP view.  
**Spoken Narration:**  
> "MediCart features a robust phone number and password authentication system. Users can securely sign in with their ten-digit Indian mobile number. The password field includes an interactive visibility toggle. For new users, registration enforces a real-time criteria checklist ensuring passwords contain upper and lower case letters, numbers, and special symbols, verified through a constant-time HMAC OTP. If a password is forgotten, an anti-enumeration recovery flow allows secure reset while instantly invalidating previous sessions."

---

### Scene 3: Home & Medicine Discovery (0:50 – 1:30)
*Timecode: 00:50.000 – 01:30.000 (Duration: ~40s)*  
**Visual Focus:** Authenticated Home screen, Indiranagar PIN chip, category horizontal cards, Catalogue tab, and real-time search.  
**Spoken Narration:**  
> "Logging in brings us to the Home screen, styled with Material Design 3 and an elegant Emerald Green palette. At the top, the deliverable postal PIN code displays serviceability. Customers can browse through healthcare categories including Pain Relief, Cough and Cold, First Aid, and Vitamins. Navigating to the Catalogue, twenty generic pharmaceutical formulations are fetched dynamically from our live cloud database, showing authentic MRP strikethroughs, discounts, and real-time stock indicators."

---

### Scene 4: Product Details & Cart Management (1:30 – 2:10)
*Timecode: 01:30.000 – 02:10.000 (Duration: ~40s)*  
**Visual Focus:** Medicine details view (active ingredients, warnings, storage), adding to cart, quantity stepper up to 5, safety cap validation message.  
**Spoken Narration:**  
> "Opening a medicine reveals complete clinical transparency: active ingredients, dosage warnings, manufacturer details, and return policies. Let's add Paracare 650 to the cart. In the shopping cart, MediCart enforces strict Indian e-pharmacy regulatory compliance: users are capped at a maximum of five packs per formulation to prevent hoarding. The transparent fee engine computes item totals, a flat delivery fee of thirty rupees, and hygienic packaging in integer paise."

---

### Scene 5: Checkout & Order Placement (2:10 – 2:55)
*Timecode: 02:10.000 – 02:55.000 (Duration: ~45s)*  
**Visual Focus:** Address selection, simulated UPI demo sandbox, tapping 'Place Order', idempotency protection.  
**Spoken Narration:**  
> "Proceeding to checkout, we confirm our saved delivery address in Indiranagar, Bengaluru. For payment, MediCart offers a simulated non-financial sandbox supporting UPI Demo, Cash on Delivery, and Cards, clearly communicating that no real money is deducted. When placing the order, the client attaches a unique UUID idempotency key in the request header. This guarantees that even during network jitter or accidental double-taps, duplicate orders and double inventory decrements are prevented."

---

### Scene 6: Order Confirmation & Live Tracking (2:55 – 3:30)
*Timecode: 02:55.000 – 03:30.000 (Duration: ~35s)*  
**Visual Focus:** Animated order confirmation checkmark, order reference #MC-2026-..., 4-stage tracking timeline, and order history list.  
**Spoken Narration:**  
> "The order is confirmed live by the server, generating our unique business reference. Tapping into Order Details displays a four-stage visual timeline: Order Confirmed, Dispensing and Packing, Out for Delivery, and Delivered. The line items preserve an immutable snapshot of prices at the moment of checkout. In Order History, past orders remain fully accessible, synchronized with MongoDB."

---

### Scene 7: Reliability & Security Architecture (3:30 – 4:05)
*Timecode: 03:30.000 – 04:05.000 (Duration: ~35s)*  
**Visual Focus:** Profile screen, offline mode simulation toggle, SQLite cache fallback, EncryptedSharedPreferences.  
**Spoken Narration:**  
> "Behind the scenes, reliability and security are top priorities. On the client, authentication tokens are stored using Android Jetpack Security's EncryptedSharedPreferences, protected by AES-256 GCM in the hardware Keystore. When network connectivity drops, the repository automatically falls back to an offline SQLite database, ensuring users can still access saved medicines and cart items without interruption."

---

### Scene 8: Technology Stack & Verification (4:05 – 4:35)
*Timecode: 04:05.000 – 04:35.000 (Duration: ~30s)*  
**Visual Focus:** Technical architecture diagram, Render cloud backend, MongoDB Atlas cluster, and 88 automated tests passing summary.  
**Spoken Narration:**  
> "MediCart's modern architecture combines Kotlin, Jetpack Compose, and MVVM on Android with a Node.js, Express, and MongoDB Atlas backend deployed publicly on Render. The system has undergone rigorous automated testing, achieving a one hundred percent pass rate across forty-nine Android unit tests and thirty-nine backend API tests, totaling eighty-eight verified test cases."

---

### Scene 9: Closing & Wrap-up (4:35 – 4:50)
*Timecode: 04:35.000 – 04:50.000 (Duration: ~15s)*  
**Visual Focus:** Professional outro screen with MediCart brand card, GitHub repository link, candidate name, and ALGIONS AI LABS acknowledgement.  
**Spoken Narration:**  
> "Thank you for reviewing the MediCart demonstration. With native Android excellence, cloud scalability, and pharmaceutical safety compliance, MediCart is fully prepared for evaluation. Developed with dedication by Sahil Kumar Chaudhary."
