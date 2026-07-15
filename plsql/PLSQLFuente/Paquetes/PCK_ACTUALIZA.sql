create or replace PACKAGE "PCK_ACTUALIZA" AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
  GL_ERROR_NUM                NUMBER:=9900;   
--01

PROCEDURE PR_CREAR_TABLA_CA
  (
  UN_TABLA            VARCHAR2,
  UN_CAMPOS           VARCHAR2,
  UN_LLAVES           VARCHAR2,
  UN_TABLESPACE       VARCHAR2  
  );

--02

PROCEDURE PR_CREAR_CAMPO_CA
  (
  UN_TABLA              VARCHAR2,
  UN_CAMPO              VARCHAR2,
  UN_TIPO_TAMANO        VARCHAR2,
  UN_PARAMETROS_NULL    VARCHAR2:='',
  UN_PARAMETROS_DEFAULT VARCHAR2:=''
  );

--03

PROCEDURE PR_CAMPO_DEFAULT
  (
  UN_TABLA              VARCHAR2,
  UN_CAMPO              VARCHAR2,
  UN_VAL_DEFAULT        VARCHAR2
  );

--03

PROCEDURE PR_CAMBIAR_NULL
  (
  UN_TABLA              VARCHAR2,
  UN_CAMPO              VARCHAR2,
  UN_NUEVOESTADO        VARCHAR2
  );

END PCK_ACTUALIZA;