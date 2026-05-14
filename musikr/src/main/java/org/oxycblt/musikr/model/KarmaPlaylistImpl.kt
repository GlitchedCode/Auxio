/*
 * Copyright (c) 2025 Auxio Project
 * KarmaPlaylistImpl.kt is part of Auxio.
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

package org.oxycblt.musikr.model

import org.oxycblt.musikr.KarmaPlaylist
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.covers.CoverCollection
import org.oxycblt.musikr.playlist.db.KarmaStoredPlaylistHandle
import org.oxycblt.musikr.playlist.interpret.PostPlaylist
import org.oxycblt.musikr.tag.Name

internal class KarmaPlaylistImpl(
    /** Interpreted name + handle for standard playlist operations. */
    val postPlaylist: PostPlaylist,
    override val songs: List<Song>,
    override val karmaMap: Map<Music.UID, Int>,
    /** Direct handle reference used for karma adjustments. */
    val handle: KarmaStoredPlaylistHandle,
) : KarmaPlaylist {
    override val uid = postPlaylist.handle.uid
    override val name: Name.Known = postPlaylist.name
    override val durationMs = songs.sumOf { it.durationMs }
    override val covers = CoverCollection.from(songs.mapNotNull { it.cover })

    private val hashCode =
        31 * (31 * uid.hashCode() + postPlaylist.hashCode()) + songs.hashCode()

    override fun equals(other: Any?) =
        other is KarmaPlaylistImpl &&
            postPlaylist == other.postPlaylist &&
            songs == other.songs &&
            karmaMap == other.karmaMap

    override fun hashCode() = hashCode

    override fun toString() = "KarmaPlaylist(uid=$uid, name=$name)"
}
