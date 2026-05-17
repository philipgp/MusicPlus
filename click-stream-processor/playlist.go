package main

import (
	"fmt"
	"strings"
)

type Playlist struct {
	items []PlaylistItem
	name  string
	uid   string
}

type PlaylistItem struct {
	track Track
}

type Track struct {
	title  string
	artist string
	album  string
	path   string
}

func add(playlist Playlist, track Track) (int64, error) {
	result, err := db.Exec(" INSERT INTO playlist_track (playlist_id, track_id) SELECT (SELECT id FROM playlist WHERE uid = ?),  (SELECT id FROM tracks WHERE title = ? and artist = ?);", playlist.uid, track.title, track.artist)
	if err != nil {
		return 0, fmt.Errorf("addAlbum: %v", err)
	}
	id, err := result.LastInsertId()
	if err != nil {
		return 0, fmt.Errorf("addAlbum: %v", err)
	}
	return id, nil
}

func getPlaylistItems(uid string) ([]PlaylistItem, error) {
	var playlistItems []PlaylistItem
	rows, err := db.Query("SELECT title,artist,album,path FROM tracks,playlist_track where tracks.id = playlist_track.track_id and  playlist_track.playlist_id=(select id from playlist where uid=?)", uid)
	if err != nil {
		return nil, fmt.Errorf("Failure!")
	}
	defer rows.Close()
	for rows.Next() {
		var playlistItem PlaylistItem
		if err := rows.Scan(&playlistItem.track.title, &playlistItem.track.artist, &playlistItem.track.album, &playlistItem.track.path); err != nil {
			return nil, fmt.Errorf("playlist ", err)
		}
		playlistItems = append(playlistItems, playlistItem)
	}
	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("albumsByArtist %q: %v", err)
	}
	fmt.Print(playlistItems)
	return playlistItems, nil
}

// func getPlaylist(uid string) (Playlist, error) {

// }

//

func getAll() ([]Playlist, error) {
	var playlists []Playlist
	rows, err := db.Query("SELECT name,uid FROM playlist ")
	if err != nil {
		return nil, fmt.Errorf("Failure!")
	}
	defer rows.Close()
	for rows.Next() {
		var playlist Playlist
		if err := rows.Scan(&playlist.name, &playlist.uid); err != nil {
			return nil, fmt.Errorf("playlist ", err)
		}
		playlists = append(playlists, playlist)
	}
	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("albumsByArtist %q: %v", err)
	}
	fmt.Print(playlists)
	return playlists, nil
}
func Generate(playlistitems []PlaylistItem) (string, error) {
	var sb strings.Builder
	sb.WriteString("#EXTM3U\n")
	sb.WriteString(fmt.Sprintf("#PLAYLIST:%s\n", "Auto-generated playlist"))
	for _, item := range playlistitems {
		sb.WriteString("#EXTINF\n")
		idx := strings.Index(item.track.path, "/music/")
		filepath := item.track.path[idx+1:]
		sb.WriteString(filepath + "\n")
	}
	return sb.String(), nil
}

func main2() {
	// connectToDB()
	// playlist_items, error := getPlaylistItems("uid1")
	// if error != nil {
	// 	fmt.Errorf("Failure!")
	// }
	// println(Generate(playlist_items))
	// // getAll()
	// getPlaylistItems("uid1")
	// playlist := Playlist{uid: "uid1"}
	// track := Track{artist: "Unknown Artist", title: "Intro"}
	// result, err := add(playlist, track)
	// fmt.Println(result)
	// if err != nil {
	// 	fmt.Errorf("Failure!")
	// }
	// getPlaylistItems("uid1")
}
