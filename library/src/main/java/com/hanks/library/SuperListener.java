package com.hanks.library;
import android.view.View;
/**
 *
 * Created by hanks on 15-11-16.
 */
public interface SuperListener {

    void onRefresh();
    void onLoadingMore();
    void onReadLoad(View view);
}
