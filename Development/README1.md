This is a fantastic pivot. Combining the After Dark "Mowing Man" aesthetic (watching something clean up a space) with Sim/Tycoon mechanics and Escape Velocity style progression (upgrading your "ship") creates a very compelling "Idle Game" loop.
In this concept:
The Mower is Autonomous: You don't drive. It acts like the screensaver, moving in long lines and bouncing off walls.
You are the Manager: Your job is to maintain the machine.
Fuel: The mower stops when out of gas. You must pay to refuel it.
Bag: The mower stops when the bag is full. You must "Sell Clippings" to empty it and earn money.
Upgrades: You spend money to upgrade the Engine (Speed), Tank (Duration), and Blades (Width/Yield), just like outfitting a ship in Escape Velocity.
Nature Fights Back: The grass grows back over time, ensuring an infinite gameplay loop.
Here is the playable prototype.
File 1: Mower Tycoon (Idle/Management Prototype)

___

How this meets your "Escape Velocity" meets "Sim" request:
The Screensaver Logic: The Mower moves entirely on its own. It bounces off walls. You can literally set the phone down, and it will mow until the bag is full or fuel is empty.
Resource Management:
Fuel: If it hits 0%, the mower stops dead (turns Red). You have to pay $5 to refuel it.
Bag: If it hits 100%, the mower stops (turns Orange). You have to click "Sell Clippings" to empty it.
The "Escape Velocity" Upgrade Loop:
Photon Motor: Makes the mower move faster (covers more ground per minute).
Expanded Tank: Allows it to run longer without you needing to click "Refuel."
Compression Bag: Allows it to hold more grass, so you don't have to click "Sell" as often (allows for longer idle times).
Diamond Blades: Increases the monetary value of every pound of grass sold (the economy upgrade).
How to play the prototype:
Watch the mower cut the dark green grass (Tall) into light green (Cut).
Wait for the "BAG FULL" alert or the "OUT OF FUEL" alert.
Tap Sell Clippings to get cash.
Tap Refuel to get moving again.
Scroll down to the Hardware Upgrades section. Buy the Diamond Blades first (increases income), then the Photon Motor (speed).
This architecture is much easier to port to Android Kotlin later because the "Game Logic" (the mower object and update function) is completely separated from the UI.
