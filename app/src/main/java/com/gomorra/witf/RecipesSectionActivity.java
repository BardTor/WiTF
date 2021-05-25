package com.gomorra.witf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.gomorra.witf.data.DataBaseHandler;
import com.gomorra.witf.model.JsonRecipeDataHolder;
import com.gomorra.witf.model.Product;
import com.gomorra.witf.ui.RecyclerViewAdapter;
import com.gomorra.witf.util.Contstants;
import com.gomorra.witf.util.MySingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipesSectionActivity extends AppCompatActivity {

    Bundle bundle;

    RequestQueue queue;

    ArrayList<JSONObject> availableProductsList;
    ArrayList<JSONObject> unavailableProductsList;
    private ArrayList<String> recipesFromSharedPreferences = null;

    int counter;

    final List<JsonRecipeDataHolder> jsonRecipeDataHolderArrayList = new ArrayList<>();

    private DataBaseHandler dataBaseHandler;
    private List<Product> productList;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_section);

        bundle = getIntent().getExtras();
        String urlPiece = bundle.getString("RecipeActivityToken");

        //Log.d("Testing intent", urlPiece);

        queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), jsonRecipeDataHolderArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);


        dataBaseHandler = new DataBaseHandler(this);
        productList = new ArrayList<>();

        productList = dataBaseHandler.getAllProducts();

     /*   for (Product product : productList) {
            Log.d("Product :", "ID " + product.getProductId());
        }*/

        if (!urlPiece.equals("offline")) {

            buildRecyclerViewFromJson(urlPiece);

            //below else statement builds JSONObjects from ArrayList<String> in order to create RecyclerView from offline products (favourites saved to Shared Preferences)

        } else {

            SharedPreferences sharedPreferences = getSharedPreferences(Contstants.SHARED_PREFERENCES, MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(Contstants.RECIPES_SHARED_PREFERENCES, null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            recipesFromSharedPreferences = gson.fromJson(json, type);

            //Log.d("ID :", "ID: " + recipesFromSharedPreferences.size());

            if (recipesFromSharedPreferences == null) {
                recipesFromSharedPreferences = new ArrayList<>();
            }

            Log.d("Size", "offline" + recipesFromSharedPreferences.size());

            for (String stringObject : recipesFromSharedPreferences) {


                try {
                    JSONObject jsonObjectFromString = new JSONObject(stringObject);
                    compareLists(productList, jsonObjectFromString);
                    jsonRecipeDataHolderArrayList.add(new JsonRecipeDataHolder(availableProductsList, unavailableProductsList, jsonObjectFromString));

/*                   Log.d("ID :", "ID: " + jsonObject.getInt("id"));
                   Log.d("recipeName :", "Name: " + jsonObject.getString("recipeName"));
                   Log.d("cookingTime:", "Cooking Time: " + jsonObject.getInt("cookingTime"));
                   Log.d("shortDescription:", "Short Description: " + jsonObject.getString("shortDescription"));

                   Log.d("productsNeeded:", "Products Needed: " + jsonObject.getJSONArray("productsNeeded").length());*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                exportArrayListToRV(jsonRecipeDataHolderArrayList);
            }
        }
    }

    //Volley retrieves JSON document (db.json); throughout the course of iteration recipes are checked for legibility

    private void buildRecyclerViewFromJson(String urlPiece) {

        // !!!IMPORTANT!!! => Heroku server sleeps for few hours every day, please try to access url before starting the app to wake it up and see if it is on

        //HEROKU => "https://witf-project.herokuapp.com/"
        //LOCAL => "http://10.0.2.2:3000/"

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://witf-project.herokuapp.com/" + urlPiece, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Log.d("JSONARRAY:", "onResponse: " + response);

                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject;
                    counter = 0;

                    try {
                        jsonObject = response.getJSONObject(i);

                        //Log.d("JSON", "" + jsonObject.getString("recipeName"));

                        int counterReturned = compareLists(productList, jsonObject);


                        if (isRecipeLegit(counterReturned, jsonObject.getJSONArray("productsNeeded").length())) {

                            //jsonRecipeDataHolderArrayList.add(new JsonRecipeDataHolder(jsonObject.getInt("id"), jsonObject.getString("recipeName"), availableProductsList, unavailableProductsList, jsonObject.getString("recipeMethod")));

                            jsonRecipeDataHolderArrayList.add(new JsonRecipeDataHolder(availableProductsList, unavailableProductsList, jsonObject));
            /*            Log.d("ID :", "ID: " + jsonObject.getInt("id"));
                        Log.d("recipeName :", "Name: " + jsonObject.getString("recipeName"));
                        Log.d("cookingTime:", "Cooking Time: " + jsonObject.getInt("cookingTime"));
                        Log.d("shortDescription:", "Short Description: " + jsonObject.getString("shortDescription"));
                        Log.d("Available Products :", "AP: " + availableProductsList.size());

                        Log.d("recipeMethod:", "Recipe Method: " + jsonObject.getString("recipeMethod"));*/

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    exportArrayListToRV(jsonRecipeDataHolderArrayList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecipesSectionActivity.this, "It would seem that you are offline :-)", Toast.LENGTH_SHORT).show();
                Log.d("ERROR_ARRAY", "onErrorResponse: " + error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }

    //List of legible recipes is fed into recycler view (essentially these are displayed)
    public void exportArrayListToRV(List<JsonRecipeDataHolder> jsonRecipeDataHolderArrayList) {


        //Log.d("TEST", "SIZE ONLY A after exec " + jsonRecipeDataHolderArrayList.get(0).toString());
        //Log.d("TEST", "SIZE ONLY U after exec " + jsonRecipeDataHolderArrayList.get(0).unavailableProductsList.size());

        if (jsonRecipeDataHolderArrayList.size() != 0) {

            recyclerViewAdapter = new RecyclerViewAdapter(this, jsonRecipeDataHolderArrayList);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }


    private boolean isRecipeLegit(int counterReturned, int innerArraySize) {


        if ((double) counterReturned / (double) innerArraySize * 100.0 >= 50.0)
            return true;
        else
            return false;

    }

    //below method compares DB contents with a particualr JSONObject (Recipe), if >=50% of ingredients are available (see is RecipeLegit, above), recipe will be added.

    private int compareLists(List<Product> productList, JSONObject jsonObject) throws JSONException {
        //one array and JSONobject => one to add available items, one to add missing items

        //Log.d("JSON INSIDE", "" + jsonObject.getJSONArray("productsNeeded").toString());

        availableProductsList = new ArrayList<>();
        unavailableProductsList = new ArrayList<>();

        for (int k = 0; k < jsonObject.getJSONArray("productsNeeded").length(); k++) {
            unavailableProductsList.add(jsonObject.getJSONArray("productsNeeded").getJSONObject(k));
        }

        //Log.d("AV :", "UL Size" + unavailableProductsList.size());

        for (int i = 0; i < productList.size(); i++) {

            for (int j = 0; j < jsonObject.getJSONArray("productsNeeded").length(); j++) {

                JSONObject jsonObjectInner = jsonObject.getJSONArray("productsNeeded").getJSONObject(j);

                //Log.d("Unavailable Products :", "Adding " + jsonObjectInner.getJSONArray("productsNeeded").getJSONObject(j));


                if (productList.get(i).getProductId() == jsonObjectInner.getInt("productId")) {

                    if (productList.get(i).getProductSecondaryQuantity() == 1
                            && productList.get(i).getProductTotalQuantity() >= jsonObjectInner.getInt("productWeight")) {
                        counter++;
                        availableProductsList.add(jsonObjectInner);
                        unavailableProductsList.remove(jsonObjectInner);
                    } else if (productList.get(i).getProductSecondaryQuantity() != 1
                            && productList.get(i).getProductTotalQuantity() >= jsonObjectInner.getInt("productQuantity")) {
                        counter++;
                        availableProductsList.add(jsonObjectInner);
                        unavailableProductsList.remove(jsonObjectInner);
                    } else {

                    }
                }



          /*      if (productList.get(i).getProductId() == jsonObjectInner.getInt("productId") && productList.get(i).getProductWeight() >= jsonObjectInner.getInt("productWeight")) {

                    counter++;
                    availableProductsList.add(jsonObjectInner);
                    unavailableProductsList.remove(jsonObjectInner);
                }*/
            }
        }

     /*   Log.d("AV :", "" + availableProductsList.size());
        Log.d("UNA :", "" + unavailableProductsList.size());*/

        return counter;
    }
}