package com.jazart.symphony;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class BottomNavAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments;
    private final SparseArray<WeakReference<Fragment>> instantiatedFrags;

    BottomNavAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        instantiatedFrags = new SparseArray<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);

    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFrags.put(position, new WeakReference<>(fragment));
        return fragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        instantiatedFrags.remove(position);
        super.destroyItem(container, position, object);
    }
}
