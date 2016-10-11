package code.support.demo.widget.dropdown;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class DropMask extends View {

    public DropMask(Context context) {
        this(context, null);
    }

    public DropMask(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropMask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void close() {
        setVisibility(GONE);
    }
}
