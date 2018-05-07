package amalhichri.androidprojects.com.a2fasample.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.hbb20.CountryCodePicker;

import amalhichri.androidprojects.com.a2fasample.R;
import amalhichri.androidprojects.com.a2fasample.utils.Statics;

public class SignupActivity extends Activity {

    private static String twoFactorAuthOn="false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        setContentView(R.layout.activity_signup);
        ((ExpandableRelativeLayout) findViewById(R.id.phnNbrLayout)).collapse();

        // material editTexts error msg
        (findViewById(R.id.emailSignupTxt)).setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                    ((EditText) findViewById(R.id.emailSignupTxt)).setError("Password is incorrect.");
                return false;
            }
        });
        (findViewById(R.id.emailSignupTxt)).setOnFocusChangeListener(new View.OnFocusChangeListener(){
                                                                         @Override public void onFocusChange(    View v,    boolean hasFocus){
                                                                             if (hasFocus) ((EditText) findViewById(R.id.emailSignupTxt)).setError(null);
                                                                         }
                                                                     }
        );

        /** enabling two factor authentication **/
        ((CheckBox)findViewById(R.id.enable2FAchkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked ){
                    ((ExpandableRelativeLayout) findViewById(R.id.phnNbrLayout)).expand();
                    twoFactorAuthOn="true";
                }if(!isChecked){
                    ((ExpandableRelativeLayout) findViewById(R.id.phnNbrLayout)).collapse();
                    twoFactorAuthOn = "false";
                }
            }
        });

        /** navigate to login **/
        (findViewById(R.id.navToSignin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });

    }


    /*****************************************************************************
     * * Firebase signup
     * **************************************************************************/
    public void signUp(View v) {

        /** simple data validation ... */
        if (((EditText) findViewById(R.id.emailSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.emailSignupTxt)).setError("Email missing !");
            return;
        }
        if (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty())
                && (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().length() < 6)) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password should be at least 6 characters");
            return;
        }
        if (((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Full name missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty())
                && !(isFullName(((EditText) findViewById(R.id.fullNameTxt)).getText().toString()))) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Please provide your full name");
            return;
        }

        if ((((android.widget.EditText)findViewById(R.id.phoneNumberEdt)).getText().toString().isEmpty()) &&(twoFactorAuthOn.equals("true")))
            twoFactorAuthOn = "false";
        //authenticate user + add it to firebase DB ..
        Toast.makeText(getApplicationContext(), "Phone : "+((CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode()+((EditText) findViewById(R.id.phoneNumberEdt)).getText().toString(), Toast.LENGTH_LONG).show();
        Statics.signUp(((EditText) findViewById(R.id.emailSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.pswSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.fullNameTxt)).getText().toString(),
                twoFactorAuthOn,
                ((EditText) findViewById(R.id.phoneNumberEdt)).getText().toString(),
                ((CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode(),
                SignupActivity.this);
    }


    //
    private boolean isFullName(String s){
        int j=0;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==' ')
                j++;
        }
        if (j==1)
            return true;
        else
            return false;
    }
}
