import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import drawings.FramePainter;
import utils.CursorStyle;

public class ScreenshotPanel extends JPanel implements MouseListener, MouseMotionListener {

	// Happy little Eclipse
	private static final long serialVersionUID = 4027241526278456032L;
	
	// References
	public Point lastMovePos;
	public Point lastPressPos;
	public Rectangle initialCanvasPosition; // Used when moving / resizing image
	private Rectangle lockedPosition; // Used after selecting range
	private FramePainter painter = new FramePainter();
	private EditorPanel editor;
	
	// Variables
	public CursorStyle cursorStyle = CursorStyle.DEFAULT;
	public boolean adjustPosition = false; // Called when changing size
	public boolean inputLocked = false;
	public boolean mousePressed;
	public boolean showTooltip = true;
	public boolean drawOnCancel = false;
	private BufferedImage screenshot;
	
	// Constructor
	public ScreenshotPanel(BufferedImage screenshot, JFrame f) {
		lastMovePos = MouseInfo.getPointerInfo().getLocation();
		this.screenshot = screenshot;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	// Uses the current informations and takes a screenshot
	public BufferedImage takeScreenshot() {
		return screenshot.getSubimage(lockedPosition.x, lockedPosition.y, lockedPosition.width, lockedPosition.height);
	}
	
	// Adds the second overlay
	public void addEditorOverlay(int width, int height) {
		// Add new Overlay
		editor = new EditorPanel(this);
		getParent().add(editor);
		editor.setBounds(0, 0, width, height);
		editor.repaint();
	}
	
	
	// Paint the components
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// Draw Tooltip
		if (showTooltip) {
			painter.PaintTooltip(g, lastMovePos);
		}
		
		// Draw Rect
		if (mousePressed || adjustPosition || drawOnCancel) {
			Rectangle bounds;
			
			// Set and adjust bounds if needed
			if (adjustPosition || drawOnCancel) {
				bounds = lockedPosition;
				drawOnCancel = false;
			}
			else {
				bounds = new Rectangle(lastPressPos.x, lastPressPos.y, lastMovePos.x - lastPressPos.x, lastMovePos.y - lastPressPos.y);
				//lockedPosition = bounds;
			}
			// Correct negative width
			if (bounds.width < 0) {
				int tmp = bounds.width * -1;
				bounds.x = bounds.x + bounds.width;
				bounds.width = tmp;
			}
			// Correct negative height
			if (bounds.height < 0) {
				int tmp = bounds.height * -1;
				bounds.y = bounds.y + bounds.height;
				bounds.height = tmp;
			}
			// Special Case: Didn't draw yet
			if (bounds.height == 0 || bounds.width == 0) {
				return;
			}
			
			// Draw Border
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3.5f}, 0));
			g2d.setColor(Color.white);
			g2d.drawRect(bounds.x-1, bounds.y-1, bounds.width+1, bounds.height+1);
			
			// Paint Border's drag-points
			editor.paintDragPoints(bounds);
			
			// Draw Screenshot
			g2d.drawImage(screenshot.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height), bounds.x, bounds.y, this);
		}
	}	
	
	private void changeCursor(Point mousePos) {

		// Move area
		if (Checks.isInsideArea(lockedPosition, mousePos)) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			cursorStyle = CursorStyle.FREE_MOVE;
		} else {
			// Resize area
			int point = Checks.isAtopDragPoint(lockedPosition, mousePos);
			switch (point) {
				case 0:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					cursorStyle = CursorStyle.NW_RESIZE;
					break;
				case 1:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					cursorStyle = CursorStyle.N_RESIZE;
					break;
				case 2:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					cursorStyle = CursorStyle.NE_RESIZE;
					break;
				case 3:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					cursorStyle = CursorStyle.E_RESIZE;
					break;
				case 4:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					cursorStyle = CursorStyle.SE_RESIZE;
					break;
				case 5:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					cursorStyle = CursorStyle.S_RESIZE;
					break;
				case 6:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					cursorStyle =CursorStyle.SW_RESIZE;
					break;
				case 7:
					this.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					cursorStyle = CursorStyle.W_RESIZE;
					break;
				default:
					// Outside area
					this.setCursor(null);
					cursorStyle = CursorStyle.DEFAULT;
			}
		}	
	}
	
	// Updates upon mouse movement
	@Override
	public void mouseMoved(MouseEvent e) {
		lastMovePos = e.getPoint();
		
		// if not painted yet
		if (!mousePressed && !inputLocked)
			repaint();
		
		if (inputLocked) {
			editor.repaint();
			changeCursor(e.getPoint());
		}
	}	
	
	// Handles presses of LMB
	@Override
	public void mousePressed(MouseEvent e) {
		// Position update
		lastPressPos = e.getPoint();
		
		// When clicking at free space
		if (cursorStyle == CursorStyle.DEFAULT || !inputLocked) {
			// When the user didn't select an area yet
			mousePressed = true;
			showTooltip = false;
			repaint();
		}
		// Rectangle will be moved in some way
		else if (inputLocked) {
			initialCanvasPosition = (Rectangle) lockedPosition.clone();
		}
	}

	// Handles releases of LMB
	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		adjustPosition = false;
		
		// Special case: nothing was drawn
		Rectangle bounds = new Rectangle(lastPressPos.x, lastPressPos.y, lastMovePos.x - lastPressPos.x, lastMovePos.y - lastPressPos.y);
		if (bounds.height == 0 || bounds.width == 0) {	
			if (lockedPosition.width > 0) 
				drawOnCancel = true;
			else
				showTooltip = true;
			repaint();
			return;
		}	
		
		// Lock screen
		inputLocked = true;
		lockedPosition = bounds;		
	}
	
	// Mouse is pressed AND moved
	int x = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		switch(cursorStyle) {
		case DEFAULT: {
			lastMovePos = e.getPoint();
			repaint();
			return;
		}
		case FREE_MOVE: {
			lockedPosition.x = initialCanvasPosition.x + e.getPoint().x - lastPressPos.x;
			lockedPosition.y = initialCanvasPosition.y + e.getPoint().y - lastPressPos.y;
			break;
		}
		case N_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y + e.getPoint().y - lastPressPos.y;
			lockedPosition.height = initialCanvasPosition.height + lastPressPos.y - e.getPoint().y;
			break;
		}
		case E_RESIZE: {
			lockedPosition.x = initialCanvasPosition.x;
			lockedPosition.width = initialCanvasPosition.width + e.getPoint().x - lastPressPos.x;
			break;
		}
		case S_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y;
			lockedPosition.height = initialCanvasPosition.height + e.getPoint().y - lastPressPos.y;
			break;
		}
		case W_RESIZE: {
			lockedPosition.x = initialCanvasPosition.x + e.getPoint().x - lastPressPos.x;
			lockedPosition.width = initialCanvasPosition.width + lastPressPos.x - e.getPoint().x;
			break;
		}
		case NE_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y + e.getPoint().y - lastPressPos.y;
			lockedPosition.height = initialCanvasPosition.height + lastPressPos.y - e.getPoint().y;
			lockedPosition.x = initialCanvasPosition.x;
			lockedPosition.width = initialCanvasPosition.width + e.getPoint().x - lastPressPos.x;
			break;
		}
		case SE_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y;
			lockedPosition.height = initialCanvasPosition.height + e.getPoint().y - lastPressPos.y;
			lockedPosition.x = initialCanvasPosition.x;
			lockedPosition.width = initialCanvasPosition.width + e.getPoint().x - lastPressPos.x;
			break;
		}
		case SW_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y;
			lockedPosition.height = initialCanvasPosition.height + e.getPoint().y - lastPressPos.y;
			lockedPosition.x = initialCanvasPosition.x + e.getPoint().x - lastPressPos.x;
			lockedPosition.width = initialCanvasPosition.width + lastPressPos.x - e.getPoint().x;
			break;
		}
		case NW_RESIZE: {
			lockedPosition.y = initialCanvasPosition.y + e.getPoint().y - lastPressPos.y;
			lockedPosition.height = initialCanvasPosition.height + lastPressPos.y - e.getPoint().y;
			lockedPosition.x = initialCanvasPosition.x + e.getPoint().x - lastPressPos.x;
			lockedPosition.width = initialCanvasPosition.width + lastPressPos.x - e.getPoint().x;
			break;
		}
		
		}
		
		// Update position
		adjustPosition();
	}
	
	// Simply repaints the window with adjustPosition = true and checks screen bounds
	public void adjustPosition() {
		adjustPosition = true;
		snapToScreen();
		repaint();
	}
	
	// TODO ADD MORE POSSIBILITIES
	// Adjusts the lockedPosition if it was moved outside of the screen
	public void snapToScreen() {
		if (lockedPosition.x < 0) lockedPosition.x = 0;
		if (lockedPosition.x + lockedPosition.width > screenshot.getWidth()) lockedPosition.x  = screenshot.getWidth() - lockedPosition.width;
		if (lockedPosition.x + lockedPosition.width < 0) lockedPosition.width = lockedPosition.x * -1;
		if (lockedPosition.y < 0) lockedPosition.y = 0;
		if (lockedPosition.y + lockedPosition.height > screenshot.getHeight()) lockedPosition.y = screenshot.getHeight() - lockedPosition.height;
		if (lockedPosition.y + lockedPosition.height < 0) lockedPosition.height = lockedPosition.y * -1;
	}
	
	// ---- Needs to be implemented, but is useless \\

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
