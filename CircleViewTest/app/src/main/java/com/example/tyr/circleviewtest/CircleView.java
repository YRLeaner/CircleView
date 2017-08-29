package com.example.tyr.circleviewtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tyr on 2017/8/28.
 */
public class CircleView extends View {

    private int len;
    private RectF oval;



    //弧形的半径
    private float radius;
    //小球弧形距离
    private float pDisArc;
    //小球数量
    private int count;
    //文字大小
    private int textSize;

    //弧形的画笔
    private Paint paint;
    // 绘制文字
    private Paint textPaint;
    // 画水球的画笔
    private Paint waterPaint;
    boolean useCenter = false;

    //起始角度
    private float startAngle=150;
    //经过角度
    private float sweepAngle=240;
    //目标角度
    private float targetAngle = 0;
    //转过的角度占比
    float percent;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(context, attrs, defStyleAttr);
        initPaint();
    }

    private void parseAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleView,defStyleAttr,0);
        pDisArc = typedArray.getDimensionPixelSize(R.styleable.CircleView_pointDisArc,dp2px(context,20));
        textSize = typedArray.getDimensionPixelSize(R.styleable.CircleView_textSize,dp2px(context,20));
        typedArray.recycle();
    }

    private static int dp2px(Context pContext,int pDpVal){
        float _Scale = pContext.getResources().getDisplayMetrics().density;
        return (int)(pDpVal*_Scale+0.5f*(pDpVal>=0?1:-1));
    }

    private void initPaint() {

        paint =new Paint();
        //设置画笔颜色
        paint.setColor(Color.BLACK);
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //让画出的图形是空心的(不填充)
        paint.setStyle(Paint.Style.STROKE);

        //文本画笔
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        //水波纹画笔
        waterPaint = new Paint();
        waterPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
        drawViewLine(canvas);
        drawArow(canvas);
        drawText(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //根据模式测算长宽

        int width = measureSize(widthMeasureSpec);
        int height = measureSize(heightMeasureSpec);
        //以最小值为刻度区域正方形的长
        len = Math.min(width, height);
        //确定圆弧所在的矩形区域
        oval = new RectF(0, 0, len, len);
        radius = len/2;
        //设置测量高度和宽度

        setMeasuredDimension(len, len);
    }

    private int measureSize(int measureSpec){
        int reslut = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode==MeasureSpec.EXACTLY){
            reslut = specSize;
        }else{
            reslut = 200;
            if (specMode==MeasureSpec.AT_MOST){
                reslut = Math.min(reslut,specSize);
            }
        }

        return  reslut;
    }

    private void drawViewLine(Canvas canvas) {
        canvas.save();
        //移动canvas
        canvas.translate(radius,radius);
        //旋转canvas
        canvas.rotate(startAngle-90);
        //普通刻度
        Paint linePatin=new Paint();
        //设置普通刻度画笔颜色
        linePatin.setColor(Color.BLACK);
        //线宽
        linePatin.setStrokeWidth(2);
        //设置画笔抗锯齿
        linePatin.setAntiAlias(true);
       /* //画一条刻度线
        canvas.drawLine(0,radius,0,radius-40,linePatin);*/
        //画101条刻度线
        //确定每次旋转的角度
        float rotateAngle=sweepAngle/59;
        //绘制需要有颜色部分的画笔
        Paint targetLinePatin=new Paint();
        targetLinePatin.setColor(Color.GREEN);
        targetLinePatin.setStrokeWidth(2);
        targetLinePatin.setAntiAlias(true);
        //记录已经绘制过的有色部分范围(角度float)
        float hasDraw=0;
        for(int i=0;i<60;i++){
            if (hasDraw <= targetAngle && targetAngle != 0) {
                percent=hasDraw/sweepAngle;
                int red= 255-(int) (255*percent);
                int green= (int) (255*percent);
                //角度变化颜色值由外界传入而改变
                if (onAngleColorListener!=null){
                    onAngleColorListener.colorListener(red,green);
                }
                targetLinePatin.setARGB(255,red,green,0);
                //计算已经绘制的比例
                canvas.drawCircle(0,radius-pDisArc,10,targetLinePatin);
               // canvas.drawLine(0, radius-20, 0, radius - 30, targetLinePatin);
            } else {
                canvas.drawCircle(0,radius-pDisArc,5,linePatin);
                //canvas.drawLine(0,radius-20,0,radius-30,linePatin);
            }
            hasDraw += rotateAngle;
            canvas.rotate(rotateAngle);
        }

        //操作完成后恢复状态
        canvas.restore();
    }



    private void drawArow(Canvas canvas){
        canvas.save();
        canvas.translate(radius, radius);
        canvas.rotate(targetAngle+startAngle-90);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.arrow);
        float left =0 ;
        float top = radius-150;
        canvas.drawBitmap(bitmap,left,top,paint);
        canvas.restore();
    }

    private void drawText(Canvas canvas){
        canvas.save();
        canvas.translate(radius, radius);
        canvas.drawText("仪表盘", 0, 0, textPaint);
        canvas.drawText((int)(percent*100)+"%",0,-80,textPaint);
        canvas.restore();
    }




    //判断是否在动
    private boolean isRunning;
    //判断是回退的状态还是前进状态
    private int state = 1;

    /**
     * 5 根据角度变化刻度
     * @param trueAngle
     */
    public void changeAngle(final float trueAngle) {
        if (isRunning){//如果在动直接返回
            return;
        }
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (state) {
                    case 1://后退状态
                        isRunning = true;
                        targetAngle -= 3;
                        if (targetAngle <= 0) {//如果回退到0
                            targetAngle = 0;
                            //改为前进状态
                            state = 2;
                        }
                        break;
                    case 2://前进状态
                        targetAngle += 3;
                        if (targetAngle >= trueAngle) {//如果增加到指定角度
                            targetAngle = trueAngle;
                            //改为后退状态
                            state = 1;
                            isRunning = false;
                            //结束本次运动
                            timer.cancel();
                        }
                        break;
                    default:
                        break;
                }
                //重新绘制（子线程中使用的方法）
                postInvalidate();
            }
        }, 500, 30);
    }

    private OnAngleColorListener onAngleColorListener;

    public void setOnAngleColorListener(OnAngleColorListener onAngleColorListener) {
        this.onAngleColorListener = onAngleColorListener;
    }

    public interface OnAngleColorListener{
        void colorListener(int red,int green);
    }

}
