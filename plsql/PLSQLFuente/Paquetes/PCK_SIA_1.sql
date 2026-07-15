create or replace PACKAGE BODY PCK_SIA AS

-- 1
PROCEDURE PR_TRANSLADAR_ANIO 
/*
    NAME              : PR_CARGAR_PLAN_VIG
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 04/12/2018                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CONFIGURACION CUENTAS DESCUENTO
    @NAME:    configurarCuentasDesc
    @METHOD:  PUT
    */
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
 )
AS 
  MI_ANIOSIG      PCK_SUBTIPOS.TI_ENTERO;
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_EXISTE       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
   MI_ANIOSIG := UN_ANIO + 1;

    BEGIN
      SELECT COUNT(1) EXISTE
      INTO MI_EXISTE
       FROM PLAN_CONTABLE
        WHERE COMPANIA = UN_COMPANIA 
          AND ANO      = MI_ANIOSIG ;
      --EXECUTE IMMEDIATE MI_STRSQL;
      IF MI_EXISTE = 0 THEN 
        BEGIN 
         -- MI_RTA :='No se puede realizar la transacción porque los datos iniciales son iguales a los datos finales';
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSIA;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSIA THEN
              MI_MSGERROR(1).CLAVE := 'ANIO';
              MI_MSGERROR(1).VALOR :=  MI_ANIOSIG;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD 		=>	SQLCODE,
                          UN_ERROR_COD	=>	PCK_ERRORES.ERR_SYSMANSIA_CONFIGDATOS,
                          UN_TABLAERROR => 'PLAN_CONTABLE',
                          UN_REEMPLAZOS => MI_MSGERROR); 
        END;
      END IF;

    END;         

   BEGIN 
    BEGIN               
       MI_TABLA := 'PLAN_CONTABLE';

       MI_MERGEUSING := 'SELECT COMPANIA
                                , ANO
                                , CODIGO
                                , TIPODESCUENTO_SIA
                         FROM PLAN_CONTABLE
                         WHERE COMPANIA = ''' || UN_COMPANIA || '''
                           AND ANO      =   ' || UN_ANIO || '
                           AND TIPODESCUENTO_SIA IS NOT NULL';

       MI_MERGEENLACE := 'TABLA.COMPANIA    = VISTA.COMPANIA
                         AND TABLA.CODIGO = VISTA.CODIGO';

       MI_MERGEEXISTE := 'UPDATE SET TABLA.TIPODESCUENTO_SIA = VISTA.TIPODESCUENTO_SIA
                           WHERE TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                             AND TABLA.ANO      = '  || MI_ANIOSIG ;

       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                    ,UN_ACCION      => 'MM'
                                    ,UN_MERGEUSING  => MI_MERGEUSING
                                    ,UN_MERGEENLACE => MI_MERGEENLACE
                                    ,UN_MERGEEXISTE => MI_MERGEEXISTE); 

  	  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
		   RAISE PCK_EXCEPCIONES.EXC_SYSMANSIA;

   END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSIA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD 		=>	SQLCODE,
        UN_ERROR_COD	=>	PCK_ERRORES.ERR_SYSMANSIA_CONFIGCUENTAS,
        UN_TABLAERROR => MI_TABLA
      );                                                            
  END;                                

END PR_TRANSLADAR_ANIO;

FUNCTION FC_SUBIR_CONSOLIDADO_SIA
/*
    NAME              : FC_SUBIR_CONSOLIDADO_SIA
    AUTHORS           : LVEGA
    DATE MIGRADOR     : 27/11/2024
    TIME              : 02:00 PM
    DESCRIPTION       : Sube informacion de las tablas sia dependiendo del informe seleccionado
      --NAME:    subirConsolidadoSia
      --METHOD:  INSERT UPDATE
  */
(
   UN_TABLA            IN VARCHAR2,
   UN_DATOS            IN CLOB,
   UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
   UN_SOBRESCRIBIR     IN VARCHAR2 --SI es sobrescribir
)
RETURN VARCHAR2
AS
  MI_RETORNO                    CLOB;
  MI_REGISTROS_TABLA            PCK_SYSMAN_UTL.T_SPLIT;
  MI_CAMPOS_TABLA               PCK_SYSMAN_UTL.T_SPLIT;
  MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
  MI_CANTIDAD                   NUMBER(3,0);
  MI_CANTIDADES                 NUMBER(8,0);
  MI_EXISTE                     NUMBER(10,0);
  MI_TOTAL                      NUMBER(10,0);
  MI_STR_SQL                    VARCHAR2(32000) := '';
  MI_ACCION                     NUMBER(1,0) := 0;--0 validar 1 validado
  MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
  MI_EXCLUIDOS                  VARCHAR2(32000);
  MI_SOBRESCRIBIR               BOOLEAN;
  MI_CONSECUTIVO                PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CRITERIO                   PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPO_CODIGO               VARCHAR2(50);

BEGIN
    MI_CANTIDAD := 0;
    MI_SOBRESCRIBIR := CASE UN_SOBRESCRIBIR 
                      WHEN 'NO' THEN NULL 
                      WHEN 'SI' THEN TRUE 
                   END;
                   
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA,MI_EXCLUIDOS);
      MI_CAMPOS := MI_CAMPOS || ', DATE_CREATED, CREATED_BY';


     MI_REGISTROS_TABLA := PCK_SYSMAN_UTL.FC_SPLIT_SYS( UN_LISTA        => UN_DATOS,
                                                     UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_REG);
                                                     
     FOR RS IN MI_REGISTROS_TABLA.FIRST..MI_REGISTROS_TABLA.LAST
        LOOP
            MI_CAMPOS_TABLA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_REGISTROS_TABLA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
    
            FOR REC IN (SELECT COLUMN_NAME, DATA_TYPE 
                        FROM USER_TAB_COLUMNS 
                        WHERE TABLE_NAME = UN_TABLA 
                        AND COLUMN_NAME NOT IN ('CONSECUTIVO','DATE_CREATED','CREATED_BY','DATE_MODIFIED', 'MODIFIED_BY')
                        ORDER BY COLUMN_ID) 
            LOOP
                MI_CANTIDAD := MI_CANTIDAD + 1;  
                IF REC.DATA_TYPE IN ('VARCHAR2', 'CHAR') THEN
                    MI_VALORES := MI_VALORES || ', ''' || MI_CAMPOS_TABLA(MI_CANTIDAD) || '''';
                ELSIF REC.DATA_TYPE IN ('NUMBER') THEN
                    MI_VALORES := MI_VALORES || ', ' || MI_CAMPOS_TABLA(MI_CANTIDAD);
                ELSIF REC.DATA_TYPE IN ('DATE', 'TIMESTAMP(6) WITH LOCAL TIME ZONE') THEN
                    MI_VALORES := MI_VALORES || ', TO_DATE(''' || MI_CAMPOS_TABLA(MI_CANTIDAD) || ''', ''DD-MM-YYYY'')';
                ELSE
                    RAISE_APPLICATION_ERROR(-20001, 'Tipo de dato no soportado: ' || REC.DATA_TYPE);
                END IF;
            END LOOP;
        
            MI_VALORES := SUBSTR(MI_VALORES, 2);
            
            IF MI_SOBRESCRIBIR IS NULL THEN
                  MI_STR_SQL := 'SELECT COUNT(*) FROM ' || UN_TABLA || ' WHERE ' ||
                  'ANO = ' || MI_CAMPOS_TABLA(2) || ' AND ' ||
                  'MES_INICIAL = ' || MI_CAMPOS_TABLA(3) || ' AND ' ||
                  'MES_FINAL = ' || MI_CAMPOS_TABLA(4);
        
              EXECUTE IMMEDIATE MI_STR_SQL INTO MI_CANTIDADES;
                
                  IF MI_CANTIDADES <> 0 THEN
                    MI_RETORNO := 'Ya se encuentra información del reporte VRep para el periodo ' || 
                      TRUNC(MI_CAMPOS_TABLA(2)) || ' de ' || 
                      TRUNC(MI_CAMPOS_TABLA(3)) || ' a ' || 
                      TRUNC(MI_CAMPOS_TABLA(4)) || '. Desea reescribir la información?.';            
                    RETURN MI_RETORNO;
                  ELSE
                  MI_SOBRESCRIBIR := FALSE;
                  END IF;
            END IF;   
          
            IF MI_SOBRESCRIBIR THEN
            MI_CONDICION := 'ANO     = '''|| MI_CAMPOS_TABLA(2) ||'''
                    AND MES_INICIAL = '''|| MI_CAMPOS_TABLA(3) ||'''
                    AND MES_FINAL =  '''|| MI_CAMPOS_TABLA(4) ||''' ';
            
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => UN_TABLA
                                                    ,UN_ACCION  => 'E'
                                                    ,UN_CONDICION => MI_CONDICION);
              MI_SOBRESCRIBIR := FALSE; -- Evita que vuelva a eliminar
            END IF;
            
             MI_VALORES := MI_VALORES || ', SYSDATE, ''' || UN_USUARIO || '''';
         
             IF INSTR(MI_CAMPOS, 'CONSECUTIVO') > 0 THEN
             
              MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA,'CONSECUTIVO');
              MI_CAMPO_CODIGO := PCK_SYSMAN_UTL.FC_LISTA_LLAVESTABLA('SIA_RELACION_INGRESOS', 'COMPANIA, ANO, MES_INICIAL, MES_FINAL, CONSECUTIVO','IC','TABLA','VISTA');
              
              MI_CRITERIO := 'ANO     = '''|| MI_CAMPOS_TABLA(2) ||'''
                    AND MES_INICIAL = '''|| MI_CAMPOS_TABLA(3) ||'''
                    AND MES_FINAL =  '''|| MI_CAMPOS_TABLA(4) ||''' ';
                    
              MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => UN_TABLA,
                                                                 UN_CRITERIO => MI_CRITERIO,
                                                                 UN_CAMPO => 'CONSECUTIVO');
                  
              MI_CAMPOS := MI_CAMPOS || ', DATE_CREATED, CREATED_BY, CONSECUTIVO';
              MI_VALORES := MI_VALORES || ', ' || MI_CONSECUTIVO || ' ';
        
             END IF;
            BEGIN
                 BEGIN    
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => UN_TABLA
                                                   ,UN_ACCION  => 'I'
                                                   ,UN_CAMPOS  => MI_CAMPOS
                                                   ,UN_VALORES => MI_VALORES);
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_SYSMANSIA;
                 END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSIA THEN
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                           UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSIA_INSERTDATOS);
            END;
            MI_VALORES := NULL;
            MI_CANTIDAD := 0;
        
      END LOOP; 
      RETURN MI_RETORNO;

END FC_SUBIR_CONSOLIDADO_SIA;

END PCK_SIA;