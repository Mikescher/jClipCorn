ALTER TABLE MOVIES ADD COLUMN TAGS SMALLINT;
UPDATE MOVIES SET TAGS = STATUS;
ALTER TABLE MOVIES DROP COLUMN STATUS;

ALTER TABLE EPISODES ADD COLUMN TAGS SMALLINT;
UPDATE EPISODES SET TAGS = STATUS;
ALTER TABLE EPISODES DROP COLUMN STATUS;