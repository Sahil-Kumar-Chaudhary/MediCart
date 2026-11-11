package com.medicart.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.medicart.app.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * SQLite Local Database Helper for MediCart.
 * Provides resilient, offline-first persistence for products, cart items,
 * placed orders, and delivery addresses.
 */
class MediCartDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS (
                $COL_PROD_ID TEXT PRIMARY KEY,
                $COL_PROD_NAME TEXT NOT NULL,
                $COL_PROD_GENERIC_NAME TEXT NOT NULL,
                $COL_PROD_PACK_SIZE TEXT NOT NULL,
                $COL_PROD_PRICE REAL NOT NULL,
                $COL_PROD_MRP REAL NOT NULL,
                $COL_PROD_DISCOUNT INTEGER NOT NULL,
                $COL_PROD_CATEGORY_ID TEXT NOT NULL,
                $COL_PROD_IN_STOCK INTEGER NOT NULL,
                $COL_PROD_STOCK_COUNT INTEGER NOT NULL,
                $COL_PROD_IS_EXPIRED INTEGER NOT NULL,
                $COL_PROD_EXPIRY_DATE TEXT NOT NULL,
                $COL_PROD_ACTIVE_INGREDIENT TEXT NOT NULL,
                $COL_PROD_ICON_EMOJI TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CART (
                $COL_CART_PRODUCT_ID TEXT PRIMARY KEY,
                $COL_CART_QUANTITY INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ORDERS (
                $COL_ORDER_REF TEXT PRIMARY KEY,
                $COL_ORDER_CREATED_AT TEXT NOT NULL,
                $COL_ORDER_TOTAL REAL NOT NULL,
                $COL_ORDER_STATUS TEXT NOT NULL,
                $COL_ORDER_PAYMENT_METHOD TEXT NOT NULL,
                $COL_ORDER_ADDRESS_JSON TEXT NOT NULL,
                $COL_ORDER_ITEMS_JSON TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ADDRESSES (
                $COL_ADDR_ID TEXT PRIMARY KEY,
                $COL_ADDR_RECIPIENT TEXT NOT NULL,
                $COL_ADDR_MOBILE TEXT NOT NULL,
                $COL_ADDR_FLAT TEXT NOT NULL,
                $COL_ADDR_STREET TEXT NOT NULL,
                $COL_ADDR_CITY TEXT NOT NULL,
                $COL_ADDR_STATE TEXT NOT NULL,
                $COL_ADDR_PIN TEXT NOT NULL,
                $COL_ADDR_TYPE TEXT NOT NULL,
                $COL_ADDR_IS_DEFAULT INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADDRESSES")
        onCreate(db)
    }

    // --- Product Operations ---
    fun saveProducts(products: List<Product>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (p in products) {
                val cv = ContentValues().apply {
                    put(COL_PROD_ID, p.id)
                    put(COL_PROD_NAME, p.name)
                    put(COL_PROD_GENERIC_NAME, p.genericName)
                    put(COL_PROD_PACK_SIZE, p.packSize)
                    put(COL_PROD_PRICE, p.priceInr)
                    put(COL_PROD_MRP, p.mrpInr)
                    put(COL_PROD_DISCOUNT, p.discountPercent)
                    put(COL_PROD_CATEGORY_ID, p.categoryId)
                    put(COL_PROD_IN_STOCK, if (p.inStock) 1 else 0)
                    put(COL_PROD_STOCK_COUNT, p.stockCount)
                    put(COL_PROD_IS_EXPIRED, if (p.isExpired) 1 else 0)
                    put(COL_PROD_EXPIRY_DATE, p.expiryDate)
                    put(COL_PROD_ACTIVE_INGREDIENT, p.activeIngredient)
                    put(COL_PROD_ICON_EMOJI, p.iconEmoji)
                }
                db.insertWithOnConflict(TABLE_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, "$COL_PROD_NAME ASC")
        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }
        return products
    }

    fun getProductById(id: String): Product? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COL_PROD_ID = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToProduct(it)
            }
        }
        return null
    }

    private fun cursorToProduct(c: Cursor): Product {
        return Product(
            id = c.getString(c.getColumnIndexOrThrow(COL_PROD_ID)),
            name = c.getString(c.getColumnIndexOrThrow(COL_PROD_NAME)),
            genericName = c.getString(c.getColumnIndexOrThrow(COL_PROD_GENERIC_NAME)),
            packSize = c.getString(c.getColumnIndexOrThrow(COL_PROD_PACK_SIZE)),
            priceInr = c.getDouble(c.getColumnIndexOrThrow(COL_PROD_PRICE)),
            mrpInr = c.getDouble(c.getColumnIndexOrThrow(COL_PROD_MRP)),
            discountPercent = c.getInt(c.getColumnIndexOrThrow(COL_PROD_DISCOUNT)),
            categoryId = c.getString(c.getColumnIndexOrThrow(COL_PROD_CATEGORY_ID)),
            inStock = c.getInt(c.getColumnIndexOrThrow(COL_PROD_IN_STOCK)) == 1,
            stockCount = c.getInt(c.getColumnIndexOrThrow(COL_PROD_STOCK_COUNT)),
            isExpired = c.getInt(c.getColumnIndexOrThrow(COL_PROD_IS_EXPIRED)) == 1,
            expiryDate = c.getString(c.getColumnIndexOrThrow(COL_PROD_EXPIRY_DATE)),
            activeIngredient = c.getString(c.getColumnIndexOrThrow(COL_PROD_ACTIVE_INGREDIENT)),
            iconEmoji = c.getString(c.getColumnIndexOrThrow(COL_PROD_ICON_EMOJI))
        )
    }

    // --- Cart Operations ---
    fun saveCart(cartItems: List<CartItem>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CART, null, null)
            for (item in cartItems) {
                val cv = ContentValues().apply {
                    put(COL_CART_PRODUCT_ID, item.product.id)
                    put(COL_CART_QUANTITY, item.quantity)
                }
                db.insert(TABLE_CART, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getCart(): List<CartItem> {
        val items = mutableListOf<CartItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT c.$COL_CART_QUANTITY, p.* 
            FROM $TABLE_CART c 
            JOIN $TABLE_PRODUCTS p ON c.$COL_CART_PRODUCT_ID = p.$COL_PROD_ID
            """.trimIndent(),
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val qty = it.getInt(it.getColumnIndexOrThrow(COL_CART_QUANTITY))
                val product = cursorToProduct(it)
                items.add(CartItem(product = product, quantity = qty))
            }
        }
        return items
    }

    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_CART, null, null)
    }

    // --- Order Operations ---
    fun insertOrder(order: Order) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ORDER_REF, order.orderReference)
            put(COL_ORDER_CREATED_AT, order.createdAt)
            put(COL_ORDER_TOTAL, order.totalAmount)
            put(COL_ORDER_STATUS, order.status.name)
            put(COL_ORDER_PAYMENT_METHOD, order.paymentMethod.name)
            put(COL_ORDER_ADDRESS_JSON, addressToJson(order.address))
            put(COL_ORDER_ITEMS_JSON, itemsToJson(order.items))
        }
        db.insertWithOnConflict(TABLE_ORDERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ORDERS, null, null, null, null, null, "$COL_ORDER_CREATED_AT DESC")
        cursor.use {
            while (it.moveToNext()) {
                val ref = it.getString(it.getColumnIndexOrThrow(COL_ORDER_REF))
                val createdAt = it.getString(it.getColumnIndexOrThrow(COL_ORDER_CREATED_AT))
                val total = it.getDouble(it.getColumnIndexOrThrow(COL_ORDER_TOTAL))
                val statusStr = it.getString(it.getColumnIndexOrThrow(COL_ORDER_STATUS))
                val paymentStr = it.getString(it.getColumnIndexOrThrow(COL_ORDER_PAYMENT_METHOD))
                val addrJson = it.getString(it.getColumnIndexOrThrow(COL_ORDER_ADDRESS_JSON))
                val itemsJson = it.getString(it.getColumnIndexOrThrow(COL_ORDER_ITEMS_JSON))

                val status = runCatching { OrderStatus.valueOf(statusStr) }.getOrDefault(OrderStatus.PROCESSING)
                val payment = runCatching { PaymentMethod.valueOf(paymentStr) }.getOrDefault(PaymentMethod.UPI_DEMO)
                val address = jsonToAddress(addrJson)
                val items = jsonToItems(itemsJson)

                orders.add(
                    Order(
                        orderReference = ref,
                        createdAt = createdAt,
                        items = items,
                        address = address,
                        paymentMethod = payment,
                        totalAmount = total,
                        status = status
                    )
                )
            }
        }
        return orders
    }

    // --- Address Operations ---
    fun saveAddress(address: DeliveryAddress) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ADDR_ID, address.id)
            put(COL_ADDR_RECIPIENT, address.recipientName)
            put(COL_ADDR_MOBILE, address.mobileNumber)
            put(COL_ADDR_FLAT, address.flatNumber)
            put(COL_ADDR_STREET, address.streetLocality)
            put(COL_ADDR_CITY, address.city)
            put(COL_ADDR_STATE, address.state)
            put(COL_ADDR_PIN, address.pinCode)
            put(COL_ADDR_TYPE, address.addressType.name)
            put(COL_ADDR_IS_DEFAULT, if (address.isDefault) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_ADDRESSES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAddresses(): List<DeliveryAddress> {
        val addresses = mutableListOf<DeliveryAddress>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ADDRESSES, null, null, null, null, null, "$COL_ADDR_IS_DEFAULT DESC")
        cursor.use {
            while (it.moveToNext()) {
                addresses.add(
                    DeliveryAddress(
                        id = it.getString(it.getColumnIndexOrThrow(COL_ADDR_ID)),
                        recipientName = it.getString(it.getColumnIndexOrThrow(COL_ADDR_RECIPIENT)),
                        mobileNumber = it.getString(it.getColumnIndexOrThrow(COL_ADDR_MOBILE)),
                        flatNumber = it.getString(it.getColumnIndexOrThrow(COL_ADDR_FLAT)),
                        streetLocality = it.getString(it.getColumnIndexOrThrow(COL_ADDR_STREET)),
                        city = it.getString(it.getColumnIndexOrThrow(COL_ADDR_CITY)),
                        state = it.getString(it.getColumnIndexOrThrow(COL_ADDR_STATE)),
                        pinCode = it.getString(it.getColumnIndexOrThrow(COL_ADDR_PIN)),
                        addressType = runCatching {
                            AddressType.valueOf(it.getString(it.getColumnIndexOrThrow(COL_ADDR_TYPE)))
                        }.getOrDefault(AddressType.HOME),
                        isDefault = it.getInt(it.getColumnIndexOrThrow(COL_ADDR_IS_DEFAULT)) == 1
                    )
                )
            }
        }
        return addresses
    }

    // --- JSON Serialization Helpers ---
    private fun addressToJson(address: DeliveryAddress): String {
        return JSONObject().apply {
            put("id", address.id)
            put("recipientName", address.recipientName)
            put("mobileNumber", address.mobileNumber)
            put("flatNumber", address.flatNumber)
            put("streetLocality", address.streetLocality)
            put("city", address.city)
            put("state", address.state)
            put("pinCode", address.pinCode)
            put("addressType", address.addressType.name)
            put("isDefault", address.isDefault)
        }.toString()
    }

    private fun jsonToAddress(jsonStr: String): DeliveryAddress {
        return try {
            val obj = JSONObject(jsonStr)
            DeliveryAddress(
                id = obj.optString("id", "addr_1"),
                recipientName = obj.optString("recipientName", "Sahil Kumar"),
                mobileNumber = obj.optString("mobileNumber", "+91 98765 43210"),
                flatNumber = obj.optString("flatNumber", "Flat 402, Block B, Shanti Vihar"),
                streetLocality = obj.optString("streetLocality", "12th Main Road, HAL 2nd Stage"),
                city = obj.optString("city", "Bengaluru"),
                state = obj.optString("state", "Karnataka"),
                pinCode = obj.optString("pinCode", "560038"),
                addressType = runCatching {
                    AddressType.valueOf(obj.optString("addressType", "HOME"))
                }.getOrDefault(AddressType.HOME),
                isDefault = obj.optBoolean("isDefault", true)
            )
        } catch (e: Exception) {
            DeliveryAddress(
                id = "addr_fallback",
                recipientName = "Sahil Kumar",
                mobileNumber = "+91 98765 43210",
                flatNumber = "Flat 402",
                streetLocality = "12th Main Road",
                city = "Bengaluru",
                state = "Karnataka",
                pinCode = "560038",
                addressType = AddressType.HOME,
                isDefault = true
            )
        }
    }

    private fun itemsToJson(items: List<CartItem>): String {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("quantity", item.quantity)
                val p = item.product
                put("product", JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("genericName", p.genericName)
                    put("packSize", p.packSize)
                    put("priceInr", p.priceInr)
                    put("mrpInr", p.mrpInr)
                    put("discountPercent", p.discountPercent)
                    put("categoryId", p.categoryId)
                    put("inStock", p.inStock)
                    put("stockCount", p.stockCount)
                    put("isExpired", p.isExpired)
                    put("expiryDate", p.expiryDate)
                    put("activeIngredient", p.activeIngredient)
                    put("iconEmoji", p.iconEmoji)
                })
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun jsonToItems(jsonStr: String): List<CartItem> {
        val list = mutableListOf<CartItem>()
        return try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val qty = obj.getInt("quantity")
                val pObj = obj.getJSONObject("product")
                val product = Product(
                    id = pObj.getString("id"),
                    name = pObj.getString("name"),
                    genericName = pObj.getString("genericName"),
                    packSize = pObj.getString("packSize"),
                    priceInr = pObj.getDouble("priceInr"),
                    mrpInr = pObj.getDouble("mrpInr"),
                    discountPercent = pObj.getInt("discountPercent"),
                    categoryId = pObj.getString("categoryId"),
                    inStock = pObj.getBoolean("inStock"),
                    stockCount = pObj.getInt("stockCount"),
                    isExpired = pObj.optBoolean("isExpired", false),
                    expiryDate = pObj.getString("expiryDate"),
                    activeIngredient = pObj.getString("activeIngredient"),
                    iconEmoji = pObj.getString("iconEmoji")
                )
                list.add(CartItem(product, qty))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        const val DATABASE_NAME = "medicart_database.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PRODUCTS = "products"
        const val COL_PROD_ID = "id"
        const val COL_PROD_NAME = "name"
        const val COL_PROD_GENERIC_NAME = "generic_name"
        const val COL_PROD_PACK_SIZE = "pack_size"
        const val COL_PROD_PRICE = "price_inr"
        const val COL_PROD_MRP = "mrp_inr"
        const val COL_PROD_DISCOUNT = "discount_percent"
        const val COL_PROD_CATEGORY_ID = "category_id"
        const val COL_PROD_IN_STOCK = "in_stock"
        const val COL_PROD_STOCK_COUNT = "stock_count"
        const val COL_PROD_IS_EXPIRED = "is_expired"
        const val COL_PROD_EXPIRY_DATE = "expiry_date"
        const val COL_PROD_ACTIVE_INGREDIENT = "active_ingredient"
        const val COL_PROD_ICON_EMOJI = "icon_emoji"

        const val TABLE_CART = "cart_items"
        const val COL_CART_PRODUCT_ID = "product_id"
        const val COL_CART_QUANTITY = "quantity"

        const val TABLE_ORDERS = "orders"
        const val COL_ORDER_REF = "order_reference"
        const val COL_ORDER_CREATED_AT = "created_at"
        const val COL_ORDER_TOTAL = "total_amount"
        const val COL_ORDER_STATUS = "status"
        const val COL_ORDER_PAYMENT_METHOD = "payment_method"
        const val COL_ORDER_ADDRESS_JSON = "address_json"
        const val COL_ORDER_ITEMS_JSON = "items_json"

        const val TABLE_ADDRESSES = "addresses"
        const val COL_ADDR_ID = "id"
        const val COL_ADDR_RECIPIENT = "recipient_name"
        const val COL_ADDR_MOBILE = "mobile_number"
        const val COL_ADDR_FLAT = "flat_number"
        const val COL_ADDR_STREET = "street_locality"
        const val COL_ADDR_CITY = "city"
        const val COL_ADDR_STATE = "state"
        const val COL_ADDR_PIN = "pin_code"
        const val COL_ADDR_TYPE = "address_type"
        const val COL_ADDR_IS_DEFAULT = "is_default"
    }
}
