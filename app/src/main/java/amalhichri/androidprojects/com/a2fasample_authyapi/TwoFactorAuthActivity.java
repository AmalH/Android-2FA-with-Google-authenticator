package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.Utils.AppSingleton;

public class TwoFactorAuthActivity extends AppCompatActivity {

    private static String email;
    private static String password;
    private String username;
    private String phoneNumber;
    private String countryCode;
    private String addUserUrl;
    private String addedUserId;

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

        /*************************************************************************************************
         *                      Using an authentication app in another device *
         *  **********************************************************************************************/
        (findViewById(R.id.twofaViaPhone2nd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** 1.Ask the user for their phone number **/
                phoneNumber =((EditText)findViewById(R.id.phoneNumber)).getText().toString();
                countryCode =((com.hbb20.CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode();

                addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                        +"&user[cellphone]="+phoneNumber
                        +"&user[country_code]="+countryCode+"&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

                /** 2.Add the user to the Authy API **/
                JSONObject obj = new JSONObject();
                // post call for Authy api to add a user | response contains the added user's id
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    /** get the returned id **/
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();
                                    //Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
                                    /** 3.Call the Authy API to generate a QRCode:  will be handled in QRCodeActivity **/
                                    // and pass it to next activity
                                    Intent qrCodeIntent = new Intent(getApplicationContext(), QRCodeActivity.class);
                                    qrCodeIntent.putExtra("userId",addedUserId);
                                    startActivity(qrCodeIntent);
                                    /** 4. validating user provided key: will be handled in QRCodeActivity**/
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
                (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);



            }
        });

        /*************************************************************************************************
         *                       Using text messages *
         *  **********************************************************************************************/
        (findViewById(R.id.twofaViaText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** 1.Ask the user for their phone number **/
                phoneNumber =((EditText)findViewById(R.id.phoneNumber)).getText().toString();
                countryCode =((com.hbb20.CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode();

                addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                        +"&user[cellphone]="+phoneNumber
                        +"&user[country_code]="+countryCode+"&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

                /** 2.Add the user to the Authy API **/
                JSONObject obj = new JSONObject();
                // post call for Authy api to add a user | response contains the added user's id
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    /** get the returned id **/
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();
                                   // Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
                                    /** 3.call the Authy API to send a code through sms **/
                                    /** 4.call the Authy API to validate code provided by user [embedded in sendSecurityCodeTo method **/
                                    sendSecurityCodeTo(addedUserId);
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
                (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);
            }
        });


        /*************************************************************************************************
         *                       Using an authentication app in the same device *
         *  **********************************************************************************************/
        (findViewById(R.id.twofaViaPhone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** 1.Ask the user for their phone number **/
                phoneNumber =((EditText)findViewById(R.id.phoneNumber)).getText().toString();
                countryCode =((com.hbb20.CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode();

                addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                        +"&user[cellphone]="+phoneNumber
                        +"&user[country_code]="+countryCode+"&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

                /** 2.Add the user to the Authy API **/
                JSONObject obj = new JSONObject();
                // post call for Authy api to add a user | response contains the added user's id
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    /** get the returned id **/
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();
                                    // Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
                                    /** 3.Call the Authy API to generate appropriate passcode
                                     * then open GoogleAuthenticator on this device to use it ! **/
                                    String uri = "otpauth://totp/AdsChain:" + email + "?secret=" + "811854" + "&issuer=AdsChain";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                    /** 4.Ask user for passcode and validate it **/
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(TwoFactorAuthActivity.this);
                                    alertDialog.setTitle("Validate security code");
                                    alertDialog.setMessage("Enter the code you received in sms");

                                    final EditText input = new EditText(TwoFactorAuthActivity.this);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    alertDialog.setView(input);
                                    alertDialog.setPositiveButton("Validate",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    /** call authy api to validate code provided by the user **/
                                                    validateSecurityCode(input.getText().toString(),addedUserId,username,TwoFactorAuthActivity.this);
                                                }
                                            });

                                    alertDialog.setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    alertDialog.show();
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
                (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);
            }
        });



    }

    private void sendSecurityCodeTo(final String userId){
        JSONObject obj = new JSONObject();
        String getCodeSMS="https://api.authy.com/protected/json/sms/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx&force=true";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,getCodeSMS,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("success").equals("true"));
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(TwoFactorAuthActivity.this);
                            alertDialog.setTitle("Validate security code");
                            alertDialog.setMessage("Enter the code you received in sms");

                            final EditText input = new EditText(TwoFactorAuthActivity.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            alertDialog.setView(input);
                            alertDialog.setPositiveButton("Validate",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            /** call authy api to validate code provided by the user **/
                                            validateSecurityCode(input.getText().toString(),userId,username,TwoFactorAuthActivity.this);
                                        }
                                    });

                            alertDialog.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            alertDialog.show();
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
        (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);
    }

    public static void validateSecurityCode(String code, final String userId, final String username, final Context context){
        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+code+"/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
        JSONObject obj = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if((response.getString("token")).equals("is valid"))
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
                                                Toast.makeText(context, "Successfully registered!", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        context.startActivity(new Intent(context, SigninActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "ERROR! "+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            else
                                Toast.makeText(context, "You typed a wrong code!", Toast.LENGTH_LONG).show();
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
        (AppSingleton.getInstance(context).getRequestQueue()).add(jsObjRequest);
    }





}
