package com.example.administrator.myverifycode.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.myverifycode.R;

/**
 * 验证码输入效果
 * Created by zzwwws on 2015/9/9.
 */
public class MaterialVerifyGroup extends LinearLayout implements View.OnKeyListener, MaterialShortEditText.TouchListener, View.OnFocusChangeListener {

    private Context context;

    private StringBuilder sb;

    private final int childcount = 4;

    private MaterialVerifyGroup verifyGroup;

    private WatcherListener listener;

    private String codes;

    private int hintTextsize;

    private String hintText;

    private int hintColor;

    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public MaterialVerifyGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialVerifyGroup(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        this.context = context;
        verifyGroup = this;

        hintTextsize = context.getResources().getDimensionPixelSize(R.dimen.text_size_16);
        hintColor = context.getResources().getColor(R.color.color_grey_999999);
        sb = new StringBuilder();
        for (int i = 0; i < childcount; i++) {
            addMaterialEditText(i);
        }

        MaterialShortEditText first = (MaterialShortEditText) verifyGroup.getChildAt(0);
        first.clearFocus();
        first.requestFocus();

        setHint(context.getString(R.string.verify_group_hint));
    }

    private void addMaterialEditText(int pos) {
        final MaterialShortEditText editText = new MaterialShortEditText(context);
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.rightMargin = 28;
        editText.setLayoutParams(params);
        editText.setBaseColor(context.getResources().getColor(R.color.color_alpha_45_ffffff));
        editText.setPrimaryColor(context.getResources().getColor(R.color.white));
        editText.setTypeface(Typeface.DEFAULT_BOLD);
        editText.setTextColor(context.getResources().getColor(R.color.white));
        editText.setTextSize(22);
        editText.setSingleLine(true);
        editText.setCursorVisible(false);
        editText.setFocusRequestBold(true);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTouchListener(this);
        if (pos != childcount - 1) {
            editText.setOnFocusChangeListener(this);
        }
        editText.setOnKeyListener(this);
        editText.setTag(pos);

        InputFilter filter = new InputFilter.LengthFilter(1);
        editText.setFilters(new InputFilter[]{filter});
        addView(editText, pos);

        editText.addTextChangedListener(new CustomWatcher(pos));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            dispatchDelEvent();
            return true;
        }
        return false;
    }

    private void dispatchDelEvent() {
        int valuePosition = getLastNoneEmptyIndex();
        if (valuePosition < 0) return;
        MaterialShortEditText editText = (MaterialShortEditText) verifyGroup.getChildAt(valuePosition);
        editText.setText("");
        editText.requestFocus();
    }

    @Override
    public boolean dispatchEvent(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int valuePosition = getLastNoneEmptyIndex();
            if (valuePosition != childcount - 1) {
                valuePosition += 1;
            }

            activity.showKeyboardDelayed(verifyGroup.getChildAt(valuePosition));

        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        MaterialShortEditText editText = (MaterialShortEditText) v;
        MaterialShortEditText editTextNext;
        int tag = (int) v.getTag();
        if (tag < childcount - 1) {
            editTextNext = (MaterialShortEditText) verifyGroup.getChildAt((int) (v.getTag()) + 1);
            if (hasFocus) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    editTextNext.requestFocus();
                }
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.isEmpty(hintText)) {
            Paint.FontMetrics fm = new Paint.FontMetrics();
            textPaint.setColor(hintColor);
            textPaint.setTextSize(hintTextsize);
            textPaint.getFontMetrics(fm);

            float measureHintWidth = textPaint.measureText(hintText);
            float startX = getScrollX() + getWidth() / 2 - measureHintWidth / 2;
            float startY = getScrollY() + hintTextsize + Density.dp2px(context, 10);

            canvas.drawText(hintText, startX, startY, textPaint);
        }
        super.onDraw(canvas);
    }

    public interface WatcherListener {
        void actionEnd();
    }

    public void setWatcherLister(WatcherListener listener) {
        this.listener = listener;
    }

    class CustomWatcher implements TextWatcher {

        private int pos = 0;

        private MaterialShortEditText editTextCur;

        public CustomWatcher(int pos) {
            this.pos = pos;
            editTextCur = (MaterialShortEditText) verifyGroup.getChildAt(pos);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (sb.length() == 1) {
                sb.deleteCharAt(0);

            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (pos < childcount - 1) {
                MaterialShortEditText editTextNext = (MaterialShortEditText) getChildAt(pos + 1);
                if (sb.length() == 0 && editTextCur.length() == 1) {
                    sb.append(s);
                    editTextCur.clearFocus();
                    editTextNext.requestFocus();

                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (sb.length() == 0) {
                editTextCur.requestFocus();
            }
            if (pos == 0) {
                setHint(s.length() > 0 ? null : context.getString(R.string.verify_group_hint));
            }
            if (pos == childcount - 1 && s.length() > 0) {
                editTextCur.clearFocus();
                MaterialShortEditText editTextNext = (MaterialShortEditText) getChildAt(0);
                editTextNext.clearFocus();
                if (listener != null) {
                    listener.actionEnd();
                }
            }
        }
    }

    public void reset() {
        sb = new StringBuilder("");
        for (int i = 0; i < childcount; i++) {
            MaterialShortEditText editText = (MaterialShortEditText) verifyGroup.getChildAt(i);
            if (editText != null) {
                editText.setText("");
            }
            editText.clearFocus();
        }
        (verifyGroup.getChildAt(0)).requestFocus();
        setHint(context.getString(R.string.verify_group_hint));

    }

    private int getLastNoneEmptyIndex() {
        int count = verifyGroup.getChildCount();
        for (int i = count - 1; i > -1; i--) {
            MaterialShortEditText child = (MaterialShortEditText) verifyGroup.getChildAt(i);
            if (!TextUtils.isEmpty(child.getText())) {
                return i;
            }
        }
        return -1;
    }

    public String getTextToString() {
        String result = "";
        int count = verifyGroup.getChildCount();
        for (int i = count - 1; i > -1; i--) {
            MaterialShortEditText child = (MaterialShortEditText) verifyGroup.getChildAt(i);
            result += child.getText().toString();
        }
        return result;
    }

    /**
     * 自动填充
     *
     * @param codes
     */
    public void autoCompleteMsg(String codes) {
        reset();

        this.codes = codes;
        handler.removeMessages(MSG_NEXT);

        Message message = Message.obtain();
        message.what = MSG_NEXT;
        message.arg1 = 0;
        message.obj = codes.charAt(0);

        handler.sendMessageDelayed(message, 500);
    }

    private void setAutoCode(String code, int index) {
        if (index == childcount - 1) handler.removeMessages(MSG_NEXT);
        MaterialShortEditText child = (MaterialShortEditText) verifyGroup.getChildAt(index);
        if (child != null) {
            child.setText(code);
            child.requestFocus();
        }
    }


    private final int MSG_NEXT = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_NEXT) {
                int index = msg.arg1;
                char code = (char) msg.obj;
                setAutoCode(code + "", index);

                if (index < childcount - 1) {
                    Message message = Message.obtain();

                    message.what = MSG_NEXT;
                    index += 1;
                    message.arg1 = index;
                    message.obj = codes.charAt(index);
                    handler.sendMessageDelayed(message, 300);
                }
            }
        }
    };

    public void setHint(CharSequence hint) {
        hintText = hint == null ? null : hint.toString();

        postInvalidate();
    }

    public void setHintColor(int color) {
        this.hintColor = color;
        postInvalidate();
    }
}
