package com.calcexample.testjson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.ArrayList;

public class MainActivityLevel extends AppCompatActivity {
    private ArrayList<Level> items;

    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_level);
        initializeAttributes();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayLanguage(findViewById(R.id.Language));
        Log.d("OnRestart", "Back To Main Activity Specialty");
    }

    private void initializeAttributes() {
        items = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ///Create Request Of JSon And Put It In requestQueue
        int idSpecialty = getIntent().getIntExtra("Id_Specialty", -1);
        if (idSpecialty == -1)
            Toast.makeText(MainActivityLevel.this, "i don't Have The Id Check Your API", Toast.LENGTH_SHORT).show();
        else {
            requestQueue.add(new JsonArrayRequest(Request.Method.GET
                    , "https://num.univ-biskra.dz/psp/pspapi" + "/level?specialty=" + idSpecialty + "&semester=2" + "&key=appmob"
                    , null
                    , this::handleJsonResponse
                    , this::handleJsonError));
        }
    }

    private void handleJsonResponse(JSONArray response) {
        ///Initial The buttons Table With The size of response
        buttons = new Button[response.length()];
        LinearLayout layout = findViewById(R.id.layout);
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                Level item = new Level(jsonObject.getInt("id_niv_spec"), jsonObject.getString("id_niveau"));
                items.add(item);

                Button button = SimilarParts.createButtonForLayout(this, getResources());
                ((LinearLayout) findViewById(R.id.layout)).addView(button);
                button.setOnClickListener(v -> goToSection(button));    // Set click listener
                buttons[i] = button;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        displayLanguage(findViewById(R.id.Language));
    }

    private void handleJsonError(VolleyError error) {
        Log.d("JsonResponse", "error:" + error.toString());
        if (error instanceof NetworkError && error.networkResponse != null) {
            Log.d("Network Error", "Status code" + error.networkResponse.statusCode);
        }
        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown Error";
        Toast.makeText(MainActivityLevel.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void switchLanguageMethod(View view) {
        SimilarParts.switchLanguage();
        displayLanguage(findViewById(R.id.Language));
    }

    public void goToSection(Button clickedButton) {
        if (items.isEmpty()) {  ///There Is No Faculty (Problem in json)
            Toast.makeText(MainActivityLevel.this, "The List Is Empty You Cant Go There Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivityLevel.this, MainActivitySection.class);
        for (int i = 0; i < buttons.length; i++)
            if (clickedButton == buttons[i]) {
                SimilarParts.setIdNiveau(Integer.parseInt(items.get(i).getLevel()));
                intent.putExtra("Id_Niv_Spec", items.get(i).getId());
                clickedButton.getContext().startActivity(intent);
                return;
            }
    }

    public void displayLanguage(Button languageButton) {
        if (items.isEmpty()) {  ///There Is No Department (Problem in json)
            Toast.makeText(MainActivityLevel.this, "The List Is Empty Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SimilarParts.getLanguageStatus()) {
            if (languageButton != null)
                languageButton.setText(R.string.French);

        } else {
            if (languageButton != null)
                languageButton.setText(R.string.Arabic);
        }
        for (int i = 0; i < buttons.length; i++)
            buttons[i].setText(items.get(i).getLevel());
    }

    class Level {

        private int id;

        private String level;

        Level(int id, String level) {
            this.id = id;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public String getLevel() {
            return level;
        }

    }

}