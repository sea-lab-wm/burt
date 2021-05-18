package edu.semeru.android.core.entity.model;

public class Coords {
	public long x, y;
	public Coords(long x, long y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
}