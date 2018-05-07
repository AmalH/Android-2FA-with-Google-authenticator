# Two-factor authentication in Android - Using Authy API
This is a sample for implementing two-factor authentication in Android using [Authy API](https://www.twilio.com/docs/authy/api)

## Description

You can use this project and the following tutorials to implement:

* ### Two-factor authentication using Google authenticator on a different device (using QR codes)

	
![2faQr](https://raw.githubusercontent.com/AmalH/Android-2FA-with-Google-authenticator/master/screenshots/2fagoogleAuthenticator1.png)

Tutorial: [Authy API](https://www.twilio.com/docs/authy/api)

```Java
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
                Statics.validateSecurityCode(((EditText)findViewById(R.id.validationCode)).getText().toString(),userId,QRCodeActivity.this,
                        ((EditText)findViewById(R.id.validationCode)),((TextView)findViewById(R.id.errorTxt)));
            }
        });
```
	

* ### Two-factor authentication using Google authenticator on user device
    * Code snippet:
```javascript
..................
```
    * Screenshot:
    * Tutorial:

* ### Two-factor authentication using SMS code
	* Code snippet:
```javascript
..................
```
    * Screenshot:
	
    * Tutorial:

## Getting started

Clone this repository and import into Android Studio
```javascript
git clone https://github.com/AmalH/Android-2FA-with-Google-authenticator.git
```
### Pre-requisites
* Android SDK 27
* Android Build Tools v27.0.3
* Android Support Repository
