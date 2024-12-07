package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"

	"github.com/gin-gonic/gin"
)

type trackProgress struct {
	FingerPrint     string `json:"fingerprint"`
	UID             string `json:"uid"`
	ProgressPercent int    `json:"progressPercent"`
	Event           string `json:"event"`
	StartTime       string `json:"startTime"`
	EndTime         string `json:"endTime"`
	EventTime       string `json:"eventTime"`
}

func addProgress(c *gin.Context) {
	var trackProgress trackProgress
	if err := c.BindJSON(&trackProgress); err != nil {
		return
	}
	writeToFile(trackProgress)
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
	router.POST("/progress", addProgress)
	router.Run(":8080")
}
