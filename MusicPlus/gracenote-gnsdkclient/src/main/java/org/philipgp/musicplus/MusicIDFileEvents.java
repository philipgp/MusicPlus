package org.philipgp.musicplus;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnMusicIdFileCallbackStatus;
import com.gracenote.gnsdk.GnMusicIdFileInfo;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnResponseDataMatches;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdFileEvents;
import com.gracenote.gnsdk.IGnStatusEvents;

class MusicIDFileEvents implements IGnMusicIdFileEvents, IGnStatusEvents {
	final static int BUFFER_READ_SIZE = 1024; 
	
	@Override
	public void
	statusEvent( GnStatus status, long percent_complete, long bytes_total_sent, long bytes_total_received, IGnCancellable canceller ) {
		
		System.out.print( "status (" );

		switch ( status )
		{
		case kStatusUnknown:
			System.out.print( "Unknown " );
			break;

		case kStatusBegin:
			System.out.print( "Begin " );
			break;

		case kStatusConnecting:
			System.out.print( "Connecting " );
			break;

		case kStatusSending:
			System.out.print( "Sending " );
			break;

		case kStatusReceiving:
			System.out.print( "Receiving " );
			break;

		case kStatusDisconnected:
			System.out.print( "Disconnected " );
			break;

		case kStatusComplete:
			System.out.print( "Complete " );
			break;

		default:
			break;
		}

		System.out.println( "), % complete (" + percent_complete + "), sent (" + bytes_total_sent + "), received (" + bytes_total_received + ")" );
	}

	@Override
	public void
	musicIdFileStatusEvent( GnMusicIdFileInfo fileinfo, GnMusicIdFileCallbackStatus status, long currentFile, long totalFiles, IGnCancellable canceller ) {
		
	}

	@Override
	public void
	musicIdFileAlbumResult( GnResponseAlbums album_result, long current_album, long total_albums, IGnCancellable canceller ) {
		System.out.println( "\n*Album " + current_album + " of " + total_albums + "*\n" );

		try {
			displayResult(album_result);
		} catch ( GnException gnException ) {
			System.out.println("GnException \t" + gnException.getMessage());
		}
	}
	void
	displayResult( GnResponseAlbums response ) throws GnException
	{
		System.out.println( "\tAlbum count: " + response.albums().count() );

		int matchCounter = 0;
		GnAlbumIterator itr = response.albums().getIterator();

		while ( itr.hasNext() ) {
			GnAlbum album = itr.next();
			System.out.println( "\tMatch " + ++matchCounter + " - Album Title:\t" + album.title().display()+" artis"+album.artist().name().display() );
		}
	}

	@Override
	public void
	musicIdFileMatchResult( GnResponseDataMatches matches_result, long current_match, long total_matches, IGnCancellable canceller ) {
		System.out.println( "\n*Match " + current_match + " of " + total_matches + "*\n" );
	}

	@Override
	public void
	musicIdFileResultNotFound( GnMusicIdFileInfo fileinfo, long currentFile, long totalFiles, IGnCancellable canceller ) {
		
	}

	@Override
	public void
	musicIdFileComplete( GnError completeError ) {
		
	}

	@Override 
	public void
	gatherFingerprint( GnMusicIdFileInfo fileInfo, long currentFile, long totalFiles, IGnCancellable canceller) {
		
		boolean complete = false;
	
		try {
			
			File audioFile = new File( fileInfo.fileName() );
			
			if (audioFile.exists()) {
				
				FileInputStream audioFileInputStream = null;
				DataInputStream audioDataInputStream = null;

				audioFileInputStream = new FileInputStream(audioFile);
				
				// skip the wave header (first 44 bytes). the format of the sample files is known,
				// but please be aware that many wav file headers are larger then 44 bytes!
				audioFileInputStream.skip(44);

				// initialize the fingerprinter
				// Note: The sample files are non-standard 11025 Hz 16-bit mono to save on file size
				fileInfo.fingerprintBegin(11025, 16, 1);
				
				audioDataInputStream = new DataInputStream(audioFileInputStream);

				byte[] audioBuffer = new byte[BUFFER_READ_SIZE];
				int readSize = 0;
				do {

					// read data, check for -1 to see if we are at end of file
					readSize = audioDataInputStream.read( audioBuffer );
					if ( readSize == -1) {
						break;
					}
					
					complete = fileInfo.fingerprintWrite( audioBuffer, readSize );
					
					// does the fingerprinter have enough audio?
					if (complete) {
						break;
					}
					
				}
				while ( (readSize > 0) && (complete == false) );
				
				audioDataInputStream.close();
				
				fileInfo.fingerprintEnd();
				
				if (!complete){
					// Fingerprinter doesn't have enough data to generate a fingerprint.
					// Note that the sample data does include one track that is too short to fingerprint.
					System.out.println("Warning: input file does not contain enough data to generate a fingerprint:\n" + audioFile.getPath());
				}
				
			}
			
		} catch ( GnException gnException ) {
			System.out.println("GnException \t" + gnException.getMessage());
		} catch ( IOException e ){
			System.out.println( "Execption reading audio file" + e.getMessage() );
		}			
	}

	@Override
	public void
	gatherMetadata( GnMusicIdFileInfo fileInfo, long currentFile, long totalFiles, IGnCancellable canceller ) {

		try {
			
			// A typical use for this callback is to read file tags (ID3, etc) for the basic
			// metadata of the track.  To keep the sample code simple, we went with .wav files
			// and hardcoded in metadata for just one of the sample tracks, index 5 from
			// sampleAudioFile. So, if this isn't the correct sample track, return.
			if ( fileInfo.identifier().equals( '5' ) == false ) {
				return;
			}

			fileInfo.albumArtist( "kardinal offishall" );
			fileInfo.albumTitle ( "quest for fire" );
			fileInfo.trackTitle ( "intro" );
		
		} catch ( GnException gnException ) {
			System.out.println("GnException \t" + gnException.getMessage());
		}
	}

};	
