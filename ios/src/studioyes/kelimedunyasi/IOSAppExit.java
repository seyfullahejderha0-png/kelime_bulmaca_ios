package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;

import studioyes.kelimedunyasi.util.AppExit;

/**
 * iOS uygulama kapatma.
 * AppExit interface'ini implement eder.
 *
 * NOT: Apple, uygulamaları programatik olarak kapatmayı
 * App Store Review Guidelines kapsamında reddedebilir.
 * Bu nedenle bu sınıf yalnızca dispose işlemi yapar
 * ve uygulamayı "arka plana" alır (exit(0) ÇAĞIRMAZ).
 */
public class IOSAppExit implements AppExit {

    @Override
    public void exitApp() {
        Gdx.app.log("IOSAppExit", "exitApp() called — iOS arka plana alıyor");
        // iOS'ta exit(0) Apple kurallarına göre kabul edilmez.
        // Gdx.app.exit() çağrısı RoboVM'de güvenli şekilde durdurur.
        Gdx.app.exit();
    }
}
