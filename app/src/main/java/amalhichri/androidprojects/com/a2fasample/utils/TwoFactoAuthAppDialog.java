package amalhichri.androidprojects.com.a2fasample.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.a2fasample.R;
import amalhichri.androidprojects.com.a2fasample.activities.QRCodeActivity;
import amalhichri.androidprojects.com.a2fasample.models.User;

public class TwoFactoAuthAppDialog extends Dialog {

    private Context context;
    private static String email,password;
    private String username, phoneNumber, countryCode,addUserUrl,addedUserId;
    private int style;

    public TwoFactoAuthAppDialog (Context context,int style) {
        super(context);
        this.context=context;
        this.style = style;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twofactorauth_app_ui);


        /*************************************************************************************************
         *                       2FA using Authenticator app on another device *
         *  **********************************************************************************************/

        (findViewById(R.id.authAppOnOtherDevice)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /** 1.Get user's creds! phone number included.. **/
                        email = (dataSnapshot.getValue(User.class)).getEmailAddress();
                        username = (dataSnapshot.getValue(User.class)).getFirstName()+" "+(dataSnapshot.getValue(User.class)).getLastName();
                        // password= (dataSnapshot.getValue(User.class)).getPassword();
                        // password= (dataSnapshot.getValue(User.class)).getPassword();
                        phoneNumber = (dataSnapshot.getValue(User.class)).getPhoneNumber();
                        countryCode = (dataSnapshot.getValue(User.class)).getPhoneCountryCode();
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
                                            Intent qrCodeIntent = new Intent( getContext().getApplicationContext(), QRCodeActivity.class);
                                            qrCodeIntent.putExtra("userId",addedUserId);
                                            dismiss();
                                            getContext().startActivity(qrCodeIntent);
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
                        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });

        /*************************************************************************************************
         *                       2FA using Authenticator app on this device *
         *  **********************************************************************************************/
        (findViewById(R.id.authAppOnThisPhone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /** 1.Get user's creds! phone number included.. **/
                        email = (dataSnapshot.getValue(User.class)).getEmailAddress();
                        username = (dataSnapshot.getValue(User.class)).getFirstName()+" "+(dataSnapshot.getValue(User.class)).getLastName();
                        // password= (dataSnapshot.getValue(User.class)).getPassword();
                        // password= (dataSnapshot.getValue(User.class)).getPassword();
                        phoneNumber = (dataSnapshot.getValue(User.class)).getPhoneNumber();
                        countryCode = (dataSnapshot.getValue(User.class)).getPhoneCountryCode();
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
                                            /** 3.Call the Authy API to generate appropriate passcode
                                             * then open GoogleAuthenticator on this device to use it ! **/
                                            String uri = "otpauth://totp/AdsChain:" + email + "?secret=" + "811854" + "&issuer=AdsChain";
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                            getContext().startActivity(intent);
                                            /** 4.Ask user for passcode and validate it **/
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                            alertDialog.setTitle("Validate security code");
                                            alertDialog.setMessage("Enter the code you received in sms");

                                            final EditText input = new EditText(getContext());
                                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT);
                                            input.setLayoutParams(lp);
                                            alertDialog.setView(input);
                                            alertDialog.setPositiveButton("Validate",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            /** call authy api to validate code provided by the user **/
                                                            Statics.validateSecurityCode(input.getText().toString(),addedUserId,getContext());
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
                        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });
    }




}