const Product = require('../models/Product');

const CATEGORIES = [
  { id: 'cat_pain', title: 'Pain Relief', iconEmoji: '💊' },
  { id: 'cat_cough', title: 'Cold & Cough', iconEmoji: '🫁' },
  { id: 'cat_personal', title: 'Personal Care', iconEmoji: '🧴' },
  { id: 'cat_firstaid', title: 'First Aid', iconEmoji: '🩺' },
  { id: 'cat_essentials', title: 'Healthcare Essentials', iconEmoji: '🩹' },
  { id: 'cat_vitamins', title: 'Vitamins & Supplements', iconEmoji: '🛡️' },
  { id: 'cat_digestive', title: 'Digestive Care', iconEmoji: '🌿' }
];

const getCategories = async () => {
  const counts = await Product.aggregate([
    { $group: { _id: '$category', count: { $sum: 1 } } }
  ]);
  const countMap = counts.reduce((acc, curr) => {
    acc[curr._id] = curr.count;
    return acc;
  }, {});

  return CATEGORIES.map(cat => ({
    ...cat,
    productCount: countMap[cat.title] || 0
  }));
};

const getProducts = async (queryParams) => {
  const {
    search,
    category,
    inStock,
    sort = 'newest',
    page = 1,
    limit = 20
  } = queryParams;

  const filter = {};

  if (search && search.trim()) {
    const regex = new RegExp(search.trim(), 'i');
    filter.$or = [{ name: regex }, { genericName: regex }, { description: regex }];
  }

  if (category && category.trim()) {
    filter.category = new RegExp(`^${category.trim()}$`, 'i');
  }

  if (inStock !== undefined && inStock !== '') {
    const isAvailable = inStock === 'true' || inStock === true;
    filter.isAvailable = isAvailable;
    filter.stockCount = isAvailable ? { $gt: 0 } : 0;
  }

  let sortOption = { createdAt: -1 };
  if (sort === 'price_asc') {
    sortOption = { pricePaise: 1 };
  } else if (sort === 'price_desc') {
    sortOption = { pricePaise: -1 };
  } else if (sort === 'name_asc') {
    sortOption = { name: 1 };
  }

  const pageNum = Math.max(1, parseInt(page, 10) || 1);
  const limitNum = Math.min(50, Math.max(1, parseInt(limit, 10) || 20));
  const skip = (pageNum - 1) * limitNum;

  const [products, totalCount] = await Promise.all([
    Product.find(filter).sort(sortOption).skip(skip).limit(limitNum).lean(),
    Product.countDocuments(filter)
  ]);

  return {
    products,
    pagination: {
      totalItems: totalCount,
      totalPages: Math.ceil(totalCount / limitNum),
      currentPage: pageNum,
      limit: limitNum
    }
  };
};

const getProductById = async (id) => {
  let product = await Product.findOne({ productId: id }).lean();
  if (!product && id.match(/^[0-9a-fA-F]{24}$/)) {
    product = await Product.findById(id).lean();
  }
  return product;
};

module.exports = {
  getCategories,
  getProducts,
  getProductById
};
