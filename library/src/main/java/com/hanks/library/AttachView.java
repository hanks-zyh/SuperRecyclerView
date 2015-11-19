package com.hanks.library;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 添加到界面中:可以显示加载中,数据为空,加载失败
 * Created by hanks on 15-11-17.
 */
public class AttachView extends FrameLayout {

    private View view_empty;
    private View view_error;
    private View view_loading;
    private View btn_reload;

    private AttchListener listener;

    public AttachView(Context context) {
        super(context);
        init();
    }

    public AttachView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AttachView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public AttachView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_attachview, this);
        setClickable(true);
        view_empty = findViewById(R.id.view_empty);
        view_error = findViewById(R.id.view_error);
        view_loading = findViewById(R.id.view_loading);
        btn_reload = findViewById(R.id.btn_reload);
        if (btn_reload != null) {
            btn_reload.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View view) {
                    if (listener != null) {
                        listener.onReadLoad(view);
                    }
                }
            });
        }
    }

    public void showLoadingView() {
        setVisibility(VISIBLE);
        view_empty.setVisibility(GONE);
        view_error.setVisibility(GONE);
        view_loading.setVisibility(VISIBLE);
    }

    public void showErrorView() {
        setVisibility(VISIBLE);
        view_empty.setVisibility(GONE);
        view_loading.setVisibility(GONE);
        view_error.setVisibility(VISIBLE);
    }

    public void showEmptyView() {
        setVisibility(VISIBLE);
        view_error.setVisibility(GONE);
        view_loading.setVisibility(GONE);
        view_empty.setVisibility(VISIBLE);
    }

    public void hideAllView() {
        view_error.setVisibility(GONE);
        view_loading.setVisibility(GONE);
        view_empty.setVisibility(GONE);
        setVisibility(GONE);
    }

    public void setLisenter(AttchListener lisenter) {
        this.listener = lisenter;
    }

    /**
     * @param emptyHint the hint text when showing Empty view
     */
    public void showEmptyView(String emptyHint) {
        if (view_empty instanceof TextView) {
            ((TextView) view_empty).setText(emptyHint);
        }
        showEmptyView();
    }

    public interface AttchListener {
        void onReadLoad(View reloadView);
    }
}
