package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import studioyes.kelimedunyasi.model.Constants;

public class Ayarlar {
    String tamamlananLevelOdulAldi="TamamlananLevelOdulAldi";
    String odulAldi="OdulAldi";
    public boolean getOdulAldi(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getBoolean(odulAldi, true);
    }
    public void setOdulAldi(boolean count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(odulAldi, count);
        preferences.flush();
    }
    public int getTamamlananLevelOdulAldi(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(tamamlananLevelOdulAldi, 0);
    }
    public void setTamamlananLevelOdulAldi(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(tamamlananLevelOdulAldi, count);
        preferences.flush();
    }

    public int getSeviyePuan(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(tamamlananLevelOdulAldi, 0);
    }
    public void setSeviyePuan(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(tamamlananLevelOdulAldi, count);
        preferences.flush();
    }

    public float getTopPanelHeigh(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getFloat("TopPanelYukseklik", 200);
    }
    public void setTopPanelHeigh(float count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putFloat("TopPanelYukseklik", count);
        preferences.flush();
    }

    public int getSutunSayisi(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger("SutunSayisi", 0);
    }
    public void setSutunSayisi(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger("SutunSayisi", count);
        preferences.flush();
    }

    //todo level sonu puanı ikiye katla
    //todo spin tomorrow diğer dillerde ekle


}
