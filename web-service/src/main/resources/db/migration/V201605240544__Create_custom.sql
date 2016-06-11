CREATE TABLE custom (
  id          VARCHAR2(15) CONSTRAINT id_pk PRIMARY KEY ,
  name        VARCHAR2(15) NOT NULL ,
  age         NUMBER,
  update_date TIMESTAMP,
  create_date TIMESTAMP
);
CREATE SEQUENCE custom_seq INCREMENT BY 1 START WITH 100000000000000 NOMAXVALUE NOCYCLE CACHE 20;
CREATE INDEX idx_custom_name ON custom (name);
INSERT INTO custom (id, name, age) VALUES (custom_seq.nextval,'pjf',28);
