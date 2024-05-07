package com.calcexample.testjson;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.content.Context;
import android.widget.LinearLayout;

public class SimilarParts {

    private static boolean languageFr = false;////Language Display True == French / False == Arabic

    private static int idSpec = -1;

    private static int idNiveau = -1;

    private static int idSection = -1;

    protected static Button createButtonForLayout(Context currentClass, Resources resources) {
        Button button = new Button(currentClass);

        ///Make params to add the size and margin
        LayoutParams params = new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (67 * resources.getDisplayMetrics().density));

        params.setMargins(0, 0, 0, (int) (20 * resources.getDisplayMetrics().density)); // Adjust marginBottomInPixels as needed

        button.setTextSize(15);
        button.setTextColor(Color.WHITE);

        button.setBackgroundResource(R.drawable.rounded_button_shape);

        button.setLayoutParams(params);

        // Set a unique ID for each button
        button.setId(View.generateViewId());
        return button;
    }

    public static void switchLanguage() { SimilarParts.languageFr = !languageFr; } ///Switch The Language

    public static boolean getLanguageStatus() {
        return languageFr;
    }

    public static void setIdSpec(int idSpec) {
        SimilarParts.idSpec = idSpec;
    }

    public static void setIdNiveau(int idNiveau) {
        SimilarParts.idNiveau = idNiveau;
    }

    public static void setIdSection(int idSection) {
        SimilarParts.idSection = idSection;
    }

    public static int getIdSpec() {
        return idSpec;
    }

    public static int getIdNiveau() {
        return idNiveau;
    }

    public static int getIdSection() {
        return idSection;
    }

}
