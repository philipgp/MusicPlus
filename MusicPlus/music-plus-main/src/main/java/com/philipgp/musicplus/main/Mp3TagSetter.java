package com.philipgp.musicplus.main;

import org.apache.commons.lang3.StringUtils;
import org.philipgp.musicplus.gracenote.TrackIdCallBack;
import org.philipgp.musicplus.gracenote.music.GracenoteMetaDataReader;
import org.slf4j.LoggerFactory;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnResponseAlbums;

import ch.qos.logback.classic.Logger;
import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class Mp3TagSetter implements TrackIdCallBack {
	Logger logger = (Logger) LoggerFactory.getLogger("MUSICPLUS");
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
			Integer matchConfidence = 0; 
			Integer threshold = 50;
			if(album.trackMatched()!=null)
				matchConfidence= new Long(album.trackMatched().matchScore()).intValue();
			logger.debug("match confidence:{},threshold:{}",matchConfidence,threshold);
			if(album.trackMatched().matched() && matchConfidence>=threshold){
				
		MetadataReader graceNoteMetaDataReader = new GracenoteMetaDataReader(album);
		MusicMetadata metadata = graceNoteMetaDataReader.getMetaData();
		musicFile.saveMetaData(metadata);
			}
		}
		
	}

}
