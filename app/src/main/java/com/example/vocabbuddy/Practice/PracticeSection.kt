package com.example.vocabbuddy.Practice

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.animation.doOnEnd
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
        window.statusBarColor = resources.getColor(R.color.AccentDark, null);

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
        next.setOnClickListener {
            animateNextButton(false)
            animateNextCard()
        }
    }

    private fun generateNewCard() {
        if (remainingWords.size == 0) {
            card_wrapper.visibility = View.GONE;
            next.visibility = View.GONE;
            question_no.visibility = View.GONE
            retake_quiz.visibility = View.VISIBLE
            final_score.visibility = View.VISIBLE
            final_score.text = "Final Score\n${currScore} / ${allWords.size}"
            return
        }

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
        animateNextButton(true)
    }

    fun restartActivity(v: View) {
        recreate()
    }


    private fun animateNextCard() {
        var xLen = resources.displayMetrics.widthPixels.toFloat() * -1;
        val currX = card_wrapper.translationX;

        val scaleDownXAnimation = ObjectAnimator.ofFloat(card_wrapper, View.SCALE_X, 0.9f)
        val scaleDownYAnimation = ObjectAnimator.ofFloat(card_wrapper, View.SCALE_Y, 0.9f)
        val moveOutAnimation = ObjectAnimator.ofFloat(card_wrapper, View.TRANSLATION_X, xLen)

        val moveInAnimation = ObjectAnimator.ofFloat(card_wrapper, View.TRANSLATION_X, xLen*-1, currX)
        val scaleInXAnimation = ObjectAnimator.ofFloat(card_wrapper, View.SCALE_X, 1f)
        val scaleInYAnimation = ObjectAnimator.ofFloat(card_wrapper, View.SCALE_Y, 1f)

        val moveOutAnimatorSet = AnimatorSet();
        val moveInAnimatorSet = AnimatorSet();

        val animationDuration = 250L;

        moveInAnimatorSet.apply {
            play(scaleInXAnimation).with(scaleInYAnimation).after(moveInAnimation)
            duration = animationDuration
        }

        moveOutAnimatorSet.apply {
            play(scaleDownXAnimation).with(scaleDownYAnimation).before(moveOutAnimation )
            duration = animationDuration
            doOnEnd {
                moveInAnimatorSet.start()
                generateNewCard()
            }
            start()
        }

    }

    private fun animateNextButton(show: Boolean) {
        val start = if (show) 0f else 1f;
        val end = if (show) 1f else 0f;

        val scaleAnimation = ScaleAnimation(start, end, start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = 300
        next.visibility = View.VISIBLE
        next.startAnimation(scaleAnimation);
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
