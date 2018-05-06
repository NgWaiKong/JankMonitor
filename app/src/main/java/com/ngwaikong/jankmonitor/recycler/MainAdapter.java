package com.ngwaikong.jankmonitor.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ngwaikong.jankmonitor.recycler.BaseAdapterHelper;
import com.ngwaikong.jankmonitor.recycler.BaseSimpleAdapter;

import java.util.List;

import com.ngwaikong.jankmonitor.R;

/**
 * Created by weijiangwu on 2018/5/5.
 */

public class MainAdapter extends BaseSimpleAdapter<String, BaseAdapterHelper> {
    protected MainAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, String item, final int position) {
        helper.setText(R.id.text_view, item);
        helper.setOnClickListener(R.id.text_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
    }

    public MainAdapter(Context context, int layoutResId, List data) {
        super(context, layoutResId, data);
    }

    protected MainAdapter(Context context, MultiItemTypeSupport multiItemTypeSupport) {
        super(context, multiItemTypeSupport);
    }

    protected MainAdapter(Context context, MultiItemTypeSupport multiItemTypeSupport, List data) {
        super(context, multiItemTypeSupport, data);
    }


}
