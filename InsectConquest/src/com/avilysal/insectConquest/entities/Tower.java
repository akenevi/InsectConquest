package com.avilysal.insectConquest.entities;

import com.avilysal.insectConquest.map.Map;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tower extends Entity{
	private final int FACTION = NEUTRAL;
	private final int ATTACK_TYPE = MELEE;
	private final int BASE_HEALTH = 100;
	private final float BASE_RANGE = 20;
	private final float BASE_DAMAGE = 20;
	private final float BASE_SPEED = 0f;
	
	private int upgrade_state = 0;
	private boolean upgrading = false;
	
	private final float collisionWidth = 10;
	private final float collisionHeight = 10;
	
	public Tower(ShapeRenderer visualDebug, Map map, Sprite sprite, float x, float y) {
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
	}
	
	public void upgrade(int upgNum){
		upgrade_state = upgNum;
		upgrading = true;
	}
	
	@Override
	public void ai(float delta) {
		applyBehavior();
	}
	@Override
	public void attack(Entity ent) {
		
	}
	@Override
	public void defend(Entity ent) {
		
	}
	@Override
	public void die(float delta) {
		
	}
}