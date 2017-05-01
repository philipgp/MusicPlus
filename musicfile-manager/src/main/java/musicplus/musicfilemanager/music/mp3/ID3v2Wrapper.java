package musicplus.musicfilemanager.music.mp3;

import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_4;

public class ID3v2Wrapper {
	private AbstractID3v2 id3V2Tag;
	private ID3Version id3V2Version;
	public ID3v2Wrapper(AbstractID3v2 id3v2Tag) {
		super();
		id3V2Tag = id3v2Tag;
		setId3V2Version();
	}
	public void setId3V2Version(){
		if(id3V2Tag instanceof ID3v2_2){
			id3V2Version = ID3Version.ID3V2_2;
		}else if(id3V2Tag instanceof ID3v2_3){
			id3V2Version = ID3Version.ID3V2_3;
		}else if(id3V2Tag instanceof ID3v2_4){
			id3V2Version = ID3Version.ID3V2_4;
		}
	}
	
	
}
