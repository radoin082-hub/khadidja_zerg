package com.calcexample.testjson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.ArrayList;

public class MainActivityFaculty extends AppCompatActivity {
    protected ArrayList<Faculty> items;

    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_faculty);
        initializeAttributes();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayLanguage(findViewById(R.id.Language));
        Log.d("OnRestart", "Back To Main Activity Faculty");
    }


    private void initializeAttributes() {
        items = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ///Initial The buttons Table With The Buttons From xml
        buttons = new Button[]
                {findViewById(R.id.button1), findViewById(R.id.button2)
                        , findViewById(R.id.button3), findViewById(R.id.button4)
                        , findViewById(R.id.button5), findViewById(R.id.button6)
                        , findViewById(R.id.button7), findViewById(R.id.button8)};

        ///Create Request Of JSon And Put It In requestQueue
        requestQueue.add(new JsonArrayRequest(Request.Method.GET
                , "https://num.univ-biskra.dz/psp/pspapi" + "/faculty?" + "key=appmob"
                , null
                , this::handleJsonResponse
                , this::handleJsonError));
    }

    private void handleJsonResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                items.add(new Faculty(jsonObject.getInt("id_fac"), jsonObject.getString("name_fac"), jsonObject.getString("name_fac_ar")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        switchLanguageMethod(null);
    }

    private void handleJsonError(VolleyError error) {
        Log.d("JsonResponse", "error:" + error.toString());
        if (error instanceof NetworkError && error.networkResponse != null) {
            Log.d("Network Error", "Status code" + error.networkResponse.statusCode);
        }
        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown Error";
        Toast.makeText(MainActivityFaculty.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void switchLanguageMethod(View view) {
        SimilarParts.switchLanguage();
        displayLanguage(findViewById(R.id.Language));
    }

    public void displayLanguage(Button languageButton) {
        if (items.isEmpty()) {  ///There Is No Faculty (Problem in json)
            Toast.makeText(MainActivityFaculty.this, "The List Is Empty Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SimilarParts.getLanguageStatus()) {
            ///Display French Language
            if (languageButton != null)
                languageButton.setText(R.string.French);
            for (int i = 0; i < buttons.length; i++)
                buttons[i].setText(items.get(i).getNameFacultyFr());
        } else {
            ///Display Arabic Language
            if (languageButton != null)
                languageButton.setText(R.string.Arabic);
            for (int i = 0; i < buttons.length; i++)
                buttons[i].setText(items.get(i).getNameFacultyAr());
        }
    }

    public void goToDepartment(View view) {
        if (items.isEmpty()) {  ///There Is No Faculty (Problem in json)
            Toast.makeText(MainActivityFaculty.this, "The List Is Empty You Cant Go There Check Your API", Toast.LENGTH_SHORT).show();
            return;
        }
        Button clickedButton = (Button) view;
        Intent intent = new Intent(MainActivityFaculty.this, MainActivityDepartment.class);
        for (int i = 0; i < buttons.length; i++)
            if (clickedButton == buttons[i]) {
                intent.putExtra("Id_Faculty", items.get(i).getId());
                clickedButton.getContext().startActivity(intent);
                return;
            }
    }

    class Faculty {

        private int id;

        private String nameFacultyFr;

        private String nameFacultyAr;

        Faculty(int id, String nameFacultyFr, String nameFacultyAr) {
            this.id = id;
            this.nameFacultyFr = nameFacultyFr;
            this.nameFacultyAr = nameFacultyAr;
        }

        public int getId() {
            return id;
        }

        public String getNameFacultyFr() {
            return nameFacultyFr;
        }

        public String getNameFacultyAr() {
            return nameFacultyAr;
        }
    }
}
