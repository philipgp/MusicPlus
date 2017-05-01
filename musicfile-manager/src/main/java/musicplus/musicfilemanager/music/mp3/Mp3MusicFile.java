package musicplus.musicfilemanager.music.mp3;

import java.io.File;

import org.farng.mp3.MP3File;

import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3MusicFile implements IMusicFile{
	private MP3File mp3File;
	public Mp3MusicFile(String fileName) throws Exception{
		mp3File = new MP3File(fileName);
	}
	public Mp3MusicFile(File file) throws Exception{
		mp3File = new MP3File(file);
	}
	@Override
	public MusicMetadata getMetaData() {
		// TODO Auto-generated method stub
		
		return null;
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		// TODO Auto-generated method stub
		
	}
}
