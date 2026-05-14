/*
 * Copyright (c) 2024 Auxio Project
 * KarmaSettings.kt is part of Auxio.
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

package org.oxycblt.auxio.playback

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.settings.Settings

/**
 * Settings controlling karma playlist behavior.
 *
 * All thresholds are in milliseconds; all weights are integers added to (or subtracted from) the
 * per-song karma value (clamped to [org.oxycblt.musikr.KarmaPlaylist.MAX_KARMA]).
 */
interface KarmaSettings : Settings<Nothing> {
    /**
     * Position threshold (ms) below which a skip counts as an "early skip".
     * Default: 15 000 ms.
     */
    val earlySkipThresholdMs: Long

    /**
     * Distance from the end of the song (ms) above which a skip counts as a "late skip"
     * (i.e. `position > duration - lateSkipThresholdMs`).
     * Default: 30 000 ms.
     */
    val lateSkipThresholdMs: Long

    /** Karma delta applied when skipping early. Default: -2. */
    val earlySkipWeight: Int

    /**
     * Karma delta applied when skipping in the middle zone (after early threshold, before late
     * threshold). Default: 0.
     */
    val midSkipWeight: Int

    /** Karma delta applied when skipping during the outro zone. Default: +10. */
    val lateSkipWeight: Int

    /** Karma delta applied when a song completes naturally. Default: +10. */
    val completionWeight: Int
}

class KarmaSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<Nothing>(context), KarmaSettings {

    override val earlySkipThresholdMs: Long
        get() =
            sharedPreferences
                .getLong(KEY_EARLY_SKIP_THRESHOLD_MS, DEFAULT_EARLY_SKIP_THRESHOLD_MS)

    override val lateSkipThresholdMs: Long
        get() =
            sharedPreferences
                .getLong(KEY_LATE_SKIP_THRESHOLD_MS, DEFAULT_LATE_SKIP_THRESHOLD_MS)

    override val earlySkipWeight: Int
        get() = sharedPreferences.getInt(KEY_EARLY_SKIP_WEIGHT, DEFAULT_EARLY_SKIP_WEIGHT)

    override val midSkipWeight: Int
        get() = sharedPreferences.getInt(KEY_MID_SKIP_WEIGHT, DEFAULT_MID_SKIP_WEIGHT)

    override val lateSkipWeight: Int
        get() = sharedPreferences.getInt(KEY_LATE_SKIP_WEIGHT, DEFAULT_LATE_SKIP_WEIGHT)

    override val completionWeight: Int
        get() = sharedPreferences.getInt(KEY_COMPLETION_WEIGHT, DEFAULT_COMPLETION_WEIGHT)

    companion object {
        private const val KEY_EARLY_SKIP_THRESHOLD_MS = "karma_early_skip_threshold_ms"
        private const val KEY_LATE_SKIP_THRESHOLD_MS = "karma_late_skip_threshold_ms"
        private const val KEY_EARLY_SKIP_WEIGHT = "karma_early_skip_weight"
        private const val KEY_MID_SKIP_WEIGHT = "karma_mid_skip_weight"
        private const val KEY_LATE_SKIP_WEIGHT = "karma_late_skip_weight"
        private const val KEY_COMPLETION_WEIGHT = "karma_completion_weight"

        const val DEFAULT_EARLY_SKIP_THRESHOLD_MS = 15_000L
        const val DEFAULT_LATE_SKIP_THRESHOLD_MS = 30_000L
        const val DEFAULT_EARLY_SKIP_WEIGHT = -2
        const val DEFAULT_MID_SKIP_WEIGHT = 0
        const val DEFAULT_LATE_SKIP_WEIGHT = 10
        const val DEFAULT_COMPLETION_WEIGHT = 10
    }
}
