package com.example.pgg.mobilevideo321.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.bean.NetVideoItem;
import com.example.pgg.mobilevideo321.utils.TimeUtils;

import java.util.List;

/**
 * Created by pgg on 18-6-14.
 */

public class NetVideoFragmentAdapter extends BaseAdapter {

    private Context context;
    private List<NetVideoItem.TrailersBean> netVideoItems;

    public NetVideoFragmentAdapter(Context context, List<NetVideoItem.TrailersBean> netVideoItem) {
        this.context=context;
        this.netVideoItems =netVideoItem;
    }

    @Override
    public int getCount() {
        return netVideoItems.size();
    }

    @Override
    public NetVideoItem.TrailersBean getItem(int position) {
        return netVideoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetVideoViewHolder holder;
        NetVideoItem.TrailersBean trailersBean = netVideoItems.get(position);
        if (convertView==null){
            convertView=View.inflate(context, R.layout.item_net_video_page,null);
            holder=new NetVideoViewHolder();
            holder.iv_icon=convertView.findViewById(R.id.iv_icon);
            holder.tv_video_title=convertView.findViewById(R.id.tv_video_title);
            holder.tv_summary=convertView.findViewById(R.id.tv_summary);
            holder.tv_video_type=convertView.findViewById(R.id.tv_video_type);
            holder.tv_video_length=convertView.findViewById(R.id.tv_video_length);
            convertView.setTag(holder);
        }else {
            holder= (NetVideoViewHolder) convertView.getTag();
        }
        Glide.with(context)
                .load(trailersBean.getCoverImg())
                .error(R.drawable.errorview)
                .into(holder.iv_icon);
        holder.tv_video_length.setText(TimeUtils.stringForTime(trailersBean.getVideoLength()*1000));
        StringBuilder builder=new StringBuilder();
        for (int i=0;i<trailersBean.getType().size();i++){
            builder.append(trailersBean.getType().get(i)+" ");
        }
        holder.tv_video_type.setText(builder.toString());
        holder.tv_summary.setText(trailersBean.getSummary());
        holder.tv_video_title.setText(trailersBean.getVideoTitle());
        return convertView;
    }


    static class NetVideoViewHolder{
        ImageView iv_icon;
        TextView tv_video_title;
        TextView tv_summary;
        TextView tv_video_length;
        TextView tv_video_type;
    }
}
