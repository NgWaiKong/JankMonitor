package com.ngwaikong.jankmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ngwaikong.jankmonitor.recycler.BaseSimpleAdapter;
import com.ngwaikong.jankmonitor.recycler.MainAdapter;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.title_view)).setText("Main");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> list = initDatas();
        final MainAdapter mainAdapter = new MainAdapter(this, R.layout.recycler_item_layout, list);
        mainAdapter.setOnItemClickListener(new BaseSimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String item = mainAdapter.getItem(position);
                initClick(item);
            }
        });
        recyclerView.setAdapter(mainAdapter);
    }

    @NonNull
    private ArrayList<String> initDatas() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("framecallback");
        list.add("sampler");
        return list;
    }

    private void initClick(String item) {
        if (item.equals("framecallback")) {
            Intent intent = new Intent(this, FrameCallbackActivity.class);
            startActivity(intent);
        }
    }
}
