package com.avilysal.insectConquest.util;

import com.avilysal.insectConquest.map.Cell;

public class Path {
	
	private Cell[] path = null;
	private int index = 0;
	
	public Path(Cell[] nodes){
		path = new Cell[nodes.length];
		for(int i=0; i<nodes.length; i++){
			path[i] = nodes[i];
		}
	}
	
	public int getIndex(){
		return index;
	}
	public Cell getCurrentPoint(){
		if(path.length > index)
			return path[index];
		return null;
	}
	public Cell getPoint(int indexInPath){
		if(path.length > indexInPath && indexInPath >= 0){
			index = indexInPath;
			return path[index];
		}
		return null;
	}
	public Cell getNextPoint(){
		if (index+1 < path.length) 
			return path[index+1];
		return null;
	}
	public Cell getPointFromIndex(int indexDifference){
		if(index+indexDifference < path.length && index+indexDifference >= 0)
			return path[index+indexDifference];
		return null;
	}
	public void changeIndex(int amount){
		index += amount;
	}
	
	public Cell[] getAllPoints(){
		return path;
	}
	
	public int getLength(){
		return path.length;
	}
}
