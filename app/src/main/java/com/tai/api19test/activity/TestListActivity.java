package com.tai.api19test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tai.api19test.R;

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

    private void myProcess() {
        List<String> names = new ArrayList<>();
        List<String> abilities = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            names.add("名字" + (i + 1));
            abilities.add("作用" + (i + 1));
        }
        TestListAdapter adapter = new TestListAdapter(TestListActivity.this, names, abilities);
        testList.setAdapter(adapter);
    }

    private static class TestListAdapter extends BaseAdapter {
        private final Context context;
        private final List<String> names;
        private final List<String> abilities;

        TestListAdapter(Context context, List<String> names, List<String> abilities) {
            this.context = context;
            this.names = names;
            this.abilities = abilities;
        }

        @Override
        public int getCount() {// 条目个数
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView  = View.inflate(context, R.layout.list_item_test_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.name = convertView.findViewById(R.id.name);
                viewHolder.ability = convertView.findViewById(R.id.ability);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(names.get(position));
            viewHolder.ability.setText(abilities.get(position));

            return convertView;
        }

        private static class ViewHolder {
            TextView name;
            TextView ability;
        }
    }
}