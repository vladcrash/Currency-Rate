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
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AnalysisFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void weekButtonShouldBeDefaultOption() {
        ViewInteraction analysisTab = onView(withId(R.id.analysis));
        analysisTab.perform(click());

        ViewInteraction weekRadioButton = onView(withId(R.id.week));
        weekRadioButton.check(matches(isChecked()));
    }
}
