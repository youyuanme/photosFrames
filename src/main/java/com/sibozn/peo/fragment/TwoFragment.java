package com.sibozn.peo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.sibozn.peo.R;
import com.sibozn.peo.activity.DetailActivity;
import com.sibozn.peo.adapter.EffectsRecycleAdapter;
import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.view.FullyGridLayoutManager;
import com.sibozn.peo.view.MyScrollview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TwoFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private final int TOP_PICTURE_SIZE = 5;

    @BindView(R.id.slider)
    SliderLayout slider;
    @BindView(R.id.tv_effects)
    TextView tv_effects;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.my_scrollview)
    MyScrollview my_scrollview;

    private Context mContext;
    private List<Beans> effectsList, effectsTopic;

    public void setEffectsList(List<Beans> effectsList) {
        this.effectsList = effectsList;
    }

    @Override
    public View onCreateThisFragment(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_two, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initViews(View viewFragment) {
        mContext = getActivity();
    }

    @Override
    protected void initData() {
        if (effectsList != null && effectsList.size() > 0) {
            effectsTopic = new ArrayList<Beans>();
            for (int i = 0; i < effectsList.size(); i++) {
                effectsTopic.add(effectsList.remove(i));
                if (i >= TOP_PICTURE_SIZE) {
                    break;
                }
            }
        } else {
            tv_effects.setVisibility(View.GONE);
            return;
        }
        for (int i = 0; i < effectsTopic.size(); i++) {
            Beans beans = effectsTopic.get(i);
            TextSliderView textSliderView = new TextSliderView(mContext);
            textSliderView.getView().findViewById(R.id.description_layout).setBackground(null);
            // initialize a SliderLayout
            textSliderView
                    .description(beans.name)
                    .image(beans.topic)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            //add your extra information
            Bundle bundle = new Bundle();
            bundle.putString("beans", beans.toString());
            textSliderView.bundle(bundle);
            slider.addSlider(textSliderView);
        }
        slider.setPresetTransformer(SliderLayout.Transformer.Default);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(4000);
        slider.addOnPageChangeListener(this);

        final FullyGridLayoutManager manager = new FullyGridLayoutManager(mContext, 3);
        //GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        //LinearLayoutManager manager = new LinearLayoutManager(this);
        recycle_view.setLayoutManager(manager);
        //recycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recycle_view.setItemAnimator(new DefaultItemAnimator());
        EffectsRecycleAdapter detailRecyclerViewAdapter = new EffectsRecycleAdapter(mContext, effectsList);
        detailRecyclerViewAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Beans beans = (Beans) view.getTag();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("beans", beans.toString());
                startActivity(intent);
            }
        });
        recycle_view.setAdapter(detailRecyclerViewAdapter);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onStop() {
        super.onStop();
        slider.stopAutoCycle();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("beans", slider.getBundle().getString("beans"));
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
