package org.ssau.privatechannel.constants;

public interface Endpoints {
    String API_V1_SERVER = "/api/v1/server";
    String API_V1_CLIENT = "/api/v1/client";
    String UPLOAD_DATA = "/upload-data";
    String GENERATE_DATA = "/generate-data/{count}";
    String GENERATE_SCHEDULE = "/generate-schedule/{duration}";
    String SCHEDULES = "/schedules";
    String SCHEDULE = "/schedule";
    String GET_NEW_SCHEDULE = "/getNewSchedule";
}
