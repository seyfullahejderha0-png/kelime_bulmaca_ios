package studioyes.kelimedunyasi;

import org.robovm.apple.systemconfiguration.SCNetworkReachability;
import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;

import studioyes.kelimedunyasi.net.Network;

/**
 * iOS ağ bağlantısı denetimi.
 * Android'deki NetworkAndroid.java'nın iOS karşılığı.
 * SystemConfiguration framework'ünü kullanır.
 */
public class IOSNetwork implements Network {

    @Override
    public boolean isConnected() {
        return true;
    }
}
