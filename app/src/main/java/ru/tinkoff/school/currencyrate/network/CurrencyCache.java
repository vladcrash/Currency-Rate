package ru.tinkoff.school.currencyrate.network;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class CurrencyCache {
    public static final int FIVE_MINUTES = 5 * 60 * 1000;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CURRENCY_CACHE_FILE = "currency_cache.txt";
    private static final String TEMP_FILE = "temp_file.txt";
    private Context mContext;
    private File mCacheFile;
    private Date mDate;
    private double mRate;
    private String mUpperCurrency;
    private String mLowerCurrency;

    public CurrencyCache(Context context, String upperCurrency, String lowerCurrency) {
        mCacheFile = new File(context.getCacheDir(), CURRENCY_CACHE_FILE);
        mContext = context;
        mUpperCurrency = upperCurrency;
        mLowerCurrency = lowerCurrency;
    }

    public double getRate() {
        return mRate;
    }

    public Date getDate() {
        return mDate;
    }

    public Boolean isShouldGoToTheNetwork() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(mCacheFile));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] line = currentLine.split(" ");
                if (line[0].equals(mUpperCurrency + mLowerCurrency)) {
                    mRate = Double.parseDouble(line[1]);
                    mDate = new Date(Long.parseLong(line[2]));
                    return (new Date()).getTime() - mDate.getTime() > FIVE_MINUTES;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeToCache(double rate, Date date, Boolean abrakadabra) {
        if (abrakadabra == null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(mCacheFile, true));
                String original = mUpperCurrency + mLowerCurrency + " " + rate + " " + date.getTime() + LINE_SEPARATOR;
                String reverse = mLowerCurrency + mUpperCurrency + " " + 1.0 / rate + " " + date.getTime() + LINE_SEPARATOR;
                writer.write(original);
                writer.write(reverse);

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            rewriteCache(rate);
        }

    }

    private void rewriteCache(double rate) {
        File tempFile = new File(mContext.getCacheDir(), TEMP_FILE);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(mCacheFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] line = currentLine.split(" ");
                if (line[0].equals(mUpperCurrency + mLowerCurrency) || line[0].equals(mLowerCurrency + mUpperCurrency)) {
                    continue;
                }
                writer.write(currentLine + LINE_SEPARATOR);
            }

            String original = mUpperCurrency + mLowerCurrency + " " + rate + " " + (new Date()).getTime() + LINE_SEPARATOR;
            String reverse = mLowerCurrency + mUpperCurrency + " " + 1.0 / rate + " " + (new Date()).getTime() + LINE_SEPARATOR;
            writer.write(original);
            writer.write(reverse);

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCacheFile.delete();
        tempFile.renameTo(mCacheFile);
    }
}
