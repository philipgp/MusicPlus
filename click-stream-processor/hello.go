package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	"cloud.google.com/go/pubsub"
	"github.com/gin-gonic/gin"
	"github.com/go-sql-driver/mysql"
	"github.com/linkedin/goavro/v2"
)

var db *sql.DB

type trackProgress struct {
	FingerPrint     string `json:"fingerprint"`
	UID             string `json:"uid"`
	ProgressPercent int    `json:"progressPercent"`
	Event           string `json:"event"`
	StartTime       string `json:"startTime"`
	EndTime         string `json:"endTime"`
	EventTime       string `json:"eventTime"`
	Title           string `json:"title"`
	Uri             string `json:"uri"`
	AlbumArtist     string `json:"albumArtist"`
	Artist          string `json:"artist"`
	Album           string `json:"album"`
}

func connectToDB() {
	// Capture connection properties.2
	cfg := mysql.Config{
		User:   os.Getenv("MY_SQL_USERNAME"),
		Passwd: os.Getenv("MY_SQL_PASSWORD"),
		Net:    "tcp",
		Addr:   os.Getenv("MY_SQL_URL"), //"127.0.0.1:3306",
		DBName: "musick",
	}
	// Get a database handle.
	var err error
	db, err = sql.Open("mysql", cfg.FormatDSN())
	if err != nil {
		log.Fatal(err)
	}

	pingErr := db.Ping()
	if pingErr != nil {
		log.Fatal(pingErr)
	}
	fmt.Println("Connected!")
}
func saveProgressToDB(ctx context.Context, TrackProgress trackProgress) (int64, error) {
	tx, err := db.BeginTx(ctx, nil)
	if err != nil {
		return 0, fmt.Errorf("saveProgrjessToDB: %v", err)
	}
	defer tx.Rollback()
	// log.Fatalf("Inserting data")
	result, err := db.Exec("INSERT INTO track_play_progress (uid,fingerprint,progressPercent,album,artist,title,albumArtist) VALUES (?, ?, ?,?,?,?,?)", TrackProgress.UID, TrackProgress.FingerPrint, TrackProgress.ProgressPercent, TrackProgress.Album, TrackProgress.Artist, TrackProgress.Title, TrackProgress.AlbumArtist)
	// log.Fatalf("inserted data ")
	if err != nil {
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	id, err := result.LastInsertId()
	if err != nil {
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	if err = tx.Commit(); err != nil {
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}

	return id, nil
}
func publishToPubSub(TrackProgress trackProgress) error {
	var projectID = os.Getenv("music_stream_project_id")
	var topicID = os.Getenv("music_stream_topic_id")
	ctx := context.Background()
	client, err := pubsub.NewClient(ctx, projectID)
	if err != nil {
		return fmt.Errorf("pubsub.NewClient: %w", err)
	}
	avroSource, err := os.ReadFile("schema.avsc")
	if err != nil {
		return fmt.Errorf("os.ReadFile err: %w", err)
	}
	codec, err := goavro.NewCodec(string(avroSource))
	if err != nil {
		return fmt.Errorf("goavro.NewCodec err: %w", err)
	}

	record := map[string]interface{}{"fingerprint": TrackProgress.FingerPrint, "uid": TrackProgress.UID, "event": "", "startTime": "", "endTime": "", "eventTime": "", "title": TrackProgress.Title, "uri": "", "albumArtist": "", "artist": TrackProgress.Artist, "album": TrackProgress.Album, "progressPercent": TrackProgress.ProgressPercent}
	t := client.Topic(topicID)
	cfg, err := t.Config(ctx)
	if err != nil {
		return fmt.Errorf("topic.Config err: %w", err)
	}
	encoding := cfg.SchemaSettings.Encoding

	var msg []byte
	switch encoding {
	case pubsub.EncodingBinary:
		msg, err = codec.BinaryFromNative(nil, record)
		if err != nil {
			return fmt.Errorf("codec.BinaryFromNative err: %w", err)
		}
	case pubsub.EncodingJSON:
		msg, err = codec.TextualFromNative(nil, record)
		if err != nil {
			return fmt.Errorf("codec.TextualFromNative err: %w", err)
		}
	default:
		return fmt.Errorf("invalid encoding: %v", encoding)
	}

	result := t.Publish(ctx, &pubsub.Message{
		Data: msg,
	})
	_, err = result.Get(ctx)
	if err != nil {
		return fmt.Errorf("result.Get: %w", err)
	}
	// fmt.Fprintf( "Published avro record: %s\n", string(msg))
	return nil

}

func addProgress(c *gin.Context) {
	var trackProgress trackProgress
	if err := c.BindJSON(&trackProgress); err != nil {
		return
	}
	publishToPubSub(trackProgress)
	// writeToFile(trackProgress)
	// ctx := context.TODO()
	// saveProgressToDB(ctx, trackProgress)
	c.IndentedJSON(http.StatusCreated, trackProgress)
}

func writeToFile(TrackProgress trackProgress) {
	var directory = os.Getenv("base_directory")
	t := time.Now()
	var fileName = t.Format("20060102150405")
	var file = directory + fileName + ".log"
	fmt.Println(file)
	b, err := json.Marshal(TrackProgress)
	if err != nil {
		fmt.Println(err)
		return
	}
	os.WriteFile(file, b, 0644)
}

func main() {
	router := gin.Default()
	// connectToDB()
	router.POST("/progress", addProgress)
	router.Run(":8080")
}
