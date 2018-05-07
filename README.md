# Two-factor authentication in Android - Using Authy API
This is a sample for implementing two-factor authentication in Android using [Authy API](https://www.twilio.com/docs/authy/api)

## Description

You can use this project and the following tutorials to implement:

* ### Two-factor authentication using Google authenticator on a different device (using QR codes)

	Tutorial: [Android - Implementing two-step authentication through Google authenticator](https://www.pragmatictheories.tech/android-implementing-two-step-authentication-through-google-authenticator)

![2faQr](https://raw.githubusercontent.com/AmalH/Android-2FA-with-Google-authenticator/master/screenshots/2fagoogleAuthenticator1.png)


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

	Tutorial: [Android - Implementing two-step authentication through Google authenticator](https://www.pragmatictheories.tech/android-implementing-two-step-authentication-through-google-authenticator)

![2faOnSameDevice](https://raw.githubusercontent.com/AmalH/Android-2FA-with-Google-authenticator/master/screenshots/2fagoogleAuthenticator2.png)

	
```java
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
```
 

* ### Two-factor authentication using SMS code
	* Code snippet:
```javascript
..................
```

## Getting started

Clone this repository and import into Android Studio
```
git clone https://github.com/AmalH/Android-2FA-with-Google-authenticator.git
```
### Pre-requisites
* Android SDK 27
* Android Build Tools v27.0.3
* Android Support Repository
