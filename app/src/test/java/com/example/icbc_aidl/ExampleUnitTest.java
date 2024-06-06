package com.example.icbc_aidl;

import org.junit.Test;

import static org.junit.Assert.*;

import com.icbc.selfserviceticketing.deviceservice.printer.Utils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Double a = Utils.millimetersToPixels(72);
        int b = Utils.inchesToPixels(3);
        Double mm =Utils.pixelsToMillimeters(576);
        assertEquals(4, 2 + 2);
    }
}


