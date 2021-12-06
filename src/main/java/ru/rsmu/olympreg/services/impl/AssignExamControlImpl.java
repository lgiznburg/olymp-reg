package ru.rsmu.olympreg.services.impl;

import ru.rsmu.olympreg.entities.AssignExamInfo;
import ru.rsmu.olympreg.entities.GettingResultInfo;
import ru.rsmu.olympreg.services.AssignExamControl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author leonid.
 */
public class AssignExamControlImpl implements AssignExamControl {
    private BlockingQueue<AssignExamInfo> assignExamQueue;
    private BlockingQueue<GettingResultInfo> gettingResultQueue;

    public AssignExamControlImpl(  ) {
        this.assignExamQueue = new LinkedBlockingQueue<>(10);
        this.gettingResultQueue = new LinkedBlockingQueue<>( 10);
    }

    @Override
    public BlockingQueue<AssignExamInfo> getAssignExamQueue() {
        return assignExamQueue;
    }

    @Override
    public BlockingQueue<GettingResultInfo> getResultQueue() {
        return gettingResultQueue;
    }
}
