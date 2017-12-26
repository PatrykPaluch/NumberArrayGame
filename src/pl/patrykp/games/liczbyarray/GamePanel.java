package pl.patrykp.games.liczbyarray;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GamePanel extends JComponent{
	//Do dodatnia:
	//Podœwietlanie kolejnych liczb (po jakims czasie, do wy³¹czenia w menu)
	//Podpowiedz co trzeba znalezc (do wylaczenai w menu)
	//Przemieszanie po kilku ruchach (na wiekszych planaszach) (do wylaczenia w menu)
	//Zapamietywanie nicku
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Sequence ok_seq;
	protected Sequence nope_seq; 
	protected Sequence win_seq; 
	protected Sequencer sequencer;

	
	protected GameManager gameManager;
	protected JPanel gamePanel;
	protected JPanel numberBlocksPanel;
	
	protected JPanel menuPanel;
	protected JLabel gameTimeLb;
	protected JButton gameExitBt;
	
	protected Timer gameTimeTimer;
	
	protected ScoreBoard scoreBoard;
	
	String lastNickName = "nobody";;
	
	public GamePanel() {
		super();
		setLayout(new BorderLayout());
		
		try {
			ok_seq = MidiSystem.getSequence(new File("ok.mid"));
			nope_seq = MidiSystem.getSequence(new File("nope.mid"));
			win_seq = MidiSystem.getSequence(new File("win.mid"));
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
		}catch (IOException | InvalidMidiDataException | MidiUnavailableException er) {
			er.printStackTrace();
		}
		
		gameManager = new GameManager();
		
		scoreBoard = new ScoreBoard();
		if(scoreBoard.getCount()>0) {
			lastNickName = scoreBoard.getScore(0).getName();
		}
		
		initGameLayout();
		initMenu();
		showMenu();
	}
	JComponent createVisibleComponent(Dimension d) {
	    JPanel panel = new JPanel();
	    panel.setMinimumSize(d);
	    panel.setMaximumSize(d);
	    panel.setPreferredSize(d);

	    return panel;
	}
	
	protected void initGameLayout() {
		gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout());
		
		gameTimeLb = new JLabel("Time: ");
		gameTimeLb.setMinimumSize(new Dimension(0, 0));
		gameTimeLb.setPreferredSize(new Dimension(0, 0));
		gameTimeLb.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		gameTimeLb.setHorizontalAlignment(SwingConstants.CENTER);
		
		gameExitBt = new JButton("<  Back to menu");
		gameExitBt.addActionListener((e)->{
			showMenu();
		});
		JPanel gameInfoPanel = new JPanel();
		
		gameInfoPanel.setLayout(new BoxLayout(gameInfoPanel, BoxLayout.X_AXIS));
		gameInfoPanel.add(gameExitBt);
		gameInfoPanel.add(gameTimeLb);
		gameInfoPanel.add(Box.createHorizontalStrut(gameExitBt.getPreferredSize().width));
		
		gameInfoPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
				new EmptyBorder(5, 5, 5, 5)
			)
		);
		
		gamePanel.add(gameInfoPanel, BorderLayout.NORTH);
	}
	
	protected void initGame( int size ) {
		if(size <1) throw new IllegalArgumentException("Size can't be less than 1.");
		//remove old
		if(numberBlocksPanel!=null) gamePanel.remove(numberBlocksPanel);
		numberBlocksPanel = new JPanel();
		//add new
		gamePanel.add(numberBlocksPanel, BorderLayout.CENTER);
		numberBlocksPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridLayout layout = new GridLayout(size,size);
		layout.setHgap(4);
		layout.setVgap(4);
		numberBlocksPanel.setLayout(layout);
		
		Random rand = new Random();
		
		int kw = size*size;
		LinkedHashSet<Integer> values = new LinkedHashSet<Integer>();
		while(values.size()<kw) {
			int los = rand.nextInt(kw)+1;
			if(!values.add(los)) continue;
			
			JButton elem = new JButton(Integer.toString(los));
			Color rndColor = getRandomColor(rand);
			elem.setForeground(rndColor);
			//TODO: poprawic zaokraglenie
			elem.setBorder(new LineBorder(rndColor, 3, true));
			elem.setOpaque(false);
			elem.setContentAreaFilled(false);
			elem.setFocusable(false);
			elem.setFont(elem.getFont().deriveFont(24f));
			
			elem.addActionListener((e)->{
				if(gameManager.pickNumber(los)) {
					//System.out.println("GJ :)");
					elem.setText("");
					elem.setEnabled(false);
					elem.setBorderPainted(false);
					if(gameManager.isEnded()) {
						//System.out.println("GG! :D");
						playSound(win_seq);
						JPanel inputData = new JPanel();
						inputData.setLayout(new BoxLayout(inputData, BoxLayout.Y_AXIS));
						inputData.add(new JLabel(
									String.format("You win! %1$sYour time is %2$.3f seconds (%5$d:%6$d), with %3$d points! (max %4$dp.)",
										System.lineSeparator(), gameManager.getTimeInSeconds(),
										gameManager.getPointCount(), kw,
										(int)(gameManager.getTimeInSeconds()/60),
										(int)(gameManager.getTimeInSeconds()%60)
									)
								));
						JTextField nameTf = new JTextField(lastNickName);
						inputData.add(nameTf);
						nameTf.setFocusable(true);
						nameTf.requestFocus();
						JOptionPane.showMessageDialog(this,
							inputData,
							"WIN!",
							JOptionPane.INFORMATION_MESSAGE);
						
						lastNickName = nameTf.getText();
						scoreBoard.addScore(nameTf.getText(), gameManager.getTime(), gameManager.getPointCount(), kw);
						showMenu();
						return;
					}
					playSound(ok_seq);
				}else {
					playSound(nope_seq);
					//System.out.println("Nope :S");
				}
			});
			numberBlocksPanel.add(elem);
		}
		showGame();
		gameManager.restart(size);
		gameTimeTimer = new Timer(true);
		gameTimeTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(gameManager.isEnded()) {
					this.cancel();
					return;
				}
				
				gameTimeLb.setText(
						String.format("Time: %.1f", gameManager.getTimeInSeconds())
				);
			}
		},0L, 100L);
	}
	private Color getRandomColor(Random rnd) {
		return new Color(rnd.nextInt(200),rnd.nextInt(200),rnd.nextInt(200)).darker();
	}
	private boolean playSound(Sequence seq) {
		if(seq==null || !sequencer.isOpen()) return false;
		try {
			sequencer.setSequence(seq);
			sequencer.stop();
			sequencer.setTickPosition(0);
			sequencer.start();
			return true;
		}catch( InvalidMidiDataException er) {
			er.printStackTrace();
			return false;
		}
	}
//	private boolean contains(int elem, int[] arr) {
//		for(int i = arr.length ; --i>=0;) {
//			if(arr[i]==elem) return true;
//		}
//		return false;
//	}
	protected void disposeGame() {
		
	}
	protected void showGame() {
		this.remove(menuPanel);
		this.add(gamePanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}
	
	protected void initMenu() {
		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		
		JButton startBt = new JButton("Start");
		JSlider sizeSl = new JSlider(JSlider.HORIZONTAL, 3, 20, 8);
		JLabel sizeLb = new JLabel(Integer.toString(sizeSl.getValue()));
		JButton scoreboardBt = new JButton("Scoreboard");
		JButton aboutBt = new JButton("Credits");
		JButton exitBt = new JButton("Exit");
		
		sizeSl.addChangeListener((e)->{
			sizeLb.setText( Integer.toString(sizeSl.getValue()) );
		});
		exitBt.addActionListener(  (e)->{
			System.exit(0);
		});
		startBt.addActionListener((e)->{
			initGame(sizeSl.getValue());
		});
		aboutBt.addActionListener((e)->{
			JOptionPane.showMessageDialog(this, "Credits:" +System.lineSeparator()+"Patryk Paluch", "Credits", JOptionPane.PLAIN_MESSAGE);
		});
		scoreboardBt.addActionListener((e)->{
			String[] columnNames = new String[] {
					"Name", "Date", "Time [mm:ss]", "points", "mapSize"
			};
			
			JTable table = new JTable(scoreBoard.toObjectArray_FormattedData(), columnNames);
			table.setFillsViewportHeight(true);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(50);
			table.getColumnModel().getColumn(2).setPreferredWidth(100);
			table.getColumnModel().getColumn(1).setPreferredWidth(150);
			table.getColumnModel().getColumn(0).setPreferredWidth(150);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
			panel.add(table, BorderLayout.CENTER);
			JDialog dialog = new JDialog();
			dialog.getContentPane().add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
			//JOptionPane.showMessageDialog(this, panel, "Scoreboard", JOptionPane.PLAIN_MESSAGE);
		});
		
		
		Dimension buttonSize = new Dimension(150, 20);
		sizeSl.setMaximumSize(new Dimension(200, sizeSl.getMaximumSize().height));
		startBt.setMaximumSize(buttonSize);
		exitBt.setMaximumSize( buttonSize);
		aboutBt.setMaximumSize( buttonSize);
		sizeLb.setMaximumSize( buttonSize);
		
		sizeSl.setAlignmentX(0.5f);
		sizeLb.setAlignmentX(0.5f);
		startBt.setAlignmentX(0.5f);
		scoreboardBt.setAlignmentX(0.5f);
		aboutBt.setAlignmentX(0.5f);
		exitBt.setAlignmentX(0.5f);
		
		
		sizeLb.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		
		menuPanel.add(startBt);
		menuPanel.add(Box.createVerticalStrut(5));
		menuPanel.add(sizeSl);
		menuPanel.add(sizeLb);
		menuPanel.add(Box.createVerticalStrut(20));
		menuPanel.add(scoreboardBt);
		menuPanel.add(Box.createVerticalStrut(5));
		menuPanel.add(aboutBt);
		menuPanel.add(Box.createVerticalStrut(5));
		menuPanel.add(exitBt);
	}
	protected void showMenu() {
		stopTimer();
		this.remove(gamePanel);
		this.add(menuPanel, BorderLayout.CENTER);
		this.revalidate();
		repaint();
	}
	protected void stopTimer() {
		if(gameTimeTimer!=null) gameTimeTimer.cancel();
	}
}
