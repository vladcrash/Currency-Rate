package ru.tinkoff.school.currencyrate;

import android.support.v7.util.SortedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.tinkoff.school.currencyrate.adapters.CurrencyAdapter;
import ru.tinkoff.school.currencyrate.models.Currency;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CurrencyAdapterTest {

    private CurrencyAdapter mCurrencyAdapter;

    @Mock
    private Currency mCurrency;

    @Mock
    private SortedList mSortedList;

    @Before
    public void setUp() {
        mCurrencyAdapter = new CurrencyAdapter();
        mCurrencyAdapter.setSortedList(mSortedList);
    }

    @Test
    public void currencyAdapterShouldReturnCollectionSize() {
        when(mSortedList.size()).thenReturn(3);
        assertEquals(3, mCurrencyAdapter.getItemCount());
    }

    @Test
    public void currencyAdapterShouldRecalculateItemByPosition() {
        mCurrencyAdapter.recalculatePosition(5);
        verify(mSortedList, times(1)).recalculatePositionOfItemAt(5);
    }

    @Test
    public void currencyAdapterShouldReturnItemByPosition() {
        when(mSortedList.get(3)).thenReturn(mCurrency);
        assertEquals(mCurrency, mCurrencyAdapter.getItem(3));
    }

    @Test
    public void currencyAdapterShouldRemoveItem() {
        mCurrencyAdapter.remove(mCurrency);
        verify(mSortedList, times(1)).remove(mCurrency);
    }

    @Test
    public void currencyAdapterShouldAddItem() {
        mCurrencyAdapter.add(mCurrency);
        verify(mSortedList, times(1)).add(mCurrency);
    }

    @After
    public void cleanUp() {
        mCurrencyAdapter = null;
    }
}
