package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.List;

import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingCallback;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingItem;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingProcessor;

/**
 * iOS uygulama içi satın alma işleyicisi.
 * ShoppingProcessor interface'ini implement eder.
 *
 * Apple StoreKit (SKPaymentQueue, SKProductsRequest) kullanır.
 * RoboVM binding için iOS StoreKit binding JAR gereklidir:
 *   https://github.com/MobiVM/robovm-robopods (storekit modülü)
 *
 * Preferences ile "remove_ads_purchased" durumunu kalıcı saklar.
 *
 * Mac'te tam implementasyon yapılacak — bu sınıf şimdilik
 * çalışan bir iskelet (skeleton) sağlamaktadır.
 */
public class IOSShoppingProcessor implements ShoppingProcessor {

    private static final String PREF_NAME          = "wordconnect_ios_prefs";
    private static final String KEY_REMOVE_ADS     = "remove_ads_purchased";

    private final List<String> productIds = new ArrayList<>();
    private ShoppingCallback   shoppingCallback;
    private boolean            removeAdsPurchased;
    private String             removeAdsPrice = "";

    // Simüle edilen ürün fiyatları (gerçekte StoreKit'ten gelecek)
    private static final String DEFAULT_PRICE = "—";

    public IOSShoppingProcessor() {
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        removeAdsPurchased = prefs.getBoolean(KEY_REMOVE_ADS, false);
    }

    /** IOSLauncher tarafından çağrılır — satın alınabilecek tüm ürün ID'lerini ekler */
    public void addProduct(String productId) {
        if (!productIds.contains(productId)) {
            productIds.add(productId);
        }
    }

    // ------------------------------------------------------------------
    // ShoppingProcessor interface
    // ------------------------------------------------------------------

    @Override
    public boolean isIAPEnabled() {
        return true; // iOS'ta IAP her zaman etkin (ayar gerektirmez)
    }

    @Override
    public void queryShoppingItems(ShoppingCallback callback) {
        this.shoppingCallback = callback;
        Gdx.app.log("IOSIAPs", "queryShoppingItems() — StoreKit binding bekleniyor");

        /*
         * Gerçek implementasyon (Mac'te yapılacak):
         *   NSSet<NSString> productIdentifiers = new NSMutableSet<>(productIds);
         *   SKProductsRequest request = new SKProductsRequest(productIdentifiers);
         *   request.setDelegate(this);
         *   request.start();
         *
         * productsRequest:didReceiveResponse: callback'inde:
         *   response.getProducts() → List<SKProduct>
         *   ShoppingItem'lere dönüştür → callback.onShoppingItemsReady(items)
         */

        // Şimdilik stub ürünler döndür
        List<ShoppingItem> stubItems = new ArrayList<>();
        for (String id : productIds) {
            stubItems.add(new ShoppingItem(id, DEFAULT_PRICE, id));
        }
        if (callback != null) {
            callback.onShoppingItemsReady(stubItems);
        }
    }

    @Override
    public void reportItemRetrivalError(int code) {
        if (shoppingCallback != null) shoppingCallback.onShoppingItemsError(code);
    }

    @Override
    public void reportTransactionError(int code) {
        if (shoppingCallback != null) shoppingCallback.onTransactionError(code);
    }

    @Override
    public void makeAPurchase(final String sku) {
        Gdx.app.log("IOSIAPs", "makeAPurchase: " + sku + " — StoreKit binding bekleniyor");
        /*
         * Gerçek implementasyon (Mac'te yapılacak):
         *   SKProduct product = findProduct(sku);
         *   SKPayment payment = SKPayment.getPaymentWithProduct(product);
         *   SKPaymentQueue.getDefaultQueue().addPayment(payment);
         *
         * paymentQueue:updatedTransactions: callback'inde:
         *   SKPaymentTransactionState state → handle purchase/failure/restore
         */
    }

    @Override
    public void hasMadeAPurchase(String sku, boolean newPurchase) {
        Gdx.app.log("IOSIAPs", "hasMadeAPurchase: " + sku + " newPurchase=" + newPurchase);

        if (sku.equals(IOSLauncher.IAP_REMOVE_ADS)) {
            removeAdsPurchased = true;
            removeAdsPrice     = "";
            // Kalıcı kayıt
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
        return removeAdsPurchased;
    }

    @Override
    public String getRemoveAdsPrice() {
        return removeAdsPrice;
    }
}
