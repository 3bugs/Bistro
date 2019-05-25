package th.ac.dusit.dbizcom.bistro.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.bistro.R;
import th.ac.dusit.dbizcom.bistro.adapter.BeverageCategoryListPagerAdapter;
import th.ac.dusit.dbizcom.bistro.etc.Utils;
import th.ac.dusit.dbizcom.bistro.model.BeverageCategory;
import th.ac.dusit.dbizcom.bistro.net.ApiClient;
import th.ac.dusit.dbizcom.bistro.net.GetBeverageResponse;
import th.ac.dusit.dbizcom.bistro.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.bistro.net.WebServices;

public class MenuFragment extends Fragment {

    private MenuFragmentListener mListener;

    private List<BeverageCategory> mBeverageCategoryList = null;

    private View mProgressView;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        if (mBeverageCategoryList == null) {
            doGetBeverage(view);
        } else {
            setupViewPager(view);
        }
    }

    private void doGetBeverage(final View view) {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetBeverageResponse> call = services.getBeverage();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetBeverageResponse>() {
                    @Override
                    public void onSuccess(GetBeverageResponse responseBody) {
                        mBeverageCategoryList = responseBody.beverageCategoryList;
                        setupViewPager(view);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                    }
                }
        ));
    }

    private void setupViewPager(View view) {
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        final BeverageCategoryListPagerAdapter adapter = new BeverageCategoryListPagerAdapter(
                getChildFragmentManager(),
                getContext(),
                mBeverageCategoryList
        );
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                BeverageListFragment fragment =
                        (BeverageListFragment) adapter.getRegisteredFragment(position);
                //todo:
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuFragmentListener) {
            mListener = (MenuFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MenuFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MenuFragmentListener {
    }
}
