package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {
    private Texture bucketImage;
    private TextureObject bucket;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private EntityManager entityManager;

    @Override
    public void create() {
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        bucket = new TextureObject(bucketImage, 300, 20, 50, 50);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        entityManager = new EntityManager();
        
        // Add the bucket
        entityManager.addEntity(bucket);

        // 1. Create a Circle
        Shapes ball = new Shapes(ShapeType.CIRCLE, 100, 300, Color.RED);
        ball.setDimensions("radius", 50f); 
        entityManager.addEntity(ball);

        // 2. Create a Triangle (falling down with speed -5)
        Shapes tri = new Shapes(ShapeType.TRIANGLE, 400, 400, Color.BLUE, -5f);
        tri.setDimensions("size", 60f);
        entityManager.addEntity(tri);

        // 3. Create a Rectangle
        Shapes box = new Shapes(ShapeType.RECTANGLE, 200, 200, Color.GREEN);
        box.setDimensions("width", 50f);
        box.setDimensions("height", 80f);
        entityManager.addEntity(box);
    }
    
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        
        // 1. Draw Textures (The Bucket)
        batch.begin();
        batch.draw(bucket.getTexture(), 
                   bucket.getX(), 
                   bucket.getY(), 
                   bucket.getTexture().getWidth(), 
                   bucket.getTexture().getHeight());
        batch.end();

        // 2. Draw Shapes (The Circle, Triangle, Box)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(shapeRenderer); 
        shapeRenderer.end();
    }
    
    @Override
    public void dispose() {
        bucketImage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}