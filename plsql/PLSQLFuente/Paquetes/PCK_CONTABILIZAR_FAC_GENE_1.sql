create or replace PACKAGE BODY PCK_CONTABILIZAR_FAC_GENE AS

  
FUNCTION FC_CONCEPTOSINCONFIGURACION
    /*
      NAME              : FC_CONCEPTOSINCONFIGURACION --> EN ACCESS ConceptoSin_Click()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JORGE ALEXANDER ROMERO BENITEZ
      DATE MIGRADOR     : 15/01/2018
      TIME              : 16:50 PM
      SOURCE MODULE     : INTERFACES_PB_2017_11_02
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : FUNCION QUE GENERA CONCEPTOS SIN CONFIGURAR POR CENTRO DE COSTO
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO         => ANIO POR EL CUAL SE VA A FILTRAR LA INFORMACION.

      @NAME:  enviarConceptosSinConfiguracion
      @METHOD:  GET
    */
    (UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANIO                  IN PCK_SUBTIPOS.TI_ANIO
    )
    RETURN CLOB
    AS
    MI_RSCONCEPTOS            SYS_REFCURSOR;
    MI_RSCONCEPTOSSINCONF     SYS_REFCURSOR;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_RTA                    CLOB;   
    MI_CONTADOR               NUMBER(10,0);
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN

   MI_RTA:= '************************Conceptos Sin Configurar por Centro de Costo*****************************'||CHR(10)||CHR(13);
   MI_CONTADOR :=0;

      <<CONCEPTOS>>
    FOR MI_RSCONCEPTOS IN (
      SELECT SF_CONCEPTOFACTURACION.COMPANIA,
              UN_ANIO                           ANO,
              CENTRO_COSTO.CODIGO            CCOSTO,
              SF_CONCEPTOFACTURACION.CODIGO  CONCEPTO
      FROM SF_CONCEPTOFACTURACION
        INNER JOIN CENTRO_COSTO
          ON SF_CONCEPTOFACTURACION.COMPANIA = CENTRO_COSTO.COMPANIA
      WHERE CENTRO_COSTO.COMPANIA = UN_COMPANIA
      AND CENTRO_COSTO.ANO = UN_ANIO
      AND CENTRO_COSTO.MOVIMIENTO NOT IN  (0)
      AND CENTRO_COSTO.CODIGO NOT       IN
        ( SELECT DISTINCT CENTRO_COSTO
        FROM CUENTAS_CONCEPTOS_FACT_CNT
        WHERE ANO    = UN_ANIO
        AND CONCEPTO = SF_CONCEPTOFACTURACION.CODIGO
        )
      AND SF_CONCEPTOFACTURACION.CODIGO NOT IN
        (SELECT DISTINCT CONCEPTO
        FROM CUENTAS_CONCEPTOS_FACT_CNT
        WHERE ANO        = UN_ANIO
        AND CENTRO_COSTO = CENTRO_COSTO.CODIGO
        ))
        LOOP

        MI_CAMPOS := 'COMPANIA
                      ,ANO
                      ,CENTRO_COSTO
                      ,CONCEPTO'; 


        MI_VALORES:= ' '''||MI_RSCONCEPTOS.COMPANIA||'''
                        ,' ||MI_RSCONCEPTOS.ANO|| '
                        ,'''||MI_RSCONCEPTOS.CCOSTO||''' 
                        ,'''||MI_RSCONCEPTOS.CONCEPTO||'''';              

        BEGIN
         BEGIN

                         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CUENTAS_CONCEPTOS_FACT_CNT'
                                                                ,UN_ACCION  => 'I'
                                                                ,UN_CAMPOS  => MI_CAMPOS
                                                                ,UN_VALORES => MI_VALORES);  

                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                                                
         END;

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                        MI_MSGERROR(1).VALOR := MI_RSCONCEPTOS.CONCEPTO;
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                                      UN_EXC_COD =>SQLCODE
                                                      ,UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTCUENTACONCEPTO
                                                      ,UN_REEMPLAZOS  => MI_MSGERROR);


        END; 


        IF PCK_DATOS.GL_RTA NOT IN (0)THEN
        MI_CONTADOR := MI_CONTADOR +1;
        MI_RTA := MI_RTA || 
                  MI_CONTADOR|| ' - El Concepto: '||MI_RSCONCEPTOS.CONCEPTO||' Con centro de costo '||MI_RSCONCEPTOS.CCOSTO||' No existe en la configuración se insertara el registro.' ||CHR(10)||CHR(13);

        END IF;

        END LOOP CONCEPTOS;


  MI_CONTADOR := 0;
        <<CONCEPTOS_SIN_CONFIGURAR>>
    FOR MI_RSCONCEPTOSSINCONF IN (                  
                 SELECT CUENTAS_CONCEPTOS_FACT_CNT.COMPANIA,
                        CUENTAS_CONCEPTOS_FACT_CNT.ANO,
                        CUENTAS_CONCEPTOS_FACT_CNT.CONCEPTO,
                        CUENTAS_CONCEPTOS_FACT_CNT.CENTRO_COSTO AS CCOSTO,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_BASE_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_BASE_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_IVA_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_IVA_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_RTFTE_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_RTFTE_C,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_BASE_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_BASE_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_IVA_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_IVA_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_RTFTE_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_RTFTE_R,
                        CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_BASE_VA,
                        CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_IVA_VA,
                        CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_RTFT_VA
                 FROM CUENTAS_CONCEPTOS_FACT_CNT 
                 WHERE CUENTAS_CONCEPTOS_FACT_CNT.COMPANIA     = UN_COMPANIA
                   AND CUENTAS_CONCEPTOS_FACT_CNT.ANO              = UN_ANIO
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_BASE_C   IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_BASE_C  IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_IVA_C    IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_IVA_C   IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_RTFTE_C  IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_RTFTE_C IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_BASE_R   IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_BASE_R  IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_IVA_R    IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_IVA_R   IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.DEBITO_RTFTE_R  IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CREDITO_RTFTE_R IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_BASE_VA  IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_IVA_VA   IS NULL
                   AND CUENTAS_CONCEPTOS_FACT_CNT.CUENTA_RTFT_VA  IS NULL 
                  ORDER BY CUENTAS_CONCEPTOS_FACT_CNT.CONCEPTO
        )LOOP
        MI_CONTADOR:= MI_CONTADOR +  1;

        MI_RTA:= MI_RTA ||
                 MI_CONTADOR || ' El Concepto:'||MI_RSCONCEPTOSSINCONF.CONCEPTO||' Con centro de costo '|| MI_RSCONCEPTOSSINCONF.CCOSTO||' Se encuentra sin configuración.' ||CHR(10)||CHR(13);

        END LOOP CONCEPTOS_SIN_CONFIGURAR;

   MI_RTA:= MI_RTA ||'Fin del archivo' ;

  RETURN MI_RTA;

END FC_CONCEPTOSINCONFIGURACION;


END PCK_CONTABILIZAR_FAC_GENE;