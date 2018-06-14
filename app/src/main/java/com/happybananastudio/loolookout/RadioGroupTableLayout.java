package com.happybananastudio.loolookout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by mgint on 6/12/2018.
 */

public class RadioGroupTableLayout
        extends TableLayout
        implements OnClickListener {
    private static final String TAG = "RadioGroupTableLayout";
    private RadioButton activeRadioButton;

    public RadioGroupTableLayout(Context context) {
        super(context);
    }

    public RadioGroupTableLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if (activeRadioButton != null) {
            activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        activeRadioButton = rb;
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow) child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow) child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i = 0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {
                v.setOnClickListener(this);
            }
        }
    }
    public int getCheckedRadioButtonId() {
        if (activeRadioButton != null) {
            return activeRadioButton.getId();
        }
        return -1;
    }

    public void setCheckedRadioButtonId(int id){
        if(activeRadioButton != null) {
            activeRadioButton.setChecked(false);
        }
        activeRadioButton = (RadioButton) findViewById(id);
        activeRadioButton.setChecked(true);
    }
}
