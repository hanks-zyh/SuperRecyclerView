package com.hanks.library;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.hanks.library.pullrefresh.PullRefreshLayout;

import java.util.List;
/**
 * Created by hanks on 15-11-16.
 */
public class SuperRecyclerView extends FrameLayout {

    private PullRefreshLayout mRefreshView;
    private RecyclerView      mRecyclerView;
    private View              mLoadingView;
    private View              mEmptyView;
    private View              mLoadingMoreView;
    private View              mHeaderView;
    private View              mFinishLoadView;

    private boolean canLoadingMore   = true;
    private boolean canRefresh       = true;
    private boolean canLoadingFinish = true;

    private RecyclerView.Adapter adapter;

    private List dataList;

    private SuperListener listener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public SuperRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRecyclerView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_superrecyclerview, this);
        //        mLoadingView = findViewById(R.id.layout_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRefreshView = (PullRefreshLayout) findViewById(R.id.pull_refresh);
        //        mEmptyView = findViewById(R.id.empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRefreshView.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                if(listener!=null){
                    listener.onRefresh();
                }
            }
        });
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }

    public void stopRefresh() {
        mRefreshView.setRefreshing(false);
    }

    public void setListener(SuperListener listener) {
        this.listener = listener;
    }
}
