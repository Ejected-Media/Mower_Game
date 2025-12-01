Global Configuration Constants
These constants define the static rules and content of the game. They do not change during gameplay.

| Constant Name | Data Type | Purpose & Contents |
|---|---|---|
| MAPS | Array<Object> | Defines the available levels. Each object contains:
- id: Unique identifier (e.g., 'yard')
- w, h: Grid dimensions
- rocks: Number of obstacles to spawn
- growth: Base probability of grass growing per tick
- color: The specific green hex code for that map |
| UPGRADES | Array<Object> | Defines items available in the Shop. Includes:
- cost: Price in Cash
- type: Category ('drone', 'hull', 'tech')
- tank/bag: Stats applied to the mower when purchased |
| GRASS_COLORS | Array<String> | Holds the 4 Hex color codes representing the grass growth stages:
Index 0: Cut (Lightest)
Index 3: Tall (Darkest) |
Game Class State Variables
These variables live inside the Game class (this.variable) and track the dynamic state of the world.

| Variable Name | Data Type | Key Responsibilities |
|---|---|---|
| this.money | Integer | The player's current Cash balance. Used for buying upgrades, maps, refuels, and repairs. |
| this.owned | Array<String> | A list of IDs representing everything the player has bought (e.g., ['yard', 'drone', 'autosell']). Checked before unlocking shop buttons. |
| this.grid | 2D Array<Int> | The map matrix. grid[y][x] stores an integer (0-3) representing the growth stage of that specific tile. |
| this.rocks | Array<Object> | Stores the {x, y} coordinates of every rock on the map. Checked every frame for collisions. |
| this.drones | Array<Object> | A list of active drone objects. Each drone tracks its own position (x, y) and movement timer. |
| this.weather | String | Tracks current weather condition ('sunny', 'cloudy', 'rainy'). Affects grass growth speed. |
| this.tileSize | Number | Calculated dynamically based on screen width. Determines how many pixels wide each grid square is drawn. |

The Mower Object (this.mower)
The this.mower object is a complex container holding all stats related to the player's character.

| Property | Data Type | Purpose |
|---|---|---|
| x, y | Integer | The mower's current grid coordinates. |
| dx, dy | Integer | The movement vector. (1, 0) is Right, (0, -1) is Up, etc. |
| fuel | Float | Current fuel level (0-100). Decreases based on movement and throttle settings. |
| bag | Integer | Current count of clippings collected. Capped by maxBag. |
| health | Integer | Hull integrity (0-100). Decreases when hitting rocks. |
| throttle | Integer | The speed setting (1-10) set by the UI slider. Determines the delay between moves. |
| state | String | The Finite State Machine status. Controls behavior:

- 'MOWING': Normal operation
- 'NO_FUEL': Stops movement
- 'BROKEN': Stops movement
- 'BAG_FULL': Stops cutting, but allows movement |
- 
