package code.support.demo.widget.dropdown;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class DropDownLayout extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private DropMask mDropMask;
    private DropMatter mDropMatter;

    public DropDownLayout(Context context) {
        this(context, null);
    }

    public DropDownLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        findChildView();
        if (mDropMask == null || mDropMatter == null) {
            throw new IllegalArgumentException("you layout must be contain  MaskView MenuLayout");
        }

        if (Build.VERSION.SDK_INT < 16) {
            removeLayoutListenerPre16();
        } else {
            removeLayoutListenerPost16();
        }
        mDropMatter.setVisibility(GONE);
        mDropMask.setVisibility(GONE);
        mDropMask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMatter();
            }
        });
    }

    private void findChildView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (mDropMask != null && mDropMatter != null)
                break;
            View childItem = getChildAt(i);
            if (childItem instanceof DropMask) {
                mDropMask = (DropMask) childItem;
            } else if (childItem instanceof DropMatter) {
                mDropMatter = (DropMatter) childItem;
            }
        }
    }

    private void removeLayoutListenerPre16() {
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @TargetApi(16)
    private void removeLayoutListenerPost16() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public void showMatterAt(int position) {
        if (!mDropMatter.isShow()) {
            mDropMask.show();
            mDropMatter.show();
        }
        mDropMatter.setCurrentItem(position);
    }

    public void closeMatter() {
        if (mDropMatter.isShow()) {
            mDropMask.close();
            mDropMatter.close();
        }
    }

}
