package com.jetlee.simpleedittext;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author：Jet啟思
 * @date:2018/8/3 10:02
 */
public class SampleEditText extends AppCompatEditText {

    private Paint textPaint;
    private Paint linePaint;
    // hint字体大小
    private static float hintSize = Utils.dpToPx(14);
    // label与输入框的距离
    private static float OFFSET_LABEL = Utils.dpToPx(8);
    // 左边距
    private static float OFFSET_LABEL_LEFT = Utils.dpToPx(4);
    // label底部距离最顶部的距离
    private static float OFFSET_LABEL_TOP = Utils.dpToPx(0);
    // label是否显示，根据字数判断
    private boolean isLabelShow;
    // 动画系数
    private float labelFraction;
    private ObjectAnimator objectAnimator;
    // hint动画的距离
    private float animatorDistance = OFFSET_LABEL + hintSize;

    // 左边图标ID
    private int leftIconId;
    private Bitmap leftIconBitmap;
    private int leftIconSize = (int) Utils.dpToPx(24);

    public SampleEditText(Context context) {
        super(context);
    }

    public SampleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    public SampleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SampleEditText);
        leftIconId = typedArray.getResourceId(R.styleable.SampleEditText_leftIcon, -1);
        leftIconBitmap = Utils.getBitmap(getResources(), leftIconSize, leftIconId);
        typedArray.recycle();
    }

    @SuppressLint("NewApi")
    private void init() {

        setBackground(null);
        float leftPadding = 0;
        if (leftIconId != -1 && leftIconBitmap != null) {
            leftPadding = leftIconSize + Utils.dpToPx(6);
        }
        setPadding((int) (OFFSET_LABEL_LEFT + leftPadding), (int) (getPaddingTop() + OFFSET_LABEL + hintSize + OFFSET_LABEL_TOP), getPaddingRight(), getPaddingBottom());
        animatorDistance = OFFSET_LABEL + getTextSize();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(hintSize);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.colorAccent));
        linePaint.setStrokeWidth(Utils.dpToPx(1));
        setTextChangedListener();
        setFocusChangeListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float leftPadding = 0;
        // 画左边icon
        if (leftIconId != -1 && leftIconBitmap != null) {
            canvas.drawBitmap(leftIconBitmap, OFFSET_LABEL_LEFT, getPaddingTop(), linePaint);
            leftPadding = leftIconSize + Utils.dpToPx(6);
        }
        // 画hint
        CharSequence hint = getHint();
        textPaint.setAlpha((int) (labelFraction * 0xff));
        canvas.drawText(hint, 0, hint.length(), OFFSET_LABEL_LEFT + leftPadding,
                getPaddingTop() - OFFSET_LABEL + (1 - labelFraction) * animatorDistance, textPaint);
        // 画下划线
        canvas.drawLine(OFFSET_LABEL_LEFT + leftPadding, getBottom() - Utils.dpToPx(8),
                getWidth() - getPaddingRight(), getBottom() - Utils.dpToPx(8), linePaint);
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

    private void setFocusChangeListener() {
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    linePaint.setColor(getResources().getColor(R.color.colorAccent));
                    linePaint.setStrokeWidth(Utils.dpToPx(2));
                } else {
                    linePaint.setColor(Color.parseColor("#666666"));
                    linePaint.setStrokeWidth(Utils.dpToPx(1));
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
