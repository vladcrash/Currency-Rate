package ru.tinkoff.school.currencyrate;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.tinkoff.school.currencyrate.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class HistoryFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void whenOtherButtonIsPressedDateButtonsShouldBeDisplayed() {
        ViewInteraction historyTab = onView(withId(R.id.history));
        historyTab.perform(click());

        ViewInteraction filterMenuItem = onView(withId(R.id.action_filter));
        filterMenuItem.perform(click());

        ViewInteraction otherRadioButton = onView(withId(R.id.other));
        otherRadioButton.perform(click());

        ViewInteraction beginDateButton = onView(withId(R.id.begin_date_button));
        beginDateButton.check(matches(isDisplayed()));

        ViewInteraction endDateButton = onView(withId(R.id.end_date_button));
        endDateButton.check(matches(isDisplayed()));
    }

    @Test
    public void whenOtherButtonIsNotPressedDateButtonsShouldNotBeDisplayed() {
        ViewInteraction historyTab = onView(withId(R.id.history));
        historyTab.perform(click());

        ViewInteraction filterMenuItem = onView(withId(R.id.action_filter));
        filterMenuItem.perform(click());

        ViewInteraction weekRadioButton = onView(withId(R.id.week_button));
        weekRadioButton.perform(click());

        ViewInteraction beginDateButton = onView(withId(R.id.begin_date_button));
        beginDateButton.check(matches(not(isDisplayed())));

        ViewInteraction endDateButton = onView(withId(R.id.end_date_button));
        endDateButton.check(matches(not(isDisplayed())));
    }

    @Test
    public void filterMenuItemShouldBeDisplayed() {
        ViewInteraction historyTab = onView(withId(R.id.history));
        historyTab.perform(click());

        ViewInteraction filterMenuItem = onView(withId(R.id.action_filter));
        filterMenuItem.check(matches(isDisplayed()));
    }


    @Test
    public void confirmMenuItemShouldBeDisplayed() {
        ViewInteraction historyTab = onView(withId(R.id.history));
        historyTab.perform(click());

        ViewInteraction filterMenuItem = onView(withId(R.id.action_filter));
        filterMenuItem.perform(click());

        ViewInteraction confirmMenuItem = onView(withId(R.id.confirm));
        confirmMenuItem.check(matches(isDisplayed()));
    }

}
