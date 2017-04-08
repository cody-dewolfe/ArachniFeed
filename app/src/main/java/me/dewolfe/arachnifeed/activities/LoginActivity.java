package me.dewolfe.arachnifeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import me.dewolfe.arachnifeed.R;

public class LoginActivity extends AppCompatActivity {
    private TextView mNameTextView;
    private FirebaseAuth mFirebaseAuth;

    private Button mLoginButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private final String APP_NAME_FORMATTED = "<font color='#009688'>Arachni</font><font color='#FF5722'>Feed</font>"; //Primary Color + Accent Color
    private final String LOG_TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO: Implement correct Google and FaceBook sign in

        mNameTextView = (TextView) findViewById(R.id.app_name);
        mNameTextView.setText(Html.fromHtml(APP_NAME_FORMATTED), TextView.BufferType.SPANNABLE);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoginButton = (Button) findViewById(R.id.sign_in_email);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailEditText = (EditText) findViewById(R.id.email_et);
                mPasswordEditText = (EditText) findViewById(R.id.password_et);

                final String email = mEmailEditText.getText().toString();
                final String password = mPasswordEditText.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                //Sign the user up with the password provided
                                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                //If this happens, something has gone very wrong because the user shouldn't be able to even get to this stage if another user exists with that email!
                                                Log.wtf(LOG_TAG, "This really shouldn't have happened. Like really.");
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                mPasswordEditText.setError("Your password is too weak.");
                                                mPasswordEditText.requestFocus();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                mEmailEditText.setError("Please enter a valid email address.");
                                                mEmailEditText.requestFocus();
                                            } catch (Exception e) {
                                                Log.e(LOG_TAG, e.toString());
                                            }
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                //Their password is wrong
                                mPasswordEditText.setError("The username or password could not be found.");
                                mPasswordEditText.requestFocus();
                            } catch (Exception e) {
                                Log.e(LOG_TAG, e.toString());
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
