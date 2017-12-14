package ru.tinkoff.school.currencyrate;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.tinkoff.school.currencyrate.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ExchangeActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void whenClickAnyCurrencySecondCurrencyShouldBeUSD() throws InterruptedException {
        String firstCurrency = "SEK";
        String secondCurrency = "USD";

        ViewInteraction currencyRecyclerView = onView(withId(R.id.currency_recycler_view));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(firstCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(firstCurrency)), click()));

        ViewInteraction upperTextView = onView(withId(R.id.upper_currency));
        upperTextView.check(matches(withText(firstCurrency)));

        ViewInteraction lowerTextView = onView(withId(R.id.lower_currency));
        lowerTextView.check(matches(withText(secondCurrency)));
    }

    @Test
    public void whenClickUSDSecondCurrencyShouldBeRUB() {
        String firstCurrency = "USD";
        String secondCurrency = "RUB";

        ViewInteraction currencyRecyclerView = onView(withId(R.id.currency_recycler_view));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(firstCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(firstCurrency)), click()));

        ViewInteraction upperTextView = onView(withId(R.id.upper_currency));
        upperTextView.check(matches(withText(firstCurrency)));

        ViewInteraction lowerTextView = onView(withId(R.id.lower_currency));
        lowerTextView.check(matches(withText(secondCurrency)));
    }

    @Test
    public void longClickPlusClickCurrenciesShouldBeDisplayedAppropriately() {
        String firstCurrency = "AUD";
        String secondCurrency = "GBP";

        ViewInteraction currencyRecyclerView = onView(withId(R.id.currency_recycler_view));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(firstCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(firstCurrency)), longClick()));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(secondCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(secondCurrency)), click()));

        ViewInteraction upperTextView = onView(withId(R.id.upper_currency));
        upperTextView.check(matches(withText(firstCurrency)));

        ViewInteraction lowerTextView = onView(withId(R.id.lower_currency));
        lowerTextView.check(matches(withText(secondCurrency)));
    }

    @Test
    public void exchangeButtonShouldBeEnabled() throws InterruptedException {
        ViewInteraction currencyRecyclerView = onView(withId(R.id.currency_recycler_view));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItemAtPosition(4, click()));
        Thread.sleep(1000);

        ViewInteraction exchangeButton = onView(withId(R.id.exchange_button));
        exchangeButton.check(matches(isEnabled()));
    }
}
