package musicplus.musicfilemanager.music.mp3.id3.v2;

import musicplus.musicfilemanager.music.MetadataWriter;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;
import musicplus.musicfilemanager.music.mp3.id3.Id3KnownTagNames;

public class Id3V2MetadataWriter implements MetadataWriter{
	private ID3v2Wrapper id3V2Wrapper;
	public Id3V2MetadataWriter(ID3v2Wrapper id3v2Wrapper) {
		super();
		id3V2Wrapper = id3v2Wrapper;
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.ARTIST, metaData.getArtist());
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.BAND, metaData.getArtist());
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.ALBUM, metaData.getAlbum());
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.YEAR, metaData.getYear());
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.GRACENOTE_ID, metaData.getGracenoteId());
		id3V2Wrapper.setKnownTagValue(Id3KnownTagNames.TITLE, metaData.getTitle());
	}

}

