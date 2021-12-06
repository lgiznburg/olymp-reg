package ru.rsmu.olympreg.services;

import ru.rsmu.olympreg.entities.AssignExamInfo;
import ru.rsmu.olympreg.entities.GettingResultInfo;

import java.util.concurrent.BlockingQueue;

/**
 * @author leonid.
 */
public interface AssignExamControl {
    BlockingQueue<AssignExamInfo> getAssignExamQueue();
    BlockingQueue<GettingResultInfo> getResultQueue();
}
