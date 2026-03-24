package com.brocku.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {

    // ---------------------------------------------------------------
    // Layout constants  — adjust these to fit your screen/assets
    // ---------------------------------------------------------------
    private static final int TILE_SIZE    = 80;   // px per tile
    private static final int BOARD_OFFSET_X = 40; // left margin
    private static final int BOARD_OFFSET_Y = 40; // bottom margin

    // ---------------------------------------------------------------
    // libGDX objects
    // ---------------------------------------------------------------
    private final Match3Game game;        // reference to main Game class
    private SpriteBatch      batch;
    private ShapeRenderer    shapeRenderer;
    private OrthographicCamera camera;

    // Tile textures — one per TileType (load your own PNGs into assets/)
    private Texture[] tileTextures;
    private Texture   selectedOverlay;    // highlight for selected tile

    // ---------------------------------------------------------------
    // Game state
    // ---------------------------------------------------------------
    private GameBoard board;
    private int       score = 0;

    // Reusable vector for un-projecting touch coordinates
    private Vector3   touchPos = new Vector3();

    // ---------------------------------------------------------------

    public GameScreen(Match3Game game) {
        this.game = game;
    }

    // ---------------------------------------------------------------
    // Screen lifecycle
    // ---------------------------------------------------------------

    @Override
    public void show() {
        batch         = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Load one texture per TileType — files must exist in assets/
        // Name them red.png, blue.png, green.png, yellow.png, purple.png
        tileTextures = new Texture[Tile.TileType.values().length];
        tileTextures[0] = new Texture("red.png");
        tileTextures[1] = new Texture("blue.png");
        tileTextures[2] = new Texture("green.png");
        tileTextures[3] = new Texture("yellow.png");
        tileTextures[4] = new Texture("purple.png");

        selectedOverlay = new Texture("selected.png"); // semi-transparent highlight

        board = new GameBoard();
    }

    @Override
    public void render(float delta) {
        // 1. Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Handle input
        handleInput();

        // 3. Check for matches after every swap
        //    (your teammate will implement findAndMarkMatches /
        //     removeMatchesAndRefill — stubs are in GameBoard)
        if (board.findAndMarkMatches()) {
            board.removeMatchesAndRefill();
            // TODO: add to score here
        }

        // 4. Draw everything
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawBoard();
        batch.end();
    }

    // ---------------------------------------------------------------
    // Input
    // ---------------------------------------------------------------

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        // Convert screen touch to world / camera coordinates
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        // Convert world coords to grid coords
        int gridX = (int) ((touchPos.x - BOARD_OFFSET_X) / TILE_SIZE);
        int gridY = (int) ((touchPos.y - BOARD_OFFSET_Y) / TILE_SIZE);

        board.handleTap(gridX, gridY);
    }

    // ---------------------------------------------------------------
    // Rendering
    // ---------------------------------------------------------------

    private void drawBoard() {
        for (int x = 0; x < GameBoard.COLS; x++) {
            for (int y = 0; y < GameBoard.ROWS; y++) {
                Tile tile = board.getTile(x, y);
                if (tile == null) continue;

                float drawX = BOARD_OFFSET_X + x * TILE_SIZE;
                float drawY = BOARD_OFFSET_Y + y * TILE_SIZE;

                // Draw the tile sprite
                Texture tex = tileTextures[tile.getType().ordinal()];
                batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);

                // Draw selection highlight on top
                if (tile.isSelected()) {
                    batch.draw(selectedOverlay, drawX, drawY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Cleanup — ALWAYS dispose libGDX objects to avoid memory leaks
    // ---------------------------------------------------------------

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        selectedOverlay.dispose();
        for (Texture t : tileTextures) {
            if (t != null) t.dispose();
        }
    }

    // ---------------------------------------------------------------
    // Unused Screen interface methods (required by libGDX)
    // ---------------------------------------------------------------

    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
}
