package com.example.mifans.matrixtest.Activity.Views;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.example.mifans.matrixtest.R;
public class MultiView extends android.support.v7.widget.AppCompatImageView {
    private Context mContext;

    //类型：圆形or圆角
    private int type;
    //圆形
    public static final int TYPE_CIRCLE = 0;
    //圆角
    public static final int TYPE_ROUND = 1;
    //圆角默认半径
    private static final int BODER_RADIUS_DEFAULT = 10;
    //圆角半径
    private int mBorderRadius;

    private Paint mBitmapPaint;
    //圆形半径
    private int mRadius;

    //矩阵，主要用于缩小放大
    private Matrix mMatrix;

    //着色器
    private BitmapShader mBitmapShader;

    //view宽度
    private int mWidth;
    private RectF mRoundRect;

    public MultiView(Context context) {
        this(context, null);
    }

    public MultiView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }

    public MultiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        init(context, attrs);

    }

    public void init(Context context, AttributeSet attrs) {
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageView);

//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                        BODER_RADIUS_DEFAULT,
//                        getResources().getDisplayMetrics());将10dp转为安卓标准单位px
        mBorderRadius = typedArray.getDimensionPixelSize(
                                R.styleable.RoundImageView_borderRadius,
                               BODER_RADIUS_DEFAULT);// 默认为10dp
        type = typedArray.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLE);// 默认为Circle

        typedArray.recycle();
    }


    /**
     * 初始化BitmapShader
     */
    private void initBitmapShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitamp(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        mWidth = Math.min(getWidth(),getHeight());
        mRadius = mWidth/2;
        if (type == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;

            //圆角
        } else if (type == TYPE_ROUND) {

            if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())) {
                // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight() * 1.0f / bmp.getHeight());
            }

        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);

        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        initBitmapShader();

        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius,
                    mBitmapPaint);
        }else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
        }
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 圆角图片的范围
        if (type == TYPE_ROUND)
            mRoundRect = new RectF(0, 0, w, h);
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;

    }




}