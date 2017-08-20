package com.iszumi.movielover.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Thanks to my sensei: @hendrawd
 */

public class AutoFitImageView extends android.support.v7.widget.AppCompatImageView {

    public AutoFitImageView(Context context) {
        super(context);
    }

    public AutoFitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(w, h);
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}