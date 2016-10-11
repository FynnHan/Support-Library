package code.support.demo.Test.test6ment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Arrays;

import code.support.demo.Test.TestActivity_6;

public class FragmentCategory extends Fragment {

    private String clothes[] = {"All", "Socks", "Hat", "Glove", "Briefs", "Jacket"};
    private ListView listView;
    private FilterAdapter adapter;
    private TestActivity_6 mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (TestActivity_6) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        return listView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setDividerHeight(0);
        adapter = new FilterAdapter(getActivity(), Arrays.asList(clothes));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.onFilter(1, clothes[position]);
                adapter.setCheckItem(position);
            }
        });
    }
}
