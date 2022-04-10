package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.exception.ValidationException;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ScheduleValidatorService {

    public void validateSchedules(List<Schedule> schedules) throws ValidationException {
        for (Schedule schedule : schedules) {
            validateSchedule(schedule);
        }
    }

    private void validateSchedule(Schedule schedule) throws ValidationException {
        for (TimeFrame timeFrame : schedule.getTimeFrames()) {
            validateTimeframe(timeFrame);
        }

        checkTimeFramesForIntersects(schedule.getTimeFrames());
        checkTimeframesOrder(schedule.getTimeFrames());
    }

    private void validateTimeframe(TimeFrame timeFrame) throws ValidationException {

        if (timeFrame.getStartTime().isBefore(LocalDateTime.now())) {
            String errorMessage = String.format("Timeframe %s has start before current time. " +
                    "It is incorrect", timeFrame);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (timeFrame.getStartTime().isAfter(timeFrame.getEndTime())){
            String errorMessage = String.format(
                    "Schedule is incorrect. Timeframe start must be before timeframe end. Got: %s", timeFrame);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkTimeFramesForIntersects(List<TimeFrame> timeFrames) throws ValidationException {
        for (int i = 0; i < timeFrames.size()-1; ++i) {
            for (int j = 1; j < timeFrames.size(); ++j) {
                if (timeFrames.get(i).isIntersectsWith(timeFrames.get(j))) {
                    String errorMessage = String.format("Timeframes %s and %s intersects",
                            timeFrames.get(i),
                            timeFrames.get(j));
                    log.error(errorMessage);
                    throw new ValidationException(errorMessage);
                }
            }
        }
    }

    private void checkTimeframesOrder(List<TimeFrame> timeFrames) throws ValidationException {
        for (int i = 0; i < timeFrames.size() - 1; ++i) {
            LocalDateTime firstTimeframeStartTime = timeFrames.get(i).getStartTime();
            LocalDateTime secondTimeframeStartTime = timeFrames.get(i+1).getStartTime();
            if (secondTimeframeStartTime.isBefore(firstTimeframeStartTime)) {
                String errorMessage = String.format("Timeframes must be ordered. " +
                        "Timeframe %s must be after and %s", secondTimeframeStartTime, firstTimeframeStartTime);
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }
    }


}
