package org.ssau.privatechannel.comparator;

import org.ssau.privatechannel.model.TimeFrame;

import java.util.Comparator;

public class TimeFrameComparatorToMax implements Comparator<TimeFrame> {
    @Override
    public int compare(TimeFrame o1, TimeFrame o2) {
        return o1.getEndTime().compareTo(o2.getEndTime());
    }
}
