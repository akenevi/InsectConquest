package com.avilysal.insectConquest.map;


import com.avilysal.insectConquest.entities.Entity;
import com.avilysal.insectConquest.util.PathFinder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Map {
	private TiledMapTileLayer ground;
	public Cell[][] grid;
	public PathFinder pathFinder;
	
	private int tileHeight = 0, tileWidth = 0;
	
	private final String traversabilityCost = "cost";
	private final String traversabilityState = "blocked";
	
	public Map(TiledMap map){
		updateMap(map);
		setNeighbors();
		pathFinder = new PathFinder(this);
		Thread pFind = new Thread(pathFinder);
		pFind.setDaemon(true);
		pFind.setName("Path Finder");
		pFind.start();
		try {
			pFind.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateMap(TiledMap map) {
		ground = (TiledMapTileLayer) map.getLayers().get(0);
		
//		Creating new grid, based on given map and get height as well as width of the tiles in the map.
		if(tileHeight == 0 || tileWidth == 0 || grid == null){
			grid = new Cell[ground.getWidth()][ground.getHeight()];
			tileHeight = (int) ground.getTileHeight();
			tileWidth = (int) ground.getTileWidth();
		}
		
//		Update nodes from data gathered from given map.
		for (int column = grid.length - 1; column >= 0; column--){
			for (int row = 0; row < grid[column].length; row++) {
				grid[column][row] = new Cell(column * tileWidth + tileWidth/2, row * tileHeight + tileHeight/2);
				
//				If tile has a moving cost, save it into the node.
				if (ground.getCell(column, row).getTile().getProperties().containsKey(traversabilityCost)) {
					grid[column][row].setT((Double) ground.getCell(row, column).getTile().getProperties().get(traversabilityCost));
					
				}
				
//				Finding tiles with property defined by traversabilityState and setting their counterparts on grid to non-traversable.
				if (ground.getCell(column, row).getTile().getProperties().containsKey(traversabilityState)) {
					grid[column][row].setTraversable(false);
				} else
					grid[column][row].setTraversable(true);
			}
		}
	}
	
	/** @return <tt>int</tt> Width of tile textures. */
	public int getTileWidth(){
		return tileWidth;
	}
	/** @return <tt>int</tt> Height of the tile textures. */
	public int getTileHeight(){
		return tileHeight;
	}
	
	/** Returns the cell that contains given coordinates.
	 * @param x <tt>float</tt> 
	 * @param y <tt>float</tt> 
	 * @return {@link Cell}*/
	public Cell getCell(float x, float y){
		if(((int)x / tileWidth) < 0 || ((int)x / tileWidth) >= grid.length ||
		   ((int)y / tileHeight) < 0 || ((int)y / tileHeight) >= grid[((int)x / tileWidth)].length)
			return null;
		return grid[((int)x / tileWidth)][((int)y / tileHeight)];
	}
	
	/** Removes the entity from it's original {@link Bag} and stores it in the {@link Bag} of the {@link Cell} defined by changeXIndex, changeYIndex.
	 * @param ent {@link Entity}
	 * @param changeXIndex <tt>int</tt> change in columns, use -1, 0, 1.
	 * @param changeYIndex <tt>int</tt> change in rows, use -1, 0, 1.*/
	public void changeBag(Entity ent, int changeXIndex, int changeYIndex){
		if(changeXIndex >= -1 && changeXIndex <= 1 && changeYIndex >= -1 && changeYIndex <= 1){
			Cell destination = grid[ent.getCell().getX()/tileWidth + changeXIndex][ent.getCell().getY()/tileHeight+changeYIndex];
			ent.setCell(destination);
		}
	}
	
	/** Setting up neighbors.*/
	private void setNeighbors() {
		int nRows = ground.getHeight() - 1;
		int nColumns = ground.getWidth() - 1;
		
		for (int column = 0; column <= nColumns; column++) {
			for (int row = 0; row <= nRows; row++) {
				
				if (column != 0 && column != nColumns && row != 0 && row != nRows) {
					// if node does not reside on edge or in a corner
					grid[column][row].setNeighgorsAmount(8);
					grid[column][row].addNeighbor(0, grid[column - 1][row - 1]);
					grid[column][row].addNeighbor(1, grid[column - 1][row]);
					grid[column][row].addNeighbor(2, grid[column - 1][row + 1]);
					grid[column][row].addNeighbor(3, grid[column][row + 1]);
					grid[column][row].addNeighbor(4, grid[column + 1][row + 1]);
					grid[column][row].addNeighbor(5, grid[column + 1][row]);
					grid[column][row].addNeighbor(6, grid[column + 1][row - 1]);
					grid[column][row].addNeighbor(7, grid[column][row - 1]);
				} else if (column == 0 && row == 0) {
					// if node resides in south-western corner
					grid[column][row].setNeighgorsAmount(3);
					grid[column][row].addNeighbor(0, grid[column][row + 1]);
					grid[column][row].addNeighbor(1, grid[column + 1][row + 1]);
					grid[column][row].addNeighbor(2, grid[column + 1][row]);
				} else if (column == 0 && row != 0 && row != nRows) {
					// if node resides on western edge, but not in a corner
					grid[column][row].setNeighgorsAmount(5);
					grid[column][row].addNeighbor(0, grid[column][row - 1]);
					grid[column][row].addNeighbor(1, grid[column][row + 1]);
					grid[column][row].addNeighbor(2, grid[column + 1][row + 1]);
					grid[column][row].addNeighbor(3, grid[column + 1][row]);
					grid[column][row].addNeighbor(4, grid[column + 1][row - 1]);
				} else if (column == 0 && row == nRows) {
					// if node resides in north-western corner
					grid[column][row].setNeighgorsAmount(3);
					grid[column][row].addNeighbor(0, grid[column][row - 1]);
					grid[column][row].addNeighbor(1, grid[column + 1][row]);
					grid[column][row].addNeighbor(2, grid[column + 1][row - 1]);
				} else if (column != 0 && column != nColumns && row == nRows) {
					// if node resides on northern edge, but not in a corner
					grid[column][row].setNeighgorsAmount(5);
					grid[column][row].addNeighbor(0, grid[column - 1][row - 1]);
					grid[column][row].addNeighbor(1, grid[column - 1][row]);
					grid[column][row].addNeighbor(2, grid[column + 1][row]);
					grid[column][row].addNeighbor(3, grid[column + 1][row - 1]);
					grid[column][row].addNeighbor(4, grid[column][row - 1]);
				} else if (column == nColumns && row == nRows) {
					// if node resides in north-eastern corner
					grid[column][row].setNeighgorsAmount(3);
					grid[column][row].addNeighbor(0, grid[column - 1][row - 1]);
					grid[column][row].addNeighbor(1, grid[column - 1][row]);
					grid[column][row].addNeighbor(2, grid[column][row - 1]);
				} else if (column == nColumns && row != 0 && row != nRows) {
					// if node resides on eastern edge, but not in a corner
					grid[column][row].setNeighgorsAmount(5);
					grid[column][row].addNeighbor(0, grid[column - 1][row - 1]);
					grid[column][row].addNeighbor(1, grid[column - 1][row]);
					grid[column][row].addNeighbor(2, grid[column - 1][row + 1]);
					grid[column][row].addNeighbor(3, grid[column][row + 1]);
					grid[column][row].addNeighbor(4, grid[column][row - 1]);
				} else if (column == nColumns && row == 0) {
					// if node resides in south-eastern corner
					grid[column][row].setNeighgorsAmount(3);
					grid[column][row].addNeighbor(0, grid[column - 1][row]);
					grid[column][row].addNeighbor(1, grid[column - 1][row + 1]);
					grid[column][row].addNeighbor(2, grid[column][row + 1]);
				} else if (column != 0 && column != nColumns && row == 0) {
					// if node resides on southern edge, but not in a corner
					grid[column][row].setNeighgorsAmount(5);
					grid[column][row].addNeighbor(0, grid[column - 1][row]);
					grid[column][row].addNeighbor(1, grid[column - 1][row + 1]);
					grid[column][row].addNeighbor(2, grid[column][row + 1]);
					grid[column][row].addNeighbor(3, grid[column + 1][row + 1]);
					grid[column][row].addNeighbor(4, grid[column + 1][row]);
				}
			}
		}
	}
}