alter table SCRUMIT_SPRINT_BACKLOG add constraint FK_SCRUMIT_SPRINT_BACKLOG_ON_SPRINT foreign key (SPRINT_ID) references SCRUMIT_SPRINT(ID);
create index IDX_SCRUMIT_SPRINT_BACKLOG_ON_SPRINT on SCRUMIT_SPRINT_BACKLOG (SPRINT_ID);
