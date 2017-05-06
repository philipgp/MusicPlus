package musicplus.musicfilemanager.music.mp3.id3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.AbstractID3v2FrameBody;
import org.farng.mp3.id3.FrameBodyPCNT;
import org.farng.mp3.id3.FrameBodyTALB;
import org.farng.mp3.id3.FrameBodyTBPM;
import org.farng.mp3.id3.FrameBodyTCOM;
import org.farng.mp3.id3.FrameBodyTCON;
import org.farng.mp3.id3.FrameBodyTEXT;
import org.farng.mp3.id3.FrameBodyTIT2;
import org.farng.mp3.id3.FrameBodyTPE1;
import org.farng.mp3.id3.FrameBodyTPE2;
import org.farng.mp3.id3.FrameBodyTPE3;
import org.farng.mp3.id3.FrameBodyTRCK;
import org.farng.mp3.id3.FrameBodyTRDA;
import org.farng.mp3.id3.FrameBodyTXXX;
import org.farng.mp3.id3.FrameBodyTYER;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_2Frame;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_3Frame;
import org.farng.mp3.id3.ID3v2_4;
import org.farng.mp3.id3.ID3v2_4Frame;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ID3v2Wrapper implements Iterable<Id3FrameWrapper>{
	private AbstractID3v2 id3V2Tag;
	private ID3Version id3V2Version;
	Logger logger =  (Logger) LoggerFactory.getLogger("MUSICPLUS");
	public ID3v2Wrapper(AbstractID3v2 id3v2Tag) {
		super();
		id3V2Tag = id3v2Tag;
		
		setId3V2Version();
	}
	private void setId3V2Version(){
		if(id3V2Tag instanceof ID3v2_4){
			id3V2Version = ID3Version.ID3V2_4;
		}else if(id3V2Tag instanceof ID3v2_3){
			id3V2Version = ID3Version.ID3V2_3;
		}else if(id3V2Tag instanceof ID3v2_2){
			id3V2Version = ID3Version.ID3V2_2;
		}
		
	}
	
	public AbstractID3v2 getId3V2Tag() {
		return id3V2Tag;
	}
	
	@Override
	public Iterator<Id3FrameWrapper> iterator() {
		List<Id3FrameWrapper> id3FrameWrapperList = new ArrayList<>();
		if(id3V2Tag!=null){
			Iterator<AbstractID3v2Frame> iterator = id3V2Tag.iterator();
			while(iterator.hasNext()){
				AbstractID3v2Frame it = iterator.next();
				Id3FrameWrapper id3FrameWrapper = new Id3FrameWrapper(it);
				id3FrameWrapperList.add(id3FrameWrapper);
	
			}
		}
		return id3FrameWrapperList.iterator();
	}
	public ID3Version getId3V2Version() {
		return id3V2Version;
	}
	public Id3KnownTagNames getTagName(Id3FrameWrapper frame){
		String tagName = frame.getKey();
		Id3KnownTagNames[] id3KnownTagNames = Id3KnownTagNames.values();
		if(id3KnownTagNames!=null){
			for(Id3KnownTagNames id3KnownTagName:id3KnownTagNames){
				if(StringUtils.equalsIgnoreCase(id3KnownTagName.getCodes(id3V2Version), tagName)){
					return id3KnownTagName;
				}
			}
		}
		return null;
	}
	public void addNewTag(Id3KnownTagNames tagName,String value){
		AbstractID3v2FrameBody frameBody = getFrameBody(tagName);
		if(frameBody!=null){
			
		AbstractID3v2Frame abstractId3Frame = getNewFrame(frameBody);
		Id3FrameWrapper frameWrapper = new Id3FrameWrapper(abstractId3Frame);
//		id3V2FrameWrapper.setKey(tagName.);
		frameWrapper.setText(value);
		id3V2Tag.setFrame(abstractId3Frame);
		
		}
	}
	private AbstractID3v2FrameBody getFrameBody(Id3KnownTagNames tagName){
		if(tagName == Id3KnownTagNames.ARTIST){
			return new FrameBodyTPE1();
		}else if(tagName == Id3KnownTagNames.ALBUM){
			return new FrameBodyTALB();
		}else if(tagName == Id3KnownTagNames.TITLE){
			return new FrameBodyTIT2();
		}else if(tagName == Id3KnownTagNames.BAND){
			return new FrameBodyTPE2();
		}else if(tagName == Id3KnownTagNames.CONDUCTOR){
			return new FrameBodyTPE3();
		}else if(tagName == Id3KnownTagNames.BEATS_PER_MINUTE){
			return new FrameBodyTBPM();
		}else if(tagName == Id3KnownTagNames.COMMENTS){
			return new FrameBodyTCOM();
		}else if(tagName == Id3KnownTagNames.CONTENT_TYPE){
			return new FrameBodyTCON();
		}else if(tagName == Id3KnownTagNames.GRACENOTE_ID){
			return new FrameBodyGNID();
			
		}else if(tagName == Id3KnownTagNames.LYRICIST){
			return new FrameBodyTEXT();
		}else if(tagName == Id3KnownTagNames.PLAY_COUNTER){
			return new FrameBodyPCNT();
		}else if(tagName == Id3KnownTagNames.TRACK_NUMBER){
			return new FrameBodyTRCK();
		}else if(tagName == Id3KnownTagNames.YEAR){
			return new FrameBodyTYER();
		}else if(tagName == Id3KnownTagNames.RECORDING_DATE){
			return new FrameBodyTRDA();
		}
		return null;
	}
	private AbstractID3v2Frame getNewFrame(AbstractID3v2FrameBody frameBody){
		
		AbstractID3v2Frame abstractFrame = null;
		if( id3V2Version == ID3Version.ID3V2_2){
			abstractFrame = new ID3v2_2Frame(frameBody);
			
		}else if( id3V2Version == ID3Version.ID3V2_3){
			abstractFrame  =  new ID3v2_3Frame(frameBody);
		}else if( id3V2Version == ID3Version.ID3V2_4){
			abstractFrame  =  new ID3v2_4Frame(frameBody);
		}
		
		//id3Wrapper = new Id3FrameWrapper(abstractFrame);
			return abstractFrame;
			
	}
	public void setKnownTagValue(Id3KnownTagNames tagName,String value){
		if(StringUtils.isNotEmpty(value)){
		for(Id3FrameWrapper tag:this){
			
			logger.debug("tag:{}",tag);
			if(StringUtils.equalsIgnoreCase(tag.getKey(), tagName.getCodes(id3V2Version))){
				if(tagName == Id3KnownTagNames.ARTIST){
					id3V2Tag.setLeadArtist(value);
				}else if(tagName == Id3KnownTagNames.ALBUM){
					id3V2Tag.setAlbumTitle(value);
				}else if(tagName == Id3KnownTagNames.COMMENTS){
					id3V2Tag.setSongComment(value);
				}else if(tagName == Id3KnownTagNames.TITLE){
					id3V2Tag.setSongTitle(value);
				}
				/*tag.setText(value);
				*/
				return;
			}
		}
	//	addNewTag(tagName, value);
		}
	}
	public boolean isKnownTagName(Id3FrameWrapper frame){
		Id3KnownTagNames tag = getTagName(frame);
		return tag!=null?true:false;
	}
	
	
}
