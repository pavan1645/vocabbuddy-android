package com.example.vocabbuddy.Practice

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.vocabbuddy.Models.Word
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_practice_section.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class PracticeSection : AppCompatActivity() {

//    TODO: Add score to DB

    private var allWords: List<Word> = ArrayList();
    private var remainingWords: MutableList<Word> = ArrayList();
    private var answerIndex: Int = -1;
    private var score: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_section)

        val sectionId = intent.extras?.getInt("id") ?: 0;

        val db = VocabDb(this);

        GlobalScope.launch {
            allWords = db.WordDao().getAllWords(sectionId)
            remainingWords.addAll(allWords);
            generateNewCard()
        }

        next.setOnClickListener { generateNewCard() }
    }

    private fun generateNewCard() {
        if (remainingWords.size == 0) return Toast.makeText(this, "Total Score ${score}", Toast.LENGTH_SHORT).show()

        val questionWord: Word = remainingWords[Random.nextInt(0, remainingWords.size)];
        answerIndex = Random.nextInt(0, 4);

        question_no.text = "Question ${allWords.size - remainingWords.size + 1} / ${allWords.size}"

        val answers: MutableList<String> = MutableList(4) {""};
        for (i in 0..3) {
            if (i == answerIndex) {
                answers[i] = questionWord.definition.orEmpty()
                continue
            }

            var ansWord = allWords[Random.nextInt(0, allWords.size)]
            while (ansWord.id == questionWord.id) ansWord = allWords[Random.nextInt(0, allWords.size)]

            answers[i] = ansWord.definition.orEmpty()
        }

        remainingWords.remove(questionWord);

        word.text = questionWord.word;
        answerRadioGroup.clearCheck()
        for (i in 0..answerRadioGroup.childCount) {
            if (answerRadioGroup.getChildAt(i) !is RadioButton) continue;
            val radioButton = answerRadioGroup.getChildAt(i) as RadioButton
            radioButton.text = answers[i]
        }
        toogleRadioGroup(true, true)
    }

    fun optionSelected(v: View) {
        val successBg = resources.getColor(R.color.colorSuccessBg, null);
        val dangerBg = resources.getColor(R.color.colorDangerBg, null);

        v as RadioButton;

        if (v.tag.toString().equals(answerIndex.toString())) {
            v.setBackgroundColor(successBg)
            v.buttonTintList = ColorStateList.valueOf(successBg)
            score++;
            total_score.text = "Score: ${score}"
        } else {
            val correctOptionRadio = answerRadioGroup.getChildAt(answerIndex) as RadioButton
            correctOptionRadio.setBackgroundColor(successBg)
            v.setBackgroundColor(dangerBg)
            v.buttonTintList = ColorStateList.valueOf(dangerBg)
        }
        toogleRadioGroup(false, false)
    }

    private fun toogleRadioGroup(isClickable: Boolean, reset: Boolean) {
        for (i in 0..answerRadioGroup.childCount) {
            val radioButton = answerRadioGroup.getChildAt(i);
            if (radioButton == null) continue;
            radioButton as RadioButton;
            radioButton.isClickable = isClickable;
            if (reset) {
                radioButton.setBackgroundColor(resources.getColor(R.color.colorWhite, null))
                radioButton.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary, null))
            }
        }
    }

}
