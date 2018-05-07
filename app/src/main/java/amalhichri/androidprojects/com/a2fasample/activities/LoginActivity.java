package amalhichri.androidprojects.com.a2fasample.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import amalhichri.androidprojects.com.a2fasample.R;
import amalhichri.androidprojects.com.a2fasample.models.User;
import amalhichri.androidprojects.com.a2fasample.utils.Enable2FAdialog;
import amalhichri.androidprojects.com.a2fasample.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        (findViewById(R.id.signUpTxtVw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });


    }

    /*****************************************************************************
     * * Firebase login
     * **************************************************************************/
  public void login(View view) {


      Statics.auth.signInWithEmailAndPassword(((EditText) findViewById(R.id.emailLoginTxt)).getText().toString(), ((EditText)findViewById(R.id.pswLoginTxt)).getText().toString())
              .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                          @Override
                          public void onSuccess(AuthResult authResult) {
                              Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                      String twoFactorAuthOn  = (dataSnapshot.getValue(User.class)).getTwoFactorAuthOn();
                                      /** if user has activated two factor authentication **/
                                      if(twoFactorAuthOn.equals("true")){
                                          SharedPreferences loggedUserPrefs = getSharedPreferences("2FA",0);
                                          SharedPreferences.Editor e=loggedUserPrefs.edit();
                                          e.putString("status","unfinished");
                                          e.commit();
                                          (new Enable2FAdialog(LoginActivity.this,R.style.TwoFADialogs)).show();
                                      }
                                      /** if user hasnt activated two factor authentication just  **/
                                      if(twoFactorAuthOn.equals("false")){
                                          SharedPreferences loggedUserPrefs = getSharedPreferences("2FA",0);
                                          SharedPreferences.Editor e=loggedUserPrefs.edit();
                                          e.putString("status","finished");
                                          e.commit();
                                         LoginActivity.this.startActivity(new Intent( LoginActivity.this, HomeActivity.class));
                                      }
                                  }
                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {
                                      throw databaseError.toException();
                                  }
                              });
                          }
                      }
              )
              .addOnCompleteListener
                      (LoginActivity.this, new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if (!task.isSuccessful()) {
                                  Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                              }
                          }
                      });
  }


    /*****************************************************************************
     * * Password recovery
     * **************************************************************************/
    public void resetPassword(View v) {
        if (((EditText)findViewById(R.id.emailLoginTxt)).getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please provide your email\nto send you password recovery info.", Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(((EditText)findViewById(R.id.emailLoginTxt)).getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "A password recovery email has been sent to "+((EditText)findViewById(R.id.emailLoginTxt)).getText().toString(), Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(getApplicationContext(), "No such email KotlinLearn database, please provide your email!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}