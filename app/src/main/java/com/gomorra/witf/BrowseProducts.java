package com.gomorra.witf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.gomorra.witf.data.DataBaseHandler;
import com.gomorra.witf.model.Product;
import com.gomorra.witf.ui.RecyclerViewAdapterBP;

import java.util.ArrayList;
import java.util.List;

//this class takes care of retrieving products from database and feeding them to RecyclerView of ViewHolders(individual UI representation of products)

public class BrowseProducts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapterBP recyclerViewAdapterBP;
    private List<Product> productList;
    private DataBaseHandler dataBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_products);

        recyclerView = findViewById(R.id.recyclerview);
        dataBaseHandler = new DataBaseHandler(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        productList = dataBaseHandler.getAllProducts();

       for (Product product : productList) {
            Log.d("BrowseProductsTesting", "Product name: " + product.getProductName());
            Log.d("BrowseProductsTesting", "Product ID: " + product.getProductId());
            Log.d("BrowseProductsTesting", "Product Primary: " + product.getProductQuantity());
            Log.d("BrowseProductsTesting", "Product Secondary: " + product.getProductSecondaryQuantity());
            Log.d("BrowseProductsTesting", "Product Total: " + product.getProductTotalQuantity());
        }

        recyclerViewAdapterBP = new RecyclerViewAdapterBP(this, productList);
        recyclerView.setAdapter(recyclerViewAdapterBP);
        //method informs adapter to changes in product values (quantity, expiry date)
        recyclerViewAdapterBP.notifyDataSetChanged();
    }
}