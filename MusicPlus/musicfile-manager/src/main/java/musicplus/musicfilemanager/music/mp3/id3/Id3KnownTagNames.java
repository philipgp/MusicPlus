package musicplus.musicfilemanager.music.mp3.id3;

public enum Id3KnownTagNames {
	COMMENTS,PLAY_COUNTER,ALBUM,BEATS_PER_MINUTE,
	COMPOSER,CONTENT_TYPE,LYRICIST,TITLE,LENGTH,
	ARTIST,BAND,CONDUCTOR,TRACK_NUMBER,RECORDING_DATE,
	YEAR,GRACENOTE_ID,MOOD,GENRE;
	
	public String getCodes(ID3Version id3Version){
		if(id3Version ==ID3Version.ID3V2_3)
			return getCodesForId3v23();
		else if(id3Version ==ID3Version.ID3V2_4)
			return getCodesForId3v24();
		else if(id3Version ==ID3Version.ID3V2_2)
			return getCodesForId3v22();
		else
			return null;
	}
	
	private String getCodesForId3v24(){
		switch (this) {
		case COMMENTS:
			return "COMM";
		case PLAY_COUNTER:
			return "PCNT";
		case ALBUM:
			return "TALB";
		case BEATS_PER_MINUTE:
			return "TBPM";
		case COMPOSER:
			return "TCOM";
		case CONTENT_TYPE:
			return "TCON";
		case LYRICIST:
			return "TEXT";
		case MOOD:
			return "TMOO";
		case TITLE:
			return "TIT2";
		case LENGTH:
			return "TLEN";
		case ARTIST:
			return "TPE1";
		case BAND:
			return "TPE2";
		case CONDUCTOR:
			return "TPE3";
		case TRACK_NUMBER:
			return "TRCK";
		case RECORDING_DATE:
			return "TRDA";
		case YEAR:
			return "TDRC";
		case GRACENOTE_ID:
			return "GNID";
		default:
			return null;
		}
	}
	private String getCodesForId3v22(){
		switch (this) {
		case COMMENTS:
			return "COM";
		case PLAY_COUNTER:
			return "CNT";
		case ALBUM:
			return "TAL";
		case BEATS_PER_MINUTE:
			return "TBP";
		case COMPOSER:
			return "TCM";
		case CONTENT_TYPE:
			return "TCO";
		case LYRICIST:
			return "TXT";
		case TITLE:
			return "TT2";
		case LENGTH:
			return "TLE";
		case ARTIST:
			return "TP1";
		case BAND:
			return "TP2";
		case CONDUCTOR:
			return "TP3";
		case TRACK_NUMBER:
			return "TRK";
		case RECORDING_DATE:
			return "TRD";
		case YEAR:
			return "TYE";
		case GRACENOTE_ID:
			return "GND";
		default:
			return null;
		}
	}
	
	private String getCodesForId3v23(){
		switch (this) {
		case COMMENTS:
			return "COMM";
		case PLAY_COUNTER:
			return "PCNT";
		case ALBUM:
			return "TALB";
		case BEATS_PER_MINUTE:
			return "TBPM";
		case COMPOSER:
			return "TCOM";
		case CONTENT_TYPE:
			return "TCON";
		case LYRICIST:
			return "TEXT";
		case TITLE:
			return "TIT2";
		case LENGTH:
			return "TLEN";
		case ARTIST:
			return "TPE1";
		case BAND:
			return "TPE2";
		case CONDUCTOR:
			return "TPE3";
		case TRACK_NUMBER:
			return "TRCK";
		case RECORDING_DATE:
			return "TRDA";
		case YEAR:
			return "TYER";
		case GRACENOTE_ID:
			return "GNID";
		default:
			return null;
		}
	}
}
