package org.philipgp.musicplus.gracenote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.gracenote.gnsdk.GnString;
import com.gracenote.gnsdk.IGnUserStore;

public class UserStore implements IGnUserStore{
	@Override
	public GnString loadSerializedUser( String clientId ) {
		
		try {
			InputStream userFileInputStream = new FileInputStream( "user.txt" );
			
			Scanner scanner = new java.util.Scanner( userFileInputStream ).useDelimiter("\\A");
			GnString serializeUser = new GnString( scanner.hasNext() ? scanner.next() : "" );
			
			userFileInputStream.close();
			
			return serializeUser;
			
		} catch (IOException e) {
			// ignore
		}
		
		return null;
	}
	
	@Override
	public boolean storeSerializedUser( String clientId, String serializedUser ) {
		
		try{
			File userFile = new File( "user.txt" );
			if ( !userFile.exists() )
				userFile.createNewFile();
			PrintWriter out = new PrintWriter( userFile );
			out.print( serializedUser );
			out.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
