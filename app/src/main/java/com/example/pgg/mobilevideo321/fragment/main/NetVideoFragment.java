package com.example.pgg.mobilevideo321.fragment.main;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.activity.MainActivity;
import com.example.pgg.mobilevideo321.base.BaseFragment;

/**
 * Created by pgg on 18-6-11.
 */

public class NetVideoFragment extends BaseFragment {

    public static NetVideoFragment newInstance(MainActivity mainActivity){
        NetVideoFragment fragment=new NetVideoFragment();
        return fragment;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_net_video;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void managerArguments() {

    }
}
