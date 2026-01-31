package com.example.questionaire.data.local.questions

import com.example.questionaire.model.Question
import com.example.questionaire.utils.Result
import com.example.questionaire.model.QuestionCategory

interface QuestionsRepository {

    suspend fun getAllQuestions(): Result<List<Question>>

    suspend fun getQuestionCategory(category: QuestionCategory): Result<List<Question>>
}