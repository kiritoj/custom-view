package com.example.mifans.testview.CustomizeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyHistogram extends View {
    private Paint mpaintX;//画x轴
    private Paint mpaintY;//画y轴
    private Paint mpaintText;//写字
    private Paint mpaintRect;//绘制矩形
    private int marginleft = 80;//第一个柱形图距离y轴的位置
    private int edge = 120;//距离x轴和y轴的距离
    private int linecounts;//刻度线的条数
    private String name;//表名称
    private String[] strings;//x轴上每一个柱形图的名称
    private int max;//y轴最大值
    private int[] data;//柱形图数据
    private int[] colors;//每个柱形图的颜色
    int[] data1;
    float[] height;//记录每个矩形的高度
    boolean flag = false;

    public MyHistogram(Context context) {
        super(context);
        initPaints();
    }

    public MyHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
       // 下面这四个数据是x轴的起始点
        int startX = edge+getPaddingLeft();
        int startY = getHeight()-edge-getPaddingBottom();
        int stopX = getWidth()-getPaddingRight();
        int stopY = startY;
        //画x轴
        canvas.drawLine(startX,startY,stopX,startY,mpaintX);
        //画y轴
        canvas.drawLine(startX,startY,startX,getPaddingTop()+edge,mpaintY);

        //刻度线的间距
        int spacingVertical = (startY-getPaddingTop()-edge)/linecounts;
        //绘制刻度值及刻度线
        int scale = max/linecounts;
        //让文字在垂直方向居中显示
        Paint.FontMetrics fm = mpaintText.getFontMetrics();
        mpaintText.setTextAlign(Paint.Align.RIGHT);
        float mheight = (fm.descent-fm.ascent)/2-fm.descent;
        for (int i = 1; i <= linecounts; i++) {
            canvas.save();
            canvas.translate(0,-spacingVertical*i);
            mpaintX.setColor(Color.rgb(217,217,217));
            canvas.drawLine(startX,startY,stopX,startY,mpaintX);
            canvas.drawText(i*scale+"",startX-10,startY+mheight,mpaintText);
            canvas.restore();
        }

        //绘制图的名称
        mpaintText.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(name,startX,50,mpaintText);
        //绘制x轴的文字
        mpaintText.setTextAlign(Paint.Align.CENTER);
        int spaceHorizontal = (getHeight()-edge-marginleft)/strings.length;
        for (int i = 0; i < strings.length ; i++) {
            int width = edge+marginleft+i*spaceHorizontal;
            canvas.drawText(strings[i],width+50,startY+50,mpaintText);
        }

        //计算每个矩形的高度
        for (int i = 0; i < height.length; i++) {
             height[i] = ((float)(max-data[i])/max)*(getHeight()-2*edge-getPaddingTop()-getPaddingBottom())+edge+getPaddingTop();
        }

        //绘制柱形图以及顶部的数字
        for (int i = 0; i < colors.length; i++) {
            int width = edge+marginleft+i*spaceHorizontal;
            //计算柱形图占view高度的多少

            mpaintRect.setColor(colors[i]);



                canvas.drawRoundRect(width + getPaddingLeft(),data1[i], width + 100, startY, 10, 10, mpaintRect);
                canvas.drawText(data[i] + "", width + 50 + getPaddingLeft(), data1[i] - 10, mpaintText);




        }
        //只要还存在一个矩形区域没有达到指定高度，就继续重绘
        for (int i = 0; i < data1.length; i++) {
            if (data1[i] > height[i]){
                flag = true;
            }
        }
        if (flag){
            for (int i = 0; i < data1.length; i++) {
                data1[i] = data1[i]-1>height[i]?data1[i]-3:data1[i];//每次减少1来达到动画效果
            }
            postInvalidateDelayed(10);
        }




    }
    public void initPaints(){
        mpaintX = new Paint();
        mpaintY = new Paint();
        mpaintText = new Paint();
        mpaintRect = new Paint();
        mpaintX.setColor(Color.rgb(243,104,109));
        mpaintX.setStrokeWidth(5);
        mpaintY.setColor(Color.rgb(243,104,109));
        mpaintY.setStrokeWidth(5);
        mpaintText.setColor(Color.rgb(243,104,109));
        mpaintText.setTextSize(50);
        mpaintRect.setStyle(Paint.Style.FILL);







    }
    public void setLinecounts(int counts){
        //设置刻度线的条数
        this.linecounts = counts;
    }
    public void setname(String name){
        this.name = name;

    }
    public void setxItemName(String[] strings){
        this.strings = strings;
    }
    public void setMax(int max){
        this.max = max;
    }

    public void setData(int[] data) {
        this.data = data;
        data1 = new int[data.length];
        height = new float[data.length];

    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        for (int i = 0; i < data1.length; i++) {
            data1[i] = MeasureSpec.getSize(heightMeasureSpec)-edge-getPaddingBottom();

        }
    }
}
