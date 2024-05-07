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

public class MainActivitySection extends AppCompatActivity {
    private ArrayList<Section> items;

    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_section);
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
        int idNeveu = getIntent().getIntExtra("Id_Niv_Spec", -1);
        if (idNeveu == -1)
            Toast.makeText(MainActivitySection.this, "i don't Have The Id Check Your API", Toast.LENGTH_SHORT).show();
        else {
            requestQueue.add(new JsonArrayRequest(Request.Method.GET
                    , "https://num.univ-biskra.dz/psp/pspapi" + "/section?level_specialty=" + idNeveu + "&semester=2" + "&key=appmob"
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
                Section item = new Section(jsonObject.getInt("sectionn_id"), "id = " + jsonObject.getInt("sectionn_id") + " " + jsonObject.getString("Abrev_fr"), "id = " + jsonObject.getInt("sectionn_id") + " " + jsonObject.getString("Abrev_ar"));
                items.add(item);

                Button button = SimilarParts.createButtonForLayout(this, getResources());
                ((LinearLayout) findViewById(R.id.layout)).addView(button);
                button.setOnClickListener(v -> goToTimeTable(button));    // Set click listener
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
        Toast.makeText(MainActivitySection.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void switchLanguageMethod(View view) {
        SimilarParts.switchLanguage();
        displayLanguage(findViewById(R.id.Language));
    }

    public void displayLanguage(Button languageButton) {
        if (items.isEmpty()) {  ///There Is No Department (Problem in json)
            Toast.makeText(MainActivitySection.this, "The List Is Empty Check Your API", Toast.LENGTH_SHORT).show();
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

    public void goToTimeTable(Button clickedButton) {
        if (items.isEmpty()) {  ///There Is No Faculty (Problem in json)
            Toast.makeText(MainActivitySection.this, "The List Is Empty You Cant Go There Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivitySection.this, MainActivityTabTime.class);
        for (int i = 0; i < buttons.length; i++)
            if (clickedButton == buttons[i]) {
                SimilarParts.setIdSection(items.get(i).getId());
                intent.putExtra("Id_Section", items.get(i).getId());
                clickedButton.getContext().startActivity(intent);
                return;
            }
    }

    class Section {

        private int id;

        private String nameSectionFr;

        private String nameSectionAr;

        Section(int id, String nameSectionFr, String nameSectionAr) {
            this.id = id;
            this.nameSectionFr = nameSectionFr;
            this.nameSectionAr = nameSectionAr;
        }

        public int getId() {
            return id;
        }

        public String getNameFr() {
            return nameSectionFr;
        }

        public String getNameAr() {
            return nameSectionAr;
        }

    }
}