package ru.rsmu.olympreg.services;

/**
 * @author leonid.
 */
public interface CompetitorNotificationService {
    void notifyUncompletedCompetitors();
    void notifyPreviousYearCompetitors();
    boolean isDone();
}
