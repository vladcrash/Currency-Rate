package ru.tinkoff.school.currencyrate.models;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Filter {
    private long mBeginDate;
    private long mEndDate;
    private long mBeginDateOther;
    private long mEndDateOther;
    private Date mDatePickerStartDate;
    private Date mDatePickerEndDate;
    private List<Currency> mCurrencies;

    public Filter() {
        mCurrencies = new ArrayList<>();
    }

    public long getBeginDate() {
        return mBeginDate;
    }

    public void setBeginDate(long beginDate) {
        if (beginDate != 0) {
            mBeginDate = getBeginTimeOfTheDay(new Date(beginDate));
        } else {
            mBeginDate = 0;
        }
    }

    public long getEndDate() {
        return mEndDate;
    }

    public void setEndDate(long endDate) {
        if (endDate != 0) {
            mEndDate = getEndTimeOfTheDay(new Date(endDate));
        } else {
            mEndDate = 0;
        }
    }

    public long getBeginDateOther() {
        return mBeginDateOther;
    }

    public void setBeginDateOther(long beginDateOther) {
        mBeginDateOther = getBeginTimeOfTheDay(new Date(beginDateOther));
    }

    public long getEndDateOther() {
        return mEndDateOther;
    }

    public void setEndDateOther(long endDateOther) {
        mEndDateOther = getEndTimeOfTheDay(new Date(endDateOther));
    }

    public Date getDatePickerStartDate() {
        return mDatePickerStartDate;
    }

    public void setDatePickerStartDate(Date datePickerStartDate) {
        mDatePickerStartDate = datePickerStartDate;
    }

    public Date getDatePickerEndDate() {
        return mDatePickerEndDate;
    }

    public void setDatePickerEndDate(Date datePickerEndDate) {
        mDatePickerEndDate = datePickerEndDate;
    }

    public List<Currency> getCurrencies() {
        return mCurrencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        mCurrencies = currencies;
    }

    private long getBeginTimeOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndTimeOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}


