package ca.uwaterloo.lab4_203_31;

import android.graphics.PointF;

public class PathPoint extends PointF {
	boolean traversable;
	public int H;
	public int G;
	public int F;
	public PathPoint parent;

	public PathPoint(float x, float y, boolean traversable) {
		super(x, y);
		H = 0;
		G = 0;
		F = 0;
		this.traversable = traversable;
		parent = null;
	}

	public PathPoint(float x, float y, boolean traversable, PathPoint parent) {
		super(x, y);
		H = 0;
		G = 0;
		F = 0;
		this.traversable = traversable;
		this.parent = parent;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setParent(PathPoint parent) {
		this.parent = parent;
	}

	public void setG(int value) {
		this.G = value;
		F = H + G;
	}

	public void setH(int value) {
		this.H = value;
		F = H + G;
	}

	public boolean equal(PathPoint point) {
		return (point.x == this.x && point.y == this.y);
	}

	public void setTraversability(boolean value) {
		this.traversable = value;
	}
}
