package com.example.mowinggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Mowing Man Native Android Game
 * * Logic:
 * - Uses a custom SurfaceView for the Game Loop.
 * - Draws grid, mower, and obstacles directly to Canvas.
 * - Handles touch input (tapping sides of screen) to steer.
 */
class MainActivity : AppCompatActivity() {

    private var gameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize our custom game view
        gameView = GameView(this)
        // Hide status bar for immersive mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView?.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView?.resume()
    }

    /**
     * The Main Game Engine
     */
    class GameView(context: Context) : SurfaceView(context), Runnable {

        private var thread: Thread? = null
        private var isPlaying = false
        private val surfaceHolder: SurfaceHolder = holder
        private val paint = Paint()

        // Game Grid Configuration
        private val numTilesX = 15
        private var numTilesY = 20 // Calculated dynamically based on aspect ratio
        private var tileSize = 0
        private var screenWidth = 0
        private var screenHeight = 0

        // Game State
        private var mowerX = 0
        private var mowerY = 0
        private var dirX = 0
        private var dirY = 0
        
        // 0 = cut, 1 = uncut
        private lateinit var grassGrid: Array<IntArray>
        
        // Simple obstacle class
        data class Point(val x: Int, val y: Int)
        private val rocks = CopyOnWriteArrayList<Point>()

        private var lastMoveTime: Long = 0
        private val moveDelay = 150L // Milliseconds between moves (Speed)

        init {
            // No setup needed here, handled in onSizeChanged or run
        }

        // Called when the view is sized (perfect for grid setup)
        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            screenWidth = w
            screenHeight = h
            
            tileSize = w / numTilesX
            if (tileSize == 0) return 
            
            numTilesY = h / tileSize
            
            resetGame()
        }

        private fun resetGame() {
            grassGrid = Array(numTilesX) { IntArray(numTilesY) { 1 } } // 1 represents uncut grass
            mowerX = 0
            mowerY = 0
            dirX = 0
            dirY = 0
            
            rocks.clear()
            // Add random rocks
            repeat(5) {
                val rx = (0 until numTilesX).random()
                val ry = (0 until numTilesY).random()
                if (rx != 0 || ry != 0) {
                    rocks.add(Point(rx, ry))
                }
            }
            
            // Cut starting pos
            grassGrid[mowerX][mowerY] = 0
        }

        override fun run() {
            while (isPlaying) {
                update()
                draw()
                control()
            }
        }

        private fun update() {
            val now = System.currentTimeMillis()
            if (now - lastMoveTime < moveDelay) return
            lastMoveTime = now

            if (dirX == 0 && dirY == 0) return

            val nextX = mowerX + dirX
            val nextY = mowerY + dirY

            // Boundary checks
            if (nextX < 0 || nextX >= numTilesX || nextY < 0 || nextY >= numTilesY) {
                // Hit wall - stop
                dirX = 0
                dirY = 0
                return
            }

            // Rock checks
            for (rock in rocks) {
                if (rock.x == nextX && rock.y == nextY) {
                    // Hit rock - stop
                    dirX = 0
                    dirY = 0
                    return
                }
            }

            // Move
            mowerX = nextX
            mowerY = nextY
            
            // Cut Grass
            grassGrid[mowerX][mowerY] = 0
        }

        private fun draw() {
            if (surfaceHolder.surface.isValid) {
                val canvas: Canvas = surfaceHolder.lockCanvas()
                
                // Background (Dirt/Base)
                canvas.drawColor(Color.parseColor("#3b2e2a"))

                // Draw Grid
                for (i in 0 until numTilesX) {
                    for (j in 0 until numTilesY) {
                        val left = i * tileSize
                        val top = j * tileSize
                        val right = left + tileSize
                        val bottom = top + tileSize

                        // 1 is uncut (Dark Green), 0 is cut (Light Green/Transparent)
                        if (grassGrid[i][j] == 1) {
                            paint.color = Color.parseColor("#228B22") // Forest Green
                            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                            
                            // Grid outline
                            paint.style = Paint.Style.STROKE
                            paint.color = Color.parseColor("#1e7a3e")
                            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                            paint.style = Paint.Style.FILL
                        } else {
                            paint.color = Color.parseColor("#32CD32") // Lime Green (Cut)
                            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                        }
                    }
                }

                // Draw Rocks
                paint.color = Color.GRAY
                for (rock in rocks) {
                    val cx = (rock.x * tileSize) + (tileSize / 2f)
                    val cy = (rock.y * tileSize) + (tileSize / 2f)
                    canvas.drawCircle(cx, cy, tileSize / 2.5f, paint)
                }

                // Draw Mower
                paint.color = Color.RED
                val mowerLeft = mowerX * tileSize + 10f
                val mowerTop = mowerY * tileSize + 10f
                val mowerRight = (mowerX * tileSize) + tileSize - 10f
                val mowerBottom = (mowerY * tileSize) + tileSize - 10f
                canvas.drawRect(mowerLeft, mowerTop, mowerRight, mowerBottom, paint)

                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

        private fun control() {
            try {
                Thread.sleep(17) // ~60 FPS cap
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        fun pause() {
            isPlaying = false
            try {
                thread?.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        fun resume() {
            isPlaying = true
            thread = Thread(this)
            thread?.start()
        }

        // Simple touch controls: Tap top/bottom/left/right halves relative to screen center
        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y
                
                val cx = screenWidth / 2
                val cy = screenHeight / 2
                
                val dx = x - cx
                val dy = y - cy
                
                // Determine dominant axis
                if (Math.abs(dx) > Math.abs(dy)) {
                    // Horizontal
                    if (dx > 0) {
                        dirX = 1; dirY = 0 // Right
                    } else {
                        dirX = -1; dirY = 0 // Left
                    }
                } else {
                    // Vertical
                    if (dy > 0) {
                        dirX = 0; dirY = 1 // Down
                    } else {
                        dirX = 0; dirY = -1 // Up
                    }
                }
            }
            return true
        }
    }
}
