package studioyes.kelimedunyasi.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;


import com.badlogic.gdx.backends.android.AndroidApplication;


import java.util.ArrayList;
import java.util.List;

import studioyes.kelimedunyasi.R;
import studioyes.kelimedunyasi.managers.AdManager;
import studioyes.kelimedunyasi.model.GameData;
import studioyes.kelimedunyasi.util.RewardedVideoCloseCallback;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class AdActivity extends AndroidApplication implements AdManager {

    private boolean isRewardedVideoEnabledToEarnCoins;
    private boolean isRewardedVideoEnabledToEarnMoves;
    private boolean isRewardedVideoEnabledToSpinWheel;
    protected boolean isInterstitialEnabled;

    private InterstitialAd m_interstitialAd;
    private RewardedAd m_rewardedAd;
    private RewardedVideoCloseCallback rewardedAdFinishedCallback;
    private Runnable interstitialClosedCallback;
    private boolean rewardEarned;
    private ConsentInformation consentInformation;
    private boolean inEUCountry;
    public String keyRemoveAdsPurchased = "keyRemoveAdsPurchased";
    private boolean interstitialLoaded;
    private boolean rewardedLoaded;
    SharedHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedHelper=new SharedHelper(getApplicationContext());
        isRewardedVideoEnabledToEarnCoins = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_EARN_COINS);
        isRewardedVideoEnabledToEarnMoves = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_EARN_MOVES);
        isRewardedVideoEnabledToSpinWheel = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_SPIN_LUCKY_WHEEL);
        isRewardedVideoEnabledToSonrakiSeviye = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_SonrakiSeviye);


        boolean purchasedIAP = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(keyRemoveAdsPurchased, false);
        if(purchasedIAP)
        {
            sharedHelper.setSatinAlindi(true);
            isInterstitialEnabled = false;
        }
        else isInterstitialEnabled = getResources().getBoolean(R.bool.ADMOB_IS_INTERSTITIAL_AD_ENABLED);

        if(isInterstitialEnabled || isRewardedVideoEnabledToEarnCoins ||
                isRewardedVideoEnabledToEarnMoves || isRewardedVideoEnabledToSpinWheel||isRewardedVideoEnabledToSonrakiSeviye)
        {
            checkGDPR();
        }
    }





    private void checkGDPR(){

        boolean testingGDPR = getResources().getBoolean(R.bool.ADMOB_IS_TESTING_GDPR);

        ConsentRequestParameters params;

        if(testingGDPR){

            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            params = new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build();
        }else{
            params = new ConsentRequestParameters.Builder().build();
        }



        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        if(testingGDPR) {
            consentInformation.reset();
            inEUCountry = false;
        }
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess()
                    {
                        inEUCountry = consentInformation.isConsentFormAvailable();

                        if (consentInformation.isConsentFormAvailable() && consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            loadForm();
                        }else{
                            setupAds();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        setupAds();
                        //Log.d("gdpr", "onConsentInfoUpdateFailure, code:"+formError.getErrorCode()+", " + formError.getMessage());
                        // Handle the error.
                    }
                });

    }




    public void loadForm(){
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        consentForm.show(AdActivity.this, new ConsentForm.OnConsentFormDismissedListener() {
                                    @Override
                                    public void onConsentFormDismissed(FormError formError) {
                                        setupAds();
                                    }
                                });
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        setupAds();
                        //Log.d("gdpr", "onConsentFormLoadFailure: "+formError.getErrorCode()+", " + formError.getMessage());
                    }
                }
        );
    }

    private void enableTestAds(){
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDevices).build();
        MobileAds.setRequestConfiguration(configuration);
    }

    private void setupAds(){

        if(getResources().getBoolean(R.bool.ADMOB_IS_TESTING_ADS)) enableTestAds();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //Log.d("rewarded", "onInitializationComplete");
                if(isRewardedVideoEnabledToEarnCoins
                        || isRewardedVideoEnabledToEarnMoves
                        || isRewardedVideoEnabledToSpinWheel
                        || isRewardedVideoEnabledToSonrakiSeviye) initRewardedAds();
                if(isInterstitialEnabled) loadInterstitialAds();
                MobileAds.setAppMuted(GameData.isGameMuted());
            }
        });


    }


    private void initRewardedAds(){
        m_rewardedAd = null;
        RewardedAd.load(this, getString(R.string.ADMOB_REWARDED_AD_UNIT_ID), new AdRequest.Builder().build(), rewardedAdLoadCallback);
    }


    private void loadInterstitialAds(){
        //Log.d("interstitial_ad", isInterstitialEnabled+"");
        m_interstitialAd = null;
        if(isInterstitialAdEnabled()) InterstitialAd.load(this, getString(R.string.ADMOB_INTERSTITIAL_AD_UNIT_ID), new AdRequest.Builder().build(), interstitialAdLoadCallback);

    }

    private InterstitialAdLoadCallback interstitialAdLoadCallback = new InterstitialAdLoadCallback(){

        @Override
        public void onAdLoaded(InterstitialAd interstitialAd) {
            //Log.d("interstitial_ad", "Interstitial ad loaded");
            m_interstitialAd = interstitialAd;
            interstitialAd.setFullScreenContentCallback(interstitialCallback);
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            //Log.d("interstitial_ad", "Interstitial ad failed to load: " + loadAdError.toString());
            m_interstitialAd = null;
            tryToLoadInterstitialAgain();
        }

    };



    private FullScreenContentCallback interstitialCallback = new FullScreenContentCallback(){

        @Override
        public void onAdDismissedFullScreenContent() {
            loadInterstitialAds();
            if(interstitialClosedCallback != null) interstitialClosedCallback.run();
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            tryToLoadInterstitialAgain();
        }

    };


    private RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback(){

        @Override
        public void onAdLoaded(RewardedAd rewardedAd) {
            //Log.d("rewarded_ad", "Rewarded ad loaded");
            m_rewardedAd = rewardedAd;
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            //Log.d("rewarded_ad", "Rewarded ad failed to load: " + loadAdError.toString());
            m_rewardedAd = null;
            tryToLoadRewardedAgain();
        }
    };

    private void tryToLoadRewardedAgain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRewardedAds();
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    private void tryToLoadInterstitialAgain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadInterstitialAds();
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }


    @Override
    public boolean isInterstitialAdEnabled() {
        return isInterstitialEnabled;
    }


    @Override
    public boolean isRewardedAdEnabledToEarnCoins() {
        return isRewardedVideoEnabledToEarnCoins;
    }


    @Override
    public boolean isRewardedAdEnabledToEarnMoves(){
        return isRewardedVideoEnabledToEarnMoves;
    }


    @Override
    public boolean isRewardedAdEnabledToSpinWheel() {
        return isRewardedVideoEnabledToSpinWheel;
    }


    @Override
    public boolean isRewardedAdLoaded() {
        return m_rewardedAd != null;
    }


    @Override
    public boolean isInterstitialAdLoaded() {
        return m_interstitialAd != null;
    }

    @Override
    public void showInterstitialAd(final Runnable closedCallback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialClosedCallback = closedCallback;
                if(isInterstitialAdLoaded()) m_interstitialAd.show(AdActivity.this);
            }
        });

    }





    @Override
    public void showRewardedAd(final RewardedVideoCloseCallback finishedCallback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedAdFinishedCallback = finishedCallback;
                rewardEarned = false;
                m_rewardedAd.setFullScreenContentCallback(rewardedAdCallback);
                if(isRewardedAdLoaded()) {
                    m_rewardedAd.show(AdActivity.this, onUserEarnedRewardListener);
                }
            }
        });

    }



    private OnUserEarnedRewardListener onUserEarnedRewardListener = new OnUserEarnedRewardListener() {
        @Override
        public void onUserEarnedReward(RewardItem rewardItem) {
            rewardEarned = true;
        }
    };


    private FullScreenContentCallback rewardedAdCallback = new FullScreenContentCallback(){

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            tryToLoadRewardedAgain();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            initRewardedAds();
            rewardedAdFinishedCallback.closed(rewardEarned);
        }

    };



    @Override
    public int getIntervalBetweenRewardedAds(){
        return getResources().getInteger(R.integer.ADMOB_INTERVAL_BETWEEN_REWARDED_ADS_IN_SECONDS);
    }



    @Override
    public void openGDPRForm(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadForm();
            }
        });
    }


    @Override
    public boolean isUserInEU(){
        return inEUCountry;
    }
    private boolean isRewardedVideoEnabledToSonrakiSeviye;
    @Override
    public boolean isRewardedAdEnabledToSonrakiSeviye() {
        return isRewardedVideoEnabledToSonrakiSeviye;
    }

}
