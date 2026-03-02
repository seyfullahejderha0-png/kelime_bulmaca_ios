package studioyes.kelimedunyasi.activity;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {
    Context context;
    String onaylandi="Onaylandi";
    String satinAlindi="SatinAlindi";
    SharedPreferences sharedPref;

    public SharedHelper(Context context) {
        this.context = context;
        sharedPref= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isOnaylandi()
    {
        return sharedPref.getBoolean(onaylandi,false);
    }

    public void setOnaylandi(boolean deger)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(onaylandi,deger); //boolean değer ekleniyor
        editor.apply(); //Kayıt
    }

    public boolean isSatinAlindi()
    {
        return sharedPref.getBoolean(satinAlindi,false);
    }

    public void setSatinAlindi(boolean deger)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(satinAlindi,deger); //boolean değer ekleniyor
        editor.apply(); //Kayıt
    }




}
