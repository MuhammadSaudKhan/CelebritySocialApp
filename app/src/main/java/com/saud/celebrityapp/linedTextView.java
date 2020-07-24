package com.saud.celebrityapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class linedTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Rect mRect;
    private Paint mPaint;

    // we need this constructor for LayoutInflater
    public linedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20f);
        mPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;

        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);
            //canvas.drawRGB(0,0,0);
            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
        }

        super.onDraw(canvas);
    }
}