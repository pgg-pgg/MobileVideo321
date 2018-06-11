package com.example.pgg.mobilevideo321.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.IndicatorOptions;
import com.cleveroad.slidingtutorial.OnTutorialPageChangeListener;
import com.cleveroad.slidingtutorial.PageOptions;
import com.cleveroad.slidingtutorial.TransformItem;
import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageOptionsProvider;
import com.cleveroad.slidingtutorial.TutorialSupportFragment;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.SplashActivity;
import com.example.pgg.mobilevideo321.constant.Constant;
import com.example.pgg.mobilevideo321.utils.SPUtils;
import com.example.pgg.mobilevideo321.widget.DrawableRender;

/**
 * Created by pgg on 2018/5/3.
 */

public class CustomPresentationPagerFragment extends TutorialSupportFragment implements OnTutorialPageChangeListener {

    private static final String TAG="CustomPagerFragment";
    private static final int TOTAL_PAGES=3;
    private static final int ACTUAL_PAGES_COUNT=3;

    private final View.OnClickListener mOnSkipClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            SPUtils.put(getActivity(), Constant.FIRST_OPEN,true);
            getActivity().finish();
        }
    };
    private int[] pagesColors;

    private final TutorialPageOptionsProvider mTutorialPageOptionsProvider = new TutorialPageOptionsProvider() {
        @NonNull
        @Override
        public PageOptions provide(int position) {
            @LayoutRes int pageLayoutResId;
            TransformItem[] tutorialItems;
            position %= ACTUAL_PAGES_COUNT;
            switch (position) {
                case 0: {
                    pageLayoutResId = R.layout.fragment_page_first;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.LEFT_TO_RIGHT, 0.2f),
                            TransformItem.create(R.id.ivSecondImage, Direction.RIGHT_TO_LEFT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.LEFT_TO_RIGHT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.RIGHT_TO_LEFT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.RIGHT_TO_LEFT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.RIGHT_TO_LEFT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.RIGHT_TO_LEFT, 0.14f),
                            TransformItem.create(R.id.ivEighthImage, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 1: {
                    pageLayoutResId = R.layout.fragment_page_second;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.RIGHT_TO_LEFT, 0.2f),
                            TransformItem.create(R.id.ivSecondImage, Direction.LEFT_TO_RIGHT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.RIGHT_TO_LEFT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.LEFT_TO_RIGHT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.LEFT_TO_RIGHT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.LEFT_TO_RIGHT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.LEFT_TO_RIGHT, 0.14f),
                            TransformItem.create(R.id.ivEighthImage, Direction.LEFT_TO_RIGHT, 0.07f)
                    };
                    break;
                }
                case 2: {
                    pageLayoutResId = R.layout.fragment_page_third;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.RIGHT_TO_LEFT, 0.2f),
                            TransformItem.create(R.id.ivSecondImage, Direction.LEFT_TO_RIGHT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.RIGHT_TO_LEFT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.LEFT_TO_RIGHT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.LEFT_TO_RIGHT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.LEFT_TO_RIGHT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.LEFT_TO_RIGHT, 0.14f)
                    };
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown position: " + position);
                }
            }

            return PageOptions.create(pageLayoutResId, position, tutorialItems);
        }
    };
    @Override
    protected TutorialOptions provideTutorialOptions() {
        return newTutorialOptionsBuilder(getContext()).setUseInfiniteScroll(true)
                .setPagesColors(pagesColors).setPagesCount(TOTAL_PAGES)
                .setTutorialPageProvider(mTutorialPageOptionsProvider)
                .setIndicatorOptions(IndicatorOptions.newBuilder(getContext())
                        .setElementSizeRes(R.dimen.indicator_size)
                        .setElementSpacingRes(R.dimen.indicator_spacing)
                .setElementColorRes(android.R.color.darker_gray)
                .setSelectedElementColor(Color.LTGRAY)
                .setRenderer(DrawableRender.create(getContext())).build())
                .onSkipClickListener(mOnSkipClickListener)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pagesColors==null){
            pagesColors=new int[]{
                    ContextCompat.getColor(getContext(), android.R.color.holo_orange_dark),
                    ContextCompat.getColor(getContext(), android.R.color.holo_red_dark),
                    ContextCompat.getColor(getContext(), android.R.color.holo_purple),
                    ContextCompat.getColor(getContext(), android.R.color.holo_green_dark),
                    ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark),
                    ContextCompat.getColor(getContext(), android.R.color.darker_gray)
            };
        }
        addOnTutorialPageChangeListener(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.custom_tutorial_layout;
    }

    @Override
    public void onPageChanged(int position) {
        if (position== TutorialSupportFragment.EMPTY_FRAGMENT_POSITION){
            Log.i(TAG, "onPageChanged: Empty fragment is visible");
        }
    }
}
