import java.awt.Point;
import java.awt.Rectangle;

public class Checks {

	
	// Checks whether a given point lies within the given bounds
	public static boolean isInsideArea(Rectangle bounds, Point click) {

		int x2 = bounds.x + bounds.width;
		int y2 = bounds.y + bounds.height;
		
		// Check all possiblities
		if (x2 < bounds.x && y2 < bounds.y && click.x > x2 && click.x < bounds.x && click.y > y2 && click.y < bounds.y ||  // Both sides inverse
				x2 < bounds.x && click.x > x2 && click.x < bounds.x && click.y > bounds.y && click.y < y2 			   ||  // X inverse
					y2 < bounds.y && click.x > bounds.x && click.x < x2 && click.y > y2 && click.y < bounds.y          ||  // Y inverse	
						click.x > bounds.x && click.x < x2 && click.y > bounds.y && click.y < y2)						   // Normal position
		{
			return true;
		}
			
		
		return false;
	}
	
	// Checks whether a given point lies within the given bounds' drag points (corner and middle parts)
		public static int isAtopDragPoint(Rectangle bounds, Point click) {

			// Drag Point positions
			Point[] dragPoints = new Point[] {
					 new Point(bounds.x-3, bounds.y-3),												// North-West
					 new Point(bounds.x-3 + (bounds.width / 2), bounds.y - 3),						// North
					 new Point(bounds.x-3 + bounds.width, bounds.y - 3),						    // North-East
					 new Point(bounds.x-3 + bounds.width, bounds.y + (bounds.height / 2) -3),	// East															
					 new Point(bounds.x-3 + bounds.width, bounds.y + bounds.height -3),			// South-East
					 new Point(bounds.x-3 + (bounds.width / 2), bounds.y + bounds.height - 3),	// South	
					 new Point(bounds.x-3, bounds.y + bounds.height - 3),							// South-West
					 new Point(bounds.x-3, bounds.y + (bounds.height / 2) - 3)						// West
				};
			
			// Iterate all drag points
			for (int i = 0; i < dragPoints.length; i++) {
				if (dragPoints[i].x > click.x-10 && dragPoints[i].x < click.x+10 && 
						dragPoints[i].y > click.y-10 && dragPoints[i].y < click.y+10) {
					System.out.println(i);
					return i;
				}
			}
			
			// No hit
			return -1;
		}
}
