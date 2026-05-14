/*
 * Copyright (c) 2025 Auxio Project
 * RawKarmaPlaylist.kt is part of Auxio.
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

package org.oxycblt.musikr.playlist.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.oxycblt.musikr.Music

/**
 * Raw karma playlist information persisted to [PlaylistDatabase].
 */
internal data class RawKarmaPlaylist(
    @Embedded val playlistInfo: KarmaPlaylistInfo,
    @Relation(
        parentColumn = "playlistUid",
        entityColumn = "playlistUid",
        entity = KarmaPlaylistSongCrossRef::class,
    )
    val songs: List<KarmaPlaylistSongCrossRef>,
)

/**
 * UID and name information for a karma playlist.
 */
@Entity
internal data class KarmaPlaylistInfo(
    @PrimaryKey val playlistUid: Music.UID,
    val name: String,
)

/**
 * Links a song to a karma playlist, carrying per-song karma value.
 *
 * Karma starts at [MAX_KARMA] and is decremented on early skip. If it reaches zero or below,
 * the song is removed from the playlist automatically.
 */
@Entity
internal data class KarmaPlaylistSongCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val playlistUid: Music.UID,
    @ColumnInfo(index = true) val songUid: Music.UID,
    val karma: Int = MAX_KARMA,
) {
    companion object {
        const val MAX_KARMA = 10
    }
}
