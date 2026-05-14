/*
 * Copyright (c) 2025 Auxio Project
 * KarmaPlaylistDao.kt is part of Auxio.
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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import org.oxycblt.musikr.Music

@Dao
internal abstract class KarmaPlaylistDao {

    @Transaction
    @Query("SELECT * FROM KarmaPlaylistInfo")
    abstract suspend fun readRawKarmaPlaylists(): List<RawKarmaPlaylist>

    @Transaction
    open suspend fun insertKarmaPlaylist(raw: RawKarmaPlaylist) {
        insertKarmaInfo(raw.playlistInfo)
        insertKarmaRefs(raw.songs)
    }

    @Transaction
    open suspend fun replaceKarmaPlaylistInfo(info: KarmaPlaylistInfo) {
        deleteKarmaInfo(info.playlistUid)
        insertKarmaInfo(info)
    }

    @Transaction
    open suspend fun deleteKarmaPlaylist(uid: Music.UID) {
        deleteKarmaInfo(uid)
        deleteKarmaRefs(uid)
    }

    @Transaction
    open suspend fun insertKarmaPlaylistSongs(uid: Music.UID, refs: List<KarmaPlaylistSongCrossRef>) {
        insertKarmaRefs(refs)
    }

    @Transaction
    open suspend fun replaceKarmaPlaylistSongs(uid: Music.UID, refs: List<KarmaPlaylistSongCrossRef>) {
        deleteKarmaRefs(uid)
        insertKarmaRefs(refs)
    }

    /**
     * Atomically adjust the karma of a song in a karma playlist.
     *
     * @return The new karma value, or null if the song was removed (karma <= 0).
     */
    @Transaction
    open suspend fun adjustKarma(
        playlistUid: Music.UID,
        songUid: Music.UID,
        delta: Int,
    ): Int? {
        val current = getKarma(playlistUid, songUid) ?: return null
        val newKarma = minOf(current + delta, KarmaPlaylistSongCrossRef.MAX_KARMA)
        return if (newKarma <= 0) {
            deleteKarmaRef(playlistUid, songUid)
            null
        } else {
            updateKarma(playlistUid, songUid, newKarma)
            newKarma
        }
    }

    /** Internal. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertKarmaInfo(info: KarmaPlaylistInfo)

    /** Internal. */
    @Query("DELETE FROM KarmaPlaylistInfo WHERE playlistUid = :uid")
    abstract suspend fun deleteKarmaInfo(uid: Music.UID)

    /** Internal. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertKarmaRefs(refs: List<KarmaPlaylistSongCrossRef>)

    /** Internal. */
    @Query("DELETE FROM KarmaPlaylistSongCrossRef WHERE playlistUid = :uid")
    abstract suspend fun deleteKarmaRefs(uid: Music.UID)

    /** Internal. */
    @Query(
        "SELECT karma FROM KarmaPlaylistSongCrossRef " +
            "WHERE playlistUid = :playlistUid AND songUid = :songUid LIMIT 1"
    )
    abstract suspend fun getKarma(playlistUid: Music.UID, songUid: Music.UID): Int?

    /** Internal. */
    @Query(
        "UPDATE KarmaPlaylistSongCrossRef SET karma = :karma " +
            "WHERE playlistUid = :playlistUid AND songUid = :songUid"
    )
    abstract suspend fun updateKarma(playlistUid: Music.UID, songUid: Music.UID, karma: Int)

    /** Internal. */
    @Query(
        "DELETE FROM KarmaPlaylistSongCrossRef " +
            "WHERE playlistUid = :playlistUid AND songUid = :songUid"
    )
    abstract suspend fun deleteKarmaRef(playlistUid: Music.UID, songUid: Music.UID)
}
