package com.example.pgg.mobilevideo321.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.pgg.mobilevideo321.R;
import com.example.pgg.mobilevideo321.bean.MediaItem;

import java.util.List;

/**
 * Created by pgg on 18-6-12.
 */

public class VideoFragmentAdapter extends BaseAdapter {

    List<MediaItem> mediaItems;
    Context context;

    public VideoFragmentAdapter(Context context, List<MediaItem> mediaItems) {
        this.mediaItems=mediaItems;
        this.context=context;
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
        if (convertView==null){
            convertView=View.inflate(context, R.layout.item_video_page,null);
        }
        return null;
    }
}
