package studioyes.kelimedunyasi.desktop;


import studioyes.kelimedunyasi.net.WordMeaningProvider;
import studioyes.kelimedunyasi.net.WordMeaningRequest;

public class WordMeaningProviderDesktop implements WordMeaningProvider {


    public WordMeaningRequest get(String langCode){
        if(langCode.equals("en"))
            return new WordMeaningRequest_en();

        return null;

    }

}
