CREATE TABLE custom (
  id          VARCHAR2(15) CONSTRAINT id_pk PRIMARY KEY ,
  name        VARCHAR2(15) NOT NULL ,
  age         NUMBER,
  update_date TIMESTAMP,
  create_date TIMESTAMP
);
create public synonym CUSTOM for PENGJINFEI.CUSTOM;
CREATE SEQUENCE custom_seq INCREMENT BY 1 START WITH 1000000000000000 NOMAXVALUE NOCYCLE CACHE 20;
CREATE PUBLIC SYNONYM CUSTOM_SEQ FOR PENGJINFEI.CUSTOM_SEQ;
CREATE INDEX idx_custom_name ON custom (name);
INSERT INTO custom (id, name, age, update_date, create_date) VALUES (custom_seq.nextval,'pjf',28,sysdate,sysdate);
