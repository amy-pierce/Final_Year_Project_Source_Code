package com.example.aws.blogapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aws.blogapp.R;

import top.defaults.colorpicker.ColorPickerPopup;


public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    View mview;
    private TextView mColorPreview;

    public static int mDefaultTextColor= Color.BLACK;
    public static int mDefaultColor= Color.rgb(255,253,208);
    public static int Defaultstyle=0;

    public static Typeface mDefaultfont;

    private int mtextcolor= Color.BLACK;
    private int mcolor= Color.rgb(255,253,208);
    private int style=0;

    Switch ItalicsSwitch ;
    Switch BoldSwitch;
    Spinner mySpinner;
    private Button pick_color_button2;
    private Button pick_color_button;
    private Button button2;
    private Button button;

    private OCRFragment.OnFragmentInteractionListener mListener;
    Typeface tf;
    public SettingsFragment() {
    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mview= inflater.inflate(R.layout.fragment_settings, container, false);
        pick_color_button2= mview.findViewById(R.id.pick_color_button2);
        pick_color_button=mview.findViewById(R.id.pick_color_button);
        button =mview.findViewById(R.id.button);
        button2 =mview.findViewById(R.id.button2);
        mySpinner=mview.findViewById(R.id.spinner);
        ItalicsSwitch = (Switch) mview.findViewById(R.id.switch1);
        BoldSwitch = (Switch) mview.findViewById(R.id.switch2);
        ItalicsSwitch.setOnCheckedChangeListener(this);
        BoldSwitch.setOnCheckedChangeListener(this);

        mColorPreview=mview.findViewById(R.id.textView2);
        mColorPreview.setBackgroundColor(mDefaultColor);
        mColorPreview.setTextColor(mDefaultTextColor);
        mColorPreview.setTypeface(Typeface.defaultFromStyle(Defaultstyle));

        ArrayAdapter<String> myAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.fonts));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);


        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                String path=( adapterView.getItemAtPosition(i).toString())+".ttf";

                tf= Typeface.createFromAsset(getActivity().getAssets(), path);
                Typeface face=Typeface.create(tf,style);
                mColorPreview.setTypeface(face);


                mColorPreview.setTextSize(determineMaxTextSize((String) mColorPreview.getText(),250,0,face));
                try {
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        String path=(mySpinner.getSelectedItem().toString())+".ttf";
        tf= Typeface.createFromAsset(getActivity().getAssets(), path);

        pick_color_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new ColorPickerPopup.Builder(getActivity()).initialColor(
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
                        new ColorPickerPopup.Builder(getActivity()).initialColor(
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


        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mDefaultColor = mcolor;
                        mDefaultTextColor = mtextcolor;
                        Defaultstyle=style;
                        mDefaultfont=tf;
                        Toast toast= Toast.makeText(getActivity(),"Preferences saved", Toast.LENGTH_LONG);
                        toast.show();

                    }
                });
        button2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mtextcolor= Color.BLACK;
                        mcolor= Color.rgb(255,253,208);
                        style=0;
                        mColorPreview.setBackgroundColor(mcolor);
                        mColorPreview.setTextColor(mtextcolor);
                        ItalicsSwitch.setChecked(false);
                        BoldSwitch.setChecked(false);
                        tf= Typeface.createFromAsset(getActivity().getAssets(), "Arial.ttf");
                        mColorPreview.setTypeface(tf,style);
                        Toast toast= Toast.makeText(getActivity(),"Preferences reset", Toast.LENGTH_LONG);
                        toast.show();

                    }
                });
        return mview;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static int getValueInDP(Context context, int value){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {


        switch (compoundButton.getId()) {
            case R.id.switch1:
                if (isChecked) {
                    style++;
                } else {
                    style--;
                }
                break;
            case R.id.switch2:

                if (isChecked) {
                    style += 2;
                } else {
                    style -= 2;
                }
                break;
        }
        mColorPreview.setTypeface(tf,style);

    }

    private int determineMaxTextSize(String str, float maxWidth, int previousSize, Typeface face)
    {
        int size = 0;

        Paint paint = new Paint();
        paint.setTypeface(face);
        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(String.valueOf(str)) <= (maxWidth));

        if(size>previousSize&&previousSize!=0){
            size=previousSize;
            paint.setTextSize(size);

        }

        return size;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
