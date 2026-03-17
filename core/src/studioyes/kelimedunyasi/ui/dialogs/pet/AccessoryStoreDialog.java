package studioyes.kelimedunyasi.ui.dialogs.pet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;
import java.util.Map;

import studioyes.kelimedunyasi.graphics.AtlasRegions;
import studioyes.kelimedunyasi.graphics.NinePatches;
import studioyes.kelimedunyasi.managers.ResourceManager;
import studioyes.kelimedunyasi.managers.HintManager;
import studioyes.kelimedunyasi.model.GameData;
import studioyes.kelimedunyasi.model.PetAccessory;
import studioyes.kelimedunyasi.screens.BaseScreen;
import studioyes.kelimedunyasi.screens.GameScreen;
import studioyes.kelimedunyasi.ui.dialogs.BaseDialog;
import studioyes.kelimedunyasi.ui.pet.DoodiePet;

public class AccessoryStoreDialog extends BaseDialog {

    private Table contentTable;
    private ScrollPane scrollPane;
    private Label coinLabel;
    private PetAccessory.Category currentTab = PetAccessory.Category.HAT;
    private ResourceManager resourceManager;
    private DoodiePet petPreview;

    public AccessoryStoreDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);
        this.resourceManager = screen.wordConnectGame.resourceManager;
        
        GameScreen gs = (screen instanceof GameScreen) ? (GameScreen) screen : null;
        petPreview = new DoodiePet(screen.wordConnectGame, gs);
        petPreview.setScale(0.8f);
        
        content.setSize(width * 0.9f, height * 0.82f);
        setContentBackground();
        
        createUI();
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        content.addActor(root);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.WHITE);
        Label title = new Label("Doodie Store", titleStyle);
        root.add(title).pad(10).row();

        // Pet Preview Area
        Table petTable = new Table();
        petTable.add(petPreview).size(petPreview.getWidth() * 0.8f, petPreview.getHeight() * 0.8f);
        root.add(petTable).pad(10).row();

        // Coins
        coinLabel = new Label("Coins: " + HintManager.getRemainingCoins(), titleStyle);
        coinLabel.setFontScale(0.7f);
        root.add(coinLabel).right().padRight(40).row();

        // Tabs
        Table tabs = new Table();
        for (final PetAccessory.Category cat : PetAccessory.Category.values()) {
            if (cat == PetAccessory.Category.CLOTHING) continue;
            
            TextButton.TextButtonStyle tabStyle = new TextButton.TextButtonStyle();
            tabStyle.font = resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class);
            tabStyle.fontColor = Color.LIGHT_GRAY;
            
            final TextButton btn = new TextButton(cat.name(), tabStyle);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentTab = cat;
                    refreshStoreItems();
                }
            });
            tabs.add(btn).pad(10);
        }
        root.add(tabs).pad(10).row();

        // Content
        contentTable = new Table();
        scrollPane = new ScrollPane(contentTable);
        root.add(scrollPane).grow().pad(20).row();

        // Close button
        setCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        refreshStoreItems();
    }

    private void refreshStoreItems() {
        contentTable.clear();
        
        HashSet<Integer> owned = GameData.getOwnedAccessories();
        Map<PetAccessory.Category, Integer> equipped = GameData.getEquippedAccessories();
        
        Array<PetAccessory> items = new Array<>();
        for (PetAccessory acc : PetAccessory.getAll()) {
            if (acc.getCategory() == currentTab) {
                items.add(acc);
            }
        }

        for (final PetAccessory item : items) {
            Table itemRow = new Table();
            itemRow.setBackground(new NinePatchDrawable(NinePatches.rrect));
            itemRow.getColor().a = 0.2f;
            
            // Icon
            if (item.getCategory() == PetAccessory.Category.COLOR) {
                Image colorBox = new Image(AtlasRegions.roundRectRegion);
                colorBox.setColor(Color.valueOf(item.getRegionName()));
                itemRow.add(colorBox).size(64).pad(10);
            } else {
                Image icon = new Image(resourceManager.getAtlasRegion(item.getRegionName()));
                itemRow.add(icon).size(64).pad(10);
            }

            itemRow.add(new Label(item.getName(), getLabelStyle())).expandX().left().padLeft(20);

            boolean isOwned = owned.contains(item.getId()) || item.getPrice() == 0;
            boolean isEquipped = equipped.get(item.getCategory()) != null && equipped.get(item.getCategory()) == item.getId();
            
            // Level requirement check (e.g., Golden Doodie at level 100)
            int currentLevel = GameData.getPetLevel();
            boolean levelRequirementMet = true;
            if (item.getId() == 104 && currentLevel < 100) {
                levelRequirementMet = false;
            }

            if (isEquipped) {
                itemRow.add(new Label("EQUIPPED", getLabelStyle())).pad(10);
            } else if (!levelRequirementMet) {
                Label lockLabel = new Label("Level 100+", getLabelStyle());
                lockLabel.setColor(Color.RED);
                itemRow.add(lockLabel).pad(10);
            } else if (isOwned) {
                TextButton equipBtn = new TextButton("EQUIP", getButtonStyle());
                equipBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        GameData.saveEquippedAccessory(item.getCategory(), item.getId());
                        if (item.getCategory() == PetAccessory.Category.COLOR) {
                            GameData.saveEquippedColor(item.getRegionName());
                        }
                        petPreview.applyCustomizations();
                        refreshStoreItems();
                    }
                });
                itemRow.add(equipBtn).size(120, 50).pad(10);
            } else {
                TextButton buyBtn = new TextButton(item.getPrice() + " C", getButtonStyle());
                buyBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (HintManager.getRemainingCoins() >= item.getPrice()) {
                            HintManager.spendCoins(item.getPrice());
                            GameData.saveOwnedAccessory(item.getId());
                            coinLabel.setText("Coins: " + HintManager.getRemainingCoins());
                            refreshStoreItems();
                        }
                    }
                });
                itemRow.add(buyBtn).size(120, 50).pad(10);
            }

            contentTable.add(itemRow).growX().pad(5).row();
        }
    }

    private TextButton.TextButtonStyle getButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class);
        style.fontColor = Color.WHITE;
        return style;
    }

    private Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.WHITE);
    }

    @Override
    public void hide() {
        super.hide();
        if (screen instanceof GameScreen) {
            GameScreen gs = (GameScreen) screen;
            if (gs.doodiePet != null) {
                gs.doodiePet.applyCustomizations();
            }
        }
    }
}
