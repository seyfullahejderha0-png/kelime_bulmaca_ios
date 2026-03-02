package studioyes.kelimedunyasi.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import studioyes.kelimedunyasi.config.UIConfig;
import studioyes.kelimedunyasi.graphics.NinePatches;


public class Modal extends Group {

    public Modal(float width, float height){

        setSize(width, height);

        Image bg = new Image(NinePatches.rect);
        bg.setSize(width, height);
        bg.setColor(UIConfig.DIALOG_MODAL_BACKGROUND_COLOR);
        addActor(bg);
    }
}
