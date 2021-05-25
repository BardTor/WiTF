package com.gomorra.witf.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gomorra.witf.model.Product;
import com.gomorra.witf.util.Contstants;

import java.util.ArrayList;
import java.util.List;

//class responsible for handling SQLite queries (creation of database, tables, values, manipulations, et al)

public class DataBaseHandler extends SQLiteOpenHelper {
    private final Context context;

    public DataBaseHandler(@Nullable Context context) {


        super(context, Contstants.DATABASE_NAME, null, Contstants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + Contstants.TABLE_NAME + "("
                + Contstants.KEY_PRODUCT_ID + " INTEGER PRIMARY KEY,"
                + Contstants.KEY_PRODUCT_NAME + " INTEGER,"
                + Contstants.KEY_PRODUCT_WEIGHT + " INTEGER,"
                + Contstants.KEY_PRODUCT_QUANTITY + " INTEGER,"
                + Contstants.KEY_PRODUCT_EXPIRY_DATE + " INTEGER,"
                + Contstants.KEY_PRODUCT_SECONDARY_QUANTITY + " INTEGER,"
                + Contstants.KEY_PRODUCT_TOTAL_QUANTITY + " INTEGER"
                +
                ");";

        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Contstants.TABLE_NAME);
        onCreate(db);
    }

    //CRUD operations => add product
    public void addProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contstants.KEY_PRODUCT_ID, product.getProductId());
        contentValues.put(Contstants.KEY_PRODUCT_NAME, product.getProductName());
        contentValues.put(Contstants.KEY_PRODUCT_WEIGHT, product.getProductWeight());
        contentValues.put(Contstants.KEY_PRODUCT_QUANTITY, product.getProductQuantity());
        contentValues.put(Contstants.KEY_PRODUCT_EXPIRY_DATE, product.getProductExpiryDate());
        contentValues.put(Contstants.KEY_PRODUCT_SECONDARY_QUANTITY, product.getProductSecondaryQuantity());
        contentValues.put(Contstants.KEY_PRODUCT_TOTAL_QUANTITY, product.getProductTotalQuantity());

        //adding row

        db.insert(Contstants.TABLE_NAME, null, contentValues);
        Log.d("DBHANDLER", "added Product: ");
        db.close();

    }

    //CRUD operations => retrieve product

    public Product getProduct(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Contstants.TABLE_NAME,
                new String[]{Contstants.KEY_PRODUCT_ID,
                        Contstants.KEY_PRODUCT_NAME,
                        Contstants.KEY_PRODUCT_WEIGHT,
                        Contstants.KEY_PRODUCT_QUANTITY,
                        Contstants.KEY_PRODUCT_EXPIRY_DATE,
                        Contstants.KEY_PRODUCT_SECONDARY_QUANTITY,
                        Contstants.KEY_PRODUCT_TOTAL_QUANTITY},
                Contstants.KEY_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)}, null, null, null, null);

        Product product = new Product();

        if (cursor != null) {
            cursor.moveToFirst();

            product.setProductId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_ID))));
            product.setProductName(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_NAME)));
            product.setProductWeight(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_WEIGHT))));
            product.setProductQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_QUANTITY))));
            product.setProductExpiryDate(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_EXPIRY_DATE)));
            product.setProductSecondaryQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_SECONDARY_QUANTITY))));
            product.setProductTotalQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_TOTAL_QUANTITY))));
        }

        cursor.close();
        db.close();
        return product;
    }

    //CRUD operations => retrieve all products

    public List<Product> getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Product> productList = new ArrayList<>();

        Cursor cursor = db.query(Contstants.TABLE_NAME,
                new String[]{Contstants.KEY_PRODUCT_ID,
                        Contstants.KEY_PRODUCT_NAME,
                        Contstants.KEY_PRODUCT_WEIGHT,
                        Contstants.KEY_PRODUCT_QUANTITY,
                        Contstants.KEY_PRODUCT_EXPIRY_DATE,
                        Contstants.KEY_PRODUCT_SECONDARY_QUANTITY,
                        Contstants.KEY_PRODUCT_TOTAL_QUANTITY},
                null, null, null, null, Contstants.KEY_PRODUCT_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_ID))));
                product.setProductName(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_NAME)));
                product.setProductWeight(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_WEIGHT))));
                product.setProductQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_QUANTITY))));
                product.setProductExpiryDate(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_EXPIRY_DATE)));
                product.setProductSecondaryQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_SECONDARY_QUANTITY))));
                product.setProductTotalQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contstants.KEY_PRODUCT_TOTAL_QUANTITY))));
                productList.add(product);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;

    }

    //CRUD operations => update product

    public int updateProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contstants.KEY_PRODUCT_ID, product.getProductId());
        contentValues.put(Contstants.KEY_PRODUCT_NAME, product.getProductName());
        contentValues.put(Contstants.KEY_PRODUCT_WEIGHT, product.getProductWeight());
        contentValues.put(Contstants.KEY_PRODUCT_QUANTITY, product.getProductQuantity());
        contentValues.put(Contstants.KEY_PRODUCT_EXPIRY_DATE, product.getProductExpiryDate());
        contentValues.put(Contstants.KEY_PRODUCT_SECONDARY_QUANTITY, product.getProductSecondaryQuantity());
        contentValues.put(Contstants.KEY_PRODUCT_TOTAL_QUANTITY, product.getProductTotalQuantity());

        return db.update(Contstants.TABLE_NAME, contentValues, Contstants.KEY_PRODUCT_ID + "=?", new String[]{String.valueOf(product.getProductId())});

    }

    //CRUD operations => delete product

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contstants.TABLE_NAME, Contstants.KEY_PRODUCT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getProductCount() {
        String productCount = "SELECT * FROM " + Contstants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(productCount, null);
        int numberOfProducts = cursor.getCount();
        cursor.close();
        return numberOfProducts;
    }
}
