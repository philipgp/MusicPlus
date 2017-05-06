package musicplus.musicfilemanager.music.mp3.id3;

import org.farng.mp3.id3.AbstractFrameBodyTextInformation;

public class FrameBodyGNID extends AbstractFrameBodyTextInformation{

	@Override
	public String getIdentifier() {
		return "TGND";
	}

}
