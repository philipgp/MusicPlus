-- Copies computed columns from `tracks` into `music_play_progress`,
-- matching rows where title AND artist are equal.

UPDATE `music_play_progress` AS `t`
  JOIN `tracks` AS `s`
    ON `t`.`title`  = `s`.`title`
   AND `t`.`artist` = `s`.`artist`
SET
    `t`.`is_genre_electronic`   = `s`.`is_genre_electronic`,
    `t`.`is_genre_ambient`      = `s`.`is_genre_ambient`,
    `t`.`is_genre_classical`    = `s`.`is_genre_classical`,
    `t`.`is_genre_jazz`         = `s`.`is_genre_jazz`,
    `t`.`is_genre_blues`        = `s`.`is_genre_blues`,
    `t`.`is_genre_rock`         = `s`.`is_genre_rock`,
    `t`.`is_genre_pop`          = `s`.`is_genre_pop`,
    `t`.`is_genre_hiphop`       = `s`.`is_genre_hiphop`,
    `t`.`is_genre_dance`        = `s`.`is_genre_dance`,
    `t`.`is_genre_house`        = `s`.`is_genre_house`,
    `t`.`is_genre_trance`       = `s`.`is_genre_trance`,
    `t`.`is_genre_drum_and_bass`= `s`.`is_genre_drum_and_bass`,
    `t`.`is_genre_country`      = `s`.`is_genre_country`,
    `t`.`is_genre_reggae`       = `s`.`is_genre_reggae`,
    `t`.`is_genre_rnb`          = `s`.`is_genre_rnb`,
    `t`.`is_genre_folk_country` = `s`.`is_genre_folk_country`,
    `t`.`is_genre_alternative`  = `s`.`is_genre_alternative`,
    `t`.`mood_acoustic`         = `s`.`mood_acoustic`,
    `t`.`mood_aggressive`       = `s`.`mood_aggressive`,
    `t`.`mood_electronic`       = `s`.`mood_electronic`,
    `t`.`mood_happy`            = `s`.`mood_happy`,
    `t`.`mood_party`            = `s`.`mood_party`,
    `t`.`mood_relaxed`          = `s`.`mood_relaxed`,
    `t`.`mood_sad`              = `s`.`mood_sad`,
    `t`.`mb_artist_id_1`        = `s`.`mb_artist_id_1`,
    `t`.`mb_artist_id_2`        = `s`.`mb_artist_id_2`,
    `t`.`mb_artist_id_3`        = `s`.`mb_artist_id_3`,
    `t`.`mb_artist_id_4`        = `s`.`mb_artist_id_4`,
    `t`.`mb_album_artist_id_1`  = `s`.`mb_album_artist_id_1`,
    `t`.`mb_album_artist_id_2`  = `s`.`mb_album_artist_id_2`,
    `t`.`mb_album_artist_id_3`  = `s`.`mb_album_artist_id_3`,
    `t`.`mb_album_artist_id_4`  = `s`.`mb_album_artist_id_4`;
