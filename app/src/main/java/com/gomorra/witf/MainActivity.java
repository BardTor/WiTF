package com.gomorra.witf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObservable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button scanProductButton;
    Button browseProductsButton;
    Button browseRecipesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanProductButton = findViewById(R.id.scan_product_button);
        browseProductsButton = findViewById(R.id.browse_products_button);
        browseRecipesButton = findViewById(R.id.seek_recipes_button);
        scanProductButton.setOnClickListener(this);
        browseProductsButton.setOnClickListener(this);
        browseRecipesButton.setOnClickListener(this);

    }

    // basic onClick switch statement, whichever button is clicked, user is "sent" to new activity"

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_product_button:

                startActivity(new Intent(MainActivity.this, ScanProduct.class));
                break;

            case R.id.browse_products_button:

                startActivity(new Intent(MainActivity.this, BrowseProducts.class));
                break;

            case R.id.seek_recipes_button:

                startActivity(new Intent(MainActivity.this, RecipesActivity.class));
                break;
        }
    }
}