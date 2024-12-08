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

	"github.com/gin-gonic/gin"
	"github.com/go-sql-driver/mysql"
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
}

func connectToDB() {
	// Capture connection properties.
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
		return 0, fmt.Errorf("saveProgressToDB: %v", err)
	}
	defer tx.Rollback()
	// log.Fatalf("Inserting data")
	result, err := db.Exec("INSERT INTO track_play_progress (uid,fingerprint,progressPercent) VALUES (?, ?, ?)", TrackProgress.UID, TrackProgress.FingerPrint, TrackProgress.ProgressPercent)
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

func addProgress(c *gin.Context) {
	var trackProgress trackProgress
	if err := c.BindJSON(&trackProgress); err != nil {
		return
	}
	writeToFile(trackProgress)
	ctx := context.TODO()
	saveProgressToDB(ctx, trackProgress)
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
	connectToDB()
	router.POST("/progress", addProgress)
	router.Run(":8080")
}
