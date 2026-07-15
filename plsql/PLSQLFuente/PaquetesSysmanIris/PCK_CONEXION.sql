CREATE OR REPLACE PACKAGE                "PCK_CONEXION" 
AS 
  --Paquete que permite set y get de las variables de conexion
  GL_USER        VARCHAR2(32000) :='USUARIO';        -- 26/01/2015 - Funci¿ue guarda el usuario con el cual se conecta 
  GL_IPCOMPT     VARCHAR2(32000) :='DESCONOCIDA';    -- 26/01/2015 - Funci¿ue guarda la Ip desde la cual se conecta el usuario
  GL_FORM_MENU   VARCHAR2(32000) :='FORMS_000';      -- 11/02/2015 - Funci¿ue guarda el formulario del cual se conecta el usuario

FUNCTION FC_GETUSER
RETURN VARCHAR2;

FUNCTION FC_GETIP
RETURN VARCHAR2;

FUNCTION FC_GETFORM_MENU
RETURN VARCHAR2;

PROCEDURE PR_SETUSER(
  UN_USUARIO VARCHAR2
);

PROCEDURE PR_SETIP(
  UN_IP VARCHAR2
);

PROCEDURE PR_SETFORM_MENU(
  UN_FORM_MENU VARCHAR2
);

END PCK_CONEXION;