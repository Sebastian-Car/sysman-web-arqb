create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_UTL AS

--1
FUNCTION FC_CICLOCAL
 /*
   NAME              : FC_CICLOCAL --> En Access  CicloCal
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 26/07/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SysmanUE_SPTA2017.05.02
   MODIFIER          : 
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Funcion que retorna verdadero si no se encuentran valores con 
                       los filtros enviados, si es asi se debe agregar el mensaje de alerta
                       desde el llamado en el controlador 
                       "El ciclo ya esta calculado no se permite hacer cambios" 
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO A CONSULTAR

   MODIFICATIONS     :

   @NAME:    obtenerCicloCalculado
   @METHOD:  GET
 */
(
	 UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
	,UN_CICLO     IN PCK_SUBTIPOS.TI_CICLO
) 
RETURN PCK_SUBTIPOS.TI_LOGICO 
AS 
    MI_EXISTE PCK_SUBTIPOS.TI_LOGICO;
BEGIN 
    BEGIN 
        SELECT NVL(INDPREPARADO,0) 
          INTO MI_EXISTE
          FROM SP_CICLO 
         WHERE COMPANIA = UN_COMPANIA 
           AND NUMERO   = UN_CICLO 
           AND NVL(INDPREPARADO,0) NOT IN(0) 
           AND NVL(INDFACTURADO,0) IN(0) 
           AND NVL(INDCALCULADO,0) IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
    	RETURN -1;--'El ciclo ya esta calculado no se permite hacer cambios';
    END;
    RETURN 0;
END FC_CICLOCAL;  

--2
FUNCTION FC_VALIDAREMPRESAEXTERNA
 /*
   NAME              : FC_VALIDAREMPRESAEXTERNA --> En Access  Form_BeforeUpdate form FRM_ASIGNA_EMPRESA
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 26/07/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SysmanUE_SPTA2017.05.02
   MODIFIER          : 
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Funcion que permite validar si un registro se debe a o no actualizar
                       la empresa externa y codigo. en la tabla sp_usuario
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO A CONSULTAR
                       UN_CODIGORUTA      => CODIGO DE RUTA DEL USUSRIO A ACTUALIZAR
                       UN_EMPRESAASEOEXT  => VALOR DE LA EMPRESA EXTERNA SELECCIONADA EN EL FORMULARIO
                       UN_CODIGO_EXTERNO  => VALOR DEL CODIGO EXTERNO DEL USURAIO
   MODIFICATIONS     :

   @NAME:    validarEmpresaExterna
   @METHOD:  GET
 */
(
     UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO   
    ,UN_CODIGORUTA     IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_EMPRESAASEOEXT IN SP_EMPRESAS_TERCERIZA.ID%TYPE 
    ,UN_CODIGO_EXTERNO IN SP_USUARIO.CODIGO_EXTERNO%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS  
    MI_EXISTE     PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    IF UN_EMPRESAASEOEXT IS NOT NULL AND UN_CODIGO_EXTERNO IS NULL THEN
        BEGIN 
          	RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
	                      UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => 'SP_USUARIO'
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUA_EMPREXTERNA1);
        END;
    END IF;
    BEGIN 
        SELECT CODIGORUTA 
          INTO MI_EXISTE
          FROM SP_USUARIO
         WHERE COMPANIA       = UN_COMPANIA
           AND CICLO          = UN_CICLO
           AND CODIGORUTA     NOT IN(UN_CODIGORUTA) 
           AND CODIGO_EXTERNO IN(UN_CODIGO_EXTERNO) 
           AND EMPRESAASEOEXT IN(UN_EMPRESAASEOEXT) ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE:=NULL;
    END;

    IF MI_EXISTE IS NOT NULL THEN
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='CODIGORUTA';
            MI_REEMPLAZOS(0).VALOR:=MI_EXISTE;
        	  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        	  PCK_ERR_MSG.RAISE_WITH_MSG(
	                      UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => 'SP_USUARIO'
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUA_EMPREXTERNA2
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
    END IF;
    RETURN -1;
END FC_VALIDAREMPRESAEXTERNA;

--3
PROCEDURE PR_CARGARPLANO_NOV_CONVENIO
/*
    NAME              : PR_PLANO_NOVEDADES_CONVENIO  --> En Access PlanoNovedadesConvenio
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 31/07/2017
    TIME              : 04:30 PM
    SOURCE MODULE     : SysmanUE_SPTA2017.05.02
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : REGISTRA EN LA TABLA SP_HISTORIA_NOVEDADES LAS DIFERENTES NOVEVADES DE LOS CONVENIOS
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_CODIGOINTERNO  =>  CODIGO INTERNO PARA REGISTRAR LA NOVEDAD.
                        UN_CICLO          =>  CICLO PARA EL CUAL SE GENERARA LA NOVEDAD.
                        UN_ANO            =>  AÑO PARA EL CUAL SE GENERARA LA NOVEDAD.
                        UN_PERIODO        =>  PERIODO PARA EL CUAL SE GENERARA LA NOVEDAD.
                        UN_NIT            =>  NIT DE LA EMPRESA DEL CONVENIO.
                        UN_TOTAL          =>  VALOR TOTAL DE LA NOVEDAD. 
                        UN_CUOTASPACTADAS =>  NUMERO DE CUOTAS PACTADAS PARA LA NOVEDAD.
                        UN_CUOTAAPAGAR    =>  VALOR CUOTA A PAGAR PARA LA NOVEDAD.
                        UN_CAPITAL        =>  VALOR CAPITAL DE LA NOVEDAD.
                        UN_INTERES        =>  VALOR INTERES DE LA NOVEDAD.
                        UN_OTROS          =>  OTROS VALORES.
                        UN_USUARIO        =>  USUARIO QUE ESTA REALIZANDO EL PROCESO.
      @NAME:    cargarNovedadesConvenio 
      @METHOD:  POST
  */
(
  UN_COMPANIA                  IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOINTERNO             IN   SP_USUARIO.CODIGOINTERNO%TYPE,
  UN_CICLO                     IN   SP_USUARIO.CICLO%TYPE,
  UN_ANO                       IN   SP_USUARIO.ANO%TYPE,
  UN_PERIODO                   IN   SP_USUARIO.PERIODO%TYPE,
  UN_NIT                       IN   SP_EMPRESAS_CONVENIO.NIT%TYPE,
  UN_TOTAL                     IN   SP_HISTORIA_CONVENIOS.TOTAL%TYPE,
  UN_CUOTASPACTADAS            IN   SP_HISTORIA_CONVENIOS.CUOTASPACTADAS%TYPE,
  UN_CUOTAAPAGAR               IN   SP_HISTORIA_CONVENIOS.CUOTAAPAGAR%TYPE,
  UN_CAPITAL                   IN   SP_HISTORIA_CONVENIOS.CAPITAL%TYPE,
  UN_INTERES                   IN   SP_HISTORIA_CONVENIOS.INTERES%TYPE,
  UN_OTROS                     IN   SP_HISTORIA_CONVENIOS.OTROS%TYPE,
  UN_USUARIO                   IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_ENSITIO              PCK_SUBTIPOS.TI_LOGICO;
  MI_CICLO                SP_USUARIO.CICLO%TYPE;
  MI_CODIGORUTA           SP_USUARIO.CODIGORUTA%TYPE;
  MI_ANO                  SP_USUARIO.ANO%TYPE;
  MI_PERIODO              SP_USUARIO.PERIODO%TYPE;
  MI_ESTADO               SP_USUARIO.ESTADO%TYPE;
  MI_CODIGOINTERNO        SP_USUARIO.CODIGOINTERNO%TYPE;
  MI_FIMM                 SP_USUARIO.FIMM%TYPE;
  MI_ID                   SP_EMPRESAS_CONVENIO.ID%TYPE;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES; 
  MI_IND                  SP_CICLO.INDPREPARADO%TYPE;
BEGIN
  IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                           UN_NOMBRE    => 'FACTURACION EN SITIO', 
                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                           UN_FECHA_PAR => SYSDATE)= 'SI' THEN
    MI_ENSITIO:=1;                                                               
  END IF;

  BEGIN
    BEGIN
      SELECT CICLO, 
      CODIGORUTA,
      ANO, 
      PERIODO, 
      ESTADO, 
      CODIGOINTERNO, 
      FIMM
      INTO 
      MI_CICLO,
      MI_CODIGORUTA,
      MI_ANO,
      MI_PERIODO,
      MI_ESTADO,
      MI_CODIGOINTERNO,
      MI_FIMM
      FROM SP_USUARIO 
      WHERE COMPANIA= UN_COMPANIA
      AND CODIGOINTERNO= UN_CODIGOINTERNO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
       RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_USUARNOREGISTRADO/*El usuario no se encontró en la base de datos*/
                        );                      
  END;

  IF MI_PERIODO <> UN_PERIODO OR MI_ANO <> UN_ANO  OR MI_CICLO <> UN_CICLO THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_CICLOPERANIO/*Error en el ciclo, periodo y año*/
                            );
    END;
  END IF;


  IF MI_ESTADO = 'R' THEN
   BEGIN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_USUARIORET/*Usuario Retirado*/
                            );
    END;
  END IF;

  IF MI_ENSITIO = 1 AND MI_FIMM = 'P' THEN
   BEGIN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_USUARIOSITIOMOD/*Usuario en Sitio no se permite Modificar*/
                            );
    END;
  END IF;

  BEGIN
    BEGIN
      SELECT ID 
      INTO   MI_ID
      FROM SP_EMPRESAS_CONVENIO 
      WHERE COMPANIA=UN_COMPANIA
      AND NIT= UN_NIT;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN   
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_NITEMPRESACONV/*El nit de la Empresa del convenio no Existe en la base de datos*/
                        );                      
  END;

  BEGIN
  BEGIN
   SELECT FACTURA 
   INTO MI_ID
   FROM SP_HISTORIA_CONVENIOS 
   WHERE COMPANIA = UN_COMPANIA
    AND CICLO = MI_CICLO
    AND CODIGORUTA = MI_CODIGORUTA 
    AND ANO = UN_ANO 
    AND PERIODO = UN_PERIODO 
    AND ID_EMPRESA =MI_ID;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
           RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_EXISTENOVEDAD /*Ya existe novedad no se permite modificar */
                        );                      
  END;

  IF UN_TOTAL = 0 THEN
     BEGIN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_VALORTOTAL/*El valor Total es 0*/
                            );
    END;
  END IF;


  MI_CAMPOS:= 'COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,ID_EMPRESA,CODIGO,CUOTASPACTADAS, CUOTAAPAGAR, CAPITAL, INTERES, OTROS, TOTAL, DATE_CREATED, CREATED_BY';
  MI_VALORES:= ' '''||  UN_COMPANIA       ||''',
               '    ||  MI_CICLO          ||', 
               '''  ||  MI_CODIGORUTA     ||''', 
               '    ||  UN_ANO            ||', 
               '''  ||  UN_PERIODO        ||''', 
               '''  ||  MI_ID             ||''', 
               '''  ||  UN_CODIGOINTERNO  ||''',
               '    ||  UN_CUOTASPACTADAS ||', 
               '    ||  UN_CUOTAAPAGAR    ||', 
               '    ||  UN_CAPITAL        ||', 
               '    ||  UN_INTERES        ||', 
               '    ||  UN_OTROS          ||', 
               '    ||  UN_TOTAL          ||',
               SYSDATE,
               '''||UN_USUARIO||''' ';
   BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SP_HISTORIA_CONVENIOS', 
                                                 UN_ACCION  =>  'I',
                                                 UN_CAMPOS  =>  MI_CAMPOS, 
                                                 UN_VALORES =>  MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INSHISTCONVENIO
                          );
      END;
END PR_CARGARPLANO_NOV_CONVENIO;

END PCK_SERVICIOS_PUBLICOS_UTL;