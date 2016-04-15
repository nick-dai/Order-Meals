public class Record {
	private int count, entry, size, type;
	private String date, time;
	public Record(String d, String t, int tp, int e, int s, int c) {
		setDate(d);
		setTime(t);
		setType(tp);
		setEntry(e);
		setSize(s);
		setCount(c);
	}
	public void setDate(String d) {
		date = d;
	}
	public void setTime(String t) {
		time = t;
	}
	public void setType(int t) {
		type = t;
	}
	public void setEntry(int e) {
		entry = e;
	}
	public void setSize(int s) {
		size = s;
	}
	public void setCount(int c) {
		count = c;
	}
	public String getDate() {
		return date;
	}
	public String getTime() {
		return time;
	}
	public int getType() {
		return type;
	}
	public int getEntry() {
		return entry;
	}
	public int getSize() {
		return size;
	}
	public int getCount() {
		return count;
	}
}