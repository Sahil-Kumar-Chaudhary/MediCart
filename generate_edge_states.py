import os
import subprocess
import shutil

EDGE_STATES = [
    {
        "id": "state_empty_cart",
        "title": "State - Empty Cart",
        "file": "state_empty_cart.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>
        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] font-bold text-sm text-[#172B27]">Shopping Cart</div>
        <div class="flex-1 p-6 flex flex-col items-center justify-center text-center">
          <div class="w-20 h-20 rounded-full bg-[#DDF1EA] flex items-center justify-center text-3xl mb-4">🛒</div>
          <h3 class="text-base font-bold text-[#172B27]">Your Cart is Empty</h3>
          <p class="text-xs text-[#71817B] mt-1.5 max-w-[220px]">You have no medicines or health items added to your basket yet.</p>
          <button class="mt-6 px-6 py-2.5 rounded-xl bg-[#087E6B] text-white font-bold text-xs shadow-md shadow-[#087E6B]/20">
            Explore Medicines
          </button>
        </div>
        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "state_empty_search",
        "title": "State - Empty Search Results",
        "file": "state_empty_search.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>
        <div class="bg-white px-4 py-2 border-b border-[#E5ECE8]">
          <input type="text" value="Azithroxyz 999mg" class="w-full h-9 px-3 rounded-lg bg-[#F7F9F7] text-xs font-semibold text-[#172B27]">
        </div>
        <div class="flex-1 p-6 flex flex-col items-center justify-center text-center">
          <div class="w-20 h-20 rounded-full bg-[#F7F9F7] border border-[#E5ECE8] flex items-center justify-center text-3xl mb-4">🔍</div>
          <h3 class="text-base font-bold text-[#172B27]">No Medicines Found</h3>
          <p class="text-xs text-[#71817B] mt-1.5 max-w-[240px]">We couldn't find any products matching "Azithroxyz 999mg". Check your spelling or browse categories.</p>
          <button class="mt-6 px-5 py-2.5 rounded-xl bg-[#087E6B] text-white font-bold text-xs">
            Browse All Categories
          </button>
        </div>
        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "state_out_of_stock",
        "title": "State - Out of Stock",
        "file": "state_out_of_stock.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>
        <div class="bg-white px-4 py-3 border-b border-[#E5ECE8] font-bold text-sm text-[#172B27]">Product Details</div>
        <div class="flex-1 p-4 space-y-4">
          <div class="w-full h-44 rounded-2xl bg-white border border-[#E5ECE8] flex items-center justify-center text-5xl relative opacity-60">
            😷
            <span class="absolute top-3 left-3 px-2 py-0.5 rounded text-[10px] font-bold bg-[#E49A32] text-white">Out of Stock</span>
          </div>
          <div class="bg-white p-4 rounded-xl border border-[#E5ECE8] space-y-2">
            <span class="text-xs font-bold text-[#E49A32]">⚠️ Temporarily Unavailable</span>
            <h3 class="text-sm font-bold text-[#172B27]">N95 Healthcare Respirator Mask</h3>
            <p class="text-xs text-[#71817B]">This product is currently out of stock at your local dispensary hub.</p>
          </div>
          <div class="bg-[#F7F9F7] p-3 rounded-xl border border-[#E5ECE8] text-xs text-[#71817B]">
            We expect restock within 2 business days.
          </div>
        </div>
        <div class="bg-white p-3 border-t border-[#E5ECE8]">
          <button class="w-full h-11 bg-slate-300 text-slate-500 font-bold text-xs rounded-xl cursor-not-allowed">
            Out of Stock • Notify Me
          </button>
        </div>
        <div class="w-18 h-1 bg-[#172B27]/30 rounded-full mx-auto mb-2"></div>
        """
    },
    {
        "id": "state_network_offline",
        "title": "State - Network Offline",
        "file": "state_network_offline.html",
        "body": """
        <div class="h-11 px-6 pt-3 flex justify-between items-center text-xs text-[#172B27] font-semibold">
          <span>9:41</span><div class="w-3 h-3 bg-[#172B27] rounded-full"></div><span>🔋 98%</span>
        </div>
        <div class="bg-[#D64B4B] text-white text-[11px] py-1.5 px-3 text-center font-bold">
          📡 No Internet Connection Detected
        </div>
        <div class="flex-1 p-6 flex flex-col items-center justify-center text-center">
          <div class="w-20 h-20 rounded-full bg-red-50 text-[#D64B4B] flex items-center justify-center text-3xl mb-4">📶</div>
          <h3 class="text-base font-bold text-[#172B27]">You're Offline</h3>
          <p class="text-xs text-[#71817B] mt-1.5 max-w-[240px]">MediCart needs an active internet connection to verify medicine stock and process orders.</p>
          <button class="mt-6 px-6 py-2.5 rounded-xl bg-[#087E6B] text-white font-bold text-xs shadow-md shadow-[#087E6B]/20">
            🔄 Check Connection & Retry
          </button>
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
  <title>{title}</title>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{ width: 393px; height: 852px; overflow: hidden; background-color: #F7F9F7; color: #172B27; font-family: 'Plus Jakarta Sans', system-ui, sans-serif; }}
    .mobile-screen {{ width: 393px; height: 852px; display: flex; flex-direction: column; position: relative; background-color: #F7F9F7; }}
  </style>
</head>
<body>
  <div class="mobile-screen">{body}</div>
</body>
</html>
"""

def generate():
    screens_dir = r"c:\Users\sahil\OneDrive\Desktop\assigmrnt\ALGIONS AI LABS PRIVATE LIMITED\MediCart\ui-artboards\screens"
    artboards_dir = r"c:\Users\sahil\OneDrive\Desktop\assigmrnt\ALGIONS AI LABS PRIVATE LIMITED\MediCart\artboards"
    artifact_dir = r"C:\Users\sahil\.gemini\antigravity\brain\cd4604bd-4ae4-4f84-bac2-13e92219dc38"
    chrome_path = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

    for state in EDGE_STATES:
        file_path = os.path.join(screens_dir, state["file"])
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(HTML_TEMPLATE.format(title=state["title"], body=state["body"]))

        png_name = f"{state['id']}.png"
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
                print(f"Captured: {png_name}")
                shutil.copy(out_png, os.path.join(artifact_dir, png_name))
        except Exception as e:
            print(f"Error {png_name}: {e}")

if __name__ == "__main__":
    generate()
