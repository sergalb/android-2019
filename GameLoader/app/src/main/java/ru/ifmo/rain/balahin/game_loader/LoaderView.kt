package ru.ifmo.rain.balahin.game_loader

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.ceil
import kotlin.math.min

@SuppressLint("ViewConstructor")
class LoaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val thick: Int
    private val crossLength: Int
    private val radius: Float
    private val destinationToDot: Int
    private val destinationBetweenDot: Int
    private val animationLatency: Int
    private val animationDuration: Int
    private val maxScale: Float
    private val color: Int

    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val verticalCrossRect: RectF
    private val horizontalCrossrect: RectF
    private val leftDot: RectF
    private val rightDot: RectF
    private val topDot: RectF
    private val downDot: RectF

    private var crossAnimator = ObjectAnimator.ofFloat(0f, 180f)
    private var topDotAnimatorFirst: Animator
    private var topDotAnimatorSecond: Animator
    private var rightDotAnimatorFirst: Animator
    private var rightDotAnimatorSecond: Animator
    private var downDotAnimatorFirst: Animator
    private var downDotAnimatorSecond: Animator
    private var leftDotAnimatorFirst: Animator
    private var leftDotAnimatorSecond: Animator

    private var crossRotation = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var topDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var rightDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var downDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var leftDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var animator: Animator? = null

    init {
        val a: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LoaderView, defStyleAttr, 0)
        try {
            thick =
                a.getDimensionPixelSize(R.styleable.LoaderView_cross_thick, dp(10f).toInt())
            crossLength =
                a.getDimensionPixelSize(R.styleable.LoaderView_cross_length, dp(22f).toInt())
            radius =
                a.getDimensionPixelSize(R.styleable.LoaderView_radius, dp(3f).toInt()).toFloat()
            destinationToDot =
                a.getDimensionPixelSize(R.styleable.LoaderView_destination_to_dot, dp(16f).toInt())
            destinationBetweenDot =
                a.getDimensionPixelSize(
                    R.styleable.LoaderView_destination_between_dot,
                    dp(6f).toInt()
                )
            animationLatency = a.getInt(R.styleable.LoaderView_animation_latency, 1000)
            animationDuration = a.getInt(R.styleable.LoaderView_animation_duration, 300)
            maxScale = a.getFloat(R.styleable.LoaderView_max_scale, 1.3f)
            color = a.getColor(R.styleable.LoaderView_color, 0)


            val crossFirstPart = (crossLength - thick).toFloat() / 2
            val crossSecondPart = (crossLength + thick).toFloat() / 2
            crossPaint.color = color
            verticalCrossRect = RectF(
                crossFirstPart,
                0f,
                crossSecondPart,
                crossLength.toFloat()
            )
            horizontalCrossrect = RectF(
                0f,
                crossFirstPart,
                crossLength.toFloat(),
                crossSecondPart
            )

            val centerOfFourDot =
                crossLength + destinationToDot + thick + destinationBetweenDot.toFloat() / 2
            val fromCenterToDot = destinationBetweenDot.toFloat() / 2
            leftDot = RectF(
                (centerOfFourDot - fromCenterToDot - thick),
                crossFirstPart,
                (centerOfFourDot - fromCenterToDot),
                crossSecondPart
            )
            topDot = RectF(
                centerOfFourDot - thick / 2,
                0f,
                centerOfFourDot + thick / 2,
                thick.toFloat()
            )
            rightDot = RectF(
                centerOfFourDot + fromCenterToDot,
                crossFirstPart,
                centerOfFourDot + fromCenterToDot + thick,
                crossSecondPart
            )
            downDot = RectF(
                centerOfFourDot - thick / 2,
                (thick + destinationBetweenDot).toFloat(),
                centerOfFourDot + thick / 2,
                (thick + destinationBetweenDot + thick).toFloat()
            )

            crossAnimator.apply {
                duration = animationDuration.toLong()
                addUpdateListener { crossRotation = it.animatedValue as Float }
            }
            topDotAnimatorFirst = ObjectAnimator.ofFloat(1f, maxScale).apply {
                duration = animationDuration.toLong()
                addUpdateListener { topDotScale = animatedValue as Float }
            }
            topDotAnimatorSecond = ObjectAnimator.ofFloat(maxScale, 1f).apply {
                duration = animationDuration.toLong()
                addUpdateListener { topDotScale = animatedValue as Float }
            }
            rightDotAnimatorFirst = ObjectAnimator.ofFloat(1f, maxScale).apply {
                duration = animationDuration.toLong()
                addUpdateListener { rightDotScale = animatedValue as Float }
            }
            rightDotAnimatorSecond = ObjectAnimator.ofFloat(maxScale, 1f).apply {
                duration = animationDuration.toLong()
                addUpdateListener { rightDotScale = animatedValue as Float }
            }
            downDotAnimatorFirst = ObjectAnimator.ofFloat(1f, maxScale).apply {
                duration = animationDuration.toLong()
                addUpdateListener { downDotScale = animatedValue as Float }
            }
            downDotAnimatorSecond = ObjectAnimator.ofFloat(maxScale, 1f).apply {
                duration = animationDuration.toLong()
                addUpdateListener { downDotScale = animatedValue as Float }
            }
            leftDotAnimatorFirst = ObjectAnimator.ofFloat(1f, maxScale).apply {
                duration = animationDuration.toLong()
                addUpdateListener { leftDotScale = animatedValue as Float }
            }
            leftDotAnimatorSecond = ObjectAnimator.ofFloat(maxScale, 1f).apply {
                duration = animationDuration.toLong()
                addUpdateListener { leftDotScale = animatedValue as Float }
            }
        } finally {
            a.recycle()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getSize(
                widthMeasureSpec,
                crossLength + destinationToDot + 2 * thick + destinationBetweenDot + ceil(thick * (maxScale - 1)).toInt()
            ),
            getSize(heightMeasureSpec, crossLength + ceil(thick * (maxScale - 1)).toInt() * 2)
        )
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()
        animator = AnimatorSet().apply {
            startDelay = animationLatency.toLong()
            interpolator = LinearInterpolator()
            playSequentially(
                crossAnimator,
                topDotAnimatorFirst,
                AnimatorSet().apply {playTogether(topDotAnimatorSecond, rightDotAnimatorFirst)},
                AnimatorSet().apply { playTogether(rightDotAnimatorSecond, downDotAnimatorFirst) },
                AnimatorSet().apply {  playTogether(downDotAnimatorSecond, leftDotAnimatorFirst) },
                leftDotAnimatorSecond
            )
            start()
            this.doOnEnd { start() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = crossLength.toFloat() / 2
        val cy = crossLength.toFloat() / 2

        canvas.translate(0f, thick * (maxScale - 1))
        val save = canvas.save()
        canvas.rotate(crossRotation, cx, cy)
        canvas.drawRoundRect(verticalCrossRect, radius, radius, crossPaint)
        canvas.drawRoundRect(horizontalCrossrect, radius, radius, crossPaint)
        canvas.restoreToCount(save)

        drawDot(leftDot, canvas, leftDotScale)
        drawDot(topDot, canvas, topDotScale)
        drawDot(rightDot, canvas, rightDotScale)
        drawDot(downDot, canvas, downDotScale)

    }


    private fun getSize(measureSpec: Int, desired: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.AT_MOST -> min(size, desired)
            MeasureSpec.EXACTLY -> size
            MeasureSpec.UNSPECIFIED -> desired
            else -> desired
        }
    }

    private fun drawDot(rect: RectF, canvas: Canvas, scale: Float) {
        val save = canvas.save()
        canvas.scale(scale, scale, rect.centerX(), rect.centerY())
        canvas.drawRoundRect(rect, radius, radius, crossPaint)
        canvas.restoreToCount(save)

    }

    private fun dp(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }
}