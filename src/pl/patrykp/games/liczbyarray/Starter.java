package pl.patrykp.games.liczbyarray;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class Starter {

	public static void main(String[] args) {
		JFrame o = new JFrame();
		o.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		o.setTitle("Game");
		o.setSize(400, 400);
		o.setLocationRelativeTo(null);
		o.getContentPane().add(new GamePanel(), BorderLayout.CENTER);
		o.setVisible(true);
	}

}
