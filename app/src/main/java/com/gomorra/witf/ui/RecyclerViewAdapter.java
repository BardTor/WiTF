package com.gomorra.witf.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gomorra.witf.MainActivity;
import com.gomorra.witf.R;
import com.gomorra.witf.data.DataBaseHandler;
import com.gomorra.witf.model.JsonRecipeDataHolder;
import com.gomorra.witf.model.Product;
import com.gomorra.witf.util.Contstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

//RecyclerView used to maintain ViewHolders of Recipes

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    private ArrayList<String> recipesFromSharedPreferences;

    private Context context;
    //(int id, String recipeName, ArrayList<JSONObject> availableProductsList, ArrayList<JSONObject> unavailableProductsList, String recipeMethod)
    private List<JsonRecipeDataHolder> jsonRecipeDataHolderArrayListRV;

    public RecyclerViewAdapter(Context context, List<JsonRecipeDataHolder> jsonRecipeDataHolderArrayList) {

        this.context = context;
        this.jsonRecipeDataHolderArrayListRV = jsonRecipeDataHolderArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        JsonRecipeDataHolder jsonRecipeDataHolderRV = jsonRecipeDataHolderArrayListRV.get(position);

        //Log.d("Recipe Name", jsonRecipeDataHolderRV.getRecipeName());
        //Log.d("Whole String", "toString" + jsonRecipeDataHolderRV.toString());

        //Log.d("AVAILABLE", "SIZE ONLY A after RV " + jsonRecipeDataHolderArrayListRV.get(0).availableProductsList.size());

        //holder.recipe.setText(MessageFormat.format("Recipe: {0}",jsonRecipeDataHolderRV.getRecipeName()));
        try {

            String availableProductsHTML = "<b>" + "Available Products:" + "<br>" + "</b>" + tidyAvailableUnavailableProductsLists(jsonRecipeDataHolderRV.getAvailableProductsList());
            String unavailableProductsHTML = "<b>" + "Unavailable Products:" + "<br>" + "</b>" + tidyAvailableUnavailableProductsLists(jsonRecipeDataHolderRV.getUnavailableProductsList());
            String recipeMethod = "<b>" + "Recipe Method:" + "<br>" + "</b>" + jsonRecipeDataHolderRV.getJsonObject().getString("recipeMethod");

            holder.recipe.setText(MessageFormat.format("Recipe: {0}", jsonRecipeDataHolderRV.getJsonObject().getString("recipeName")));
            holder.ingredientsAvailableUnavailable.setText(MessageFormat.format("Available Products: {0}", jsonRecipeDataHolderRV.getAvailableProductsList().size() + "/" + (jsonRecipeDataHolderRV.getAvailableProductsList().size() + jsonRecipeDataHolderRV.getUnavailableProductsList().size())));
            holder.prepTime.setText(MessageFormat.format("Prep Time: {0}", jsonRecipeDataHolderRV.getJsonObject().getInt("cookingTime")) + "min");
            holder.description.setText(MessageFormat.format("Description: {0}", jsonRecipeDataHolderRV.getJsonObject().getString("shortDescription")));


            holder.availableProductsTextView.setText(Html.fromHtml(availableProductsHTML));
            holder.unavailableProductsTextView.setText(Html.fromHtml(unavailableProductsHTML));
            holder.recipeMethod.setText(Html.fromHtml(recipeMethod));

            //holder.availableProductsTextView.setText(MessageFormat.format("Available Products: {0}",  "\n" + tidyAvailableUnavailableProductsLists(jsonRecipeDataHolderRV.getAvailableProductsList())));
            //holder.unavailableProductsTextView.setText(MessageFormat.format("Unavailable Products: {0}",  "\n" + tidyAvailableUnavailableProductsLists(jsonRecipeDataHolderRV.getUnavailableProductsList())));
            //holder.recipeMethod.setText(MessageFormat.format("Recipe Method: {0}", "\n" + jsonRecipeDataHolderRV.getJsonObject().getString("recipeMethod")));

            holder.unavailableProductsTextView.setVisibility(jsonRecipeDataHolderRV.getUnavailableProductsList().size() != 0 ? View.VISIBLE : View.GONE);

            //Log.d("Recipe Type", "" + jsonRecipeDataHolderRV.getJsonObject().getString("recipeType"));

            updateFoodTypeImage(holder, jsonRecipeDataHolderRV);

            boolean isExpanded = jsonRecipeDataHolderArrayListRV.get(position).isExpanded();
            holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //updating image depending on food type (e.g vegan)

    private void updateFoodTypeImage(@NonNull ViewHolder holder, JsonRecipeDataHolder jsonRecipeDataHolderRV) throws JSONException {
        if (jsonRecipeDataHolderRV.getJsonObject().getString("recipeType").equals("meat")) {
            holder.recipeTypeImageView.setImageResource(R.drawable.meat_icon);
        } else if (jsonRecipeDataHolderRV.getJsonObject().getString("recipeType").equals("vegetarian")) {
            holder.recipeTypeImageView.setImageResource(R.drawable.vegetarian_icon);
        } else if (jsonRecipeDataHolderRV.getJsonObject().getString("recipeType").equals("fish")) {
            holder.recipeTypeImageView.setImageResource(R.drawable.fish_icon);
        } else {
            holder.recipeTypeImageView.setImageResource(R.drawable.vegan_icon);
        }
    }


    @Override
    public int getItemCount() {
        return jsonRecipeDataHolderArrayListRV.size();
    }

    //below class assigns values to individual ViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //below relate to list_row

        public int id;
        public TextView recipe;
        public TextView ingredientsAvailableUnavailable;
        public TextView prepTime;
        public TextView description;
        public TextView availableProductsTextView;
        public TextView unavailableProductsTextView;
        public TextView recipeMethod;
        public RelativeLayout expandableLayout;
        public ImageButton viewRecipeButton;
        public ImageButton saveRecipeButton;
        public ImageButton cookRecipeButton;
        public ImageView recipeTypeImageView;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            recipe = itemView.findViewById(R.id.recipe_name);
            ingredientsAvailableUnavailable = itemView.findViewById(R.id.ingredients_available_unavailable);
            prepTime = itemView.findViewById(R.id.prep_time);
            description = itemView.findViewById(R.id.recipe_description);
            availableProductsTextView = itemView.findViewById(R.id.available_products);
            unavailableProductsTextView = itemView.findViewById(R.id.unavailable_products);
            viewRecipeButton = itemView.findViewById(R.id.view_button);
            saveRecipeButton = itemView.findViewById(R.id.save_button);
            cookRecipeButton = itemView.findViewById(R.id.cook_button);
            recipeMethod = itemView.findViewById(R.id.recipe_details);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            viewRecipeButton.setOnClickListener(this);
            saveRecipeButton.setOnClickListener(this);
            cookRecipeButton.setOnClickListener(this);
            recipeTypeImageView = itemView.findViewById(R.id.reipe_type_id);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            JsonRecipeDataHolder jsonRecipeDataHolder = jsonRecipeDataHolderArrayListRV.get(position);
            JSONObject jsonObjectToBeSaved = jsonRecipeDataHolder.getJsonObject();


            switch (v.getId()) {
                case R.id.view_button:
                    //viewRecipe(jsonRecipeDataHolder);
                    jsonRecipeDataHolder.setExpanded(!jsonRecipeDataHolder.isExpanded());
                    notifyItemChanged(position);
                    break;

                case R.id.save_button:
                    Log.d("Testing commit", "Save pressed");
                    saveRecipeToSharedPreferences(jsonObjectToBeSaved);
                    saveRecipeButton.setVisibility(View.GONE);
                    break;

                case R.id.cook_button:

                    confirmCommit();

                    break;
            }
        }

        private void confirmCommit() {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.recipe_commit_confirmation_popup, null);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            Button commitPopupButton = view.findViewById(R.id.confirmation_yes_button_ra);
            Button noPopupButton = view.findViewById(R.id.confirmation_no_button_ra);

            noPopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Testing commit", "No pressed");

                    dialog.dismiss();

                }
            });
            commitPopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Testing commit", "Yes Pressed");
                    updateDatabaseUponCommit();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);

                }
            });
        }

        //below class is responsible for amending quantities of products once product is committed (cooked by the user)

        private void updateDatabaseUponCommit() {

            int positionCommitted = getAdapterPosition();
            JsonRecipeDataHolder jsonRecipeDataHolderCommitted = jsonRecipeDataHolderArrayListRV.get(positionCommitted);

            DataBaseHandler db = new DataBaseHandler(context);
            List<Product> productsList = db.getAllProducts();
            //Product productAdjustedUponCommit = products.get

            for (int i = 0; i < productsList.size(); i++) {

                Product productCommitted = productsList.get(i);

                for (int j = 0; j < jsonRecipeDataHolderCommitted.availableProductsList.size(); j++) {

                    JSONObject jsonObjectCommitted = jsonRecipeDataHolderCommitted.availableProductsList.get(j);

                    try {
                        if (productCommitted.getProductId() == jsonObjectCommitted.getInt("productId")) {


                            if (productCommitted.getProductSecondaryQuantity() == 1) {


                                if (productCommitted.getProductTotalQuantity() - jsonObjectCommitted.getInt("productWeight") == 0 && productCommitted.getProductQuantity() == 1) {

                                    //Log.d("Deleting product ", productCommitted.getProductName());

                                    db.deleteProduct(productCommitted.getProductId());

                                } else if (productCommitted.getProductTotalQuantity() - jsonObjectCommitted.getInt("productWeight") > 0) {

                                    int tempTotalQuantity = productCommitted.getProductTotalQuantity();
                                    int tempQuantity = productCommitted.getProductQuantity();

                                    //Log.d("Initial", "Product Total Quantity1" + productCommitted.getProductName() + "-" + tempTotalQuantity);

                                    productCommitted.setProductTotalQuantity(tempTotalQuantity - jsonObjectCommitted.getInt("productWeight"));

                                    //Log.d("Changed", "Product Total Quantity2" + productCommitted.getProductName() + "-" + productCommitted.getProductTotalQuantity());

                                    if (productCommitted.getProductQuantity() > 1) {

                                        //Log.d("Initial", "Product Quantity3" + productCommitted.getProductName() + "-" + tempQuantity);

                                        if (productCommitted.getProductTotalQuantity() % productCommitted.getProductWeight() == 0) {

                                            productCommitted.setProductQuantity(productCommitted.getProductTotalQuantity() / productCommitted.getProductWeight());

                                        } else {

                                            productCommitted.setProductQuantity(productCommitted.getProductTotalQuantity() / productCommitted.getProductWeight() + 1);
                                            
                                        }

                                        //productCommitted.setProductQuantity(tempQuantity - (productCommitted.getProductTotalQuantity() / jsonObjectCommitted.getInt("productWeight")));

                                        /*Log.d("Changed", "tempQuantity" + tempQuantity);
                                        Log.d("Changed", "totalQuantity" + tempTotalQuantity);
                                        Log.d("Changed", "productWeight" + jsonObjectCommitted.getInt("productWeight"));*/
                                    }

                                    db.updateProduct(productCommitted);

                                } else {
                                }
                            }

                            if ((productCommitted.getProductSecondaryQuantity() != 1)) {

                                //non quantity constant items, e.g. onions or tomatoes in their packs
                                if (jsonObjectCommitted.getInt("productWeight") == -1) {

                                    //Log.d("Initial", "Product Total Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductTotalQuantity());

                                    productCommitted.setProductTotalQuantity(productCommitted.getProductTotalQuantity() - jsonObjectCommitted.getInt("productQuantity"));

                                    //Log.d("Changed", "Product Total Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductTotalQuantity());

                                }
                                //quantity constant items, e.g. brioches in a pack
                                else {

                                    //Log.d("Initial", "Product Total Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductTotalQuantity());
                                    //Log.d("Initial", "Product Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductQuantity());

                                    productCommitted.setProductTotalQuantity(productCommitted.getProductTotalQuantity() - jsonObjectCommitted.getInt("productQuantity"));
                                    productCommitted.setProductQuantity(productCommitted.getProductQuantity() - (jsonObjectCommitted.getInt("productWeight")) / productCommitted.getProductWeight());

                                    //Log.d("Changed", "Product Total Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductTotalQuantity());
                                    //Log.d("Changed", "Product Quantity" + productCommitted.getProductName() + "-" + productCommitted.getProductQuantity());
                                }

                                if (productCommitted.getProductTotalQuantity() == 0 || productCommitted.getProductWeight() == 0 || productCommitted.getProductQuantity() == 0) {
                                    db.deleteProduct(productCommitted.getProductId());

                                } else {
                                    db.updateProduct(productCommitted);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            db.close();
        }
    }

    //below class creates HTML breaks so Available/Unavailable products could be placed in normal font under Bold headers

    private String tidyAvailableUnavailableProductsLists(ArrayList<JSONObject> availableUnavailableproductList) throws JSONException {
        String tidiedList = new String();

        for (JSONObject jsonObject : availableUnavailableproductList) {
            if (jsonObject.getInt("productWeight") != -1) {
                if (jsonObject.getInt("productQuantity") != -1)
                    tidiedList = tidiedList.concat(jsonObject.getString("productName") + ": " + jsonObject.getInt("productWeight") + jsonObject.getString("productUnit") + "/" + jsonObject.getInt("productQuantity") + "<br>");
                else
                    tidiedList = tidiedList.concat(jsonObject.getString("productName") + ": " + jsonObject.getInt("productWeight") + jsonObject.getString("productUnit") + "<br>");
            } else {
                tidiedList = tidiedList.concat(jsonObject.getString("productName") + ": " + jsonObject.getInt("productQuantity") + "<br>");
            }
        }

        return tidiedList;
    }


    //------------------------------------------------------------------------------------------------

    // saving recipe to Shared Preferences using GSON library

    private void saveRecipeToSharedPreferences(JSONObject jsonObjectToBeSaved) {

/*        String test = jsonObjectToBeSaved.toString();
        Log.d("Test", test);

        try {
            JSONObject jsonObject = new JSONObject(test);
            Log.d("Array", "" + jsonObject.getJSONArray("productsNeeded"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        ArrayList<String> retrievedRecipesFromSharedPreferences = loadRecipesFromSharedPreferences();
        ArrayList<Integer> listOfIdsOnly = new ArrayList<>();

        Log.d("test", "" + retrievedRecipesFromSharedPreferences.size());

        for (String recipeInStringFormat : retrievedRecipesFromSharedPreferences) {

            try {
                JSONObject jsonObjectFromString = new JSONObject(recipeInStringFormat);
                listOfIdsOnly.add(jsonObjectFromString.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (retrievedRecipesFromSharedPreferences.isEmpty()) {
            try {
                Log.d("TESTING SAVE", "IF EMPTY " + jsonObjectToBeSaved.getInt("id"));

                retrievedRecipesFromSharedPreferences.add(jsonObjectToBeSaved.toString());

                SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(retrievedRecipesFromSharedPreferences);
                editor.putString(Contstants.RECIPES_SHARED_PREFERENCES, json);
                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                if (!listOfIdsOnly.contains(jsonObjectToBeSaved.getInt("id"))) {

                    retrievedRecipesFromSharedPreferences.add(jsonObjectToBeSaved.toString());
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(retrievedRecipesFromSharedPreferences);
                    editor.putString(Contstants.RECIPES_SHARED_PREFERENCES, json);
                    //Log.d("TESTING SAVE", "IF SP NOT EMPTY " + jsonObjectToBeSaved.getInt("id"));
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        //Log.d("SIZE", "" + retrievedRecipesFromSharedPreferences.size());

    }

    //retrieving recipes from Shared Preferences (from string back to JSONObjects)

    private ArrayList<String> loadRecipesFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Contstants.RECIPES_SHARED_PREFERENCES, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        recipesFromSharedPreferences = gson.fromJson(json, type);

        if (recipesFromSharedPreferences == null) {
            recipesFromSharedPreferences = new ArrayList<>();
        }

        return recipesFromSharedPreferences;
    }


    //------------------------------------------------------------------------------------------------

/*    private void saveRecipeToSharedPreferences(JSONObject jsonObjectToBeSaved) {

        ArrayList<JSONObject>retrievedRecipesFromSharedPreferences = loadRecipesFromSharedPreferences();
        ArrayList<Integer> listOfIdsOnly = new ArrayList<>();

        for (JSONObject jsonObject : retrievedRecipesFromSharedPreferences) {
            try {
                listOfIdsOnly.add(jsonObject.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (retrievedRecipesFromSharedPreferences.isEmpty()) {
            try {
                Log.d("TESTING SAVE", "IF EMPTY " + jsonObjectToBeSaved.getInt("id"));
                retrievedRecipesFromSharedPreferences.add(jsonObjectToBeSaved);

                SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(retrievedRecipesFromSharedPreferences);
                editor.putString(Contstants.RECIPES_SHARED_PREFERENCES, json);
                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        else {
            try {
                if (!listOfIdsOnly.contains(jsonObjectToBeSaved.getInt("id"))) {

                    retrievedRecipesFromSharedPreferences.add(jsonObjectToBeSaved);
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(retrievedRecipesFromSharedPreferences);
                    editor.putString(Contstants.RECIPES_SHARED_PREFERENCES, json);
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        Log.d("SIZE", "" + retrievedRecipesFromSharedPreferences.size());

    }

    private ArrayList<JSONObject> loadRecipesFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Contstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Contstants.RECIPES_SHARED_PREFERENCES, null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        recipesFromSharedPreferences = gson.fromJson(json, type);

        if (recipesFromSharedPreferences == null) {
            recipesFromSharedPreferences = new ArrayList<>();
        }

        return  recipesFromSharedPreferences;
    }*/
}

