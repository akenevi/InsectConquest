package com.avilysal.insectConquest.util;

import com.avilysal.insectConquest.map.Cell;

public class Path {
	
	private Cell[] path = null;
	private int iterator = 0;
	
	public Path(Cell[] nodes){
		path = new Cell[nodes.length];
		for(int i=0; i<nodes.length; i++){
			path[i] = nodes[i];
		}
	}
	
	public Cell getPoint(int indexInPath){
		iterator = indexInPath;
		return path[indexInPath];
	}
	public Cell getNextPoint(){
		iterator++;
		if (iterator > path.length-1) iterator = path.length-1;
		return path[iterator];
	}
	
	public Cell getPreviousPoint(){
		iterator--;
		if (iterator < 0) iterator = 0;
		return path[iterator];
	}
	
	public Cell[] getAllPoints(){
		return path;
	}
	
	public int getLength(){
		return path.length;
	}
}
