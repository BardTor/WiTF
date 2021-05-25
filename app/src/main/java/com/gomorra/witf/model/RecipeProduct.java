package com.gomorra.witf.model;

public class RecipeProduct {

    private int recipeProductID;
    private String recipeProductName;
    private int productPrimaryQuantity;
    private int productSecondaryQuantity;
    private int productRecipeQuantity;

    public RecipeProduct(int recipeProductID, String recipeProductName, int productPrimaryQuantity, int productSecondaryQuantity, int productRecipeQuantity) {
        this.recipeProductID = recipeProductID;
        this.recipeProductName = recipeProductName;
        this.productPrimaryQuantity = productPrimaryQuantity;
        this.productSecondaryQuantity = productSecondaryQuantity;
        this.productRecipeQuantity = productRecipeQuantity;
    }

    public int getRecipeProductID() {
        return recipeProductID;
    }

    public void setRecipeProductID(int recipeProductID) {
        this.recipeProductID = recipeProductID;
    }

    public String getRecipeProductName() {
        return recipeProductName;
    }

    public void setRecipeProductName(String recipeProductName) {
        this.recipeProductName = recipeProductName;
    }

    public int getProductPrimaryQuantity() {
        return productPrimaryQuantity;
    }

    public void setProductPrimaryQuantity(int productPrimaryQuantity) {
        this.productPrimaryQuantity = productPrimaryQuantity;
    }

    public int getProductSecondaryQuantity() {
        return productSecondaryQuantity;
    }

    public void setProductSecondaryQuantity(int productSecondaryQuantity) {
        this.productSecondaryQuantity = productSecondaryQuantity;
    }

    public int getProductRecipeQuantity() {
        return productRecipeQuantity;
    }

    public void setProductRecipeQuantity(int productRecipeQuantity) {
        this.productRecipeQuantity = productRecipeQuantity;
    }
}