#!/usr/bin/env python3
"""Generate a MySQL UPDATE script that sets the computed columns
(one-hot genre/mood flags and multi-value MusicBrainz ID columns)
for rows already present in the tracks table, matched by path.
"""

import argparse
import json
import sys
from pathlib import Path

import json_to_db as m


def _escape(val) -> str:
    if val is None:
        return "NULL"
    s = str(val).replace("\\", "\\\\").replace("'", "\\'")
    return f"'{s}'"


def build_set_clause(row: dict) -> str:
    parts = []

    for _, col in m.GENRE_ONE_HOT:
        parts.append(f"`{col}` = {row[col]}")

    for _, col in m.MOOD_ONE_HOT:
        parts.append(f"`{col}` = {row[col]}")

    for _, prefix, max_n in m.MULTI_ID_FIELDS:
        for i in range(1, max_n + 1):
            col = f"{prefix}_{i}"
            parts.append(f"`{col}` = {_escape(row.get(col))}")

    return ",\n    ".join(parts)


def main():
    parser = argparse.ArgumentParser(description="Generate UPDATE SQL for computed columns.")
    parser.add_argument("json_file", help="Path to mp3_tags.json")
    parser.add_argument("-o", "--output", default="music_update.sql", help="Output SQL file (default: music_update.sql)")
    args = parser.parse_args()

    json_path = Path(args.json_file)
    if not json_path.exists():
        sys.exit(f"File not found: {json_path}")

    print(f"Reading {json_path} ...")
    data = json.loads(json_path.read_text())
    tracks = data["tracks"]

    output_path = Path(args.output)
    written = skipped = 0

    with output_path.open("w", encoding="utf-8") as f:
        f.write("SET NAMES utf8mb4;\n\n")

        for track in tracks:
            if "error" in track:
                skipped += 1
                continue

            row = m.record_to_row(track)
            path_val = _escape(track.get("path"))
            set_clause = build_set_clause(row)

            f.write(
                f"UPDATE `{m.TABLE_NAME}` SET\n"
                f"    {set_clause}\n"
                f"  WHERE `path` = {path_val};\n\n"
            )
            written += 1

    print(f"Done. {written} UPDATE statements written, {skipped} skipped.")
    print(f"Output: {output_path.resolve()}")


if __name__ == "__main__":
    main()
