const request = require('supertest');
const app = require('../app');
const User = require('../src/models/User');
const Otp = require('../src/models/Otp');
const { setupTestDB } = require('./setup');

setupTestDB();

describe('MediCart Auth Redesign Acceptance Test Suite (17 Tests)', () => {
  const testName = 'Dr. Rajesh Sharma';
  const testPhone = '9876500001';
  const testPassword = 'Password@123';
  const newPassword = 'NewSecret@456';
  let initialJwtToken;

  // 1. Successful registration step 1 & 2
  test('1. Successful registration with valid details and OTP verification', async () => {
    // Step 1: Request Registration OTP
    const reqRes = await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: testName,
        phoneNumber: testPhone,
        password: testPassword
      });

    expect(reqRes.status).toBe(200);
    expect(reqRes.body.success).toBe(true);
    expect(reqRes.body.data.simulatedOtp).toBe('487210');

    // Step 2: Verify Registration OTP
    const verifyRes = await request(app)
      .post('/api/auth/register/verify-otp')
      .send({
        phoneNumber: testPhone,
        otp: '487210',
        name: testName,
        password: testPassword
      });

    expect(verifyRes.status).toBe(201);
    expect(verifyRes.body.success).toBe(true);
    expect(verifyRes.body.data.token).toBeDefined();
    expect(verifyRes.body.data.user.name).toBe(testName);
    expect(verifyRes.body.data.user.phoneNumber).toBe(testPhone);
    expect(verifyRes.body.data.user.passwordHash).toBeUndefined(); // Never leaked

    initialJwtToken = verifyRes.body.data.token;
  });

  // 2. Duplicate mobile registration rejection
  test('2. Duplicate mobile registration request is rejected with 409', async () => {
    const res = await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: 'Another Name',
        phoneNumber: testPhone,
        password: 'AnotherPassword@1'
      });

    expect(res.status).toBe(409);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('USER_ALREADY_EXISTS');
  });

  // 3. Invalid registration OTP rejection
  test('3. Invalid registration OTP is rejected with 400', async () => {
    const newPhone = '9876500002';
    await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: 'Test Two',
        phoneNumber: newPhone,
        password: testPassword
      });

    const verifyRes = await request(app)
      .post('/api/auth/register/verify-otp')
      .send({
        phoneNumber: newPhone,
        otp: '000000',
        name: 'Test Two',
        password: testPassword
      });

    expect(verifyRes.status).toBe(400);
    expect(verifyRes.body.success).toBe(false);
    expect(verifyRes.body.errorCode).toBe('INVALID_OTP');
  });

  // 4. Successful password login
  test('4. Successful login with correct phone number and password', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: testPhone,
        password: testPassword
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.token).toBeDefined();
    expect(res.body.data.user.phoneNumber).toBe(testPhone);
    expect(res.body.data.user.passwordHash).toBeUndefined();
  });

  // 5. Incorrect password rejection
  test('5. Incorrect password is rejected with 401', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: testPhone,
        password: 'WrongPassword@999'
      });

    expect(res.status).toBe(401);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INVALID_CREDENTIALS');
  });

  // 6. Unregistered phone login rejection
  test('6. Unregistered phone number login is rejected with 401', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: '9111111111',
        password: testPassword
      });

    expect(res.status).toBe(401);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INVALID_CREDENTIALS');
  });

  // 7. Forgot password OTP request
  test('7. Forgot password OTP request succeeds for registered user', async () => {
    const res = await request(app)
      .post('/api/auth/forgot-password/request-otp')
      .send({
        phoneNumber: testPhone
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.simulatedOtp).toBe('487210');
  });

  // 8. Invalid reset OTP
  test('8. Invalid reset OTP is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/auth/forgot-password/reset')
      .send({
        phoneNumber: testPhone,
        otp: '999999',
        newPassword: newPassword
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INVALID_OTP');
  });

  // 9. Successful password reset
  test('9. Successful password reset using valid OTP', async () => {
    const res = await request(app)
      .post('/api/auth/forgot-password/reset')
      .send({
        phoneNumber: testPhone,
        otp: '487210',
        newPassword: newPassword
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.message).toContain('Password has been reset successfully');
  });

  // 10. Login with new password
  test('10. User can successfully log in using the newly reset password', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: testPhone,
        password: newPassword
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.data.token).toBeDefined();
  });

  // 11. Old password rejected after reset
  test('11. Previous old password is now rejected with 401', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: testPhone,
        password: testPassword
      });

    expect(res.status).toBe(401);
    expect(res.body.success).toBe(false);
    expect(res.body.errorCode).toBe('INVALID_CREDENTIALS');
  });

  // 12. Expired OTP rejection
  test('12. Expired OTP code is rejected with 400', async () => {
    const phone = '9876500003';
    // Request registration OTP
    await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: 'Expired User',
        phoneNumber: phone,
        password: testPassword
      });

    // Manually expire the record in test database
    await Otp.updateOne(
      { phoneNumber: phone, purpose: 'REGISTRATION' },
      { $set: { expiresAt: new Date(Date.now() - 1000) } }
    );

    const res = await request(app)
      .post('/api/auth/register/verify-otp')
      .send({
        phoneNumber: phone,
        otp: '487210',
        name: 'Expired User',
        password: testPassword
      });

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('OTP_EXPIRED');
  });

  // 13. OTP attempt limit exceeded
  test('13. Exceeding OTP max attempts locks the code and returns 400', async () => {
    const phone = '9876500004';
    await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: 'Attempt User',
        phoneNumber: phone,
        password: testPassword
      });

    // Set attempts to 5
    await Otp.updateOne(
      { phoneNumber: phone, purpose: 'REGISTRATION' },
      { $set: { attempts: 5 } }
    );

    const res = await request(app)
      .post('/api/auth/register/verify-otp')
      .send({
        phoneNumber: phone,
        otp: '487210',
        name: 'Attempt User',
        password: testPassword
      });

    expect(res.status).toBe(400);
    expect(res.body.errorCode).toBe('OTP_ATTEMPTS_EXCEEDED');
  });

  // 14. Password complexity validator enforcement
  test('14. Weak password lacking uppercase or number is rejected with 400', async () => {
    const res = await request(app)
      .post('/api/auth/register/request-otp')
      .send({
        name: 'Weak Pass User',
        phoneNumber: '9876500005',
        password: 'weakpassword' // lacks uppercase and number
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
  });

  // 15. Generic forgot-password responses for non-registered phone
  test('15. Forgot password request returns identical generic response for non-registered phone', async () => {
    const res = await request(app)
      .post('/api/auth/forgot-password/request-otp')
      .send({
        phoneNumber: '9000000000' // Unregistered
      });

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.message).toContain('If this mobile number is registered');
  });

  // 16. JWT authentication after password login
  test('16. JWT token from password login allows access to protected GET /api/auth/me', async () => {
    const loginRes = await request(app)
      .post('/api/auth/login')
      .send({
        phoneNumber: testPhone,
        password: newPassword
      });

    const token = loginRes.body.data.token;
    const meRes = await request(app)
      .get('/api/auth/me')
      .set('Authorization', `Bearer ${token}`);

    expect(meRes.status).toBe(200);
    expect(meRes.body.data.phoneNumber).toBe(testPhone);
  });

  // 17. Session invalidation after password reset (tokenVersion increment)
  test('17. Previous JWT issued before password reset is rejected with 401 SESSION_REVOKED', async () => {
    // initialJwtToken was issued during registration before the password reset
    const res = await request(app)
      .get('/api/auth/me')
      .set('Authorization', `Bearer ${initialJwtToken}`);

    expect(res.status).toBe(401);
    expect(res.body.errorCode).toBe('SESSION_REVOKED');
  });
});
