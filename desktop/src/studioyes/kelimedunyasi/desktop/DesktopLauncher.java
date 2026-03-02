package studioyes.kelimedunyasi.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.HashMap;
import java.util.Map;


import studioyes.kelimedunyasi.WordConnectGame;

import studioyes.kelimedunyasi.net.WordMeaningProvider;
import studioyes.kelimedunyasi.ui.calendar.DateUtil;
import studioyes.kelimedunyasi.util.AppExit;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 366;
		config.height = 650;
		config.y = 100;


		Map<String, WordMeaningProvider> provider = new HashMap<>();
		provider.put("en", new WordMeaningProviderDesktop());

		DateUtil dateUtil = new DateUtilImpl();

		WordConnectGame game = new WordConnectGame(new NetworkDesktop(), provider);

		game.dateUtil = dateUtil;
		game.appExit = new AppExit() {
			@Override
			public void exitApp() {
				Gdx.app.exit();
			}
		};


		new LwjglApplication(game, config);
	}

}
