package com.avilysal.insectConquest;

public class Player {
	private int faction, progression, res1=0, res2=0;
	
	public Player(int faction){
		this.faction = faction;
		progression = 0;
	}
	
	public Player(int faction, int progression){
		this.faction = faction;
		this.progression = progression;
	}
		
	public int getFaction(){
		return faction;
	}
	
	public void changeProgression(int amount){
		progression+=amount;
	}
	public int getProgression(){
		return progression;
	}
	
	public void setRes1(int newRes1){
		res1 = newRes1;
	}
	public void setRes2(int newRes2){
		res2 = newRes2;
	}
	
	public void changeRes1(int amount){
		res1+=amount;
	}
	public void changeRes2(int amount){
		res2+=amount;
	}
	
	public int getRes1(){
		return res1;
	}
	public int getRes2(){
		return res2;
	}
}
