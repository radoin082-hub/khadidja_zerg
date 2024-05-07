package com.calcexample.testjson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class MainActivityTabTime extends AppCompatActivity {

    private RequestQueue requestQueueForeground;
    private RequestQueue requestQueueBackground;
    private ArrayList<TimeTable> timeTableArrayListAr;
    private ArrayList<TimeTable> timeTableArrayListFr;
    private ArrayList<TextViewSession> infoSessions;
    private Button previouslyClickedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab_time);
        requestQueueForeground = Volley.newRequestQueue(this);
        requestQueueBackground = Volley.newRequestQueue(this);
        timeTableArrayListFr = new ArrayList<>();
        timeTableArrayListAr = new ArrayList<>();
        infoSessions = new ArrayList<>();
        previouslyClickedButton = findViewById(R.id.AllSession);
        downLoadJsonDataTimeTable(SimilarParts.getIdSpec(), SimilarParts.getIdNiveau(), SimilarParts.getIdSection(), getIntent().getIntExtra("Id_Group", 0));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayText(getCurrentList());
        Log.d("OnRestart", "Back To Main Activity TabTime");
    }

    private void downLoadJsonDataTimeTable(int id_spec, int id_niv, int id_sec, int id_grp) {
        Log.d("Value Of Parameter", id_spec + " " + id_niv + " " + id_sec + " " + id_grp);
        requestQueueForeground.add(new JsonArrayRequest(Request.Method.GET, "https://num.univ-biskra.dz/psp/emploi/section2_public?select_spec=" + id_spec + "&niveau=" + id_niv + "&section=" + id_sec + "&groupe=" + (id_grp == 0 ? "null" : id_grp) + "&sg=0&langu=" + (SimilarParts.getLanguageStatus() ? "fr" : "ar") + "&sem=2&id_year=2", null, this::handleJsonResponseForeground, this::handleJsonError));
        requestQueueBackground.add(new JsonArrayRequest(Request.Method.GET, "https://num.univ-biskra.dz/psp/emploi/section2_public?select_spec=" + id_spec + "&niveau=" + id_niv + "&section=" + id_sec + "&groupe=" + (id_grp == 0 ? "null" : id_grp) + "&sg=0&langu=" + (!SimilarParts.getLanguageStatus() ? "fr" : "ar") + "&sem=2&id_year=2", null, response -> handleJsonResponse(response, (!SimilarParts.getLanguageStatus() ? timeTableArrayListFr : timeTableArrayListAr)), this::handleJsonError));
    }

    private void handleJsonResponseForeground(JSONArray response) {
        handleJsonResponse(response, getCurrentList());

        for (TimeTable timeTable : getCurrentList())
            createSession(timeTable);

        displayText(getCurrentList());
    }

    private void handleJsonResponse(JSONArray response, ArrayList<TimeTable> timeTableArrayList) {
        for (int i = 0; i < response.length(); i++) {
            JSONArray jsonArray;
            try {
                jsonArray = response.getJSONArray(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            TimeTable timeTable = new TimeTable(jsonArray.optString(0) // className
                    , jsonArray.optString(1) // location
                    , jsonArray.optString(2) // typeOfCourse
                    , jsonArray.optString(3) // levelOfStudy
                    , jsonArray.optString(4) // field
                    , jsonArray.optString(5) // professorLastName
                    , jsonArray.optString(6) // professorFirstName
                    //, jsonArray.optString(7)
                    , jsonArray.optString(8) // moduleName
                    //, jsonArray.optString(9)
                    //, jsonArray.optString(10)
                    //, jsonArray.optString(11)
                    , jsonArray.optInt(12), jsonArray.optInt(13)
                    //, jsonArray.optString(14)
                    //, jsonArray.optString(15)
                    //, jsonArray.optString(16)
                    //, jsonArray.optString(17)
                    //, jsonArray.optString(18)
                    , (jsonArray.optString(19).equals("1")), (jsonArray.optString(20).equals("1")), jsonArray.optString(21), jsonArray.optString(22));
            timeTableArrayList.add(timeTable);
            /// run a piece of code on the UI thread from a background thread
            runOnUiThread(() -> Log.d("JsonResponse", "timeTable:" + timeTable));
        }
        /// Sort The List
        timeTableArrayList.sort(Comparator.comparingInt(TimeTable::getDayOfWeek));
    }

    private void handleJsonError(VolleyError error) {
        Log.d("JsonResponse", "error:" + error.toString());
        if (error instanceof NetworkError && error.networkResponse != null) {
            Log.d("Network Error", "Status code" + error.networkResponse.statusCode);
        }
        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown Error";
        Toast.makeText(MainActivityTabTime.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<TimeTable> getCurrentList() {
        return (SimilarParts.getLanguageStatus() ? timeTableArrayListFr : timeTableArrayListAr);
    }

    public void switchLanguageMethod(View view) {
        SimilarParts.switchLanguage();/// Switch The Language
        displayText(getListOfDay(previouslyClickedButton.getId()));
    }

    public void displayText(ArrayList<TimeTable> timeTableArrayList) {
        /// The List Of chosen Language Is Empty
        if (timeTableArrayList.isEmpty()) {
            Toast.makeText(this, "There Is No Session", Toast.LENGTH_SHORT).show();
            return;
        }
        ((TextView) findViewById(R.id.Field)).setText(timeTableArrayList.get(0).getField());
        ((TextView) findViewById(R.id.Level)).setText(timeTableArrayList.get(0).getLevel());
        ((Button) findViewById(R.id.Language)).setText(SimilarParts.getLanguageStatus() ? R.string.French : R.string.Arabic);
        for (int i = 0; i < Math.min(timeTableArrayList.size(), infoSessions.size()); i++) {
            infoSessions.get(i).setNames(timeTableArrayList.get(i));
        }
    }

    public void switchDay(View view) {
        ///Switch Color
        previouslyClickedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        previouslyClickedButton = (Button) view;
        previouslyClickedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.selected_button));

        ArrayList<TimeTable> listChosenDay = getListOfDay(view.getId());

        ((LinearLayout) findViewById(R.id.layout)).removeAllViews();
        infoSessions.clear();

        for (TimeTable timeTable : listChosenDay)
            createSession(timeTable);

        displayText(listChosenDay);
    }

    private ArrayList<TimeTable> getListOfDay(int id) {
        int chosenDay;
        if (id == R.id.Sunday) chosenDay = 1;
        else if (id == R.id.Monday) {
            chosenDay = 2;
        } else if (id == R.id.Tuesday) {
            chosenDay = 3;
        } else if (id == R.id.Wednesday) {
            chosenDay = 4;
        } else if (id == R.id.Thursday) {
            chosenDay = 5;
        } else if (id == R.id.Saturday) {
            chosenDay = 0;
        } else {
            return getCurrentList();
        }

        ///Reach The Session Of The chosenDay and add to listChosenDay
        ArrayList<TimeTable> listChosenDay = new ArrayList<>();
        for (TimeTable timeTable : getCurrentList())
            if (timeTable.getDayOfWeek() == chosenDay) listChosenDay.add(timeTable);

        return listChosenDay;///get The Session Of The Day
    }

    private void createSession(TimeTable session) {
        // Create the parent LinearLayout
        LinearLayout sessionLayout = new LinearLayout(this);

        sessionLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 350));

        sessionLayout.setPadding(0, (int) (10 * getResources().getDisplayMetrics().density), 0, (int) (10 * getResources().getDisplayMetrics().density)); // Set top Padding

        sessionLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create the left side LinearLayout
        LinearLayout leftLayout = new LinearLayout(this);
        leftLayout.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * getResources().getDisplayMetrics().density), // Width
                LinearLayout.LayoutParams.MATCH_PARENT));

        leftLayout.setOrientation(LinearLayout.VERTICAL);
        leftLayout.setBackgroundColor(Color.WHITE);
        leftLayout.setFocusable(true); // Set focusable

        // Add TextView for module name to leftLayout
        TextView moduleName = new TextView(this);
        moduleName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        moduleName.setGravity(Gravity.CENTER);
        moduleName.setText(session.getClassName());
        moduleName.setTextColor(Color.BLACK);
        moduleName.setTextSize(18);
        leftLayout.addView(moduleName);

        // Add TextView for professor name to leftLayout
        TextView profName = new TextView(this);
        profName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        profName.setGravity(Gravity.CENTER);
        profName.setText(session.getProfessorFirstName() + " " + session.getProfessorLastName());
        profName.setTextColor(ContextCompat.getColor(this, R.color.gray));
        profName.setTextSize(20);
        leftLayout.addView(profName);

        // Create the right side LinearLayout
        LinearLayout rightLayout = new LinearLayout(this);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        rightLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.coure)); // Assuming td color is dark gray

        // Add TextView for module shortcut to rightLayout
        TextView moduleShortcut = new TextView(this);
        moduleShortcut.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        moduleShortcut.setGravity(Gravity.CENTER);
        moduleShortcut.setText(session.getModuleNameShortCut());
        moduleShortcut.setTextColor(Color.BLACK);
        moduleShortcut.setTextSize(20);
        moduleShortcut.setPadding(0, 0, 0, 10); // Set bottom margin
        rightLayout.addView(moduleShortcut);

        // Add horizontal line below module shortcut
        View lineView = new View(this);
        lineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        lineView.setBackgroundColor(Color.BLACK);
        rightLayout.addView(lineView);

        // Add TextView for class name to rightLayout
        TextView className = new TextView(this);
        className.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        className.setGravity(Gravity.CENTER);
        className.setText(session.getClassLocation());
        className.setTextColor(Color.BLACK);
        className.setTextSize(20);
        className.setPadding(0, 10, 0, 0); // Set top margin
        className.setOnClickListener(View -> openPlace(session));
        rightLayout.addView(className);

        // Add left and right layouts to the sessionLayout
        sessionLayout.addView(leftLayout);
        sessionLayout.addView(rightLayout);

        ((LinearLayout) findViewById(R.id.layout)).addView(sessionLayout);
        // Add sessionLayout to the parent layout in your activity
        // Example: parentLayout.addView(sessionLayout);

        infoSessions.add(new TextViewSession(moduleName, profName, moduleShortcut, className));
    }

    private void openPlace(TimeTable session) {
        if (session.isOnline()) { // open meet
            if (!session.getOnlineLink().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(session.getOnlineLink()));
                startActivity(intent);
            } else Toast.makeText(MainActivityTabTime.this, "URL Is Empty", Toast.LENGTH_SHORT).show();
        } else {   // Open GPS
            // Split the string using comma
            String[] parts = session.getLocationGPS().split(",");

            // Extract latitude and longitude values
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, "geo:%f,%f?q=%f,%f", latitude, longitude, latitude, longitude)));

            intent.setPackage("com.google.android.apps.maps"); // Ensure that Google Maps is used
            startActivity(intent);
        }
    }

    public void goToGroup(View view) { ///Without Put Extra
        Intent intent = new Intent(MainActivityTabTime.this, MainActivityGroupe.class);
        startActivity(intent);
    }
}

class TimeTable {
    private final String className;
    private final String location;
    private final String typeOfCourse;
    private final String levelOfStudy;
    private final String field;
    private final String professorLastName;
    private final String professorFirstName;
    //private final String unknownField1;
    private final String moduleNameShortCut;
    /*
    private final String location2;
    private final String unknownField2;
    private final String unknownField3;
     */
    private final int dayOfWeek;
    private final int timeSlot;
    /*
    private final String unknownField4;
    private final String unknownField5;
    private final String unknownField6;
    private final String unknownField7;
    private final String sub_group;
    */
    private final boolean online;
    private final boolean biweekly;
    private final String onlineLink;
    private final String locationGPS;

    public TimeTable(String className, String location, String typeOfCourse, String levelOfStudy, String field, String professorLastName, String professorFirstName, String moduleName, int dayOfWeek, int timeSlot, boolean online, boolean biweekly, String onlineLink, String locationGPS) {
        this.className = className;
        this.location = location;
        this.typeOfCourse = typeOfCourse;
        this.levelOfStudy = levelOfStudy;
        this.field = field;
        this.professorLastName = professorLastName;
        this.professorFirstName = professorFirstName;
        //this.unknownField1 = unknownField1;
        this.moduleNameShortCut = moduleName;
        /*
        this.location2 = location2;
        this.unknownField2 = unknownField2;
        this.unknownField3 = unknownField3;
        */
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        /*
        this.unknownField4 = unknownField4;
        this.unknownField5 = unknownField5;
        this.unknownField6 = unknownField6;
        this.unknownField7 = unknownField7;
        this.sub_group = sub_group;
        */
        this.online = online;
        this.biweekly = biweekly;
        this.onlineLink = onlineLink;
        this.locationGPS = locationGPS;
    }

    public String getClassName() {
        return className;
    }

    public String getClassLocation() {
        return location;
    }

    public String getTypeOfCourse() {
        return typeOfCourse;
    }

    public String getLevel() {
        return levelOfStudy;
    }

    public String getField() {
        return field;
    }

    public String getProfessorFirstName() {
        return professorFirstName;
    }

    public String getProfessorLastName() {
        return professorLastName;
    }

    public String getModuleNameShortCut() {
        return moduleNameShortCut;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isBiweekly() {
        return biweekly;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public String getLocationGPS() {
        return locationGPS;
    }

}

class TextViewSession {
    private final TextView moduleTextView;
    private final TextView profTextView;
    private final TextView moduleShotCutTextView;
    private final TextView classLocationTextView;

    public TextViewSession(TextView moduleTextView, TextView profTextView, TextView moduleShotCutTextView, TextView classLocationTextView) {
        this.moduleTextView = moduleTextView;
        this.profTextView = profTextView;
        this.moduleShotCutTextView = moduleShotCutTextView;
        this.classLocationTextView = classLocationTextView;
    }

    public void setNames(TimeTable timeTable) {
        moduleTextView.setText(timeTable.getClassName());
        profTextView.setText(timeTable.getProfessorFirstName() + " " + timeTable.getProfessorLastName());
        moduleShotCutTextView.setText(timeTable.getModuleNameShortCut());
        classLocationTextView.setText(timeTable.getClassLocation());

        /// Set Color Session
        int color;
        switch (timeTable.getTypeOfCourse()) {
            case "Cours":
            case "محاضرة":
                color = ContextCompat.getColor(classLocationTextView.getContext(), R.color.coure);
                break;
            case "TP":
            case "ع\u202B.\u202Cت":
                color = ContextCompat.getColor(classLocationTextView.getContext(), R.color.tp);
                break;
            case "TD":
            case "ع\u202B.\u202Cم":
                color = ContextCompat.getColor(classLocationTextView.getContext(), R.color.td);
                break;
            case "Workshop":
            case "Atelier":
                color = ContextCompat.getColor(classLocationTextView.getContext(), R.color.workShop);
                break;
            default:
                color = Color.WHITE;
        }
        ((LinearLayout) classLocationTextView.getParent()).setBackgroundColor(color);
    }
}
