package com.avilysal.insectConquest;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "InsectConquest";
		cfg.useGL20 = true;
		cfg.width = 640;
		cfg.height = 640;
		
		new LwjglApplication(new InsectConquest(), cfg);
	}
}
