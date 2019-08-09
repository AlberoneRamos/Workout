package com.alberoneramos.workout.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.controller.NavigationManager;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.editText_carrierNumber)
    EditText editTextCarrierNumber;
    @BindView(R.id.phoneInput)
    TextInputLayout phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
    }

    public void sendMessage(View view) {
        if (TextUtils.isEmpty(this.editTextCarrierNumber.getText().toString())) {
            phoneNumber.setError(getResources().getString(R.string.empty_phone_input));
        } else if (!ccp.isValidFullNumber()) {
            phoneNumber.setError(getResources().getString(R.string.invalid_phone_number));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("PHONE_NUMBER", this.ccp.getFormattedFullNumber());
            bundle.putString("PHONE_NUMBER_UNFORMATTTED", this.ccp.getFullNumberWithPlus());
            NavigationManager.openActivity(this,PINActivity.class,bundle);
        }
    }

}
