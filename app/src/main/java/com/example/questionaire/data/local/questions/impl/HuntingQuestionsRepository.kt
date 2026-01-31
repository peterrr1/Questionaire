package com.example.questionaire.data.local.questions.impl

import com.example.questionaire.data.local.questions.QuestionsRepository
import com.example.questionaire.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.example.questionaire.utils.Result
import com.example.questionaire.model.QuestionCategory
import javax.inject.Inject

class HuntingQuestionsRepository @Inject constructor() : QuestionsRepository {

    override suspend fun getAllQuestions(): Result<List<Question>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(questionsWithOptions)
            }
        }
    }

    override suspend fun getQuestionCategory(category: QuestionCategory): Result<List<Question>> {
        return withContext(Dispatchers.IO) {
            delay(1000)
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(
                    data = questionsWithOptions.filter { q ->
                        q.category == category
                    }
                )
            }
        }
    }

    private var requestCount = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}

