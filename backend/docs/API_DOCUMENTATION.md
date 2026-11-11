# MediCart REST API Documentation

**Version:** 1.0.0  
**Base URL:** `http://localhost:5000/api`  
**Environment:** Development / Staging / Production  
**Authentication Scheme:** Bearer JWT in `Authorization` header (`Authorization: Bearer <token>`)

---

## 1. Global Specifications

### Response Envelope Format

All responses from MediCart REST API conform to a uniform envelope standard:

#### Standard Success Response (`200 OK`, `201 Created`):
```json
{
  "success": true,
  "message": "Human-readable operation summary",
  "data": { ... } // Payload object or array
}
```

#### Standard Error Response (`400`, `401`, `403`, `404`, `429`, `500`):
```json
{
  "success": false,
  "message": "Human-readable error description",
  "errorCode": "STRING_ERROR_CODE",
  "errors": [ ... ] // Optional detailed field errors (Zod validation errors)
}
```

### Currency & Integer Representation
In strict accordance with Indian financial and accounting standards, all monetary amounts are computed, stored, and transmitted in **Integer Paise** (1 INR = 100 Paise).
- `pricePaise: 3400` = ₹34.00
- `subtotalPaise: 9000` = ₹90.00
- `deliveryFeePaise: 3000` = ₹30.00
- `packagingFeePaise: 1400` = ₹14.00
- `totalAmountPaise: 13400` = ₹134.00

---

## 2. Authentication APIs

### 2.1 Request Mobile OTP
Sends or generates a 6-digit OTP code for a valid 10-digit Indian mobile number.
- **Endpoint:** `POST /api/auth/request-otp`
- **Auth Required:** No
- **Rate Limit:** 5 requests / 15 minutes per IP

#### Request Headers:
`Content-Type: application/json`

#### Request Body:
```json
{
  "phoneNumber": "9876543210"
}
```
*Validation:* Must be a valid 10-digit Indian mobile number (regex `^[6-9]\d{9}$`).

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Verification code dispatched successfully",
  "data": {
    "phoneNumber": "9876543210",
    "expiresInSeconds": 300,
    "simulatedOtp": "487210" // Excluded in production
  }
}
```

---

### 2.2 Verify Mobile OTP
Verifies the 6-digit code, creates a User record if first-time sign-in, and returns a signed JWT.
- **Endpoint:** `POST /api/auth/verify-otp`
- **Auth Required:** No
- **Rate Limit:** 10 attempts / 15 minutes per IP

#### Request Body:
```json
{
  "phoneNumber": "9876543210",
  "otp": "487210"
}
```

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Phone number verified successfully",
  "data": {
    "user": {
      "_id": "6732389a01f92e80bc41a100",
      "name": "Sahil Kumar",
      "phoneNumber": "9876543210",
      "createdAt": "2026-11-11T16:30:00.000Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isNewUser": false
  }
}
```

#### Failure Response (`400 Bad Request`):
```json
{
  "success": false,
  "message": "Invalid verification code",
  "errorCode": "INVALID_OTP"
}
```

---

### 2.3 Get Current User Profile
Retrieves the authenticated user's profile details.
- **Endpoint:** `GET /api/auth/me`
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "_id": "6732389a01f92e80bc41a100",
    "name": "Sahil Kumar",
    "phoneNumber": "9876543210",
    "createdAt": "2026-11-11T16:30:00.000Z",
    "updatedAt": "2026-11-11T16:30:00.000Z"
  }
}
```

---

## 3. Product Catalogue APIs

### 3.1 List & Search Products
Retrieves catalogue products with multi-attribute text search, category filtering, availability filtering, sorting, and pagination.
- **Endpoint:** `GET /api/products`
- **Auth Required:** No

#### Query Parameters:
| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `search` | String | No | - | Substring query matching `name` or `genericName` |
| `category` | String | No | - | Exact category filter (e.g., `Pain Relief`, `Cold & Cough`) |
| `inStockOnly` | Boolean | No | `false` | When `true`, filters items with `isAvailable: true` and `stockCount > 0` |
| `sortBy` | String | No | `createdAt` | Options: `pricePaise`, `name`, `createdAt` |
| `sortOrder`| String | No | `asc` | Options: `asc`, `desc` |
| `page` | Integer| No | `1` | Page number |
| `limit` | Integer| No | `20` | Items per page (max `100`) |

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {
    "products": [
      {
        "_id": "6732389a01f92e80bc41a101",
        "productId": "med_01",
        "name": "Paracare 650mg Tablets",
        "genericName": "Paracetamol IP 650mg",
        "description": "Fast acting generic paracetamol formulation for pain relief and fever reduction.",
        "category": "Pain Relief",
        "pricePaise": 3400,
        "mrpPaise": 3800,
        "packSize": "Strip of 15 Tablets",
        "stockCount": 45,
        "isAvailable": true,
        "isExpired": false,
        "expiryDate": "Dec 2028",
        "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400"
      }
    ],
    "pagination": {
      "totalItems": 20,
      "currentPage": 1,
      "totalPages": 1,
      "limit": 20
    }
  }
}
```

---

### 3.2 Get Product Details
Retrieves detailed information for a single product by `productId` or Mongo `_id`.
- **Endpoint:** `GET /api/products/:id`
- **Auth Required:** No

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Product details retrieved successfully",
  "data": {
    "_id": "6732389a01f92e80bc41a101",
    "productId": "med_01",
    "name": "Paracare 650mg Tablets",
    "genericName": "Paracetamol IP 650mg",
    "description": "Fast acting generic paracetamol formulation for pain relief and fever reduction.",
    "category": "Pain Relief",
    "pricePaise": 3400,
    "mrpPaise": 3800,
    "packSize": "Strip of 15 Tablets",
    "stockCount": 45,
    "isAvailable": true,
    "isExpired": false,
    "expiryDate": "Dec 2028",
    "imageUrl": "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400"
  }
}
```

---

### 3.3 List Catalogue Categories
Retrieves all unique product categories present in the active inventory.
- **Endpoint:** `GET /api/categories`
- **Auth Required:** No

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    "Pain Relief",
    "Cold & Cough",
    "Digestive Care",
    "Healthcare Devices",
    "First Aid",
    "Vitamins & Supplements",
    "Personal Care"
  ]
}
```

---

## 4. Delivery Address APIs

### 4.1 List User Addresses
Retrieves all delivery addresses saved by the authenticated user.
- **Endpoint:** `GET /api/addresses`
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Addresses retrieved successfully",
  "data": [
    {
      "_id": "6732389a01f92e80bc41a102",
      "userId": "6732389a01f92e80bc41a100",
      "recipientName": "Sahil Kumar",
      "phoneNumber": "9876543210",
      "houseNumber": "Flat 402, Block B, Shanti Vihar",
      "locality": "12th Main Road, HAL 2nd Stage",
      "city": "Bengaluru",
      "state": "Karnataka",
      "pinCode": "560038",
      "addressType": "HOME",
      "isDefault": true,
      "createdAt": "2026-11-11T16:30:00.000Z"
    }
  ]
}
```

---

### 4.2 Create Delivery Address
Creates a new delivery address for the authenticated user.
- **Endpoint:** `POST /api/addresses`
- **Auth Required:** Yes (`Bearer <token>`)

#### Request Body:
```json
{
  "recipientName": "Sahil Kumar",
  "phoneNumber": "9876543210",
  "houseNumber": "Flat 402, Block B, Shanti Vihar",
  "locality": "12th Main Road, HAL 2nd Stage",
  "city": "Bengaluru",
  "state": "Karnataka",
  "pinCode": "560038",
  "addressType": "HOME",
  "isDefault": true
}
```

#### Success Response (`201 Created`):
```json
{
  "success": true,
  "message": "Address created successfully",
  "data": {
    "_id": "6732389a01f92e80bc41a102",
    "userId": "6732389a01f92e80bc41a100",
    "recipientName": "Sahil Kumar",
    "phoneNumber": "9876543210",
    "houseNumber": "Flat 402, Block B, Shanti Vihar",
    "locality": "12th Main Road, HAL 2nd Stage",
    "city": "Bengaluru",
    "state": "Karnataka",
    "pinCode": "560038",
    "addressType": "HOME",
    "isDefault": true
  }
}
```

---

### 4.3 Update Delivery Address
Updates an existing address belonging to the authenticated user (strict user isolation).
- **Endpoint:** `PUT /api/addresses/:id`
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Address updated successfully",
  "data": { ... }
}
```

---

### 4.4 Delete Delivery Address
Deletes an address belonging to the authenticated user.
- **Endpoint:** `DELETE /api/addresses/:id`
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Address removed successfully",
  "data": null
}
```

---

## 5. Orders & Checkout APIs

### 5.1 Create Order
Submits a verified order request with full server-side pricing enforcement, atomic inventory decrement, item/address snapshots, and idempotency protection.
- **Endpoint:** `POST /api/orders`
- **Auth Required:** Yes (`Bearer <token>`)

#### Request Body:
```json
{
  "addressId": "6732389a01f92e80bc41a102",
  "items": [
    { "productId": "med_01", "quantity": 2 },
    { "productId": "med_02", "quantity": 1 }
  ],
  "paymentMethod": "UPI_DEMO",
  "idempotencyKey": "req_sub_unique_uuid_or_timestamp"
}
```

#### Validation Rules Enforced Server-Side:
1. `items` array must not be empty.
2. Max quantity per item is **5 units** (Indian pharmacy regulatory safety limit).
3. Product must exist and not be expired (`isExpired: false`).
4. Available stock must satisfy `stockCount >= quantity`.
5. Client prices are completely ignored; backend fetches active prices from database.
6. Delivery fee: ₹30.00 (`3000` paise).
7. Packaging fee: ₹14.00 (`1400` paise).
8. Idempotency: if request with identical `idempotencyKey` is resent, server returns existing order without creating duplicate or decrementing stock twice.

#### Success Response (`201 Created` / `200 OK for idempotent duplicate`):
```json
{
  "success": true,
  "message": "Order request submitted and confirmed by backend",
  "data": {
    "_id": "6732389a01f92e80bc41a105",
    "orderId": "#MC-2026-1042",
    "userId": "6732389a01f92e80bc41a100",
    "items": [
      {
        "productId": "med_01",
        "name": "Paracare 650mg Tablets",
        "genericName": "Paracetamol IP 650mg",
        "unitPricePaise": 3400,
        "quantity": 2,
        "totalPricePaise": 6800
      },
      {
        "productId": "med_02",
        "name": "Cetriz-L 10mg Tablets",
        "genericName": "Levocetirizine Dihydrochloride 10mg",
        "unitPricePaise": 2200,
        "quantity": 1,
        "totalPricePaise": 2200
      }
    ],
    "deliveryAddress": {
      "recipientName": "Sahil Kumar",
      "phoneNumber": "9876543210",
      "houseNumber": "Flat 402, Block B, Shanti Vihar",
      "locality": "12th Main Road, HAL 2nd Stage",
      "city": "Bengaluru",
      "state": "Karnataka",
      "pinCode": "560038",
      "addressType": "HOME"
    },
    "subtotalPaise": 9000,
    "deliveryFeePaise": 3000,
    "packagingFeePaise": 1400,
    "totalAmountPaise": 13400,
    "paymentMethod": "UPI_DEMO",
    "paymentStatus": "CONFIRMED_SIMULATED",
    "orderStatus": "PROCESSING",
    "idempotencyKey": "req_sub_unique_uuid_or_timestamp",
    "createdAt": "2026-11-11T16:30:00.000Z"
  }
}
```

---

### 5.2 List User Orders
Retrieves orders placed by the authenticated user in reverse chronological order.
- **Endpoint:** `GET /api/orders`
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Orders retrieved successfully",
  "data": [
    {
      "orderId": "#MC-2026-1042",
      "subtotalPaise": 9000,
      "totalAmountPaise": 13400,
      "orderStatus": "PROCESSING",
      "items": [ ... ],
      "createdAt": "2026-11-11T16:30:00.000Z"
    }
  ]
}
```

---

### 5.3 Get Order By ID
Retrieves details of an order by `orderId` or Mongo `_id`. Strict ownership check applied (returns `403 FORBIDDEN` if another user attempts access).
- **Endpoint:** `GET /api/orders/:id`
- **Note:** For order references containing `#` (e.g. `#MC-2026-1042`), URL encode as `%23MC-2026-1042`.
- **Auth Required:** Yes (`Bearer <token>`)

#### Success Response (`200 OK`):
```json
{
  "success": true,
  "message": "Order details retrieved successfully",
  "data": {
    "orderId": "#MC-2026-1042",
    "userId": "6732389a01f92e80bc41a100",
    "items": [ ... ],
    "deliveryAddress": { ... },
    "subtotalPaise": 9000,
    "deliveryFeePaise": 3000,
    "packagingFeePaise": 1400,
    "totalAmountPaise": 13400,
    "paymentMethod": "UPI_DEMO",
    "paymentStatus": "CONFIRMED_SIMULATED",
    "orderStatus": "PROCESSING",
    "createdAt": "2026-11-11T16:30:00.000Z"
  }
}
```

---

## 6. System Health API

### Health Check
- **Endpoint:** `GET /health`
- **Auth Required:** No

#### Success Response (`200 OK`):
```json
{
  "status": "healthy",
  "service": "medicart-api",
  "environment": "development",
  "timestamp": "2026-11-11T16:30:00.000Z"
}
```
