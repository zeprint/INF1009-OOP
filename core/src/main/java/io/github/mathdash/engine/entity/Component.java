package io.github.mathdash.engine.entity;

/**
 * Component - Base interface for all components that can be attached to an Entity.
 * Components define an entity's data and behavior through composition.
 */ 

public interface Component {

    // Called once when this component is first attached to an entity.
    void init(Entity owner);

    // Called every frame to update this component's state.
    void update(float deltaTime);

    // Called when this component is removed from its entity or the entity is disposed.
    void dispose();
}