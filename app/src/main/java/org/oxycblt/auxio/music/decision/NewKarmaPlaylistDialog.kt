/*
 * Copyright (c) 2025 Auxio Project
 * NewKarmaPlaylistDialog.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.music.decision

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogPlaylistNameBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import timber.log.Timber as L

/**
 * A dialog that prompts for the name of a new [org.oxycblt.musikr.KarmaPlaylist].
 */
@AndroidEntryPoint
class NewKarmaPlaylistDialog : ViewBindingMaterialDialogFragment<DialogPlaylistNameBinding>() {
    private val musicModel: MusicViewModel by activityViewModels()
    private var pendingName: String? = null

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.lbl_new_karma_playlist)
            .setPositiveButton(R.string.lbl_ok) { _, _ ->
                val name = pendingName?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.lbl_karma_playlist)
                L.d("Confirmed karma playlist name: $name")
                musicModel.createKarmaPlaylist(name)
                musicModel.playlistDecision.consume()
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.lbl_cancel) { _, _ ->
                musicModel.playlistDecision.consume()
            }
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogPlaylistNameBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogPlaylistNameBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.playlistName.hint = getString(R.string.lbl_karma_playlist)
        binding.playlistName.addTextChangedListener { text ->
            pendingName = text?.toString()
            updateOkButton()
        }
    }

    override fun onResume() {
        super.onResume()
        updateOkButton()
    }

    private fun updateOkButton() {
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
    }
}
