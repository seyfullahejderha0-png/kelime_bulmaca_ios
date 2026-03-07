package studioyes.kelimedunyasi;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import java.util.HashMap;
import java.util.Map;

import studioyes.kelimedunyasi.net.WordMeaningProvider;
import org.robovm.apple.uikit.UIApplication;

/**
 * iOS Launcher — AndroidLauncher'ın iOS karşılığı.
 *
 * Bu sınıf:
 * - WordConnectGame nesnesini oluşturur
 * - Tüm platform-spesifik interface'leri enjekte eder (Ads, IAP, Network, vb.)
 * - RoboVM IOSApplication'ı başlatır
 *
 * Derleme için Mac + Xcode + RoboVM gereklidir.
 */
public class IOSLauncher extends IOSApplication.Delegate {

    // -----------------------------------------------------------------------
    // Statik Sabitler — kendi AdMob iOS birim ID'lerinizi buraya girin
    // -----------------------------------------------------------------------
    public static final String ADMOB_APP_ID = "ca-app-pub-8670933603778284~1824503232";
    public static final String ADMOB_BANNER_ID = "ca-app-pub-8670933603778284/9421793788";
    public static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-8670933603778284/6885258228";
    public static final String ADMOB_REWARDED_ID = "ca-app-pub-8670933603778284/8896702687";

    // IAP Ürün ID'leri (App Store Connect'teki ID'lerle eşleşmeli)
    public static final String IAP_REMOVE_ADS = "studioyes.wordconnect.removeads";
    public static final String IAP_COIN_240 = "studioyes.wordconnect.coin240";
    public static final String IAP_COIN_760 = "studioyes.wordconnect.coin760";
    public static final String IAP_COIN_1340 = "studioyes.wordconnect.coin1340";
    public static final String IAP_COIN_2940 = "studioyes.wordconnect.coin2940";
    public static final String IAP_COIN_6240 = "studioyes.wordconnect.coin6240";
    public static final String IAP_COIN_13440 = "studioyes.wordconnect.coin13440";
    public static final String IAP_PACK_MINI = "studioyes.wordconnect.pack.mini";
    public static final String IAP_PACK_MEDIUM = "studioyes.wordconnect.pack.medium";
    public static final String IAP_PACK_LARGE = "studioyes.wordconnect.pack.large";
    public static final String IAP_PACK_JUMBO = "studioyes.wordconnect.pack.jumbo";

    // App Store destek e-postası
    public static final String SUPPORT_EMAIL = "destek@biequiz.com";
    public static final String APP_STORE_ID = "6759913485"; // App Store'daki numeric ID

    // -----------------------------------------------------------------------

    private WordConnectGame game;
    private IOSAdManager adManager;
    private IOSShoppingProcessor shoppingProcessor;

    @Override
    protected IOSApplication createApplication() {
        System.out.println("[WC] createApplication() starting");

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        // config.hdpiMode = HdpiMode.Pixels; // Skipping for now to ensure build
        // success if import fails

        // Initialize Managers
        adManager = new IOSAdManager();
        shoppingProcessor = new IOSShoppingProcessor();

        // Register products to IAP manager
        shoppingProcessor.addProduct(IAP_REMOVE_ADS);
        shoppingProcessor.addProduct(IAP_COIN_240);
        shoppingProcessor.addProduct(IAP_COIN_760);
        shoppingProcessor.addProduct(IAP_COIN_1340);
        shoppingProcessor.addProduct(IAP_COIN_2940);
        shoppingProcessor.addProduct(IAP_COIN_6240);
        shoppingProcessor.addProduct(IAP_COIN_13440);
        shoppingProcessor.addProduct(IAP_PACK_MINI);
        shoppingProcessor.addProduct(IAP_PACK_MEDIUM);
        shoppingProcessor.addProduct(IAP_PACK_LARGE);
        shoppingProcessor.addProduct(IAP_PACK_JUMBO);

        // WordMeaning providers map (stubs for now)
        Map<String, WordMeaningProvider> providerMap = new HashMap<>();

        // Create the game instance
        game = new WordConnectGame(null, providerMap);
        game.adManager = adManager;
        game.shoppingProcessor = shoppingProcessor;
        game.version = "1.0.45"; // Build 45

        System.out.println("[WC] Returning IOSApplication with real game");
        return new IOSApplication(game, config);
    }

    public static void showCrashAlert(final String title, final String message) {
        System.err.println("[WC-DIAG] ALERT: " + title + " - " + message);
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(new Runnable() {
            @Override
            public void run() {
                org.robovm.apple.uikit.UIAlertController alert = new org.robovm.apple.uikit.UIAlertController(title,
                        message, org.robovm.apple.uikit.UIAlertControllerStyle.Alert);
                alert.addAction(new org.robovm.apple.uikit.UIAlertAction("OK",
                        org.robovm.apple.uikit.UIAlertActionStyle.Default, null));

                org.robovm.apple.uikit.UIWindow window = UIApplication.getSharedApplication().getKeyWindow();
                if (window == null || window.getRootViewController() == null) {
                    window = new org.robovm.apple.uikit.UIWindow(
                            org.robovm.apple.uikit.UIScreen.getMainScreen().getBounds());
                    window.setRootViewController(new org.robovm.apple.uikit.UIViewController());
                    window.makeKeyAndVisible();
                }
                window.getRootViewController().presentViewController(alert, true, null);
            }
        });
    }

    @Override
    public boolean didFinishLaunching(UIApplication application,
            org.robovm.apple.uikit.UIApplicationLaunchOptions launchOptions) {

        // Initialize AdMob
        org.robovm.pods.google.mobileads.GADMobileAds.getSharedInstance().start(null);

        // Initialize IAP
        if (shoppingProcessor != null) {
            shoppingProcessor.initialize();
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println("[WC-DIAG] UNCAUGHT: " + e.toString());
                showCrashAlert("Uncaught Exception", e.toString());
            }
        });

        try {
            boolean result = super.didFinishLaunching(application, launchOptions);
            System.out.println("[WC-DIAG] didFinishLaunching returned " + result);
            if (!result) {
                showCrashAlert("LibGDX Init Failed", "EAGLContext cannot be spawned.");
            }

            // Initial ad loads
            if (adManager != null) {
                adManager.loadBanner();
                adManager.loadInterstitial();
                adManager.loadRewarded();
            }

            return true;
        } catch (Throwable t) {
            System.err.println("[WC-DIAG] CRASH in didFinishLaunching: " + t);
            t.printStackTrace();
            showCrashAlert("Crash in Startup", t.toString());
            return true;
        }
    }

    public static void main(String[] argv) {
        try {
            NSAutoreleasePool pool = new NSAutoreleasePool();
            UIApplication.main(argv, null, IOSLauncher.class);
            pool.close();
        } catch (Throwable t) {
            System.err.println("[WC-DIAG] CRASH in main: " + t);
            t.printStackTrace();
            showCrashAlert("Main Crash", t.toString());
        }
    }
}
