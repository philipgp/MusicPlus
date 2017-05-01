package musicplus.musicfilemanager.music.mp3;

public enum ID3Version {
	ID3v1("title","artist","album","year","comment","genre","",""),
	ID3V1_1("title","artist","album","year","comment","genre","track",""),
	ID3V2_2("TT2","TP1","TAL","TYE","COM","TCO","TRK","TCM"),
	ID3V2_3("TIT2","TPE1","TALB","TYER","COMM","TCON","TRCK","TCOM"),
	ID3V2_4("TIT2","TPE1","TALB","TDRC","COMM","TCON","TRCK","TCOM");
	
	private ID3Version(String sONG_TITLE, String lEAD_ARTIST, String aLBUM_TITLE, String yEAR_RELEASED, String cOMMENT,
			String sONG_GENRE, String tRACK_NUMBER, String cOMPOSER) {
		SONG_TITLE = sONG_TITLE;
		LEAD_ARTIST = lEAD_ARTIST;
		ALBUM_TITLE = aLBUM_TITLE;
		YEAR_RELEASED = yEAR_RELEASED;
		COMMENT = cOMMENT;
		SONG_GENRE = sONG_GENRE;
		TRACK_NUMBER = tRACK_NUMBER;
		COMPOSER = cOMPOSER;
	}

	public String SONG_TITLE,LEAD_ARTIST,ALBUM_TITLE,YEAR_RELEASED,COMMENT,SONG_GENRE,TRACK_NUMBER,COMPOSER;
}
