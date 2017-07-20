package com.example.huyigong.route_nightrun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class NightRunActivity extends AppCompatActivity {

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_run);

        // 添加TabHost标签
        tabHost = (TabHost) findViewById(R.id.night_run_tabhost);
        tabHost.setup();
        // 创建视图
        View view_estimate = createTabView("评估", R.drawable.test); // 评估
        View view_run = createTabView("夜跑", R.drawable.running); // 夜跑
        View view_talk = createTabView("约跑", R.drawable.talk); // 约跑
        View view_safe = createTabView("报警", R.drawable.safe); // 报警

        tabHost.addTab(tabHost.newTabSpec("estimate").setIndicator(view_estimate).setContent(R.id.fragment_running_estimate));
        tabHost.addTab(tabHost.newTabSpec("run").setIndicator(view_run).setContent(R.id.fragment_running));
        tabHost.addTab(tabHost.newTabSpec("talk").setIndicator(view_talk).setContent(R.id.fragment_talk));
        tabHost.addTab(tabHost.newTabSpec("safe").setIndicator(view_safe).setContent(R.id.fragment_safe));
        tabHost.getTabWidget().setStripEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 生成Tab标签
     * @param title Tab标题
     * @param drawableId 图标ID
     * @return Tab视图
     */
    View createTabView(String title, int drawableId) {
        View view = getLayoutInflater().inflate(R.layout.tab_item_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_title);
        textView.setText(title);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_item_icon);
        imageView.setImageDrawable(getResources().getDrawable(drawableId, null));
        return view;
    }
}
