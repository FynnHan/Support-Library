package code.support.demo.widget.expand;


import android.animation.TimeInterpolator;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Expandable {

    /**
     * Duration of expand animation
     */
    int DEFAULT_DURATION = 300;
    /**
     * Visibility of the layout when the layout attaches
     */
    boolean DEFAULT_EXPANDED = false;
    /**
     * Orientation of child views
     */
    int HORIZONTAL = 0;
    int VERTICAL = 1;

    /**
     * Orientation of layout
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL, VERTICAL})
    @interface Orientation {
    }

    /**
     * Starts animation the state of the view to the inverse of its current state.
     */
    void toggle();

    /**
     * Starts animation the state of the view to the inverse of its current state.
     *
     * @param interpolator use the default interpolator if the argument is null.
     */
    void toggle(final long duration, @Nullable final TimeInterpolator interpolator);

    /**
     * Starts expand animation.
     */
    void expand();

    /**
     * Starts expand animation.
     *
     * @param interpolator use the default interpolator if the argument is null.
     */
    void expand(final long duration, @Nullable final TimeInterpolator interpolator);

    /**
     * Starts collapse animation.
     */
    void collapse();

    /**
     * Starts collapse animation.
     *
     * @param interpolator use the default interpolator if the argument is null.
     */
    void collapse(final long duration, @Nullable final TimeInterpolator interpolator);

    /**
     * Initializes this layout.
     * <p>
     * This method doesn't work in the {@link LayoutRelative}.
     * You should use the {@link LayoutLinear} if size of children change.
     *
     * @param isMaintain #Notice Not support this argument.
     */
    @Deprecated
    void initLayout(final boolean isMaintain);

    /**
     * Sets the expandable layout listener.
     *
     * @param listener ExpandableLayoutListener
     */
    void setListener(@NonNull final ExpandableListener listener);

    /**
     * Sets the length of the animation.
     * The default duration is 300 milliseconds.
     */
    void setDuration(final int duration);

    /**
     * Sets state of expanse.
     *
     * @param expanded The layout is visible if expanded is true
     */
    void setExpanded(final boolean expanded);

    /**
     * Gets state of expanse.
     *
     * @return true if the layout is visible
     */
    boolean isExpanded();

    /**
     * The time interpolator used in calculating the elapsed fraction of this animation.
     * The interpolator determines whether the animation runs with linear or non-linear motion, such as acceleration and deceleration.
     * The default value is  {@link android.view.animation.AccelerateDecelerateInterpolator}
     */
    void setInterpolator(@NonNull final TimeInterpolator interpolator);

    class SavedState extends View.BaseSavedState {

        private int size;
        private float weight;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.size = in.readInt();
            this.weight = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.size);
            out.writeFloat(this.weight);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public float getWeight() {
            return this.weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }
}
