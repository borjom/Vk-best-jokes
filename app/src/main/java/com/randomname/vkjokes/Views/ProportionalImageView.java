package com.randomname.vkjokes.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProportionalImageView extends ImageView {
    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionalImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d == null) {
            super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int imageHeight = d.getIntrinsicHeight();
        int imageWidth = d.getIntrinsicWidth();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float imageRatio = 0.0F;
        if (imageHeight > 0) {
            imageRatio = imageWidth / imageHeight;
        }
        float sizeRatio = 0.0F;
        if (heightSize > 0) {
            sizeRatio = widthSize / heightSize;
        }

        int width;
        int height;
        if (imageRatio >= sizeRatio) {
            // set width to maximum allowed
            width = widthSize;
            // scale height
            height = width * imageHeight / imageWidth;
        } else {
            // set height to maximum allowed
            height = heightSize;
            // scale width
            width = height * imageWidth / imageHeight;
        }

        setMeasuredDimension(width, height);
    }
}
