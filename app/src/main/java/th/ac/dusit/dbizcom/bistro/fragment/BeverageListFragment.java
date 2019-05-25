package th.ac.dusit.dbizcom.bistro.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import th.ac.dusit.dbizcom.bistro.R;
import th.ac.dusit.dbizcom.bistro.model.Beverage;

public class BeverageListFragment extends Fragment {

    private static final String ARG_BEVERAGE_LIST_JSON = "beverage_list_json";

    private List<Beverage> mBeverageList;

    private BeverageListFragmentListener mListener;

    private ListView mBeverageListView;

    public BeverageListFragment() {
        // Required empty public constructor
    }

    public static BeverageListFragment newInstance(List<Beverage> beverageList) {
        BeverageListFragment fragment = new BeverageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BEVERAGE_LIST_JSON, new Gson().toJson(beverageList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String beverageListJson = getArguments().getString(ARG_BEVERAGE_LIST_JSON);
            mBeverageList = new Gson().fromJson(
                    beverageListJson, new TypeToken<List<Beverage>>() {
                    }.getType()
            );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beverage_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            mBeverageListView = view.findViewById(R.id.beverage_list_view);
            ArrayAdapter<Beverage> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    mBeverageList
            );
            mBeverageListView.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BeverageListFragmentListener) {
            mListener = (BeverageListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BeverageListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface BeverageListFragmentListener {
    }
}
