package com.avilysal.insectConquest;

import com.avilysal.insectConquest.screens.Level;
import com.badlogic.gdx.Game;

public class InsectConquest extends Game {
	@Override
	public void create() {
		this.setScreen(new Level());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

	@Override
	public void render() {
		super.render();	
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}