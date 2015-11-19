package com.hanks.library;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.hanks.library.pullrefresh.PullRefreshLayout;

import java.util.List;
/**
 *
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


    public static final int LAYOUT_MANAGER_LINEAR  =  0x11;
    public static final int LAYOUT_MANAGER_GRID  =  0x12;
    public static final int LAYOUT_MANAGER_STAGGERED_GRID  =  0x13;

    /**
     * layoutManager的类型（枚举）
     */
    protected int layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;
/*    *//**
     * 是否正在加载
     *//*
    private boolean isLoadingMore = false;*/

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;


    /**
     * init child for this
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_superrecyclerview, this);
        //        mLoadingView = findViewById(R.id.layout_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRefreshView = (PullRefreshLayout) findViewById(R.id.pull_refresh);
        //        mEmptyView = findViewById(R.id.empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // add refreshListener
        mRefreshView.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //  int lastVisibleItemPosition = -1;
                if (layoutManagerType == 0) {
                    if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_LINEAR;
                    } else if (layoutManager instanceof GridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_GRID;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_STAGGERED_GRID;
                    } else {
                        throw new RuntimeException(
                                "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                switch (layoutManagerType) {
                    case LAYOUT_MANAGER_LINEAR:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                                .findLastVisibleItemPosition();
                        break;
                    case LAYOUT_MANAGER_GRID:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                                .findLastVisibleItemPosition();
                        break;
                    case LAYOUT_MANAGER_STAGGERED_GRID:
                        StaggeredGridLayoutManager staggeredGridLayoutManager
                                = (StaggeredGridLayoutManager) layoutManager;
                        if (lastPositions == null) {
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        }
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScrollState = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastVisibleItemPosition) >= totalItemCount - 1)) {
                    //Log.d(TAG, "is loading more");
                    if(listener!=null)listener.onLoadingMore();
                }
            }

        });
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
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
