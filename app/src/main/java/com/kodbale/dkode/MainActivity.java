package com.kodbale.dkode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kodbale.dkode.Database.QuestionManager;
import com.kodbale.dkode.Fragments.TextQuestion;
import com.kodbale.dkode.Login.LoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_QR_SCAN = 101;
    private static final String LOGTAG = "QRSCAN";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button submit;
    private Button skip;
    private FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiations
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        submit = (Button) findViewById(R.id.submit);
        skip = (Button) findViewById(R.id.skip);
        frame = (FrameLayout) findViewById(R.id.frame);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new TextQuestion()).commit();

        submit.setOnClickListener(this);
        skip.setOnClickListener(this);
        if ( user == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
        QuestionManager.get(getApplicationContext()).insertAllQuestions();
        Log.i("i", "inserted questions");
        QuestionManager.get(getApplicationContext()).initializeNotAnsweredList();
        QuestionManager.get(getApplicationContext()).initializeAnsweredList();
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.submit:
             Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
             startActivityForResult( i,REQUEST_CODE_QR_SCAN);
             break;
         case R.id.skip:  //TODO get the next question and create a update the ui
             break;
     }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;

            String solution = "something";
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");

            solution = result;

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Scan result");

            if(result.equals(solution)) {
                alertDialog.setMessage("you successfully cracked the question");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Next Question",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialgo, int which) {
                                Toast.makeText(getApplicationContext(), "you are in the next question!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                alertDialog.setMessage("you failed to answer the question");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
            alertDialog.show();

        }
    }

}
