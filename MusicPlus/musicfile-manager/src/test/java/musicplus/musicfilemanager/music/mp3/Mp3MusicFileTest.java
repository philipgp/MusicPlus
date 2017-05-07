package musicplus.musicfilemanager.music.mp3;

import static org.junit.Assert.*;

import org.farng.mp3.TagConstant;
import org.farng.mp3.TagOptionSingleton;
import org.junit.Test;

import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3MusicFileTest {

	@Test
	public void testGetMetaData() throws Exception {
		
		TagOptionSingleton.getInstance().setOriginalSavedAfterAdjustingID3v2Padding(false);
		Mp3MusicFile mp3MusicFile  = new Mp3MusicFile("/home/philip/music/lovehurts.mp3");
		MusicMetadata metadata = mp3MusicFile.getMetaData();
		metadata.setArtist("new Artist2");
		metadata.setAlbum("new album");
		metadata.setTitle("new title");
		metadata.setYear("2008");
		//metadata.setGracenoteId("gnid");
		mp3MusicFile.saveMetaData(metadata);
		System.out.println(metadata);
	}

}
