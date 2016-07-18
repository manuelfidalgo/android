
package com.mfidalgo.android.ocr;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class InstrumentationTest {

    @Rule
    public ActivityTestRule<OcrCaptureActivity> mActivityRule = new ActivityTestRule(OcrCaptureActivity.class);

    @Test
    public void initialTextIsCorrect() {


        onView(withText("Read Text")).check(matches(isDisplayed()));
    }
}
