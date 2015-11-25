package com.hanks.library;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.hanks.library.helper.SimpleItemTouchHelperCallback;
import com.hanks.library.pullrefresh.PullRefreshLayout;
import com.hanks.library.superrecycler.SuperRecyclerAdapter;

import java.util.List;
/**
 * Created by hanks on 15-11-16.
 */
public class SuperRecyclerView extends FrameLayout {

    public static final int LAYOUT_MANAGER_LINEAR         = 0x11;
    public static final int LAYOUT_MANAGER_GRID           = 0x12;
    public static final int LAYOUT_MANAGER_STAGGERED_GRID = 0x13;

    protected int layoutManagerType;

    private PullRefreshLayout    mRefreshView;
    private RecyclerView         mRecyclerView;
    private SuperRecyclerAdapter mAdapter;

    private AttachView mAttachView;

    private boolean canLoadingMore   = true;
    private boolean canRefresh       = true;
    private boolean canLoadingFinish = true;

    private List          dataList;
    private SuperListener listener;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;
    /**
     * 最后一个可见的item的位置
     */
    private int   lastVisibleItemPosition;
    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

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

    /**
     * init child for this
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_superrecyclerview, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRefreshView = (PullRefreshLayout) findViewById(R.id.pull_refresh);
        mAttachView = (AttachView) findViewById(R.id.view_attach);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // add refreshListener
        mRefreshView.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                if (listener != null) {
                    mAttachView.hideAllView();
                    listener.onRefresh();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                        throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                switch (layoutManagerType) {
                    case LAYOUT_MANAGER_LINEAR:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case LAYOUT_MANAGER_GRID:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case LAYOUT_MANAGER_STAGGERED_GRID:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        if (lastPositions == null) {
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        }
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                }

            }

            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScrollState = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastVisibleItemPosition) >= totalItemCount - 1)) {
                    //Log.d(TAG, "is loading more");
                    if (listener != null) {
                        mAttachView.hideAllView();
                        listener.onLoadingMore();
                    }
                }
            }

        });

        mAttachView.hideAllView();
        mAttachView.setLisenter(new AttachView.AttchListener() {
            @Override public void onReadLoad(View reloadView) {
                if (listener != null) {
                    mRefreshView.setRefreshing(true);
                    listener.onReadLoad(reloadView);
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

    public void setAdapter(SuperRecyclerAdapter adapter) {
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void hideRefresh() {
        mRefreshView.setRefreshing(false);
    }

    public void setListener(SuperListener listener) {
        this.listener = listener;
    }

    public void showLoadingView() {
        mAttachView.showLoadingView();
    }

    public void showEmptyView() {
        mAttachView.showEmptyView();
    }

    public void showErrorView() {
        mAttachView.showErrorView();
    }

    private void hideAttachView() {
        mAttachView.hideAllView();
    }

    public void showRefresh() {
        hideAttachView();
        mRefreshView.setRefreshing(true);
    }

    public void hideAll() {
        hideAttachView();
        hideRefresh();
        hideLoadingMore();
    }

    public void showLoadingMore() {
        hideAttachView();
        if (mAdapter instanceof SuperRecyclerAdapter) {
            ((SuperRecyclerAdapter) mAdapter).displayLoadingRow(true);
        }
    }

    private void hideLoadingMore() {
        if (mAdapter instanceof SuperRecyclerAdapter) {
            ((SuperRecyclerAdapter) mAdapter).displayLoadingRow(false);
        }
    }

    public void setCanLoadingMore(boolean canLoadingMore) {
        if (mAdapter instanceof SuperRecyclerAdapter) {
            ((SuperRecyclerAdapter) mAdapter).displayLoadingRow(canLoadingMore);
        }
    }
}
