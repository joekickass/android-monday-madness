package com.joekickass.mondaymadness.menu.interval

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker

/**
 * Override [NumberPicker] to allow min and max to be specified as XML attributes.
 */
class AddIntervalPicker : NumberPicker {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        processAttributeSet(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        processAttributeSet(attrs)
    }

    private fun processAttributeSet(attrs: AttributeSet) {
        this.minValue = attrs.getAttributeIntValue(null, "min", 0)
        this.maxValue = attrs.getAttributeIntValue(null, "max", 0)
    }
}
