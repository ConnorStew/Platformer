package Game.Sound;

import java.io.*;
import javax.sound.sampled.*;

/**
 * This class handles loading and paying sounds.
 * @author Connor Stewart
 */
public class Sound {

	/** The sound file location. */
	private String fileLocation;

	/** The loaded sound clip. */
	private Clip soundClip;

	/** The available filters for sound files. */
	public enum Filter {IncreaseOverTime, None}

	/**
	 * Preloads the sound from the given location and applies a filter.
	 * @param fileLocation the location of the sound file
	 * @param filter the filter to apply to the sound file
	 */
	public Sound(String fileLocation, Filter filter) {
		this.fileLocation = fileLocation;
		try {
			switch (filter) {
				case IncreaseOverTime:
					File file = new File(fileLocation);
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					AudioFormat format = stream.getFormat();
					IncreaseOverTimeFilter filtered = new IncreaseOverTimeFilter(stream);
					AudioInputStream f = new AudioInputStream(filtered,format,stream.getFrameLength());

					DataLine.Info info = new DataLine.Info(Clip.class, format);
					soundClip = (Clip)AudioSystem.getLine(info);
					soundClip.open(f);
					break;
				case None:
					loadSound();
					break;
				default:
					loadSound();
					break;
			}
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.err.println("Failed to load sound: " + fileLocation);
			e.printStackTrace();
		}
	}

    /**
     * Sets the volume of the sound clip.
     * @param change the change in decibels
     */
	public void adjustVolume(float change) {
        FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(change);
    }

	/**
	 * Loads the sound.
	 * @throws LineUnavailableException error loading sound
	 * @throws IOException error loading sound
	 * @throws UnsupportedAudioFileException error loading sound
	 */
	private void loadSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File file = new File(fileLocation);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);

		soundClip = AudioSystem.getClip();
		soundClip.open(stream);
	}

	/**
	 * Plays the sound on a new thread.
	 */
	public void play() {
		soundClip.setFramePosition(0);
		soundClip.setMicrosecondPosition(0);

		new Thread(() -> soundClip.start()).start();
	}
}