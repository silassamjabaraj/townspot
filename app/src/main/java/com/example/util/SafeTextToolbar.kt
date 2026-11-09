package com.example.util

import android.graphics.Rect as AndroidRect
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A safe [TextToolbar] implementation that intercepts FloatingActionMode lifecycle events,
 * preventing DecorView warnings ("Destroying unexpected ActionMode instance of TYPE_FLOATING")
 * caused by duplicate or asynchronous finish() calls during text selection or View/Dialog detachment.
 */
class SafeTextToolbar(private val view: View) : TextToolbar {
    private var currentActionMode: ActionMode? = null
    private val isFinishing = AtomicBoolean(false)
    private val isDestroyed = AtomicBoolean(false)

    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        if (!view.isAttachedToWindow) return

        val activeMode = currentActionMode
        if (activeMode != null && !isDestroyed.get() && !isFinishing.get()) {
            try {
                activeMode.invalidateContentRect()
            } catch (_: Throwable) {
                try {
                    activeMode.invalidate()
                } catch (_: Throwable) {
                }
            }
            return
        }

        isDestroyed.set(false)
        isFinishing.set(false)
        status = TextToolbarStatus.Shown

        val callback = object : ActionMode.Callback2() {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                if (mode == null || menu == null) return false
                if (onCopyRequested != null) {
                    menu.add(Menu.NONE, android.R.id.copy, 0, android.R.string.copy)
                }
                if (onPasteRequested != null) {
                    menu.add(Menu.NONE, android.R.id.paste, 1, android.R.string.paste)
                }
                if (onCutRequested != null) {
                    menu.add(Menu.NONE, android.R.id.cut, 2, android.R.string.cut)
                }
                if (onSelectAllRequested != null) {
                    menu.add(Menu.NONE, android.R.id.selectAll, 3, android.R.string.selectAll)
                }
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                val handled = when (item?.itemId) {
                    android.R.id.copy -> {
                        onCopyRequested?.invoke()
                        true
                    }
                    android.R.id.paste -> {
                        onPasteRequested?.invoke()
                        true
                    }
                    android.R.id.cut -> {
                        onCutRequested?.invoke()
                        true
                    }
                    android.R.id.selectAll -> {
                        onSelectAllRequested?.invoke()
                        true
                    }
                    else -> false
                }
                if (handled) {
                    safeFinish(mode)
                }
                return handled
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                isDestroyed.set(true)
                isFinishing.set(false)
                currentActionMode = null
                status = TextToolbarStatus.Hidden
            }

            override fun onGetContentRect(mode: ActionMode?, view: View?, outRect: AndroidRect?) {
                outRect?.set(
                    rect.left.toInt(),
                    rect.top.toInt(),
                    rect.right.toInt(),
                    rect.bottom.toInt()
                )
            }
        }

        try {
            currentActionMode = view.startActionMode(callback, ActionMode.TYPE_FLOATING)
        } catch (_: Throwable) {
            status = TextToolbarStatus.Hidden
            currentActionMode = null
            isDestroyed.set(true)
            isFinishing.set(false)
        }
    }

    override fun hide() {
        status = TextToolbarStatus.Hidden
        val mode = currentActionMode
        safeFinish(mode)
    }

    private fun safeFinish(mode: ActionMode?) {
        if (mode == null) return
        if (isFinishing.compareAndSet(false, true)) {
            currentActionMode = null
            if (!isDestroyed.get() && view.isAttachedToWindow) {
                try {
                    mode.finish()
                } catch (_: Throwable) {
                }
            }
            isDestroyed.set(true)
        }
    }
}

@Composable
fun ProvideSafeTextToolbar(content: @Composable () -> Unit) {
    val currentToolbar = LocalTextToolbar.current
    if (currentToolbar is SafeTextToolbar) {
        content()
    } else {
        val view = LocalView.current
        val safeToolbar = remember(view) { SafeTextToolbar(view) }
        CompositionLocalProvider(LocalTextToolbar provides safeToolbar) {
            content()
        }
    }
}
