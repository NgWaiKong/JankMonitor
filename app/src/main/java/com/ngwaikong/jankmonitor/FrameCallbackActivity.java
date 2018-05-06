package com.ngwaikong.jankmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ngwaikong.jankmonitor.recycler.BaseSimpleAdapter;
import com.ngwaikong.jankmonitor.recycler.MainAdapter;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import ngwaikong.com.jankmonitor.core.BaseFrameCallback;
import ngwaikong.com.jankmonitor.core.ChoreographerManager;
import ngwaikong.com.jankmonitor.toolbox.framecallback.AverageFrameCallback;
import ngwaikong.com.jankmonitor.toolbox.framecallback.DropFrameCallback;
import ngwaikong.com.jankmonitor.toolbox.framecallback.SkipFrameCallback;
import ngwaikong.com.jankmonitor.toolbox.framecallback.UnitFrameCallback;

/**
 * Created by weijiangwu on 2018/5/5.
 */

public class FrameCallbackActivity extends Activity {
    private static final String TAG = "FrameCallbackActivity";
    private static final String AVERAGE = "average";
    private static final String DROP_FRAME = "drop_frame";
    private static final String SKIP_FRAME = "skip_frame";
    private static final String UNIT_FRAME = "unit_frame";
    private static final String COST_TEST = "cost_test";
    private static final String FRAME_CALLBACK = "FrameCallback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.title_view)).setText(FRAME_CALLBACK);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> list = initDataList();
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
    private ArrayList<String> initDataList() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add(AVERAGE);
        list.add(DROP_FRAME);
        list.add(SKIP_FRAME);
        list.add(UNIT_FRAME);
        list.add(COST_TEST);
        return list;
    }

    private void initClick(String item) {
        Log.i(TAG, "initClick: item:" + item);
        if (item.equals(COST_TEST)) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        BaseFrameCallback callback;
        switch (item) {
            case AVERAGE:
                callback = initAverageFrameCallback();
                break;
            case DROP_FRAME:
                callback = initDropFrameCallback();
                break;
            case SKIP_FRAME:
                callback = initSkipFrameCallback();
                break;
            case UNIT_FRAME:
                callback = initUnitFrameCallback();
                break;
            default:
                callback = customBaseFrameCallback();
                break;
        }
        ChoreographerManager.INSTANCE.start(callback);
    }

    @NonNull
    private BaseFrameCallback customBaseFrameCallback() {
        BaseFrameCallback callback;
        callback = new BaseFrameCallback() {
            @Override
            public void onDoFrame(long frameTimeNanos) {
                Log.i(TAG, "onDoFrame: custom base frame callback");
            }
        };
        return callback;
    }

    @NonNull
    private BaseFrameCallback initUnitFrameCallback() {
        BaseFrameCallback callback;
        callback = new UnitFrameCallback(1000);
        ((UnitFrameCallback) callback).setOnUnitFrameFunc(new Function2<Integer, ArrayList<Long>, Unit>() {
            @Override
            public Unit invoke(Integer smoothness, ArrayList<Long> list) {
                Log.i(TAG, "invoke: smoothness:" + smoothness + ",list:" + list.size());
                return null;
            }
        });
        return callback;
    }

    @NonNull
    private BaseFrameCallback initSkipFrameCallback() {
        BaseFrameCallback callback;
        callback = new SkipFrameCallback();
        ((SkipFrameCallback) callback).setOnDoFrameListener(new Function4<Integer, Long, Long, ArrayList<Long>, Unit>() {
            @Override
            public Unit invoke(Integer skipFrameCount, Long cur, Long last, ArrayList<Long> list) {
                Log.i(TAG, "invoke: skipFrameCount:" + skipFrameCount + ",cur:" + cur + ",last:" + last + ",list.size:" + list.size());
                return null;
            }
        });
        return callback;
    }

    @NonNull
    private BaseFrameCallback initDropFrameCallback() {
        BaseFrameCallback callback;
        callback = new DropFrameCallback(200);
        ((DropFrameCallback) callback).setOnDropFramesListener(new Function3<Long, Long, Long, Unit>() {
            @Override
            public Unit invoke(Long interval, Long cur, Long last) {
                Log.i(TAG, "invoke: interval:" + interval + ",cur:" + cur + ",last:" + last);
                return null;
            }
        });
        return callback;
    }

    @NonNull
    private BaseFrameCallback initAverageFrameCallback() {
        BaseFrameCallback callback;
        callback = new AverageFrameCallback();
        ((AverageFrameCallback) callback).setOnAverageListener(new Function2<Integer, ArrayList<Long>, Unit>() {
            @Override
            public Unit invoke(Integer average, ArrayList<Long> list) {
                Log.i(TAG, "invoke: average:" + average + ",list:" + list.size());
                return null;
            }
        });
        return callback;
    }
}
