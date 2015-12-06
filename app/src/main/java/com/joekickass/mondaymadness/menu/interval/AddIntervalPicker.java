package com.joekickass.mondaymadness.menu.interval;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Override {@link NumberPicker} to allow min and max to be specified as XML attributes.
 */
public class AddIntervalPicker extends NumberPicker {

    public AddIntervalPicker(Context context) {
        super(context);
    }

    public AddIntervalPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public AddIntervalPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }
    private void processAttributeSet(AttributeSet attrs) {
        this.setMinValue(attrs.getAttributeIntValue(null, "min", 0));
        this.setMaxValue(attrs.getAttributeIntValue(null, "max", 0));
    }
}
