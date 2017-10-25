package wheel.chenmh.com.bigwheel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 自定义View
 * Created by cccxx on 2017/10/17.
 */
public class BitWheelView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private SurfaceHolder holder;
    /**
     * 圆的直径
     */
    private int mRadius;
    /**
     * 控件的padding，这里我们认为4个padding的值一致，以paddingleft为标准
     */
    private int mPadding;
    /**
     * 中心点
     */
    private int mCenter;
    /**
     * 绘制转盘的画笔
     */
    private Paint mPaint;
    /**
     * 初始化文字的画笔
     */
    private Paint mTextPaint;
    /**
     * 文字的大小
     */
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
    /**
     * 绘制圆弧
     */
    private RectF mRange = new RectF();
    /**
     * 初始化图片
     */
    private Bitmap[] mImgBitmap;
    /**
     * 转盘的个数
     */
    private int mCount = 6;
    /**
     * 设置线程开关
     */
    private boolean isRunning;
    private Thread mThread;
    /**
     * 获取surface中的画布（通过绑定）
     */
    private Canvas canvas;
    /**
     * 滚动的速度
     */
    private float mStartAngle = 0;
    /**
     * 转盘的颜色
     */
    private int[] mColors = {0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
            0xFFF17E01, 0xFFFFC300, 0xFFF17E01 };
    /**
     * 抽奖的文字
     */
    private String[] mTexts = {"苹果8手机", "没中奖", "美女一枚", "10元优惠卷",
            "没有中奖", "10元优惠卷" };

    /**
     * 是否点击了停止
     */
    private boolean isShouldEnd;
    /**
     * 滚动的速度
     */
    private double mSpeed;
    private int[] imgs={R.drawable.a,R.drawable.c,R.drawable.b,R.drawable.d,R.drawable.c,R.drawable.d};
    public BitWheelView(Context context) {
        super(context,null);
    }

    public BitWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //使用getHolder方法获取SurfaceView
        holder = getHolder();
        holder.addCallback(this);
        //可以获取焦点
        setFocusable(true);
        this.setKeepScreenOn(true);
    }

    //设置控件为正方形
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Math中的min方法是用来比较两个数大小的，比较结果中返回较小的那个数值
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        //padding值表示四个边的宽度
        mPadding = getPaddingLeft();
        //中心点
        mCenter= width / 2;
        //绘制的View的大小
        setMeasuredDimension(width,width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //初始化绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFffffff);
        mTextPaint.setTextSize(mTextSize);
        //绘制圆弧
        mRange = new RectF(getPaddingLeft(),getPaddingRight(),mRadius+getPaddingLeft(),mRadius+getPaddingLeft());
        //初始化图片
        mImgBitmap = new Bitmap[mCount];//mCount设置转盘图片的个数
        for (int i=0;i<mCount;i++){
            //设置循环使用的图片
            mImgBitmap[i] = BitmapFactory.decodeResource(getResources(),imgs[i]);
        }
        //开启线程
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();//开启线程
    }
    //不进行任何改变操作
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    //通知销毁关闭线程
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }
    //开启线程操作的绘制
    @Override
    public void run() {
        //不断进行draw
        while (isRunning){
            //开始时间
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if (end-start<50){
                        Thread.sleep(50-(end-start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //背景图
    private Bitmap mBigBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg2);
    private void
    draw() {
        //获得画布Canvas
        canvas = holder.lockCanvas();
        try {

        if (canvas!=null){
            //绘制背景 根据当前旋转的mStartAngle计算当前滚动到的区域 绘制背景
            canvas.drawColor(0xFFFFFFFF);
            canvas.drawBitmap(mBigBitmap, null, new Rect(mPadding / 2,
                    mPadding / 2, getMeasuredWidth() - mPadding / 2,
                    getMeasuredWidth() - mPadding / 2), null);
            //绘制每块 每块上的文本图片
            float tmpAngle = mStartAngle;
            float sweepAngle = (float) (360 / mCount);
            //绘制6个区域板块
            for (int i=0;i<mCount;i++){
                //绘制每块
                mPaint.setColor(mColors[i]);//每块地颜色
                canvas.drawArc(mRange,tmpAngle,sweepAngle,true,mPaint);
                //绘制文本
                drawText(tmpAngle,sweepAngle,mTexts[i]);
                //绘制icon
                drawIcon(tmpAngle,i);
                tmpAngle+=sweepAngle;
            }

            // 如果mSpeed不等于0，则相当于在滚动
            mStartAngle += mSpeed;
            // 点击停止时，设置mSpeed为递减，为0值转盘停止
            if (isShouldEnd){
                mSpeed -=1;
            }
            if (mSpeed<=0){
                mSpeed=0;
                isShouldEnd = false;
            }
            // 根据当前旋转的mStartAngle计算当前滚动到的区域
            calInExactArea(mStartAngle);
        }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (canvas!=null){
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    //绘制文本
    private void drawText(float tmpAngle, float sweepAngle, String mText) {
        Path mPath = new Path();
        mPath.addArc(mRange,tmpAngle,sweepAngle);
        float textWith = mTextPaint.measureText(mText);
        // 利用水平偏移让文字居中
        float hOffset = (float) (mRadius * Math.PI / mCount/ 2 - textWith / 2);// 水平偏移
        float vOffset = mRadius / 2 / 6;// 垂直偏移
        canvas.drawTextOnPath(mText,mPath,hOffset,vOffset,mTextPaint);
    }
    //绘制icon
    private void drawIcon(float tmpAngle, int i) {
        // 设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;

        float angle = (float) ((30 + tmpAngle) * (Math.PI / 180));

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        // 确定绘制图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);

        canvas.drawBitmap(mImgBitmap[i], null, rect, null);
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域
     * @param mStartAngle
     */
    private void calInExactArea(float mStartAngle) {
        // 让指针从水平向右开始计算
        float rotate = mStartAngle + 90;
        rotate %= 360.0;
        for (int i = 0; i <mCount; i++)
        {
            // 每个的中奖范围
            float from = 360 - (i + 1) * (360 / mCount);
            float to = from + 360 - (i) * (360 / mCount);

            if ((rotate > from) && (rotate < to))
            {
                return;
            }
        }
    }
    /**
     * 点击开始旋转
     *
     * @param luckyIndex
     */
    public void luckyStart(int luckyIndex)
    {
        // 每项角度大小
        float angle = (float) (360 / mCount);
        // 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210-270；）
        float from = 270 - (luckyIndex + 1) * angle;
        float to = from + angle;
        // 停下来时旋转的距离
        float targetFrom = 4 * 360 + from;
        /**
         * <pre>
         *  (v1 + 0) * (v1+1) / 2 = target ;
         *  v1*v1 + v1 - 2target = 0 ;
         *  v1=-1+(1*1 + 8 *1 * target)/2;
         * </pre>
         */
        float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
        float targetTo = 4 * 360 + to;
        float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;

        mSpeed = (float) (v1 + Math.random() * (v2 - v1));
        isShouldEnd = false;
    }

    public void luckyEnd()
    {
        mStartAngle = 0;
        isShouldEnd = true;
    }

    public boolean isStart()
    {
        return mSpeed != 0;
    }

    public boolean isShouldEnd()
    {
        return isShouldEnd;
    }
}
