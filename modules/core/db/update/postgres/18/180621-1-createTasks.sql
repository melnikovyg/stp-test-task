create table SCRUMIT_TASKS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SHORTDESC varchar(50) not null,
    TASK_ID uuid,
    --
    primary key (ID)
);
