package com.example.huyigong.route_nightrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huyigong.route_nightrun.substances.ShuoshuoModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.support.v7.widget.RecyclerView.*;
import static com.example.huyigong.route_nightrun.R.layout.recycler_item_shuoshuo;

public class ShuoshuoActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    ArrayList<ShuoshuoModel> mShuoshuoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuoshuo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 获取说说数据
        initData();
        // 初始化说说界面
        mRecyclerView = (RecyclerView) findViewById(R.id.shuoshuo_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new ShuoshuoAdapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
    }

    protected void initData() {
        mShuoshuoModels = new ArrayList<>();
        ShuoshuoModel model1 = new ShuoshuoModel();
        model1.setUserImage("my.jpg");
        model1.setUserName("HPDell");
        model1.setContent("今天跑步好开心");
        mShuoshuoModels.add(model1);
        ShuoshuoModel model2 = new ShuoshuoModel();
        model2.setUserImage("my.jpg");
        model2.setUserName("HPDell");
        model2.setContent("希望自己以后可以天天坚持跑步。");
        mShuoshuoModels.add(model2);
    }

    /**
     * 说说布局适配器
     */
    class ShuoshuoAdapter extends RecyclerView.Adapter<ShuoshuoAdapter.ShuoshuoViewHolder> {

        @Override
        public ShuoshuoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ShuoshuoViewHolder(LayoutInflater.from(ShuoshuoActivity.this).inflate(recycler_item_shuoshuo, parent, false));
        }

        @Override
        public void onBindViewHolder(ShuoshuoViewHolder holder, int position) {
            holder.mUserNameTextView.setText(mShuoshuoModels.get(position).getUserName());
            holder.mContentTextView.setText(mShuoshuoModels.get(position).getContent());
            try {
                holder.mUserImageView.setImageBitmap(new LoadHttpImageAsyncTask().execute(getString(R.string.static_resource_url_root) + "imgs/" + mShuoshuoModels.get(position).getUserImage()).get());
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(ShuoshuoActivity.this, "用户头像加载失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            String contentImageUrl = mShuoshuoModels.get(position).getContentImage();
            if (!(contentImageUrl != null && contentImageUrl.isEmpty())) {
                try {
                    holder.mContentImageView.setImageBitmap(new LoadHttpImageAsyncTask().execute(getString(R.string.static_resource_url_root) + "imgs/" + contentImageUrl).get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            holder.mToolsThumbButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ShuoshuoActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mShuoshuoModels.size();
        }

        /**
         * 说说的ViewHolder
         */
        class ShuoshuoViewHolder extends ViewHolder {

            ImageView mUserImageView;
            TextView mUserNameTextView;
            TextView mContentTextView;
            ImageView mContentImageView;
            Button mToolsThumbButton;

            public ShuoshuoViewHolder(View itemView) {
                super(itemView);
                mUserImageView = (ImageView) itemView.findViewById(R.id.shuoshuo_user_image);
                mUserNameTextView = (TextView) itemView.findViewById(R.id.shuoshuo_user_name);
                mContentTextView = (TextView) itemView.findViewById(R.id.shuoshuo_content);
                mContentImageView = (ImageView) itemView.findViewById(R.id.shuoshuo_content_image);
                mToolsThumbButton = (Button) itemView.findViewById(R.id.shuoshuo_tools_thumb);
            }
        }
    }

    /**
     * 异步加载网络上的图片
     * Created by huyigong on 2017/9/25.
     */
    public static class LoadHttpImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            URL url;
            Bitmap bitmap = null;
            try {
                url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                InputStream stream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
            } catch (Exception e) {
                Log.i("获取网络图片出错；", e.getMessage());
            }
            return bitmap;
        }
    }
}
