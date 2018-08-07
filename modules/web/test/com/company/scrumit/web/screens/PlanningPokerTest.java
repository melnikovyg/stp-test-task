package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class PlanningPokerTest {

    static private Task TASK_1;
    static private Task TASK_2;
    static private Task TASK_2_1;
    static private Task TASK_2_1_1;
    static private Task TASK_2_1_2;
    static private Task TASK_2_2;
    static private List<Task> listToParse;
    static private Map<Task, List<Integer>> actualResult;
    static private Map<Task, List<Integer>> expectedResult;
    static private List<Integer> list;

    @BeforeClass
    public static void setUp() {
        TASK_1 = new Task();
        TASK_1.setShortdesc("Task 1");
        TASK_2 = new Task();
        TASK_2.setShortdesc("Task 2");
        TASK_2_1 = new Task();
        TASK_2_1.setShortdesc("Task 2-1");
        TASK_2_1.setTask(TASK_2);
        TASK_2_1.setTop(TASK_2);
        TASK_2_1_1 = new Task();
        TASK_2_1_1.setShortdesc("Task 2-1-1");
        TASK_2_1_1.setTask(TASK_2_1);
        TASK_2_1_1.setTop(TASK_2);
        TASK_2_1_2 = new Task();
        TASK_2_1_2.setShortdesc("Task 2-1-2");
        TASK_2_1_2.setTask(TASK_2_1);
        TASK_2_1_2.setTop(TASK_2);
        TASK_2_2 = new Task();
        TASK_2_2.setShortdesc("Task 2-2");
        TASK_2_2.setTask(TASK_2);
        TASK_2_2.setTop(TASK_2);

        expectedResult = new HashMap<>();
        List<Integer> listE1 = new ArrayList<>();
        listE1.add(-1);
        expectedResult.put(TASK_1, listE1);
        List<Integer> listE2 = new ArrayList<>();
        listE2.add(8);
        expectedResult.put(TASK_2, listE2);
        List<Integer> listE21 = new ArrayList<>();
        listE21.add(5);
        expectedResult.put(TASK_2_1, listE21);
        List<Integer> listE211 = new ArrayList<>();
        listE211.add(3);
        expectedResult.put(TASK_2_1_1, listE211);
        List<Integer> listE212 = new ArrayList<>();
        listE212.add(8);
        expectedResult.put(TASK_2_1_2, listE212);
        List<Integer> listE22 = new ArrayList<>();
        listE22.add(8);
        expectedResult.put(TASK_2_2, listE22);

        list = new ArrayList<>();
        list.addAll(Arrays.asList(-1, 1, 2, 3, 5, 8, 13, 21, 34, 55));

    }

    @Before
    public void before() {

    }

    @Test
    public void makeResultTableTest() {
        listToParse = new ArrayList<>();
        listToParse.add(TASK_1);
        listToParse.add(TASK_2_1_1);
        listToParse.add(TASK_2_1_2);
        listToParse.add(TASK_2_2);

        actualResult = new HashMap<>();
        List<Integer> listA1 = new ArrayList<>();
        listA1.addAll(Arrays.asList(21, 1, 1));
        actualResult.put(TASK_1, listA1);
        List<Integer> listA211 = new ArrayList<>();
        listA211.addAll(Arrays.asList(2, 3));
        actualResult.put(TASK_2_1_1, listA211);
        List<Integer> listA212 = new ArrayList<>();
        listA212.addAll(Arrays.asList(5, 5, 8, 8));
        actualResult.put(TASK_2_1_2, listA212);
        List<Integer> listA22 = new ArrayList<>();
        listA22.addAll(Arrays.asList(5, 8, 13));
        actualResult.put(TASK_2_2, listA22);

        PlanningPoker PlanningPoker = new PlanningPoker();
        PlanningPoker.parseResultTable(listToParse, actualResult, list);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void countTheMiddleValue() {
        PlanningPoker PlanningPoker = new PlanningPoker();
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(2, 3));
        Assert.assertSame(3, PlanningPoker.countTheMiddleValue(list, PlanningPokerTest.list));
        list.clear();
        list.addAll(Arrays.asList(5, 8, 13));
        Assert.assertSame(8, PlanningPoker.countTheMiddleValue(list, PlanningPokerTest.list));
    }

    @Test
    public void countTheMiddleValueUndefined() {
        PlanningPoker PlanningPoker = new PlanningPoker();
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(21, 1, 1));
        Assert.assertSame(-1, PlanningPoker.countTheMiddleValue(list, PlanningPokerTest.list));
    }


}
