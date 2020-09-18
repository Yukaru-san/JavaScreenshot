import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Popup extends JPanel {

	// Text
	private JLabel tooltip;
	
	public Popup(String text) {
		// Text setup
		tooltip = new JLabel(text);
		setLayout(new GridLayout(0,1));
		add(tooltip);
		
		// Shadow
		setBorder(BorderFactory.createRaisedBevelBorder());
	}
	
	public JLabel Tooltip() {
		return tooltip;
	}
}
