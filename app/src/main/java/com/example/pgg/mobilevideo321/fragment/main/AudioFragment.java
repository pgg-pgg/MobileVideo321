package com.example.pgg.mobilevideo321.fragment.main;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.MainActivity;
import com.example.pgg.mobilevideo321.base.BaseFragment;

/**
 * Created by pgg on 18-6-11.
 */

public class AudioFragment extends BaseFragment {


    public static AudioFragment newInstance(MainActivity mainActivity){
        AudioFragment fragment=new AudioFragment();
        return fragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_audio;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void managerArguments() {

    }
}