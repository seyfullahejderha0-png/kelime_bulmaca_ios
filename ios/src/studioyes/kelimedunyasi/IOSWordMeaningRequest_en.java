package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;

import studioyes.kelimedunyasi.managers.LanguageManager;
import studioyes.kelimedunyasi.net.WordMeaningRequest;
import studioyes.kelimedunyasi.ui.dialogs.DictionaryDialog;

/**
 * iOS kelime anlamı isteği — WordMeaningRequest_en'in iOS versiyonu.
 *
 * Fark: Android SDK kontrolü (Application.ApplicationType.Android) kaldırıldı.
 * iOS'ta ağ istekleri her zaman ayrı Thread'de çalıştırılır.
 *
 * jsoup kütüphanesi RoboVM ile kullanılabilir (JVM tabanlı, native bağımlılık yok).
 * build.gradle'a jsoup bağımlılığını eklemek gerekir:
 *   implementation 'org.jsoup:jsoup:1.21.2'
 */
public class IOSWordMeaningRequest_en implements WordMeaningRequest {

    private DictionaryDialog.DictionaryCallback callback;
    private String word;

    @Override
    public void request(String word, DictionaryDialog.DictionaryCallback callback) {
        this.word = word;
        this.callback = callback;

        com.badlogic.gdx.utils.StringBuilder sb = new com.badlogic.gdx.utils.StringBuilder();
        sb.append("s=" + word);
        sb.append("&o8=1");
        sb.append("&o1=1");
        sb.append("&h=000000000000000");
        sb.append("&sub=Search WordNet");
        sb.append("&o2=");
        sb.append("&o0=");
        sb.append("&o7=");
        sb.append("&o5=");
        sb.append("&o9=");
        sb.append("&o6=");
        sb.append("&o3=");
        sb.append("&o4=");
        sb.append("&c=-1");

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl("http://wordnetweb.princeton.edu/perl/webwn");
        request.setContent(sb.toString());
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Host", "wordnetweb.princeton.edu");
        request.setHeader("Referer", "http://wordnetweb.princeton.edu/perl/webwn");
        request.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15");

        // iOS'ta her zaman Thread ile çalıştır (Android koşulu kaldırıldı)
        RequestSender sender = new RequestSender();
        sender.request = request;
        new Thread(sender).start();
    }

    class RequestSender implements Runnable {

        Net.HttpRequest request;

        @Override
        public void run() {
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                    String response = parseResponse(httpResponse.getResultAsString());
                    if (response.isEmpty()) response = LanguageManager.get("no_response");
                    final String toSend = response;
                    Gdx.app.postRunnable(() ->
                        IOSWordMeaningRequest_en.this.callback.onMeaning(IOSWordMeaningRequest_en.this.word, toSend)
                    );
                }

                @Override
                public void failed(Throwable t) {
                    final String msg = t != null ? t.getMessage() : "network error";
                    Gdx.app.postRunnable(() ->
                        IOSWordMeaningRequest_en.this.callback.onMeaning(IOSWordMeaningRequest_en.this.word, msg)
                    );
                }

                @Override
                public void cancelled() { /* no-op */ }
            });
        }

        private String parseResponse(String html) {
            Document doc = Jsoup.parse(html);
            Elements h3  = doc.select("h3");
            Elements uls = doc.select("ul");
            com.badlogic.gdx.utils.StringBuilder sb = new com.badlogic.gdx.utils.StringBuilder();

            for (int i = 0; i < h3.size(); i++) {
                sb.append(h3.get(i).text().toUpperCase(Locale.ENGLISH));
                sb.append("\n");
                Element ul = uls.get(i);
                Elements li = ul.select("li");
                for (int j = 0; j < li.size(); j++) {
                    sb.append(li.get(j).text().replaceFirst("S:", "*"));
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }
}
