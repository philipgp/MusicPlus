package org.philipgp.musicplus;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLog;
import com.gracenote.gnsdk.GnLogFilters;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMusicIdFile;
import com.gracenote.gnsdk.GnMusicIdFileInfo;
import com.gracenote.gnsdk.GnMusicIdFileProcessType;
import com.gracenote.gnsdk.GnMusicIdFileResponseType;
import com.gracenote.gnsdk.GnUser;

public class GnTest {
	final static String CLIENT_APP_VERSION = "1.0.0.0";
	public static void main(String[] a) throws GnException{
		System.loadLibrary("gnsdk_java_marshal");
		
		GnManager gnManager = new GnManager("/home/philip/Downloads/gnsdk/lib/linux_x86-64", "/home/philip/gnsdklicense", GnLicenseInputMode.kLicenseInputModeFilename);
		System.out.println("\nGNSDK Product Version : " + GnManager.productVersion() + "\t(built " + GnManager.buildDate() + ")");
		GnLog sampleLog = new GnLog("sample.log", null );
		sampleLog.filters(new GnLogFilters().error().warning());
		GnUser gnUser = new GnUser( new UserStore(), "1764707746", "D545A1DB60E1BA56E19DDD11CD252E67", CLIENT_APP_VERSION );
		GnMusicIdFile midf = new GnMusicIdFile( gnUser, new MusicIDFileEvents() );
		
		GnMusicIdFileInfo fileInfo;

		fileInfo = midf.fileInfos().add("1" );

		// Set data for this file information instance.
		// This only sets file path but all available data
		// should be set, such as artist name, track title
		// and album tile if available, such as via audio
		// file tags.
		fileInfo.fileName( "/home/philip/bss.mp3" );
		
		
		midf.doTrackId(GnMusicIdFileProcessType.kQueryReturnSingle, GnMusicIdFileResponseType.kResponseAlbums);
		System.out.println("afasdfas");
	}
}
