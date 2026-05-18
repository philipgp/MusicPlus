package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"log/slog"
	"net/http"
	"os"
	"time"

	"cloud.google.com/go/pubsub"
	"github.com/gin-gonic/gin"
	"github.com/go-sql-driver/mysql"
	"github.com/linkedin/goavro/v2"
	"go.opentelemetry.io/contrib/instrumentation/github.com/gin-gonic/gin/otelgin"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/codes"
)

var db *sql.DB

var tracer = otel.Tracer("click-stream-processor")

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
	BluetoothDevice string `json:"bluetoothDevice"`
}

func connectToDB() {
	cfg := mysql.Config{
		User:                 os.Getenv("MY_SQL_USERNAME"),
		Passwd:               os.Getenv("MY_SQL_PASSWORD"),
		Net:                  "tcp",
		Addr:                 os.Getenv("MY_SQL_URL"),
		DBName:               "musick",
		AllowNativePasswords: true,
	}
	var err error
	db, err = sql.Open("mysql", cfg.FormatDSN())
	if err != nil {
		slog.Error("failed to open database", "error", err)
		os.Exit(1)
	}
	if err = db.Ping(); err != nil {
		slog.Error("database ping failed", "error", err)
		os.Exit(1)
	}
	slog.Info("database connected", "addr", os.Getenv("MY_SQL_URL"), "db", "musick")
}

func saveProgressToDB(ctx context.Context, tp trackProgress) (int64, error) {
	ctx, span := tracer.Start(ctx, "db.save_progress")
	defer span.End()
	span.SetAttributes(
		attribute.String("db.table", "track_play_progress"),
		attribute.String("track.uid", tp.UID),
		attribute.String("track.title", tp.Title),
	)

	slog.InfoContext(ctx, "saveProgressToDB: beginning transaction")
	tx, err := db.BeginTx(ctx, nil)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	defer tx.Rollback()

	slog.InfoContext(ctx, "saveProgressToDB: executing insert", "uid", tp.UID, "title", tp.Title)
	queryStart := time.Now()
	result, err := db.ExecContext(ctx,
		"INSERT INTO track_play_progress (uid,fingerprint,progressPercent,album,artist,title,albumArtist) VALUES (?, ?, ?,?,?,?,?)",
		tp.UID, tp.FingerPrint, tp.ProgressPercent, tp.Album, tp.Artist, tp.Title, tp.AlbumArtist,
	)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		slog.ErrorContext(ctx, "saveProgressToDB: insert failed", "error", err, "duration_ms", time.Since(queryStart).Milliseconds())
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	slog.InfoContext(ctx, "saveProgressToDB: insert succeeded", "duration_ms", time.Since(queryStart).Milliseconds())

	id, err := result.LastInsertId()
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	slog.InfoContext(ctx, "saveProgressToDB: committing transaction", "id", id)
	if err = tx.Commit(); err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		slog.ErrorContext(ctx, "saveProgressToDB: commit failed", "error", err)
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}

	slog.InfoContext(ctx, "saveProgressToDB: completed", "id", id, "uid", tp.UID, "title", tp.Title)
	return id, nil
}

func publishToPubSub(ctx context.Context, tp trackProgress) error {
	ctx, span := tracer.Start(ctx, "pubsub.publish")
	defer span.End()

	projectID := os.Getenv("music_stream_project_id")
	topicID := os.Getenv("music_stream_topic_id")
	span.SetAttributes(
		attribute.String("messaging.system", "pubsub"),
		attribute.String("messaging.destination", topicID),
		attribute.String("track.title", tp.Title),
		attribute.String("track.artist", tp.Artist),
		attribute.String("track.uid", tp.UID),
	)

	slog.InfoContext(ctx, "publishToPubSub: starting",
		"project_id", projectID,
		"topic_id", topicID,
		"title", tp.Title,
		"uid", tp.UID,
	)

	if projectID == "" {
		slog.ErrorContext(ctx, "publishToPubSub: music_stream_project_id env var is empty")
		return fmt.Errorf("music_stream_project_id is not set")
	}
	if topicID == "" {
		slog.ErrorContext(ctx, "publishToPubSub: music_stream_topic_id env var is empty")
		return fmt.Errorf("music_stream_topic_id is not set")
	}

	slog.InfoContext(ctx, "publishToPubSub: creating pubsub client")
	client, err := pubsub.NewClient(ctx, projectID)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return fmt.Errorf("pubsub.NewClient: %w", err)
	}
	defer client.Close()
	slog.InfoContext(ctx, "publishToPubSub: pubsub client created")

	avroSource, err := os.ReadFile("schema.avsc")
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return fmt.Errorf("os.ReadFile err: %w", err)
	}
	slog.InfoContext(ctx, "publishToPubSub: avro schema loaded")

	codec, err := goavro.NewCodec(string(avroSource))
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return fmt.Errorf("goavro.NewCodec err: %w", err)
	}
	slog.InfoContext(ctx, "publishToPubSub: avro codec created")

	record := map[string]interface{}{
		"fingerprint":     tp.FingerPrint,
		"uid":             tp.UID,
		"event":           "",
		"startTime":       "",
		"endTime":         "",
		"eventTime":       "",
		"title":           tp.Title,
		"uri":             "",
		"albumArtist":     "",
		"artist":          tp.Artist,
		"album":           tp.Album,
		"progressPercent": tp.ProgressPercent,
		"bluetoothDevice": tp.BluetoothDevice,
	}

	t := client.Topic(topicID)
	slog.InfoContext(ctx, "publishToPubSub: fetching topic config")
	cfg, err := t.Config(ctx)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return fmt.Errorf("topic.Config err: %w", err)
	}
	slog.InfoContext(ctx, "publishToPubSub: topic config fetched", "encoding", cfg.SchemaSettings.Encoding)

	var msg []byte
	switch cfg.SchemaSettings.Encoding {
	case pubsub.EncodingBinary:
		msg, err = codec.BinaryFromNative(nil, record)
	case pubsub.EncodingJSON:
		msg, err = codec.TextualFromNative(nil, record)
	default:
		err = fmt.Errorf("invalid encoding: %v", cfg.SchemaSettings.Encoding)
	}
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return err
	}
	slog.InfoContext(ctx, "publishToPubSub: message encoded", "size_bytes", len(msg))

	slog.InfoContext(ctx, "publishToPubSub: publishing message")
	result := t.Publish(ctx, &pubsub.Message{Data: msg})
	msgID, err := result.Get(ctx)
	if err != nil {
		span.RecordError(err)
		span.SetStatus(codes.Error, err.Error())
		return fmt.Errorf("result.Get: %w", err)
	}

	slog.InfoContext(ctx, "publishToPubSub: published successfully", "message_id", msgID, "topic", topicID)
	return nil
}

func addProgress(c *gin.Context) {
	ctx := c.Request.Context()
	start := time.Now()
	slog.InfoContext(ctx, "addProgress: request received")

	var tp trackProgress
	if err := c.BindJSON(&tp); err != nil {
		slog.WarnContext(ctx, "addProgress: failed to parse request body", "error", err)
		return
	}

	slog.InfoContext(ctx, "addProgress: parsed request body",
		"uid", tp.UID,
		"title", tp.Title,
		"artist", tp.Artist,
		"progress_percent", tp.ProgressPercent,
		"bluetooth_device", tp.BluetoothDevice,
	)

	slog.InfoContext(ctx, "addProgress: calling publishToPubSub")
	pubStart := time.Now()
	if err := publishToPubSub(ctx, tp); err != nil {
		slog.ErrorContext(ctx, "addProgress: publishToPubSub failed",
			"error", err,
			"duration_ms", time.Since(pubStart).Milliseconds(),
		)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	slog.InfoContext(ctx, "addProgress: publishToPubSub succeeded", "duration_ms", time.Since(pubStart).Milliseconds())

	slog.InfoContext(ctx, "addProgress: request completed", "total_duration_ms", time.Since(start).Milliseconds())
	c.IndentedJSON(http.StatusCreated, tp)
}

func writeToFile(tp trackProgress) {
	directory := os.Getenv("base_directory")
	fileName := time.Now().Format("20060102150405")
	file := directory + fileName + ".log"

	b, err := json.Marshal(tp)
	if err != nil {
		slog.Error("failed to marshal track progress", "error", err)
		return
	}
	if err := os.WriteFile(file, b, 0644); err != nil {
		slog.Error("failed to write file", "file", file, "error", err)
		return
	}
	slog.Info("wrote progress to file", "file", file)
}

func playlist_getAll(c *gin.Context) {
	playlists, err := getAll()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"items": playlists})
}

func playlist_clear(c *gin.Context) {
	uid := c.Param("uid")
	if err := clear(uid); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"cleared": uid})
}
func playlist_getM3U(c *gin.Context) {
	uid := c.Param("uid")

	playlistDetail, err := getPlaylistDetail(uid)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	playlist_items, err := getPlaylistItems(uid)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	m3u, err := Generate(playlistDetail, playlist_items)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.String(http.StatusOK, m3u)
}

func playlist_addSong(c *gin.Context) {
	uid := c.Param("uid")       // path parameter
	title := c.Query("title")   // ?limit=50
	artist := c.Query("artist") // ?shuffle=true
	playlist := Playlist{UID: uid}
	track := Track{Artist: artist, Title: title}
	added, err := add(playlist, track)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"added": added})
}

func main() {
	connectToDB()
	ctx := context.Background()
	shutdown := setupTelemetry(ctx)
	defer shutdown(ctx)

	slog.Info("starting click-stream-processor")

	router := gin.Default()
	router.Use(otelgin.Middleware(serviceName()))
	router.POST("/progress", addProgress)
	router.GET("/playlist/get", playlist_getAll)
	router.GET("/playlist/m3u/:uid", playlist_getM3U)
	router.POST("/playlist/:uid/song", playlist_addSong)
	router.DELETE("/playlist/:uid/songs", playlist_clear)
	router.Run(":8080")
}
