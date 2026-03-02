package studioyes.kelimedunyasi.config;

import com.badlogic.gdx.math.MathUtils;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import studioyes.kelimedunyasi.graphics.AtlasRegions;
import studioyes.kelimedunyasi.i18n.Locale;
import studioyes.kelimedunyasi.model.Constants;
import studioyes.kelimedunyasi.ui.dialogs.iap.ItemContent;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingDialog;
import studioyes.kelimedunyasi.ui.dialogs.wheel.Slice;

import static studioyes.kelimedunyasi.ui.dialogs.wheel.RewardRevealType.COINS;
import static studioyes.kelimedunyasi.ui.dialogs.wheel.RewardRevealType.FINGER_REVEAL;
import static studioyes.kelimedunyasi.ui.dialogs.wheel.RewardRevealType.MULTI_RANDOM_REVEAL;
import static studioyes.kelimedunyasi.ui.dialogs.wheel.RewardRevealType.ROCKET_REVEAL;
import static studioyes.kelimedunyasi.ui.dialogs.wheel.RewardRevealType.SINGLE_RANDOM_REVEAL;


public class GameConfig {

    /**
     * WARNING: Don't forget to turn off the debug-related settings below
     * before publishing your game to the App Store.
     */



    //Displays lucky wheel as if it is time to show it. SKIP_INTRO must be false
    public static final boolean DEBUG_LUCKY_WHEEL                                = false;

    //Display the rate us dialog as if it is time to show it. SKIP_INTRO must be false
    public static final boolean DEBUG_RATE_US                                    = false;

    //To see the correct answers of levels on logcat output. Filter it using "game.log"
    public static final boolean DEBUG_LEVEL_ANSWERS                              = true;

    //Bonus words win dialog opens as if you won a reward when you click the bonus words button (button with a star on)
    public static final boolean DEBUG_BONUS_WORDS_WIN_DIALOG                     = false;


    /**
     * To debug any level you wish. It skips the preceding levels.
     * If the latest level you've played was incomplete, any data related to
     * it would be deleted when you use this option.
     * This option may not show the booster in the chosen level that displays
     * in normal game-play.
     * The number must be your desired level number minus one (it is zero-based).
     * Make it -1 when you are done.
     */
    public static final int DEBUG_LEVEL_INDEX                                   = -1;





    /**
     * Enables to skip the intro screen.
     * When intro is skipped, back navigation button via the back button at the top-left of
     * the screen will be hidden and the native back button of Android will
     * bring the app to background when tapped at game screen.
     *
     */
    public static final boolean SKIP_INTRO                                       = false;




    /**
     * Players can use reveal hints when they enough coins.
     * It is set to 0 here because they have a lot of change to win coins.
     */
    public static final int DEFAULT_COIN_COUNT                                  = 250;



    /**
     * Number of reveal counts must be at least 2 because
     * 1 of them will be used for tutorial and it is a good idea to have
     * one after the tutorial
     */
    public static final int DEFAULT_SINGLE_RANDOM_REVEAL_COUNT                  = 2;
    public static final int DEFAULT_FINGER_REVEAL_COUNT                         = 2;
    public static final int DEFAULT_MULTI_RANDOM_REVEAL_COUNT                   = 2;
    public static final int DEFAULT_ROCKET_REVEAL_COUNT                         = 2;

    //When the player finds words that don't exist on board, he/she earns some reward
    public static final int NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD            = 25;
    public static final int NUMBER_OF_COINS_AWARDED_FOR_BONUS_WORDS_REWARD      = 25;



    /**
     * When the player runs out of hints, he/she can still use reveal hints
     * as long as he/she has some coins that are equal or greater than
     * the following values. The cost of reveal hint is then subtracted from coins.
     */
    public static final int COIN_COST_OF_USING_SINGLE_RANDOM_REVEAL             = 50;
    public static final int COIN_COST_OF_USING_MULTI_RANDOM_REVEAL              = 150;
    public static final int COIN_COST_OF_USING_FINGER_REVEAL                    = 100;
    public static final int COIN_COST_OF_USING_ROCKET_REVEAL                    = 100;


    //Gold coins on game board (appears after using a rocket reveal hint and after killing the monster)
    public static final int NUMBER_OF_COINS_EARNED_FOR_TAKING_1_COIN            = 3;


    public static final int NUM_OF_TILES_TO_SET_COIN_AFTER_KILLING_THE_MONSTER  = 10;


    /**
     * The rewarded video ad button display a glow effect to catch the attention
     *  of the player. By default it is a random value between 15 and 60 seconds.
     */
    public static final float REWARDED_VIDEO_BUTTON_GLOW_INTERVAL               = MathUtils.random(15, 60);





    /**
     * By default the cost of a single random reveal hint is 100. If the player
     * watches 4 videos, he/she will earn a single random reveal hint.
     * Note: You can configure rewarded video availability at res/strings.xml
     */
    public static final int NUMBER_OF_COINS_EARNED_FOR_WATCHING_VIDEO           = 100;





    /**
     * Multi-random reveal button is the one with a blue bolt on.
     * You should return at least 2 so that it reveals more than the single random hint.
     * It is a good idea to decorate the board with more revealed tiles as the
     * size of the board, hence the difficulty of the game increases. So you should
     *  return a number proportional to the levelIndex parameter.
     *  Or, you can totally ignore this and return a constant number such as 3.
     * @param levelIndex 0-based level number
     * @return reveal count
     */
    public static int getNumberOfTilesToRevealForMultiRandomHint(int levelIndex){
        if(levelIndex < 100) return 3;
        else if(levelIndex < 500) return 4;
        else if(levelIndex < 3000) return 5;
        else return 6;
    }


    public static boolean shouldWeShowAnInterstitialAdForThisLevel(int levelIndex){

        int realLevel = levelIndex + 1;

        // 1️⃣ İlk 6 level reklam yok
        if (realLevel <= 4) {
            return false;
        }

        // 2️⃣ 7–20 arası: 2 levelde 1 reklam
        if (realLevel <= 50) {
            return realLevel % 2 == 0;
        }

        // 3️⃣ 20'den sonra: her level reklam
        return true;
    }

    public static final boolean SHOW_REMOVE_ADS_DIALOG_AFTER_INTERSTITIAL   = true;


    public static final boolean SHOW_WATCH_AD_AFTER_IAP                     = true;

    public static final boolean SHOW_WATCH_AD_WHEN_NO_COINS_AND_HINTS_LEFT  = true;


    public static void setUpIAPToItemMapping(){

        ShoppingDialog.mapping.put("006", new ItemContent(250, AtlasRegions.coins1));//IAP_ITEM_coin_240
        ShoppingDialog.mapping.put("007", new ItemContent(800, AtlasRegions.coins2));//IAP_ITEM_coin_760
        ShoppingDialog.mapping.put("008", new ItemContent(1350, AtlasRegions.coins3));//IAP_ITEM_coin_1340
        ShoppingDialog.mapping.put("009", new ItemContent(3000, AtlasRegions.coins4));//IAP_ITEM_coin_2940
        ShoppingDialog.mapping.put("010", new ItemContent(6250, AtlasRegions.coins5));//IAP_ITEM_coin_6240
        ShoppingDialog.mapping.put("011", new ItemContent(13500, AtlasRegions.coins6));//IAP_ITEM_coin_13440

        ShoppingDialog.mapping.put("002", new ItemContent(3350, 3, 3, 3, 3, AtlasRegions.bundle1));//IAP_ITEM_pack_mini
        ShoppingDialog.mapping.put("003", new ItemContent(7350, 7, 7, 7, 7, AtlasRegions.bundle2));//IAP_ITEM_pack_medium
        ShoppingDialog.mapping.put("004", new ItemContent(15600, 14, 14, 14, 14, AtlasRegions.bundle3));//IAP_ITEM_pack_large
        ShoppingDialog.mapping.put("005", new ItemContent(33600, 30, 30, 30, 30, AtlasRegions.bundle4));//IAP_ITEM_pack_jumbo

        ShoppingDialog.mapping.put("001", new ItemContent(true, AtlasRegions.remove_ads));//IAP_ITEM_remove_ads

    };


    public static final int ALLOWED_SPIN_COUNT                           = 3;

    //Ozel kaç tane olacak
    public static final int ALLOWED_SPIN_COUNTOzel                           = 3;

    //Kaç Seviyede bir gelecek
    public static final int ALLOWED_SPIN_COUNTOzelSeviye                           = 10;

    //hediye çarkın kaçıncı seviyeden itibaren başlayacı
    public static final int ALLOWED_SPIN_COUNTOzelBaslangic                           = 15;


    public static Slice[] slices = new Slice[]{
            new Slice("25",     COINS,                  25, 17),
            new Slice("50",     COINS,                  50, 16),
            new Slice("1",      FINGER_REVEAL,          1,  16),
            new Slice("500",    COINS,                  500,1),
            new Slice("1",      ROCKET_REVEAL,          1,  16),
            new Slice("1",      SINGLE_RANDOM_REVEAL,   1,  17),
            new Slice("1",      MULTI_RANDOM_REVEAL,    1,  16),
            new Slice("750",    COINS,                  750,1)
    };

    public static Slice[] slicesOzel = new Slice[]{
            new Slice("5",      COINS,                  5, 20),
            new Slice("10",     COINS,                  10, 20),
            new Slice("1",      FINGER_REVEAL,          1,  14),
            new Slice("100",    COINS,                  100,1),
            new Slice("1",      ROCKET_REVEAL,          1,  14),
            new Slice("1",      SINGLE_RANDOM_REVEAL,   1,  15),
            new Slice("1",      MULTI_RANDOM_REVEAL,    1,  15),
            new Slice("250",    COINS,                  250,1)
    };

    public static Map<String, Locale> availableLanguages = new LinkedHashMap<String, Locale>(){
        {
            put("en", new Locale(5000));
            put("tr", new Locale(5000));
            put("de", new Locale(1000));
            put("fr", new Locale(1000));
            put("it", new Locale(1000));
            put("esp", new Locale(1000));
            put("ru", new Locale(1000));
        }
    };


    public static final int BOOSTERS_START_LEVEL = 14;


    public static final int OFFER_BOOSTER_EVERY_N_LEVEL = 5;


    //Appearance order for boosters. If you wish to change their order.
    public static final int BOOSTER_UFO       = 0;
    public static final int BOOSTER_BOMB      = 1;
    public static final int BOOSTER_GOLD_PACK = 2;
    public static final int BOOSTER_MONSTER   = 3;



    //If you don't like any of the boosters, leave it out by specifying it as false below
    public static final Map<Integer, Boolean> ENABLED_BOOSTERS = new HashMap<Integer, Boolean>(){
        {
            put(BOOSTER_UFO,          true);
            put(BOOSTER_BOMB,         true);
            put(BOOSTER_GOLD_PACK,    true);
            put(BOOSTER_MONSTER,      true);
        }
    };



    //The ufo flies away after the time specified below. Starts when the ufo finishes landing.
    public static final float SECONDS_BEFORE_UFO_DISAPPEARS = 10f;

    //If the player hits the ufo before it flies away.
    public static final int NUMBER_OF_COINS_EARNED_FOR_HITTING_UFO = 15;



    //If the player can't disarm the bomb with the given moves, he/she
    //will be given some more moves in return to watching a rewarded ad.
    public static final int EXTRA_BOMB_MOVES_FOR_WATCHING_AD = 5;

    public static final boolean SHOW_RATE_DIALOG        = true;
    public static final int DAYS_TO_ELAPSE_BEFORE_RATE  = 3;
    public static final int APP_LAUNCHES_BEFORE_RATE    = 5;


    public static final float IDLE_TIMER_DURATION = 15;
    public static final float INTERVAL_BETWEEN_HINT_INDICATIONS = 5;


    //Speed of animating letters after a bonus word is found
    public static final float BONUS_WORD_LETTER_ANIM_SPEED = 0.3f;


}
