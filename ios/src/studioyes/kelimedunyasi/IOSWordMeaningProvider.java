package studioyes.kelimedunyasi;

import studioyes.kelimedunyasi.net.WordMeaningProvider;
import studioyes.kelimedunyasi.net.WordMeaningRequest;

/**
 * iOS kelime anlamı sağlayıcısı.
 * WordMeaningProviderAndroid'ın iOS karşılığı.
 *
 * IOSWordMeaningRequest_en kullanır (Android kontrolleri çıkarılmış versiyon).
 */
public class IOSWordMeaningProvider implements WordMeaningProvider {

    @Override
    public WordMeaningRequest get(String langCode) {
        if ("en".equals(langCode)) return new IOSWordMeaningRequest_en();
        return null;
    }
}
