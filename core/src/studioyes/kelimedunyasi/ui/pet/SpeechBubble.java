package studioyes.kelimedunyasi.ui.pet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import studioyes.kelimedunyasi.graphics.NinePatches;
import studioyes.kelimedunyasi.managers.ResourceManager;

public class SpeechBubble extends Group {

    private Image background;
    private Label label;

    public SpeechBubble(ResourceManager resourceManager, String text) {
        background = new Image(new NinePatchDrawable(NinePatches.round_rect_shadow));
        background.setColor(studioyes.kelimedunyasi.config.UIConfig.INTERACTIVE_TUTORIAL_TEXT_BG_COLOR);
        addActor(background);

        Label.LabelStyle style = new Label.LabelStyle(
                resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class),
                Color.WHITE);
        
        label = new Label(text, style);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(0.6f);
        addActor(label);

        updateSize(text);
        
        setVisible(false);
        setScale(0);
        setOrigin(Align.bottomLeft);
    }

    private void updateSize(String text) {
        float minWidth = 150;
        float maxWidth = 250;
        label.setWidth(maxWidth - 40);
        label.setText(text);
        
        float width = Math.max(minWidth, label.getPrefWidth() + 40);
        float height = label.getPrefHeight() + 30;
        
        setSize(width, height);
        background.setSize(width, height);
        
        label.setSize(width - 20, height - 20);
        label.setPosition(10, 10);
    }

    public void show(String text) {
        label.setText(text);
        updateSize(text);
        setVisible(true);
        clearActions();
        addAction(Actions.sequence(
            Actions.scaleTo(1f, 1f, 0.3f, studioyes.kelimedunyasi.actions.Interpolation.backOut),
            Actions.delay(8f),
            Actions.scaleTo(0f, 0f, 0.3f, studioyes.kelimedunyasi.actions.Interpolation.backIn),
            Actions.visible(false)
        ));
    }
    
    public void hide() {
        clearActions();
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 0f, 0.2f, studioyes.kelimedunyasi.actions.Interpolation.backIn),
            Actions.visible(false)
        ));
    }
}
