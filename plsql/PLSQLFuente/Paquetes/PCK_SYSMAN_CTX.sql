CREATE OR REPLACE PACKAGE PCK_SYSMAN_CTX IS
  /********************************************************************************
  *  PACKAGE        : PCK_SYSMAN_CTX
  *  AUTHOR         : GERMAN DAVID ROJAS
  *  DESCRIPTION    :
  *
  *  Paquete encargado de administrar el Application Context SYSMAN_CTX utilizado
  *  como mecanismo de control lógico para procesos críticos.
  *
  *  Este contexto permite activar o desactivar comportamientos específicos
  *  dentro de triggers o validaciones, principalmente para operaciones masivas
  *  donde se requiere omitir reglas de negocio costosas o que afectan rendimiento.
  *
************************************************************************************/

  -- Activa/desactiva el modo de cierre contable
  PROCEDURE SET_CIERRECONTABLE(VAL NUMBER);

  -- Restablece el valor a 0 de forma segura (autonomous transaction)
  PROCEDURE RESET_CIERRECONTABLE;

END PCK_SYSMAN_CTX;
