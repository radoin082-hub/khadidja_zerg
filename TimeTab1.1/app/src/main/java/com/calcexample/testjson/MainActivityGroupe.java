package com.calcexample.testjson;

import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivityGroupe extends AppCompatActivity {

    private ArrayList<Groupe> groupesArrayList;

    private RequestQueue requestQueue;

    private int[] idButtons;
   private int idSection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_groupe);
        groupesArrayList=new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        idSection= SimilarParts.getIdSection();
        downLoadJsonDatasection(idSection);
    }

    private void downLoadJsonDatasection(int idSection) {
        if(idSection == -1)
        {
            Toast toast = Toast.makeText(MainActivityGroupe.this, "i dont Have The Id Cheak Your API", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String url = "https://num.univ-biskra.dz/psp/pspapi" + "/group?section=" + idSection + "&semester=2" + "&key=appmob";
        JsonArrayRequest jsonArrayRequest =new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            idButtons = new int[response.length()];
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = response.getJSONObject(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Groupe groupe = new Groupe(
                            jsonObject.getInt("groupe_id"),
                            jsonObject.getString("groupe_name"),
                            jsonObject.getInt("sectionn_id"),
                            jsonObject.getString("Abrev_fr"),
                            jsonObject.getString("Abrev_ar"),
                            jsonObject.getInt("capacite_grp"),
                            jsonObject.getString("active"));

                    groupesArrayList.add(groupe);
                    idButtons[i] = addButtonToLayout(String.valueOf(groupe.getGroupe_name()));
                    runOnUiThread(() -> Log.d("JsonResponse","level:"+ groupe));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JsonError", "Error parsing Json", e);
                Toast toast = Toast.makeText(MainActivityGroupe.this, "Error parssing JSON", Toast.LENGTH_SHORT);
                toast.show();
            }
        }, error -> {
            Log.d("JsonResponse","error:"+error.toString());
            if(error instanceof NetworkError)
            {
                if(error.networkResponse !=null)
                {
                    Log.d("Network Error","Status code"+error.networkResponse.statusCode);
                }
            }
            Toast toast = Toast.makeText(MainActivityGroupe.this, error.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void goToTimeLab(int id)
    {
        for(int i=0;i<idButtons.length;i++)
        {
            if(idButtons[i] == id)
            {
                Intent intent = new Intent(MainActivityGroupe.this, MainActivityTabTime.class);
                 intent.putExtra("Id_Group", groupesArrayList.get(i).getGroupe_id());
               startActivity(intent);
            }
        }
    }

    private int addButtonToLayout(String nameOfDepartement) {
        Button button = new Button(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getPixel(330),
                getPixel(80)
        );

        params.setMargins(0, 0, 0, getPixel(20)); // Adjust marginBottomInPixels as needed
        params.setMarginStart(getPixel(30));

        button.setTextSize(15);
        button.setTextColor(Color.WHITE);

        button.setBackgroundResource(R.drawable.rounded_button_shape);

        button.setLayoutParams(params);

        button.setText(nameOfDepartement);

        // Set a unique ID for each button
        button.setId(View.generateViewId());

        // Set click listener
        button.setOnClickListener(v -> goToTimeLab(button.getId()));

        LinearLayout lay = findViewById(R.id.layout);
        lay.addView(button);

        return button.getId();
    }

    private int getPixel(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

class Groupe {
    private final int groupe_id;
    private final String groupe_name;
    private final int section_id;
    private final String Abrev_fr;
    private final String Abrev_ar;
    private final int capacite_grp;
    private final String active;

    public Groupe(int groupeId, String groupeName, int sectionId, String abrevFr, String abrevAr, int capaciteGrp, String active) {
        groupe_id = groupeId;
        groupe_name = groupeName;
        section_id = sectionId;
        Abrev_fr = abrevFr;
        Abrev_ar = abrevAr;
        capacite_grp = capaciteGrp;
        this.active = active;
    }

    public int getGroupe_id() {
        return groupe_id;
    }

    public String getGroupe_name() {
        return groupe_name;
    }

    public int getSection_id() {
        return section_id;
    }

    public String getAbrev_fr() {
        return Abrev_fr;
    }

    public String getAbrev_ar() {
        return Abrev_ar;
    }

    public int getCapacite_grp() {
        return capacite_grp;
    }

    public String isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Groupe{" +
                "groupe_id=" + groupe_id +
                ", groupe_name='" + groupe_name + '\'' +
                ", section_id=" + section_id +
                ", Abrev_fr='" + Abrev_fr + '\'' +
                ", Abrev_ar='" + Abrev_ar + '\'' +
                ", capacite_grp=" + capacite_grp +
                ", active=" + active +
                '}';
    }
}