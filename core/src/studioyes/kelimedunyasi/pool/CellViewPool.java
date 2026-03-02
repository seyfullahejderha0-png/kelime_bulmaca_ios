package studioyes.kelimedunyasi.pool;

import com.badlogic.gdx.utils.Pool;

import studioyes.kelimedunyasi.ui.board.CellView;

public class CellViewPool extends Pool<CellView> {

    public CellViewPool(){
        super(9, 50);
    }


    @Override
    protected CellView newObject() {
        return new CellView();
    }

}
