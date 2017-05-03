package musicplus.musicfilemanager.music.mp3;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;

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
		AbstractID3v2 id3V2Tag = mp3File.getID3v2Tag();
		AbstractID3v1 id3V1Tag = mp3File.getID3v1Tag();
		Mp3MusicMetadataBuilder mp3MetadataReader = new Mp3MusicMetadataBuilder(id3V2Tag, id3V1Tag);
		return mp3MetadataReader.getMetaData();
	}
	@Override
	public void saveMetaData(MusicMetadata metaData) {
		AbstractID3v2 id3V2Tag = mp3File.getID3v2Tag();
		AbstractID3v1 id3V1Tag = mp3File.getID3v1Tag();
		Mp3MusicMetadataWriter metaDatawriter = new Mp3MusicMetadataWriter(id3V2Tag, id3V1Tag);
		metaDatawriter.saveMetaData(metaData);
		try {
			mp3File.save();
		} catch (IOException | TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
