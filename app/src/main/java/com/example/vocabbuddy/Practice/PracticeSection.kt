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

    private var allWords: List<Word> = ArrayList();
    private var remainingWords: MutableList<Word> = ArrayList();
    private var answerIndex: Int = -1;
    private var bestScore: Int = 0;
    private var currScore: Int = 0;
    private lateinit var db: VocabDb;
    private var sectionId: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_section)

        sectionId = intent.extras?.getInt("id") ?: 0;
        db = VocabDb(this);

        GlobalScope.launch {
            allWords = db.WordDao().getAllWords(sectionId)
            val section = db.SectionDao().getSectionById(sectionId)
            remainingWords.addAll(allWords);
            bestScore = section.best_score ?: 0;
            best_score.text = "Best Score: ${bestScore}";
            generateNewCard()
        }

        next.setOnClickListener { generateNewCard() }
    }

    private fun generateNewCard() {
        if (remainingWords.size == 0) return Toast.makeText(this, "Total Score ${currScore}", Toast.LENGTH_SHORT).show()

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
        val successBg = resources.getColor(R.color.SuccessBg, null);
        val dangerBg = resources.getColor(R.color.DangerBg, null);

        v as RadioButton;

        if (v.tag.toString().equals(answerIndex.toString())) {
            v.setBackgroundColor(successBg)
            v.buttonTintList = ColorStateList.valueOf(successBg)
            currScore++;
            current_score.text = "Current Score: ${currScore}"

            if (currScore > bestScore) {
                GlobalScope.launch { db.SectionDao().setSectionScore(sectionId, currScore) }
                bestScore = currScore;
                best_score.text = "Best Score: ${bestScore}";
            }

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
                radioButton.setBackgroundColor(resources.getColor(R.color.White, null))
                radioButton.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.SecondaryBg, null))
            }
        }
    }

}
