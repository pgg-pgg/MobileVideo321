package com.example.pgg.mobilevideo321.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.bean.MediaItem;
import com.example.pgg.mobilevideo321.global.MyApplication;
import com.example.pgg.mobilevideo321.utils.TimeUtils;

import java.util.List;

/**
 * Created by pgg on 18-6-12.
 */

public class VideoFragmentAdapter extends BaseAdapter {

    List<MediaItem> mediaItems;
    boolean isVideo;

    Context context;
    public VideoFragmentAdapter(Context context, List<MediaItem> mediaItems,boolean isVideo) {
        this.mediaItems=mediaItems;
        this.context=context;
        this.isVideo=isVideo;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public MediaItem getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView=View.inflate(context, R.layout.item_video_page,null);
            holder=new ViewHolder();
            holder.iv_icon=convertView.findViewById(R.id.iv_icon);
            holder.tv_name=convertView.findViewById(R.id.tv_name);
            holder.tv_size=convertView.findViewById(R.id.tv_size);
            holder.tv_time=convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        holder.tv_name.setText(mediaItem.getName());
        holder.tv_time.setText(TimeUtils.stringForTime((int) mediaItem.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        if (!isVideo){
            holder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_size;
        TextView tv_time;

    }
}
