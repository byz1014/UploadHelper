package com.resources.uploadlib.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.resources.uploadlib.R

/**
 * create by byz
 * 可滑动进度条
 */
class VerticalSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    var defaultPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    var radius = 20f
    var padding = 10f
    var defaultColor = Color.parseColor("#ffCCCCCC")
    var activiteColor = Color.parseColor("#ff0011aa")

    init {
        context.obtainStyledAttributes(attrs, R.styleable.verticalSeekBar).apply {
            radius = this.getDimension(R.styleable.verticalSeekBar_seekBarRadius, 20f)
            padding = this.getDimension(R.styleable.verticalSeekBar_seekBarPadding, 10f)
            defaultColor = this.getColor(R.styleable.verticalSeekBar_defaultColor,Color.parseColor("#ffCCCCCC"))
            activiteColor = this.getColor(R.styleable.verticalSeekBar_ActiveColor,Color.parseColor("#ff0011aa"))
            mPaint.color = activiteColor
            defaultPaint.color = defaultColor
        }


    }


    var devalue = 0.0f
    var maxHeight = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension((radius * 2 + padding * 2 + 1).toInt(), maxHeight)
    }

    var barX = 0f
    var barY = 0f
    var lineStartY = 0f
    var lineEndY = 0f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            var value = maxHeight - 2 * (radius + padding)
            lineStartY = radius + padding
            lineEndY = maxHeight - radius - padding
            drawLine(radius + padding, lineStartY, radius + padding, lineEndY, defaultPaint)
            drawLine(
                radius + padding,
                maxHeight - radius - padding,
                radius + padding,
                maxHeight - radius - padding - value * devalue,
                mPaint
            )
            barX = radius + padding;
            barY = maxHeight - radius - padding - value * devalue
            drawCircle(barX, barY, radius, mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (this.action) {
                MotionEvent.ACTION_DOWN -> {
                    pointType = pointType(event.y)
                    if (pointType == 1) {
                        setValue(event.y)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (pointType == 0) {
                        setValue(event.y)
                    }
                }
            }
        }
        return true
    }


    var pointType = -1

    fun pointType(mY: Float): Int {
        if (mY >= (barY - radius) && mY <= (barY + radius)) {
            return 0
        }
        if (mY in lineStartY..lineEndY) {
            return 1
        }
        return -1
    }


    fun setValue(y: Float) {
        var value = maxHeight - 2 * (radius + padding)
        (1 - (y - padding + radius) / value).apply {
            devalue = if (this < 0) {
                0f
            } else if (this > 1) {
                1f
            } else {
                this
            }
        }

        mListener?.apply {
            onProgress(devalue)
        }
        invalidate()
    }



    var mListener:OnSeekBarValueListener?=null

    fun setOnSeekBarValueListener(mOnSeekBarValueListener:OnSeekBarValueListener){
        this.mListener = mOnSeekBarValueListener
    }

    interface OnSeekBarValueListener{
        fun onProgress(value:Float)
    }
}