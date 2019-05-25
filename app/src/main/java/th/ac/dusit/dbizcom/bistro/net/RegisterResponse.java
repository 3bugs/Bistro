package th.ac.dusit.dbizcom.bistro.net;

import com.google.gson.annotations.SerializedName;

import th.ac.dusit.dbizcom.bistro.model.User;

public class RegisterResponse extends BaseResponse {

    @SerializedName("user")
    public User user;
}
