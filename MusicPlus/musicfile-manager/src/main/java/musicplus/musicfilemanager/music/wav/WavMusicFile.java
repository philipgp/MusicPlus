package musicplus.musicfilemanager.music.wav;

import java.io.File;

import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class WavMusicFile implements IMusicFile{

	private File wavFile;
	
	public WavMusicFile(String fileName){
		wavFile = new File(fileName);
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

	@Override
	public File getFile() {
		return wavFile;
	}

}
