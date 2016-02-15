package net.polybugger.apollot.db;

import android.content.Context;

import net.polybugger.apollot.R;

public enum DaysBits {

    M  (1 << 0),
    T  (1 << 1),
    W  (1 << 2),
    Th (1 << 3),
    F  (1 << 4),
    S  (1 << 5),
    Su (1 << 6),
    M_F (M.mValue + T.mValue + W.mValue + Th.mValue + F.mValue),
    MWF (M.mValue + W.mValue + F.mValue),
    TTh (T.mValue + Th.mValue);

    private final int mValue;

    private DaysBits(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static String intToString(Context context, int days) {
        if(days == M_F.mValue)
            return context.getString(R.string.days_mf);
        if(days == MWF.mValue)
            return context.getString(R.string.days_mwf);
        if(days == TTh.mValue)
            return context.getString(R.string.days_tth);
        if(days == M.mValue)
            return context.getString(R.string.days_mon);
        if(days == T.mValue)
            return context.getString(R.string.days_tue);
        if(days == W.mValue)
            return context.getString(R.string.days_wed);
        if(days == Th.mValue)
            return context.getString(R.string.days_thu);
        if(days == F.mValue)
            return context.getString(R.string.days_fri);
        if(days == S.mValue)
            return context.getString(R.string.days_sat);
        if(days == Su.mValue)
            return context.getString(R.string.days_sun);

        StringBuilder daysStr = new StringBuilder();
        if((days & M.mValue) == M.mValue)
            daysStr.append(context.getString(R.string.days_m));
        if((days & T.mValue) == T.mValue)
            daysStr.append(context.getString(R.string.days_t));
        if((days & W.mValue) == W.mValue)
            daysStr.append(context.getString(R.string.days_w));
        if((days & Th.mValue) == Th.mValue)
            daysStr.append(context.getString(R.string.days_th));
        if((days & F.mValue) == F.mValue)
            daysStr.append(context.getString(R.string.days_f));
        if((days & S.mValue) == S.mValue)
            daysStr.append(context.getString(R.string.days_s));
        if((days & Su.mValue) == Su.mValue)
            daysStr.append(context.getString(R.string.days_su));

        return daysStr.toString();
    }
}
