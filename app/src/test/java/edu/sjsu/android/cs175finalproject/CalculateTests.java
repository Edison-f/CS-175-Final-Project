package edu.sjsu.android.cs175finalproject;

import static junit.framework.TestCase.*;

import org.junit.Test;

public class CalculateTests {
    @Test
    public void NoneTest() {
        Course c = new Course();
        c.recalculate();
        System.out.println(c.getNumericalGrade());
        assertEquals("A", c.getLetterGrade());
    }

    @Test
    public void oneHundred() {
        Course c = new Course();
        Course.Assignment a1 = new Course.Assignment(100, 100, "g1");
        c.addAssignment("g1", a1);
        c.recalculate();
        System.out.println(c.getNumericalGrade());
        assertEquals("A+", c.getLetterGrade());
    }

    @Test
    public void oneFifty() {
        Course c = new Course();
        Course.Assignment a1 = new Course.Assignment(50, 100, "g1");
        c.addAssignment("g1", a1);
        c.recalculate();
        System.out.println(c.getNumericalGrade());
        assertEquals("F-", c.getLetterGrade());
    }
}
