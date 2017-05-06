package com.philipgp.musicplus.main;

import java.io.File;

import org.philipgp.musicplus.gracenote.GracenoteClient;

import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.converter.Mp3ToWavConvertor;
import musicplus.musicfilemanager.music.converter.MusicFileConvertor;
import musicplus.musicfilemanager.music.mp3.Mp3MusicFile;

public class MusicPlusMainTest {
	public static void main(String[] a) throws Exception{
		
		GracenoteClient gracenoteClient = new GracenoteClient();
		String fileName = 	"/home/philip/music";
		File file = new File(fileName);
		File[] files = file.listFiles();
		for(File childFile:files){
		Mp3MusicFile musicFile = new Mp3MusicFile(childFile.getAbsolutePath());
		MusicFileConvertor converter = new Mp3ToWavConvertor();
		IMusicFile wavMusicFile = converter.convertFrom(musicFile);
		gracenoteClient.doTrackId(wavMusicFile, new Mp3TagSetter(musicFile));
		}
	}
}

