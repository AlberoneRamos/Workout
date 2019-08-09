package com.alberoneramos.workout.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alberoneramos.workout.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PINActivity extends AppCompatActivity {

    @BindView(R.id.textView3)
    TextView instruction;
    @BindView(R.id.pinview)
    Pinview pin;
    @BindView(R.id.resend_sms)
    Button resendCodeButton;
    @BindView(R.id.pin_error_text)
    TextView pinErrorText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private final String TAG = "phoneAuth";
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);
        PINController pinController = new PINController(this);
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        mAuth = FirebaseAuth.getInstance();
        resendCodeButton.setPaintFlags(resendCodeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pin.setPinViewEventListener((pinview, fromUser) -> pinController.verifyPhoneNumberWithCode(mVerificationId, pinview.getValue()));
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("PhoneAuthActivity", "onVerificationCompleted:" + credential);

                pinController.signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        resendCodeButton.setOnClickListener(l -> pinController.resendVerificationCode(phoneNumber, mResendToken));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(getIntent().getStringExtra("PHONE_NUMBER_UNFORMATTTED"),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        instruction.append(" " + phoneNumber);
    }

    public void goBack(View v) {
        finish();
    }

    class PINController {
        Activity activity;

        PINController(Activity activity) {
            this.activity = activity;
        }

        void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, 60, TimeUnit.SECONDS, activity, mCallbacks, token);
        }

        void verifyPhoneNumberWithCode(String verificationId, String code) {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);
            } catch (Exception e) {
                pin.setValue("");
                YoYo.with(Techniques.Shake).playOn(findViewById(R.id.pinview));
                pinErrorText.setText(getResources().getString(R.string.invalid_pin));

            }
        }

        void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            pin.setValue(Objects.requireNonNull(credential.getSmsCode()));
                            Intent intent = new Intent(PINActivity.this, HomescreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            Handler handler = new Handler();
                            handler.postDelayed(() -> startActivity(intent), 500);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                pin.setValue("");
                                YoYo.with(Techniques.Shake).playOn(findViewById(R.id.pinview));
                                pinErrorText.setText(getResources().getString(R.string.invalid_pin));
                            }
                        }
                    });
        }
    }
}
