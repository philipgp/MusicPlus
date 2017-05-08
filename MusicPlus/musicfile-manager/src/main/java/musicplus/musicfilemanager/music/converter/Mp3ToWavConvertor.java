package musicplus.musicfilemanager.music.converter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import musicplus.musicfilemanager.music.IMusicFile;
import musicplus.musicfilemanager.music.mp3.Mp3MusicFile;
import musicplus.musicfilemanager.music.wav.WavMusicFile;

public class Mp3ToWavConvertor implements MusicFileConvertor{
	Logger logger = (Logger) LoggerFactory.getLogger("MUSICPLUS");
	/*public static void main(String[] a) throws Exception, IOException{
		File file = new File("/home/philip/beegee.mp3");
		Converter converter = new Converter();
		
		ProgressListener progressListener = new ProgressListener() {
			
			@Override
			public void readFrame(int frameNo, Header header) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void parsedFrame(int frameNo, Header header) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void decodedFrame(int frameNo, Header header, Obuffer o) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void converterUpdate(int updateID, int param1, int param2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean converterException(Throwable t) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		converter.convert("/home/philip/beegee.mp3", "/home/philip/beegee.wav",progressListener);
		InputStream fis  = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		AudioInputStream mp3Stream = AudioSystem.get
		
		AudioFormat sourceFormat = mp3Stream.getFormat();
		AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		        sourceFormat.getSampleRate(), 16, 
		        sourceFormat.getChannels(), 
		        sourceFormat.getChannels() * 2,
		        sourceFormat.getSampleRate(),
		        false);
		AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
		AudioSystem.write(converted, AudioFileFormat.Type.WAVE, new File("/home/philip/bss.wav"));
	}*/

	public static void main(String [] ar) throws ConvertException, Exception{
		Mp3ToWavConvertor converter = new Mp3ToWavConvertor();
		converter.convertFrom(new Mp3MusicFile("/home/philip/music/manassil ini.mp3"));
	}
	@Override
	public IMusicFile convertFrom(IMusicFile sourceFile) throws ConvertException {
		File file = sourceFile.getFile();
		if(file.exists()){
			logger.info("file exists, starting to convert");
			String outputFileName = file.getName()+"_converted.wav";
			String absolutePath = file.getAbsolutePath();
			/*if(StringUtils.contains(absolutePath, " "))
				//absolutePath = "\""+absolutePath+"\"";
			if(StringUtils.contains(outputFileName, " "))
				outputFileName = "\""+outputFileName+"\"";*/
			String command = "ffmpeg -i "+absolutePath+" -y -acodec pcm_s16le -ac 1 -ar 11025 "+outputFileName+" &>/home/philip/out.txt";
			logger.debug(command);
			String[] params = new String[11];
			params[0]="ffmpeg";
			params[1]="-i";
			params[2]=absolutePath;
			params[3]="-y";
			params[4]="-acodec";
			params[5]="pcm_s16le";
			params[6]="-ac";
			params[7]="1";
			params[8]="-ar";
			params[9]="11025";
			params[10]=outputFileName;
			
			try {
				
				Process process = Runtime.getRuntime().exec(params);
				InputStream inputStream = process.getErrorStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				/*while (bis.available() != 0) {
					char c= (char) bis.read();
					System.out.print(c);
				}*/
				process.waitFor();
				
				int exitValue = process.exitValue(); 
				logger.info("conversion complete");
				WavMusicFile wavMusicFile = new WavMusicFile(outputFileName);
				return wavMusicFile;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ConvertException(e);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			logger.error(" file {} doesn't exist",file);
		}
		return null;
	}
}
