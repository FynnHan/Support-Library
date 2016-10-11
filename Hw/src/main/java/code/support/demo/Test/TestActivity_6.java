package code.support.demo.Test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;
import code.support.demo.Test.test6ment.FragmentCategory;
import code.support.demo.Test.test6ment.FragmentFloor;
import code.support.demo.Test.test6ment.FragmentSort;
import code.support.demo.widget.dropdown.DropDownLayout;
import code.support.demo.widget.dropdown.DropDownTable;
import code.support.demo.widget.dropdown.DropMatter;

public class TestActivity_6 extends AppCompatActivity {

    private DropDownTable tabs;
    private ArrayList<DropDownTable.ITabEntity> mTabEntities = new ArrayList<>();

    private String[] mTitles = {"All Floor", "All Merchant", "All Category"};
    private int[] mIconUnselectIds = {
            R.mipmap.tab_floor_unselected,
            R.mipmap.tab_category_unseleted,
            R.mipmap.tab_sort_unseleted};
    private int[] mIconSelectIds = {
            R.mipmap.tab_floor_selected,
            R.mipmap.tab_category_seleted,
            R.mipmap.tab_floor_selected};

    private DropDownLayout dropDownLayout;
    private DropMatter dropMatter;

    private ShopAdapter shopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_06);

        tabs = (DropDownTable) findViewById(R.id.tabs);

        dropDownLayout = (DropDownLayout) findViewById(R.id.dropdown);
        dropMatter = (DropMatter) findViewById(R.id.contentView);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentFloor());
        fragments.add(new FragmentCategory());
        fragments.add(new FragmentSort());
        dropMatter.setFragmentManager(getSupportFragmentManager());
        dropMatter.bindFragments(fragments);

        tabs.setOnTabSelectListener(new DropDownTable.OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                dropDownLayout.showMatterAt(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (dropMatter.isShow()) {
                    dropDownLayout.closeMatter();
                } else {
                    dropDownLayout.showMatterAt(position);
                }
            }
        });

        shopAdapter = new ShopAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(shopAdapter);

        updateTabData();
    }

    private void updateTabData() {
        mTabEntities.clear();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabs.setTabData(mTabEntities);
    }

    public void onFilter(int type, String tag) {
        dropDownLayout.closeMatter();
        switch (type) {
            case 0:
                mTitles[0] = tag;
                break;
            case 1:
                mTitles[1] = tag;

                break;
            case 2:
                mTitles[2] = tag;
                break;
        }
        updateTabData();
    }

    public class TabEntity implements DropDownTable.ITabEntity {
        public String title;
        public int selectedIcon;
        public int unSelectedIcon;

        public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
            this.title = title;
            this.selectedIcon = selectedIcon;
            this.unSelectedIcon = unSelectedIcon;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public int getTabSelectedIcon() {
            return selectedIcon;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unSelectedIcon;
        }
    }

    public static class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHoler> {
        private String[] data = {"shop1", "shop2", "shop3", "shop4", "shop5", "shop5", "shop6", "shop6", "shop7", "shop8", "shop9", "shop10", "shop1", "shop1", "shop1", "shop1", "shop1", "shop8", "shop9", "shop10", "shop1", "shop1", "shop1", "shop1", "shop1", "shop8", "shop9", "shop10", "shop1", "shop1", "shop1", "shop1", "shop1"};

        @Override
        public ShopViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setHeight(300);
            return new ShopViewHoler(tv);
        }

        @Override
        public void onBindViewHolder(ShopViewHoler holder, int position) {
            holder.mTextView.setText(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        public static class ShopViewHoler extends RecyclerView.ViewHolder {

            public final TextView mTextView;

            public ShopViewHoler(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
            }
        }
    }
}
