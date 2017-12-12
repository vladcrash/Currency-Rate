package ru.tinkoff.school.currencyrate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.school.currencyrate.adapters.FilterAdapter;
import ru.tinkoff.school.currencyrate.models.Currency;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FilterAdapterTest {

    @Test
    public void viewHolderShouldInvokeBindMethod() {
        List<Currency> list = mock(ArrayList.class);
        Currency currency = mock(Currency.class);
        FilterAdapter.FilterViewHolder filterViewHolder = mock(FilterAdapter.FilterViewHolder.class);

        when(list.get(7)).thenReturn(currency);
        FilterAdapter filterAdapter = new FilterAdapter(list);
        filterAdapter.onBindViewHolder(filterViewHolder, 7);
        verify(filterViewHolder, times(1)).bind(currency);
    }
}
