package musicplus.musicfilemanager.music.mp3.id3;

import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v2_2Frame;
import org.farng.mp3.id3.ID3v2_3Frame;

import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Id3V2MetaDataReader implements MetadataReader {

	private ID3v2Wrapper id3V2Wrapper;
	public Id3V2MetaDataReader(ID3v2Wrapper id3v2Wrapper) {
		super();
		id3V2Wrapper = id3v2Wrapper;
	}
	@Override
	public MusicMetadata getMetaData() {
		MusicMetadata metaData = new MusicMetadata();
		for(Id3FrameWrapper id3V2Frame:id3V2Wrapper){
			if(id3V2Wrapper.isKnownTagName(id3V2Frame)){
				setTag(metaData, id3V2Wrapper.getTagName(id3V2Frame), id3V2Frame);
			}else
				metaData.addTag(id3V2Frame.getKey(), id3V2Frame.getText());
			
			System.out.println(id3V2Frame);
		}
		return metaData;
	}
	
	private void setTag(MusicMetadata metaData,Id3KnownTagNames knownTagName,Id3FrameWrapper id3V2Frame){
		if(knownTagName == Id3KnownTagNames.ALBUM ){
			metaData.setAlbum(id3V2Frame.getText());
		}
		else if(knownTagName == Id3KnownTagNames.ARTIST ){
			metaData.setArtist(id3V2Frame.getText());
		}
		else if(knownTagName == Id3KnownTagNames.TITLE ){
			metaData.setTitle(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.BAND ){
			metaData.setBand(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.BEATS_PER_MINUTE ){
			metaData.setBpm(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.COMMENTS ){
			metaData.setComments(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.COMPOSER ){
			metaData.setComposer(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.CONDUCTOR ){
			metaData.setConductor(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.CONTENT_TYPE ){
			metaData.setContentType(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.GRACENOTE_ID ){
			metaData.setGracenoteId(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.LENGTH ){
			metaData.setLength(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.LYRICIST ){
			metaData.setLyricist(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.MOOD ){
			metaData.setMood(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.PLAY_COUNTER ){
			metaData.setPlayCounter(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.RECORDING_DATE ){
			metaData.setRecordingDate(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.TRACK_NUMBER ){
			metaData.setTrackNumber(id3V2Frame.getText());
		}else if(knownTagName == Id3KnownTagNames.YEAR ){
			metaData.setYear(id3V2Frame.getText());
		}
	}
	

}
