package com.pyotrpopov443.fourier

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.lang.Math.random
import kotlin.math.*

class DrawFourier(context: Context, attributes: AttributeSet) : View(context, attributes) {

    //colors
    private var canvasColor: Int
    private var epicycleColor: Int
    private var lineColor: Int
    private var shapeColor: Int
    private var resultColor: Int

    //paints
    private val backgroundPaint: Paint
    private val epicyclePaint: Paint
    private val shapePaint: Paint
    private val resultPaint: Paint
    //mathematics
    private val twoPi = PI.toFloat() * 2
    private var drawing = false
    private var epicyclesNumber = 50
    private var time = 0f
    private var touchX = 0f
    private var touchY = 0f
    private var shape = mutableListOf<Vector>()
    private var trail = mutableListOf<Vector>()
    private var epicyclesColors = mutableListOf<Int>()
    private var epicycles = mutableListOf<Epicycle>()

    fun getEpicyclesNumber(): Int {
        return epicyclesNumber
    }

    fun getMaxEpicyclesNumber(): Int {
        return shape.size
    }

    fun changeEpicyclesNumber(epicyclesNumber: Int) {
        this.epicyclesNumber = epicyclesNumber
        approximate(shape)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas){
        canvas.drawPaint(backgroundPaint)

        val path = Path()

        if (drawing) {
            val point = Vector(touchX, touchY)
            epicyclesColors.add(randomColor())
            shape.add(point)

            path.moveTo(shape[0].x + width/2, shape[0].y + height/2)
            for (i in 1 until shape.size) path.lineTo(shape[i].x + width/2, shape[i].y + height/2)
        } else {
            if (trail.size < shape.size) drawResult(epicycles, canvas)
            val point = currentPoint(epicycles, canvas)
            trail.add(point)

            path.moveTo(trail[0].x, trail[0].y)
            for (i in 1 until trail.size) path.lineTo(trail[i].x, trail[i].y)

            time %= twoPi
            time += twoPi / shape.size
            if (trail.size > shape.size) trail.removeAt(0)
        }
        canvas.drawPath(path, shapePaint)
        invalidate()
    }

    fun touch(event: MotionEvent) {
        if (event.x < 10 * context.resources.displayMetrics.density) return
        touchX = event.x - width/2
        touchY = event.y - height/2
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!drawing) {
                    drawing = true
                    shape.clear()
                    epicyclesColors.clear()
                    time = 0f
                }
            }
            MotionEvent.ACTION_UP -> {
                if (epicyclesNumber > shape.size) epicyclesNumber = shape.size
                approximate(shape)
            }
        }
    }

    private fun discreteFourierTransform(complexShape: MutableList<Complex>, epicyclesNumber: Int): MutableList<Epicycle> {
        val epicycles = mutableListOf<Epicycle>()
        val shapeLength = complexShape.size
        val r = epicyclesNumber % 2
        val m = (epicyclesNumber - r) / 2
        for (k in -m until m + r) {
            val sum = Complex(0f,0f)
            for (n in 0 until shapeLength) {
                val theta = twoPi * k * n / shapeLength
                val complex = Complex(cos(theta), -sin(theta))
                sum.add(complexShape[n].multiply(complex))
            }
            sum.real /= shapeLength
            sum.imaginary /= shapeLength
            val amplitude = sqrt(sum.real * sum.real + sum.imaginary * sum.imaginary)
            val phase = atan2(sum.imaginary, sum.real)
            epicycles.add(Epicycle(k, amplitude, phase))
        }
        epicycles.sortByDescending {it.amplitude}
        return epicycles
    }

    private fun approximate(shape: MutableList<Vector>) {
        val complexShape = mutableListOf<Complex>()
        trail.clear()
        for (i in 0 until shape.size) complexShape.add(Complex(shape[i].x, shape[i].y))
        epicycles = discreteFourierTransform(complexShape, epicyclesNumber)
        drawing = false
    }

    private fun currentPoint(epicycles: MutableList<Epicycle>, canvas: Canvas): Vector {
        var x = width / 2f
        var y = height / 2f
        for (i in 0 until epicycles.size) {
            val prevX = x
            val prevY = y
            val frequency = epicycles[i].frequency
            val amplitude = epicycles[i].amplitude
            val phase = epicycles[i].phase
            x += amplitude * cos(frequency * time + phase)
            y += amplitude * sin(frequency * time + phase)
            epicyclePaint.color = epicyclesColors[i]
            canvas.drawCircle(prevX, prevY, amplitude, epicyclePaint)
            epicyclePaint.color = lineColor
            canvas.drawLine(prevX, prevY, x, y, epicyclePaint)
        }
        return Vector(x, y)
    }

    private fun drawResult(epicycles: MutableList<Epicycle>, canvas: Canvas) {
        if (epicycles.size == 0) return
        var t = 0f
        val path = Path()
        while (t < twoPi) {
            var x = width/2f
            var y = height/2f
            for (j in 0 until epicycles.size) {
                val frequency = epicycles[j].frequency
                val amplitude = epicycles[j].amplitude
                val phase = epicycles[j].phase
                x += amplitude * cos(frequency * t + phase)
                y += amplitude * sin(frequency * t + phase)
            }
            if (t == 0f) path.moveTo(x, y)
            path.lineTo(x, y)
            t += twoPi / shape.size
        }
        canvas.drawPath(path, resultPaint)
    }

    fun applyDemoPath(demo: ArrayList<IntArray>, epicyclesNumber: Int) {
        time = 0f
        shape.clear()
        epicyclesColors.clear()
        for (i in 0 until demo.size) {
            val x = demo[i][0].toFloat()
            val y = demo[i][1].toFloat()
            shape.add(Vector(x, -y))
            epicyclesColors.add(randomColor())
        }
        changeEpicyclesNumber(epicyclesNumber)
    }

    private fun randomColor(): Int {
        val from = 100
        val to = 50
        val difference = from - to
        val r = from + round(random()*difference).toInt()
        val g = from + round(random()*difference).toInt()
        val b = from + round(random()*difference).toInt()
        return Color.rgb(r, g, b)
    }

    private val demo: ArrayList<IntArray> = arrayListOf(
        intArrayOf(-50, -70),
        intArrayOf(-25, 8),
        intArrayOf(0, 85),
        intArrayOf(25, 8),
        intArrayOf(50, -70),
        intArrayOf(-15, -23),
        intArrayOf(-80, 25),
        intArrayOf(0, 25),
        intArrayOf(80, 25),
        intArrayOf(15, -8)
    )

    init {
        val typedValue = TypedValue()
        val theme = context.theme

        theme.resolveAttribute(R.attr.canvasColor, typedValue, true)
        canvasColor = typedValue.data

        theme.resolveAttribute(R.attr.epicycleColor, typedValue, true)
        epicycleColor = typedValue.data

        theme.resolveAttribute(R.attr.lineColor, typedValue, true)
        lineColor = typedValue.data

        theme.resolveAttribute(R.attr.shapeColor, typedValue, true)
        shapeColor = typedValue.data

        theme.resolveAttribute(R.attr.resultColor, typedValue, true)
        resultColor = typedValue.data

        backgroundPaint = Paint().apply { style = Paint.Style.FILL; color = canvasColor }

        epicyclePaint  = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 3f; color = epicycleColor }

        shapePaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 8f; color = shapeColor }

        resultPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 8f; color = resultColor }

        applyDemoPath(demo, 10)
    }

}