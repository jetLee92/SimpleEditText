package com.jetlee.simpleedittext;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/8/3.
 */

public class SampleEditText extends AppCompatEditText {

    private Paint textPaint;
    // hint字体大小
    private static float hintSize = Utils.dpToPx(14);
    // label与输入框的距离
    private static float OFFSET_LABEL = Utils.dpToPx(10);
    // label底部距离最顶部的距离
    private static float OFFSET_LABEL_TOP = Utils.dpToPx(12);
    // label是否显示，根据字数判断
    private boolean isLabelShow;
    // 动画系数
    private float labelFraction;
    private ObjectAnimator objectAnimator;
    // hint动画的距离
    private float animatorDistance = OFFSET_LABEL + hintSize;

    public SampleEditText(Context context) {
        super(context);
    }

    public SampleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SampleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setPadding(getPaddingLeft(), (int) (getPaddingTop() + OFFSET_LABEL + hintSize + OFFSET_LABEL_TOP), getPaddingRight(), getPaddingBottom());
        animatorDistance = OFFSET_LABEL + getTextSize();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(hintSize);
        setTextChangedListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        CharSequence hint = getHint();
        textPaint.setAlpha((int) (labelFraction * 0xff));
//        textPaint.setTextSize();
        canvas.drawText(hint, 0, hint.length(), getPaddingLeft(),
                hintSize + OFFSET_LABEL_TOP + (1 - labelFraction) * animatorDistance, textPaint);

    }

    private void setTextChangedListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !isLabelShow) {  // 只在第一次显示的时候做动画
                    isLabelShow = true;
                    getAnimator().start();
                } else if (s.length() == 0) {
                    isLabelShow = false;
                    getAnimator().reverse();
                }
            }
        });
    }

    private ObjectAnimator getAnimator() {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(SampleEditText.this, "labelFraction", 0, 1);
        }
        return objectAnimator;
    }

    public float getLabelFraction() {
        return labelFraction;
    }

    public void setLabelFraction(float labelFraction) {
        this.labelFraction = labelFraction;
        invalidate();
    }
}
