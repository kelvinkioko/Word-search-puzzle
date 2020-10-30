package com.word.search.puzzle.play.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.constants.PreferenceHandler
import com.word.search.puzzle.play.ui.game.Direction.Companion.getDirection
import java.util.Random
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.hypot

class WSLayout : LinearLayout {
    private var pencilOffset: Int? = null
    private var pencilOffsetRect: Rect? = null
    private var pencilEndRect: Rect? = null
    private var selectionCount: Int? = null
    private var direction: Direction? = null
    private var defaultPaint: Paint? = null
    private var correctPaint: Paint? = null
    private var previousWord: MutableList<View>? = null
    private var memory: Bitmap? = null
    private var colWidth = 0
    var numColumns: Int
        private set
    var numRows: Int
        private set
    private var pencilRadius = 0f
    private val density = resources.displayMetrics.density
    private val delta: Float = (50.0 * density + 0.5).toFloat()
    private var gameStatus: GameStatus? = null
    private var onWordHighlightedListener: OnWordHighlightedListener? = null
    private var preferenceHandler: PreferenceHandler

    constructor(context: Context) : super(context) {
        preferenceHandler = PreferenceHandler(
            context.getSharedPreferences(
                PreferenceHandler.PREFS_FILE,
                Context.MODE_PRIVATE
            )
        )
        numColumns = 10
        numRows = 10
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        preferenceHandler = PreferenceHandler(
            context.getSharedPreferences(
                PreferenceHandler.PREFS_FILE,
                Context.MODE_PRIVATE
            )
        )
        numColumns = preferenceHandler.getGridSize()!!.toInt()
        numRows = numColumns
        init()
    }

    private fun clearSelection() {
        if (direction != null && selectionCount != null) {
            onWordHighlightedListener!!.wordHighlighted(
                findCoordinatesUnderPencil(
                    direction!!,
                    pencilOffset!!,
                    selectionCount!!
                )
            )
        }
        pencilOffset = null
        selectionCount = null
        direction = null
        postInvalidate()
    }

    private fun findChildByPosition(index: Int): View {
        val row = floor(index.toDouble() / numColumns.toDouble()).toInt()
        val col = index % numColumns
        val rowView = getChildAt(row) as LinearLayout
        return rowView.getChildAt(col)
    }

    private fun findCoordinatesUnderPencil(direction: Direction, startPosition: Int, steps: Int): List<Int> {
        val positions: MutableList<Int> = ArrayList()
        var curRow = startPosition / numColumns
        var curCol = startPosition % numColumns
        for (i in 0..steps) {
            positions.add(curRow * numColumns + curCol)
            if (direction.isUp) {
                curRow -= 1
            } else if (direction.isDown) {
                curRow += 1
            }
            if (direction.isLeft) {
                curCol -= 1
            } else if (direction.isRight) {
                curCol += 1
            }
            if (curRow < 0 || curCol < 0 || curRow >= numRows || curCol >= numColumns) {
                break
            }
        }
        return positions
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is GameStatus) {
            super.onRestoreInstanceState(state.superState)
            gameStatus = state
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val state = super.onSaveInstanceState()
        val saveState = GameStatus(state)
        saveState.foundWords = gameStatus!!.foundWords
        return saveState
    }

    fun clear() {
        if (previousWord != null) {
            previousWord!!.clear()
        }
        gameStatus = GameStatus()
        pencilOffset = null
        pencilOffsetRect = null
        pencilEndRect = null
        direction = null
        memory = null
        selectionCount = null
    }

    private fun isNewSelection(xPos: Float, yPos: Float) {
        if (pencilOffset == null) {
            val position = point2XAxis(xPos, yPos)
            if (position >= 0) {
                val item = findChildByPosition(position)
                pencilOffsetRect = getLetterBounds(item)
                pencilOffset = position
            }
            postInvalidate()
        } else {
            val xDelta = xPos - pencilOffsetRect!!.centerX()
            val yDelta = (yPos - pencilOffsetRect!!.centerY()) * -1
            val distance = hypot(xDelta.toDouble(), yDelta.toDouble())
            if (isInTouchMode && distance < delta) {
                return
            }
            val previousDirection = direction
            val previousSteps = selectionCount
            direction = getDirection(atan2(yDelta.toDouble(), xDelta.toDouble()).toFloat())
            val stepSize = if (direction!!.isAngle) hypot(colWidth.toDouble(), colWidth.toDouble()).toFloat() else colWidth.toFloat()
            selectionCount = Math.round(distance.toFloat() / stepSize)
            if (selectionCount == 0) {
                selectionCount = null
            }
            if (direction !== previousDirection || selectionCount !== previousSteps) {
                val selectedViews = selectedLetters ?: return
                if (previousWord != null && previousWord!!.isNotEmpty()) {
                    val oldViews: MutableList<View> = ArrayList(previousWord)
                    oldViews.removeAll(selectedViews)
                }
                previousWord = selectedViews
                if (selectedViews.isNotEmpty()) {
                    val endView = selectedViews[selectedViews.size - 1]
                    pencilEndRect = getLetterBounds(endView)
                }
                postInvalidate()
            }
        }
    }

    fun populateBoard(board: Array<CharArray>) {
        for (i in board.indices) {
            for (j in board[i].indices) {
                val v = findChildByPosition(i * numColumns + j)
                (v.findViewById<View>(R.id.letter) as TextView).text = "" + board[i][j]
            }
        }
    }

    fun setOnWordHighlightedListener(onWordSelectedListener: OnWordHighlightedListener?) {
        onWordHighlightedListener = onWordSelectedListener
    }

    fun goal(word: Word) {
        if (!gameStatus!!.foundWords.contains(word)) {
            gameStatus!!.foundWords.add(word)
            if (memory == null) {
                memory = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            }
            val foundCanvas = Canvas(memory!!)
            val constColor = false
            if (!constColor) {
                val random = Random()
                val r = random.nextInt(256)
                val g = random.nextInt(256)
                val b = random.nextInt(256)
                correctPaint!!.setARGB(0xA0, r, g, b)
            }
            word.color = correctPaint!!.color
            paintGenericSelection(word, foundCanvas, correctPaint)
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (memory == null) {
            memory = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val foundCanvas = Canvas(memory!!)
            for (word in gameStatus!!.foundWords) {
                paintGenericSelection(word, foundCanvas, correctPaint)
            }
        }
        canvas.drawBitmap(memory!!, 0f, 0f, correctPaint)
        if (direction != null && selectionCount != null && pencilOffset != null) {
            paintCurrentSelection(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isInEditMode) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(
                    heightMeasureSpec
                )
            )
        } else {
            if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) {
                super.onMeasure(heightMeasureSpec, heightMeasureSpec)
                setMeasuredDimension(
                    MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(
                        heightMeasureSpec
                    )
                )
            } else {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec)
                setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(
                        widthMeasureSpec
                    )
                )
            }
        }
        colWidth = (measuredWidth.toFloat() / numColumns.toFloat()).toInt()
        // Settings.getBooleanValue(context, context.resources.getString(R.string.pref_key_rounded_line), true)
        val roundedPencil: Boolean = true
        pencilRadius = if (roundedPencil) {
            measuredWidth / 28.0f
        } else {
            0f
        }
    }

    private fun paintCurrentSelection(canvas: Canvas) {
        val pad = colWidth / 3.2f
        val superRect = RectF(-pad, -pad, pad, pad)
        val xDelta = pencilEndRect!!.centerX() - pencilOffsetRect!!.centerX().toFloat()
        val yDelta = (pencilEndRect!!.centerY() - pencilOffsetRect!!.centerY()) * (-1).toFloat()
        val distance = hypot(xDelta.toDouble(), yDelta.toDouble())
        superRect.right += distance.toFloat()
        canvas.save()
        canvas.translate(
            pencilOffsetRect!!.centerX().toFloat(),
            pencilOffsetRect!!.centerY().toFloat()
        )
        canvas.rotate(direction!!.angleDegree)
        canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, defaultPaint!!)
        canvas.restore()
    }

    private fun paintGenericSelection(word: Word, canvas: Canvas, paint: Paint?) {
        val angleStep = hypot(colWidth.toDouble(), colWidth.toDouble()).toFloat()
        val pad = colWidth / 3.2f
        val firstDistance = if (word.direction.isAngle) { angleStep } else { colWidth }
        val distance: Float = (firstDistance.toInt() * (word.text.length - 1)).toFloat()
        val superRect = RectF(-pad, -pad, pad, pad)
        superRect.right += distance
        val v = findChildByPosition(word.y * numColumns + word.x)
        val viewRect = getLetterBounds(v)
        val savedColor = word.color
        if (savedColor != 0) {
            paint!!.setARGB(
                0xA0, Color.red(savedColor), Color.green(savedColor), Color.blue(
                    savedColor
                )
            )
        }
        canvas.save()
        canvas.translate(viewRect.centerX().toFloat(), viewRect.centerY().toFloat())
        canvas.rotate(word.direction.angleDegree)
        canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, paint!!)
        canvas.restore()
    }

    private val selectedLetters: MutableList<View>?
        private get() {
            if (pencilOffset == null || selectionCount == null || direction == null) {
                return null
            }
            val views: MutableList<View> = ArrayList()
            for (position in findCoordinatesUnderPencil(
                direction!!,
                pencilOffset!!,
                selectionCount!!
            )) {
                views.add(findChildByPosition(position))
            }
            return views
        }

    private fun getLetterBounds(v: View): Rect {
        val viewRect = Rect()
        v.getDrawingRect(viewRect)
        viewRect.offset(v.left, (v.parent as ViewGroup).top)
        return viewRect
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        setWillNotDraw(false)
        orientation = VERTICAL
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        lp.weight = 1.0f
        for (i in 0 until numColumns) {
            val row = LinearLayout(context)
            row.orientation = HORIZONTAL
            for (j in 0 until numColumns) {
                val view: View = LayoutInflater.from(context).inflate(
                    R.layout.word_search_grid_cell,
                    null
                )
                view.isFocusable = true
                row.addView(view, lp)
            }
            addView(row, lp)
        }
        setOnTouchListener { v: View?, e: MotionEvent ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> isNewSelection(e.x, e.y)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> clearSelection()
            }
            true
        }
        gameStatus = GameStatus()
        val color = 0x0099cc
        defaultPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        defaultPaint!!.setARGB(0xFF, Color.red(color), Color.green(color), Color.blue(color))
        correctPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        correctPaint!!.setARGB(0xA0, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun containsPoint(x: Float, y: Float, view: View): Boolean {
        val rect = Rect()
        view.getDrawingRect(rect)
        rect.offset(view.left, (view.parent as ViewGroup).top)
        return rect.contains(x.toInt(), y.toInt())
    }

    private fun point2XAxis(x: Float, y: Float): Int {
        for (i in 0 until numColumns * numColumns) {
            if (containsPoint(x, y, findChildByPosition(i))) {
                return i
            }
        }
        return -1
    }

    interface OnWordHighlightedListener {
        fun wordHighlighted(positions: List<Int>?)
    }
}
