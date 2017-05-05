package musicplus.musicfilemanager.music.mp3;

import static org.junit.Assert.*;

import org.junit.Test;

import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3MusicFileTest {

	@Test
	public void testGetMetaData() throws Exception { 
		Mp3MusicFile mp3MusicFile  = new Mp3MusicFile("/home/philip/beegee.mp3");
		MusicMetadata metadata = mp3MusicFile.getMetaData();
		metadata.setArtist("new Artist");
		metadata.setAlbum("new album"); 
		mp3MusicFile.saveMetaData(metadata);
		System.out.println(metadata);
	}

}
