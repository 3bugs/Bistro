package th.ac.dusit.dbizcom.bistro.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import th.ac.dusit.dbizcom.bistro.model.BeverageCategory;

public class GetBeverageResponse extends BaseResponse {

    @SerializedName("data_list")
    public List<BeverageCategory> beverageCategoryList;

}
