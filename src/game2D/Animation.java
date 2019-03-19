package game2D;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

/**
    The Animation class manages a series of images (frames) and
    the amount of time to display each frame.
    
    @author David Cairns
*/
public class Animation {

    private ArrayList<Image> frames;	// The set of animation frames
    private int currFrameIndex;				// Current frame animation is on
    private long animTime;					// Current animation time
    private float animSpeed = 1.0f;			// Animation speed, e.g. 2 will be twice as fast
    private boolean loop;			// True if the animation should continue looping
	private int frameTime;

	/**
     * Creates a new, empty Animation.
     * @param repeat whether to repeat the animation
     */
    public Animation(boolean repeat) {
        loop = repeat;
        frames = new ArrayList<>();
    }

    public void restart() {
        currFrameIndex = 0;
        animTime = 0;
    }

    /**
     * Updates this animation's current image (frame) based
     * on how much time has elapsed.
     *
     * @param elapsedTime	Time that has elapsed since last call
     */
    public void update(long elapsedTime) {
    	elapsedTime = (long)(elapsedTime * animSpeed);

        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime > frameTime) {
            	animTime = 0;
            	if (frames.size() - 1 == currFrameIndex) {
            		if (loop) {
            			currFrameIndex = 0;
					}
				} else {
					currFrameIndex++;
				}
			}
        }
    }

    /**
     * Gets this Animation's current image. Returns null if this
     * animation has no images.
     * 
     * @return The current image that should be displayed
     */
    public Image getImage() {
        if (frames.size() == 0) {
            return null;
        } else {
            return frames.get(currFrameIndex);
        }
    }

    
    /**
     * Loads a complete animation from an animation sheet and adds each
     * frame in the sheet to the animation with the given frameDuration.
     *
     * @param fileName	The path to the file to load the animations from
     * @param rows		How many rows there are in the sheet
     * @param columns	How many columns there are in the sheet
     * @param frameDuration	The duration of each frame
     */
    public void loadAnimationFromSheet(String fileName, int columns, int rows, int frameDuration) {
    	Image sheet = new ImageIcon(fileName).getImage();
    	Image[] images = getImagesFromSheet(sheet, columns, rows);
    	frameTime = frameDuration;

    	frames.addAll(Arrays.asList(images));
    }

    /**
     * Loads a set of images from a sprite sheet so that they can be added to an animation.
     * Courtesy of Donald Robertson.
     * 
     * @param sheet
     * @param rows
     * @param columns
     * @return
     */
    private Image[] getImagesFromSheet(Image sheet, int columns, int rows) {

        // basic method to achieve split of sprite sheet
        // overloading could be used to achieve more complex things 
    	// such as sheets where all images are not the same dimensions
        // deliberately 'overcommented' for clarity when integrating with
    	// main engine

        // initialise image array to return
        Image[] split = new Image[rows*columns];

        // easiest way to count as going through sprite sheet as though it is a 2d array
        int count = 0;

        // initialise width & height of split up images
        int width = sheet.getWidth(null)/columns;
        int height = sheet.getHeight(null)/rows;

        // for each column in each row
        for(int i = 0; i < rows; i++) 
        {
            for(int j = 0; j < columns; j++) 
            {
            	// create an image filter
            	// top left (x) = j*width, (y) = i*height
            	// extract rectangular region of width and height from origin x,y
            	ImageFilter cropper = new CropImageFilter(j*width,i*height, width, height);
            	
                // create image source based on original sprite sheet with filter applied
                // results in image source for cropped image being generated
                FilteredImageSource cropped = new FilteredImageSource(sheet.getSource(), cropper);
                
                // create a new image using generated image source and store in appropriate array element
                split[count] = Toolkit.getDefaultToolkit().createImage(cropped);
                        
                // increment count to prevent elements being overwritten
                count++;
            }
        }

        // return array
        return split;
    }

}
