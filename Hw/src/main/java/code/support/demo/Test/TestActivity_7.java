package code.support.demo.Test;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import code.support.demo.R;
import code.support.demo.widget.expand.Expandable;
import code.support.demo.widget.expand.ExpandableListener;
import code.support.demo.widget.expand.LayoutLinear;
import code.support.demo.widget.expand.LayoutRelative;
import code.support.demo.widget.expand.LayoutWeight;

public class TestActivity_7 extends AppCompatActivity {

    private LayoutWeight mWeight;
    private LayoutRelative mRelative_1;
    private TextView mOverlayText;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    private LayoutRelative mRelative_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_07);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWeight = (LayoutWeight) findViewById(R.id.layout_weight);
        mRelative_1 = (LayoutRelative) findViewById(R.id.layout_relative_1);
        mOverlayText = (TextView) findViewById(R.id.overlayText);
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRelative_1.move(mOverlayText.getHeight(), 0, null);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }
            }
        };
        mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        mRelative_2 = (LayoutRelative) findViewById(R.id.layout_relative_2);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final List<ItemModel> data = new ArrayList<>();

        data.add(new ItemModel(
                "0 ACCELERATE_DECELERATE_INTERPOLATOR",
                R.color.material_red_500,
                R.color.material_red_300,
                LayoutLinear.createInterpolator(LayoutLinear.ACCELERATE_DECELERATE_INTERPOLATOR)));
        data.add(new ItemModel(
                "1 ACCELERATE_INTERPOLATOR",
                R.color.material_pink_500,
                R.color.material_pink_300,
                LayoutLinear.createInterpolator(LayoutLinear.ACCELERATE_INTERPOLATOR)));
        data.add(new ItemModel(
                "2 BOUNCE_INTERPOLATOR",
                R.color.material_purple_500,
                R.color.material_purple_300,
                LayoutLinear.createInterpolator(LayoutLinear.BOUNCE_INTERPOLATOR)));
        data.add(new ItemModel(
                "3 DECELERATE_INTERPOLATOR",
                R.color.material_deep_purple_500,
                R.color.material_deep_purple_300,
                LayoutLinear.createInterpolator(LayoutLinear.DECELERATE_INTERPOLATOR)));
        data.add(new ItemModel(
                "4 FAST_OUT_LINEAR_IN_INTERPOLATOR",
                R.color.material_indigo_500,
                R.color.material_indigo_300,
                LayoutLinear.createInterpolator(LayoutLinear.FAST_OUT_LINEAR_IN_INTERPOLATOR)));
        data.add(new ItemModel(
                "5 FAST_OUT_SLOW_IN_INTERPOLATOR",
                R.color.material_blue_500,
                R.color.material_blue_300,
                LayoutLinear.createInterpolator(LayoutLinear.FAST_OUT_SLOW_IN_INTERPOLATOR)));
        data.add(new ItemModel(
                "6 LINEAR_INTERPOLATOR",
                R.color.material_light_blue_500,
                R.color.material_light_blue_300,
                LayoutLinear.createInterpolator(LayoutLinear.LINEAR_INTERPOLATOR)));
        data.add(new ItemModel(
                "7 LINEAR_OUT_SLOW_IN_INTERPOLATOR",
                R.color.material_cyan_500,
                R.color.material_cyan_300,
                LayoutLinear.createInterpolator(LayoutLinear.LINEAR_OUT_SLOW_IN_INTERPOLATOR)));
        recyclerView.setAdapter(new RecyclerViewRecyclerAdapter(data));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.test_7_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_1:
//                mWeight.toggle();
                break;
            case R.id.menu_2:
//                mRelative_1.expand();
//                mOverlayText.setVisibility(View.GONE);
                break;
            case R.id.menu_3:
                mRelative_2.toggle();
                break;
            case R.id.menu_4:
                mRelative_2.moveChild(0);
                break;
            case R.id.menu_5:
                mRelative_2.moveChild(1);
                break;
        }

        return true;
    }

    static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        private Drawable mDivider;

        public DividerItemDecoration(final Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }

    class ItemModel {
        public final String description;
        public final int colorId1;
        public final int colorId2;
        public final TimeInterpolator interpolator;

        public ItemModel(String description, int colorId1, int colorId2, TimeInterpolator interpolator) {
            this.description = description;
            this.colorId1 = colorId1;
            this.colorId2 = colorId2;
            this.interpolator = interpolator;
        }
    }

    static class RecyclerViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewRecyclerAdapter.ViewHolder> {

        private final List<ItemModel> data;
        private Context context;
        private SparseBooleanArray expandState = new SparseBooleanArray();

        public RecyclerViewRecyclerAdapter(final List<ItemModel> data) {
            this.data = data;
            for (int i = 0; i < data.size(); i++) {
                expandState.append(i, false);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            this.context = parent.getContext();
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_demo_07_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ItemModel item = data.get(position);
            holder.textView.setText(item.description);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, item.colorId1));
            holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context, item.colorId2));
            holder.expandableLayout.setInterpolator(item.interpolator);
            holder.expandableLayout.setExpanded(expandState.get(position));
            holder.expandableLayout.setListener(new ExpandableListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {

                }

                @Override
                public void onPreOpen() {
                    createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                    expandState.put(position, true);
                }

                @Override
                public void onPreClose() {
                    createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                    expandState.put(position, false);
                }

                @Override
                public void onOpened() {

                }

                @Override
                public void onClosed() {

                }
            });

            holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);
            holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onClickButton(holder.expandableLayout);
                }
            });
        }

        private void onClickButton(final Expandable expandableLayout) {
            expandableLayout.toggle();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public RelativeLayout buttonLayout;
            /**
             * You must use the ExpandableLinearLayout in the recycler view.
             * The ExpandableRelativeLayout doesn't work.
             */
            public LayoutLinear expandableLayout;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.textView);
                buttonLayout = (RelativeLayout) v.findViewById(R.id.button);
                expandableLayout = (LayoutLinear) v.findViewById(R.id.expandableLayout);
            }
        }

        public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(LayoutLinear.createInterpolator(LayoutLinear.LINEAR_INTERPOLATOR));
            return animator;
        }
    }


}
