package code.support.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import code.support.demo.R;

public class PickerView extends View {

    public static final float MARGIN_ALPHA = 2.8F;
    public static final float SPEED = 2.0F;

    private Timer timer;
    private List<String> mDataList;
    private Paint maxPaint;
    private Paint minPaint;

    private boolean isInit;
    private boolean canScroll = true;

    private int mViewWidth;
    private int mViewHeight;

    private float mMaxTextAlpha = 255.0F;
    private float mMaxTextSize = 80.0F;
    private float mMinTextAlpha = 120.0F;
    private float mMinTextSize = 40.0F;

    private int mCurrentSelected;
    private float mMoveLen;
    private float mLastDownY;

    private OnSelectListener mSelectListener;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message paramAnonymousMessage) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0.0F;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
            } else {
                mMoveLen = mMoveLen - SPEED * (mMoveLen / Math.abs(mMoveLen));
            }
            invalidate();
        }
    };

    public PickerView(Context context) {
        super(context);
        init();
    }

    public PickerView(Context paramContext, AttributeSet attributeSet) {
        super(paramContext, attributeSet);
        init();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList();

        maxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxPaint.setStyle(Paint.Style.FILL);
        maxPaint.setTextAlign(Paint.Align.CENTER);
        maxPaint.setColor(getResources().getColor(R.color.colorPrimary));

        minPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minPaint.setStyle(Paint.Style.FILL);
        minPaint.setTextAlign(Paint.Align.CENTER);
        minPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        mMaxTextSize = mViewHeight / 7.0F;
        mMinTextSize = mMaxTextSize / 2.2F;

        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInit)
            drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        // 根据抛物线比例设置文字大小
        float scale = parabola(mViewHeight / 4.0F, mMoveLen);
        float size = scale * (mMaxTextSize - mMinTextSize) + mMinTextSize;
        maxPaint.setTextSize(size);
        maxPaint.setAlpha((int) (scale * (mMaxTextAlpha - mMinTextAlpha) + mMinTextAlpha));

        Paint.FontMetricsInt fontMetricsInt = maxPaint.getFontMetricsInt();
        float x = (float) (mViewWidth / 2.0D);
        float y = (float) (mViewHeight / 2.0D + mMoveLen);
        float baseline = (float) (y - (fontMetricsInt.bottom / 2.0D + fontMetricsInt.top / 2.0D));
        canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, maxPaint);

        for (int i = 1; mCurrentSelected - i >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        for (int j = 1; j + mCurrentSelected < mDataList.size(); j++) {
            drawOtherText(canvas, j, 1);
        }
    }

    private float parabola(float height, float moveLen) {
        float scale = (float) (1.0D - Math.pow(moveLen / height, 2.0D));

        return scale < 0.0F ? 0.0F : scale;
    }

    private void drawOtherText(Canvas canvas, int position, int type) {
        float f = MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen;

        float scale = parabola(mViewHeight / 4.0F, f);
        float size = scale * (mMaxTextSize - mMinTextSize) + mMinTextSize;
        this.minPaint.setTextSize(size);
        this.minPaint.setAlpha((int) (scale * (mMaxTextAlpha - mMinTextAlpha) + mMinTextAlpha));

        Paint.FontMetricsInt fontMetricsInt = minPaint.getFontMetricsInt();
        float x = (float) (mViewWidth / 2.0D);
        float y = (float) (mViewHeight / 2.0D + f * type);
        float baseline = (float) (y - (fontMetricsInt.bottom / 2.0D + fontMetricsInt.top / 2.0D));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position), x, baseline, minPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return canScroll && super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp();
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {
        mMoveLen += event.getY() - mLastDownY;
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / SPEED) {
            moveTailToHead();
            mMoveLen -= MARGIN_ALPHA * mMinTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / SPEED) {
            moveHeadToTail();
            mMoveLen += MARGIN_ALPHA * mMinTextSize;
        }
        mLastDownY = event.getY();
        invalidate();
    }

    private void moveTailToHead() {
        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
    }


    private void moveHeadToTail() {
        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
    }

    private void doUp() {
        if (Math.abs(mMoveLen) < 0) {
            mMoveLen = 0.0F;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    public void setData(List<String> datas) {
        mDataList = datas;
        if (getCurrentSelected() == 0) {
            mCurrentSelected = (datas.size() / 4);
        }
        if (mCurrentSelected > datas.size() - 1) {
            mCurrentSelected = (datas.size() - 1);
        }
        invalidate();
    }

    public int getCurrentSelected() {
        return mCurrentSelected;
    }

    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
        }
    }

    public void setSelected(int selected) {
        mCurrentSelected = selected;
        int distance = mDataList.size() / 2 - mCurrentSelected;

        if (distance < 0) {
            for (int i = 0; i < -distance; i++) {
                moveHeadToTail();
                mCurrentSelected = (mCurrentSelected - 1);
            }
            if (distance > 0) {
                for (int j = 0; j < distance; j++) {
                    moveTailToHead();
                    mCurrentSelected = (mCurrentSelected + 1);
                }
            }
        }
        invalidate();
        performSelect();
    }

    private void performSelect() {
        if (mSelectListener != null) {
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
        }
    }

    public int getSelectedIndex(String paramString) {
        int index = 0;

        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).equals(paramString)) {
                return i;
            }
        }

        return index;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.mSelectListener = listener;
    }

    public interface OnSelectListener {
        void onSelect(String mSelectItem);
    }

    class MyTimerTask extends TimerTask {

        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }
}