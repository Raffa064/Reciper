package com.raffa064.reciper;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.raffa064.reciper.screens.GameScreen;

public class MyGdxGame extends Game {
	
	private AndroidOperations aOperations;
	
	public MyGdxGame(AndroidOperations androidOperations) {
		aOperations = androidOperations;
	}
	
	@Override
	public void create() {
		setScreen(new GameScreen());
	}

	@Override
	public void render() {        
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}


}
