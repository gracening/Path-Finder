package ca.uwaterloo.lab4_203_31;

import java.util.LinkedList;

import android.graphics.PointF;
import ca.uwaterloo.lab4_203_31.mapper.Mapper;

public class PathFinder {

	private Mapper mapView;
	private int xLength;
	private int yLength;
	private int unit;
	public PathPoint[][] map;
	private PathPoint start;
	private PathPoint end;

	public PathFinder(Mapper mapView, PathPoint[][] map, int xLength, int yLength, int unit) {
		this.mapView = mapView;
		this.xLength = xLength;
		this.yLength = yLength;
		this.unit = unit;
		this.map = map;
		this.start = null;
		this.end = null;
	}

	private void clear() {
		for (int j = 0; j < yLength; j++) {
			for (int i = 0; i < xLength; i++) {
				map[i][j].F = 0;
				map[i][j].G = 0;
				map[i][j].H = 0;
				map[i][j].parent = null;
			}
		}

	}
	
	public boolean isTraversable(PointF point){
		int x = (int) (point.x * unit);
		int y = (int) (point.y * unit);
		return map[x][y].traversable;
	}
	
	private int[] getMapLocation(PathPoint point) {
		int[] result = { 0, 0 };
		result[1] = (int) (point.x);
		result[0] = (int) (point.y);
		return result;
	}

	public PointF toPointF(PathPoint p) {
		return new PointF(p.x / unit, p.y / unit);
	}

	public LinkedList<PathPoint> getNeighbour(PathPoint point) {
		LinkedList<PathPoint> neighbour = new LinkedList<PathPoint>();
		int[] coordinate = getMapLocation(point);
		if (coordinate[1] == 0) {
			if (coordinate[0] == 0) {// 1
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] + 1]);
			} else if (coordinate[0] == yLength - 1) {// 7
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
			} else {// 4
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] + 1]);
			}
		} else if (coordinate[1] == xLength - 1) {
			if (coordinate[0] == 0) {// 3
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
			} else if (coordinate[0] == yLength - 1) {// 9
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
			} else {// 6
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
			}
		} else {
			if (coordinate[0] == 0) {// 2
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] + 1]);
			} else if (coordinate[0] == yLength - 1) {// 8
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
			} else {// 5
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] - 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] - 1][coordinate[0] + 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1]][coordinate[0] + 1]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] - 1]);
				neighbour.add(map[coordinate[1] + 1][coordinate[0]]);
//				neighbour.add(map[coordinate[1] + 1][coordinate[0] + 1]);
			}
		}

		return neighbour;
	}

	public void setStart(float x, float y) {
		this.start = map[(int) (x * unit)][(int) (y * unit)];
	}

	public void setEnd(float x, float y) {
		this.end = map[(int) (x * unit)][(int) (y * unit)];
	}

	private PathPoint getPointWithLowestF(LinkedList<PathPoint> list) {
		if (list.isEmpty()) {
			return null;
		} else {
			PathPoint result = list.get(0);
			for (PathPoint n : list) {
				if (n.F < result.F) {
					result = n;
				}
			}
			return result;
		}
	}
/*
	public boolean isDiagonals(PathPoint point1, PathPoint point2) {
		if (getNeighbour(point1).contains(point2)) {
			if (getMapLocation(point1)[0] == getMapLocation(point2)[0]
					|| getMapLocation(point1)[1] == getMapLocation(point2)[1]) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
*/
	public int getG(PathPoint point) {
		int result = 0;
		if (point.parent == null) {
			return Integer.MAX_VALUE;
		}
		/*
		if (isDiagonals(point, point.parent)) {
			result = point.parent.G + 14;
		} else {
			result = point.parent.G + 10;
		}
		*/
		result = point.parent.G + 10;
		return result;
	}

	public int getH(PathPoint point) {
		return (Math.abs(getMapLocation(point)[0] - getMapLocation(end)[0])
				+ Math.abs(getMapLocation(point)[1] - getMapLocation(end)[1])) * 10;
	}

	public LinkedList<PathPoint> getResult() {
		clear();
		if (start == null || end == null) {
			return null;
		}

		LinkedList<PathPoint> result = new LinkedList<PathPoint>();
		LinkedList<PathPoint> openList = new LinkedList<PathPoint>();
		LinkedList<PathPoint> closeList = new LinkedList<PathPoint>();
		openList.add(start);
		for (PathPoint n : getNeighbour(start)) {
			n.setParent(start);
		}
		PathPoint current = start;
		while (true) {
			if (getPointWithLowestF(openList).equal(end)) {
				end.setParent(current);
				break;
			}
			if (openList.isEmpty()) {
				return null;
			}

			current = getPointWithLowestF(openList);
			openList.remove(current);
			closeList.add(current);
			if (current.equal(end)) {

			}
			for (PathPoint n : getNeighbour(current)) {
				if (n.parent == null) {
					n.parent = current;
					n.setG(getG(n));
					n.setH(getH(n));
				}
			}

			for (PathPoint n : getNeighbour(current)) {

				if (n.traversable == false || closeList.contains(n)) {
					continue;
				}
				int newG = 0;
				/*
				if (isDiagonals(n, current)) {
					newG = current.G + 14;
				} else {
					newG = current.G + 10;
				}
				*/
				newG = current.G + 10;
				if (!openList.contains(n) || newG <= n.G) {
					n.setG(getG(n));
					n.setH(getH(n));
					n.setParent(current);
					if (!openList.contains(n)) {
						openList.add(n);
					}
				}
			}

		}

		for (current = end; !current.equal(start); current = current.parent) {
			result.add(current);
		}
		result.add(start);
		return result;
	}

	public LinkedList<PointF> getPath() {
		LinkedList<PointF> path = new LinkedList<PointF>();
		LinkedList<PathPoint> result = getResult();
		if (result == null) {
			return null;
		}
		for (PathPoint u : result) {
			path.push(new PointF(u.x / unit, u.y / unit));
		}
		return path;
	}

	public void setLinearObstacle(float x1, float x2, float y1, float y2) {
		int startX = (int) (x1 * unit);
		int endX = (int) (x2 * unit);
		int startY = (int) (y1 * unit);
		int endY = (int) (y2 * unit);
		if (startX < 0 || startX > xLength - 1 || startY < 0 || startY > yLength - 1 || endX < 0 || endX > xLength - 1
				|| endY < 0 || endY > yLength - 1) {
			return;
		}
		boolean xIncreasing = startX <= endX;
		boolean yIncreasing = startY <= endY;
		int i;
		int j = startY;
		while (j <= endY) {
			i = startX;
			while (i <= endX) {
				map[i][j].setTraversability(false);

				if (xIncreasing) {
					i++;
				} else {
					i--;
				}
			}

			if (yIncreasing) {
				j++;
			} else {
				j--;
			}
		}
	}
	
	public void removeLinearObstacle(float x1, float x2, float y1, float y2) {
		int startX = (int) (x1 * unit);
		int endX = (int) (x2 * unit);
		int startY = (int) (y1 * unit);
		int endY = (int) (y2 * unit);
		if (startX < 0 || startX > xLength - 1 || startY < 0 || startY > yLength - 1 || endX < 0 || endX > xLength - 1
				|| endY < 0 || endY > yLength - 1) {
			return;
		}
		boolean xIncreasing = startX <= endX;
		boolean yIncreasing = startY <= endY;
		int i;
		int j = startY;
		while (j <= endY) {
			i = startX;
			while (i <= endX) {
				map[i][j].setTraversability(true);

				if (xIncreasing) {
					i++;
				} else {
					i--;
				}
			}

			if (yIncreasing) {
				j++;
			} else {
				j--;
			}
		}
	}
}
