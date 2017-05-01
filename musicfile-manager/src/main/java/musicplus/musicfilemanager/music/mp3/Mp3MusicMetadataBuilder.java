package musicplus.musicfilemanager.music.mp3;

import org.farng.mp3.id3.AbstractID3;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;

import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3MusicMetadataBuilder {
	private AbstractID3v1 id3V1Tag;
	private AbstractID3v2 id3V2Tag;
	
	public Mp3MusicMetadataBuilder(AbstractID3v1 id3v1Tag, AbstractID3v2 id3v2Tag) {
		super();
		id3V1Tag = id3v1Tag;
		id3V2Tag = id3v2Tag;
	}
	
	public MusicMetadata getMetadata(){
		MusicMetadata metaData = new MusicMetadata();
		
		return metaData;
	}
	
}
