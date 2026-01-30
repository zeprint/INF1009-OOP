package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameMaster extends ApplicationAdapter{
	private Texture bucketImage;
	private TextureObject bucket;
	private SpriteBatch batch;
	private Circle circle;
	private EntityManager entityManager;

	@Override
	public void create() {
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		bucket = new TextureObject(bucketImage, 300, 20, 50, 50);

		batch = new SpriteBatch();
		
		entityManager = new EntityManager();
		
		entityManager.addEntity(bucket);
	}
	
	@Override
	public void render() {
	    ScreenUtils.clear(0, 0, 0.2f, 1);
	    
	    bucket.movement(5);

	    batch.begin();
	    
	    
	    batch.draw(bucket.getTexture(), 
	               bucket.getX(), 
	               bucket.getY(), 
	               bucket.getTexture().getWidth(), 
	               bucket.getTexture().getHeight());
	    
	    batch.end();

	}
	
	@Override
	public void dispose() {
		bucketImage.dispose();
		batch.dispose();
	}
}