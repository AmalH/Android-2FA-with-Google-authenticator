package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.Utils.AppSingleton;

public class TwoFactorAuthActivity extends AppCompatActivity {

    private String email,password,username,phoneNumber,countryCode,addUserUrl,qrCodeCallUrl;
    private static String addedUserId;

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


        /************** Using an authentication app in another device ******************/
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
                RequestQueue queue =  AppSingleton.getInstance(getApplicationContext()).getRequestQueue();
                // post call for Authy api to add a user that return the added user's id
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    // get the returned id
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();
                                   // Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
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

                /** 3.Call the Authy API to generate a QRCode **/
                // and pass it to next activity because it will be user
                qrCodeCallUrl="https://api.authy.com/protected/json/users/80749027/secret?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
                Intent qrCodeIntent = new Intent(getApplicationContext(), QRCodeActivity.class);
                qrCodeIntent.putExtra("qrCodeCallUrl",qrCodeCallUrl);
                startActivity(qrCodeIntent);

            }
        });

        /************** Using an authentication app in the same device ******************/
        (findViewById(R.id.twofaViaPhone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 2.Add the user to the Authy API **/

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
                /** 3.Call the Authy API to generate appropriate passcode
                 * then open GoogleAuthenticator on this device to use it ! **/

                /** 4.Ask user for passcode entered, validate it and register user to firebase **/
            }
        });

        /************** Using text messages ******************/
        (findViewById(R.id.twofaViaText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 2.Add the user to the Authy API **/

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
                /** 3.Call the Authy API to generate appropriate passcode
                 * then open GoogleAuthenticator on this device to use it ! **/

                /** 4.Ask user for passcode entered, validate it and register user to firebase **/
            }
        });
    }



}
