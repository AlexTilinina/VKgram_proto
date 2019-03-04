package ru.kpfu.itis.vkgram.ui.auth.telegram;

import static ru.kpfu.itis.vkgram.utils.Constants.CODE_SENT;
import static ru.kpfu.itis.vkgram.utils.Constants.T_TAG;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.arellomobile.mvp.presenter.InjectPresenter;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.ui.base.BaseActivity;

public class TelegramAuthActivity extends BaseActivity implements TelegramAuthView, OnClickListener{

    private Toolbar toolbar;
    private EditText etPhoneNumber;
    private EditText etCode;
    private EditText etFirstName;
    private EditText etLastName;
    private Button btnSingIn;

    private boolean codeSent;

    @InjectPresenter
    TelegramAuthPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_telegram_auth, frameLayout);
        initViews();
        codeSent = getIntent().getBooleanExtra(CODE_SENT, false);
        Log.d(T_TAG, "onCreate: " + codeSent);
    }

    public static void start(@NonNull Activity activity, boolean phone) {
        Intent intent = new Intent(activity, TelegramAuthActivity.class);
        intent.putExtra(CODE_SENT, !phone);
        Log.d(T_TAG, "start: " + !phone);
        activity.startActivity(intent);
    }

    @Override
    public void changeViewSingUp() {
        etCode.setVisibility(View.VISIBLE);
//        etFirstName.setVisibility(View.VISIBLE);
//        etLastName.setVisibility(View.VISIBLE);
        btnSingIn.setText(R.string.sing_in);
        etPhoneNumber.setVisibility(View.GONE);
        codeSent = true;
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        //TODO регистрация, пока только авторизация
        if (!codeSent){
            presenter.getAuthCode(etPhoneNumber.getText().toString());
        }
        else {
            if (etFirstName.getText().toString().isEmpty() || etLastName.getText().toString().isEmpty()) {
                presenter.sendAuthCode(etCode.getText().toString(), this);
            } else {
                presenter.sendAuthCode(etCode.getText().toString(), etFirstName.getText().toString(),
                        etLastName.getText().toString(), this);
            }

        }
    }

    @Override
    public void changeViewSingIn(){
        etCode.setVisibility(View.VISIBLE);
        btnSingIn.setText(R.string.sing_in);
        etPhoneNumber.setVisibility(View.GONE);
        codeSent = true;
    }

    private void initViews(){
        toolbar = findViewById(R.id.tb_tg_auth);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etCode = findViewById(R.id.et_code);
        btnSingIn = findViewById(R.id.btn_sing_in);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        btnSingIn.setOnClickListener(this);
        supportActionBar(toolbar);
        setBackArrow(toolbar);
        if (codeSent){
            changeViewSingIn();
        }
    }
}
