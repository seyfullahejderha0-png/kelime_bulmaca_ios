package studioyes.kelimedunyasi.ui.pet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import studioyes.kelimedunyasi.config.UIConfig;
import studioyes.kelimedunyasi.graphics.AtlasRegions;
import studioyes.kelimedunyasi.managers.ResourceManager;
import studioyes.kelimedunyasi.screens.GameScreen;
import studioyes.kelimedunyasi.model.GameData;
import studioyes.kelimedunyasi.model.PetAccessory;


public class DoodiePet extends Group {

    public enum PetState {
        IDLE, EAT, HAPPY, SAD, SLEEP
    }

    private PetState currentState;

    private float stateTime;
    private GameScreen gameScreen;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> eatAnimation;
    private Animation<TextureRegion> happyAnimation;
    private Animation<TextureRegion> sadAnimation;
    private Animation<TextureRegion> sleepAnimation;

    private Image petImage;
    private ProgressBar inkMeter;
    private Label levelDisplay;
    private java.util.Map<PetAccessory.Category, Image> accessoryImages;
    
    // XP rules
    private int currentLevel;
    private int currentInk;
    private int inkRequiredForNextLevel;

    private float growthScaleFactor = 1.0f;

    public DoodiePet(studioyes.kelimedunyasi.WordConnectGame game, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        ResourceManager resourceManager = game.resourceManager;
        this.stateTime = 0f;
        this.currentState = PetState.IDLE;
        
        loadXP();
        accessoryImages = new java.util.HashMap<>();
        createAnimations();
        createUI(resourceManager);
        applyCustomizations();
        
        setSize(petImage.getWidth() * 1.5f, petImage.getHeight() + inkMeter.getHeight() + 40); 
        setOrigin(Align.center);
    }

    private void loadXP() {
        currentLevel = GameData.getPetLevel();
        currentInk = GameData.getPetInk();
        inkRequiredForNextLevel = calculateRequiredInk(currentLevel);
        growthScaleFactor = 1.0f + (currentLevel * 0.02f); // 2% growth per level
    }
    
    private int calculateRequiredInk(int level) {
        // Base requirement 100, increases with level
        return 100 + (level * 50); 
    }

    private void createAnimations() {
        // --- PLACEHOLDERS ---
        // For now, we will use existing monster/particle graphics.
        // The user will replace these texture regions later with actual doodle art.
        
        Array<TextureAtlas.AtlasRegion> idleFrames = AtlasRegions.monsterIdleAnimation;
        if(idleFrames != null && idleFrames.size > 0) {
             idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames, Animation.PlayMode.LOOP);
        } else {
            // Fallback if monster anims are deleted
            TextureRegion fallback = AtlasRegions.star_particle;
            idleAnimation = new Animation<TextureRegion>(0.1f, fallback);
        }

        // using same idle as placeholder for others, varied by Actions later if needed, 
        // or just set all to idle and rely on Actions for 'eat' 'sad' visual difference for now.
        eatAnimation = idleAnimation; 
        happyAnimation = idleAnimation;
        sadAnimation = idleAnimation;
        sleepAnimation = idleAnimation;
    }

    private void createUI(ResourceManager resourceManager) {
        petImage = new Image();
        updatePetImageRegion();
        // Position pet image at bottom of the group
        petImage.setPosition(0, 0); 
        petImage.setScale(growthScaleFactor);
        this.addActor(petImage);

        // Simple ink meter on top of the pet
        inkMeter = new ProgressBar(AtlasRegions.bonus_bar_bg, AtlasRegions.bonus_words_bar_track);
        inkMeter.setSize(petImage.getWidth() * 0.8f, 20); // Scale relative to pet
        inkMeter.setPosition((petImage.getWidth() - inkMeter.getWidth()) / 2f, petImage.getHeight() + 20);
        updateInkMeterDisplay();
        this.addActor(inkMeter);

        // Level Label
        String font = UIConfig.LEVEL_NUMBER_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle style = new Label.LabelStyle(resourceManager.get(font, BitmapFont.class), UIConfig.LEVEL_NUMBER_TEXT_COLOR);
        levelDisplay = new Label("Lv." + currentLevel, style);
        levelDisplay.setFontScale(0.6f);
        levelDisplay.setPosition(inkMeter.getX() + inkMeter.getWidth() + 10, inkMeter.getY() - 5);
        this.addActor(levelDisplay);
    }
    
    private void updatePetImageRegion() {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(0);
        
        petImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(currentFrame));
        petImage.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        petImage.setOrigin(Align.center);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        
        Animation<TextureRegion> currentAnim = getAnimationForState(currentState);
        if(currentAnim != null) {
            TextureRegion frame = currentAnim.getKeyFrame(stateTime);
            ((com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable) petImage.getDrawable()).setRegion(frame);
            
            // Sync accessory positions and origins if they were to move or scale with the pet
            // For now they are children of the group, so they stay in place relative to this group.
        }
    }

    public void applyCustomizations() {
        // Apply color
        String hex = GameData.getEquippedColor();
        petImage.setColor(Color.valueOf(hex));

        // Apply accessories
        java.util.Map<PetAccessory.Category, Integer> equipped = GameData.getEquippedAccessories();
        
        // Clear old ones
        for (Image img : accessoryImages.values()) {
            img.remove();
        }
        accessoryImages.clear();

        for (PetAccessory.Category cat : PetAccessory.Category.values()) {
            if (cat == PetAccessory.Category.COLOR) continue;
            
            Integer accId = equipped.get(cat);
            if (accId != null && accId > 0) {
                PetAccessory acc = PetAccessory.getById(accId);
                if (acc != null) {
                    addAccessoryOverlay(acc);
                }
            }
        }
    }

    private void addAccessoryOverlay(PetAccessory acc) {
        TextureRegion region = gameScreen.wordConnectGame.resourceManager.getAtlasRegion(acc.getRegionName());
        if (region == null) return;

        Image accImg = new Image(region);
        accImg.setOrigin(Align.center);
        
        // Approximate placement logic - user will refine these
        // In a real app, each accessory might have specific offset data
        float x = 0;
        float y = 0;
        
        switch(acc.getCategory()) {
            case HAT:
                x = (petImage.getWidth() - accImg.getWidth()) / 2f;
                y = petImage.getHeight() * 0.8f;
                break;
            case GLASSES:
                x = (petImage.getWidth() - accImg.getWidth()) / 2f;
                y = petImage.getHeight() * 0.55f;
                break;
            case BOWTIE:
                x = (petImage.getWidth() - accImg.getWidth()) / 2f;
                y = petImage.getHeight() * 0.2f;
                break;
            default:
                // Other categories (COLOR, CLOTHING) don't have standard positions yet
                break;
        }
        
        accImg.setPosition(x, y);
        addActor(accImg);
        accessoryImages.put(acc.getCategory(), accImg);
    }
    
    private Animation<TextureRegion> getAnimationForState(PetState state) {
        switch (state) {
            case EAT: return eatAnimation;
            case HAPPY: return happyAnimation;
            case SAD: return sadAnimation;
            case SLEEP: return sleepAnimation;
            case IDLE:
            default: return idleAnimation;
        }
    }

    // ---- STATE TRANSITIONS AND ACTIONS ----

    public void setIdle() {
        if(currentState != PetState.IDLE) {
            currentState = PetState.IDLE;
            stateTime = 0f;
            petImage.clearActions();
            petImage.setScale(1f);
            petImage.setRotation(0f);
        }
    }

    public void eat(int inkGained) {
        currentState = PetState.EAT;
        stateTime = 0f;
        
        currentInk += inkGained;
        
        // Physical jump reaction
        petImage.clearActions();
        Action jumpUp = Actions.moveBy(0, 30, 0.2f, Interpolation.circleOut);
        Action scaleUp = Actions.scaleTo(1.2f, 1.2f, 0.2f);
        Action jumpDown = Actions.moveBy(0, -30, 0.2f, Interpolation.circleIn);
        Action scaleDown = Actions.scaleTo(1f, 1f, 0.2f);
        
        Action returnToIdle = Actions.run(new Runnable() {
            @Override
            public void run() {
                checkLevelUp();
                setIdle();
            }
        });
        
        petImage.addAction(Actions.sequence(
            Actions.parallel(jumpUp, scaleUp),
            Actions.parallel(jumpDown, scaleDown),
            returnToIdle
        ));

        // UI progress reaction
        updateInkMeterDisplay();
        GameData.savePetInk(currentInk);
    }

    public void sad() {
        currentState = PetState.SAD;
        stateTime = 0f;
        
        // Shake reaction
        petImage.clearActions();
        Action left = Actions.moveBy(-10, 0, 0.05f);
        Action right = Actions.moveBy(20, 0, 0.1f);
        Action center = Actions.moveBy(-10, 0, 0.05f);
        
        Action returnToIdle = Actions.run(new Runnable() {
            @Override
            public void run() {
                setIdle();
            }
        });
        
        petImage.addAction(Actions.sequence(
            left, right, left, right, center,
            Actions.delay(0.5f),
            returnToIdle
        ));
    }
    
    public void happy() {
        currentState = PetState.HAPPY;
        stateTime = 0f;
        
        // Spin or big jump reaction
        petImage.clearActions();
        Action spin = Actions.rotateBy(360, 0.5f, Interpolation.circleOut);
        
        Action returnToIdle = Actions.run(new Runnable() {
            @Override
            public void run() {
                setIdle();
            }
        });
        petImage.addAction(Actions.sequence(spin, returnToIdle));
    }
    
    public void sleep() {
        if(currentState != PetState.SLEEP) {
            currentState = PetState.SLEEP;
            stateTime = 0f;
            petImage.clearActions();
            // maybe scale down slightly to look "resting"
            petImage.addAction(Actions.scaleTo(0.95f, 0.90f, 0.5f));
        }
    }

    private void checkLevelUp() {
        if(currentInk >= inkRequiredForNextLevel) {
            currentInk -= inkRequiredForNextLevel; // rollover
            currentLevel++;
            GameData.savePetLevel(currentLevel);
            GameData.savePetInk(currentInk);
            inkRequiredForNextLevel = calculateRequiredInk(currentLevel);
            
            levelDisplay.setText("Lv." + currentLevel);
            updateInkMeterDisplay();

            // Reward player with coins
            if (gameScreen != null) {
                gameScreen.awardPetCoinReward(50, getX() + getWidth() / 2f, getY() + getHeight() / 2f);
            }
            
            // Check for evolution milestone
            if (currentLevel == 11 || currentLevel == 26 || currentLevel == 51) {
                playEvolutionEffect();
            } else {
                happy(); // regular level up celebration
                applyGrowth();
            }
        }
    }

    private void applyGrowth() {
        growthScaleFactor = 1.0f + (currentLevel * 0.02f);
        petImage.addAction(Actions.scaleTo(growthScaleFactor, growthScaleFactor, 0.5f, Interpolation.swingOut));
    }

    private void playEvolutionEffect() {
        // Epic evolution animation
        petImage.clearActions();
        
        Action rotate = Actions.rotateBy(720, 1f, Interpolation.sine);
        Action scaleUp = Actions.scaleTo(growthScaleFactor * 1.5f, growthScaleFactor * 1.5f, 0.5f, Interpolation.pow5In);
        Action colorFlash = Actions.sequence(
            Actions.color(Color.WHITE, 0.1f),
            Actions.color(Color.GOLD, 0.1f),
            Actions.color(Color.WHITE, 0.1f)
        );
        
        Action finalState = Actions.run(new Runnable() {
            @Override
            public void run() {
                // Here we would swap to new textures for the stage
                // For now, just reset scale to the new normal
                applyGrowth();
                happy();
            }
        });

        petImage.addAction(Actions.sequence(
            Actions.parallel(rotate, scaleUp, Actions.repeat(3, colorFlash)),
            finalState
        ));
    }

    private void updateInkMeterDisplay() {
        float percent = (float) currentInk / (float) inkRequiredForNextLevel;
        if(percent > 1f) percent = 1f;
        inkMeter.setPercent(percent);
    }

    // Helper class for the ink meter, drawing a background and a filled track
    private class ProgressBar extends Group {
        private Image bg;
        private Image track;
        private float maxWidth;

        public ProgressBar(TextureRegion bgRegion, TextureRegion trackRegion) {
            bg = new Image(bgRegion);
            track = new Image(trackRegion);
            
            this.addActor(bg);
            this.addActor(track);
            
            // color track ink-blue
            track.setColor(new Color(0.2f, 0.5f, 0.9f, 1f)); 
        }

        @Override
        public void setSize(float width, float height) {
            super.setSize(width, height);
            bg.setSize(width, height);
            track.setSize(0, height); // starts at 0
            maxWidth = width;
        }

        public void setPercent(float percent) {
            // Animate track filling
            track.clearActions();
            float targetWidth = maxWidth * percent;
            track.addAction(Actions.sizeTo(targetWidth, track.getHeight(), 0.3f, Interpolation.sineOut));
        }
    }
}
