package com.philipgp.musicplus.main;

import org.philipgp.musicplus.gracenote.TrackIdCallBack;
import org.philipgp.musicplus.gracenote.music.GracenoteMetaDataReader;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnResponseAlbums;

import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3TagSetter implements TrackIdCallBack {
	public Mp3TagSetter(IMusicFile musicFile) {
		super();
		this.musicFile = musicFile;
	}
	IMusicFile musicFile;
	@Override
	public void handle(GnResponseAlbums album_result) {
		 GnAlbumIterator it = album_result.albums().getIterator();
		while(it.hasNext()){
			GnAlbum album = null;
			try {
				album = it.next();
			} catch (GnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		MetadataReader graceNoteMetaDataReader = new GracenoteMetaDataReader(album);
		MusicMetadata metadata = graceNoteMetaDataReader.getMetaData();
		musicFile.saveMetaData(metadata);
		}
		
	}

}
