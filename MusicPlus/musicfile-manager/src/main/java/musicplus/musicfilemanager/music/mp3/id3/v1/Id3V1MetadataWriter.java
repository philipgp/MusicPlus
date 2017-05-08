package musicplus.musicfilemanager.music.mp3.id3.v1;

import musicplus.musicfilemanager.music.MetadataWriter;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;
import musicplus.musicfilemanager.music.mp3.id3.Id3KnownTagNames;

public class Id3V1MetadataWriter implements MetadataWriter{
	Id3V1Wrapper id3V1Wrapper;
	public Id3V1MetadataWriter(Id3V1Wrapper id3v1Wrapper) {
		super();
		id3V1Wrapper = id3v1Wrapper;
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.ARTIST, metaData.getArtist());
		//id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.BAND, metaData.getArtist());
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.ALBUM, metaData.getAlbum());
		//id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.YEAR, metaData.getYear());
		//id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.GRACENOTE_ID, metaData.getGracenoteId());
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.TITLE, metaData.getTitle());
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.YEAR, metaData.getYear());
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.COMMENTS, metaData.getComments());
		id3V1Wrapper.setKnownTagValue(Id3KnownTagNames.GENRE, Id3v1GenreMapper.getGenreCode(metaData.getGenre()));
		
	}

}
