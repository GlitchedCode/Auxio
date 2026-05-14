/*
 * Copyright (c) 2024 Auxio Project
 * Library.kt is part of Auxio.
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
 
package org.oxycblt.musikr

import org.oxycblt.musikr.fs.Path

/**
 * An immutable music library.
 *
 * No operations here will create side effects.
 */
interface Library {
    val songs: Collection<Song>
    val albums: Collection<Album>
    val artists: Collection<Artist>
    val genres: Collection<Genre>
    val playlists: Collection<Playlist>
    /** All [KarmaPlaylist]s in this library. */
    val karmaPlaylists: Collection<KarmaPlaylist>

    /**
     * Whether this library is empty (i.e no songs, which means no other music item)
     *
     * @return true if this library is empty, false otherwise
     */
    fun empty(): Boolean

    /**
     * Find a [Song] by it's [Music.UID]
     *
     * @param uid the [Music.UID] of the song
     * @return the song if found, null otherwise
     */
    fun findSong(uid: Music.UID): Song?

    /**
     * Find a [Song] by it's [Path]
     *
     * @param path the [Path] of the song
     * @return the song if found, null otherwise
     */
    fun findSongByPath(path: Path): Song?

    /**
     * Find an [Album] by it's [Music.UID]
     *
     * @param uid the [Music.UID] of the album
     * @return the album if found, null otherwise
     */
    fun findAlbum(uid: Music.UID): Album?

    /**
     * Find an [Artist] by it's [Music.UID]
     *
     * @param uid the [Music.UID] of the artist
     * @return the artist if found, null otherwise
     */
    fun findArtist(uid: Music.UID): Artist?

    /**
     * Find a [Genre] by it's [Music.UID]
     *
     * @param uid the [Music.UID] of the genre
     * @return the genre if found, null otherwise
     */
    fun findGenre(uid: Music.UID): Genre?

    /**
     * Find a [Playlist] (including [KarmaPlaylist]) by its [Music.UID].
     *
     * @param uid the [Music.UID] of the playlist
     * @return the playlist if found, null otherwise
     */
    fun findPlaylist(uid: Music.UID): Playlist?

    /**
     * Find a [Playlist] by its name (searches regular playlists only).
     *
     * @param name the name of the playlist
     * @return the playlist if found, null otherwise
     */
    fun findPlaylistByName(name: String): Playlist?

    /**
     * Find a [KarmaPlaylist] by its [Music.UID].
     *
     * @param uid the [Music.UID] of the karma playlist
     * @return the karma playlist if found, null otherwise
     */
    fun findKarmaPlaylist(uid: Music.UID): KarmaPlaylist?
}

/**
 * A mutable extension of [Library].
 *
 * Operations here will cause side-effects within the [Config] used when this library was loaded.
 * However, it won't actually mutate the [Library] itself, rather return a cloned instance with the
 * changes applied. It is up to the client to update their reference to the library within their
 * state handling.
 */
interface MutableLibrary : Library {
    /**
     * Create a new [Playlist] with the given name and songs.
     *
     * This will commit the new playlist to the stored playlists in the [Config] used to load the
     * library.
     *
     * @param name the name of the playlist
     * @param songs the songs to add to the playlist
     * @return a new [MutableLibrary] with the new playlist
     */
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary

    /**
     * Rename a [Playlist].
     *
     * This will commit to whatever playlist source the given [Playlist] was loaded from.
     *
     * @param playlist the playlist to rename
     * @param name the new name of the playlist
     * @return a new [MutableLibrary] with the renamed playlist
     */
    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary

    /**
     * Add songs to a [Playlist].
     *
     * This will commit to whatever playlist source the given [Playlist] was loaded from.
     *
     * @param playlist the playlist to add songs to
     * @param songs the songs to add to the playlist
     * @return a new [MutableLibrary] with the edited playlist
     */
    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    /**
     * Remove songs from a [Playlist].
     *
     * This will commit to whatever playlist source the given [Playlist] was loaded from.
     *
     * @param playlist the playlist to remove songs from
     * @param songs the songs to remove from the playlist
     * @return a new [MutableLibrary] with the edited playlist
     */
    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    /**
     * Remove a [Playlist].
     *
     * This will commit to whatever playlist source the given [Playlist] was loaded from.
     *
     * @param playlist the playlist to delete
     * @return a new [MutableLibrary] with the edited playlist
     */
    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary

    /**
     * Create a new [KarmaPlaylist] with the given name and songs.
     *
     * @param name the name of the playlist
     * @param songs the songs to populate the playlist with (each starts at max karma)
     * @return a new [MutableLibrary] with the added karma playlist
     */
    suspend fun createKarmaPlaylist(name: String, songs: List<Song>): MutableLibrary

    /**
     * Delete a [KarmaPlaylist].
     *
     * @param playlist the karma playlist to delete
     * @return a new [MutableLibrary] without the given karma playlist
     */
    suspend fun deleteKarmaPlaylist(playlist: KarmaPlaylist): MutableLibrary

    /**
     * Add songs to a [KarmaPlaylist]. New songs start at max karma.
     *
     * @param playlist the karma playlist to add to
     * @param songs the songs to add
     * @return a new [MutableLibrary] with the updated karma playlist
     */
    suspend fun addToKarmaPlaylist(playlist: KarmaPlaylist, songs: List<Song>): MutableLibrary

    /**
     * Adjust the karma of [song] in [playlist] by [delta].
     *
     * The karma is clamped to [KarmaPlaylist.MAX_KARMA] and cannot go below 0 (the song is
     * removed when it reaches 0).
     *
     * @return a pair of (newLibrary, newKarmaValue). [newKarmaValue] is null if the song was
     *   removed from the playlist.
     */
    suspend fun adjustKarma(
        playlist: KarmaPlaylist,
        song: Song,
        delta: Int,
    ): Pair<MutableLibrary, Int?>
}
