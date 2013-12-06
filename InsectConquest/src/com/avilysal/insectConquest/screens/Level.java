package com.avilysal.insectConquest.screens;

import java.util.ArrayList;
import java.util.Random;

import com.avilysal.insectConquest.entities.Builder;
import com.avilysal.insectConquest.map.Map;
import com.avilysal.insectConquest.util.Bag;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Level implements Screen, InputProcessor{
	
//	GUI flags
	
//	GUI elements
	//TODO create button for the tower
	private TextureAtlas atlas;
	private BitmapFont font;
	private SpriteBatch guiBatch;
//	Map and rendering elements
	private boolean initialized = false;
	private float[][] spawnXY;
	private TiledMap tiledMap;
	private Map map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
//	Entities
	private Random random;
	private Bag projectiles, friendly, neutral, hostile;
//	Resource management stuff
	private int humanDNA = 0;
	private int insectDNA = 0;
	
	public int getHumanDNA(){
		return humanDNA;
	}
	public int getInsectDNA(){
		return insectDNA;
	}
	public boolean addHumanDNA(int amount){
		humanDNA += amount;
		if (humanDNA > 0)
			return true;
		else
			return false;
	}
	public boolean addRes2(int amount){
		insectDNA += amount;
		if (insectDNA > 0)
			return true;
		else
			return false;
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.setView(camera);
		renderer.render();
		
		renderer.getSpriteBatch().begin();
		
		
		//debugging purposes rendering number of containments of every cell's bag
		font.setColor(font.getColor().r, font.getColor().g, font.getColor().b,0.5f);
		for(int column = 0; column < map.grid.length; column++)
			for(int row = 0; row < map.grid[column].length; row++){
				font.draw(renderer.getSpriteBatch(), ""+map.grid[column][row].getBag().size(), map.grid[column][row].getX()+13, map.grid[column][row].getY());
			}
		font.setColor(font.getColor().r, font.getColor().g, font.getColor().b,1f);
		//end debugging purposes
		
		
		for(int i = 0; i<projectiles.size(); i++)
			if (projectiles.get(i) != null)
				projectiles.get(i).draw(renderer.getSpriteBatch());
		for(int i = 0; i<friendly.size(); i++)
			if (friendly.get(i) != null)
				friendly.get(i).draw(renderer.getSpriteBatch());
		for(int i = 0; i<neutral.size(); i++)
			if (neutral.get(i) != null)
				neutral.get(i).draw(renderer.getSpriteBatch());
		for(int i = 0; i<hostile.size(); i++)
			if (hostile.get(i) != null)
				hostile.get(i).draw(renderer.getSpriteBatch());
		renderer.getSpriteBatch().end();
		
		guiBatch.begin();
		/*TODO
		 * Render resources to GUI
		 * Render enemies left to GUI
		 */
		font.drawMultiLine(guiBatch, " fps:"+Gdx.graphics.getFramesPerSecond()+
				"\n memory usage: "+Gdx.app.getJavaHeap()/1000000L+" mb"+
				"\n delta time: "+Gdx.graphics.getDeltaTime()+
				"\n cam x:"+camera.position.x+" cam y:"+camera.position.y+" cam zoom:"+camera.zoom+
				"\n mouse x:"+Gdx.input.getX()+" mouse y:"+(Gdx.graphics.getHeight()-Gdx.input.getY())+
				"\n arach x:"+(friendly.get(0).getPositionX())+
				"\n arach y:"+(friendly.get(0).getPositionY())
				, 10, Gdx.graphics.getHeight()-25);
//		rendering resource amounts
		font.drawMultiLine(guiBatch, humanDNA +" human dna"+
				"\n"+insectDNA+" insect dna", Gdx.graphics.getWidth()-150, Gdx.graphics.getHeight()-25);
		
		/* path finding nodes, for debugging purposes
		if(enemy.getPath() != null)
			for(Vector2 node : enemy.getPath().getAllPoints()){
				Sprite nodeSprite;
				if(enemy.getCurDest() != node)
					nodeSprite = new Sprite(new Texture("img/pathNodeNew.png"));
				else
					nodeSprite = new Sprite(new Texture("img/pathNodeOld.png"));
				nodeSprite.setPosition(node.x, node.y);
				nodeSprite.draw(guiBatch);
			}
		*/
		guiBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override //method called upon creation
	public void show() {
		Gdx.input.setInputProcessor((InputProcessor) ((Game) Gdx.app.getApplicationListener()).getScreen());
		
		if(!initialized){
			init();
		}
	}
	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		//dispose of map and it's components
		tiledMap.dispose();
		renderer.dispose();
		atlas.dispose();
		//dispose of GUI
		guiBatch.dispose();
		font.dispose();
		projectiles.clear();
		friendly.clear();
		neutral.clear();
		hostile.clear();
	}
	
	private void init(){
		atlas = new TextureAtlas("map/TDTileSet.pack");
		tiledMap = new TmxMapLoader().load("map/level1.tmx");
		random = new Random();
		map = new Map(tiledMap);
		
		projectiles = new Bag();
		friendly = new Bag();
		neutral = new Bag();
		hostile = new Bag();
		
		renderer = new OrthogonalTiledMapRenderer(tiledMap);
		camera = new OrthographicCamera();
		camera.translate(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
//		TODO
//		optimize camera view
//		camera.frustum culling for rendering only that, which is in the camera view
//		look into Octrees as well
		
		setSpawnableCoordiantes();
		
		guiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		
		initialized = true;
		createBuilder();
	}
	
	public Map getMap(){
		return map;
	}
	
	private void createBuilder(){
		int spawnIndex = random.nextInt(spawnXY.length);
		friendly.add(new Builder(map, atlas.createSprite("Arach"), 319, 321, 10, 10));
	}
	
	private void createNewEnemies(int amount){
		for(int i=0; i<amount; i++){
			float spawnIndex = random.nextInt();
		}
	}
	
	private void setSpawnableCoordiantes(){
		ArrayList<Vector2> useableTiles = new ArrayList<Vector2>();
		TiledMapTileLayer ground = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		
		float tileHeight = ground.getTileHeight();
		float tileWidth = ground.getTileWidth();
		
		System.out.println("setting up spawn tiles");
		
		for(int i = ground.getWidth()-1; i >= 0; i--){
			for(int j = ground.getHeight()-1; j >= 0; j--){
				if( i == 0 || i == ground.getWidth()-1 || j == ground.getHeight()-1 || j == 0){
					if(ground.getCell(i,j).getTile().getProperties().containsKey("blocked") == false) // :)
						useableTiles.add(new Vector2(i*tileWidth, j*tileHeight));
				}
			}
		}
		
		spawnXY = new float[useableTiles.size()][2];
		for(int i=0; i<useableTiles.size(); i++){
			spawnXY[i][0] = useableTiles.get(i).x;
			spawnXY[i][1] = useableTiles.get(i).y;
		}
		System.out.println("spawn tiles set");
	}
	
	@Override
	public boolean keyDown(int keycode) {
		int cameraSpeed = 100;
		switch(keycode){
		case Keys.ESCAPE:
			Gdx.app.exit();
			break;
		case Keys.SPACE:
			break;
		case Keys.N:
			break;
		case Keys.UP:
			camera.translate(0*camera.zoom, cameraSpeed*camera.zoom);
			camera.update();
			break;
		case Keys.RIGHT:
			camera.translate(cameraSpeed*camera.zoom, 0*camera.zoom);
			camera.update();
			break;
		case Keys.DOWN:
			camera.translate(0*camera.zoom, -cameraSpeed*camera.zoom);
			camera.update();
			break;
		case Keys.LEFT:
			camera.translate(-cameraSpeed*camera.zoom, 0*camera.zoom);
			camera.update();
			break;
		default:
			break;
		}
		return true;
	}
	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		switch(button){
		case 0:
//			Vector3 vec = new Vector3(screenX, screenY, 0);
//			camera.unproject(vec);
//			friendly.get(0).setVelocity(new Vector2(vec.x, vec.y));
/*			if(!captured && !builderSummoned){
				Tween.to(arach, SpriteAccessor.POSITION, (float)Math.sqrt(vec.x*vec.x + vec.y*vec.y)/200).
				target(vec.x-arach.getWidth()/2, vec.y-arach.getHeight()/2).start(manager);
			} else if (captured){
				builderSummoned = true;
				Tween.to(arach, SpriteAccessor.POSITION, (float)Math.sqrt(vec.x*vec.x + vec.y*vec.y)/200).
				target(vec.x-arach.getWidth()/2, vec.y-arach.getHeight()/2).setCallback(
						new TweenCallback() {
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								towers.get(towers.size()-1).setPosition(arach.getX(), arach.getY());
								Tween.set(towers.get(towers.size()-1), SpriteAccessor.ALPHA).target(1f).start(manager);
								captured = false;
								builderSummoned = false;
							}
						}).start(manager);
			}
*/			break;
		default:
			break;
		}
		return true;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//if not pointing at GUI element
		if(Gdx.input.isButtonPressed(1)){
			camera.translate(-Gdx.input.getDeltaX()*camera.zoom, Gdx.input.getDeltaY()*camera.zoom);
			camera.update();
		}
		return true;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
			Vector3 vec = new Vector3(screenX, screenY, 0);
			camera.unproject(vec);
		return true;
	}
	@Override
	public boolean scrolled(int amount) {
		switch(amount){
		case 1:
			camera.zoom += 0.05;
			if(camera.zoom >= 2) camera.zoom = 2;
			camera.update();
			break;
		case -1:
			camera.zoom -= 0.05;
			if(camera.zoom <= 0.09) camera.zoom = 0.1f;
			camera.update();
			break;
		default:
			break;
		}
		return true;
	}
}