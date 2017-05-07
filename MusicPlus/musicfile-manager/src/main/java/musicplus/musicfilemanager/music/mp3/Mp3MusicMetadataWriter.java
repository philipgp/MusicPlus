package musicplus.musicfilemanager.music.mp3;

import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import musicplus.musicfilemanager.music.MetadataWriter;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;
import musicplus.musicfilemanager.music.mp3.id3.v1.Id3V1MetadataWriter;
import musicplus.musicfilemanager.music.mp3.id3.v1.Id3V1Wrapper;
import musicplus.musicfilemanager.music.mp3.id3.v2.ID3v2Wrapper;
import musicplus.musicfilemanager.music.mp3.id3.v2.Id3V2MetadataWriter;

public class Mp3MusicMetadataWriter implements MetadataWriter{
	private AbstractID3v2 id3V2Tag;
	private AbstractID3v1 id3V1Tag;
	Logger logger = (Logger) LoggerFactory.getLogger("MUSICPLUS");
	public Mp3MusicMetadataWriter(AbstractID3v2 id3v2Tag, AbstractID3v1 id3v1Tag) {
		super();
		id3V2Tag = id3v2Tag;
		id3V1Tag = id3v1Tag;
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		if(id3V2Tag!=null){
			logger.debug("writing id3v2tag");
		ID3v2Wrapper id3V2Wrapper = new ID3v2Wrapper(id3V2Tag);
		Id3V2MetadataWriter id3V2Writer = new Id3V2MetadataWriter(id3V2Wrapper);
		id3V2Writer.saveMetaData(metaData);
		}else{
			logger.debug("no id3V2 tag.i am not gonna write");
		}
		if(id3V1Tag!=null){
			logger.debug("writing id3v1tag");
		Id3V1Wrapper id3v1Wrapper = new Id3V1Wrapper(id3V1Tag);
		Id3V1MetadataWriter id3V1Writer = new Id3V1MetadataWriter(id3v1Wrapper);
		id3V1Writer.saveMetaData(metaData);
		}else{
			logger.debug("no id3v1 tag");
		}
		
	}


	
}
