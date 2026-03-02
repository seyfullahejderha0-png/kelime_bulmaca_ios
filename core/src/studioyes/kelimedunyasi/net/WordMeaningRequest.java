package studioyes.kelimedunyasi.net;


import studioyes.kelimedunyasi.ui.dialogs.DictionaryDialog;

public interface WordMeaningRequest {
    void request(String word, DictionaryDialog.DictionaryCallback callback);
}
