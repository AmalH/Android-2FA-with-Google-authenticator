package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.Utils.AppSingleton;

public class QRCodeActivity extends AppCompatActivity {

    private String qrCodeCallUrl,qrCodePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        /** get auth creds from previous activity **/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrCodeCallUrl= extras.getString("qrCodeCallUrl");
        }

        /** get the  qr code image path**/
        JSONObject obj = new JSONObject();
        RequestQueue queue =  AppSingleton.getInstance(getApplicationContext()).getRequestQueue();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,qrCodeCallUrl,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            qrCodePath = response.getString("qr_code");
                            Toast.makeText(getApplicationContext(), "Code: "+qrCodePath, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ",error.getMessage());
                    }
                });
        queue.add(jsObjRequest);

        /** set the imageView's src **/

        ImageView qrCodeImgVw = findViewById(R.id.qrCodeImgVw);
        Picasso.get().load("https://s3.amazonaws.com/qr-codes-9f266de4dd32a7244bf6862baea01379/h0B0wyANHtW6KKocnwV2H59JNT6eBqUMqVIYYJjYSHc.png").into(qrCodeImgVw);

        /** pass the code entred by user to the Authy API to verify it **/
        (findViewById(R.id.confirmSignupBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeValidationUrl="https://api.authy.com/protected/json/verify/"+
                        ((EditText)findViewById(R.id.validationCode)).getText().toString()+"/80749027?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
               /* String codeValidationUrl = "https://api.authy.com/protected/json/verify/"
                +((EditText)findViewById(R.id.validationCode)).getText().toString()+
                "80749027"+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";*/
                String isValid;
                JSONObject obj = new JSONObject();
                RequestQueue queue =  AppSingleton.getInstance(getApplicationContext()).getRequestQueue();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,qrCodeCallUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if((response.getString("toker")).equals("is valid"))
                                        Toast.makeText(getApplicationContext(), "Registered: ", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "Not registered!", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ERROR! ",error.getMessage());
                            }
                        });
                queue.add(jsObjRequest);
            }
        });
    }

    /*private void firebaseSignup(final String email, String password){
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
    }*/
}
