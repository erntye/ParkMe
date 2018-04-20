package com.example.chiilek.parkme;

import android.content.res.Resources;

import org.junit.Test;

import static android.util.Log.ASSERT;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String test = Resources.getSystem().getString(R.string.google_api_key);
        assertEquals("AIzaSyCMcA56knRPYgayHU95ceIL2nNyLkpIeUo",test );
    }
}