package org.philipgp.musicplus.gracenote.music;

import org.apache.commons.lang3.StringUtils;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnArtist;
import com.gracenote.gnsdk.GnContent;
import com.gracenote.gnsdk.GnContentIterable;
import com.gracenote.gnsdk.GnContentIterator;
import com.gracenote.gnsdk.GnDataLevel;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnTrack;

import musicplus.musicfilemanager.music.MetadataReader;
import musicplus.musicfilemanager.music.metadata.MusicMetadata;

public class GracenoteMetaDataReader implements MetadataReader {
	GnAlbum album;
	public GracenoteMetaDataReader(GnAlbum album){
		this.album = album;
				
	}
	private void setMood(MusicMetadata metadata,GnTrack matchedTrack ){
		String moodLevel1 = matchedTrack.mood(GnDataLevel.kDataLevel_4);
		if(StringUtils.isNotEmpty(moodLevel1)){
			metadata.setMood(moodLevel1);
			String moodLevel2 = matchedTrack.mood(GnDataLevel.kDataLevel_2);
			if(StringUtils.isNotEmpty(moodLevel2)){
				
			}
		}
	}
	private void setGenre(MusicMetadata metadata,GnTrack matchedTrack ){
		String genreLevel1 = matchedTrack.genre(GnDataLevel.kDataLevel_1);
		if(StringUtils.isNotEmpty(genreLevel1)){
			
			String genreLevel2 = matchedTrack.mood(GnDataLevel.kDataLevel_2);
			if(StringUtils.isNotEmpty(genreLevel2)){
				
			}
		}
	}
	private void setTempo(MusicMetadata metadata,GnTrack matchedTrack ){
		String tempoLevel1 = matchedTrack.tempo(GnDataLevel.kDataLevel_1);
		if(StringUtils.isNotEmpty(tempoLevel1)){
			
			String tempoLevel2 = matchedTrack.mood(GnDataLevel.kDataLevel_2);
			if(StringUtils.isNotEmpty(tempoLevel2)){
				
			}
		}
	}
	private void setTrackData(MusicMetadata musicMetadata,GnTrack matchedTrack){
		if(matchedTrack!=null){
			musicMetadata.setTitle(matchedTrack.title().display());
			musicMetadata.setTrackNumber(matchedTrack.trackNumber());
			musicMetadata.setGracenoteId(matchedTrack.gnId());
			
			GnContentIterable contents = matchedTrack.contents();
			GnContentIterator iterator = contents.getIterator();
			setMood(musicMetadata, matchedTrack);
			setGenre(musicMetadata, matchedTrack);
			setTempo(musicMetadata, matchedTrack);
			while(iterator.hasNext()){
				try {
					GnContent content = iterator.next();
					
				} catch (GnException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			musicMetadata.setLength(String.valueOf(matchedTrack.duration()));
			GnArtist artist = matchedTrack.artist();
			if(artist!=null && artist.name()!=null && StringUtils.isNotEmpty(artist.name().display())){
				
				musicMetadata.setArtist(artist.name().display());
			}
		}
	}
	@Override
	public MusicMetadata getMetaData() {
		MusicMetadata musicMetadata = new MusicMetadata();
		if(album!=null){
			
			musicMetadata.setAlbum(album.title().display());
			GnArtist artist = album.artist();
if(artist!=null && artist.name()!=null && StringUtils.isNotEmpty(artist.name().display())){
				
				musicMetadata.setArtist(artist.name().display());
			}
			GnTrack matchedTrack = album.trackMatched();
			setTrackData(musicMetadata, matchedTrack);
		}
		return musicMetadata;
	}
	

}
