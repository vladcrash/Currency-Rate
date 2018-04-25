package ru.tinkoff.school.currencyrate.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

import ru.tinkoff.school.currencyrate.database.DateTypeConverter;


@Entity
public class Currency {
    @PrimaryKey(autoGenerate = true)
    private long mId;
    private String mTo;
    private double rate;
    private double mValue;

    @ColumnInfo(name = "is_favourite")
    private boolean isFavourite;

    @TypeConverters(DateTypeConverter.class)
    private Date recent;

    @Ignore
    public Currency() {
    }

    public Currency(String to) {
        this(to, 0.0);
    }

    @Ignore
    public Currency(String to, double rate) {
        this.mTo = to;
        this.rate = rate;
        recent = new Date();
    }

    @Ignore
    public Currency(double value, String name) {
        this.mValue = value;
        this.mTo = name;
        recent = new Date();
    }


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Date getRecent() {
        return recent;
    }

    public void setRecent(Date recent) {
        this.recent = recent;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        this.mTo = to;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Currency response = (Currency) obj;
        return new EqualsBuilder()
                .append(mTo, response.mTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mTo)
                .toHashCode();
    }

}
