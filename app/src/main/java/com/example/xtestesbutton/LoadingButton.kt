package com.example.xtestesbutton

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                              var action: (() -> Unit)? = null)
    : AppCompatButton(context, attrs, R.layout.view_fast_button) {

    private var animatedDrawable: CircularAnimatedDrawable? = null

    private var colorIndicator = 0
    private var canvas: Canvas? = null

    private val typedArray by lazy { context.obtainStyledAttributes(attrs, R.styleable.FastLoadingButton, 0, 0) }

    private val textButton by lazy { typedArray.getString(R.styleable.FastLoadingButton_text)?.let {
            return@lazy it
        } ?: run {
            return@lazy ""
        }
    }

    private var isShadowEnabled = typedArray.getBoolean(R.styleable.FastLoadingButton_lb_isShadowEnable, true)

    private var isLoading = typedArray.getBoolean(R.styleable.FastLoadingButton_lb_isLoading, false)

    private var paddingProgress = typedArray.getDimensionPixelSize(R.styleable.FastLoadingButton_lb_loaderWidth, 7)

    private var strokeWidth = typedArray.getDimensionPixelSize(R.styleable.FastLoadingButton_lb_loaderMargin, 7)

    init {
        text = textButton
        setOnClickListener {
            action?.invoke()
        }
        isShadowEnabled = true
        text = textButton
    }

    /**
     * O método onDraw() oferece um Canvas sobre o qual você pode
    implementar o que quiser: gráficos 2D, outros componentes padrão ou personalizados,
    texto estilizado ou qualquer outra coisa que você possa imaginar.

    Canvas: a tela na qual o plano de fundo será desenhado
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        invalidate()
        this.canvas = canvas
        if (isLoading) {
            drawIndeterminateProgress(canvas)
            text = ""
        } else {
            if (textButton.length != 0) text = textButton
        }
    }

    /**
     * Instancia a classe do loading e define seu limite no botao
     */
    private fun drawIndeterminateProgress(canvas: Canvas) {
        if (animatedDrawable == null) {
            val offset = (width - height) / 2

            colorIndicator = resources.getColor(R.color.white)

            animatedDrawable = CircularAnimatedDrawable(colorIndicator, strokeWidth.toFloat())

            //definindo limite do loading
            val left = offset + paddingProgress
            val right = width - offset - paddingProgress
            val bottom = height - paddingProgress
            val top = paddingProgress

            animatedDrawable!!.setBounds(left, top, right, bottom)
            animatedDrawable!!.callback = this
            animatedDrawable!!.start()
        } else {
            animatedDrawable!!.draw(canvas)
        }
    }

    /**
     * define quando o loading será mostrado
     */
    private fun setLoading(loading: Boolean) {
        isLoading = loading
        if (isLoading) {
            drawIndeterminateProgress(canvas!!)
            text = ""
        } else {
            if (textButton.length != 0) text = textButton
        }
    }

    fun showLoading() {
        setLoading(true)
    }

    fun hideLoading() {
        setLoading(false)
    }
}

