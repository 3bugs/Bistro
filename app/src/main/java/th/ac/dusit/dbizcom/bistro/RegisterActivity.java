package th.ac.dusit.dbizcom.bistro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.bistro.etc.Utils;
import th.ac.dusit.dbizcom.bistro.model.User;
import th.ac.dusit.dbizcom.bistro.net.ApiClient;
import th.ac.dusit.dbizcom.bistro.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.bistro.net.RegisterResponse;
import th.ac.dusit.dbizcom.bistro.net.WebServices;

import static th.ac.dusit.dbizcom.bistro.LoginActivity.KEY_EMAIL;

public class RegisterActivity extends AppCompatActivity {

    private String mName, mEmail, mPassword, mConfirmPassword;

    private EditText mNameEditText, mEmailEditText;
    private EditText mPasswordEditText, mConfirmPasswordEditText;
    private Button mRegisterButton;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameEditText = findViewById(R.id.name_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);

        mProgressView = findViewById(R.id.progress_view);

        mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    Utils.hideKeyboard(RegisterActivity.this);
                    doRegister();
                } else {
                    Utils.showShortToast(RegisterActivity.this, "กรอกข้อมูลให้ครบ");
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        mConfirmPassword = mConfirmPasswordEditText.getText().toString().trim();
        if (mConfirmPassword.length() == 0) {
            mConfirmPasswordEditText.setText("");
            mConfirmPasswordEditText.setError("กรอกรหัสผ่านอีกครั้งเพื่อยืนยัน");
            valid = false;
        }
        mPassword = mPasswordEditText.getText().toString().trim();
        if (mPassword.length() == 0) {
            mPasswordEditText.setText("");
            mPasswordEditText.setError("กรอกรหัสผ่าน");
            valid = false;
        }
        if (mPassword.length() > 0 && mConfirmPassword.length() > 0
                && !mPassword.equals(mConfirmPassword)) {
            mConfirmPasswordEditText.setError("กรอกยืนยันรหัสผ่านให้ตรงกัน");
            valid = false;
        }
        mName = mNameEditText.getText().toString().trim();
        if (mName.length() == 0) {
            mNameEditText.setText("");
            mNameEditText.setError("กรอกชื่อ");
            valid = false;
        }
        mEmail = mEmailEditText.getText().toString().trim();
        if (mEmail.length() == 0) {
            mEmailEditText.setText("");
            mEmailEditText.setError("กรอกอีเมล");
            valid = false;
        } else if (!Utils.isValidEmail(mEmail)) {
            mEmailEditText.setError("รูปแบบอีเมลไม่ถูกต้อง");
            valid = false;
        }

        return valid;
    }

    private void doRegister() {
        mProgressView.setVisibility(View.VISIBLE);
        mRegisterButton.setEnabled(false);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<RegisterResponse> call = services.register(
                mName, mEmail, mPassword
        );
        call.enqueue(new MyRetrofitCallback<>(
                RegisterActivity.this,
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<RegisterResponse>() {
                    @Override
                    public void onSuccess(RegisterResponse responseBody) { // register สำเร็จ
                        User user = responseBody.user;
                        // ส่ง email ที่ register สำเร็จ กลับไปแสดงในหน้า login
                        Intent intent = new Intent();
                        intent.putExtra(KEY_EMAIL, user.email);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) { // register ไม่สำเร็จ หรือเกิดข้อผิดพลาดอื่นๆ (เช่น ไม่มีเน็ต, server ล่ม)
                        Utils.showOkDialog(RegisterActivity.this, "ผิดพลาด", errorMessage, null);
                        mRegisterButton.setEnabled(true);
                    }
                }
        ));
    }
}
