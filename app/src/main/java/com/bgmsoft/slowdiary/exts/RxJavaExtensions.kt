package com.bgmsoft.slowdiary.exts

import com.bgmsoft.slowdiary.view.components.AutoClearedDisposable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) = this.add(disposable)

operator fun AutoClearedDisposable.minusAssign(disposable: Disposable) = this.remove(disposable)