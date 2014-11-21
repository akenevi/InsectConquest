package com.avilysal.insectConquest.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.avilysal.insectConquest.map.Cell;
import com.avilysal.insectConquest.map.Map;
import com.avilysal.insectConquest.util.Bag;
import com.avilysal.insectConquest.util.Path;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Disposable{
	private float posX, posY, cellX, cellY, cellWidth, cellHeight;
	private Cell cell;
	
	protected Map map;
	protected Sprite sprite;
	protected Animation currentAnimation;
	protected boolean animated = false;
	protected float animStateTime = 0f;
	
	private Rectangle collisionRect;
	private int health, attackType, faction, behavior;
	private float speed, damage, attackRange;
	
	public Entity pathTarget;
	protected Path path;
	private Vector2 velocity;
	private boolean selected = false, dirSet = false;
	private float dirCellX, dirCellY, dirAngle;
	
//	Constants for the type
	public static final int PROJECTILE = 0;
	public static final int INSECTOID = 1;
	public static final int NEUTRAL = 2;
	public static final int HUMAN = 3;
	
//	Type determines the use of attack and some AI behavior
	public static final int NONE = 0;
	public static final int MELEE = 1;
	public static final int RANGED = 2;
	
//	AI behavior towards entities of other factions
	public static final int AGRESSIVE = 0;
	public static final int PACIFIST = 1;
	public static final int AVOIDING = 2;
	
//	Stuff for visual representation of what's happening
	protected ShapeRenderer debug;
	private boolean debugging = false;
	
	
//	Updating and drawing
	/** Override of {@link Sprite#draw(com.badlogic.gdx.graphics.g2d.Batch)} to include {@link #update(float)} method.
	 * @param batch SpriteBatch to draw onto.*/
	public void draw(Batch batch){
		update(Gdx.graphics.getDeltaTime());
		if(animated){
			animStateTime += Gdx.graphics.getDeltaTime();
			currentAnimation.getKeyFrame(animStateTime);
		} else {
			sprite.draw(batch);
		}
	}
	
	public void setAnimation(Animation newAnimation){
		if(!animated)
			animated = true;
		animStateTime = 0f;
		currentAnimation = newAnimation;
	}
	
	/** If {@link #health} < 0 calls {@link #die(float)}, otherwise calls {@link #ai(float)}. Passes delta time to the method called.
	 * @param delta <tt>float</tt> equals to the return value of <tt>Gdx.graphics.getDeltaTime()</tt>.*/
	public void update(float delta){
		if(debugging){
			debug.begin(ShapeType.Line);
			debug.setColor(1, 0, 0, 0.1f);
			debug.rect(posX-collisionRect.width/2, posY-collisionRect.height/2, collisionRect.width, collisionRect.height);
			debug.end();
		}
		if(health <= 0)
			die(delta);
		else
			ai(delta);
	}
	
//	Methods concerning collision rectangle
	/** Creates a collision rectangle with given parameters and sets it to the center of this Entity.
	 * @param collosionWidth <tt>float</tt> with of collision rectangle.
	 * @param collisionHeight <tt>float</tt> height of collision rectangle.*/
	protected void setCollisionRect(float collosionWidth, float collisionHeight){
		collisionRect = new Rectangle(0, 0, collosionWidth, collisionHeight);
	}
	/** Updates the position of {@link #collisionRect} to the given position.
	 * @param x <tt>float</tt> new position of the center on x axis.
	 * @param y <tt>float</tt> new position of the center on y axis.*/
	public void updateCollisionRectPosition(float x, float y){
		collisionRect.setCenter(x, y);
	}
	/** @return Collision {@link Rectangle} of this Entity. */
	public Rectangle getCollisionRec(){
		return collisionRect;
	}
		
//	Positional setters and getters
	/** Override of {@link Sprite#setPosition(float, float)} to fit this class.
	 * @param x <tt>float</tt> new position on x axis.
	 * @param y <tt>float</tt> new position on y axis.*/
	public void setPosition(float x, float y){
		posX = x;
		posY = y;
		sprite.setX(posX - sprite.getWidth()/2);
		sprite.setY(posY - sprite.getHeight()/2);
		updateCollisionRectPosition(posX,posY);
	}
	/** @return <tt>float</tt> Center of this Entity on x axis.*/
	public float getPositionX(){
		return posX;
	}
	/** @return <tt>float</tt> Center of this Entity on y axis.*/
	public float getPositionY(){
		return posY;
	}
	
//	Velocity and speed related
	/** Sets velocity to given {@link Vector2}.
	 * @param newVelocity copies linked {@link Vector2} as it's velocity.*/
	public void setVelocity(Vector2 newVelocity){
		velocity = newVelocity.cpy();
	}
	public void setVelocity(float newVel){
		velocity = new Vector2(0, newVel);
	}
	/** Adds given {@link Vector2} to current velocity.
	 * @param velocityDelta {@link Velctor2}*/
	public void changeVelocity(Vector2 velocityDelta){
		velocity.add(velocityDelta);
	}
	/** Adds given degrees to the current angle of the velocity {@link Vector2}.
	 * @param degrees <tt>float</tt> degrees to change the angle by.*/
	public void rotateVelocityVector(float degrees){
		velocity.setAngle(velocity.angle()+degrees);
		sprite.rotate(degrees);
	}
	/** Adds given values to velocity {@link Vector2}.
	 * @param xVel <tt>float</tt> adds given amount to x axis of velocity {@link Vector2}.
	 * @param yVel <tt>float</tt> adds given amount to y axis of velocity {@link Vector2}.*/
	public void changeVelocity(float xVel, float yVel){
		velocity.x += xVel;
		velocity.y += yVel;
	}
	/** Sets speed of this Entity.
	 * @param newSpeed <tt>float</tt>*/
	public void setSpeed(float newSpeed){
		speed = newSpeed;
	}
	
//	AI
	public abstract void ai(float delta);
	public abstract void attack(Entity ent);
	public abstract void defend(Entity ent);
	public abstract void die(float delta);
	
//	Cell related
	/** Sets Cell this entity belongs to.
	 * @param newCell assigns this Entity to passed {@link Cell}.*/
	public void setCell(Cell newCell){
		if(cell != null)
			cell.getBag().remove(this);
		cell = newCell;
		cell.getBag().add(this);
	}
	/** @return {@link Cell} this Entity is in.*/
	public Cell getCell(){
		return cell;
	}
	/** Sets {@link Cell} dimensions and initiates position within spawning {@link Cell}.
	 * @param cellWidth <tt>float</tt> width of the {@link Cell}.
	 * @param cellHeight <tt>float</tt> height of the {@link Cell}.*/
	protected void setCellDimensions(float cellWidth, float cellHeight){
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		initCellPos();
	}
	/** Initiates position within spawning {@link Cell}.*/
	private void initCellPos(){
		cellX = posX - ((int)posX/(int)cellWidth)*cellWidth;
		cellY = posY - ((int)posY/(int)cellHeight)*cellHeight;
	}
	/** @return <tt>float</tt> Position on x axis within the cell the Entity's in.*/
	public float getCellX(){
		return cellX;
	}
	/** @return <tt>float</tt> Position on y axis within the cell the Entity's in.*/
	public float getCellY(){
		return cellY;
	}
	/** Checks if the cell has changed, if true, updates {@link #cell} and calls {@link Map#changeBag(Entity, int, int)}.*/
	public void updateCell(){
		int changeXIndex = 0, changeYIndex = 0;
		if (cellX > cellWidth){
			changeXIndex = 1;
			cellX -= cellWidth;
		}
		if (cellX < 0){
			changeXIndex = -1;
			cellX += cellWidth;
		}
		if (cellY > cellHeight){
			changeYIndex = 1;
			cellY -= cellHeight;
		}
		if (cellY < 0){
			changeYIndex = -1;
			cellY += cellHeight;
		}
		if(changeXIndex != 0 || changeYIndex != 0)
			map.changeBag(this, changeXIndex, changeYIndex);
	}
	
	/** Moves accordingly to velocity and updates cell bag, if needed.
	 * @param delta <tt>Gdx.graphics.getDeltaTime</tt> should be passed here.
	 */
	public void move(float delta){
		if(velocity.x != 0 || velocity.y != 0){
			float distanceX = velocity.x*speed*delta;
			float distanceY = velocity.y*speed*delta;
			setPosition(posX+distanceX, posY+distanceY);
			cellX += distanceX;
			cellY += distanceY;
			
			if(posX >= map.getTileWidth()*map.grid.length){
				setPosition(map.getTileWidth()*map.grid.length, posY);
				cellX = cellWidth;
			}
			if(posX <= 0){
				setPosition(0, posY);
				cellX = 0;
			}
			if(posY >= map.getTileHeight()*map.grid[0].length){
				setPosition(posX, map.getTileHeight()*map.grid.length);
				cellY = cellHeight;
			}
			if(posY <= 0){
				setPosition(posX, 0);
				cellY = 0;
			}
			
			updateCell();
		}
	}
	
	/** Deletes the path and sets {@link #velocity} to 0. */
	public void delPath(){
		path = null;
		dirSet = false;
		setVelocity(0);
	}
	
	/** Deletes current path and sets a new path to given x and y coordinates.
	 * @param x <tt>int</tt> target coordinate on x axis.
	 * @param y <tt>int</tt> target coordinate on y axis. */
	public void setPathTo(int x, int y){
		delPath();
		path = map.pathFinder.findPath(posX, posY, x, y);
		if(path != null) setVelocity(1);
	}
	
	/** Deletes current path and sets a new path to given x and y coordinates.
	 * @param x <tt>float</tt> target coordinate on x axis.
	 * @param y <tt>float</tt> target coordinate on y axis. */
	public void setPathTo(float x, float y){
		delPath();
		path = map.pathFinder.findPath(posX, posY, x, y);
		if(path != null) setVelocity(1);
	}
	
	/** Flags given {@link Entity} at this {@link Entity}'s target.
	 * Calls {@link #setPathTo(float, float)} with coordinates of given {@link Entity}.
	 * @param ent {@link Entity} to set path to. */
	public void setPathTarget(Entity ent){
		if(ent != null){
			pathTarget = ent;
			setPathTo(ent.getPositionX(), ent.getPositionY());
		}
	}
	
	/** Sets {@link #pathTarget} to <tt>null</tt>, calls {@link #delPath()}. */
	public void setPathTarget(){
		pathTarget = null;
		delPath();
	}
	
	/** Simple movement according to path given via PathFinder with rotation of the sprite via {@link #rotateSprite()}.
	 * Sets {@link #path} to <tt>null</tt> upon reaching last point in it.
	 * Renders the path onto ShapeRenderer {@link #debug} if {@link #debugging} is set to <tt>true</tt>.
	 * @param delta time between frames, <tt>Gdx.graphics.getDeltaTime()</tt>.*/
	public void followPath(float delta) {
		if(path != null && path.getLength() == path.getIndex()) delPath();
		
		if(path != null){
			
			if(debugging){
				debug.begin(ShapeType.Filled);
				debug.setColor(1, .37f, 0, 0.01f);
				for(Cell cell : path.getAllPoints())
					debug.rect(cell.getX(), cell.getY(), 6, 6);
				debug.end();
			}
			
			if(!dirSet){
				velocity.setAngle(new Vector2(path.getCurrentPoint().getX(), path.getCurrentPoint().getY()).sub(posX, posY).angle());
				dirCellX = path.getCurrentPoint().getX();
				dirCellY = path.getCurrentPoint().getY();
				dirAngle = velocity.angle();
				dirSet = true;
			}
			
			move(delta);
			
			if(dirAngle >= 0 && dirAngle < 90){
				if (posX >= dirCellX && posY >= dirCellY){
					posX = dirCellX; posY = dirCellY;
					dirSet = false;
					path.changeIndex(1);
				}
			} else if (dirAngle >= 90 && dirAngle < 180){
				if (posX <= dirCellX && posY >= dirCellY){
					posX = dirCellX; posY = dirCellY;
					dirSet = false;
					path.changeIndex(1);
				}
			} else if (dirAngle >= 180 && dirAngle < 270){
				if (posX <= dirCellX && posY <= dirCellY){
					posX = dirCellX; posY = dirCellY;
					dirSet = false;
					path.changeIndex(1);
				}
			} else if (dirAngle >= 270 && dirAngle < 360){
				if (posX >= dirCellX && posY <= dirCellY){
					posX = dirCellX; posY = dirCellY;
					dirSet = false;
					path.changeIndex(1);
				}
			}
		}
	}
	
	/** Rotates {@link #sprite} according to movement direction, if not moving, faces in direction of {@link #pathTarget}. */
	private void rotateSprite(){
		if(velocity.x != 0){
			sprite.rotate(velocity.angle() - 90 - sprite.getRotation());
		} else if(pathTarget != null){
			sprite.rotate(new Vector2(pathTarget.getPositionX(), pathTarget.getPositionY()).sub(posX, posY).angle() - 90 - sprite.getRotation());
		}
	}
	
/*
	protected boolean rayCast(float finishX, float finishY, boolean applyToVelocity){
		float startX = 0, startY = 0, toXWall = 0, toYWall = 0;
		
		Vector2 tempPath = new Vector2(new Vector2(finishX, finishY).sub(posX, posY));
		Vector2 tempV = new Vector2(posX,posY);
		float pathAngle = tempPath.angle();
		
		debug.begin(ShapeType.Line);
		debug.setColor(1, 0, 1, 1);
//		visualDebug.rect(6, 100, 10, 20);
//		visualDebug.circle(470, 420, 20);
//		visualDebug.end();
		
		while(true){
			
			startX = tempV.x - ((int)tempV.x/(int)cellWidth)*cellWidth; 
			startY = tempV.y - ((int)tempV.y/(int)cellHeight)*cellHeight;
			toXWall = cellWidth - startX;
			toYWall = cellHeight - startY;
			
			tempV = tempV.add(tempPath.cpy().limit((float) Math.sqrt(toXWall*toXWall + toYWall*toYWall)));
			
			if(map.getCell(tempV.x, tempV.y) != null && !map.getCell(tempV.x, tempV.y).isTraversable()){
				debug.end();
				return false;
			}
			
			if(pathAngle >= 0f && pathAngle < 90f){
				if(tempV.x > finishX && tempV.y > finishX)
					break;
			} else if (pathAngle >= 90f && pathAngle < 180){
				if(tempV.x < finishX && tempV.y > finishX)
					break;
			} else if (pathAngle >= 180 && pathAngle < 270){
				if(tempV.x < finishX && tempV.y < finishY)
					break;
			} else if (pathAngle >= 270 && pathAngle < 360){
				if(tempV.x > finishX && tempV.y < finishY)
					break;
			}
		}
		if(applyToVelocity){
			debug.line(posX, posY, finishX, finishY);
			velocity.setAngle(tempPath.angle());
			sprite.rotate(velocity.angle()- 90 - sprite.getRotation());
		}
		debug.end();
		return true;
	}
	
	public boolean positionIsInLineOfSight(float x, float y){
		return rayCast(x,y, false);
	}
	
	public boolean enityIsInLineOfSight(Entity ent){
		return rayCast(ent.getCell().getX(), ent.getCell().getY(), false);
	}
*/
/*
	protected void reactivePathFollowing(float delta){
		
		if(path != null){
//			path rendering, for debugging purposes
			debug.begin(ShapeType.Filled);
			debug.setColor(1, .37f, 0, 0.1f);
			for(Cell cell : path.getAllPoints())
				debug.rect(cell.getX(), cell.getY(), 6, 6);
			debug.end();
			
			
			
			setVelocity(1);
//			look 2 points ahead, and check if can go there
			if(path.getPointFromIndex(2) != null && 
			positionIsInLineOfSight(path.getPointFromIndex(2).getX(), path.getPointFromIndex(2).getY())){
				rayCast(path.getPointFromIndex(2).getX(), path.getPointFromIndex(2).getY(), true);
			} else if (path.getNextPoint() != null && positionIsInLineOfSight(path.getNextPoint().getX(), path.getNextPoint().getY())){
//			if previous failed, look 1 point ahead and check if can go there
				rayCast(path.getNextPoint().getX(), path.getNextPoint().getY(), true);
			} else {
//			if previous failed, look at current point
				rayCast(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(), true);
			}
			
//			check if passed coordinates of current target node
			if((velocity.angle() >= 0 && velocity.angle() < 90 && (posX > path.getCurrentPoint().getX() || posY > path.getCurrentPoint().getY()))
			|| (velocity.angle() >= 90 && velocity.angle() < 180 && (posX < path.getCurrentPoint().getX() || posY > path.getCurrentPoint().getY()))
			|| (velocity.angle() >= 180 && velocity.angle() < 270 && (posX < path.getCurrentPoint().getX() || posY < path.getCurrentPoint().getY()))
			|| (velocity.angle() >= 270 && velocity.angle() < 360 && (posX > path.getCurrentPoint().getX() || posY < path.getCurrentPoint().getY())))
			{
				if(path.getNextPoint() == null){
//				check if this is the end of the path
					path = null;
				} else {
//				if not the end of the path
					path.changeIndex(1);
				}
			}
			move(delta);			
		} else {
			setVelocity(0);
		}
*/

	/** Finds and returns the cells of the map that are in range of this Entity.
	 * @return an Array of {@link Cell} that are in range. */
	public Cell[] findCellsInRange(){
		PriorityQueue<Cell> cells = new PriorityQueue<Cell>(11, new Comparator<Cell>(){
            public int compare(Cell a, Cell b){
                return Float.compare(a.getX(), b.getX());
            }
        });

		ArrayList<Cell> inRange = new ArrayList<Cell>();
		Cell inspect = null;
		inRange.add(cell);
		for(Cell n : cell.getNeighbors())
			cells.add(n);
		
		while(cells.isEmpty() == false){
			inspect = cells.poll();
			if((posX-inspect.getX())*(posX-inspect.getX())+(posY-inspect.getY())*(posY-inspect.getY()) <= attackRange*attackRange){
				inRange.add(inspect);
				for(Cell n : inspect.getNeighbors())
					if(cells.contains(n) == false && inRange.contains(n) == false)
						cells.add(n);
			}
		}
		
		if(debugging){
			debug.begin(ShapeType.Line);
			debug.setColor(0.6f, 0f, 1f, 0.2f);
			for(Cell c : inRange){
				debug.rect(c.getX()-15, c.getY()-15, 30, 30);
			}
			debug.circle(posX, posY, attackRange);
			debug.end();
		}
		
		Cell[] cellsInRange = new Cell[inRange.size()];
		for(int i = 0; i<inRange.size(); i++){
			cellsInRange[i] = inRange.get(i);
		}
		return cellsInRange;
	}
	
//	Other attribute setters and getters
	/** Sets health to the given amount.
	 * @param newHealth <tt>int</tt> new health.*/
	public void setHealth(int newHealth){
		health = newHealth;
	}
	/** Changes current health by given amount, for subtracting use negative value.
	 * @param amount <tt>int</tt> to be added to the current health.*/
	public void changeHealth(int amount){
		health += amount;
	}
	/** @return <tt>int</tt> current health*/
	public int getHealth(){
		return health;
	}
	
	/** Sets damage to given value.
	 * @param newDmg <tt>float</tt> new damage of this entity.*/
	public void setDamage(float newDmg){
		damage = newDmg;
	}
	/** @return <tt>float</tt> damage of this Entity.*/
	public float getDamage(){
		return damage;
	}
	
	/** Sets attack type of this Entity.
	 * @param newAttackType new attack type, defined by public constants in this class.*/
	public void setAttackType(int newAttackType){
		attackType = newAttackType;
	}
	/** @return attack type of this Entity, given by public constants in this class.*/
	public int getAttackType(){
		return attackType;
	}
	
	/** Sets attack range of this Entity to given value.
	 * @param newAttackRange <tt>float</tt> new range.*/
	public void setAttackRange(float newAttackRange){
		attackRange = newAttackRange;
	}
	/** @return <tt>float</tt> attack range of this Entity.*/
	public float getAttackRange(){
		return attackRange;
	}
	
	/** Sets faction of this Entity, defined by public constants in this class.
	 * @param newFaction new type of relation to other factions from perspective of player.*/
	public void setFaction(int newFaction){
		faction = newFaction;
	}
	/** @return faction of this Entity to this player, that is defined by public constants in this class.*/
	public int getFaction(){
		return faction;
	}
	
	/** Sets behavior of this unit toward other factions, defined by public constants in this class.
	 * @param newBehavior new behavior, defined by constants of this class: <tt>AGRESSIVE PACIFIST AVOIDING</tt>. */
	public void setBehavior(int newBehavior){
		behavior = newBehavior;
	}
	
	/** @return behavior of this Entity towards Entities of other factions. */
	public int getBehavior(){
		return behavior;
	}
	
	/** Piece of AI that dictates what should be done when encountering other Entities, based on faction and behavior. */
	protected void applyBehavior(){
		rotateSprite();
		if (pathTarget != null){
			Cell[] inRange = findCellsInRange();
			for(Cell c : inRange){
				Bag cellBag = c.getBag();
				if (!cellBag.isEmpty()){
					for(int i=0; i<cellBag.size(); i++){
						int cellEntityFaction = cellBag.get(i).getFaction();
						if (cellEntityFaction != faction){
							switch(behavior){
							case AGRESSIVE:
								if (cellEntityFaction != PROJECTILE && cellEntityFaction != NEUTRAL){
									attack(cellBag.get(i)); //opposing faction
								} else if (cellEntityFaction == NEUTRAL){
									attack(cellBag.get(i)); //neutral entity
								}
								break;
							case PACIFIST:
								if (cellEntityFaction != PROJECTILE && cellEntityFaction != NEUTRAL){
									attack(cellBag.get(i)); //opposing faction
								} else if (cellEntityFaction == NEUTRAL){
									//neutral entity
								}
								break;
							case AVOIDING:
								if (cellEntityFaction != PROJECTILE && cellEntityFaction != NEUTRAL){
									//opposing faction
								} else if (cellEntityFaction == NEUTRAL){
									//neutral entity
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/** Sets selection of this Entity to given value.
	 * @param <tt>boolean</tt>*/
	public void setSelected(boolean select){
		selected = select;
	}
	/** @return <tt>boolean</tt> is this Entity selected?*/
	public boolean isSelected(){
		return selected;
	}
	
//	Dispose
	/** Dispose method, that should be called when getting rid of this Entity to prevent memory leaks. */
	public void dispose() {
		collisionRect = null;
		path = null;
		sprite.getTexture().dispose();
	}
	
	/** Sets the state of visual debugging.
	 * @param state <tt>boolean</tt> new state. */
	public void setDebugging(boolean state){
		debugging = state;
	}
}