# Abstract Engine — Final Report

## 1. Overview

The Abstract Engine is a **non-contextual**, reusable foundation built with **libGDX only**.
The Logic Engine (GameScene, Bucket, Droplet) is the **contextual** game layer built on top.

```
┌─────────────────────────────────────────────────────┐
│                  Abstract Engine                    │
│                                                     │
│  GameMaster ─── coordinates all managers below      │
│                                                     │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐      │
│  │  Scene     │ │  Entity    │ │  Movement  │      │
│  │  Manager   │ │  Manager   │ │  Manager   │      │
│  └────────────┘ └────────────┘ └────────────┘      │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐      │
│  │ Collision  │ │   Input    │ │   Audio    │      │
│  │  Manager   │ │  Manager   │ │  Manager   │      │
│  └────────────┘ └────────────┘ └────────────┘      │
├─────────────────────────────────────────────────────┤
│                   Logic Engine                      │
│                                                     │
│  GameScene ─── Bucket, Droplet ─── game rules       │
└─────────────────────────────────────────────────────┘
```

---

## 2. Complete File List (39 files)

### Interfaces (12)

| # | Interface | Purpose | Implemented by |
|---|-----------|---------|----------------|
| 1 | `IEntity` | Entity lifecycle: initialize, update, dispose | Entity |
| 2 | `Renderable` | Draw with SpriteBatch / ShapeRenderer | Entity |
| 3 | `ISceneSystem` | Scene add, load, switch, dispose | SceneManager |
| 4 | `IMovementSystem` | Register/update movement components | MovementManager |
| 5 | `ICollisionSystem` | Collision detection and resolution | CollisionManager |
| 6 | `IInputSystem` | Keyboard/mouse input polling | InputManager |
| 7 | `IAudioSystem` | Sound loading and playback | AudioManager |
| 8 | `Collidable` | Entity participates in collisions | Bucket, Droplet |
| 9 | `HasRotation` | Entity exposes rotation angle | RotatingShape |
| 10 | `Acceleratable` | Component has acceleration | GravityMovement |
| 11 | `Rotatable` | Component has angular velocity | RotationComponent |
| 12 | `DistributionType` | Pluggable random strategy | MobileRandom |

### Abstract Engine Classes (24)

| # | Class | Role |
|---|-------|------|
| 1 | `GameMaster` | Engine coordinator — creates managers, runs game loop |
| 2 | `Entity` *(abstract)* | Base for all simulation objects |
| 3 | `TextureObject` | Entity drawn with a Texture (SpriteBatch) |
| 4 | `Shapes` | Entity drawn as a primitive shape |
| 5 | `RotatingShape` | Entity drawn as a rotating shape |
| 6 | `Scene` *(abstract)* | Base for all scenes |
| 7 | `SimulationScene` | Scene with full engine pipeline |
| 8 | `PauseScene` | Semi-transparent pause overlay |
| 9 | `MovementComponent` *(abstract)* | Base for movement logic |
| 10 | `GravityMovement` | Gravity + acceleration movement |
| 11 | `RotationComponent` | Angular rotation movement |
| 12 | `EntityManager` | Manages all entities |
| 13 | `SceneManager` | Manages all scenes |
| 14 | `MovementManager` | Manages movement components |
| 15 | `CollisionManager` | Detects/resolves collisions |
| 16 | `InputManager` | Polls keyboard/mouse state |
| 17 | `AudioManager` | Loads and plays sounds |
| 18 | `InputBindings` | Maps keys → actions/axes |
| 19 | `CollisionResult` | Immutable collision data |
| 20 | `CollisionType` | Collision behaviour config |
| 21 | `MobileRandom` | Uniform random generator |
| 22 | `ShapeType` *(enum)* | CIRCLE, RECTANGLE, TRIANGLE |
| 23 | `InputAction` *(enum)* | TOGGLE_MOUSE_MODE, DEBUG, MUTE, PAUSE |
| 24 | `InputAxis` *(enum)* | MOVE_X, MOVE_Y |

### Logic Engine Classes (3) — Game-Specific

| # | Class | Role |
|---|-------|------|
| 1 | `GameScene` | Sets up game entities, handles bucket input |
| 2 | `Bucket` | Player-controlled bucket, plays sound on collision |
| 3 | `Droplet` | Falling water droplet, resets on collision |

---

## 3. Inheritance Hierarchy

```
ApplicationAdapter (libGDX)
  └── GameMaster

Entity (abstract)  ← implements IEntity, Renderable
  ├── TextureObject
  │     ├── Bucket        ← implements Collidable  [Logic Engine]
  │     └── Droplet       ← implements Collidable  [Logic Engine]
  ├── Shapes
  └── RotatingShape       ← implements HasRotation

Scene (abstract)
  ├── SimulationScene
  │     └── GameScene                               [Logic Engine]
  └── PauseScene

MovementComponent (abstract)
  ├── GravityMovement     ← implements Acceleratable
  └── RotationComponent   ← implements Rotatable
```

---

## 4. How Managers Connect (DIP)

GameMaster creates concrete managers once, stores them as interfaces:

```
GameMaster creates:              Stores as:
───────────────────              ──────────
new SceneManager()           →   ISceneSystem
new MovementManager()        →   IMovementSystem
new CollisionManager()       →   ICollisionSystem
new InputManager(bindings)   →   IInputSystem
new AudioManager()           →   IAudioSystem
new EntityManager()          →   EntityManager
```

GameScene and SimulationScene only see the interface types.
Swapping any manager (e.g. a different collision algorithm) requires
changing only the one `new` line in GameMaster.

---

## 5. Game Demo — What Runs

```
 Screen Layout
┌─────────────────────────────────┐
│  △ rotating     ○ bouncing      │
│    triangle       circle        │
│                                 │
│              ◆ static square    │
│                                 │
│  💧  💧  💧  💧  💧  (falling)   │
│                                 │
│          🪣 bucket (A/D keys)   │
└─────────────────────────────────┘

Press P or ESC → pause/unpause
Bucket catches droplet → click.wav plays
```

### Game Entities and Which Systems They Use

| Entity | EntityMgr | MovementMgr | CollisionMgr | InputMgr | AudioMgr |
|--------|:---------:|:-----------:|:------------:|:--------:|:--------:|
| Bucket | ✓ | — | ✓ | ✓ (A/D) | ✓ (click) |
| Droplet ×5 | ✓ | ✓ (gravity) | ✓ | — | — |
| Rotating △ | ✓ | ✓ (rotation) | — | — | — |
| Moving ○ | ✓ | ✓ (linear) | — | — | — |
| Static ◆ | ✓ | — | — | — | — |

---

## 6. Per-Frame Pipeline

```
GameMaster.render()
  │
  ├── 1. inputSystem.update()         ← poll keyboard/mouse
  ├── 2. check TOGGLE_PAUSE           ← switch scenes if pressed
  │
  ├── 3. if NOT paused:
  │       GameScene.update(dt)
  │         ├── handleBucketInput()    ← move bucket with A/D
  │         ├── handleCircleBounce()   ← bounce circle off walls
  │         └── super.update(dt)
  │               ├── MovementMgr.update()     ← apply velocity
  │               ├── EntityMgr.update()       ← per-entity logic
  │               └── CollisionMgr.check()     ← detect & resolve
  │
  ├── 4. GameScene.render()           ← always (frozen when paused)
  │       ├── SpriteBatch pass        ← textures (bucket, droplets)
  │       └── ShapeRenderer pass      ← shapes (triangle, circle, square)
  │
  └── 5. if paused:
          PauseScene.render()          ← dark overlay + "PAUSED" text
```

---

## 7. SOLID Compliance

### S — Single Responsibility

| Class | One job |
|-------|---------|
| EntityManager | Manage entity lifecycle |
| CollisionManager | Detect and resolve collisions |
| MovementManager | Register and update movement components |
| InputManager | Poll and expose input state |
| AudioManager | Load and play sounds |
| SceneManager | Register and switch scenes |
| GameMaster | Wire everything together |
| GameScene | Game-specific rules and setup |

### O — Open/Closed

New entity types → subclass Entity (no engine changes needed).
New movement types → subclass MovementComponent.
New scenes → subclass Scene or SimulationScene.
New collision systems → implement ICollisionSystem.

### L — Liskov Substitution

**Fixed:** RotationComponent now checks `instanceof HasRotation`
instead of unsafe cast to RotatingShape. Any Entity subclass can
safely have a RotationComponent attached.

### I — Interface Segregation

**Fixed:** MovementComponent no longer implements IEntity.
A movement component is not an entity — it shouldn't be forced
to have initialize() or dispose().

Each interface is small and focused:
- IEntity: initialize, update, dispose
- Renderable: draw(SpriteBatch), draw(ShapeRenderer)
- Collidable: getBounds, getType, onCollision
- Acceleratable: getAccelerationX, getAccelerationY
- Rotatable: getAngularVelocity
- HasRotation: setRotationAngle, getRotationAngle

### D — Dependency Inversion

Every manager is behind an interface:

| Consumer | Depends on | Not on |
|----------|-----------|--------|
| GameMaster | IAudioSystem | ~~AudioManager~~ |
| GameMaster | ISceneSystem | ~~SceneManager~~ |
| SimulationScene | IMovementSystem | ~~MovementManager~~ |
| SimulationScene | ICollisionSystem | ~~CollisionManager~~ |
| SimulationScene | IInputSystem | ~~InputManager~~ |
| SimulationScene | IAudioSystem | ~~AudioManager~~ |
| Bucket | IAudioSystem | ~~AudioManager~~ |
| GravityMovement | DistributionType | ~~MobileRandom~~ |

---

## 8. Changes Made in This Final Pass

| Change | Type | Details |
|--------|------|---------|
| `Updateable` → `IEntity` | Rename (UML) | Added initialize(), dispose() per UML spec |
| `AudioOutput` → `IAudioSystem` | Rename (UML) | All references updated |
| Duplicated `updateEntityPosition()` | DRY fix | Extracted to `MovementComponent.applyVelocity()` |
| `MovementComponent implements Updateable` | ISP fix | Removed — not an entity |
| `SceneManager.loadScene()` re-creates | Bug fix | Now tracks created state, only calls create() once |
| `InputAction.TOGGLE_PAUSE` | Feature | Added for scene switching |
| `GameScene`, `Bucket`, `Droplet` | Logic Engine | Complete playable game demo |

---

## 9. Assets Required

Place these in your project's `assets/` folder:

| File | Purpose |
|------|---------|
| `bucket.png` | Bucket sprite (player) |
| `droplet.png` | Water droplet sprite |
| `click.wav` | Sound effect on catch |
