-- ============================================================
-- HeatWave AutoML — predict progressPercent
-- Source: music_play_progress
-- ============================================================

-- ------------------------------------------------------------
-- Step 1: Create a training view
-- Drops raw IDs, timestamps, and infrastructure columns that
-- carry no signal for predicting how much of a track is played.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW `music_progress_training_v` AS
SELECT
    -- user & context
    `uid`,
    `bluetoothDevice`,
    `event`,

    -- time features derived from publish_time
    HOUR(CAST(`publish_time` AS DATETIME))        AS `publish_hour`,
    DAYOFWEEK(CAST(`publish_time` AS DATETIME))   AS `publish_day_of_week`,
    -- DAYOFWEEK: 1=Sunday, 2=Monday, ..., 7=Saturday

    -- track identity / metadata
    `title`,
    `artist`,
    `albumArtist`,
    `album`,
    `year`,
    `track`,
    `disc`,
    `genre`,
    `composer`,
    `bpm`,
    `duration_seconds`,
    `bitrate_kbps`,
    `sample_rate_hz`,
    `channels`,
    `has_cover_art`,
    `publisher`,
    `music_key`,
    `language`,
    `original_year`,

    -- one-hot genre flags
    `is_genre_electronic`,
    `is_genre_ambient`,
    `is_genre_classical`,
    `is_genre_jazz`,
    `is_genre_blues`,
    `is_genre_rock`,
    `is_genre_pop`,
    `is_genre_hiphop`,
    `is_genre_dance`,
    `is_genre_house`,
    `is_genre_trance`,
    `is_genre_drum_and_bass`,
    `is_genre_country`,
    `is_genre_reggae`,
    `is_genre_rnb`,
    `is_genre_folk_country`,
    `is_genre_alternative`,

    -- one-hot mood flags
    `mood_acoustic`,
    `mood_aggressive`,
    `mood_electronic`,
    `mood_happy`,
    `mood_party`,
    `mood_relaxed`,
    `mood_sad`,

    -- target
    `progressPercent`

FROM `music_play_progress`
WHERE `progressPercent` IS NOT NULL;


-- ------------------------------------------------------------
-- Step 2: Load the view's backing table into HeatWave
-- (HeatWave resolves the view to its base table at runtime)
-- ------------------------------------------------------------
ALTER TABLE `music_play_progress` SECONDARY_ENGINE = RAPID;
ALTER TABLE `music_play_progress` SECONDARY_LOAD;


-- ------------------------------------------------------------
-- Step 3: Train — AutoML regression on progressPercent
-- ------------------------------------------------------------
CALL sys.ML_TRAIN(
    'musick.music_progress_training_v',
    'progressPercent',
    JSON_OBJECT(
        'task',            'regression',
        'optimization_metric', 'rmse'
    ),
    @model_handle
);

SELECT @model_handle;   -- save this value; you'll need it to reload the model later


-- ------------------------------------------------------------
-- Step 4: Load the trained model into HeatWave memory
-- ------------------------------------------------------------
CALL sys.ML_MODEL_LOAD(@model_handle, NULL);


-- ------------------------------------------------------------
-- Step 5: Score the model on the training data
-- ------------------------------------------------------------
CALL sys.ML_SCORE(
    'musick.music_progress_training_v',
    'progressPercent',
    @model_handle,
    'rmse',
    @score,
    NULL
);
SELECT @score AS rmse_score;


-- ------------------------------------------------------------
-- Step 6: Explain — see which features matter most
-- ------------------------------------------------------------
CALL sys.ML_EXPLAIN(
    'musick.music_progress_training_v',
    'progressPercent',
    @model_handle,
    JSON_OBJECT('prediction_explainer', 'permutation_importance'),
    NULL
);


-- ------------------------------------------------------------
-- Step 7: Predict on the full table and write to a new table
-- ------------------------------------------------------------
CALL sys.ML_PREDICT_TABLE(
    'musick.music_progress_training_v',
    @model_handle,
    'musick.music_progress_predictions',
    NULL
);

-- Preview predictions alongside actuals
SELECT
    t.`title`,
    t.`artist`,
    t.`progressPercent`  AS actual,
    p.`ml_results`       AS predicted_json
FROM `music_play_progress` t
JOIN `music_progress_predictions` p
  ON t.`title`  = p.`title`
 AND t.`artist` = p.`artist`
LIMIT 20;
