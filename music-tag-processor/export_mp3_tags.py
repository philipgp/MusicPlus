#!/usr/bin/env python3
"""Recursively scan a directory for MP3 files, extract ID3 tags, and export to JSON."""

import argparse
import json
import re
import sys
from pathlib import Path

try:
    from mutagen.mp3 import MP3
    from mutagen.id3 import ID3NoHeaderError
except ImportError:
    sys.exit("mutagen is required: pip install mutagen")


# Well-known frame ID -> friendly name for top-level fields
KNOWN_FRAMES = {
    "TIT2": "title",
    "TPE1": "artist",
    "TPE2": "album_artist",
    "TALB": "album",
    "TDRC": "year",
    "TRCK": "track",
    "TPOS": "disc",
    "TCON": "genre",
    "TCOM": "composer",
    "TLEN": "length_ms",
    "TBPM": "bpm",
    "COMM": "comment",
    "USLT": "lyrics",
    "APIC": "has_cover_art",
}

# Multi-value separators in ID3 (null byte and semicolon)
_SPLIT_RE = re.compile(r"[\x00;]+")


def _split_value(raw: str) -> list[str] | str:
    parts = [p.strip() for p in _SPLIT_RE.split(raw) if p.strip()]
    return parts if len(parts) > 1 else (parts[0] if parts else raw)


def _frame_to_value(frame_id: str, frame) -> object:
    if frame_id.startswith("APIC"):
        return True
    if hasattr(frame, "text"):
        # text frames: list of text values
        raw = "\x00".join(str(t) for t in frame.text)
        return _split_value(raw)
    if hasattr(frame, "url"):
        return str(frame.url)
    return str(frame)


def extract_tags(path: Path) -> dict:
    try:
        audio = MP3(path)
    except Exception as e:
        return {"path": str(path), "error": str(e)}

    tags = {
        "path": str(path),
        "duration_seconds": round(audio.info.length, 2),
        "bitrate_kbps": audio.info.bitrate // 1000,
        "sample_rate_hz": audio.info.sample_rate,
        "channels": audio.info.channels,
    }

    if not audio.tags:
        tags["has_cover_art"] = False
        return tags

    # Well-known frames -> friendly top-level keys
    for frame_id, field in KNOWN_FRAMES.items():
        # COMM/USLT/APIC can appear multiple times with different descriptions
        matched = [k for k in audio.tags.keys() if k == frame_id or k.startswith(frame_id + ":")]
        if not matched:
            continue
        frame = audio.tags[matched[0]]
        tags[field] = _frame_to_value(frame_id, frame)

    tags.setdefault("has_cover_art", False)

    # Extended tags: everything not already captured above
    covered_prefixes = tuple(KNOWN_FRAMES.keys())
    extended = {}
    for key, frame in audio.tags.items():
        frame_id = key.split(":")[0]
        if frame_id in covered_prefixes:
            continue
        value = _frame_to_value(frame_id, frame)
        # Use the full key (e.g. "TXXX:replaygain_track_gain") as the field name
        extended[key] = value

    if extended:
        tags["extended_tags"] = extended

    return tags


def scan_directory(root: Path) -> list[dict]:
    results = []
    for mp3_path in sorted(root.rglob("*.mp3")):
        results.append(extract_tags(mp3_path))
    return results


def main():
    parser = argparse.ArgumentParser(description="Export MP3 tags from a directory to JSON.")
    parser.add_argument("directory", help="Root directory to scan")
    parser.add_argument("-o", "--output", default="mp3_tags.json", help="Output JSON file (default: mp3_tags.json)")
    parser.add_argument("--indent", type=int, default=2, help="JSON indentation (default: 2)")
    args = parser.parse_args()

    root = Path(args.directory).expanduser().resolve()
    if not root.is_dir():
        sys.exit(f"Not a directory: {root}")

    print(f"Scanning {root} ...")
    tracks = scan_directory(root)
    print(f"Found {len(tracks)} MP3 file(s).")

    output_path = Path(args.output)
    payload = {"source_directory": str(root), "track_count": len(tracks), "tracks": tracks}
    output_path.write_text(json.dumps(payload, indent=args.indent, ensure_ascii=False))
    print(f"Exported to {output_path}")


if __name__ == "__main__":
    main()
