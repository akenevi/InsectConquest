package com.avilysal.insectConquest.entities;

import java.util.ArrayList;

import com.avilysal.insectConquest.map.Cell;
import com.avilysal.insectConquest.map.Map;
import com.avilysal.insectConquest.util.Path;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Disposable{
	private float posX, posY, cellX, cellY, cellWidth, cellHeight;
	private Cell cell;
	
	protected Map map;
	protected Sprite sprite;
	
	private Rectangle collisionRect;
	private int health, attackType, faction;
	private float speed, damage, attackRange;
	
	private Vector2 velocity;
	private boolean selected = false;
	private Path path;
	
//	Constants for the type
	public static final int PROJECTILE = 0;
	public static final int FRIENDLY = 1;
	public static final int NEUTRAL = 2;
	public static final int HOSTILE = 3;
	
//	Type determines the use of attack and some AI behavior
	public static final int NONE = 0;
	public static final int MELEE = 1;
	public static final int RANGED = 2;
	
	
//	Updating and drawing
	/** Override of {@link Sprite#draw(com.badlogic.gdx.graphics.g2d.Batch)} to include {@link #update(float)} method.
	 * @param batch SpriteBatch to draw onto.*/
	public void draw(Batch batch){
		update(Gdx.graphics.getDeltaTime());
		sprite.draw(batch);
	}
	/** If {@link #health} < 0 calls {@link #die(float)}, otherwise calls {@link #ai(float)}. Passes delta time to the method called.
	 * @param delta <tt>float</tt> equals to the return value of <tt>Gdx.graphics.getDeltaTime()</tt>*/
	public void update(float delta){
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
		updateCollisionRectPosition(posX, posY);
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
	/** Sets the velocity {@link Vector2} to given parameters.
	 * @param xVel <tt>float</tt> sets x axis of velocity {@link Vector2} to given amount.
	 * @param yVel <tt>float</tt> sets y axis of velocity {@link Vector2} to given amount.*/
	public void setVelocity(float xVel, float yVel){
		velocity.x = xVel;
		velocity.y = yVel;
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
			
			if(posX > map.getTileWidth()*map.grid.length){
				setPosition(map.getTileWidth()*map.grid.length, posY);
				cellX = cellWidth;
			}
			if(posX < 0){
				setPosition(0, posY);
				cellX = 0;
			}
			if(posY > map.getTileHeight()*map.grid[0].length){
				setPosition(posX, map.getTileHeight()*map.grid.length);
				cellY = cellHeight;
			}
			if(posY < 0){
				setPosition(posX, 0);
				cellY = 0;
			}
			
			updateCell();
		}
	}
	
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
		initCellXCellY();
	}
	/** Initiates position within spawning {@link Cell}.*/
	private void initCellXCellY(){
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
	
	
	
	
	
	
	
	
	
	
	
	
	protected boolean rayCast(float finishX, float finishY){
		float startX = 0, startY = 0, toXWall = 0, toYWall = 0;
		
		Vector2 path = new Vector2(new Vector2(finishX, finishY).sub(posX, posY));
		Vector2 tempV = new Vector2(posX,posY);
		float pathAngle = path.angle();
		
		while(true){
			
			startX = tempV.x - ((int)tempV.x%cellWidth)*cellWidth; 
			startY = tempV.y - ((int)tempV.y%cellHeight)*cellHeight;
			toXWall = cellWidth - startX;
			toYWall = cellHeight - startY;
			
			tempV = tempV.add(path.cpy().limit((float) Math.sqrt(toXWall*toXWall + toYWall*toYWall)));
			
			if(!map.getCell(tempV.x, tempV.y).isTraversable())
				return false;
			
			if(pathAngle >= 0f && pathAngle < 90f){
				if(tempV.x > finishX && tempV.y > finishX)
					break;
			} else if (pathAngle >= 90f && pathAngle < 180){
				if(tempV.x < finishX && tempV.y > finishX)
					break;
			} else if (pathAngle >= 180 && pathAngle < 270){
				if(tempV.x < finishX && tempV.y < finishY)
					break;
			} else if (pathAngle >= 360 && pathAngle < 0){
				if(tempV.x > finishX && tempV.y < finishY)
					break;
			}
		}
		return true;
	}
	
	public boolean isInLineOfSight(Entity ent){
		return rayCast(ent.getCell().getX(), ent.getCell().getY());
	}
	
	private void reactivePathFollowing(){
		Vector2 dirVect = new Vector2(posX,posY).add(velocity.cpy().scl(10f));
		Vector2 fov = velocity.cpy().scl(10f);
		fov.setAngle(fov.angle()-90);
		Vector2 rightSide = dirVect.cpy().add(fov);
		Vector2 leftSide = dirVect.cpy().sub(fov);
		
		if(rayCast(rightSide.x, rightSide.y) && rayCast(leftSide.x, leftSide.y)){
			//continue moving to next node
		} else if (!rayCast(rightSide.x, rightSide.y) && rayCast(leftSide.x, leftSide.y)){
			//obstruction on right side, turn left until cleared
		} else if (rayCast(rightSide.x, rightSide.y) && !rayCast(leftSide.x, leftSide.y)){
			//obstruction on left side, turn right until cleared
		} else if (!rayCast(rightSide.x, rightSide.y) && !rayCast(leftSide.x, leftSide.y)){
			//obstruction on both sides
		}
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
	/** @return faction of relation of this Entity to this player, that is defined by public constants in this class.*/
	public int getFaction(){
		return faction;
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
	public void dispose() {
		collisionRect = null;
		sprite.getTexture().dispose();
	}
}