package musicplus.musicfilemanager.music.mp3;

import org.farng.mp3.id3.AbstractID3;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;

import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.MetadataWriter;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;
import musicplus.musicfilemanager.music.mp3.id3.ID3v2Wrapper;
import musicplus.musicfilemanager.music.mp3.id3.Id3V2MetaDataReader;
import musicplus.musicfilemanager.music.mp3.id3.Id3V2MetadataWriter;

public class Mp3MusicMetadataWriter implements MetadataWriter{
	private AbstractID3v2 id3V2Tag;
	private AbstractID3v1 id3V1Tag;
	public Mp3MusicMetadataWriter(AbstractID3v2 id3v2Tag, AbstractID3v1 id3v1Tag) {
		super();
		id3V2Tag = id3v2Tag;
		id3V1Tag = id3v1Tag;
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		ID3v2Wrapper id3V2Wrapper = new ID3v2Wrapper(id3V2Tag);
		Id3V2MetadataWriter id3V2Writer = new Id3V2MetadataWriter(id3V2Wrapper);
		id3V2Writer.saveMetaData(metaData);
	}


	
}
