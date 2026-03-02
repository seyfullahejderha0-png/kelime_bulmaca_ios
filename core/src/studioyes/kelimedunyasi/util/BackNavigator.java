package studioyes.kelimedunyasi.util;


import studioyes.kelimedunyasi.screens.BaseScreen;

public interface BackNavigator {

    void notifyNavigationController(BaseScreen screen);
    boolean navigateBack();
}
