package org.philipgp.musicplus.gracenote;

import org.slf4j.LoggerFactory;

import com.gracenote.gnsdk.GnDescriptor;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLanguage;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnLocaleGroup;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMusicIdFile;
import com.gracenote.gnsdk.GnMusicIdFileInfo;
import com.gracenote.gnsdk.GnMusicIdFileProcessType;
import com.gracenote.gnsdk.GnMusicIdFileResponseType;
import com.gracenote.gnsdk.GnRegion;
import com.gracenote.gnsdk.GnUser;

import ch.qos.logback.classic.Logger;
import musicplus.musicfilemanager.music.IMusicFile;

public class GracenoteClient {
	private static String gnsdkLibraryLocation="/home/philip/Downloads/gnsdk/lib/linux_x86-64";
	private static String gnsdkLicenseLocation="/home/philip/gnsdklicense";
	private static String CLIENT_TAG="D545A1DB60E1BA56E19DDD11CD252E67";
	private static String CLIENT_ID="1764707746";
	final static String CLIENT_APP_VERSION = "1.0.0.0";
	static Logger logger = (Logger) LoggerFactory.getLogger("MUSICPLUS");
	static GnUser gnUser ;
	static GnManager gnManager;
	static GnLocale locale;

	static{
		System.loadLibrary("gnsdk_java_marshal");
		try {
			gnManager = new GnManager(gnsdkLibraryLocation, gnsdkLicenseLocation , GnLicenseInputMode.kLicenseInputModeFilename);
			logger.info("loaded Gracente Manager");
			logger.info("\nGNSDK Product Version : " + GnManager.productVersion() + "\t(built " + GnManager.buildDate() + ")");
			gnUser = new GnUser( new UserStore(), CLIENT_ID, CLIENT_TAG, CLIENT_APP_VERSION );
			logger.info("created user{}",gnUser);
			locale = new GnLocale(
					GnLocaleGroup.kLocaleGroupMusic, 
					GnLanguage.kLanguageEnglish, 	
					GnRegion.kRegionDefault, 
					GnDescriptor.kDescriptorDetailed, 
					gnUser);
			locale.setGroupDefault();
			logger.info("locale set:{}",locale);
		} catch (GnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doTrackId(IMusicFile musicFile,TrackIdCallBack trackIdCallBack) throws GracnoteException{
		GnMusicIdFileInfo fileInfo;
		GnMusicIdFile midf;
		try {
			midf = new GnMusicIdFile( gnUser, new MusicIDFileEvents(trackIdCallBack));
			fileInfo = midf.fileInfos().add("1" );
			
			// Set data for this file information instance.
			// This only sets file path but all available data
			// should be set, such as artist name, track title
			// and album tile if available, such as via audio
			// file tags.
			fileInfo.fileName(musicFile.getFile().getAbsolutePath() );
			
			midf.doTrackId(GnMusicIdFileProcessType.kQueryReturnSingle, GnMusicIdFileResponseType.kResponseAlbums);
			System.out.println(fileInfo.trackArtist());
		} catch (GnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GracnoteException(e);
		}
		
	}
}