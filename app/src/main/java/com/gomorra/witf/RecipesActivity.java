package com.gomorra.witf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gomorra.witf.util.Contstants;

//simple activity that directs user to whichever section he/she clicked on

public class RecipesActivity extends AppCompatActivity implements View.OnClickListener {


    String meatSectionURL;
    String veganSectionURL;
    String vegetarianSectionURL;
    String allSectionURL;
    String offlineSectionURL;


    Button meatButton;
    Button veganButton;
    Button vegetarianButton;
    Button allButton;
    Button offlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        meatSectionURL = "meat";
        veganSectionURL = "vegan";
        vegetarianSectionURL = "vegetarian";
        allSectionURL = "all";
        offlineSectionURL = "offline";

        meatButton = findViewById(R.id.meat_section_button_ar);
        veganButton = findViewById(R.id.vegan_section_button_ar);
        vegetarianButton = findViewById(R.id.vegetarian_section_button_ar);
        allButton = findViewById(R.id.all_section_button_ar);
        offlineButton = findViewById(R.id.offline_section_button_ar);

        meatButton.setOnClickListener(this);
        veganButton.setOnClickListener(this);
        vegetarianButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        offlineButton.setOnClickListener(this);


        //Log.d("Testing", "RecipesActivity");
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(RecipesActivity.this, RecipesSectionActivity.class);

        switch (v.getId()) {
            case R.id.meat_section_button_ar:

                intent.putExtra(Contstants.TOKEN, meatSectionURL);
                startActivity(intent);
                break;
            case R.id.vegan_section_button_ar:

                intent.putExtra(Contstants.TOKEN, veganSectionURL);
                startActivity(intent);
                break;
            case R.id.vegetarian_section_button_ar:

                intent.putExtra(Contstants.TOKEN, vegetarianSectionURL);
                startActivity(intent);
                break;
            case R.id.all_section_button_ar:

                intent.putExtra(Contstants.TOKEN, allSectionURL);
                startActivity(intent);
                break;

            case R.id.offline_section_button_ar:

                intent.putExtra(Contstants.TOKEN, offlineSectionURL);
                startActivity(intent);
                break;
        }

    }
}