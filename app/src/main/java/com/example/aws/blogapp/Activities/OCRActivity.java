package com.example.aws.blogapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aws.blogapp.Fragments.SettingsFragment;
import com.example.aws.blogapp.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;



public class OCRActivity<Panit> extends Activity {


    ImageView imgView;
    TextView detectedText;

    Paint p=new Paint();
    Paint textPaint= new Paint();
    int size=0;
    StringBuilder Textdetected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);
        imgView=(ImageView)findViewById(R.id.myimageview);
        detectedText = findViewById(R.id.textView);


    }

    public void displayText(){
        detectedText.setText(Textdetected);

    }

    public void home(View view){
        Intent intent= new Intent(this,OcrCaptureActivity.class);
        startActivity(intent);
    }
    public void back(View view){
        Intent intent= new Intent(this,UploadActivity.class);
        startActivity(intent);
    }

    public void draw(Rect r, String s){

        Bitmap bitmap2 = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        Bitmap o= Bitmap.createScaledBitmap(bitmap2,bitmap2.getWidth(),bitmap2.getHeight(),true);
        Bitmap c=o.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(c);
        p.setColor((Color.RED));
        canvas.drawRect(r.left-2,r.top-2,r.right+2,r.bottom+2,p);


        textPaint.setColor((SettingsFragment.mDefaultTextColor));
        textPaint.setTypeface(Typeface.defaultFromStyle(SettingsFragment.Defaultstyle));
        canvas.drawRect(10,10,20,20,p);
        size= (determineMaxTextSize(s,r,size));

        textPaint.setTextSize(size);
        float left = (r.left)+2;
        float bottom = (r.bottom)-2;
        canvas.drawText(s, left, bottom, textPaint);

        Context context = getApplicationContext();
        imgView.setImageBitmap(c);
    }

    private int determineMaxTextSize(String str, Rect rect, int previousSize)
    {
        int size = 0;
        float maxWidth=rect.width();
        Paint paint = new Paint();
        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(String.valueOf(str)) <= (maxWidth));

        if(size>previousSize&&previousSize!=0){
            size=previousSize;
            paint.setTextSize(size);
        }
        return size;
    }

    public void detect() {

        TextRecognizer recognizer = new TextRecognizer.Builder(OCRActivity.this).build();

        Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<TextBlock> sparseArray =  recognizer.detect(frame);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i < sparseArray.size(); i++){
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();
            Rect x=tx.getBoundingBox();
            draw(x, tx.getValue());
            stringBuilder.append(str);
        }
        Textdetected=stringBuilder;
    }

}