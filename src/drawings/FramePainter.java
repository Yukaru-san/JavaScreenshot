package drawings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class FramePainter {

	// Paints the Tooltip onto the screen
	public void PaintTooltip(Graphics g, Point lastMousePosition) {
		
		// Shadow
		g.setColor(PaintPresets.TOOLTIP_SHADOW_COLOR);
		g.fillRect(lastMousePosition.x + PaintPresets.TOOLTIP_BACKGROUND_OFFSET.x, lastMousePosition.y + PaintPresets.TOOLTIP_BACKGROUND_OFFSET.y, PaintPresets.TOOLTIP_SHADOW_SIZE.x, PaintPresets.TOOLTIP_SHADOW_SIZE.y);
				
		
		// White Background
		g.setColor(Color.WHITE);
		g.fillRect(lastMousePosition.x + PaintPresets.TOOLTIP_BACKGROUND_OFFSET.x, lastMousePosition.y + PaintPresets.TOOLTIP_BACKGROUND_OFFSET.y, PaintPresets.TOOLTIP_BACKGROUND_SIZE.x, PaintPresets.TOOLTIP_BACKGROUND_SIZE.y);
		
		
		// Text
		g.setFont(PaintPresets.TOOLTIP_FONT);
		g.setColor(PaintPresets.TOOLTIP_TEXT_COLOR);
		g.drawString("Choose Area", lastMousePosition.x + PaintPresets.TOOLTIP_FONT_OFFSET.x, lastMousePosition.y + PaintPresets.TOOLTIP_FONT_OFFSET.y);
	}

}
