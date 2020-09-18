import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class EditorPanel extends JPanel {

	// Happy little Eclipse
	private static final long serialVersionUID = 2279543052365473747L;
	
	// References
	private ScreenshotPanel sp;
	
	// Variables
	public Point[] dragPoints; // used to determine mouse-press effects
	private Rectangle frameRect; // used to calculate points in the first place
	
	// Constructor
	public EditorPanel(ScreenshotPanel sp) {
		this.sp = sp;
		setBackground(new Color(0, 0, 0, 0) );
	}
	
	// Paint the components
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (frameRect != null) {
			Graphics2D g2d = (Graphics2D) g;
			
			// Calculate drag-point positions
			Point[] dragPoints = new Point[] {
				 new Point(frameRect.x-3, frameRect.y-3),												// North-West
				 new Point(frameRect.x-3 + (frameRect.width / 2), frameRect.y - 3),						// North
				 new Point(frameRect.x-3 + frameRect.width, frameRect.y - 3),						    // North-East
				 new Point(frameRect.x-3 + frameRect.width, frameRect.y + (frameRect.height / 2) -3),	// East															
				 new Point(frameRect.x-3 + frameRect.width, frameRect.y + frameRect.height -3),			// South-East
				 new Point(frameRect.x-3 + (frameRect.width / 2), frameRect.y + frameRect.height - 3),	// South	
				 new Point(frameRect.x-3, frameRect.y + frameRect.height - 3),							// South-West
				 new Point(frameRect.x-3, frameRect.y + (frameRect.height / 2) - 3)						// West
			};
			
			
			// Paint drag-points
			for (Point point : dragPoints) {
				g2d.setColor(Color.black);
				g2d.fillRect(point.x, point.y, 6, 6);
				g2d.setColor(Color.white);
				g2d.drawRect(point.x, point.y, 6, 6);
			}		
		}
	}

	// Paints the drag-points corresponding to the given Rectangle
	public void paintDragPoints(Rectangle r) {
		frameRect = r;
		repaint();
	}
	
}
