package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.*;

import java.util.ArrayList;
import java.util.List;

import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingCallback;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingItem;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingProcessor;

public class IOSShoppingProcessor implements ShoppingProcessor, PurchaseObserver {

    private static final String PREF_NAME = "wordconnect_ios_prefs";
    private static final String KEY_REMOVE_ADS = "remove_ads_purchased";

    private final List<String> productIds = new ArrayList<>();
    private ShoppingCallback shoppingCallback;
    private boolean removeAdsPurchased;
    private String removeAdsPrice = "";
    private boolean prefsLoaded = false;

    private PurchaseManager purchaseManager;

    public IOSShoppingProcessor() {
    }

    private void loadPrefsIfNeeded() {
        if (!prefsLoaded && Gdx.app != null) {
            Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
            removeAdsPurchased = prefs.getBoolean(KEY_REMOVE_ADS, false);
            prefsLoaded = true;
        }
    }

    public void addProduct(String productId) {
        if (!productIds.contains(productId)) {
            productIds.add(productId);
        }
    }

    public void initialize() {
        PurchaseManagerConfig config = new PurchaseManagerConfig();
        for (String id : productIds) {
            config.addOffer(new Offer().setType(OfferType.CONSUMABLE).setIdentifier(id));
        }
        // Remove Ads is non-consumable
        config.getOffer(IOSLauncher.IAP_REMOVE_ADS).setType(OfferType.ENTITLEMENT);

        purchaseManager = Gdx.pay;
        if (purchaseManager != null) {
            purchaseManager.install(this, config, true);
        }
    }

    @Override
    public boolean isIAPEnabled() {
        return purchaseManager != null && purchaseManager.installed();
    }

    @Override
    public void queryShoppingItems(ShoppingCallback callback) {
        this.shoppingCallback = callback;
        if (purchaseManager != null && purchaseManager.installed()) {
            // Already installed, but we might need to refresh UI
            // gdx-pay usually handles product info during installation or via inventory
        } else {
            initialize();
        }
    }

    @Override
    public void reportItemRetrivalError(int code) {
        if (shoppingCallback != null)
            shoppingCallback.onShoppingItemsError(code);
    }

    @Override
    public void reportTransactionError(int code) {
        if (shoppingCallback != null)
            shoppingCallback.onTransactionError(code);
    }

    @Override
    public void makeAPurchase(final String sku) {
        if (purchaseManager != null && purchaseManager.installed()) {
            purchaseManager.purchase(sku);
        }
    }

    @Override
    public void hasMadeAPurchase(String sku, boolean newPurchase) {
        if (sku.equals(IOSLauncher.IAP_REMOVE_ADS)) {
            removeAdsPurchased = true;
            Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
            prefs.putBoolean(KEY_REMOVE_ADS, true);
            prefs.flush();
        }

        if (newPurchase && shoppingCallback != null) {
            shoppingCallback.onPurchase(sku);
        }
    }

    @Override
    public boolean isRemoveAdsPurchased() {
        loadPrefsIfNeeded();
        return removeAdsPurchased;
    }

    @Override
    public String getRemoveAdsPrice() {
        return removeAdsPrice;
    }

    // PurchaseObserver methods
    @Override
    public void handleInstall() {
        Gdx.app.log("IOSIAPs", "PurchaseManager installed");
        List<ShoppingItem> items = new ArrayList<>();
        for (String id : productIds) {
            Information info = purchaseManager.getInformation(id);
            String price = (info != null && info.getLocalPricing() != null) ? info.getLocalPricing() : "—";
            items.add(new ShoppingItem(id, price, id));
            if (id.equals(IOSLauncher.IAP_REMOVE_ADS))
                removeAdsPrice = price;
        }
        if (shoppingCallback != null) {
            shoppingCallback.onShoppingItemsReady(items);
        }
    }

    @Override
    public void handleInstallError(Throwable e) {
        Gdx.app.error("IOSIAPs", "PurchaseManager install error", e);
        if (shoppingCallback != null)
            shoppingCallback.onShoppingItemsError(1);
    }

    @Override
    public void handleRestore(Transaction[] transactions) {
        for (Transaction t : transactions) {
            if (t.getIdentifier().equals(IOSLauncher.IAP_REMOVE_ADS)) {
                hasMadeAPurchase(t.getIdentifier(), false);
            }
        }
    }

    @Override
    public void handleRestoreError(Throwable e) {
    }

    @Override
    public void handlePurchase(Transaction transaction) {
        hasMadeAPurchase(transaction.getIdentifier(), true);
    }

    @Override
    public void handlePurchaseError(Throwable e) {
        if (shoppingCallback != null)
            shoppingCallback.onTransactionError(1);
    }

    @Override
    public void handlePurchaseCanceled() {
        if (shoppingCallback != null)
            shoppingCallback.onTransactionError(0);
    }
}
