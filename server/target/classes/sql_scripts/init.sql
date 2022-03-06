insert into schedule(schedule_id, time_end)
VALUES (1, now() + interval '1 week');
insert into schedule(schedule_id, time_end)
VALUES (2, now() + interval '2 week');
insert into schedule(schedule_id, time_end)
VALUES (3, now() + interval '3 week');
insert into schedule(schedule_id, time_end)
VALUES (4, now() + interval '4 week');

insert into time_frame(end_time, start_time, schedule_id)
VALUES (now(), now() + interval '1 day', 1);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + 2 * interval '1 day', now() + 3 * interval '1 day', 1);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '1 week', now() + interval '1 day' + interval '1 week', 2);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '1 week' + 2 * interval '1 day', now() + interval '1 week' + 3 * interval '1 day', 2);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '2 week', now() + interval '1 day' + interval '2 week', 3);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '2 week' + 2 * interval '1 day', now() + interval '2 week' + 3 * interval '1 day', 3);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '3 week', now() + interval '1 day' + interval '3 week', 4);
insert into time_frame(end_time, start_time, schedule_id)
VALUES (now() + interval '3 week' + 2 * interval '1 day', now() + interval '3 week' + 3 * interval '1 day', 4);