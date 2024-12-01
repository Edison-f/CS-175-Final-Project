package edu.sjsu.android.cs175finalproject;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AutoTest {

    @Test
    public void testCourse() {
        Course course = new Course();

        assertEquals("100.00", course.getNumericalGrade());
        assertEquals("A+", course.getLetterGrade());

        Course.Assignment assignment = new Course.Assignment(90, 100, "Default");
        course.addAssignment("Default", assignment);
        course.recalculate();
        assertEquals("90.00", course.getNumericalGrade());
        assertEquals("A-", course.getLetterGrade());

        Course.Assignment assignment2 = new Course.Assignment(80, 100, "Default");
        course.addAssignment("Default", assignment2);
        course.recalculate();
        assertEquals("85.00", course.getNumericalGrade());
        assertEquals("B", course.getLetterGrade());

        course.addGroup("New Group");
        course.setGroupWeight("New Group", 2.0);
        assertEquals(2, course.getGroups().size());

        Course.Assignment assignment3 = new Course.Assignment(70, "New Group");
        course.addAssignment("New Group", assignment3);
        course.recalculate();
        assertEquals("85.00", course.getNumericalGrade());
        assertEquals("B", course.getLetterGrade());
    }

    @Test
    public void testGetNumericalGrade() {
        Course course = new Course();
        assertEquals("100.00", course.getNumericalGrade());
    }

    @Test
    public void testGetLetterGrade() {
        Course course = new Course();
        assertEquals("A+", course.getLetterGrade());
    }

    @Test
    public void testRecalculate() {
        Course course = new Course();
        Course.Assignment assignment = new Course.Assignment(100, 100, "Default");
        course.addAssignment("Default", assignment);
        course.recalculate();
        assertEquals("A+", course.getLetterGrade());
    }

    @Test
    public void testRecalculateWithMultipleGroups() {
        Course course = new Course();
        Course.Assignment assignment1 = new Course.Assignment(90, 100, "Group1");
        Course.Assignment assignment2 = new Course.Assignment(80, 100, "Group2");
        course.addAssignment("Group1", assignment1);
        course.addAssignment("Group2", assignment2);
        course.recalculate();
        assertEquals("B", course.getLetterGrade());
    }

    @Test
    public void testRecalculateWithDifferentWeights() {
        Course course = new Course();
        Course.Assignment assignment1 = new Course.Assignment(8, 10, "Group1");
        Course.Assignment assignment2 = new Course.Assignment(70, 100, "Group2");
        course.addAssignment("Group1", assignment1);
        course.addAssignment("Group2", assignment2);
        course.setGroupWeight("Group1", 0.8);
        course.setGroupWeight("Group2", 0.2);
        course.recalculate();
        assertEquals("C+", course.getLetterGrade());
    }

    @Test
    public void testAddGroup() {
        Course course = new Course();
        course.addGroup("New Group");
        assertEquals(2, course.getGroups().size());
    }

    @Test
    public void testMinimumGrade() {
        Course course = new Course();
        assertEquals(0.0, course.minimumGrade(100), 0.01);
    }

}
