import os
import subprocess
import shutil

SCREEN_DEFS = [
    {
        "id": "00_splash",
        "title": "Splash Screen",
        "file": "screen_00_splash.html",
        "body": """
        <!-- Status Bar -->
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span>
          <div class="w-3 h-3 bg-[#172B27] rounded-full"></div>
          <div class="flex items-center gap-1.5 text-xs">
            <span>5G</span>
            <span>📶</span>
            <span>🔋 98%</span>
          </div>
        </div>

        <div class="flex-1 flex flex-col items-center justify-between p-8 text-center">
          <div></div>
          
          <!-- Center Logo & Name -->
          <div class="flex flex-col items-center">
            <div class="relative mb-6">
              <div class="absolute -inset-4 bg-[#DDF1EA] rounded-full blur-xl opacity-80"></div>
              <div class="relative w-28 h-28 rounded-3xl bg-gradient-to-br from-[#087E6B] to-[#075B4F] flex items-center justify-center shadow-xl shadow-[#087E6B]/30 border border-[#087E6B]/30">
                <svg class="w-16 h-16 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
                  <path d="M3 6h18"/>
                  <path d="M16 10a4 4 0 0 1-8 0"/>
                  <path d="M12 13v6" stroke="#DDF1EA" stroke-width="2.5"/>
                  <path d="M9 16h6" stroke="#DDF1EA" stroke-width="2.5"/>
                </svg>
              </div>
            </div>

            <h1 class="text-3xl font-extrabold text-[#172B27] tracking-tight">Medi<span class="text-[#087E6B]">Cart</span></h1>
            <p class="text-sm font-semibold text-[#71817B] mt-2 tracking-wide uppercase text-[11px]">Healthcare, Made Convenient.</p>
            <div class="mt-4 px-3 py-1 rounded-full bg-[#DDF1EA] text-[#087E6B] text-[11px] font-bold">
              🇮🇳 India's Trusted E-Pharmacy
            </div>
          </div>

          <!-- Bottom Indicator & Launcher Spec -->
          <div class="w-full flex flex-col items-center gap-3">
            <div class="flex items-center gap-2">
              <div class="w-2 h-2 rounded-full bg-[#087E6B] animate-ping"></div>
              <span class="text-xs text-[#71817B]">Securing connection...</span>
            </div>
            <div class="p-3 rounded-xl bg-white border border-[#E5ECE8] w-full text-left flex items-center gap-3 shadow-sm">
              <div class="w-8 h-8 rounded-lg bg-[#087E6B] flex items-center justify-center text-white shrink-0">
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 6v12M6 12h12"/></svg>
              </div>
              <div>
                <div class="text-[11px] font-bold text-[#172B27]">Reusable Launcher Icon</div>
                <div class="text-[10px] text-[#71817B]">Adaptive vector icon for Android home screen</div>
              </div>
            </div>
          </div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "01_login",
        "title": "Login Screen",
        "file": "screen_01_login.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="flex-1 p-6 flex flex-col justify-between">
          <div>
            <div class="flex items-center gap-2.5 mb-8">
              <div class="w-9 h-9 rounded-xl bg-[#087E6B] flex items-center justify-center text-white shadow-sm">
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M12 13v5M9.5 15.5h5"/></svg>
              </div>
              <span class="text-lg font-bold text-[#172B27]">Medi<span class="text-[#087E6B]">Cart</span></span>
            </div>

            <div class="mb-8">
              <h2 class="text-2xl font-bold text-[#172B27] tracking-tight">Welcome to MediCart 👋</h2>
              <p class="text-sm text-[#71817B] mt-1.5">Enter your Indian mobile number to sign in or create your healthcare account.</p>
            </div>

            <div class="bg-white p-5 rounded-2xl border border-[#E5ECE8] shadow-sm mb-5">
              <label class="block text-xs font-bold text-[#172B27] uppercase tracking-wider mb-2">Mobile Number</label>
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-1.5 px-3 py-2.5 rounded-xl bg-[#F7F9F7] border border-[#E5ECE8] text-sm font-semibold text-[#172B27]">
                  <span>🇮🇳</span>
                  <span>+91</span>
                </div>
                <div class="flex-1 px-3.5 py-2.5 rounded-xl border-2 border-[#087E6B] bg-white flex items-center justify-between">
                  <span class="text-base font-semibold text-[#172B27]">98765 43210</span>
                  <span class="text-sm text-[#16865B]">✓</span>
                </div>
              </div>
              <p class="text-[11px] text-[#71817B] mt-2 flex items-center gap-1">
                <span>🔒</span> We will send a 6-digit OTP for instant login.
              </p>
            </div>

            <button class="w-full h-12 bg-[#087E6B] text-white font-bold text-sm rounded-xl shadow-md shadow-[#087E6B]/20 flex items-center justify-center gap-2">
              <span>Get Verification Code</span>
              <span>→</span>
            </button>

            <div class="grid grid-cols-2 gap-3 mt-6">
              <div class="p-3 rounded-xl bg-[#DDF1EA]/60 border border-[#DDF1EA] text-left">
                <div class="text-xs font-bold text-[#087E6B]">Verified Generic Meds</div>
                <div class="text-[11px] text-[#71817B]">Quality tested formulations</div>
              </div>
              <div class="p-3 rounded-xl bg-[#DDF1EA]/60 border border-[#DDF1EA] text-left">
                <div class="text-xs font-bold text-[#087E6B]">PIN-Code Delivery</div>
                <div class="text-[11px] text-[#71817B]">Across 19,000+ Indian PINs</div>
              </div>
            </div>
          </div>

          <div class="pt-6 border-t border-[#E5ECE8] text-center">
            <p class="text-[11px] text-[#71817B] leading-relaxed">
              By tapping continue, you agree to MediCart's <strong class="text-[#087E6B]">Terms of Use</strong> and <strong class="text-[#087E6B]">Privacy Policy</strong>.
            </p>
          </div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "02_otp",
        "title": "OTP Verification",
        "file": "screen_02_otp.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="flex-1 p-6 flex flex-col justify-between">
          <div>
            <div class="flex items-center gap-3 mb-6">
              <button class="w-9 h-9 rounded-xl bg-white border border-[#E5ECE8] flex items-center justify-center text-[#172B27] shadow-xs">
                ←
              </button>
              <span class="text-xs font-bold uppercase tracking-wider text-[#71817B]">Verification</span>
            </div>

            <div class="mb-6">
              <h2 class="text-2xl font-bold text-[#172B27] tracking-tight">Enter 6-Digit OTP</h2>
              <p class="text-sm text-[#71817B] mt-1.5">
                We sent a verification SMS to <span class="font-bold text-[#172B27]">+91 98765 43210</span>.
                <span class="text-[#087E6B] font-semibold underline ml-1 text-xs">Edit</span>
              </p>
            </div>

            <div class="grid grid-cols-6 gap-2 my-6">
              <div class="h-13 py-3 rounded-xl bg-white border-2 border-[#087E6B] text-center text-xl font-bold text-[#172B27] shadow-sm">4</div>
              <div class="h-13 py-3 rounded-xl bg-white border-2 border-[#087E6B] text-center text-xl font-bold text-[#172B27] shadow-sm">8</div>
              <div class="h-13 py-3 rounded-xl bg-white border-2 border-[#087E6B] text-center text-xl font-bold text-[#172B27] shadow-sm">7</div>
              <div class="h-13 py-3 rounded-xl bg-white border-2 border-[#087E6B] text-center text-xl font-bold text-[#172B27] shadow-sm">2</div>
              <div class="h-13 py-3 rounded-xl bg-white border-2 border-[#087E6B] text-center text-xl font-bold text-[#172B27] shadow-sm">1</div>
              <div class="h-13 py-3 rounded-xl bg-[#DDF1EA] border-2 border-[#087E6B] text-center text-xl font-bold text-[#087E6B] shadow-sm">|</div>
            </div>

            <div class="flex items-center justify-between text-xs px-1 mb-6">
              <span class="text-[#71817B]">Resend OTP in <strong class="text-[#172B27]">00:24s</strong></span>
              <span class="text-[#71817B] font-semibold opacity-60">Resend SMS</span>
            </div>

            <button class="w-full h-12 bg-[#087E6B] text-white font-bold text-sm rounded-xl shadow-md shadow-[#087E6B]/20 flex items-center justify-center gap-2">
              <span>Verify & Proceed</span>
              <span>✓</span>
            </button>
          </div>

          <div class="bg-white/80 backdrop-blur rounded-2xl p-3 border border-[#E5ECE8] shadow-sm">
            <div class="grid grid-cols-3 gap-2 text-center">
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">1</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">2</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">3</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">4</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">5</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">6</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">7</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">8</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">9</div>
              <div class="py-2.5 rounded-lg text-xs font-semibold text-[#71817B] flex items-center justify-center">CLEAR</div>
              <div class="py-2.5 rounded-lg hover:bg-[#F7F9F7] font-bold text-base text-[#172B27]">0</div>
              <div class="py-2.5 rounded-lg text-xs font-semibold text-[#71817B] flex items-center justify-center">⌫</div>
            </div>
          </div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "03_home",
        "title": "Home Screen",
        "file": "screen_03_home.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 pt-2 pb-3 border-b border-[#E5ECE8]">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-[#DDF1EA] text-[#087E6B] flex items-center justify-center font-bold">📍</div>
              <div>
                <div class="text-[10px] uppercase font-bold text-[#71817B] tracking-wider">Deliver To</div>
                <div class="text-xs font-bold text-[#172B27] flex items-center gap-1">
                  <span>Indiranagar, Bengaluru - 560038</span>
                  <span class="text-[#087E6B]">▾</span>
                </div>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-[#F7F9F7] border border-[#E5ECE8] flex items-center justify-center text-xs relative">
                🔔
                <span class="absolute top-1 right-1 w-2 h-2 rounded-full bg-[#D64B4B]"></span>
              </div>
              <div class="w-8 h-8 rounded-full bg-[#087E6B] text-white flex items-center justify-center font-bold text-xs shadow-xs">
                SK
              </div>
            </div>
          </div>

          <div class="relative">
            <input type="text" readonly placeholder="Search medicines, health essentials..." class="w-full h-11 pl-10 pr-10 rounded-xl bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
            <span class="absolute left-3.5 top-3.5 text-xs text-[#71817B]">🔍</span>
            <span class="absolute right-3.5 top-3.5 text-xs text-[#087E6B]">🎙️</span>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-4 space-y-5">
          <div class="p-4 rounded-2xl bg-gradient-to-br from-[#087E6B] to-[#075B4F] text-white shadow-md shadow-[#087E6B]/15 relative overflow-hidden">
            <div class="relative z-10 max-w-[210px]">
              <span class="px-2 py-0.5 rounded-full bg-white/20 text-[10px] font-bold tracking-wide uppercase">Monsoon Wellness</span>
              <h3 class="text-base font-extrabold mt-1 leading-tight">Healthcare Essentials at Transparent Prices</h3>
              <p class="text-[11px] text-white/80 mt-1">Verified formulations delivered safely to your home.</p>
              <button class="mt-3 px-3 py-1.5 rounded-lg bg-white text-[#087E6B] font-bold text-[11px] shadow-sm">
                Explore Catalogue →
              </button>
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-bold text-[#172B27]">Shop by Category</h3>
              <span class="text-xs font-bold text-[#087E6B]">View All (8)</span>
            </div>
            <div class="grid grid-cols-4 gap-2.5">
              <div class="bg-white p-2.5 rounded-xl border border-[#E5ECE8] flex flex-col items-center text-center shadow-xs">
                <div class="w-10 h-10 rounded-lg bg-[#DDF1EA] text-[#087E6B] flex items-center justify-center text-lg mb-1.5">💊</div>
                <span class="text-[11px] font-bold text-[#172B27]">Pain Relief</span>
                <span class="text-[9px] text-[#71817B]">42 items</span>
              </div>
              <div class="bg-white p-2.5 rounded-xl border border-[#E5ECE8] flex flex-col items-center text-center shadow-xs">
                <div class="w-10 h-10 rounded-lg bg-[#DDF1EA] text-[#087E6B] flex items-center justify-center text-lg mb-1.5">🫁</div>
                <span class="text-[11px] font-bold text-[#172B27]">Cough & Cold</span>
                <span class="text-[9px] text-[#71817B]">38 items</span>
              </div>
              <div class="bg-white p-2.5 rounded-xl border border-[#E5ECE8] flex flex-col items-center text-center shadow-xs">
                <div class="w-10 h-10 rounded-lg bg-[#DDF1EA] text-[#087E6B] flex items-center justify-center text-lg mb-1.5">🩺</div>
                <span class="text-[11px] font-bold text-[#172B27]">First Aid</span>
                <span class="text-[9px] text-[#71817B]">29 items</span>
              </div>
              <div class="bg-white p-2.5 rounded-xl border border-[#E5ECE8] flex flex-col items-center text-center shadow-xs">
                <div class="w-10 h-10 rounded-lg bg-[#DDF1EA] text-[#087E6B] flex items-center justify-center text-lg mb-1.5">🛡️</div>
                <span class="text-[11px] font-bold text-[#172B27]">Vitamins</span>
                <span class="text-[9px] text-[#71817B]">54 items</span>
              </div>
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between mb-3">
              <div>
                <h3 class="text-sm font-bold text-[#172B27]">Featured Medicines</h3>
                <p class="text-[10px] text-[#71817B]">Verified pharmacy items</p>
              </div>
              <span class="text-xs font-bold text-[#087E6B]">See More</span>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div class="bg-white rounded-xl border border-[#E5ECE8] p-3 flex flex-col justify-between shadow-xs">
                <div>
                  <div class="w-full h-24 rounded-lg bg-[#F7F9F7] flex items-center justify-center relative mb-2">
                    <span class="text-3xl">💊</span>
                    <span class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded text-[9px] font-bold bg-[#DDF1EA] text-[#087E6B]">10% OFF</span>
                  </div>
                  <div class="text-[10px] font-bold text-[#16865B] mb-0.5">● In Stock</div>
                  <h4 class="text-xs font-bold text-[#172B27] leading-snug">Paracare 650mg</h4>
                  <p class="text-[10px] text-[#71817B]">Strip of 15 Tablets</p>
                </div>
                <div class="mt-3 pt-2 border-t border-[#E5ECE8] flex items-center justify-between">
                  <div>
                    <div class="text-xs font-extrabold text-[#172B27]">₹34.00</div>
                    <div class="text-[9px] text-[#71817B] line-through">₹38.00</div>
                  </div>
                  <button class="px-2.5 py-1 bg-[#087E6B] text-white text-[11px] font-bold rounded-lg shadow-xs">+ Add</button>
                </div>
              </div>

              <div class="bg-white rounded-xl border border-[#E5ECE8] p-3 flex flex-col justify-between shadow-xs">
                <div>
                  <div class="w-full h-24 rounded-lg bg-[#F7F9F7] flex items-center justify-center relative mb-2">
                    <span class="text-3xl">🌿</span>
                    <span class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded text-[9px] font-bold bg-[#DDF1EA] text-[#087E6B]">15% OFF</span>
                  </div>
                  <div class="text-[10px] font-bold text-[#16865B] mb-0.5">● In Stock</div>
                  <h4 class="text-xs font-bold text-[#172B27] leading-snug">AyurTulsi Syrup</h4>
                  <p class="text-[10px] text-[#71817B]">Bottle of 100 ml</p>
                </div>
                <div class="mt-3 pt-2 border-t border-[#E5ECE8] flex items-center justify-between">
                  <div>
                    <div class="text-xs font-extrabold text-[#172B27]">₹85.00</div>
                    <div class="text-[9px] text-[#71817B] line-through">₹99.00</div>
                  </div>
                  <button class="px-2.5 py-1 bg-[#087E6B] text-white text-[11px] font-bold rounded-lg shadow-xs">+ Add</button>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex items-center justify-between">
            <div class="flex items-center gap-2.5">
              <div class="w-10 h-10 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-lg">🩺</div>
              <div>
                <div class="text-xs font-bold text-[#172B27]">OmniTherm Digital Thermometer</div>
                <div class="text-[10px] text-[#71817B]">1 Unit • ₹199.00</div>
              </div>
            </div>
            <button class="text-xs font-bold text-[#087E6B]">View</button>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] px-4 py-2 flex justify-around items-center">
          <div class="flex flex-col items-center text-[#087E6B]"><span class="text-sm">🏠</span><span class="text-[10px] font-bold mt-0.5">Home</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📑</span><span class="text-[10px] font-medium mt-0.5">Catalogue</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📦</span><span class="text-[10px] font-medium mt-0.5">Orders</span></div>
          <div class="flex flex-col items-center text-[#71817B] relative">
            <span class="text-sm">🛍️</span>
            <span class="absolute -top-1 -right-2 bg-[#087E6B] text-white text-[9px] font-bold px-1 rounded-full">2</span>
            <span class="text-[10px] font-medium mt-0.5">Cart</span>
          </div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "04_catalogue",
        "title": "Medicine Catalogue",
        "file": "screen_04_catalogue.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-2.5 border-b border-[#E5ECE8] space-y-2.5">
          <div class="flex items-center gap-3">
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
            <div class="flex-1 relative">
              <input type="text" value="Paracetamol" class="w-full h-9 pl-8 pr-7 rounded-lg bg-[#F7F9F7] border border-[#087E6B] text-xs font-semibold text-[#172B27]">
              <span class="text-xs text-[#087E6B] absolute left-2.5 top-2.5">🔍</span>
              <span class="text-xs text-[#71817B] absolute right-2.5 top-2.5">✕</span>
            </div>
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] flex items-center justify-center text-xs">⚙️</button>
          </div>

          <div class="flex items-center gap-2 overflow-x-auto pb-1 text-xs">
            <span class="px-2.5 py-1 rounded-full bg-[#087E6B] text-white font-bold text-[11px] whitespace-nowrap">All (14)</span>
            <span class="px-2.5 py-1 rounded-full bg-white border border-[#E5ECE8] text-[#172B27] font-medium text-[11px] whitespace-nowrap">Tablets (8)</span>
            <span class="px-2.5 py-1 rounded-full bg-white border border-[#E5ECE8] text-[#172B27] font-medium text-[11px] whitespace-nowrap">Syrup (4)</span>
            <span class="px-2.5 py-1 rounded-full bg-white border border-[#E5ECE8] text-[#172B27] font-medium text-[11px] whitespace-nowrap">Drops (2)</span>
          </div>
        </div>

        <div class="px-4 py-2 bg-[#F7F9F7] flex items-center justify-between text-[11px] text-[#71817B] border-b border-[#E5ECE8]">
          <span>Showing 14 results for <strong>"Paracetamol"</strong></span>
          <span class="font-bold text-[#087E6B]">Sort: Price Low → High ▾</span>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3">
          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex gap-3 shadow-xs">
            <div class="w-20 h-20 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-3xl shrink-0">💊</div>
            <div class="flex-1 flex flex-col justify-between">
              <div>
                <div class="flex items-center justify-between">
                  <span class="text-[10px] font-bold text-[#16865B]">● In Stock</span>
                  <span class="text-[9px] font-bold text-[#087E6B] bg-[#DDF1EA] px-1.5 py-0.5 rounded">10% OFF</span>
                </div>
                <h4 class="text-xs font-bold text-[#172B27] mt-0.5">Paracare 650mg Tablets</h4>
                <p class="text-[10px] text-[#71817B]">Strip of 15 Tablets • Paracetamol IP</p>
              </div>
              <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#E5ECE8]">
                <div class="flex items-baseline gap-1.5">
                  <span class="text-sm font-extrabold text-[#172B27]">₹34.00</span>
                  <span class="text-[10px] text-[#71817B] line-through">₹38.00</span>
                </div>
                <div class="flex items-center gap-2 bg-[#DDF1EA] px-2 py-1 rounded-lg">
                  <button class="text-xs font-bold text-[#087E6B]">-</button>
                  <span class="text-xs font-bold text-[#172B27]">1</span>
                  <button class="text-xs font-bold text-[#087E6B]">+</button>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex gap-3 shadow-xs">
            <div class="w-20 h-20 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-3xl shrink-0">🧴</div>
            <div class="flex-1 flex flex-col justify-between">
              <div>
                <div class="flex items-center justify-between">
                  <span class="text-[10px] font-bold text-[#16865B]">● In Stock</span>
                  <span class="text-[9px] font-bold text-[#087E6B] bg-[#DDF1EA] px-1.5 py-0.5 rounded">12% OFF</span>
                </div>
                <h4 class="text-xs font-bold text-[#172B27] mt-0.5">Paracare Pediatric Syrup</h4>
                <p class="text-[10px] text-[#71817B]">Bottle of 60 ml • 120mg/5ml</p>
              </div>
              <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#E5ECE8]">
                <div class="flex items-baseline gap-1.5">
                  <span class="text-sm font-extrabold text-[#172B27]">₹42.00</span>
                  <span class="text-[10px] text-[#71817B] line-through">₹48.00</span>
                </div>
                <button class="px-3 py-1 bg-[#087E6B] text-white text-[11px] font-bold rounded-lg shadow-xs">+ Add</button>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex gap-3 shadow-xs">
            <div class="w-20 h-20 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-3xl shrink-0">💊</div>
            <div class="flex-1 flex flex-col justify-between">
              <div>
                <div class="flex items-center justify-between">
                  <span class="text-[10px] font-bold text-[#E49A32]">⚡ Only 3 left</span>
                </div>
                <h4 class="text-xs font-bold text-[#172B27] mt-0.5">Paracare 500mg Standard</h4>
                <p class="text-[10px] text-[#71817B]">Strip of 10 Tablets • Paracetamol IP</p>
              </div>
              <div class="flex items-center justify-between mt-2 pt-1 border-t border-[#E5ECE8]">
                <div class="flex items-baseline gap-1.5">
                  <span class="text-sm font-extrabold text-[#172B27]">₹22.00</span>
                  <span class="text-[10px] text-[#71817B] line-through">₹25.00</span>
                </div>
                <button class="px-3 py-1 bg-[#087E6B] text-white text-[11px] font-bold rounded-lg shadow-xs">+ Add</button>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] px-4 py-2 flex justify-around items-center">
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🏠</span><span class="text-[10px] font-medium">Home</span></div>
          <div class="flex flex-col items-center text-[#087E6B]"><span class="text-sm">📑</span><span class="text-[10px] font-bold">Catalogue</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📦</span><span class="text-[10px] font-medium">Orders</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🛍️</span><span class="text-[10px] font-medium">Cart</span></div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "05_product",
        "title": "Product Details",
        "file": "screen_05_product.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center justify-between">
          <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
          <span class="text-xs font-bold text-[#172B27]">Medicine Details</span>
          <div class="flex items-center gap-2">
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-xs">🤍</button>
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-xs relative">🛍️<span class="absolute top-1 right-1 w-2 h-2 rounded-full bg-[#087E6B]"></span></button>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-4 space-y-4">
          <div class="w-full h-48 rounded-2xl bg-white border border-[#E5ECE8] flex items-center justify-center relative shadow-xs">
            <span class="text-6xl">💊</span>
            <span class="absolute top-3 left-3 px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#DDF1EA] text-[#087E6B]">Verified Generic</span>
            <span class="absolute bottom-3 right-3 text-[10px] text-[#71817B] bg-[#F7F9F7] px-2 py-1 rounded-md border border-[#E5ECE8]">Standard Blister Pack</span>
          </div>

          <div class="bg-white p-4 rounded-2xl border border-[#E5ECE8] shadow-xs">
            <div class="flex items-center justify-between mb-1">
              <span class="text-[11px] font-bold text-[#16865B]">● In Stock • Ready for dispatch</span>
              <span class="text-[10px] font-bold text-[#087E6B] bg-[#DDF1EA] px-2 py-0.5 rounded-full">10% Savings</span>
            </div>
            <h2 class="text-lg font-bold text-[#172B27]">Paracare 650mg Tablets</h2>
            <p class="text-xs text-[#71817B] mt-0.5">Strip of 15 Tablets • Oral Solid Dosage</p>

            <div class="flex items-baseline gap-2 mt-3 pt-3 border-t border-[#E5ECE8]">
              <span class="text-2xl font-extrabold text-[#172B27]">₹34.00</span>
              <span class="text-xs text-[#71817B] line-through">MRP ₹38.00</span>
              <span class="text-[10px] text-[#71817B] ml-auto">Inclusive of all taxes</span>
            </div>
          </div>

          <div class="bg-white p-4 rounded-2xl border border-[#E5ECE8] shadow-xs space-y-3">
            <h3 class="text-xs font-bold text-[#172B27] uppercase tracking-wider">Product Information</h3>
            <div class="space-y-2 text-xs">
              <div class="flex justify-between py-1 border-b border-[#F7F9F7]">
                <span class="text-[#71817B]">Active Ingredient</span>
                <span class="font-bold text-[#172B27]">Paracetamol IP 650 mg</span>
              </div>
              <div class="flex justify-between py-1 border-b border-[#F7F9F7]">
                <span class="text-[#71817B]">Form & Packaging</span>
                <span class="font-bold text-[#172B27]">15 Tablets in Blister Pack</span>
              </div>
              <div class="flex justify-between py-1 border-b border-[#F7F9F7]">
                <span class="text-[#71817B]">Storage Instruction</span>
                <span class="font-bold text-[#172B27]">Store below 25°C in dry place</span>
              </div>
              <div class="flex justify-between py-1">
                <span class="text-[#71817B]">Return Policy</span>
                <span class="font-bold text-[#172B27]">7 Days Returnable</span>
              </div>
            </div>

            <div class="p-3 rounded-xl bg-[#F7F9F7] border border-[#E5ECE8] text-[10px] text-[#71817B] leading-relaxed">
              ℹ️ <strong>Healthcare Disclaimer:</strong> MediCart provides verified fictional pharmacy product data for order fulfillment demo. No dosage or medical advice is provided.
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] p-3 flex items-center gap-3">
          <div class="flex items-center gap-3 bg-[#F7F9F7] border border-[#E5ECE8] px-3 py-2 rounded-xl">
            <button class="text-sm font-bold text-[#71817B]">-</button>
            <span class="text-xs font-bold text-[#172B27]">1</span>
            <button class="text-sm font-bold text-[#087E6B]">+</button>
          </div>
          <button class="flex-1 h-11 bg-[#087E6B] text-white font-bold text-xs rounded-xl shadow-md shadow-[#087E6B]/20 flex items-center justify-center gap-2">
            <span>Add to Cart</span>
            <span>•</span>
            <span>₹34.00</span>
          </button>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "06_cart",
        "title": "Shopping Cart",
        "file": "screen_06_cart.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center justify-between">
          <div class="flex items-center gap-3">
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
            <span class="text-sm font-bold text-[#172B27]">Shopping Cart (2 items)</span>
          </div>
          <button class="text-xs font-bold text-[#D64B4B]">Clear</button>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3">
          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex items-center justify-between text-xs">
            <div class="flex items-center gap-2">
              <span class="text-base">📍</span>
              <div class="leading-tight">
                <span class="text-[10px] text-[#71817B] block">Delivering to:</span>
                <strong class="text-[#172B27]">Indiranagar, Bengaluru - 560038</strong>
              </div>
            </div>
            <button class="text-[#087E6B] font-bold text-xs">Change</button>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex gap-3 shadow-xs">
            <div class="w-16 h-16 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-2xl shrink-0">💊</div>
            <div class="flex-1 flex flex-col justify-between">
              <div class="flex justify-between items-start">
                <div>
                  <h4 class="text-xs font-bold text-[#172B27]">Paracare 650mg Tablets</h4>
                  <p class="text-[10px] text-[#71817B]">Strip of 15 Tablets</p>
                </div>
                <button class="text-[#71817B] text-xs">✕</button>
              </div>
              <div class="flex items-center justify-between mt-2">
                <span class="text-xs font-bold text-[#172B27]">₹68.00 <span class="text-[9px] text-[#71817B] font-normal">(₹34 × 2)</span></span>
                <div class="flex items-center gap-2 bg-[#DDF1EA] px-2 py-0.5 rounded-lg">
                  <button class="text-xs font-bold text-[#087E6B]">-</button>
                  <span class="text-xs font-bold text-[#172B27]">2</span>
                  <button class="text-xs font-bold text-[#087E6B]">+</button>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex gap-3 shadow-xs">
            <div class="w-16 h-16 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-2xl shrink-0">🌿</div>
            <div class="flex-1 flex flex-col justify-between">
              <div class="flex justify-between items-start">
                <div>
                  <h4 class="text-xs font-bold text-[#172B27]">AyurTulsi Cough Syrup</h4>
                  <p class="text-[10px] text-[#71817B]">Bottle of 100 ml</p>
                </div>
                <button class="text-[#71817B] text-xs">✕</button>
              </div>
              <div class="flex items-center justify-between mt-2">
                <span class="text-xs font-bold text-[#172B27]">₹85.00 <span class="text-[9px] text-[#71817B] font-normal">(₹85 × 1)</span></span>
                <div class="flex items-center gap-2 bg-[#DDF1EA] px-2 py-0.5 rounded-lg">
                  <button class="text-xs font-bold text-[#087E6B]">-</button>
                  <span class="text-xs font-bold text-[#172B27]">1</span>
                  <button class="text-xs font-bold text-[#087E6B]">+</button>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2">
            <h4 class="text-xs font-bold text-[#172B27] uppercase tracking-wider">Bill Details</h4>
            <div class="space-y-1.5 text-xs">
              <div class="flex justify-between text-[#71817B]">
                <span>Item Total</span>
                <span class="text-[#172B27] font-medium">₹153.00</span>
              </div>
              <div class="flex justify-between text-[#71817B]">
                <span>Standard Delivery Fee</span>
                <span class="text-[#172B27] font-medium">₹30.00</span>
              </div>
              <div class="flex justify-between text-[#71817B]">
                <span>Packaging & Safety Charge</span>
                <span class="text-[#172B27] font-medium">₹14.00</span>
              </div>
              <div class="flex justify-between text-[#087E6B] font-semibold text-[11px]">
                <span>Total Discount Saved</span>
                <span>-₹18.00</span>
              </div>
              <div class="pt-2 border-t border-[#E5ECE8] flex justify-between items-center text-sm font-extrabold text-[#172B27]">
                <span>Total Amount</span>
                <span>₹197.00</span>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] p-3 flex items-center justify-between">
          <div>
            <span class="text-[10px] text-[#71817B] uppercase font-bold block">Payable Total</span>
            <span class="text-base font-extrabold text-[#172B27]">₹197.00</span>
          </div>
          <button class="h-11 px-6 bg-[#087E6B] text-white font-bold text-xs rounded-xl shadow-md shadow-[#087E6B]/20 flex items-center gap-2">
            <span>Proceed to Checkout</span>
            <span>→</span>
          </button>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "07_address",
        "title": "Delivery Address",
        "file": "screen_07_address.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center gap-3">
          <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
          <span class="text-sm font-bold text-[#172B27]">Add Delivery Address</span>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-4 space-y-3.5">
          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2.5">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block">Recipient Information</span>
            <div>
              <label class="block text-[11px] font-semibold text-[#172B27] mb-1">Full Name</label>
              <input type="text" value="Sahil Kumar" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
            </div>
            <div>
              <label class="block text-[11px] font-semibold text-[#172B27] mb-1">Mobile (+91)</label>
              <input type="text" value="+91 98765 43210" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2.5">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block">Address Details</span>
            <div>
              <label class="block text-[11px] font-semibold text-[#172B27] mb-1">Flat / House No. / Building</label>
              <input type="text" value="Flat 402, Block B, Shanti Vihar" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
            </div>
            <div>
              <label class="block text-[11px] font-semibold text-[#172B27] mb-1">Street / Locality</label>
              <input type="text" value="12th Main Road, HAL 2nd Stage" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
            </div>
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="block text-[11px] font-semibold text-[#172B27] mb-1">City</label>
                <input type="text" value="Bengaluru" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-[#172B27] mb-1">State</label>
                <input type="text" value="Karnataka" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-xs font-medium text-[#172B27]">
              </div>
            </div>
            <div>
              <label class="block text-[11px] font-semibold text-[#172B27] mb-1">6-Digit PIN Code</label>
              <div class="relative">
                <input type="text" value="560038" class="w-full h-9 px-3 rounded-lg bg-white border-2 border-[#16865B] text-xs font-bold text-[#172B27]">
                <span class="absolute right-2.5 top-2 text-[10px] font-bold text-[#16865B]">✓ Deliverable Area</span>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] shadow-xs">
            <label class="block text-[10px] font-bold text-[#71817B] uppercase tracking-wider mb-2">Save Address As</label>
            <div class="flex gap-2">
              <button class="px-3 py-1.5 rounded-lg bg-[#087E6B] text-white text-xs font-bold shadow-xs">🏠 Home</button>
              <button class="px-3 py-1.5 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-[#172B27] text-xs font-medium">🏢 Work</button>
              <button class="px-3 py-1.5 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-[#172B27] text-xs font-medium">📍 Other</button>
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] p-3">
          <button class="w-full h-11 bg-[#087E6B] text-white font-bold text-xs rounded-xl shadow-md shadow-[#087E6B]/20">
            Save & Deliver Here
          </button>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "08_checkout",
        "title": "Checkout",
        "file": "screen_08_checkout.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center gap-3">
          <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
          <span class="text-sm font-bold text-[#172B27]">Checkout & Review</span>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3">
          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs">
            <div class="flex items-center justify-between mb-1.5">
              <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider">Delivering To</span>
              <span class="text-xs font-bold text-[#087E6B]">Change</span>
            </div>
            <div class="flex items-start gap-2.5">
              <span class="text-base">🏠</span>
              <div>
                <h5 class="text-xs font-bold text-[#172B27]">Sahil Kumar • +91 98765 43210</h5>
                <p class="text-[11px] text-[#71817B] mt-0.5 leading-snug">Flat 402, Block B, Shanti Vihar, 12th Main Road, Indiranagar, Bengaluru - 560038</p>
              </div>
            </div>
          </div>

          <div class="bg-white p-3 rounded-xl border border-[#E5ECE8] flex items-center justify-between text-xs shadow-xs">
            <div class="flex items-center gap-2">
              <span class="text-sm">📦</span>
              <span><strong>2 items</strong> in order</span>
            </div>
            <span class="font-bold text-[#172B27]">₹197.00 Total</span>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider">Payment Method (Simulated)</span>
              <span class="text-[9px] font-bold text-[#087E6B] bg-[#DDF1EA] px-2 py-0.5 rounded-full">Test Sandbox</span>
            </div>

            <div class="p-2.5 rounded-lg bg-[#DDF1EA]/50 border border-[#DDF1EA] text-[10px] text-[#075B4F] leading-tight">
              ℹ️ <strong>Simulated Payment:</strong> No real funds or banking transactions occur. This tests order confirmation reliability.
            </div>

            <div class="p-3 rounded-xl border-2 border-[#087E6B] bg-[#F7F9F7] flex items-center justify-between">
              <div class="flex items-center gap-2.5">
                <input type="radio" checked class="w-4 h-4 accent-[#087E6B]">
                <div>
                  <div class="text-xs font-bold text-[#172B27] flex items-center gap-1.5">
                    <span>⚡ UPI (Simulated)</span>
                    <span class="text-[10px] font-bold bg-[#DDF1EA] text-[#087E6B] px-1.5 rounded">Fastest</span>
                  </div>
                  <div class="text-[10px] text-[#71817B]">Google Pay, PhonePe, Paytm, or UPI ID</div>
                </div>
              </div>
              <span class="text-base">📲</span>
            </div>

            <div class="p-3 rounded-xl border border-[#E5ECE8] flex items-center justify-between">
              <div class="flex items-center gap-2.5">
                <input type="radio" class="w-4 h-4 accent-[#087E6B]">
                <div>
                  <div class="text-xs font-bold text-[#172B27]">💵 Cash on Delivery</div>
                  <div class="text-[10px] text-[#71817B]">Pay at delivery via Cash or QR</div>
                </div>
              </div>
              <span class="text-base">🤝</span>
            </div>

            <div class="p-3 rounded-xl border border-[#E5ECE8] flex items-center justify-between">
              <div class="flex items-center gap-2.5">
                <input type="radio" class="w-4 h-4 accent-[#087E6B]">
                <div>
                  <div class="text-xs font-bold text-[#172B27]">💳 Debit / Credit Card (Demo)</div>
                  <div class="text-[10px] text-[#71817B]">RuPay, Visa, Mastercard sandbox</div>
                </div>
              </div>
              <span class="text-base">💳</span>
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] p-3 flex items-center justify-between">
          <div>
            <span class="text-[10px] text-[#71817B] uppercase font-bold block">Total Payable</span>
            <span class="text-base font-extrabold text-[#172B27]">₹197.00</span>
          </div>
          <button class="h-11 px-6 bg-[#087E6B] text-white font-bold text-xs rounded-xl shadow-md shadow-[#087E6B]/20 flex items-center gap-2">
            <span>Place Order (Demo)</span>
            <span>→</span>
          </button>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "09_confirmation",
        "title": "Order Confirmation",
        "file": "screen_09_confirmation.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="flex-1 p-6 flex flex-col justify-between text-center">
          <div></div>

          <div class="flex flex-col items-center">
            <div class="relative mb-5">
              <div class="w-20 h-20 rounded-full bg-[#DDF1EA] flex items-center justify-center">
                <div class="w-14 h-14 rounded-full bg-[#16865B] flex items-center justify-center text-white shadow-lg shadow-[#16865B]/30 text-2xl font-bold">
                  ✓
                </div>
              </div>
            </div>

            <span class="px-3 py-1 rounded-full bg-[#DDF1EA] text-[#087E6B] text-[11px] font-bold uppercase tracking-wider mb-2">Backend Confirmed</span>
            <h2 class="text-2xl font-extrabold text-[#172B27] tracking-tight">Order Placed Successfully!</h2>
            <p class="text-xs text-[#71817B] mt-1.5 max-w-[260px]">
              Your medicine order request has been received and confirmed by the local pharmacy dispensary.
            </p>

            <div class="w-full bg-white p-4 rounded-2xl border border-[#E5ECE8] shadow-xs mt-6 text-left space-y-2.5">
              <div class="flex justify-between items-center pb-2 border-b border-[#E5ECE8]">
                <span class="text-[10px] text-[#71817B] uppercase font-bold">Order ID</span>
                <span class="text-xs font-mono font-bold text-[#087E6B]">#MC-2026-8942</span>
              </div>
              <div class="flex justify-between text-xs">
                <span class="text-[#71817B]">Order Date</span>
                <span class="font-semibold text-[#172B27]">09 Nov 2026, 10:24 PM</span>
              </div>
              <div class="flex justify-between text-xs">
                <span class="text-[#71817B]">Estimated Delivery</span>
                <span class="font-bold text-[#16865B]">Tomorrow by 11:00 AM</span>
              </div>
              <div class="flex justify-between text-xs">
                <span class="text-[#71817B]">Paid Amount</span>
                <span class="font-extrabold text-[#172B27]">₹197.00 (UPI Demo)</span>
              </div>
              <div class="flex justify-between text-xs pt-2 border-t border-[#E5ECE8] items-center">
                <span class="text-[#71817B]">Current Status</span>
                <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#DDF1EA] text-[#087E6B]">● Pharmacist Verification</span>
              </div>
            </div>
          </div>

          <div class="space-y-2.5 pt-4">
            <button class="w-full h-12 bg-[#087E6B] text-white font-bold text-xs rounded-xl shadow-md shadow-[#087E6B]/20">
              View Order Details & Status
            </button>
            <button class="w-full h-11 bg-white border border-[#E5ECE8] text-[#172B27] font-bold text-xs rounded-xl shadow-xs">
              Continue Shopping
            </button>
          </div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "10_history",
        "title": "Order History",
        "file": "screen_10_history.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center justify-between">
          <span class="text-base font-bold text-[#172B27]">Order History</span>
          <span class="text-xs font-semibold text-[#71817B]">4 Total</span>
        </div>

        <div class="bg-white px-4 py-2 border-b border-[#E5ECE8] flex gap-2">
          <span class="px-3 py-1 rounded-full bg-[#087E6B] text-white font-bold text-[11px]">All</span>
          <span class="px-3 py-1 rounded-full bg-[#F7F9F7] text-[#71817B] font-medium text-[11px]">Active (1)</span>
          <span class="px-3 py-1 rounded-full bg-[#F7F9F7] text-[#71817B] font-medium text-[11px]">Completed (2)</span>
          <span class="px-3 py-1 rounded-full bg-[#F7F9F7] text-[#71817B] font-medium text-[11px]">Cancelled (1)</span>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3">
          <div class="bg-white p-3.5 rounded-xl border-2 border-[#087E6B] shadow-xs space-y-2.5">
            <div class="flex justify-between items-center">
              <span class="font-mono text-xs font-bold text-[#087E6B]">#MC-2026-8942</span>
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#DDF1EA] text-[#087E6B]">● In Processing</span>
            </div>
            <div class="text-[11px] text-[#71817B]">Placed on 09 Nov 2026, 10:24 PM</div>
            <div class="flex items-center justify-between pt-2 border-t border-[#E5ECE8]">
              <div>
                <span class="text-xs font-bold text-[#172B27]">₹197.00</span>
                <span class="text-[10px] text-[#71817B] block">2 Products (Paracare, AyurTulsi)</span>
              </div>
              <button class="px-3 py-1 bg-[#087E6B] text-white text-[11px] font-bold rounded-lg shadow-xs">Track →</button>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2.5">
            <div class="flex justify-between items-center">
              <span class="font-mono text-xs font-bold text-[#172B27]">#MC-2026-7810</span>
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-[#16865B]">✓ Delivered</span>
            </div>
            <div class="text-[11px] text-[#71817B]">Delivered on 05 Nov 2026, 04:12 PM</div>
            <div class="flex items-center justify-between pt-2 border-t border-[#E5ECE8]">
              <div>
                <span class="text-xs font-bold text-[#172B27]">₹216.00</span>
                <span class="text-[10px] text-[#71817B] block">3 Products (First Aid, Bandages)</span>
              </div>
              <button class="px-3 py-1 bg-[#F7F9F7] border border-[#E5ECE8] text-[#172B27] text-[11px] font-bold rounded-lg">Reorder</button>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2.5 opacity-80">
            <div class="flex justify-between items-center">
              <span class="font-mono text-xs font-bold text-[#71817B]">#MC-2026-5190</span>
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-red-100 text-[#D64B4B]">✕ Cancelled</span>
            </div>
            <div class="text-[11px] text-[#71817B]">Cancelled on 28 Oct 2026</div>
            <div class="flex items-center justify-between pt-2 border-t border-[#E5ECE8]">
              <div>
                <span class="text-xs font-bold text-[#172B27]">₹72.00</span>
                <span class="text-[10px] text-[#71817B] block">Refund processed to UPI</span>
              </div>
              <button class="text-xs font-semibold text-[#71817B]">Details</button>
            </div>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] px-4 py-2 flex justify-around items-center">
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🏠</span><span class="text-[10px] font-medium">Home</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📑</span><span class="text-[10px] font-medium">Catalogue</span></div>
          <div class="flex flex-col items-center text-[#087E6B]"><span class="text-sm">📦</span><span class="text-[10px] font-bold">Orders</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🛍️</span><span class="text-[10px] font-medium">Cart</span></div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "11_order_details",
        "title": "Order Details",
        "file": "screen_11_order_details.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center justify-between">
          <div class="flex items-center gap-3">
            <button class="w-8 h-8 rounded-lg bg-[#F7F9F7] flex items-center justify-center text-[#172B27]">←</button>
            <div>
              <span class="text-xs font-bold text-[#172B27] block">Order #MC-2026-8942</span>
              <span class="text-[10px] text-[#71817B]">Placed Today, 10:24 PM</span>
            </div>
          </div>
          <button class="text-xs font-bold text-[#087E6B]">Invoice 📄</button>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3">
          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-3">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block">Fulfillment Status</span>
            <div class="space-y-3 text-xs">
              <div class="flex items-start gap-3">
                <div class="w-6 h-6 rounded-full bg-[#087E6B] text-white flex items-center justify-center text-xs font-bold shrink-0">✓</div>
                <div>
                  <h5 class="font-bold text-[#172B27]">Order Confirmed</h5>
                  <p class="text-[10px] text-[#71817B]">Validated by backend API (10:24 PM)</p>
                </div>
              </div>
              <div class="ml-3 w-0.5 h-3 bg-[#087E6B]"></div>
              <div class="flex items-start gap-3">
                <div class="w-6 h-6 rounded-full bg-[#DDF1EA] border-2 border-[#087E6B] text-[#087E6B] flex items-center justify-center text-xs font-bold shrink-0">●</div>
                <div>
                  <h5 class="font-bold text-[#087E6B]">Dispensing & Packing</h5>
                  <p class="text-[10px] text-[#71817B]">Pharmacist checking batch & expiry</p>
                </div>
              </div>
              <div class="ml-3 w-0.5 h-3 bg-[#E5ECE8]"></div>
              <div class="flex items-start gap-3 opacity-50">
                <div class="w-6 h-6 rounded-full bg-[#F7F9F7] border border-[#E5ECE8] text-[#71817B] flex items-center justify-center text-xs font-bold shrink-0">3</div>
                <div>
                  <h5 class="font-bold text-[#172B27]">Out for Delivery</h5>
                  <p class="text-[10px] text-[#71817B]">Assigned to delivery agent</p>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs space-y-2">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block">Items in this Package (2)</span>
            <div class="flex justify-between items-center py-1.5 border-b border-[#F7F9F7] text-xs">
              <div>
                <span class="font-bold text-[#172B27]">Paracare 650mg Tablets</span>
                <span class="text-[10px] text-[#71817B] block">Strip of 15 • Qty: 2</span>
              </div>
              <span class="font-bold text-[#172B27]">₹68.00</span>
            </div>
            <div class="flex justify-between items-center py-1.5 text-xs">
              <div>
                <span class="font-bold text-[#172B27]">AyurTulsi Cough Relief Syrup</span>
                <span class="text-[10px] text-[#71817B] block">100 ml • Qty: 1</span>
              </div>
              <span class="font-bold text-[#172B27]">₹85.00</span>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block mb-1">Delivered To</span>
            <p class="text-xs font-semibold text-[#172B27]">Sahil Kumar • +91 98765 43210</p>
            <p class="text-[11px] text-[#71817B] mt-0.5">Flat 402, Shanti Vihar, Indiranagar, Bengaluru - 560038</p>
          </div>

          <button class="w-full py-2.5 bg-white border border-[#E5ECE8] text-[#172B27] font-bold text-xs rounded-xl shadow-xs flex items-center justify-center gap-1.5">
            <span>💬 Need Help with this Order?</span>
          </button>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "12_profile",
        "title": "Profile & Settings",
        "file": "screen_12_profile.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>

        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] flex items-center justify-between">
          <span class="text-base font-bold text-[#172B27]">My Account</span>
          <span class="text-xs font-bold text-[#087E6B]">Edit</span>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-4 space-y-4">
          <div class="bg-white p-4 rounded-2xl border border-[#E5ECE8] flex items-center gap-3.5 shadow-xs">
            <div class="w-14 h-14 rounded-full bg-[#087E6B] text-white flex items-center justify-center font-extrabold text-lg shadow-md shadow-[#087E6B]/20">
              SK
            </div>
            <div>
              <h3 class="text-sm font-bold text-[#172B27]">Sahil Kumar</h3>
              <p class="text-xs font-medium text-[#71817B]">+91 98765 43210</p>
              <span class="inline-block mt-1 px-2 py-0.5 rounded text-[10px] font-bold bg-[#DDF1EA] text-[#087E6B]">Verified Account</span>
            </div>
          </div>

          <div class="bg-white p-3.5 rounded-xl border border-[#E5ECE8] shadow-xs">
            <span class="text-[10px] font-bold text-[#71817B] uppercase tracking-wider block mb-2">Preferred Language</span>
            <div class="grid grid-cols-2 gap-2">
              <button class="py-2 px-3 rounded-lg bg-[#087E6B] text-white text-xs font-bold shadow-xs flex items-center justify-center gap-1.5">
                <span>✓</span> English
              </button>
              <button class="py-2 px-3 rounded-lg bg-[#F7F9F7] border border-[#E5ECE8] text-[#172B27] text-xs font-medium hover:bg-white flex items-center justify-center">
                हिन्दी (Hindi)
              </button>
            </div>
          </div>

          <div class="bg-white rounded-xl border border-[#E5ECE8] divide-y divide-[#E5ECE8] shadow-xs">
            <div class="p-3 flex items-center justify-between text-xs cursor-pointer">
              <div class="flex items-center gap-2.5">
                <span class="text-base">📍</span>
                <span class="font-semibold text-[#172B27]">Saved Delivery Addresses</span>
              </div>
              <span class="text-[#71817B] font-mono">2 saved →</span>
            </div>
            <div class="p-3 flex items-center justify-between text-xs cursor-pointer">
              <div class="flex items-center gap-2.5">
                <span class="text-base">📦</span>
                <span class="font-semibold text-[#172B27]">All Orders & Invoices</span>
              </div>
              <span class="text-[#71817B] font-mono">4 orders →</span>
            </div>
            <div class="p-3 flex items-center justify-between text-xs cursor-pointer">
              <div class="flex items-center gap-2.5">
                <span class="text-base">🔔</span>
                <span class="font-semibold text-[#172B27]">Order Notifications & SMS</span>
              </div>
              <span class="text-[#16865B] font-bold text-[11px]">Enabled</span>
            </div>
            <div class="p-3 flex items-center justify-between text-xs cursor-pointer">
              <div class="flex items-center gap-2.5">
                <span class="text-base">🛡️</span>
                <span class="font-semibold text-[#172B27]">Terms & E-Pharmacy Compliance</span>
              </div>
              <span class="text-[#71817B]">→</span>
            </div>
          </div>

          <button class="w-full py-3 rounded-xl bg-white border border-[#D64B4B]/30 text-[#D64B4B] font-bold text-xs hover:bg-red-50 transition-colors shadow-xs">
            Log Out of Account
          </button>

          <div class="text-center">
            <span class="text-[10px] text-[#71817B]">MediCart Android App • Version 2.4.0 (Build 2026.11)</span>
          </div>
        </div>

        <div class="bg-white border-t border-[#E5ECE8] px-4 py-2 flex justify-around items-center">
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🏠</span><span class="text-[10px] font-medium">Home</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📑</span><span class="text-[10px] font-medium">Catalogue</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">📦</span><span class="text-[10px] font-medium">Orders</span></div>
          <div class="flex flex-col items-center text-[#71817B]"><span class="text-sm">🛍️</span><span class="text-[10px] font-medium">Cart</span></div>
        </div>

        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    }
]

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=393, height=852, initial-scale=1.0">
  <title>MediCart - {title}</title>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    * {{
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }}
    body {{
      width: 393px;
      height: 852px;
      overflow: hidden;
      background-color: #F7F9F7;
      color: #172B27;
      font-family: 'Plus Jakarta Sans', system-ui, sans-serif;
      margin: 0;
      padding: 0;
    }}
    .mobile-screen {{
      width: 393px;
      height: 852px;
      display: flex;
      flex-direction: column;
      position: relative;
      background-color: #F7F9F7;
    }}
  </style>
</head>
<body>
  <div class="mobile-screen">
    {body}
  </div>
</body>
</html>
"""

def generate():
    screens_dir = r"c:\Users\sahil\OneDrive\Desktop\assigmrnt\ALGIONS AI LABS PRIVATE LIMITED\MediCart\ui-artboards\screens"
    artboards_dir = r"c:\Users\sahil\OneDrive\Desktop\assigmrnt\ALGIONS AI LABS PRIVATE LIMITED\MediCart\artboards"
    artifact_dir = r"C:\Users\sahil\.gemini\antigravity\brain\cd4604bd-4ae4-4f84-bac2-13e92219dc38"

    os.makedirs(screens_dir, exist_ok=True)
    os.makedirs(artboards_dir, exist_ok=True)

    chrome_path = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

    for screen in SCREEN_DEFS:
        file_path = os.path.join(screens_dir, screen["file"])
        html_content = HTML_TEMPLATE.format(title=screen["title"], body=screen["body"])
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(html_content)
        print(f"Generated HTML: {screen['file']}")

        # Render PNG screenshot via headless Chrome
        png_name = f"screen_{screen['id']}.png"
        out_png = os.path.join(artboards_dir, png_name)
        
        cmd = [
            chrome_path,
            "--headless=new",
            "--no-sandbox",
            "--disable-gpu",
            "--force-device-scale-factor=1",
            "--window-size=393,852",
            f"--screenshot={out_png}",
            f"file:///{file_path.replace(os.sep, '/')}"
        ]
        try:
            subprocess.run(cmd, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            if os.path.exists(out_png):
                print(f"Captured screenshot: {png_name} ({os.path.getsize(out_png)} bytes)")
                # Copy to artifact directory for presentation
                shutil.copy(out_png, os.path.join(artifact_dir, png_name))
        except Exception as e:
            print(f"Error capturing {png_name}: {e}")

if __name__ == "__main__":
    generate()
