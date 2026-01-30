package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EntityManager {
	private Array<Entity> entityList;
	
	// add entity
	public void addEntity(Entity entity) {
		entityList.add(entity);
	}
	
	// remove entity
	public void removeEntity(Entity entity, boolean identity) {
		entityList.removeValue(entity, identity);
	}
	
	// get entity list
	public Array<Entity> getEntityList() {
		return entityList;
	}
	
	// update entity list
	/*public void updateList() {
		for (Entity entity : entityList) {
			entity.update();
		}
	}*/
	
	// allow entity to move with their own speeds by calling movement function
	/*public void MovementComponent(float speed) {
		for (Entity entity : entityList) {
			entity.MovementComponent(speed);
		}
	}*/
	
	// draw entity
	public void draw(ShapeRenderer shape) {
		for (Entity entity : entityList) {
			entity.draw(shape);
		}
	}
	
	// constructor for entity list
	public EntityManager() {
		entityList = new Array<Entity>();
	}

}
