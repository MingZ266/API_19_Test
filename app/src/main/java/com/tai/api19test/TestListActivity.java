package com.tai.api19test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestListActivity extends AppCompatActivity {
    private ListView testList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        initView();
        myProcess();
    }

    private void initView() {
        testList = findViewById(R.id.testList);
    }

    @SuppressLint("SetTextI18n")
    private void myProcess() {
        List<View> viewList = new ArrayList<>();
        View item;
        for (int i = 0; i < 100; i++) {
            item = View.inflate(TestListActivity.this, R.layout.list_item_test_layout, null);
            ((TextView) item.findViewById(R.id.name)).setText("名称" + i);
            ((TextView) item.findViewById(R.id.ability)).setText("作用" + i);
            viewList.add(item);
        }
        testList.setAdapter(new TestListAdapter(viewList));
    }

    private static class TestListAdapter extends BaseAdapter {
        private List<View> viewList;

        TestListAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object getItem(int position) {
            return viewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = viewList.get(position);
            return convertView;
        }
    }
}