create or replace PACKAGE BODY PCK_SYSMAN_CTX IS

  -- Procedimiento normal para cambiar la variable de contexto
  PROCEDURE SET_CIERRECONTABLE(VAL NUMBER) IS
  BEGIN
    DBMS_SESSION.SET_CONTEXT(
      namespace => 'SYSMAN_CTX',
      attribute => 'CIERRECONTABLE',
      value     => VAL
    );
  END SET_CIERRECONTABLE;


  -- Procedimiento blindado para reiniciar el valor a 0
  PROCEDURE RESET_CIERRECONTABLE IS
    PRAGMA AUTONOMOUS_TRANSACTION;
  BEGIN
    DBMS_SESSION.SET_CONTEXT(
      namespace => 'SYSMAN_CTX',
      attribute => 'CIERRECONTABLE',
      value     => 0
    );
    COMMIT;  -- IMPORTANTE: obligatorio por autonomous transaction
  EXCEPTION
    WHEN OTHERS THEN
      -- En caso extremo, intentamos nuevamente dejarlo en 0
      DBMS_SESSION.SET_CONTEXT(
        namespace => 'SYSMAN_CTX',
        attribute => 'CIERRECONTABLE',
        value     => 0
      );
      COMMIT;
  END RESET_CIERRECONTABLE;

END PCK_SYSMAN_CTX;