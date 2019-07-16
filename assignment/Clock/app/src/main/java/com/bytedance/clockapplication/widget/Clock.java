package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import android.os.Handler;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    private TimerHandler mHandler;

    public Clock(Context context) {
        super(context);
        init(context, null);
        mHandler = new TimerHandler(this);
        //getTime();
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        mHandler = new TimerHandler(this);
        //getTime();
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        mHandler = new TimerHandler(this);
        //getTime();
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }


    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    private static final class TimerHandler extends Handler{
        private WeakReference<Clock> clockWeakReference;
        private TimerHandler(Clock clockview){
            clockWeakReference = new WeakReference<>(clockview);
        }

        @Override
        public void handleMessage(Message msg){
            Clock clockView = clockWeakReference.get();
            if(clockView!=null){
                clockView.invalidate();
                sendEmptyMessageDelayed(1,1000);
            }
        }
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
        //postInvalidateDelayed(1000);//每1秒重新绘制一次 但可能越来越频繁地draw(16ms)


    }

    private void drawDegrees(Canvas canvas) {//这块应该是画表盘

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));



            System.out.println(getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {

        int halfWidth = mWidth / 2;

        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        int rEnd = (int) (mWidth * 0.4f);
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setTextSize(50);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top=fontMetrics.top;
        float bottom=fontMetrics.bottom;
        int lineWidth;
        for (int i = 0; i < 360; i++) {
            if (i % 30 == 0) {

                String text = ((i % 360) == 0 ? 12 : (i / 30)) + "";
                canvas.drawText(text,(int) (mCenterX + rEnd * Math.sin(Math.toRadians(i))),(int) (mCenterY - rEnd * Math.cos(Math.toRadians(i)))-top/2-bottom/2,mPaint);
                //canvas.drawText(text,mCenterX,mCenterY,mPaint);
                System.out.println("run");
        }
        }


    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        Calendar calendar=Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        float angle_Hour = (hour+(float)minute/60)*30;
        float angle_Minute = (minute+(float)second/60)*6;
        float angle_Second = second*6;


        int halfWidth = mWidth / 2;

        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        int rEnd = (int) (mWidth * 0.45f);

        canvas.save();
        //canvas.rotate(angle_Second);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH /2);//mWidth为整个的宽度
        paint.setColor(degreesColor);


        //int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(angle_Second)));
        //int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(angle_Second)));

        int stopX = (int) (mCenterX + rEnd * Math.sin(Math.toRadians(angle_Second)));
        int stopY = (int) (mCenterY - rEnd * Math.cos(Math.toRadians(angle_Second)));

        canvas.drawLine(mCenterX,mCenterY,stopX,stopY,paint);
        canvas.restore();

        canvas.save();
        //canvas.rotate(angle_Second);
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH );//mWidth为整个的宽度
        paint2.setColor(degreesColor);
        int rEnd2 = (int) (mWidth * 0.35f);


        //int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(angle_Second)));
        //int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(angle_Second)));

        stopX = (int) (mCenterX + rEnd2 * Math.sin(Math.toRadians(angle_Minute)));
        stopY = (int) (mCenterY - rEnd2 * Math.cos(Math.toRadians(angle_Minute)));

        canvas.drawLine(mCenterX,mCenterY,stopX,stopY,paint2);

        canvas.restore();

        canvas.save();
        //canvas.rotate(angle_Second);
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH );//mWidth为整个的宽度
        paint3.setColor(degreesColor);
        int rEnd3 = (int) (mWidth * 0.25f);


        //int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(angle_Second)));
        //int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(angle_Second)));

        stopX = (int) (mCenterX + rEnd3 * Math.sin(Math.toRadians(angle_Hour)));
        stopY = (int) (mCenterY - rEnd3 * Math.cos(Math.toRadians(angle_Hour)));

        canvas.drawLine(mCenterX,mCenterY,stopX,stopY,paint3);

    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }


}
