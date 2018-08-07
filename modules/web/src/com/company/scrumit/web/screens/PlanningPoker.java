package com.company.scrumit.web.screens;

import com.company.scrumit.entity.LabourIntensity;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebTable;
import com.haulmont.cuba.web.gui.components.WebTreeTable;

import javax.inject.Inject;
import java.util.*;

public class PlanningPoker extends EntityCombinedScreen {

    private static final String DEFAULT_NOTIFY_NO_SELECTED_TASK = "Focus: no";
    private static final int SCORE_VALUE_UNDEFINED = -1;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private Table<Task> tasks;

    @Inject
    private Table<LabourIntensity> scoresTable;

    @Inject
    private Label taskCurrentLabel;

    @Inject
    private CollectionDatasource tasksDs;

    private final List<Task> taskHasNoParent = new ArrayList();

    private Task taskCurrentInScoresTable;

    private final Map<Task, List<LabourIntensity>> tasksScores = new HashMap<>();

    @Override
    public void init(Map<String, Object> params) {

        Action selectTaskAction = new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                Collection list = ((WebTreeTable) component).getSelected();
                if (list.size() == 0) {
                    return;
                }
                Task task = (Task) list.iterator().next();
                if (task != taskCurrentInScoresTable && taskHasNoParent.contains(task)) {
                    selectActiveTask(task)
                    ;
                }
            }
        };
        tasks.setItemClickAction(selectTaskAction);
        tasks.setEnterPressAction(selectTaskAction);

        tasksDs.addCollectionChangeListener(e -> {
            taskHasNoParent.clear();
            tasksDs.getItems().forEach(o -> taskHasNoParent.add((Task) o));
            tasksDs.getItems().forEach(o -> {
                        Task task = (Task) o;
                        if (taskHasNoParent.contains(task.getTask())) {
                            taskHasNoParent.remove(task.getTask());
                        }
                    }
            );
        });
        taskCurrentLabel.setValue(DEFAULT_NOTIFY_NO_SELECTED_TASK);
    }

    public void onAddButton(Component source) {
        openLookup(LabourIntensity.class, items -> {
            items.stream().forEach(o -> {
                LabourIntensity copyFrom = (LabourIntensity) o;
                LabourIntensity copyTo = metadata.create(LabourIntensity.class);
                copyTo.setName(copyFrom.getName());
                copyTo.setValue(copyFrom.getValue());
                scoresTable.getDatasource().addItem(copyTo);
            });
        }, WindowManager.OpenType.DIALOG);
    }

    public void onDeleteButton(Component source) {
        Entity selectedLine = (Entity) ((WebTable) getComponent("scoresTable")).getSelected().stream().findFirst().orElse(null);
        if (selectedLine != null) {
            scoresTable.getDatasource().removeItem(selectedLine);
        }
    }

    private void selectActiveTask(Task task) {
        if (taskCurrentInScoresTable != null) {
            saveScoresTable(taskCurrentInScoresTable);
            loadScoresTable(task);
        }
        taskCurrentInScoresTable = task;
        taskCurrentLabel.setValue("Focus: " + task.getShortdesc());
        getComponent("scoreAddButton").setEnabled(true);
        getComponent("scoreDeleteButton").setEnabled(true);
    }

    private void loadScoresTable(Task task) {
        if (!tasksScores.containsKey(task)) return;

        List<LabourIntensity> labourIntensities = tasksScores.get(task);
        labourIntensities.forEach(o -> scoresTable.getDatasource().addItem(o));
    }

    private void saveScoresTable(Task task) {
        if (task == null || scoresTable.getDatasource().size() == 0) return;

        List<LabourIntensity> labourIntensities;
        if (!tasksScores.containsKey(task)) {
            labourIntensities = new ArrayList<>();
            tasksScores.put(task, labourIntensities);
        } else {
            labourIntensities = tasksScores.get(task);
        }
        labourIntensities.clear();

        scoresTable.getDatasource().getItems().forEach(o -> labourIntensities.add((LabourIntensity) o));
        scoresTable.getDatasource().clear();
    }


    public void save(Component source) {
        saveScoresTable(taskCurrentInScoresTable);
        if (tasksScores.size() > 0) {
            ArrayList<Task> listToParse = new ArrayList<>(taskHasNoParent);
            Map<Task, List<Integer>> result = initResultTable();

            Map<Integer, LabourIntensity> labourIntensityMap = buildLaborMap();
            ArrayList<Integer> intList = new ArrayList<>();
            intList.addAll(labourIntensityMap.keySet());
            parseResultTable(listToParse, result, intList);

            saveToDB(result, labourIntensityMap);
        }

        close(Window.COMMIT_ACTION_ID, true);
    }

    private Map<Integer, LabourIntensity> buildLaborMap() {
        Map<Integer, LabourIntensity> result = new TreeMap<>(Comparator.comparingInt(o -> o));
        LoadContext<LabourIntensity> ctx = LoadContext.create(LabourIntensity.class).setQuery(LoadContext.createQuery(
                "select l from scrumit$LabourIntensity l"
        ));
        List<LabourIntensity> list = dataManager.loadList(ctx);
        list.forEach(x -> result.put(x.getValue(), x));
        return result;
    }

    private Map<Task, List<Integer>> initResultTable() {
        Map<Task, List<Integer>> result = new HashMap<>();
        tasksScores.forEach((t, l) -> {
            ArrayList<Integer> scoresList = new ArrayList<>();
            l.forEach(labourIntensity -> scoresList.add(labourIntensity.getValue()));
            result.put(t, scoresList);
        });
        return result;
    }

    void parseResultTable(List<Task> listToParse, Map<Task, List<Integer>> result, List<Integer> labourList) {
        while (listToParse.size() > 0) {
            Task task = listToParse.get(0);
            int middleValue = 0;
            if (result.containsKey(task)) {
                List<Integer> scoresList = result.get(task);
                if (!scoresList.isEmpty()) {
                    middleValue = countTheMiddleValue(scoresList, labourList);
                    scoresList.clear();
                    scoresList.add(middleValue);

                    Task parentTask = getParentTask(task);
                    if (parentTask != null) {
                        List<Integer> integers = result.getOrDefault(parentTask, new ArrayList<>());
                        integers.add(middleValue);
                        if (!listToParse.contains(parentTask)) {
                            listToParse.add(parentTask);
                            result.put(parentTask, integers);
                        } else {
                            result.replace(parentTask, integers);
                        }
                    }
                }
            }
            listToParse.remove(0);
        }
    }

    private Task getParentTask(Task task) {
        try {
            return task.getTask();
        } catch (IllegalStateException ex) {
            LoadContext<Task> ctx = LoadContext.create(Task.class).setId(task.getId()).setView("task-view-poker");
            return dataManager.load(ctx).getTask();
        }
    }

    int countTheMiddleValue(List<Integer> list1, List<Integer> list2) {
        //todo add checking wide variation for score
        if (list1.isEmpty() || list2.isEmpty()) return 0;

        double x = list1.stream().mapToInt(value -> value).average().getAsDouble();
        int preResult = SCORE_VALUE_UNDEFINED;
        for (int i = 0; i < list2.size(); i++) {
            Integer integer = list2.get(i);
            if (integer - x == 0) {
                preResult = integer;
                break;
            } else if (x - integer < 0) {
                double m = (list2.get(i - 1) + list2.get(i)) / 2;
                if (x - m == 0 || x - m > 0) {
                    preResult = list2.get(i);
                } else {
                    preResult = list2.get(i - 1);
                }
                break;
            }
        }
        return preResult;
    }

    private void saveToDB(Map<Task, List<Integer>> result, Map<Integer, LabourIntensity> labourMap) {
        result.forEach((task, integers) -> task.setScore(labourMap.get(integers.get(0))));
        dataManager.commit(new CommitContext(result.keySet()));
    }

    public void cancel(Component source) {
        close(Window.CLOSE_ACTION_ID);
    }


}