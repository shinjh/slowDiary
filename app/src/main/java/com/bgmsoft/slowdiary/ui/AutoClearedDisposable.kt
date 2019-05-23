package com.bgmsoft.slowdiary.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bgmsoft.slowdiary.util.L
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable(
    private val lifecycleOwner: AppCompatActivity,
    private val alwaysClearOnStop: Boolean = true,
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : LifecycleObserver {

    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.add(disposable)
    }

    fun remove(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.remove(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        if (!alwaysClearOnStop) return

        compositeDisposable.clear()

        L.e(lifecycleOwner.componentName.className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        if (!alwaysClearOnStop && !lifecycleOwner.isFinishing) return

        compositeDisposable.clear()

        L.e(lifecycleOwner.componentName.className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        compositeDisposable.clear()

        lifecycleOwner.lifecycle.removeObserver(this)
    }

    fun isEmpty() = compositeDisposable.size() == 0
}