alter table SCRUMIT_TASKS_FILE add constraint FK_SCRUMIT_TASKS_FILE_ON_TASK foreign key (TASK_ID) references SCRUMIT_TASK(ID);
alter table SCRUMIT_TASKS_FILE add constraint FK_SCRUMIT_TASKS_FILE_ON_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_SCRUMIT_TASKS_FILE_ON_TASK on SCRUMIT_TASKS_FILE (TASK_ID);
create index IDX_SCRUMIT_TASKS_FILE_ON_FILE on SCRUMIT_TASKS_FILE (FILE_ID);
