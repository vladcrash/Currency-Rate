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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class CurrencyListFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void removedItemShouldBeReturnedBack() {
        String firstCurrency = "NOK";
        String secondCurrency = "SEK";

        ViewInteraction currencyRecyclerView = onView(withId(R.id.currency_recycler_view));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(firstCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(firstCurrency)), longClick()));
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(secondCurrency))));
        currencyRecyclerView.perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(secondCurrency)), click()));
        pressBack();
        currencyRecyclerView.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(firstCurrency))));
        currencyRecyclerView.check(matches(hasDescendant(withText(firstCurrency))));
    }

}
