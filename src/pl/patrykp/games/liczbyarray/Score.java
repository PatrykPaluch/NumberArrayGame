package pl.patrykp.games.liczbyarray;

import java.sql.Date;

public class Score {
	private String name;
	private long time;
	private int points;
	private int size;
	private long date;
	
	public Score(String name, long date, long time, int points, int size) {
		this.name = name;
		this.date = date;
		this.time = time;
		this.points = points;
		this.size = size;
	}
	
	/**
	 * @return new Object[] { name, date, time, points, size };
	 */
	public Object[] toObjectArray() {
		return new Object[] { name, date, time, points, size };
	}
	
	
	public Object[] toObjectArray_FormattedData() {
		return new Object[] { 
				name, 
				String.format("%1$tH:%1$tM:%1$tS %1$tF", date), 
				String.format(  "%d:%d (%.3fs)", (int)( (time/1e9)/60 ), (int)( (time/1e9)%60 ), (time/1e9) ), 
				points, 
				size 
			};
	}
	
	
	@Override
	public String toString() {
		return String.format(
				"Score [%s; Date: %d;Time: %d; Points: %d; Size: %d]",
				name,date,time,points,size
				);
	}
	
	@Override
	public int hashCode() {
		return (int)(name.hashCode() * time);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		if(obj.hashCode()!=this.hashCode()) return false;
		if(obj.getClass()==this.getClass()) {
			Score o = (Score) obj;
			return
					this.name.equals(o.name) &&
					this.date==o.date &&
					this.points==o.points &&
					this.size==o.size &&
					this.time==o.time;
		}
		return false;
	}
	
	// ========= GETTERS

	public String getName() {
		return name;
	}
	
	public long getDate() {
		return date;
	}
	
	public long getTime() {
		return time;
	}

	public int getPoints() {
		return points;
	}

	public int getSize() {
		return size;
	}
}
