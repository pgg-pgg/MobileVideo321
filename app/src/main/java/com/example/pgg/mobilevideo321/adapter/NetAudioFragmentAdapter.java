package com.example.pgg.mobilevideo321.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.bean.NetMusicItem;
import com.example.pgg.mobilevideo321.global.MyApplication;
import com.example.pgg.mobilevideo321.utils.TimeUtils;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by pgg on 18-6-17.
 */

public class NetAudioFragmentAdapter extends BaseAdapter {

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;




    private Context context;
    private NetMusicItem netMusicItem;

    public NetAudioFragmentAdapter(Context context, NetMusicItem netMusicItem) {
        this.context=context;
        this.netMusicItem=netMusicItem;
    }


    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        String type = netMusicItem.getList().get(position).getType();
        int itemViewType=-1;
        if ("video".equals(type)){
            itemViewType=TYPE_VIDEO;
        }else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广播
        }
        return itemViewType;
    }

    @Override
    public int getCount() {
        return netMusicItem.getList().size();
    }

    @Override
    public NetMusicItem.ListBean getItem(int position) {
        return netMusicItem.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView==null){
            //初始化
            //初始化item布局
            viewHolder = new ViewHolder();
            convertView = initView(convertView, itemViewType, viewHolder);
            //初始化公共的视图
            initCommonView(convertView, itemViewType, viewHolder);
            //设置tag
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        NetMusicItem.ListBean listBean = netMusicItem.getList().get(position);
        bindData(itemViewType,viewHolder,listBean);

        return convertView;
    }


    private void bindData(int itemViewType, ViewHolder viewHolder, NetMusicItem.ListBean mediaItem) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                bindData(viewHolder, mediaItem);
                //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
                viewHolder.jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0), mediaItem.getVideo().getThumbnail().get(0), null);
                viewHolder.tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(TimeUtils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");

                break;
            case TYPE_IMAGE://图片
                bindData(viewHolder, mediaItem);
                viewHolder.iv_image_icon.setImageResource(R.drawable.bg_item);
                int  height = mediaItem.getImage().getHeight()<= MyApplication.screenWidth*0.75?mediaItem.getImage().getHeight(): (int) (MyApplication.screenHeight * 0.75);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MyApplication.screenWidth,height);
                viewHolder.iv_image_icon.setLayoutParams(params);
                if(mediaItem.getImage() != null &&  mediaItem.getImage().getBig()!= null&&mediaItem.getImage().getBig().size() >0){
                    Glide.with(context).load(mediaItem.getImage().getBig().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.bg_item).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.iv_image_icon);
                }
                break;
            case TYPE_TEXT://文字
                bindData(viewHolder, mediaItem);
                break;
            case TYPE_GIF://gif
                bindData(viewHolder, mediaItem);
                System.out.println("mediaItem.getGif().getImages().get(0)" + mediaItem.getGif().getImages().get(0));
                Glide.with(context).load(mediaItem.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_image_gif);

                break;
            case TYPE_AD://软件广告
                break;
        }
        //设置文本
        viewHolder.tv_context.setText(mediaItem.getText());
    }

    private void initCommonView(View convertView, int itemViewType, ViewHolder viewHolder) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
            case TYPE_IMAGE://图片
            case TYPE_TEXT://文字
            case TYPE_GIF://gif
                //加载除开广告部分的公共部分视图
                //user info
                viewHolder.iv_headpic =  convertView.findViewById(R.id.iv_headpic);
                viewHolder.tv_name =  convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time_refresh =  convertView.findViewById(R.id.tv_time_refresh);
                viewHolder.iv_right_more =  convertView.findViewById(R.id.iv_right_more);
                //bottom
                viewHolder.iv_video_kind =  convertView.findViewById(R.id.iv_video_kind);
                viewHolder.tv_video_kind_text =  convertView.findViewById(R.id.tv_video_kind_text);
                viewHolder.tv_shenhe_ding_number =  convertView.findViewById(R.id.tv_shenhe_ding_number);
                viewHolder.tv_shenhe_cai_number =  convertView.findViewById(R.id.tv_shenhe_cai_number);
                viewHolder.tv_posts_number =  convertView.findViewById(R.id.tv_posts_number);
                viewHolder.ll_download =  convertView.findViewById(R.id.ll_download);

                break;
        }


        //中间公共部分 -所有的都有
        viewHolder.tv_context =  convertView.findViewById(R.id.tv_context);
    }

    private View initView(View convertView, int itemViewType, ViewHolder viewHolder) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                convertView = View.inflate(context, R.layout.all_video_item, null);
                //在这里实例化特有的
                viewHolder.tv_play_nums =  convertView.findViewById(R.id.tv_play_nums);
                viewHolder.tv_video_duration =  convertView.findViewById(R.id.tv_video_duration);
                viewHolder.iv_commant =  convertView.findViewById(R.id.iv_commant);
                viewHolder.tv_commant_context =  convertView.findViewById               (R.id.tv_commant_context);
                viewHolder.jcv_videoplayer =  convertView.findViewById(R.id.jcv_videoplayer);
                break;
            case TYPE_IMAGE://图片
                convertView = View.inflate(context, R.layout.all_image_item, null);
                viewHolder.iv_image_icon =  convertView.findViewById(R.id.iv_image_icon);
                break;
            case TYPE_TEXT://文字
                convertView = View.inflate(context, R.layout.all_text_item, null);
                break;
            case TYPE_GIF://gif
                convertView = View.inflate(context, R.layout.all_gif_item, null);
                viewHolder.iv_image_gif =  convertView.findViewById(R.id.iv_image_gif);
                break;
            case TYPE_AD://软件广告
                convertView = View.inflate(context, R.layout.all_ad_item, null);
                viewHolder.btn_install =  convertView.findViewById(R.id.btn_install);
                viewHolder.iv_image_icon =  convertView.findViewById(R.id.iv_image_icon);
                break;
        }
        return convertView;
    }

    private void bindData(ViewHolder viewHolder, NetMusicItem.ListBean mediaItem) {
        if(mediaItem.getU()!=null&& mediaItem.getU().getHeader()!=null&&mediaItem.getU().getHeader().get(0)!=null){
            Glide.with(context).load(mediaItem.getU().getHeader().get(0)).error(R.drawable.empty_icon).into(viewHolder.iv_headpic);
        }
        if(mediaItem.getU() != null&&mediaItem.getU().getName()!= null){
            viewHolder.tv_name.setText(mediaItem.getU().getName()+"");
        }

        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());

        //设置标签
        List<NetMusicItem.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp());
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        viewHolder.tv_posts_number.setText(mediaItem.getForward()+"");

    }

    static class ViewHolder {
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
        TextView tv_play_nums;
        TextView tv_video_duration;
        ImageView iv_commant;
        TextView tv_commant_context;
        JCVideoPlayer jcv_videoplayer;
        //Image
        ImageView iv_image_icon;
        //Gif
        GifImageView iv_image_gif;

        //软件推广
        Button btn_install;
    }
}
