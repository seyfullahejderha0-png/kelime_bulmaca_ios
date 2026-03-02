package studioyes.kelimedunyasi.desktop;


import studioyes.kelimedunyasi.ui.calendar.Date;
import studioyes.kelimedunyasi.ui.calendar.DateUtil;

public class DateUtilImpl implements DateUtil {

    @Override
    public Date newDate() {
        return new GameDate();
    }



    @Override
    public Date newDate(long millis) {
        return new GameDate(millis);
    }




    @Override
    public Date newDate(int year, int month) {
        GameDate gameDate = new GameDate();
        gameDate.setYear(year);
        gameDate.setMonth(month);
        return gameDate;
    }





    @Override
    public Date newDate(int year, int month, int date) {
        GameDate gameDate = new GameDate();
        gameDate.setYear(year);
        gameDate.setMonth(month);
        gameDate.setDate(date);
        return gameDate;
    }





}
