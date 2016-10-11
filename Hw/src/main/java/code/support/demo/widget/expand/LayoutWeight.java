package code.support.demo.widget.expand;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import code.support.demo.R;

public class LayoutWeight extends RelativeLayout implements Expandable {

    public static final int ACCELERATE_DECELERATE_INTERPOLATOR = 0;
    public static final int ACCELERATE_INTERPOLATOR = 1;
    public static final int ANTICIPATE_INTERPOLATOR = 2;
    public static final int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 3;
    public static final int BOUNCE_INTERPOLATOR = 4;
    public static final int DECELERATE_INTERPOLATOR = 5;
    public static final int FAST_OUT_LINEAR_IN_INTERPOLATOR = 6;
    public static final int FAST_OUT_SLOW_IN_INTERPOLATOR = 7;
    public static final int LINEAR_INTERPOLATOR = 8;
    public static final int LINEAR_OUT_SLOW_IN_INTERPOLATOR = 9;
    public static final int OVERSHOOT_INTERPOLATOR = 10;

    private int duration;
    private boolean defaultExpanded;
    private TimeInterpolator interpolator = new LinearInterpolator();

    private boolean isExpanded;
    private boolean isCalculatedSize = false;
    private boolean isArranged = false;

    private float layoutWeight = 0.0f;

    private boolean isAnimating = false;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    private ExpandableListener listener;
    private SavedState savedState;

    public LayoutWeight(final Context context) {
        this(context, null);
    }

    public LayoutWeight(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutWeight(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LayoutWeight(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Expandable, defStyleAttr, 0);

        duration = a.getInteger(R.styleable.Expandable_exp_duration, DEFAULT_DURATION);
        defaultExpanded = a.getBoolean(R.styleable.Expandable_exp_expanded, DEFAULT_EXPANDED);
        final int interpolatorType = a.getInteger(R.styleable.Expandable_exp_interpolator, LINEAR_INTERPOLATOR);

        a.recycle();

        interpolator = createInterpolator(interpolatorType);
        isExpanded = defaultExpanded;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Check this layout using the attribute of weight
        if (!(getLayoutParams() instanceof LinearLayout.LayoutParams)) {
            throw new AssertionError("You must arrange in LinearLayout.");
        }
        if (0 >= getCurrentWeight()) throw new AssertionError("You must set a weight than 0.");
    }

    private float getCurrentWeight() {
        return ((LinearLayout.LayoutParams) getLayoutParams()).weight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!isCalculatedSize) {
            layoutWeight = getCurrentWeight();
            isCalculatedSize = true;
        }

        if (isArranged) return;
        setWeight(defaultExpanded ? layoutWeight : 0);
        isArranged = true;

        if (savedState == null) return;
        setWeight(savedState.getWeight());
    }

    private void setWeight(final float weight) {
        ((LinearLayout.LayoutParams) getLayoutParams()).weight = weight;
    }

    @Override
    public void toggle() {
        toggle(duration, interpolator);
    }

    @Override
    public void toggle(long duration, @Nullable TimeInterpolator interpolator) {
        if (0 < getCurrentWeight()) {
            collapse(duration, interpolator);
        } else {
            expand(duration, interpolator);
        }
    }

    @Override
    public void expand() {
        if (isAnimating) return;

        createExpandAnimator(0, layoutWeight, duration, interpolator).start();
    }

    /**
     * Creates value animator.
     *
     * @param interpolator TimeInterpolator
     */
    private ValueAnimator createExpandAnimator(final float from, final float to, final long duration, @Nullable final TimeInterpolator interpolator) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator == null ? this.interpolator : interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                setWeight((float) animation.getAnimatedValue());
                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                if (listener == null) {
                    return;
                }
                listener.onAnimationStart();
                if (layoutWeight == to) {
                    listener.onPreOpen();
                    return;
                }
                if (0 == to) {
                    listener.onPreClose();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                isExpanded = to > 0;
                if (listener == null) {
                    return;
                }
                listener.onAnimationEnd();
                if (to == layoutWeight) {
                    listener.onOpened();
                    return;
                }
                if (to == 0) {
                    listener.onClosed();
                }
            }
        });
        return valueAnimator;
    }

    @Override
    public void expand(long duration, @Nullable TimeInterpolator interpolator) {
        if (isAnimating) return;

        if (duration <= 0) {
            isExpanded = true;
            setWeight(layoutWeight);
            requestLayout();
            notifyListeners();
            return;
        }
        createExpandAnimator(getCurrentWeight(), layoutWeight, duration, interpolator).start();
    }

    private void notifyListeners() {
        if (listener == null) return;

        listener.onAnimationStart();
        if (isExpanded) {
            listener.onPreOpen();
        } else {
            listener.onPreClose();
        }
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }

                listener.onAnimationEnd();
                if (isExpanded) {
                    listener.onOpened();
                } else {
                    listener.onClosed();
                }
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    @Override
    public void collapse() {
        if (isAnimating) return;

        createExpandAnimator(getCurrentWeight(), 0, duration, interpolator).start();
    }

    @Override
    public void collapse(long duration, @Nullable TimeInterpolator interpolator) {
        if (isAnimating) return;

        if (duration <= 0) {
            isExpanded = false;
            setWeight(0);
            requestLayout();
            notifyListeners();
            return;
        }
        createExpandAnimator(getCurrentWeight(), 0, duration, interpolator).start();
    }

    @Override
    public void initLayout(boolean isMaintain) {
        layoutWeight = 0;
        isArranged = isMaintain;
        isCalculatedSize = false;
        savedState = null;

        super.requestLayout();
    }

    @Override
    public void setListener(@NonNull ExpandableListener listener) {
        this.listener = listener;
    }

    @Override
    public void setDuration(int duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + duration);
        }
        this.duration = duration;
    }

    @Override
    public void setExpanded(boolean expanded) {
        final float currentWeight = getCurrentWeight();
        if ((expanded && (currentWeight == layoutWeight)) || (!expanded && currentWeight == 0))
            return;

        isExpanded = expanded;
        setWeight(expanded ? layoutWeight : 0);
        requestLayout();
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void setInterpolator(@NonNull TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void move(float weight) {
        move(weight, duration, interpolator);
    }

    /**
     * Change to weight.
     * Sets 0 to duration if you want to move immediately.
     *
     * @param interpolator use the default interpolator if the argument is null.
     */
    public void move(float weight, long duration, @Nullable TimeInterpolator interpolator) {
        if (isAnimating) return;

        if (duration <= 0L) {
            isExpanded = weight > 0;
            setWeight(weight);
            requestLayout();
            notifyListeners();
            return;
        }
        createExpandAnimator(getCurrentWeight(), weight, duration, interpolator).start();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable parcelable = super.onSaveInstanceState();

        final SavedState ss = new SavedState(parcelable);
        ss.setWeight(getCurrentWeight());
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        savedState = ss;
    }

    /**
     * Creates Interpolator.
     *
     * @param interpolatorType type
     */
    static TimeInterpolator createInterpolator(@IntRange(from = 0, to = 10) final int interpolatorType) {
        switch (interpolatorType) {
            case ACCELERATE_DECELERATE_INTERPOLATOR:
                return new AccelerateDecelerateInterpolator();
            case ACCELERATE_INTERPOLATOR:
                return new AccelerateInterpolator();
            case ANTICIPATE_INTERPOLATOR:
                return new AnticipateInterpolator();
            case ANTICIPATE_OVERSHOOT_INTERPOLATOR:
                return new AnticipateOvershootInterpolator();
            case BOUNCE_INTERPOLATOR:
                return new BounceInterpolator();
            case DECELERATE_INTERPOLATOR:
                return new DecelerateInterpolator();
            case FAST_OUT_LINEAR_IN_INTERPOLATOR:
                return new FastOutLinearInInterpolator();
            case FAST_OUT_SLOW_IN_INTERPOLATOR:
                return new FastOutSlowInInterpolator();
            case LINEAR_INTERPOLATOR:
                return new LinearInterpolator();
            case LINEAR_OUT_SLOW_IN_INTERPOLATOR:
                return new LinearOutSlowInInterpolator();
            case OVERSHOOT_INTERPOLATOR:
                return new OvershootInterpolator();
            default:
                return new LinearInterpolator();
        }
    }
}
