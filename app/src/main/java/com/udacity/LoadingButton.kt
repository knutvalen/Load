package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import timber.log.Timber
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
    private val max = 100f
    private var valueAnimator = ValueAnimator.ofFloat(0f, max)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, newValue ->
        text = when (newValue) {
            ButtonState.Loading -> resources.getString(R.string.button_loading)
            else -> resources.getString(R.string.button_name)
        }

        if (newValue == ButtonState.Clicked) {
            valueAnimator.duration = 3000
            valueAnimator.start()
        }

        if (newValue == ButtonState.Completed) {
            isEnabled = true

            if (valueAnimator.isRunning) {
                valueAnimator.end()
            }

            invalidate()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    init {
        isClickable = true

        valueAnimator.addUpdateListener {
            invalidate()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                Timber.i("valueAnimator onAnimationStart")
                isEnabled = false
                buttonState = ButtonState.Loading
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (primaryColor != null) {
            paint.color = primaryColor
        }

        canvas?.drawRoundRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), 8f, 8f, paint)

        if (primaryDarkColor != null) {
            paint.color = primaryDarkColor
        }

        (valueAnimator.animatedValue as Float).let { animatedValue ->
            Timber.i("onDraw animatedValue: $animatedValue")

            if (buttonState == ButtonState.Loading) {
                val right = widthSize / max * animatedValue
                canvas?.drawRoundRect(0f, 0f, right, heightSize.toFloat(), 8f, 8f, paint)
            }
        }

        if (colorOnPrimary != null) {
            paint.color = colorOnPrimary
        }

        val textXPosition = widthSize / 2
        val textYPosition = heightSize / 2 + textFrame.height() / 2

        canvas?.drawText(text, textXPosition.toFloat(), textYPosition.toFloat(), paint)

        //TODO: use drawArc() to draw circle

        if (secondaryColor != null) {
            paint.color = secondaryColor
        }

        val circleXPosition = textXPosition + textFrame.width()
        val circleYPosition = heightSize / 2

        canvas?.drawCircle(circleXPosition.toFloat(), circleYPosition.toFloat(), textFrame.height().toFloat(), paint)
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

    fun setState(state: ButtonState) {
        Timber.i("setState: $state")
        buttonState = state
    }

}