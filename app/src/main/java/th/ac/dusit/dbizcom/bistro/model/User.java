package th.ac.dusit.dbizcom.bistro.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    public final int id;
    @SerializedName("name")
    public final String name;
    @SerializedName("email")
    public final String email;
    @SerializedName("created_at")
    public final String createdAt;

    public User(int id, String name, String email, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
