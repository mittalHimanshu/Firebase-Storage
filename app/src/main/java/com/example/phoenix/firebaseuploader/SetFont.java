package com.example.phoenix.firebaseuploader;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;

public class SetFont {

    public void setButtonFont(Button b, Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "HelloRaleigh.ttf");
        b.setTypeface(typeface);
    }

}
