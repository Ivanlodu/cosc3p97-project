package com.brocku.game;

import java.util.Random;

public class GameBoard {

    public static final int COLS = 8;
    public static final int ROWS = 8;

    private Tile[][] grid;
    private Random random;

    // The first tile the player tapped (waiting for a second tap to swap)
    private Tile selectedTile;

    public GameBoard() {
        random = new Random();
        grid = new Tile[COLS][ROWS];
        fillBoard();
    }

    // ---------------------------------------------------------------
    // Board initialisation
    // ---------------------------------------------------------------

    /** Fill every cell with a random tile, re-rolling any that would
     *  immediately create a 3-in-a-row so the board starts clean. */
    private void fillBoard() {
        Tile.TileType[] types = Tile.TileType.values();

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Tile.TileType type;
                do {
                    type = types[random.nextInt(types.length)];
                } while (wouldMatch(x, y, type));
                grid[x][y] = new Tile(type, x, y);
            }
        }
    }

    /** Returns true if placing [type] at (x,y) would create a match
     *  with the two tiles already placed to its left or below it. */
    private boolean wouldMatch(int x, int y, Tile.TileType type) {
        // Check horizontal: two tiles to the left
        if (x >= 2
            && grid[x-1][y].getType() == type
            && grid[x-2][y].getType() == type) return true;
        // Check vertical: two tiles below
        if (y >= 2
            && grid[x][y-1].getType() == type
            && grid[x][y-2].getType() == type) return true;
        return false;
    }

    // ---------------------------------------------------------------
    // Input handling — called from GameScreen on touch/tap
    // ---------------------------------------------------------------

    /**
     * Called when the player taps a tile at grid position (x, y).
     * First tap  → selects the tile.
     * Second tap → attempts a swap if the tiles are adjacent,
     *              or changes selection if they are not.
     *
     * @return true if a swap was attempted (valid or not)
     */
    public boolean handleTap(int x, int y) {
        if (!inBounds(x, y)) return false;

        Tile tapped = grid[x][y];

        if (selectedTile == null) {
            // First tap: select this tile
            tapped.setSelected(true);
            selectedTile = tapped;
            return false;
        }

        if (selectedTile == tapped) {
            // Tapped the same tile: deselect
            tapped.setSelected(false);
            selectedTile = null;
            return false;
        }

        if (isAdjacent(selectedTile, tapped)) {
            // Valid swap target — deselect and swap
            selectedTile.setSelected(false);
            swap(selectedTile, tapped);
            selectedTile = null;
            return true;
        }

        // Not adjacent: move selection to new tile
        selectedTile.setSelected(false);
        tapped.setSelected(true);
        selectedTile = tapped;
        return false;
    }

    // ---------------------------------------------------------------
    // Swap
    // ---------------------------------------------------------------

    /** Swap two tiles in the grid (and update their stored positions). */
    public void swap(Tile a, Tile b) {
        int ax = a.getGridX(), ay = a.getGridY();
        int bx = b.getGridX(), by = b.getGridY();

        grid[ax][ay] = b;
        grid[bx][by] = a;

        a.setGridX(bx); a.setGridY(by);
        b.setGridX(ax); b.setGridY(ay);
    }

    // ---------------------------------------------------------------
    // Match detection  (TODO for your teammate)
    // ---------------------------------------------------------------

    /**
     * Scans the whole board and marks every tile that is part of a
     * 3-or-more match.
     *
     * TODO: implement this in the second half of development.
     * Hint: iterate rows checking horizontal runs, then columns for vertical.
     *
     * @return true if at least one match was found
     */
    public boolean findAndMarkMatches() {
        // Placeholder — replace with real implementation
        return false;
    }

    /**
     * Removes all tiles marked as matched and lets tiles above fall
     * down to fill the gaps, then refills empty cells at the top.
     *
     * TODO: implement this in the second half of development.
     */
    public void removeMatchesAndRefill() {
        // Placeholder — replace with real implementation
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    public boolean isAdjacent(Tile a, Tile b) {
        int dx = Math.abs(a.getGridX() - b.getGridX());
        int dy = Math.abs(a.getGridY() - b.getGridY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < COLS && y >= 0 && y < ROWS;
    }

    public Tile getTile(int x, int y) {
        return grid[x][y];
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }
}
