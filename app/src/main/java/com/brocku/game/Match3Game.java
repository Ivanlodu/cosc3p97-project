;

import com.badlogic.gdx.Game;

/**
 * Main entry point for the libGDX application.
 * AndroidLauncher and the desktop launcher both instantiate this class.
 */
public class Match3Game extends Game {

    @Override
    public void create() {
        // Start on the game screen straight away.
        // Add a MenuScreen here later if you want a title screen first.
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
