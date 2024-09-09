package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_1, false),
        Question(R.string.question_2, true),
        Question(R.string.question_3, true),
        Question(R.string.question_4, false),
        Question(R.string.question_5, false)
    )

    private val answeredQuestions = MutableList(questionBank.size) { false }

    var correctAnswers = 0
        private set
    var wrongAnswers = 0
        private set

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = (currentIndex - 1 + questionBank.size) % questionBank.size
    }

    fun markQuestionAsAnswered(userAnswer: Boolean) {
        if (!answeredQuestions[currentIndex]) {
            answeredQuestions[currentIndex] = true
            if (userAnswer == currentQuestionAnswer) {
                correctAnswers++
            } else {
                wrongAnswers++
            }
        }
    }

    fun isCurrentQuestionAnswered(): Boolean {
        return answeredQuestions[currentIndex]
    }

    fun allQuestionsAnswered(): Boolean {
        return answeredQuestions.all { it }
    }
}