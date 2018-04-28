package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.Utils.AppSingleton;

public class QRCodeActivity extends AppCompatActivity {

    private String qrCodeCallUrl,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        /** get auth creds from previous activity **/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId= extras.getString("userId");
        }
        qrCodeCallUrl="https://api.authy.com/protected/json/users/"+userId+"/secret?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

        /** call authy api to get qr code **/
        JSONObject obj = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,qrCodeCallUrl,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String qrCodePath = response.getString("qr_code");
                           // Toast.makeText(getApplicationContext(), "Code: "+qrCodePath, Toast.LENGTH_LONG).show();
                            /** set the imageView's src **/
                            ImageView qrCodeImgVw = findViewById(R.id.qrCodeImgVw);
                            Picasso.get().load(qrCodePath).into(qrCodeImgVw);
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

        /** pass the code provided by user to the Authy API to verify it **/
        (findViewById(R.id.confirmSignupBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoFactorAuthActivity.validateSecurityCode(((EditText)findViewById(R.id.validationCode)).getText().toString(),userId,"",QRCodeActivity.this);
               /** String codeValidationUrl="https://api.authy.com/protected/json/verify/"+
                        ((EditText)findViewById(R.id.validationCode)).getText().toString()+"/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
                JSONObject obj = new JSONObject();
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if((response.getString("token")).equals("is valid"))
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
                (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);**/
            }
        });
    }


}
