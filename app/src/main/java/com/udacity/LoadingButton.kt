package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val primaryColor = ContextCompat.getColorStateList(context, R.color.colorPrimary)?.defaultColor
    private val primaryDarkColor = ContextCompat.getColorStateList(context, R.color.colorPrimaryDark)?.defaultColor
    private val secondaryColor = ContextCompat.getColorStateList(context, R.color.colorAccent)?.defaultColor
    private val colorOnPrimary = ContextCompat.getColorStateList(context, R.color.white)?.defaultColor
    private lateinit var textFrame: Rect
    private var text = resources.getString(R.string.button_name)
    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    init {
        isClickable = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (primaryColor != null) {
            paint.color = primaryColor
        }
        canvas?.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 8f, 8f, paint)

        if (colorOnPrimary != null) {
            paint.color = colorOnPrimary
        }

        val textXPosition = width / 2
        val textYPosition = height / 2 + textFrame.height() / 2

        canvas?.drawText(text, textXPosition.toFloat(), textYPosition.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minimumWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rect = Rect()
        paint.getTextBounds(text, 0, text.count(), rect)
        textFrame = rect
    }
}