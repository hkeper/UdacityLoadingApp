package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_detail.view.*
import kotlin.properties.Delegates
import androidx.core.content.ContextCompat

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textColor = 0
    private var custBackgroundColor = 0
    private lateinit var buttonText: String
    private val r = Rect()
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val circleRadius = 35.0f
    private val rectRadius = 45.0f
    private val circleOffset = 20.0f

    private var rectEnd = pointPosition.x
    private var textEnd = pointPosition.x
    private var circleEnd = pointPosition.x
    private var value = pointPosition.x
    private var durationAnimation = 3000L

    private var valueAnimatorCircle = ValueAnimator()
    private var valueAnimatorBar = ValueAnimator()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = textColor
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                animatorSet.playTogether(circleAnimation(), progressBarAnimation())
                animatorSet.start()
            }
            else -> {
                buttonText = resources.getString(R.string.download)
                animatorSet.cancel()
                value = pointPosition.x
                circleEnd = value
                rectEnd = value
                invalidate()
            }
        }
    }

    private val animatorSet = AnimatorSet().apply {
        duration = durationAnimation
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                this@LoadingButton.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                this@LoadingButton.isEnabled = true
                buttonState = ButtonState.Completed
            }
        })
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_custTextColor, 0)
            custBackgroundColor = getColor(R.styleable.LoadingButton_custBackgroundColor, 0)
            buttonText = getString(R.styleable.LoadingButton_buttonText).toString()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = custBackgroundColor
        canvas.drawRoundRect(
            pointPosition.x,
            pointPosition.y,
            widthSize.toFloat(),
            heightSize.toFloat(),
            rectRadius,
            rectRadius,
            paint)
        canvas.apply {
            drawProgressBar()
            drawProgressCircle()
        }
        paint.color = textColor
        drawCenter(canvas, paint, buttonText)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawCenter(canvas: Canvas, paint: Paint, text: String) {
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth / 2f - r.width() / 2f - r.left
        val y = cHeight / 2f + r.height() / 2f - r.bottom
        textEnd = r.width() / 2f + circleOffset
        canvas.drawText(text, x, y, paint)
    }

    private fun Canvas.drawProgressCircle() {
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        drawArc(
            widthSize / 2f + textEnd,
            heightSize / 2f - circleRadius,
            widthSize / 2f + textEnd + circleRadius * 2,
            heightSize / 2f + circleRadius,
            0.0f,
            circleEnd,
            true,
            paint
        )
    }

    private fun Canvas.drawProgressBar() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        drawRoundRect(pointPosition.x, pointPosition.y, rectEnd, heightSize.toFloat(), rectRadius, rectRadius, paint)
    }

    private fun circleAnimation(): ValueAnimator {
        valueAnimatorCircle = ValueAnimator.ofFloat(pointPosition.x, 360f)
        valueAnimatorCircle.addUpdateListener {
            value = valueAnimatorCircle.animatedValue as Float
            circleEnd = value
            invalidate()
        }
        valueAnimatorCircle.duration = durationAnimation
        return valueAnimatorCircle
    }

    private fun progressBarAnimation(): ValueAnimator {
        valueAnimatorBar = ValueAnimator.ofFloat(pointPosition.x, widthSize.toFloat())
        valueAnimatorBar.addUpdateListener {
            value = it.animatedValue as Float
            rectEnd = value
            invalidate()
        }
        valueAnimatorBar.duration = durationAnimation
        return valueAnimatorBar
    }


}