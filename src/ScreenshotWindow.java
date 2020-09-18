import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenshotWindow extends JFrame implements KeyListener {

	// Some serial number to not get warnings
	private static final long serialVersionUID = -7077528694515804789L;
	
	// Elements
	private ScreenshotPanel overlayPanel;
	
	// Variables
	private Dimension screenDimensions;
	
	// DEBUG Main method
	public static void main(String[] args)  {
		new ScreenshotWindow();
	}

	// Constructor setting up the frame itself
	public ScreenshotWindow() {
		
		// Initial setup
		screenDimensions = GetMaxDimensions();
		BufferedImage[] screenshots = TakeInitialScreenshot();
		
		// Add the key listener
		addKeyListener(this);
		
		// Ignore pressing X on the window
		// setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		// Set window size and position
		setSize(screenDimensions);
		setUndecorated(true);
		setVisible(true);
		setAlwaysOnTop(true);
		setLayout(null);
		
		// Add Overlay Panel
		overlayPanel = new ScreenshotPanel(screenshots[0], this);
		overlayPanel.setBounds(0, 0, screenDimensions.width, screenDimensions.height);
		add(overlayPanel);
		overlayPanel.addEditorOverlay(screenDimensions.width, screenDimensions.height);
		
		// Set background
		JLabel bg = new JLabel(new ImageIcon(screenshots[1]));
		bg.setBounds(0, 0, screenDimensions.width, screenDimensions.height);
		overlayPanel.add(bg);
		
		// See-through background TODO set alpha to 0 within bounds
		//setBackground(new Color(0,0,0,125));
	
	}
	
	// Takes both a clean and darker version of the screen
	private BufferedImage[] TakeInitialScreenshot() {
		try {
			// Initial Screenshots
			BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(0 ,0, screenDimensions.width, screenDimensions.height));
			BufferedImage darkScreenshot = new Robot().createScreenCapture(new Rectangle(0 ,0, screenDimensions.width, screenDimensions.height));
			// Obtain the Graphics2D context associated with the BufferedImage.
			Graphics2D g = darkScreenshot.createGraphics();

			// Draw on the BufferedImage via the graphics context.
			g.setColor(new Color(0,0,0,125));
			g.fillRect(0, 0, screenDimensions.width, screenDimensions.height);

			return new BufferedImage[] {screenshot, darkScreenshot};
			
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}	
			
	}

	// Returns the maximum screen sizes (TODO: Test for multiple monitors on top of each other)
	private Dimension GetMaxDimensions() {

		// Get Graphics Environment
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		// Get Displays
		GraphicsDevice[] gs = ge.getScreenDevices();	

		
		// Find the needed height and width
		Dimension dimensions = new Dimension();
		
		for (GraphicsDevice curGs : gs)
		{
		  DisplayMode mode = curGs.getDisplayMode();
		  dimensions.width += mode.getWidth();
		  if (mode.getHeight() > dimensions.height) {
			  dimensions.height = mode.getHeight();
		  }
		}
		
		return dimensions;
			
	}	

	// Close the Overlay upon pressing ESC
	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == 27)
			System.exit(0);
	}
	
	
	
	// --------------- Not needed, but has to be implemented --------------- \\
	
	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			BufferedImage shot = overlayPanel.takeScreenshot();
			File outputfile = new File(System.getProperty("user.home") + "/javaScreenshot.png");
			try {
				ImageIO.write(shot, "png", outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent event) {}

}




/*


int width = 0;
int height = 0;
GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
GraphicsDevice[] gs = ge.getScreenDevices();
for (GraphicsDevice curGs : gs)
{
  DisplayMode mode = curGs.getDisplayMode();
  width += mode.getWidth();
  height = mode.getHeight();
}


*/

/*

*/
