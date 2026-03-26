package com.brocku.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {

    private int TILE_SIZE;
    private int BOARD_OFFSET_X;
    private int BOARD_OFFSET_Y;

    private final Match3Game game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont font;

    private Texture[] tileTextures;
    private Texture selectedOverlay;

    private GameBoard board;
    private int score = 0;

    private final Vector3 touchPos = new Vector3();

    public GameScreen(Match3Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        setupBoardDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // === SHAPE TEXTURES ===
        tileTextures = new Texture[Tile.TileType.values().length];
        Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE };

        for (int i = 0; i < colors.length; i++) {
            Pixmap pm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);

            pm.setColor(0, 0, 0, 0);
            pm.fill();

            pm.setColor(colors[i]);

            switch (i) {
                case 0: pm.fillRectangle(12, 12, 40, 40); break; // square
                case 1: pm.fillCircle(32, 32, 20); break;       // circle
                case 2: pm.fillTriangle(32, 12, 12, 52, 52, 52); break; // triangle
                case 3:
                    pm.fillTriangle(32, 52, 12, 32, 32, 12);
                    pm.fillTriangle(32, 52, 52, 32, 32, 12);
                    break; // diamond
                case 4: pm.fillRectangle(18, 18, 28, 28); break; // small square
            }

            tileTextures[i] = new Texture(pm);
            pm.dispose();
        }

        // selection overlay
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 0.3f);
        pm.fill();
        selectedOverlay = new Texture(pm);
        pm.dispose();

        board = new GameBoard();
    }

    private void setupBoardDimensions(int width, int height) {
        // Fill most of the screen
        TILE_SIZE = (int)(Math.min(
            width / (float)GameBoard.COLS,
            height / (float)GameBoard.ROWS
        ) * 0.95f);

        BOARD_OFFSET_X = (width  - TILE_SIZE * GameBoard.COLS) / 2;
        BOARD_OFFSET_Y = (height - TILE_SIZE * GameBoard.ROWS) / 2 - 20;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // cascade logic
        while (board.findAndMarkMatches()) {
            score += board.countMatched() * 10;
            board.removeMatchesAndRefill();
        }

        camera.update();

        // draw background
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 1);

        shapeRenderer.rect(
            BOARD_OFFSET_X,
            BOARD_OFFSET_Y,
            TILE_SIZE * GameBoard.COLS,
            TILE_SIZE * GameBoard.ROWS
        );

        shapeRenderer.end();

        // draw tiles
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawBoard();
        drawScore();
        batch.end();
    }

    private void handleInput() {
        if (!Gdx.input.justTouched()) return;

        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        int gridX = (int) ((touchPos.x - BOARD_OFFSET_X) / TILE_SIZE);
        int gridY = (int) ((touchPos.y - BOARD_OFFSET_Y) / TILE_SIZE);

        if (!board.inBounds(gridX, gridY)) return;

        board.handleTap(gridX, gridY);
    }

    private void drawBoard() {
        float padding = TILE_SIZE * 0.12f;

        for (int x = 0; x < GameBoard.COLS; x++) {
            for (int y = 0; y < GameBoard.ROWS; y++) {
                Tile tile = board.getTile(x, y);
                if (tile == null) continue;

                float drawX = BOARD_OFFSET_X + x * TILE_SIZE;
                float drawY = BOARD_OFFSET_Y + y * TILE_SIZE;

                Texture tex = tileTextures[tile.getType().ordinal()];

                batch.draw(
                    tex,
                    drawX + padding,
                    drawY + padding,
                    TILE_SIZE - 2 * padding,
                    TILE_SIZE - 2 * padding
                );

                if (tile.isSelected()) {
                    batch.draw(
                        selectedOverlay,
                        drawX + padding,
                        drawY + padding,
                        TILE_SIZE - 2 * padding,
                        TILE_SIZE - 2 * padding
                    );
                }
            }
        }
    }

    private void drawScore() {
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        setupBoardDimensions(width, height);
    }

    @Override public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        selectedOverlay.dispose();
        font.dispose();

        for (Texture t : tileTextures) {
            if (t != null) t.dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
