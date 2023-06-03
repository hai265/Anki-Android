/*
 * Copyright (c) 2022 Nishant Bhandari <nishantbhandari0019@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ichi2.compat

import android.annotation.TargetApi
import android.view.*
import com.ichi2.anki.Reviewer
import com.ichi2.anki.reviewer.FullScreenMode

/** Implementation of [Compat] for SDK level 30 and higher. Check [Compat]'s for more detail.  */
@TargetApi(30)
open class CompatV30 : CompatV29(), Compat {
    override fun setFullscreen(window: Window?) {
        window?.decorView?.windowInsetsController?.hide(WindowInsets.Type.statusBars())
    }

    override fun hideSystemBars(
        window: Window?,
        toolbar: View?,
        answerButtons: View?,
        topBar: View?,
        fullScreenMode: FullScreenMode
    ) {
        window?.setDecorFitsSystemWindows(false)
        val controller = window?.insetsController
        controller?.let {
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }

        window?.decorView?.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (toolbar == null || topBar == null || answerButtons == null) {
                return@setOnApplyWindowInsetsListener windowInsets
            }
            val visible = windowInsets.isVisible(WindowInsets.Type.navigationBars())
            if (visible) {
                Reviewer.showViewWithAnimation(toolbar)
                if (fullScreenMode == FullScreenMode.BUTTONS_AND_MENU) {
                    Reviewer.showViewWithAnimation(topBar)
                    Reviewer.showViewWithAnimation(answerButtons)
                }
            } else {
                Reviewer.hideViewWithAnimation(toolbar)
                if (fullScreenMode == FullScreenMode.FULLSCREEN_ALL_GONE) {
                    Reviewer.hideViewWithAnimation(topBar)
                    Reviewer.hideViewWithAnimation(answerButtons)
                }
            }
            return@setOnApplyWindowInsetsListener windowInsets
        }
    }

    override fun isImmersiveSystemUiVisible(window: Window?): Boolean {
        return window?.decorView?.windowInsetsController?.systemBarsAppearance == 0
    }
}
