package org.akvo.flow.api.app;

public class NoDataPointsAssignedException extends Exception{
    public NoDataPointsAssignedException(String no_datapoints_assigned_found) {
        super(no_datapoints_assigned_found);
    }
}
