package th.ac.dusit.dbizcom.bistro.net;

import com.google.gson.annotations.SerializedName;

import th.ac.dusit.dbizcom.bistro.model.User;

public class LoginResponse extends BaseResponse {

    @SerializedName("login_success")
    public boolean loginSuccess;
    @SerializedName("user")
    public User user;
}
