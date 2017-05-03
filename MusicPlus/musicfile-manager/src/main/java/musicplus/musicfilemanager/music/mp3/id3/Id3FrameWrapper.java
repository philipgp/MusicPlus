package musicplus.musicfilemanager.music.mp3.id3;

import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.id3.AbstractID3v2Frame;

public class Id3FrameWrapper {
	@Override
	public String toString() {
		return "Id3FrameWrapper [id3V2Frame=" + id3V2Frame + "]";
	}
	private AbstractID3v2Frame id3V2Frame;

	public Id3FrameWrapper(AbstractID3v2Frame id3v2Frame) {
		super();
		id3V2Frame = id3v2Frame;
	}
	public String getKey(){
		return id3V2Frame.getIdentifier();
	}
	public void setText(String text){
		AbstractMP3FragmentBody body = id3V2Frame.getBody();
		if(body !=null){
			 body.setObject("Text",text);
		}
		
	}
	public String getText(){
		AbstractMP3FragmentBody body = id3V2Frame.getBody();
		if(body !=null)
			return (String) body.getObject("Text");
		else
			return "";
	}
	
}
