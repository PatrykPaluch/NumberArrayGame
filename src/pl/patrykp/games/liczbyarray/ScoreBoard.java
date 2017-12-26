package pl.patrykp.games.liczbyarray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JOptionPane;

public class ScoreBoard {
	
	public static final String SCOREBOARD_SAVE_FILE = "scoreboard.scr";
	
	private ArrayList<Score> board;
	
	public ScoreBoard() {
		board = new ArrayList<Score>();
		loadScoreBoard();
		board.sort(new Comparator<Score>() {
			@Override
			public int compare(Score o1, Score o2) {
				return (o2.getDate()<o1.getDate())?-1:1;
			}
		});
	}
	
	public void addScore(String name, long time, int points, int size) {
		this.addScore(name, System.currentTimeMillis(), time, points, size);
	}
	public void addScore(String name, long date, long time, int points, int size) {
		board.add(new Score(name, date, time, points, size));
		board.sort(new Comparator<Score>() {
			@Override
			public int compare(Score o1, Score o2) {
				return (o2.getDate()<o1.getDate())?-1:1;
			}
		});
		saveScoreBoard();
	}
	
	public Score[] getScores() {
		return board.toArray(new Score[0]);
	}
	
	public int getCount() {
		return board.size();
	}
	
	public Score getScore(int index) {
		return board.get(index);
	}
	
	public Object[][] toObjectArray(){
		Object[][] out = new Object[board.size()][];
		for(int i = 0 ; i < board.size() ; i++) 
			out[i] = board.get(i).toObjectArray();
		
		return out;
	}
	public Object[][] toObjectArray_FormattedData(){
		Object[][] out = new Object[board.size()][];
		for(int i = 0 ; i < board.size() ; i++) 
			out[i] = board.get(i).toObjectArray_FormattedData();
		
		return out;
	}
	
	
	/*
	 First: int - score count
	 Next sequence of:
		 Data format:
		 UTF	| name
		 long	| date
		 long	| time
		 int	| points
		 int 	| size
	 */
	public void saveScoreBoard() {
		try {
			File f = new File(SCOREBOARD_SAVE_FILE);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
			dos.writeInt(board.size());
			for(Score s : board) {
				dos.writeUTF(s.getName());
				dos.writeLong(s.getDate());
				dos.writeLong(s.getTime());
				dos.writeInt(s.getPoints());
				dos.writeInt(s.getSize());
			}
			dos.close();
		}catch(IOException er) {
			er.printStackTrace();
			JOptionPane.showMessageDialog(null, "Can't save scoreboard."+System.lineSeparator()+er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	protected void loadScoreBoard() {
		try {
			board.clear();
			File f = new File(SCOREBOARD_SAVE_FILE);
			if(!f.exists())return;
			DataInputStream dos = new DataInputStream(new FileInputStream(f));
			int count = dos.readInt();
			while(count-->0) {
				String name = dos.readUTF();
				long date = dos.readLong();
				long time = dos.readLong();
				int points =dos.readInt();
				int size = dos.readInt();
				board.add(new Score(name, date, time, points, size));
			}
			dos.close();
		}catch(IOException er) {
			er.printStackTrace();
			JOptionPane.showMessageDialog(null, "Can't load scoreboard."+System.lineSeparator()+er.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
