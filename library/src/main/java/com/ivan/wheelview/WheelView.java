package com.ivan.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
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

    /**
     * 选中字体颜色
     */
    private int textColor_selected;
    /**
     * 正常字体颜色
     */
    private int textColor_normal;
    /**
     * 字体大小
     */
    private float textSize;
    /**
     * 分割线颜色
     */
    private int divideColor;
    /**
     * 分割线大小
     */
    private int divideSize;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WheelView);
        textColor_selected = a.getColor(R.styleable.WheelView_textColor_selected, 0XFF0000);
        textColor_normal = a.getColor(R.styleable.WheelView_textColor_normal, 0X666666);
        textSize = a.getDimensionPixelSize(R.styleable.WheelView_textSize, 15);
        divideColor = a.getColor(R.styleable.WheelView_divideColor, 0X00000000);
        divideSize = a.getDimensionPixelSize(R.styleable.WheelView_divideSize, 0);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (divideSize == 0) {
            return;
        }
        if (null == paint) {
            paint = new Paint();
            paint.setColor(divideColor);
            paint.setStrokeWidth(divideSize);
        }
        canvas.drawLine(0, getScrollY() + header.getMeasuredHeight(), mWidth, getScrollY() + header.getMeasuredHeight(), paint);
        canvas.drawLine(0, getScrollY() + header.getMeasuredHeight() + itemHeight, mWidth, getScrollY() + header.getMeasuredHeight() + itemHeight, paint);
    }

    private TextView createItemView(int position) {
        TextView item = new TextView(getContext());
        item.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setSingleLine(true);
        item.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        item.setTextColor(textColor_normal);
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
                        handler.sendEmptyMessage(position);
                    }
                }, 100);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void scrollToPosition(int position) {
        currentPosition = position;
        smoothScrollTo(0, currentPosition * itemHeight);
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

        if (position != currentPosition) {
            currentPosition = position;
        }
        for (int i = currentPosition - 3; i < currentPosition + 4; i++) {
            if (i >= 0 && i < data.size()) {
                TextView item = (TextView) container.getChildAt(i + 1);
                if (i != currentPosition) {
                    item.setTextColor(textColor_normal);
                } else {
                    item.setTextColor(textColor_selected);
                }
                int angle = 30 * (i * itemHeight - scrollY) / itemHeight;
                int divX = Math.abs(5 * (i * itemHeight - scrollY) / itemHeight);
                if (i <= currentPosition) {
                    item.setRotationX(-angle);
                    int paddingHL = dip2px(8 + divX);
                    int paddingHR = dip2px(8);
                    int paddingV = dip2px(4);
                    item.setPadding(paddingHL, paddingV, paddingHR, paddingV);
                } else if (i > currentPosition) {
                    item.setRotationX(angle);
                    int paddingHL = dip2px(8 + divX);
                    int paddingHR = dip2px(8);
                    int paddingV = dip2px(4);
                    item.setPadding(paddingHL, paddingV, paddingHR, paddingV);
                }
            }
        }
    }

    private void refreshItemsState() {
        for (int i = currentPosition - 3; i < currentPosition + 4; i++) {
            if (i >= 0 && i < data.size()) {
                TextView item = (TextView) container.getChildAt(i + 1);
                if (i != currentPosition) {
                    item.setTextColor(textColor_normal);
                } else {
                    item.setTextColor(textColor_selected);
                }
                int angle = 30 * (i * itemHeight - getScrollY()) / itemHeight;
                int divX = Math.abs(5 * (i * itemHeight - getScrollY()) / itemHeight);
                if (i <= currentPosition) {
                    item.setRotationX(-angle);
                    int paddingHL = dip2px(8 + divX);
                    int paddingHR = dip2px(8);
                    int paddingV = dip2px(4);
                    item.setPadding(paddingHL, paddingV, paddingHR, paddingV);
                } else if (i > currentPosition) {
                    item.setRotationX(angle);
                    int paddingHL = dip2px(8 + divX);
                    int paddingHR = dip2px(8);
                    int paddingV = dip2px(4);
                    item.setPadding(paddingHL, paddingV, paddingHR, paddingV);
                }
            }
        }
    }

    Paint paint;

    @Override
    public void setBackgroundDrawable(Drawable background) {
//        if (null == paint) {
//            paint = new Paint();
//            paint.setColor(Color.parseColor("#83cde6"));
//            paint.setStrokeWidth(dip2px(1f));
//        }
//        background = new Drawable() {
//            @Override
//            public void draw(Canvas canvas) {
//                canvas.drawLine(0, header.getMeasuredHeight(), mWidth, header.getMeasuredHeight(), paint);
//                canvas.drawLine(0, header.getMeasuredHeight() + itemHeight, mWidth, header.getMeasuredHeight() + itemHeight, paint);
//            }
//            @Override
//            public void setAlpha(int alpha) {
//            }
//            @Override
//            public void setColorFilter(ColorFilter cf) {
//            }
//            @Override
//            public int getOpacity() {
//                return 0;
//            }
//        };
        super.setBackgroundDrawable(background);
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
