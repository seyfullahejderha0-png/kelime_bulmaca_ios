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

	public WordConnectGame(Network network, Map<String, WordMeaningProvider> providerMap) {
		ConnectionManager.network = network;
		LanguageManager.wordMeaningProviderMap = providerMap;
	}

	public void notificationReceived(int coins, String title, String text) {
		int current = HintManager.getRemainingCoins();
		int newAmount = current + coins;
		HintManager.setCoinCount(newAmount);

		if (currentScreen != null)
			currentScreen.notificationReceived(newAmount, title, text);
	}

	@Override
	public void create() {
		System.out.println("[WC-DIAG] STAGE-9: WordConnectGame.create() called (GL context ready)");
		try {
			System.out.println("[WC-DIAG] STAGE-10: Creating SplashScreen");
			setScreen(new SplashScreen(this));
			System.out.println("[WC-DIAG] STAGE-10: SplashScreen set OK");
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in create(): " + t);
			t.printStackTrace(System.err);
			// throw new RuntimeException("[WC-DIAG] Game create failed", t); // DONT KILL
			// GL THREAD
		}
	}

	@Override
	public void setScreen(Screen screen) {
		if (currentScreen != null)
			currentScreen.dispose();

		currentScreen = (BaseScreen) screen;
		super.setScreen(screen);
	}

	@Override
	public void render() {
		com.badlogic.gdx.Gdx.gl.glClearColor(1f, 0f, 0f, 1f); // RED DIAGNOSTIC
		com.badlogic.gdx.Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
		try {
			super.render();
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in render(): " + t);
			// Do NOT throw it further! This prevents the native abort!
			// We leave the screen red to indicate the crash happened inside render()!
		}
	}

	@Override
	public void dispose() {
		try {
			super.dispose();
			if (currentScreen != null)
				currentScreen.dispose();
			resourceManager.clear();
			resourceManager.dispose();
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in dispose(): " + t);
		}
	}

	@Override
	public void resize(int width, int height) {
		try {
			super.resize(width, height);
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in resize(): " + t);
		}
	}

	@Override
	public void pause() {
		try {
			super.pause();
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in pause(): " + t);
		}
	}

	@Override
	public void resume() {
		try {
			super.resume();
		} catch (Throwable t) {
			System.err.println("[WC-DIAG] CRASH in resume(): " + t);
		}
	}

	int yukseklik;

	public int getYukseklik() {
		return yukseklik;
	}

	public void setYukseklik(int yukseklik) {
		this.yukseklik = yukseklik;
	}

}
