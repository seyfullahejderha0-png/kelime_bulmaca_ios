package studioyes.kelimedunyasi;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.Map;


import studioyes.kelimedunyasi.managers.AdManager;
import studioyes.kelimedunyasi.managers.ConnectionManager;
import studioyes.kelimedunyasi.managers.HintManager;
import studioyes.kelimedunyasi.managers.LanguageManager;
import studioyes.kelimedunyasi.managers.ResourceManager;
import studioyes.kelimedunyasi.net.Network;
import studioyes.kelimedunyasi.net.WordMeaningProvider;

import studioyes.kelimedunyasi.screens.BaseScreen;
import studioyes.kelimedunyasi.screens.SplashScreen;
import studioyes.kelimedunyasi.ui.calendar.DateUtil;
import studioyes.kelimedunyasi.ui.dialogs.iap.ShoppingProcessor;
import studioyes.kelimedunyasi.util.AppExit;
import studioyes.kelimedunyasi.util.RateUsLauncher;
import studioyes.kelimedunyasi.util.SupportRequest;
import studioyes.kelimedunyasi.ads.BannerController;


public class WordConnectGame extends Game {

    public BannerController bannerController;
	public ShoppingProcessor shoppingProcessor;
	private BaseScreen currentScreen;
	public ResourceManager resourceManager = new ResourceManager();
	public DateUtil dateUtil;
	public String version;
	public AdManager adManager;
	public AppExit appExit;
	public RateUsLauncher rateUsLauncher;
	public SupportRequest supportRequest;



	public WordConnectGame(Network network, Map<String, WordMeaningProvider> providerMap){
		ConnectionManager.network = network;
		LanguageManager.wordMeaningProviderMap = providerMap;
	}




	public void notificationReceived(int coins, String title, String text){
		int current = HintManager.getRemainingCoins();
		int newAmount = current + coins;
		HintManager.setCoinCount(newAmount);

		if(currentScreen != null) currentScreen.notificationReceived(newAmount, title, text);
	}




	@Override
	public void create() {
		setScreen(new SplashScreen(this));
	}




	@Override
	public void setScreen(Screen screen){
		if(currentScreen != null)
			currentScreen.dispose();

		currentScreen = (BaseScreen)screen;
		super.setScreen(screen);
	}


	@Override
	public void render() {
		super.render();
	}


	@Override
	public void dispose() {
		super.dispose();
		currentScreen.dispose();
		resourceManager.clear();
		resourceManager.dispose();
	}

	int yukseklik;

	public int getYukseklik() {
		return yukseklik;
	}

	public void setYukseklik(int yukseklik) {
		this.yukseklik = yukseklik;
	}














}
