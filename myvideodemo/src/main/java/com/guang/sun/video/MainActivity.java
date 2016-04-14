package com.guang.sun.video;

import java.io.IOException;
import java.lang.ref.SoftReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

public class MainActivity extends Activity implements OnItemClickListener {

    private static final String TAG = "MainActivity";
    ListView mList;
    private Cursor mCursor;
    private final SparseArray<SoftReference<ImageView>> mImageViewsToLoad = new SparseArray<SoftReference<ImageView>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = (ListView) findViewById(R.id.list);
        mList.setOnItemClickListener(this);
        mCursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Video.Media.DATE_MODIFIED + " desc");
        SimpleCursorAdapter adapter = new videoListAdapter(this,
                R.layout.video_listitem, mCursor,
                new String[]{MediaStore.Video.Media.TITLE},
                new int[]{R.id.video_title});
        mList.setAdapter(adapter);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // 扫描新多媒体文件,添加到数据库中
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://"
                        + Environment.getExternalStorageDirectory()
                        .getAbsolutePath())));
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (mCursor.moveToPosition(position)) {
            int index = -1;
            index = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = null;
            if (index >= 0) {
                path = mCursor.getString(index);
                try {
                    ClipUtil.clipVideo(path, 5, 15);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private static final class ViewHolder {
        /**
         * 视频名称
         */
        TextView titleView;
        /**
         * 视频时长
         */
        TextView durationView;
        /**
         * 文件大小
         */
        TextView sizeView;
    }

    private class videoListAdapter extends SimpleCursorAdapter {

        /*
         * constructor.
         */
        public videoListAdapter(Context context, int layout, Cursor c,
                                String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.titleView = (TextView) view
                        .findViewById(R.id.video_title);
                holder.durationView = (TextView) view
                        .findViewById(R.id.video_duration);
                holder.sizeView = (TextView) view.findViewById(R.id.video_size);
            }
            view.setTag(holder);
            final ImageView iv = (ImageView) view.findViewById(R.id.thumbnail);
            int index = -1;
            index = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = null;
            if (index >= 0) {
                path = mCursor.getString(index);
                Glide.with(MainActivity.this).load(path).into(iv);

            }
            index = -1;
            index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            String title = null;
            if (index >= 0) {
                title = cursor.getString(index);
                holder.titleView.setText(title);
            }
            index = -1;
            index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            int duration;
            if (index >= 0) {
                duration = cursor.getInt(index);
                holder.durationView.setText(duration+"");
            }
            index = -1;
            index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            long size;
            if (index >= 0) {
                size = cursor.getLong(index);
                holder.sizeView.setText(size + "");
            }
            return view;

        }

    }


}