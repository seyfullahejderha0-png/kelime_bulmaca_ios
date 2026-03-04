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
        try {
            SCNetworkReachability reachability = new SCNetworkReachability("8.8.8.8");
            if (reachability == null) return false;
            SCNetworkReachabilityFlags flags = reachability.getFlags();
            if (flags == null) return false;
            boolean reachable = flags.contains(SCNetworkReachabilityFlags.Reachable);
            boolean connectionRequired = flags.contains(SCNetworkReachabilityFlags.ConnectionRequired);
            return reachable && !connectionRequired;
        } catch (Exception e) {
            return false;
        }
    }
}
