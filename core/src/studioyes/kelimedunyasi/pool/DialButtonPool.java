package studioyes.kelimedunyasi.pool;

import com.badlogic.gdx.utils.Pool;

import studioyes.kelimedunyasi.model.Constants;
import studioyes.kelimedunyasi.ui.dial.DialButton;

public class DialButtonPool extends Pool<DialButton> {

    public DialButtonPool(){
            super(5, Constants.MAX_LETTERS);
    }


    @Override
    protected DialButton newObject() {
        return new DialButton();
    }
}
