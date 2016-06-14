package com.ivan.wheelview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * WheelView 自定义滚轮选择空间
 *
 * @author Xuzj
 */
public class WheelView extends ScrollView {

    private final static String TAG = "WheelView";

    /**
     * 当前选中位置(默认为0)
     */
    private int currentPosition = 0;

    /**
     * 控件宽度
     */
    private int mWidth;
    /**
     * 控件高度
     */
    private int mHeight;
    /**
     * 头部绑定视图
     */
    private View header;
    /**
     * 底部绑定视图
     */
    private View footer;
    /**
     * Item的高度
     */
    private int itemHeight;
    private OnItemSelectListener onItemSelectListener;


    /**
     * 内容管理layout
     */
    private LinearLayout container;

    private List<String> data;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        this.addView(container);
        header = new View(getContext());
        footer = new View(getContext());
    }

    public void setData(List<String> data) {
        this.data = data;
        if (data == null) {
            return;
        }
        initViewWithData();
    }

    private void initViewWithData() {
        container.removeAllViews();
        container.addView(header);
        for (int i = 0; i < data.size(); i++) {
            TextView item = createItemView(i);
            item.setId(i);
            item.setOnClickListener(listener);
            container.addView(item);
        }
        container.addView(footer);
        scrollToPosition(0);
    }

    private TextView createItemView(int position) {
        TextView item = new TextView(getContext());
        item.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setSingleLine(true);
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        item.setTextColor(Color.parseColor("#666666"));
        item.setText(data.get(position));
        item.setGravity(Gravity.CENTER);
        int paddingH = dip2px(8);
        int paddingV = dip2px(4);
        item.setPadding(paddingH, paddingV, paddingH, paddingV);
        itemHeight = getViewMeasuredHeight(item);
        return item;
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (header != null) {
            ViewGroup.LayoutParams params = header.getLayoutParams();
            params.width = mWidth;
            params.height = (mHeight - createItemView(0).getMeasuredHeight()) / 2;
            header.setLayoutParams(params);
            footer.setLayoutParams(params);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int scrollY = getScrollY();
                        int position = scrollY / itemHeight;
                        position += (scrollY % itemHeight > itemHeight / 2 ? 1 : 0);
//                        scrollToPosition(position);
                        handler.sendEmptyMessage(position);
                    }
                }, 100);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void scrollToPosition(int position) {
        currentPosition = position;
        int scrollY = 0;
        for (int i = 0; i < currentPosition; i++) {
            TextView item = (TextView) container.getChildAt(currentPosition + 1);
            scrollY += item.getMeasuredHeight();
        }
//        scrollTo(0, scrollY);
        smoothScrollTo(0, scrollY);
        refreshItemsState();
        if (onItemSelectListener != null) {
            onItemSelectListener.onItemSelect(currentPosition);
        }
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() >= 0 && view.getId() <= data.size()) {
                if (view.getId() != currentPosition) {
                    scrollToPosition(view.getId());
                }
            }
        }
    };

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (data == null) {
            return;
        }
        int scrollY = getScrollY();
        int position = scrollY / itemHeight;
        position += (scrollY % itemHeight > itemHeight / 2 ? 1 : 0);
        int angle = 10 * (scrollY % itemHeight) / itemHeight;
        if (position != currentPosition) {
            currentPosition = position;
        }
        for (int i = currentPosition - 3; i < currentPosition + 4; i++) {
            if (i >= 0 && i < data.size()) {
                TextView item = (TextView) container.getChildAt(i + 1);
                if (i != currentPosition) {
                    item.setTextColor(Color.parseColor("#666666"));
                } else {
                    item.setTextColor(Color.parseColor("#FF0000"));
                }
                item.setRotationX(20 * (currentPosition - i) - angle);
            }
        }
    }

    private void refreshItemsState() {
        for (int i = currentPosition - 3; i < currentPosition + 4; i++) {
            if (i >= 0 && i < data.size()) {
                TextView item = (TextView) container.getChildAt(i + 1);
                if (i != currentPosition) {
                    item.setTextColor(Color.parseColor("#666666"));
                } else {
                    item.setTextColor(Color.parseColor("#FF0000"));
                }
                item.setRotationX(20 * (currentPosition - i));
            }
        }
    }

    public int getSelectPosition() {
        return currentPosition;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(int position);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            scrollToPosition(msg.what);
        }
    };

}
