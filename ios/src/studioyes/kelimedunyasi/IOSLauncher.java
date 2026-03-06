package studioyes.kelimedunyasi;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import java.util.HashMap;
import java.util.Map;

import studioyes.kelimedunyasi.net.WordMeaningProvider;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;

/**
 * iOS Launcher — AndroidLauncher'ın iOS karşılığı.
 *
 * Bu sınıf:
 *  - WordConnectGame nesnesini oluşturur
 *  - Tüm platform-spesifik interface'leri enjekte eder (Ads, IAP, Network, vb.)
 *  - RoboVM IOSApplication'ı başlatır
 *
 * Derleme için Mac + Xcode + RoboVM gereklidir.
 */
public class IOSLauncher extends IOSApplication.Delegate {

    // -----------------------------------------------------------------------
    // Statik Sabitler — kendi AdMob iOS birim ID'lerinizi buraya girin
    // -----------------------------------------------------------------------
    public static final String ADMOB_APP_ID              = "ca-app-pub-8670933603778284~1824503232";
    public static final String ADMOB_BANNER_ID           = "ca-app-pub-8670933603778284/9421793788";
    public static final String ADMOB_INTERSTITIAL_ID     = "ca-app-pub-8670933603778284/6885258228";
    public static final String ADMOB_REWARDED_ID         = "ca-app-pub-8670933603778284/8896702687";

    // IAP Ürün ID'leri (App Store Connect'teki ID'lerle eşleşmeli)
    public static final String IAP_REMOVE_ADS            = "studioyes.wordconnect.removeads";
    public static final String IAP_COIN_240              = "studioyes.wordconnect.coin240";
    public static final String IAP_COIN_760              = "studioyes.wordconnect.coin760";
    public static final String IAP_COIN_1340             = "studioyes.wordconnect.coin1340";
    public static final String IAP_COIN_2940             = "studioyes.wordconnect.coin2940";
    public static final String IAP_COIN_6240             = "studioyes.wordconnect.coin6240";
    public static final String IAP_COIN_13440            = "studioyes.wordconnect.coin13440";
    public static final String IAP_PACK_MINI             = "studioyes.wordconnect.pack.mini";
    public static final String IAP_PACK_MEDIUM           = "studioyes.wordconnect.pack.medium";
    public static final String IAP_PACK_LARGE            = "studioyes.wordconnect.pack.large";
    public static final String IAP_PACK_JUMBO            = "studioyes.wordconnect.pack.jumbo";

    // App Store destek e-postası
    public static final String SUPPORT_EMAIL             = "destek@biequiz.com";
    public static final String APP_STORE_ID              = "6759913485"; // App Store'daki numeric ID

    // -----------------------------------------------------------------------

    private WordConnectGame game;
    private IOSAdManager    adManager;
    private IOSShoppingProcessor shoppingProcessor;

    @Override
    protected IOSApplication createApplication() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println("[WC-DIAG] GLOBAL UNCAUGHT CRASH: " + e);
                e.printStackTrace();
                showCrashAlert("Fatal Error", e.toString());
            }
        });
        System.out.println("[WC-DIAG] STAGE-1: createApplication() started");
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = false;
        config.preventScreenDimming = false;

        System.out.println("[WC-DIAG] STAGE-2: Creating IOSAdManager");
        adManager = new IOSAdManager();

        System.out.println("[WC-DIAG] STAGE-3: Creating IOSShoppingProcessor");
        shoppingProcessor = new IOSShoppingProcessor();

        System.out.println("[WC-DIAG] STAGE-4: Adding IAP products");
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

        System.out.println("[WC-DIAG] STAGE-5: Creating IOSNetwork");
        Map<String, WordMeaningProvider> providerMap = new HashMap<>();
        providerMap.put("en", new IOSWordMeaningProvider());
        IOSNetwork network = new IOSNetwork();
        IOSDateUtil dateUtil = new IOSDateUtil();

        System.out.println("[WC-DIAG] STAGE-6: Creating WordConnectGame");
        game = new WordConnectGame(network, providerMap);
        game.dateUtil          = dateUtil;
        game.adManager         = adManager;
        game.shoppingProcessor = shoppingProcessor;
        game.appExit           = new IOSAppExit();
        game.rateUsLauncher    = new IOSRateUsLauncher(APP_STORE_ID);
        game.supportRequest    = new IOSSupportRequest(SUPPORT_EMAIL, game);

        System.out.println("[WC-DIAG] STAGE-7: Checking network reachability");
        boolean networkAvailable = network.isConnected();
        System.out.println("[WC-DIAG] STAGE-7 done: networkAvailable=" + networkAvailable);

        if (networkAvailable && !shoppingProcessor.isRemoveAdsPurchased()) {
            game.setYukseklik(200);
        } else {
            game.setYukseklik(0);
        }

        System.out.println("[WC-DIAG] STAGE-8: Creating IOSApplication (pre-GL init)");
        IOSApplication app = new IOSApplication(game, config);
        System.out.println("[WC-DIAG] STAGE-8: IOSApplication created OK");
        return app;
    }

    public static void showCrashAlert(final String title, final String message) {
        System.err.println("[WC-DIAG] ALERT: " + title + " - " + message);
        NSOperationQueue.getMainQueue().addOperation(new Runnable() {
            @Override
            public void run() {
                UIAlertController alert = new UIAlertController(title, message, UIAlertControllerStyle.Alert);
                alert.addAction(new UIAlertAction("OK", UIAlertActionStyle.Default, null));
                
                UIWindow window = UIApplication.getSharedApplication().getKeyWindow();
                if (window == null || window.getRootViewController() == null) {
                    window = new UIWindow(UIScreen.getMainScreen().getBounds());
                    window.setRootViewController(new UIViewController());
                    window.makeKeyAndVisible();
                }
                window.getRootViewController().presentViewController(alert, true, null);
            }
        });
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        try {
            boolean result = super.didFinishLaunching(application, launchOptions);
            System.out.println("[WC-DIAG] didFinishLaunching returned " + result);
            if (!result) {
                showCrashAlert("LibGDX Init Failed", "didFinishLaunching returned false");
            }
            return result;
        } catch (Throwable t) {
            System.err.println("[WC-DIAG] CRASH in didFinishLaunching: " + t);
            t.printStackTrace();
            showCrashAlert("Crash in Startup", t.toString());
            return true;
        }
    }

    public static void main(String[] argv) {
        try {
            try {
                // WC-DIAG10: Binary search for crash timing
                Thread.sleep(8000);
            } catch (Exception ignore) {}
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
