package com.example.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()
{

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        updateQuestion()

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true, view)
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false, view)
        }
        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)

        }
        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }


    private fun updateQuestion()
    {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        // Check if the current question has been answered and disable buttons accordingly
        if (quizViewModel.isCurrentQuestionAnswered()) {
            binding.trueButton.isEnabled = false
            binding.falseButton.isEnabled = false
        } else {
            // Re-enable the buttons for unanswered questions
            binding.trueButton.isEnabled = true
            binding.falseButton.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean, view: View)
    {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = if (userAnswer == correctAnswer) {
            if (quizViewModel.isCheater)
                R.string.judgment_toast
            else
                R.string.correct_toast
        } else {
            if (quizViewModel.isCheater)
                R.string.judgment_toast
            else
                R.string.incorrect_toast
        }

        // Show the feedback message
        Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT).show()

        // Mark the current question as answered
        quizViewModel.markQuestionAsAnswered(userAnswer)

        // Disable the buttons after answering
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false

        // Check if all questions have been answered
        if (quizViewModel.allQuestionsAnswered()) {
            showScore(view)
        }
    }

    private fun showScore(view: View)
    {
        val correct = quizViewModel.correctAnswers
        val total = quizViewModel.correctAnswers + quizViewModel.wrongAnswers
        val message = getString(R.string.score_message, correct, total)

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}