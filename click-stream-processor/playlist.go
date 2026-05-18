package main

import (
	"fmt"
	"log/slog"
	"strings"
	"time"
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
	slog.Info("add: inserting track into playlist", "playlist_uid", playlist.uid, "title", track.title, "artist", track.artist)
	start := time.Now()
	result, err := db.Exec(" INSERT INTO playlist_track (playlist_id, track_id) SELECT (SELECT id FROM playlist WHERE uid = ?),  (SELECT id FROM tracks WHERE title = ? and artist = ?);", playlist.uid, track.title, track.artist)
	if err != nil {
		slog.Error("add: insert failed", "error", err, "duration_ms", time.Since(start).Milliseconds())
		return 0, fmt.Errorf("addAlbum: %v", err)
	}
	id, err := result.LastInsertId()
	if err != nil {
		return 0, fmt.Errorf("addAlbum: %v", err)
	}
	slog.Info("add: insert succeeded", "id", id, "duration_ms", time.Since(start).Milliseconds())
	return id, nil
}
func getPlaylistDetail(uid string) (Playlist, error) {
	slog.Info("getPlaylistItems: querying", "uid", uid)
	start := time.Now()
	playlist := Playlist{}
	row := db.QueryRow("SELECT name,uid FROM playlist where  uid=?", uid)
	err := row.Scan(&playlist.name, &playlist.uid)
	if err != nil {
		slog.Error("getPlaylistItems: query failed", "error", err, "duration_ms", time.Since(start).Milliseconds())
		return playlist, fmt.Errorf("getPlaylistItems: %v", err)
	}
	slog.Info("getPlaylistItems: completed", "uid", uid, "duration_ms", time.Since(start).Milliseconds())
	return playlist, nil
}
func getPlaylistItems(uid string) ([]PlaylistItem, error) {
	slog.Info("getPlaylistItems: querying", "uid", uid)
	start := time.Now()
	var playlistItems []PlaylistItem
	rows, err := db.Query("SELECT title,artist,album,path FROM tracks,playlist_track where tracks.id = playlist_track.track_id and  playlist_track.playlist_id=(select id from playlist where uid=?)", uid)
	if err != nil {
		slog.Error("getPlaylistItems: query failed", "error", err, "duration_ms", time.Since(start).Milliseconds())
		return nil, fmt.Errorf("getPlaylistItems: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var playlistItem PlaylistItem
		if err := rows.Scan(&playlistItem.track.title, &playlistItem.track.artist, &playlistItem.track.album, &playlistItem.track.path); err != nil {
			slog.Error("getPlaylistItems: scan failed", "error", err)
			return nil, fmt.Errorf("getPlaylistItems scan: %v", err)
		}
		playlistItems = append(playlistItems, playlistItem)
	}
	if err := rows.Err(); err != nil {
		slog.Error("getPlaylistItems: rows error", "error", err)
		return nil, fmt.Errorf("getPlaylistItems rows: %v", err)
	}
	slog.Info("getPlaylistItems: completed", "count", len(playlistItems), "duration_ms", time.Since(start).Milliseconds())
	return playlistItems, nil
}

// func getPlaylist(uid string) (Playlist, error) {

// }

//

func clear(uid string) error {
	slog.Info("clear: deleting all items in playlist", "uid", uid)
	start := time.Now()
	_, err := db.Exec("DELETE FROM playlist_track WHERE playlist_id = (SELECT id FROM playlist WHERE uid = ?)", uid)
	if err != nil {
		slog.Error("clear: delete failed", "error", err, "duration_ms", time.Since(start).Milliseconds())
		return fmt.Errorf("clear: %v", err)
	}
	slog.Info("clear: completed", "uid", uid, "duration_ms", time.Since(start).Milliseconds())
	return nil
}

func getAll() ([]Playlist, error) {
	slog.Info("getAll: querying all playlists")
	start := time.Now()
	var playlists []Playlist
	rows, err := db.Query("SELECT name,uid FROM playlist ")
	if err != nil {
		slog.Error("getAll: query failed", "error", err, "duration_ms", time.Since(start).Milliseconds())
		return nil, fmt.Errorf("getAll: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var playlist Playlist
		if err := rows.Scan(&playlist.name, &playlist.uid); err != nil {
			slog.Error("getAll: scan failed", "error", err)
			return nil, fmt.Errorf("getAll scan: %v", err)
		}
		playlists = append(playlists, playlist)
	}
	if err := rows.Err(); err != nil {
		slog.Error("getAll: rows error", "error", err)
		return nil, fmt.Errorf("getAll rows: %v", err)
	}
	slog.Info("getAll: completed", "count", len(playlists), "duration_ms", time.Since(start).Milliseconds())
	return playlists, nil
}
func Generate(playlist Playlist, playlistitems []PlaylistItem) (string, error) {
	var sb strings.Builder
	sb.WriteString("#EXTM3U\n")
	sb.WriteString(fmt.Sprintf("#PLAYLIST:%s\n", playlist.name))
	for _, item := range playlistitems {
		sb.WriteString("#EXTINF\n")
		idx := strings.Index(item.track.path, "/music/")
		filepath := item.track.path[idx+7:]
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
