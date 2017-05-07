package musicplus.musicfilemanager.music.metadata;

import java.util.HashMap;

public class MusicMetadata {
	
	
	private final static String ARTIST_KEY="__ARTIST";
	private final static String ALBUM_KEY="__ALBUM";
	private final static String TITLE_KEY="__TITLE";
	private final static String YEAR_KEY="__YEAR";
	private final static String COMMENTS_KEY="__COMMENTS";
	private final static String PLAY_COUNTER_KEY="__PLAY_COUNTER";
	private final static String BEATS_PER_MINUTE_KEY="__BEATS_PER_MINUTE";
	private final static String COMPOSER_KEY="__COMPOSER";
	private final static String CONTENT_TYPE_KEY="__CONTENT_TYPE";
	private final static String LYRICIST_KEY="__LYRICIST";
	private final static String LENGTH_KEY="__LENGTH";
	private final static String BAND_KEY="__BAND";
	private final static String CONDUCTOR_KEY="__CONDUCTOR";
	private final static String TRACK_NUMBER_KEY="__TRACK_NUMBER";
	private final static String RECORDING_DATE_KEY="__RECORDING_DATE";
	private final static String GRACENOTE_ID_KEY="__GRACENOTE_ID";
	private final static String MOOD_KEY="__MOOD";
	private final static String GENRE_KEY="__GENRE";
	private HashMap<String,String> map  = new HashMap<>();
	
	
	//private String artist,album,title,year,comments,playCounter,bpm,composer,contentType,lyricist,length,band,conductor,trackNumber,recordingDate,gracenoteId,mood;
	
	//private String genre;
	public String getGenre() {
		return map.get(GENRE_KEY);
	}

	public void setGenre(String genre) {
		map.put(GENRE_KEY, genre);
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	public String getYear() {
		return map.get(YEAR_KEY);
	}

	public void setYear(String year) {
		map.put(YEAR_KEY, year);
	}

	public String getComments() {
		return map.get(COMMENTS_KEY);
	}

	public void setComments(String comments) {
		map.put(COMMENTS_KEY, comments);
	}

	public String getPlayCounter() {
		return map.get(PLAY_COUNTER_KEY);
	}

	public void setPlayCounter(String playCounter) {
		map.put(PLAY_COUNTER_KEY, playCounter);
	}

	public String getBpm() {
		return map.get(BEATS_PER_MINUTE_KEY);
	}

	public void setBpm(String bpm) {
		map.put(BEATS_PER_MINUTE_KEY, bpm);
	}

	public String getComposer() {
		return map.get(COMPOSER_KEY);
	}

	public void setComposer(String composer) {
		map.put(COMPOSER_KEY, composer);
	}

	public String getContentType() {
		return map.get(CONTENT_TYPE_KEY);
	}

	public void setContentType(String contentType) {
		map.put(CONTENT_TYPE_KEY, contentType);
	}

	public String getLyricist() {
		return map.get(LYRICIST_KEY);
	}

	public void setLyricist(String lyricist) {
		map.put(LYRICIST_KEY, lyricist);
	}

	public String getLength() {
		return map.get(LENGTH_KEY);
	}

	public void setLength(String length) {
		map.put(LENGTH_KEY, length);
	}

	public String getBand() {
		return map.get(BAND_KEY);
	}

	public void setBand(String band) {
		map.put(BAND_KEY, band);
	}

	public String getConductor() {
		return map.get(CONDUCTOR_KEY);
	}

	public void setConductor(String conductor) {
		map.put(CONDUCTOR_KEY, conductor);
	}

	public String getTrackNumber() {
		return map.get(TRACK_NUMBER_KEY);
	}

	public void setTrackNumber(String trackNumber) {
		map.put(TRACK_NUMBER_KEY, trackNumber);
	}

	public String getRecordingDate() {
		return map.get(RECORDING_DATE_KEY);
	}

	public void setRecordingDate(String recordingDate) {
		map.put(RECORDING_DATE_KEY, recordingDate);
	}

	public String getGracenoteId() {
		return map.get(GRACENOTE_ID_KEY);
	}

	public void setGracenoteId(String gracenoteId) {
		map.put(GRACENOTE_ID_KEY, gracenoteId);
	}

	public String getMood() {
		return map.get(MOOD_KEY);
	}

	public void setMood(String mood) {
		map.put(MOOD_KEY, mood);
	}

	public String getArtist() {
		return map.get(ARTIST_KEY);
	}

	public void setArtist(String artist) {
		map.put(ARTIST_KEY, artist);
	}

	public String getAlbum() {
		return map.get(ALBUM_KEY);
	}

	public void setAlbum(String album) {
		map.put(ALBUM_KEY, album);
	}

	public String getTitle() {
		return map.get(TITLE_KEY);
	}

	public void setTitle(String title) {
		map.put(TITLE_KEY, title);
	}
	public void addTag(String tagName,String tagValue){
		map.put(tagName, tagValue);
	}

	@Override
	public String toString() {
		return "MusicMetadata [getYear()=" + getYear() + ", getComments()=" + getComments() + ", getPlayCounter()="
				+ getPlayCounter() + ", getBpm()=" + getBpm() + ", getComposer()=" + getComposer()
				+ ", getContentType()=" + getContentType() + ", getLyricist()=" + getLyricist() + ", getLength()="
				+ getLength() + ", getBand()=" + getBand() + ", getConductor()=" + getConductor()
				+ ", getTrackNumber()=" + getTrackNumber() + ", getRecordingDate()=" + getRecordingDate()
				+ ", getGracenoteId()=" + getGracenoteId() + ", getMood()=" + getMood() + ", getArtist()=" + getArtist()
				+ ", getAlbum()=" + getAlbum() + ", getTitle()=" + getTitle() + "]";
	}

	
	
}

