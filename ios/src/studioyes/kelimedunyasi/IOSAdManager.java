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
        if (bannerView != null)
            return;
        UIViewController root = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
        bannerView = new GADBannerView(GADAdSize.Banner());
        bannerView.setAdUnitID(IOSLauncher.ADMOB_BANNER_ID);
        bannerView.setRootViewController(root);
        root.getView().addSubview(bannerView);
        bannerView.loadRequest(new GADRequest());
    }

    public void showBanner() {
        if (bannerView != null)
            bannerView.setHidden(false);
    }

    public void hideBanner() {
        if (bannerView != null)
            bannerView.setHidden(true);
    }

    public void loadInterstitial() {
        GADRequest request = new GADRequest();
        GADInterstitialAd.load(IOSLauncher.ADMOB_INTERSTITIAL_ID, request, (ad, error) -> {
            if (error != null) {
                interstitialLoaded = false;
                return;
            }
            interstitialAd = ad;
            interstitialLoaded = true;
            interstitialAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                @Override
                public void didDismissFullScreenContent(GADFullScreenPresentingAd ad) {
                    interstitialLoaded = false;
                    interstitialAd = null;
                    loadInterstitial();
                }
            });
        });
    }

    @Override
    public boolean isInterstitialAdLoaded() {
        return interstitialLoaded;
    }

    @Override
    public void showInterstitialAd(Runnable closedCallback) {
        if (interstitialLoaded && interstitialAd != null) {
            UIViewController root = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
            interstitialAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                @Override
                public void didDismissFullScreenContent(GADFullScreenPresentingAd ad) {
                    interstitialLoaded = false;
                    interstitialAd = null;
                    if (closedCallback != null)
                        closedCallback.run();
                    loadInterstitial();
                }
            });
            interstitialAd.presentFromRootViewController(root);
        } else {
            if (closedCallback != null)
                closedCallback.run();
            loadInterstitial();
        }
    }

    public void loadRewarded() {
        GADRequest request = new GADRequest();
        GADRewardedAd.load(IOSLauncher.ADMOB_REWARDED_ID, request, (ad, error) -> {
            if (error != null) {
                rewardedLoaded = false;
                return;
            }
            rewardedAd = ad;
            rewardedLoaded = true;
            rewardedAd.setFullScreenContentDelegate(new GADFullScreenContentDelegateAdapter() {
                @Override
                public void didDismissFullScreenContent(GADFullScreenPresentingAd ad) {
                    rewardedLoaded = false;
                    rewardedAd = null;
                    loadRewarded();
                }
            });
        });
    }

    @Override
    public boolean isRewardedAdLoaded() {
        return rewardedLoaded;
    }

    @Override
    public void showRewardedAd(RewardedVideoCloseCallback finishedCallback) {
        if (rewardedLoaded && rewardedAd != null) {
            UIViewController root = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
            rewardedAd.present(root, () -> {
                Gdx.app.postRunnable(() -> {
                    if (finishedCallback != null)
                        finishedCallback.closed(true);
                });
            });
        } else {
            loadRewarded();
        }
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
