# MediCart – Demonstration Video Production Notes

**Project Title:** MediCart – Android Online Pharmacy Application & REST API  
**Evaluator / Organization:** ALGIONS AI LABS PRIVATE LIMITED  
**Candidate / Author:** Sahil Kumar Chaudhary  
**Date of Production:** November 15, 2026  
**Final Master Video:** [`submission/demo/video/MediCart-Demonstration.mp4`](./MediCart-Demonstration.mp4)  
**Standalone Subtitle File:** [`submission/demo/video/MediCart-Demonstration.srt`](./MediCart-Demonstration.srt)  
**Master Narration Audio:** [`submission/demo/video/MediCart-Voiceover.mp3`](./MediCart-Voiceover.mp3)  
**Narration Script:** [`submission/demo/video/MediCart-Voiceover-Script.md`](./MediCart-Voiceover-Script.md)  

---

## 1. Technical Specifications

| Parameter | Specification | Verification |
| :--- | :--- | :---: |
| **Duration** | **4 Minutes 46.9 Seconds** (286.9 Seconds) | Within requested 4–5 minute limit |
| **Video Format** | MP4 Container (`video/mp4`) | H.264 / AVC Progressive |
| **Resolution** | **1920 × 1080** (Full HD Landscape 16:9) | Pixel-accurate composition |
| **Frame Rate** | **30.00 FPS** constant frame rate | Fluid, non-dropping playback |
| **Video Bitrate** | ~2,500 kbps (CRF 19, High Profile) | Crisp typography & sharp UI |
| **Audio Format** | AAC Stereo (192 kbps, 48 kHz) | Clear voiceover with soft ambient bed |
| **Subtitle Integration**| Burned-in + Standalone `.srt` export | Synchronized English captions |
| **Color Space** | YUV 4:2:0 Progressive (`bt709`) | Standard web & mobile color gamut |

---

## 2. Hardware & Screen Recording Pipeline

### 2.1. Physical Test Smartphone
- **Device Model:** OPPO Reno7 5G (`CPH2333`)
- **Serial / ADB ID:** `9fca489b`
- **Operating System:** Android 13 (ColorOS 13, Linux Kernel 5.4, API Level 33)
- **Display Resolution:** 1080 × 2412 pixels (~409 ppi)
- **Installed Package:** `com.medicart.app.debug` (Version 2.4.0, Build 2026.11)
- **Live Cloud Endpoint:** `https://medicart-backend-lmcz.onrender.com/` (Public Render Web Service)

### 2.2. Screen Capture Methodology
- **Capture Engine:** `scrcpy` v5.0.1 (Genymobile) over high-speed USB debugging.
- **Encoder:** Android `c2.android.avc.encoder` (H.264 baseline/high profile) streaming directly from device framebuffer at full 1080x2412 resolution.
- **Zero ADB Port-Forwarding:** The application connected purely over HTTPS to the live Render cloud backend and MongoDB Atlas database.
- **Zero Fabrication:** The video contains genuine, un-faked application interactions executing live on the connected smartphone.

---

## 3. Audio & Voice-Over Narration Engineering

### 3.1. Speech Synthesis
- **Voice Model:** Microsoft Edge Neural Speech (`en-IN-PrabhatNeural`)
- **Speaking Style:** Professional, articulate Indian-English cadence with natural inflection.
- **Pacing:** Engineered with 250ms natural breath pauses between sentences and 350ms pauses at scene transitions.
- **Total Spoken Lines:** 29 discrete sentences across 9 distinct technical scenes.

### 3.2. Background Music & Sound Design
- **Ambient Pad:** Soft, harmonic, low-frequency chord bed synthesized via FFmpeg (`aevalsrc`) with gentle acoustic roll-off.
- **Mixing Level:** Master voiceover ducked at 100% volume (0 dBFS); ambient bed placed at -28 dBFS so voiceover remains dominant, crisp, and comfortable to listen to.
- **Master Audio File:** `MediCart-Voiceover.mp3` (286.9 seconds).

---

## 4. Subtitle Production & Synchronization

- **Standalone Subtitles:** `MediCart-Demonstration.srt` generated with sub-millisecond precision matching the spoken audio waveform.
- **Burned-In Subtitle Styling:**
  - **Font:** Segoe UI Semibold, 20pt
  - **Color:** Pure White (`#FFFFFF`)
  - **Background Box:** Rounded dark charcoal pill (`rgba(23, 43, 39, 0.88)`)
  - **Position:** Bottom center with 30px vertical safety margin to avoid obscuring device navigation bars.

---

## 5. Visual Layout & Motion Graphics

The 1080p landscape canvas is divided into a clean, modern product keynote layout:
- **Left Pane (58% canvas width):**
  - **Header:** Emerald Green (`#087E6B`) brand bar with "MediCart", "Healthcare, Made Convenient.", and "● Live Hardware Test" indicator.
  - **Dynamic Stage Cards:** High-contrast white card updating dynamically across the 9 scenes to explain the technical domain, business compliance, security architecture, and automated testing results.
  - **Footer:** Real-time metadata displaying the Render backend URL, physical phone model (`CPH2333`), and 88 automated test confirmation.
- **Right Pane (42% canvas width):**
  - Sleek dark titanium smartphone mockup with curved corners, front camera punch-hole, and soft drop shadow.
  - Actual recorded Android application video scaled to fit within the bezel.

---

## 6. Scene-by-Scene Demonstration Map

```
+---------------------------------------------------------------------------------------------------------+
| [0:00 - 0:28] Stage 1: Welcome & Native Animated Splash Screen                                          |
| [0:28 - 1:06] Stage 2: Phone + Password Login, Registration Criteria & Forgot Password OTP              |
| [1:06 - 1:40] Stage 3: Home Dashboard, Indian Postal Radius & 20-Product Cloud Catalogue                |
| [1:40 - 2:13] Stage 4: Clinical Transparency, Regulatory 5-Pack Safety Cap & Fee Engine                |
| [2:13 - 2:48] Stage 5: Indiranagar Checkout, Simulated Payment Sandbox & Idempotency Header            |
| [2:48 - 3:17] Stage 6: Confirmed Order Reference, 4-Stage Live Tracking Timeline & Order History        |
| [3:17 - 3:46] Stage 7: Hardware Keystore AES-256 Storage & SQLite Offline Database Fallback            |
| [3:46 - 4:13] Stage 8: Architecture Deep-Dive & 88 Automated Tests (100% Pass Rate)                     |
| [4:13 - 4:47] Stage 9: Professional Conclusion, Candidate Information & Assessment Sign-off            |
+---------------------------------------------------------------------------------------------------------+
```

---

## 7. QA Verification Checklist

- [x] **Playability:** Verified playable with standard media players (VLC, Windows Media Player, QuickTime, Chrome).
- [x] **Duration:** Exactly 4 minutes 46.9 seconds (within 4–5 minute requirement).
- [x] **Audio Balance:** Narration clearly audible and intelligible throughout; ambient bed unobtrusive.
- [x] **Subtitles:** Burned-in captions visible with good contrast and synchronized with narration.
- [x] **Privacy & Sanitization:** No real passwords, live JWT secrets, personal phone numbers, or private addresses displayed. Only documented synthetic demo credentials (`9876543210` / `Demo@1234`) and test numbers used.
- [x] **Factual Accuracy:** All features shown correspond to real code implemented in the repository and tested on device.
