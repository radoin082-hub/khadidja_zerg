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

public class MainActivitySpecialty extends AppCompatActivity {
    private ArrayList<Specialty> items;

    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_specialty);
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
        int idDepartment = getIntent().getIntExtra("Id_Department", -1);
        if (idDepartment == -1)
            Toast.makeText(MainActivitySpecialty.this, "i don't Have The Id Check Your API", Toast.LENGTH_SHORT).show();
        else {
            requestQueue.add(new JsonArrayRequest(Request.Method.GET
                    , "https://num.univ-biskra.dz/psp/pspapi" + "/specialty?department=" + idDepartment + "&semester=2" + "&key=appmob"
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
                Specialty item = new Specialty(jsonObject.getInt("id_specialty"), jsonObject.getString("Nom_spec"), jsonObject.getString("name_spec_ar"));
                items.add(item);

                Button button = SimilarParts.createButtonForLayout(this, getResources());
                ((LinearLayout) findViewById(R.id.layout)).addView(button);
                button.setOnClickListener(v -> goToLevel(button));    // Set click listener
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
        Toast.makeText(MainActivitySpecialty.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void switchLanguageMethod(View view) {
        SimilarParts.switchLanguage();
        displayLanguage(findViewById(R.id.Language));
    }

    public void goToLevel(Button clickedButton) {
        if (items.isEmpty()) {  ///There Is No Faculty (Problem in json)
            Toast.makeText(MainActivitySpecialty.this, "The List Is Empty You Cant Go There Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivitySpecialty.this, MainActivityLevel.class);
        for (int i = 0; i < buttons.length; i++)
            if (clickedButton == buttons[i]) {
                SimilarParts.setIdSpec(items.get(i).getId());
                intent.putExtra("Id_Specialty", items.get(i).getId());
                clickedButton.getContext().startActivity(intent);
                return;
            }
    }

    public void displayLanguage(Button languageButton) {
        if (items.isEmpty()) {  ///There Is No Department (Problem in json)
            Toast.makeText(MainActivitySpecialty.this, "The List Is Empty Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SimilarParts.getLanguageStatus()) {
            ///Display French Language
            if (languageButton != null)
                languageButton.setText(R.string.French);
            for (int i = 0; i < buttons.length; i++)
                buttons[i].setText(items.get(i).getNameFr());
        } else {
            ///Display Arabic Language
            if (languageButton != null)
                languageButton.setText(R.string.Arabic);
            for (int i = 0; i < buttons.length; i++)
                buttons[i].setText(items.get(i).getNameAr());
        }
    }

    class Specialty {

        private int id;

        private String nameSpecialtyFr;

        private String nameSpecialtyAr;

        Specialty(int id, String nameSpecialtyFr, String nameSpecialtyAr) {
            this.id = id;
            this.nameSpecialtyFr = nameSpecialtyFr;
            this.nameSpecialtyAr = nameSpecialtyAr;
        }

        public int getId() {
            return id;
        }

        public String getNameFr() {
            return nameSpecialtyFr;
        }

        public String getNameAr() {
            return nameSpecialtyAr;
        }

    }

}