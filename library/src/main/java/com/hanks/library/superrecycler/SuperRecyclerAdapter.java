package com.hanks.library.superrecycler;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hanks.library.LoadingListItemCreator;
import com.hanks.library.helper.ItemTouchHelperAdapter;
import com.hanks.library.helper.OnStartDragListener;

/**
 * created by hanks
 */
public class SuperRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private static final int ITEM_VIEW_TYPE_LOADING = Integer.MAX_VALUE - 50; // Magic

    private RecyclerView.Adapter   wrappedAdapter         = null;
    private LoadingListItemCreator loadingListItemCreator = null;
    private OnStartDragListener    mDragStartListener     = null;

    private boolean displayLoadingRow = true;

    public SuperRecyclerAdapter(RecyclerView.Adapter adapter, LoadingListItemCreator creator) {
        this.wrappedAdapter = adapter;
        this.loadingListItemCreator = creator;
    }

    public SuperRecyclerAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_LOADING) {
            return loadingListItemCreator.onCreateViewHolder(parent, viewType);
        } else {
            return wrappedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (isLoadingRow(position)) {
            loadingListItemCreator.onBindViewHolder(holder, position);
        } else {
            // Start a drag whenever the handle view it touched
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        if (mDragStartListener != null) mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            wrappedAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override public int getItemCount() {
        return displayLoadingRow ? wrappedAdapter.getItemCount() + 1 : wrappedAdapter.getItemCount();
    }

    @Override public int getItemViewType(int position) {
        return isLoadingRow(position) ? ITEM_VIEW_TYPE_LOADING : wrappedAdapter.getItemViewType(position);
    }

    @Override public long getItemId(int position) {
        return isLoadingRow(position) ? RecyclerView.NO_ID : wrappedAdapter.getItemId(position);
    }

    @Override public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        wrappedAdapter.setHasStableIds(hasStableIds);
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return wrappedAdapter;
    }

    public boolean isDisplayLoadingRow() {
        return displayLoadingRow;
    }

    public void displayLoadingRow(boolean displayLoadingRow) {
        if (this.displayLoadingRow != displayLoadingRow) {
            this.displayLoadingRow = displayLoadingRow;
            notifyDataSetChanged();
        }
    }

    public boolean isLoadingRow(int position) {
        return displayLoadingRow && position == getLoadingRowPosition();
    }

    private int getLoadingRowPosition() {
        return displayLoadingRow ? getItemCount() - 1 : -1;
    }

    @Override public boolean onItemMove(int fromPosition, int toPosition) {

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override public void onItemDismiss(int position) {
        notifyItemRemoved(position);
    }
}
