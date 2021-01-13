package com.example.myfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Guideline1: "Tools" -> "Firebase" -> "Analytics"
    // Guideline2: "Fire homepage" -> "Firebase projects are containers for you apps" -> "Learn more" -> "Get started with ..."
    // add in Cloud Firestore dependency from Guideline2
    // add in dependency 'com.android.support:multidex:1.0.3' and [multiDexEnabled true] at defaultConfig
    TextView textView,tv;
    ImageView imageView;
    Button btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fbauth;
    FirebaseUser fbuser;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.output);
        imageView=findViewById(R.id.imageView);
        tv=findViewById(R.id.textView);
        btn=findViewById(R.id.button);
        fbauth=FirebaseAuth.getInstance();
        fbuser=fbauth.getCurrentUser();
        if(fbuser!=null){
            Glide.with(this).load(fbuser.getPhotoUrl()).into(imageView);
            tv.setText(fbuser.getDisplayName());
        }
        googleSignInClient= GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fbauth.signOut();
                            Toast.makeText(MainActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
        saveData();
        loadData(textView);
        // to delete specific document (can add OnSuccessListener to know status):
        // db.collection("Student").document("randomApaLJ").delete();

    }

    private void saveData(){
        Map<String,Object> student;
        student=new HashMap<>();
        student.put("Name","Willy");
        student.put("Age",221);
        db.collection("Student").document("document1").set(student);//will not add record, will update document1
        //db.collection("Student").add(student); //add in new record everytime with random document name
    }

    private void loadData(View v){
        /*// Part1 - load only one document:
        db.collection("Student").document("document1").get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String display=documentSnapshot.getId()+":"+documentSnapshot.getString("Name")+"("+documentSnapshot.getLong("Age")+")";
                textView.setText(display);
            }
        });
        */
    // Part2 - to get all documents in one go:
    db.collection("Student").get()
    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            StringBuilder sb=new StringBuilder();
            for(QueryDocumentSnapshot document:queryDocumentSnapshots){
                sb.append(document.getId()+":"+document.getString("Name")+"("+document.getLong("Age")+")\n");
            }
            textView.setText(sb.toString());
        }
    });

    }

}
