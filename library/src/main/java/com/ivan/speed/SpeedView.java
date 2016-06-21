package com.ivan.speed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author Xuzj
 */
public class SpeedView extends View {

    private final static String TAG = "SpeedView";

    private Paint paint;
    private Paint textPaint;
    private Paint textSPaint;
    private Paint centerPaint;
    private Paint speedPaint;
    private Paint.FontMetricsInt fmi;
    private Paint.FontMetricsInt fmiS;
    private int cirR;

    private int currentSpeed = 0;
    private int speed = 0;
    private int addSpeed = 7;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (currentSpeed != speed) {
                        if (Math.abs(currentSpeed - speed) >= addSpeed) {
                            if (currentSpeed > speed) {
                                currentSpeed -= addSpeed;
                            } else {
                                currentSpeed += addSpeed;
                            }
                            if (Math.abs(currentSpeed - speed) < 30) {
                                addSpeed--;
                                if (addSpeed == 0) {
                                    addSpeed = 1;
                                }
                            }
                            postInvalidate();
                            handler.sendEmptyMessageDelayed(0, 30);
                        } else {
                            addSpeed = addSpeed / 2;
                            if (addSpeed == 0) {
                                addSpeed = 1;
                            }
                            handler.sendEmptyMessage(0);
                        }
                        Log.v(TAG, "currentSpeed = " + currentSpeed);
                        Log.v(TAG, "speed = " + speed);
                    }
                    break;
            }
        }
    };

    public SpeedView(Context context) {
        this(context, null);
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        speedPaint = new Paint();
        speedPaint.setColor(Color.parseColor("#FF0000"));
        speedPaint.setStrokeWidth(6);
        speedPaint.setStyle(Paint.Style.STROKE);
        speedPaint.setAntiAlias(true);

        centerPaint = new Paint();
        centerPaint.setColor(Color.parseColor("#0000FF"));
        centerPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#0000FF"));
        textPaint.setStyle(Paint.Style.FILL);   // 设置样式
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        fmi = textPaint.getFontMetricsInt();

        textSPaint = new Paint();
        textSPaint.setColor(Color.parseColor("#FF0000"));
        textSPaint.setStyle(Paint.Style.FILL);   // 设置样式
        textSPaint.setTextSize(80);
        textSPaint.setTextAlign(Paint.Align.CENTER);
        fmiS = textSPaint.getFontMetricsInt();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cirR == 0) {
            cirR = getMeasuredWidth() / 2 - 50;
            if (cirR > getMeasuredHeight() / 2 - 50) {
                cirR = getMeasuredHeight() / 2 - 50;
            }
        }
        RectF oval = new RectF();
        oval.left = getMeasuredWidth() / 2 - cirR;
        oval.right = getMeasuredWidth() / 2 + cirR;
        oval.top = getMeasuredHeight() / 2 - cirR;
        oval.bottom = getMeasuredHeight() / 2 + cirR;
//        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, cirR, paint);
        canvas.drawArc(oval, -210, 240, false, paint);
        //画表盘
        for (int i = 0; i <= 20; i++) {
            double startX = getMeasuredWidth() / 2 + cirR * Math.cos(i * Math.PI / 15 - Math.PI * 7 / 6);
            double startY = getMeasuredHeight() / 2 + cirR * Math.sin(i * Math.PI / 15 - Math.PI * 7 / 6);
            double endX = getMeasuredWidth() / 2 + (cirR - 30) * Math.cos(i * Math.PI / 15 - Math.PI * 7 / 6);
            double endY = getMeasuredHeight() / 2 + (cirR - 30) * Math.sin(i * Math.PI / 15 - Math.PI * 7 / 6);
            if (i % 2 == 0) {
                endX = getMeasuredWidth() / 2 + (cirR - 60) * Math.cos(i * Math.PI / 15 - Math.PI * 7 / 6);
                endY = getMeasuredHeight() / 2 + (cirR - 60) * Math.sin(i * Math.PI / 15 - Math.PI * 7 / 6);
                double positionX = getMeasuredWidth() / 2 + (cirR - 90) * Math.cos(i * Math.PI / 15 - Math.PI * 7 / 6);
                double positionY = getMeasuredHeight() / 2 + (cirR - 90) * Math.sin(i * Math.PI / 15 - Math.PI * 7 / 6);
                String text = i * 10 + "";
                float height = fmi.bottom - fmi.top;
                if (i < 10) {
                    canvas.drawText(text, (float) positionX - fmi.ascent / 2, (float) positionY + height / 2, textPaint);
                } else {
                    canvas.drawText(text, (float) positionX + fmi.ascent / 2, (float) positionY + height / 2, textPaint);
                }
            }
            canvas.drawLine((float) startX, (float) startY, (float) endX, (float) endY, paint);
        }

        //画速度指针
        double startX = getMeasuredWidth() / 2 - 30 * Math.cos(((double) currentSpeed / 200) * Math.PI * 4 / 3 - Math.PI * 7 / 6);
        double startY = getMeasuredHeight() / 2 - 30 * Math.sin(((double) currentSpeed / 200) * Math.PI * 4 / 3 - Math.PI * 7 / 6);
        double endX = getMeasuredWidth() / 2 + (cirR - 200) * Math.cos(((double) currentSpeed / 200) * Math.PI * 4 / 3 - Math.PI * 7 / 6);
        double endY = getMeasuredHeight() / 2 + (cirR - 200) * Math.sin(((double) currentSpeed / 200) * Math.PI * 4 / 3 - Math.PI * 7 / 6);
        canvas.drawLine((float) startX, (float) startY, (float) endX, (float) endY, paint);

        //圆心点
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 12, centerPaint);

        double leftX = getMeasuredWidth() / 2 + cirR * Math.cos(-Math.PI * 7 / 6);
        double leftY = getMeasuredHeight() / 2 + cirR * Math.sin(-Math.PI * 7 / 6);
        double rightX = getMeasuredWidth() / 2 + cirR * Math.cos(Math.PI / 3);

        Rect targetRect = new Rect((int) leftX, (int) leftY - 30, (int) rightX, (int) leftY + 30);
        int baseline = (targetRect.bottom + targetRect.top - fmiS.bottom - fmiS.top) / 2;
        canvas.drawText(speed + " km/h", getMeasuredWidth() / 2, baseline, textSPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cirR = getMeasuredWidth() / 2 - 50;
        if (cirR > getMeasuredHeight() / 2 - 50) {
            cirR = getMeasuredHeight() / 2 - 50;
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        addSpeed = 7;
        invalidate();
        if (currentSpeed != speed) {
            handler.sendEmptyMessageDelayed(0, 30);
        }
    }
}

