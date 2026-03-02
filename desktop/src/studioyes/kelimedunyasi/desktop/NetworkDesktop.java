package studioyes.kelimedunyasi.desktop;


import studioyes.kelimedunyasi.net.Network;

public class NetworkDesktop implements Network {


    @Override
    public boolean isConnected() {
        return true;
    }
}
