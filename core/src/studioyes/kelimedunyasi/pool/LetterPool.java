package studioyes.kelimedunyasi.pool;

import com.badlogic.gdx.utils.Pool;

import studioyes.kelimedunyasi.model.Constants;
import studioyes.kelimedunyasi.ui.preview.Letter;

public class LetterPool extends Pool<Letter>{


    public LetterPool(){
        super(Constants.MAX_LETTERS, Constants.MAX_LETTERS * 4);
    }


    @Override
    protected Letter newObject() {
        return new Letter();
    }
}
