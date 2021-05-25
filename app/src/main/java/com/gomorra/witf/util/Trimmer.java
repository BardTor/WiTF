package com.gomorra.witf.util;

import android.util.Log;

import com.gomorra.witf.model.RecipeProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//trimmer receives decoded QR Code in form of a string, splits and returns to TextViews/EditText fields of ScanProduct activity UI

public class Trimmer {

    private boolean verifier;
    private int productToken = 19222106;
    private int recipeToken = 60122291;
    private int returnedToken;

    private int id;

    private String productName;
    private int weight;
    private int quantity;

    private String date;
    public int day;
    private int month;
    private int year;

    private int secondaryQuantity;

    private boolean needsVisibility;

    private String recipeName;

    private List<String> listOfItemCharacteristics;

    private ArrayList<RecipeProduct> recipeProductArrayList;

    public Trimmer() {
        //trimQRString(qrCodeString);
    }

    public void trimQRString(String qrCodeString) {
        //19222106,20004132,Chopped Tomatoes,400,1,31082023,-1
        String[] arrayOfItemCharacteristics = qrCodeString.split(",");
        listOfItemCharacteristics = new ArrayList<String>();
        listOfItemCharacteristics = Arrays.asList(arrayOfItemCharacteristics);

        if (Integer.parseInt(listOfItemCharacteristics.get(0)) == productToken) {
            verifier = true;
            this.id = Integer.parseInt(listOfItemCharacteristics.get(1));
            this.productName = listOfItemCharacteristics.get(2);
            this.weight = Integer.parseInt(listOfItemCharacteristics.get(3));
            this.quantity = Integer.parseInt(listOfItemCharacteristics.get(4));
            this.date = listOfItemCharacteristics.get(5);
            this.secondaryQuantity = Integer.parseInt(listOfItemCharacteristics.get(6));
            this.needsVisibility = Boolean.parseBoolean(listOfItemCharacteristics.get(7));

        } else if (Integer.parseInt(listOfItemCharacteristics.get(0)) == recipeToken) {
            recipeProductArrayList = new ArrayList<>();
            //60122291,Sticky Beef Stir Fry,49183190,Stir Fry Beef,1,1,400
            verifier = true;
            this.recipeName = listOfItemCharacteristics.get(1);
            for (int i = 2; i < listOfItemCharacteristics.size(); i = i + 5) {

                int id = Integer.parseInt(listOfItemCharacteristics.get(i));
                String name = (listOfItemCharacteristics.get(i + 1));
                int primaryQuantity = Integer.parseInt(listOfItemCharacteristics.get(i + 2));
                int secondaryQuantity = Integer.parseInt(listOfItemCharacteristics.get(i + 3));
                int recipeQuantity = Integer.parseInt(listOfItemCharacteristics.get(i + 4));

                recipeProductArrayList.add(new RecipeProduct(id, name, primaryQuantity, secondaryQuantity, recipeQuantity));
            }

        } else {
            verifier = false;
        }


    }

    public boolean isVerifier() {
        setReturnedToken(Integer.parseInt(listOfItemCharacteristics.get(0)));
        return verifier;

    }

    public void setVerifier(boolean verifier) {
        this.verifier = verifier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getProductToken() {
        return productToken;
    }

    public void setProductToken(int productToken) {
        this.productToken = productToken;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void dateSplitter() {
        this.day = Integer.parseInt(date.substring(6, 8));
        this.month = Integer.parseInt(date.substring(4, 6));
        this.year = Integer.parseInt(date.substring(0, 4));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public int getSecondaryQuantity() {
        return secondaryQuantity;
    }

    public void setSecondaryQuantity(int secondaryQuantity) {
        this.secondaryQuantity = secondaryQuantity;
    }

    public boolean isNeedsVisibility() {
        return needsVisibility;
    }

    public void setNeedsVisibility(boolean needsVisibility) {
        this.needsVisibility = needsVisibility;
    }

    public String addLeadingZeros(String productExpiryDateExtracted) {
        //2020-8-1
        String[] date = productExpiryDateExtracted.split("-");
        List<String> dateList = new ArrayList<String>();
        dateList = Arrays.asList(date);

        if (Integer.parseInt(dateList.get(1)) < 10) {
            dateList.set(1, "0" + dateList.get(1));
        }

        if (Integer.parseInt(dateList.get(2)) < 10) {
            dateList.set(2, "0" + dateList.get(2));
        }

        return dateList.get(0) + "-" + dateList.get(1) + "-" + dateList.get(2);

    }

    public int getRecipeToken() {
        return recipeToken;
    }

    public void setRecipeToken(int recipeToken) {
        this.recipeToken = recipeToken;
    }

    public int getReturnedToken() {
        return returnedToken;
    }

    public void setReturnedToken(int returnedToken) {
        this.returnedToken = returnedToken;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<String> getListOfItemCharacteristics() {
        return listOfItemCharacteristics;
    }

    public void setListOfItemCharacteristics(List<String> listOfItemCharacteristics) {
        this.listOfItemCharacteristics = listOfItemCharacteristics;
    }

    public ArrayList<RecipeProduct> getRecipeProductArrayList() {
        return recipeProductArrayList;
    }

    public void setRecipeProductArrayList(ArrayList<RecipeProduct> recipeProductArrayList) {
        this.recipeProductArrayList = recipeProductArrayList;
    }

    public String trimmedRecipeString(String recipeProducts, String productRemoved) {

        if (!recipeProducts.isEmpty() && !productRemoved.isEmpty() && recipeProducts.contains(productRemoved)) {

            recipeProducts = recipeProducts.replaceAll(productRemoved + ", ", "");
        }
        return recipeProducts;

    }
}
