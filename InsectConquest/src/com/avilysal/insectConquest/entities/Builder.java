package com.avilysal.insectConquest.entities;

import com.avilysal.insectConquest.map.Map;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Builder extends Entity{
	private final int FACTION = FRIENDLY;
	private final int ATTACK_TYPE = RANGED;
	private final int BASE_HEALTH = 100;
	private final float BASE_RANGE = 300;
	private final float BASE_DAMAGE = 20;
	private final float BASE_SPEED = 15f;
	
	public Builder(Map map, Sprite sprite, float x, float y, float collisionWidth, float collisionHeight) {
		this.map = map;
		this.sprite = sprite;
		setPosition(x,y);
		setCollisionRect(collisionWidth, collisionHeight);
		
		setCell(map.getCell(getPositionX(), getPositionY()));
		setCellDimensions(map.getTileWidth(), map.getTileHeight());
		
		setFaction(FACTION);
		setAttackType(ATTACK_TYPE);
		setHealth(BASE_HEALTH);
		setAttackRange(BASE_RANGE);
		setDamage(BASE_DAMAGE);
		
		setSpeed(BASE_SPEED);
		setVelocity(new Vector2(0,1));
	}

	@Override
	public void ai(float delta) {
		move(delta);
		rotateVelocityVector(1f);
	}

	@Override
	public void attack(Entity ent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defend(Entity ent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void die(float delta) {
		// TODO Auto-generated method stub
		
	}
}
