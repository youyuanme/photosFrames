package com.sibozn.peo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibozn.peo.R;

/**
 * Created by Administrator on 2016/11/18.
 */

public abstract class BaseFragment extends Fragment {

    protected String TAG = this.getClass().getSimpleName();

    public abstract View onCreateThisFragment(LayoutInflater layoutInflater, ViewGroup container);

    protected View viewFragment;

    protected abstract void initData();

    protected abstract void initViews(View viewFragment);

    protected AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        viewFragment = onCreateThisFragment(inflater, container);
        return viewFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews(viewFragment);
        initData();
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.xiaoxitishi));
        builder.setCancelable(false);
    }

}
