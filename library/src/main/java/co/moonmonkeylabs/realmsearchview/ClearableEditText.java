package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import static android.view.View.*;

/**
 * A {@link EditText} field with a clear drawable.
 */
public class ClearableEditText extends EditText
        implements OnTouchListener, OnFocusChangeListener, TextWatcher {

    private Drawable clearDrawable;

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        clearDrawable = getCompoundDrawables()[2];
        if (clearDrawable == null) {
            clearDrawable = getResources().getDrawable(R.drawable.ic_cancel_black_18dp);
        }
        setClearDrawable(clearDrawable);
        setClearIconVisible(false);
        setOnTouchListener(this);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            boolean tappedX = event.getX() >
                    (getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(isNotEmpty(getText().toString()));
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFocused()) {
            setClearIconVisible(isNotEmpty(s.toString()));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    private void setClearIconVisible(boolean visible) {
        boolean wasVisible = (getCompoundDrawables()[2] != null);
        if (visible != wasVisible) {
            Drawable x = visible ? clearDrawable : null;
            setCompoundDrawables(
                    getCompoundDrawables()[0],
                    getCompoundDrawables()[1],
                    x,
                    getCompoundDrawables()[3]);
        }
    }

    public void setClearDrawable(Drawable clearDrawable) {
        this.clearDrawable = clearDrawable;
        this.clearDrawable.setBounds(
                0,
                0,
                clearDrawable.getIntrinsicWidth(),
                clearDrawable.getIntrinsicHeight());
    }
}