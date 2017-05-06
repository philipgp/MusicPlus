package musicplus.musicfilemanager.music.converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

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
		converter.convertFrom(new Mp3MusicFile("/home/philip/beegee.mp3"));
	}
	@Override
	public IMusicFile convertFrom(IMusicFile sourceFile) throws ConvertException {
		File file = sourceFile.getFile();
		if(file.exists()){
			logger.info("file exists, starting to convert");
			String outputFileName = file.getName()+"_converted.wav";
			String command = "ffmpeg -i "+file.getAbsolutePath()+" -y -acodec pcm_s16le -ac 1 -ar 11025 "+outputFileName;
			logger.debug(command);
			try {
				Runtime.getRuntime().exec(command);
				logger.info("conversion complete");
				WavMusicFile wavMusicFile = new WavMusicFile(outputFileName);
				return wavMusicFile;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ConvertException(e);
			}
			
		}else{
			logger.error(" file {} doesn't exist",file);
		}
		return null;
	}
}
