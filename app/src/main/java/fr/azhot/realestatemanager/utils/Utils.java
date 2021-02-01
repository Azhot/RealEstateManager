package fr.azhot.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    public static final Float EUR_DOLLAR_EXCHANGE_RATE = 0.812f;

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars the dollar amount to be converted
     * @return the euro rounded amount converted from the dollars argument
     */
    public static int convertDollarToEuro(int dollars) {
        return Math.round(dollars * EUR_DOLLAR_EXCHANGE_RATE);
    }

    public static int convertEuroToDollar(int euros) {
        return Math.round(euros / EUR_DOLLAR_EXCHANGE_RATE);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return the current Date formatted as a String dd/MM/yyyy
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param context the context from which the method is called
     * @return a Boolean stating the status of the internet availability
     */
    public static Boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo ni = null;
            if (cm != null) ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnectedOrConnecting();
        } else {
            NetworkCapabilities nc = null;
            if (cm != null) nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
    }
}
