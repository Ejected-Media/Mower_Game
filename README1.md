Core Game Engine Functions

| Function Name | Purpose | Key Responsibilities |
|---|---|---|
| init() | Game Bootstrapper | The entry point. It loads saved data from LocalStorage, loads the default map, resizes the screen, and starts the animation loop. |
| loop() | The Heartbeat | Runs ~60 times per second. It triggers growth, checks state (fuel/health), updates positions, and calls the draw() function. |
| draw() | The Painter | Clears the screen and repaints every pixel for the current frame. This includes the grass textures, the mower sprite, rocks, particles, and drones. |
| resize() | Responsive Layout | Calculates the screen aspect ratio (16:9) and adjusts the HTML5 Canvas size and tile size so the game fits perfectly on any device. |

Gameplay Logic

| Function Name | Purpose | Key Responsibilities |
|---|---|---|
| moveMower() | Movement Physics | The most complex function. It handles:

1. "Snake" Logic: Shifting down/over when hitting walls (After Dark style).
2. Collisions: Bouncing off rocks and taking damage.
3. Speed: Moving based on throttle settings. |

| input(x, y) | Input Handler | Receives commands from the N64 Joystick or Keyboard. Updates the mower's direction (dx, dy) and forces an immediate move tick for responsive feeling. |
| cut(x, y, fillBag) | Grass Mechanics | Checks the grid at specific coordinates. If grass > 0, it resets it to 0, spawns particles, and increments the bag count (or money if it's a drone). |
| updateDrones() | AI Logic | Iterates through all owned drones, giving them random movement directions so they wander the map autonomously. |

Economy & UI

| Function Name | Purpose | Key Responsibilities |
|---|---|---|
| updateUI() | DOM Interface | Updates the HTML overlay (Top HUD and Control Panel). It changes the text for Cash/Fuel and toggles CSS classes for flashing red alerts. |
| sell(), refuel(), repair() | Player Actions | Simple transaction functions. They check if the player has enough money/resources and update the game state accordingly. |
| buy(id) | Shop System | Handles purchasing logic. It differentiates between buying Upgrades (adding to inventory) and Maps (switching levels). |
| updateMenu() | Shop Rendering | Dynamically generates the HTML for the Shop menu based on what items the player already owns (disabling buttons for owned items). |

Data & Utilities

| Function Name | Purpose | Key Responsibilities |
|---|---|---|
| save() | Persistence | Serializes the game state (Money, Owned Items, Current Map) into a JSON string and saves it to the browser's localStorage. |
| loadSave() | Persistence | Reads localStorage on startup to restore the player's progress, upgrades, and hull modifications. |
| rand(min, max) | RNG Helper | A utility to generate random numbers, used heavily for placing rocks, grass growth, and particle effects. |
| getDelay() | Speed Calculator | Converts the Throttle Slider value (1-10) into game-frames. Higher throttle = fewer frames between moves (faster). |
