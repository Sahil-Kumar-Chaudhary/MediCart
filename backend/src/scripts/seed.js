const mongoose = require('mongoose');
const { connectDB, disconnectDB } = require('../config/db');
const Product = require('../models/Product');
const User = require('../models/User');
const Address = require('../models/Address');

const PRODUCTS_DATA = [
  {
    productId: 'med_01',
    name: 'Paracare 650mg Tablets',
    genericName: 'Paracetamol IP 650mg',
    description: 'Fast acting generic paracetamol formulation for pain relief and fever reduction.',
    category: 'Pain Relief',
    pricePaise: 3400, // ₹34.00
    mrpPaise: 3800,   // ₹38.00
    packSize: 'Strip of 15 Tablets',
    stockCount: 45,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Dec 2028',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400'
  },
  {
    productId: 'med_02',
    name: 'Cetriz-L 10mg Tablets',
    genericName: 'Levocetirizine Dihydrochloride 10mg',
    description: 'Non-drowsy antihistamine for allergic rhinitis, watery eyes and seasonal allergies.',
    category: 'Cold & Cough',
    pricePaise: 2200, // ₹22.00
    mrpPaise: 2800,   // ₹28.00
    packSize: 'Strip of 10 Tablets',
    stockCount: 20,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Nov 2028',
    imageUrl: 'https://images.unsplash.com/photo-1585435557343-3b092031a831?w=400'
  },
  {
    productId: 'med_03',
    name: 'AyurTulsi Cough Relief Herbal Syrup',
    genericName: 'Tulsi, Vasaka, Mulethi & Honey Herbal Blend',
    description: 'Soothes throat irritation and productive cough using time-tested botanical extracts.',
    category: 'Cold & Cough',
    pricePaise: 8500, // ₹85.00
    mrpPaise: 9900,   // ₹99.00
    packSize: 'Bottle of 100 ml',
    stockCount: 18,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Mar 2028',
    imageUrl: 'https://images.unsplash.com/photo-1514733670139-4d87a1941d55?w=400'
  },
  {
    productId: 'med_04',
    name: 'Panto-D SR Gastro Capsules',
    genericName: 'Pantoprazole 40mg + Domperidone 30mg',
    description: 'Sustained-release generic combination for relief from acid reflux and heartburn.',
    category: 'Digestive Care',
    pricePaise: 9500, // ₹95.00
    mrpPaise: 11800,  // ₹118.00
    packSize: 'Strip of 10 Capsules',
    stockCount: 30,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Jul 2028',
    imageUrl: 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'
  },
  {
    productId: 'med_05',
    name: 'OmniTherm Fast Digital Thermometer',
    genericName: 'Clinical Fast Digital Thermometer',
    description: 'Medical-grade oral and axillary digital temperature sensor with beep indicator.',
    category: 'Healthcare Essentials',
    pricePaise: 19900, // ₹199.00
    mrpPaise: 26000,   // ₹260.00
    packSize: '1 Unit with Protective Case',
    stockCount: 8,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'N/A (Device)',
    imageUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400'
  },
  {
    productId: 'med_06',
    name: 'VitaZinc Immunity Chewables',
    genericName: 'Vitamin C 500mg + Zinc 10mg',
    description: 'Antioxidant and micronutrient support for daily physiological immune function.',
    category: 'Vitamins & Supplements',
    pricePaise: 24000, // ₹240.00
    mrpPaise: 29000,   // ₹290.00
    packSize: 'Bottle of 60 Chewables',
    stockCount: 12,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Oct 2028',
    imageUrl: 'https://images.unsplash.com/photo-1550572017-edd951b55104?w=400'
  },
  {
    productId: 'med_07',
    name: 'N95 Healthcare Respirator Mask',
    genericName: '5-Layer Particulate Filter Mask',
    description: 'High efficiency filtration mask for respiratory barrier protection. Currently out of stock.',
    category: 'First Aid',
    pricePaise: 15000, // ₹150.00
    mrpPaise: 20000,   // ₹200.00
    packSize: 'Pack of 5 Masks',
    stockCount: 0,     // OUT OF STOCK DEMO
    isAvailable: false,
    isExpired: false,
    expiryDate: 'Dec 2029',
    imageUrl: 'https://images.unsplash.com/photo-1584634731339-252c581abfc5?w=400'
  },
  {
    productId: 'med_08',
    name: 'ColdRelief Night Syrup (Expired Batch)',
    genericName: 'Fictional Night Decongestant',
    description: 'Demonstration item representing a batch withheld past official expiration date.',
    category: 'Cold & Cough',
    pricePaise: 4500, // ₹45.00
    mrpPaise: 5500,   // ₹55.00
    packSize: 'Bottle of 100 ml',
    stockCount: 5,
    isAvailable: false,
    isExpired: true,  // EXPIRED DEMO
    expiryDate: 'Aug 2026 (Expired)',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400'
  },
  {
    productId: 'med_09',
    name: 'DermaHeal Antiseptic Cream',
    genericName: 'Chlorhexidine Gluconate 1% + Cetrimide 3%',
    description: 'Topical soothing first-aid antiseptic cream for superficial cuts, grazes and burns.',
    category: 'First Aid',
    pricePaise: 6500, // ₹65.00
    mrpPaise: 8000,   // ₹80.00
    packSize: 'Tube of 30g',
    stockCount: 25,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Jan 2029',
    imageUrl: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400'
  },
  {
    productId: 'med_10',
    name: 'CalciBone Plus Tablets',
    genericName: 'Calcium Citrate 1000mg + Vitamin D3 400 IU',
    description: 'Bioavailable elemental calcium supplement supporting bone density and mineral balance.',
    category: 'Vitamins & Supplements',
    pricePaise: 18000, // ₹180.00
    mrpPaise: 22000,   // ₹220.00
    packSize: 'Bottle of 30 Tablets',
    stockCount: 22,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'May 2028',
    imageUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400'
  },
  {
    productId: 'med_11',
    name: 'BioFlora Daily Probiotic Sachets',
    genericName: 'Lactobacillus acidophilus 5 Billion CFU',
    description: 'Daily probiotic dietary supplement for maintaining healthy gastrointestinal gut flora.',
    category: 'Digestive Care',
    pricePaise: 12000, // ₹120.00
    mrpPaise: 15000,   // ₹150.00
    packSize: 'Box of 10 Sachets',
    stockCount: 15,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Apr 2028',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400'
  },
  {
    productId: 'med_12',
    name: 'Ibugel Fast Pain Relief Gel',
    genericName: 'Diclofenac Diethylamine Gel 1.16%',
    description: 'Topical analgesic anti-inflammatory gel for localized joint stiffness and back pain.',
    category: 'Pain Relief',
    pricePaise: 11000, // ₹110.00
    mrpPaise: 13500,   // ₹135.00
    packSize: 'Tube of 50g',
    stockCount: 16,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Sep 2028',
    imageUrl: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400'
  },
  {
    productId: 'med_13',
    name: 'AloeSoothe Deep Moisturizing Lotion',
    genericName: 'Pure Aloe Vera & Vitamin E Lotion',
    description: 'Hypoallergenic fragrance-free barrier hydration for dry, sensitive winter skin.',
    category: 'Personal Care',
    pricePaise: 16000, // ₹160.00
    mrpPaise: 19500,   // ₹195.00
    packSize: 'Pump Bottle of 200 ml',
    stockCount: 20,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Jun 2028',
    imageUrl: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400'
  },
  {
    productId: 'med_14',
    name: 'PureShield Antibacterial Hand Sanitizer',
    genericName: '70% Isopropyl Alcohol with Glycerin',
    description: 'Hospital grade alcohol-based hand rub with skin moisturizers for instant sanitization.',
    category: 'Personal Care',
    pricePaise: 5000, // ₹50.00
    mrpPaise: 6500,   // ₹65.00
    packSize: 'Flip-top Bottle of 100 ml',
    stockCount: 40,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Feb 2029',
    imageUrl: 'https://images.unsplash.com/photo-1584634731339-252c581abfc5?w=400'
  },
  {
    productId: 'med_15',
    name: 'SteriGauze Cotton Bandage Roll',
    genericName: '100% Medical Bleached Cotton Bandage',
    description: 'High tensile absorbent bandage roll for dressing wounds and securing primary pads.',
    category: 'First Aid',
    pricePaise: 4000, // ₹40.00
    mrpPaise: 5000,   // ₹50.00
    packSize: 'Pack of 2 Rolls (7.5cm x 3m)',
    stockCount: 35,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Jan 2030',
    imageUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400'
  },
  {
    productId: 'med_16',
    name: 'PulseCheck Fingertip Pulse Oximeter',
    genericName: 'Non-Invasive SpO2 and Pulse Monitor',
    description: 'High accuracy OLED screen pulse oximeter for real-time blood oxygen saturation tracking.',
    category: 'Healthcare Essentials',
    pricePaise: 69900, // ₹699.00
    mrpPaise: 95000,   // ₹950.00
    packSize: '1 Device with AAA Batteries and Lanyard',
    stockCount: 6,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'N/A (Device)',
    imageUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400'
  },
  {
    productId: 'med_17',
    name: 'GasoRelief Effervescent Antacid',
    genericName: 'Sodium Bicarbonate + Citric Acid (Lemon Flavor)',
    description: 'Fast-dissolving effervescent antacid powder for rapid relief from gastric acidity.',
    category: 'Digestive Care',
    pricePaise: 3000, // ₹30.00
    mrpPaise: 3600,   // ₹36.00
    packSize: 'Box of 6 Sachets (5g each)',
    stockCount: 50,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Aug 2028',
    imageUrl: 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'
  },
  {
    productId: 'med_18',
    name: 'MedicoPlast Waterproof Bandages',
    genericName: 'Hypoallergenic Breathable Adhesive Strips',
    description: 'Water-resistant barrier strips with non-stick antiseptic wound pad for minor injuries.',
    category: 'First Aid',
    pricePaise: 5500, // ₹55.00
    mrpPaise: 7000,   // ₹70.00
    packSize: 'Pack of 20 Assorted Strips',
    stockCount: 28,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Nov 2029',
    imageUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400'
  },
  {
    productId: 'med_19',
    name: 'Herbovita Traditional Chyawanprash',
    genericName: 'Wild Amla & 40 Rejuvenating Ayurvedic Herbs',
    description: 'Immunity and respiratory health paste prepared according to traditional pharmacopeia.',
    category: 'Vitamins & Supplements',
    pricePaise: 21000, // ₹210.00
    mrpPaise: 25000,   // ₹250.00
    packSize: 'Jar of 500g',
    stockCount: 14,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Dec 2027',
    imageUrl: 'https://images.unsplash.com/photo-1514733670139-4d87a1941d55?w=400'
  },
  {
    productId: 'med_20',
    name: 'GentleWash Sensitive Skin Cleanser',
    genericName: 'Soap-Free pH 5.5 Dermatological Formula',
    description: 'Mild, non-comedogenic liquid wash suitable for face and body without skin barrier stripping.',
    category: 'Personal Care',
    pricePaise: 17500, // ₹175.00
    mrpPaise: 21000,   // ₹210.00
    packSize: 'Bottle of 150 ml',
    stockCount: 18,
    isAvailable: true,
    isExpired: false,
    expiryDate: 'Oct 2028',
    imageUrl: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400'
  }
];

const seedDatabase = async () => {
  try {
    console.log('[Seed] Connecting to MongoDB...');
    await connectDB();

    console.log('[Seed] Seeding Products...');
    await Product.deleteMany({});
    const inserted = await Product.insertMany(PRODUCTS_DATA);
    console.log(`[Seed] Successfully inserted ${inserted.length} realistic fictional products.`);

    console.log('[Seed] Seeding Sample User & Address...');
    let user = await User.findOne({ phoneNumber: '9876543210' });
    if (!user) {
      user = await User.create({
        name: 'Sahil Kumar',
        phoneNumber: '9876543210'
      });
      console.log(`[Seed] Created default user: ${user.name} (${user.phoneNumber})`);
    }

    const addressCount = await Address.countDocuments({ userId: user._id });
    if (addressCount === 0) {
      await Address.create({
        userId: user._id,
        recipientName: 'Sahil Kumar',
        phoneNumber: '9876543210',
        houseNumber: 'Flat 402, Block B, Shanti Vihar',
        locality: '12th Main Road, HAL 2nd Stage',
        city: 'Bengaluru',
        state: 'Karnataka',
        pinCode: '560038',
        addressType: 'HOME',
        isDefault: true
      });
      console.log('[Seed] Created default address for sample user.');
    }

    console.log('[Seed] Seeding finished successfully.');
    await disconnectDB();
    process.exit(0);
  } catch (error) {
    console.error('[Seed Error] Failed to seed database:', error);
    process.exit(1);
  }
};

if (require.main === module) {
  seedDatabase();
}

module.exports = { seedDatabase, PRODUCTS_DATA };
