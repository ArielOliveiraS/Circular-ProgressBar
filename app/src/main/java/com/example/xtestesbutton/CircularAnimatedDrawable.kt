package com.example.xtestesbutton

import android.animation.*
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class CircularAnimatedDrawable @JvmOverloads constructor(arcColor: Int, borderWidth: Float) :
    Drawable(), Animatable {

    private val ANGLE_ANIMATOR_DURATION = 800L
    private val SWEEP_ANIMATOR_DURATION = 500L
    private val MIN_SWEEP_ANGLE = 80

    private var modeAppearing: Boolean = false
    private var currentGlobalAngleOffset = 3f
    private var currentGlobalAngle = 3f
    private var currentSweepAngle = 3f

    //especifica as coordenadas da animacao (limites)
    private val fBounds: RectF by lazy {
        RectF().apply {
            left = bounds.left.toFloat() + borderWidth / 2F + .5F
            right = bounds.right.toFloat() - borderWidth / 2F - .5F
            top = bounds.top.toFloat() + borderWidth / 2F + .5F
            bottom = bounds.bottom.toFloat() - borderWidth / 2F - .5F
        }
    }

    /**
     * A classe Paint contém as informações de estilo e cor sobre como desenhar
     * geometrias, texto e bitmaps.
     */
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        color = arcColor
    }

    /**
     * Draw in its bounds (set via setBounds) respecting optional effects such
     * as alpha (set via setAlpha) and color filter (set via setColorFilter).
     */
    override fun draw(canvas: Canvas) {
        var startAngle = currentGlobalAngle - currentGlobalAngleOffset
        var sweepAngle = currentSweepAngle
        if (!modeAppearing) {
            startAngle += sweepAngle
            sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE  //algulo do espaco aberto no loading
            invalidateSelf()
        } else {
            sweepAngle += MIN_SWEEP_ANGLE.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, paint!!)
    }



    private val indeterminateAnimator = AnimatorSet().apply {
        playTogether(
            angleValueAnimator(LinearInterpolator()),
            sweepValueAnimator(FastOutSlowInInterpolator())
        )
    }

    private fun angleValueAnimator(timeInterpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F).apply {
            interpolator = timeInterpolator
            duration = ANGLE_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation -> currentGlobalAngle = animation.animatedValue as Float }
        }

    private fun sweepValueAnimator(timeInterpolator: TimeInterpolator): ValueAnimator =
        ValueAnimator.ofFloat(0F, 360F - 2 * MIN_SWEEP_ANGLE).apply {
            interpolator = timeInterpolator
            duration = SWEEP_ANIMATOR_DURATION
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation ->
                currentSweepAngle = animation.animatedValue as Float
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator) {
                    toggleSweep()
                }
            })
        }

    /**
     * Temos os métodos start, stop e isRunning, para que a visualização possa
     * iniciar e parar o carregamento da animação no momento apropriado
     *
     * Precisamos chamar invalidate em todas as atualizações de animação para que nossa view possa ser redesenhada.
     */
    override fun isRunning(): Boolean = indeterminateAnimator.isRunning

    override fun start() {
        if (isRunning) {
            return
        }
        indeterminateAnimator.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        indeterminateAnimator.end()
        invalidateSelf()
    }


    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    private fun toggleSweep() {
        modeAppearing = !modeAppearing
        if (modeAppearing) {
            currentGlobalAngleOffset = (currentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }
}