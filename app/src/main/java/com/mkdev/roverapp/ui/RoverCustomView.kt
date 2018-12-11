package com.mkdev.roverapp.ui

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.mkdev.roverapp.R
import com.mkdev.roverapp.model.WeirsItem
import com.mkdev.roverapp.ui.dialogs.AlertDialog
import com.mkdev.roverapp.utils.dpToPx
import com.mkdev.roverapp.utils.vibrate
import timber.log.Timber
import kotlin.concurrent.thread
import kotlin.math.min

const val M_DIRECTION: Char = 'M'
const val L_DIRECTION: Char = 'L'
const val R_DIRECTION: Char = 'R'

class RoverCustomView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var arrowUp: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_up)
    private var arrowRight: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_right)
    private var arrowLeft: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_left)
    private var arrowDown: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_down)

    private var cellWidth = 0f
    private val cellPadding = dpToPx(5).toFloat()

    private lateinit var roverRect: RectF
    private var direction = Direction.UP
    private var isBlocked = false
    private var cancelMovement = false
    private var roverThread: Thread? = null
    private var blockedCells = Array(20) { BooleanArray(10) }
    private var roverPosition = Point(0, 0)
    private var moveMil = 100L

    private val blockedCellPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.secondaryColorGray)
    }

    private val cellPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.secondaryLightColorGreen)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (measuredWidth > 0 && measuredHeight > 0) {

            cellWidth = min((measuredWidth - (11 * cellPadding)) / 9, (measuredHeight - (21 * cellPadding)) / 20)
            setMeasuredDimension((cellWidth * 10).toInt(), (cellWidth * 20).toInt())
            calculateRoverRect()
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (y in 0..19) {
            for (x in 0..9) {
                canvas.drawRect(
                    x * cellWidth + cellPadding,
                    y * cellWidth + cellPadding,
                    x * cellWidth + cellWidth,
                    y * cellWidth + cellWidth,
                    if (blockedCells[19 - y][x]) blockedCellPaint else cellPaint
                )

                if (19 - y == roverPosition.y && x == roverPosition.x) {
                    canvas.drawBitmap(getRoverBitmap(), null, roverRect, null)
                }
            }
        }
    }

    private fun reset() {
        direction = Direction.UP
        blockedCells = Array(20) { BooleanArray(10) }
        roverPosition = Point(0, 0)
        calculateRoverRect()
        invalidate()
    }

    private fun calculateRoverRect() {
        roverRect = RectF(
            roverPosition.x * cellWidth + cellPadding,
            (19 - roverPosition.y) * cellWidth + cellPadding,
            roverPosition.x * cellWidth + cellWidth,
            (19 - roverPosition.y) * cellWidth + cellWidth
        )
    }

    fun runCommand(startPoint: Point, blocks: List<WeirsItem>, commands: String) {
        blocks.forEach {
            blockedCells[it.Y][it.X] = true
        }
        roverPosition = Point(startPoint)
        calculateRoverRect()
        invalidate()

        roverThread = thread {
            try {
                commands.forEachIndexed { index, item ->
                    if (cancelMovement) {
                        cancelMovement = false
                        return@thread
                    }

                    when (item) {
                        M_DIRECTION -> handler.post { moveRover() }
                        R_DIRECTION -> handler.post { turnRight() }
                        L_DIRECTION -> handler.post { turnLeft() }
                    }
                    if (!Thread.currentThread().isInterrupted)
                        Thread.sleep(500)
                }
            } catch (consumed: InterruptedException) {
                Timber.d(consumed)
            }
        }
    }

    fun stopProcess() {
        roverThread?.interrupt()
        reset()
    }

    private fun moveRover() {
        if (!checkPath()) {
            cancelMovement = true
            if (isBlocked) {
                AlertDialog(context, content = context.getString(R.string.weirs_alert)).show()
            } else if (!isBlocked) {
                AlertDialog(context, content = context.getString(R.string.wall_alert)).show()
            }
            vibrate(context, 1000L)
            return
        }
        when (direction) {
            Direction.UP -> roverPosition.y += 1
            Direction.RIGHT -> roverPosition.x += 1
            Direction.LEFT -> roverPosition.x -= 1
            Direction.DOWN -> roverPosition.y -= 1
        }

        vibrate(context, moveMil)
        calculateRoverRect()
        invalidate()
    }

    private fun turnRight() {
        direction = when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
        vibrate(context, moveMil)
        invalidate()
    }

    private fun turnLeft() {
        direction = when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.RIGHT -> Direction.UP
        }
        vibrate(context, moveMil)
        invalidate()
    }
    private fun checkPath(): Boolean {
        val nextPos = Point(roverPosition)
        when (direction) {
            Direction.UP -> nextPos.y += 1
            Direction.RIGHT -> nextPos.x += 1
            Direction.LEFT -> nextPos.x -= 1
            Direction.DOWN -> nextPos.y -= 1
        }
        isBlocked = false
        return when {
            nextPos.y !in 0..19 -> false
            nextPos.x !in 0..9 -> false
            blockedCells[nextPos.y][nextPos.x] -> {
                isBlocked = true
                false
            }
            else -> true
        }
    }

    private fun getRoverBitmap() = when (direction) {
        Direction.UP -> arrowUp
        Direction.RIGHT -> arrowRight
        Direction.LEFT -> arrowLeft
        Direction.DOWN -> arrowDown
    }

    enum class Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}