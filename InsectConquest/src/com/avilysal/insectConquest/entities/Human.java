package com.avilysal.insectConquest.entities;

import com.avilysal.insectConquest.map.Map;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Human extends Entity{
	private final int FACTION = HUMAN;
	private final int ATTACK_TYPE = RANGED;
	private final int BASE_HEALTH = 100;
	private final float BASE_RANGE = 100;
	private final float BASE_DAMAGE = 20;
	private final float BASE_SPEED = 40f;
	
	private final float collisionWidth = 10;
	private final float collisionHeight = 10;
		
	public Human(ShapeRenderer visualDebug, Map map, Sprite sprite, float x, float y) {
		this.debug = visualDebug;
		this.map = map;
		this.sprite = sprite;
		setCollisionRect(collisionWidth, collisionHeight);
		setPosition(x,y);
		
		setCell(map.getCell(getPositionX(), getPositionY()));
		setCellDimensions(map.getTileWidth(), map.getTileHeight());
		
		setFaction(FACTION);
		setHealth(BASE_HEALTH);
		setAttackType(ATTACK_TYPE);
		setAttackRange(BASE_RANGE);
		setDamage(BASE_DAMAGE);
		
		setSpeed(BASE_SPEED);
		setVelocity(0);
		
		setBehavior(AGRESSIVE);
		setDebugging(false);
	}

	@Override
	public void ai(float delta) {
		if (path != null){
			followPath(delta);
		} else {
			if (pathTarget != null)
				setPathTo(pathTarget.getPositionX(), pathTarget.getPositionY());
		}
		applyBehavior();
	}

	@Override
	public void attack(Entity ent) {
		delPath();
	}

	@Override
	public void defend(Entity ent) {
		
	}

	@Override
	public void die(float delta) {
		
	}
}
