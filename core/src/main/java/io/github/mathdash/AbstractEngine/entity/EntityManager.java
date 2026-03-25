package io.github.mathdash.AbstractEngine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * EntityManager - Creates, manages, updates, and disposes all entities.
 * Uses deferred queues to safely add and remove entities during updates.
 */

public class EntityManager {

    private final ObjectMap<String, Entity> entitiesById;
    private final Array<Entity> entityList;

    // Deferred queues — processed at the start of each update cycle
    private final Array<Entity> pendingAdd;
    private final Array<Entity> pendingRemove;

    // Creates a new, empty EntityManager.
    public EntityManager() {
        this.entitiesById = new ObjectMap<>();
        this.entityList = new Array<>();
        this.pendingAdd = new Array<>();
        this.pendingRemove = new Array<>();
    }

    // ---- Entity Lifecycle ----

    // Queues an entity to be added at the start of the next update cycle.
    public void addEntity(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Cannot add a null entity.");
        }
        if (entitiesById.containsKey(entity.getId())) {
            throw new IllegalArgumentException(
                "An entity with ID '" + entity.getId() + "' already exists."
            );
        }
        pendingAdd.add(entity);
    }

    // Queues an entity to be removed at the start of the next update cycle.
    public void removeEntity(Entity entity) {
        if (entity != null) {
            pendingRemove.add(entity);
        }
    }

    // Queues an entity for removal by its ID.
    public void removeEntity(String id) {
        Entity entity = entitiesById.get(id);
        if (entity != null) {
            pendingRemove.add(entity);
        }
    }

    // Processes pending additions and removals, then updates all active entities.
    public void update(float deltaTime) {
        // Process deferred operations first
        processPending();

        // Update all active entities
        for (int i = 0; i < entityList.size; i++) {
            Entity entity = entityList.get(i);
            if (entity.isActive()) {
                entity.update(deltaTime);
            }
        }
    }

    // Disposes all entities and clears the manager.
    public void dispose() {
        // Also dispose anything in the pending queue
        for (int i = 0; i < pendingAdd.size; i++) {
            pendingAdd.get(i).dispose();
        }
        pendingAdd.clear();
        pendingRemove.clear();

        for (int i = 0; i < entityList.size; i++) {
            entityList.get(i).dispose();
        }
        entityList.clear();
        entitiesById.clear();
    }

    // Retrieves an entity by its unique ID.
    public Entity getEntity(String id) {
        return entitiesById.get(id);
    }

    // Returns all entities of a given type (class or subclass).
    @SuppressWarnings("unchecked")
    public <T> Array<T> getEntitiesByType(Class<T> type) {
        Array<T> result = new Array<>();
        for (int i = 0; i < entityList.size; i++) {
            Entity entity = entityList.get(i);
            if (type.isInstance(entity)) {
                result.add((T) entity);
            }
        }
        return result;
    }

    // Returns all entities that have a specific component type.
    public Array<Entity> getEntitiesWithComponent(Class<? extends Component> componentType) {
        Array<Entity> result = new Array<>();
        for (int i = 0; i < entityList.size; i++) {
            Entity entity = entityList.get(i);
            if (entity.hasComponent(componentType)) {
                result.add(entity);
            }
        }
        return result;
    }

    // Returns the total number of managed entities (excluding pending).
    public int getEntityCount() {
        return entityList.size;
    }

    // Returns a copy of all entities.
    public Array<Entity> getAllEntities() {
        return new Array<>(entityList);
    }

    // ---- Internal ----

    // Processes the deferred add and remove queues.
    private void processPending() {
        // Process removals
        if (pendingRemove.size > 0) {
            for (int i = 0; i < pendingRemove.size; i++) {
                Entity entity = pendingRemove.get(i);
                if (entitiesById.containsKey(entity.getId())) {
                    entityList.removeValue(entity, true);
                    entitiesById.remove(entity.getId());
                    entity.dispose();
                }
            }
            pendingRemove.clear();
        }

        // Process additions
        if (pendingAdd.size > 0) {
            for (int i = 0; i < pendingAdd.size; i++) {
                Entity entity = pendingAdd.get(i);
                entitiesById.put(entity.getId(), entity);
                entityList.add(entity);
            }
            pendingAdd.clear();
        }
    }
}