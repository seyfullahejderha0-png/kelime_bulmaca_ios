package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.pods.google.mobileads.*;

import studioyes.kelimedunyasi.managers.AdManager;
import studioyes.kelimedunyasi.util.RewardedVideoCloseCallback;

public class IOSAdManager implements AdManager {

    private GADBannerView bannerView;
    private GADInterstitialAd interstitialAd;
    private GADRewardedAd rewardedAd;

    private boolean rewardedLoaded = false;
    private boolean interstitialLoaded = false;

    public IOSAdManager() {
    }

    public void loadBanner() {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            if (bannerView != null)
                return;
            org.robovm.apple.uikit.UIWindow window = UIApplication.getSharedApplication().getKeyWindow();
            if (window == null)
                return;
            UIViewController root = window.getRootViewController();
            if (root == null)
                return;

            bannerView = new GADBannerView(GADAdSize.Banner());
            bannerView.setAdUnitID(IOSLauncher.ADMOB_BANNER_ID);
            bannerView.setRootViewController(root);

            double screenWidth = window.getBounds().getSize().getWidth();
            double safeTop = window.getSafeAreaInsets().getTop();
            if (safeTop == 0)
                safeTop = 20; // standard status bar height if no notch

            bannerView.setFrame(new org.robovm.apple.coregraphics.CGRect((screenWidth - 320) / 2.0, safeTop, 320, 50));
            root.getView().addSubview(bannerView);
            bannerView.loadRequest(new GADRequest());
        });
    }

    public void showBanner() {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            if (bannerView != null)
                bannerView.setHidden(false);
        });
    }

    public void hideBanner() {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            if (bannerView != null)
                bannerView.setHidden(true);
        });
    }

    public void loadInterstitial() {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            GADRequest request = new GADRequest();
            GADInterstitialAd.load(IOSLauncher.ADMOB_INTERSTITIAL_ID, request, (ad, error) -> {
                if (error != null) {
                    interstitialLoaded = false;
                    return;
                }
                interstitialAd = ad;
                interstitialLoaded = true;
            });
        });
    }

    @Override
    public boolean isInterstitialAdLoaded() {
        return interstitialLoaded;
    }

    @Override
    public void showInterstitialAd(Runnable closedCallback) {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            if (interstitialLoaded && interstitialAd != null) {
                UIViewController root = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();

                interstitialAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                    @Override
                    public void adDidDismissFullScreenContent(GADFullScreenPresentingAd presentingAd) {
                        interstitialLoaded = false;
                        interstitialAd = null;
                        if (closedCallback != null) {
                            Gdx.app.postRunnable(closedCallback);
                        }
                        loadInterstitial();
                    }

                    @Override
                    public void didFailToPresentFullScreenContent(GADFullScreenPresentingAd presentingAd,
                            NSError error) {
                        interstitialLoaded = false;
                        interstitialAd = null;
                        if (closedCallback != null) {
                            Gdx.app.postRunnable(closedCallback);
                        }
                        loadInterstitial();
                    }
                });

                interstitialAd.presentFromRootViewController(root);
            } else {
                if (closedCallback != null) {
                    Gdx.app.postRunnable(closedCallback);
                }
                loadInterstitial();
            }
        });
    }

    public void loadRewarded() {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            GADRequest request = new GADRequest();
            GADRewardedAd.load(IOSLauncher.ADMOB_REWARDED_ID, request, (ad, error) -> {
                if (error != null) {
                    rewardedLoaded = false;
                    return;
                }
                rewardedAd = ad;
                rewardedLoaded = true;
            });
        });
    }

    @Override
    public boolean isRewardedAdLoaded() {
        return rewardedLoaded;
    }

    @Override
    public void showRewardedAd(RewardedVideoCloseCallback finishedCallback) {
        org.robovm.apple.foundation.NSOperationQueue.getMainQueue().addOperation(() -> {
            if (rewardedLoaded && rewardedAd != null) {
                UIViewController root = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();

                rewardedAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                    @Override
                    public void adDidDismissFullScreenContent(GADFullScreenPresentingAd presentingAd) {
                        rewardedLoaded = false;
                        rewardedAd = null;
                        loadRewarded();
                    }

                    @Override
                    public void didFailToPresentFullScreenContent(GADFullScreenPresentingAd presentingAd,
                            NSError error) {
                        rewardedLoaded = false;
                        rewardedAd = null;
                        loadRewarded();
                    }
                });

                rewardedAd.present(root, () -> {
                    if (finishedCallback != null) {
                        Gdx.app.postRunnable(() -> finishedCallback.closed(true));
                    }
                });
            } else {
                if (finishedCallback != null) {
                    Gdx.app.postRunnable(() -> finishedCallback.closed(false));
                }
                loadRewarded();
            }
        });
    }

    @Override
    public boolean isInterstitialAdEnabled() {
        return true;
    }

    @Override
    public boolean isRewardedAdEnabledToEarnCoins() {
        return true;
    }

    @Override
    public boolean isRewardedAdEnabledToEarnMoves() {
        return true;
    }

    @Override
    public boolean isRewardedAdEnabledToSpinWheel() {
        return true;
    }

    @Override
    public boolean isRewardedAdEnabledToSonrakiSeviye() {
        return true;
    }

    @Override
    public int getIntervalBetweenRewardedAds() {
        return 0;
    }

    @Override
    public void openGDPRForm() {
    }

    @Override
    public boolean isUserInEU() {
        return false;
    }
}
