package code.support.demo.widget.dropdown;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.List;

import code.support.demo.R;

public class DropMatter extends RelativeLayout {

    private FragmentManager fragmentManager;
    private List<Fragment> fragments;

    private int currentItem = 0;

    private Animation animationIn;
    private Animation animationOut;

    public DropMatter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropMatter(Context context) {
        this(context, null);
    }

    public DropMatter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void bindFragments(List<Fragment> fragmentList) {
        this.fragments = fragmentList;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment frg : fragments) {
            transaction.add(getMenuId(), frg);
        }
        transaction.commit();
    }

    private int getMenuId() {
        if (getId() == NO_ID) {
            setId(R.id.menu_id);
        }
        return getId();
    }

    public void setCurrentItem(int position) {
        currentItem = position;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideLastFragment(transaction);
        transaction.show(fragments.get(position));
        transaction.commit();
    }

    private void hideLastFragment(FragmentTransaction transaction) {
        for (int i = 0; i < fragments.size(); i++) {
            if (i != currentItem || !fragments.get(i).isHidden()) {
                transaction.hide(fragments.get(i));
            }
        }
    }

    public void show() {
        if (animationIn == null) {
            animationIn = AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_in);
        }
        setVisibility(VISIBLE);
        setAnimation(animationIn);
        animationIn.start();
    }

    public void close() {
        if (animationOut == null) {
            animationOut = AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_out);
        }
        setVisibility(GONE);
        setAnimation(animationOut);
        animationOut.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !(animationOut != null && animationOut.hasEnded()) && animationOut != null;
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }
}
