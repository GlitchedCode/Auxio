/*
 * Copyright (c) 2025 Auxio Project
 * KarmaStoredPlaylistHandle.kt is part of Auxio.
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

import org.oxycblt.musikr.Song
import org.oxycblt.musikr.playlist.PlaylistHandle

internal class KarmaStoredPlaylistHandle(
    private val playlistInfo: KarmaPlaylistInfo,
    private val karmaDao: KarmaPlaylistDao,
) : PlaylistHandle {
    override val uid = playlistInfo.playlistUid

    override suspend fun rename(name: String) {
        karmaDao.replaceKarmaPlaylistInfo(playlistInfo.copy(name = name))
    }

    override suspend fun rewrite(songs: List<Song>) {
        karmaDao.replaceKarmaPlaylistSongs(
            uid,
            songs.map { KarmaPlaylistSongCrossRef(playlistUid = uid, songUid = it.uid) },
        )
    }

    override suspend fun add(songs: List<Song>) {
        karmaDao.insertKarmaPlaylistSongs(
            uid,
            songs.map { KarmaPlaylistSongCrossRef(playlistUid = uid, songUid = it.uid) },
        )
    }

    override suspend fun delete() {
        karmaDao.deleteKarmaPlaylist(uid)
    }

    /**
     * Atomically adjust the karma of a [song] in this playlist by [delta].
     *
     * The result is clamped to [KarmaPlaylistSongCrossRef.MAX_KARMA]. If the new karma value
     * reaches 0 or below, the song is automatically removed from the playlist.
     *
     * @return The new karma value, or null if the song was removed.
     */
    suspend fun adjustKarma(song: Song, delta: Int): Int? =
        karmaDao.adjustKarma(uid, song.uid, delta)
}
