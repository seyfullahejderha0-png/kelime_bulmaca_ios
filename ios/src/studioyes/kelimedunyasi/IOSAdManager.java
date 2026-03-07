package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIScreen;
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
    private boolean interstitialEnabled = true;
    private boolean rewardedCoinsEnabled = true;
    private boolean rewardedMovesEnabled = true;
    private boolean rewardedWheelEnabled = true;
    private boolean rewardedNextLevel = true;
    private boolean userInEU = false;

    public void loadBanner() {
        if (bannerView != null)
            return;

        UIViewController rootViewController = UIApplication.getSharedApplication().getKeyWindow()
                .getRootViewController();

        // Banner size - standard banner
        GADAdSize adSize = GADAdSize.Banner();
        bannerView = new GADBannerView(adSize);
        bannerView.setAdUnitID(IOSLauncher.ADMOB_BANNER_ID);
        bannerView.setRootViewController(rootViewController);

        // Position at top center
        CGRect screenBounds = UIScreen.getMainScreen().getBounds();
        double x = (screenBounds.getWidth() - adSize.getSize().getWidth()) / 2.0;
        bannerView.setFrame(new CGRect(x, 0, adSize.getSize().getWidth(), adSize.getSize().getHeight()));

        rootViewController.getView().addSubview(bannerView);
        bannerView.loadRequest(new GADRequest());
    }

    public void loadInterstitial() {
        GADInterstitialAd.load(IOSLauncher.ADMOB_INTERSTITIAL_ID, new GADRequest(), new GADInterstitialAdLoadHandler() {
            @Override
            public void done(GADInterstitialAd ad, NSError error) {
                if (error != null) {
                    Gdx.app.log("IOSAdManager", "Interstitial load failed: " + error.getLocalizedDescription());
                    interstitialLoaded = false;
                    return;
                }
                interstitialAd = ad;
                interstitialLoaded = true;
                Gdx.app.log("IOSAdManager", "Interstitial loaded");

                interstitialAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                    @Override
                    public void didDismissFullScreenContent(GADFullScreenPresentingAd ad) {
                        interstitialLoaded = false;
                        interstitialAd = null;
                        loadInterstitial(); // Pre-load next
                    }
                });
            }
        });
    }

    public void loadRewarded() {
        GADRewardedAd.load(IOSLauncher.ADMOB_REWARDED_ID, new GADRequest(), new GADRewardedAdLoadHandler() {
            @Override
            public void done(GADRewardedAd ad, NSError error) {
                if (error != null) {
                    Gdx.app.log("IOSAdManager", "Rewarded load failed: " + error.getLocalizedDescription());
                    rewardedLoaded = false;
                    return;
                }
                rewardedAd = ad;
                rewardedLoaded = true;
                Gdx.app.log("IOSAdManager", "Rewarded loaded");

                rewardedAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                    @Override
                    public void didDismissFullScreenContent(GADFullScreenPresentingAd ad) {
                        rewardedLoaded = false;
                        rewardedAd = null;
                        loadRewarded(); // Pre-load next
                    }
                });
            }
        });
    }

    @Override
    public boolean isInterstitialAdEnabled() {
        return interstitialEnabled;
    }

    @Override
    public boolean isRewardedAdEnabledToEarnCoins() {
        return rewardedCoinsEnabled;
    }

    @Override
    public boolean isRewardedAdEnabledToEarnMoves() {
        return rewardedMovesEnabled;
    }

    @Override
    public boolean isRewardedAdEnabledToSpinWheel() {
        return rewardedWheelEnabled;
    }

    @Override
    public boolean isRewardedAdEnabledToSonrakiSeviye() {
        return rewardedNextLevel;
    }

    @Override
    public boolean isRewardedAdLoaded() {
        return rewardedLoaded;
    }

    @Override
    public boolean isInterstitialAdLoaded() {
        return interstitialLoaded;
    }

    @Override
    public void showInterstitialAd(final Runnable closedCallback) {
        if (interstitialLoaded && interstitialAd != null) {
            UIViewController rootViewController = UIApplication.getSharedApplication().getKeyWindow()
                    .getRootViewController();
            interstitialAd.presentFromRootViewController(rootViewController);
            if (closedCallback != null)
                closedCallback.run(); // Usually triggered on dismiss, but keep consistent with Android
        } else {
            if (closedCallback != null)
                closedCallback.run();
            loadInterstitial();
        }
    }

    @Override
    public void showRewardedAd(final RewardedVideoCloseCallback finishedCallback) {
        if (rewardedLoaded && rewardedAd != null) {
            UIViewController rootViewController = UIApplication.getSharedApplication().getKeyWindow()
                    .getRootViewController();
            rewardedAd.presentFromRootViewController(rootViewController, new GADUserDidEarnRewardHandler() {
                @Override
                public void done(GADAdReward reward) {
                    if (finishedCallback != null) {
                        Gdx.app.postRunnable(() -> finishedCallback.closed(true));
                    }
                }
            });
        } else {
            if (finishedCallback != null) {
                Gdx.app.postRunnable(() -> finishedCallback.closed(false));
            }
            loadRewarded();
        }
    }

    @Override
    public int getIntervalBetweenRewardedAds() {
        return 30;
    }

    @Override
    public void openGDPRForm() {
        Gdx.app.log("IOSAdManager", "openGDPRForm() — UMP integration recommended for production");
    }

    @Override
    public boolean isUserInEU() {
        return userInEU;
    }

    public void disableInterstitial() {
        interstitialEnabled = false;
    }
}
