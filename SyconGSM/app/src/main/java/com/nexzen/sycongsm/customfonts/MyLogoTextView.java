package com.nexzen.sycongsm.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class MyLogoTextView extends TextView {

    public MyLogoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyLogoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyLogoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Modak-Regular.ttf");
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Tillana-Regular.ttf");
            setTypeface(tf);
        }
    }

}