package studioyes.kelimedunyasi.pool;

import com.badlogic.gdx.utils.Pool;

import studioyes.kelimedunyasi.ui.top_panel.Coin;

public class CoinPool extends Pool<Coin> {


    @Override
    protected Coin newObject() {
        return new Coin();
    }
}
