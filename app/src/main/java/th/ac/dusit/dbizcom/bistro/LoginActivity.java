package th.ac.dusit.dbizcom.bistro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.bistro.etc.MyPrefs;
import th.ac.dusit.dbizcom.bistro.etc.Utils;
import th.ac.dusit.dbizcom.bistro.model.User;
import th.ac.dusit.dbizcom.bistro.net.ApiClient;
import th.ac.dusit.dbizcom.bistro.net.LoginResponse;
import th.ac.dusit.dbizcom.bistro.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.bistro.net.WebServices;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 10001;
    static final String KEY_EMAIL = "email";

    private String mEmail, mPassword;

    private EditText mEmailEditText, mPasswordEditText;
    private Button mLoginButton;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mProgressView = findViewById(R.id.progress_view);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            }
        });

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    Utils.hideKeyboard(LoginActivity.this);
                    doLogin();
                } else {
                    Utils.showShortToast(LoginActivity.this, "กรอกข้อมูลให้ครบ");
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        mEmail = mEmailEditText.getText().toString().trim();
        if (mEmail.isEmpty()) {
            mEmailEditText.setText("");
            mEmailEditText.setError("กรอกอีเมล");
            valid = false;
        }
        mPassword = mPasswordEditText.getText().toString().trim();
        if (mPassword.isEmpty()) {
            mPasswordEditText.setText("");
            mPasswordEditText.setError("กรอกรหัสผ่าน");
            valid = false;
        }

        return valid;
    }

    private void doLogin() {
        mProgressView.setVisibility(View.VISIBLE);
        mLoginButton.setEnabled(false);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<LoginResponse> call = services.login(mEmail, mPassword);
        call.enqueue(new MyRetrofitCallback<>(
                LoginActivity.this,
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse responseBody) {
                        if (responseBody.loginSuccess) { // login สำเร็จ
                            User user = responseBody.user;
                            // จำว่า user login แล้ว
                            MyPrefs.setUserPref(LoginActivity.this, user);

                            // แสดง toast
                            String msg = String.format(
                                    Locale.getDefault(),
                                    "ยินดีต้อนรับ %s",
                                    user.name
                            );
                            Utils.showShortToast(LoginActivity.this, msg);

                            // ไปหน้าหลัก
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            // ปิดหน้า login
                            finish();
                        } else { // login ไม่สำเร็จ
                            Utils.showOkDialog(LoginActivity.this, "เข้าสู่ระบบไม่สำเร็จ", "ชื่อผู้ใช้ หรือรหัสผ่าน ไม่ถูกต้อง", null);
                            mLoginButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) { // เกิดข้อผิดพลาด (เช่น ไม่มีเน็ต, server ล่ม)
                        Utils.showOkDialog(LoginActivity.this, "ผิดพลาด", errorMessage, null);
                        mLoginButton.setEnabled(true);
                    }
                }
        ));
    }
}
