package com.brocku.game;
public class Tile {

    public enum TileType {
        RED, BLUE, GREEN, YELLOW, PURPLE
    }

    private TileType type;
    private int gridX;   // column in the board
    private int gridY;   // row in the board
    private boolean selected;
    private boolean matched;

    public Tile(TileType type, int gridX, int gridY) {
        this.type = type;
        this.gridX = gridX;
        this.gridY = gridY;
        this.selected = false;
        this.matched = false;
    }

    // --- Getters & setters ---

    public TileType getType()               { return type; }
    public void setType(TileType type)      { this.type = type; }

    public int getGridX()                   { return gridX; }
    public void setGridX(int gridX)         { this.gridX = gridX; }

    public int getGridY()                   { return gridY; }
    public void setGridY(int gridY)         { this.gridY = gridY; }

    public boolean isSelected()             { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean isMatched()              { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }
}
