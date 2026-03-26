package com.brocku.game;

import java.util.Random;

public class GameBoard {

    public static final int COLS = 8;
    public static final int ROWS = 8;

    private Tile[][] grid;
    private Random random;
    private Tile selectedTile;

    public GameBoard() {
        random = new Random();
        grid = new Tile[COLS][ROWS];
        fillBoard();
    }

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

    private boolean wouldMatch(int x, int y, Tile.TileType type) {
        if (x >= 2
            && grid[x-1][y].getType() == type
            && grid[x-2][y].getType() == type) return true;
        if (y >= 2
            && grid[x][y-1].getType() == type
            && grid[x][y-2].getType() == type) return true;
        return false;
    }

    public boolean handleTap(int x, int y) {
        if (!inBounds(x, y)) return false;

        Tile tapped = grid[x][y];

        if (selectedTile == null) {
            tapped.setSelected(true);
            selectedTile = tapped;
            return false;
        }

        if (selectedTile == tapped) {
            tapped.setSelected(false);
            selectedTile = null;
            return false;
        }

        if (isAdjacent(selectedTile, tapped)) {
            selectedTile.setSelected(false);
            swap(selectedTile, tapped);

            // If the swap creates no match, reverse it
            if (!findAndMarkMatches()) {
                swap(tapped, selectedTile); // swap back
            } else {
                // Clear the marks — GameScreen's while loop will re-detect them
                clearMatches();
            }

            selectedTile = null;
            return true;
        }

        selectedTile.setSelected(false);
        tapped.setSelected(true);
        selectedTile = tapped;
        return false;
    }

    public void swap(Tile a, Tile b) {
        int ax = a.getGridX(), ay = a.getGridY();
        int bx = b.getGridX(), by = b.getGridY();

        grid[ax][ay] = b;
        grid[bx][by] = a;

        a.setGridX(bx); a.setGridY(by);
        b.setGridX(ax); b.setGridY(ay);
    }

    public boolean findAndMarkMatches() {
        boolean found = false;

        // Horizontal runs
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS - 2; x++) {
                Tile.TileType t = grid[x][y].getType();
                if (t == grid[x+1][y].getType() && t == grid[x+2][y].getType()) {
                    int end = x + 2;
                    while (end + 1 < COLS && grid[end+1][y].getType() == t) end++;
                    for (int i = x; i <= end; i++) grid[i][y].setMatched(true);
                    found = true;
                    x = end;
                }
            }
        }

        // Vertical runs
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS - 2; y++) {
                Tile.TileType t = grid[x][y].getType();
                if (t == grid[x][y+1].getType() && t == grid[x][y+2].getType()) {
                    int end = y + 2;
                    while (end + 1 < ROWS && grid[x][end+1].getType() == t) end++;
                    for (int i = y; i <= end; i++) grid[x][i].setMatched(true);
                    found = true;
                    y = end;
                }
            }
        }

        return found;
    }

    public void removeMatchesAndRefill() {
        Tile.TileType[] types = Tile.TileType.values();
        for (int x = 0; x < COLS; x++) {
            int writeY = 0;
            for (int y = 0; y < ROWS; y++) {
                if (!grid[x][y].isMatched()) {
                    grid[x][writeY] = grid[x][y];
                    grid[x][writeY].setGridY(writeY);
                    writeY++;
                }
            }
            while (writeY < ROWS) {
                Tile.TileType type = types[random.nextInt(types.length)];
                grid[x][writeY] = new Tile(type, x, writeY);
                writeY++;
            }
        }
    }

    private void clearMatches() {
        for (int x = 0; x < COLS; x++)
            for (int y = 0; y < ROWS; y++)
                grid[x][y].setMatched(false);
    }

    public int countMatched() {
        int count = 0;
        for (int x = 0; x < COLS; x++)
            for (int y = 0; y < ROWS; y++)
                if (grid[x][y].isMatched()) count++;
        return count;
    }

    public boolean isAdjacent(Tile a, Tile b) {
        int dx = Math.abs(a.getGridX() - b.getGridX());
        int dy = Math.abs(a.getGridY() - b.getGridY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < COLS && y >= 0 && y < ROWS;
    }

    public Tile getTile(int x, int y) { return grid[x][y]; }
    public Tile getSelectedTile()     { return selectedTile; }
}
