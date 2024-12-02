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

    @Test
    public void testGroups() {
        // Create a new course instance
        Course course = new Course();

        // Add groups to the course
        course.addGroup("Homework", 0.3);
        course.addGroup("Midterm", 0.3);
        course.addGroup("Final", 0.4);

        Course.Assignment a1 = new Course.Assignment(100, 100, "Homework");
        Course.Assignment a2 = new Course.Assignment(100, 100, "Midterm");
        Course.Assignment a3 = new Course.Assignment(0, 100, "Final");

        // Add assignments to the groups
        course.addAssignment("Homework", a1);
        course.addAssignment("Midterm", a2);
        course.addAssignment("Final", a3);

        // Recalculate the course grade
        course.recalculate();

        // Print the result (optional)
        System.out.println(course.getNumericalGrade());
        // Assertions to verify the result
        assertEquals("60.00", course.getNumericalGrade());
    }
}
