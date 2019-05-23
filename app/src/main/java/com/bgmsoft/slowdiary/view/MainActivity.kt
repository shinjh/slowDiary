package com.bgmsoft.slowdiary.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.bgmsoft.slowdiary.R
import com.bgmsoft.slowdiary.databinding.ActivityMainBinding
import com.bgmsoft.slowdiary.view.components.AutoClearedDisposable
import com.bgmsoft.slowdiary.view.components.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewDisposables = AutoClearedDisposable(this)

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // layout binding
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            this.lifecycleOwner = this@MainActivity
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    fun setLogEvent(id: String, name: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}
