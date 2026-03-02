package studioyes.kelimedunyasi.activity;
import studioyes.kelimedunyasi.util.RewardedVideoCloseCallback;
import studioyes.kelimedunyasi.managers.AdManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;




import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



import studioyes.kelimedunyasi.DateUtilImpl;
import studioyes.kelimedunyasi.NetworkAndroid;
import studioyes.kelimedunyasi.R;
import studioyes.kelimedunyasi.WordConnectFirebaseMessagingService;
import studioyes.kelimedunyasi.WordConnectGame;
import studioyes.kelimedunyasi.WordMeaningProviderAndroid;
import studioyes.kelimedunyasi.net.WordMeaningProvider;
import studioyes.kelimedunyasi.util.AppExit;
import studioyes.kelimedunyasi.util.RateUsLauncher;
import studioyes.kelimedunyasi.util.SupportRequest;



import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.AdError;
import studioyes.kelimedunyasi.managers.AdManager;
import studioyes.kelimedunyasi.util.RewardedVideoCloseCallback;



import androidx.annotation.NonNull;

public class AndroidLauncher extends IAPActivity implements AdManager, AppExit, RateUsLauncher, SupportRequest {
    private WordConnectGame game;
    private AdView adView;
    private RewardedAd rewardedAd;
    private RewardedVideoCloseCallback rewardedVideoCloseCallback;
    private RewardedVideoCloseCallback finishedCallback;
    private boolean rewardEarned = false;
    private boolean isRewardedLoading = false;
    SharedHelper sharedHelper;
    private static final int UPDATE_REQUEST_CODE = 1001;
    private AppUpdateManager appUpdateManager;

    private InstallStateUpdatedListener updateListener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            if (appUpdateManager != null) {
                appUpdateManager.completeUpdate();
            }
        }
    };
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this);
        //getWindow().getDecorView();


        sharedHelper=new SharedHelper(getApplicationContext());
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = getResources().getBoolean(R.bool.IMMERSIVE_MODE);

        Map<String, WordMeaningProvider> provider = new HashMap<>();
        provider.put("en", new WordMeaningProviderAndroid());



        //put you word meaning provider above

        DateUtilImpl dateUtil = new DateUtilImpl();
        dateUtil.context = this;

        game = new WordConnectGame(new NetworkAndroid(this), provider);
        game.dateUtil = dateUtil;
        game.shoppingProcessor = androidShoppingProcessor;
        game.adManager = this;
        game.appExit = this;
        game.rateUsLauncher = this;
        game.supportRequest = this;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            game.version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initialize(game, config);
        loadRewardedAd();

        LocalBroadcastManager.getInstance(this).registerReceiver(mCoinsMessageReceiver, new IntentFilter(WordConnectFirebaseMessagingService.INTENT_RECEIVED_PUSH_MESSAGE));


        fcm();

        if (isNetworkAvailable())game.setYukseklik(200);
        else game.setYukseklik(0);

        if (sharedHelper.isSatinAlindi())game.setYukseklik(0);

        if (!sharedHelper.isSatinAlindi())
        {
            RelativeLayout layout = new RelativeLayout(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(params);

            View gameView=initializeForView(game, config);

            RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            gameViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            gameViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

            gameView.setLayoutParams(gameViewParams);
            layout.addView(gameView);

            adView = new AdView(this);
            adView.setAdSize(getAdSize());
            adView.setAdUnitId(getString(R.string.ADMOB_BANNER_ID));

            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            adView.loadAd(adRequestBuilder.build());


            adView.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded() {
                    RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                    topParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    layout.addView(adView, topParams);
                    adView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    super.onAdLoaded();
                }
            });

            setContentView(layout);
            checkForUpdate();
        }

    }

    @Override
    protected void onResume() {
        if (adView!=null) adView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (adView!=null)adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCoinsMessageReceiver);
        if (adView != null) adView.destroy();

        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(updateListener);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                checkForUpdate(); // kullanıcı kaçarsa tekrar sor
            }
        }
    }




    private void fcm(){
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            int coinCount = 0;


            if(bundle.containsKey(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) || bundle.containsKey(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT)){

                if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof String)
                    coinCount = Integer.parseInt(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT).toString());
                else if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof Object)
                    coinCount = (int)bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT);
                else if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof Integer)
                    coinCount = ((Integer) bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT)).intValue();


                //Log.d("fcm", "Received coins from intent " + coinCount);

            }

            String text = "";
            String title = "";

            if(bundle.containsKey(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT)){
                Object rawText = bundle.get(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT);
                if(rawText != null) text = rawText.toString();

                Object rawTitle = bundle.get(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TITLE);
                if(rawTitle != null) rawTitle.toString();
            }

            notifyUserAboutIncomingPushMessage(coinCount, title, text);

        }




        FirebaseMessaging.getInstance().subscribeToTopic("coin_topic")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribe successful";
                        if (!task.isSuccessful()) {
                            msg = "subscribe failed";
                        }
                        //Log.d("fcm", msg);

                    }
                });

    }






    @Override
    public void exitApp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        });

    }



    @Override
    public void launch() {
        String param = getPackageName();
        Uri uri = Uri.parse("market://details?id=" + param);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + param)));
        }
    }






    @Override
    public void sendSupportEmail(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request for " + getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, getDeviceInfo());
        startActivity(Intent.createChooser(intent, "Send e-mail..."));
    }





    public String getDeviceInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("Please type your request above");
        sb.append("\n");
        sb.append("Brand: ");
        sb.append(Build.BRAND);
        sb.append("\n");
        sb.append("Model: ");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("SDK: ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append("\n");

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = Resources.getSystem().getConfiguration().locale;
        }

        sb.append("Locale: ");
        sb.append(locale.getLanguage() + "-" + locale.getCountry());
        sb.append("\n");
        sb.append("App version: ");

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            sb.append(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return sb.toString();
    }





    private BroadcastReceiver mCoinsMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int coins = intent.getIntExtra(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT, 0);
            String title = intent.getStringExtra(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TITLE);
            String text = intent.getStringExtra(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT);
            notifyUserAboutIncomingPushMessage(coins, title, text);
        }
    };





    private void notifyUserAboutIncomingPushMessage(int coins, String title, String text){
        game.notificationReceived(coins, title, text);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadRewardedAd() {

        if (isRewardedLoading || rewardedAd != null) return;

        isRewardedLoading = true;

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(
                this,
                getString(R.string.ADMOB_REWARDED_AD_UNIT_ID),
                adRequest,
                new RewardedAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        isRewardedLoading = false;
                        System.out.println("✅ Rewarded Ad LOADED");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAd = null;
                        isRewardedLoading = false;
                        System.out.println("❌ Rewarded Ad FAILED: " + loadAdError.getMessage());
                    }
                }
        );
    }








    @Override
    public void showRewardedAd(RewardedVideoCloseCallback callback) {

        runOnUiThread(() -> {

            if (rewardedAd == null) {
                if (callback != null) {
                    callback.closed(false);
                }
                loadRewardedAd();
                return;
            }

            finishedCallback = callback;
            rewardEarned = false;

            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAd = null;
                    loadRewardedAd();

                    if (finishedCallback != null) {
                        finishedCallback.closed(rewardEarned);
                        finishedCallback = null; // ÇOK ÖNEMLİ
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    rewardedAd = null;
                    loadRewardedAd();

                    if (finishedCallback != null) {
                        finishedCallback.closed(false);
                        finishedCallback = null;
                    }
                }
            });

            rewardedAd.show(AndroidLauncher.this, rewardItem -> {
                rewardEarned = true;
            });
        });
    }

    private void checkForUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);

        appUpdateManager.registerListener(updateListener);

        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {

                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    UPDATE_REQUEST_CODE
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
