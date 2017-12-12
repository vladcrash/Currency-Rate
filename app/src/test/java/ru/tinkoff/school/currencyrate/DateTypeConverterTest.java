package ru.tinkoff.school.currencyrate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import ru.tinkoff.school.currencyrate.database.DateTypeConverter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


@RunWith(MockitoJUnitRunner.class)
public class DateTypeConverterTest {

    @Test
    public void shouldReturnCorrectDateValue() {
        long almostRandomValue = 1512950400000L;
        Date date = DateTypeConverter.toDate(almostRandomValue);
        assertEquals(almostRandomValue, date.getTime());
    }

    @Test
    public void shouldReturnCorrectLongValue() {
        Date date = new Date(1512950400000L);
        long almostRandomValue = DateTypeConverter.toDate(date);
        assertEquals(date.getTime(), almostRandomValue);
    }

    @Test
    public void longToDateShouldBeNullWhenPassNull() {
        assertNull((DateTypeConverter.toDate((Long) null)));
    }

    @Test
    public void dateToLongShouldBeNullWhenPassNull() {
        assertNull((DateTypeConverter.toDate((Date) null)));
    }

}
