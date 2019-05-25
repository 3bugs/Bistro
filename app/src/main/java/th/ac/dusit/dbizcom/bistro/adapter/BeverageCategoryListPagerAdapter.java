package th.ac.dusit.dbizcom.bistro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import th.ac.dusit.dbizcom.bistro.fragment.BeverageListFragment;
import th.ac.dusit.dbizcom.bistro.model.BeverageCategory;

public class BeverageCategoryListPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = BeverageCategoryListPagerAdapter.class.getName();

    private Context mContext;
    private List<BeverageCategory> mBeverageCategoryList;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    public BeverageCategoryListPagerAdapter(FragmentManager fm, Context context,
                                            List<BeverageCategory> beverageCategoryList) {
        super(fm);
        mContext = context;
        mBeverageCategoryList = beverageCategoryList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem(): " + position);

        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mBeverageCategoryList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return BeverageListFragment.newInstance(mBeverageCategoryList.get(position).beverageList);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mBeverageCategoryList.get(position).name;
    }
}
