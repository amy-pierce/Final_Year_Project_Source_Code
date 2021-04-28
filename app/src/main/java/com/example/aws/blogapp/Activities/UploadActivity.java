package com.example.aws.blogapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.aws.blogapp.camera.GraphicOverlay;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import com.example.aws.blogapp.R;

public class UploadActivity extends Activity {
    private static final String TAG = "YIKES";

    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private TextToSpeech tts;
    TextView detectedText;
    ImageView i;
    TextView displayed;
    private ImageView viewImage;
    private static final int OFFSET = 120;
    private ImageView OCRiMAGE;
    boolean uploaded;
    private int mOffset = OFFSET;
    private static final int MULTIPLIER = 100;
    Button b;
    String path;
    Paint p=new Paint();
    Paint textPaint= new Paint();
    int size=0;

    Bitmap img;
    StringBuilder text;

       TextToSpeech textToSpeech;




       @SuppressWarnings("deprecation")
       public void speak(int x, int y){
           TextRecognizer recognizer = new TextRecognizer.Builder(UploadActivity.this).build();
           Bitmap bitmap=img;
           Frame frame = new Frame.Builder().setBitmap(bitmap).build();
           SparseArray<TextBlock> sparseArray =  recognizer.detect(frame);
           StringBuilder stringBuilder = new StringBuilder();
           for(int i=0;i < sparseArray.size(); i++){
               TextBlock tx = sparseArray.get(i);
               String str = tx.getValue();
               Rect rect=tx.getBoundingBox();

               if (rect.contains(x,y)){
                       tts.speak(tx.getValue(), TextToSpeech.QUEUE_ADD,null);
               }

           }

       }


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                speak(x,y);

            }

            return false;
        }
    };

    public void OCR(View view){


        if(uploaded==true) {
            setContentView(R.layout.activity_o_c_r);

            OCRiMAGE=(ImageView)findViewById(R.id.myimageview);
            OCRiMAGE.setOnTouchListener(touchListener);
            displayed = (TextView) findViewById(R.id.textView);
            img = (BitmapFactory.decodeFile(path));
            OCRiMAGE.setImageBitmap(img);
            detect();
        }
        else{
            Toast toast= Toast.makeText(getApplicationContext(),"No Image Selected", Toast.LENGTH_LONG);
            toast.show();
                }
    }




        public void draw(Rect r, String s, TextBlock tx){
            Bitmap bitmap2 = ((BitmapDrawable)OCRiMAGE.getDrawable()).getBitmap();

            Bitmap o= Bitmap.createScaledBitmap(bitmap2,bitmap2.getWidth(),bitmap2.getHeight(),true);
            Bitmap c=o.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(c);
            p.setColor(SettingsActivity.mDefaultColor);
            canvas.drawRect(r.left-10,r.top-10,r.right+10,r.bottom+10,p);


            textPaint.setColor((SettingsActivity.mDefaultTextColor));

             List<? extends Text> t=tx.getComponents();
              for(Text currentText : t) {
                         size= (determineMaxTextSize(currentText.getValue(),r,size));
                         textPaint.setTextSize(size);
                     }


             for(Text currentText:t){
                 float left = currentText.getBoundingBox().left+2;
                 float bottom = currentText.getBoundingBox().bottom-2;
                 canvas.drawText(currentText.getValue(), left, bottom, textPaint);




             }


            Context context = getApplicationContext();
            OCRiMAGE.setImageBitmap(c);
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
                    TextRecognizer recognizer = new TextRecognizer.Builder(UploadActivity.this).build();
                    Bitmap bitmap=img;
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> sparseArray =  recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i=0;i < sparseArray.size(); i++){
                        TextBlock tx = sparseArray.get(i);
                        String str = tx.getValue();
                        Rect x=tx.getBoundingBox();
                        draw(x, tx.getValue(),tx);
                        stringBuilder.append(str);
                        stringBuilder.append("\n"+"\n");
                    }
                            text=stringBuilder;
                }

                public void display(View view){
                    displayed.setText(text);

                }


    public void home(View view){
        Intent intent= new Intent(this,OcrCaptureActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        i=findViewById(R.id.myimageview);
        uploaded=false;
        setContentView(R.layout.activity_upload);
        detectedText = findViewById(R.id.textView);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
                        public void onInit(int i) {
                            if(i!= TextToSpeech.ERROR){
                                tts.setLanguage(Locale.UK);
                            } 
                        } 
                    }); 



        b=(Button)findViewById(R.id.btnSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.myimageview);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void back(View view){
        setContentView(R.layout.activity_upload);
    }


    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());

                    if (f.getName().equals("temp.jpg")) {
                        f = f;
                        uploaded=true;
                    }



                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile("/storage/emulated/0/temp.jpg");
                    path="/storage/emulated/0/temp.jpg";
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        uploaded=true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                path=picturePath;
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                viewImage.setImageBitmap(thumbnail);
                uploaded=true;
            }
        }
    }
}