package com.jetlee.simpleedittext;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author：Jet啟思
 * @date:2018/8/3 10:02
 */
public class SampleEditText extends AppCompatEditText {

    // hint画笔
    private Paint textPaint;
    // 下划线画笔
    private Paint linePaint;
    // 长度画笔
    private Paint lengthPaint;
    // 错误语画笔
    private Paint errorPaint;

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
    // 最大text长度
    private int maxLength = 10;
    // 是否开启
    private boolean hasError;
    private String error;
    private Bitmap leftIconBitmap;
    private int leftIconSize = (int) Utils.dpToPx(24);
    // 限制字数的TextBounds
    private Rect maxLengthBounds;
    // 提示語Bounds
    private Rect tipsBounds;

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
        // 获取左边图标的资源ID
        leftIconId = typedArray.getResourceId(R.styleable.SampleEditText_leftIcon, -1);
        leftIconBitmap = Utils.getBitmap(getResources(), leftIconSize, leftIconId);
        // 获取最大字数限制
        maxLength = typedArray.getInt(R.styleable.SampleEditText_maxLength, -1);
        hasError = typedArray.getBoolean(R.styleable.SampleEditText_error, false);
        typedArray.recycle();
    }

    @SuppressLint("NewApi")
    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(hintSize);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.colorAccent));
        linePaint.setStrokeWidth(Utils.dpToPx(1));
        lengthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lengthPaint.setColor(Color.parseColor("#666666"));
        errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        errorPaint.setColor(getResources().getColor(R.color.colorAccent));

        setBackground(null);
        float leftPadding = 0;
        if (leftIconId != -1 && leftIconBitmap != null) {
            leftPadding = leftIconSize + Utils.dpToPx(6);
        }
        maxLengthBounds = new Rect();
        tipsBounds = new Rect();
        if (!TextUtils.isEmpty(error)) {
            linePaint.setTextSize(Utils.dpToPx(16));
            linePaint.getTextBounds(error, 0, error.length(), tipsBounds);
        }
        maxLengthBounds = tipsBounds;
        if (maxLength != -1) {
            String limitText = maxLength + " / " + getEditableText().length();
            lengthPaint.setTextSize(Utils.dpToPx(16));
            lengthPaint.getTextBounds(limitText, 0, limitText.length(), maxLengthBounds);
        }
        setPadding((int) (OFFSET_LABEL_LEFT + leftPadding), (int) (getPaddingTop() + OFFSET_LABEL + hintSize + OFFSET_LABEL_TOP),
                getPaddingRight(), (int) (getPaddingBottom() + maxLengthBounds.bottom - maxLengthBounds.top + Utils.dpToPx(8)));
        animatorDistance = OFFSET_LABEL + getTextSize();

        setTextChangedListener();
        setFocusChangeListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        canvas.drawLine(OFFSET_LABEL_LEFT + leftPadding, getBottom() - Utils.dpToPx(8) - Utils.dpToPx(8) - (maxLengthBounds.bottom - maxLengthBounds.top),
                getWidth() - getPaddingRight(), getBottom() - Utils.dpToPx(8) - Utils.dpToPx(8) - (maxLengthBounds.bottom - maxLengthBounds.top), linePaint);
        // 画text长度限制
        String limitText = maxLength + " / " + getEditableText().length();
        if (maxLength != -1) {
            canvas.drawText(limitText,
                    getWidth() - getPaddingRight() - (maxLengthBounds.right - maxLengthBounds.left) - Utils.dpToPx(4),
                    getBottom() - Utils.dpToPx(8), lengthPaint);
        }
        // 画错误提示语
        if (!TextUtils.isEmpty(error)) {
            canvas.drawText(error, 0, error.length(), OFFSET_LABEL_LEFT + leftPadding,
                    getBottom() - Utils.dpToPx(8), linePaint);
        }
    }

    private void setTextChangedListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = getText();
                if (maxLength != -1 && editable.length() > maxLength) {
                    String newStr = editable.toString().substring(0, maxLength);//截取新字符串
                    setText(newStr);
                    Selection.setSelection(getText(), newStr.length());
                    error = "字数最长是" + maxLength + "个字";
                } else {
                    error = "";
                }
                if (maxLength != -1) {
                    String limitText = maxLength + " / " + getEditableText().length();
                    linePaint.setTextSize(Utils.dpToPx(16));
                    linePaint.getTextBounds(limitText, 0, limitText.length(), maxLengthBounds);
                }

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
