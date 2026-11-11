const request = require('supertest');
const app = require('../app');
const Product = require('../src/models/Product');
const User = require('../src/models/User');
const Address = require('../src/models/Address');
const Order = require('../src/models/Order');
const { setupTestDB } = require('./setup');
const { PRODUCTS_DATA } = require('../src/scripts/seed');

setupTestDB();

describe('MediCart REST API Acceptance Test Suite (22 Tests)', () => {
  let userToken;
  let userBToken;
  let userId;
  let userBId;
  let userAddressId;
  const userPhone = '9876543210';
  const userBPhone = '9123456789';

  beforeAll(async () => {
    // Seed products in isolated test database
    await Product.deleteMany({});
    await Product.insertMany(PRODUCTS_DATA);
  });

  // 1. Server Health
  test('1. Server health check endpoint returns 200 and healthy status', async () => {
    const res = await request(app).get('/health');
    expect(res.status).toBe(200);
    expect(res.body.status).toBe('healthy');
    expect(res.body.service).toBe('medicart-api');
  });

  // 2. Successful OTP Verification & User Token
  test('2. Successful OTP verification creates user and issues valid JWT', async () => {
    // Step 1: Request OTP
    const reqRes = await request(app)
      .post('/api/auth/request-otp')
      .send({ phoneNumber: userPhone });
    expect(reqRes.status).toBe(200);
    expect(reqRes.body.success).toBe(true);
    expect(reqRes.body.data.simulatedOtp).toBe('487210');

    // Step 2: Verify OTP
    const verifyRes = await request(app)
      .post('/api/auth/verify-otp')
      .send({ phoneNumber: userPhone, otp: '487210' });

    expect(verifyRes.status).toBe(200);
    expect(verifyRes.body.success).toBe(true);
    expect(verifyRes.body.data.token).toBeDefined();
    expect(verifyRes.body.data.user.phoneNumber).toBe(userPhone);

    userToken = verifyRes.body.data.token;
    userId = verifyRes.body.data.user._id;

    // Also register User B for isolation/ownership testing
    await request(app)
      .post('/api/auth/request-otp')
      .send({ phoneNumber: userBPhone });
    const verifyBRes = await request(app)
      .post('/api/auth/verify-otp')
      .send({ phoneNumber: userBPhone, otp: '487210' });
    userBToken = verifyBRes.body.data.token;
    userBId = verifyBRes.body.data.user._id;
  });

  // 3. Invalid OTP Rejection
  test('3. Invalid OTP code is rejected with 400', async () => {
    await request(app)
      .post('/api/auth/request-otp')
      .send({ phoneNumber: '9998887776' });

    const res = await request(app)
      .post('/api/auth/verify-otp')
      .send({ phoneNumber: '9998887776', otp: '000000' });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INVALID_OTP');
  });

  // 4. JWT Authorization
  test('4. Protected route GET /api/auth/me succeeds with valid Bearer token', async () => {
    const res = await request(app)
      .get('/api/auth/me')
      .set('Authorization', `Bearer ${userToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.phoneNumber).toBe(userPhone);
  });

  // 5. Unauthorized Access
  test('5. Protected route rejects missing or invalid authorization header with 401', async () => {
    const noTokenRes = await request(app).get('/api/auth/me');
    expect(noTokenRes.status).toBe(401);
    expect(noTokenRes.body.errorCode).toBe('UNAUTHORIZED');

    const badTokenRes = await request(app)
      .get('/api/auth/me')
      .set('Authorization', 'Bearer invalid_signature_token');
    expect(badTokenRes.status).toBe(401);
    expect(badTokenRes.body.errorCode).toBe('INVALID_TOKEN');
  });

  // 6. Product Search
  test('6. Product search by name or generic name returns matching items', async () => {
    const res = await request(app)
      .get('/api/products')
      .query({ search: 'Paracetamol' });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.products.length).toBeGreaterThan(0);
    expect(res.body.data.products[0].genericName).toContain('Paracetamol');
  });

  // 7. Category Filtering
  test('7. Filter products by category returns correct categorized subset', async () => {
    const res = await request(app)
      .get('/api/products')
      .query({ category: 'Pain Relief' });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    res.body.data.products.forEach(p => {
      expect(p.category.toLowerCase()).toBe('pain relief');
    });
  });

  // 8. Product Details
  test('8. GET /api/products/:id returns complete details for valid product', async () => {
    const res = await request(app).get('/api/products/med_01');
    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.productId).toBe('med_01');
    expect(res.body.data.pricePaise).toBe(3400); // ₹34
    expect(res.body.data.isAvailable).toBe(true);
  });

  // 9. Address Creation
  test('9. Authenticated user can create a valid delivery address', async () => {
    const res = await request(app)
      .post('/api/addresses')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        recipientName: 'Sahil Kumar',
        phoneNumber: userPhone,
        houseNumber: 'Flat 402, Block B, Shanti Vihar',
        locality: '12th Main Road, HAL 2nd Stage',
        city: 'Bengaluru',
        state: 'Karnataka',
        pinCode: '560038',
        addressType: 'HOME',
        isDefault: true
      });

    expect(res.status).toBe(201);
    expect(res.body.success).toBe(true);
    expect(res.body.data.city).toBe('Bengaluru');
    expect(res.body.data.pinCode).toBe('560038');

    userAddressId = res.body.data._id;
  });

  // 10. Address Ownership Protection
  test('10. User B cannot update or delete User A\'s address (403 Forbidden)', async () => {
    const updateRes = await request(app)
      .put(`/api/addresses/${userAddressId}`)
      .set('Authorization', `Bearer ${userBToken}`)
      .send({ city: 'Hacked City' });

    expect(updateRes.status).toBe(403);
    expect(updateRes.body.errorCode).toBe('FORBIDDEN');

    const deleteRes = await request(app)
      .delete(`/api/addresses/${userAddressId}`)
      .set('Authorization', `Bearer ${userBToken}`);

    expect(deleteRes.status).toBe(403);
    expect(deleteRes.body.errorCode).toBe('FORBIDDEN');
  });

  // 11. Successful Order Creation
  test('11. Successful order creation calculates totals, stores snapshots and returns reference', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [
          { productId: 'med_01', quantity: 2 }, // 3400 * 2 = 6800
          { productId: 'med_02', quantity: 1 }  // 2200 * 1 = 2200
        ],
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(201);
    expect(res.body.success).toBe(true);
    expect(res.body.data.orderId).toMatch(/^#MC-2026-\d{4}/);
    expect(res.body.data.subtotalPaise).toBe(9000); // 6800 + 2200 = 9000 (₹90.00)
    expect(res.body.data.deliveryFeePaise).toBe(3000); // ₹30.00
    expect(res.body.data.packagingFeePaise).toBe(1400); // ₹14.00
    expect(res.body.data.totalAmountPaise).toBe(13400); // ₹134.00
    expect(res.body.data.paymentMethod).toBe('UPI_DEMO');
    expect(res.body.data.paymentStatus).toBe('CONFIRMED_SIMULATED');
  });

  // 12. Empty Cart Rejection
  test('12. Order creation with empty cart is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [],
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
  });

  // 13. Out-of-Stock Product Rejection
  test('13. Order creation containing out-of-stock item is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_07', quantity: 1 }], // med_07 stockCount: 0
        paymentMethod: 'CASH_ON_DELIVERY'
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INSUFFICIENT_STOCK');
  });

  // 14. Expired Product Rejection
  test('14. Order creation containing expired medicine batch is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_08', quantity: 1 }], // med_08 isExpired: true
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('EXPIRED_PRODUCT');
  });

  // 15. Invalid Quantity Rejection
  test('15. Order creation with quantity exceeding 5 packs is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_01', quantity: 6 }], // Max limit is 5
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
  });

  // 16. Server-side Price Calculation
  test('16. Backend enforces its own catalogue prices and ignores tampered client amounts', async () => {
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_03', quantity: 1 }], // Official price 8500 paise
        tamperedClientPrice: 100, // Should be ignored completely
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(201);
    expect(res.body.data.items[0].unitPricePaise).toBe(8500);
    expect(res.body.data.subtotalPaise).toBe(8500);
    expect(res.body.data.totalAmountPaise).toBe(8500 + 3000 + 1400);
  });

  // 17. Duplicate Order Prevention / Idempotency
  test('17. Repeating order request with same idempotencyKey returns existing order without creating duplicate', async () => {
    const key = `idemp_${Date.now()}_test_key`;

    const res1 = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_04', quantity: 1 }],
        paymentMethod: 'UPI_DEMO',
        idempotencyKey: key
      });
    expect(res1.status).toBe(201);
    const firstOrderId = res1.body.data.orderId;

    // Retry tap with identical idempotencyKey
    const res2 = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_04', quantity: 1 }],
        paymentMethod: 'UPI_DEMO',
        idempotencyKey: key
      });
    expect(res2.status).toBe(200);
    expect(res2.body.data.orderId).toBe(firstOrderId);

    // Verify order was only created once in DB
    const matchingOrders = await Order.countDocuments({ idempotencyKey: key });
    expect(matchingOrders).toBe(1);
  });

  // 18. Order History Retrieval
  test('18. GET /api/orders returns placed orders for the authenticated user', async () => {
    const res = await request(app)
      .get('/api/orders')
      .set('Authorization', `Bearer ${userToken}`);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(Array.isArray(res.body.data)).toBe(true);
    expect(res.body.data.length).toBeGreaterThan(0);
  });

  // 19. Unauthorized Order Access
  test('19. User B cannot access User A\'s order details (403 Forbidden)', async () => {
    // Get an order placed by User A
    const ordersRes = await request(app)
      .get('/api/orders')
      .set('Authorization', `Bearer ${userToken}`);
    const userAOrderId = ordersRes.body.data[0].orderId;

    // Attempt to access with User B credentials
    const unauthorizedRes = await request(app)
      .get(`/api/orders/${encodeURIComponent(userAOrderId)}`)
      .set('Authorization', `Bearer ${userBToken}`);

    expect(unauthorizedRes.status).toBe(403);
    expect(unauthorizedRes.body.errorCode).toBe('FORBIDDEN');
  });

  // 20. Inventory Consistency
  test('20. Inventory is safely decremented after confirmed order submission', async () => {
    const prodBefore = await Product.findOne({ productId: 'med_06' });
    const originalStock = prodBefore.stockCount;

    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [{ productId: 'med_06', quantity: 2 }],
        paymentMethod: 'CARD_DEMO'
      });

    expect(res.status).toBe(201);

    const prodAfter = await Product.findOne({ productId: 'med_06' });
    expect(prodAfter.stockCount).toBe(originalStock - 2);
  });

  // 21. Multi-Item Concurrency & Partial Failure Rollback
  test('21. Multi-item order failure rolls back previously reserved inventory cleanly', async () => {
    const prodValidBefore = await Product.findOne({ productId: 'med_05' });
    const originalValidStock = prodValidBefore.stockCount;

    // med_05 is in stock (8), med_07 is out-of-stock (0)
    const res = await request(app)
      .post('/api/orders')
      .set('Authorization', `Bearer ${userToken}`)
      .send({
        addressId: userAddressId,
        items: [
          { productId: 'med_05', quantity: 1 },
          { productId: 'med_07', quantity: 1 }
        ],
        paymentMethod: 'UPI_DEMO'
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);

    // Verify med_05 stock was completely preserved/restored and not leaked
    const prodValidAfter = await Product.findOne({ productId: 'med_05' });
    expect(prodValidAfter.stockCount).toBe(originalValidStock);
  });

  // 22. Rapid Concurrent Duplicate Checkout Protection
  test('22. Rapid concurrent requests with identical idempotencyKey produce exactly 1 order', async () => {
    const concurrentKey = `rapid_key_${Date.now()}`;
    const prodBefore = await Product.findOne({ productId: 'med_02' });
    const stockBefore = prodBefore.stockCount;

    // Fire 3 simultaneous checkout requests
    const promises = [1, 2, 3].map(() =>
      request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          addressId: userAddressId,
          items: [{ productId: 'med_02', quantity: 1 }],
          paymentMethod: 'UPI_DEMO',
          idempotencyKey: concurrentKey
        })
    );

    const results = await Promise.all(promises);
    results.forEach(r => {
      expect([200, 201]).toContain(r.status);
      expect(r.body.success).toBe(true);
    });

    // Exactly one order in DB
    const matchingCount = await Order.countDocuments({ idempotencyKey: concurrentKey });
    expect(matchingCount).toBe(1);

    // Stock decremented by exactly 1 unit
    const prodAfter = await Product.findOne({ productId: 'med_02' });
    expect(prodAfter.stockCount).toBe(stockBefore - 1);
  });
});
