package com.wayne.run;

import com.wayne.task.Task;
import com.wayne.task.TaskGroup;
import com.wayne.test.AssertUtil;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Run {
    public static void main(String[] args) {
        Integer taskCount = 30;
        if(taskCount>32){throw new RuntimeException("task should be not bigger than 32 in this case");}

        Integer taskGroupCapacity = 10;
        if(taskGroupCapacity>taskCount){throw new RuntimeException("there are not enough task to select");}
        AssertUtil.setTaskGroupCapacity(taskGroupCapacity);

        Integer taskGroupCount = 10;
        CountDownLatch latch = new CountDownLatch(taskGroupCount);

        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            taskList.add(new Task(i));
        }

        List<TaskGroup> taskGroupList = new ArrayList<>();
        for (int i = 0; i < taskGroupCount; i++) {
            taskGroupList.add(new TaskGroup(i, taskGroupCapacity, taskList,latch));
        }

        for (TaskGroup taskGroup : taskGroupList) {
            new Thread(taskGroup).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("任务执行完毕");
        taskGroupList.stream()
                .forEach(
                        taskGroup -> {
                            System.out.println("任务组 " + taskGroup.getGroupId()
                                    +" ：任务 " + taskGroup.getMyTaskList()
                                    .stream().map(Task::getId).map(String::valueOf)
                            .collect(Collectors.joining(",")));
                        }
                );
    }
}
