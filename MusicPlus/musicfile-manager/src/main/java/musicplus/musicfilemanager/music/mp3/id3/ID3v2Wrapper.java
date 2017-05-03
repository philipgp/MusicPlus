package musicplus.musicfilemanager.music.mp3.id3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_4;

public class ID3v2Wrapper implements Iterable<Id3FrameWrapper>{
	private AbstractID3v2 id3V2Tag;
	private ID3Version id3V2Version;
	public ID3v2Wrapper(AbstractID3v2 id3v2Tag) {
		super();
		id3V2Tag = id3v2Tag;
		Iterator iterator = id3v2Tag.iterator();
		
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
		Iterator<AbstractID3v2Frame> iterator = id3V2Tag.iterator();
		while(iterator.hasNext()){
			AbstractID3v2Frame it = iterator.next();
			Id3FrameWrapper id3FrameWrapper = new Id3FrameWrapper(it);
			id3FrameWrapperList.add(id3FrameWrapper);

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
	public void setKnownTagValue(Id3KnownTagNames tagName,String value){
		for(Id3FrameWrapper tag:this){
			if(StringUtils.equalsIgnoreCase(tag.getKey(), tagName.getCodes(id3V2Version))){
				tag.setText(value);
				return;
			}
		}
	}
	public boolean isKnownTagName(Id3FrameWrapper frame){
		Id3KnownTagNames tag = getTagName(frame);
		return tag!=null?true:false;
	}
	
	
}
