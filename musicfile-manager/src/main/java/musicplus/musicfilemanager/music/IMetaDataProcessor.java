package musicplus.musicfilemanager.music;

import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public interface IMetaDataProcessor {
	public MusicMetadata getMetaData();
	public void saveMetaData(MusicMetadata metaData);
}
