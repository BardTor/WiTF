package com.gomorra.witf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.gomorra.witf.data.DataBaseHandler;
import com.gomorra.witf.model.Product;
import com.gomorra.witf.model.RecipeProduct;
import com.gomorra.witf.util.Trimmer;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScanProduct extends AppCompatActivity implements View.OnClickListener {

    CodeScanner codeScanner;
    CodeScannerView codeScannerView;

    Trimmer trimmer;
    Trimmer recipeProductsTrimmer;

    TextView productTextView;
    TextView weightTextView;
    EditText quantityEditText;
    EditText dateEditText;
    EditText secondaryQuantityEditText;
    RelativeLayout relativeLayoutSecondaryQuantity;
    RelativeLayout relativeLayoutHideableSection;


    Button cancelButton;
    Button confirmButton;

    TextView recipeHeader;
    TextView recipeProducts;

    private DataBaseHandler dataBaseHandler;

    List<Product> productsListFromDB;

    String productExtracted;
    int productWeightExtracted;
    int productQuantityExtracted;
    String productExpiryDateExtracted;
    int productSecondaryQuantityExtracted;
    boolean secondaryQuantityVisibility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_product);

        codeScannerView = findViewById(R.id.scan_product_view);
        codeScanner = new CodeScanner(this, codeScannerView);

        productTextView = findViewById(R.id.product_textView);
        weightTextView = findViewById(R.id.weight_textView);
        quantityEditText = findViewById(R.id.quantity_editText);
        dateEditText = findViewById(R.id.date_editText);
        secondaryQuantityEditText = findViewById(R.id.secondary_quantity_editText);

        relativeLayoutSecondaryQuantity = findViewById(R.id.secondary_quantity_data);
        relativeLayoutHideableSection = findViewById(R.id.recipe_hideable_section);

        recipeHeader = findViewById(R.id.recipe_products_header);
        recipeProducts = findViewById(R.id.recipe_products);
        relativeLayoutSecondaryQuantity.setVisibility(View.GONE);

        hideRecipeSection();


        recipeProductsTrimmer = new Trimmer();

        cancelButton = findViewById(R.id.cancel_scan_button);
        confirmButton = findViewById(R.id.confirm_scan_button);


        dateEditText.setOnClickListener(this);
        codeScannerView.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        dataBaseHandler = new DataBaseHandler(ScanProduct.this);

        productsListFromDB = dataBaseHandler.getAllProducts();

        List<Product> products = dataBaseHandler.getAllProducts();



    /*    for (Product product : products) {
            Log.d("Testing_DB", "Product ID in DB: " + product.getProductId());
            Log.d("Testing_DB", "Product Name in DB: " + product.getProductName());
            Log.d("Testing_DB", "Product Weight in DB: " + product.getProductWeight());
            Log.d("Testing_DB", "Product Quantity in DB: " + product.getProductQuantity());
            Log.d("Testing_DB", "Product Expiry Date in DB: " + product.getProductExpiryDate());
        }*/

        //camera functionality; trimmer engaged in order to decode QR barcodes and feed data to UI TextViews/EditTexts

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override

            public void onDecoded(@NonNull Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //token     id      name            w    q date     sq visible
                        //19222106,24998702,Red Onions Pack,1000,1,20201217,8,true
                        trimmer = new Trimmer();

                        String qrResult = result.getText();

                        trimmer.trimQRString(qrResult);

                        if (trimmer.isVerifier()) {

                            if (trimmer.getReturnedToken() == 19222106) {

                                trimmer.dateSplitter();
                                productExtracted = trimmer.getProductName();
                                productWeightExtracted = trimmer.getWeight();
                                productQuantityExtracted = trimmer.getQuantity();
                                productExpiryDateExtracted = trimmer.getYear() + "-" + trimmer.getMonth() + "-" + trimmer.getDay();
                                productSecondaryQuantityExtracted = trimmer.getSecondaryQuantity();
                                secondaryQuantityVisibility = trimmer.isNeedsVisibility();

                                productTextView.setText(productExtracted);
                                weightTextView.setText(Integer.toString(productWeightExtracted));
                                quantityEditText.setText(Integer.toString(productQuantityExtracted));
                                dateEditText.setText(productExpiryDateExtracted);

                                secondaryQuantityEditText.setText(Integer.toString(productSecondaryQuantityExtracted));

                                relativeLayoutSecondaryQuantity.setVisibility(secondaryQuantityVisibility ? View.VISIBLE : View.GONE);


                                //Log.d("Token returned", "" + trimmer.getReturnedToken());
                            } else {

                                //Log.d("Token returned", "" + trimmer.getReturnedToken());

                                String text = "";

                                for (RecipeProduct recipeProduct : compareRecipeAgainstDB(productsListFromDB, trimmer.getRecipeProductArrayList())) {
                                    text = text.concat(recipeProduct.getRecipeProductName() + ", ");
                                }

                                Log.d("Recipe List", "Display: " + text);
                                recipeProducts.setText(text);
                                relativeLayoutHideableSection.setBackground(getDrawable(R.drawable.radius_recipe_top));
                                unhideRecipeSection();
                            }

                        } else {
                            Toast.makeText(ScanProduct.this, "QR Barcode not recognized.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            //method that compares recipe against database in order to find out outstanding products that need to be buy in order to prepare recipe

            private ArrayList<RecipeProduct> compareRecipeAgainstDB(List<Product> productsList, ArrayList<RecipeProduct> recipeProductArrayList) {

                for (int i = 0; i < productsList.size(); i++) {
                    Product productFromDB = productsList.get(i);
                    for (int j = 0; j < recipeProductArrayList.size(); j++) {
                        RecipeProduct recipeProduct = recipeProductArrayList.get(j);

                        if (productFromDB.getProductId() == recipeProduct.getRecipeProductID() && productFromDB.getProductTotalQuantity() >= recipeProduct.getProductRecipeQuantity()) {

                            recipeProductArrayList.remove(recipeProduct);
                        }
                    }
                }
                return recipeProductArrayList;
            }
        });
    }

    private void hideRecipeSection() {

        if (String.valueOf(recipeProducts.getText()).isEmpty()) {


            relativeLayoutHideableSection.setBackgroundColor(getResources().getColor(R.color.vegan_secondary));
            recipeHeader.setVisibility(View.GONE);
            recipeProducts.setVisibility(View.GONE);

        }
    }

    private void unhideRecipeSection() {

        if (!String.valueOf(recipeProducts.getText()).isEmpty()) {

            recipeHeader.setVisibility(View.VISIBLE);
            recipeProducts.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        enableCameraForApp();
    }

    //Dexter/Karumi library; user is prompted to "allow" up to use camera

    private void enableCameraForApp() {
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(ScanProduct.this, "Camera is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_editText:
                Calendar calendar = Calendar.getInstance();
                //calendar.set(2023, 8, 31);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        productExpiryDateExtracted = year + "-" + month + "-" + day;
                        dateEditText.setText(productExpiryDateExtracted);
                        //Log.d("Testing_date", "" + dayOfMonth + " " + monthOfYear + " " + year);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScanProduct.this, R.style.calendar_theme, onDateSetListener, year, month, day);
                datePickerDialog.show();
                //datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                break;

            case R.id.scan_product_view:
                codeScanner.startPreview();
                relativeLayoutSecondaryQuantity.setVisibility(View.GONE);
                break;

            case R.id.cancel_scan_button:
                clearProductFields();
                codeScanner.startPreview();
                relativeLayoutSecondaryQuantity.setVisibility(View.GONE);
                break;

            case R.id.confirm_scan_button:

                //Log.d("Testing_product", productExtracted);
                //Log.d("Testing_weight", String.valueOf(productWeightExtracted));
                //Log.d("Testing_quantity", String.valueOf(quantityEditText.getText()));
                //Log.d("Testing_date", productExpiryDateExtracted);

                //below if statement tests validity of scanned qr barcode

                if (!String.valueOf(quantityEditText.getText()).isEmpty() && !String.valueOf(dateEditText.getText()).isEmpty() && !String.valueOf(secondaryQuantityEditText.getText()).isEmpty()) {

                    if (Integer.parseInt(quantityEditText.getText().toString()) != 0 || Integer.parseInt(secondaryQuantityEditText.getText().toString()) != 0) {
                        saveProductToDatabase(v);
                        Toast.makeText(ScanProduct.this, productExtracted + " exported to DB.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScanProduct.this, "Quantity cannot be 0, please amend.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScanProduct.this, "One or more of the fields is blank, please amend.", Toast.LENGTH_SHORT).show();
                }
                relativeLayoutSecondaryQuantity.setVisibility(View.GONE);

                String adjustedRecipeProductsString = recipeProductsTrimmer.trimmedRecipeString(String.valueOf(recipeProducts.getText()), String.valueOf(productTextView.getText()));

                recipeProducts.setText(adjustedRecipeProductsString);
                //Log.d("testing recipes", " " + recipeProducts.getText());
                clearProductFields();
                codeScanner.startPreview();

                break;
        }
    }

    private void clearProductFields() {
        productTextView.setText(null);
        weightTextView.setText(null);
        quantityEditText.setText(null);
        dateEditText.setText(null);
        secondaryQuantityEditText.setText(null);
    }

    //saving product to database; data is taken from TextViews and EditText fields and fed into SQLite database

    private void saveProductToDatabase(View view) {

        boolean productInDatabaseAlready = false;
        int quantityInDatabaseAlready = 0;
        String expiryDateInDatabaseAlready = "";
        int secondaryQuantityInDatabaseAlready = 0;
        int totalQuantityInDatabaseAlready = 0;

        int newProductId = trimmer.getId();
        String newProduct = productExtracted;
        int newProductWeight = Integer.parseInt(String.valueOf(productWeightExtracted));
        int newProductQuantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        String newProductExpiryDate = trimmer.addLeadingZeros(productExpiryDateExtracted.trim());
        int newProductSecondaryQuantity = Integer.parseInt(secondaryQuantityEditText.getText().toString().trim());
        int newProductTotalQuantity = calculateTotalQuantity(newProductWeight, newProductQuantity, newProductSecondaryQuantity);

    /*    Log.d("Testing_id", String.valueOf(newProductId));
        Log.d("Testing_product", newProduct);
        Log.d("Testing_weight", String.valueOf(newProductWeight));
        Log.d("Testing_quantity", String.valueOf(newProductQuantity));
        Log.d("Testing_leading", newProductExpiryDate);*/

        //int id, String productName, int productWeight, int quantity, String date
        Product productAddedtoDB = new Product(newProductId, newProduct, newProductWeight, newProductQuantity, newProductExpiryDate, newProductSecondaryQuantity, newProductTotalQuantity);

        for (Product product : productsListFromDB) {
            if (product.getProductId() == newProductId) {
                productInDatabaseAlready = true;
                quantityInDatabaseAlready = product.getProductQuantity();
                expiryDateInDatabaseAlready = product.getProductExpiryDate();
                secondaryQuantityInDatabaseAlready = product.getProductSecondaryQuantity();
                totalQuantityInDatabaseAlready = product.getProductTotalQuantity();
                break;
            }
        }

        if (!productInDatabaseAlready) {

            dataBaseHandler.addProduct(new Product(newProductId, newProduct, newProductWeight, newProductQuantity, newProductExpiryDate, newProductSecondaryQuantity, newProductTotalQuantity));

          /*  dataBaseHandler.addProduct(new Product(43214130, "Brioche Burger Buns", 280, 4, "2020-12-31", 4, 16));
            dataBaseHandler.addProduct(new Product(79501066, "Chickpeas", 400, 1, "2020-12-31", 1, 400));
            dataBaseHandler.addProduct(new Product(24998702, "Red Onions", 1000, 1, "2020-12-31", 8, 8));
            dataBaseHandler.addProduct(new Product(94127594, "Flat Leaf Parsley",	25,	1, "2020-12-31", 1, 25));
            dataBaseHandler.addProduct(new Product(89857232, "Ground Cumin",	50,	1, "2020-12-31", 1, 50));
            dataBaseHandler.addProduct(new Product(44055656, "Ground  Coriander", 50, 1, "2020-12-31", 1, 50));
            dataBaseHandler.addProduct(new Product(93288194, "Chilli Powder", 50, 1, "2020-12-31", 1, 50));
            dataBaseHandler.addProduct(new Product(52584040, "Plain Flour", 2000, 1, "2020-12-31", 1, 2000));
            dataBaseHandler.addProduct(new Product(45881892, "Sunflower Oil", 1000,1, "2020-12-31", 1, 1000));
            dataBaseHandler.addProduct(new Product(63973771, "Tomatoes", 500, 1, "2020-12-31", 10, 10));
            dataBaseHandler.addProduct(new Product(34783163, "Spinach", 500, 1, "2020-12-31", 1, 500));

            dataBaseHandler.addProduct(new Product(52712318, "Red Peppers", 500, 1, "2020-12-27", 3, 3));
            dataBaseHandler.addProduct(new Product(68051427, "Organic Eggs", 350, 1, "2020-12-31", 6, 6));
            dataBaseHandler.addProduct(new Product(28156909, "Mushrooms", 200, 1, "2020-12-31", 1, 200));*/


            //Log.d("Retrieve product", + newProductId + " " + newProduct + " " + newProductWeight + " " + newProductQuantity + " " + newProductExpiryDate);
        } else {
            if (newProductSecondaryQuantity == 1) {
                dataBaseHandler.updateProduct(new Product(newProductId, newProduct, newProductWeight, (newProductQuantity + quantityInDatabaseAlready), expiryDateInDatabaseAlready, newProductSecondaryQuantity, (newProductTotalQuantity + totalQuantityInDatabaseAlready)));

            } else {
                dataBaseHandler.updateProduct(new Product(newProductId, newProduct, newProductWeight, (newProductQuantity + quantityInDatabaseAlready), expiryDateInDatabaseAlready, (newProductSecondaryQuantity + secondaryQuantityInDatabaseAlready), (newProductTotalQuantity + totalQuantityInDatabaseAlready)));
            }
        }

        dataBaseHandler.close();
    }

    private int calculateTotalQuantity(int newProductWeight, int newProductQuantity, int newProductSecondaryQuantity) {

        int total = 0;

        if (newProductSecondaryQuantity == 1)
            total = total + (newProductWeight * newProductQuantity);
        else
            total = total + newProductSecondaryQuantity;

        return total;
    }
}