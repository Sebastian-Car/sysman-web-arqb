create or replace PACKAGE PCK_AYUDAS AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
  
  FUNCTION FC_CARGAR_TAREAS
( 
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CADENA      IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB;

END PCK_AYUDAS;