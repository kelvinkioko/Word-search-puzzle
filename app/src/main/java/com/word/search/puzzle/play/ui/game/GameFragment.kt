package com.word.search.puzzle.play.ui.game

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.constants.PreferenceHandler
import com.word.search.puzzle.play.database.WordSearchReader
import com.word.search.puzzle.play.databinding.FragmentGameBinding
import com.word.search.puzzle.play.util.viewBinding
import java.util.Collections
import java.util.Locale
import java.util.Objects
import java.util.Random
import kotlin.math.min
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment(R.layout.fragment_game), WSLayout.OnWordHighlightedListener {

    private val binding by viewBinding(FragmentGameBinding::bind)

    private var rows = 10
    private var cols = 10
    private lateinit var wordList: Array<String?>

    private val wordAdapter = WordAdapter()
    private val solution = mutableListOf<Word>()
    private var foundWords = mutableListOf<Word>()

    private lateinit var board: Array<CharArray>
    private lateinit var lock: Array<BooleanArray>

    private val directions = Direction.values()
    private lateinit var randIndices: IntArray

    private lateinit var preferenceHandler: PreferenceHandler

    // Is the stopwatch running?
    private var handler: Handler? = null
    private var seconds: Int = 0

    private var running = true
    private var appPause = false

    private var wasRunning = false

    private lateinit var dialog: Dialog

    private val viewModel: GameViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler()

        preferenceHandler = PreferenceHandler(
            requireContext().getSharedPreferences(
                PreferenceHandler.PREFS_FILE,
                Context.MODE_PRIVATE
            )
        )

        binding.apply {
            cols = gameBoard.numColumns
            rows = gameBoard.numRows
        }
        board = Array(rows) { CharArray(cols) }
        lock = Array(rows) { BooleanArray(cols) }

        if (savedInstanceState == null) {
            val r = Runnable {
                selectWords()
                requireActivity().runOnUiThread {
                    clearBoard()
                    randomizeWords()
                    prepareBoard()
                    // if (word_list_label_group != null) word_list_label_group.setVisibility(View.VISIBLE)
                    val anim = AlphaAnimation(0.0f, 1.0f)
                    anim.duration = 500
                    binding.apply {
                        gameBoard.isVisible = true
                        gameBoard.startAnimation(anim)
                        gameBoard.setOnWordHighlightedListener(this@GameFragment)
                    }
                }
            }
            Thread(r).start()
        } else {
            wordList = savedInstanceState.getStringArray("words")!!
            clearBoard()
            var words: List<Word?>? = savedInstanceState.getParcelableArrayList("solution")
            for (word in words!!) {
                embedWord(word!!)
            }
            words = savedInstanceState.getParcelableArrayList("found")
            foundWords.addAll(words!!)
            binding.apply {
                wordsList.layoutAnimation = null
                gameBoard.setOnWordHighlightedListener(this@GameFragment)
                gameBoard.visibility = View.VISIBLE
            }
            prepareBoard()
            // if (word_list_label_group != null) word_list_label_group.setVisibility(View.VISIBLE)
        }

        binding.apply {
            pauseResumeAction.setOnClickListener {
                appPause = if (appPause) {
                    pauseResumeAction.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_pause_selector, null)!!)
                    handler?.postDelayed(runnable, 0)
                    false
                } else {
                    pauseResumeAction.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_resume_selector, null)!!)
                    handler?.removeCallbacks(runnable)
                    showPauseDialog()
                    true
                }
            }
        }
    }

    private fun prepareBoard() {
        binding.apply {
            gameBoard.populateBoard(board)
            wordsList.adapter = wordAdapter
        }
        val sortedWords: List<Word> = ArrayList(solution)
        Collections.sort(sortedWords)
        wordAdapter.setWordsFound(sortedWords)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("solution", ArrayList(solution))
        outState.putParcelableArrayList("found", ArrayList(foundWords))
        outState.putStringArray("words", wordList)
    }

    override fun wordHighlighted(positions: List<Int>?) {
        val firstPos = positions!![0]
        val lastPos = positions[positions.size - 1]
        val forwardWord = StringBuilder()
        val reverseWord = StringBuilder()
        for (position in positions) {
            val row = position / cols
            val col = position % cols
            val c = board[row][col]
            forwardWord.append(c)
            reverseWord.insert(0, c)
        }

        for (word in solution) {
            val wordStart = word.y * cols + word.x
            if (wordStart != firstPos && wordStart != lastPos) {
                continue
            }
            var found = if (word.text.equals(forwardWord.toString(), ignoreCase = true)) word else null
            if (found == null) {
                found = if (word.text.equals(reverseWord.toString(), ignoreCase = true)) word else null
            }
            if (found != null) {
                val ok = foundWords.add(found)
                binding.gameBoard.goal(found)
                wordAdapter.setWordFound(found)
                if (ok) {
                    val mPlayer: MediaPlayer = MediaPlayer.create(
                        requireContext(),
                        R.raw.word_found
                    )
                    mPlayer.start()
                }
                break
            }
        }

        if (foundWords.size == solution.size) {
            puzzleFinished()
        }
    }

    private fun startNewGame() {
        seconds = 0
        clearBoard()
        randomizeWords()
        prepareBoard()
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.apply {
            gameBoard.startAnimation(anim)
            wordsList.startAnimation(anim)
            gameBoard.isEnabled = true
            wordsList.isEnabled = true
        }
        // if (word_list_label_group != null) word_list_label_group.startAnimation(anim)
    }

    private fun gameFinishAnimation() {
        var fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.fillAfter = true
        fadeOut.duration = 500
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // congratView.setVisibility(View.VISIBLE)
                val mPlayer: MediaPlayer = MediaPlayer.create(
                    requireContext(),
                    R.raw.board_finished
                )
                mPlayer.start()
            }
        })
        binding.apply {
            gameBoard.startAnimation(fadeOut)
            wordsList.startAnimation(fadeOut)
        }
        // if (word_list_label_group != null) word_list_label_group.startAnimation(fadeOut)
        fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.fillAfter = true
        fadeOut.duration = 500
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // grd_word_list.startLayoutAnimation()
            }
        })
    }

    private fun puzzleFinished() {
        binding.apply {
            gameBoard.isEnabled = false
            wordsList.isEnabled = false
        }
        showLevelCompleteDialog()
        //gameFinishAnimation()
    }

    private fun selectWords() {
        val count: String = preferenceHandler.getRandomWordCount()!!
        val wsd = WordSearchReader(requireContext())
        wsd.open()
        wordList = wsd.getRandomWords(
            count = count.toInt(),
            selectedLangTable = preferenceHandler.getLanguage()!!
        )
        wsd.close()
    }

    private fun randomizeWords() {
        randIndices = IntArray(rows * cols)
        val rand = Random(System.currentTimeMillis())
        for (i in randIndices.indices) {
            randIndices[i] = i
        }
        for (i in randIndices.size - 1 downTo 1) {
            val randIndex = rand.nextInt(i)
            val realIndex: Int = randIndices[i]
            randIndices[i] = randIndices[randIndex]
            randIndices[randIndex] = realIndex
        }
        for (i in wordList.size - 1 downTo 1) {
            val randIndex = rand.nextInt(i)
            val word = wordList[i]
            wordList[i] = wordList[randIndex]
            wordList[randIndex] = word
        }
        for (word in wordList) {
            addWord(word!!)
        }
    }

    private fun addWord(word: String) {
        if (word.length > cols && word.length > rows) {
            return
        }
        val rand = Random()
        for (i in directions.size - 1 downTo 1) {
            val randIndex = rand.nextInt(i)
            val direction: Direction = directions[i]
            directions[i] = directions[randIndex]
            directions[randIndex] = direction
        }
        var bestDirection: Direction? = null
        var bestRow = -1
        var bestCol = -1
        var bestScore = -1
        for (index in randIndices) {
            val row = index / cols
            val col = index % cols
            for (direction in directions) {
                val score = isEmbeddable(word, direction, row, col)
                if (score > bestScore) {
                    bestRow = row
                    bestCol = col
                    bestDirection = direction
                    bestScore = score
                }
            }
        }
        if (bestScore >= 0) {
            val result = Word(word, bestRow, bestCol, bestDirection, requireContext())
            embedWord(result)
        }
    }

    private fun embedWord(word: Word) {
        var curRow = word.y
        var curCol = word.x
        val wordStr = word.text
        val direction = word.direction
        for (element in wordStr) {
            board[curRow][curCol] = element
            lock[curRow][curCol] = true
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
        }
        solution.add(word)
    }

    private fun isEmbeddable(word: String, direction: Direction, row: Int, col: Int): Int {
        if (getEmptySpace(direction, row, col) < word.length) {
            return -1
        }
        var score = 0
        var curRow = row
        var curCol = col
        for (element in word) {
            if (lock[curRow][curCol] && board[curRow][curCol] != element) {
                return -1
            } else if (lock[curRow][curCol]) {
                score++
            }
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
        }
        return score
    }

    private fun getEmptySpace(direction: Direction, row: Int, col: Int): Int {
        return when (direction) {
            Direction.SOUTH -> rows - row
            Direction.SOUTH_WEST -> min(rows - row, col)
            Direction.SOUTH_EAST -> min(rows - row, cols - col)
            Direction.WEST -> col
            Direction.EAST -> cols - col
            Direction.NORTH -> row
            Direction.NORTH_WEST -> min(row, col)
            Direction.NORTH_EAST -> min(row, cols - col)
        }
    }

    private fun clearBoard() {
        foundWords.clear()
        solution.clear()
        binding.gameBoard.clear()
        for (i in lock.indices) {
            for (j in lock[i].indices) {
                lock[i][j] = false
            }
        }
        val c: Char = if (wordList.isNotEmpty()) { wordList[0].toString()[0] } else { 'A' }

        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = c
            }
        }
    }

    private fun handleTimer() {
        if (!wasRunning && !appPause) {
            handler?.postDelayed(runnable, 0)
            wasRunning = true
        } else if (!appPause) {
            wasRunning = if (wasRunning) {
                // TODO: Play pause button
                handler?.removeCallbacks(runnable)
                false
            } else {
                // TODO: Show pause button
                handler?.postDelayed(runnable, 0)
                true
            }
        }
    }

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            val minutes: Int = seconds % 3600 / 60
            val secs: Int = seconds % 60

            // Format the seconds into hours, minutes,
            // and seconds.
            val time: String = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)

            // Set the text view text.
            binding.timer.text = time
            println("timer $time")

            // If running is true, increment the
            // seconds variable.
            if (running) {
                seconds++
            }

            // Post the code again
            // with a delay of 1 second.
            handler!!.postDelayed(this, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        handleTimer()
    }

    override fun onPause() {
        super.onPause()
        handleTimer()
    }

    private fun showPauseDialog() {
        dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_paused)

        val window: Window = Objects.requireNonNull<Window>(dialog.window)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        val resume: Button = dialog.findViewById(R.id.resume)
        val restart: Button = dialog.findViewById(R.id.restart)
        val exit: Button = dialog.findViewById(R.id.exit)
        val rating: Button = dialog.findViewById(R.id.rating)
        val share: Button = dialog.findViewById(R.id.share)

        resume.setOnClickListener {
            binding.apply {
                appPause = if (appPause) {
                    pauseResumeAction.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_pause_selector, null)!!)
                    handler?.postDelayed(runnable, 0)
                    false
                } else {
                    pauseResumeAction.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_resume_selector, null)!!)
                    handler?.removeCallbacks(runnable)
                    showPauseDialog()
                    true
                }
            }
            dialog.dismiss()
        }
        restart.setOnClickListener {
            startNewGame()
            dialog.dismiss()
        }
        exit.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
        rating.setOnClickListener {
            viewModel.rateApp(requireActivity())
            dialog.dismiss()
        }
        share.setOnClickListener {
            viewModel.shareApp(requireActivity())
            dialog.dismiss()
        }
    }

    private fun showLevelCompleteDialog() {
        dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_completed)

        val window: Window = Objects.requireNonNull<Window>(dialog.window)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        val restart: Button = dialog.findViewById(R.id.restart)
        val exit: Button = dialog.findViewById(R.id.exit)
        val rating: Button = dialog.findViewById(R.id.rating)
        val share: Button = dialog.findViewById(R.id.share)

        restart.setOnClickListener {
            startNewGame()
            dialog.dismiss()
        }
        exit.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
        rating.setOnClickListener {
            viewModel.rateApp(requireActivity())
            dialog.dismiss()
        }
        share.setOnClickListener {
            viewModel.shareApp(requireActivity())
            dialog.dismiss()
        }
    }
}
