package com.avilysal.insectConquest.map;

import com.avilysal.insectConquest.util.Bag;

public class Cell {
	private int x, y;
	private double hValue, fValue, gValue, tValue;
	private boolean traversable;
	
	private Cell parent;
	private Cell[] neighbors;
	
	private Bag bag;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		gValue = 1;
		tValue = 0;
		bag = new Bag();
		setParent(this);
	}
	
//	Positional setters and getters
	/** Set position of the center of this cell on x axis.
	 * @param x <tt>int</tt> new position on x axis*/
	public void setX(int x) {
		this.x = x;
	}
	/** Set position of the center of this cell on y axis.
	 * @param y <tt>int</tt> new position on y axis*/
	public void setY(int y) {
		this.y = y;
	}
	/** @return <tt>int</tt> Position of the center of this cell on x axis.*/
	public int getX() {
		return x;
	}
	/** @return <tt>int</tt> Position of the center of this cell on y axis.*/
	public int getY() {
		return y;
	}
	
//	Path finding function cost related setters and getters
	/** Sets heuristic cost.
	 * @param newH <tt>double</tt> new heuristic value.*/
	public void setH(double newH) {
		hValue = newH;
	}
	/** Sets cost of this cell, set by path finder.
	 * @param newG <tt>double</tt> new cost of this cell.*/
	public void setG(double newG) {
		gValue = newG;
	}
	/** Resets g cost to it's original value (1).*/
	public void resetG(){
		gValue = 1;
	}
	/** Sets cost of traversing through this cell, set by appearing events.
	 * @param traversabilityCost <tt>double</tt> new traversability cost.*/
	public void setT(double traversabilityCost) {
		tValue = traversabilityCost;
	}
	/** Resets traversability cost to it's original value (0).*/
	public void resetT(){
		tValue = 0;
	}
	/** Calculates path finding function cost.*/
	public void setF(){
		fValue = hValue + gValue + tValue;
	}
	/** @return <tt>double</tt> Cost of this cell, set by path finder.*/
	public double getG() {
		return gValue;
	}
	/** @return <tt>double</tt> Cost of traversing through this cell, set by appearing events.*/
	public double getT() {
		return tValue;
	}
	/** @return <tt>double</tt> Cost of path finding function of this cell.*/
	public double getF() {
		return fValue;
	}
	
//	Setter and getter for traversability
	/** Sets the traversability of this cell.
	 * @param traversable <tt>boolean</tt>*/
	public void setTraversable(boolean traversable) {
		this.traversable = traversable;
	}
	/** @return <tt>boolean</tt> Traversability of this cell.*/
	public boolean isTraversable() {
		return traversable;
	}
	
//	Setter and getter for parent
	/** Sets the parent of this cell to given cell. Set to itself by default.
	 * @param newParent {@link Cell}*/
	public void setParent(Cell newParent) {
		parent = newParent;
	}
	/** Resets the parent of this cell to itself.*/
	public void resetParent(){
		parent = this;
	}
	/** @return {@link Cell} Parent of this cell.*/
	public Cell getParent() {
		return parent;
	}
	
//	Setters and getters for neighbors
	/** Sets the amount of neighbors for this cell.
	 * @param amount <tt>int</tt>*/
	public void setNeighgorsAmount(int amount){
		neighbors = new Cell[amount];
	}
	/** Add given cell to the neighbors of this cell at given index.
	 * @param index <tt>int</tt>
	 * @param neighbor {@link Cell}*/
	public void addNeighbor(int index, Cell neighbor) {
		neighbors[index] = neighbor;
	}
	/** @return An array of {@link Cell} containing all the neighbors of this cell.*/
	public Cell[] getNeighbors() {
		return neighbors;
	}
	
//	Bag related
	/** @return The {@link Bag} of this cell.*/
	public Bag getBag() {
		return bag;
	}
}