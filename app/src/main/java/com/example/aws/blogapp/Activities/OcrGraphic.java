
package com.example.aws.blogapp.Activities;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.example.aws.blogapp.Fragments.SettingsFragment;
import com.example.aws.blogapp.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;


public class OcrGraphic extends GraphicOverlay.Graphic {

    private int id;
    int s=0;



    private static Paint rectPaint;
    private static Paint textPaint;
    private final TextBlock textBlock;

    private int determineMaxTextSize(String str, RectF rect, int previousSize)
    {

        int size = 0;
        float maxWidth=rect.width()-10;
        Paint paint = new Paint();
        Typeface tf= Typeface.create(SettingsFragment.mDefaultfont,SettingsFragment.Defaultstyle);
        paint.setTypeface(tf);
        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(String.valueOf(str)) <= (maxWidth));

        if(size>previousSize&&previousSize!=0){
            size=previousSize;
            paint.setTextSize(size);

        }
        return size;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);


        textBlock = text;

            rectPaint = new Paint();
            rectPaint.setColor(SettingsFragment.mDefaultColor);
            rectPaint.setStrokeWidth(4.0f);

            textPaint = new Paint();
            textPaint.setColor(SettingsFragment.mDefaultTextColor);
            Typeface tf= Typeface.create(SettingsFragment.mDefaultfont,SettingsFragment.Defaultstyle);
            textPaint.setTypeface(tf);

        postInvalidate();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        if (textBlock == null) {
            return false;
        }
        RectF rect = new RectF(textBlock.getBoundingBox());
        rect = translateRect(rect);
        return rect.contains(x, y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */

    public void draw(Canvas canvas) {
        if (textBlock == null) {
            return;
        }

        RectF rect = new RectF(textBlock.getBoundingBox().left,
                textBlock.getBoundingBox().top,
                textBlock.getBoundingBox().right,
                textBlock.getBoundingBox().bottom);

        RectF rect1 = new RectF(textBlock.getBoundingBox().left-10,
                textBlock.getBoundingBox().top-10,
                textBlock.getBoundingBox().right+10,
                textBlock.getBoundingBox().bottom+10);


        rect1 = translateRect(rect1);
        rect=translateRect(rect);
        canvas.drawRect(rect1, rectPaint);

        List<? extends Text> textComponents = textBlock.getComponents();
        for(Text currentText : textComponents) {
            s= (determineMaxTextSize(currentText.getValue(),rect,s));
            textPaint.setTextSize(s);
        }

        for(Text currentText : textComponents) {

            float left = translateX(currentText.getBoundingBox().left)+1;
            float bottom = translateY(currentText.getBoundingBox().bottom)-1;
            canvas.drawText(currentText.getValue(), left, bottom, textPaint);
        }
    }



}
