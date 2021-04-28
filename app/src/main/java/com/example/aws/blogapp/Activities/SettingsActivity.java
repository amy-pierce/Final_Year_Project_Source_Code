package com.example.aws.blogapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.aws.blogapp.R;


import top.defaults.colorpicker.ColorPickerPopup;

public class SettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener {


    public static int mDefaultTextColor= Color.BLACK;
    public static int mDefaultColor= Color.rgb(255,253,208);
    public static int Defaultstyle=0;

    private int mtextcolor= Color.BLACK;
    private int mcolor= Color.rgb(255,253,208);
    private int style=0;

    Switch ItalicsSwitch ;
    Switch BoldSwitch;

    private Button pick_color_button2;
    private Button pick_color_button;
    private TextView mColorPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);





        pick_color_button2=findViewById(R.id.pick_color_button2);
        pick_color_button=findViewById(R.id.pick_color_button);

        ItalicsSwitch = (Switch) findViewById(R.id.switch1);
        BoldSwitch = (Switch) findViewById(R.id.switch2);
        ItalicsSwitch.setOnCheckedChangeListener(this);
        BoldSwitch.setOnCheckedChangeListener(this);

        mColorPreview=findViewById(R.id.textView2);
        mColorPreview.setBackgroundColor(mDefaultColor);
        mColorPreview.setTextColor(mDefaultTextColor);
        mColorPreview.setTypeface(Typeface.defaultFromStyle(Defaultstyle));


        pick_color_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new ColorPickerPopup.Builder(SettingsActivity.this).initialColor(
                                Color.RED)
                                .enableBrightness(true)
                                .enableAlpha(true)
                                .okTitle("Choose")
                                .cancelTitle("Cancel")
                                .showIndicator(true)
                                .showValue(true)
                                .build()
                                .show(
                                        v,
                                        new ColorPickerPopup.ColorPickerObserver() {
                                            @Override
                                            public void
                                            onColorPicked(int textcolor) {

                                                mColorPreview.setTextColor(textcolor);
                                                mtextcolor=textcolor;

                                            }
                                        });
                    }
                });


        pick_color_button2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new ColorPickerPopup.Builder(SettingsActivity.this).initialColor(
                                Color.RED)
                                .enableBrightness(true)
                                .enableAlpha(true)
                                .okTitle("Choose")
                                .cancelTitle("Cancel")
                                .showIndicator(true)
                                .showValue(true)
                                .build()
                                .show(
                                        v,
                                        new ColorPickerPopup.ColorPickerObserver() {
                                            @Override
                                            public void
                                            onColorPicked(int color) {
                                                mColorPreview.setBackgroundColor(color);
                                                mcolor=color;
                                            }
                                        });
                    }
                });
    }

    public void home(View view){
        Intent intent= new Intent(this,OcrCaptureActivity.class);
        startActivity(intent);
    }



    public void save(View view){
        mDefaultColor = mcolor;
        mDefaultTextColor = mtextcolor;
        Defaultstyle=style;
        home(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            if(compoundButton.getId()==2131230980){
                style+=2;
            }
            else if(compoundButton.getId()==2131230979){
                style++;
            }
        } else {
            if(compoundButton.getId()==2131230980){

                style-=2;
            }
            else if(compoundButton.getId()==2131230979){

                style--;

            }
        }
        mColorPreview.setTypeface(Typeface.defaultFromStyle(style));

    }

    public void reset(View view){
         mtextcolor= Color.BLACK;
         mcolor= Color.rgb(255,253,208);
         style=0;
        mColorPreview.setBackgroundColor(mcolor);
        mColorPreview.setTextColor(mtextcolor);
        mColorPreview.setTypeface(Typeface.defaultFromStyle(style));

        ItalicsSwitch.setChecked(false);
         BoldSwitch.setChecked(false);

    }
}