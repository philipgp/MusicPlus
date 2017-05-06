package musicplus.musicfilemanager.music.converter;

import musicplus.musicfilemanager.music.IMusicFile;

public interface MusicFileConvertor {
	public IMusicFile convertFrom(IMusicFile sourceFile) throws ConvertException;
}
