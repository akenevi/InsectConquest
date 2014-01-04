package com.avilysal.insectConquest.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.avilysal.insectConquest.map.Cell;
import com.avilysal.insectConquest.map.Map;

public class PathFinder implements Runnable {
	
	private Cell start, goal;
	private Map map;

	@Override
	public void run() {
		System.out.println("Path Finding is up and runnin");
	}
	
	/** Constructor. Layer at index 0 of given map must be ground layer.
	 * @param map to work with.*/
	public PathFinder(Map newMap) {
		map = newMap;
	}

	/** A* pathfinding.
	 * 
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return
	 */
	public Path findPath(float fromX, float fromY, float toX, float toY) {
		
//		Open and closed lists
		PriorityQueue<Cell> open = new PriorityQueue<Cell>(11, new SortQueueViaPriority());
		ArrayList<Cell> closed = new ArrayList<Cell>();

//		Calculate starting and goal nodes from given vectors, returns null if given position is out of map bounds.
		if (fromX >= 0 && fromX <= map.grid.length * map.getTileWidth() && fromY >= 0 && fromY <= map.grid[0].length * map.getTileHeight()) {
			if (toX >= 0 && toX <= map.grid.length * map.getTileWidth() && toY >= 0 && toY <= map.grid[0].length * map.getTileHeight()) {
				start = map.getCell(fromX, fromY);
				goal = map.getCell(toX, toY);
			} else
				return null;
		} else
			return null;
//		reset Node parents and G values
		resetNodes();
		
		open.add(start);
		start.setH(heuristics(start, goal));
		start.setF();
		
		while(!open.isEmpty()){
			Cell current = open.poll();
			if(current == goal){
				return reconstructPath(current);
			}
			
			closed.add(current);
			for(Cell n : current.getNeighbors()){
				if(n.isTraversable() == false){
					closed.add(n);
				}
				
				if(closed.contains(n))
					continue;
				
				double tempG = current.getG() + getDistance(current, n) + n.getT();
				
				if(!open.contains(n) || tempG < n.getG() && !closed.contains(n)){
					n.setParent(current);
					n.setG(tempG);
					n.setH(heuristics(n, goal));
					n.setF();
					if(!open.contains(n)){
						open.add(n);
					}
				}
			}
		}
//		System.out.println("No path found");
		return null;
	}
	
	/** Calculates heuristics.
	 * @param start
	 * @param goal
	 * @return h value.*/
	private double heuristics(Cell start, Cell goal) {
        return abs(goal.getX() - start.getX()) + abs(goal.getY() - start.getY());
	}
	
	/** Calculates distance^2.
	 * @param from
	 * @param to
	 * @return distance^2*/
	private double getDistance(Cell from, Cell to){
		return (sq(from.getX() - to.getX()) + sq(from.getY() - to.getY()));
	}
	
	/** @param a given value
	 * @return a^2*/
	private double sq(double a){
		return a*a;
	}
	
	/**@param a given value.
	 * @return real number of given value.*/
	private int abs(int a) {
        return (a < 0) ? -a : a;
    }
	
	/**@param current goal node
	 * @param smoothed do we want the path to be smoothed or not
	 * @return new Path.
	 */
	private Path reconstructPath(Cell current) {
		ArrayList<Cell> path = new ArrayList<Cell>();
		Cell temp = current;
//		copy nodes in order from goal to start
		while(temp.getParent() != temp){
			path.add(temp);
			temp = temp.getParent();
		}
		
//      Reversing the order of the path
		Cell[] foundPath = new Cell[path.size()];
		for(int i = path.size()-1; i>=0; i--){
			foundPath[path.size()-1-i] = path.get(i);
		}
//      return processed path
		return new Path(foundPath);
	}
	
	/** Resets parents to themselves and g values to 0 for all nodes.*/
	public void resetNodes(){
		for(Cell[] row : map.grid)
			for(Cell n : row){
				n.resetParent();
				n.resetG();
			}
	}
	
//	Creating priority sorter for priority queue.
	private class SortQueueViaPriority implements Comparator<Cell> {
	    @Override
	    public int compare(Cell n1, Cell n2) {
	        return Double.compare(n1.getF(), n2.getF());
	    }
	}
}