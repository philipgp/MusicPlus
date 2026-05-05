#!/usr/bin/env python3
"""Read mp3_tags.json and generate a MySQL DDL/DML .sql script.
TXXX:ab:genre and TXXX:ab:mood are one-hot encoded into boolean columns.
"""

import argparse
import json
import sys
from pathlib import Path

# ---------------------------------------------------------------------------
# Mapping: (source_key_path, dest_column, mysql_type)
# source_key_path supports dot notation: "extended_tags.TXXX:WORK"
# ---------------------------------------------------------------------------
FIELD_MAP: list[tuple[str, str, str]] = [
    ("path",                                  "path",               "VARCHAR(1024)"),
    ("title",                                 "title",              "VARCHAR(512)"),
    ("artist",                                "artist",             "VARCHAR(512)"),
    ("album_artist",                          "album_artist",       "VARCHAR(512)"),
    ("album",                                 "album",              "VARCHAR(512)"),
    ("year",                                  "year",               "VARCHAR(32)"),
    ("track",                                 "track",              "VARCHAR(16)"),
    ("disc",                                  "disc",               "VARCHAR(16)"),
    ("genre",                                 "genre",              "VARCHAR(256)"),
    ("composer",                              "composer",           "VARCHAR(512)"),
    ("bpm",                                   "bpm",                "VARCHAR(16)"),
    ("comment",                               "comment",            "TEXT"),
    ("duration_seconds",                      "duration_seconds",   "FLOAT"),
    ("bitrate_kbps",                          "bitrate_kbps",       "INT"),
    ("sample_rate_hz",                        "sample_rate_hz",     "INT"),
    ("channels",                              "channels",           "INT"),
    ("has_cover_art",                         "has_cover_art",      "TINYINT(1)"),
    ("extended_tags.TPUB",                    "publisher",          "VARCHAR(256)"),
    ("extended_tags.TKEY",                    "music_key",          "VARCHAR(16)"),
    ("extended_tags.TLAN",                    "language",           "VARCHAR(64)"),
    ("extended_tags.TENC",                    "encoded_by",         "VARCHAR(256)"),
    ("extended_tags.TPE3",                    "conductor",          "VARCHAR(256)"),
    ("extended_tags.TXXX:originalyear",       "original_year",      "VARCHAR(16)"),
    ("extended_tags.TXXX:WORK",               "work",               "VARCHAR(512)"),
    ("extended_tags.TXXX:ARTISTS",            "all_artists",        "TEXT"),
    ("extended_tags.TXXX:Acoustid Id",        "acoustid",           "VARCHAR(64)"),
    ("extended_tags.TXXX:MusicBrainz Album Id",          "mb_album_id",        "VARCHAR(64)"),
    ("extended_tags.TXXX:MusicBrainz Artist Id",         "_mb_artist_id",      None),   # expanded below
    ("extended_tags.TXXX:MusicBrainz Album Artist Id",   "_mb_album_artist_id",None),   # expanded below
    ("extended_tags.TXXX:MusicBrainz Release Track Id",  "mb_track_id",        "VARCHAR(64)"),
    ("extended_tags.TXXX:MusicBrainz Release Group Id",  "mb_release_group_id","VARCHAR(64)"),
    ("extended_tags.TXXX:ab:genre",           "_ab_genre",          None),    # internal; one-hot below
    ("extended_tags.TXXX:ab:mood",            "_ab_mood",           None),    # internal; one-hot below
]

# One-hot columns: (source_internal_field, value_to_match, dest_column)
# For mood, match the positive label (ignore "Not X" variants)
GENRE_ONE_HOT: list[tuple[str, str]] = [
    ("Electronic",       "is_genre_electronic"),
    ("Ambient",          "is_genre_ambient"),
    ("Classical",        "is_genre_classical"),
    ("Jazz",             "is_genre_jazz"),
    ("Blues",            "is_genre_blues"),
    ("Rock",             "is_genre_rock"),
    ("Pop",              "is_genre_pop"),
    ("Hiphop",           "is_genre_hiphop"),
    ("Dance",            "is_genre_dance"),
    ("House",            "is_genre_house"),
    ("Trance",           "is_genre_trance"),
    ("Drum and Bass",    "is_genre_drum_and_bass"),
    ("Country",          "is_genre_country"),
    ("Reggae",           "is_genre_reggae"),
    ("Rhythm and Blues", "is_genre_rnb"),
    ("Folk/Country",     "is_genre_folk_country"),
    ("Alternative",      "is_genre_alternative"),
]

MOOD_ONE_HOT: list[tuple[str, str]] = [
    ("Acoustic",    "mood_acoustic"),
    ("Aggressive",  "mood_aggressive"),
    ("Electronic",  "mood_electronic"),
    ("Happy",       "mood_happy"),
    ("Party",       "mood_party"),
    ("Relaxed",     "mood_relaxed"),
    ("Sad",         "mood_sad"),
]

# Multi-value ID fields: (source_dotpath, dest_column_prefix, max_slots)
MULTI_ID_FIELDS: list[tuple[str, str, int]] = [
    ("extended_tags.TXXX:MusicBrainz Artist Id",       "mb_artist_id",       4),
    ("extended_tags.TXXX:MusicBrainz Album Artist Id", "mb_album_artist_id", 4),
]

TABLE_NAME = "tracks"


def _get(record: dict, dotpath: str):
    """Resolve a dot-separated key path against a nested dict."""
    parts = dotpath.split(".", 1)
    val = record.get(parts[0])
    if len(parts) == 1 or val is None:
        return val
    return _get(val, parts[1]) if isinstance(val, dict) else None


def _to_str(val) -> str | None:
    if val is None:
        return None
    if isinstance(val, list):
        return ", ".join(str(v) for v in val)
    return str(val)


def _as_list(val) -> list[str]:
    if val is None:
        return []
    if isinstance(val, list):
        return [str(v) for v in val]
    return [str(val)]


def build_schema() -> str:
    cols = ["id INT AUTO_INCREMENT PRIMARY KEY"]
    for src, dest, dtype in FIELD_MAP:
        if dtype is None:
            continue
        cols.append(f"`{dest}` {dtype}")
    for src, prefix, max_n in MULTI_ID_FIELDS:
        for i in range(1, max_n + 1):
            cols.append(f"`{prefix}_{i}` VARCHAR(64)")
    for _, col in GENRE_ONE_HOT:
        cols.append(f"`{col}` TINYINT(1) DEFAULT 0")
    for _, col in MOOD_ONE_HOT:
        cols.append(f"`{col}` TINYINT(1) DEFAULT 0")
    return f"CREATE TABLE IF NOT EXISTS `{TABLE_NAME}` (\n  " + ",\n  ".join(cols) + "\n) CHARACTER SET utf8mb4"


def record_to_row(track: dict) -> dict:
    row: dict = {}

    for src, dest, dtype in FIELD_MAP:
        if dtype is None:
            continue
        val = _get(track, src)
        if dtype in ("INT", "TINYINT(1)"):
            row[dest] = int(val) if val is not None else None
        elif dtype == "FLOAT":
            row[dest] = float(val) if val is not None else None
        else:
            row[dest] = _to_str(val)

    # multi-value ID fields -> numbered columns
    for src, prefix, max_n in MULTI_ID_FIELDS:
        values = _as_list(_get(track, src))[:max_n]
        for i in range(1, max_n + 1):
            row[f"{prefix}_{i}"] = values[i - 1] if i <= len(values) else None

    # one-hot: ab:genre
    ab_genre = _as_list(_get(track, "extended_tags.TXXX:ab:genre"))
    for value, col in GENRE_ONE_HOT:
        row[col] = 1 if value in ab_genre else 0

    # one-hot: ab:mood (positive label only; "Not X" entries are ignored)
    ab_mood = _as_list(_get(track, "extended_tags.TXXX:ab:mood"))
    for value, col in MOOD_ONE_HOT:
        row[col] = 1 if value in ab_mood else 0

    return row


def _escape(val) -> str:
    if val is None:
        return "NULL"
    s = str(val).replace("\\", "\\\\").replace("'", "\\'")
    return f"'{s}'"


def _escape_val(val, dtype: str) -> str:
    if val is None:
        return "NULL"
    if dtype in ("INT", "TINYINT(1)", "FLOAT"):
        return str(val)
    return _escape(val)


def main():
    parser = argparse.ArgumentParser(description="Generate MySQL DDL/DML script from mp3_tags.json")
    parser.add_argument("json_file", help="Path to mp3_tags.json")
    parser.add_argument("-o", "--output", default="music.sql", help="Output SQL file (default: music.sql)")
    parser.add_argument("--drop", action="store_true", help="Include DROP TABLE before CREATE TABLE")
    args = parser.parse_args()

    json_path = Path(args.json_file)
    if not json_path.exists():
        sys.exit(f"File not found: {json_path}")

    print(f"Reading {json_path} ...")
    data = json.loads(json_path.read_text())
    tracks = data["tracks"]

    columns = [dest for src, dest, dtype in FIELD_MAP if dtype is not None]
    for src, prefix, max_n in MULTI_ID_FIELDS:
        columns += [f"{prefix}_{i}" for i in range(1, max_n + 1)]
    columns += [col for _, col in GENRE_ONE_HOT]
    columns += [col for _, col in MOOD_ONE_HOT]
    col_list = ", ".join(f"`{c}`" for c in columns)

    dtype_map = {dest: dtype for src, dest, dtype in FIELD_MAP if dtype is not None}
    for src, prefix, max_n in MULTI_ID_FIELDS:
        dtype_map.update({f"{prefix}_{i}": "VARCHAR(64)" for i in range(1, max_n + 1)})
    dtype_map.update({col: "TINYINT(1)" for _, col in GENRE_ONE_HOT})
    dtype_map.update({col: "TINYINT(1)" for _, col in MOOD_ONE_HOT})

    output_path = Path(args.output)
    inserted = skipped = 0

    with output_path.open("w", encoding="utf-8") as f:
        f.write("SET NAMES utf8mb4;\n\n")

        if args.drop:
            f.write(f"DROP TABLE IF EXISTS `{TABLE_NAME}`;\n\n")

        f.write(build_schema() + ";\n\n")

        for track in tracks:
            if "error" in track:
                skipped += 1
                continue
            row = record_to_row(track)
            values = ", ".join(_escape_val(row.get(c), dtype_map[c]) for c in columns)
            f.write(f"INSERT INTO `{TABLE_NAME}` ({col_list}) VALUES ({values});\n")
            inserted += 1

    print(f"Done. {inserted} INSERT statements written, {skipped} skipped.")
    print(f"Output: {output_path.resolve()}")


if __name__ == "__main__":
    main()
