package studioyes.kelimedunyasi.ui.top_panel;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import studioyes.kelimedunyasi.Ayarlar;
import studioyes.kelimedunyasi.config.ConfigProcessor;
import studioyes.kelimedunyasi.config.GameConfig;
import studioyes.kelimedunyasi.config.UIConfig;
import studioyes.kelimedunyasi.graphics.AtlasRegions;
import studioyes.kelimedunyasi.managers.ResourceManager;
import studioyes.kelimedunyasi.screens.BaseScreen;
import studioyes.kelimedunyasi.screens.GameScreen;
import studioyes.kelimedunyasi.util.UiUtil;


public class TopPanel extends Group {


    public CoinView coinView;
    public TopComboAndLevelDisplay topComboDisplay;
    public BaseScreen screen;
    public ImageButton btnMenu;
    public ImageButton backBtn;
    private float width;


    public TopPanel(BaseScreen screen, float width){
        this.screen = screen;
        this.width = width;
        ResourceManager resourceManager = screen.wordConnectGame.resourceManager;

        float sayi= (AtlasRegions.coin_view_bg.originalHeight+screen.wordConnectGame.getYukseklik()) * (UiUtil.isScreenWide() ? UIConfig.MARGIN_TOP_WIDE_SCREEN : UIConfig.MARGIN_TOP_NORMAL_SCREEN);
        setWidth(width);
        setHeight(sayi);
        new Ayarlar().setTopPanelHeigh(sayi);


        if(screen instanceof GameScreen) {
            topComboDisplay = new TopComboAndLevelDisplay(resourceManager);
            topComboDisplay.setY((getHeight() - topComboDisplay.getHeight()) * 0.5f);
            addActor(topComboDisplay);

            if(!GameConfig.SKIP_INTRO) {
                backBtn = new ImageButton(new TextureRegionDrawable(AtlasRegions.back_up), new TextureRegionDrawable(AtlasRegions.back_down));
                addActor(backBtn);
                backBtn.setX(0);
                backBtn.setY((getHeight() - backBtn.getHeight()) * 0.5f);
                backBtn.addListener(((GameScreen) screen).gotoIntroScreen);
            }
        }

        coinView = new CoinView(screen);
        coinView.setX(width - coinView.getWidth());
        coinView.setY((getHeight() - coinView.getHeight()) * 0.5f);
        addActor(coinView);

        boolean inEu = screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isUserInEU();
        if(ConfigProcessor.isMenuEnabled(inEu, GameConfig.availableLanguages.size() > 1)) {
            btnMenu = new ImageButton(new TextureRegionDrawable(AtlasRegions.settings_up), new TextureRegionDrawable(AtlasRegions.settings_down));
            addActor(btnMenu);
            if(backBtn != null) btnMenu.setX(backBtn.getX() + backBtn.getWidth() * 1.2f);
            else btnMenu.setX(0);

            btnMenu.setY((getHeight() - btnMenu.getHeight()) * 0.5f);
        }

        if(topComboDisplay != null ) {
            float leftMost = coinView.getWidth();
            float centerWidth = coinView.getX() - leftMost;
            topComboDisplay.setWidth(centerWidth);
            topComboDisplay.setX(leftMost);
            topComboDisplay.setComboCount(0, null);
        }


    }





    public void addMenuButtonListener(ChangeListener changeListener){
        if(btnMenu != null) btnMenu.addListener(changeListener);
    }


}
