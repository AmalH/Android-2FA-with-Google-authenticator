package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import amalhichri.androidprojects.com.Utils.AppSingleton;

public class TwoFactorAuthActivity extends AppCompatActivity {

    private String email,password,username,phoneNumber,countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_auth);


        /** get user credentials from previous activity **/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            password = extras.getString("password");
            username = extras.getString("username");
        }


        /** 2FA  work goes here **/

            /** 1.Ask the user for their phone number **/
        phoneNumber =((EditText)findViewById(R.id.phoneNumber)).getText().toString();
        countryCode ="216";

            /** 2.Add the user to the Authy API **/
        String addUserUrl = "https://api.authy.com/protected/json/users/new?user[email]="+email
                +"&user[cellphone]="+phoneNumber
                +"&user[country_code]="+countryCode+"&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                addUserUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Call Passed:", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.getMessage());
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq);

            /** 3.Call the Authy API to generate a QRCode **/

        /** register user through firebase  **/
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //Log.d("Test","CALLED");
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // add user to database
                                Log.d("Test","Facebook to firebase success");
                                amalhichri.androidprojects.com.a2fasample_authyapi.Utils.User userToAdd = new amalhichri.androidprojects.com.a2fasample_authyapi.Utils.User(username,email);
                                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userToAdd).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.d("Failure",e.getMessage());
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_LONG).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR:",e.getMessage());
                    }
                });
            }
        });
    }
}
