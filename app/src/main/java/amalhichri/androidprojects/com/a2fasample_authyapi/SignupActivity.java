package amalhichri.androidprojects.com.a2fasample_authyapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.link_singin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
            }
        });

        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupCredsIntent =new Intent(SignupActivity.this, TwoFactorAuthActivity.class);
                signupCredsIntent.putExtra("email",((EditText)findViewById(R.id.emailSignup)).getText().toString());
                signupCredsIntent.putExtra("password",((EditText)findViewById(R.id.passwordSignup)).getText().toString());
                signupCredsIntent.putExtra("username",((EditText)findViewById(R.id.userNameSignup)).getText().toString());
                startActivity(signupCredsIntent);
            }
        });

    }
}
