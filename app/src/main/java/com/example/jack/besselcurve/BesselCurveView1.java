package com.example.jack.besselcurve;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jack on 2016/9/17.
 */

public class BesselCurveView1 extends View implements View.OnClickListener {
    //时间
    private String time;
    //所有步数
    private int allStop;
    //还有平均步数
    private int friendAverageStep;
    //平均步数
    private int averageStep;
    //排名
    private String ranking;
    //头像
    private Bitmap champion_icon;
    //冠军名字
    private String champion;
    //外圆的画笔
    private Paint mCirclePaint=null;





    //更多的path
    private Path morePath=null;
    //文字的间隔
    private int marginText=20;
    //整个圆的半径
    private int radius=0;
    //最近7天的间隔
    private int marginLineChart=25;
    //虚线距离文字的间隔
    private int marginBottomText=0;
    //整体view的颜色
    private int mBesselCurveColor;
    //文字的颜色
    private int besselColorText;
    //波浪颜色
    private int wavyColor=Color.parseColor("#00B4F8");
    //画圆弧的动画值
    private float mCircleNum=0;
    //中心值的动画值
    private int mCenterNum=0;
    //动画效果的添加
    private AnimatorSet animSet;
    //日期类
    private Calendar mCalendar=Calendar.getInstance();
    //一个星期的运动步数
    private List<Integer> mListStep=new ArrayList<>();
    //达标步数
    private int mStandardStop=4000;
    //柱状图一半的高
    private int mCircleRectHeight=30;
    //波浪动画值
    private int mOffset;
    //宽
    private int widthView;
    //高
    private int heightView;

    private int mWaveCount;
    int mWaveLength = 1000;

    public void setListStep(List<Integer> mListStep) { this.mListStep = mListStep; }

    public void setWavyColor(int wavyColor) { this.wavyColor = wavyColor; }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAllStop(int allStop) { this.allStop = allStop; }

    public void setFriendAverageStep(int friendAverageStep) { this.friendAverageStep = friendAverageStep; }

    public void setAverageStep(int averageStep) {
        this.averageStep = averageStep;
    }

    public void setRanking(String ranking) { this.ranking = ranking; }

    public void setChampion_icon(Bitmap champion_icon) {
        this.champion_icon = champion_icon;
    }

    public void setChampion(String champion) {
        this.champion = champion;
    }

    public BesselCurveView1(Context context) {
        this(context,null);
    }

    public BesselCurveView1(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public BesselCurveView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.BesselCurveView,defStyleAttr,0);
        int numCount=mTypedArray.getIndexCount();
        for(int i=0;i<numCount;i++){
            int attr=mTypedArray.getIndex(i);
            switch(attr){
                case R.styleable.BesselCurveView_allStep:
                    allStop=mTypedArray.getInt(attr,0);
                    break;
                case R.styleable.BesselCurveView_averageStep:
                    averageStep=mTypedArray.getInt(attr,0);
                    break;
                case R.styleable.BesselCurveView_friendAverageStep:
                    friendAverageStep = mTypedArray.getInt(attr,0);
                    break;
                case R.styleable.BesselCurveView_time:
                    time=mTypedArray.getString(attr);
                    break;
                case R.styleable.BesselCurveView_ranking:
                    ranking=mTypedArray.getString(attr);
                    break;
                case R.styleable.BesselCurveView_champion:
                    champion=mTypedArray.getString(attr);
                    break;
                case R.styleable.BesselCurveView_besselColor:
                    mBesselCurveColor=mTypedArray.getColor(attr,Color.BLUE);
                    break;
                case R.styleable.BesselCurveView_besselColorText:
                    besselColorText=mTypedArray.getColor(attr,Color.GRAY);
                    break;
            }
        }
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthModel=MeasureSpec.getMode(widthMeasureSpec);
        int heightModel=MeasureSpec.getMode(heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if(widthModel==MeasureSpec.EXACTLY){
            width=widthSize;
        }else{
            width=getPaddingLeft()+getPaddingRight()+widthSize*2/3;
        }
        if(heightModel==MeasureSpec.EXACTLY){
            height=heightSize;
        }else{
            height=getPaddingBottom()+getPaddingTop()+heightSize*2/3;
        }
        setMeasuredDimension(width,height);
    }

    public void initValue(){
        animSet=new AnimatorSet();
        //外圆的画笔
        mCirclePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(radius/10);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setAntiAlias(true);








        mWaveCount = (int) Math.round(widthView / mWaveLength + 1.5);
        marginBottomText=radius/4;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        widthView=w;
        heightView=h;
        if(w<h){
            radius=Float.valueOf((float)(w/3.7)).intValue();
        }else{
            radius=Float.valueOf((float)(h/3.7)).intValue();
        }
        initValue();
        startAnimator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(widthView/2,(heightView*((float)2/3))/2);

        //画内圆圈
        mCirclePaint.setColor(besselColorText);
        RectF mCircleRectF=new RectF(-radius,-radius,radius,radius);
        canvas.drawArc(mCircleRectF,120,300,false,mCirclePaint);

        //画外圆圈
        mCirclePaint.setColor(mBesselCurveColor);
        canvas.drawArc(mCircleRectF,120,mCircleNum,false,mCirclePaint);
//        canvas.drawArc(oval,
//                (float) (180 - floatAngel),
//                (float) (180 + 2 * floatAngel), false, paint);



        String tempFriendAverageStep=stringTemplate(R.string.besselTime,time);
        Rect mTopRect=new Rect();
        int halfTopWidthText=(mTopRect.right-mTopRect.left)/2;

        //画下边的文字
        String tempAverageStep=stringTemplate(R.string.friendAverageStep,friendAverageStep+"");
        Rect mBottomRect=new Rect();
        int halfBottomWidthText=(mBottomRect.right-mBottomRect.left)/2;
        int mBottomHeightText=(mBottomRect.bottom-mBottomRect.top);













    }

    public void startAnimator(){
        ValueAnimator mCircleAminator=ValueAnimator.ofFloat(0f,300f);
        mCircleAminator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircleNum=(float)animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator mCenterText=ValueAnimator.ofInt(0,allStop);
        mCenterText.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCenterNum=(int)animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator mWavyAnimator = ValueAnimator.ofInt(0, mWaveLength);
        mWavyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animSet.setDuration(2000);
        animSet.playTogether(mCircleAminator,mCenterText,mWavyAnimator);
        animSet.start();
    }

    //字符串拼接
    public String stringTemplate(int template,String content){
        return String.format(getContext().getResources().getString(template),content);
    }

    //画圆角图片
    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels,int width,int height) {
        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0,width,height);
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(),"onClick",Toast.LENGTH_SHORT).show();
    }

}
