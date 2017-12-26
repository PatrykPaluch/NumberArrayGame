package pl.patrykp.games.liczbyarray;

public class GameManager {
	
	private boolean ready = false;
	private int lastNumber = -1;
	private int lastAcceptedNumber = 0;
	private long startTime = 0;
	private long endTime = 0;
	private int points = 0;
	
	public GameManager() {
		
	}
	public GameManager(int size) {
		this();
		restart(size);
	}
	
	public void restart(int size) {
		lastNumber = size*size;
		lastAcceptedNumber = 0;
		points = 0;
		ready = true;
		startTime = System.nanoTime();
	}

	public boolean pickNumber(int number) {
		if(number>lastNumber) return false;
		if(number!=lastAcceptedNumber+1) {
			points -= 1;
			return false;
		}
		lastAcceptedNumber = number;
		points += 1;
		if(lastAcceptedNumber==lastNumber) end();
		return true;
	}
	private void end() {
		ready = false;
		endTime = System.nanoTime();
	}

	public boolean isEnded() {
		return lastNumber==lastAcceptedNumber;
	}
	
	//====== GETTERS & SETTERS =======
	
	public int getLastNumber() {
		if(!ready) throw new java.lang.IllegalStateException("GameManager is not ready.");
		return lastNumber;
	}
	public boolean isReady() {
		return ready;
	}
	public int getLastAcceptedNumber() {
		if(!ready) throw new java.lang.IllegalStateException("GameManager is not ready.");
		return lastAcceptedNumber;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public long getTime() {
		if(!(ready || isEnded()) ) throw new java.lang.IllegalStateException("GameManager is not ready.");
		if(isEnded()) return endTime - startTime;
		else return System.nanoTime() - startTime;
	}
	public double getTimeInSeconds() {
		if(!(ready || isEnded())) throw new java.lang.IllegalStateException("GameManager is not ready.");
		return getTime()/1e9;
	}
	
	public int getPointCount() {
		if(!(ready || isEnded())) throw new java.lang.IllegalStateException("GameManager is not ready.");
		return points;
	}
}
