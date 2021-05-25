package com.gomorra.witf.model;

import org.json.JSONObject;

import java.util.ArrayList;

public class JsonRecipeDataHolder {

    private int id;
    private String recipeName;
    public ArrayList<JSONObject> availableProductsList;
    public ArrayList<JSONObject> unavailableProductsList;
    private String recipeMethod;
    private boolean expanded;
    private JSONObject jsonObject;

    public JsonRecipeDataHolder(int id, String recipeName, ArrayList<JSONObject> availableProductsList, ArrayList<JSONObject> unavailableProductsList, String recipeMethod) {
        this.id = id;
        this.recipeName = recipeName;
        this.availableProductsList = availableProductsList;
        this.unavailableProductsList = unavailableProductsList;
        this.recipeMethod = recipeMethod;
        this.expanded = false;
    }

    public JsonRecipeDataHolder(ArrayList<JSONObject> availableProductsList, ArrayList<JSONObject> unavailableProductsList, JSONObject jsonObject) {
        this.availableProductsList = availableProductsList;
        this.unavailableProductsList = unavailableProductsList;
        this.jsonObject = jsonObject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public ArrayList<JSONObject> getAvailableProductsList() {
        return availableProductsList;
    }

    public void setAvailableProductsList(ArrayList<JSONObject> availableProductsList) {
        this.availableProductsList = availableProductsList;
    }

    public ArrayList<JSONObject> getUnavailableProductsList() {
        return unavailableProductsList;
    }

    public void setUnavailableProductsList(ArrayList<JSONObject> unavailableProductsList) {
        this.unavailableProductsList = unavailableProductsList;
    }

    public String getRecipeMethod() {
        return recipeMethod;
    }

    public void setRecipeMethod(String recipeMethod) {
        this.recipeMethod = recipeMethod;
    }

    @Override
    public String toString() {
        return "JsonRecipeDataHolder{" +
                "id=" + id +
                ", recipeName='" + recipeName + '\'' +
                ", availableProductsList=" + availableProductsList +
                ", unavailableProductsList=" + unavailableProductsList +
                ", recipeMethod='" + recipeMethod + '\'' +
                '}';
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
