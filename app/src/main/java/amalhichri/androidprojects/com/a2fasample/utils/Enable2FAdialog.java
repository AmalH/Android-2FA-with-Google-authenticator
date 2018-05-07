package amalhichri.androidprojects.com.a2fasample.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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
import amalhichri.androidprojects.com.a2fasample.models.User;


public class Enable2FAdialog extends Dialog {

    private static String email, password;
    private String username, phoneNumber, countryCode, addUserUrl, addedUserId;
    private Activity context;
    private int style;

    public Enable2FAdialog(Activity a, int style) {
        super(a,style);
        this.context = a;
        this.style = style;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.enable_2fa_dialog_ui);


        /*************************************************************************************************
         *                      2FA using text messages *
         *  **********************************************************************************************/
        (findViewById(R.id.smsOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /** 1.Get user's creds! phone number included.. **/
                        email = (dataSnapshot.getValue(User.class)).getEmailAddress();
                        username = (dataSnapshot.getValue(User.class)).getFirstName() + " " + (dataSnapshot.getValue(User.class)).getLastName();
                        // password= (dataSnapshot.getValue(User.class)).getPassword();
                        phoneNumber = (dataSnapshot.getValue(User.class)).getPhoneNumber();
                        countryCode = (dataSnapshot.getValue(User.class)).getPhoneCountryCode();
                        addUserUrl = "https://api.authy.com/protected/json/users/new?user[email]=" + email
                                + "&user[cellphone]=" + phoneNumber
                                + "&user[country_code]=" + countryCode + "&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

                        /** 2.Add the user to the Authy API **/
                        JSONObject obj = new JSONObject();
                        // post call for Authy api to add a user | response contains the added user's id
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, addUserUrl, obj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Gson gson = new Gson();
                                        try {
                                            /** get the returned id **/
                                            JsonObject addedUser = gson.fromJson(response.getString("user"), JsonObject.class);
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
                                        Log.e("ERROR! ", "ee: " + error.getMessage());
                                    }
                                });
                        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
                //countryCode =((com.hbb20.CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode();
            }
        });

        /*************************************************************************************************
         *                       2FA using Authenticator app *
         *  **********************************************************************************************/

        (findViewById(R.id.authAppOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                (new TwoFactoAuthAppDialog(getContext(),R.style.TwoFADialogs)).show();
            }
        });
    }

    /*************************************************************************************************
     *                       helpers *
     *  **********************************************************************************************/


    private void sendSecurityCodeTo(final String userId) {

        JSONObject obj = new JSONObject();
        String getCodeSMS = "https://api.authy.com/protected/json/sms/" + userId + "?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx&force=true";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getCodeSMS, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismiss();
                        final Dialog dialog = new Dialog(getContext(), R.style.TwoFADialogs);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.validate2fasms_ui);
                        dialog.findViewById(R.id.validateBtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /** call authy api to validate code provided by the user **/
                                Statics.validateSecurityCode(((EditText)dialog.findViewById(R.id.codeEdtx)).getText().toString(),userId,getContext()
                                        ,((EditText)dialog.findViewById(R.id.codeEdtx)),((TextView)dialog.findViewById(R.id.errorTxt)));                            }
                        });
                        dialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ", error.getMessage());
                    }
                });
        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);


    }
}
