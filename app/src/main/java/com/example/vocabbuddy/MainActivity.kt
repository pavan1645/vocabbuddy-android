package com.example.vocabbuddy

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.vocabbuddy.Learn.LearnSectionList
import com.example.vocabbuddy.Practice.PracticeSectionList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = resources.getColor(R.color.Violet, null);

        learn_btn.startAnimation(getViewAnimation(1f))
        practice_btn.startAnimation(getViewAnimation(0f))
    }

    fun openPracticeSectionList(v: View) {
        val intent = Intent(this, PracticeSectionList::class.java)
        startActivity(intent)
    }

    fun openLearnSectionList(v: View) {
        val intent = Intent(this, LearnSectionList::class.java)
        startActivity(intent)
    }

    private fun getViewAnimation(pivotX: Float): Animation {
        val anim = ScaleAnimation(0f, 1f,1f, 1f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, 1f)
        anim.fillAfter = true
        anim.duration = 300
        return anim
    }
}
