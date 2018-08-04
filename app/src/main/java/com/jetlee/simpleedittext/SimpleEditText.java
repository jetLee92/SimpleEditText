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
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * @author：Jet啟思
 * @date:2018/8/3 10:02
 */
public class SimpleEditText extends AppCompatEditText implements View.OnClickListener {

    // hint画笔
    private Paint textPaint;
    // 下划线画笔
    private Paint linePaint;
    // 限制长度画笔
    private Paint lengthPaint;
    // 错误语画笔
    private Paint errorPaint;

    // hint字体大小
    private static float hintSize = Utils.dpToPx(14);
    // label与输入框的距离
    private static float OFFSET_LABEL = Utils.dpToPx(8);
    // 左边距
    private static float OFFSET_LABEL_LEFT = Utils.dpToPx(6);
    private int defaultColor = Color.parseColor("#666666");
    private int leftIconSize = (int) Utils.dpToPx(16);
    private int rightIconSize = (int) Utils.dpToPx(20);
    // label是否显示，根据字数判断
    private boolean isLabelShow;
    // 动画系数
    private float labelFraction;
    private ObjectAnimator objectAnimator;
    // hint动画的距离
    private float animatorDistance = OFFSET_LABEL + hintSize;

    /************* 自定义的style *****************/
    // 左边图标ID
    private int leftIconId;
    private Bitmap leftIconBitmap;
    // 右边清楚图标ID
    private int clearIconId;
    private Bitmap clearBitmap;
    // 最大text长度
    public int maxLength;
    // 是否开启错误提示
    private boolean hasError;
    // 是否有下划线
    private boolean hasDeadline;
    // hint颜色
    private int hintColor;
    // 错误语颜色
    private int errorColor;
    // 字数限制颜色
    private int lengthColor;
    // 下划线颜色
    private int deadlineColor;
    int deadlinePadding;
    // 错误提示语
    private String error = "请输入正确的手机号码";
    // 下划线颜色

    private boolean isClear;
    private boolean isHasText;
    // 限制字数的TextBounds
    private Rect maxLengthBounds;

    public SimpleEditText(Context context) {
        super(context);
    }

    public SimpleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    public SimpleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    // 是否显示顶部的hint
    private boolean hasTopHint;

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleEditText);
        hasTopHint = typedArray.getBoolean(R.styleable.SimpleEditText_hasTopHint, false);
        // 获取左边图标的资源ID
        leftIconId = typedArray.getResourceId(R.styleable.SimpleEditText_leftIcon, -1);
        leftIconBitmap = Utils.getBitmap(getResources(), leftIconSize, leftIconId);
        clearIconId = typedArray.getResourceId(R.styleable.SimpleEditText_rightClear, -1);
        clearBitmap = Utils.getBitmap(getResources(), rightIconSize, clearIconId);
        // 获取最大字数限制
        maxLength = typedArray.getInt(R.styleable.SimpleEditText_maxLength, -1);
//        hasError = typedArray.getBoolean(R.styleable.SimpleEditText_error, false);
        hasDeadline = typedArray.getBoolean(R.styleable.SimpleEditText_hasDeadline, true);
        deadlineColor = typedArray.getColor(R.styleable.SimpleEditText_deadlineColor, getResources().getColor(R.color.colorAccent));
        hintColor = typedArray.getColor(R.styleable.SimpleEditText_hintColor, defaultColor);
        errorColor = typedArray.getColor(R.styleable.SimpleEditText_errorColor, getResources().getColor(R.color.colorAccent));
        lengthColor = typedArray.getColor(R.styleable.SimpleEditText_lengthColor, defaultColor);
        typedArray.recycle();
    }

    @SuppressLint("NewApi")
    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(hintSize);
        textPaint.setColor(hintColor);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(deadlineColor);
        linePaint.setStrokeWidth(Utils.dpToPx(1));
        lengthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lengthPaint.setColor(lengthColor);
        errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        errorPaint.setColor(errorColor);
        errorPaint.setTextSize(Utils.dpToPx(16));

        setBackground(null);
        float leftPadding = 0;
        if (leftIconId != -1 && leftIconBitmap != null) {
            leftPadding = leftIconSize + Utils.dpToPx(6);
        }
        int rightPadding = 0;
        if (clearIconId != -1 && clearBitmap != null) {
            rightPadding = (int) (rightIconSize + Utils.dpToPx(12));
        }
        maxLengthBounds = new Rect();
        Rect tipsBounds = new Rect();
        if (!TextUtils.isEmpty(error)) {
            errorPaint.getTextBounds(error, 0, error.length(), tipsBounds);
        }
        maxLengthBounds = tipsBounds;
        if (maxLength != -1) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            String limitText = maxLength + " / " + getEditableText().length();
            lengthPaint.setTextSize(Utils.dpToPx(16));
            lengthPaint.getTextBounds(limitText, 0, limitText.length(), maxLengthBounds);
        }
        if (!hasTopHint) {
            OFFSET_LABEL = 0;
            hintSize = 0;
        } else {
            OFFSET_LABEL = Utils.dpToPx(8);
            hintSize = Utils.dpToPx(14);
        }
        deadlinePadding = 0;
        if (hasDeadline) {
            deadlinePadding = (int) Utils.dpToPx(8);
        }
        if (!hasDeadline && !TextUtils.isEmpty(error)) {
            deadlinePadding = (int) Utils.dpToPx(4);
        }
        setPadding((int) (OFFSET_LABEL_LEFT + leftPadding), (int) (getPaddingTop() + OFFSET_LABEL + hintSize),
                getPaddingRight() + rightPadding, (int) (getPaddingBottom() + maxLengthBounds.bottom - maxLengthBounds.top + deadlinePadding));
        animatorDistance = OFFSET_LABEL + getTextSize();
        // 输入框监听
        setTextChangedListener();
        // 焦点监听
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
            canvas.drawBitmap(leftIconBitmap, OFFSET_LABEL_LEFT,
                    getPaddingTop() + Utils.dpToPx(2) + getLineHeight() * getLineCount() / 2 - leftIconBitmap.getHeight() / 2, linePaint);
            leftPadding = leftIconSize + Utils.dpToPx(6);
        }
        // 画右边清楚图标
        if (clearIconId != -1 && clearBitmap != null) {
            canvas.drawBitmap(clearBitmap, getWidth() - rightIconSize - Utils.dpToPx(12),
                    getPaddingTop() + Utils.dpToPx(2) + getLineHeight() * getLineCount() / 2 - rightIconSize / 2, linePaint);
        }
        // 画hint
        if (hasTopHint) {
            CharSequence hint = getHint();
            textPaint.setAlpha((int) (labelFraction * 0xff));
            canvas.drawText(hint, 0, hint.length(), OFFSET_LABEL_LEFT + leftPadding,
                    getPaddingTop() - OFFSET_LABEL + (1 - labelFraction) * animatorDistance, textPaint);
        }
        // 画下划线
        if (hasDeadline) {
            canvas.drawLine(OFFSET_LABEL_LEFT + leftPadding, getBottom() - Utils.dpToPx(8) - Utils.dpToPx(8) - (maxLengthBounds.bottom - maxLengthBounds.top),
                    getWidth() - Utils.dpToPx(4), getBottom() - Utils.dpToPx(8) - Utils.dpToPx(8) - (maxLengthBounds.bottom - maxLengthBounds.top), linePaint);
        }
        // 画text长度限制
        String limitText = maxLength + " / " + getEditableText().length();
        if (maxLength != -1) {
            canvas.drawText(limitText,
                    getWidth() - Utils.dpToPx(4) - (maxLengthBounds.right - maxLengthBounds.left) - Utils.dpToPx(4),
                    getBottom() - Utils.dpToPx(8), lengthPaint);
        }
        // 画错误提示语
        if (!TextUtils.isEmpty(error)) {
            int errorPadding = (int) Utils.dpToPx(8);
            canvas.drawText(error, 0, error.length(), OFFSET_LABEL_LEFT + leftPadding,
                    getBottom() - errorPadding, errorPaint);
        }

    }

    private void setTextChangedListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isHasText = s.length() > 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    if (hasTopHint) {
                        getAnimator().start();
                    }
                } else if (s.length() == 0 && !isClear || (s.length() == 0 && isHasText)) {
                    isLabelShow = false;
                    if (hasTopHint) {
                        getAnimator().reverse();
                    }
                }
                isHasText = false;
                isClear = false;
            }
        });
    }

    private void setFocusChangeListener() {
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    linePaint.setColor(deadlineColor);
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
            objectAnimator = ObjectAnimator.ofFloat(SimpleEditText.this, "labelFraction", 0, 1);
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

    /**
     * 设置错误提示
     *
     * @param error
     */
    public void setError(String error) {
        this.error = error;
        invalidate();
    }

    /**
     * 获取最大的长度限制
     *
     * @return maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public void onClick(View v) {
        isClear = true;
        setText("");
        Toast.makeText(getContext(), "输入框已清空", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (clearIconId != -1 && clearBitmap != null) {
                    if ((getWidth() - getPaddingRight() < x && x < getWidth())) {
                        onClick(this);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
