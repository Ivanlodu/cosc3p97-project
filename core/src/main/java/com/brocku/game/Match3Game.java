package com.brocku.game;

import com.badlogic.gdx.Game;

public class Match3Game extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }
}
