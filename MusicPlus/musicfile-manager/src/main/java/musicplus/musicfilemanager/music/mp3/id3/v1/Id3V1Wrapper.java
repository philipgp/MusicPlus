package musicplus.musicfilemanager.music.mp3.id3.v1;

import org.apache.commons.lang3.StringUtils;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;

import musicplus.musicfilemanager.music.mp3.id3.Id3KnownTagNames;

public class Id3V1Wrapper {
	public Id3V1Wrapper(AbstractID3v1 id3v1Tag) {
		super();
		id3V1Tag = id3v1Tag;
	}

	private AbstractID3v1 id3V1Tag;
	
	public void setKnownTagValue(Id3KnownTagNames tagName,String value){
		if(StringUtils.isNotEmpty(value)){
			if(tagName == Id3KnownTagNames.ARTIST){
				id3V1Tag.setLeadArtist(value);
			}else if(tagName == Id3KnownTagNames.ALBUM){
				id3V1Tag.setAlbumTitle(value);
			}else if(tagName == Id3KnownTagNames.TITLE){
				id3V1Tag.setSongTitle(value);
			}else if(tagName == Id3KnownTagNames.YEAR){
				id3V1Tag.setYearReleased(value);
			}else if(tagName == Id3KnownTagNames.COMMENTS){
				id3V1Tag.setSongComment(value);
			}else if(tagName == Id3KnownTagNames.GENRE){
				id3V1Tag.setSongGenre(value);
			}
		}
	}
}
