package studioyes.kelimedunyasi;

import java.util.Calendar;

import studioyes.kelimedunyasi.ui.calendar.Date;
import studioyes.kelimedunyasi.ui.calendar.DateUtil;

/**
 * iOS DateUtil implementasyonu.
 * Android'deki DateUtilImpl ile birebir aynı mantığı taşır.
 * GameDate sınıfını yeniden kullanır (java.util.Calendar tabanlı).
 *
 * NOT: GameDate Android paketinde olduğundan, bu pakette
 * IOSGameDate adıyla kopyalanmıştır (aşağıda iç sınıf olarak).
 * Alternatif: GameDate core modülüne taşınabilir.
 */
public class IOSDateUtil implements DateUtil {

    @Override
    public Date newDate() {
        return new IOSGameDate();
    }

    @Override
    public Date newDate(long millis) {
        return new IOSGameDate(millis);
    }

    @Override
    public Date newDate(int year, int month) {
        IOSGameDate d = new IOSGameDate();
        d.setYear(year);
        d.setMonth(month);
        return d;
    }

    @Override
    public Date newDate(int year, int month, int date) {
        IOSGameDate d = new IOSGameDate();
        d.setYear(year);
        d.setMonth(month);
        d.setDate(date);
        return d;
    }

    // ------------------------------------------------------------------
    // IOSGameDate — GameDate'in iOS modülündeki kopyası
    // (java.util.Calendar tüm platformlarda çalışır — Android özel importu yok)
    // ------------------------------------------------------------------
    public static class IOSGameDate implements Date {

        private final Calendar cal;

        public IOSGameDate() {
            cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
        }

        public IOSGameDate(long millis) {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);
            cal.setFirstDayOfWeek(Calendar.MONDAY);
        }

        @Override public void setDate(int date)  { cal.set(Calendar.DAY_OF_MONTH, date); }
        @Override public void setMonth(int month) { cal.set(Calendar.MONTH, month); }
        @Override public void setYear(int year)   { cal.set(Calendar.YEAR, year); }
        @Override public long getTime()           { return cal.getTimeInMillis(); }
        @Override public int  getDay()            { return cal.get(Calendar.DAY_OF_WEEK); }
        @Override public int  getDate()           { return cal.get(Calendar.DAY_OF_MONTH); }
        @Override public int  getMonth()          { return cal.get(Calendar.MONTH); }
        @Override public int  getYear()           { return cal.get(Calendar.YEAR); }
        @Override public int  getNumDaysInMonth() { return cal.getActualMaximum(Calendar.DAY_OF_MONTH); }
        @Override public boolean before(Date other){ return cal.before(other); }
        @Override public boolean after(Date other) { return cal.after(other); }
        @Override public Object getDateProvider() { return cal; }
        @Override public String toString()        { return cal.getTime().toString(); }
    }
}
