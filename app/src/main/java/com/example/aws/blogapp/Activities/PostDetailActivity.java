package com.example.aws.blogapp.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aws.blogapp.Adapters.CommentAdapter;
import com.example.aws.blogapp.Fragments.SettingsFragment;
import com.example.aws.blogapp.Models.Comment;
import com.example.aws.blogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost,imgUserPost,imgCurrentUser;
    TextView ResultDesc,ResultDateName,ResultTitle, ResultName;
    EditText editTextComment,fontSize;
    Button btnAddComment, updatebtn, increase, decrease,save;
    String key;
    String ResultKey, CurrentResultTitle;
    String FontSize;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    int font=12;
    static String COMMENT_KEY = "Posts" ;
    DatabaseReference resultRef=FirebaseDatabase.getInstance().getReference("Posts");




    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        RvComment = findViewById(R.id.rv_comment);
        imgPost =findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);

        ResultTitle = findViewById(R.id.post_detail_title);
        ResultDesc = findViewById(R.id.post_detail_desc);
        ResultDateName = findViewById(R.id.post_detail_date_name);
        ResultName = findViewById(R.id.post_detail_name);
        ResultDesc.setTypeface(SettingsFragment.mDefaultfont,SettingsFragment.Defaultstyle);
        ResultTitle.setTypeface(SettingsFragment.mDefaultfont,SettingsFragment.Defaultstyle);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        updatebtn = findViewById(R.id.update);
        increase=findViewById(R.id.increase);
        decrease=findViewById(R.id.decrease);
        save=findViewById(R.id.button3);

        fontSize=findViewById(R.id.integer_number);
        ResultDesc.setTextSize(font);



        CurrentResultTitle = getIntent().getExtras().getString("postKey");
        CurrentResultTitle = getIntent().getExtras().getString("title");




        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();



        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(ResultKey);


            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePost(ResultKey);


            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font++;
                ResultDesc.setTextSize(font);
                fontSize.setText(font+"pt");


            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                font--;
                ResultDesc.setTextSize(font);
                fontSize.setText(font+"pt");
            }
        });






        String postImage = getIntent().getExtras().getString("postImage") ;
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        ResultTitle.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        ResultDesc.setText(postDescription);

        String userName = getIntent().getExtras().getString("userName");
        ResultName.setText(userName);



        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        ResultDateName.setText(date);


        String fs = getIntent().getExtras().getString("fontSize");
        int f= Integer.parseInt(fs);
        ResultDesc.setTextSize(f);
        fontSize.setText(f+"pt");

        iniRvComment();


    }

    private void updatePost(String resultKey) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to update '"+CurrentResultTitle+"' ?")
                .setMessage("This cannot be undone")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      updateResult(key);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showMessage("'"+CurrentResultTitle+"' not updated");
                    }
                })
                .show();
    }

    private void updateResult(String resultKey) {
        resultRef.child(key).child("description").setValue( ResultDesc.getText().toString());
        resultRef.child(key).child("title").setValue(ResultTitle.getText().toString());
        resultRef.child(key).child("fontSize").setValue(String.valueOf(font));
        showMessage("'"+resultKey+"' updated");
    }
    private void deleteResult(String resultKey) {
        resultRef.child(key).removeValue();
        Intent HomeActivity = new Intent(getApplicationContext(),Home.class);
        startActivity(HomeActivity);
        showMessage("'"+CurrentResultTitle+"' deleteted");
    }



    private void deletePost(String resultKey) {

        key=resultKey;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to delete '"+CurrentResultTitle+"' ?")
                .setMessage("This cannot be undone")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resultRef.child(key).removeValue();
                        Intent HomeActivity = new Intent(getApplicationContext(),Home.class);
                        startActivity(HomeActivity);
                        showMessage("'"+CurrentResultTitle+"' deleteted");
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showMessage("'"+CurrentResultTitle+"' not deleted");
                    }
                })
                .show();

    }



    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(ResultKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;

                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;


    }


}
