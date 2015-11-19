package com.hanks.spuerrecyclerview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.library.LoadingListItemCreator;
import com.hanks.library.SuperListener;
import com.hanks.library.SuperRecyclerView;
import com.hanks.library.WrapperAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SuperRecyclerView superRecyclerView;

    private List<String> dataList = new ArrayList<>();
    private WrapperAdapter mAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        superRecyclerView = (SuperRecyclerView) findViewById(R.id.super_recyclerview);

        // wrapperAdapter for your adapter
        mAdapter = new WrapperAdapter(new MyAdapter(), LoadingListItemCreator.DEFAULT);
        superRecyclerView.setAdapter(mAdapter);

        superRecyclerView.setListener(new SuperListener() {
            @Override public void onRefresh() {
                new GetDataTask().execute(GetDataTask.TASK_TYPE_CLEAR);
            }

            @Override public void onLoadingMore() {
                new GetDataTask().execute(GetDataTask.TASK_TYPE_ADD);
            }

            @Override public void onReadLoad(View view) {
                new GetDataTask().execute(GetDataTask.TASK_TYPE_ADD);
            }
        });
        new GetDataTask().execute(GetDataTask.TASK_TYPE_ADD);
    }

    private void updateUI() {
        superRecyclerView.stopRefresh();
        mAdapter.notifyDataSetChanged();
        if (dataList.size() <= 0) {
            int random = new Random().nextInt(100);
            if (random > 50) {
                superRecyclerView.showErrorView();
            }else {
                superRecyclerView.showEmptyView();
            }
        }
    }

    class GetDataTask extends AsyncTask<Integer, Void, Void> {

        public static final int TASK_TYPE_ADD   = 0x10;   // add data
        public static final int TASK_TYPE_CLEAR = 0x11; // clear data

        @Override protected Void doInBackground(Integer... params) {
            try {
                if (params[0] == TASK_TYPE_ADD) {
                    int startIndex = dataList.size();
                    for (int i = startIndex; i < startIndex + 15; i++) {
                        dataList.add("this is a simple item: " + i);
                    }
                } else if (params[0] == TASK_TYPE_CLEAR) {
                    dataList.clear();
                }
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_simple_text, parent, false);
            return new MyHolder(view);
        }

        @Override public void onBindViewHolder(MyHolder holder, int position) {
            holder.tv.setText(dataList.get(position));
        }

        @Override public int getItemCount() {
            return dataList.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyHolder(final View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), dataList.get(getAdapterPosition()), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}
