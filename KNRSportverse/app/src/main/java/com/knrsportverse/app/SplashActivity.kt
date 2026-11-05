package com.knrsportverse.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

/**
 * Opening animation in the same blue/red style as the KNR logo:
 * rings spin in, logo pops with overshoot, title + tagline slide up, loader fills, then fade into the site.
 */
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var launched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ringBlue = findViewById<View>(R.id.ringBlue)
        val ringRed = findViewById<View>(R.id.ringRed)
        val logo = findViewById<View>(R.id.logo)
        val title = findViewById<View>(R.id.title)
        val tagline = findViewById<View>(R.id.tagline)
        val bar = findViewById<ProgressBar>(R.id.loader)
        val root = findViewById<View>(R.id.root)

        listOf(ringBlue, ringRed, logo).forEach { it.alpha = 0f; it.scaleX = 0.3f; it.scaleY = 0.3f }
        listOf(title, tagline, bar).forEach { it.alpha = 0f; it.translationY = 60f }

        val rings = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(ringBlue, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(ringBlue, View.SCALE_X, 0.3f, 1f),
                ObjectAnimator.ofFloat(ringBlue, View.SCALE_Y, 0.3f, 1f),
                ObjectAnimator.ofFloat(ringBlue, View.ROTATION, -200f, 0f),
                ObjectAnimator.ofFloat(ringRed, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(ringRed, View.SCALE_X, 0.3f, 1f),
                ObjectAnimator.ofFloat(ringRed, View.SCALE_Y, 0.3f, 1f),
                ObjectAnimator.ofFloat(ringRed, View.ROTATION, 260f, 0f)
            )
            duration = 900
            interpolator = AccelerateDecelerateInterpolator()
        }

        val logoPop = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.3f, 1f),
                ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.3f, 1f)
            )
            duration = 700
            interpolator = OvershootInterpolator(1.6f)
        }

        val texts = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(title, View.TRANSLATION_Y, 60f, 0f),
                ObjectAnimator.ofFloat(tagline, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(tagline, View.TRANSLATION_Y, 60f, 0f),
                ObjectAnimator.ofFloat(bar, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(bar, View.TRANSLATION_Y, 60f, 0f)
            )
            duration = 600
        }

        val fill = ObjectAnimator.ofInt(bar, "progress", 0, 100).apply { duration = 900 }

        AnimatorSet().apply {
            play(rings)
            play(logoPop).after(300)
            play(texts).after(900)
            play(fill).after(1100)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    root.animate().alpha(0f).setDuration(350).withEndAction { openSite() }.start()
                }
            })
            start()
        }

        // Safety net
        handler.postDelayed({ openSite() }, 4500)
    }

    private fun openSite() {
        if (launched || isFinishing) return
        launched = true
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
