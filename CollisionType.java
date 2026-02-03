public class CollisionType {

    private final String name;
    private final boolean blocksMovement;
    private final boolean triggersEvent;

    public CollisionType(String name, boolean blocksMovement, boolean triggersEvent) {
        this.name = name;
        this.blocksMovement = blocksMovement;
        this.triggersEvent = triggersEvent;
    }

    public String getName() {
        return name;
    }

    public boolean blocksMovement() {
        return blocksMovement;
    }

    public boolean triggersEvent() {
        return triggersEvent;
    }
}
