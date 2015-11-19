package com.hanks.spuerrecyclerview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private SuperRecyclerView superRecyclerView;

    private List<String> dataList = new ArrayList<>();
    private WrapperAdapter mAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        superRecyclerView = (SuperRecyclerView) findViewById(R.id.super_recyclerview);
        mAdapter = new WrapperAdapter(new MyAdapter(), LoadingListItemCreator.DEFAULT);
        superRecyclerView.setAdapter(mAdapter);

        superRecyclerView.setListener(new SuperListener() {
            @Override public void onRefresh() {
                new JustSleepTask().execute();
            }

            @Override public void onLoadingMore() {
                Log.v("tag", "onLoadingMore");
                addData();
                new JustSleepTask().execute();
            }
        });

        addData();

    }

    private void addData() {// add data
        int startIndex = dataList.size();
        for (int i = startIndex; i < startIndex + 15; i++) {
            dataList.add("this is a simple item: " + i);
        }
    }

    class JustSleepTask extends AsyncTask<Void, Void, Void> {

        @Override protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            superRecyclerView.stopRefresh();
            mAdapter.notifyDataSetChanged();
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
