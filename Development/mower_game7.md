<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>mower_game</title>
    <style>
        :root {
            --bg-color: #222;
            --panel-bg: #333;
            --wc-gold: #FFD700;
            --wc-text: #eee;
            --accent-green: #2e7d32;
            --accent-red: #c62828;
            --btn-gray: #444;
        }

        body {
            margin: 0;
            padding: 0;
            background-color: var(--bg-color);
            color: var(--wc-text);
            font-family: 'Courier New', Courier, monospace; /* Retro feel */
            display: flex;
            flex-direction: column;
            height: 100vh;
            overflow: hidden;
            touch-action: none;
        }

        /* --- WARCRAFT STYLE HUD (TOP) --- */
        #top-bar {
            height: 40px;
            background-color: #000;
            border-bottom: 2px solid #555;
            display: flex;
            align-items: center;
            justify-content: space-around;
            padding: 0 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.8);
            z-index: 20;
        }

        .res-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: bold;
            color: var(--wc-gold);
            text-shadow: 1px 1px 0 #333;
        }

        .res-icon { font-size: 16px; }
        .res-val { color: white; }
        
        /* Alert colors for resources */
        .res-alert { color: #ff3333; animation: flash 1s infinite; }
        @keyframes flash { 50% { opacity: 0.5; } }

        /* --- GAME VIEWPORT --- */
        #viewport {
            flex: 1;
            background: #111;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            overflow: hidden;
        }

        canvas {
            box-shadow: 0 0 20px rgba(0,0,0,0.8);
            image-rendering: pixelated;
        }

        /* --- CONTROL PANEL (V1 STYLE) --- */
        #controls-panel {
            height: 220px;
            background-color: var(--panel-bg);
            border-top: 4px solid #444;
            padding: 15px;
            display: flex;
            gap: 20px;
            box-sizing: border-box;
        }

        /* Section 1: D-Pad */
        .control-group {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            flex: 1;
        }

        .dpad-grid {
            display: grid;
            grid-template-columns: 50px 50px 50px;
            grid-template-rows: 50px 50px;
            gap: 5px;
        }

        .d-btn {
            width: 100%; height: 100%;
            background: var(--btn-gray);
            border: 2px solid #555;
            border-radius: 50%; /* Round buttons from V1 */
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 20px;
            color: #ccc;
            cursor: pointer;
            box-shadow: 0 4px 0 #222;
        }
        .d-btn:active { transform: translateY(4px); box-shadow: none; background: #666; }
        
        #d-up { grid-column: 2; grid-row: 1; }
        #d-left { grid-column: 1; grid-row: 2; }
        #d-down { grid-column: 2; grid-row: 2; }
        #d-right { grid-column: 3; grid-row: 2; }

        /* Section 2: Speed & Actions */
        .center-console {
            flex: 1.5;
            display: flex;
            flex-direction: column;
            gap: 15px;
            justify-content: center;
        }

        /* Throttle Slider */
        .slider-box {
            background: #222;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #444;
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .slider-label { font-size: 10px; color: #888; text-transform: uppercase; letter-spacing: 1px; }
        input[type=range] { width: 100%; accent-color: var(--wc-gold); cursor: pointer; }

        /* Action Buttons */
        .action-row { display: flex; gap: 10px; }
        
        .action-btn {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 8px;
            color: white;
            font-weight: bold;
            font-family: inherit;
            cursor: pointer;
            text-transform: uppercase;
            font-size: 12px;
            box-shadow: 0 4px 0 rgba(0,0,0,0.4);
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .action-btn:active { transform: translateY(4px); box-shadow: none; }
        
        .btn-green { background: var(--accent-green); border: 1px solid #4caf50; }
        .btn-blue { background: #0277bd; border: 1px solid #29b6f6; }
        .btn-gold { background: #c79100; border: 1px solid #ffd700; color: #111; }

        .btn-alert { animation: pulse 1s infinite alternate; }
        @keyframes pulse { from { filter: brightness(1); } to { filter: brightness(1.3); } }

        /* --- MENUS --- */
        #overlay-menu {
            display: none;
            position: absolute;
            top: 40px; left: 0; bottom: 0; right: 0;
            background: rgba(0,0,0,0.95);
            z-index: 50;
            padding: 20px;
            overflow-y: auto;
        }
        #overlay-menu.open { display: block; }
        
        .menu-header { color: var(--wc-gold); border-bottom: 1px solid #555; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
        .menu-section { margin-bottom: 30px; }
        .section-title { font-size: 12px; color: #888; margin-bottom: 10px; text-transform: uppercase; }
        
        .shop-item {
            background: #1a1a1a; border: 1px solid #333; padding: 12px; margin-bottom: 8px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .shop-btn { background: var(--wc-gold); color: black; border: none; padding: 5px 15px; font-weight: bold; cursor: pointer; }
        .shop-btn:disabled { background: #444; color: #777; cursor: not-allowed; }

        /* Floating Text */
        .floater {
            position: absolute;
            color: var(--wc-gold);
            font-weight: bold;
            font-size: 18px;
            pointer-events: none;
            z-index: 100;
            text-shadow: 1px 1px 0 #000;
            animation: floatUp 1s forwards;
        }
        @keyframes floatUp { to { transform: translateY(-40px); opacity: 0; } }

    </style>
</head>
<body>

    <!-- WARCRAFT STYLE HUD -->
    <div id="top-bar">
        <div class="res-item">
            <span class="res-icon">💰</span>
            <span>Gold: <span class="res-val" id="hud-money">0</span></span>
        </div>
        <div class="res-item" id="hud-fuel-box">
            <span class="res-icon">⛽</span>
            <span>Fuel: <span class="res-val" id="hud-fuel">100%</span></span>
        </div>
        <div class="res-item" id="hud-bag-box">
            <span class="res-icon">📦</span>
            <span>Bag: <span class="res-val" id="hud-bag">0/20</span></span>
        </div>
    </div>

    <!-- MAIN GAME AREA -->
    <div id="viewport">
        <canvas id="gameCanvas"></canvas>
    </div>

    <!-- V1 STYLE CONTROLS -->
    <div id="controls-panel">
        
        <!-- D-Pad -->
        <div class="control-group">
            <div class="dpad-grid">
                <div class="d-btn" id="d-up" onpointerdown="game.input(0, -1)">▲</div>
                <div class="d-btn" id="d-left" onpointerdown="game.input(-1, 0)">◀</div>
                <div class="d-btn" id="d-down" onpointerdown="game.input(0, 1)">▼</div>
                <div class="d-btn" id="d-right" onpointerdown="game.input(1, 0)">▶</div>
            </div>
        </div>

        <!-- Center Console -->
        <div class="center-console">
            
            <div class="slider-box">
                <div style="display:flex; justify-content:space-between;">
                    <span class="slider-label">Mower Speed</span>
                    <span class="slider-label" id="speed-label">5</span>
                </div>
                <input type="range" min="1" max="10" value="5" oninput="game.setThrottle(this.value)">
            </div>

            <div class="action-row">
                <button class="action-btn btn-green" id="btn-sell" onclick="game.sell()">
                    <span>Sell</span>
                    <span style="font-size:10px; opacity:0.8" id="val-sell">$0</span>
                </button>
                <button class="action-btn btn-blue" id="btn-fuel" onclick="game.refuel()">
                    <span>Refuel</span>
                    <span style="font-size:10px; opacity:0.8">$5</span>
                </button>
                <button class="action-btn btn-gold" onclick="toggleMenu()">
                    <span>Maps</span>
                    <span style="font-size:10px; opacity:0.8">Menu</span>
                </button>
            </div>
        </div>

    </div>

    <!-- MENU OVERLAY -->
    <div id="overlay-menu">
        <div class="menu-header">
            <h2 style="margin:0">MOWER_GAME</h2>
            <button class="shop-btn" style="background:#c62828; color:white;" onclick="toggleMenu()">CLOSE</button>
        </div>

        <div class="menu-section">
            <div class="section-title">Select Location</div>
            <div id="list-maps"></div>
        </div>

        <div class="menu-section">
            <div class="section-title">Hardware</div>
            <div id="list-upgrades"></div>
        </div>
        
        <button class="action-btn btn-blue" style="width:100%" onclick="game.repair()">Repair Hull ($20)</button>
    </div>

    <script>
        // --- DATA ---
        const MAPS = [
            { id: 'yard', name: 'Backyard', w: 32, h: 18, rocks: 5, growth: 0.02, cost: 0, color: '#2d5a27' },
            { id: 'park', name: 'City Park', w: 48, h: 27, rocks: 25, growth: 0.05, cost: 500, color: '#1b5e20' },
            { id: 'golf', name: 'Golf Course', w: 64, h: 36, rocks: 60, growth: 0.08, cost: 5000, color: '#33691e' }
        ];

        const UPGRADES = [
            { id: 'drone', name: 'Auto-Drone', cost: 250, desc: 'Helper bot', type: 'drone' },
            { id: 'rider', name: 'Rider Hull', cost: 1000, desc: '300 Fuel / 100 Bag', type: 'hull', tank: 300, bag: 100 },
            { id: 'autosell', name: 'Auto-Link', cost: 2000, desc: 'Auto-sell clipping', type: 'tech' }
        ];

        class Game {
            constructor() {
                this.canvas = document.getElementById('gameCanvas');
                this.ctx = this.canvas.getContext('2d');
                this.viewport = document.getElementById('viewport');

                this.money = 0;
                this.map = MAPS[0];
                this.owned = ['yard'];
                this.drones = [];
                this.particles = [];

                this.mower = {
                    x: 0, y: 0, dx: 1, dy: 0, // Start moving right
                    fuel: 100, maxFuel: 100,
                    bag: 0, maxBag: 20,
                    health: 100,
                    throttle: 5,
                    moveTimer: 0,
                    state: 'MOWING'
                };

                this.grid = [];
                this.rocks = [];
                this.tileSize = 20;

                // Inputs
                window.addEventListener('resize', () => this.resize());
                window.addEventListener('keydown', (e) => {
                    if(e.key==='ArrowUp') this.input(0,-1);
                    if(e.key==='ArrowDown') this.input(0,1);
                    if(e.key==='ArrowLeft') this.input(-1,0);
                    if(e.key==='ArrowRight') this.input(1,0);
                });
            }

            init() {
                this.loadSave();
                this.loadMap(this.map.id, true);
                this.resize();
                this.updateMenu();
                requestAnimationFrame(() => this.loop());
                setInterval(() => this.save(), 5000);
            }

            resize() {
                // Enforce 16:9 aspect ratio
                const vw = this.viewport.clientWidth;
                const vh = this.viewport.clientHeight;
                
                // Calculate ideal dimensions
                let targetW = vw;
                let targetH = vw * (9/16);

                if (targetH > vh) {
                    targetH = vh;
                    targetW = vh * (16/9);
                }

                this.canvas.width = targetW;
                this.canvas.height = targetH;
                
                // Calculate Tile Size based on current map size
                this.tileSize = targetW / this.map.w;
                this.ctx.imageSmoothingEnabled = false;
            }

            setThrottle(val) {
                this.mower.throttle = parseInt(val);
                document.getElementById('speed-label').innerText = val;
            }

            // High throttle = Low delay frames
            getDelay() { return Math.max(2, 22 - (this.mower.throttle * 2)); }

            loadMap(id, force=false) {
                let m = MAPS.find(x => x.id === id);
                if(!m) return;
                if(!force && this.map.id === id) return;

                this.map = m;
                this.grid = [];
                this.rocks = [];

                // Init Grid
                for(let y=0; y<this.map.h; y++) {
                    let row = [];
                    for(let x=0; x<this.map.w; x++) row.push(2); // 2 = tall grass
                    this.grid.push(row);
                }
                
                // Rocks
                for(let i=0; i<this.map.rocks; i++) {
                    this.rocks.push({x: rand(0, this.map.w), y: rand(0, this.map.h)});
                }

                this.mower.x = 0;
                this.mower.y = 0;
                this.mower.dx = 1; this.mower.dy = 0;
                this.resize();
                toggleMenu(false);
            }

            input(x, y) {
                if(this.mower.state === 'NO_FUEL' || this.mower.state === 'BROKEN') return;
                this.mower.dx = x;
                this.mower.dy = y;
                if(this.mower.state !== 'BAG_FULL') this.mower.state = 'MOWING';
                this.mower.moveTimer = 100; // Force immediate move
            }

            loop() {
                // Growth
                if(Math.random() < this.map.growth) {
                    let rx = rand(0, this.map.w);
                    let ry = rand(0, this.map.h);
                    if(this.grid[ry][rx] < 2) this.grid[ry][rx]++;
                }

                // Automation
                if(this.owned.includes('autosell') && this.mower.bag >= this.mower.maxBag) this.sell();

                // State Check
                if(this.mower.health <= 0) this.mower.state = 'BROKEN';
                else if(this.mower.fuel <= 0) this.mower.state = 'NO_FUEL';
                else if(this.mower.bag >= this.mower.maxBag) this.mower.state = 'BAG_FULL';
                else this.mower.state = 'MOWING';

                // Mower Movement
                if(this.mower.state === 'MOWING') {
                    this.mower.moveTimer++;
                    if(this.mower.moveTimer >= this.getDelay()) {
                        this.mower.moveTimer = 0;
                        this.moveMower();
                    }
                }

                // Drones
                this.updateDrones();
                
                // Particles
                this.updateParticles();

                this.draw();
                this.updateUI();
                requestAnimationFrame(() => this.loop());
            }

            // --- AFTER DARK MOVEMENT LOGIC ---
            moveMower() {
                let nx = this.mower.x + this.mower.dx;
                let ny = this.mower.y + this.mower.dy;
                let hitWall = false;
                let hitRock = false;

                // 1. Check Walls
                if (nx < 0 || nx >= this.map.w || ny < 0 || ny >= this.map.h) {
                    hitWall = true;
                }

                // 2. Check Rocks
                if (!hitWall) {
                    for(let r of this.rocks) {
                        if (r.x === nx && r.y === ny) {
                            hitRock = true; 
                            break;
                        }
                    }
                }

                if (hitRock) {
                    // Rock collision: Damage + Bounce
                    this.mower.health -= (this.mower.throttle);
                    this.mower.dx *= -1; 
                    this.mower.dy *= -1;
                    this.spawnParticle(nx, ny, '#888');
                } 
                else if (hitWall) {
                    // "Scanline" Logic: If moving horizontally and hit wall -> move down, then flip X
                    if (this.mower.dx !== 0) {
                        // We are moving horizontal
                        let nextY = this.mower.y + 1;
                        if (nextY < this.map.h) {
                            // Move Down
                            this.mower.y = nextY;
                            this.mower.x = this.mower.x; // Keep X same for this frame (just moved down)
                            // Flip X for NEXT frame
                            this.mower.dx *= -1;
                            // Burn fuel for the turn
                            this.mower.fuel -= 0.1;
                            this.cut(this.mower.x, this.mower.y, true);
                        } else {
                            // Bottom reached: Standard bounce or wrap? Let's Bounce Up.
                            this.mower.dy = -1;
                            this.mower.dx = 0;
                        }
                    } else {
                        // Moving Vertical and hit top/bottom: Just bounce
                        this.mower.dy *= -1;
                    }
                } 
                else {
                    // Clear path
                    this.mower.x = nx;
                    this.mower.y = ny;
                    this.mower.fuel -= (0.05 * (this.mower.throttle/2));
                    this.cut(nx, ny, true);
                }
            }

            updateDrones() {
                this.drones.forEach(d => {
                    d.timer++;
                    if(d.timer >= 15) {
                        d.timer = 0;
                        // Random walk
                        if(Math.random()<0.3) d.dx = Math.random()>0.5?1:-1;
                        if(Math.random()<0.3) d.dy = Math.random()>0.5?1:-1;
                        let nx = d.x + d.dx; 
                        let ny = d.y + d.dy;
                        if(nx >= 0 && nx < this.map.w && ny >= 0 && ny < this.map.h) {
                            d.x = nx; d.y = ny;
                            this.cut(nx, ny, false);
                        } else {
                            d.dx*=-1; d.dy*=-1;
                        }
                    }
                });
            }

            cut(x, y, fillBag) {
                if(this.grid[y][x] > 0) {
                    this.grid[y][x] = 0;
                    this.spawnParticle(x, y, '#4caf50');
                    if(fillBag) this.mower.bag++;
                    else this.money++;
                }
            }

            spawnParticle(x, y, color) {
                this.particles.push({
                    x: x * this.tileSize + this.tileSize/2,
                    y: y * this.tileSize + thi
