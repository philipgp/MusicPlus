package org.philipgp.musicplus.gracenote;

import com.gracenote.gnsdk.GnResponseAlbums;

public interface TrackIdCallBack {
	public void handle(GnResponseAlbums album_result);
}
