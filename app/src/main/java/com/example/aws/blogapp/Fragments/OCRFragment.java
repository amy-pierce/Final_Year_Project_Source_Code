package com.example.aws.blogapp.Fragments;
import android.annotation.SuppressLint;
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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aws.blogapp.Activities.Home;
import com.example.aws.blogapp.Activities.OcrGraphic;
import com.example.aws.blogapp.R;
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


public class OCRFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    View mview;
    ImageView viewImage,i;
    Button btnSelectPhoto,ocrbtn,displaybtn,increase, decrease, save;
    boolean uploaded;
    public static String path;
    Bitmap img, keep;
    Frame frame;
    public static Bitmap selectedImage, c;
    public static Uri originalImage;
    public static String fontsz;
    public static TextView displayed;
    public static String dis="";
    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private TextToSpeech tts;
    TextView detectedText;
    EditText fontSize;
    TextRecognizer recognizer;
    int font=12;
    SparseArray<TextBlock> sparseArray ;
    StringBuilder stringBuilder ;
    private static final int OFFSET = 120;
    private int mOffset = OFFSET;
    private static final int MULTIPLIER = 100;
    Paint p=new Paint();
    Paint textPaint= new Paint();
    int size=0;
   public static StringBuilder text;


    private OnFragmentInteractionListener mListener;

    public OCRFragment() {
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        mview= inflater.inflate(R.layout.ocr_fragment, container, false);
        viewImage=mview.findViewById(R.id.myimageview);
        btnSelectPhoto=mview.findViewById(R.id.btnSelectPhoto);
        ocrbtn=mview.findViewById(R.id.x);
        displaybtn=mview.findViewById(R.id.display);
        ocrbtn.setVisibility(View.INVISIBLE);
        save=mview.findViewById(R.id.button3);

        increase=mview.findViewById(R.id.increase);
        decrease=mview.findViewById(R.id.decrease);
        fontSize=mview.findViewById(R.id.integer_number);

        increase.setVisibility(View.INVISIBLE);
        decrease.setVisibility(View.INVISIBLE);
        fontSize.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);

        displayed = (TextView) mview.findViewById(R.id.textView);

        displayed.setTextSize(font);
        fontsz= "12";
        i=mview.findViewById(R.id.myimageview);
        detectedText = mview.findViewById(R.id.textView);
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!= TextToSpeech.ERROR){
                    tts.setLanguage(Locale.US);
                }
            }
        });
        btnSelectPhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        select();

                    }
                });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font++;
                displayed.setTextSize(font);
                fontSize.setText(font+"pt");
                fontsz= String.valueOf(font);

            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font--;
                displayed.setTextSize(font);
                fontSize.setText(font+"pt");
                fontsz= String.valueOf(font);
            }
        });


        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dis="";
                        text=null;
                        ocrbtn.setVisibility(View.VISIBLE);
                        displaybtn.setVisibility(View.INVISIBLE);
                        displayed.setVisibility(View.INVISIBLE);
                        increase.setVisibility(View.INVISIBLE);
                        decrease.setVisibility(View.INVISIBLE);
                        fontSize.setVisibility(View.INVISIBLE);
                        Bitmap thumbnail = (BitmapFactory.decodeFile(path));
                        viewImage.setImageBitmap(thumbnail);
                        Toast toast= Toast.makeText(getActivity(),"Reload", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        ocrbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ocrbtn.setVisibility(View.INVISIBLE);
                        displaybtn.setVisibility(View.VISIBLE);
                        OCR();
                        Toast toast= Toast.makeText(getActivity(),"Click a textbox to have the text spoken", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        displaybtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        display();

                    }
                });
        return mview;
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
    @SuppressLint("RestrictedApi")
    public void OCR(){

        Bitmap b=(BitmapFactory.decodeFile(path));
        originalImage=getImageUri(getActivity(),b);
        keep=b;
        viewImage.setImageBitmap(b);
        if(uploaded==true) {

            Home.fab.setVisibility(View.VISIBLE);
            btnSelectPhoto.setVisibility(View.INVISIBLE);
            viewImage.setOnTouchListener(touchListener);
            img = (BitmapFactory.decodeFile(path));
            selectedImage=img;
            detect();

        }
        else{
             Toast toast= Toast.makeText(getActivity(),"No Image Selected", Toast.LENGTH_LONG);
            toast.show();
        }
    }



    public void display(){
        displaybtn.setVisibility(View.INVISIBLE);
        increase.setVisibility(View.VISIBLE);
        decrease.setVisibility(View.VISIBLE);
        fontSize.setVisibility(View.VISIBLE);
       save.setVisibility(View.INVISIBLE);
        displayed.setText(text);
        dis= String.valueOf(text);
    }

    @SuppressWarnings("deprecation")
    public void speak(int x, int y){
        TextRecognizer recognizer = new TextRecognizer.Builder(getActivity()).build();
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
    private void select() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());

                if (f.getName().equals("0")) {
                    f = f;
                    uploaded=true;
                    ocrbtn.setVisibility(View.VISIBLE);
                }




                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile("/storage/emulated/0/temp.jpg");
                    path="/storage/emulated/0/temp.jpg";
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator;
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File( String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        uploaded=true;
                        ocrbtn.setVisibility(View.VISIBLE);
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
                Uri u;
                u = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(u,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                path=picturePath;
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                viewImage.setImageBitmap(thumbnail);
                uploaded=true;
                ocrbtn.setVisibility(View.VISIBLE);

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path =
                MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                        "IMG_"+String.valueOf(System.currentTimeMillis()), null);
        return Uri.parse(path);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    public void draw(Rect r, String s, TextBlock tx){
        size=0;

        Bitmap bitmap2 = ((BitmapDrawable)viewImage.getDrawable()).getBitmap();

        Bitmap o= Bitmap.createScaledBitmap(bitmap2,bitmap2.getWidth(),bitmap2.getHeight(),true);
        c=o.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(c);
        p.setColor(SettingsFragment.mDefaultColor);
        canvas.drawRect(r.left-10,r.top-10,r.right+30,r.bottom+10,p);


        textPaint.setColor((SettingsFragment.mDefaultTextColor));
        Typeface face=Typeface.create(SettingsFragment.mDefaultfont,SettingsFragment.Defaultstyle);
        textPaint.setTypeface(face);

        List<? extends Text> t=tx.getComponents();
        for(Text currentText : t) {
            size= (determineMaxTextSize(currentText.getValue(),r,size, face));
            textPaint.setTextSize(size);
        }

        for(Text currentText:t){
            float left = currentText.getBoundingBox().left+2;
            float bottom = currentText.getBoundingBox().bottom-2;
            canvas.drawText(currentText.getValue(), left, bottom, textPaint);
        }
        viewImage.setImageBitmap(c);
        selectedImage=c;

    }

    private int determineMaxTextSize(String str, Rect rect, int previousSize, Typeface face)
    {

        int size = 0;
        float maxWidth=rect.width();
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

    public void detect() {
        Bitmap b=(BitmapFactory.decodeFile(path));
        stringBuilder = new StringBuilder();

        recognizer = new TextRecognizer.Builder(getActivity()).build();
        Bitmap bitmap=b;
         frame = new Frame.Builder().setBitmap(bitmap).build();
       sparseArray =  recognizer.detect(frame);
        for(int i=0;i < sparseArray.size(); i++){
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();
            Rect x=tx.getBoundingBox();
            draw(x, tx.getValue(),tx);
            stringBuilder.append(str);
            stringBuilder.append("\n"+"\n");
        }
        text=stringBuilder;
        dis= String.valueOf(text);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
