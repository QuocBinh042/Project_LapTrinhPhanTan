package app;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

public class PanelHome extends JPanel{
	public PanelHome() {
		ImageIcon icon = new ImageIcon("src/main/java/img/trangChu.jpg");
		Image image = icon.getImage();
		JLabel label = new JLabel(icon);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Image scaledImage = image.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
		label.setIcon(new ImageIcon(scaledImage));
		add(label);
	}

	public void getPanelTrangChu() {
		// TODO Auto-generated method stub
		new PanelHome();
	}
}
