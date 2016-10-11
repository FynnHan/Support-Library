package code.support.demo.widget.dropdown;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import code.support.demo.R;
import code.support.demo.util.UtilDensity;

/**
 * 下拉列表的导航栏
 */
public class DropDownTable extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;

    private Context mContext;
    private LinearLayout mTabsContainer;

    /**
     * indicator
     */
    private int mIndicatorStyle = STYLE_NORMAL;
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private boolean mIndicatorAnimEnable;
    private boolean mIndicatorBounceEnable;
    private long mIndicatorAnimDuration;
    private int mIndicatorGravity;

    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;

    /**
     * divider
     */
    private int mDividerColor;
    private float mDividerWidth;
    private float mDividerPadding;

    /**
     * title
     */
    private float mTextsize;
    private int mTextSelectColor;
    private int mTextUnselectColor;
    private boolean mTextBold;
    private boolean mTextAllCaps;

    /**
     * icon
     */
    private boolean mIconVisible;
    private int mIconGravity;
    private float mIconWidth;
    private float mIconHeight;
    private float mIconMargin;

    /**
     * tab
     */
    private boolean mTabSpaceEqual;
    private float mTabWidth;
    private float mTabPadding;

    private int mHeight;

    /**
     * anim
     */
    private ValueAnimator mValueAnimator;
    private OvershootInterpolator mInterpolator = new OvershootInterpolator(1.5f);
    private IndicatorPoint mLastP = new IndicatorPoint();
    private IndicatorPoint mCurrentP = new IndicatorPoint();

    private int mTabCount;
    private int mLastTab;
    private int mCurrentTab;

    private Rect mIndicatorRect = new Rect();

    /**
     * paint
     */
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mIsFirstDraw = true;
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();
    private Path mTrianglePath = new Path();

    /**
     * set
     */
    private ArrayList<ITabEntity> mTabEntitys = new ArrayList<>();
    private FragmentChangeManager mFragmentChangeManager;
    private OnTabSelectListener mListener;

    public DropDownTable(Context context) {
        this(context, null, 0);
    }

    public DropDownTable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 重写onDraw方法,需要调用这个方法来清除flag
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);

        obtainAttributes(context, attrs);

        // get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        // create ViewPager
        switch (height) {
            case ViewGroup.LayoutParams.MATCH_PARENT + "":
                break;
            case ViewGroup.LayoutParams.WRAP_CONTENT + "":
                break;
            default:
                int[] systemAttrs = {android.R.attr.layout_height};
                TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
                mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                a.recycle();
                break;
        }

        mValueAnimator = ValueAnimator.ofObject(new PointEvaluator(), mLastP, mCurrentP);
        mValueAnimator.addUpdateListener(this);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownTable);

        mIndicatorStyle = a.getInt(R.styleable.DropDownTable_dropdown_indicator_style, 0);
        mIndicatorColor = a.getColor(R.styleable.DropDownTable_dropdown_indicator_color, Color.parseColor(mIndicatorStyle == STYLE_BLOCK ? "#4B6A87" : "#ffffff"));
        mIndicatorHeight = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_height, UtilDensity.dp2px(context, mIndicatorStyle == STYLE_TRIANGLE ? 4 : (mIndicatorStyle == STYLE_BLOCK ? -1 : 2)));
        mIndicatorWidth = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_width, UtilDensity.dp2px(context, mIndicatorStyle == STYLE_TRIANGLE ? 10 : -1));
        mIndicatorCornerRadius = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_corner_radius, UtilDensity.dp2px(context, mIndicatorStyle == STYLE_BLOCK ? -1 : 0));
        mIndicatorMarginLeft = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_margin_left, UtilDensity.dp2px(context, 0));
        mIndicatorMarginTop = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_margin_top, UtilDensity.dp2px(context, mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorMarginRight = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_margin_right, UtilDensity.dp2px(context, 0));
        mIndicatorMarginBottom = a.getDimension(R.styleable.DropDownTable_dropdown_indicator_margin_bottom, UtilDensity.dp2px(context, mIndicatorStyle == STYLE_BLOCK ? 7 : 0));
        mIndicatorAnimEnable = a.getBoolean(R.styleable.DropDownTable_dropdown_indicator_anim_enable, true);
        mIndicatorBounceEnable = a.getBoolean(R.styleable.DropDownTable_dropdown_indicator_bounce_enable, true);
        mIndicatorAnimDuration = a.getInt(R.styleable.DropDownTable_dropdown_indicator_anim_duration, -1);
        mIndicatorGravity = a.getInt(R.styleable.DropDownTable_dropdown_indicator_gravity, Gravity.BOTTOM);

        mUnderlineColor = a.getColor(R.styleable.DropDownTable_dropdown_underline_color, Color.parseColor("#ffffff"));
        mUnderlineHeight = a.getDimension(R.styleable.DropDownTable_dropdown_underline_height, UtilDensity.dp2px(context, 0));
        mUnderlineGravity = a.getInt(R.styleable.DropDownTable_dropdown_underline_gravity, Gravity.BOTTOM);

        mDividerColor = a.getColor(R.styleable.DropDownTable_dropdown_divider_color, Color.parseColor("#ffffff"));
        mDividerWidth = a.getDimension(R.styleable.DropDownTable_dropdown_divider_width, UtilDensity.dp2px(context, 0));
        mDividerPadding = a.getDimension(R.styleable.DropDownTable_dropdown_divider_padding, UtilDensity.dp2px(context, 12));

        mTextsize = a.getDimension(R.styleable.DropDownTable_dropdown_textsize, UtilDensity.sp2px(context, 13f));
        mTextSelectColor = a.getColor(R.styleable.DropDownTable_dropdown_textSelectColor, Color.parseColor("#ffffff"));
        mTextUnselectColor = a.getColor(R.styleable.DropDownTable_dropdown_textUnselectColor, Color.parseColor("#AAffffff"));
        mTextBold = a.getBoolean(R.styleable.DropDownTable_dropdown_textBold, false);
        mTextAllCaps = a.getBoolean(R.styleable.DropDownTable_dropdown_textAllCaps, false);

        mIconVisible = a.getBoolean(R.styleable.DropDownTable_dropdown_iconVisible, true);
        mIconGravity = a.getInt(R.styleable.DropDownTable_dropdown_iconGravity, Gravity.TOP);
        mIconWidth = a.getDimension(R.styleable.DropDownTable_dropdown_iconWidth, UtilDensity.dp2px(context, 0));
        mIconHeight = a.getDimension(R.styleable.DropDownTable_dropdown_iconHeight, UtilDensity.dp2px(context, 0));
        mIconMargin = a.getDimension(R.styleable.DropDownTable_dropdown_iconMargin, UtilDensity.dp2px(context, 2.5f));

        mTabSpaceEqual = a.getBoolean(R.styleable.DropDownTable_dropdown_tab_space_equal, true);
        mTabWidth = a.getDimension(R.styleable.DropDownTable_dropdown_tab_width, UtilDensity.dp2px(context, -1));
        mTabPadding = a.getDimension(R.styleable.DropDownTable_dropdown_tab_padding, mTabSpaceEqual || mTabWidth > 0 ? UtilDensity.dp2px(context, 0) : UtilDensity.dp2px(context, 10));

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        // draw divider
        if (mDividerWidth > 0) {
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(paddingLeft + tab.getRight(), mDividerPadding, paddingLeft + tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }

        // draw underline
        if (mUnderlineHeight > 0) {
            mRectPaint.setColor(mUnderlineColor);
            if (mUnderlineGravity == Gravity.BOTTOM) {
                canvas.drawRect(paddingLeft, height - mUnderlineHeight, mTabsContainer.getWidth() + paddingLeft, height, mRectPaint);
            } else {
                canvas.drawRect(paddingLeft, 0, mTabsContainer.getWidth() + paddingLeft, mUnderlineHeight, mRectPaint);
            }
        }

        // draw indicator line
        if (mIndicatorAnimEnable) {
            if (mIsFirstDraw) {
                mIsFirstDraw = false;
                calcIndicatorRect();
            }
        } else {
            calcIndicatorRect();
        }

        if (mIndicatorStyle == STYLE_TRIANGLE) {
            if (mIndicatorHeight > 0) {
                mTrianglePaint.setColor(mIndicatorColor);
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingLeft + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.left / 2 + mIndicatorRect.right / 2, height - mIndicatorHeight);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        } else if (mIndicatorStyle == STYLE_BLOCK) {
            if (mIndicatorHeight < 0) {
                mIndicatorHeight = height - mIndicatorMarginTop - mIndicatorMarginBottom;
            } else {
            }

            if (mIndicatorHeight > 0) {
                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                    mIndicatorCornerRadius = mIndicatorHeight / 2;
                }

                mIndicatorDrawable.setColor(mIndicatorColor);
                mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left, (int) mIndicatorMarginTop, (int) (paddingLeft + mIndicatorRect.right - mIndicatorMarginRight), (int) (mIndicatorMarginTop + mIndicatorHeight));
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        } else {
            if (mIndicatorHeight > 0) {
                mIndicatorDrawable.setColor(mIndicatorColor);
                if (mIndicatorGravity == Gravity.BOTTOM) {
                    mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left, height - (int) mIndicatorHeight - (int) mIndicatorMarginBottom, paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight, height - (int) mIndicatorMarginBottom);
                } else {
                    mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left, (int) mIndicatorMarginTop, paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight, (int) mIndicatorHeight + (int) mIndicatorMarginTop);
                }
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        }
    }

    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;

        if (mIndicatorWidth < 0) {
            // indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip
        } else {
            // indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        mIndicatorRect.left = (int) p.left;
        mIndicatorRect.right = (int) p.right;

        if (mIndicatorWidth < 0) {
            // indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip

        } else {
            // indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = p.left + (currentTabView.getWidth() - mIndicatorWidth) / 2;

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);
        }
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
            }
        }
        super.onRestoreInstanceState(state);
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            ITabEntity tabEntity = mTabEntitys.get(i);
            iv_tab_icon.setImageResource(isSelect ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
        }
    }

    /**
     * 关联数据支持同时切换fragments
     */
    public void setTabData(ArrayList<ITabEntity> tabEntitys, FragmentActivity fa, int containerViewId, ArrayList<Fragment> fragments) {
        mFragmentChangeManager = new FragmentChangeManager(fa.getSupportFragmentManager(), containerViewId, fragments);
        setTabData(tabEntitys);
    }

    public void setTabData(ArrayList<ITabEntity> tabEntitys) {
        if (tabEntitys == null || tabEntitys.size() == 0) {
            throw new IllegalStateException("TabEntitys can not be NULL or EMPTY !");
        }

        this.mTabEntitys.clear();
        this.mTabEntitys.addAll(tabEntitys);

        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTabEntitys.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            if (mIconGravity == Gravity.LEFT) {
                tabView = View.inflate(mContext, R.layout.tab_left_layout, null);
            } else if (mIconGravity == Gravity.RIGHT) {
                tabView = View.inflate(mContext, R.layout.tab_right_layout, null);
            } else if (mIconGravity == Gravity.BOTTOM) {
                tabView = View.inflate(mContext, R.layout.tab_bottom_layout, null);
            } else {
                tabView = View.inflate(mContext, R.layout.tab_top_layout, null);
            }

            tabView.setTag(i);
            addTab(i, tabView);
        }
        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        tv_tab_title.setText(mTabEntitys.get(position).getTabTitle());
        ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
        iv_tab_icon.setImageResource(mTabEntitys.get(position).getTabUnselectedIcon());

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                if (mCurrentTab != position) {
                    setCurrentTab(position);
                    if (mListener != null) {
                        mListener.onTabSelect(position);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onTabReselect(position);
                    }
                }
            }
        });

        // 每一个Tab的布局参数
        LinearLayout.LayoutParams lp_tab = mTabSpaceEqual ? new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) : new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (mTabWidth > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) mTabWidth, LayoutParams.MATCH_PARENT);
        }
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void setCurrentTab(int currentTab) {
        mLastTab = this.mCurrentTab;
        this.mCurrentTab = currentTab;
        updateTabSelection(currentTab);
        if (mFragmentChangeManager != null) {
            mFragmentChangeManager.setFragments(currentTab);
        }
        if (mIndicatorAnimEnable) {
            calcOffset();
        } else {
            invalidate();
        }
    }

    private void calcOffset() {
        final View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        mCurrentP.left = currentTabView.getLeft();
        mCurrentP.right = currentTabView.getRight();

        final View lastTabView = mTabsContainer.getChildAt(this.mLastTab);
        mLastP.left = lastTabView.getLeft();
        mLastP.right = lastTabView.getRight();

        if (mLastP.left == mCurrentP.left && mLastP.right == mCurrentP.right) {
            invalidate();
        } else {
            mValueAnimator.setObjectValues(mLastP, mCurrentP);
            if (mIndicatorBounceEnable) {
                mValueAnimator.setInterpolator(mInterpolator);
            }

            if (mIndicatorAnimDuration < 0) {
                mIndicatorAnimDuration = mIndicatorBounceEnable ? 500 : 250;
            }
            mValueAnimator.setDuration(mIndicatorAnimDuration);
            mValueAnimator.start();
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View tabView = mTabsContainer.getChildAt(i);
            tabView.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
            TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tv_tab_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnselectColor);
            tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextsize);
            if (mTextAllCaps) {
                tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
            }

            if (mTextBold) {
                tv_tab_title.getPaint().setFakeBoldText(mTextBold);
            }

            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            if (mIconVisible) {
                iv_tab_icon.setVisibility(View.VISIBLE);
                ITabEntity tabEntity = mTabEntitys.get(i);
                iv_tab_icon.setImageResource(i == mCurrentTab ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIconWidth <= 0 ? LinearLayout.LayoutParams.WRAP_CONTENT : (int) mIconWidth, mIconHeight <= 0 ? LinearLayout.LayoutParams.WRAP_CONTENT : (int) mIconHeight);
                if (mIconGravity == Gravity.LEFT) {
                    lp.rightMargin = (int) mIconMargin;
                } else if (mIconGravity == Gravity.RIGHT) {
                    lp.leftMargin = (int) mIconMargin;
                } else if (mIconGravity == Gravity.BOTTOM) {
                    lp.topMargin = (int) mIconMargin;
                } else {
                    lp.bottomMargin = (int) mIconMargin;
                }

                iv_tab_icon.setLayoutParams(lp);
            } else {
                iv_tab_icon.setVisibility(View.GONE);
            }
        }
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    class PointEvaluator implements TypeEvaluator<IndicatorPoint> {
        @Override
        public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
            float left = startValue.left + fraction * (endValue.left - startValue.left);
            float right = startValue.right + fraction * (endValue.right - startValue.right);
            IndicatorPoint point = new IndicatorPoint();
            point.left = left;
            point.right = right;
            return point;
        }
    }

    class IndicatorPoint {
        public float left;
        public float right;
    }

    public class FragmentChangeManager {
        private FragmentManager mFragmentManager;
        private int mContainerViewId;
        /**
         * Fragment切换数组
         */
        private ArrayList<Fragment> mFragments;
        /**
         * 当前选中的Tab
         */
        private int mCurrentTab;

        public FragmentChangeManager(FragmentManager fm, int containerViewId, ArrayList<Fragment> fragments) {
            this.mFragmentManager = fm;
            this.mContainerViewId = containerViewId;
            this.mFragments = fragments;
            initFragments();
        }

        /**
         * 初始化fragments
         */
        private void initFragments() {
            for (Fragment fragment : mFragments) {
                mFragmentManager.beginTransaction().add(mContainerViewId, fragment).hide(fragment).commit();
            }

            setFragments(0);
        }

        /**
         * 界面切换控制
         */
        public void setFragments(int index) {
            for (int i = 0; i < mFragments.size(); i++) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                Fragment fragment = mFragments.get(i);
                if (i == index) {
                    ft.show(fragment);
                } else {
                    ft.hide(fragment);
                }
                ft.commit();
            }
            mCurrentTab = index;
        }

        public int getCurrentTab() {
            return mCurrentTab;
        }

        public Fragment getCurrentFragment() {
            return mFragments.get(mCurrentTab);
        }
    }

    public interface ITabEntity {
        String getTabTitle();

        int getTabSelectedIcon();

        int getTabUnselectedIcon();
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);

        void onTabReselect(int position);
    }

    /**
     * 标签的标识效果
     */
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            show(tipView, num);

            if (mInitSetMap.get(position) != null && mInitSetMap.get(position)) {
                return;
            }

            if (!mIconVisible) {
                setMsgMargin(position, 2, 2);
            } else {
                setMsgMargin(position, 0, mIconGravity == Gravity.LEFT || mIconGravity == Gravity.RIGHT ? 4 : 0);
            }

            mInitSetMap.put(position, true);
        }
    }

    /**
     * 设置提示红点偏移,注意
     * ---
     * 1.控件为固定高度:参照点为tab内容的右上角
     * 2.控件高度不固定(WRAP_CONTENT):参照点为tab内容的右上角,此时高度已是红点的最高显示范围,所以这时bottomPadding其实就是topPadding
     */
    private void setMsgMargin(int position, float leftPadding, float bottomPadding) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextsize);
            float textWidth = mTextPaint.measureText(tv_tab_title.getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();

            float iconH = mIconHeight;
            float margin = 0;
            if (mIconVisible) {
                if (iconH <= 0) {
                    iconH = mContext.getResources().getDrawable(mTabEntitys.get(position).getTabSelectedIcon()).getIntrinsicHeight();
                }
                margin = mIconMargin;
            }

            if (mIconGravity == Gravity.TOP || mIconGravity == Gravity.BOTTOM) {
                lp.leftMargin = UtilDensity.dp2px(mContext, leftPadding);
                lp.topMargin = mHeight > 0 ? (int) (mHeight - textHeight - iconH - margin) / 2 - UtilDensity.dp2px(mContext, bottomPadding) : UtilDensity.dp2px(mContext, bottomPadding);
            } else {
                lp.leftMargin = UtilDensity.dp2px(mContext, leftPadding);
                lp.topMargin = mHeight > 0 ? (int) (mHeight - Math.max(textHeight, iconH)) / 2 - UtilDensity.dp2px(mContext, bottomPadding) : UtilDensity.dp2px(mContext, bottomPadding);
            }

            tipView.setLayoutParams(lp);
        }
    }

    /**
     * 隐藏消息
     */
    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

    /**
     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
     */
    public MsgView getMsgView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        return (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
    }

    public static void show(MsgView msgView, int num) {
        if (msgView == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
        DisplayMetrics dm = msgView.getResources().getDisplayMetrics();
        msgView.setVisibility(View.VISIBLE);
        if (num <= 0) {
            // 圆点,设置默认宽高
            msgView.setStrokeWidth(0);
            msgView.setText("");

            lp.width = (int) (5 * dm.density);
            lp.height = (int) (5 * dm.density);
            msgView.setLayoutParams(lp);
        } else {
            lp.height = (int) (18 * dm.density);
            if (num > 0 && num < 10) {
                // 圆
                lp.width = (int) (18 * dm.density);
                msgView.setText(num + "");
            } else if (num > 9 && num < 100) {
                // 圆角矩形,圆角是高度的一半,设置默认padding
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                msgView.setPadding((int) (6 * dm.density), 0, (int) (6 * dm.density), 0);
                msgView.setText(num + "");
            } else {
                // 数字超过两位,显示99+
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                msgView.setPadding((int) (6 * dm.density), 0, (int) (6 * dm.density), 0);
                msgView.setText("99+");
            }
            msgView.setLayoutParams(lp);
        }
    }

}
