# MediCart Backend REST API

Production-ready Node.js & Express REST API for the **MediCart** Android Healthcare Platform.

---

## 🛠️ Tech Stack & Architecture

- **Runtime:** Node.js (v18+)
- **Framework:** Express.js 4.x
- **Database:** MongoDB Atlas / Local MongoDB via Mongoose ODM
- **Authentication:** Phone OTP with cryptographic HMAC verification and signed JWT Bearer tokens
- **Input Validation:** Zod schema validation middleware
- **Security:** Helmet HTTP security headers, CORS configuration, Express Rate Limiting
- **Testing:** Jest + Supertest + `mongodb-memory-server` (Zero external dependencies for automated CI tests)
- **Monetary Unit:** Integer Paise (e.g. ₹34.00 = `3400` paise)

---

## 📁 Project Structure

```
backend/
├── app.js                       # Express app configuration & middleware pipeline
├── server.js                    # Server entry point & graceful shutdown
├── package.json                 # Dependencies and npm scripts
├── .env.example                 # Sample environment variables
├── .gitignore                   # Ignores node_modules, .env, coverage
├── src/
│   ├── config/
│   │   ├── env.js               # Centralized typed environment parser
│   │   └── db.js                # Mongoose connection manager with reconnection logic
│   ├── models/
│   │   ├── User.js              # User collection (unique indexed phone)
│   │   ├── Product.js           # Medicine catalogue with paise pricing & search index
│   │   ├── Address.js           # User delivery addresses with Indian PIN validation
│   │   ├── Order.js             # Orders with price/address snapshots & idempotency
│   │   └── Otp.js               # Temporary OTP verification records with TTL auto-expiry
│   ├── middleware/
│   │   ├── authMiddleware.js    # JWT Bearer token validator & req.user injector
│   │   ├── validate.js          # Zod schema validation middleware
│   │   ├── rateLimiter.js       # Express rate limiter for authentication routes
│   │   └── errorHandler.js      # Global 404 and structured error response handlers
│   ├── routes/
│   │   ├── authRoutes.js        # /api/auth routes
│   │   ├── productRoutes.js     # /api/products and /api/categories
│   │   ├── addressRoutes.js     # /api/addresses routes
│   │   └── orderRoutes.js       # /api/orders routes
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── productController.js
│   │   ├── addressController.js
│   │   └── orderController.js
│   ├── services/
│   │   ├── authService.js       # OTP generation, hashing, verification, token issuance
│   │   ├── productService.js    # Catalogue search, filtering, and sorting
│   │   ├── addressService.js    # Address CRUD with user ownership protection
│   │   └── orderService.js      # Atomic stock check/decrement, fee calculation, idempotency
│   ├── validators/
│   │   ├── authValidator.js     # Indian mobile number and OTP Zod schemas
│   │   ├── addressValidator.js  # Address fields and PIN code Zod schema
│   │   └── orderValidator.js    # Order submission Zod schema (max 5 items, positive quantities)
│   ├── utils/
│   │   ├── responseEnvelope.js  # Standardized { success, message, data, errorCode } responses
│   │   └── orderIdGenerator.js  # Generates #MC-2026-XXXX formatted order IDs
│   └── scripts/
│       └── seed.js              # Seeds 20 realistic fictional Indian pharmacy products
├── tests/
│   ├── setup.js                 # In-memory MongoDB lifecycle runner
│   └── api.test.js              # 20 acceptance tests covering complete specification
└── docs/
    ├── API_DOCUMENTATION.md     # Full REST API specification
    ├── API_CONTRACT.md          # Kotlin Android DTO & Retrofit contract
    └── MediCart_Postman_Collection.json # Importable Postman v2.1 collection
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Node.js 18.x or higher
- MongoDB instance (MongoDB Atlas connection string or local MongoDB daemon)

### 2. Environment Configuration
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Default `.env` configuration:
```env
PORT=5000
NODE_ENV=development
MONGODB_URI=mongodb://localhost:27017/medicart
JWT_SECRET=your_super_secret_jwt_key_medicart_2026
JWT_EXPIRES_IN=30d
```

### 3. Installation
```bash
npm install
```

### 4. Seed the Database
Populates MongoDB with 20 realistic fictional Indian pharmacy products and a sample user:
```bash
npm run seed
```

### 5. Start Development Server
```bash
npm run dev
# Or production mode:
npm start
```

---

## 🧪 Running Automated Tests

MediCart includes a test suite of 20 acceptance tests using `jest`, `supertest`, and `mongodb-memory-server` (requires no external MongoDB server running):

```bash
npm test
```

Expected output:
```
PASS tests/api.test.js
  MediCart REST API Acceptance Test Suite (20 Tests)
    √ 1. Server health check endpoint returns 200 and healthy status
    √ 2. Successful OTP verification creates user and issues valid JWT
    √ 3. Invalid OTP code is rejected with 400
    √ 4. Protected route GET /api/auth/me succeeds with valid Bearer token
    √ 5. Protected route rejects missing or invalid authorization header with 401
    √ 6. Product search by name or generic name returns matching items
    √ 7. Filter products by category returns correct categorized subset
    √ 8. GET /api/products/:id returns complete details for valid product
    √ 9. Authenticated user can create a valid delivery address
    √ 10. User B cannot update or delete User A's address (403 Forbidden)
    √ 11. Successful order creation calculates totals, stores snapshots and returns reference
    √ 12. Order creation with empty cart is rejected with 400
    √ 13. Order creation containing out-of-stock item is rejected with 400
    √ 14. Order creation containing expired medicine batch is rejected with 400
    √ 15. Order creation with quantity exceeding 5 packs is rejected with 400
    √ 16. Backend enforces its own catalogue prices and ignores tampered client amounts
    √ 17. Repeating order request with same idempotencyKey returns existing order without creating duplicate
    √ 18. GET /api/orders returns placed orders for the authenticated user
    √ 19. User B cannot access User A's order details (403 Forbidden)
    √ 20. Inventory is safely decremented after confirmed order submission

Test Suites: 1 passed, 1 total
Tests:       20 passed, 20 total
```

---

## 🛡️ Security & Integrity Highlights

1. **Server-Side Price Authority:** Client applications never transmit prices. The backend looks up active database prices in paise, computes subtotal, adds standard delivery (`₹30.00` = 3000 paise) and packaging fees (`₹14.00` = 1400 paise).
2. **Atomic Inventory Decrement:** Orders atomically decrement stock using MongoDB `$gte` condition checks (`{ productId, stockCount: { $gte: quantity } }`). Stock is rolled back automatically if order creation fails.
3. **Idempotency Protection:** Clients transmit unique `idempotencyKey` values. If an identical order is received again, the backend returns the existing order rather than creating duplicate orders or double-charging.
4. **User Isolation:** All addresses and orders enforce ownership verification; attempts to view, edit, or delete another user's resources return `403 FORBIDDEN`.
5. **No Secrets in Repo:** `.env` is git-ignored and only `.env.example` is tracked.
