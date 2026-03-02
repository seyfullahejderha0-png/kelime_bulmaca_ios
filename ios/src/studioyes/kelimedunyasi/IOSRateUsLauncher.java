package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;

import studioyes.kelimedunyasi.util.RateUsLauncher;

/**
 * iOS App Store değerlendirme yönlendirici.
 * RateUsLauncher interface'ini implement eder.
 *
 * İki yöntem desteklenir:
 *  1. SKStoreReviewController.requestReview() — native popup (iOS 10.3+)
 *  2. App Store URL yönlendirmesi (yedek yöntem)
 */
public class IOSRateUsLauncher implements RateUsLauncher {

    private final String appStoreId;

    public IOSRateUsLauncher(String appStoreId) {
        this.appStoreId = appStoreId;
    }

    @Override
    public void launch() {
        Gdx.app.log("IOSRateUs", "Launching App Store rating for app: " + appStoreId);

        Gdx.app.postRunnable(() -> {
            try {
                /*
                 * Yöntem 1: SKStoreReviewController (Mac'te RoboVM binding ile):
                 *   SKStoreReviewController.requestReview();
                 *
                 * Yöntem 2: App Store URL'ye yönlendir (her zaman çalışır):
                 */
                String urlStr = "itms-apps://itunes.apple.com/app/id" + appStoreId + "?action=write-review";
                NSURL url = new NSURL(urlStr);
                if (UIApplication.getSharedApplication().canOpenURL(url)) {
                    UIApplication.getSharedApplication().openURL(url);
                } else {
                    // Fallback: tarayıcıdan aç
                    NSURL webUrl = new NSURL(
                        "https://apps.apple.com/app/id" + appStoreId + "?action=write-review"
                    );
                    UIApplication.getSharedApplication().openURL(webUrl);
                }
            } catch (Exception e) {
                Gdx.app.error("IOSRateUs", "Failed to open App Store", e);
            }
        });
    }
}
