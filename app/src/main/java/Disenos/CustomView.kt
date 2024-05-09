package Disenos
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    private lateinit var drawPath: Path
    private lateinit var drawPaint: Paint
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasPaint: Paint

    private var backgroundColor: Int = Color.WHITE
    private var paintColor: Int = Color.BLACK

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPath = Path()
        drawPaint = Paint().apply {
            color = paintColor
            isAntiAlias = true
            strokeWidth = 5f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
        drawCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var strokeWidth = 5f
    enum class ShapeType {
        LINE,
        RECTANGLE,
        CIRCLE
    }

    // Variable para almacenar el tipo de figura seleccionada
    private var currentShape: ShapeType? = null
    fun setShapeType(shapeType: ShapeType) {
        currentShape = shapeType
    }

    private fun drawShape(startX: Float, startY: Float, endX: Float, endY: Float) {
        when (currentShape) {
            ShapeType.LINE -> drawCanvas.drawLine(startX, startY, endX, endY, drawPaint)
            ShapeType.RECTANGLE -> {
                val rect = RectF(startX, startY, endX, endY)
                drawCanvas.drawRect(rect, drawPaint)
            }
            ShapeType.CIRCLE -> {
                val radius = Math.sqrt(Math.pow((endX - startX).toDouble(), 2.0) + Math.pow((endY - startY).toDouble(), 2.0)).toFloat()
                drawCanvas.drawCircle(startX, startY, radius, drawPaint)
            }
            else -> return
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (currentShape != null) {
                    startX = touchX
                    startY = touchY
                } else {
                    drawPath.reset()
                    drawPath.moveTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentShape == null) {
                    drawPath.lineTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (currentShape != null) {
                    endX = touchX
                    endY = touchY
                    drawShape(startX, startY, endX, endY)
                    currentShape = null
                } else {
                    drawCanvas.drawPath(drawPath, drawPaint)
                }
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setPaintColor(color: Int) {
        paintColor = color
        drawPaint.color = paintColor
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        drawCanvas.drawColor(backgroundColor)
        invalidate()
    }

    fun getBitmap(): Bitmap {
        return canvasBitmap
    }

    fun clearCanvas() {
        drawCanvas.drawColor(backgroundColor, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun incrementStrokeWidth(increment: Float) {
        strokeWidth += increment
        if (strokeWidth < 1) strokeWidth = 1f // Asegurarse de que el grosor mínimo sea 1
        drawPaint.strokeWidth = strokeWidth
    }
    fun decrementStrokeWidth(decrement: Float) {
        strokeWidth -= decrement
        if (strokeWidth < 1) strokeWidth = 1f // Asegurarse de que el grosor mínimo sea 1
        drawPaint.strokeWidth = strokeWidth
    }
}
