package studioyes.kelimedunyasi;


import studioyes.kelimedunyasi.net.WordMeaningProvider;
import studioyes.kelimedunyasi.net.WordMeaningRequest;

public class WordMeaningProviderAndroid implements WordMeaningProvider {


    public WordMeaningRequest get(String langCode){
        if(langCode.equals("en")) return new WordMeaningRequest_en();

        return null;
    }

}
