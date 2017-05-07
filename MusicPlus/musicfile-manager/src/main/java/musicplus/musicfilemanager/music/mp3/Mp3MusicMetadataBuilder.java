package musicplus.musicfilemanager.music.mp3;

import org.farng.mp3.id3.AbstractID3;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;

import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;
import musicplus.musicfilemanager.music.mp3.id3.v2.ID3v2Wrapper;
import musicplus.musicfilemanager.music.mp3.id3.v2.Id3V2MetaDataReader;

public class Mp3MusicMetadataBuilder implements MetadataReader{
	private AbstractID3v2 id3V2Tag;
	private AbstractID3v1 id3V1Tag;
	public Mp3MusicMetadataBuilder(AbstractID3v2 id3v2Tag, AbstractID3v1 id3v1Tag) {
		super();
		id3V2Tag = id3v2Tag;
		id3V1Tag = id3v1Tag;
	}
	@Override
	public MusicMetadata getMetaData() {
		ID3v2Wrapper id3V2Wrapper = new ID3v2Wrapper(id3V2Tag);
		Id3V2MetaDataReader id3V2MetaDataReader = new Id3V2MetaDataReader(id3V2Wrapper);
		return id3V2MetaDataReader.getMetaData();
	}

	
}
