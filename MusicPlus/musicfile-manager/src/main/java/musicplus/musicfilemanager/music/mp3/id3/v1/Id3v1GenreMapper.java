package musicplus.musicfilemanager.music.mp3.id3.v1;

import org.apache.commons.lang3.StringUtils;

public class Id3v1GenreMapper {
	public static String getGenreCode(String genre){
		
		String codes[]={"Blues",
				"Classic Rock", 
				"Country",
				"Dance",
				"Disco",
				"Funk",
				"Grunge",
				"Hip-Hop",
				"Jazz",
				"Metal",
				"New Age",
				"Oldies",
				"Other",
				"Pop",
				"Rhythm And Blues",
				"Rap",
				"Reggae",
				"Rock",
				"Techno",
				"Industrial",
				"Alternative",
				"Ska",
				"Death Metal",
				"Pranks",
				"SoundTrack",
				"Euro-Techno",
				"Ambient",
				"Trip-Hop",
				"Vocal",
				"Jazz & Funk",
				"Fusion",
				"Trance",
				"Classical",
				"Instrumental",
				"Acid",
				"House",
				"Game",
				"Sound Clip",
				"Gospel",
				"Noise",
				"Alternative Rock",
				"Bass",
				"Soul",
				"Punk",
				"Space",
				"Meditative",
				"Instrumental Pop",
				"Instrumentl Rock",
				"Ethnic",
				"Gothic",
				"Darkwave",
				"Techno Industrial",
				"Electronic",
				"Pop-Folk",
				"EuroDance",
				"Dream",
				"Southern Rock",
				"Comedy",
				"Cult",
				"Gangsta",
				"Top 40",
				"Christian Rap",
				"Pop/Funk",
				"Jungle",
				"Native US",
				"Cabaret",
				"New Wave",
				"Psychedelic",
				"Rave",
				"Showtunes",
				"Trailer",
				"Lo-Fi",
				"Tribal",
				"Acid Punk",
				"Acid Jazz",
				"Polka",
				"Retro",
				"Musical",
				"Rock 'n Roll",
				"Hard Rock"
				
				};
		int index = 0;
	for(String code2:codes){
		if(StringUtils.contains(code2, genre)){
			if(index<10)
				return "0"+index;
			else
				return String.valueOf(index);
			
		}
		index++;
	}
	return "12";
		
		
	}
	
	public static void main(String[] a){
		Id3v1GenreMapper mapper = new Id3v1GenreMapper();
		System.out.println(mapper.getGenreCode("Trailer"));
	}
}
