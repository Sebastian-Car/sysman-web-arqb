create or replace PACKAGE BODY  PCK_NOMINA_COM9 AS 

--1

PROCEDURE PR_CALCULO_SINDICATOS
 /*
    NAME              : PR_CALCULO_SINDICATOS
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 04/04/2019
    TIME              : 16:00 PM
    MODIFIER          : JOSE PASCUAL GOMEZ
    DATE MODIFIED     : 23/04/2019
    DESCRIPTION       : FUNCION QUE CALCULA LOS VALORES DE SINDICATOS PARA UN EMPLEADO
                        Se ajusta para que tome el valor del concepto don la funciÃ³n FC_CN; por otro lado para que cuando
                        el tipo de descuento sea porcentual se divida en 100 
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ID_DE_EMPLEADO => CODIGO DEL EMPLEADO PARA EL QUE SE LE VA HACER EL CALCULO                
  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ID_DE_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)
AS 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO;   
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALOR            NUMBER(20,6); 
    MI_VALOR_BASE       NUMBER(20,2) := 0;
    MI_VALOR_CONCEPTO   NUMBER(20,2) := 0;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_STRSQL;
    MI_CRITERIO         PCK_SUBTIPOS.TI_STRSQL;
    MI_VALORAUX         NUMBER(20,6); 

BEGIN
    <<APORTE_POR_EMPLEADO>>
    FOR MI_RS IN (SELECT CLASEFONDOAPORTE.CLASE_ID_DE_FONDO,
                          CLASEFONDOAPORTE.CODIGO,
                          CLASEFONDOAPORTE.FORMA_DESCUENTO ,
                          CLASEFONDOAPORTE.CONCEPTO,
                          CLASEFONDOAPORTE.VALOR
                   FROM CLASEFONDOAPORTE
                   INNER JOIN PERSONAL_APORTE
                      ON PERSONAL_APORTE.COMPANIA          = CLASEFONDOAPORTE.COMPANIA
                     AND PERSONAL_APORTE.CLASE_ID_DE_FONDO = CLASEFONDOAPORTE.CLASE_ID_DE_FONDO
                     AND PERSONAL_APORTE.CODIGO_APORTE     = CLASEFONDOAPORTE.CODIGO
                   WHERE PERSONAL_APORTE.COMPANIA        = UN_COMPANIA
                     AND PERSONAL_APORTE.ID_DE_EMPLEADO  = UN_ID_DE_EMPLEADO
                     AND PCK_NOMINA.FC_CN(CLASEFONDOAPORTE.CONCEPTO) = 0
                     ORDER BY CLASEFONDOAPORTE.CONCEPTO)
    LOOP
        MI_VALOR_BASE := 0;
        MI_VALOR_CONCEPTO := 0;
        IF MI_RS.FORMA_DESCUENTO = 'F' THEN
            MI_VALOR_CONCEPTO := MI_RS.VALOR;    
        ELSE 
            <<CONCEPTOS_PORCENTUALES>>
            FOR MI_RS_CONCEPTO IN (SELECT CONCEPTOS_BASE_APORTE.CONCEPTO 
                                 FROM CONCEPTOS_BASE_APORTE
                                 INNER JOIN PERSONAL_APORTE
                                    ON PERSONAL_APORTE.COMPANIA          = CONCEPTOS_BASE_APORTE.COMPANIA
                                   AND PERSONAL_APORTE.CLASE_ID_DE_FONDO = CONCEPTOS_BASE_APORTE.CLASE_ID_DE_FONDO
                                   AND PERSONAL_APORTE.CODIGO_APORTE     = CONCEPTOS_BASE_APORTE.CODIGO                                
                                 INNER JOIN CONCEPTOS
                                    ON CONCEPTOS_BASE_APORTE.COMPANIA           = CONCEPTOS.COMPANIA
                                    AND CONCEPTOS_BASE_APORTE.CONCEPTO          = CONCEPTOS.ID_DE_CONCEPTO
                                 WHERE CONCEPTOS_BASE_APORTE.COMPANIA           = UN_COMPANIA
                                   AND CONCEPTOS_BASE_APORTE.CLASE_ID_DE_FONDO  = MI_RS.CLASE_ID_DE_FONDO
                                   AND CONCEPTOS_BASE_APORTE.CODIGO             = MI_RS.CODIGO
                                   AND PERSONAL_APORTE.ID_DE_EMPLEADO           = UN_ID_DE_EMPLEADO
                                 ORDER BY CONCEPTOS_BASE_APORTE.CONCEPTO
            )LOOP
			
			IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'LIQUIDAR SINDICATOS CON SUELDO DE ENCARGO', UN_MODULO => 6, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'NO') = 'SI' AND PCK_NOMINA.FC_CN(10) NOT IN(0) THEN
                MI_VALOR_BASE := MI_VALOR_BASE +  PCK_NOMINA.FC_CN(10);
			ELSE
				MI_VALOR_BASE := MI_VALOR_BASE +  PCK_NOMINA.FC_CN(MI_RS_CONCEPTO.CONCEPTO);
			END IF;
			
            END LOOP CONCEPTOS_PORCENTUALES;       
            MI_VALOR_CONCEPTO := MI_VALOR_BASE * (MI_RS.VALOR/100);       
        END IF;  
        
        -- TICKET 7729872 ECABRERA: DESCUENTO ANTICIPADO DE CUOTA PARA FONDOS CONFIGURABLES, EN PERIODO DE DISFRUTE
        --                          DE VACACIONES NO SE DESCUENTA
        MI_VALORAUX := MI_VALOR_CONCEPTO;
        IF ( PCK_PARST.FC_PAR('DESCUENTO ANTICIPADO AUTOMATICO POR VACACIONES','NO') = 'SI' AND PCK_NOMINA_COM2.FC_PERMITEDUPLICAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).COMPANIA,MI_RS.CONCEPTO) ) THEN
            IF ( PCK_NOMINA.FC_CN(403) <> 0 ) THEN
            	-- TICKET 7738816 ECABRERA: EN CASO DE NOVEDAD CN 439 SE RESPETA EL VALOR DE LA NOVEDAD
            	MI_VALORAUX := MI_VALORAUX * CASE WHEN PCK_NOMINA.FC_CN(439) = 0 THEN 2 ELSE PCK_NOMINA.FC_CN(439) END;
            	-- TICKET 7738816 FIN --
            ELSIF ( PCK_NOMINA.FC_CN(35) <> 0  ) THEN
                MI_VALORAUX := 0;
            END IF;
        END IF;
                                
        PCK_NOMINA.CN(MI_RS.CONCEPTO) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RS.CONCEPTO) + NVL(MI_VALORAUX,0),0);   
        -- TICKET 7729872 FIN --    
    END LOOP  APORTE_POR_EMPLEADO;  
END PR_CALCULO_SINDICATOS;

PROCEDURE PR_INCLUIREMPLEADOSPRIMAANUAL
    /*
    NAME              : PR_INCLUIREMPLEADOSPRIMAANUAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
    DATE MIGRADOR     : 05/04/2019
    TIME              : 12:00 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:incluirEmpleadoPrimaAnual
    @METHOD:  POST
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_RS           SYS_REFCURSOR;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_RANGOANT     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_LLAVEANT     PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_FECHA        VARCHAR2(10 CHAR);
BEGIN
    MI_FECHA := TO_CHAR('01/'|| PCK_SYSMAN_UTL.FC_STRZERO(UN_MES,2) ||'/'|| UN_ANO);
    <<DETALLESPRIMA>>
    FOR MI_RS IN (
        SELECT D.ANO, P.MES, P.PERIODO, D.ANORANGOPRIMAANUAL, P.CODIGO PAGOESPECIAL, D.CONSECUTIVO
        FROM DETALLEPAGOESPECIAL D INNER JOIN PAGOESPECIAL P
             ON D.COMPANIA =  P.COMPANIA
            AND D.CODIGOPAGOESP =  P.CODIGO
        WHERE P.COMPANIA = UN_COMPANIA
        AND P.PRIMAANUAL <> 0
        AND D.ANO = UN_ANO
        AND P.MES = UN_MES
        AND P.PERIODO = UN_PERIODO
        ORDER BY P.CODIGO , D.CONSECUTIVO ASC
    )
    LOOP

        BEGIN
            IF MI_RS.PAGOESPECIAL <> MI_LLAVEANT THEN
                BEGIN
                    MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                                  AND CODIGOPAGOESP = '|| MI_RS.PAGOESPECIAL ||'  ';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME
                        (UN_TABLA => 'PAGOESPECIAL_PERSONAL'
                        ,UN_ACCION => 'E'
                        ,UN_CONDICION => MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
                MI_RANGOANT := 0;
            END IF;

            MI_LLAVEANT :=  MI_RS.PAGOESPECIAL;

            MI_CAMPOS := ' COMPANIA
                          , CODIGOPAGOESP
                          , CODIGODETALLEPAGO
                          , ID_DE_EMPLEADO
                          , CREATED_BY
                          , DATE_CREATED
                      ';

            MI_VALORES := '
                SELECT P.COMPANIA, '|| MI_RS.PAGOESPECIAL ||' PAGOESPECIAL, '|| MI_RS.CONSECUTIVO ||' CODIGODETALLEPAGO, P.ID_DE_EMPLEADO
                       , '''|| UN_USUARIO ||''' CREATED_BY, SYSDATE DATE_CREATED
                FROM PERSONAL P
                WHERE P.COMPANIA = '''|| UN_COMPANIA ||'''
                  AND P.ESTADO_ACTUAL <> 3
                  AND ID_DE_EMPLEADO NOT IN(0)
                  AND P.FECHA_DE_RETIRO IS NULL
                  AND PCK_SYSMAN_UTL.FC_EDAD(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, ''DD/MM/YYYY''), ''DD/MM/YYYY''), TO_DATE('''||MI_FECHA||''', ''DD/MM/YYYY'') ,-1)  > '|| MI_RANGOANT ||'
                  AND PCK_SYSMAN_UTL.FC_EDAD(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, ''DD/MM/YYYY''), ''DD/MM/YYYY''), TO_DATE('''||MI_FECHA||''', ''DD/MM/YYYY'') ,-1)  <= '|| MI_RS.ANORANGOPRIMAANUAL ||'
                  ';

            MI_RANGOANT := MI_RS.ANORANGOPRIMAANUAL;

            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME
                    (UN_TABLA    => 'PAGOESPECIAL_PERSONAL'
                    ,UN_ACCION   => 'IS'
                    ,UN_CAMPOS   => MI_CAMPOS
                    ,UN_VALORES  => MI_VALORES );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_NOMINA THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            --Se presentÃ³ error al ingresar los empleados a los cuales se les aplica prima anual.
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_ERRORINSERTPAGOPEMPL);
        END;

    END LOOP DETALLESPRIMA;



END PR_INCLUIREMPLEADOSPRIMAANUAL;

PROCEDURE PR_INLUIRNOVPAGOESPECIAL
/*
NAME              : PR_INLUIRNOVPAGOESPECIAL
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
DATE MIGRADOR     : 09/04/2019
TIME              : 02:30 PM
SOURCE MODULE     :
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Carga el type PCK_NOMINA.CN con el valor de los pagos especiales configurados.
PARAMETERS        :

MODIFICATIONS     :

@NAME:incluirNovedadPagoEspecial
@METHOD:  POST
*/
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_RS   SYS_REFCURSOR;
BEGIN
    <<NOVPAGOSESPECIALES>>
    FOR MI_RS IN (
        WITH PAGOSAPLICAR AS
        (
            SELECT 1 ORDEN, COMPANIA, CODIGOPAGOESP, CODIGODETALLEPAGO
            FROM PAGOESPECIAL_CATEGORIA
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_CATEGORIA = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CATEGORIA
              AND ESCALAFON = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON
              AND ANO = UN_ANO

            UNION ALL
            SELECT 2 ORDEN, COMPANIA, CODIGOPAGOESP, CODIGODETALLEPAGO
            FROM PAGOESPECIAL_CARGOS
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_CARGO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO

            UNION ALL
            SELECT 3 ORDEN, COMPANIA, CODIGOPAGOESP, CODIGODETALLEPAGO
            FROM PAGOESPECIAL_PERSONAL
            WHERE COMPANIA =  UN_COMPANIA
              AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        )
        SELECT PA.ORDEN, PE.ID_DE_CONCEPTO, DP.VALOR
        FROM PAGOESPECIAL PE
            INNER JOIN DETALLEPAGOESPECIAL DP
                 ON PE.COMPANIA = DP.COMPANIA
                AND PE.CODIGO = DP.CODIGOPAGOESP
            INNER JOIN PAGOSAPLICAR PA
                 ON DP.COMPANIA = PA.COMPANIA
                AND DP.CODIGOPAGOESP = PA.CODIGOPAGOESP
                AND DP.CONSECUTIVO = PA.CODIGODETALLEPAGO
        WHERE PE.COMPANIA = UN_COMPANIA
          AND DP.ANO = UN_ANO
          AND (PE.MES = 0 OR PE.MES = UN_MES)
          AND PE.PERIODO = UN_PERIODO
        ORDER BY PA.ORDEN ASC
    )
    LOOP
        IF MI_RS.ID_DE_CONCEPTO > 0 AND MI_RS.ID_DE_CONCEPTO <= PCK_NOMINA.MAXI THEN
            PCK_NOMINA.CN(MI_RS.ID_DE_CONCEPTO) := MI_RS.VALOR;
            PCK_NOMINA.CNNOVEDAD(MI_RS.ID_DE_CONCEPTO) := MI_RS.VALOR;  --Para que respete el valor que se envia por pago especial.
        END IF;
    END LOOP NOVPAGOSESPECIALES;


END PR_INLUIRNOVPAGOESPECIAL;

PROCEDURE PR_PRIMANAVIDADBUCARAMANGA 
    /*
    NAME              : PR_PRIMANAVIDADBUCARAMANGA
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 29/04/2019
    TIME              : 10:06 AM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  PRIMANAVIDADBUCARAMANGA
    */
    (    
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    )
AS 
    MI_BASE                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORPRIMA           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;    
    MI_FECHAIPNFACTORES DATE;
    MI_FECHAFPNFACTORES DATE;
    MI_ANIOANTERIOR     NUMBER;
    MI_FECHAIPNDIAS     DATE;
    MI_FECHAFPNDIAS     DATE;    
	MI_CONCEPTOS        PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_ENCARGO_FECINICIO    DATE;
    MI_ENCARGO_FECFIN       DATE;
    MI_SUELDO               PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASP NUMBER;--(CFBARRERA_CC:5511_29-04-2025)

BEGIN    

    MI_ANIOANTERIOR := PCK_NOMINA.GL_SANO - 1;
	MI_FECHAIPNFACTORES := TO_DATE('01/12/' || MI_ANIOANTERIOR, 'DD/MM/YYYY');   
	PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > MI_FECHAIPNFACTORES THEN PCK_NOMINA.GL_FECHAI ELSE MI_FECHAIPNFACTORES END;
	MI_FECHAFPNFACTORES := TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');

    MI_FECHAIPNDIAS := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');   
	MI_FECHAIPNDIAS := CASE WHEN PCK_NOMINA.GL_FECHAI > MI_FECHAIPNDIAS THEN PCK_NOMINA.GL_FECHAI ELSE MI_FECHAIPNDIAS END;
	MI_FECHAFPNDIAS := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    IF PCK_NOMINA.FC_CN(404) <> 0 THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN        
            MI_FECHAFPNFACTORES := CASE WHEN PCK_NOMINA.GL_FECHAR IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO ELSE PCK_NOMINA.GL_FECHAR END  ;
            MI_FECHAFPNDIAS := CASE WHEN PCK_NOMINA.GL_FECHAR IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO ELSE PCK_NOMINA.GL_FECHAR END  ;	
        ELSE
            MI_FECHAFPNFACTORES :=  PCK_NOMINA.GL_FECHAR ;
            MI_FECHAFPNDIAS := PCK_NOMINA.GL_FECHAR  ;	
        END IF;
	END IF;	 

    --Obtiendo doceava de la Prima de Servicios						
    MI_CONCEPTOS(1).CLAVE := 'CODIGO CONCEPTO AJUSTES PRIMA SEMESTRAL';-- (CFBARRERA,FECHA:31/12/2024,TICKET:7802866,Se ajusta el cálculo de los conceptos para evaluar uno o más dentro del rango definido en las iteraciones)
    MI_CONCEPTOS(1).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA SEMESTRAL', '503')); 	
    
    IF PCK_NOMINA.FC_CN(404) <> 0 THEN  		
    PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_ACUMULARVALORCONCEPTO(UN_ANO1  => PCK_NOMINA.GL_SANO, 
                                                                                              UN_MES1  => PCK_NOMINA.GL_SMES, 
                                                                                              UN_PER1  => 1, 
                                                                                              UN_ANO2  => PCK_NOMINA.GL_SANO,
                                                                                              UN_MES2  => PCK_NOMINA.GL_SMES,
                                                                                              UN_PER2  => 99,
                                                                                              UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                                              UN_CONCEPTO => 160,
                                                                                              UN_CONCEPTO2 => MI_CONCEPTOS)
                                                                                              / 12 , 0);


      PCK_NOMINA.CN(931) := PCK_NOMINA.FC_CN(931) +PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.FC_CN(160)/12,0);-- CFBARRERA:CC 551 - Se ajusta la suma para incluir el concepto calculado y el último concepto pagado.

		ELSE
	    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
	    PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
    END IF;
    	
    --Obtiendo doceava de Bonificacion Anual por Servicios Prestados(BASP)
    MI_CONCEPTOS.DELETE;
    MI_CONCEPTOS(1).CLAVE := 'CODIGO CONCEPTO AJUSTES B.A.S.P.';
    MI_CONCEPTOS(1).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES B.A.S.P.', '514'));-- (CFBARRERA,FECHA:31/12/2024,TICKET:7802866,Se ajusta el cálculo de los conceptos para evaluar uno o más dentro del rango definido en las iteraciones)    
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_ACUMULARVALORCONCEPTO(UN_ANO1  => PCK_NOMINA.GL_SANO, 
                                                                                              UN_MES1  => PCK_NOMINA.GL_SMES, 
                                                                                              UN_PER1  => 1, 
                                                                                              UN_ANO2  => PCK_NOMINA.GL_SANO,
                                                                                              UN_MES2  => PCK_NOMINA.GL_SMES,
                                                                                              UN_PER2  => 99,
                                                                                              UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                                              UN_CONCEPTO => 150,
                                                                                              UN_CONCEPTO2 => MI_CONCEPTOS)
                                                                                              / 12 , 0);  


     PCK_NOMINA.CN(939) := PCK_NOMINA.FC_CN(939) +PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.FC_CN(150)/12,0);-- CFBARRERA:CC 551 - Se ajusta la suma para incluir el concepto calculado y el último concepto pagado.

    IF PCK_NOMINA.FC_CN(404) <> 0 THEN  -- (CFBARRERA_CC:5511_29-04-2025) Suma el valor del concepto 501 al valor del concepto 155, comenzando desde la última aparición encontrada del concepto 155 en adelante.
    --Obteniendo valor de la prima de vacaciones                                                                                             
    PCK_NOMINA.GL_PVAC := 0;
    MI_CONCEPTOS.DELETE;
    MI_CONCEPTOS(1).CLAVE := 'PRIMA DE VACACIONES';
    MI_CONCEPTOS(1).VALOR := 155;
    MI_CONCEPTOS(2).CLAVE := 'AJUSTES PRIMA DE VACACIONES';
    MI_CONCEPTOS(2).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA DE VACACIONES', '501'));         
    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTE(UN_ANO1  => PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPN), 
                                                                                              UN_MES1  => PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 
                                                                                              UN_PER1  => 1, 
                                                                                              UN_ANO2  => PCK_NOMINA.GL_SANO,
                                                                                              UN_MES2  => PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPNFACTORES),
                                                                                              UN_PER2  => PCK_NOMINA.GL_SPER,
                                                                                              UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                                              UN_CONCEPTOS => MI_CONCEPTOS)
                                                                                              / 12 , 0);                                                                              
                                                                                              
     PCK_NOMINA.CN(932) := PCK_NOMINA.FC_CN(932) +PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.FC_CN(155)/12,0);
    
     ELSE -- Toma únicamente el valor del concepto que se está calculando,sin sumar el valor correspondiente al concepto 501.
    --Obteniendo valor de la prima de vacaciones    
    PCK_NOMINA.GL_PVAC := 0;
    MI_CONCEPTOS.DELETE;
    MI_CONCEPTOS(1).CLAVE := 'AJUSTES PRIMA DE VACACIONES';
    MI_CONCEPTOS(1).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA DE VACACIONES', '501'));-- (CFBARRERA,FECHA:31/12/2024,TICKET:7802866,Se ajusta el cálculo de los conceptos para evaluar uno o más dentro del rango definido en las iteraciones)     
    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_ACUMULARVALORCONCEPTO(UN_ANO1  => PCK_NOMINA.GL_SANO, 
                                                                                              UN_MES1  => PCK_NOMINA.GL_SMES, 
                                                                                              UN_PER1  => 1, 
                                                                                              UN_ANO2  => PCK_NOMINA.GL_SANO,
                                                                                              UN_MES2  => PCK_NOMINA.GL_SMES,
                                                                                              UN_PER2  => 99,
                                                                                              UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                                              UN_CONCEPTO => 155,
                                                                                              UN_CONCEPTO2 => MI_CONCEPTOS)
                                                                                              / 12 , 0);
                                                                                              
     PCK_NOMINA.CN(932) := PCK_NOMINA.FC_CN(932) +PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.FC_CN(155)/12,0);-- CFBARRERA:CC 551 - Se ajusta la suma para incluir el concepto calculado y el último concepto pagado. 
     END IF;
     
    --Se tendrÃ¡ en cuenta la asignaciÃ³n bÃ¡sica que tenga el funcionario a 30 de noviembre, ya sea la del cargo titular o la del encargo
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 11, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNAN(10) <> 0 THEN --mod JM CC 3240 se cambia por cnan ya que no esta tomando el valor
        BEGIN
            SELECT FECHAINICIO, FECHAFINAL
            INTO MI_ENCARGO_FECINICIO, MI_ENCARGO_FECFIN                  
            FROM ENCARGOS 
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = PCK_NOMINA.GL_SANO
            AND ID_DE_PROCESO = 1
            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
            AND TO_DATE(TO_CHAR(FECHAINICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_FECHAFPNFACTORES AND TO_DATE(TO_CHAR(FECHAFINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_FECHAFPNFACTORES;

           MI_SUELDO := PCK_NOMINA.FC_CNAN(10);--mod JM CC 3240 se cambia por cnan ya que no esta tomando el valor
        EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_SUELDO := PCK_NOMINA.FC_CN(1);                
        END;
    ELSE
        MI_SUELDO := PCK_NOMINA.FC_CN(1);
    END IF;     

    --(APINEDA:03/12/2019)-Cambio por daÃ±os en retenciÃ³n, se elimina lÃ­nea que acumula conceptos de todo el aÃ±o y se cambia por ciclo
    FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP; 

    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPNDIAS, MI_FECHAFPNDIAS) - PCK_NOMINA.GL_DNT;	    

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA_CC:5511_29-04-2025)
    MI_BASP := CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) ELSE (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) END;--(CFBARRERA_CC:5511_29-04-2025_MI_BASP/12 := FC_CN:=932)
    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(MI_SUELDO + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA +(MI_BASP/12) + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(932), 0);
    PCK_NOMINA.GL_FACTORPN := MI_BASE;

    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR I IN 2 .. 599 LOOP
            IF ((I <> 125) AND (I <> 160) AND (I <> 67) AND (I <> 404)) AND (I < 599) OR (I >= 600 AND I <= 798) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                PCK_NOMINA.CN(I) := 0;
            END IF;
        END LOOP;
    END IF;      

    MI_VALORPRIMA := CASE WHEN PCK_NOMINA.FC_CN(158) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN / 360) * PCK_NOMINA.GL_DCC, 0) ELSE PCK_NOMINA.FC_CN(158) END;
    PCK_NOMINA.CN(158) := MI_VALORPRIMA; 
    --Guardando factores Prima de Navidad
    PCK_NOMINA.CN(930) := MI_SUELDO;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;    
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT;

END PR_PRIMANAVIDADBUCARAMANGA;

PROCEDURE PR_COPIARDATOSPERIODOCALCULADO
    /*
    NAME              : PR_COPIARDATOSPERIODOCALCULADO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 05/06/2019
    TIME              : 03:06 PM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Copia en tabla temporal los datos del periodo recibido por parÃ¡metro, para el cÃ¡lculo de retroactivo
    @NAME:  COPIARDATOSPERIODOCALCULADO
    */
(
UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
UN_PROCESO      IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_EMP_INICIAL  IN VARCHAR2,
UN_EMP_FINAL    IN VARCHAR2,
UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
UN_MES          IN PCK_SUBTIPOS.TI_MES,
UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI
)
AS 
MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    PCK_NOMINA.MI_BASESNOVEDADES.DELETE;
    PCK_NOMINA.MI_PERSONAL_HISTORICO.DELETE;
    PCK_NOMINA.MI_RETEFUENTE_CALCULOS.DELETE;
    --(APINEDA:31/07/2019)-Se compara el PERIODO con el valor 3 por defecto, debido a que en la tabla BASESNOVEDADES siempre se guardan los registros con periodo 3 a pesar de que los valores pueden corresponder a varios periodos acumulados.

   IF NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'NOMINA MENSUAL', UN_MODULO => PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'SI') = 'NO' 
       AND PCK_NOMINA.GL_SPER IN (1, 2) AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN--(CC:3708_CFBARRERA_INI_Nomina quincenal: toma el periodo actual (1 o 2))
        SELECT *
        BULK COLLECT INTO PCK_NOMINA.MI_BASESNOVEDADES
        FROM BASESNOVEDADES
        WHERE COMPANIA         = UN_COMPANIA
          AND ANO              = UN_ANO    
          AND MES              = UN_MES    
          AND PERIODO          = 2
          AND ID_DE_EMPLEADO BETWEEN UN_EMP_INICIAL AND UN_EMP_FINAL
        ORDER BY ID_DE_EMPLEADO;
    ELSE
        -- Nómina mensual: siempre toma periodo 3
        SELECT *
        BULK COLLECT INTO PCK_NOMINA.MI_BASESNOVEDADES
        FROM BASESNOVEDADES
        WHERE COMPANIA         = UN_COMPANIA
          AND ANO              = UN_ANO    
          AND MES              = UN_MES    
          AND PERIODO          = 3
          AND ID_DE_EMPLEADO BETWEEN UN_EMP_INICIAL AND UN_EMP_FINAL
        ORDER BY ID_DE_EMPLEADO;
    END IF;--(CC:3708_CFBARRERA_FIN)

    SELECT *
    BULK COLLECT INTO PCK_NOMINA.MI_RETEFUENTE_CALCULOS
    FROM RETEFUENTE_CALCULOS
    WHERE COMPANIA = UN_COMPANIA
      AND ID_DE_PROCESO = UN_PROCESO
      AND ANO = UN_ANO    
      AND MES = UN_MES    
      AND PERIODO = UN_PERIODO
      AND ID_DE_EMPLEADO BETWEEN UN_EMP_INICIAL AND UN_EMP_FINAL
    ORDER BY ID_DE_EMPLEADO;       

    BEGIN
        IF PCK_NOMINA.MI_BASESNOVEDADES.COUNT = 0 THEN 
            MI_MSGERROR(1).CLAVE := 'TABLA'; 
            MI_MSGERROR(1).VALOR := 'BASESNOVEDADES';             
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;                        
        END IF;     

        IF PCK_NOMINA.MI_RETEFUENTE_CALCULOS.COUNT = 0 THEN 
            MI_MSGERROR(1).CLAVE := 'TABLA'; 
            MI_MSGERROR(1).VALOR := 'RETEFUENTE_CALCULOS';             
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;                        
        END IF;         
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(2).CLAVE := 'PERIODO';
            MI_MSGERROR(2).VALOR := UN_PERIODO;                                  
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_SINDATOSTABLAPERIODO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );             
    END; 
END PR_COPIARDATOSPERIODOCALCULADO;

PROCEDURE PR_CREARPERIODORETROACTIVO 
/*
    NAME              : CREARPERIODO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 30/05/2019
    TIME              : 12:25 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Crear periodo de nÃ³mina retroactivo                    
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESORETROACTIVO   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                  IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODORETROACTIVO   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USER                 IN PCK_SUBTIPOS.TI_USUARIO         DEFAULT PCK_CONEXION.FC_GETUSER()  
  )
  AS
    MI_EXISTE           PCK_SUBTIPOS.TI_LOGICO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_ERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_ACUMULADO        NUMBER DEFAULT 0;
    MI_NOMBREPERIODO    VARCHAR2(12) DEFAULT ' ';
    MI_DIFERENCIASRETROACTIVO NUMBER DEFAULT 0;
BEGIN
  BEGIN
    SELECT  DISTINCT 1
      INTO  MI_EXISTE
    FROM    PERIODOS
    WHERE   COMPANIA      = UN_COMPANIA
      AND   ID_DE_PROCESO = UN_PROCESORETROACTIVO
      AND   ANO           = UN_ANIO
      AND   MES           = UN_MES
      AND   PERIODO       = UN_PERIODORETROACTIVO;
      EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_EXISTE := 0;
  END ;
  --(APINEDA:31/07/2019)-Se agrega rango de periodos del 30 a 39 para indicar que corresponden a las diferencias de retroactivo y que serÃ¡n acumulados.
    IF UN_PERIODORETROACTIVO BETWEEN 30 AND 39 THEN
        MI_ACUMULADO := -1;
        MI_NOMBREPERIODO := 'Diferencias ';
        MI_DIFERENCIASRETROACTIVO := -1;
    ELSE        
        MI_ACUMULADO := 0;
        MI_NOMBREPERIODO := 'Retroactivo ';    
    END IF;
    BEGIN
      IF MI_EXISTE = 0 THEN
            --(APINEDA:07/10/2019)-Se agrega indicador DIFERENCIASRETROACTIVO
            MI_CAMPOS := 'COMPANIA
                         ,ID_DE_PROCESO
                         ,ANO
                         ,MES
                         ,PERIODO
                         ,NOM_PERIODO
                         ,ESTADO
                         ,ACUMULADO
                         ,DIAS
                         ,DIFERIDOS
                         ,OBSERVACION
                         ,CREATED_BY
                         ,FECHAINICIO
                         ,DATE_CREATED
                         ,FECHAFINAL
                         ,DIFERENCIASRETROACTIVO';  

          MI_VALORES := 'SELECT COMPANIA
                             ,'||UN_PROCESORETROACTIVO||'
                             ,ANO
                             ,MES
                             ,'||UN_PERIODORETROACTIVO||'
                             ,SUBSTR( ''' || MI_NOMBREPERIODO || ''' || REPLACE(UPPER(NOM_PERIODO),''NOMINA '',''''), 1, 20)
                             ,-1
                             ,' || MI_ACUMULADO || '
                             ,DIAS
                             ,0
                             ,''Periodo creado para guardar nÃ³mina con '|| MI_NOMBREPERIODO || '''
                             ,'''||UN_USER||'''
                             ,FECHAINICIO
                             ,SYSDATE
                             ,FECHAFINAL
                             ,' || MI_DIFERENCIASRETROACTIVO ||'
                          FROM PERIODOS
                          WHERE COMPANIA        = '''||UN_COMPANIA||'''
                            AND ID_DE_PROCESO   = '  ||PCK_NOMINA.GL_PROCESOACTUAL||'
                            AND ANO             = '  ||UN_ANIO||' 
                            AND MES             = '  ||UN_MES||' 
                            AND PERIODO         = '  ||PCK_NOMINA.GL_SPER||' ';

            BEGIN
             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'PERIODOS'
                                                    ,UN_ACCION  => 'IS'
                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                    ,UN_VALORES => MI_VALORES);    
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;        
            END;    
      ELSE
        MI_CAMPOS := 'ESTADO = -1, ACUMULADO =' || MI_ACUMULADO || ', DIFERIDOS = 0, PRELIMINAR = 0, AJUSTES = 0, DIFERENCIASRETROACTIVO =' || MI_DIFERENCIASRETROACTIVO;

        MI_CAMPOS := MI_CAMPOS || ', MODIFIED_BY =''' || UN_USER || ''', DATE_MODIFIED = SYSDATE ';
             BEGIN
                 MI_CONDICION := ' COMPANIA            = '''|| UN_COMPANIA||
                                 ''' AND ID_DE_PROCESO = '  || UN_PROCESORETROACTIVO ||
                                 '   AND ANO           = '  || UN_ANIO    ||
                                 '   AND MES           = '  || UN_MES     ||
                                 '   AND PERIODO       = '  || UN_PERIODORETROACTIVO;
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('PERIODOS', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION );   

                 EXCEPTION
                      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_NOMINA;
             END;
      END IF;
     EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
               MI_ERROR(1).CLAVE := 'TABLA';
               MI_ERROR(1).VALOR := 'PERIODOS';
               MI_ERROR(2).CLAVE := 'PARAMETRO';
               MI_ERROR(2).VALOR := UN_COMPANIA||','||
                                    UN_PROCESORETROACTIVO||','||
                                    UN_ANIO||','||
                                    UN_MES||','||
                                    UN_PERIODORETROACTIVO;
               MI_ERROR(3).CLAVE := 'ANO';
               MI_ERROR(3).VALOR := UN_ANIO;

               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_TABLAERROR => 'PERIODOS',
                                          UN_ERROR_COD  => PCK_ERRORES.ERRRR_ACT_PERIODOACUMULABLE,
                                          UN_REEMPLAZOS => MI_ERROR);      

    END;  
END PR_CREARPERIODORETROACTIVO;

PROCEDURE PR_GUARDARDATOSRETROACTIVO 
/*
    NAME              : PR_GUARDARDATOSRETROACTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 07/06/2019
    TIME              : 08:44 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Traslada los datos del periodo calculado a periodo y proceso retroactivo
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESORETROACTIVO   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODORETROACTIVO   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMP_INICIAL          IN VARCHAR2,
    UN_EMP_FINAL            IN VARCHAR2,    
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                  IN PCK_SUBTIPOS.TI_MES 
  )
AS
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_ERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_OBS        VARCHAR2(255);--(3708: lleva la observacion del peirodo)
    MI_USER       VARCHAR2(32);--(3708: lleva el usuario) 
    MI_EXISTE     PCK_SUBTIPOS.TI_LOGICO;
BEGIN
    BEGIN 

    --(CC:3708_CFBARRERA_INI:Crea el periodo 53 para primera y segunda quincena retroactivo)
    IF NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'NOMINA MENSUAL', UN_MODULO => PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'SI') = 'NO' 
           AND PCK_NOMINA.GL_SPER IN (1, 2) AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN

           MI_OBS  := '';
     MI_USER := PCK_CONEXION.FC_GETUSER;    
    
            SELECT COUNT(0)
            INTO   MI_EXISTE
            FROM   PERIODOS
            WHERE  COMPANIA      = PCK_NOMINA.GL_COMPANIA
              AND  ID_DE_PROCESO = PCK_NOMINA.GL_PROCESOACTUAL
              AND  ANO           = PCK_NOMINA.GL_ANOACTUAL  
              AND  MES           = PCK_NOMINA.GL_SMES
              AND  PERIODO       = 33;

           --Crea el periodo 33 y valida que no exista antes
           IF MI_EXISTE = 0 THEN

                MI_CAMPOS := 'COMPANIA
                             ,ID_DE_PROCESO
                             ,ANO
                             ,MES
                             ,PERIODO
                             ,NOM_PERIODO
                             ,ESTADO
                             ,ACUMULADO
                             ,DIAS
                             ,DIFERIDOS
                             ,HIST_FONDOS
                             ,OBSERVACION
                             ,VALORDTF
                             ,CREATED_BY
                             ,MODIFIED_BY
                             ,FECHAINICIO
                             ,DATE_MODIFIED
                             ,DATE_CREATED
                             ,FECHAFINAL
                             ,PRELIMINAR
                             ,AJUSTES
                             ,DIFERENCIASRETROACTIVO
                             ,ID';

                MI_VALORES := ''''
                           || UN_COMPANIA                             -- COMPANIA
                           || ''','
                           || PCK_NOMINA.GL_PROCESOACTUAL             -- ID_DE_PROCESO
                           || ','
                           || PCK_NOMINA.GL_ANOACTUAL                 -- ANO
                           || ','
                           || PCK_NOMINA.GL_SMES                      -- MES
                           || ','
                           || 33                                     -- PERIODO
                           || ',''Diferencias RETRO'''               -- NOM_PERIODO
                           || ',-1'                                   -- ESTADO
                           || ',-1'                                   -- ACUMULADO
                           || ',30'                                   -- DIAS
                           || ',0'                                    -- DIFERIDOS
                           || ',NULL'                                 -- HIST_FONDOS
                           || ',''' || MI_OBS || ''''                 -- OBSERVACION
                           || ',0'                                    -- VALORDTF
                           || ',''' || MI_USER || ''''                -- CREATED_BY
                           || ',''' || MI_USER || ''''                -- MODIFIED_BY
                           || ',TO_TIMESTAMP(''01/'                   -- FECHAINICIO
                               || LPAD(PCK_NOMINA.GL_SMES, 2, '0')
                               || '/'
                               || PCK_NOMINA.GL_ANOACTUAL
                               || ''',''DD/MM/YYYY'')'
                           || ',SYSTIMESTAMP'                         -- DATE_MODIFIED
                           || ',SYSTIMESTAMP'                         -- DATE_CREATED
                           || ',LAST_DAY(TO_TIMESTAMP(''01/'          -- FECHAFINAL
                               || LPAD(PCK_NOMINA.GL_SMES, 2, '0')
                               || '/'
                               || PCK_NOMINA.GL_ANOACTUAL
                               || ''',''DD/MM/YYYY''))'
                           || ',0'                                    -- PRELIMINAR
                           || ',0'                                    -- AJUSTES
                           || ',-1'                                   -- DIFERENCIASRETROACTIVO
                           || ',NULL';                                -- ID

                BEGIN
                    MI_TABLA := 'PERIODOS';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                             UN_TABLA   => MI_TABLA
                                            ,UN_ACCION  => 'I'
                                            ,UN_CAMPOS  => MI_CAMPOS
                                            ,UN_VALORES => MI_VALORES);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;

            END IF;   
        
            MI_CAMPOS    := 'PERIODO = 53, ID_DE_PROCESO = ' || UN_PROCESORETROACTIVO;   
            MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA ||
                            ''' AND ID_DE_PROCESO = ' || PCK_NOMINA.GL_PROCESOACTUAL ||
                            '   AND ANO           = ' || UN_ANIO    ||
                            '   AND MES           = ' || UN_MES     ||
                            '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || 
                            ' AND ' || UN_EMP_FINAL ||
                            '   AND PERIODO       = ' || 2;
            BEGIN         
                MI_TABLA := 'BASESNOVEDADES';                         
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(MI_TABLA, 'M', MI_CAMPOS, 
                                                       NULL, NULL, MI_CONDICION);   
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
            
        ELSE
        
        --(APINEDA:31/07/2019)-Para la tabla BASESNOVEDADES se filtran registros por defecto con periodo 3, para actualizarlos a periodo 53 (NÃ³mina retroactivo).
        MI_CAMPOS := 'PERIODO =' || 53 || ', ID_DE_PROCESO =' || UN_PROCESORETROACTIVO;    
        MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                         ''' AND ID_DE_PROCESO = '  || PCK_NOMINA.GL_PROCESOACTUAL ||
                         '   AND ANO           = '  || UN_ANIO    ||
                         '   AND MES           = '  || UN_MES     ||
                         '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || ' AND ' || UN_EMP_FINAL ||
                         '   AND PERIODO       = '  || 3;
        BEGIN         
         MI_TABLA := 'BASESNOVEDADES';                         
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (MI_TABLA, 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);   
         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
  END IF;--(CC:3708_CFBARRERA_FIN)

		--(APINEDA:31/07/2019)-Para la tabla BASESNOVEDADES se filtran registros por defecto con periodo 3, para actualizarlos a periodo 53 (NÃ³mina retroactivo).
        MI_CAMPOS := 'PERIODO =' || 53 || ', ID_DE_PROCESO =' || UN_PROCESORETROACTIVO;    
        MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                         ''' AND ID_DE_PROCESO = '  || PCK_NOMINA.GL_PROCESOACTUAL ||
                         '   AND ANO           = '  || UN_ANIO    ||
                         '   AND MES           = '  || UN_MES     ||
                         '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || ' AND ' || UN_EMP_FINAL ||
                         '   AND PERIODO       = '  || 3;
        BEGIN         
         MI_TABLA := 'BASESNOVEDADES';                         
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (MI_TABLA, 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);   
         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
        --(APINEDA:31/07/2019)-Para las demÃ¡s tablas se continÃºa teniendo en cuenta el periodo que se estÃ¡ calculando.
        MI_CAMPOS := 'PERIODO =' || UN_PERIODORETROACTIVO || ', ID_DE_PROCESO =' || UN_PROCESORETROACTIVO;    
        MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                         ''' AND ID_DE_PROCESO = '  || PCK_NOMINA.GL_PROCESOACTUAL ||
                         '   AND ANO           = '  || UN_ANIO    ||
                         '   AND MES           = '  || UN_MES     ||
                         '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || ' AND ' || UN_EMP_FINAL ||
                         '   AND PERIODO       = '  || PCK_NOMINA.GL_SPER;
        BEGIN  
         MI_TABLA := 'RETEFUENTE_CALCULOS';
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (MI_TABLA, 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);       
         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;      
     EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
               MI_ERROR(1).CLAVE := 'TABLA';
               MI_ERROR(1).VALOR := MI_TABLA;
               MI_ERROR(2).CLAVE := 'PROCESO';
               MI_ERROR(2).VALOR := UN_PROCESORETROACTIVO;
               MI_ERROR(3).CLAVE := 'ANO';
               MI_ERROR(3).VALOR := UN_ANIO;
               MI_ERROR(4).CLAVE := 'PERIODODESTINO';
               MI_ERROR(4).VALOR := UN_PERIODORETROACTIVO;               
               MI_ERROR(5).CLAVE := 'MES';
               MI_ERROR(5).VALOR := UN_MES;   
               MI_ERROR(6).CLAVE := 'PERIODOORIGEN';
               MI_ERROR(6).VALOR := PCK_NOMINA.GL_SPER;                                 
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_TABLAERROR => MI_TABLA,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_ERRORALCAMBIARPERIODO,
                                          UN_REEMPLAZOS => MI_ERROR);            
    END;     
END PR_GUARDARDATOSRETROACTIVO;

PROCEDURE PR_REESTABLECERDATOSPERIODO
    /*
    NAME              : PR_REESTABLECERDATOSPERIODO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 07/06/2019
    TIME              : 10:07 AM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se restauran datos originales del periodo, almacenados al inicio del cÃ¡lculo en tablas temporales.
    @NAME:  REESTABLECERDATOSPERIODO
    */
AS 
    MI_REGBASESNOVEDADES         BASESNOVEDADES%ROWTYPE;
    MI_REGRETEFUENTE_CALCULOS    RETEFUENTE_CALCULOS%ROWTYPE;
BEGIN
    BEGIN
        <<I_BASES>>
        IF (PCK_NOMINA.MI_BASESNOVEDADES.COUNT > 0) THEN 
            FOR I_BASES IN PCK_NOMINA.MI_BASESNOVEDADES.FIRST .. PCK_NOMINA.MI_BASESNOVEDADES.LAST LOOP
                MI_REGBASESNOVEDADES := PCK_NOMINA.MI_BASESNOVEDADES(I_BASES);
                INSERT INTO BASESNOVEDADES VALUES MI_REGBASESNOVEDADES;        
            END LOOP I_BASES;
        END IF;
        <<I_RETEFUENTE>>
        IF (PCK_NOMINA.MI_RETEFUENTE_CALCULOS.COUNT > 0) THEN 
            FOR I_RETEFUENTE IN PCK_NOMINA.MI_RETEFUENTE_CALCULOS.FIRST .. PCK_NOMINA.MI_RETEFUENTE_CALCULOS.LAST LOOP
                MI_REGRETEFUENTE_CALCULOS := PCK_NOMINA.MI_RETEFUENTE_CALCULOS(I_RETEFUENTE);
                INSERT INTO RETEFUENTE_CALCULOS VALUES MI_REGRETEFUENTE_CALCULOS;        
            END LOOP I_RETEFUENTE;
        END IF;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ERR_RESTAURANDODATOSORIGINALES);
    END;
END PR_REESTABLECERDATOSPERIODO;

PROCEDURE PR_CALCDIFERENCIASRETROACTIVO
    /*
    NAME              : FC_CALCDIFERENCIASRETROACTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 10/06/2019
    TIME              : 10:00 AM
    SOURCE MODULE     : Nueva
    MODIFIER          :
    DATE MODIFIED     : 07/08/2019
    TIME              : 04:00 PM
    DESCRIPTION       : Calcula las diferencias entre el periodo de nÃ³mina retroactivo y la nÃ³mina normal, y guarda estos valores en el periodo 5.
                        Se ajusta para que se tomen todos los conceptos originales
    */
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO            IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO            IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMPLEADO           IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_PROCESORETROACTIVO IN PCK_SUBTIPOS.TI_ID_DE_PROCESO DEFAULT 10,
    UN_PERIODORETROACTIVO IN PCK_SUBTIPOS.TI_PERIODO_NOMI  DEFAULT PCK_NOMINA.GL_PERIODORETROACTIVO,
    UN_PERIODODIFERENCIAS IN PCK_SUBTIPOS.TI_PERIODO_NOMI  DEFAULT PCK_NOMINA.GL_PERIODODIFERENCIASRETRO,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO       DEFAULT PCK_CONEXION.FC_GETUSER()      
)
AS
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;    
    MI_RTA              VARCHAR2(32000);
    MI_NETO             PCK_SUBTIPOS.TI_DOBLE;
    MI_SUELDO_VAC       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3876
    MI_SUELDO_VAC_ANT   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3876
    MI_DIFERENCIA_VAC   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3876
    MI_DIFERENCIA_BER   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3876
    MI_DIFERENCIA_PVAC  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3876
    MI_AUX              BOOLEAN DEFAULT FALSE; --JM CC3876
    MI_BASE_SS          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
    MI_BASE_SS_ANT      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
    MI_SALUDE_SS        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
    MI_SALUDP_SS        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
    MI_PENSIONE_SS      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
    MI_PENSIONP_SS      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC3932
BEGIN
    BEGIN
        MI_CAMPOS := 'COMPANIA, 
                      ID_DE_PROCESO, 
                      ANO, 
                      MES, 
                      PERIODO, 
                      ID_DE_EMPLEADO, 
                      ID_DE_CONCEPTO, 
                      VALOR, 
                      FECHA,
                      CREATED_BY';  
         IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO'--(CC:3708_CFBARRERA_INI: Inserta las diferencias de las quincenas completas con las quicenas del retro y lo guarda en el periodo 33)
        AND PCK_NOMINA.GL_SPER = 2 AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN      
        
                  MI_CONDICION := ' COMPANIA = '''      || UN_COMPANIA || '''' || CHR(10) || CHR(13) || 
                                ' AND ID_DE_PROCESO = 1'                     || CHR(10) || CHR(13) || 
                                ' AND ANO = '         || UN_ANO              || CHR(10) || CHR(13) || 
                                ' AND MES = '         || UN_MES              || CHR(10) || CHR(13) || 
                                ' AND PERIODO = 33'                          || CHR(10) || CHR(13) || 
                                ' AND ID_DE_EMPLEADO = ' || UN_EMPLEADO;     
                
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
                           
                MI_VALORES := ' SELECT CASE WHEN NORMAL.COMPANIA       IS NULL THEN RETRO.COMPANIA       ELSE NORMAL.COMPANIA       END COMPANIA,
                                       ' || UN_PROCESO || ' ID_DE_PROCESO, 
                                       CASE WHEN NORMAL.ANO            IS NULL THEN RETRO.ANO            ELSE NORMAL.ANO            END ANO,
                                       CASE WHEN NORMAL.MES            IS NULL THEN RETRO.MES            ELSE NORMAL.MES            END MES,
                                       ' || CASE WHEN UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO'
                                                     AND PCK_NOMINA.GL_SPER = 2
                                                    THEN UN_PERIODODIFERENCIAS + 1
                                                    ELSE UN_PERIODODIFERENCIAS
                                               END || ' PERIODO,
                                       CASE WHEN NORMAL.ID_DE_EMPLEADO IS NULL THEN RETRO.ID_DE_EMPLEADO ELSE NORMAL.ID_DE_EMPLEADO      END ID_DE_EMPLEADO,
                                       CASE WHEN NORMAL.C_RETRO        IS NULL THEN TO_NUMBER(NVL(NORMAL.ID_DE_CONCEPTO, RETRO.ID_DE_CONCEPTO)) ELSE TO_NUMBER(NORMAL.C_RETRO) END ID_DE_CONCEPTO,
                                       SUM(NVL(RETRO.VALOR,0)-NVL(NORMAL.VALOR,0)) DIFERENCIA,
                                       SYSDATE,
                                       ''' || UN_USUARIO || '''
                                FROM
                                    (
                                        SELECT HISTORICOS.COMPANIA, HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.ID_DE_EMPLEADO, HISTORICOS.ID_DE_CONCEPTO,
                                               CONCEPTOS.C_RETRO,
                                               SUM(VALOR) AS VALOR  
                                        FROM HISTORICOS INNER JOIN CONCEPTOS
                                          ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                                         AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                        WHERE HISTORICOS.COMPANIA       = ''' || UN_COMPANIA || '''
                                          AND HISTORICOS.ID_DE_PROCESO  = ' || UN_PROCESO || '
                                          AND HISTORICOS.ANO            = ' || UN_ANO || '
                                          AND HISTORICOS.MES            = ' || UN_MES || '
                                          AND HISTORICOS.PERIODO        IN (1, 2)
                                          AND HISTORICOS.ID_DE_EMPLEADO = ' || UN_EMPLEADO || '
                                          AND HISTORICOS.ID_DE_CONCEPTO NOT BETWEEN 490 AND 499
                                           '  || CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CALCULAR RETENCION EN RETROACTIVO', UN_MODULO => 6, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'NO') = 'NO' THEN '' ELSE 'AND HISTORICOS.ID_DE_CONCEPTO NOT IN (303)' END  ||' 
                                          AND HISTORICOS.ID_DE_CONCEPTO < 1000                                
                                        GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.ID_DE_EMPLEADO, HISTORICOS.ID_DE_CONCEPTO,
                                                 CONCEPTOS.C_RETRO
                                    ) NORMAL 
                                FULL OUTER JOIN 
                                    (
                                        SELECT COMPANIA, ANO, MES,  ID_DE_EMPLEADO, ID_DE_CONCEPTO,
                                               SUM(VALOR) AS VALOR      
                                        FROM HISTORICOS
                                        WHERE COMPANIA       = ''' || UN_COMPANIA || '''
                                          AND ID_DE_PROCESO  = ' || UN_PROCESORETROACTIVO || '
                                          AND ANO            = ' || UN_ANO || '
                                          AND MES            = ' || UN_MES || '
                                          AND PERIODO        IN (51, 52)
                                          AND ID_DE_EMPLEADO = ' || UN_EMPLEADO || '
                                        GROUP BY COMPANIA, ANO, MES,  ID_DE_EMPLEADO, ID_DE_CONCEPTO
                                    )  RETRO 
                                     ON NORMAL.COMPANIA       = RETRO.COMPANIA
                                    AND NORMAL.ANO            = RETRO.ANO
                                    AND NORMAL.MES            = RETRO.MES
                                    AND NORMAL.ID_DE_EMPLEADO = RETRO.ID_DE_EMPLEADO
                                    AND NORMAL.ID_DE_CONCEPTO = RETRO.ID_DE_CONCEPTO
                                GROUP BY CASE WHEN NORMAL.COMPANIA       IS NULL THEN RETRO.COMPANIA       ELSE NORMAL.COMPANIA       END,
                                         CASE WHEN NORMAL.ANO            IS NULL THEN RETRO.ANO            ELSE NORMAL.ANO            END,
                                         CASE WHEN NORMAL.MES            IS NULL THEN RETRO.MES            ELSE NORMAL.MES            END,
                                         CASE WHEN NORMAL.ID_DE_EMPLEADO IS NULL THEN RETRO.ID_DE_EMPLEADO ELSE NORMAL.ID_DE_EMPLEADO END,
                                         CASE WHEN NORMAL.C_RETRO        IS NULL THEN TO_NUMBER(NVL(NORMAL.ID_DE_CONCEPTO, RETRO.ID_DE_CONCEPTO)) ELSE TO_NUMBER(NORMAL.C_RETRO) END
                                HAVING SUM(NVL(RETRO.VALOR,0)-NVL(NORMAL.VALOR,0)) <>0';
            
            BEGIN
                MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                           ,UN_ACCION  => 'IS'
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;        
            END;
    ELSE
        IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO'
            AND PCK_NOMINA.GL_SPER = 1 AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN 
            RETURN;
        END IF;--(CC:3708_CFBARRERA_FIN)

        --(APINEDA:31/07/2019)-Se cambia periodo 5 por UN_PERIODODIFERENCIAS para el registro de diferencias sobre la tabla HISTORICOS en el cÃ¡lculo de nÃ³mina retroactivo.
        --(JGOMEZ:07/08/2019) Para incluir todas las diferencias pues no se incluian las que estan en el periodo normal y no en el periodo retroactivo acumulado
		--(APINEDA:07/10/2019)-Se excluyen conceptos de beneficios y provisiones TAR1000094968 retroactivo IDSN
        -- MOD JM 23-04-2025 CC 1402 para que no reste el CN 303 cuando el parametro ( "CALCULAR RETENCION EN RETROACTIVO" este en SI)
        MI_VALORES := ' SELECT CASE WHEN NORMAL.COMPANIA       IS NULL THEN RETRO.COMPANIA       ELSE NORMAL.COMPANIA       END COMPANIA,
                               ' || UN_PROCESO || ' ID_DE_PROCESO, 
                               CASE WHEN NORMAL.ANO            IS NULL THEN RETRO.ANO            ELSE NORMAL.ANO            END ANO,
                               CASE WHEN NORMAL.MES            IS NULL THEN RETRO.MES            ELSE NORMAL.MES            END MES,
                               '|| UN_PERIODODIFERENCIAS ||' PERIODO,
                               CASE WHEN NORMAL.ID_DE_EMPLEADO IS NULL THEN RETRO.ID_DE_EMPLEADO ELSE NORMAL.ID_DE_EMPLEADO      END ID_DE_EMPLEADO,
                               CASE WHEN NORMAL.C_RETRO        IS NULL THEN NVL(NORMAL.ID_DE_CONCEPTO, RETRO.ID_DE_CONCEPTO) ELSE TO_NUMBER(NORMAL.C_RETRO) END ID_DE_CONCEPTO,
                               SUM(NVL(RETRO.VALOR,0)-NVL(NORMAL.VALOR,0)) DIFERENCIA,
                               SYSDATE,
                               '''|| UN_USUARIO ||'''
                        FROM
                            (
                                SELECT HISTORICOS.COMPANIA, HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.ID_DE_EMPLEADO, HISTORICOS.ID_DE_CONCEPTO,
                                       CONCEPTOS.C_RETRO,
                                       SUM(VALOR) AS VALOR	
                                FROM HISTORICOS INNER JOIN CONCEPTOS
                                  ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                                 AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                WHERE HISTORICOS.COMPANIA       = '''|| UN_COMPANIA ||'''
                                  AND HISTORICOS.ID_DE_PROCESO  = '  || UN_PROCESO  || '
                                  AND HISTORICOS.ANO            = '  || UN_ANO      || '
                                  AND HISTORICOS.MES            = '  || UN_MES      || '
                                  AND HISTORICOS.PERIODO        = '  || UN_PERIODO  || '
                                  AND HISTORICOS.ID_DE_EMPLEADO = '  || UN_EMPLEADO ||'
                                  AND HISTORICOS.ID_DE_CONCEPTO NOT BETWEEN 490 AND 499
                                  '  || CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CALCULAR RETENCION EN RETROACTIVO', UN_MODULO => 6, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'NO') = 'NO' THEN '' ELSE 'AND HISTORICOS.ID_DE_CONCEPTO NOT IN (303)' END  ||' 
                                  AND HISTORICOS.ID_DE_CONCEPTO < 1000								  
                                GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.ID_DE_EMPLEADO, HISTORICOS.ID_DE_CONCEPTO,
                                         CONCEPTOS.C_RETRO
                            ) NORMAL 
                        FULL OUTER JOIN 
                            (
                                SELECT COMPANIA, ANO, MES,  ID_DE_EMPLEADO, ID_DE_CONCEPTO,
                                       SUM(VALOR) AS VALOR		
                                FROM HISTORICOS
                                WHERE COMPANIA       = '''|| UN_COMPANIA           || '''
                                  AND ID_DE_PROCESO  = '  || UN_PROCESORETROACTIVO || '
                                  AND ANO            = '  || UN_ANO                || '
                                  AND MES            = '  || UN_MES                || '  
                                  AND PERIODO        = '  || UN_PERIODORETROACTIVO || ' 
                                  AND ID_DE_EMPLEADO = '  || UN_EMPLEADO           || '
                                GROUP BY COMPANIA, ANO, MES,  ID_DE_EMPLEADO, ID_DE_CONCEPTO
                            )  RETRO 
                             ON NORMAL.COMPANIA       = RETRO.COMPANIA
                            AND NORMAL.ANO            = RETRO.ANO
                            AND NORMAL.MES            = RETRO.MES
                            AND NORMAL.ID_DE_EMPLEADO = RETRO.ID_DE_EMPLEADO
                            AND NORMAL.ID_DE_CONCEPTO = RETRO.ID_DE_CONCEPTO
                        GROUP BY CASE WHEN NORMAL.COMPANIA       IS NULL THEN RETRO.COMPANIA       ELSE NORMAL.COMPANIA       END,
                                 CASE WHEN NORMAL.ANO            IS NULL THEN RETRO.ANO            ELSE NORMAL.ANO            END,
                                 CASE WHEN NORMAL.MES            IS NULL THEN RETRO.MES            ELSE NORMAL.MES            END,
                                 CASE WHEN NORMAL.ID_DE_EMPLEADO IS NULL THEN RETRO.ID_DE_EMPLEADO ELSE NORMAL.ID_DE_EMPLEADO END,
                                 CASE WHEN NORMAL.C_RETRO        IS NULL THEN NVL(NORMAL.ID_DE_CONCEPTO, RETRO.ID_DE_CONCEPTO) ELSE TO_NUMBER(NORMAL.C_RETRO) END
                        HAVING SUM(NVL(RETRO.VALOR,0)-NVL(NORMAL.VALOR,0)) <>0';
        BEGIN
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'IS'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;        
        END;

        END IF;--(CC:3708_CFBARRERA_FIN)

        --JM CC3876 INI 
        IF (PCK_NOMINA.FC_CN(94)+PCK_NOMINA.FC_CN(96)+PCK_NOMINA.FC_CN(510)) = 0 AND PCK_NOMINA.GL_PROCESOREAL = 10  AND PCK_NOMINA.FC_CN(35) <> 0  AND NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'NOMINA MENSUAL', UN_MODULO => PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS => -1), 'SI') = 'SI' AND  PCK_NOMINA.GL_SMES = 1 THEN 
        
        MI_CAMPOS := 'COMPANIA,
                      ID_DE_PROCESO,
                      ANO,
                      MES,
                      PERIODO,
                      ID_DE_EMPLEADO,
                      ID_DE_CONCEPTO,
                      VALOR,
                      FECHA,
                      CREATED_BY';
                      
        MI_SUELDO_VAC := CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        MI_AUX := CASE WHEN MI_SUELDO_VAC > (PCK_NOMINA.FC_CN(201) * 2 ) THEN FALSE ELSE TRUE END;
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 80, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 33, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(80) > 0  AND MI_AUX THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 81, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 33, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_SUELDO_VAC := MI_SUELDO_VAC + PCK_NOMINA.FC_CNP(81);
        END IF;
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 79, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 33, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(79) > 0 AND MI_AUX THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 82, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 33, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_SUELDO_VAC := MI_SUELDO_VAC + PCK_NOMINA.FC_CNP(82);
        END IF;

        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 10, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(10) > 0 THEN 
            MI_SUELDO_VAC_ANT := PCK_NOMINA.FC_CNP(10);
        ELSE
            PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 1, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_SUELDO_VAC_ANT := PCK_NOMINA.FC_CNP(1);
        END IF;
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 80, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(80) > 0 THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 81, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_SUELDO_VAC_ANT := MI_SUELDO_VAC_ANT + PCK_NOMINA.FC_CNP(81);
        END IF;
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 79, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(79) > 0 THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 82, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_SUELDO_VAC_ANT := MI_SUELDO_VAC_ANT + PCK_NOMINA.FC_CNP(82);
        END IF;

        MI_DIFERENCIA_VAC := PCK_SYSMAN_UTL.FC_ROUND((MI_SUELDO_VAC / 30 * PCK_NOMINA.FC_CN(35)) - (MI_SUELDO_VAC_ANT /30 * PCK_NOMINA.FC_CN(35)),0);
    
    --JM CC 3932  descontar SS completa de lo pagado anticipado en diciembre 
        IF PCK_PARENTR.PARAMETRO70 <> 'S' THEN 
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 10, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(10) > 0 THEN
            MI_BASE_SS_ANT := PCK_NOMINA.FC_CNP(10);
        ELSE
            PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 1, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_BASE_SS_ANT := PCK_NOMINA.FC_CNP(1);
        END IF;
        
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 150, PCK_NOMINA.GL_SANO, 1, 3, PCK_NOMINA.GL_SANO, 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_BASE_SS_ANT := MI_BASE_SS_ANT + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(UN_COMPANIA,'150') = -1, PCK_NOMINA.FC_CNP(150), 0); 
        
        MI_BASE_SS := (CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END
         + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(UN_COMPANIA,'150') = -1, PCK_NOMINA.FC_CN(150), 0))-MI_BASE_SS_ANT; 

        MI_SALUDE_SS :=  ROUND(MI_BASE_SS * PCK_PARENTR.PARAMETRO41 / 100 , CASE WHEN PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI' THEN -2 ELSE 0 END);
        MI_SALUDP_SS :=  ROUND(MI_BASE_SS * PCK_PARENTR.PARAMETRO43 / 100 ,-2) - MI_SALUDE_SS;
        MI_SALUDP_SS :=  MI_SALUDP_SS + 100; --como al menos tiene 2 novedades por redondeos va a dar 100 pesos de mas 
        
        MI_PENSIONE_SS :=  ROUND(MI_BASE_SS * PCK_PARENTR.PARAMETRO36 / 100 , CASE WHEN PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI' THEN -2 ELSE 0 END);
        MI_PENSIONP_SS :=  ROUND(MI_BASE_SS * PCK_PARENTR.PARAMETRO39 / 100 ,-2) - MI_PENSIONE_SS;
        MI_PENSIONP_SS := MI_PENSIONP_SS + 100; --como al menos tiene 2 novedades por redondeos va a dar 100 pesos de mas 
         
         
         BEGIN
                BEGIN
                MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                                 ''' AND ANO           = '   || PCK_NOMINA.GL_SANO    ||
                                 '   AND MES           = '   || UN_MES     ||
                                 '   AND ID_DE_EMPLEADO = ' || UN_EMPLEADO ||
                                 '   AND ID_DE_PROCESO = 1 AND PERIODO = '||UN_PERIODODIFERENCIAS|| 
                                 ' AND ID_DE_CONCEPTO in (130,131,113,118,116,117)';
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 113 , '||MI_SALUDE_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
                                       
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 130 , '||MI_SALUDE_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
                                       
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 118 , '||MI_PENSIONE_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
                                       
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 131 , '||MI_PENSIONE_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
                                       
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 116 , '||MI_SALUDP_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
                                       
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 117 , '||MI_PENSIONP_SS||', SYSDATE, '''|| UN_USUARIO ||'''';
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    
    END IF;  --FIN CC 3932

    IF MI_DIFERENCIA_VAC <> 0 THEN 
    
      BEGIN
                BEGIN 
                MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                                 ''' AND ANO           = '   || PCK_NOMINA.GL_SANO    ||
                                 '   AND MES           = '   || UN_MES     ||
                                 '   AND ID_DE_EMPLEADO = ' || UN_EMPLEADO ||
                                 '   AND ID_DE_PROCESO = 1 AND PERIODO = '|| UN_PERIODODIFERENCIAS||
                                 ' AND ID_DE_CONCEPTO = 510';

                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
                      
            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                      ','||UN_EMPLEADO||', 510 , '||MI_DIFERENCIA_VAC||', SYSDATE, '''|| UN_USUARIO ||'''';
            
            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                       ,UN_ACCION  => 'I'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
        
        END IF;
        
        
        -- vacaciones empezadas a disfrutar en enero 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 35, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNP(35) = 0 THEN
            
            IF PCK_NOMINA.FC_CN(548) = 0 THEN --ber
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 151, (PCK_NOMINA.GL_SANO-1), 12, 3, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                MI_DIFERENCIA_BER := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(151) * PCK_NOMINA.CCATEGORIA(1).VLR_INCREMENTO / 100,0) ;
                 IF MI_DIFERENCIA_BER <> 0 THEN 
    
                      BEGIN
                            BEGIN 
                            MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                                 ''' AND ANO           = '   || PCK_NOMINA.GL_SANO    ||
                                 '   AND MES           = '   || UN_MES     ||
                                 '   AND ID_DE_EMPLEADO = ' || UN_EMPLEADO ||
                                 '   AND ID_DE_PROCESO = 1 AND PERIODO = '|| UN_PERIODODIFERENCIAS||
                                 ' AND ID_DE_CONCEPTO = 548';

                             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                            END;
                                      
                            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                                      ','||UN_EMPLEADO||', 548 , '||MI_DIFERENCIA_BER||', SYSDATE, '''|| UN_USUARIO ||'''';
                            
                            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                        END;
                        
                END IF;
        
            END IF;
            
            IF PCK_NOMINA.FC_CN(501) = 0 THEN --prima
                MI_DIFERENCIA_PVAC := PCK_SYSMAN_UTL.FC_ROUND((MI_SUELDO_VAC / 30 * 15) - (MI_SUELDO_VAC_ANT /30 * 15),0);
                IF MI_DIFERENCIA_PVAC <> 0 THEN 
    
                      BEGIN
                            BEGIN 
                            MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                                 ''' AND ANO           = '   || PCK_NOMINA.GL_SANO    ||
                                 '   AND MES           = '   || UN_MES     ||
                                 '   AND ID_DE_EMPLEADO = ' || UN_EMPLEADO ||
                                 '   AND ID_DE_PROCESO = 1 AND PERIODO = '|| UN_PERIODODIFERENCIAS||
                                  ' AND ID_DE_CONCEPTO = 501';

                             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                            END;
                                      
                            MI_VALORES := ''''||PCK_NOMINA.GL_COMPANIA||''', '||UN_PROCESO||' , '||UN_ANO||','||UN_MES||','||UN_PERIODODIFERENCIAS||
                                      ','||UN_EMPLEADO||', 501 , '||MI_DIFERENCIA_PVAC||', SYSDATE, '''|| UN_USUARIO ||'''';
                            
                            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA    =>'HISTORICOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                        END;
                        
                END IF;
                
            END IF; 
    
        END IF;
    END IF;
    --JM CC3876 FIN

        --(JGOMEZ:07/08/2019) para actualizar el neto a pagar pues en adicionales por cuestiones de procesos manuales da errado
        SELECT NVL(SUM(CASE WHEN ID_DE_CONCEPTO = 97 THEN VALOR ELSE 0 END - CASE WHEN ID_DE_CONCEPTO = 140 THEN VALOR ELSE 0 END),0) NETO
        INTO MI_NETO
        FROM HISTORICOS
        WHERE COMPANIA      = UN_COMPANIA
          AND ID_DE_PROCESO = UN_PROCESO
          AND ANO           = UN_ANO
          AND MES           = UN_MES
          AND PERIODO       = UN_PERIODODIFERENCIAS
          AND ID_DE_EMPLEADO = UN_EMPLEADO
          AND ID_DE_CONCEPTO IN(97,140);

        MI_CAMPOS := 'VALOR = ' || MI_NETO;    
        MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA           || '''' || 
                   ' AND ID_DE_PROCESO = '  || UN_PROCESO            ||
                   ' AND ANO           = '  || UN_ANO                ||
                   ' AND MES           = '  || UN_MES                ||
                   ' AND PERIODO       = '  || UN_PERIODODIFERENCIAS ||
                   ' AND ID_DE_EMPLEADO = ' || UN_EMPLEADO           ||
                   ' AND ID_DE_CONCEPTO = 144';

        BEGIN  
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME (UN_TABLA     => 'HISTORICOS'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPOS
                                                ,UN_CONDICION => MI_CONDICION);      
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END; 
        --(APINEDA:25/03/2022)-Ticket 7711258 se agrega validacion para alertar al usuario cuando en el calculo de retroactivo se supera el tope para auxilio de transporte o alimentacion.
        FOR RSAUX IN
        (          
        SELECT HISTORICOS.ID_DE_CONCEPTO, CONCEPTOS.NOMBRE_CONCEPTO,  HISTORICOS.VALOR        
        FROM HISTORICOS INNER JOIN CONCEPTOS 
        ON (HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA)
        AND (HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO)
        WHERE HISTORICOS.COMPANIA      = UN_COMPANIA
         AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
         AND HISTORICOS.ANO            = UN_ANO
         AND HISTORICOS.MES            = UN_MES
         AND HISTORICOS.PERIODO        = UN_PERIODODIFERENCIAS
         AND HISTORICOS.ID_DE_EMPLEADO = UN_EMPLEADO
         AND HISTORICOS.ID_DE_CONCEPTO IN(524,525)
         AND HISTORICOS.VALOR < 0
          )
        LOOP
        --El funcionario --NOMEMPLEADO-- supera el tope para liquidacion de --NOMBRECONCEPTO--, por lo cual ha perdido el derecho a la liquidacion de este concepto y se esta reversando el valor cancelado en la nomina mensual 
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'NOMBRECONCEPTO';
            MI_MSG(2).VALOR := RSAUX.ID_DE_CONCEPTO || ' ' || RSAUX.NOMBRE_CONCEPTO;
            
            PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_TOPEAUXILIOTRANSALIM
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            ); 
        END LOOP;
          
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        MI_MSG(1).CLAVE := 'PROCESO';
        MI_MSG(1).VALOR := UN_PROCESO;
        MI_MSG(2).CLAVE := 'PERIODO';
        MI_MSG(2).VALOR := UN_PERIODO;        
        MI_MSG(3).CLAVE := 'PROCESORETROACTIVO';
        MI_MSG(3).VALOR := UN_PROCESORETROACTIVO;        
        MI_MSG(4).CLAVE := 'PERIODORETROACTIVO';
        MI_MSG(4).VALOR := UN_PERIODORETROACTIVO;          
        MI_MSG(5).CLAVE := 'EMPLEADO';
        MI_MSG(5).VALOR := UN_EMPLEADO;          
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_TABLAERROR => 'HISTORICOS',
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_NOHAYDATOSCALCDIFERENCIAS,
                                      UN_REEMPLAZOS => MI_MSG);   
        --PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => UN_COMPANIA, UN_MENSAJE_COD => PCK_ERRORES.ERR_NOHAYDATOSCALCDIFERENCIAS, UN_REEMPLAZOS => MI_MSG);      
    END;

    --LVEGA CC 3046
     PCK_DATOS.GL_RTA := PCK_NOMINA_COM9.FC_MANTE_ELIM_CPTO_NOMINA(
    UN_COMPANIA             =>UN_COMPANIA,
    UN_PROCESO              =>'10',
    UN_ANIO                 =>UN_ANO,
    UN_MES                  =>UN_MES,
    UN_PERIODO              =>PCK_NOMINA.GL_SPER + 50);

     PCK_DATOS.GL_RTA := PCK_NOMINA_COM9.FC_MANTE_ELIM_CPTO_NOMINA(
    UN_COMPANIA             =>UN_COMPANIA,
    UN_PROCESO              =>'1',
    UN_ANIO                 =>UN_ANO,
    UN_MES                  =>UN_MES,
    UN_PERIODO              =>PCK_NOMINA.GL_SPER + 30);
    
END PR_CALCDIFERENCIASRETROACTIVO;

PROCEDURE PR_ELIMINARDATOSNOMRETROACTIVO 
/*
    NAME              : PR_ELIMINARDATOSNOMRETROACTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 11/06/2019
    TIME              : 04:42 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Elimina los datos existentes de nÃ³mina retroactivo en las tablas HISTORICOS, PERSONAL_HISTORICO, RETEFUENTE_CALCULOS y BASESNOVEDADES
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESORETROACTIVO   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODORETROACTIVO   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMP_INICIAL          IN VARCHAR2,
    UN_EMP_FINAL            IN VARCHAR2,    
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                  IN PCK_SUBTIPOS.TI_MES 
  )
AS
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_ERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
BEGIN
    BEGIN        
        BEGIN
         --(APINEDA:31/07/2019)-Se elimina filtro por periodo para el borrado de registros de la tabla BASESNOVEDADES.
         --(APINEDA:31/07/2019)-Se agrega filtro por proceso retroactivo usando el campo ID_DE_PROCESO 

         MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                         ''' AND ANO           = '   || UN_ANIO    ||
                         '   AND MES           = '   || UN_MES     ||
                         '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || ' AND ' || UN_EMP_FINAL ||
                         '   AND ID_DE_PROCESO = 10';
         MI_TABLA := 'BASESNOVEDADES';                         
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_CONDICION := 'COMPANIA             = ''' || UN_COMPANIA||
                         ''' AND ID_DE_PROCESO = '  || UN_PROCESORETROACTIVO ||
                         '   AND ANO           = '  || UN_ANIO    ||
                         '   AND MES           = '  || UN_MES     ||
                         '   AND ID_DE_EMPLEADO BETWEEN ' || UN_EMP_INICIAL || ' AND ' || UN_EMP_FINAL ||
                         '   AND PERIODO       = '  || UN_PERIODORETROACTIVO;
        BEGIN                
         MI_TABLA := 'PERSONAL_HISTORICO';
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;    
        BEGIN       
         MI_TABLA := 'HISTORICOS';
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END; 
        BEGIN  
         MI_TABLA := 'RETEFUENTE_CALCULOS';
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
             RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;      
     EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
           MI_ERROR(1).CLAVE := 'ANO';
           MI_ERROR(1).VALOR := UN_ANIO;
           MI_ERROR(2).CLAVE := 'MES';
           MI_ERROR(2).VALOR := UN_MES;           
           MI_ERROR(3).CLAVE := 'PROCESO';
           MI_ERROR(3).VALOR := UN_PROCESORETROACTIVO;           
           MI_ERROR(4).CLAVE := 'PERIODO';
           MI_ERROR(4).VALOR := UN_PERIODORETROACTIVO;                      
           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMINANDODATOSRETROACTIVO,
                                      UN_REEMPLAZOS => MI_ERROR);          
    END;     
END PR_ELIMINARDATOSNOMRETROACTIVO;

PROCEDURE PR_COPIARHISTORICOSORIGINALES
    /*
    NAME              : PR_COPIARHISTORICOSORIGINALES
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 19/06/2019
    TIME              : 05:37 PM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se copian datos originales de HISTORICOS 
    @NAME:  COPIARHISTORICOSORIGINALES
    */
AS 
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONDICION    VARCHAR2(32000);
    MI_CONSULTA     VARCHAR2(32000);
BEGIN
    BEGIN
        PCK_NOMINA.MI_HISTORICOS.DELETE;
        --(APINEDA:30/03/2020)-TAR 1000098562 se modifica filtro de la selecciÃ³n para tener en cuenta periodo 7  
        SELECT *
        BULK COLLECT INTO PCK_NOMINA.MI_HISTORICOS
        FROM HISTORICOS
        WHERE COMPANIA = PCK_NOMINA.GL_COMPANIA
          AND ID_DE_PROCESO = PCK_NOMINA.GL_PROCESOACTUAL
          AND ANO = PCK_NOMINA.GL_SANO    
          AND MES = PCK_NOMINA.GL_SMES    
          AND (PERIODO = PCK_NOMINA.GL_SPER OR PERIODO = CASE WHEN PCK_NOMINA.GL_SPER IN (1,2,3) THEN 7 END)
          AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;      

        --(APINEDA:06/09/2019)-Se agrega a la alerta el cÃ³digo y nombre del empleado.
        IF PCK_NOMINA.MI_HISTORICOS.COUNT = 0 THEN 
            MI_MSGERROR(1).CLAVE := 'TABLA'; 
            MI_MSGERROR(1).VALOR := 'HISTORICOS, empleado ' || TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;             
            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
        --(APINEDA:19/06/2019)-Se carga CN_ORIGINAL con el valor de los conceptos calculados en el periodo original para el cÃ¡lculo del retroactivo.
        ELSE
            PCK_NOMINA.CN_ORIGINAL.DELETE;
            <<I_HISTORICOSORIGINALES>>                        
            FOR I_HISTORICOSORIGINALES IN PCK_NOMINA.MI_HISTORICOS.FIRST .. PCK_NOMINA.MI_HISTORICOS.LAST LOOP
                PCK_NOMINA.MI_REGHISTORICO_ORIGINAL := PCK_NOMINA.MI_HISTORICOS(I_HISTORICOSORIGINALES);
                --(APINEDA:30/03/2020)-TAR 1000098562 se guardan historicos del periodo correspondiente en (CN_ORIGINAL)
                IF PCK_NOMINA.MI_REGHISTORICO_ORIGINAL.PERIODO = PCK_NOMINA.GL_SPER THEN
                    PCK_NOMINA.CN_ORIGINAL(PCK_NOMINA.MI_REGHISTORICO_ORIGINAL.ID_DE_CONCEPTO) := PCK_NOMINA.MI_REGHISTORICO_ORIGINAL.VALOR;   
                END IF;
            END LOOP I_HISTORICOSORIGINALES; 
            
            --(APINEDA:17/03/2022)-Ticket 7711224. Se agrega validacion de parametro para tener en cuenta los historicos cuando se maneja periodo adicional
            IF PCK_PARST.FC_PAR('LIQUIDAN VACACIONES DENTRO DE NOMINA', 'NO') = 'NO' AND PCK_PARST.FC_PAR('DESCONTAR COOPERATIVAS EN NOMINA ADICIONAL','NO') = 'SI' THEN
                PCK_NOMINA.MI_HISTORICOSADICIONAL.DELETE;  
                MI_CONSULTA := 'SELECT * FROM HISTORICOS' || CHR(10) || CHR(13) ;  
                MI_CONDICION :=' COMPANIA = ''' || PCK_NOMINA.GL_COMPANIA || '''' || CHR(10) || CHR(13) || 
                           '   AND ID_DE_PROCESO = ' || PCK_NOMINA.GL_PROCESOACTUAL || CHR(10) || CHR(13) || 
                           '   AND ANO = ' || PCK_NOMINA.GL_SANO || CHR(10) || CHR(13) || 
                           '   AND MES = ' || PCK_NOMINA.GL_SMES || CHR(10) || CHR(13) || 
                           '   AND PERIODO = ' || TO_NUMBER(PCK_PARST.FC_PAR('PERIODO NOMINA ADICIONAL','0')) || CHR(10) || CHR(13) || 
                           '   AND ID_DE_EMPLEADO = ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || CHR(10) || CHR(13) ||
                           '   AND ID_DE_CONCEPTO BETWEEN 600 AND 698' || CHR(10) || CHR(13);
                MI_CONSULTA := MI_CONSULTA || ' WHERE ' || MI_CONDICION;           
                EXECUTE IMMEDIATE MI_CONSULTA BULK COLLECT INTO PCK_NOMINA.MI_HISTORICOSADICIONAL;                           
                                  
                IF PCK_NOMINA.MI_HISTORICOSADICIONAL.COUNT > 0 THEN  
                    <<I_HISTORILESADICIONAL>>                        
                    FOR I_HISTORILESADICIONAL IN PCK_NOMINA.MI_HISTORICOSADICIONAL.FIRST .. PCK_NOMINA.MI_HISTORICOSADICIONAL.LAST LOOP
                        PCK_NOMINA.MI_REGHISTORICO_ORIGINAL := PCK_NOMINA.MI_HISTORICOSADICIONAL(I_HISTORILESADICIONAL);
                        PCK_NOMINA.CN_ORIGINAL(PCK_NOMINA.MI_REGHISTORICO_ORIGINAL.ID_DE_CONCEPTO) := PCK_NOMINA.MI_REGHISTORICO_ORIGINAL.VALOR;
                    END LOOP I_HISTORICOSORIGINALES;
                END IF;
                BEGIN                
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;                  
            END IF;
        END IF;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(2).CLAVE := 'PERIODO';
            MI_MSGERROR(2).VALOR := PCK_NOMINA.GL_SPER;
            --(APINEDA:06/09/2019)-Se cambia error por alerta
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,
                                       UN_MENSAJE_COD => PCK_ERRORES.ERR_SINDATOSTABLAPERIODO,
                                       UN_REEMPLAZOS => MI_MSGERROR,
                                       UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL,
                                       UN_ANO => PCK_NOMINA.GL_ANOACTUAL,
                                       UN_MES => PCK_NOMINA.GL_MESACTUAL,
                                       UN_PERIODO => PCK_NOMINA.GL_SPER,
                                       UN_USER => PCK_CONEXION.FC_GETUSER);                                              
    END;
    --(APINEDA:30/03/2020)-TAR 1000098562 se modifica condiciÃ³n para tener en cuenta periodo 7 en la eliminaciÃ³n de registros luego de realizar copia.
    MI_CONDICION := 'COMPANIA             = ''' || PCK_NOMINA.GL_COMPANIA       ||
                     ''' AND ID_DE_PROCESO = '  || PCK_NOMINA.GL_PROCESOACTUAL  ||
                     '   AND ANO           = '  || PCK_NOMINA.GL_SANO           ||
                     '   AND MES           = '  || PCK_NOMINA.GL_SMES           ||
                     '   AND ID_DE_EMPLEADO = ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO ||
                     '   AND (PERIODO = ' || PCK_NOMINA.GL_SPER || ' OR PERIODO = CASE WHEN ' || PCK_NOMINA.GL_SPER || ' IN (1,2,3) THEN 7 END)';
    BEGIN                
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;                                   
END PR_COPIARHISTORICOSORIGINALES;

PROCEDURE PR_COPIARPERSONALHISTORICO
    /*
    NAME              : PR_COPIARPERSONALHISTORICO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 19/06/2019
    TIME              : 05:50 PM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se copian datos originales de PERSONAL_HISTORICO 
    @NAME:  COPIARPERSONALHISTORICO
    */
AS 
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONDICION    VARCHAR2(32000);
BEGIN
    BEGIN          
        PCK_NOMINA.MI_PERSONAL_HISTORICO.DELETE;  
        SELECT *
        BULK COLLECT INTO PCK_NOMINA.MI_PERSONAL_HISTORICO
        FROM PERSONAL_HISTORICO
        WHERE COMPANIA = PCK_NOMINA.GL_COMPANIA
          AND ID_DE_PROCESO = PCK_NOMINA.GL_PROCESOACTUAL
          AND ANO = PCK_NOMINA.GL_SANO    
          AND MES = PCK_NOMINA.GL_SMES    
          AND PERIODO = PCK_NOMINA.GL_SPER
          AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;           
        --(APINEDA:06/09/2019)-Se agrega a la alerta el cÃ³digo y nombre del empleado.
        IF PCK_NOMINA.MI_PERSONAL_HISTORICO.COUNT = 0 THEN 
            MI_MSGERROR(1).CLAVE := 'TABLA'; 
            MI_MSGERROR(1).VALOR := 'PERSONAL_HISTORICO, empleado ' || TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;             
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;                        
        ELSE
            <<I_PERSONALORIGINALES>>                        
            FOR I_PERSONALORIGINALES IN PCK_NOMINA.MI_PERSONAL_HISTORICO.FIRST .. PCK_NOMINA.MI_PERSONAL_HISTORICO.LAST LOOP
                PCK_NOMINA.MI_REGPERSONAL_ORIGINAL := PCK_NOMINA.MI_PERSONAL_HISTORICO(I_PERSONALORIGINALES);
            END LOOP I_PERSONALORIGINALES; 
        END IF;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(2).CLAVE := 'PERIODO';
            MI_MSGERROR(2).VALOR := PCK_NOMINA.GL_SPER;
            --(APINEDA:06/09/2019)-Se cambia error por alerta
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,
                                       UN_MENSAJE_COD => PCK_ERRORES.ERR_SINDATOSTABLAPERIODO,
                                       UN_REEMPLAZOS => MI_MSGERROR,
                                       UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL,
                                       UN_ANO => PCK_NOMINA.GL_ANOACTUAL,
                                       UN_MES => PCK_NOMINA.GL_MESACTUAL,
                                       UN_PERIODO => PCK_NOMINA.GL_SPER,
                                       UN_USER => PCK_CONEXION.FC_GETUSER);                                      
    END;
    MI_CONDICION := 'COMPANIA             = ''' || PCK_NOMINA.GL_COMPANIA       ||
                     ''' AND ID_DE_PROCESO = '  || PCK_NOMINA.GL_PROCESOACTUAL  ||
                     '   AND ANO           = '  || PCK_NOMINA.GL_SANO           ||
                     '   AND MES           = '  || PCK_NOMINA.GL_SMES           ||
                     '   AND ID_DE_EMPLEADO = ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO ||
                     '   AND PERIODO       = '  || PCK_NOMINA.GL_SPER;
    BEGIN                
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'PERSONAL_HISTORICO', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);         
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;                                   
END PR_COPIARPERSONALHISTORICO;

PROCEDURE PR_CARGARCAMPOSPERIODOORIGINAL
    /*
    NAME              : PR_CARGARCAMPOSPERIODOORIGINAL
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 19/06/2019
    TIME              : 07:36 PM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Obtiene valores de configuraciÃ³n de empleado en el mes original, a tener en cuenta en el cÃ¡lculo de retroactivo.
    @NAME:  CARGARCAMPOSPERIODOORIGINAL
    */
AS 
BEGIN    
    IF PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO THEN
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DEPENDENCIA := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.DEPENDENCIA;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_CENTRO_DE_COSTO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ID_CENTRO_DE_COSTO;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.SUBTIPOCOTIZANTE;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SALUD := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.FONDO_SALUD;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_RIESGOS := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.FONDO_RIESGOS; 
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DEL_FONDO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ID_DEL_FONDO;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CAJA_COMPENSACION := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.CAJA_COMPENSACION;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMEROPATRONAL := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.NUMEROPATRONAL;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.SINDICATO;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.FONDO_SINDICATO;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.PORC_SINDICATO;        
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3 := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.SINDICATO3;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO3 := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.FONDO_SINDICATO3;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO3 := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.PORC_SINDICATO3;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ESCALAFON;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ID_DE_CARGO;
        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CATEGORIA := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.ID_DE_CATEGORIA;
        --PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FACTOR_RIESGO := PCK_NOMINA.MI_REGPERSONAL_ORIGINAL.FACTOR_RIESGO;        --No existe en PERSONAL
    END IF;            
END PR_CARGARCAMPOSPERIODOORIGINAL;

PROCEDURE PR_REESTABLECERHISTORICOS
    /*
    NAME              : PR_REESTABLECERHISTORICOS
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 20/06/2019
    TIME              : 10:19 AM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se restauran datos originales del periodo para HISTORICOS y PERSONAL_HISTORICO, se crean registros de periodo retroactivo en PERSONAL_HISTORICO.
    @NAME:  REESTABLECERDATOSPERIODO
    */
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO
)    
AS 
    MI_REGHISTORICOS             HISTORICOS%ROWTYPE;
    MI_REGPERSONAL_HISTORICO     PERSONAL_HISTORICO%ROWTYPE;
BEGIN
    BEGIN
        <<I_HISTORICOS>>
        IF (PCK_NOMINA.MI_HISTORICOS.COUNT > 0) THEN 
            FOR I_HISTORICOS IN PCK_NOMINA.MI_HISTORICOS.FIRST .. PCK_NOMINA.MI_HISTORICOS.LAST LOOP
                MI_REGHISTORICOS := PCK_NOMINA.MI_HISTORICOS(I_HISTORICOS);
                INSERT INTO HISTORICOS VALUES MI_REGHISTORICOS;        
            END LOOP I_HISTORICOS;
        END IF;     
        <<I_PERSONALH>>
        IF (PCK_NOMINA.MI_PERSONAL_HISTORICO.COUNT > 0) THEN 
            FOR I_PERSONALH IN PCK_NOMINA.MI_PERSONAL_HISTORICO.FIRST .. PCK_NOMINA.MI_PERSONAL_HISTORICO.LAST LOOP
                MI_REGPERSONAL_HISTORICO := PCK_NOMINA.MI_PERSONAL_HISTORICO(I_PERSONALH);
                INSERT INTO PERSONAL_HISTORICO VALUES MI_REGPERSONAL_HISTORICO;
                MI_REGPERSONAL_HISTORICO.ID_DE_PROCESO := UN_PROCESO;
                MI_REGPERSONAL_HISTORICO.PERIODO := PCK_NOMINA.GL_PERIODORETROACTIVO;
                MI_REGPERSONAL_HISTORICO.SALARIO_BASE_IBC := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC;
                MI_REGPERSONAL_HISTORICO.CREATED_BY := UN_USUARIO;
                MI_REGPERSONAL_HISTORICO.SALARIO_BASE_CATE := PCK_NOMINA_COM1.FC_SALARIO_BASE_CATEG(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                MI_REGPERSONAL_HISTORICO.DATE_CREATED := SYSDATE;
                INSERT INTO PERSONAL_HISTORICO VALUES MI_REGPERSONAL_HISTORICO;
                MI_REGPERSONAL_HISTORICO.ID_DE_PROCESO := PCK_NOMINA.GL_PROCESOACTUAL;
                --(APINEDA:31/07/2019)-Se cambia periodo 5 por variable global GL_PERIODODIFERENCIASRETRO
                MI_REGPERSONAL_HISTORICO.PERIODO := PCK_NOMINA.GL_PERIODODIFERENCIASRETRO;                    
                INSERT INTO PERSONAL_HISTORICO VALUES MI_REGPERSONAL_HISTORICO;
            END LOOP I_PERSONALH;
        END IF;
        --(APINEDA:18/03/2022)-TICKET 7711224 Se agrega para tener en cuenta los historicos de descuentos cuando hay periodo adicional de nómina
        <<I_HISTORICOSADICIONAL>>
        IF (PCK_NOMINA.MI_HISTORICOSADICIONAL.COUNT > 0) THEN 
            FOR I_HISTORICOSADICIONAL IN PCK_NOMINA.MI_HISTORICOSADICIONAL.FIRST .. PCK_NOMINA.MI_HISTORICOSADICIONAL.LAST LOOP
                MI_REGHISTORICOS := PCK_NOMINA.MI_HISTORICOSADICIONAL(I_HISTORICOSADICIONAL);
                INSERT INTO HISTORICOS VALUES MI_REGHISTORICOS;        
            END LOOP I_HISTORICOS;
        END IF;     
    END;
END PR_REESTABLECERHISTORICOS;

PROCEDURE PR_CALCULARCESANTIASFND(
  /*
  NAME               : CALCULARCESANTIASFND
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 11/07/2019
  TIME               : 12:30 PM
  SOURCE MODULE      : NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LAS CESANTÃ�AS PARA FEDERACIÃ“N DE DEPARTAMENTOS
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARCESANTIASFND
  */
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
  MI_ALIMRET            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_RECARGOSUELDO      PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_PROMFAC            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DIASPROMEDIO       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_COMISIONES         PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0; 
  MI_SBM                PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_ANIO               PCK_SUBTIPOS.TI_ANIO   DEFAULT 0;
  MI_MES                PCK_SUBTIPOS.TI_MES    DEFAULT 0;
  MI_DIA                NUMBER :=0;
  MI_MASDIASOTRAENTIDAD NUMBER :=0;
  MI_ANTICIPOS          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DP                 PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DIASINT            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_DBLPROMHOREXTRAS   PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DBLAJSUELDO        PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DBLREINTEGRO       PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_DESCUENTO          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_AUXT1              PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_SLD_MED_TRIM       PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_CESANTIA1          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  --(APINEDA:13/08/2019)-Se crea variable para almacenar dias trabajados en el aÃ±o
  MI_DIASLABORANIO            PCK_SUBTIPOS.TI_DOBLE          :=0;  
BEGIN
  PCK_NOMINA.GL_BASCES := 0;

  --Se deja el IF tal cual como esta en Access pero se cumpla o no hace lo mismo 
  IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) > 90 THEN

      MI_SBM  := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 
                 THEN PCK_NOMINA.FC_CN(1)
                 ELSE PCK_NOMINA.FC_CN(900)
                 END;
  ELSE
      MI_SBM  := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 
                 THEN PCK_NOMINA.FC_CN(1)
                 ELSE PCK_NOMINA.FC_CN(900)
                 END;
  END IF;


  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN --Ganan Comisiones
       --Salario promedio mensual
       --Acumulados del Ãºltimo aÃ±o
       IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
          PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
          PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
          PCK_NOMINA.GL_PERA :=  CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16
                                 THEN 1
                                 ELSE 2
                                 END ;
          MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1, 3,0);
       ELSE
          PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
          PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)); 
          PCK_NOMINA.GL_PERA := CASE WHEN  PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN))< 16
                                THEN 1
                                ELSE 2
                                END;
          MI_DIASPROMEDIO    := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN),PCK_NOMINA.GL_FECHAFIN1,3,0);                              
       END IF;

       PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
       MI_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
       PCK_NOMINA.CN(971) := MI_COMISIONES;
  END IF;

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
     MI_SBM := MI_SBM + MI_COMISIONES;
  ELSE
     MI_SBM := MI_SBM;
  END IF;
  -- revisar aqui si el regimen o la fecha esta bien
  -- ultquinquenio = valorultimoquinquenio(personal!Id_de_Empleado)
  IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996','DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
         MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR);
         MI_MES  := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR);
         MI_DIA  := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16
                    THEN 1
                    ELSE 2
                    END ;

         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,MI_ANIO,MI_MES,MI_DIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- Acumulados
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) ;
         MI_DIA := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS; -- estos dÃ­as de interrupciÃ³n ya fueron descontados en la fecha de ingreso real
         -- los dÃ­as en otra entidad se adicionan ademÃ¡s del tener en cuenta la continuidad
         -- dada en la diferencia de fachas entre la fecha de ingreso y la fecha de ingreso a entidades similares.
         MI_DIA := MI_DIA + MI_MASDIASOTRAENTIDAD;
         MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
         MI_DP := 360;
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,1,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  ELSE --ley 50 
         PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                                  THEN PCK_NOMINA.GL_FECHAIR
                                  ELSE TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                  END;

         MI_MES := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC);
         MI_DIA :=  CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16
                    THEN 1
                    ELSE 2
                    END ;

         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, MI_MES,MI_DIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;--Acumulado del aÃ±o actual                         
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);

         MI_DIA := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 + (CASE WHEN PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIC) = PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAFIN1) AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC) = PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFIN1) THEN 1 ELSE 0 END) ) - PCK_NOMINA.GL_LICENCIAS;--01032019 ajustes nomina intergradas
         MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 31); 

  END IF;

          MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                                           THEN      PCK_NOMINA.GL_FECHAIR
                                                           ELSE      TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                                           END ,     PCK_NOMINA.GL_FECHAFIN1 + ( CASE WHEN PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIC) = PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAFIN1) AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC) = PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFIN1) 
                                                                                                      THEN 1 
                                                                                                      ELSE 0 
                                                                                                      END ))- PCK_NOMINA.GL_LICENCIAS; --01032019

          MI_ANIO :=PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIC);
          MI_MES :=PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC);

          PCK_NOMINA.GL_AC    := PCK_NOMINA.FC_ACUM(UN_COMPANIA,MI_ANIO,MI_MES,3,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
          MI_COMISIONES        := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CN(500)) * 30 / MI_DIA,0);
          PCK_NOMINA.GL_MESES := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);          
          MI_DBLAJSUELDO      := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(518) + PCK_NOMINA.FC_CN(518)) * 30 / MI_DIA ,0);
          MI_DBLREINTEGRO     := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(517) + PCK_NOMINA.FC_CN(517)) * 30 / MI_DIA ,0);
          MI_DESCUENTO        := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(631) + PCK_NOMINA.FC_CN(631)) * 30 / MI_DIA,0);

/*********/
          IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_SALARIO = 'V' THEN
             MI_AUXT1 :=  FC_PROM_AUX(UN_COMPANIA,PCK_NOMINA.GL_SPRC,PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,-1);--FALTA MIGRAR
          ELSE
             MI_AUXT1 := PCK_NOMINA.GL_AUXT;
          END IF;
          MI_SLD_MED_TRIM :=   FC_SUELDO_MEDIA_TRIM(UN_COMPANIA);   --DEBE CREAR FUNCION sueldo_media_trimestre
          --(APINEDA:13/08/2019)-TAR 1000093896 FederaciÃ³n Nacional de Departamentos, se agrega secciÃ³n para las horas extra en retiro y periodo mensual
          IF PCK_NOMINA.FC_CN(404) = 0 THEN  
            MI_DBLPROMHOREXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(50) + PCK_NOMINA.FC_CNA(51) + PCK_NOMINA.FC_CNA(49) + PCK_NOMINA.FC_CNA(56) + PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) / (MI_DIA / 30)  ,0);--'promedio extras ultimo trimestre                                                  
          ELSE
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);          
            MI_DIASLABORANIO := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO) - PCK_NOMINA.GL_DNT;                                        
            MI_DBLPROMHOREXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) / MI_DIASLABORANIO * 30, 0);  
          END IF;                    
          MI_PROMFAC := (MI_SLD_MED_TRIM + MI_COMISIONES + MI_DBLPROMHOREXTRAS + MI_DBLAJSUELDO + MI_DBLREINTEGRO + MI_AUXT1  )- MI_DESCUENTO;
          MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND( (MI_PROMFAC)* MI_DIA / 360,0) - MI_ANTICIPOS;


          IF PCK_NOMINA.FC_CN(411) <> 0 THEN
                   PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0
                                              THEN   CASE WHEN MI_CESANTIA1 < 0 
                                                     THEN 0 
                                                     ELSE PCK_SYSMAN_UTL.FC_ROUND( MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360,0) 
                                                     END
                                              ELSE PCK_NOMINA.FC_CN(169)
                                              END;

                   PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0
                                              THEN MI_CESANTIA1
                                              ELSE PCK_NOMINA.FC_CN(177)
                                              END;

          ELSIF    PCK_NOMINA.FC_CN(404) <> 0 THEN                             
                   PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0
                                              THEN PCK_SYSMAN_UTL.FC_ROUND( MI_PROMFAC * MI_DIA / 360,0)
                                              ELSE PCK_NOMINA.FC_CN(177)
                                              END;

                   PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0
                                              THEN PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.FC_CN(177) * 12 / 100 * MI_DIASINT / 360 ,0)
                                              ELSE PCK_NOMINA.FC_CN(169)
                                              END;


          ELSIF    PCK_NOMINA.FC_CN(412) <> 0  THEN

                   IF NOT (PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996','DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                      PCK_NOMINA.CN(269) :=  CASE WHEN PCK_NOMINA.FC_CN(269) = 0
                                                  THEN CASE WHEN MI_CESANTIA1 < 0 
                                                            THEN 0
                                                            ELSE PCK_SYSMAN_UTL.FC_ROUND( MI_CESANTIA1 * 12 / 100 * MI_DIA / 360,0)
                                                            END
                                                  ELSE PCK_NOMINA.FC_CN(269)
                                                  END;
                   END IF;

                   PCK_NOMINA.CN(277) :=  CASE WHEN  PCK_NOMINA.FC_CN(277) = 0
                                               THEN  MI_CESANTIA1 
                                               ELSE  PCK_NOMINA.FC_CN(277)
                                               END;

                   IF PCK_NOMINA.GL_SMES = 12 THEN
                      PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
                   END IF;

          END IF;

    --Guardando Factores
     PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1); --'SBM                                                     ' Sueldo
     PCK_NOMINA.CN(903) := MI_AUXT1;
     PCK_NOMINA.CN(991) := PCK_SYSMAN_UTL.FC_ROUND(MI_COMISIONES + MI_DBLAJSUELDO + MI_DBLREINTEGRO,0);
     PCK_NOMINA.CN(908) := 0; --' Prima de Localizacion
     PCK_NOMINA.CN(909) := 0; -- Ultimo Quinquenio
     PCK_NOMINA.CN(910) := 0; -- Dias
     PCK_NOMINA.CN(911) := MI_ANTICIPOS; --Anticipos
     PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;--Dias no trabajados por licencias
     PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0); --' Promedio
     PCK_NOMINA.CN(914) := 0;

     IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_CES_BASE := MI_PROMFAC;
        PCK_NOMINA.GL_CONSOLIDADO_CES := MI_PROMFAC;
        PCK_NOMINA.GL_CES_TOTAL := PCK_SYSMAN_UTL.FC_ROUND( MI_PROMFAC * MI_DIA / 360,0) - MI_ANTICIPOS;
        PCK_NOMINA.GL_CES_MESANT := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC * CASE WHEN (MI_DIA - 30) <= 0
                                                                              THEN 0
                                                                              ELSE (MI_DIA - 30)
                                                                              END / 360
                                                            ,0) - MI_ANTICIPOS;

        PCK_NOMINA.GL_CES_PAGOSMES :=PCK_NOMINA.FC_CNA(177);                                                     
        PCK_NOMINA.GL_CES_PRV := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_CES_TOTAL - PCK_NOMINA.GL_CES_MESANT,0);
        PCK_NOMINA.CN(499) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_CES_TOTAL -  PCK_NOMINA.GL_CES_MESANT,0);
        PCK_NOMINA.GL_CONSOLIDADO_CES := PCK_SYSMAN_UTL.FC_ROUND( MI_PROMFAC * MI_DIA / 360,0);   
        PCK_NOMINA.GL_ANTICIPOS_CES := MI_ANTICIPOS;    
        PCK_NOMINA.GL_ICES_DIAS := MI_DIA;  
        PCK_NOMINA.GL_ICES_BASE := MI_PROMFAC;
        PCK_NOMINA.GL_ICES_TOTAL := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_CES_TOTAL * 12 / 100 / 360 * MI_DIA,0);
        PCK_NOMINA.GL_ICES_MESANT := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_CES_TOTAL * 12 / 100 / 360 * CASE WHEN (MI_DIA - 30) <= 0
                                                                                                              THEN 0
                                                                                                              ELSE (MI_DIA - 30)
                                                                                                              END,0); 
        PCK_NOMINA.GL_ICES_PAGOSMES := PCK_NOMINA.FC_CNA(169);                                                                                                               
        PCK_NOMINA.GL_ICES_PRV := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_ICES_TOTAL - PCK_NOMINA.GL_ICES_MESANT,0); 
        PCK_NOMINA.CN(496) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_ICES_TOTAL - PCK_NOMINA.GL_ICES_MESANT,0);    

     END IF;

END PR_CALCULARCESANTIASFND;

FUNCTION FC_PROM_AUX 
/*
  NAME               : PROM_AUX
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 12/07/2019
  TIME               : 12:26 PM
  SOURCE MODULE      : NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : 
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_FECHAI     IN DATE,
    UN_FECHAF     IN DATE,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_PRIMA      IN PCK_SUBTIPOS.TI_LOGICO
)
  RETURN NUMBER
  AS

  MI_MESES          PCK_SUBTIPOS.TI_MES    DEFAULT 0;
  MI_TMESES         PCK_SUBTIPOS.TI_MES    DEFAULT 0;
  MI_DIAST          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_DI             NUMBER :=0;
  MI_RTA            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_RS             SYS_REFCURSOR;
  MIRS_IDPROCESO    PCK_SUBTIPOS.TI_ID_DE_PROCESO;
  MIRS_ANIO         PCK_SUBTIPOS.TI_ANIO;
  MIRS_MES          PCK_SUBTIPOS.TI_MES;
  MIRS_PERIODO      PCK_SUBTIPOS.TI_PERIODO_NOMI;
  MIRS_IDEMPLEADO   PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
  MIRS_C1           PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C186         PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C201         PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C80          PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C81          PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C9           PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C36          PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C500         PCK_SUBTIPOS.TI_DOBLE;
  MIRS_C70          PCK_SUBTIPOS.TI_DOBLE;

BEGIN  

 OPEN MI_RS FOR 
                 SELECT * FROM   ( 
                                      SELECT  ID_DE_PROCESO, ANO, MES, PERIODO,ID_DE_EMPLEADO,ID_DE_CONCEPTO,NVL(VALOR,0)TVALOR
                                      FROM    HISTORICOS
                                      WHERE   HISTORICOS.COMPANIA = UN_COMPANIA
                                              AND ID_DE_PROCESO = UN_PROCESO
                                              AND ANO BETWEEN PCK_SYSMAN_UTL.FC_ANIO( UN_FECHA => UN_FECHAI) AND PCK_SYSMAN_UTL.FC_ANIO( UN_FECHA => UN_FECHAF)
                                              AND MES BETWEEN PCK_SYSMAN_UTL.FC_MES( UN_FECHA => UN_FECHAI)  AND PCK_SYSMAN_UTL.FC_MES( UN_FECHA => UN_FECHAF)
                                              AND PERIODO = 3
                                              AND ID_DE_EMPLEADO = UN_IDEMPLEADO
                                              AND HISTORICOS.ID_DE_CONCEPTO IN (1,186,201,80,81,9,36,500,70)
                                ) 
                         PIVOT (SUM(TVALOR)    
                         FOR (ID_DE_CONCEPTO) IN ( 1 AS C1,186 AS C186,201 AS C201,80 AS C80,81 AS C81 ,9 AS C9,36 AS C36,500 AS C500,70 AS C70  ));
 LOOP
 FETCH MI_RS
 INTO  MIRS_IDPROCESO,
       MIRS_ANIO,
       MIRS_MES,
       MIRS_PERIODO,
       MIRS_IDEMPLEADO,
       MIRS_C1,           
       MIRS_C186,        
       MIRS_C201,        
       MIRS_C80,          
       MIRS_C81,          
       MIRS_C9,          
       MIRS_C36,          
       MIRS_C500,         
       MIRS_C70;

       IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN

                 IF ( NVL(PCK_NOMINA.FC_CN(1),0) + NVL(PCK_NOMINA.FC_CN(186),0) ) <= 2  * NVL(PCK_NOMINA.FC_CN(201),0) THEN
                    MI_RTA:= NVL(PCK_NOMINA.FC_CN(81),0);
                    MI_TMESES := MI_TMESES + 1;
                 END IF;
                 EXIT WHEN MI_RS%NOTFOUND ;

       ELSIF MI_RS%NOTFOUND  THEN 

          EXIT WHEN MI_RS%NOTFOUND ;

       ELSE

              MI_TMESES := MI_TMESES + 1 ;

              IF NOT (PCK_NOMINA.GL_SANO = MIRS_ANIO AND PCK_NOMINA.GL_SMES = MIRS_MES) THEN
                       IF NVL(MIRS_C36,0) > 0 AND NVL(MIRS_C36,0) < 30 AND NVL(MIRS_C80,0) <> 0 THEN
                             MI_RTA := MI_RTA + (NVL(MIRS_C80,0) / MIRS_C36) * 30;
                       ELSIF ( NVL(MIRS_C1,0) + NVL(MIRS_C186,0) + NVL(MIRS_C500,0) )< 2 * NVL(MIRS_C201,0) THEN   
                             MI_RTA := MI_RTA + NVL(MIRS_C81,0);
                       END IF;
              ELSE
                       IF ( NVL(PCK_NOMINA.FC_CN(1),0) + NVL(PCK_NOMINA.FC_CN(186),0) + NVL(PCK_NOMINA.FC_CN(500),0) ) < 2 * NVL(PCK_NOMINA.FC_CN(201),0)   THEN
                            MI_RTA := MI_RTA + NVL(PCK_NOMINA.FC_CN(81),0);
                       END IF;
              END IF;

       END IF;
    END LOOP;  

      IF  MI_TMESES <> 0 THEN
          MI_RTA := MI_RTA / MI_TMESES ;
      END IF;

      CLOSE MI_RS; 
      RETURN MI_RTA;
 END FC_PROM_AUX ;

FUNCTION FC_SUELDO_MEDIA_TRIM 
/*
  NAME               : SUELDO_MEDIA_TRIMESTRE
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 15/07/2019
  TIME               : 12:30 PM
  SOURCE MODULE      : NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : 
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
  AS  
  MI_MESACTUAL          PCK_SUBTIPOS.TI_DOBLE    DEFAULT 0;
  MI_MESPENULTIMO       PCK_SUBTIPOS.TI_DOBLE    DEFAULT 0;
  MI_MESANTEPENULTIMO   PCK_SUBTIPOS.TI_DOBLE    DEFAULT 0;
  MI_ANIOAUX            PCK_SUBTIPOS.TI_ANIO     DEFAULT 0;
  MI_VALOR              PCK_SUBTIPOS.TI_DOBLE    DEFAULT 0;
  MI_RTA                PCK_SUBTIPOS.TI_DOBLE    DEFAULT 0;
  BEGIN 
               BEGIN

                        SELECT NVL(VALOR,0)
                        INTO   MI_VALOR
                        FROM   HISTORICOS
                        WHERE  COMPANIA = UN_COMPANIA
                               AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                               AND ANO = PCK_NOMINA.GL_SANO
                               AND MES = PCK_NOMINA.GL_SMES
                               AND PERIODO = 3
                               AND ID_DE_CONCEPTO = 1
                               AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;


                         MI_MESACTUAL :=  MI_VALOR;
               EXCEPTION WHEN NO_DATA_FOUND THEN
                         MI_MESACTUAL := NVL(PCK_NOMINA.FC_CN(1),0);
               END;

                 MI_ANIOAUX := CASE WHEN PCK_NOMINA.GL_SMES > 1 
                                    THEN PCK_NOMINA.GL_SANO
                                    ELSE PCK_NOMINA.GL_SANO - 1
                                    END ;
              BEGIN
                               SELECT VALOR 
                               INTO   MI_VALOR
                               FROM   HISTORICOS
                               WHERE  COMPANIA = UN_COMPANIA
                                      AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                                      AND ANO = MI_ANIOAUX
                                      AND MES = CASE WHEN PCK_NOMINA.GL_SMES > 1 
                                                     THEN PCK_NOMINA.GL_SMES - 1
                                                     ELSE 12
                                                     END
                                      AND PERIODO = 3 
                                      AND ID_DE_CONCEPTO = 1
                                      AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;


                        MI_MESPENULTIMO := MI_VALOR;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_MESPENULTIMO := MI_MESACTUAL; 
              END;
              BEGIN

                                     SELECT VALOR 
                                     INTO   MI_VALOR
                                     FROM   HISTORICOS
                                     WHERE  COMPANIA = UN_COMPANIA
                                            AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                                            AND ANO = MI_ANIOAUX
                                            AND MES = CASE WHEN PCK_NOMINA.GL_SMES > 1 
                                                           THEN PCK_NOMINA.GL_SMES - 2
                                                           ELSE 11
                                                           END
                                            AND PERIODO = 3 
                                            AND ID_DE_CONCEPTO = 1
                                            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                        MI_MESANTEPENULTIMO := MI_VALOR;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_MESANTEPENULTIMO := MI_MESACTUAL ;
              END;            
              MI_RTA := PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR => ( MI_MESACTUAL + MI_MESPENULTIMO + MI_MESANTEPENULTIMO ) / 3 ,UN_PRECISION => 0);   
  RETURN MI_RTA;
  END FC_SUELDO_MEDIA_TRIM; 

PROCEDURE PR_CALPRIMADEVACACIONESFND 
  /*
  NAME               : CALCULARPRIMADEVACACIONESFND
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 16/07/2019
  TIME               : 08:05 AM
  SOURCE MODULE      : NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LA PRIMA DE VACACIONES  PARA FEDERACIÃ“N DE DEPARTAMENTOS
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARPRIMADEVACACIONESFND
  */    
(

     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
MI_ANIO               PCK_SUBTIPOS.TI_ANIO        DEFAULT 0;
MI_MES                PCK_SUBTIPOS.TI_MES         DEFAULT 0;
MI_DBLCOM             PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0;
MI_DIASP              PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0;
MI_TDIASP_VACAC       PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0;
MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_PVCOMPLETA         PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0; 
MI_DBLSUELDO          PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0; 
MI_DIAS_PVACRETIRO    PCK_SUBTIPOS.TI_ENTERO      DEFAULT 0; --En Access DIASPROPORCIONALESDEVACACIONESENRETIRO
--(APINEDA:13/08/2019)-Variables para tomar valor horas extra en retiro de acuerdo a TAR 1000093896 FederaciÃ³n Nacional de Departamentos
MI_EXTRAS             PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0; 
MI_DIASLABORANIO      PCK_SUBTIPOS.TI_DOBLE       DEFAULT 0;
BEGIN
      IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11'  THEN
             PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(170),  UN_PRECISION => 0 ) / 2 ;
      ELSE
          PCK_NOMINA.GL_DIASVAC := 0;
          PCK_NOMINA.GL_DIASPENDIENTES :=0;
          PCK_NOMINA.GL_PENDIENTES :=0;
          PCK_NOMINA.GL_LICENCIAS :=0;
          PCK_NOMINA.GL_PRIMAPROPORCIONAL :=0;
          PCK_NOMINA.GL_DINEROPROPORCIONAL :=0;

          PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL
                                        THEN PCK_NOMINA.GL_FECHAI 
                                        ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL
                                        END;

          IF PCK_NOMINA.FC_CN(404) <> 0 THEN 

           MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO( UN_FECHA => PCK_NOMINA.GL_FECHAUV);
           MI_MES  := PCK_SYSMAN_UTL.FC_MES( UN_FECHA => PCK_NOMINA.GL_FECHAUV);

               PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA   => UN_COMPANIA,
                                                       UN_ANO1       => MI_ANIO,
                                                       UN_MES1       => MI_MES,
                                                       UN_PERIODO1   => 1,
                                                       UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                       UN_MES2       => PCK_NOMINA.GL_SMES,
                                                       UN_PERIODO2   => 99,
                                                       UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                      );

          PCK_NOMINA.GL_LICENCIAS := ( PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339))
                                          + CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL 
                                                 THEN PCK_NOMINA.FC_CESANTIA(  UN_COMPANIA  =>   UN_COMPANIA, UN_EMPLEADO  =>   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, UN_PARAMETRO =>   'L')
                                                 ELSE 0 
                                                 END
                                          + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;

          PCK_NOMINA.GL_DIASPENDIENTES :=  PCK_NOMINA.FC_CNA(91); 
          PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL( UN_FECHAIN => CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL
                                                                            THEN PCK_NOMINA.GL_FECHAI
                                                                            ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1
                                                                            END
                                                                       , UN_FECHAFIN => PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_LICENCIAS ;                                
          PCK_NOMINA.GL_PERIODOS :=  TRUNC(PCK_NOMINA.GL_DTV/ 360);
          PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS ,  UN_PRECISION => 2 );
          PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM(  UN_COMPANIA   => UN_COMPANIA ,
                                                    UN_ANO1       => (PCK_NOMINA.GL_SANO - 1) ,
                                                    UN_MES1       => ( PCK_NOMINA.GL_SMES + 1),
                                                    UN_PERIODO1   => 1,
                                                    UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                    UN_MES2       => PCK_NOMINA.GL_SMES,
                                                    UN_PERIODO2   => 3,
                                                    UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                  );
          PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CNA(9) + PCK_NOMINA.FC_CN(9); 
          MI_DBLCOM := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => (PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CN(500)) * 30 / PCK_NOMINA.GL_DCC ,  UN_PRECISION => 0 );
          MI_DBLSUELDO := FC_SUELDO_MEDIA_TRIM( UN_COMPANIA   => UN_COMPANIA) ;
          --HORAS EXTRA
          --(APINEDA:13/08/2019)-En retiro de acuerdo a TAR 1000093896 FederaciÃ³n Nacional de Departamentos
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA
                                              ,UN_ANO1       => PCK_NOMINA.GL_SANO
                                              ,UN_MES1       => 1
                                              ,UN_PERIODO1   => 1
                                              ,UN_ANO2       => PCK_NOMINA.GL_SANO
                                              ,UN_MES2       => PCK_NOMINA.GL_SMES
                                              ,UN_PERIODO2   => 99
                                              ,UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); 
          PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);          
          --DIAS TRABAJADOS EN EL AÃ‘O          
          MI_DIASLABORANIO := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO) - PCK_NOMINA.GL_DNT;                                                            
          MI_EXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) / MI_DIASLABORANIO * 30, 0);  
          --(13/08/2019)-TAR 1000093896 Se agregan las horas extra como factor para el calculo de las cesantias e intereses de cesantias.
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_DBLSUELDO + MI_DBLCOM + MI_EXTRAS,  UN_PRECISION => 0 );
          MI_DIASP := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_DTV * 15 / 360 ,  UN_PRECISION => 2 );                      

          MI_TDIASP_VACAC := FC_TOTALDIASPENDIENTESVAC(UN_COMPANIA => UN_COMPANIA
                                                      ,UN_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                      ,UN_FECHAF   => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);					 

         IF MI_TDIASP_VACAC > MI_DIASP THEN
                  MI_DIASP := MI_TDIASP_VACAC;
                  MI_MSG(1).CLAVE :=  'EMPLEADO';
                  MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;

                  PCK_NOMINA_COM7.PR_ALERTA
                                          ( UN_COMPANIA    => UN_COMPANIA,
                                            UN_MENSAJE_COD => PCK_ERRORES.ALER_NOVEDADDIAS_PENDIENTES,
                                            UN_REEMPLAZOS  => MI_MSG ,
                                            UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL,
                                            UN_ANO          => PCK_NOMINA.GL_ANOACTUAL,
                                            UN_MES          => PCK_NOMINA.GL_MESACTUAL,
                                            UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL,
                                            UN_USER         => PCK_CONEXION.FC_GETUSER
                                           );
          END IF;
                 IF PCK_NOMINA.FC_CN(175) = 0  THEN 
                    PCK_NOMINA.CN(175) :=  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * MI_DIASP ,  UN_PRECISION => 0 );
                    IF PCK_NOMINA.GL_SPRC = 99 THEN
                       PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
                    ELSE
                       PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_DINEROPROPORCIONAL + PCK_NOMINA.GL_PENDIENTES; 
                    END IF;          
                 END IF;
                 PCK_NOMINA.CN(96) := MI_DIASP;--(11/03/2020 JORDUZ) Se agrega valor al concepto NUMERO DE DIAS VACACIONES EN DINERO ya que no se crea el concepto en historicos y no se muestra e el volante de pago
                 IF PCK_NOMINA.GL_SPRC = 99 THEN                 
                       IF PCK_PARST.FC_PAR( UN_PARAMETRO => 'ENTIDAD PUBLICA O PRIVADA', UN_VLOMISION => '') = 'PRIVADA' THEN 

                             PCK_NOMINA.GL_PV_DIAS := 0;
                             PCK_NOMINA.GL_PV_BASE := 0;
                             PCK_NOMINA.GL_PV_TOTAL := 0;
                             PCK_NOMINA.GL_PV_MESANT := 0;
                             PCK_NOMINA.GL_PV_PAGOSMES := 0;
                             PCK_NOMINA.GL_PV_PRV := 0;
                             PCK_NOMINA.CN(491) := 0;

                        ELSE   
                             PCK_NOMINA.GL_DIASVAC := 0;
                             PCK_NOMINA.GL_DIASVAC := PCK_NOMINA.GL_DTV;
                             MI_PVCOMPLETA := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * 15 ,  UN_PRECISION => 0 );
                             PCK_NOMINA.CN(494) := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV,  UN_PRECISION => 0 ) / 30 * 15 ,  UN_PRECISION => 0 ) / 360 * PCK_NOMINA.GL_DIASVAC,  UN_PRECISION => 0 );
                             PCK_NOMINA.GL_PV_DIAS :=   PCK_NOMINA.GL_DTV;
                             PCK_NOMINA.GL_PV_BASE :=   PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV,  UN_PRECISION => 0 );
                             PCK_NOMINA.GL_PV_TOTAL :=  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV,  UN_PRECISION => 0 ) / 30 * 15,  UN_PRECISION => 0 ) / 360 * PCK_NOMINA.GL_DIASVAC  ,  UN_PRECISION => 0 );
                             PCK_NOMINA.GL_PV_MESANT := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_NOMINA.GL_FACTORESPV / 30 * 15 / 360 * CASE WHEN (PCK_NOMINA.GL_DTV - 30) <= 0 
                                                                                                                                 THEN 0 
                                                                                                                                 ELSE (PCK_NOMINA.GL_DTV - 30)
                                                                                                                                 END
                                                                                                                                 ,  UN_PRECISION => 0 );
                             PCK_NOMINA.GL_PV_PAGOSMES := PCK_NOMINA.CNA(155);
                             PCK_NOMINA.GL_PV_PRV := PCK_NOMINA.FC_CN(494) - PCK_NOMINA.GL_PV_MESANT;
                             PCK_NOMINA.CN(494) :=  PCK_NOMINA.FC_CN(494) - PCK_NOMINA.GL_PV_MESANT;        
                        END IF;

                        PCK_NOMINA.GL_DIASVAC := PCK_NOMINA.GL_DTV;
                        IF PCK_PARENTR.PARAMETRO31 = '890.481.123-1' THEN
                             MI_DIAS_PVACRETIRO := 15;
                        ELSE
                             MI_DIAS_PVACRETIRO := TO_NUMBER(PCK_PARST.FC_PAR(UN_PARAMETRO => 'DIAS PROPORCIONALES DE VACACIONES EN RETIRO', UN_VLOMISION =>  '0'));
                        END IF;

                          PCK_NOMINA.CN(497) := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>   PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV ,  UN_PRECISION => 0 ) / 30  * MI_DIAS_PVACRETIRO ,  UN_PRECISION => 0 ) / 360 * PCK_NOMINA.GL_DIASVAC  ,  UN_PRECISION => 0 );
                          PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DIASVAC;
                          PCK_NOMINA.GL_VACD_BASE := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV ,  UN_PRECISION => 0 );
                          PCK_NOMINA.GL_VACD_TOTAL := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>   PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV ,  UN_PRECISION => 0 ) / 30  * MI_DIAS_PVACRETIRO ,  UN_PRECISION => 0 ) / 360 * PCK_NOMINA.GL_DIASVAC  ,  UN_PRECISION => 0 );
                          PCK_NOMINA.GL_VACD_MESANT := PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR =>  PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV ,  UN_PRECISION => 0 ) / 30  * MI_DIAS_PVACRETIRO ,  UN_PRECISION => 0 ) / 360 * CASE WHEN (PCK_NOMINA.GL_DIASVAC - 30 ) <= 0 THEN 0 ELSE (PCK_NOMINA.GL_DIASVAC - 30 ) END   ,  UN_PRECISION => 0 );
                          PCK_NOMINA.GL_VACD_PAGOSMES := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.FC_CN(174);
                          PCK_NOMINA.GL_VACD_PRV :=  PCK_NOMINA.FC_CN(497) + PCK_NOMINA.GL_VACD_MESANT ;
                          PCK_NOMINA.CN(497) := PCK_NOMINA.FC_CN(497) + PCK_NOMINA.GL_VACD_MESANT ;
                 END IF;
          ELSE  
                     PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM( UN_COMPANIA    => UN_COMPANIA,
                                                              UN_ANO1        => (PCK_NOMINA.GL_SANO - 1),
                                                              UN_MES1        => ( PCK_NOMINA.GL_SMES + 1),
                                                              UN_PERIODO1    => 1,
                                                              UN_ANO2        => PCK_NOMINA.GL_SANO,
                                                              UN_MES2        => PCK_NOMINA.GL_SMES,
                                                              UN_PERIODO2    => 3,
                                                              UN_IDEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                            );
                     PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CNA(9) + PCK_NOMINA.FC_CN(9);
                     MI_DBLCOM := 0;
                     MI_DBLSUELDO := PCK_NOMINA.FC_CN(1);

                     PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM( UN_COMPANIA    => UN_COMPANIA ,
                                                              UN_ANO1        => PCK_SYSMAN_UTL.FC_ANIO( UN_FECHA => PCK_NOMINA.GL_FECHAUV) ,
                                                              UN_MES1        => PCK_SYSMAN_UTL.FC_MES( UN_FECHA => PCK_NOMINA.GL_FECHAUV),
                                                              UN_PERIODO1    => 1,
                                                              UN_ANO2        => PCK_NOMINA.GL_SANO,
                                                              UN_MES2        => PCK_NOMINA.GL_SMES,
                                                              UN_PERIODO2    => 99,
                                                              UN_IDEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                            );

                    PCK_NOMINA.GL_DIASPENDIENTES :=  PCK_NOMINA.FC_CNA(91); 
                    IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN 

                       MI_MSG(1).CLAVE :=  'EMPLEADO';
                       MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                       MI_MSG(2).CLAVE := 'DIAS';
                       MI_MSG(2).VALOR := PCK_NOMINA.GL_DIASPENDIENTES;

                       PCK_NOMINA_COM7.PR_ALERTA
                                            ( UN_COMPANIA    => UN_COMPANIA,
                                              UN_MENSAJE_COD => PCK_ERRORES.ALER_DIASPENDIENTESVACACIONES,
                                              UN_REEMPLAZOS  => MI_MSG ,
                                              UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL,
                                              UN_ANO          => PCK_NOMINA.GL_ANOACTUAL,
                                              UN_MES          => PCK_NOMINA.GL_MESACTUAL,
                                              UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL,
                                              UN_USER         => PCK_CONEXION.FC_GETUSER
                                             );
                    END IF;
                    PCK_NOMINA.CN(93):= PCK_NOMINA.FC_CN(68) + PCK_NOMINA.FC_CN(164);
                    PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM(  UN_COMPANIA    =>  UN_COMPANIA,
                                                              UN_ANO1        => (PCK_NOMINA.GL_SANO - 1),
                                                              UN_MES1        => ( PCK_NOMINA.GL_SMES + 1),
                                                              UN_PERIODO1    => 1,
                                                              UN_ANO2        => PCK_NOMINA.GL_SANO,
                                                              UN_MES2        => PCK_NOMINA.GL_SMES,
                                                              UN_PERIODO2    => 99,
                                                              UN_IDEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                            );                                                                        
                    PCK_NOMINA.GL_FACTORESPV :=  MI_DBLCOM + MI_DBLSUELDO;
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN                                
                            IF PCK_NOMINA.FC_CN(403) <> 0 THEN  
                               PCK_NOMINA.CN(174):= CASE WHEN PCK_NOMINA.FC_CN(174) = 0 
                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94),  UN_PRECISION => 0 )
                                                         ELSE PCK_NOMINA.FC_CN(174)
                                                         END;
                            END IF;                                         
                            IF PCK_NOMINA.FC_CN(419) <> 0 THEN 

                               PCK_NOMINA.CN(175):= CASE WHEN PCK_NOMINA.FC_CN(175) = 0 
                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96),  UN_PRECISION => 0 )
                                                         ELSE PCK_NOMINA.FC_CN(175)
                                                         END;
                            END IF;                                  
                    ELSE                                   
                            IF PCK_NOMINA.FC_CN(403) <> 0 THEN  
                               PCK_NOMINA.CN(174):= CASE WHEN PCK_NOMINA.FC_CN(174) = 0 
                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94),  UN_PRECISION => 0 )
                                                         ELSE PCK_NOMINA.FC_CN(174)
                                                         END;
                            END IF;

                            IF PCK_NOMINA.FC_CN(419) <> 0 THEN 

                               PCK_NOMINA.CN(175):= CASE WHEN PCK_NOMINA.FC_CN(175) = 0 
                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(  UN_VALOR => PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96),  UN_PRECISION => 0 )
                                                         ELSE PCK_NOMINA.FC_CN(175)
                                                         END;
                            END IF;                                   
                    END IF;            
              END IF;
      END IF;      
      --(APINEDA:13/08/2019)-Se realiza correcciÃ³n de lÃ­nea migrada cambiando PCK_NOMINA.FC_CN(404) <> 0 por PCK_NOMINA.FC_CN(404) = 0.
      IF PCK_PARST.FC_PAR(UN_PARAMETRO => 'CALCULAR INDEMNIZACION DE VACACIONES CON NOVEDAD DE RETIRO', UN_VLOMISION => '') = 'SI'  AND PCK_NOMINA.FC_CN(404) = 0 THEN      
         PCK_NOMINA.CN(176):=  CASE WHEN PCK_NOMINA.FC_CN(176) = 0 
                                    THEN PCK_NOMINA.FC_CN(175)
                                    ELSE PCK_NOMINA.FC_CN(176)
                                    END ;
         PCK_NOMINA.CN(175):= 0;      
      ELSE    
         PCK_NOMINA.CN(175):=  CASE WHEN PCK_NOMINA.FC_CN(175) = 0 
                                    THEN PCK_NOMINA.FC_CN(175)
                                    ELSE PCK_NOMINA.FC_CN(175)
                                    END ;

      END IF;

      PCK_NOMINA.CN(960):= PCK_NOMINA.GL_FACTORESPV;
      IF PCK_NOMINA.GL_SPRC = 99 THEN

         PCK_NOMINA.CN(961):= PCK_NOMINA.FC_CN(93);
         PCK_NOMINA.CN(962):= PCK_NOMINA.GL_PERIODOS ;

      ELSE
         PCK_NOMINA.CN(961):= PCK_NOMINA.FC_CN(94);
         PCK_NOMINA.CN(962):= PCK_NOMINA.FC_CN(164);

      END IF;

      PCK_NOMINA.CN(963):= PCK_NOMINA.GL_LICENCIAS;
      PCK_NOMINA.CN(964):= PCK_NOMINA.GL_DIASPENDIENTES;
      PCK_NOMINA.CN(965):= PCK_NOMINA.GL_PRIMAPROPORCIONAL ;
      PCK_NOMINA.CN(966):= PCK_NOMINA.GL_DINEROPROPORCIONAL ;
      PCK_NOMINA.CN(967):= PCK_NOMINA.GL_PENDIENTES ;
      PCK_NOMINA.CN(968):= PCK_NOMINA.GL_DIASPROPORCIONAL ;
      PCK_NOMINA.CN(975):= CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                THEN PCK_NOMINA.FC_CN(10)
                                ELSE PCK_NOMINA.FC_CN(1)
                                END ;
END PR_CALPRIMADEVACACIONESFND;  

FUNCTION FC_TOTALDIASPENDIENTESVAC
 /*
    NAME              : TOTAL_DIAS_PENDIENTES_VACACIONES
    AUTHOR MIGRACION  : CAMILO ANDRÃ‰S PÃ‰REZ DUEÃ‘AS
    DATE MIGRADOR     : 17/07/2019
    TIME              : 14:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : FUNCIÃ“N QUE CALCULA EL TOTAL DE DÃ�AS PENDIENTES PARA LAS VACACIONES 
	                      SE TOMA DE LA VERSION DE NOMINA NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ.accdb
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_EMPLEADO    => EMPLEADO A LIQUIDAR
                        UN_FECHAF      => FECHA TERMINACIÃ“N DEL CONTRATO DEL EMPLEADO                        
    @NAME:  FC_TOTALDIASPENDIENTESVAC                    
  */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHAF         IN PCK_SUBTIPOS.TI_FECHA
)
RETURN NUMBER AS   
  MI_RESPUESTA                PCK_SUBTIPOS.TI_DOBLE          :=0;
  MI_CUENTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO   :=0;
  MI_DIASHABILESPAGADOS       PCK_SUBTIPOS.TI_ENTERO         :=0;
  MI_RS2_DIASHABILESPAGADOS   PCK_SUBTIPOS.TI_ENTERO         :=0;
  MI_RS                       SYS_REFCURSOR                     ;
  MI_RS_FECHA_DE_INGRESO      PCK_SUBTIPOS.TI_FECHA             ;
  --(APINEDA:05/08/2019)-Se reemplaza FECHA_DE_RETIRO por FECHATERCONTRATO y se eliminan sumas y restas sobre esta fecha en toda la funciÃ³n.
  MI_RS_FECHATERCONTRATO      PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_ANO                   PCK_SUBTIPOS.TI_ANIO              ;
  MI_RS_MES                   PCK_SUBTIPOS.TI_MES               ;
  MI_RS_INICIO_DISFRUTE       PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_FINAL_DISFRUTE        PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_FECHA_INICIO          PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_FECHA_FINAL           PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_FECHAPAGO             PCK_SUBTIPOS.TI_FECHA             ;
  MI_RS_ID_DE_EMPLEADO        PCK_SUBTIPOS.TI_ID_DE_EMPLEADO    ;
  MI_RS_DIAS                  PCK_SUBTIPOS.TI_DIA               ;
  MI_RS_DIASHABILES           PCK_SUBTIPOS.TI_DIA               ;
  MI_RS_NUMPERIODOS           PCK_SUBTIPOS.TI_PERIODO           ;
  MI_RS_DIASDINERO            PCK_SUBTIPOS.TI_DIA               ;
  MI_TTDIAS                   PCK_SUBTIPOS.TI_DOBLE          :=0;
  MI_FECHAF                   PCK_SUBTIPOS.TI_FECHA             ;
  MI_LICENCIASFECHAS          PCK_SUBTIPOS.TI_ENTERO_LARGO   :=0;
  MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR; 
BEGIN
    MI_FECHAF := UN_FECHAF;
    BEGIN
        --08/01/2014 JP si no existe el empleado no genere mensaje
        SELECT  COUNT(0)
        INTO MI_CUENTA
        FROM PERSONAL 
        WHERE COMPANIA       = UN_COMPANIA  
          AND ID_DE_EMPLEADO = UN_EMPLEADO;
        IF MI_CUENTA = 0 THEN 
            RETURN MI_RESPUESTA;
        END IF; 
    EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN MI_RESPUESTA;
    END;
    MI_CUENTA :=0;
    MI_DIASHABILESPAGADOS := 0;
    --<VACACIONESPERSONAL>
    OPEN MI_RS FOR SELECT PER.FECHA_DE_INGRESO
                         ,PER.FECHATERCONTRATO
                         ,VAC.ANO
                         ,VAC.MES
                         ,VAC.INICIO_DISFRUTE
                         ,VAC.FINAL_DISFRUTE
                         ,VAC.FECHA_INICIO
                         ,VAC.FECHA_FINAL
                         ,VAC.FECHAPAGO
                         ,VAC.ID_DE_EMPLEADO
                         ,VAC.DIAS
                         ,VAC.DIASHABILES
                         ,VAC.NUMPERIODOS
                         ,VAC.DIASDINERO
                   FROM VACACIONES VAC
                     LEFT JOIN PERSONAL PER 
                       ON  VAC.COMPANIA       = PER.COMPANIA 
                       AND VAC.ID_DE_EMPLEADO = PER.ID_DE_EMPLEADO 
                   WHERE VAC.COMPANIA       =  UN_COMPANIA 
                     AND VAC.ID_DE_EMPLEADO =  UN_EMPLEADO
                   ORDER BY VAC.ANO
                           ,VAC.MES
                           ,VAC.INICIO_DISFRUTE
                           ,VAC.FECHAPAGO;
    LOOP FETCH MI_RS
        INTO   MI_RS_FECHA_DE_INGRESO
              ,MI_RS_FECHATERCONTRATO
              ,MI_RS_ANO
              ,MI_RS_MES
              ,MI_RS_INICIO_DISFRUTE
              ,MI_RS_FINAL_DISFRUTE
              ,MI_RS_FECHA_INICIO 
              ,MI_RS_FECHA_FINAL
              ,MI_RS_FECHAPAGO 
              ,MI_RS_ID_DE_EMPLEADO
              ,MI_RS_DIAS
              ,MI_RS_DIASHABILES
              ,MI_RS_NUMPERIODOS 
              ,MI_RS_DIASDINERO;
        IF MI_RS%ROWCOUNT = 0 AND MI_RS%NOTFOUND THEN --SI NO EXISTEN DATOS
            --cuando no tiene vacaciones, debe contrat solamente desde la fecha de ingreso. no toma registros de vacaicones porque no necesiten datos.
            MI_RESPUESTA := 0;
            BEGIN
                SELECT P.FECHA_DE_INGRESO
                      ,P.FECHATERCONTRATO
                      ,P.ID_DE_EMPLEADO
                INTO  MI_RS_FECHA_DE_INGRESO
                     ,MI_RS_FECHATERCONTRATO
                     ,MI_RS_ID_DE_EMPLEADO
                FROM PERSONAL P 
                WHERE P.COMPANIA       = UN_COMPANIA 
                  AND P.ID_DE_EMPLEADO = UN_EMPLEADO;

                MI_TTDIAS := 0;
                IF MI_FECHAF > MI_RS_FECHATERCONTRATO THEN
                    MI_FECHAF := MI_RS_FECHATERCONTRATO;
                END IF;
                IF PCK_SYSMAN_UTL.FC_DIA(MI_FECHAF) = 31 THEN
                    MI_FECHAF := MI_FECHAF - 1;
                END IF;
                MI_TTDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => MI_RS_FECHA_DE_INGRESO
                                                               ,UN_FECHAFIN => MI_FECHAF); -- MESES DE 30 AÃ‘O DE 360 DIAS, YA QUE SIMPRE SE LIQUIDAR POPRPORCIONAL A 360 DIAS.
                MI_LICENCIASFECHAS := PCK_NOMINA.FC_AUSENTISMOEMPLEADO(UN_COMPANIA     => UN_COMPANIA
                                                                      ,UN_IDEMPLEADO   => UN_EMPLEADO
                                                                      ,UN_FECHAINICIO  => MI_RS_FECHA_DE_INGRESO
                                                                      ,UN_FECHAFINAL   => MI_FECHAF);
                MI_TTDIAS := MI_TTDIAS - MI_LICENCIASFECHAS;
                MI_RESPUESTA := PCK_SYSMAN_UTL.FC_ROUND((MI_TTDIAS * 15 / 360), 2);
            /*
              * SE VALIDA SI NO EXISTE EL EMPLEADO
            */         
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;          
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_NOMINA THEN
                    MI_MSGERROR(1).CLAVE := 'CODIGO';
                    MI_MSGERROR(1).VALOR := UN_EMPLEADO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                              ,UN_ERROR_COD   => PCK_ERRORES.ERR_N_FM_NDF_VERIFICAPERSONAL
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
                END; 
            END;
            EXIT WHEN MI_RS%NOTFOUND ;           
        ELSIF MI_RS%NOTFOUND  THEN -- SI EXISTEN DATOS Y YA TERMINO DE PROCESAR
            EXIT WHEN MI_RS%NOTFOUND ;
        ELSE-- SI EXISTEN DATOS
            MI_CUENTA := 1;
            MI_DIASHABILESPAGADOS := MI_DIASHABILESPAGADOS 
                                     + MI_RS_DIASHABILES 
                                     + (CASE WHEN MI_RS_DIASHABILES > 0 
                                           THEN 0
                                           ELSE MI_RS_DIASDINERO
                                        END);
            IF MI_RS_INICIO_DISFRUTE > MI_RS_FECHA_DE_INGRESO THEN
                MI_RS2_DIASHABILESPAGADOS := MI_RS2_DIASHABILESPAGADOS 
                                             + MI_RS_DIASHABILES
                                             + MI_RS_DIASDINERO;
            END IF;
        END IF;
    END LOOP; --<VACACIONESPERSONAL>
    CLOSE MI_RS; 
    IF MI_CUENTA = 1 THEN 
        MI_TTDIAS := 0;
        IF MI_FECHAF > MI_RS_FECHATERCONTRATO THEN
            MI_FECHAF := MI_RS_FECHATERCONTRATO;
        END IF;
        IF PCK_SYSMAN_UTL.FC_DIA(MI_FECHAF) = 31 THEN
            MI_FECHAF := MI_FECHAF - 1;
        END IF;
        MI_TTDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => MI_RS_FECHA_DE_INGRESO
                                                       ,UN_FECHAFIN => MI_FECHAF ); -- MESES DE 30 AÃ‘O DE 360 DIAS, YA QUE SIMPRE SE LIQUIDAR PPoRPORCIONAL A 360 DIAS.


        MI_LICENCIASFECHAS := PCK_NOMINA.FC_AUSENTISMOEMPLEADO(UN_COMPANIA     => UN_COMPANIA
                                                              ,UN_IDEMPLEADO   => UN_EMPLEADO
                                                              ,UN_FECHAINICIO  => MI_RS_FECHA_DE_INGRESO
                                                              ,UN_FECHAFINAL   => MI_FECHAF);
        MI_TTDIAS    := MI_TTDIAS - MI_LICENCIASFECHAS;
       --(01/10/2020: JALFONSO) se toman en cuenta los dias pagados en dinero para el concepto 175 ya que se le restaban unicamente los dias habiles de vacaciones,para el calculo total del concepto        
        MI_RESPUESTA := PCK_SYSMAN_UTL.FC_ROUND((MI_TTDIAS * 15 / 360), 2) - MI_RS2_DIASHABILESPAGADOS;
        --cuando es negativo, es porque cambiaron fecha de ingreso a personal que ya tenia vacaciones,
        --ej empleado 832, tiene que contar vacaciones mayores a fecha de ingreso. del nuevo registro.
        IF MI_RESPUESTA < 0 THEN 
            MI_DIASHABILESPAGADOS := MI_RS2_DIASHABILESPAGADOS;
            MI_TTDIAS := 0;
            IF PCK_SYSMAN_UTL.FC_DIA(MI_FECHAF) = 31 THEN
                MI_FECHAF := MI_FECHAF - 1;
            END IF;
            MI_TTDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => MI_RS_FECHA_DE_INGRESO
                                                           ,UN_FECHAFIN => MI_FECHAF ); -- MESES DE 30 AÃ‘O DE 360 DIAS, YA QUE SIMPRE SE LIQUIDAR POPRPORCIONAL A 360 DIAS.
            MI_LICENCIASFECHAS := PCK_NOMINA.FC_AUSENTISMOEMPLEADO(UN_COMPANIA     => UN_COMPANIA
                                                                  ,UN_IDEMPLEADO   => UN_EMPLEADO
                                                                  ,UN_FECHAINICIO  => MI_RS_FECHA_DE_INGRESO
                                                                  ,UN_FECHAFINAL   => MI_FECHAF);
            MI_TTDIAS := MI_TTDIAS - MI_LICENCIASFECHAS;
            MI_RESPUESTA := PCK_SYSMAN_UTL.FC_ROUND((MI_TTDIAS * 15 / 360), 2) - MI_DIASHABILESPAGADOS;
        END IF;
    END IF; 
    RETURN MI_RESPUESTA;
END FC_TOTALDIASPENDIENTESVAC; 

PROCEDURE PR_CALCULARPRIMASEMESTRALFND
 /*
    NAME              : CALCULARPRIMASEMESTRALFND
    AUTHOR MIGRACION  : CAMILO ANDRÃ‰S PÃ‰REZ DUEÃ‘AS
    DATE MIGRADOR     : 11/07/2019
    TIME              : 14:15 PM
    MODIFIER          :
    DATE MODIFIED     :
    DESCRIPTION       : PROCEDIMIENTO QUE CALCULA LA PRIMA SEMESTRAL DEL FEDERACIÃ“N DE DEPARTAMENTOS.
	                    SE TOMA DE LA VERSION DE NOMINA NOMINAP2019.07.02_UNIFICADAS MPV 09072019_MPV - 519 NIIF CITA_ESPAGZ_UES_AGZ.accdb
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA
    @NAME:  CALCULARPRIMASEMESTRALFND
  */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DBLCOMISIONES			      PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DNT1				              PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DBLGREP                  PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DBLAJSUELDO              PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL            ;
    MI_STRSQL1                  PCK_SUBTIPOS.TI_STRSQL            ;
    MI_CONTEO                   PCK_SUBTIPOS.TI_ENTERO         :=0;
    MI_RS                       SYS_REFCURSOR                     ;
    MI_RS_TVALOR                PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_RS_PER                   PCK_SUBTIPOS.TI_ENTERO_LARGO   :=0;
    MI_ASIG_PROM                PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_VAL_DES_PRO              PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_AUXT1                    PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_INDRETIR                 PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_I                        PCK_SUBTIPOS.TI_ENTERO_LARGO   :=0;
    MI_PRIMAJUNIO               PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DBLDESCUENTO             PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_PSCOMPLETA               PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_PS_MESANT                PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DBLREINTEGRO             PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DBLEXTRAS                PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_DIASPACTADOS             PCK_SUBTIPOS.TI_DOBLE          :=0;
    --(APINEDA:13/08/2019)-Se crea variable para almacenar dias trabajados en el aÃ±o
    MI_DIASLABORANIO            PCK_SUBTIPOS.TI_DOBLE          :=0;
    MI_VALOR                    PCK_SUBTIPOS.TI_DOBLE          :=0;
BEGIN
    PCK_NOMINA.GL_FACTORPS  := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT       := 0;
    MI_DNT1                 := 0;

    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                 THEN TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                 ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
				   			              END;
	  PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI> PCK_NOMINA.GL_FECHAIPS
		  					                 THEN PCK_NOMINA.GL_FECHAI
                                 ELSE PCK_NOMINA.GL_FECHAIPS
                              END;
    PCK_NOMINA.GL_FECHAFPS :=  CASE WHEN PCK_NOMINA.FC_CN(404) <> 0
	                                THEN PCK_NOMINA.GL_FECHAFIN1
		                 							ELSE CASE WHEN PCK_NOMINA.GL_SMES = 6
									                        THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                          ELSE TO_DATE('31/12/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
										                   END
                               END;
	  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_ANO1       => PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS)
                                          ,UN_MES1       => PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS)
										  ,UN_PERIODO1   => 1
										  ,UN_ANO2       => PCK_NOMINA.GL_SANO
										  ,UN_MES2       => PCK_NOMINA.GL_SMES
                                          ,UN_PERIODO2   => 99
										  ,UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356)
	                     + PCK_NOMINA.FC_CNA(357)
                		   + PCK_NOMINA.FC_CNA(359)
                       + PCK_NOMINA.FC_CN(356)
                       + PCK_NOMINA.FC_CN(357)
					             + PCK_NOMINA.FC_CN(359)
					             + PCK_NOMINA.FC_CNA(339)
					             + PCK_NOMINA.FC_CN(339);

    --(APINEDA:23/07/2019)-Se elimina la suma de un dÃ­a a la fecha final de la prima semestral.
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => PCK_NOMINA.GL_FECHAIPS
                                                            ,UN_FECHAFIN => PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    --(MZANGUNA:12/12/2019)-Se sube esta parte del cÃ³digo dado que las horas extras las debe tomar con el acumulado del aÃ±o.
    IF PCK_NOMINA.FC_CN(404) = 0 THEN
        IF (PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) > 0 THEN
            MI_DBLEXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) * 30 / PCK_NOMINA.GL_DCC, 0);
        ELSE
            MI_DBLEXTRAS := 0;
        END IF;

        IF (PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) <> 0 THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) * 30 / PCK_NOMINA.GL_DCC, 0); --17122018
        ELSE
           PCK_NOMINA.CN(946) := 0;
        END IF;

        MI_VALOR := PCK_NOMINA.FC_CN(946);

    END IF;

    IF (PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CN(500)) > 0 THEN -- 01032019 control de valores nulos division por cero
        MI_DBLCOMISIONES := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.FC_CNA(500)
                                                      + PCK_NOMINA.FC_CN(500)
                                                     ) * 30 / PCK_NOMINA.GL_DCC
												                            , 0); --Comisiones
    ELSE
        MI_DBLCOMISIONES := 0;
    END IF;
  	IF (PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CN(500)) > 0 THEN -- 01032019 control de valores nulos division por cero
        MI_DBLGREP := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.FC_CNA(61)
		                                            + PCK_NOMINA.FC_CN(61)
                                               ) * 30 / PCK_NOMINA.GL_DCC
											                        , 0); --Gastos de representaciÃ³n
    ELSE
        MI_DBLGREP := 0;
    END IF;
    MI_DBLAJSUELDO  := 0; --RECARGOS NOCTURNOS
	MI_DBLREINTEGRO := 0;
    --(APINEDA:13/08/2019)-Se traslada secciÃ³n de horas extra por orden en la funciÃ³n.
    PCK_NOMINA.CN(950) := MI_DBLCOMISIONES; --17122018 SOBRESUELDO OK
    IF PCK_NOMINA.FC_CN(404) <> 0 THEN
        IF PCK_NOMINA.GL_SMES <= 6 THEN
            MI_STRSQL1 := ' AND MES<=' || PCK_NOMINA.GL_SMES || ' ';
        ELSE
            MI_STRSQL1 := 'AND MES BETWEEN 7 AND ' || PCK_NOMINA.GL_SMES || '';
        END IF;
    ELSE
        IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 6 THEN
            MI_STRSQL1 := ' AND MES < 6 ';
        ELSE
            MI_STRSQL1 := ' AND MES BETWEEN 7 AND 11 ';
        END IF;
    END IF;
	  MI_STRSQL := ' SELECT SUM(NVL(VALOR,0))     TVALOR
	                       ,COUNT(ID_DE_EMPLEADO) PER
                   FROM HISTORICOS
                   WHERE COMPANIA = '''  || UN_COMPANIA || '''
				             AND ID_DE_PROCESO = 1
                     AND ANO =  ' || PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS) || ' ' ||
                     MI_STRSQL1 || '
                     AND PERIODO        = 3
                     AND ID_DE_EMPLEADO = '  || (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) || '
                     AND ID_DE_CONCEPTO IN(1)';
    BEGIN
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_RS_TVALOR
		                                    ,MI_RS_PER;
        IF NOT MI_RS_TVALOR  IS NULL AND NOT NVL(MI_RS_PER,0) = 0  THEN
            IF NVL(MI_RS_PER, 0) <> 0 THEN
                IF PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS) = PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAFPS) AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS) = PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) THEN
                    MI_VAL_DES_PRO := MI_VAL_DES_PRO
                                      + (  PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS)
                                         - PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS)
                                         + 1
                                        )
                                      * PCK_NOMINA.FC_CN(1) / 30;
                ELSE
                    IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) <> 1 AND PCK_NOMINA.GL_FECHAIPS <> LAST_DAY(PCK_NOMINA.GL_FECHAIPS) THEN
                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   =>  UN_COMPANIA
                                                              ,UN_ANO1       =>  PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS)
                                                              ,UN_MES1       =>  PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS)
                                                              ,UN_PERIODO1   =>  3
                                                              ,UN_ANO2       =>  PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS)
                                                              ,UN_MES2       =>  PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS)
                                                              ,UN_PERIODO2   =>  3
                                                              ,UN_IDEMPLEADO =>  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

                        IF PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAIPS)) = PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) THEN -- ORIGINAL DE ACCES If UltimoDia(FECHAIPS) = Day(FECHAIPS) Then
                            MI_VAL_DES_PRO := 1 * PCK_NOMINA.FC_CN(1) / 30;
                        ELSE
                            MI_VAL_DES_PRO := (PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) - 1) * PCK_NOMINA.FC_CNA(1) / 30;
                        END IF;
                    END IF;
                    IF (PCK_NOMINA.GL_FECHAFPS <> LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        MI_VAL_DES_PRO := MI_VAL_DES_PRO + (30 - PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS)) * PCK_NOMINA.FC_CN(1) / 30;
                    END IF ;
                END IF;
                MI_ASIG_PROM := PCK_SYSMAN_UTL.FC_ROUND( ( NVL(MI_RS_TVALOR, 0)
                                                          + (CASE WHEN PCK_NOMINA.GL_SPRC = 99
                                                                 THEN 0
                                                                 ELSE PCK_NOMINA.FC_CN(1)
                                                              END)
                                                          - MI_VAL_DES_PRO
                                                         ) * 30 / PCK_NOMINA.GL_DCC
                                                        , 0);
                MI_ASIG_PROM := PCK_NOMINA.FC_CN(1);
                IF UN_COMPANIA = '002' AND PCK_NOMINA.GL_SANO >= 2017 THEN
                    MI_ASIG_PROM := PCK_NOMINA.FC_CN(1);
                END IF;
            ELSE
                MI_ASIG_PROM := PCK_NOMINA.FC_CN(1);
            END IF;
        ELSE
            MI_ASIG_PROM := PCK_NOMINA.FC_CN(1);
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ASIG_PROM := PCK_NOMINA.FC_CN(1);
    END;
    --10/01/2013 JP SegÃºn financiera solo se debe realizar si el sueldo es variable
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_SALARIO = 'V' THEN
        MI_AUXT1 := PCK_NOMINA.GL_AUXT; --'PROM_AUX("01", CVDate(FECHAIPS), CVDate(FECHAFPS), personal!Id_de_Empleado, True)
    ELSE
        MI_AUXT1 := PCK_NOMINA.GL_AUXT;
    END IF;

    PCK_NOMINA.CN(948) := MI_AUXT1; --17122018

    --HORAS EXTRA
    --(APINEDA:13/08/2019)-Se agrega secciÃ³n para el cÃ¡lculo de valor correspondiente a las horas extra en retiro y periodo mensual
    IF PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.CN(949) := MI_DBLEXTRAS; --17122018
    ELSE
        --(APINEDA:13/08/2019)-En retiro de acuerdo a TAR 1000093896 FederaciÃ³n Nacional de Departamentos
	    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_ANO1       => PCK_NOMINA.GL_SANO
                                          ,UN_MES1       => 1
										  ,UN_PERIODO1   => 1
										  ,UN_ANO2       => PCK_NOMINA.GL_SANO
										  ,UN_MES2       => PCK_NOMINA.GL_SMES
                                          ,UN_PERIODO2   => 99
										  ,UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --DIAS TRABAJADOS EN EL AÃ‘O
        MI_DIASLABORANIO := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) > 0 THEN
            MI_DBLEXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) / MI_DIASLABORANIO * 30, 0);
        ELSE
            MI_DBLEXTRAS := 0;
        END IF;
        PCK_NOMINA.CN(949) := MI_DBLEXTRAS; --17122018
        IF (PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) <> 0 THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(74) + PCK_NOMINA.FC_CN(74)) / MI_DIASLABORANIO * 30, 0);
        ELSE
           PCK_NOMINA.CN(946) := 0;
        END IF;
    END IF;
    --FIN HORAS EXTRA
	--(APINEDA:23/07/2019)-Se agrega calculo de factores para la Prima de servicios
    PCK_NOMINA.GL_FACTORPS := (MI_ASIG_PROM + MI_DBLGREP + MI_DBLCOMISIONES + MI_DBLEXTRAS + MI_DBLAJSUELDO + MI_DBLREINTEGRO + MI_AUXT1) + PCK_NOMINA.FC_CN(946);
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA
                                           ,UN_ANO1      => PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS)
                                           ,UN_MES1      => PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS)
                                           ,UN_PERIODO1  => 3
                                           ,UN_ANO2      => PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAFPS)
                                           ,UN_MES2      => PCK_NOMINA.GL_SMES
                                           ,UN_PERIODO2  => 99
                                           ,UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0
                       	       THEN PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.GL_FACTORPS * PCK_NOMINA.GL_DCC)
							                                               / 360 + 0.05
							                                             , 0)
                               ELSE PCK_NOMINA.FC_CN(160)
                          END;
    MI_PRIMAJUNIO    := PCK_NOMINA.FC_CN(160);
    MI_DIASPACTADOS  := PCK_NOMINA.FC_CN(67);
    MI_INDRETIR      := PCK_NOMINA.FC_CN(404);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR MI_I IN 2.. 526 LOOP
            IF (MI_I <> 125 AND MI_I <> 243 AND MI_I <> 201 AND MI_I <> 401 AND MI_I <> 402 AND MI_I <> 4) THEN
                PCK_NOMINA.CN(MI_I) := 0;
            END IF;
        END LOOP;
        MI_I := 0;
        FOR MI_I IN 528.. 699 LOOP
            IF MI_I <= 698 AND MI_I <> 633 AND MI_I <> 606 THEN
                PCK_NOMINA.CN(MI_I) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67)  := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
    PCK_NOMINA.CN(243) := PCK_SYSMAN_UTL.FC_ROUND( MI_DBLGREP
	                                                 + MI_DBLCOMISIONES
												                           + MI_DBLAJSUELDO
												                           + MI_DBLREINTEGRO
	                                                , 0) - MI_DBLDESCUENTO;
    PCK_NOMINA.CN(83)  := 0; --AUXA --Base de Subsidio de alimentaciÃ³n para prima
    PCK_NOMINA.CN(85)  := 0; --AUXT --Base de Subsicio de Transporte para prima
    PCK_NOMINA.CN(945) := MI_ASIG_PROM;
    PCK_NOMINA.CN(947) := MI_DBLEXTRAS;  -- Horas Extras
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67); -- Dias pactados prima
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;

    -- Licencias
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        --PRIMA DE SERVICIOS
        PCK_NOMINA.CN(498) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0
                                  THEN PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.GL_FACTORPS  * PCK_NOMINA.GL_DCC) / 360
								                                               , 0)
                                  ELSE PCK_NOMINA.FC_CN(160)
							                 END;
        MI_PSCOMPLETA := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.GL_FACTORPS  * PCK_NOMINA.GL_DCC) / 360
		                                             , 0);
        PCK_NOMINA.GL_PS_BASE  := PCK_NOMINA.GL_FACTORPS;
        PCK_NOMINA.GL_PS_DIAS  := PCK_NOMINA.GL_DCC;
        PCK_NOMINA.GL_PS_TOTAL := MI_PSCOMPLETA;
        IF PCK_NOMINA.GL_SMES  = 8 AND PCK_NOMINA.GL_SANO = 2016 THEN
            MI_PS_MESANT := PCK_SYSMAN_UTL.FC_ROUND( ( PCK_NOMINA.GL_FACTORPS  * (PCK_NOMINA.GL_DCC - 30)) / 360
				                                              , 0);
        ELSE
            MI_PS_MESANT := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.GL_FACTORPS
                                                      * (CASE WHEN (PCK_NOMINA.GL_DCC - 30) <= 0
                                                            THEN 0
                                                            ELSE (PCK_NOMINA.GL_DCC - 30)
                                                         END)
														                          ) / 360
													                           , 0);
        END IF;
        PCK_NOMINA.GL_PS_PAGOSMES := PCK_NOMINA.FC_CNA(160);
        PCK_NOMINA.PS_PRV         := PCK_NOMINA.FC_CN(498) - MI_PS_MESANT;
        PCK_NOMINA.CN(498)        := PCK_NOMINA.FC_CN(498) - MI_PS_MESANT;
    END IF;
    --01022018
    PCK_NOMINA.GL_PS_BASE  := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS  := PCK_NOMINA.GL_DCC	;

END PR_CALCULARPRIMASEMESTRALFND;


FUNCTION FC_ACUMDIFERENCIAS
/*
    NAME              : ACUMDIFERENCIAS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 29/07/2019
    TIME              : 9:20 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : ACTUALIZA LOS VALORES DE CONCEPTOS CON VALORES ACUMULADOS DENTRO DEL RANGO DE LOS PERIODOS DE DIFERENCIAS RETROACTIVO                  
  */
  (
	UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO1       IN PCK_SUBTIPOS.TI_ANIO,
	UN_MES1       IN PCK_SUBTIPOS.TI_MES,
	UN_PERIODO1   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
	UN_ANO2       IN PCK_SUBTIPOS.TI_ANIO,
	UN_MES2       IN PCK_SUBTIPOS.TI_MES,
	UN_PERIODO2   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
	UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
  AS
    MI_CANT        NUMBER :=0;    
    MI_INICIAL    NUMBER := LPAD(UN_ANO1, 4, '0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PERIODO1, 2, '0');
    MI_FINAL       NUMBER := LPAD(UN_ANO2, 4, '0')   || LPAD(UN_MES2,2,'0') || LPAD(UN_PERIODO2, 2, '0');        
BEGIN
  PCK_NOMINA.CNA.DELETE;
  FOR RS IN (
              SELECT SUM(H.VALOR) TOTAL, H.ID_DE_CONCEPTO CONCEPTO
              FROM  HISTORICOS H INNER JOIN PERIODOS P ON  
                   (H.COMPANIA      = P.COMPANIA) 
                AND(H.ID_DE_PROCESO = P.ID_DE_PROCESO) 
                AND(H.ANO           = P.ANO)
                AND(H.MES           = P.MES) 
                AND(H.PERIODO       = P.PERIODO)
              WHERE P.COMPANIA                   = UN_COMPANIA
                AND P.ID BETWEEN MI_INICIAL AND MI_FINAL
                AND  P.ACUMULADO                  NOT IN (0) 
				AND  P.DIFERENCIASRETROACTIVO 	  NOT IN (0)
                AND  H.ID_DE_EMPLEADO             = UN_IDEMPLEADO
              GROUP BY H.ID_DE_CONCEPTO
            )
  LOOP
    IF RS.CONCEPTO > 0 AND RS.CONCEPTO <= PCK_NOMINA.MAXI THEN
      PCK_NOMINA.CNA(RS.CONCEPTO) := RS.TOTAL;
    END IF;
    MI_CANT := MI_CANT + 1;
  END LOOP;  
  RETURN MI_CANT;

END FC_ACUMDIFERENCIAS;

PROCEDURE PR_COPIAR_PERSONALHISTORICOP 
/*
    NAME              : PR_COPIAR_PERSONALHISTORICOP
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 07/08/2019
    TIME              : 08:10 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : PERMITE COPIAR LOS HISTORICOS PERSONAL DE LA PERSONA DE UN PERIODO A OTRO
                        SE USA PARA LOS PERIODOS ADICIONALES DE VACACIONES
  */
(
  UN_COMPANIA_ORIGEN  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO_ORIGEN   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANO_ORIGEN       IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES_ORIGEN       IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO_ORIGEN   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_COMPANIA_DESTINO IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO_DESTINO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANO_DESTINO      IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES_DESTINO      IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO_DESTINO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_IDEMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
) AS
    MI_REGISTRO   PERSONAL_HISTORICO%ROWTYPE;
    MI_REGISTROS  PCK_NOMINA.TYP_PERSONAL_HISTORICO; 
    MI_I          PCK_SUBTIPOS.TI_ENTERO;
    MI_IHISTORICO PCK_SUBTIPOS.TI_ENTERO;
    MI_CON_REG    PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    SELECT *
    BULK COLLECT INTO MI_REGISTROS
    FROM PERSONAL_HISTORICO P 
    WHERE P.COMPANIA       = UN_COMPANIA_ORIGEN
      AND P.ID_DE_PROCESO  = UN_PROCESO_ORIGEN
      AND P.ANO            = UN_ANO_ORIGEN
      AND P.MES            = UN_MES_ORIGEN
      AND P.PERIODO        = UN_PERIODO_ORIGEN
      AND P.ID_DE_EMPLEADO = UN_IDEMPLEADO;
    IF MI_REGISTROS.COUNT > 0 THEN 
        MI_IHISTORICO := 0;
        MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA_DESTINO || ''' AND '
                    || ' ID_DE_PROCESO     = '   || UN_PROCESO_DESTINO  || ' AND ' 
                    || ' ANO               = '   || UN_ANO_DESTINO      || ' AND ' 
                    || ' MES               = '   || UN_MES_DESTINO      || ' AND ' 
                    || ' PERIODO           = '   || UN_PERIODO_DESTINO  || ' AND ' 
                    || ' ID_DE_EMPLEADO    = '   || UN_IDEMPLEADO;
        MI_TABLA := 'PERSONAL_HISTORICO'; 
        --SE CONTROLA QUE EXISTAN HISTORICOS EN EL PERIODO DONDE SE QUIERE CREAR EL REGISTRO
        --DE LO CONTRARIO SE ELIMINA EL REGISTRO SI EXISTE PARA QUE NO QUEDEN HISTORICOS PERSONAL SIN HISTORICOS
        SELECT COUNT(COMPANIA)
        INTO MI_IHISTORICO
        FROM HISTORICOS
        WHERE COMPANIA       = UN_COMPANIA_DESTINO
          AND ID_DE_PROCESO  = UN_PROCESO_DESTINO
          AND ANO            = UN_ANO_DESTINO
          AND MES            = UN_MES_DESTINO
          AND PERIODO        = UN_PERIODO_DESTINO
          AND ID_DE_EMPLEADO = UN_IDEMPLEADO;
        IF MI_IHISTORICO = 0 THEN
            BEGIN                       
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                        UN_ACCION    => 'E', 
                                                        UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;    
        ELSE
            <<PERSONALHISTORICO>>                        
            FOR MI_CON_REG IN MI_REGISTROS.FIRST .. MI_REGISTROS.LAST LOOP
                BEGIN
                    MI_REGISTRO := MI_REGISTROS(MI_CON_REG);
                    MI_REGISTRO.COMPANIA      := UN_COMPANIA_DESTINO;
                    MI_REGISTRO.ID_DE_PROCESO := UN_PROCESO_DESTINO;
                    MI_REGISTRO.ANO           := UN_ANO_DESTINO;
                    MI_REGISTRO.MES           := UN_MES_DESTINO;
                    MI_REGISTRO.PERIODO       := UN_PERIODO_DESTINO;
                    MI_I := 0;
                    SELECT COUNT(COMPANIA)
                    INTO MI_I
                    FROM PERSONAL_HISTORICO
                    WHERE COMPANIA       = UN_COMPANIA_DESTINO
                      AND ID_DE_PROCESO  = UN_PROCESO_DESTINO
                      AND ANO            = UN_ANO_DESTINO
                      AND MES            = UN_MES_DESTINO
                      AND PERIODO        = UN_PERIODO_DESTINO
                      AND ID_DE_EMPLEADO = UN_IDEMPLEADO;
                    IF MI_I > 0 THEN
                        BEGIN                       
                             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                                    UN_ACCION    => 'E', 
                                                                    UN_CONDICION => MI_CONDICION);    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                        END;            
                    END IF;
                    INSERT INTO PERSONAL_HISTORICO VALUES MI_REGISTRO;   
                EXCEPTION WHEN  OTHERS THEN
                    MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                    MI_MSGERROR(1).VALOR := UN_IDEMPLEADO;
                    MI_MSGERROR(2).CLAVE := 'PERIODO_ORIGEN';
                    MI_MSGERROR(2).VALOR := UN_PERIODO_ORIGEN;
                    MI_MSGERROR(3).CLAVE := 'PERIODO_DESTINO';
                    MI_MSGERROR(3).VALOR := UN_PERIODO_DESTINO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                              ,UN_ERROR_COD   => PCK_ERRORES.ERR_COPIA_PERSONALHISTORICO
                                              ,UN_REEMPLAZOS  => MI_MSGERROR);     
                END;
            END LOOP PERSONALHISTORICO; 
        END IF;
    END IF;
END PR_COPIAR_PERSONALHISTORICOP;

PROCEDURE PR_DIFERENCIASBASESNOVEDADES(
  /*
  NAME               : DIFERENCIASBASESNOVEDADES
  AUTHOR MIGRACION   : ANDREA PINEDA OVALLE
  DATE MIGRADOR      : 09/08/2019
  TIME               : 06:09 PM
  SOURCE MODULE      : Nueva
  MODIFIER           : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MODIFIED      : 10/02/2026
  TIME               :
  MODIFICATIONS      : Se modifica el concepto 130 para que se mantenga igual si la entidad es exonerada y 
                       el empleado tiene horas extras. 
  DESCRIPTION        : Si existen diferencias tome el valor cÃ¡lculado de la tabla BASESNOVEDADES
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : 
  */
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PERIODOBASENOV  IN PCK_SUBTIPOS.TI_PERIODO_NOMI
)
AS
    MI_SALUDP           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALUDE           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PENSIONP         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PENSIONE         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;    
    MI_FSP              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FSPSUBSISTENCIA  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FSPADICIONAL     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTANOVEDAD    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FONDOSOLIDARIDAD PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORP           NUMBER                := 0;
    MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
    MI_PORCFSPYFS_ADI   NUMBER:=0;
    MI_PERIODO_AUX      NUMBER; --JM CC 2203
BEGIN
    IF PCK_PARST.FC_PAR('CALCULA 112 POR NOVEDADES', ' ') = 'SI' THEN
        --(MZANGUNA:29/01/2019)-Si existen diferencias tome el valor cÃ¡lculado de la tabla BASESNOVEDADES
        -- JM CC 2203
        MI_PERIODO_AUX := PCK_NOMINA.GL_SPER; --sinchi no guarda nada en el periodo 1 para el plano 
        IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) THEN 
            MI_PERIODO_AUX := 2;
        END IF; 
        -- JM FIN CC 2203
        BEGIN
            SELECT NVL(SUM(APORTEPATRONALSALUD),0) , NVL(SUM(APORTEEMPLEADOSALUD),0)
                  ,NVL(SUM(APORTEPATRONALPENSION),0) ,NVL(SUM(APORTEEMPLEADOPENSION),0)
                  ,NVL(SUM(FSP) ,0) ,NVL(SUM(FSPSUBSISTENCIA) ,0) ,NVL(SUM(FSPADICIONAL) ,0)
                  ,COUNT(TIPONOVEDAD)
            INTO MI_SALUDP, MI_SALUDE, MI_PENSIONP, MI_PENSIONE,
                 MI_FSP, MI_FSPSUBSISTENCIA, MI_FSPADICIONAL,
                 MI_CUENTANOVEDAD
            FROM BASESNOVEDADES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
              AND ANO = PCK_NOMINA.GL_SANO
              AND MES = PCK_NOMINA.GL_SMES
              AND PERIODO = MI_PERIODO_AUX; -- JM CC 2203
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_SALUDP := 0;
            MI_SALUDE := 0;
            MI_PENSIONP := 0;
            MI_PENSIONE := 0;
            MI_FSP := 0;
            MI_FSPSUBSISTENCIA := 0;
            MI_FSPADICIONAL := 0;
            MI_CUENTANOVEDAD := 0;
        END;
        IF MI_CUENTANOVEDAD > 0 THEN
            --(MZANGUNA:28/02/2019)- Se cambian rangos de -200 a 200
            --(MZANGUNA:01/03/2019)- Se coloca el rango de redondeo respecto al nÃºmero de novedades que se tengan en el mes.
            IF MI_SALUDP <> 0 AND (MI_SALUDP - PCK_NOMINA.FC_CN(116)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) AND ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN --JM 7752194  18/09/2024 
                PCK_NOMINA.CN(116) := MI_SALUDP;
            END IF;

            /*IF MI_SALUDE <> 0 AND (MI_SALUDE - PCK_NOMINA.FC_CN(130)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) THEN
                PCK_NOMINA.CN(130) := MI_SALUDE;
            END IF;*/ --(MZANGUNA:23/04/2019)-Solicitud NJIMENEZ Se documenta dado que los redondeos solo se aplican al patrono no a Empleado
            --<TAR:7707854 FECHA:16/02/2022 AUTOR:CP>
            --JM CC 228 CNP por FC_CNP para que no de error a la hora de utilizar el parametro
             IF PCK_PARST.FC_PAR('APLICA EXONERACION EN BASE TOTAL INGRESOS 097', '0') = 'SI' THEN --18122018
               IF  ((PCK_NOMINA.FC_CN(151) = PCK_NOMINA.FC_CNP(151)) Or (PCK_NOMINA.FC_CN(155) = PCK_NOMINA.FC_CNP(155))) Or ((PCK_NOMINA.FC_CN(174) + PCK_NOMINA.FC_CN(175)) = (PCK_NOMINA.FC_CNP(174) + PCK_NOMINA.FC_CNP(175))) THEN --30092021 TAR 110506 ESPCAJICA 
                    PCK_NOMINA.GL_IBLREXONERACION := PCK_NOMINA.FC_CN(97) + (PCK_NOMINA.FC_CNP(97) - PCK_NOMINA.FC_CNP(151) - PCK_NOMINA.FC_CNP(155) - PCK_NOMINA.FC_CNP(174) - PCK_NOMINA.FC_CNP(175));
                ELSE
                    PCK_NOMINA.GL_IBLREXONERACION := (PCK_NOMINA.FC_CN(97) + PCK_NOMINA.FC_CNP(97));
                END IF;
             ELSE
                   PCK_NOMINA.GL_IBLREXONERACION := PCK_NOMINA.GL_IBLR;
             END IF;
             MI_VALORP := PCK_NOMINA.GL_IBLREXONERACION;
             MI_VALORP := PCK_NOMINA.FC_CN(201);
             IF (PCK_PARST.FC_PAR('APLICAR EXONERACION APORTES SALUD PATRONO 8.5%','NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION < PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.GL_IBLREXONERACION > 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05')) THEN
               IF (MI_SALUDP + MI_SALUDE) <> 0 AND ((MI_SALUDP + MI_SALUDE) - PCK_NOMINA.FC_CN(113)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) THEN
                   PCK_NOMINA.CN(113) := (MI_SALUDP + MI_SALUDE);
               END IF;
               IF (MI_SALUDP + MI_SALUDE) <> 0 AND ((MI_SALUDP + MI_SALUDE) - PCK_NOMINA.FC_CN(130)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) THEN
                    /*INI_Ajuste para quetenga en cuenta las horas extras del empleado CC3463 MPEREZ*/
                    IF (PCK_PARST.FC_PAR('APLICAR EXONERACION APORTES SALUD PATRONO 8.5%','NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION < PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.GL_IBLREXONERACION > 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05') AND (PCK_NOMINA.FC_CN(70)+PCK_NOMINA.FC_CN(150)) <> 0) THEN
                        PCK_NOMINA.CN(130) := PCK_NOMINA.CN(130);
                    ELSE
                        PCK_NOMINA.CN(130) := (MI_SALUDP + MI_SALUDE);
                    END IF;
                    /*FIN_Ajuste para quetenga en cuenta las horas extras del empleado CC3463 MPEREZ*/
               END IF;
             END IF;
            --</TAR>
            IF MI_PENSIONP <> 0 AND (MI_PENSIONP - PCK_NOMINA.FC_CN(117)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) AND ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN --JM 7752194  18/09/2024 
                PCK_NOMINA.CN(117) := MI_PENSIONP;
            END IF;

            /*IF MI_PENSIONE <> 0 AND (MI_PENSIONE - PCK_NOMINA.FC_CN(131)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) THEN
                PCK_NOMINA.CN(131) := MI_PENSIONE;
            END IF;*/ --(MZANGUNA:23/04/2019)-Solicitud NJIMENEZ Se documenta dado que los redondeos solo se aplican al patrono no a Empleado
			
			--INI TICKET 7743926(30/04/2024 JCROJAS)
			IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN -- TICKET 7749611 EFCM: SE EXCLUYE REDONDEO A CORPOBOYACA
			--IF ( PCK_NOMINA.CPARENTRADA(1).NIT != '800252843' OR MI_CUENTANOVEDAD > 1) THEN -- TICKET 7748149 EFCM: SE EXCLUYE REDONDEO A CORTOLIMA EN FUNCIONARIOS CON UNA NOVEDAD
	           -- MOD JM CC 2203 YA QUE ESTAMOS CAMBIAMOS CN por FC_CN para el 113,130,118,131
              IF MI_SALUDE - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)) BETWEEN -100 AND 100 THEN
	                PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(113) + MI_SALUDE - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)); 
	            END IF;
	            
	            IF MI_SALUDE - (PCK_NOMINA.FC_CN(130)+PCK_NOMINA.FC_CNP(130)) BETWEEN -100 AND 100 THEN
	                PCK_NOMINA.CN(130) := PCK_NOMINA.FC_CN(130) + MI_SALUDE - (PCK_NOMINA.FC_CN(130)+PCK_NOMINA.FC_CNP(130));
	            END IF;
	            
	            IF MI_PENSIONE - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118)) BETWEEN -100 AND 100 THEN 
	                PCK_NOMINA.CN(118) := PCK_NOMINA.FC_CN(118) + MI_PENSIONE - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118));
	            END IF;
	            
	            IF MI_PENSIONE - (PCK_NOMINA.FC_CN(131)+PCK_NOMINA.FC_CNP(131)) BETWEEN -100 AND 100 THEN 
	                PCK_NOMINA.CN(131) := PCK_NOMINA.FC_CN(131) + MI_PENSIONE - (PCK_NOMINA.FC_CN(131)+PCK_NOMINA.FC_CNP(131));
	            END IF;
	        END IF; 
            --FIN TICKET 7743926(30/04/2024 JCROJAS)

            --(MZANGUNA:28/02/2019)- Se agregan diferencias para los conceptos de salud y pensiÃ³n.
            IF (MI_SALUDP + MI_SALUDE) - (PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) AND ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN --JM 7752194  18/09/2024 
                PCK_NOMINA.CN(116) := PCK_NOMINA.FC_CN(116) + ((MI_SALUDP + MI_SALUDE) - (PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116)));
            END IF;
            IF (MI_PENSIONP + MI_PENSIONE) - (PCK_NOMINA.FC_CN(118) + PCK_NOMINA.FC_CN(117)) BETWEEN (MI_CUENTANOVEDAD * 100) * -1 AND (MI_CUENTANOVEDAD * 100) AND ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN --JM 7752194  18/09/2024 
                 PCK_NOMINA.CN(117) := PCK_NOMINA.FC_CN(117) + ((MI_PENSIONP + MI_PENSIONE) - (PCK_NOMINA.FC_CN(118) + PCK_NOMINA.FC_CN(117)));
            END IF;
            -- JM INI CC 2203 no estaba caclculando 113 y 118 para la primera quincena
            IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) THEN 
                IF PCK_NOMINA.FC_CN(113) = 0 AND MI_SALUDE <> 0 THEN 
                    PCK_NOMINA.CN(113) := MI_SALUDE;
                END IF;
                
                IF PCK_NOMINA.FC_CN(118) = 0 AND MI_PENSIONE <> 0 THEN 
                    PCK_NOMINA.CN(118) := MI_PENSIONE;
                END IF;
            END IF;
            -- JM FIN CC 2203
            --GROJAS CC 3792 : Si MI_SALUDE esta en 0 el concepto 113 debe ser 0.
            IF MI_SALUDE = 0 AND PCK_NOMINA.FC_CN(113) <> 0 THEN
                PCK_NOMINA.CN(113) := 0;
            END IF;
            --GROJAS CC 3792 : Si MI_PENSIONE esta en 0 el concepto 118 debe ser 0.
            IF MI_PENSIONE = 0 AND PCK_NOMINA.FC_CN(118) <> 0 THEN
                PCK_NOMINA.CN(118) := 0;
            END IF;
            --GROJAS CC 3792
            --JM INI 17/10/2024
            --MROSERO CC1028 Se agrega periodo 7 en la condicion para que excluya recalculo. 
                IF (MI_CUENTANOVEDAD > 1 AND PCK_NOMINA.GL_PERIODOACTUAL<>7) OR PCK_SYSMAN_UTL.FC_IIF(PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO')= 'SI') THEN  --ajustar los redondeos para el plano cuando existen varias lineas de novedades 
                    PCK_NOMINA.CN(116) := (MI_SALUDP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_SALUDE) ELSE ROUND(MI_SALUDE + 49,-2) END) - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)+PCK_NOMINA.FC_CNP(116));--(CC:2870_CFBARRERA_Se agrega un CASE para realizar los redondeos junto con la suma de 0.49) (CC:3843_MPEREZ Se adiciona el acumulado del 116 para restar al 116 de la segunda quincena)
                    PCK_NOMINA.CN(117) := (MI_PENSIONP +  CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_PENSIONE) ELSE ROUND(MI_PENSIONE + 49,-2) END) - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118)+PCK_NOMINA.FC_CNP(117)) ; --(CC:3843_MPEREZ Se adiciona el acumulado del 117 para restar al 117 de la segunda quincena)
                END IF;
            --JM FIN 17/10/2024
            --JM INI CC 1314 01/07/2025
                IF (MI_CUENTANOVEDAD >= 1 AND  PCK_NOMINA.GL_PERIODOACTUAL = 7) THEN --MOD JM CC 2821
                    IF PCK_NOMINA.FC_CNP(35) = 0 THEN -- JM CC 3300
                      PCK_NOMINA.CN(130) := ROUND(MI_SALUDE - PCK_NOMINA.FC_CNP(130),0);
                      PCK_NOMINA.CN(131) := ROUND(MI_PENSIONE - PCK_NOMINA.FC_CNP(131),0);
                      PCK_NOMINA.CN(118) := ROUND(PCK_NOMINA.FC_CN(118) + MI_PENSIONE - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118)),0);
                      PCK_NOMINA.CN(113) := ROUND(PCK_NOMINA.FC_CN(113) + MI_SALUDE - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)),0);
                    END IF;
                    PCK_NOMINA.CN(116) := (MI_SALUDP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_SALUDE) ELSE ROUND(MI_SALUDE + 49,-2) END) - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CNP(116));
                    PCK_NOMINA.CN(117) := (MI_PENSIONP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_PENSIONE) ELSE ROUND(MI_PENSIONE + 49,-2) END) - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118) + PCK_NOMINA.FC_CNP(117)) ;
                END IF;
            --JM FIN CC 1314 01/07/2025
            --INI JM CC 1404 NOMINAS QUINCENALES PAGAN 31 DIAS
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' THEN 
                    PCK_NOMINA.CN(130) := MI_SALUDE - PCK_NOMINA.FC_CNP(130);
                    PCK_NOMINA.CN(131) := MI_PENSIONE - PCK_NOMINA.FC_CNP(131);
                    PCK_NOMINA.CN(118) := PCK_NOMINA.FC_CN(118) + MI_PENSIONE - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118));
                    PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(113) + MI_SALUDE - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113));
                    PCK_NOMINA.CN(116) := (MI_SALUDP + MI_SALUDE) - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)++PCK_NOMINA.FC_CNP(116));
                    PCK_NOMINA.CN(117) := (MI_PENSIONP + MI_PENSIONE) - (PCK_NOMINA.FC_CN(118)+PCK_NOMINA.FC_CNP(118)+PCK_NOMINA.FC_CNP(117));
            END IF;
            --FIN JM CC 1404 NOMINAS QUINCENALES PAGAN 31 DIAS
            --JM 02/20/2024 CC 228 entidades exoneradas que pierden exoneracion en base al 97 
            IF PCK_PARST.FC_PAR('APLICA EXONERACION EN BASE TOTAL INGRESOS 097', '0') = 'SI'  AND PCK_PARENTR.PARAMETRO70 = 'S' THEN 
                MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
                IF ((PCK_NOMINA.FC_CN(97)+ PCK_NOMINA.FC_CNP(97)) < 10 * PCK_NOMINA.FC_CN(201)) AND ((PCK_NOMINA.FC_CN(97)+ PCK_NOMINA.FC_CNP(97)) > 0) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION = 1 THEN -- MOD JM 30-04-2025 CC 1479 para que no le eplique a los senas 
                    
                      IF (PCK_NOMINA.FC_CN(35) > 0) AND ( PCK_NOMINA.FC_CN(112) > 10 * PCK_NOMINA.FC_CN(201)) THEN  --JM 03/03/2025 CC992 piede darse el caso que en vacaciones el 97 no es suficiente 
                             IF MI_SALUDE > 0 THEN 
                                IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN 
                                PCK_NOMINA.CN(130) :=  MI_SALUDE - PCK_NOMINA.FC_CNP(130);
                                PCK_NOMINA.CN(113) :=  PCK_NOMINA.FC_CN(130);
                                ELSE 
                                PCK_NOMINA.CN(130) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * PCK_NOMINA.CPARENTRADA(1).PORC_EMPLEADO_EPS / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_SYSMAN_UTL.FC_IIF(((PCK_NOMINA.GL_INDING OR PCK_NOMINA.GL_INDRET) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC = PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)), PCK_NOMINA.GL_RMINIMO1990, PCK_NOMINA.GL_RAPORTES1990)) - PCK_NOMINA.FC_CNP(130);
                                PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(130);
                                END IF;
                            END IF;
                            --JM MOD CC 2410 por alguna razon no estaba calculando arriba el 113 se trae para aca para que calcule el 116 de manera correcta 
                            PCK_NOMINA.CN(116) := (MI_SALUDP + MI_SALUDE) - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113));
                         ELSE 
                           PCK_NOMINA.CN(116) := 0;
                            IF MI_SALUDE > 0 THEN
                                IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN
                                --LVEGA 13/08/2025
                                PCK_NOMINA.CN(130) := MI_SALUDE - PCK_NOMINA.FC_CNP(130);
                                PCK_NOMINA.CN(113) :=  PCK_NOMINA.FC_CN(130);
                                ELSE
                                PCK_NOMINA.CN(130) := PCK_SYSMAN_UTL.FC_ROUND_100((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC / PCK_NOMINA.FC_CN(4) * (PCK_NOMINA.FC_CN(4)-PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(357)+PCK_NOMINA.FC_CN(356))) * PCK_NOMINA.CPARENTRADA(1).PORC_EMPLEADO_EPS / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_SYSMAN_UTL.FC_IIF(((PCK_NOMINA.GL_INDING OR PCK_NOMINA.GL_INDRET) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC = PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)), PCK_NOMINA.GL_RMINIMO1990, PCK_NOMINA.GL_RAPORTES1990)) - PCK_NOMINA.FC_CNP(130);
                                PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(130);
                                 END IF;
                            END IF;
                      END IF;

                    ELSE 
                        IF MI_PENSIONE = 0 AND PCK_NOMINA.FC_CN(360) >= 30 THEN  -- JM SE AÑADE IF CONDICION ESPECIAL CC 763
                                PCK_NOMINA.CN(116) := CASE WHEN MI_SALUDE > 0 THEN MI_SALUDE ELSE PCK_SYSMAN_UTL.FC_ROUND_100( CASE WHEN PCK_NOMINA.FC_CN(112) < PCK_NOMINA.FC_CN(201) THEN PCK_NOMINA.FC_CN(201) ELSE PCK_NOMINA.FC_CN(112) END * PCK_NOMINA.CPARENTRADA(1).PORC_EMPLEADO_EPS / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_SYSMAN_UTL.FC_IIF(((PCK_NOMINA.GL_INDING OR PCK_NOMINA.GL_INDRET) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC = PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)), PCK_NOMINA.GL_RMINIMO1990, PCK_NOMINA.GL_RAPORTES1990)) - (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CNP(130)) END;
                                PCK_NOMINA.CN(117) := MI_PENSIONP;
                                PCK_NOMINA.CN(130) :=  0;
                                PCK_NOMINA.CN(131) :=  0;
                                PCK_NOMINA.CN(118) :=  0;
                                PCK_NOMINA.CN(113) :=  0;
                          ELSE
                              IF MI_SALUDE > 0 THEN 
                                  IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN 
                                    IF (PCK_PARST.FC_PAR('APLICAR EXONERACION APORTES SALUD PATRONO 8.5%','NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION < PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.GL_IBLREXONERACION > 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05') AND (PCK_NOMINA.FC_CN(70)+PCK_NOMINA.FC_CN(150)) <> 0) THEN
                                        PCK_NOMINA.CN(113) :=  PCK_NOMINA.FC_CN(130);  -- Se controla el 130 cuando tiene horas extras CC3463 MPEREZ
                                    ELSE
                                        PCK_NOMINA.CN(130) :=  MI_SALUDE - PCK_NOMINA.FC_CNP(130); -- MOD JM CC 2355
                                        PCK_NOMINA.CN(113) :=  PCK_NOMINA.FC_CN(130);
                                    END IF;
                                  ELSE 
                                  PCK_NOMINA.CN(130) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * PCK_NOMINA.CPARENTRADA(1).PORC_EMPLEADO_EPS / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_SYSMAN_UTL.FC_IIF(((PCK_NOMINA.GL_INDING OR PCK_NOMINA.GL_INDRET) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC = PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)), PCK_NOMINA.GL_RMINIMO1990, PCK_NOMINA.GL_RAPORTES1990)) - PCK_NOMINA.FC_CNP(130);
                                  PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(130);
                                  END IF;

                                    IF ((PCK_NOMINA.FC_CN(97)+ PCK_NOMINA.FC_CNP(97)) > 10 * PCK_NOMINA.FC_CN(201)) AND ((PCK_NOMINA.FC_CN(97)+ PCK_NOMINA.FC_CNP(97)) > 0) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION = 1 THEN --JM CC 3545 (se trae un 116 raro de no se donde, mejor lo recalculo aqui)
                                        PCK_NOMINA.CN(116) := (MI_SALUDP + MI_SALUDE) - (PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113));
                                    END IF;
                                    
                              END IF;
                          END IF;
                    END IF; 
                END IF;
              --JM FIN 02/20/2024 CC 228  
                --LVEGA 13/08/2025
                IF (MI_CUENTANOVEDAD = 1) AND  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04'  THEN 
                PCK_NOMINA.CN(116) := (MI_SALUDP + MI_SALUDE) - ((PCK_NOMINA.FC_CN(113)+PCK_NOMINA.FC_CNP(113)) + PCK_NOMINA.FC_CNP(116)) ;
                END IF;
            --(MZANGUNA:28/02/2019)-Segun reuniÃ³n del dÃ­a 27/02/2019 con Henrry Puerto se procede a realizar proceso de alta ingenieria.
            --(APINEDA:16/09/2020)-TAR1000101466 Federacion. Se agrega validaciÃ³n dado que para el periodo 7 los conceptos 120 y 132 estan quedando negativos al restar acumulado de periodo 3.
            MI_FONDOSOLIDARIDAD := MI_FSP + MI_FSPSUBSISTENCIA;
            IF UN_PERIODOBASENOV <> PCK_NOMINA.GL_SPER AND NOT (PCK_NOMINA.GL_ESBONIFICACION)  THEN --(MZANGUNA:25/04/2019)-Se agrega bloque If, Para que no acumule en periodo diferente al normal.
                PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                  
                  IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO'--(CFBARRERA_INI_3708-HU2)
                        AND PCK_NOMINA.GL_SPER = 1 
                        AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN
                        
                        PCK_NOMINA.GL_AC_RETRO := PCK_NOMINA_COM1.FC_ACUMCONCEPTO_RETRO(UN_COMPANIA, 132, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                        PCK_NOMINA.CNA(132) := PCK_NOMINA_CALCULO.FC_CNP_RETRO(132);
                        
                        PCK_NOMINA.GL_AC_RETRO := PCK_NOMINA_COM1.FC_ACUMCONCEPTO_RETRO(UN_COMPANIA, 115, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                        PCK_NOMINA.CNA(115) := PCK_NOMINA_CALCULO.FC_CNP_RETRO(115);
                END IF;--(CFBARRERA_INI_3708-HU2)
                
                PCK_NOMINA.CN(132) := CASE WHEN MI_FONDOSOLIDARIDAD <> 0 THEN MI_FONDOSOLIDARIDAD - PCK_NOMINA.FC_CNA(132) ELSE 0 END;
                PCK_NOMINA.CN(115) := CASE WHEN MI_FONDOSOLIDARIDAD <> 0 THEN MI_FONDOSOLIDARIDAD - PCK_NOMINA.FC_CNA(115) ELSE 0 END;
                PCK_NOMINA.CN(120) := CASE WHEN MI_FSPADICIONAL <> 0 THEN MI_FSPADICIONAL - PCK_NOMINA.FC_CNA(120) ELSE 0 END;
            ELSE
            	-- TICKET 7744452 EFCM: PARA LA SEGUNDA QUINCENA SE DESCUENTA EL VALOR DE LA PRIMERA QUINCENA AL CN 132
            	IF ( PCK_NOMINA.GL_SPER = 2 ) THEN
                
                    IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO'--(CFBARRERA_INI_3708-HU2)
                    AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN
                        PCK_NOMINA.CNP(132) := 0;
                        PCK_NOMINA.CNP(115) := 0;
                    END IF;--(CFBARRERA_FIN_3708-HU2)

                    PCK_NOMINA.CN(132) := MI_FONDOSOLIDARIDAD - PCK_NOMINA.FC_CNP(132);
                    PCK_NOMINA.CN(115) := MI_FONDOSOLIDARIDAD - PCK_NOMINA.FC_CNP(132);
                ELSE
                    PCK_NOMINA.CN(132) := MI_FONDOSOLIDARIDAD;
                    PCK_NOMINA.CN(115) := MI_FONDOSOLIDARIDAD;
                END IF;
                -- TICKET 7744452 EFCM FIN --
                PCK_NOMINA.CN(120) := MI_FSPADICIONAL;
                /*INI_1496_NOMINA*/
                   IF (PCK_NOMINA.GL_SANO || PCK_NOMINA.GL_SMES >= '202507' AND PCK_NOMINA.GL_IBLR >= (4 * PCK_NOMINA.FC_CN(201)) AND PCK_PARST.FC_PAR('APLICAR REFORMA PENSIONAL ADICIONAL FSPYFS','NO') = 'SI' And PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN_TRANSICION = 0) Then
                     MI_PORCFSPYFS_ADI := PCK_NOMINA_SEGSOCI.FC_FSP_ADI_REFORMA(UN_COMPANIA, PCK_NOMINA.GL_SANO, 'A', PCK_NOMINA.GL_IBLR);
                     PCK_NOMINA.CN(317) := MI_PORCFSPYFS_ADI;
                     PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCFSPYFS_ADI / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) - PCK_NOMINA.FC_CNP(120);
                     MI_FSPADICIONAL := PCK_NOMINA.FC_CN(120);
                   END IF;
                /*FIN_1496_NOMINA*/
            END IF;
            
        END IF;

         --JM  CC 2203 en la segunda no hay nada que pagar, llevamos todo a 0 
        IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.GL_SPER = 2 AND PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA','SI') = 'NO' THEN --MP CC3843
                    PCK_NOMINA.CN(130) := 0;
                    PCK_NOMINA.CN(131) := 0;
                    PCK_NOMINA.CN(118) := 0;
                    PCK_NOMINA.CN(113) := 0;
                    PCK_NOMINA.CN(116) := 0;
                    PCK_NOMINA.CN(117) := 0;
                    PCK_NOMINA.CN(132) := 0;
                    PCK_NOMINA.CN(115) := 0;
                    PCK_NOMINA.CN(120) := 0;
                    PCK_NOMINA.CN(111) := 0;
                    PCK_NOMINA.CN(112) := 0;
                    PCK_NOMINA.CN(206) := 0;
                    PCK_NOMINA.CN(207) := 0;
                    PCK_NOMINA.CN(208) := 0;
            END IF; --JM FIN CC 2203

        IF PCK_NOMINA.GL_SPER = 2  --(CC:2913_CFBARRERA_INI)
           AND PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI') = 'NO'
           AND PCK_PARST.FC_PAR('APORTAR PENSION EN LNR', 'SI') = 'SI'
           AND PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA', 'SI') = 'NO'
           AND PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR SOLO PATRONAL', 'SI') = 'NO'
           AND PCK_NOMINA_PROC01.FC_AUSENTISMO1DAQUINCENA(
                   PCK_NOMINA.GL_COMPANIA,
                   PCK_NOMINA.GL_SANO,
                   PCK_NOMINA.GL_SMES,
                   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = TRUE
           AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(
                   PCK_NOMINA.GL_COMPANIA,
                   PCK_NOMINA.GL_SANO,
                   PCK_NOMINA.GL_SMES,
                   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = TRUE 
        THEN
            -- Hay ausentismo en ambas quincenas
            -- Calcular aportes SOLO en 2da quincena
            PCK_NOMINA.CN(116) := MI_SALUDP;      
            PCK_NOMINA.CN(117) := MI_PENSIONP;    
            
            -- Validar que NO se calculen:
            PCK_NOMINA.CN(111) := 0;  -- ARL = 0 (no aplica con ausentismo)
            PCK_NOMINA.CN(132) := 0;  -- FSP = 0 (no aplica en LNR)
            PCK_NOMINA.CN(115) := 0;  -- FSP =  (no aplica en LNR)
        END IF; --(CC:2913_CFBARRERA_FIN)
        /*INI_Ajuste para que si la entidad es exonerada el valor del concepto 116 sea cero CC3463 MPEREZ*/
        IF (PCK_PARST.FC_PAR('APLICAR EXONERACION APORTES SALUD PATRONO 8.5%','NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION < PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.GL_IBLREXONERACION > 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05') AND PCK_NOMINA.FC_CN(116) <> 0) THEN
            PCK_NOMINA.CN(116) := 0;                    
        END IF;
        /*INI_Ajuste para que si la entidad es exonerada el valor del concepto 116 sea cero CC3463 MPEREZ*/

    END IF;
END PR_DIFERENCIASBASESNOVEDADES;

PROCEDURE PR_CALCESANTIASFNAALCCAJICA
  /*
  NAME               : PR_CALCESANTIASFNAALCCAJICA
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 10/09/2019
  TIME               : 12:30 PM
  SOURCE MODULE      : NOMINAP2019.09.02_UNIFICADAS MPV 09092019_MPV - 532 NIIF PTU
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LAS CESANTÃ�AS PARA ALCALDIA DE CAJICA
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARCESANTIASFNAALCCAJICA
  */
(

  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
MI_ALIMRET            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
MI_RECARGOSUELDO      PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_PROMFAC            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DIAS               NUMBER DEFAULT 0;
MI_DIASINT            NUMBER DEFAULT 0;
MI_ANTICIPOS          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_APLICADAS          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_PS                 PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_PVFNA              PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_PNFNA              PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_EXTRASFNA          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_CESANTIA1          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN

PCK_NOMINA.GL_SBM := (CASE WHEN  PCK_NOMINA.FC_CN(900) = 0
                          THEN  ( CASE WHEN    PCK_NOMINA.FC_CN(10) > 0
                                  THEN    PCK_NOMINA.FC_CN(10)
                                  ELSE    PCK_NOMINA.FC_CN(1)
                                  END )
                          ELSE  PCK_NOMINA.FC_CN(900)
                          END);


IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
   PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
ELSE
   PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
END IF;

--LEY 50
PCK_NOMINA.GL_FECHAIC :=  (CASE WHEN  PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                               THEN  PCK_NOMINA.GL_FECHAIR
                               ELSE  TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                               END);



      IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
             MI_DIAS := 0;
             MI_DIASINT := 0;
             --GOTO TERMIN;
      ELSE
             MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) -  PCK_NOMINA.GL_LICENCIAS ;
             MI_DIASINT:= PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL( (CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
                                                                 THEN PCK_NOMINA.GL_FECHAIR 
                                                                 ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
                                                                 END)
                                                           , PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;  -- 27022019cvalle   
             --PARA CENTROABASTOS 140208                                                 
             IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA,PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
                MI_DIASINT:= PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL( (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                                                                    THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA
                                                                    ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                                                                    END)
                                                              ,PCK_NOMINA.GL_FECHAFIN1);      
             END IF;

             ----------
             ----------
              MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA( UN_COMPANIA,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                  PCK_NOMINA.GL_FECHAIC,
                                                  PCK_NOMINA.GL_FECHAFIN1 - 8); --31 130208

              PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
               --acumulado ultimo aÃ±o
              PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                       PCK_NOMINA.GL_SANO,
                                                        1,
                                                        1,
                                                        PCK_NOMINA.GL_SANO,
                                                        PCK_NOMINA.GL_SMES,
                                                        99,
                                                        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

               MI_APLICADAS :=  PCK_NOMINA.FC_CNA(277) + PCK_NOMINA.FC_CNA(483);
               PCK_NOMINA.CN(915) := MI_APLICADAS;
               PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
               MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;

               IF PCK_NOMINA.GL_SPER = 8 THEN
                  PCK_NOMINA.CN(2) := PCK_NOMINA.FC_CN(1);  
               END IF;

               --FACTORES ACTUALES
               MI_PS := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 ,0);
               MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(541)) / 12 ,0);
               MI_PNFNA := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CNA(504)) / 12,0);
               MI_EXTRASFNA := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)),0);
               MI_PROMFAC :=  PCK_SYSMAN_UTL.FC_ROUND(  CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE (PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + MI_PS  / 12 + MI_PVFNA / 12 + MI_PNFNA / 12) END,0);

               IF PCK_NOMINA.GL_SPER = 8 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                            PCK_NOMINA.GL_SANO,
                                                            1,
                                                            1,
                                                            PCK_NOMINA.GL_SANO,
                                                            12,
                                                            7,
                                                            PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

                    PCK_NOMINA.CN(902) :=  PCK_NOMINA.FC_CNA(70) / 12 ;
                    MI_PROMFAC :=  (CASE WHEN PCK_NOMINA.FC_CN(969) > 0
                                         THEN PCK_NOMINA.FC_CN(969)
                                         ELSE PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CNA(70) / 12
                                         END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CNA(514) / 12 + PCK_NOMINA.FC_CNA(529) / 12 + PCK_NOMINA.FC_CNA(501) / 12 + PCK_NOMINA.FC_CNA(504)/ 12;

                    MI_PROMFAC := MI_PROMFAC +  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0)
                                             +  PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12 
                                             +  PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12
                                             +  (CASE WHEN  PCK_NOMINA.FC_CN(155) > 0
                                                     THEN  PCK_NOMINA.FC_CN(155)  / 12 
                                                     ELSE  (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12 
                                                     END)   + PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12;                     

                   MI_PROMFAC :=  (CASE WHEN  PCK_NOMINA.FC_CN(969) > 0 
                                       THEN  PCK_NOMINA.FC_CN(969)
                                       ELSE  PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0)
                                       END);

                   MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);
                   PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0'))) := (CASE WHEN PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0'))) = 0
                                                                                                    THEN PCK_SYSMAN_UTL.FC_ROUND(( MI_CESANTIA1 / 360 * MI_DIAS) - MI_APLICADAS - MI_ANTICIPOS,0)
                                                                                                    ELSE PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0')))
                                                                                                    END);

                   IF PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0'))) < 0 THEN
                          --El empleado --NOMEMPLEADO--, Tiene saldo Negativo al periodo 13 de Fondo Nacional del Ahorro, CÃ©dula No. --CEDULA--, Tipo: --TIPO--.
                          MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                          MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                          MI_MSG(2).CLAVE := 'CEDULA';
                          MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                          MI_MSG(3).CLAVE := 'TIPO';
                          MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

                          PCK_NOMINA_COM7.PR_ALERTA
                              (UN_COMPANIA     => UN_COMPANIA
                              ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_SALDONEGATIVOPER13
                              ,UN_REEMPLAZOS   => MI_MSG
                              ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                              ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                              ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                              ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                              ,UN_USER         => PCK_CONEXION.FC_GETUSER
                              );
                   END IF;

                   PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(514)) / 12,0 );
                   PCK_NOMINA.CN(910) := MI_DIAS;
                   PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339); --Licencias + personal!DiasInterrupcion
                   PCK_NOMINA.CN(277) := PCK_SYSMAN_UTL.FC_ROUND( (MI_CESANTIA1 / 360 * MI_DIAS),0);
                   --Guardando Factores
                   PCK_NOMINA.CN(900) := PCK_NOMINA.GL_SBM;--Sueldo
                   PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CNA(525);
                   PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CNA(524);
                   PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(504)) / 12 ,0);
                   PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND(  PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);
                   PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(  (CASE WHEN  PCK_NOMINA.FC_CN(155) > 0 
                                                                        THEN  PCK_NOMINA.FC_CN(155) / 12 
                                                                        ELSE  (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(501)) / 12
                                                                        END)
                                                               ,0);
                   PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
                   PCK_NOMINA.CN(909) := 0;
                   PCK_NOMINA.CN(911) := MI_ANTICIPOS;
                   PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);
                   PCK_NOMINA.CN(914) := 0;

                   IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
                      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'));
                   ELSE
                      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI,PCK_NOMINA.GL_FECHAFIN1);
                  END IF;


               ELSE
                       MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND( (CASE WHEN PCK_NOMINA.FC_CN(969) > 0
                                                                   THEN PCK_NOMINA.FC_CN(969)
                                                                   ELSE PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + MI_PS + MI_PVFNA + MI_PNFNA + MI_EXTRASFNA / 12
                                                                   END)
                                                            ,0);
                       MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND( MI_PROMFAC ,0);
                       PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0'))) := PCK_SYSMAN_UTL.FC_ROUND(( MI_PROMFAC / 360 * MI_DIAS) - MI_APLICADAS - MI_ANTICIPOS,0);
                       PCK_NOMINA.CN(900) := PCK_NOMINA.GL_SBM;
                       PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
                       PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
                       PCK_NOMINA.GL_PROMCONSOLIDADASCN := MI_PROMFAC;
                       MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND( MI_PROMFAC ,0);
                       PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0'))) := PCK_SYSMAN_UTL.FC_ROUND(( MI_PROMFAC / 360 * MI_DIAS) - MI_APLICADAS - MI_ANTICIPOS,0);
                       --Guardando Factores
                       PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(150) / 12 ,0);
                       PCK_NOMINA.CN(902) := PCK_NOMINA.FC_CNA(70) / 12 ;
                       PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CNA(504)) / 12,0);
                       PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 ,0);
                       PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(541)) / 12 ,0);
                       PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
                       PCK_NOMINA.CN(909) := 0;
                       PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11);
                       PCK_NOMINA.CN(911) := MI_ANTICIPOS;
                       PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
                       PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);
                       PCK_NOMINA.CN(914) := 0;

                       IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
                            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'));
                       ELSE
                            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI,PCK_NOMINA.GL_FECHAFIN1);
                       END IF;

               END IF;
     -------------------
     ------------------
      END IF;

--<<TERMIN>>
END PR_CALCESANTIASFNAALCCAJICA;

PROCEDURE PR_CALCULARCESANTIASALCAGUAZUL (
/*
  NAME               : PR_CALCULARCESANTIASALCAGUAZUL
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 11/10/2019
  TIME               : 03:43 PM
  SOURCE MODULE      : NOMINAP2019.10.01_UNIFICADAS MPV 02102019_MPV - 540 NIIF HDA_IDRF
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LAS CESANTÃ�AS PARA ALCALDIA DE AGUAZUL
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARCESANTIASALCAGUAZUL
  */
 UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
MI_RECARGOSUELDO       PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_PROMFAC             PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DIASPROMEDIO        PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_VLR                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_MASDIASOTRAENTIDAD  PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_ANTICIPOS           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DP                  PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DIASINT             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
MI_CESANTIA1           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DATE1 DATE;
MI_DATE2 DATE;


BEGIN
       PCK_NOMINA.GL_BASCES := 0;


MI_DATE1 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
MI_DATE2 := PCK_NOMINA.GL_FECHAINI1;

       IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI1) THEN

           PCK_NOMINA.GL_SBM := (CASE WHEN PCK_NOMINA.FC_CN(900) = 0 
                                      THEN PCK_NOMINA.FC_CN(1) 
                                      ELSE PCK_NOMINA.FC_CN(900) 
                                      END
                                );

                   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN 

                              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >  PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                                            PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                                            PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                                            PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END);
                                            MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);    
                              ELSE
                                            PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                                            PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                                            PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END);
                                            MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN),PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1));

                              END IF;

                                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,
                                                                               PCK_NOMINA.GL_ANOA,
                                                                               PCK_NOMINA.GL_MESA,
                                                                               1,
                                                                               PCK_NOMINA.GL_SANO,
                                                                               (PCK_NOMINA.GL_SMES-1),
                                                                               99,
                                                                               PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                               );
                                       PCK_NOMINA.GL_COMISIONES :=  (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
                                       PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;


                   END IF;

                   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN 

                      PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
                   ELSE
                      PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
                   END IF;

                     PCK_NOMINA.GL_BONPAGADA := 0;

                   IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996','DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN

                                MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 
                                                THEN 1
                                                ELSE 2
                                                END
                                          );


                                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,
                                                                       PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR),
                                                                       PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR),
                                                                       MI_VLR,
                                                                       PCK_NOMINA.GL_SANO,
                                                                       PCK_NOMINA.GL_SMES,
                                                                       99,
                                                                       PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                       );

                                PCK_NOMINA.GL_LICENCIAS :=  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);                          
                                PCK_NOMINA.GL_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR,PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;

                                PCK_NOMINA.GL_DIAS := PCK_NOMINA.GL_DIAS + MI_MASDIASOTRAENTIDAD;
                                MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,
                                                                           PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                           PCK_NOMINA.GL_FECHAIR,
                                                                           PCK_NOMINA.GL_FECHAFIN1
                                                                           );
                                MI_DP := 360;
                                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                        PCK_NOMINA.GL_SANO,
                                                                        1,
                                                                        1,
                                                                        PCK_NOMINA.GL_SANO,
                                                                        12,
                                                                        99,
                                                                        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                      );
                   ELSE

                                PCK_NOMINA.GL_FECHAIC := (CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                                                              THEN PCK_NOMINA.GL_FECHAIR
                                                              ELSE TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                                                              END
                                                        );

                                MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 
                                                THEN 1
                                                ELSE 2
                                                END
                                          );


                                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,
                                                                       PCK_NOMINA.GL_SANO,
                                                                       PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC),
                                                                       MI_VLR,
                                                                       PCK_NOMINA.GL_SANO,
                                                                       PCK_NOMINA.GL_SMES,
                                                                       99,
                                                                       PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                       ); 

                                PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);                                                                 
                                PCK_NOMINA.GL_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC,PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ; 
                                MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,
                                                                           PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                           PCK_NOMINA.GL_FECHAIC,
                                                                           (PCK_NOMINA.GL_FECHAFIN1 - 10)
                                                                           );

                   END IF;


                   MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL( ( CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' ||PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                                                                           THEN PCK_NOMINA.GL_FECHAIR 
                                                                           ELSE TO_DATE('01/01/' ||PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                                                           END
                                                                     ),
                                                                      PCK_NOMINA.GL_FECHAFIN1
                                                                   ) - PCK_NOMINA.GL_LICENCIAS;

                  IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                       PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
                  ELSE
                       IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                             PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND( (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164),0) + PCK_NOMINA.FC_CN(155); 
                       ELSE
                             PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501); 
                       END IF;
                  END IF;


                  PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12,0);
                  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                          PCK_NOMINA.GL_SANO,
                                                          1,
                                                          1,
                                                          PCK_NOMINA.GL_SANO,
                                                          PCK_NOMINA.GL_SMES,
                                                          99,
                                                          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO 
                                                         );
                 IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12 ,0);
                 ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN   
                    PCK_NOMINA.GL_BONPAGADA := PCK_NOMINA.FC_CN(150);
                 END IF;

                 PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503) ) / 12 ,0);
                 PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12 ,0);
                 PCK_NOMINA.CN(902) := ( CASE WHEN PCK_NOMINA.FC_CN(902) = 0 
                                              THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) / 12 ,0)
                                              ELSE PCK_NOMINA.FC_CN(902) 
                                              END
                                       );
                 PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(( PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) /  12 ,0);
                 MI_PROMFAC := ( CASE WHEN PCK_NOMINA.FC_CN(969) > 0 
                                      THEN PCK_NOMINA.FC_CN(969)
                                      ELSE ( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                                  THEN PCK_NOMINA.FC_CN(10) 
                                                  ELSE PCK_NOMINA.FC_CN(1) 
                                                  END 
                                           ) + PCK_NOMINA.GL_GRPNGV +  PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA 
                                      END  
                               );

                 MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);
                 PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);

                 IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS,PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR,PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC * PCK_NOMINA.GL_DIAS / 360),0) - MI_ANTICIPOS ;
                 ELSE
                      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC * PCK_NOMINA.GL_DIAS / 360),0) - MI_ANTICIPOS ;
                 END IF;

                         IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN

                                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                                        PCK_NOMINA.CN(169) := ( CASE WHEN PCK_NOMINA.FC_CN(169) = 0
                                                                     THEN ( CASE WHEN  MI_CESANTIA1 < 0 
                                                                                 THEN  0
                                                                                 ELSE  PCK_SYSMAN_UTL.FC_ROUND( MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360,0) 
                                                                                 END
                                                                          )
                                                                     ELSE PCK_NOMINA.FC_CN(169)
                                                                     END
                                                              );                   
                                    END IF;

                                        PCK_NOMINA.CN(177) := ( CASE WHEN PCK_NOMINA.FC_CN(177) = 0 
                                                                     THEN MI_CESANTIA1
                                                                     ELSE PCK_NOMINA.FC_CN(177)
                                                                     END);   
                         ELSIF  PCK_NOMINA.FC_CN(412) <> 0 THEN


                                  IF NOT (PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996','DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                                     PCK_NOMINA.CN(269) := ( CASE WHEN PCK_NOMINA.FC_CN(269) = 0 
                                                                  THEN ( CASE WHEN MI_CESANTIA1 < 0 
                                                                              THEN 0 
                                                                              ELSE PCK_SYSMAN_UTL.FC_ROUND( MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360,0)
                                                                              END
                                                                        )
                                                                  ELSE PCK_NOMINA.FC_CN(269)
                                                                  END
                                                           );
                                  END IF;

                                  PCK_NOMINA.CN(277) :=  ( CASE WHEN PCK_NOMINA.FC_CN(277) = 0 
                                                                THEN MI_CESANTIA1
                                                                ELSE PCK_NOMINA.FC_CN(277) 
                                                                END
                                                         );
                                  IF  PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = 12 THEN
                                      PCK_NOMINA.PR_INCLUIRNOVEDAD( UN_COMPANIA,
                                                                    1,
                                                                    (PCK_NOMINA.GL_SANO + 1),
                                                                    1,
                                                                    3,
                                                                    PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                    169,
                                                                    PCK_NOMINA.FC_CN(269),
                                                                    NULL,
                                                                    PCK_CONEXION.FC_GETUSER
                                                                   );

                                  END IF;         
                         END IF;


               PCK_NOMINA.CN(900) :=  PCK_NOMINA.FC_CN(1);                                   
               PCK_NOMINA.CN(903) :=  PCK_NOMINA.GL_AUXT;                                   
               PCK_NOMINA.CN(904) :=  PCK_NOMINA.GL_AUXA;
               PCK_NOMINA.CN(901) :=  PCK_NOMINA.GL_GRPNGV;
               PCK_NOMINA.CN(909) :=  PCK_NOMINA.GL_BONPAGADA;
               PCK_NOMINA.CN(910) :=  PCK_NOMINA.GL_DIAS;
               PCK_NOMINA.CN(911) :=  MI_ANTICIPOS;
               PCK_NOMINA.CN(912) :=  PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
               PCK_NOMINA.CN(913) :=  PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC,0);
               PCK_NOMINA.CN(914) :=  PCK_NOMINA.GL_VPT;
               PCK_NOMINA.CN(973) :=  PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI,PCK_NOMINA.GL_FECHAFIN1);

        END IF;

          PCK_NOMINA.GL_CES_FECHAINI   := PCK_NOMINA.GL_FECHAIC;              
          PCK_NOMINA.GL_CES_FECHAFIN   := PCK_NOMINA.GL_FECHAFIN1;            
          PCK_NOMINA.GL_CES_LNR        := PCK_NOMINA.GL_LICENCIAS;            
          PCK_NOMINA.GL_CES_DIASSINLNR := PCK_NOMINA.GL_DIAS + PCK_NOMINA.GL_LICENCIAS;           
          PCK_NOMINA.GL_CES_GR         := PCK_NOMINA.GL_GRPNGV;          
          PCK_NOMINA.GL_CES_PT         := PCK_NOMINA.GL_VPT;          
          PCK_NOMINA.GL_CES_PA         := PCK_NOMINA.GL_VPA;          
          PCK_NOMINA.GL_CES_EXTRAS     := PCK_NOMINA.FC_CN(902);          
          PCK_NOMINA.GL_BASP_CES       := PCK_NOMINA.GL_BONPAGADA;          
          PCK_NOMINA.GL_PS_CES         := PCK_NOMINA.FC_CN(906);          
          PCK_NOMINA.GL_PN_CES         := PCK_NOMINA.FC_CN(905);          
          PCK_NOMINA.GL_PV_CES         := PCK_NOMINA.FC_CN(907);         

END PR_CALCULARCESANTIASALCAGUAZUL;

 PROCEDURE PR_CALCPRIMASEMESTRALALCAGZL (
/*
  NAME               : PR_CALCPRIMASEMESTRALALCAGZL
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 15/10/2019
  TIME               : 04:30 PM
  SOURCE MODULE      : NOMINAP2019.10.01_UNIFICADAS MPV 02102019_MPV - 540 NIIF HDA_IDRF
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO  PRIMA SEMESTRAL  PARA ALCALDIA DE AGUAZUL
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARPRIMASEMESTRALALCAGUAZUL
  */
 UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 

MI_VLR                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_PRIMAJUNIO          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
MI_DIASPACTADOS        PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_INDRETIR            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;


BEGIN

 PCK_NOMINA.GL_FACTORPS := 0;
 PCK_NOMINA.GL_FACTORPS1 := 0;
 PCK_NOMINA.GL_DNT := 0;

 IF PCK_NOMINA.GL_SMES <= 7 THEN
    PCK_NOMINA.GL_FECHAIPS :=  ( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS
                                      THEN PCK_NOMINA.GL_FECHAI
                                      ELSE PCK_NOMINA.GL_FECHAIPS
                                      END
                               );
 END IF;

 IF PCK_NOMINA.GL_SMES > 7 THEN
    PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
    PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
 END IF;

 IF PCK_NOMINA.GL_SMES = 7 THEN
    PCK_NOMINA.GL_FECHAFPS :=  ( CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL 
                                      THEN ( CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                                  THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                                  ELSE PCK_NOMINA.GL_FECHAFIN1
                                                  END
                                           )
                                     ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                     END
                               );

 ELSE
    PCK_NOMINA.GL_FECHAFPS :=  ( CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL 
                                      THEN PCK_NOMINA.GL_FECHAFIN1
                                      ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                      END
                               );
 END IF;

                                 MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 
                                                 THEN 1 
                                                 ELSE 2 
                                                 END
                                           );


                                 PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                         PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS),
                                                                         PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),
                                                                         MI_VLR,
                                                                         PCK_NOMINA.GL_SANO,
                                                                         6,
                                                                         99,
                                                                         PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                       );

                               IF  PCK_NOMINA.GL_SPRC = 99 THEN
                                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                                PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS),
                                                                                PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),
                                                                                MI_VLR,
                                                                                PCK_NOMINA.GL_SANO,
                                                                                6,
                                                                                99,
                                                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                              );
                               END IF;

                               PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);    
                               PCK_NOMINA.CN(946) := 0 ;

                               PCK_NOMINA.GL_FACTORPS := ( CASE WHEN  PCK_NOMINA.FC_CN(10) <> 0 
                                                                THEN  PCK_NOMINA.FC_CN(10)
                                                                ELSE  PCK_NOMINA.FC_CN(1)
                                                                END) + PCK_NOMINA.GL_AUXA +  PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946),0);  

                               PCK_NOMINA.GL_DCC :=  PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;

                               IF PCK_NOMINA.GL_DCC = 179 THEN
                                  PCK_NOMINA.GL_DOCEAVAS := 6;
                                  PCK_NOMINA.GL_DCC := 180;
                               ELSE
                                  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
                               END IF;

                               FOR I IN 7..12 LOOP

                                       PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                 (PCK_NOMINA.GL_SANO - 1),
                                                                                 I,
                                                                                 1,
                                                                                 (PCK_NOMINA.GL_SANO - 1),
                                                                                 I,
                                                                                 99,
                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                              );
                                      IF ( PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                           PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                           PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + ( PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                                      END IF;


                               END LOOP;

                               IF  PCK_NOMINA.GL_SMES <= 7 THEN

                                   FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                       PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                 PCK_NOMINA.GL_SANO,
                                                                                 I,
                                                                                 1,
                                                                                 PCK_NOMINA.GL_SANO,
                                                                                 I,
                                                                                 99,
                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                              );
                                              IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                                 PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                                 PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + ( PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                                              END IF;
                                   END LOOP;

                               END IF;


                               IF PCK_NOMINA.FC_CN(952) > 0 THEN
                                  PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                               END IF;

                               IF PCK_NOMINA.GL_DCC < 179 THEN
                                  PCK_NOMINA.GL_DCC := 0;
                                  PCK_NOMINA.GL_DOCEAVAS := 0;
                               END IF;

                               IF PCK_NOMINA.FC_CN(952) > 0 THEN
                                  PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                                  PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
                               END IF;

                               IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN

                                      PCK_NOMINA.CN(160) := ( CASE WHEN PCK_NOMINA.FC_CN(160) = 0 
                                                                   THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49,0)
                                                                   ELSE PCK_NOMINA.FC_CN(160) - PCK_NOMINA.FC_CNA(160)
                                                                   END);
                               ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN                                 

                                     PCK_NOMINA.CN(160) := ( CASE WHEN PCK_NOMINA.FC_CN(160) = 0 
                                                               THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49,0)
                                                               ELSE PCK_NOMINA.FC_CN(160) - PCK_NOMINA.FC_CNA(160)
                                                               END);
                               ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN

                                     PCK_NOMINA.GL_FECHAIPS := ( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS
                                                                      THEN PCK_NOMINA.GL_FECHAI
                                                                      ELSE PCK_NOMINA.GL_FECHAIPS
                                                                      END);
                                     PCK_NOMINA.GL_DOCEAVAS :=  PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS); 
                                     PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;

                                     IF PCK_NOMINA.GL_SPRC = 99 THEN

                                                FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                                      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                PCK_NOMINA.GL_SANO,
                                                                                                I,
                                                                                                1,
                                                                                                PCK_NOMINA.GL_SANO,
                                                                                                I,
                                                                                                99,
                                                                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                      );

                                                      IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                                                 PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                                                 PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                                                      END IF;

                                                END LOOP;

                                                FOR I IN 6..12 LOOP

                                                      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                (PCK_NOMINA.GL_SANO - 1),
                                                                                                I,
                                                                                                1,
                                                                                                (PCK_NOMINA.GL_SANO - 1),
                                                                                                I,
                                                                                                99,
                                                                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                      );

                                                      IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN

                                                           PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                                           PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));

                                                      END IF;

                                                END LOOP;

                                     END IF;



                                     IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
                                        PCK_NOMINA.GL_DCC := 0;
                                        PCK_NOMINA.GL_DOCEAVAS := 0;
                                     END IF;

                                     IF PCK_NOMINA.FC_CN(952) > 0 THEN
                                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
                                     END IF;

                                     PCK_NOMINA.CN(160) := ( CASE WHEN PCK_NOMINA.FC_CN(160) = 0
                                                                  THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49,0)
                                                                  ELSE PCK_NOMINA.FC_CN(160)
                                                                  END) - PCK_NOMINA.FC_CNA(160) ;


                                    IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
                                           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;

                                           FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                                      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                PCK_NOMINA.GL_SANO,
                                                                                                I,
                                                                                                1,
                                                                                                PCK_NOMINA.GL_SANO,
                                                                                                I,
                                                                                                99,
                                                                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                             );
                                                      IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN

                                                             PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359);
                                                             PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + ( PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)); 
                                                      END IF;
                                           END LOOP;

                                           PCK_NOMINA.CN(160) :=  ( CASE WHEN PCK_NOMINA.FC_CN(160) = 0 
                                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49,0)
                                                                         ELSE PCK_NOMINA.FC_CN(160)
                                                                         END) - PCK_NOMINA.FC_CNA(160);    
                                    END IF;




                               ELSE
                                          PCK_NOMINA.CN(160) :=  ( CASE WHEN PCK_NOMINA.FC_CN(160) = 0 
                                                                         THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49,0)
                                                                         ELSE PCK_NOMINA.FC_CN(160)
                                                                         END) - PCK_NOMINA.FC_CNA(160);
                               END IF;

                               MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
                               MI_DIASPACTADOS :=  PCK_NOMINA.FC_CN(67);
                               PCK_NOMINA.GL_FACTORPS1 := MI_PRIMAJUNIO - ( CASE WHEN PCK_NOMINA.GL_AUXT = 0 
                                                                                 THEN 0
                                                                                 ELSE (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS )
                                                                                 END);
                               MI_INDRETIR := PCK_NOMINA.FC_CN(404);
                               IF PCK_NOMINA.GL_SPER = 4 THEN

                                   FOR I IN 2..699 LOOP

                                      IF (I <> 125) AND (I <> 303) AND (I <> 301) AND (I <> 300) AND (I <> 599) OR ( I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                                           PCK_NOMINA.CN(I) := 0 ; 
                                      END IF;

                                   END LOOP;


                               END IF;

                               PCK_NOMINA.CN(404) := MI_INDRETIR;
                               PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
                               PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
                               PCK_NOMINA.CN(945) := ( CASE WHEN  PCK_NOMINA.FC_CN(10) <> 0
                                                            THEN  PCK_NOMINA.FC_CN(10)
                                                            ELSE  PCK_NOMINA.FC_CN(1)
                                                            END);
                               PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT;
                               PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
                               PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
                               PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV;
                               PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);
                               PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;

                               IF PCK_NOMINA.GL_SPRC = 99 THEN
                                  PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
                                  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                          PCK_NOMINA.GL_SANO,
                                                                          6,
                                                                          1,
                                                                          PCK_NOMINA.GL_SANO,
                                                                          PCK_NOMINA.GL_SMES,
                                                                          99,
                                                                          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                       );
                                 PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) +  PCK_NOMINA.FC_CNA(503);
                                 PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160);
                                 PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993);  
                               END IF;


                               PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS,0);
                               PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
                               PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;





 --calcularprimasemestralALCAGUAZUL


END PR_CALCPRIMASEMESTRALALCAGZL;

PROCEDURE PR_CALCPRIMAVACACIONESAGUAZUL
/*
NAME              : PR_CALCPRIMAVACACIONESAGUAZUL
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
DATE MIGRADOR     : 23/04/2020
TIME              : 04:34 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Se toma como base la funciÃ³n de ToncancipÃ¡ TAR 1000098628
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
        --DC := 0;
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;

        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(984) := 0;
            IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN
				--(APINEDA:27/04/2020)-No se tiene en cuenta la prima semestral calculada en el mes para la base del cÃ¡lculo de vacaciones	
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
                IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF PCK_NOMINA.FC_CN(160) = 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
                    ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                    IF PCK_NOMINA.FC_CN(981) = 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                END IF;
            ELSE
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
			--(APINEDA:27/04/2020)-No se tiene en cuenta la bonificaciÃ³n calculada en el mes para la base del cÃ¡lculo de vacaciones	
            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            --(APINEDA:31/07/2019)-Se agrega NVL a LICENCIAS para que no se presenten inconsistencias con empleados nuevos
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
              ELSE
                0
              END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            --(APINEDA:31/07/2019)- TAR 1000092350 Se restan dÃ­as a disfrutar de vacaciones pendientes CNA(99)
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91) - PCK_NOMINA.FC_CNA(99);
            PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  --(APINEDA:05/03/2019)-Se ajusta condiciÃ³n para beneficios respecto a la fecha de inicio de vacaciones TAR 1000090701
                  (CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) BETWEEN PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.GL_FECHAFIN1 THEN PCK_NOMINA.GL_FECHAINI ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) END)
                END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
            --(APINEDA:31/07/2019)-Se estaba restando dos veces la licencia
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV;

            PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV / 360);
            IF (PCK_NOMINA.GL_DTV - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
                PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_PERIODOS + 1;
            END IF;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);

            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END, 2);
            IF PCK_NOMINA.GL_DIASVAC = 0 THEN

                PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);

            END IF;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_ANOA), PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.GL_DTV = 0 THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            END IF;
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);

                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            END IF;
            IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
                --(APINEDA:31/07/2019)-TAR1000087154 Se eliminan lÃ­neas que estan asignando valor inconsistente a los dÃ­as trabajados
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;

                /*IF PCK_NOMINA.GL_RTA = 0 OR PCK_NOMINA.GL_RTA IS NULL THEN
                PCK_NOMINA.GL_RTA := MSGBOX('SE ESTÃ� CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ�BADOS COMO DÃ�A HÃ�BIL PARA VACACIONES AL EMPLEADO ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
                END IF;*/
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1));

                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_PERIODOS := 1;
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                    PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2) ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC / 360 * PCK_NOMINA.GL_DTV);
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC, 0)) + 1;
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

                        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                            MI_FECHA := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
                            --MI_FECHA := PCK_NOMINA.GL_FECHAFF1;
                            PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF1 - MI_FECHA) + 1;
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        END IF;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                            PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
                PCK_NOMINA.CN(164) := PCK_NOMINA.GL_PERIODOS;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    ELSE
                        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;

                --PCK_NOMINA.GL_RTA := MSGBOX('SE ESTÃ� CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ�BADOS COMO DÃ�A HÃ�BIL PARA VACACIONES AL EMPLEADO ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
                PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);


                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            END IF;
            IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
                PCK_NOMINA.CN(68) := 0;
                PCK_NOMINA.CN(93) := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.FC_CN(155) > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
                    IF PCK_NOMINA.GL_DIASPROP > 315 THEN
                        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
                        PCK_NOMINA.GL_DIASPROP := CASE WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN 0 ELSE PCK_NOMINA.GL_DIASPROP END;
                    END IF;
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN

                        PCK_NOMINA.CN(68) := 15;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN

                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
                        END IF;
                    END IF;
                END IF;
                --(APINEDA:27/04/2020)-Se elimina validaciÃ³n de campo DE_CARRERA debido a que no aplica respecto a la normatividad actual.                
                    --(APINEDA:31/07/2019)-Se eliminan asignaciones de valor repetidas a la variable GL_DTV

                    PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, CASE WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END);


                    PCK_NOMINA.CN(96) := 0;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        IF PCK_NOMINA.GL_RTA = 6 THEN
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        ELSE

                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            END IF;
                            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));

                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                                PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));

                            END IF;

                        END IF;
                    END IF;
                    PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    IF PCK_NOMINA.FC_CN(96) < 0 THEN
                        PCK_NOMINA.CN(96) := 0;
                    END IF;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                        IF PCK_NOMINA.FC_CN(96) < 0 THEN
                            PCK_NOMINA.CN(96) := 0;
                        END IF;
                    END IF;


                    IF PCK_NOMINA.GL_DTV < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3);
                    ELSE
                        --(APINEDA:31/07/2019)-Se modifica secciÃ³n para el cÃ¡lculo de vacaciones proporcionales en retiro.
                        --(MZANGUNA:19/12/2019)
                        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,2);
                        --PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABILDIAS30(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));
                        --(MZANGUNA:20/12/2019)
						PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));
                        MI_VALOR :=  PCK_NOMINA.FC_CN(96);
                        MI_VALOR :=  PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                        MI_VALOR :=  PCK_NOMINA.GL_DTV;
                        MI_VALOR := PCK_NOMINA.GL_DIASVAC;
                        PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);
                    END IF;
                    IF PCK_NOMINA.GL_SPRC = '99' AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175) IS NULL) AND PCK_NOMINA.FC_CN(155) > 0 THEN
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) IS NULL THEN 21 ELSE PCK_NOMINA.FC_CN(96) END;
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;

            END IF;
            PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            PCK_NOMINA.CN(982) := MI_BONPAGADA;
            PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
        ELSE

            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(984) := 0;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            /*IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
                ALERTA 'EL EMPLEADO ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES & ', TIENE ' & PCK_NOMINA.GL_DIASPENDIENTES & ' DIAS PCK_NOMINA.GL_PENDIENTES DE VACACIONES.' & ', CÃ‰DULA NO.' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO & ',TIPO: ' &PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
            END IF;*/
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164) ;
            MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;

            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN

                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA / 12, 0), 0)   ;
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
            END IF;
            PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
            END IF;
        END IF;
    END IF;


    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    PCK_NOMINA.CN(960) := PCK_NOMINA.GL_FACTORESPV ;
    IF PCK_NOMINA.GL_SPRC = '99' THEN
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS    ;
    ELSE
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164)    ;
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS  ;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;

    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;

    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);


    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    IF PCK_NOMINA.GL_SMES = '12' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) > 0 THEN
        PCK_NOMINA.CN(402) := 1;
    END IF;

    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;

END PR_CALCPRIMAVACACIONESAGUAZUL;

PROCEDURE PR_CALPRIMANAVALCAGUAZUL (
/*
  NAME               : PR_CALPRIMANAVALCAGUAZUL
  AUTHOR MIGRACION   : MIGUEL ANGEL CARDENAS 
  DATE MIGRADOR      : 22/10/2019
  TIME               : 11:19 AM
  SOURCE MODULE      : NOMINAP2019.10.01_UNIFICADAS MPV 02102019_MPV - 540 NIIF HDA_IDRF
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO PRIMA DE NAVIDAD PARA ALCALDIA DE AGUAZUL
  PARAMETROS ENTRADA : UN_COMPANIA
  @NAME              : CALCULARPRIMADENAVIDADALCAGUAZUL
  */
 UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
MI_N2                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_N1                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_ANIOS              PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_MESCOM             PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_DNT1               PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
MI_FECHAFPN           DATE;
MI_VLR                PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0;
MI_TRANSPORTELEGAL    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
MI_RETEFUENTE         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

BEGIN 

PCK_NOMINA.GL_PVAC := 0;

IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11')  THEN

                       PCK_NOMINA.GL_FACTORPN := 0;
                       PCK_NOMINA.GL_DNT := 0;
                       PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
                       PCK_NOMINA.GL_FECHAIPN := ( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN
                                                        THEN PCK_NOMINA.GL_FECHAI
                                                        ELSE PCK_NOMINA.GL_FECHAIPN
                                                        END);

                       IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN
                          PCK_NOMINA.GL_FECHAIPN := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
                          PCK_NOMINA.GL_FECHAIPN := ( CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL >= PCK_NOMINA.GL_FECHAIPN
                                                           THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL
                                                           ELSE  PCK_NOMINA.GL_FECHAIPN
                                                           END
                                                    );
                       END IF;

                       IF PCK_NOMINA.GL_FECHAI = TO_DATE('02/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN
                              PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
                       END IF;

                       PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
                       PCK_NOMINA.GL_FECHAIPN1 := ( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1
                                                         THEN PCK_NOMINA.GL_FECHAI
                                                         ELSE PCK_NOMINA.GL_FECHAIPN1
                                                         END);

                       IF PCK_NOMINA.GL_SMES = 12 AND ( PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 ) THEN
                          PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
                          PCK_NOMINA.GL_FECHAIPN1 := PCK_NOMINA.GL_FECHAIPN;
                       END IF;




                       IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN

                                  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
                                  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                          PCK_NOMINA.GL_SANO,
                                                                          PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),
                                                                          1,
                                                                          PCK_NOMINA.GL_SANO,
                                                                          11,
                                                                          99,
                                                                          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                         );
                                 PCK_NOMINA.GL_PVAC := 0;
                                 --  IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                                        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                                 --ELSE

                                 --END IF;
                                 PCK_NOMINA.GL_FACTORPN := ( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0
                                                                  THEN PCK_NOMINA.FC_CN(10)
                                                                  ELSE PCK_NOMINA.FC_CN(1) 
                                                                  END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12 + (PCK_NOMINA.GL_PVAC / 12 ) +  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);  

                                 PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12;
                                 PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12,0);
                                 PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);
                                 PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);
                                 PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);

                                 FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                                               PCK_NOMINA.GL_SANO,
                                                                               I,
                                                                               1,
                                                                               PCK_NOMINA.GL_SANO,
                                                                               I,
                                                                               99,
                                                                               PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                              );

                                      IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN

                                          PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS  - 1;

                                      END IF;

                                 END LOOP;

                                 IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN

                                     IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                                        PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                                     END IF;

                                 END IF;


                                 IF PCK_NOMINA.FC_CN(937) > 0 THEN
                                    PCK_NOMINA.GL_DCC  := PCK_NOMINA.FC_CN(937);
                                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                                 END IF;

                                 PCK_NOMINA.GL_DNT :=  PCK_NOMINA.FC_CN(938);



                                 IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                                        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                                        IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                           PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(  PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC ,0);
                                        END IF;

                                        IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN

                                               PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                                       PCK_NOMINA.GL_SANO,
                                                                                       12,
                                                                                       1,
                                                                                       PCK_NOMINA.GL_SANO,
                                                                                       12,
                                                                                       99,
                                                                                       PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                     );
                                              PCK_NOMINA.CN(158) := PCK_NOMINA.CN(158) - PCK_NOMINA.CNA(158);                                       

                                        END IF;


                                 ELSE
                                        IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                           PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS,0 ); 
                                        END IF;
                                 END IF;
                                 --PCK_NOMINA.GL_AUXA +  PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV +



                       ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN

                                     MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;

                                     MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 
                                                     THEN 1 
                                                     ELSE 2 
                                                     END
                                                );

                                     PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                             PCK_NOMINA.GL_SANO,
                                                                             PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),
                                                                             MI_VLR,
                                                                             PCK_NOMINA.GL_SANO,
                                                                             12,
                                                                             99,
                                                                             PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                            );
                                     IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                                        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / PCK_NOMINA.CNA(164);  
                                     ELSE
                                        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);  
                                     END IF;

                                     PCK_NOMINA.GL_FACTORPN := ( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                                                      THEN PCK_NOMINA.FC_CN(10)
                                                                      ELSE PCK_NOMINA.FC_CN(1)
                                                                      END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12 + (PCK_NOMINA.GL_PVAC / 12) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);

                                     PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12;
                                     PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12,0);
                                     PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);
                                     PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT; 
                                     PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;


                                     FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                             PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                                                      PCK_NOMINA.GL_SANO,
                                                                                      I,
                                                                                      1,
                                                                                      PCK_NOMINA.GL_SANO,
                                                                                      I,
                                                                                      99,
                                                                                      PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                    );

                                             IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                                     PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                             END IF;
                                     END LOOP;



                                     IF PCK_NOMINA.FC_CN(937) > 0 THEN
                                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                                     END IF;

                                     PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);

                                     IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                                                     PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                                                     IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC,0);
                                                     END IF;

                                                     IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                                                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 1,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 99,
                                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                               );
                                                       PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);                                       
                                                     END IF;
                                     ELSE
                                                      IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                          PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS,0);
                                                      END IF;
                                     END IF;






                       ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN

                                        MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');

                                        IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                                             IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                                                PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                                             END IF;
                                        END IF;

                                        MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 
                                                        THEN 1 
                                                        ELSE 2 
                                                        END
                                                   );

                                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                 PCK_NOMINA.GL_SANO,
                                                                                 PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),
                                                                                 MI_VLR,
                                                                                 PCK_NOMINA.GL_SANO,
                                                                                 12,
                                                                                 99,
                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                            );

                                        IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                                           PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / PCK_NOMINA.CNA(164);  
                                        ELSE
                                           PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);  
                                        END IF;

                                        PCK_NOMINA.GL_FACTORPN := ( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                                                         THEN PCK_NOMINA.FC_CN(10)
                                                                         ELSE PCK_NOMINA.FC_CN(1)
                                                                         END ) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12 + (PCK_NOMINA.GL_PVAC / 12) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0); 

                                        PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12;
                                        PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_PVAC / 12 ,0);
                                        PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                                        PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) -  PCK_NOMINA.GL_DNT;
                                        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);

                                        FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                                                     PCK_NOMINA.GL_SANO,
                                                                                     I,
                                                                                     1,
                                                                                     PCK_NOMINA.GL_SANO,
                                                                                     I,
                                                                                     99,
                                                                                     PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                    );

                                            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                                     PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                             END IF;                                        
                                        END LOOP;

                                        IF PCK_NOMINA.FC_CN(937) > 0 THEN
                                           PCK_NOMINA.GL_DCC  := PCK_NOMINA.FC_CN(937);
                                           PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                                        END IF;

                                        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);

                                        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                                                     PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                                                     IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC,0);
                                                     END IF;

                                                     IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                                                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 1,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 99,
                                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                               );
                                                       PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);                                       
                                                     END IF;
                                        ELSE
                                                      IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                          PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS,0);
                                                      END IF;
                                        END IF;



                       ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY' ) THEN


                                     MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
                                     MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                                     MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')) - MI_DNT1;

                                     IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                                                IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO ,'DD/MM/YYYY')THEN
                                                   MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                                                   MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR) - MI_DNT1;
                                                END IF;

                                                IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')  THEN
                                                   MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                                                END IF;    
                                     END IF;


                                     MI_VLR := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 
                                                     THEN 1 
                                                     ELSE 2 
                                                     END
                                                );

                                     PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                                             PCK_NOMINA.GL_SANO,
                                                                             PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),
                                                                             MI_VLR,
                                                                             PCK_NOMINA.GL_SANO,
                                                                             11,
                                                                             99,
                                                                             PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                            );

                                    --IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                                        --   PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                                   -- ELSE
                                           PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                                   -- END IF;

                                      PCK_NOMINA.GL_FACTORPN := (  CASE WHEN PCK_NOMINA.FC_CN(10) <> 0
                                                                        THEN PCK_NOMINA.FC_CN(10)
                                                                        ELSE PCK_NOMINA.FC_CN(1)
                                                                        END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12 + (PCK_NOMINA.GL_PVAC / 12 ) +  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);  

                                      PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                                      PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12,0);
                                      PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12,0);
                                      PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN); 

                                      FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                                           PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                                                     PCK_NOMINA.GL_SANO,
                                                                                     I,
                                                                                     1,
                                                                                     PCK_NOMINA.GL_SANO,
                                                                                     I,
                                                                                     99,
                                                                                     PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                    );

                                            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                                                     PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                                             END IF; 

                                      END LOOP;

                                      IF PCK_NOMINA.FC_CN(937) > 0 THEN
                                           PCK_NOMINA.GL_DCC  := PCK_NOMINA.FC_CN(937);
                                           PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                                      END IF;

                                      PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);

                                        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                                                     PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                                                     IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC,0);
                                                     END IF;

                                                     IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                                                        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(  UN_COMPANIA,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 1,
                                                                                                 PCK_NOMINA.GL_SANO,
                                                                                                 12,
                                                                                                 99,
                                                                                                 PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                                                               );
                                                       PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);                                       
                                                     END IF;
                                        ELSE
                                                      IF PCK_NOMINA.FC_CN(158) = 0 THEN
                                                          PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS,0);
                                                      END IF;
                                        END IF;




                                     --         PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT +
                       END IF;

END IF;

PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);

IF PCK_NOMINA.GL_SPER = 4 THEN
   FOR I IN 2..599 LOOP
       IF (I <> 125) AND (I <> 159) AND (I <> 10) AND (I <> 11) AND (I <> 303) AND (I <> 300) AND (I <> 301) AND (I <> 599) OR (I>= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
           PCK_NOMINA.CN(I) := 0;
       END IF;
   END LOOP;
END IF;

PCK_NOMINA.CN(125) := MI_RETEFUENTE;
PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
PCK_NOMINA.CN(67) :=  PCK_NOMINA.GL_DOCEAVAS;
PCK_NOMINA.CN(930) := ( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0
                             THEN PCK_NOMINA.FC_CN(10)
                             ELSE PCK_NOMINA.FC_CN(1)
                             END);
PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT;
PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67);
PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT;
PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_VPT;
PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;






END PR_CALPRIMANAVALCAGUAZUL;

FUNCTION FC_OBTENERFONDOPORCODAIFP
/*
  NAME               : FC_OBTENERFONDOPORCODAIFP
  AUTHOR MIGRACION   : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR      : 21/11/2019
  TIME               : 6:59 PM
  SOURCE MODULE      : 
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : OBTIENE EL ID DE FONDO DE ACUERDO A CODAIFP RECIBIDO POR PARAMETRO
  PARAMETROS ENTRADA : UN_COMPANIA
  */  
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CODAIFP       IN VARCHAR2
  )
RETURN VARCHAR2
AS
  MI_IDDEFONDO  VARCHAR2(8) :=  '';
  BEGIN
    SELECT ID_DEL_FONDO
    INTO   MI_IDDEFONDO 
    FROM   V_FONDO_DE_PENSIONES
    WHERE  COMPANIA = UN_COMPANIA
      AND  CODAIFP = UN_CODAIFP;

    RETURN MI_IDDEFONDO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN MI_IDDEFONDO;
END FC_OBTENERFONDOPORCODAIFP;

PROCEDURE PR_IMPUESTO_TEMPORAL
/*
  NAME               : PR_IMPUESTO_TEMPORAL
  AUTHOR MIGRACION   : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR      : 04/05/2020
  TIME               : 9:20 AM
  SOURCE MODULE      : Nuevo
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      : 
  DESCRIPTION        : Procedimiento impuesto temporal COVID-19 TAR 1000098833
  */  
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
  ) AS   
  MI_VALPARCODIGOOBLIGA         NUMBER := 0;
  MI_VALPARCODIGOVOLUNTARIO     NUMBER := 0;
  MI_BASEIMPUESTO               PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;  
  MI_TARIFA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; 
  MI_OBLIGATORIO                NUMBER := 0;
  MI_VALOREXENTO                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_VALOR_A_ADICIONAR          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_TOTALIMPUESTO              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;  

  BEGIN   
  --(APINEDA:26/05/2020)-Se agrega parÃ¡metro para controlar si se aplica o no el cÃ¡lculo de impuesto temporal 
  IF PCK_PARST.FC_PAR('CALCULAR IMPUESTO TEMPORAL', 'NO') = 'SI' THEN  
    --SUMATORIA INDICADOR FACTOR_BASE_IMPUESTO_COVID 
    <<BASEIMPUESTOEMERGENCIAS>>                                  
    FOR RS IN (SELECT ID_DE_CONCEPTO
           FROM CONCEPTOS
           WHERE COMPANIA = UN_COMPANIA
           AND FACTOR_BASE_IMPUESTO_COVID <> 0 
        )
    LOOP      
        MI_BASEIMPUESTO := MI_BASEIMPUESTO + PCK_NOMINA.FC_CN(RS.ID_DE_CONCEPTO);
    END LOOP BASEIMPUESTOEMERGENCIAS; 

    BEGIN
        SELECT TARIFA, OBLIGATORIO, VALOR_EXENTO, VALOR_A_ADICIONAR
        INTO MI_TARIFA, MI_OBLIGATORIO, MI_VALOREXENTO, MI_VALOR_A_ADICIONAR
        FROM IMPUESTO_TEMPORAL
        WHERE COMPANIA = UN_COMPANIA 
        AND ANO = PCK_NOMINA.GL_SANO 
        AND (MI_BASEIMPUESTO >= LIMITE_INFERIOR AND MI_BASEIMPUESTO < LIMITE_SUPERIOR)
        OR (MI_BASEIMPUESTO >= LIMITE_INFERIOR AND LIMITE_SUPERIOR IN 0);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_BASEIMPUESTO := 0;
    END;        


    MI_BASEIMPUESTO := MI_BASEIMPUESTO - MI_VALOREXENTO;

    --IMPUESTO SOLIDARIO OBLIGATORIO
    IF MI_OBLIGATORIO <> 0 THEN
        MI_TOTALIMPUESTO := PCK_SYSMAN_UTL.FC_ROUND(MI_BASEIMPUESTO * (MI_TARIFA/100) ,-3);
        MI_VALPARCODIGOOBLIGA := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO DESCUENTO OBLIGATORIO COVID-19', '0'));
        PCK_NOMINA.CN(MI_VALPARCODIGOOBLIGA) := MI_TOTALIMPUESTO;
    ELSE
    --APORTE SOLIDARIO VOLUNTARIO        
        IF PCK_NOMINA.FC_CN(480) <> 0 THEN
            MI_TOTALIMPUESTO := PCK_SYSMAN_UTL.FC_ROUND(MI_BASEIMPUESTO * (MI_TARIFA/100) + MI_VALOR_A_ADICIONAR,-3);
            MI_VALPARCODIGOVOLUNTARIO := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO DESCUENTO VOLUNTARIO COVID-19', '0'));
            PCK_NOMINA.CN(MI_VALPARCODIGOVOLUNTARIO) := MI_TOTALIMPUESTO;
        END IF;
    END IF;                                    
  END IF;    
END PR_IMPUESTO_TEMPORAL;

PROCEDURE PR_CALCULARCESANTIASDUITAMA (
/*
  NAME               : PR_CALCULARCESANTIASDUITAMA en ACCESS calcularcesantiasDUITAMA
  AUTHOR MIGRACION   : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR      : 06/02/2021
  TIME               : 09:00 AM
  SOURCE MODULE      : NOMINAP2020.12.05 UNIFICADAS MPV 23122020 - 618 MALLAMAS
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LAS CESANTÃ�AS PARA ALCALDIA DE DUITAMA
  PARAMETROS ENTRADA : UN_COMPANIA

  @NAME              : calcularCesantiasDuitama
  */

    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RECARGOSUELDO      PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_PROMFAC            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_ALIMRET            PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_DIASPROMEDIO       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_COMISIONES         PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;     
    MI_MASDIASOTRAENTIDAD NUMBER :=0;    
    MI_DP                 PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;  
    MI_DIASINT            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;    
    MI_ANIO               PCK_SUBTIPOS.TI_ANIO   DEFAULT 0;
    MI_MES                PCK_SUBTIPOS.TI_MES    DEFAULT 0;
    MI_DIA                NUMBER :=0;
    MI_CESANTIA1          PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_VALOR              PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;

BEGIN
    
    MI_ALIMRET := 0;
    
    IF PCK_NOMINA.GL_SPER = 6 OR PCK_NOMINA.FC_CN(411) <> 0 THEN

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NOT NULL THEN

            PCK_NOMINA.GL_FECHAFIN1 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;

        ELSE
             PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_FANTICIPO_CESANTIAS
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );
        END IF;
    END IF;
    
      PCK_NOMINA.GL_BASCES := 0;
      MI_RECARGOSUELDO := 0;
      MI_PROMFAC := 0;
      
   IF PCK_NOMINA.FC_CN(1) < PCK_NOMINA.FC_CN(89) THEN    
    PCK_NOMINA.GL_AUXA := PCK_NOMINA.FC_CN(82);   
   END IF;
   

   IF PCK_NOMINA.FC_CN(1) < PCK_NOMINA.FC_CN(201) * 2 THEN    
    PCK_NOMINA.GL_AUXT := PCK_NOMINA.FC_CN(81);   
   END IF;   

    --  revisar porque le suma 1 dia  enero 04
    --  If s_prc = "99" Then
    --    FIPromC = IIf(CVDate(FechaIR) < CVDate(FechaAnoAtras(CVDate(FECHAFIN1))), FechaAnoAtras(CVDate(FECHAFIN1)), CVDate(FechaIR)) '+ 1
    -- Else
    --    FIPromC = IIf(CVDate(FechaIR) < CVDate(FechaAnoAtras(CVDate(FECHAFIN1))), FechaAnoAtras(CVDate(FECHAFIN1)), CVDate(FechaIR)) + 1
    -- End If   
    
    
    -- Esta validacion hace lo mismo pero se migro tal cual como esta en Access
    IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS,PCK_NOMINA.GL_FECHAFIN1) > 90 THEN        
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;    
    ELSE  -- Si no se Calcula el promedio        
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;    
    END IF;
    
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN --GANAN COMISIONES
        -- SALARIO  PROMEDIO MENSUAL
        -- acumulados del ultimo aÃ±o        
        IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN --septiembre/2001
          PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
          PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
          PCK_NOMINA.GL_PERA :=  CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16
                                 THEN 1
                                 ELSE 2
                                 END ;
          MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1, 3,0);
       ELSE
          PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
          PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)); 
          PCK_NOMINA.GL_PERA := CASE WHEN  PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN))< 16
                                THEN 1
                                ELSE 2
                                END;
          MI_DIASPROMEDIO    := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN),PCK_NOMINA.GL_FECHAFIN1,3,0);                              
       END IF;     
              --  ac = AcumCC(CStr(VAL(s_Ano) - 1), strzero(s_mes + 9, 2), "01", IIf(VAL(s_mes) = 1, CStr(VAL(s_Ano) - 1), s_Ano), IIf(VAL(s_mes) = 1, "12", strzero(s_mes - 1, 2)), "99", personal!NUMERO_DCTO)
              --  ac = AcumCC(s_Ano, strzero(s_mes - 3, 2), "01", s_Ano, strzero(s_mes - 1, 2), "99", personal!NUMERO_DCTO)       
       PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
       MI_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
       PCK_NOMINA.CN(971) := MI_COMISIONES;        
       
    END IF;
        
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
         PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + MI_COMISIONES;
    ELSE
         PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
    END IF;    

  -- revisar aqui si el regimen o la fecha esta bien
  -- ultquinquenio = valorultimoquinquenio(personal!Id_de_Empleado)
  /*IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996','DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
         MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR);
         MI_MES  := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR);
         MI_DIA  := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16
                    THEN 1
                    ELSE 2
                    END ;

         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,MI_ANIO,MI_MES,MI_DIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- Acumulados
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) ;
         PCK_NOMINA.GL_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS; --- personal!DiasInterrupcion  'ESTOS DIAS DE INTERRUPCION YA FUERON DESCONTADOS EN LA FECHA DE INGRESO REAL
         --' LOS DIAS EN OTRA ENTIDAD SE ADICIONAN ADEMAS DEL TENER EN CUENTA LA CONTINUIDAD
         --' DADA EN LA DIFERENCIA DE FACHAS ENTRE LA FECHA DE INGRESO Y LA FECHA DE INGRESO A ENTIDADES SIMILARES.     
        PCK_NOMINA.GL_DIAS := PCK_NOMINA.GL_DIAS + MI_MASDIASOTRAENTIDAD;  
        PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
        MI_DP := 360;
        --ac = AcumCC(CStr(Anoa), strzero(mesa, 2), strzero(pera, 2), CStr(s_Ano), strzero(s_mes, 2), "99", personal!NUMERO_DCTO) ' Acumulado del ultimo aÃ±o
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,1,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        PCK_NOMINA.GL_FECHAFINC := PCK_NOMINA.GL_FECHAFIN1;
   ELSE    --ley50  */       
         PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                                  THEN PCK_NOMINA.GL_FECHAIR
                                  ELSE TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                  END;

         MI_MES := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC);
         MI_DIA :=  CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16
                    THEN 1
                    ELSE 2
                    END ;
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, MI_MES,MI_DIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;--Acumulado del aÃ±o actual                                             
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
         PCK_NOMINA.GL_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;         
         PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 31); 
        --'Dp = diasmescomercial(CVDate(FIPromC), CVDate(FECHAFIN1))
         PCK_NOMINA.GL_FECHAFINC := PCK_NOMINA.GL_FECHAIC;
        
    --END IF;    
    
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                                           THEN      PCK_NOMINA.GL_FECHAIR
                                                           ELSE      TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')
                                                           END , PCK_NOMINA.GL_FECHAFIN1);
                                                           
    IF PCK_NOMINA.FC_CN(155) = 0 THEN
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
        --PVac = Round((cn(1) + AUXT + AUXA + Cna(160) / 12) / 2, 0)
           PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(541) + PCK_NOMINA.FC_CNA(501);        
        ELSE            
            IF PCK_NOMINA.GL_DIAS < 360 THEN
               PCK_NOMINA.GL_PVAC := 0;
            ELSE 
               PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(541) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) ;
            END IF;
        END IF;
        
    ELSE
         PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) ;
        
    END IF;
    
 -- 'acumulado Ãšltimo aÃ±o       
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1,1,PCK_NOMINA.GL_SANO,12,21, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    PCK_NOMINA.CN(908) := ROUND(PCK_NOMINA.FC_CNA(150) / 12 + (PCK_NOMINA.FC_CNA(538) / 12) + (PCK_NOMINA.FC_CNA(514) / 12), 0);
    PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 
                             THEN ROUND((PCK_NOMINA.FC_SUMACONA(47,60) + PCK_NOMINA.FC_SUMACON(47,60) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(507) 
                                    + PCK_NOMINA.FC_CNA(508)+PCK_NOMINA.FC_CNA(518) +PCK_NOMINA.FC_CNA(519)+PCK_NOMINA.FC_CNA(520)+ PCK_NOMINA.FC_CNA(521) + PCK_NOMINA.FC_CNA(522)
                                    + PCK_NOMINA.FC_CNA(523)+ PCK_NOMINA.FC_CNA(546))/12, 0)
                             ELSE  PCK_NOMINA.FC_CN(902)      /12 END; -- EXTRAS
                
    --'TAR:1000097089; 23/01/2020; MCAR se quita la divisiÃ²n del concepto 150 y 538 ya que en el concepto 908 esta esa operaciÃ³n
    MI_PROMFAC :=  CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE 
            PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CNA(160) / 12 + ROUND(PCK_NOMINA.FC_CNA(503) / 12, 0) + PCK_NOMINA.GL_PVAC / 12 + PCK_NOMINA.FC_CNA(158) / 12 + PCK_NOMINA.FC_CNA(504) / 12 + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.FC_CN(908) END; --  ' Cna(538) / 12 'Cna(150) / 12
    
   
    --Las siguientes validaciones hacen lo mismo se migra tal cual desde ACCESS
   IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS,PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR,PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
     MI_CESANTIA1 := ROUND ((MI_PROMFAC *   PCK_NOMINA.GL_DIAS / 360) ,0) - PCK_NOMINA.GL_ANTICIPOS_CES;
   
   ELSE
    -- 'SI LLEVA MENOS DE UN AÃ‘O DE TRABAJO.
    
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR,PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := ROUND ((MI_PROMFAC *   PCK_NOMINA.GL_DIAS / 360) ,0) - PCK_NOMINA.GL_ANTICIPOS_CES;
        ELSE
            MI_CESANTIA1 := ROUND ((MI_PROMFAC *   PCK_NOMINA.GL_DIAS / 360) ,0) - PCK_NOMINA.GL_ANTICIPOS_CES;        
        END IF;
   END IF;
   
   
   IF  PCK_NOMINA.FC_CN(404) <> 0  OR PCK_NOMINA.FC_CN(411) <> 0 THEN --octubre 05/
   
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 AND PCK_PARST.FC_PAR('CALCULAR INTERESES DE CESANTÃ�AS EN EL PERIODO 6 PARA LOS EMPLEADOS RETROACTIVOS', 'NO') = 'SI' THEN
        PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN 
                                    CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE ROUND (MI_CESANTIA1 * 12 / 100 * MI_DIASINT/360 , 0) END
                                 ELSE PCK_NOMINA.FC_CN(169) END;
    ELSE
        PCK_NOMINA.CN(169) := 0 ;
    END IF;
        PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN  MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END ;
   
   ELSIF  PCK_NOMINA.FC_CN(412) <> 0 THEN
        IF NOT(PCK_NOMINA.GL_FECHAI <  TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                
        PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN
                        CASE WHEN MI_CESANTIA1 < 0 THEN  0 ELSE Round(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END  ELSE PCK_NOMINA.FC_CN(269) END;
        END IF;
        
        PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END ;
        
         --'If cn(425) <> 0 And s_mes = "12" Then
         --'   IncluirNovedad "01", CStr(VAL(s_Ano) + 1), "01", "03", personal!Id_de_Empleado, "169", cn(269)
         --'End If        
   END IF;
   --15/01/2010 Se adiciona para que pueda llamar el pago de los intereses desde el mes y periodo en el que se esta
   
   IF  PCK_NOMINA.CN(425)  <> 0 THEN 
   
    IF PCK_NOMINA.GL_SMES = 12 THEN
    BEGIN
        SELECT VALOR
        INTO MI_VALOR
        FROM HISTORICOS
        WHERE COMPANIA = UN_COMPANIA 
          AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC 
          AND ANO = PCK_NOMINA.GL_SANO 
          AND MES = 12 
          AND PERIODO = 08
          AND ID_DE_CONCEPTO = 269 
          AND ID_DE_EMPLEADO =PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
       
       EXCEPTION WHEN NO_DATA_FOUND THEN   
      		MI_VALOR := PCK_NOMINA.FC_CN(269);
       END;   
     
     ELSE
     BEGIN
        SELECT VALOR
        INTO MI_VALOR
        FROM HISTORICOS
        WHERE COMPANIA = UN_COMPANIA 
          AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC 
          AND ANO = PCK_NOMINA.GL_SANO - 1
          AND MES = 12 
          AND PERIODO = 08
          AND ID_DE_CONCEPTO = 269 
          AND ID_DE_EMPLEADO =PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
       
       EXCEPTION WHEN NO_DATA_FOUND THEN   
       		MI_VALOR := 0;
       END;  
          
    END IF; 
    
        PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, PCK_NOMINA.GL_SPRC, (PCK_NOMINA.GL_SANO + 1) , 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, MI_VALOR);
        PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN MI_VALOR ELSE PCK_NOMINA.FC_CN(169) END ;
   END IF;
   
   --'06/08/2009 el cn(483) para sogamoso es el que guarda el pago de FNA
      IF PCK_NOMINA.GL_SPER = 08 And PCK_NOMINA.GL_SMES = 12 THEN
        FOR I IN 2 .. 899 LOOP
            IF I <> 269 AND I <> 277 AND I <> 412 AND I <> 483 THEN 
                PCK_NOMINA.CN(I) := 0;
            END IF;
        END LOOP;
       END IF;
    
  -- 'Guardando Factores
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1); --'SBM                                                     ' Sueldo
      PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
      PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
      PCK_NOMINA.CN(905) := ROUND(PCK_NOMINA.FC_CNA(158) / 12, 0) + (PCK_NOMINA.FC_CNA(504) / 12);
      PCK_NOMINA.CN(906) := ROUND(PCK_NOMINA.FC_CNA(160) / 12, 0) + ROUND(PCK_NOMINA.FC_CNA(503) / 12, 0);
      PCK_NOMINA.CN(907) := ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
      PCK_NOMINA.CN(908) := ROUND(PCK_NOMINA.FC_CNA(150) / 12, 0) + (PCK_NOMINA.FC_CNA(538) / 12) + (PCK_NOMINA.FC_CNA(514) / 12); -- 'tomado para la bonificacion de servicios presCna(500) + cn(500)                                   ' Prima de Localizacion
      PCK_NOMINA.CN(909) := 0; -- 'Round(ultq / 5, 0)                                                                ' Ultimo Quinquenio
      PCK_NOMINA.CN(910) := PCK_NOMINA.GL_DIAS; --                                                                   ' Dias
      PCK_NOMINA.CN(911) := PCK_NOMINA.GL_ANTICIPOS_CES;--                                                           ' Anticipos
      PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;    --    ' Dias no trabajados por licencias
      PCK_NOMINA.CN(913) := ROUND((MI_PROMFAC), 0);                                                                --     ' Promedio
      PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;      
      --''PROMFAC = IIf(cn(969) > 0, cn(969), cn(1) + AUXA + AUXT + + (Cna(158) / 12) + (Cna(174) / 12) + ((Cna(150)) / 12))    
      
      IF PCK_NOMINA.GL_SPER = 6 And PCK_PARST.FC_PAR('PAGAR INTERESES PARCIALES EN NOMINA MENSUAL', 'NO') = 'SI' THEN
        
          PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, PCK_NOMINA.GL_PROCESOACTUAL, PCK_NOMINA.GL_ANOACTUAL ,PCK_NOMINA.GL_SMES, 
                                      CASE WHEN PCK_PARST.FC_PAR('NOMINA MENSUAL','NO') = 'SI' THEN 03 ELSE  CASE WHEN EXTRACT (DAY FROM CURRENT_DATE) < 12 THEN 01 ELSE 02 END END, 
                                      PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, 
                                      CASE WHEN PCK_NOMINA.FC_CN(269) <> 0 AND PCK_NOMINA.FC_CN(169) = 0 THEN PCK_NOMINA.FC_CN(269) ELSE PCK_NOMINA.FC_CN(169) END);  
                                      
         PCK_NOMINA_CALCULO.PR_INCLUIRCESANTIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, (PCK_NOMINA.GL_FECHAFIN), (PCK_NOMINA.FC_CN(177)), (PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(269) <> 0 AND PCK_NOMINA.FC_CN(169) = 0, PCK_NOMINA.FC_CN(269), PCK_NOMINA.FC_CN(169))), (PCK_NOMINA.GL_FECHAFIN), (PCK_NOMINA.FC_CN(1)), (PCK_NOMINA.FC_CN(913)), 0, (PCK_NOMINA.FC_CN(911)), 0, 0);                                        
         
        PCK_NOMINA.CN(974) := 0;
        PCK_NOMINA.CN(169) := 0;
        
         IF PCK_NOMINA.GL_SPER = 6 AND   PCK_NOMINA.GL_SMES = 12 THEN
           FOR I IN 2 .. 899 LOOP
            IF I <> 412 AND I <> 404 AND I <> 411 AND I <> 177 THEN 
                PCK_NOMINA.CN(I) := 0;
            END IF;
          END LOOP;
          END IF;   
            IF PCK_PARST.FC_PAR('PAGAR INTERESES PARCIALES EN NOMINA MENSUAL','NO') = 'NO' THEN
                PCK_NOMINA.CN(169) := PCK_NOMINA.FC_CN(974);
            END IF;
         
      ELSE
            
        IF PCK_NOMINA.GL_SPER = 6 THEN        
           FOR I IN 2 .. 899 LOOP
            IF I <> 412 AND I <> 404 AND I <> 411 AND I <> 177 AND I <>169 THEN 
                PCK_NOMINA.CN(I) := 0;
            END IF;
          END LOOP;
        
        END IF;
      
      END IF;
     
     
        IF PCK_NOMINA.GL_SPER = 6 THEN -- 'Or par("PAGAR INTERESES PARCIALES EN NOMINA MENSUAL") = "SI" Then '22062010
              FOR I IN 2 .. 899 LOOP
                IF I <> 177 AND I <> 169 Then
                   PCK_NOMINA.CN(I) := 0;
                END IF;
              END LOOP;
              
            IF PCK_PARST.FC_PAR('PAGAR INTERESES PARCIALES EN NOMINA MENSUAL','NO') = 'SI' THEN
                 PCK_NOMINA.CN(169) := 0;
            END IF;
            PCK_NOMINA.CN(144) := PCK_NOMINA.FC_CN(177) + PCK_NOMINA.FC_CN(169);
            
        END IF ;        
        
    --'20062018 CESANTIAS
 PCK_NOMINA.GL_CES_FECHAINI := PCK_NOMINA.GL_FECHAIC;
 PCK_NOMINA.GL_CES_FECHAFIN := PCK_NOMINA.GL_FECHAFINC;
 PCK_NOMINA.GL_CES_LNR := PCK_NOMINA.GL_LICENCIAS;
 PCK_NOMINA.GL_CES_DIASSINLNR := PCK_NOMINA.GL_DIAS + PCK_NOMINA.GL_LICENCIAS;
 PCK_NOMINA.GL_CES_GR := PCK_NOMINA.GL_GRPNGV;
 PCK_NOMINA.GL_CES_PT := PCK_NOMINA.GL_VPT;
 PCK_NOMINA.GL_CES_PA := PCK_NOMINA.GL_VPA;
 PCK_NOMINA.GL_CES_EXTRAS := PCK_NOMINA.FC_CN(902);
 PCK_NOMINA.GL_BASP_CES := PCK_NOMINA.GL_BONPAGADA; 
 PCK_NOMINA.GL_PS_CES := PCK_NOMINA.FC_CN(906); 
 PCK_NOMINA.GL_PN_CES := PCK_NOMINA.FC_CN(905); 
 PCK_NOMINA.GL_PV_CES := PCK_NOMINA.FC_CN(907);      
      
END PR_CALCULARCESANTIASDUITAMA;

PROCEDURE PR_CALPRIMANAVIDADDUITAMA (
/*
  NAME               : PR_CALPRIMANAVIDADDUITAMA en ACCESS calcularprimadenavidadDUITAMA
  AUTHOR MIGRACION   : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR      : 08/02/2021
  TIME               : 08:00 AM
  SOURCE MODULE      : NOMINAP2020.12.05 UNIFICADAS MPV 23122020 - 618 MALLAMAS
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CÃ�LCULO DE LA PRIMA DE NAVIDAD PARA ALCALDIA DE DUITAMA
  PARAMETROS ENTRADA : UN_COMPANIA

  @NAME              : calcularPrimaNavidadDuitama
  */

    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
    MI_VALORULTIMABASPPAGADA  PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_FECHAFPN               DATE;
    MI_DCC1                   NUMBER DEFAULT 0;
    MI_N2                     PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
    MI_N1                     PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
    MI_VALOR                  NUMBER(20,2)  ;
    
BEGIN

    MI_VALORULTIMABASPPAGADA :=  PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);    
    
    PCK_NOMINA.CN(939) := ROUND(PCK_NOMINA.FC_CN(186),0);
    
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '06' THEN
    
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO IN ('10','11')   THEN
            
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/'||PCK_NOMINA.GL_SANO ,'DD/MM/YYYY');
            
            PCK_NOMINA.GL_FECHAIPN  := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN 
                                       THEN PCK_NOMINA.GL_FECHAI 
                                       ELSE PCK_NOMINA.GL_FECHAIPN END;
                                       
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');  
            
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                    PCK_NOMINA.GL_SANO,
                                                    EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                    CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN)<16 THEN 01 ELSE 02 END,
                                                    PCK_NOMINA.GL_SANO,
                                                    11,
                                                    99,
                                                    PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                    );
                                                    
               IF PCK_NOMINA.FC_CN(155) = 0 THEN 
                                                    
                 IF PCK_NOMINA.FC_CNA(155) = 0 THEN --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                 
                    PCK_NOMINA.GL_PVAC := ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(150))/12 ))/2 , 0);
                 
                 ELSE
                    
                    IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                        PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA.FC_CNA(155) / PCK_NOMINA.FC_CNA(164),0);                        
                    ELSE
                        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
                    END IF;                    
                 
                 END IF;
               
               ELSE
               
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
               
               END IF;
               
          PCK_NOMINA.CN(931) :=  ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
          PCK_NOMINA.CN(932) :=  ROUND(PCK_NOMINA.GL_PVAC / 12  ,0);
          PCK_NOMINA.CN(939) :=  ROUND(PCK_NOMINA.FC_CN(186),0);
          PCK_NOMINA.CN(940) :=  ROUND((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CNA(538)) / 12 ,0);
          
          
          PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT +  PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(530) / 12) + (PCK_NOMINA.FC_CNA(541) / 12) + (PCK_NOMINA.FC_CNA(174) / 12) + ((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CNA(538)) / 12);
          
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
             PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.GL_FACTORPN * 31) / 30;
         END IF;
          
          FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
              PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA,
                                                      PCK_NOMINA.GL_SANO, 
                                                      I,
                                                      01, 
                                                      PCK_NOMINA.GL_SANO, 
                                                      I,
                                                      99, 
                                                      PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                                                      
              IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;                 
  
          END LOOP;
          
         IF PCK_NOMINA.FC_CN(158) = 0 THEN
            PCK_NOMINA.CN(158) := ROUND((PCK_NOMINA.GL_FACTORPN / 12) * PCK_NOMINA.GL_DOCEAVAS, 0);
         END IF;
      --'cn(158) = Round(cn(1) + AUXT + AUXA + (cn(160) / 12) + (cn(155) / 12), 0) * Mescom / 12
          
          
      ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '99' THEN
            PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(1);
            PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);      
      
      ELSE
            PCK_NOMINA.GL_FACTORPN := 0;
            PCK_NOMINA.GL_DNT := 0;
            PCK_NOMINA.GL_DNT1 := 0;
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')    ;
            PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPN END;
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/'|| PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')    ;
            PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPN1 END;
        --'CASO 1: NORMAL
         IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN
         
            MI_FECHAFPN := TO_DATE('31/12/'|| PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,
                                                    PCK_NOMINA.GL_SANO, 
                                                    EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN), 
                                                    CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END, 
                                                    PCK_NOMINA.GL_SANO, 
                                                    12, 
                                                    99, 
                                                    PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                                                    
         --'Factores para el calculo de la prima de navidad                                                    
            PCK_NOMINA.GL_PVAC := 0;
            PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(541) + PCK_NOMINA.FC_CNA(501);
           --''If Cna(162) > 15 Then 'Se quita la condicion debido a que no se explica el por que?? NubiaJ y DiegoA
           --''  PVac = (Cna(155) + Cna(541) + Cna(501)) * 15 / Cna(162)
           --''End If
           --'FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12)             
             
            PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(150) +  PCK_NOMINA.FC_CNA(514))/ 12)+ ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)  + ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / 12)   ;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
               PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.GL_FACTORPN * 31) / 30;
            END IF;

           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0  THEN MI_VALORULTIMABASPPAGADA  ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN , MI_FECHAFPN); --'- DNT
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           
           FOR I IN 1..PCK_NOMINA.GL_SMES LOOP

                      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM( UN_COMPANIA, 
                                                               PCK_NOMINA.GL_SANO,
                                                               I,
                                                               01,
                                                               PCK_NOMINA.GL_SANO,
                                                               I,
                                                               99,
                                                               PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                              );

                    IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;

            END LOOP;           
            
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN

                     IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                        PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                     END IF;

            END IF;
            MI_DCC1 := 0;
            IF PCK_NOMINA.FC_CN(158) = 0 THEN          
                PCK_NOMINA.CN(158) := Round((PCK_NOMINA.GL_FACTORPN / 12) * PCK_NOMINA.GL_DOCEAVAS, 0);                
            END IF;
         
         
         ELSIF PCK_NOMINA.FC_CN(404) <> 0 Then
            -- 'CASO 2: CUANDO SE ORDENAR LIQUIDACION
           MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
           PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FechaIpn),
                                                  CASE WHEN EXTRACT (DAY FROM PCK_NOMINA.GL_FechaIpn) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                                  );
                                                  
          --'Factores para el calculo de la prima de navidad
           IF PCK_NOMINA.FC_CN(155) = 0 THEN
               IF (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) = 0 Then -- ' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                  PCK_NOMINA.GL_PVAC := Round((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12)) / 2, 0);
               ELSE
                  IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                      PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164);
                  ELSE
                      PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501));
                  END IF;
    
               END IF;
           ELSE
               PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
           END IF;
          --'FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12) 
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12, 0);
           PCK_NOMINA.GL_DCC :=  PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  01,
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                                                  );
               
               IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;
           END LOOP;
           IF PCK_NOMINA.FC_CN(158) = 0 Then
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF;
         
         ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN         
          ---' Caso 3: Cuando Ingresa entre el 01/01 y el 30/06 y no se retira
           MI_FECHAFPN := TO_DATE('31/12/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');    
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/'|| PCK_NOMINA.GL_SANO) Then
                 PCK_NOMINA.GL_DCC :=  PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR) -  PCK_NOMINA.GL_DNT ;
              END IF;
           END IF ;
           --'07122018 AC = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO) '12092018MPV
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT (MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT (Day FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  ); 
                                                  
          --'Factores para el calculo de la prima de navidad                                                  
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN  --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0; --'Round((cn(1) + AUXT + AUXA + Cna(160) / 12) / 2, 0)
            ELSE
              PCK_NOMINA.GL_PVAC := 0;-- ' Cna(155)
            END IF;
         --'FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12)
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END ) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12, 0);
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN)      ;   
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  01,
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                                                  );
               
               
               IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;              
           END LOOP;         
           
               IF PCK_NOMINA.FC_CN(158) = 0 Then
                  PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 12 *  PCK_NOMINA.GL_DOCEAVAS, 0);
               END IF;
         
         ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            --' caso 4: Cuando ingresa despues del 30 de Junio
           MI_FECHAFPN := TO_DATE('31/12/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
 --          acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO)
 --          DNT = Cna(356) + Cna(357) + Cna(359) + cn(356) + cn(357) + cn(359)+ cna(339)+ cn(339)
 --          acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn1), 2), IIf(Day(FechaIpn1) < 16, "01", "02"), CStr(s_Ano), "11", "99", personal!NUMERO_DCTO)
 --          DNT1 = Cna(356) + Cna(357) + cn(356) + cn(357) + cn(359)
           -- Trae acumulados           
           MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_DNT1;
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                 MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                 MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT1;
              END IF;
              IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                 MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
           
           --'07122018 AC = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO) '12092018MPV
           PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  ); 
           --'Factores para el calculo de la prima de navidad
           IF PCK_NOMINA.FC_CNA(155) = 0 THEN  --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0; -- 'Round((cn(1) + AUXT + AUXA + Cna(160) / 12) / 2, 0)
           ELSE
              PCK_NOMINA.GL_PVAC := 0; -- Cna(155)
           END IF;
           --FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12)
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12, 0);
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);        
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  01,
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  );               
               
               IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;
           END LOOP;           
           IF PCK_NOMINA.FC_CN(158) = 0 Then
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF;  
         
         END IF;     
            
      END IF;
      
    ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --' empleados oficiales se paga 31 dias si son 12 meses, sino proporcionalmente Sept 22/05
      PCK_NOMINA.GL_FACTORPN := 0;
      PCK_NOMINA.GL_DNT := 0;
      PCK_NOMINA.GL_DNT1 := 0;
      PCK_NOMINA.GL_FECHAIPN :=  TO_DATE('01/01/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
      PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
      PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
      PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
      --'CASO 1: NORMAL       
      IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
           MI_FECHAFPN := TO_DATE('31/12/' ||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
           PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  ); --'12092018MPV
           --'Factores para el calculo de la prima de navidad
           PCK_NOMINA.GL_PVAC := 0;
           PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501));
           --'17/07/2009 Agregado para que tome el auxt y de auxa como factor en periodo 07
           PCK_NOMINA.GL_AUXT := CASE WHEN PCK_NOMINA.FC_CN(201) * 2 >= PCK_NOMINA.FC_CN(1) THEN PCK_NOMINA.FC_CN(81) ELSE 0 END;
           PCK_NOMINA.GL_AUXA := CASE WHEN PCK_NOMINA.FC_CN(89) >= PCK_NOMINA.FC_CN(1) THEN PCK_NOMINA.FC_CN(82) ELSE 0 END;
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(543)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(321) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12);
           
          IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          --' 05122018   factorpn = factorpn * 31 / 30
             PCK_NOMINA.GL_FACTORPN := ((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA) / 30 * 31) + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(543)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150) > 1 AND MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12);
          END IF;
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(543)) / 12, 0);
           PCK_NOMINA.CN(932) := Round( PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12, 0);
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN); --'- DNT
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN)          ;
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  01,
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  );
        
               IF (PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0) OR(PCK_NOMINA.CNA.EXISTS(339) AND PCK_NOMINA.CNA(339) > 0) OR(PCK_NOMINA.CN.EXISTS(339) AND PCK_NOMINA.CN(339) > 0) THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                   PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;
           END LOOP;
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                 PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
           MI_DCC1 := 0  ;
           --'05122018 MPV SEGUN CARTA doceavas = Round(doceavas * 31 / 12, 0)
            --'1. No estÃ¡ tomando el valor de los retroactivos de la BonificaciÃ³n por servicios prestados , prima de vacaciones.
            --'2. A los trabajadores oficiales no los estÃ¡ liquidando bien, que son 31 dÃ­as de salario mÃ¡s las doceavas pagadas en el aÃ±o.           
           IF PCK_NOMINA.FC_CN(158) = 0 Then
             -- '05122018 MPV SEGUN CARTA cn(158) = Round(factorpn / 30 * doceavas, 0)
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF;
      
      
      
      ELSIF PCK_NOMINA.FC_CN(404) <> 0 Then
      --'CASO 2: CUANDO SE ORDENAR LIQUIDACION
           MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;      
--'           acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO)
--'           DNT = Cna(356) + Cna(357) + Cna(359) + cn(356) + cn(357) + cn(359)
--'           acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn1), 2), IIf(Day(FechaIpn1) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO)
--'           DNT1 = Cna(356) + Cna(357) + cn(356) + cn(357) + cn(359)
--           ' Trae acumulados      

        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  );--'12092018MPV
      
           IF PCK_NOMINA.FC_CN(155) = 0 THEN
               IF PCK_NOMINA.FC_CNA(155) = 0 THEN --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                  PCK_NOMINA.GL_PVAC := Round((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12)) / 2, 0);
               ELSE
                  IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                     PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164);
                  ELSE
                     PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501));
                  END IF;
    
               END IF;
           ELSE
              PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
           END IF;  
          -- 'FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12)
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12, 0);
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;          
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  01,
                                                  PCK_NOMINA.GL_SANO,
                                                  I,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                                                  );              
          
                IF PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0 THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
           END LOOP;
           PCK_NOMINA.GL_DOCEAVAS := Round(PCK_NOMINA.GL_DOCEAVAS * 31 / 12, 0);
           IF PCK_NOMINA.FC_CN(158) = 0 Then
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 30 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF;
      ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') And PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
      --' Caso 3: Cuando Ingresa entre el 01/01 y el 30/06 y no se retira
           MI_FECHAFPN := TO_DATE('31/12/' ||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
 --'          acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO)
 --'          DNT = Cna(356) + Cna(357) + Cna(359) + cn(356) + cn(357) + cn(359)
 --'          acs = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn1), 2), IIf(Day(FechaIpn1) < 16, "01", "02"), CStr(s_Ano), "11", "99", personal!NUMERO_DCTO)
 --'          DNT1 = Cna(356) + Cna(357) + cn(356) + cn(357) + cn(359)
  --         ' Trae acumulados
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                  PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF ;
           END IF ;
          -- '07122018 AC = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO) '12092018MPV
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  );-- '12092018MPV
           --'Factores para el calculo de la prima de navidad
           IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- ' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0;-- 'Round((cn(1) + AUXT + AUXA + Cna(160) / 12) / 2, 0)
           ELSE
              PCK_NOMINA.GL_PVAC := 0 ;--' Cna(155)
           END IF;
          -- 'FactorPN = cn(1) + PROMEDIOEXTRAS + grpngv + AUXT + AUXA + BONESPMADOC + (Cna(160) / 12) + (PVAC / 12)      
           PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12, 0);
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT ;
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);     
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, 
                                                              PCK_NOMINA.GL_SANO,
                                                              I,
                                                              01,
                                                              PCK_NOMINA.GL_SANO,
                                                              I,
                                                              99,
                                                              PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                                                              );                                          
      
                IF PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0 THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
           END LOOP;
           PCK_NOMINA.GL_DOCEAVAS := Round(PCK_NOMINA.GL_DOCEAVAS * 31 / 12, 0);
           IF PCK_NOMINA.FC_CN(158) = 0 Then
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN  / 30 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF   ; 
      ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
       --' caso 4: Cuando ingresa despues del 30 de Junio
           MI_FECHAFPN := TO_DATE('31/12/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
           MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_DNT1;
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                  MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                  MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT1;
              END IF;
              IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') And PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/'||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                 MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
           --'07122018 AC = AcumCC(CStr(s_Ano), strzero(Month(FechaIpn), 2), IIf(Day(FechaIpn) < 16, "01", "02"), CStr(s_Ano), "12", "99", personal!NUMERO_DCTO) '12092018MPV
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                  PCK_NOMINA.GL_SANO,
                                                  EXTRACT(MONTH FROM PCK_NOMINA.GL_FECHAIPN),
                                                  CASE WHEN EXTRACT(DAY FROM PCK_NOMINA.GL_FECHAIPN) < 16 THEN 01 ELSE 02 END,
                                                  PCK_NOMINA.GL_SANO,
                                                  12,
                                                  99,
                                                  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                  );--           '12092018MPV
           --'Factores para el calculo de la prima de navidad
           IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- ' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0;-- 'Round((cn(1) + AUXT + AUXA + Cna(160) / 12) / 2, 0)
           ELSE
              PCK_NOMINA.GL_PVAC :=  0; --' Cna(155)
           END IF;
           PCK_NOMINA.GL_FACTORPN  := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(170) / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150) END) / 12);
           PCK_NOMINA.CN(931) := Round((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
           PCK_NOMINA.CN(932) := Round(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CNA(170) / 12, 0);
           PCK_NOMINA.CN(939) := Round(PCK_NOMINA.FC_CN(186), 0);
           PCK_NOMINA.CN(940) := Round((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 And PCK_NOMINA.FC_CN(150) > 1 And MI_VALORULTIMABASPPAGADA <> 0 THEN MI_VALORULTIMABASPPAGADA ELSE PCK_NOMINA.FC_CN(150)END) / 12, 0);
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);            
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                      PCK_NOMINA.GL_SANO,
                                                      I,
                                                      01,
                                                      PCK_NOMINA.GL_SANO,
                                                      I,
                                                      99,
                                                      PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado
                                                      );                
               
                IF PCK_NOMINA.CNA.EXISTS(359) AND PCK_NOMINA.CNA(359) > 0 THEN--(HU:7802779_CFBARRERA_SE EVALUAN SI EXISTEN O ESTAN INICIALIZADOS LOS CONCEPTOS ANTES DE INGRESAR AL IF)
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
           END LOOP;
           PCK_NOMINA.GL_DOCEAVAS := Round(PCK_NOMINA.GL_DOCEAVAS * 31 / 12, 0);
           IF PCK_NOMINA.FC_CN(158) = 0 THEN
              PCK_NOMINA.CN(158) := Round(PCK_NOMINA.GL_FACTORPN / 30 * PCK_NOMINA.GL_DOCEAVAS, 0);
           END IF;      
      
      END IF;      
      
    END IF;        
    
      PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
      --DiasPrimaDic := PCK_NOMINA.GL_DOCEAVAS; --'cn(67)
      --TRANSPORTELEGAL = cn(254)
      --Retefuente = cn(125)
      IF PCK_NOMINA.GL_SPER = 21 THEN
         FOR I IN 2 .. 699 LOOP
            --'If (i <> 125) And (i < 599) Or (i >= 600 And i <= 698) And (cn(i) > 0 And cn(i) < 1) Then
            IF (I <> 125) And (I < 599) Or (I >= 600 And I <= 698) Then
                IF (PCK_NOMINA.FC_CN(I) <> 0) And (I = PCK_NOMINA.FC_CN(470) Or I = PCK_NOMINA.FC_CN(471) Or I = PCK_NOMINA.FC_CN(472) Or I = PCK_NOMINA.FC_CN(473) Or I = PCK_NOMINA.FC_CN(474) Or I = PCK_NOMINA.FC_CN(475) Or I = PCK_NOMINA.FC_CN(476) Or I = PCK_NOMINA.FC_CN(477) Or I = PCK_NOMINA.FC_CN(478) Or I = PCK_NOMINA.FC_CN(479)) THEN --'***tener en cuenta algunos descuentos (se debe ingresar el descuento por novedad y ademas del 470-479)
                    PCK_NOMINA.PR_DUPLICARCUOTASDIFERIDAS(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES,PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Id_de_Empleado, I, 1);
                    PCK_NOMINA.CN(I) := PCK_NOMINA.FC_CN(I);
                ELSIF (I <> 470 And I <> 471 And I <> 472 And I <> 473 And I <> 474 And I <> 475 And I <> 476 And I <> 477 And I <> 478 And I <> 479) THEN
                    PCK_NOMINA.CN(I) := 0;
                END IF;
            END IF;
          END LOOP;    
          
          --'Mod Pedro Angarita 12/12/2006 verificar que se respete el concepto para los embargos cn(700 a 798)...
          FOR I IN 700 .. 798 LOOP
            --'If (i <> 125) And (i < 599) Or (i >= 600 And i <= 698) And (cn(i) > 0 And cn(i) < 1) Then
            IF (I <> 125) And (I < 599) Or (I >= 700 And I <= 798) THEN
                IF (PCK_NOMINA.FC_CN(I) <> 0) And (I = PCK_NOMINA.FC_CN(470) Or I = PCK_NOMINA.FC_CN(471) Or I = PCK_NOMINA.FC_CN(472) Or I = PCK_NOMINA.FC_CN(473) Or I = PCK_NOMINA.FC_CN(474) Or I = PCK_NOMINA.FC_CN(475) Or I = PCK_NOMINA.FC_CN(476) Or I = PCK_NOMINA.FC_CN(477) Or I = PCK_NOMINA.FC_CN(478) Or I = PCK_NOMINA.FC_CN(479)) THEN --'***tener en cuenta algunos descuentos (se debe ingresar el descuento por novedad y ademas del 470-479)
                   -- 'DuplicarCuotasDIFERIDAS s_Ano, s_mes, s_per, personal!Id_de_Empleado, CStr(i), 1
                    PCK_NOMINA.CN(I) := PCK_NOMINA.FC_CN(I);
                ELSIF (I <> 470 And I <> 471 And I <> 472 And I <> 473 And I <> 474 And I <> 475 And I <> 476 And I <> 477 And I <> 478 And I <> 479) THEN
                    PCK_NOMINA.CN(I) := 0;
                END IF;
            END IF;
          END LOOP;
      END IF;          
      PCK_NOMINA.CN(125) :=  PCK_NOMINA.FC_CN(125);  --Retefuente
      PCK_NOMINA.CN(254) :=  PCK_NOMINA.FC_CN(254); --TRANSPORTELEGAL
      PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
      PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;-- 'DIASPRIMADIC
     -- 'Guardando Factores
      PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);--   'Cna(2) + Cna(16) + Cna(28) + Cna(29) + Sumacona(42, 45) + Cna(174) + Cna(175) + Sumacona(370, 378) + Cna(507) + Cna(508) + Cna(511) + IIf(VAL(s_mes) = 12, 0, cn(2) + Sumacon(42, 45) + cn(174) + IIf(cn(404) <> 0, 0, cn(175)) + Sumacon(370, 378) + cn(507) + cn(508) + cn(511))     '+ cn(16)+ cn(28)+ cn(29)
    --'  cn(931) = 0 '  ' Recargos
    --'  cn(932) = PROMEDIOEXTRAS '(Sumacona(47, 60)) - (Cna(56)) + IIf(VAL(s_mes) = 12, 0, Sumacon(47, 60) - cn(56))               ' Horas Extras
      PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT; --'Cna(80) + Cna(95) + IIf(VAL(s_mes) = 12, 0, cn(80) + cn(95))                                 ' Transporte
      PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA; --'Cna(79) + IIf(VAL(s_mes) = 12, 0, cn(79))
      PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;                            --                                        ' Dias pactados prima
      PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC ;                               --                                        ' Dias calendario Comercial a 31 de Diciembre
      PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT ;                               --                                        ' Licencias
--'01022018 NIIF
PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN      ;


END PR_CALPRIMANAVIDADDUITAMA;

PROCEDURE PR_PRIMASEMESTRALDUITAMA
    /*
    NAME              : PR_PRIMASEMESTRALDUITAMA En access calcularprimasemestralDUITAMA
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 12/02/2021
    TIME              : 11:30 AM
    SOURCE MODULE     : NOMINAP2020.12.05 UNIFICADAS MPV 23122020 - 618 MALLAMAS 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  primaSemestralDuitama
    */
    (    
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    )
AS 
    MI_FECHA_ASIGBASICAPS   DATE;
    MI_ENCARGO_FECINICIO    DATE;
    MI_ENCARGO_FECFIN       DATE;
    MI_BASE                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORPRIMA           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO           NUMBER DEFAULT 0;
    MI_DIASPACTADOS         NUMBER DEFAULT 0;       
    MI_INDRETIR             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN   

      --'fechaIPS = CVDate(IIf(CVDate(FECHAFIN1) < CVDate("16/07/" & s_Ano), "01/06/" & CStr(VAL(s_Ano) - 1), "01/06/" & CStr(s_Ano)))
      --'se quito debido a que a todo tipo de empleado se le liquida semestralmente 26/11/2005
      --'18/06/2013 JP acumula lo del aÃ±o pero calcula seis meses
      --'If personal!ID_de_Tipo = "06" Then
      --'FechaIPS = CVDate(IIf(CVDate(FECHAFIN1) < CVDate("01/01/" & s_Ano), "01/07/" & CStr(VAL(s_Ano) - 1), "01/07/" & CStr(s_Ano) - 1))
      --'Else
    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/' ||PCK_NOMINA.GL_SANO) THEN TO_DATE('01/01/'||(PCK_NOMINA.GL_SANO - 1)) ELSE TO_DATE('01/07/' ||(PCK_NOMINA.GL_SANO - 1)) END;
      --'End If
      
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                          EXTRACT (YEAR FROM PCK_NOMINA.GL_FECHAIPS), 
                                          EXTRACT (MONTH FROM PCK_NOMINA.GL_FECHAIPS),
                                          01, 
                                          PCK_NOMINA.GL_SANO,
                                          06, 
                                          03, 
                                         PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      
      IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/'|| (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/'|| (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
      ELSE
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/07/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
      END IF;
      
      PCK_NOMINA.CN(946) := Round(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);  
      PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
      --'MfechaIps1 = CVDate(IIf(CVDate(FECHAFIN1) < CVDate("16/06/" & s_Ano), "01/01/" & CStr(s_Ano), "01/06/" & CStr(s_Ano)))
      --'MfechaIps1 = IIf(CVDate(FechaI) > CVDate(fechaIps1), FechaI, fechaIps1)
      IF PCK_NOMINA.GL_SMES = 06 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 OR PCK_NOMINA.GL_SPER = 04) THEN
      -- '  fechaIPS = CVDate("01/07/" & s_Ano - 1)
         PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
         PCK_NOMINA.GL_DNT :=PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
      ELSIF PCK_NOMINA.GL_SMES = 12 THEN --' solo acumula dode julio a diciembre
          PCK_NOMINA.GL_AC := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359', 
                                                                        EXTRACT (YEAR FROM PCK_NOMINA.GL_FECHAIPS), 
                                                                        EXTRACT (MONTH FROM PCK_NOMINA.GL_FECHAIPS), 
                                                                        1, 
                                                                        PCK_NOMINA.GL_SANO, 
                                                                        12, 
                                                                        3, 
                                                                        PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                                        0);                                                                 
                                                                        
                                                                                 
                                                                        
          PCK_NOMINA.GL_DNT:= PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                EXTRACT (YEAR FROM PCK_NOMINA.GL_FECHAIPS), 
                                                01, 
                                                01,
                                                PCK_NOMINA.GL_SANO, 
                                                06,
                                                03,
                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      END IF;
      
      PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE CASE WHEN PCK_NOMINA.GL_SMES = 06 THEN TO_DATE('30/06/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('31/12/'|| PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')END END;
      --'DNT = Cna(356) + Cna(357) + Cna(359) + cn(356) + cn(357) + cn(359) + Cna(339) + cn(339)
      --'04/06/2008 Se debe pagar el auxilio de transporte y alimentacion a las personas que devengen menos de dos salarios minimos.
       PCK_NOMINA.GL_AUXA := CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_NOMINA.FC_CN(89) THEN PCK_NOMINA.FC_CN(82) ELSE PCK_NOMINA.GL_AUXA END;
       PCK_NOMINA.GL_AUXT := CASE WHEN PCK_NOMINA.FC_CN(1) < 2 * PCK_NOMINA.FC_CN(201) THEN PCK_NOMINA.FC_CN(81) ELSE PCK_NOMINA.GL_AUXT END;
      -- '18/06/2013 JP personalizaciÃ³n propia de duitama pues los dias pactados son diferentes a lo que normalmente se maneja.
       IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' AND PCK_NOMINA.FC_CN(267) > 0 THEN
            IF PCK_NOMINA.FC_CN(946) > 0 And PCK_NOMINA.FC_CNA(150) = 0 THEN --'14122017'1707018 NIIF
                PCK_NOMINA.GL_FACTORPS := (PCK_NOMINA.FC_CN(1) / 30 * PCK_NOMINA.FC_CN(267)) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + ((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN 0 ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(946)) / 12);
            ELSE
                PCK_NOMINA.GL_FACTORPS := (PCK_NOMINA.FC_CN(1) / 30 * PCK_NOMINA.FC_CN(267)) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + ((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN 0 ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12);
            END IF;
       ELSE
            IF PCK_NOMINA.FC_CN(946) > 0 AND PCK_NOMINA.FC_CNA(150) = 0 THEN --'14122017'1707018 NIIF
                PCK_NOMINA.GL_FACTORPS := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + ((PCK_NOMINA.FC_CNA(150) + CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN 0 ELSE PCK_NOMINA.FC_CN(150) END + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(946))/ 12);
            ELSE
                PCK_NOMINA.GL_FACTORPS := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + ((PCK_NOMINA.FC_CNA(150) +  PCK_NOMINA.FC_CNA(514))/ 12);
            END IF;
       END IF;
      PCK_NOMINA.GL_FACTORPS := Round(PCK_NOMINA.GL_FACTORPS, 0);
      PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
      PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS)  ;     
      FOR I IN CASE WHEN PCK_NOMINA.GL_SMES = 12 THEN EXTRACT (MONTH FROM PCK_NOMINA.GL_FECHAIPS) ELSE 1 END .. PCK_NOMINA.GL_SMES  LOOP--' 14122017
          PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                PCK_NOMINA.GL_SANO, 
                                                I, 
                                                01,
                                                PCK_NOMINA.GL_SANO, 
                                                I,
                                                99,
                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          
          
          IF PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) > 0 Or PCK_NOMINA.FC_CNA(356) > 0 Or PCK_NOMINA.FC_CNA(357) > 0 THEN
             PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
          END IF;
      END LOOP;
    --'If DOCEAVAS < 6 Then DOCEAVAS = 0  
    IF PCK_NOMINA.GL_SPER = 4 OR PCK_NOMINA.FC_CN(404) <> 0 THEN
    IF PCK_NOMINA.GL_DOCEAVAS >= 3 THEN
      --'If CVDate(FechaIPS) >= CVDate("01/07/" & CStr(VAL(s_Ano) - 1)) And FechaIPS <= CVDate("01/01/" & s_Ano) Then
        --'If CVDate(FechaIPS) >= CVDate("01/07/" & CStr(VAL(s_Ano) - 1)) And FechaIPS <= CVDate("30/06/" & s_Ano) And cn(404) = 0 And personal!ID_de_Tipo = "06" Then
        IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/07/'|| (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY')  AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS < 12 THEN
                PCK_NOMINA.GL_DIASPROP := Round((PCK_NOMINA.GL_DOCEAVAS * PCK_NOMINA.FC_CN(67)) / 12, 0);
            ELSE
                PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.FC_CN(67);
            END IF;
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN Round((PCK_NOMINA.GL_FACTORPS * 180) / 360 , 0) ELSE PCK_NOMINA.FC_CN(160) END;
        ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/'|| PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') And PCK_NOMINA.FC_CN(404) = 0 THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN Round(PCK_NOMINA.GL_FACTORPS * (PCK_NOMINA.GL_DOCEAVAS / 12) * (PCK_NOMINA.FC_CN(67) / 30), 0) ELSE PCK_NOMINA.FC_CN(160) END;
        ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAR);
           FOR I IN 1 .. PCK_NOMINA.GL_SMES LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, 
                                                PCK_NOMINA.GL_SANO, 
                                                I, 
                                                01,
                                                PCK_NOMINA.GL_SANO, 
                                                I,
                                                99,
                                                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);               
      
               IF PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) > 0 Or PCK_NOMINA.FC_CNA(356) > 0 Or PCK_NOMINA.FC_CNA(357) > 0 THEN
                  PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
               END IF;
           END LOOP;
            IF PCK_NOMINA.GL_DOCEAVAS >= 3 THEN
                IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY') And PCK_NOMINA.GL_FECHAIPS <= TO_DATE('30/06/'|| PCK_NOMINA.GL_SANO,'DD/MM/YYYY') And PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                   IF PCK_NOMINA.GL_DOCEAVAS < 12 THEN
                       PCK_NOMINA.GL_DIASPROP := Round((PCK_NOMINA.GL_DOCEAVAS  * PCK_NOMINA.FC_CN(67)) / 12, 0);
                   ELSE
                       PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.FC_CN(67);
                   END IF;
                   PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN Round(PCK_NOMINA.GL_FACTORPS / 30 * PCK_NOMINA.GL_DIASPROP, 0) ELSE PCK_NOMINA.FC_CN(160)END ;
                 ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                   PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN Round(PCK_NOMINA.GL_FACTORPS * PCK_NOMINA.GL_DOCEAVAS / 12, 0) ELSE PCK_NOMINA.FC_CN(160) END;
                 END IF;
            END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES = 12 And PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
        PCK_NOMINA.CN(160) := 0;
        --'DOCEAVAS = 0
         PCK_NOMINA.CN(67) := 0;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_DIASPACTADOS := PCK_NOMINA.FC_CN(67);
    PCK_NOMINA.GL_FACTORPS1 := MI_PRIMAJUNIO - CASE WHEN PCK_NOMINA.GL_AUXT = 0 THEN 0 ELSE (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS) END ;
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    END IF;
    END IF    ;
    
    
    IF PCK_NOMINA.GL_SPER = 4 THEN
         FOR I IN  2 .. 699 LOOP
            --'If (i <> 125) And (i < 599) Or (i >= 600 And i <= 698) And (cn(i) > 0 And cn(i) < 1) Then
            IF (I <> 125) AND (I <> 201) And (I <> 4) And (I <> 401) And (I <> 404) And (I < 599) Or (I >= 600 And I <= 698) THEN
                IF (PCK_NOMINA.FC_CN(I) <> 0) And (I = PCK_NOMINA.FC_CN(470) Or I = PCK_NOMINA.FC_CN(471) Or I = PCK_NOMINA.FC_CN(472) Or I = PCK_NOMINA.FC_CN(473) Or I = PCK_NOMINA.FC_CN(474) Or I = PCK_NOMINA.FC_CN(475) Or I = PCK_NOMINA.FC_CN(476) Or I = PCK_NOMINA.FC_CN(477) Or I = PCK_NOMINA.FC_CN(478) Or I = PCK_NOMINA.FC_CN(479)) THEN --'***tener en cuenta algunos descuentos (se debe ingresar el descuento por novedad y ademas del 470-479)
                    PCK_NOMINA.PR_DUPLICARCUOTASDIFERIDAS(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, I, 1);
                    PCK_NOMINA.CN(I) := PCK_NOMINA.FC_CN(I);
                ELSIF (I <> 470 And I <> 471 And I <> 472 And I <> 473 And I <> 474 And I <> 475 And I <> 476 And I <> 477 And I <> 478 And I <> 479) THEN
                    PCK_NOMINA.CN(I) := 0;
                END IF;
            END IF;
          END LOOP;
          
          --'Mod Pedro Angarita 12/12/2006 verificar que se respete el concepto para los embargos cn(700 a 798)...
          FOR I IN 700 .. 798 LOOP
            --'If (i <> 125) And (i < 599) Or (i >= 600 And i <= 698) And (cn(i) > 0 And cn(i) < 1) Then
            IF (I <> 4) And (I <> 125) And (I < 599) Or (I >= 700 And I <= 798) THEN
                IF (PCK_NOMINA.FC_CN(I) <> 0) And (I = PCK_NOMINA.FC_CN(470) Or I = PCK_NOMINA.FC_CN(471) Or I = PCK_NOMINA.FC_CN(472) Or I = PCK_NOMINA.FC_CN(473) Or I = PCK_NOMINA.FC_CN(474) Or I = PCK_NOMINA.FC_CN(475) Or I = PCK_NOMINA.FC_CN(476) Or I = PCK_NOMINA.FC_CN(477) Or I = PCK_NOMINA.FC_CN(478) Or I = PCK_NOMINA.FC_CN(479)) THEN --'***tener en cuenta algunos descuentos (se debe ingresar el descuento por novedad y ademas del 470-479)
                --    'DuplicarCuotasDIFERIDAS s_Ano, s_mes, s_per, personal!Id_de_Empleado, CStr(i), 1
                    PCK_NOMINA.CN(I) := PCK_NOMINA.FC_CN(I);
                ELSIF (I <> 470 And I <> 471 And I <> 472 And I <> 473 And I <> 474 And I <> 475 And I <> 476 And I <> 477 And I <> 478 And I <> 479) THEN
                    PCK_NOMINA.CN(I) := 0;
                END IF;
            END IF;
          END LOOP;
    END IF;
    

      PCK_NOMINA.CN(404) := MI_INDRETIR;
      --'cn(160) = Round((PRIMAJUNIO / 30 * cn(67)) + (Cna(150) / 12), 0)
      --'cn(67) = doceavas
      --'Guardando Factores
      PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
      PCK_NOMINA.CN(83) := PCK_NOMINA.GL_AUXA; --'Base de Subsidio de alimentaciÃ³n para prima
      PCK_NOMINA.CN(85) := PCK_NOMINA.GL_AUXT; -- 'Base de Subsicio de Transporte para prima
      PCK_NOMINA.CN(945) := PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(16) + PCK_NOMINA.FC_CNA(28) + PCK_NOMINA.FC_CNA(29) + PCK_NOMINA.FC_SUMACONA(42, 45) + PCK_NOMINA.FC_CNA(174) + PCK_NOMINA.FC_CNA(175) 
                        + PCK_NOMINA.FC_SUMACONA(370, 378) + PCK_NOMINA.FC_CNA(507) + PCK_NOMINA.FC_CNA(508) + PCK_NOMINA.FC_CNA(511) 
                        + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/'||PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN 0 ELSE PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_SUMACONA(42, 45) + PCK_NOMINA.FC_CN(174) + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END + PCK_NOMINA.FC_SUMACON(370, 378) + PCK_NOMINA.FC_CN(507) + PCK_NOMINA.FC_CN(508) + PCK_NOMINA.FC_CN(511) END;   -- '+ cn(16) + cn(28)+ cn(29)
      PCK_NOMINA.CN(946) := PCK_NOMINA.FC_CNA(56) + PCK_NOMINA.FC_CNA(879) + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/'||PCK_NOMINA.GL_SANO,'DD/MM/YYYY')THEN 0 ELSE PCK_NOMINA.FC_CN(56)END       ;                                            -- ' Recargos
      PCK_NOMINA.CN(947) := (PCK_NOMINA.FC_SUMACONA(47, 60)) - (PCK_NOMINA.FC_CNA(56) + PCK_NOMINA.FC_CNA(879)) + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/'||PCK_NOMINA.GL_SANO,'DD/MM/YYYY')THEN 0 ELSE PCK_NOMINA.FC_SUMACON(47, 60) - PCK_NOMINA.FC_CN(56) END     ;       --   ' Horas Extras
      PCK_NOMINA.CN(948) := PCK_NOMINA.FC_CNA(80) + PCK_NOMINA.FC_CNA(95) + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/'||PCK_NOMINA.GL_SANO,'DD/MM/YYYY')THEN 0 ELSE PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(95) END                              ;   -- ' Transporte
      PCK_NOMINA.CN(949) := PCK_NOMINA.FC_CNA(79) + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/'||PCK_NOMINA.GL_SANO,'DD/MM/YYYY')THEN 0 ELSE PCK_NOMINA.FC_CN(79) END ;      -- ' Alimentacion
      PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);                                                                                   --    ' Dias pactados prima
      --'cn(951) = DiasPac
      PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;          --                                                                                ' Dias calendario Comercial a 30 de Junio
      PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;
      
      --' Licencias
--'re_error:
--'Resume Next
  --      '01022018
        PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
        PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_DCC    ;

END PR_PRIMASEMESTRALDUITAMA;
FUNCTION FC_SUMAFACTORES_CS
/*
  NAME               : SUMAFACTORES_CS
  AUTHOR MIGRACION   : CAMILO ANDRES PEREZ DUEÃ‘AS  
  DATE MIGRADOR      : 13/04/2021
  TIME               : 05:43 AM
  SOURCE MODULE      : NOMINAP2021.03.01_GNAR. En access SUMAFACTORES_CS
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : FACTORES PRIMA SEMESTRAL
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
  AS


  
  MI_RTA               PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_RS                SYS_REFCURSOR;
  MIRS_ID_DE_CONCEPTO  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MIRS_DOCCS           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;

BEGIN  

 OPEN MI_RS FOR   
                      SELECT CONCEPTOS.ID_DE_CONCEPTO, CONCEPTOS.DOCCS 
                      FROM CONCEPTOS 
                      WHERE CONCEPTOS.COMPANIA          =   UN_COMPANIA
                        AND CONCEPTOS.FACTOR_CESANTIAS NOT IN(0)  
                      ORDER BY CONCEPTOS.ID_DE_CONCEPTO;
                  
 LOOP
 FETCH MI_RS
 INTO  MIRS_ID_DE_CONCEPTO,
       MIRS_DOCCS;

       IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN
                 MI_RTA := 0;
                 EXIT WHEN MI_RS%NOTFOUND ;
       ELSIF MI_RS%NOTFOUND  THEN 
          EXIT WHEN MI_RS%NOTFOUND ;
       ELSE
            If MIRS_ID_DE_CONCEPTO = 1 THEN
                MI_RTA := MI_RTA + CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END  * 1 / MIRS_DOCCS;
             --ELSIf MIRS_ID_DE_CONCEPTO = 10 THEN
             ELSIf MIRS_ID_DE_CONCEPTO = 73 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_GASTOSREP * 1 / MIRS_DOCCS;        --' CONCEPTO 073 GASTOS DE REPRESENTACION
             ELSIf MIRS_ID_DE_CONCEPTO = 186 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_VPT * 1 / MIRS_DOCCS;              --' CONCEPTO 186 PRIMA TECNICA
             ELSIf MIRS_ID_DE_CONCEPTO = 150 THEN
                --'MI_RTA = MI_RTA + BAN * 1 / RSPS!DOCCS              ' CONCEPTO 150 BASP
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCCS;              --' CONCEPTO 150 BASP
             ELSIf MIRS_ID_DE_CONCEPTO = 80 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXT * 1 / MIRS_DOCCS;             --' CONCEPTO AUXT
             ELSIf MIRS_ID_DE_CONCEPTO = 79 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXA * 1 / MIRS_DOCCS;             --' CONCEPTO AUXA
             ELSE
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCCS;
             END IF;
       END  IF;
    END LOOP; 
    
    MI_RTA := PCK_SYSMAN_UTL.FC_ROUND(MI_RTA,0);
    CLOSE MI_RS; 
    RETURN MI_RTA;
 END FC_SUMAFACTORES_CS;

FUNCTION FC_SUMAFACTORES_PS 
/*
  NAME               : SUMAFACTORES_PS
  AUTHOR MIGRACION   : CAMILO ANDRES PEREZ DUEÃ‘AS  
  DATE MIGRADOR      : 13/04/2021
  TIME               : 05:43 AM
  SOURCE MODULE      : NOMINAP2021.03.01_GNAR. En access SUMAFACTORES_PS
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : FACTORES PRIMA SEMESTRAL
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
  AS


  
  MI_RTA               PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_RS                SYS_REFCURSOR;
  MIRS_ID_DE_CONCEPTO  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MIRS_DOCPS           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;

BEGIN  

 OPEN MI_RS FOR   
                      SELECT CONCEPTOS.ID_DE_CONCEPTO, CONCEPTOS.DOCPS 
                      FROM CONCEPTOS 
                      WHERE CONCEPTOS.COMPANIA          =   UN_COMPANIA
                        AND CONCEPTOS.FACTOR_PRIMAJUNIO NOT IN(0)  
                      ORDER BY CONCEPTOS.ID_DE_CONCEPTO;
                  
 LOOP
 FETCH MI_RS
 INTO  MIRS_ID_DE_CONCEPTO,
       MIRS_DOCPS;

       IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN
                 MI_RTA := 0;
                 EXIT WHEN MI_RS%NOTFOUND ;
       ELSIF MI_RS%NOTFOUND  THEN 
          EXIT WHEN MI_RS%NOTFOUND ;
       ELSE
             If MIRS_ID_DE_CONCEPTO = 1 THEN
                MI_RTA := MI_RTA + CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN  PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END  * 1 / MIRS_DOCPS  ;
            -- ELSIF MIRS_ID_DE_CONCEPTO = 10 THEN
             ELSIF MIRS_ID_DE_CONCEPTO = 73 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_GASTOSREP * 1 / MIRS_DOCPS;       -- CONCEPTO 073 GASTOS DE REPRESENTACION
             ELSIF MIRS_ID_DE_CONCEPTO = 186 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_VPT * 1 / MIRS_DOCPS ;             -- CONCEPTO 186 PRIMA TECNICA
             ELSIF MIRS_ID_DE_CONCEPTO = 150 THEN
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCPS;               -- CONCEPTO 150 BASP
             ELSIF MIRS_ID_DE_CONCEPTO = 80 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXT * 1 / MIRS_DOCPS;             -- CONCEPTO PCK_NOMINA.GL_AUXT
             ELSIF MIRS_ID_DE_CONCEPTO = 79 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXA * 1 / MIRS_DOCPS;            -- CONCEPTO PCK_NOMINA.GL_AUXA
             ELSE
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCPS;
             END IF;
       END IF;
    END LOOP; 
    
    MI_RTA := PCK_SYSMAN_UTL.FC_ROUND(MI_RTA,0);
    CLOSE MI_RS; 
    RETURN MI_RTA;
 END FC_SUMAFACTORES_PS;


FUNCTION FC_SUMAFACTORES_PV 
/*
  NAME               : SUMAFACTORES_PV
  AUTHOR MIGRACION   : CAMILO ANDRES PEREZ DUEÃ‘AS  
  DATE MIGRADOR      : 13/04/2021
  TIME               : 05:43 AM
  SOURCE MODULE      : NOMINAP2021.03.01_GNAR. En access SUMAFACTORES_PV
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : FACTORES PRIMA SEMESTRAL
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
  AS


  
  MI_RTA               PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_RS                SYS_REFCURSOR;
  MIRS_ID_DE_CONCEPTO  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MIRS_DOCPV           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;

BEGIN  

 OPEN MI_RS FOR   
                      SELECT CONCEPTOS.ID_DE_CONCEPTO, CONCEPTOS.DOCPV 
                      FROM CONCEPTOS 
                      WHERE CONCEPTOS.COMPANIA          =   UN_COMPANIA
                        AND CONCEPTOS.FACTOR_PRIMAVACACIONES NOT IN(0)  
                      ORDER BY CONCEPTOS.ID_DE_CONCEPTO;
                  
 LOOP
 FETCH MI_RS
 INTO  MIRS_ID_DE_CONCEPTO,
       MIRS_DOCPV;

       IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN
                 MI_RTA := 0;
                 EXIT WHEN MI_RS%NOTFOUND ;
       ELSIF MI_RS%NOTFOUND  THEN 
          EXIT WHEN MI_RS%NOTFOUND ;
       ELSE
             If MIRS_ID_DE_CONCEPTO = 1 THEN
                MI_RTA := MI_RTA + CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN  PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END  * 1 / MIRS_DOCPV;
             --ELSIF MIRS_ID_DE_CONCEPTO = 10 THEN
             ELSIF MIRS_ID_DE_CONCEPTO = 73 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_GASTOSREP * 1 / MIRS_DOCPV;        --' CONCEPTO 073 GASTOS DE REPRESENTACION
             ELSIF MIRS_ID_DE_CONCEPTO = 186 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_VPT * 1 / MIRS_DOCPV;   --           ' CONCEPTO 186 PRIMA TECNICA
             ELSIF MIRS_ID_DE_CONCEPTO = 150 THEN
                --'MI_RTA = MI_RTA + (CASE WHEN PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) > 0, PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO), PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO))) * 1 / MIRS_DOCPV
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCPV; -- CONCEPTO
             ELSIF MIRS_ID_DE_CONCEPTO = 80 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXT  * 1 / MIRS_DOCPV;            -- CONCEPTO AUXT
             ELSIF MIRS_ID_DE_CONCEPTO = 79 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXA * 1 / MIRS_DOCPV;             --' CONCEPTO AUXA
             ELSE
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCPV; --' CONCEPTO
             END IF;
       END IF;
    END LOOP; 
    
    MI_RTA := PCK_SYSMAN_UTL.FC_ROUND(MI_RTA,0);
    CLOSE MI_RS; 
    RETURN MI_RTA;
 END FC_SUMAFACTORES_PV;

FUNCTION FC_SUMAFACTORES_VC 
/*
  NAME               : SUMAFACTORES_VC
  AUTHOR MIGRACION   : CAMILO ANDRES PEREZ DUEÃ‘AS  
  DATE MIGRADOR      : 13/04/2021
  TIME               : 05:43 AM
  SOURCE MODULE      : NOMINAP2021.03.01_GNAR. En access SUMAFACTORES_VC
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : FACTORES PRIMA SEMESTRAL
  */
( 
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
  AS


  
  MI_RTA               PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
  MI_RS                SYS_REFCURSOR;
  MIRS_ID_DE_CONCEPTO  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MIRS_DOCVC           PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;

BEGIN  

 OPEN MI_RS FOR   
                      SELECT CONCEPTOS.ID_DE_CONCEPTO, CONCEPTOS.DOCVC 
                      FROM CONCEPTOS 
                      WHERE CONCEPTOS.COMPANIA          =   UN_COMPANIA
                        AND CONCEPTOS.FACTOR_VACACIONES NOT IN(0)  
                      ORDER BY CONCEPTOS.ID_DE_CONCEPTO;
                  
 LOOP
 FETCH MI_RS
 INTO  MIRS_ID_DE_CONCEPTO,
       MIRS_DOCVC;

       IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN
                 MI_RTA := 0;
                 EXIT WHEN MI_RS%NOTFOUND ;
       ELSIF MI_RS%NOTFOUND  THEN 
          EXIT WHEN MI_RS%NOTFOUND ;
       ELSE
            IF MIRS_ID_DE_CONCEPTO =  1 THEN
                MI_RTA := MI_RTA + CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN  PCK_NOMINA.FC_CNA(10) ELSE  PCK_NOMINA.FC_CN(1) END  * 1 / MIRS_DOCVC;
             --ELSIF  MIRS_ID_DE_CONCEPTO = 10 THEN
             ELSIF  MIRS_ID_DE_CONCEPTO = 73 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_GASTOSREP * 1 / MIRS_DOCVC;        -- CONCEPTO 073 GASTOS DE REPRESENTACION
             ELSIF  MIRS_ID_DE_CONCEPTO = 186 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_VPT * 1 / MIRS_DOCVC;              -- CONCEPTO 186 PRIMA TECNICA
             ELSIF  MIRS_ID_DE_CONCEPTO = 150 THEN
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCVC;              -- CONCEPTO 150 BASP
             ELSIF  MIRS_ID_DE_CONCEPTO = 80 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXT * 1 / MIRS_DOCVC;             -- CONCEPTO AUXT
             ELSIF  MIRS_ID_DE_CONCEPTO = 79 THEN
                MI_RTA := MI_RTA + PCK_NOMINA.GL_AUXA * 1 / MIRS_DOCVC;             -- CONCEPTO AUXA
             Else
                MI_RTA := MI_RTA + (PCK_NOMINA.FC_CN(MIRS_ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNA(MIRS_ID_DE_CONCEPTO)) * 1 / MIRS_DOCVC;
             END  IF;
             
       END  IF;
    END LOOP; 
    
    MI_RTA := PCK_SYSMAN_UTL.FC_ROUND(MI_RTA,0);
    CLOSE MI_RS; 
    RETURN MI_RTA;
 END FC_SUMAFACTORES_VC;


PROCEDURE PR_CALCULARPRIMADENAVIDADGNAR(
      /*
      NAME              : PR_calcularprImadeNAVIDADGNAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 14/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access CALCULARPRIMADENAVIDADGNAR
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCULARPRIMADENAVIDADGNAR
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
MI_N2                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_N1                 PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_FECHAFPN           DATE;
MI_ANIOS              PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_MESCOM             PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_DCC1               NUMBER := 0;
MI_PRIMADIC           NUMBER := 0;
MI_DIASPRIMADIC       NUMBER := 0;
MI_TRANSPORTELEGAL    NUMBER := 0;
MI_RETEFUENTE         NUMBER := 0;
MI_DATOS               NUMBER := 0;
BEGIN
  PCK_NOMINA.GL_PVAC := 0;
  PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA,PCK_NOMINA.GL_SANO, 11, 2, PCK_NOMINA.GL_SANO, 11, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(10) <> 0 AND PCK_NOMINA.FC_CNA(11) >= 15 THEN --verIFIca novedades de encargos en novIembre
      PCK_NOMINA.PR_INCLUIRNOVEDAD (PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 10, PCK_NOMINA.FC_CNA(10));
      PCK_NOMINA.CN(1) := PCK_NOMINA.FC_CNA(10);
      PCK_NOMINA.PR_INCLUIRNOVEDAD (PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 11, PCK_NOMINA.FC_CNA(11));
      PCK_NOMINA.CN(10) := PCK_NOMINA.FC_CNA(10);
      PCK_NOMINA.CN(11) := PCK_NOMINA.FC_CNA(11);
  END IF;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
     --ANOS = (Year(fecha2) - Year(fecha1))
     --MI_MESCOM = 12 * ANOS + Month(fecha2) - Month(fecha1) + 1
     --IF PCK_SYSMAN_UTL.FC_DIA(fecha1) > 1 THEN MI_MESCOM = MI_MESCOM - 1
     --IF (Month(fecha2) = 2 AND PCK_SYSMAN_UTL.FC_DIA(fecha2) < 28) Or (Month(fecha2) <> 2 AND PCK_SYSMAN_UTL.FC_DIA(fecha2) < 30) THEN MI_MESCOM = MI_MESCOM - 1
      PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
      PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI  > PCK_NOMINA.GL_FECHAIPN THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
      MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
      PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
      PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      IF PCK_NOMINA.FC_CN(155) = 0 THEN
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CNA(160) / 12) / 2, 0);
            ELSE
               IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                  PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155) / PCK_NOMINA.FC_CNA(164), 0);
               ELSE
                  PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
               END IF;
            END IF;
       ELSE
              PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
       END IF;
       --PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.FC_CN(1) + GRPGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(150) / 12
       --   PCK_NOMINA.FC_CN(905) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0)
       --         PCK_NOMINA.FC_CN(906) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0)
       --         PCK_NOMINA.FC_CN(907) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)  
       --        PCK_NOMINA.FC_CN(908) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)

       PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
       PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
       PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
       FOR I IN 7..12 LOOP
          PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), I, 1, (PCK_NOMINA.GL_SANO - 1), I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          IF PCK_NOMINA.FC_CNA(359) > 0 THEN
             PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
          END IF;
--          IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
--               PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12
--          Else
--             PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12
--          END IF
      END LOOP;      
      FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
          PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          IF PCK_NOMINA.FC_CNA(359) > 0 THEN
             PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
          END IF;
--          IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
--             PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12, 0)
--          Else
--             PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12, 0)
--          END IF
      END LOOP; 
--261104      PCK_NOMINA.GL_FACTORPN = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN, 0) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0)
      PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
              
      IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
              PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, (MI_FECHAFPN)) - PCK_NOMINA.GL_DNT;
              IF PCK_NOMINA.FC_CN(158) = 0 THEN
                           PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
              END IF;
              IF PCK_NOMINA.GL_SMES = '12' AND PCK_NOMINA.FC_CN(404) <> 0 THEN --23122011 MPV
                      PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                      PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
              END IF;
      ELSE
              IF PCK_NOMINA.FC_CN(158) = 0 THEN
                 PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
              END IF;
      END IF;
      --PCK_NOMINA.FC_CN(158) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CN(155) / 12), 0) * MI_MESCOM / 12
  ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '99' THEN
     PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(1);
     PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);
  ELSE
      PCK_NOMINA.GL_FACTORPN := 0;
      PCK_NOMINA.GL_DNT      := 0;
      PCK_NOMINA.GL_DNT1     := 0;
      PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
      PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI  > PCK_NOMINA.GL_FECHAIPN THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
      PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
      PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI  > PCK_NOMINA.GL_FECHAIPN1 THEN  PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPN1 END;
      IF PCK_NOMINA.GL_SMES = '12'AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
         PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
         PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
      END IF;
      --CASO 1: NORMAL
      IF PCK_NOMINA.FC_CN(404) = 0 AND  PCK_NOMINA.GL_FECHAI  <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
          MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN  1  ELSE 1 END , PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
           --Factores para el calculo de la prIma de navIdad
          PCK_NOMINA.GL_PVAC := 0;
          IF PCK_NOMINA.FC_CN(155) = 0 THEN
                IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                  --IF personalfechafInal
                  PCK_NOMINA.GL_PVAC := 0; --' sI no se le calculo no se ele tIene en cuenta  'PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(501)) / 12) / 2, 0)
                   
                    
                  MI_DATOS := 0;               
                  BEGIN
                    SELECT COUNT(0) DATOS
                    INTO MI_DATOS
                    FROM VACACIONES LEFT JOIN PERSONAL 
                        ON (VACACIONES.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO) 
                        AND (VACACIONES.COMPANIA = PERSONAL.COMPANIA) 
                    WHERE VACACIONES.ID_DE_EMPLEADO =  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                      AND VACACIONES.INICIO_DISFRUTE >=  TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                      AND VACACIONES.FECHAPAGO = TO_DATE('31/12/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY') 
                      AND VACACIONES.COMPANIA  =  UN_COMPANIA
                      AND ROWNUM <= 1;

                  EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_DATOS := 0;
                  END;
                  IF MI_DATOS > 0 THEN 
                        PCK_NOMINA.GL_AC   := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO - 1, 12, 1, (PCK_NOMINA.GL_SANO - 1), 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(505);
                  END IF;
                ELSE
                   IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                      PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(505)) / PCK_NOMINA.FC_CNA(164), 0);
                      --PERID = PCK_NOMINA.FC_CNA(164):
                      --PCK_NOMINA.GL_PVAC = CASE WHEN PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) > ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PERID), PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PERID)
                   ELSE
                      --28 / 11 / 2006; PCK_NOMINA.GL_PVAC = PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)
                      PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                   END IF;
                END IF;
           ELSE
              PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
           END IF;
           PCK_NOMINA.GL_AC    := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN  1 ELSE  2 END , PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
           PCK_NOMINA.CN(931)  := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
           PCK_NOMINA.CN(932)  := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939)  := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + CASE WHEN PCK_NOMINA.FC_CN(150) = 0 AND PCK_NOMINA.FC_CNA(150) <> 0 THEN  PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) ELSE  0 END  / 12), 0);
           PCK_NOMINA.GL_DCC      := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, (MI_FECHAFPN)); -- - PCK_NOMINA.GL_DNT
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           FOR I IN 1..12 LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
               IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) > 0 THEN
                  PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                  PCK_NOMINA.GL_DNT := PCK_NOMINA.GL_DNT + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357));
               END IF;
--               IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
--                 PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12
--               Else
--                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12
--               END IF
           END LOOP;
          --261104           PCK_NOMINA.GL_FACTORPN = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN, 0) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932)
            PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932);
            IF NOT PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD', ' ') = 'SI' THEN
                --SUMAFACTORES_PN
                --FACTORESPN = BASE_DOCPN
                --PCK_NOMINA.GL_FACTORPN = (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932)
            --ELSE
                PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932);
            END IF;
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                 PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
           MI_DCC1 := 0;
           IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_DNT := (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) + (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC , 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN --23122011 MPV
                    PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 12, 01, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
      ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
          --CASO 2: CUANDO SE ORDENAR LIQUIDACION
          MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
          -- Trae acumulados
          PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN  1 ELSE  2 END , PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          --Factores para el calculo de la prIma de navIdad
          PCK_NOMINA.GL_PVAC := 0;
          IF PCK_NOMINA.FC_CNA(155) <> 0 THEN
                IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                      PCK_NOMINA.GL_DCC := (TO_DATE(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL 
                                                 THEN  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO 
                                                 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                            END,'DD/MM/YYYY') - PCK_NOMINA.GL_FECHAFIN) + 1;
                  END IF;
                  IF PCK_NOMINA.GL_DCC >= 330 THEN
                      PCK_NOMINA.GL_PVAC := 0; --10102013 PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12) / 2, 0)
                  END IF;
                  IF PCK_NOMINA.GL_PVAC = 0 AND PCK_NOMINA.FC_CN(155) > 0 AND PCK_NOMINA.GL_SPER <> 7 THEN
                      PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
                  END IF;
                ELSE
                   IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                      PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164), 0);
                      --PERID = PCK_NOMINA.FC_CNA(164)
                      --        PCK_NOMINA.GL_PVAC = CASE WHEN PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) > ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PERID), PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PERID)
                   ELSE
                      PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
                   END IF;
                END IF;
            ELSE
              PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
              --28 / 11 / 2006; PCK_NOMINA.GL_PVAC = PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)
              PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
              IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PVAC = 0 THEN
                 --05042019
                 PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
              END IF;
           END IF;
           --PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12
           IF PCK_NOMINA.GL_SMES = 1 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
             PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END , PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
           Else
             PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16  THEN 01 ELSE 2 END , PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
           END IF;
           PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
           IF PCK_NOMINA.FC_CN(931) = 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.FC_CN(404) <> 0 THEN   -- 29022015 con lIquIdacIuones deIFnItIvas gobernador delgadpo raul, solo lo pagado
              --04042019
              PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
           END IF;
           PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
           --29092016 PCK_NOMINA.FC_CN(939) = PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(150) = 0 AND PCK_NOMINA.FC_CNA(150) <> 0, PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514), 0)) / 12, 0)  '05082015 PCK_NOMINA.FC_CN(150) + solo pagado
           PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) <> 0 THEN  PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) ELSE 0 END ) / 12, 0); --05082015 PCK_NOMINA.FC_CN(150) + solo pagado
           --           PCK_NOMINA.FC_CN(939) = PCK_SYSMAN_UTL.FC_ROUND(valorultImABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0) ' 04042019
           PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           --For I = 1 To PCK_NOMINA.GL_SMES
           --    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, "01", PCK_NOMINA.GL_SANO, I, "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO)
           --   IF PCK_NOMINA.FC_CNA(359) > 0 THEN
           --      PCK_NOMINA.GL_DOCEAVAS = PCK_NOMINA.GL_DOCEAVAS - 1
           --    END IF
           --Next I
           FOR I IN 1..12 LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
               IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) > 0 THEN
                  PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
               END IF;
--               IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
--                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12
--               Else
--                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12
--               END IF
           END LOOP;
--261104           PCK_NOMINA.GL_FACTORPN = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN, 0) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932)
           PCK_NOMINA.GL_FACTORPN := (CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932);
           IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_DNT := (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) + (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, (MI_FECHAFPN)) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 *  PCK_NOMINA.GL_DCC, 0);
                END IF;
                --IF PCK_NOMINA.GL_SMES = "12" AND PCK_NOMINA.FC_CN(404) <> 0 THEN '23122011 MPV
                --    PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, "12", "01", PCK_NOMINA.GL_SANO, "12", "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)
                --      PCK_NOMINA.FC_CN(158) = PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158)
                --END IF
                
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
           END IF;
           IF PCK_NOMINA.GL_SPER = 7 THEN
              PCK_NOMINA.GL_AC    := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 4 , PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
              PCK_NOMINA.CN(158)  := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
              PCK_NOMINA.CN(158)  := CASE WHEN PCK_NOMINA.FC_CN(158) < 0 AND PCK_NOMINA.FC_CN(404) <> 0 THEN  0 ELSE  PCK_NOMINA.FC_CN(158) END;
           END IF;
           --DIm DCCMES As Integer
           --DCCMES = PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CVDate(FECHAINI), CVDate(PCK_NOMINA.GL_FECHAFIN))
           --IF PCK_NOMINA.GL_SPER = "07" AND DCCMES > PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CVDate(FechaInI), CVDate(PCK_NOMINA.GL_FECHAFIN)) THEN
           --   PCK_NOMINA.FC_CN(158) = 0 ' PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158)
           --END IF
      ELSIF  PCK_NOMINA.GL_FECHAI  > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND  PCK_NOMINA.GL_FECHAI  <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
           -- Caso 3: CuANDo Ingresa entre el 01/01 y el 30/06 y no se retIra
           MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
           -- Trae acumulados
           IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                 PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
           PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN  1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO);
           --Factores para el calculo de la prIma de navIdad
           IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0;--PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12) / 2, 0)
              --'PCK_NOMINA.GL_PVAC = 0 --PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CNA(160) / 12) / 2, 0)
           ELSE
              PCK_NOMINA.GL_PVAC := 0; --' PCK_NOMINA.FC_CNA(155)
           END IF;
           --PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12
           PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
           PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + CASE WHEN PCK_NOMINA.FC_CN(150) = 0 AND PCK_NOMINA.FC_CNA(150) <> 0 THEN  PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) ELSE 0 END ) / 12, 0);
           PCK_NOMINA.GL_DCC  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_DCC1 := 0;
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           --For I = 1 To PCK_NOMINA.GL_SMES
           --    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, "01", PCK_NOMINA.GL_SANO, I, "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO)
           --    IF PCK_NOMINA.FC_CNA(359) > 0 THEN
           --       PCK_NOMINA.GL_DOCEAVAS = PCK_NOMINA.GL_DOCEAVAS - 1
           --    END IF
           --Next I
           FOR I IN 1..12 LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
               IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) > 0 THEN
                  PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
               END IF;
--               IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
--                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12
--               Else
--                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12
--               END IF
           END LOOP;
--'261104           PCK_NOMINA.GL_FACTORPN = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN, 0) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932)
           PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932);
           IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_DNT := (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) + (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN --23122011 MPV
                    PCK_NOMINA.GL_AC   := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
           END IF;
      ELSIF  PCK_NOMINA.GL_FECHAI  > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
           -- caso 4: CuANDo Ingresa despues del 30 de JunIo
           MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
           --Trae acumulados
           MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
           MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')) - PCK_NOMINA.GL_DNT1;
           IF PCK_NOMINA.GL_FECHAR IS NULL THEN
              IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                 MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                 MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT1;
              END IF;
              IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                 MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
              END IF;
           END IF;
          PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN  1 ELSE  2 END , PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO);
          --Factores para el calculo de la prIma de navIdad
           IF PCK_NOMINA.FC_CNA(155) = 0 THEN -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
              PCK_NOMINA.GL_PVAC := 0; --PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CNA(160) / 12) / 2, 0)
           ELSE
              PCK_NOMINA.GL_PVAC := 0; --' PCK_NOMINA.FC_CNA(155)
           END IF;
           --PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 + (PCK_NOMINA.GL_PVAC / 12) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12
           PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
           PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
           PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + CASE WHEN PCK_NOMINA.FC_CN(150) = 0 AND PCK_NOMINA.FC_CNA(150) <> 0 THEN  PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) ELSE 0 END) / 12, 0);
           PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
           --For I = 1 To PCK_NOMINA.GL_SMES
           --    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, "01", PCK_NOMINA.GL_SANO, I, "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)
           --    IF PCK_NOMINA.FC_CNA(359) > 0 THEN
           --       PCK_NOMINA.GL_DOCEAVAS = PCK_NOMINA.GL_DOCEAVAS - 1
           --    END IF
           --Next I
           FOR I IN 1..12 LOOP
               PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
               IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) > 0 THEN
                  PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
               END IF;
               --               IF PCK_NOMINA.FC_CNA(11) <> 0 THEN
               --                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(188) + PCK_NOMINA.FC_CNA(73)) / 12
               --               Else
               --                  PCK_NOMINA.GL_FACTORPN = PCK_NOMINA.GL_FACTORPN + (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) / 12
               --               END IF
           END LOOP;
--'261104           PCK_NOMINA.GL_FACTORPN = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN, 0) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932)
           PCK_NOMINA.GL_FACTORPN := (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(931) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.FC_CN(932);
           IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIA', ' ') = 'SI' THEN
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_DNT := (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)) + (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
                
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, (MI_FECHAFPN)) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 *  PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN --23122011 MPV
                    PCK_NOMINA.GL_AC   := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                   PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
           
            END IF;
      END IF;
  END IF;
      MI_PRIMADIC        := PCK_NOMINA.FC_CN(158);
      MI_DIASPRIMADIC    := PCK_NOMINA.GL_DOCEAVAS; --PCK_NOMINA.FC_CN(67)
      MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
      MI_RETEFUENTE         := PCK_NOMINA.FC_CN(125);
      IF PCK_NOMINA.GL_SPER = 4 OR PCK_NOMINA.GL_SPER = 14 THEN
         FOR I IN 2..699 LOOP
             IF (I <> 125) AND I <> 172 AND I <> 109 AND I <> 309 AND I <> 303 AND I <> 403 AND I <> 404 AND (I < 499) Or (I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                PCK_NOMINA.CN(I) := 0;
             END IF;
         END LOOP;
      END IF;
      PCK_NOMINA.CN(125) := MI_RETEFUENTE;
      PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
      PCK_NOMINA.CN(158) := MI_PRIMADIC;
      PCK_NOMINA.CN(67)  := PCK_NOMINA.GL_DOCEAVAS; --MI_DIASPRIMADIC
      --GuardANDo Factores
      PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);   --PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(16) + PCK_NOMINA.FC_CNA(28) + PCK_NOMINA.FC_CNA(29) + Sumacona(42, 45) + PCK_NOMINA.FC_CNA(174) + PCK_NOMINA.FC_CNA(175) + Sumacona(370, 378) + PCK_NOMINA.FC_CNA(507) + PCK_NOMINA.FC_CNA(508) + PCK_NOMINA.FC_CNA(511) + CASE WHEN VAL(PCK_NOMINA.GL_SMES) = 12, 0, PCK_NOMINA.FC_CN(2) + Sumacon(42, 45) + PCK_NOMINA.FC_CN(174) + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0, 0, PCK_NOMINA.FC_CN(175)) + Sumacon(370, 378) + PCK_NOMINA.FC_CN(507) + PCK_NOMINA.FC_CN(508) + PCK_NOMINA.FC_CN(511))     '+ PCK_NOMINA.FC_CN(16)+ PCK_NOMINA.FC_CN(28)+ PCK_NOMINA.FC_CN(29)
    --'  PCK_NOMINA.FC_CN(931) = 0; -- Recargos
    --'  PCK_NOMINA.FC_CN(932) = PROMEDIOEXTRAS '(Sumacona(47, 60)) - (PCK_NOMINA.FC_CNA(56)) + CASE WHEN VAL(PCK_NOMINA.GL_SMES) = 12, 0, Sumacon(47, 60) - PCK_NOMINA.FC_CN(56))               ' Horas Extras
      PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT; --PCK_NOMINA.FC_CNA(80) + PCK_NOMINA.FC_CNA(95) + CASE WHEN VAL(PCK_NOMINA.GL_SMES) = 12, 0, PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(95))                                 ' Transporte
      PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA; --PCK_NOMINA.FC_CNA(79) + CASE WHEN VAL(PCK_NOMINA.GL_SMES) = 12, 0, PCK_NOMINA.FC_CN(79))
      PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67);   -- DIas pactados prIma
      PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;      -- DIas calendarIo ComercIal a 31 de DIcIembre
      PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT;      -- LIcencIas
      PCK_NOMINA.CN(940) := PCK_NOMINA.GL_GASTOSREP;
      PCK_NOMINA.CN(929) := PCK_NOMINA.GL_VPT;
END PR_CALCULARPRIMADENAVIDADGNAR; 
PROCEDURE PR_CALPRIMADEVACACIONESGNAR(
      /*
      NAME              : PR_CALPRIMADEVACACIONESGNAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 14/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access CALCULARPRIMADEVACACIONESGNAR
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCULARPRIMADEVACACIONESGNAR
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
   MI_RTA          PCK_SUBTIPOS.TI_LOGICO := 0;
   MI_PSPV         NUMBER:= 0;
   MI_MSG          PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;

BEGIN
  MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;
   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
      PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CN(170)), 0) / 2;
   ELSE
      --adicionarle la condicion de no dejar liquidar mÃ¡s de un periodo de vacaciones a la vez
      PCK_NOMINA.GL_DCC := 0;
      PCK_NOMINA.GL_DIASVAC := 0;
      PCK_NOMINA.GL_DIASPENDIENTES := 0;
      PCK_NOMINA.GL_PENDIENTES := 0;
      PCK_NOMINA.GL_LICENCIAS := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
      -- Para personal que se retira
      IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                                  --PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,EXTRACT (YEAR FROM PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         --REVISAR SI DIASINTERRUPCON YA SE DESCONTO DENTRO DEL ULTIMO PERIODO DE LAS VACACIONES, O SE TIENE QUE DESCONTAR EN PCK_NOMINA.GL_FECHAI ANTES DE CALCULAR PCK_NOMINA.GL_FECHAUV PARA NIEVOS
         PCK_NOMINA.GL_LICENCIAS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359)) + CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN  PCK_NOMINA.FC_CESANTIA(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L') ELSE 0 END + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
         PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
         PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI  THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1);-- + 1   '19062015 '- 1
         PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI  THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END , PCK_NOMINA.GL_FECHAFIN1);-- + 1  '19062015'- 1
         PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_DTV / 360;
         PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
         PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
         --adicionarle la condicion de no dejar liquidar mÃ¡s de un periodo de vacaicones a la vez
         --1712 PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,CStr(Anoa), strzero(mesa + 1, 2), strzero(pera, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ' Acumulado del ultimo aÃ±o
         PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--  ' Acumulado del ultimo aÃ±o
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --'EMPLEADOS OFICIALES
            PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.FC_CN(1); --' factores prima de vacaciones
         ELSE
            -- PARA EL CASO DE SEPTIEMBRE QUE ES CUANDO MAS TIENEN BONIFICACION ANUAL, NO DEBE TOMAR EL ACUMULADO DE SEPTIEMBRE AÃ‘O ANTERIOR, SOLAMENTE LA QUE SE VA A PAGAR EN EL MES, 25/01/2006 PASTO MPV
            --''08022012 PCK_NOMINA.GL_FACTORESPV = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + CASE WHEN PCK_NOMINA.FC_CNA(160) = 0, PCK_NOMINA.FC_CN(160), PCK_NOMINA.FC_CNA(160)) / 12 + CASE WHEN PCK_NOMINA.GL_SMES <> "09", CASE WHEN PCK_NOMINA.FC_CN(150) <> 0, PCK_NOMINA.FC_CN(150) / 12, (PCK_NOMINA.FC_CNA(150) / 12) + (PCK_NOMINA.FC_CN(150) / 12)), CASE WHEN PCK_NOMINA.FC_CNA(150) = 0, PCK_NOMINA.FC_CN(150), PCK_NOMINA.FC_CNA(150)) / 12), 0) ' factores prima de vacaciones 'se quito enero 156 de 2008 requerimietno enero 11 2008  + (PCK_NOMINA.FC_CN(160) / 12)
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + CASE WHEN PCK_NOMINA.FC_CNA(160) = 0 THEN  PCK_NOMINA.FC_CN(160) ELSE PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) END / 12, 0); -- + PCK_NOMINA.FC_CN(160)
            --IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA VACACIONES") = 'SI' THEN
            --   SUMAFACTORES_PV
            --   PCK_NOMINA.GL_FACTORESPV = BASE_DOCPV
            --END IF;
            IF PCK_NOMINA.GL_SMES <> 9 THEN
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.GL_FACTORESPV + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(150) <> 0 THEN (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12 ELSE (PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) / 12) END, 0); --'04042019
            ELSE
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.GL_FACTORESPV + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN  (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12 ELSE ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12) END , 0);-- '04042019
            END IF;
         END IF;
             PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(((CASE WHEN PCK_NOMINA.FC_CNA(160) = 0 THEN  PCK_NOMINA.FC_CN(160) ELSE PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) END / 12)), 0);--PRIMA DE SERVICIOS
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --''EMPLEADOS OFICIALES
            IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA VACACIONES',' ') = 'SI' THEN
               --SUMAFACTORES_PV BASE_DOCPV
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA_COM9.FC_SUMAFACTORES_PV(UN_COMPANIA);
               PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(93) / 30, 0) ELSE PCK_NOMINA.FC_CN(155) END ;  -- ' Prima de vacaciones
            ELSE
               PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(150) / 12) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.FC_CN(170)), 0) * PCK_NOMINA.FC_CN(93) / 30, 0) ELSE PCK_NOMINA.FC_CN(155) END ;   --' Prima de vacaciones
            END IF;
         ELSE
            IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA VACACIONES',' ') = 'SI' THEN
               --SUMAFACTORES_PV
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA_COM9.FC_SUMAFACTORES_PV(UN_COMPANIA);
               PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;                 --' Vacaciones en Tiempo
            ELSE
               PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;                 --' Vacaciones en Tiempo
            END IF;
            
            --'PCK_NOMINA.GL_DTV = PCK_NOMINA.GL_DTV / 30 * 15 / 360
            --'PCK_NOMINA.FC_CN(175) = PCK_NOMINA.FC_CN(155)
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_ROUND((15 / 360 * PCK_NOMINA.GL_DTV), 0);
            --'PCK_NOMINA.GL_DTVV = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,personal![FECHA_DE_RETIRO], PCK_NOMINA.GL_DIASVAC)
            MI_RTA   := 7;--' msgbox("se estÃ¡ calculando vacaciones a un retirado, desea contarle los SÃ¡bados como dÃ­a hÃ¡bil para Vacaciones al empleado " and personal!APELLIDO1 and " " and personal!APELLIDO2 and " " and personal!NOMBRES, vbYesNo, "Sysman Software")
            PCK_NOMINA.GL_FECHAFF := NULL;
            PCK_NOMINA.GL_DTV     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI  THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1); --'+ 1 '- 1
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
            PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC);
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
                  IF MI_RTA = 6 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC);
                    --'fechaff = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,personal![FECHA_DE_RETIRO], PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,personal![FECHA_DE_RETIRO], PCK_NOMINA.GL_DIASVAC), 1)
                    PCK_NOMINA.CN(96) := ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1) - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC) ) + 1;
                  ELSE
                    PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC);
                    --PCK_NOMINA.CN(96)      := ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1) - PCK_NOMINA.GL_FECHAFF1) + 1; --mod JM CC 3205
                   PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)); -- JM CC 3205 
                  END IF;
            END IF;
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, PCK_NOMINA.GL_DIASVAC);
               PCK_NOMINA.CN(96) := ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1) - PCK_NOMINA.GL_FECHAFF) + 1;
            END IF;
            IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES VACACIONES',' ') = 'SI' THEN
               --SUMAFACTORES_VC
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA_COM9.FC_SUMAFACTORES_VC(UN_COMPANIA);
               PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0); -- vacaciones se pagan en dinero
            ELSE
               PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0); --vacaciones se pagan en dinero
            END IF;
            --19032008 IF PCK_NOMINA.GL_DTV < 720 And PCK_NOMINA.GL_DTV > 360 THEN
            --   PCK_NOMINA.FC_CN(175) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(175) / 720 * PCK_NOMINA.GL_DTV, 0)
            --ELSIF PCK_NOMINA.GL_DTV < 360 THEN
            --   PCK_NOMINA.FC_CN(175) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(175) / 360 * PCK_NOMINA.GL_DTV, 0)
            --END IF;
         END IF;
         
         -- 00/01/2008 liquidaciones definitivas
         IF PCK_NOMINA.FC_CN(151) = 0 THEN
            IF PCK_NOMINA.FC_CN(404) = 1 THEN
              IF PCK_NOMINA.GL_DTV >= 720 THEN
                 PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV - 360;
                 IF PCK_NOMINA.GL_DTV > 720 THEN
                    PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV - 360;
                 END IF;
              ELSIF PCK_NOMINA.GL_DTV > 360 THEN
                 PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI  THEN  PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END , PCK_NOMINA.GL_FECHAFIN1); --' + 1 '- 1
              END IF;
              PCK_NOMINA.CN(151) := CASE WHEN PCK_NOMINA.FC_CN(151) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV) / 30 * PCK_PARST.FC_PAR('NUMERO DE DIAS PARA BONIFICACION DE RECREACION',' ') / 360 * PCK_NOMINA.GL_DTV, 0) ELSE  PCK_NOMINA.FC_CN(151) END;
            END IF;
            PCK_NOMINA.CN(151) := CASE WHEN PCK_NOMINA.FC_CN(151) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV) / 30 * PCK_PARST.FC_PAR('NUMERO DE DIAS PARA BONIFICACION DE RECREACIO',' ') / 360 * PCK_NOMINA.GL_DTV, 0) ELSE  PCK_NOMINA.FC_CN(151) END;
         END IF;
         --PCK_NOMINA.FC_CN(155) = CASE WHEN PCK_NOMINA.FC_CN(155) = 0, PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CNA(170) / 12 + PCK_NOMINA.FC_CNA(160) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30, 0), PCK_NOMINA.FC_CN(155))   ' Prima de vacaciones
         IF PCK_NOMINA.FC_CN(175) = 0 THEN
            PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.GL_DIASVAC / 30, 0);                                                 -- vacaciones se pagan en dinero
            PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV - (PCK_NOMINA.GL_PERIODOS * 360);
            PCK_NOMINA.GL_DIASPROPORCIONAL := CASE WHEN PCK_NOMINA.GL_DIASPROPORCIONAL < 0 THEN  0 ELSE  PCK_NOMINA.GL_DIASPROPORCIONAL END ;
            IF PCK_NOMINA.GL_DIASPROPORCIONAL > 180 THEN
               PCK_NOMINA.GL_DINEROPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * 15 / 30 * PCK_NOMINA.GL_DIASPROPORCIONAL / 360, 0);
               PCK_NOMINA.GL_PRIMAPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(68) / 30 * PCK_NOMINA.GL_DIASPROPORCIONAL / 360, 0);
            END IF;
            IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
               PCK_NOMINA.GL_PENDIENTES := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.GL_DIASPENDIENTES / 30, 0);
            END IF;
            IF PCK_NOMINA.GL_SPRC = '99' THEN -- en proyecciones van PCK_NOMINA.GL_DINEROPROPORCIONAL y PCK_NOMINA.GL_PENDIENTES por aparte
               PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175); -- + PCK_NOMINA.GL_DINEROPROPORCIONAL + PCK_NOMINA.GL_PENDIENTES
            ELSE
               PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_DINEROPROPORCIONAL + PCK_NOMINA.GL_PENDIENTES;
            END IF;
         END IF;
         --actualizarnovedadesvac
                             --PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA,PCK_NOMINA.GL_SANO, 11, 2, PCK_NOMINA.GL_SANO, 11, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         IF PCK_NOMINA.FC_CNA(174) <> 0 THEN 
            PCK_NOMINA.CN(174) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(174), 0);
         END IF ;
         IF PCK_NOMINA.FC_CNA(175) <> 0 THEN 
            PCK_NOMINA.CN(175) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(175), 0); 
         END IF ;
         IF PCK_NOMINA.FC_CNA(155) <> 0 THEN
            PCK_NOMINA.CN(155) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155), 0);
         END IF ;
         IF PCK_NOMINA.FC_CNA(151) <> 0 THEN 
            PCK_NOMINA.CN(151) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(151), 0); 
         END IF  ;
      ELSE
         --Vacaciones Normales
         --acumulado para dias pensientes de vacaicones
         PCK_NOMINA.GL_AC             := PCK_NOMINA.FC_ACUM(UN_COMPANIA,EXTRACT (YEAR FROM PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
         IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN 
             --ALER_TIENE180DIASLABORADOS       CONSTANT PLS_INTEGER := 61000320;
               --"El empleado --EMPLEADO--, Tiene --DIASPENDIENTES-- dias pendientes, CÃ©dula No. --CEDULA--
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'DIASPENDIENTES';
               MI_MSG(2).VALOR := PCK_NOMINA.GL_DIASPENDIENTES;
               MI_MSG(3).CLAVE := 'CEDULA';
               MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_TIENE180DIASLABORADOS
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );
            
         END IF;
         PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);    -- dias de prima pactados para Prima de Vacaciones
         
         MI_PSPV := 0;
         IF PCK_NOMINA.GL_SMES > 7 THEN
            PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--   ' Acumulado del ultimo aÃ±o
            MI_PSPV := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.FC_CNA(503) / 12), 0);
            IF NOT (PCK_NOMINA.GL_SMES >= 9 And PCK_NOMINA.FC_CNA(150) > 0 ) THEN
               PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --' Acumulado del ultimo aÃ±o
            END IF;
         ELSE
            PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulado del ultimo aÃ±o
            MI_PSPV := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.FC_CNA(503) / 12), 0);
         END IF;
             PCK_NOMINA.CN(981) := MI_PSPV;
         --'anterior ac = AcumCC(CStr(Anoa), strzero(mesa, 2), strzero(pera, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, "99", personal!NUMERO_DCTO)
         --''PCK_NOMINA.GL_FACTORESPV = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(61) + PROMEDIOEXTRAS + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(197) + PCK_NOMINA.FC_CN(198) + PCK_NOMINA.FC_CN(199) + PCK_NOMINA.FC_CN(527) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.FC_CNA(150) / 12), 0) ' factores prima de vacaciones         'PCK_NOMINA.GL_FACTORESPV = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + ((PCK_NOMINA.FC_CNA(56)) / 12), 0)      ' factores prima de vacaciones
         --'PCK_NOMINA.GL_FACTORESPV = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + ((PCK_NOMINA.FC_CNA(56)) / 12), 0)      ' factores prima de vacaciones
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --'EMPLEADOS OFICIALES
            IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA VACACIONES',' ') = 'SI' THEN
               --SUMAFACTORES_PV 
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA_COM9.FC_SUMAFACTORES_PV(UN_COMPANIA); 
               PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(93) / 30, 0) ELSE PCK_NOMINA.FC_CN(155) END;    --' Prima de vacaciones
            ELSE
               PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.FC_CN(1); --' factores prima de vacaciones
            END IF;
         ELSE
            --' PARA EL CASO DE SEPTIEMBRE QUE ES CUANDO MAS TIENEN BONIFICACION ANUAL, NO DEBE TOMAR EL ACUMULADO DE SEPTIEMBRE AÃ‘O ANTERIOR, SOLAMENTE LA QUE SE VA A PAGAR EN EL MES, 25/01/2006 PASTO MPV
            IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA VACACIONES','') = 'SI' THEN
               --SUMAFACTORES_PV;
               PCK_NOMINA.GL_FACTORESPV  := PCK_NOMINA_COM9.FC_SUMAFACTORES_PV(UN_COMPANIA);
               --PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.GL_FACTORESPV;
               --'PCK_NOMINA.GL_FACTORESPV = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CNA(160) / 12) + CASE WHEN PCK_NOMINA.GL_SMES <> "09", CASE WHEN PCK_NOMINA.FC_CN(150) <> 0, PCK_NOMINA.FC_CN(150) / 12, (PCK_NOMINA.FC_CNA(150) / 12) + (PCK_NOMINA.FC_CN(150) / 12)), CASE WHEN PCK_NOMINA.FC_CNA(150) = 0, PCK_NOMINA.FC_CN(150), PCK_NOMINA.FC_CNA(150)) / 12), 0) ' factores prima de vacaciones
               --'PCK_NOMINA.GL_FACTORESPV1 = PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA, 0), 0) ' factores prima de vacaciones
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + MI_PSPV + 
                							CASE WHEN PCK_NOMINA.GL_SMES >= 9 THEN  CASE WHEN PCK_NOMINA.FC_CN(150) > 1 THEN  PCK_NOMINA.FC_CN(150) / 12 ELSE (((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12) + (PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514))/12) END ELSE CASE WHEN PCK_NOMINA.FC_CNA(150) = 0 THEN  PCK_NOMINA.FC_CN(150) ELSE  PCK_NOMINA.FC_CNA(150) END /12 END, 0); --' factores prima de vacaciones
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA, 0), 0); --' factores prima de vacaciones
            END IF;
         END IF;
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN --'dIFerente de salario integral
            IF PCK_NOMINA.FC_CN(403) <> 0 THEN      --' Salario de Vacaciones y prima de vaciones
               --181020133 PCK_NOMINA.FC_CN(174) = CASE WHEN PCK_NOMINA.FC_CN(174) = 0, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0), PCK_NOMINA.FC_CN(174))                         ' Vacaciones en Tiempo
               PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0);                         --' Vacaciones en Tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN      --' Vacaciones en dinero
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE  PCK_NOMINA.FC_CN(175) END ;                        -- ' Vacaciones en dinero
            END IF;
        --'    IF personal!Sindicato = True THEN 'para los sindicalizados
            --'   PCK_NOMINA.FC_CN(155) = CASE WHEN PCK_NOMINA.FC_CN(155) = 0, PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0, PCK_NOMINA.FC_CN(10), (PCK_NOMINA.FC_CN(1) / 3 * 2) + PROMEDIOEXTRAS + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(197) + PCK_NOMINA.FC_CN(198) + PCK_NOMINA.FC_CN(199) + PCK_NOMINA.FC_CN(527) + (PCK_NOMINA.FC_CNA(160) / 12) + (PCK_NOMINA.FC_CNA(150) / 12) + (PCK_NOMINA.FC_CN(173) / 12)), 0), PCK_NOMINA.FC_CN(155))       ' Prima de vacaciones
           --' ELSE 'para los no sindicalizados
            PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE  PCK_NOMINA.FC_CN(155) END;                     --' Vacaciones en Tiempo
          --'  END IF;
         ELSE --' salario Integral no tiene prima de vacaciones
            IF PCK_NOMINA.FC_CN(403) <> 0 THEN
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;                        --' Vacaciones en tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;                        --' Vacaciones en dinero
            END IF;
         END IF;
      END IF;
       --actualizarnovedadesvac
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         IF PCK_NOMINA.FC_CNA(174) <> 0 THEN 
            PCK_NOMINA.CN(174) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(174), 0);
         END IF;
         IF PCK_NOMINA.FC_CNA(175) <> 0 THEN 
            PCK_NOMINA.CN(175) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(175), 0); 
         END IF;
         IF PCK_NOMINA.FC_CNA(155) <> 0 THEN 
            PCK_NOMINA.CN(155) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155), 0);
         END IF ;
         IF PCK_NOMINA.FC_CNA(151) <> 0 THEN 
            PCK_NOMINA.CN(151) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(151), 0); 
         END IF;
   END IF;
      --'bonIFicacion especial de recreaciÃ³n
      --'IF PCK_NOMINA.FC_CN(156) = 0 And PCK_NOMINA.FC_CN(174) > 0 THEN
      --'   PCK_NOMINA.FC_CN(156) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) / 30 * 2), 0)
      --'END IF;
      --'Guardando Factores
      PCK_NOMINA.CN(960) := CASE WHEN PCK_NOMINA.FC_CN(960) = 0 THEN  PCK_NOMINA.GL_FACTORESPV ELSE  PCK_NOMINA.FC_CN(960) END;                            --' Sueldo
      IF PCK_NOMINA.GL_SPRC = '99' THEN
         PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93)  ;                                  --' Dias de Vacaciones
         PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS;                                   --' PCK_NOMINA.GL_PERIODOS de Vacaciones
      ELSE
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94);                                    --' Dias de Vacaciones
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164);                                   --' PCK_NOMINA.GL_PERIODOS de Vacaciones
      END IF;
      PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS ;                              --  ' PCK_NOMINA.GL_LICENCIAS
      PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;                        --' Dias PCK_NOMINA.GL_PENDIENTES de Vacaciones
      PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;                   -- ' Prima Proporcional
      PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;                  -- ' Valor a pagar por vacacion proporcional
      PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;                         -- ' Valor a pagar por dias PCK_NOMINA.GL_PENDIENTES
      PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;                   --   ' Dias proporcionales vacaciones
        
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;--SUELDO
    PCK_NOMINA.CN(976) := 0;--EXTRAS
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GASTOSREP;--GASTOS DE REP
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;--AUX TRANSPORTE
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;--ALIMENTACION
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;--PRIMA TECNICA


    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);--BONIFICACION ANUAL POR SERVICIOS PREST
    PCK_NOMINA.CN(983) := 0;

END PR_CALPRIMADEVACACIONESGNAR;

PROCEDURE PR_CALCULARPRIMASERVICIOSGNAR(
      /*
      NAME              : PR_CALCULARPRIMASERVICIOSGNAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 07/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access calcularprimaSERVICIOSGNAR_NUEVA
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCULARPRIMASERVICIOSGNAR
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
  MI_FACTORPS                                                    PCK_SUBTIPOS.TI_DOBLE              DEFAULT 0;
  MI_DOCEAVASMINIMASPS                                           PCK_SUBTIPOS.TI_ENTERO             DEFAULT 0;
  MI_DOCEAVASMINIMASPRS                                          PCK_SUBTIPOS.TI_ENTERO             DEFAULT 0;
  MI_CALCULARPRIMADESERPS                                        VARCHAR2(32000);
  MI_ELIMINARTIEMPOMINIMO                      VARCHAR2(32000);
  MI_PRIMAJUNIO     NUMBER:= 0;
  MI_DIASPACTADOS   NUMBER:= 0;
  MI_INDRETIR       NUMBER:= 0;
  MI_FECHAPRUEBA   DATE;
  MI_SALARIO NUMBER := 0;
  
  MI_MSG          PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 
    MI_DOCEAVASMINIMASPRS := PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1');
    MI_DOCEAVASMINIMASPS  := PCK_PARST.FC_PAR('PCK_NOMINA.GL_DOCEAVAS MINIMAS PRIMA SERVICIOS', '1');
    MI_ELIMINARTIEMPOMINIMO := PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ');
    MI_CALCULARPRIMADESERPS := PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ');
    PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO , 6, 3, PCK_NOMINA.GL_SANO , 6, 3,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    

    -- JM 7749337 INI 10/07/2024
    BEGIN 
        SELECT SUELDOMENSUAL 
        INTO MI_SALARIO
        FROM ENCARGOS 
        WHERE COMPANIA = PCK_NOMINA.GL_COMPANIA 
        AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
        BETWEEN FECHAINICIO AND FECHAFINAL;
        PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 10, MI_SALARIO,NULL,PCK_CONEXION.FC_GETUSER);
        PCK_NOMINA.CNA(10) := MI_SALARIO;
        PCK_NOMINA.CN(10) := MI_SALARIO;
        EXCEPTION  WHEN NO_DATA_FOUND THEN
        MI_SALARIO := PCK_NOMINA.FC_CN(1);
    END;

    /*IF PCK_NOMINA.FC_CNA(10) <> 0 AND PCK_NOMINA.FC_CNA(11) >= 15 THEN -- verIFica novedades de encargos en noviembre
      PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 10, PCK_NOMINA.FC_CNA(10),NULL,PCK_CONEXION.FC_GETUSER);
      PCK_NOMINA.CN(1) := PCK_NOMINA.FC_CNA(10);
      PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 11, PCK_NOMINA.FC_CNA(11),NULL,PCK_CONEXION.FC_GETUSER);
      PCK_NOMINA.CN(10) := PCK_NOMINA.FC_CNA(10);
      PCK_NOMINA.CN(11) := PCK_NOMINA.FC_CNA(11);
    END IF; */ --comentado por JM 10/07/2024 (acumulado cn(11) no existe)

    -- JM 7749337 INI 10/07/2024
   
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    PCK_NOMINA.GL_DNT1 := 0;
  
    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 <= TO_DATE('31/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN  TO_DATE('01/07/' || (TO_NUMBER(PCK_NOMINA.GL_SANO) - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' ||  PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END ; --15022012 CASA
    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN  PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPS END; 
    IF  PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 2  OR PCK_NOMINA.GL_SPER = 3 OR PCK_NOMINA.GL_SPER = 13 OR PCK_NOMINA.GL_SPER = 14 THEN
      PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 
                                OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) >= PCK_NOMINA.GL_FECHAINI1 
                                AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) <= PCK_NOMINA.GL_FECHAFIN1 
                               THEN  PCK_NOMINA.GL_FECHAFIN1 
                               ELSE  TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') 
                              END ;
    PCK_NOMINA.GL_FACTORPS := PCK_NOMINA.GL_FACTORPS;
    IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4 THEN
      PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 
                                     OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) >= PCK_NOMINA.GL_FECHAINI1 
                                     AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) <= PCK_NOMINA.GL_FECHAFIN1 
                                  THEN  PCK_NOMINA.GL_FECHAFIN1 
                                  ELSE  TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY')
                                END;
    END IF;
      
    PCK_NOMINA.GL_AC  := PCK_NOMINA.FC_ACUM(UN_COMPANIA, EXTRACT(YEAR FROM PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN  1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT; --JM CC 1988
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GASTOSREP;
    PCK_NOMINA.CN(951) := PCK_NOMINA.GL_VPT;
    
    IF PCK_NOMINA.FC_CN(952) = 0 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(150) > 0 THEN  --29022016 gnarino caso gobernador saliente
         PCK_NOMINA.CN(952) := PCK_NOMINA.FC_CN(952) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0); --04042019CASA
    END IF;
    
      
    --14022019 gnar tar 1000090012 07022019 Teniendo en cuenta la expedicion del decreto No. 2278 del 11 de diciembre de 2018, se solicita actualizar el procedimiento del calculo de la prima de servicios, teniendo en cuenta la bonIFicacion anual como factor salarial
 
    IF ((PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7) AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO  >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO  <= PCK_NOMINA.GL_FECHAFIN) THEN --14022019 gnar
         PCK_NOMINA.CN(952) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
         PCK_NOMINA.CN(952) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0); --14022019 gnar
    END IF;
      
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
      --IF CVDate(PCK_NOMINA.GL_FECHAIPS) < TO_DATE('01/07/', CStr(VAL(PCK_NOMINA.GL_SANO) - 1)) THEN
      -- 13082012 casa retiros en julio
    IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) >= (PCK_NOMINA.GL_FECHAINI1) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) <= PCK_NOMINA.GL_FECHAFIN1 THEN
            PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('31/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')  ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;  --15022012 CASA
            PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
            PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) >= PCK_NOMINA.GL_FECHAINI1 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) <= (PCK_NOMINA.GL_FECHAFIN1) THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END; 
            PCK_NOMINA.GL_DCC      := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;
    -- IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = "00308" THEN Stop
    IF PCK_NOMINA.GL_DNT <> 0 THEN
            FOR I IN 7..12 LOOP
                  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, ((TO_NUMBER(PCK_NOMINA.GL_SANO) - 1)), I, 1, ((TO_NUMBER(PCK_NOMINA.GL_SANO) - 1)), I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                  IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
                     PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                     --PCK_NOMINA.FC_CN(953) = PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359))
                  END IF;
            END LOOP;
            IF PCK_NOMINA.GL_SMES <= 7 THEN
            FOR I IN 1..PCK_NOMINA.GL_SMES LOOP 
                  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                  IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359)) > 0 THEN
                     PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                     --PCK_NOMINA.FC_CN(953) = PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359))
                  END IF;
            END LOOP;
            END IF;
    END IF;
    --ANTES FACTORPS = PCK_SYSMAN_UTL.FC_ROUND(FACTORPS, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(952) / 12, 0)
    --acumula sueldos de mayo para verIFicar
    IF PCK_NOMINA.GL_SMES = 7 THEN --04042019
         PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 6, 3, PCK_NOMINA.GL_SANO, 6 , 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0  THEN  PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(952) ; -- 19062015 se quito en actualizacion carta liq def. + PCK_NOMINA.FC_CN(952)
    ELSE
         --acs = Acum(CStr(PCK_NOMINA.GL_SANO), strzero("06", 2), "03", CStr(PCK_NOMINA.GL_SANO), strzero("06", 2), "03", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)
         PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0  THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(952);-- 19062015 se quito en actualizacion carta liq def. + PCK_NOMINA.FC_CN(952)
    END IF;
    --PCK_NOMINA.FC_CN(951) = 0
    PCK_NOMINA.CN(951) := PCK_NOMINA.GL_VPT;
      --9062015
      --14022019 PCK_NOMINA.FC_CN(952) = 0
      IF  PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA SEMESTRAL', ' ')  = 'SI' THEN
         --SUMAFACTORES_PS
         PCK_NOMINA.GL_FACTORPS := PCK_NOMINA_COM9.FC_SUMAFACTORES_PS(UN_COMPANIA);
      END IF;
      IF MI_ELIMINARTIEMPOMINIMO = 'SI' THEN -- 25102019
            PCK_NOMINA.GL_DCC               := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
            MI_DOCEAVASMINIMASPS := NVL(MI_DOCEAVASMINIMASPRS, 1);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
      ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFPS) < 179 AND PCK_NOMINA.GL_FECHAI > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
               PCK_NOMINA.GL_DOCEAVAS := 0; 
               PCK_NOMINA.GL_DCC := 0;
               --ALER_TIENE180DIASLABORADOS       CONSTANT PLS_INTEGER := 61000320;
               --El empleado: --EMPLEADO-- Tiene menos de 180 dÃ­as laborados, y no ingreso el primer dia hÃ¡bil del aÃ±o, CÃ©dula No. --CEDULA--
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'CEDULA';
               MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_TIENE180DIASLABORADOS
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );
            END IF;
      END IF;
      --19062015 cuANDo el empleado no haya laborado los 6 meses del semestre no tiene derecho caso retiro calderon carlos Eduardo ingreso en 01082014 y se retiro 30032015
      --23062015 se quito llamada jenny con gnar IF (CVDate(PCK_NOMINA.GL_FECHAIPS) > TO_DATE('01/07/', (TO_NUMBER(PCK_NOMINA.GL_SANO) - 1)) AND CVDate(PCK_NOMINA.GL_FECHAIPS) < TO_DATE('31/12/', (TO_NUMBER(PCK_NOMINA.GL_SANO) - 1))) AND CVDate(PCK_NOMINA.GL_FECHAFIN1) < TO_DATE('30/06/', PCK_NOMINA.GL_SANO) THEN
      --   PCK_NOMINA.GL_DOCEAVAS = 0
      --   Alerta "El empleado: ', personal![APELLIDO1] & " ', personal![NOMBRES] & " NO Tiene derecho por NO haber laborado mÃ­nimo 1 semestre, ajustado 19062015 ', ", CÃ©dula No. ', personal!NUMERO_DCTO
      --END IF;
      --requerimiento de mayo 20 2016 tar 63257-63259
      
      IF NOT MI_ELIMINARTIEMPOMINIMO = 'SI' THEN -- 25102019
            IF PCK_NOMINA.GL_DCC < 180 THEN 
               PCK_NOMINA.GL_DOCEAVAS := 0;
               --PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
               --ALER_NOLABOROMINIMOSEMESTRE       CONSTANT PLS_INTEGER := 61000321;
               --El empleado: --EMPLEADO-- NO Tiene derecho por NO haber laborado mÃ­nimo 1 semestre, CÃ©dula No. --CEDULA--
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'CEDULA';
               MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );
            END IF;
      END IF;
      PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0  THEN  PCK_NOMINA.FC_CNA(10) ELSE  PCK_NOMINA.FC_CN(1) END;    -- SUELDO
      
      IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/07/' || ((TO_NUMBER(PCK_NOMINA.GL_SANO) - 1)), 'DD/MM/YYYY' ) AND PCK_NOMINA.GL_FECHAIPS < TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
         PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC) / 30 * 15 + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END ; -- 04042019
      ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DCC >= 1 THEN
         PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC) / 30 * 15 + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END;  -- 04042019
      ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
         PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAR);
          --''IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '00385' THEN
          --'   PCK_NOMINA.GL_DOCEAVAS = 1
          --'END IF;
          FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
             PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
             IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359)) > 0 THEN --PCK_NOMINA.FC_CNA(359) 140607
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
             END IF;
          END LOOP;
          --140607 IF PCK_NOMINA.GL_DOCEAVAS < 6 THEN PCK_NOMINA.GL_DOCEAVAS = 0
          --19062015 cuANDo el empelado no haya laborado los 6 meses del semestre no tiene derecho caso retiro calderon carlos Eduardo ingreso en 01082014 y se retiro 30032015
            
          --IF (CVDate(PCK_NOMINA.GL_FECHAIPS) > TO_DATE('01/07/', PCK_NOMINA.GL_SANO - CASE WHEN PCK_NOMINA.GL_SMES <= "06", 1, 0)) AND CVDate(PCK_NOMINA.GL_FECHAIPS) < TO_DATE('31/12/', PCK_NOMINA.GL_SANO - CASE WHEN PCK_NOMINA.GL_SMES <= "06", 1, 0))) AND CVDate(PCK_NOMINA.GL_FECHAFIN1) < TO_DATE('30/06/', PCK_NOMINA.GL_SANO) THEN
          --   PCK_NOMINA.GL_DOCEAVAS = 0: 'PCK_NOMINA.FC_CN(160) = 0
          --   Alerta "El empleado: ', personal![APELLIDO1] & " ', personal![NOMBRES] & " NO Tiene derecho por NO haber laborado mÃ­nimo 1 semestre, ajsutado 19062015 ', ", CÃ©dula No. ', personal!NUMERO_DCTO
          --END IF;
          MI_DOCEAVASMINIMASPS := PCK_PARST.FC_PAR('PCK_NOMINA.GL_DOCEAVAS MINIMAS PRIMA SERVICIOS', '1');
          IF MI_ELIMINARTIEMPOMINIMO = 'SI' THEN -- 25102019
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
                    MI_DOCEAVASMINIMASPS := NVL(MI_DOCEAVASMINIMASPRS, 1);
                    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
          ELSE
              IF PCK_NOMINA.GL_DCC < 179 THEN 
                PCK_NOMINA.GL_DOCEAVAS := 0; 
                PCK_NOMINA.GL_DCC := 0;
                
                --PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
                --  ALER_NOLABOROMINIMOSEMESTRE    CONSTANT PLS_INTEGER := 61000321;
                --El empleado: --EMPLEADO-- NO Tiene derecho por NO haber laborado mÃ­nimo 1 semestre, CÃ©dula No. --CEDULA--
                MI_MSG(1).CLAVE := 'EMPLEADO';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'CEDULA';
                MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                PCK_NOMINA_COM7.PR_ALERTA
                   (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                   ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
                   ,UN_REEMPLAZOS   => MI_MSG
                   ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                   ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                   ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                   ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                   ,UN_USER         => PCK_CONEXION.FC_GETUSER
                   );
              END IF;
              IF PCK_NOMINA.GL_FECHAIPS > TO_DATE('01/07/' || (TO_NUMBER(PCK_NOMINA.GL_SANO) - CASE WHEN PCK_NOMINA.GL_SMES <= 6 THEN  1 ELSE  0 END),'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAFPS < TO_DATE('31/12/' || (TO_NUMBER(PCK_NOMINA.GL_SANO) - CASE WHEN PCK_NOMINA.GL_SMES <= 6 THEN  1 ELSE 0 END),'DD/MM/YYYY') THEN
                  PCK_NOMINA.GL_DOCEAVAS := 0; --PCK_NOMINA.FC_CN(160) = 0
                  --PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
               --ALER_NOLABOROMINIMOSEMESTRE       CONSTANT PLS_INTEGER := 61000321;
               --El empleado: --EMPLEADO-- NO Tiene derecho por NO haber laborado mÃ­nimo 1 semestre, CÃ©dula No. --CEDULA--
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'CEDULA';
               MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_NOLABOROMINIMOSEMESTRE
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );
              END IF;
          END IF;
          --PCK_NOMINA.FC_CN(160) = CASE WHEN PCK_NOMINA.FC_CN(160) = 0, PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS * PCK_NOMINA.GL_DOCEAVAS / 12) / 30 * 15 + 0.5, 0), PCK_NOMINA.FC_CN(160))
          PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC) / 30 * 15 + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END;          -- 04042019
      ELSE
         --PCK_NOMINA.GL_DOCEAVAS = CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= 6, PCK_NOMINA.GL_DOCEAVAS, CASE WHEN personal!Fecha_de_Ingreso >= "01/01/2004", PCK_NOMINA.GL_DOCEAVAS, 0))
              IF MI_ELIMINARTIEMPOMINIMO = 'SI' THEN -- 25102019
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
                    MI_DOCEAVASMINIMASPS := NVL(MI_DOCEAVASMINIMASPRS, 1);
                    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
              ELSE
                    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
                    IF PCK_NOMINA.GL_DCC < 179 AND PCK_NOMINA.GL_FECHAI > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                      PCK_NOMINA.GL_DOCEAVAS := 0;
                      PCK_NOMINA.CN(160) := 0;
                      PCK_NOMINA.GL_DCC := 0;
                    END IF;
              END IF;
            --PCK_NOMINA.FC_CN(160) = CASE WHEN PCK_NOMINA.FC_CN(160) = 0, PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS * PCK_NOMINA.GL_DOCEAVAS / 12) / 30 * 15 + 0.5, 0), PCK_NOMINA.FC_CN(160))
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC) / 30 * 15 + 0.5, 0) ELSE  PCK_NOMINA.FC_CN(160) END;   --04042019
      END IF;
      MI_PRIMAJUNIO   := PCK_NOMINA.FC_CN(160);
      MI_DIASPACTADOS := PCK_NOMINA.FC_CN(67);
      --FactorPS1 = PRIMAJUNIO - CASE WHEN PCK_NOMINA.GL_AUXT = 0, 0, (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS))
      MI_INDRETIR := PCK_NOMINA.FC_CN(404);
      IF PCK_NOMINA.GL_SPER = 4 OR PCK_NOMINA.GL_SPER = 14 THEN
          FOR I IN 2..699 LOOP
            IF (i <> 125) AND i <> 303 AND i <> 172 AND i <> 300 AND i <> 301 AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
               PCK_NOMINA.CN(i) := 0;
            END IF;
          END LOOP;
      END IF;
      PCK_NOMINA.CN(404) := MI_INDRETIR;
      IF MI_CALCULARPRIMADESERPS = 'SI' THEN -- 25102019
          PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DCC;
      ELSE
          PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DOCEAVAS;
      END IF;
      --GuardANDo Factores
      PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
      PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXA; --23072012 gnar 'Base de Subsidio de alimentaciÃ³n para prima
      PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXT; --se quito 05072012 carta PCK_NOMINA.GL_AUXT 'Base de Subsicio de Transporte para prima
      
      PCK_NOMINA.CN(946) := PCK_NOMINA.GL_FACTORPS;
      --PCK_NOMINA.FC_CN(947) = (Sumacona(47, 60)) - (PCK_NOMINA.FC_CNA(56) + PCK_NOMINA.FC_CNA(879)) + CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/06/', PCK_NOMINA.GL_SANO), 0, Sumacon(47, 60) - PCK_NOMINA.FC_CN(56))               ' Horas Extras
      --PCK_NOMINA.FC_CN(948) = PCK_NOMINA.FC_CNA(80) + PCK_NOMINA.FC_CNA(95) + CASE WHEN CVDate(PCK_NOMINA.GL_FECHAFIN1) < TO_DATE('16/06/', PCK_NOMINA.GL_SANO), 0, PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(95))                                  ' Transporte
      --PCK_NOMINA.FC_CN(953) = PCK_NOMINA.GL_DNT                                                                                          ' Licencias
      --PCK_NOMINA.FC_CN(951) = 0
END PR_CALCULARPRIMASERVICIOSGNAR;

PROCEDURE PR_LIQUIDAR_AUXILIO_CESANIAS(
    /*
      NAME              : PR_LIQUIDAR_AUXILIO_CESANIAS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 14/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access LIQUIDAR_AUXILIO_CESANIAS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  LIQUIDAR_AUXILIO_CESANIAS
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
  MI_DIATEMP                            NUMBER := 0;
  MI_FECHATMP                           DATE;
  MI_DIAINT                             NUMBER :=0;
  MI_SANO1                              NUMBER :=0;
  MI_SMES1                              NUMBER :=0;
  MI_CESANTIAS                          NUMBER :=0;
  MI_CESANTIA1                          NUMBER :=0;
  MI_DIA_PROMEDIO_ENCARGOS              NUMBER :=0;
  MI_DIAS                               NUMBER :=0;
  MI_SALARIOPORCENTAJE                  PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_REVPROALARIOCARGO                  PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PORCENTAJESACUMULADO               PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_TOTALDIA                           NUMBER :=0;
  MI_RS_FECHAINICIAL                    DATE;
  MI_RS_FECHAFINAL                      DATE;
  MI_RS_SALARIO_BASE                    NUMBER :=0;
  MI_BANDERA                            NUMBER :=0;
  MI_MSG                                PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RS                                 SYS_REFCURSOR;
BEGIN
IF PCK_NOMINA.GL_SPER = 6 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
        
           IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ANTICIPO_CESANTIAS IS NOT NULL THEN
              PCK_NOMINA.GL_FECHAFIN1 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ANTICIPO_CESANTIAS;
              PCK_NOMINA.GL_FECHAFIN := PCK_NOMINA.GL_FECHAFIN1;
              --Digite la fecha hasta la cual se calcularÃ¡ los ANTICIPOS. del empleado --EMPLEADO--, Documento: --CEDULA-- ,  FECHA ACTUAL= --FECHAANTICIPOMI_CESANTIAS-- 
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'CEDULA';
               MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
               MI_MSG(3).CLAVE := 'FECHAANTICIPOCESANTIAS';
               MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ANTICIPO_CESANTIAS;
               

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_NOFECHAANTICIPOCES
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );              
           ELSE
              PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ANTICIPO_CESANTIAS := PCK_NOMINA.GL_FECHAFIN1;
           END IF;
       
END IF;
IF PCK_NOMINA.FC_CN(411) <> 0 OR PCK_NOMINA.FC_CN(412) <> 0 OR (PCK_NOMINA.FC_CN(404) <> 0 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 AND PCK_NOMINA.GL_SMES = 12)) OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 AND PCK_NOMINA.GL_SMES = 12) THEN --'REGIMEN DE RETROACTIVAS SOLAMENTE EN DICIEMBRE
      PCK_NOMINA.GL_BASCES := 0;
      MI_SANO1  := CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFIN) = 1 THEN  PCK_NOMINA.GL_SANO - 1 ELSE  PCK_NOMINA.GL_SANO END ;
      MI_SMES1  := CASE WHEN PCK_NOMINA.GL_SMES = 1 THEN  12 ELSE  PCK_NOMINA.GL_SMES END;
      --ValidANDo el Cambio de Sueldo en los ultimos tres meses
      PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, (CASE WHEN PCK_NOMINA.GL_SMES < 4 THEN  PCK_NOMINA.GL_SANO - 1 ELSE PCK_NOMINA.GL_SANO END), (CASE WHEN PCK_NOMINA.GL_SMES < 4 THEN  12 - (3 - PCK_NOMINA.GL_SMES) ELSE  PCK_NOMINA.GL_SMES - 3 END ), CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN 2 ELSE  1 END , (CASE WHEN PCK_NOMINA.GL_SMES < 4 THEN  PCK_NOMINA.GL_SANO - 1 ELSE  PCK_NOMINA.GL_SANO END ), (CASE WHEN PCK_NOMINA.GL_SMES < 4 THEN  12 - (3 - PCK_NOMINA.GL_SMES) ELSE  PCK_NOMINA.GL_SMES - 3 END ), CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN '02' ELSE  '01' END , PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- ' Acumulado del aÃ±o actual
      MI_DIATEMP := PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN);
      IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN) = 31 AND (PCK_NOMINA.GL_SMES = 4 OR PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 9 OR PCK_NOMINA.GL_SMES = 11) THEN
         PCK_NOMINA.GL_FECHAFIN := PCK_NOMINA.GL_FECHAFIN - 1;
         MI_DIATEMP := PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN);
      END IF;
      IF PCK_NOMINA.GL_SMES = 2 THEN  --'17022020
         IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN) = 29 THEN --17022020
            MI_FECHATMP := TO_DATE( (PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN - 1)) || '/' || (PCK_NOMINA.GL_SMES) || '/' ||  (PCK_NOMINA.GL_SANO - 1) ,'DD/MM/YYYY');-- '17022020
         ELSE
            MI_FECHATMP := TO_DATE( (PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN))|| '/' || (PCK_NOMINA.GL_SMES) || '/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY');
         END IF;
      ELSE
         MI_FECHATMP := TO_DATE( (PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN)) || '/' || (PCK_NOMINA.GL_SMES) || '/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY');
      END IF;
      IF MI_FECHATMP IS NOT NULL AND MI_DIATEMP = 29 THEN --'17022020
         MI_DIATEMP := 28;
      END IF;
      IF  (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ULTSUELDO  IS NULL THEN  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ULTSUELDO END -  PCK_NOMINA.GL_FECHAFIN) > 90 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE(  (MI_DIATEMP - CASE WHEN PCK_NOMINA.GL_SMES = 2 AND MI_DIATEMP = 29 THEN  1 ELSE  0 END) || '/' || (PCK_NOMINA.GL_SMES) || '/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY') THEN --'17022020
         PCK_NOMINA.GL_SBM := PCK_NOMINA.FC_CN(1);
      ELSE     --'Si son DIFerentes se Calcula el promedio
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,(MI_SANO1 - 1), (PCK_NOMINA.GL_SMES + 1), 1, (MI_SANO1), (MI_SMES1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulado del aÃ±o actual
         PCK_NOMINA.GL_SBM := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(2) / CASE WHEN PCK_NOMINA.FC_CNA(404) <> 0 THEN PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01' || '/' || (CASE WHEN PCK_NOMINA.GL_SMES = 12 THEN 1 ELSE PCK_NOMINA.GL_SMES + 1 END)  || '/' || (CASE WHEN PCK_NOMINA.GL_SMES = '12' THEN  PCK_NOMINA.GL_SANO ELSE PCK_NOMINA.GL_SANO - 1 END),'DD/MM/YYYY') THEN  TO_DATE(  '01' || '/' || CASE WHEN PCK_NOMINA.GL_SMES = 12 THEN  1 ELSE PCK_NOMINA.GL_SMES + 1 END  || '/' || (CASE WHEN PCK_NOMINA.GL_SMES = 12 THEN  PCK_NOMINA.GL_SANO ELSE PCK_NOMINA.GL_SANO - 1 END),'DD/MM/YYYY') ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO END, PCK_NOMINA.GL_FECHAFIN) ELSE  12 END, 0);
      END IF;
      PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN - 15);
      PCK_NOMINA.GL_FECHAIR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
      --'IF EXTRACT(YEAR FROM PCK_NOMINA.GL_FECHAIR) < 1900 THEN
      --'     msgbox "Revise la fecha de ingreso de " || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, vbExclamation, "Sysman Software"
      --'     FV = False
      --'END IF;
    --'IF FV THEN
      IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN --'RETROACTIVAS
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,EXTRACT(YEAR FROM PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN  1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulados
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339);
         MI_DIAINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - (PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION); --''- PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PCK_NOMINA.GL_DIAINTERRUPCION  'ESTOS PCK_NOMINA.GL_DIA DE INTERRUPCION YA FUERON DESCONTADOS EN LA FECHA DE INGRESO REAL
         --'PCK_NOMINA.GL_DIA = PCK_NOMINA.GL_DIA + MasPCK_NOMINA.GL_DIAOtraentidad
         PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
        --' Dp = 360
         --'PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUMCC(PCK_NOMINA.GL_COMPANIA,(ANOA), strzero(mesa, 2), strzero(pera, 2), (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO) ' Acumulado del ultimo aÃ±o
      ELSE
         PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN  PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUMCC(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN  1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO);-- ' Acumulado del aÃ±o actual
         PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339);
         MI_DIAINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS - PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
         PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 31);
      END IF;
   --'END IF;
      --'070908 PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO - 1, PCK_NOMINA.GL_SMES, "01", PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), "99", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) 'ACUMULADO DESDE ENERO
      IF PCK_NOMINA.GL_SMES = 12 THEN
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- 'ACUMULADO DESDE ENERO
      ELSE
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO - 1, PCK_NOMINA.GL_SMES,1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --'ACUMULADO DESDE ENERO
      END IF;
      PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
      IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD',' ') = 'SI' THEN
                --SUMAFACTORES_CS
                PCK_NOMINA.GL_BASCES := PCK_NOMINA_COM9.FC_SUMAFACTORES_CS(UN_COMPANIA);
                --PCK_NOMINA.GL_BASCES = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
      ELSE
           PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
      END IF;
      PCK_NOMINA.GL_DIAS := MI_DIAINT;
      MI_CESANTIAS := PCK_NOMINA.GL_BASCES * PCK_NOMINA.GL_DIAS / 360;
      MI_CESANTIAS := MI_CESANTIAS - PCK_NOMINA.GL_ANTICIPOS_CES;
      PCK_NOMINA.CN(901) := PCK_NOMINA.GL_VPT;
      IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
         --'GuardANDo FactORes
         --'PCK_NOMINA.FC_CN(913) = PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS, 0) 'BASE
         PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1); --'PCK_NOMINA.GL_SBM                             ' Sueldo
         PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
         PCK_NOMINA.CN(902) := PCK_NOMINA.GL_GASTOSREP;
         PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0);
         IF PCK_NOMINA.FC_CN(905) = 0 THEN
            PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
         END IF;
         PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
         IF PCK_NOMINA.FC_CN(906) = 0 THEN
           PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
         END IF;
         PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
         IF PCK_NOMINA.FC_CN(907) = 0 THEN
            PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
         END IF;
         PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
         IF PCK_NOMINA.FC_CN(908) = 0 THEN
            PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
         END IF;
         PCK_NOMINA.CN(911) := PCK_NOMINA.GL_ANTICIPOS_CES; --'= PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN - 31)
         --'PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0)
         --''PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
         PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907)), 0) + PCK_NOMINA.FC_CN(908);
         IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD',' ') = 'SI' THEN
                --SUMAFACTORES_CS
                PCK_NOMINA.GL_BASCES := PCK_NOMINA_COM9.FC_SUMAFACTORES_CS(UN_COMPANIA);
                --'PCK_NOMINA.GL_BASCES = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
         ELSE
             PCK_NOMINA.GL_BASCES := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907)), 0) + PCK_NOMINA.FC_CN(908);
         END IF;
         PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_BASCES, 0);-- 'BASE
         PCK_NOMINA.GL_DIAS := MI_DIAINT;
         MI_CESANTIAS := PCK_NOMINA.GL_BASCES * PCK_NOMINA.GL_DIAS / 360;
         MI_CESANTIAS := MI_CESANTIAS - PCK_NOMINA.GL_ANTICIPOS_CES;
         PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN  MI_CESANTIAS ELSE PCK_NOMINA.FC_CN(277) END;
         --'retro no intereses
         --'PCK_NOMINA.FC_CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN  CASE WHEN MI_CESANTIA1 < 0 THEN  0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS * 12 / 100, 0) END  ELSE PCK_NOMINA.FC_CN(269) END;
         PCK_NOMINA.CN(910) := PCK_NOMINA.GL_DIAS;--                                   ' PCK_NOMINA.GL_DIAS
         PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS;--                              ' PCK_NOMINA.GL_DIA no trabajados pOR PCK_NOMINA.GL_LICENCIAS
         --'PCK_NOMINA.FC_CN(913) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_BASCES, 0)                       ' Promedio
      END IF;
      IF (PCK_NOMINA.FC_CN(412) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0) AND PCK_NOMINA.GL_SPER <> 4 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> '01') THEN --'RETROACTIVAS DIFERENTE DE FNA
         --'REVISAR EL PROMEDIO DE SALARIOS CON ENCARGOS.
         --'REVISARPROMEDIOSALARIOENCARGO PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
        MI_TOTALDIA := MI_DIAINT;
        --'MI_REVPROALARIOCARGO  OSALARIOENCARGO = VAL(RSP.RecORdCount)
        MI_BANDERA :=0;
        OPEN MI_RS FOR 
          SELECT  CONSULTAR_ENCARGOS.FECHAINICIO
                , CONSULTAR_ENCARGOS.FECHAFINAL
                , CATEGORIA.SALARIO_BASE 
          FROM ENCARGOS CONSULTAR_ENCARGOS 
            LEFT JOIN PERSONAL ON CONSULTAR_ENCARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
              AND CONSULTAR_ENCARGOS.COMPANIA = PERSONAL.COMPANIA 
            LEFT JOIN CATEGORIA ON CONSULTAR_ENCARGOS.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA
              AND CONSULTAR_ENCARGOS.ESCALAFON = CATEGORIA.ESCALAFON
              AND CONSULTAR_ENCARGOS.COMPANIA = CATEGORIA.COMPANIA
          WHERE PERSONAL.NUMERO_DCTO =  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO 
            AND CATEGORIA.ANO = CONSULTAR_ENCARGOS.ANO 
          ORDER BY CONSULTAR_ENCARGOS.FECHAINICIO;
        LOOP
        FETCH MI_RS
        INTO MI_RS_FECHAINICIAL,
            MI_RS_FECHAFINAL,
            MI_RS_SALARIO_BASE;

            IF  MI_RS%ROWCOUNT =0 AND MI_RS%NOTFOUND THEN
                  MI_REVPROALARIOCARGO  := 0;
                  PCK_NOMINA.GL_SBM := PCK_NOMINA.FC_CN(1);
                EXIT WHEN MI_RS%NOTFOUND ;
            ELSIF MI_RS%NOTFOUND  THEN 
                EXIT WHEN MI_RS%NOTFOUND ;
            ELSE
                MI_BANDERA :=1;
                MI_DIA_PROMEDIO_ENCARGOS := MI_DIA_PROMEDIO_ENCARGOS + (MI_RS_FECHAINICIAL - MI_RS_FECHAFINAL) + 1;
                MI_DIAs := 0;
                MI_DIAs := (MI_RS_FECHAINICIAL - MI_RS_FECHAFINAL) + 1;
                MI_SALARIOPORCENTAJE := PCK_SYSMAN_UTL.FC_ROUND(MI_DIAs * 100 / MI_TOTALDIA, 6);
                MI_PORCENTAJESACUMULADO := MI_PORCENTAJESACUMULADO + MI_SALARIOPORCENTAJE;
                MI_REVPROALARIOCARGO  := MI_REVPROALARIOCARGO  + PCK_SYSMAN_UTL.FC_ROUND(MI_RS_SALARIO_BASE * MI_SALARIOPORCENTAJE / 100, 0);
            END  IF;
        END LOOP; 
        CLOSE MI_RS; 
        IF MI_BANDERA =  1 THEN 
          MI_TOTALDIA := MI_DIAINT - MI_DIA_PROMEDIO_ENCARGOS;
          MI_SALARIOPORCENTAJE := 100 - MI_PORCENTAJESACUMULADO;
          PCK_NOMINA.GL_SBM := MI_REVPROALARIOCARGO  + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * MI_SALARIOPORCENTAJE / 100, 0);
        END IF;
        MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD',' ') = 'SI' THEN
                --SUMAFACTORES_CS
            MI_CESANTIAS := PCK_NOMINA_COM9.FC_SUMAFACTORES_CS(UN_COMPANIA);
            --'PCK_NOMINA.GL_BASCES = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
        ELSE
            MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS, 0); --'BASE
        PCK_NOMINA.CN(900) := PCK_NOMINA.GL_SBM; --'SUELDO
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(902) := PCK_NOMINA.GL_GASTOSREP;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0);
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
         
        PCK_NOMINA.CN(909) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        PCK_NOMINA.CN(910) := PCK_NOMINA.GL_DIAS;
        IF PCK_NOMINA.GL_SPER = 8 OR PCK_NOMINA.GL_SPER = 6 THEN
           PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIAS / 360 * PCK_NOMINA.GL_DIAS), 0) - PCK_NOMINA.GL_ANTICIPOS_CES ELSE  PCK_NOMINA.FC_CN(277) END;
        END IF;
        IF PCK_NOMINA.GL_SPER = 6 THEN
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIAS / 360 * PCK_NOMINA.GL_DIAS), 0) - PCK_NOMINA.GL_ANTICIPOS_CES ELSE  PCK_NOMINA.FC_CN(177) END;
        END IF;
         --'GuardANDo FactORes
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GASTOSREP;
        PCK_NOMINA.CN(909) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        PCK_NOMINA.CN(911) := PCK_NOMINA.GL_ANTICIPOS_CES;                                               --' PCK_NOMINA.GL_ANTICIPOS_CES
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION; --                 ' PCK_NOMINA.GL_DIA no trabajados pOR PCK_NOMINA.GL_LICENCIAS
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;-- 'PCK_NOMINA.FC_CNA(175) + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0, 0, PCK_NOMINA.FC_CN(175))
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1; --'24122014
      END IF;
      IF PCK_NOMINA.FC_CN(412) <> 0 AND PCK_NOMINA.GL_SPER <> 4 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = '015') THEN -- 'Fondo Nacional del AhORro
         --'MI_CESANTIAS = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXA + VPA + PCK_NOMINA.GL_AUXT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) / 12), 0)
         --'20052019
         PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- 'ACUMULADO DESDE ENERO
         
         MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
         IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD',' ') = 'SI' THEN
                --SUMAFACTORES_CS
                MI_CESANTIAS := PCK_NOMINA_COM9.FC_SUMAFACTORES_CS(UN_COMPANIA);
                --'PCK_NOMINA.GL_BASCES = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
         ELSE
              MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
         END IF;
         PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS, 0); --'BASE
         PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1); --'SUELDO
         PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
         PCK_NOMINA.CN(902) := PCK_NOMINA.GL_GASTOSREP;
         PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0);
         PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
         PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
         PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
         PCK_NOMINA.CN(910) := PCK_NOMINA.GL_DIAS;
         PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_CNA(277);
         PCK_NOMINA.CN(911) := PCK_NOMINA.FC_CNA(277);
         --'MI_CESANTIAS = MI_CESANTIAS - PCK_NOMINA.GL_ANTICIPOS_CES
         --'MI_CESANTIAS FNA NO SE LIQUIDA INTERESES DE MI_CESANTIAS
         --'PCK_NOMINA.FC_CN(269) = CASE WHEN PCK_NOMINA.FC_CN(269) = 0, CASE WHEN MI_CESANTIA1 < 0, 0, PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS * 12 / 100, 0)), PCK_NOMINA.FC_CN(269))
         IF PCK_NOMINA.GL_SMES <> 12 THEN
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS / 12, 0) ELSE PCK_NOMINA.FC_CN(277) END;
         ELSE
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIAS / 360 * PCK_NOMINA.GL_DIAS), 0) - PCK_NOMINA.GL_ANTICIPOS_CES ELSE PCK_NOMINA.FC_CN(277) END;
         END IF;
         --'IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = "12" THEN
         --'  IncluirNovedad "01", (PCK_NOMINA.GL_SANO + 1), "01", "03", PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, "169", PCK_NOMINA.FC_CN(269)
         --'END IF;
      END IF;
      --''05042019
      IF PCK_NOMINA.FC_CN(404) <> 0 OR (PCK_NOMINA.FC_CN(412) <> 0 AND PCK_NOMINA.GL_SPER <> 4 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> '015' OR ( PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS IS NULL AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('21/11/1996','DD/MM/YYYY')))) THEN --'ley 50
         --'INDICADOR DE CALCULAR DOCEAVAS DE MI_CESANTIAS MENSUALES
         --'DOCPS = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT) / 12, 0) 'DOCEAVA DE PRIMA DE SERVICIOS
         --'DOCPN = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + DOCPS) / 12, 0) 'DOCEAVA DE PRIMA DE NAVIDAD
         --'MI_CESANTIAS = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + DOCPS + DOCPN) / 12, 0)
         MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
         IF PCK_PARST.FC_PAR('SUMAR CONCEPTOS FACTORES PRIMA NAVIDAD',' ') = 'SI' THEN
                --SUMAFACTORES_CS
                MI_CESANTIAS := PCK_NOMINA_COM9.FC_SUMAFACTORES_CS(UN_COMPANIA);
                --'PCK_NOMINA.GL_BASCES = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0)
         ELSE
                MI_CESANTIAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0)), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
         END IF;
         PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIAS, 0); --'BASE
         PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1); --'SUELDO
         PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
         PCK_NOMINA.CN(902) := PCK_NOMINA.GL_GASTOSREP;
         PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0);
         PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
         PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
         PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
         PCK_NOMINA.CN(910) := PCK_NOMINA.GL_DIAS;
         PCK_NOMINA.GL_ANTICIPOS_CES := 0; --' PCK_NOMINA.FC_CNA(277)
         PCK_NOMINA.CN(911) := 0; --'PCK_NOMINA.FC_CNA(277)
         
         IF PCK_NOMINA.FC_CN(404) <> 0 THEN --'05042019
            PCK_NOMINA.GL_ANTICIPOS_CES := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 31); --'05042019
            PCK_NOMINA.CN(911) := PCK_NOMINA.GL_ANTICIPOS_CES; --'05042019
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIAS / 360 * PCK_NOMINA.GL_DIAS), 0) - PCK_NOMINA.GL_ANTICIPOS_CES ELSE  PCK_NOMINA.FC_CN(177) END ;-- '05042019
            PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN  CASE WHEN MI_CESANTIA1 < 0 THEN  0 ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(177) * 12 / 100 / 360 * MI_DIAINT, 0) END  ELSE  PCK_NOMINA.FC_CN(169) END; --'05042019
         ELSE
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIAS / 360 * PCK_NOMINA.GL_DIAS), 0) - PCK_NOMINA.GL_ANTICIPOS_CES ELSE  PCK_NOMINA.FC_CN(277) END ;
            PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN  CASE WHEN MI_CESANTIAS < 0 THEN  0 ELSE  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(277) * 12 / 100 / 360 * MI_DIAINT, 0) END  ELSE PCK_NOMINA.FC_CN(269) END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = 12 THEN
               PCK_NOMINA.PR_INCLUIRNOVEDAD (PCK_NOMINA.GL_COMPANIA,1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
            END IF;
         END IF;
      END IF;
     END IF;
     IF PCK_NOMINA.GL_SPER = 8 THEN --' 21112019
          FOR I IN 1..899 LOOP
              IF I <> 477 AND I <> 277 AND I <> 269 THEN
                  PCK_NOMINA.CN(I) := 0;
              END IF;
          END LOOP;
    END IF;
END PR_LIQUIDAR_AUXILIO_CESANIAS;




FUNCTION FC_PROVISIONES
/*
  NAME               : PROVISIONES
  AUTHOR MIGRACION   : CAMILO ANDRES PEREZ DUEÃ‘AS  
  DATE MIGRADOR      : 22/04/2021
  TIME               : 05:43 PM
  SOURCE MODULE      : NOMINAP2021.03.01_GNAR. En access PROVISIONES
  MODIFIER           :
  DATE MODIFIED      :
  TIME               :
  MODIFICATIONS      :
  DESCRIPTION        : CALCULO PROVISIONES MENSUALES
  */
( 
	UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
  RETURN NUMBER
AS
  MI_RTA               PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
BEGIN
    IF PCK_NOMINA.GL_SPER <> 7 AND PCK_NOMINA.GL_SPER <> 4 THEN --'24072013
        MI_RTA := 1;
        PCK_NOMINA.CN(494) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) * 0.0417, 0); --'Prima de Vacaciones
        PCK_NOMINA.CN(495) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * 1 / 24, 0); --' Provision PRIMA SEMESTRAL
        PCK_NOMINA.CN(497) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) * (0.0417), 0); --'Provision Vacaciones
        PCK_NOMINA.CN(498) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) * (1 / 24), 0); --'provision Prima de Servicio
        PCK_NOMINA.CN(830) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(160)) * 8.33 / 100, 0); --' provision cesantias
        PCK_NOMINA.CN(499) := PCK_NOMINA.FC_CN(830);-- 'PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) * (8.33 / 100), 0) --'Provision cesantias
        PCK_NOMINA.CN(493) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) * (8.33 / 100), 0); --'Provision prima de Navidad
        PCK_NOMINA.CN(496) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(499) * 12 / 100, 0); --    'Provision Intereses Cesantias
    END IF;
    MI_RTA := 1;
    RETURN MI_RTA;
END FC_PROVISIONES;

PROCEDURE PR_CALPRIMAVACACIONESGNARINTER(
      /*
      NAME              : PR_CALPRIMAVACACIONESGNARINTER
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 22/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access calcularprimadeVACACIONESGNARinterrupcion
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  PR_CALPRIMAVACACIONESGNARINTER
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
BEGIN
   IF NOT (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
      --'PCK_NOMINA.FC_CN(174) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.FC_CN(170)), 0) / 2
   --ELSE
      --'adicionarle la condicion de no dejar liquidar mÃ¡s de un periodo de vacaciones a la vez
      --DC := 0;
      PCK_NOMINA.GL_DIASVAC := 0;
      PCK_NOMINA.GL_DIASPENDIENTES := 0;
      PCK_NOMINA.GL_PENDIENTES := 0;
      PCK_NOMINA.GL_LICENCIAS := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
      --' Para personal que se retira
      IF NOT PCK_NOMINA.FC_CN(404) <> 0 THEN
      --ELSE
         --'Vacaciones NORmales
         --'acumulado para dias pensientes de vacaicones
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.CNA(91);
         --'   IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN Alerta "El empleado " & personal!APELLIDO1 & " " & personal!NOMBRES & ", Tiene " & PCK_NOMINA.GL_DIASPENDIENTES & " diasPCK_NOMINA.GL_PENDIENTES de vacaciones." & ", CÃ©dula No. " & personal!NUMERO_DCTO
         PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);    --' dias de prima pactados para Prima de Vacaciones
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulado del ultimo aÃ±o
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --EMPLEADOS OFICIALES
            PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.FC_CN(1); --' factORes prima de vacaciones
         ELSE
            --' PARA EL CASO DE SEPTIEMBRE QUE ES CUANDO MAS TIENEN BONIFICACION ANUAL, NO DEBE TOMAR EL ACUMULADO DE SEPTIEMBRE AÃ‘O ANTERIOR, SOLAMENTE LA QUE SE VA A PAGAR EN EL MES, 25/01/2006 PASTO MPV
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.CNA(160) / 12) + CASE WHEN PCK_NOMINA.GL_SMES <> 9 THEN  (PCK_NOMINA.CNA(150) / 12) + (PCK_NOMINA.FC_CN(150) / 12) ELSE  CASE WHEN PCK_NOMINA.CNA(150) = 0 THEN  PCK_NOMINA.FC_CN(150) ELSE PCK_NOMINA.CNA(150) END / 12 END , 0); -- ' factORes prima de vacaciones
            PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA, 0), 0); --' factORes prima de vacaciones
         END IF;
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN --'dIFerente de salario integral
            IF PCK_NOMINA.FC_CN(99) <> 0 THEN      --' Salario de Vacaciones y prima de vaciones
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                         --' Vacaciones en Tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN      --' Vacaciones en dinero
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;
--'            PCK_NOMINA.FC_CN(155) = CASE WHEN PCK_NOMINA.FC_CN(155) = 0, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0), PCK_NOMINA.FC_CN(155))                     ' Vacaciones en Tiempo
         ELSE --' salario Integral no tiene prima de vacaciones
            IF PCK_NOMINA.FC_CN(403) <> 0 THEN
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                        --' Vacaciones en tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;
         END IF;
      END IF;
   END IF;
END PR_CALPRIMAVACACIONESGNARINTER;

PROCEDURE PR_REVISAR_SINDICATOSS(
      /*
      NAME              : PR_REVISAR_SINDICATOSS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 14/04/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2021.03.01_GNAR. En access REVISAR_SINDICATOSS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  PR_REVISAR_SINDICATOSS
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
   MI_RTA          PCK_SUBTIPOS.TI_LOGICO := 0;
   MI_PSPV         NUMBER:= 0;
   MI_MSG          PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  IF PCK_NOMINA.FC_CN(129) > 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO = '01' THEN 
       --'29042014 PCK_NOMINA.FC_CN(629) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(129) * 5 / 100, 0)
       PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129); --'29042014 - PCK_NOMINA.FC_CN(629)
  ELSIF PCK_NOMINA.FC_CN(129) > 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO <> '01' THEN --'
        PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO SINDICATO SINTRAESTATALES',' ')) := PCK_NOMINA.FC_CN(129);
        PCK_NOMINA.CN(129) := 0;
  END IF;
  --'verIFica concepto 123 aportes UPC para duplicar cuota en vacaciones
  IF PCK_NOMINA.FC_CN(462) > 0 THEN
      IF PCK_NOMINA.CN(462) > PCK_NOMINA.FC_CN(123) THEN
         PCK_NOMINA.CN(462) := PCK_NOMINA.FC_CN(462) - PCK_NOMINA.FC_CN(123);
         PCK_NOMINA.CN(123) := 0;
      Else
         PCK_NOMINA.CN(123) := PCK_NOMINA.FC_CN(123) - PCK_NOMINA.FC_CN(462);
         PCK_NOMINA.CN(462) := 0;
      End IF;
  END IF;
  --'EN ENERO DESCONTAR 2 DIAS DE SALARIO A LOS SINDICALIZADOS, PERO A EMPLEADOS OFICIALES
  IF PCK_NOMINA.GL_SMES = 1  AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO NOT IN(0) THEN
         PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * 2, 0);
  END IF;
  --' Aportes Voluntarios se da el valor de la novedad Mensual
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' AND PCK_NOMINA.GL_SPER <> '03' THEN  --' Para los que se les paga Quincenal
      PCK_NOMINA.CN(124) := PCK_NOMINA.FC_CN(124);
  END IF;
  --'Si Aportes Voluntarios a Pension exceden el 30% del salario Basico Mensual Solo se deduce el valor para completar el 30%
  IF PCK_NOMINA.FC_CN(124) <> 0 THEN
        IF PCK_NOMINA.FC_CN(124) > ((PCK_NOMINA.FC_CN(1) * 30 / 100) - (PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131))) THEN
            PCK_NOMINA.GL_VOLUNTARIOS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) * 30 / 100), 0) - (PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131));--    ' Solo Deduce lo Permitido
            
            --ALER_VALORAPORTEVOL30SAL       CONSTANT PLS_INTEGER := 61000323;
            --El Valor de los Aportes Voluntarios, Excede el 30% del Salario Basico Mensual del Empleado, --EMPLEADO--, CÃ©dula No. --CEDULA--
               MI_MSG(1).CLAVE := 'EMPLEADO';
               MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
               MI_MSG(2).CLAVE := 'CEDULA';
               MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

               PCK_NOMINA_COM7.PR_ALERTA
                  (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                  ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_VALORAPORTEVOL30SAL
                  ,UN_REEMPLAZOS   => MI_MSG
                  ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                  ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                  ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                  ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                  ,UN_USER         => PCK_CONEXION.FC_GETUSER
                  );

        ELSE
             PCK_NOMINA.GL_VOLUNTARIOS := PCK_NOMINA.FC_CN(124);
        END IF;
    END IF;
    --' verIFica pagos anteriores
    IF PCK_NOMINA.FC_CN(463) > 0 THEN
       IF PCK_NOMINA.FC_CN(463) > PCK_NOMINA.FC_CN(124) THEN
          PCK_NOMINA.CN(463)  := PCK_NOMINA.FC_CN(463) - PCK_NOMINA.FC_CN(124);
          PCK_NOMINA.CN(124) := 0;
       ELSE
          PCK_NOMINA.CN(124) := PCK_NOMINA.FC_CN(124) - PCK_NOMINA.FC_CN(463);
          PCK_NOMINA.CN(463) := 0;
       END IF;
    END IF;
    --PonerEnCeros 0, 999, "CNA"
    FOR I IN 0..999 LOOP
        PCK_NOMINA.CNA(I) := 0;
    END LOOP;

    --' RetenciÃ³n en la fuente
    --'RETEF
    --DIFERENCIASGastosRep = 0;

END PR_REVISAR_SINDICATOSS;
PROCEDURE PR_CALCULARPENSIONADOS_GNAR(
      /*
      NAME              : PR_CALCULARPENSIONADOS_GNAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
      DATE MIGRADOR     : 03/05/2021
      TIME              :
      SOURCE  MOD ULE     : NOMINAP2021.03.01_GNAR. En access CALCULARPENSIONADOS
       MOD IFIER          :
      DATE  MOD IFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCULARPENSIONADOS
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
MI_N2                             PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_N1                             PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_FECHAFPN                       DATE;
MI_ANIOS                          PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_MESCOM                         PCK_SUBTIPOS.TI_ENTERO  DEFAULT 0; 
MI_DEVENGOS                       NUMBER  := 0;
MI_PRIMADIC                       NUMBER  := 0;
MI_IBCCAUSANTE                    NUMBER  := 0;
MI_TRANSPORTELEGAL                NUMBER  := 0;
MI_RETEFUENTE                     NUMBER  := 0;
MI_DATOS                          NUMBER  := 0;
MI_I                              NUMBER  := 0;
MI_MENOSMINIMO                    BOOLEAN := FALSE;
MI_IBCDOBLEPENSION                NUMBER  := 0; --' 15072020
MI_PORCENTAJESALUDDECRETO1122     NUMBER := 0;
MI_MSG      PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_PORCENTAJEMAX    			  NUMBER(20,2);
MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
BEGIN
            MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
            PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2',' ')) := 0 ;
            PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO SINDICATO SINTRAESTATALES',' ')) := 0;

            --AJUSTADO FEBRERO 21/2003  cedula 98145455
            PCK_NOMINA.CN(400) := 1;
            PCK_NOMINA.GL_PERVACS := 0;
            PCK_NOMINA.GL_DEVENGOSIBC := 0;
            --'26032021 DECRETO 2421 DE 2020
            PCK_NOMINA.GL_FECHAR    := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN  '' ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1 END ;
            PCK_NOMINA.GL_FECHAINI1 := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN  TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' ||  PCK_NOMINA.GL_SANO,'DD/MM/YYYY') ELSE  PCK_NOMINA.GL_FECHAINI END;
            PCK_NOMINA.GL_FECHAFIN1 := PCK_NOMINA.GL_FECHAFIN1;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL <> 0 AND PCK_NOMINA.FC_CN(1) = 0 THEN
               PCK_NOMINA.CN(1) := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL;
            END IF;
            IF PCK_NOMINA.FC_CN(1) = 0 THEN
               PCK_NOMINA.CN(1) := PCK_NOMINA.CCATEGORIA(1).SALARIO_BASE;
            END IF;
            IF PCK_NOMINA.FC_CN(1) = 0 THEN
              -- MsgBox "VerIFIque que los nIveles y categORÃ­as y sus respectIvos sueldos estÃ¡n actualIzados para el aÃ±o actual", vbCrItIcal, "Sysman Software"
              RETURN;
            END IF;
            IF PCK_NOMINA.FC_CN(9) = 0 THEN
               PCK_NOMINA.GL_FECHAI := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
               IF PCK_NOMINA.GL_FECHAI >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.GL_FECHAI <= PCK_NOMINA.GL_FECHAFIN1 THEN
                  PCK_NOMINA.CN(9) := 30 - PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAI) + 1;
               ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <= PCK_NOMINA.GL_FECHAFIN1 THEN
                  --'CUANDO SE RETIRA
                  PCK_NOMINA.CN(9) := (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1) - PCK_NOMINA.GL_FECHAINI + 1;
                  PCK_NOMINA.CN(9) := CASE WHEN PCK_NOMINA.FC_CN(9) < 0 THEN  0 ELSE PCK_NOMINA.FC_CN(9) END ;
                   PCK_NOMINA.GL_FECHAINI1 := PCK_NOMINA.GL_FECHAINI;
                  PCK_NOMINA.GL_FECHAFIN1  := PCK_NOMINA.GL_FECHAR;
               ELSIF PCK_NOMINA.FC_CN(9) = 0 THEN
                  PCK_NOMINA.CN(9) := PCK_NOMINA.FC_DIASPERIODO(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER);
                  IF PCK_NOMINA.GL_SMES = '02' AND PCK_NOMINA.FC_CN(9) < 30 THEN
                     PCK_NOMINA.CN(9) := 30;
                  END IF;
               END IF;
            END IF;
            PCK_NOMINA.CN(2) := CASE WHEN PCK_NOMINA.FC_CN(2) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * (PCK_NOMINA.FC_CN(9)), 0) ELSE PCK_NOMINA.FC_CN(2) END;-- ' Sueldo devengado
            IF PCK_NOMINA.FC_CN(451) <> 0 AND PCK_NOMINA.FC_CN(632) = 0 THEN
               PCK_NOMINA.CN(632) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * PCK_NOMINA.FC_CN(451) / 100, 0);
            END IF;
            IF PCK_NOMINA.FC_CN(452) <> 0 AND PCK_NOMINA.FC_CN(629) = 0 THEN --' ADEPEN
               PCK_NOMINA.CN(629) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * PCK_NOMINA.FC_CN(452) / 100, 0);
            END IF;
            IF PCK_NOMINA.FC_CN(453) <> 0 AND PCK_NOMINA.FC_CN(630) = 0 THEN --' ASPEMAT
               PCK_NOMINA.CN(630) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * PCK_NOMINA.FC_CN(453) / 100, 0);
            END IF;
            IF PCK_NOMINA.GL_SPER = 7 THEN --' en nomIna de mesadas retroactIvas el 07 , no debe calcularse el concepto 002
               PCK_NOMINA.CN(2) := 0.01;
            END IF;
            IF PCK_NOMINA.FC_CN(402) <> 0 THEN
               IF PCK_NOMINA.GL_SMES = 6 THEN
                  PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/01/' ||  PCK_NOMINA.GL_SANO,'DD/MM/YYYY'); --'CVDate(CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < CVDate("16/07/' ||  PCK_NOMINA.GL_SANO), '01/07/' || CStr(VAL( PCK_NOMINA.GL_SANO) - 1), '01/07/' || PCK_NOMINA.GL_SANO - 1))
                  PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN  PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPS END;
                  PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.GL_FECHAFIN1 ELSE  TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END ;
               ELSIF PCK_NOMINA.GL_SMES = '12' THEN
                  PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' ||  PCK_NOMINA.GL_SANO,'DD/MM/YYYY'); --'CVDate(CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < CVDate("16/07/' ||  PCK_NOMINA.GL_SANO), '01/07/' || CStr(VAL( PCK_NOMINA.GL_SANO) - 1), '01/07/' || PCK_NOMINA.GL_SANO - 1))
                  PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPS END;
                  PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.GL_FECHAFIN1 ELSE  TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
               END IF;
               PCK_NOMINA.CN(67) := 12; -- '05082015 VISITA
               PCK_NOMINA.GL_SEXTAS := 12; --' 05082015 VISITA  PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS)
               PCK_NOMINA.CN(158) := CASE WHEN PCK_NOMINA.FC_CN(158) = 0 THEN  PCK_NOMINA.FC_CN(1) ELSE  PCK_NOMINA.FC_CN(158) END;
               PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
               PCK_NOMINA.GL_DIASPRIMAJUNIO := PCK_NOMINA.FC_CN(67);
               IF PCK_NOMINA.GL_SPER = 4 THEN
                  FOR I IN 2 .. 599 LOOP
                    PCK_NOMINA.CN(I) := 0;
                  END LOOP;
               END IF;
               PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
               PCK_NOMINA.CN(67)  := PCK_NOMINA.GL_SEXTAS;-- ' PCK_NOMINA.GL_DIASPRIMAJUNIO
            END IF;
            IF PCK_NOMINA.FC_CN(401) <> 0 THEN
               
               PCK_NOMINA.CN(945) := PCK_NOMINA.FC_CN(1);
               PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/01/' ||  PCK_NOMINA.GL_SANO,'DD/MM/YYYY'); -- 'CVDate(CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < CVDate("16/07/' ||  PCK_NOMINA.GL_SANO), '01/07/' || CStr(VAL( PCK_NOMINA.GL_SANO) - 1), '01/07/' || PCK_NOMINA.GL_SANO - 1))
               PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN  PCK_NOMINA.GL_FECHAI ELSE  PCK_NOMINA.GL_FECHAIPS END ;
               IF PCK_NOMINA.GL_SMES = 6 THEN
                  PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.GL_FECHAFIN1 ELSE  TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END ;
               ELSIF PCK_NOMINA.GL_SMES = 12 THEN
                  PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.GL_FECHAFIN1 ELSE  TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END ;
               END IF;
               PCK_NOMINA.GL_SEXTAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
               PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  PCK_NOMINA.FC_CN(1) ELSE  PCK_NOMINA.FC_CN(160) END , 0) ELSE  PCK_NOMINA.FC_CN(160) END;
               IF PCK_NOMINA.FC_CN(160) > (15 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(160) := (15 * PCK_NOMINA.FC_CN(201));
                  --Al PensIonado --NOMEMPLEADO--, se ajustara el tope mÃƒÂ¡xImo de mesada a 15 SMLMV Decreto 692 de marzo 29 de 1994, CÃƒÂ©dula No. --CEDULA--, TIpo: --TIPO--.
                  MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                  MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                  MI_MSG(2).CLAVE := 'CEDULA';
                  MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                  MI_MSG(3).CLAVE := 'TIPO';
                  MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

                  PCK_NOMINA_COM7.PR_ALERTA
                      (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                      ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERPENSIONADOMESADA
                      ,UN_REEMPLAZOS   => MI_MSG
                      ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                      ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                      ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                      ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                      ,UN_USER         => PCK_CONEXION.FC_GETUSER
                      );
               END IF;
               PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
               PCK_NOMINA.GL_DIASPRIMAJUNIO := PCK_NOMINA.FC_CN(67);
               IF PCK_NOMINA.GL_SPER = 4 THEN
                  IF (MI_I <> 125) AND MI_I <> 303 AND MI_I <> 172 AND MI_I <> 160 AND MI_I <> 158 AND MI_I <> 300 AND MI_I <> 301 AND (MI_I < 599) OR (MI_I >= 600 AND MI_I <= 698) AND (PCK_NOMINA.FC_CN(MI_I) > 0 AND PCK_NOMINA.FC_CN(MI_I) < 1) THEN
                     PCK_NOMINA.CN(MI_I) := 0;
                  END IF;
               END IF;
               PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;
               PCK_NOMINA.CN(67)  := 30;
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <= PCK_NOMINA.GL_FECHAFIN1 THEN
                  --Alerta "El PensIonado con cÃ³dIgo: " & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', " & personal!APELLIDO1 || ' " & personal!APELLIDO2 || ' " & personal!NOMBRES || ', esta retIrado, no tendra pagos de prIma.. " || ', CÃ©dula No. " & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                  PCK_NOMINA.CN(160) := 0;
                  PCK_NOMINA.CN(67)  := 0;
                  FOR I IN 1 .. PCK_NOMINA.MAXI LOOP
                    PCK_NOMINA.CN(I) := 0;
                  END LOOP;
               END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER = 4 OR PCK_NOMINA.GL_SPER = 14 THEN
                FOR I IN 2 .. 699 LOOP
                  IF (I <> 125) AND I <> 303 AND I <> 172 AND I <> 160 AND I <> 158 AND I <> 300 AND I <> 301 AND (I < 599) OR (I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                     PCK_NOMINA.CN(I) := 0;
                  END IF;
                END LOOP;
            END IF;
           --' SUMA CONCEPTOS PARA EL IBC
            PCK_NOMINA_SEGSOCI.PR_SUMACONCEPTOSIBC(PCK_NOMINA.GL_COMPANIA) ;
            IF (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(84) + PCK_NOMINA.FC_CN(506)) <= PCK_PARENTR.PARAMETRO20 THEN --'140209
               PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30, 0), PCK_NOMINA.GL_RBASE1990);
               PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * PCK_NOMINA.FC_CN(9), 0), 0); -- '21102013
            ELSE
                PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30, PCK_NOMINA.GL_RBASE1990), 0);
            END IF;
            PCK_NOMINA.GL_IBL := PCK_NOMINA.FC_CN(112);
            
            IF PCK_NOMINA.GL_DEVENGOSIBC <> 0 THEN
               --'Devengos = PCK_NOMINA.FC_CN(2)
               IF (PCK_NOMINA.FC_CN(1) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0  THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END ) <= PCK_PARENTR.PARAMETRO20 THEN   --'140209
                  IF PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(84)), PCK_NOMINA.GL_RBASE1990) < PCK_PARENTR.PARAMETRO20 THEN --'140209
                     PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,084) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN PCK_NOMINA.FC_CN(506) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE 0 END , 0), 0) ;--   '21102013 -3
                     IF PCK_NOMINA.GL_DEVENGOSIBC < PCK_PARENTR.PARAMETRO20 THEN
                        IF PCK_NOMINA.GL_DEVENGOSIBC  MOD  1000 > 500 THEN --'011102013
                           PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE  0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE 0 END , PCK_NOMINA.GL_RBASE1990), 0) ;--   '21102013 -3
                           PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC := PCK_NOMINA.GL_DEVENGOSIBC;
                        END IF;
                     END IF;
                  ELSE
                     PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END  + PCK_NOMINA.FC_CN(506 + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN PCK_NOMINA.FC_CN(531) ELSE  0 END), PCK_NOMINA.GL_RBASE1990), 0);--   ' 12/09/06 casa
                  END IF;
               ELSE
                  IF (PCK_NOMINA.FC_CN(1) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END )  MOD  500 = 0 AND (PCK_NOMINA.FC_CN(1) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN PCK_NOMINA.FC_CN(506) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE 0 END) > PCK_NOMINA.FC_CN(201) THEN
                     PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) - 0.04) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE 0 END, PCK_NOMINA.GL_RBASE1990), 0); --  ' 12/09/06 casa
                  ELSE
                      IF PCK_NOMINA.FC_CN(2) < 1 AND PCK_NOMINA.GL_SPER <> 3 THEN --' 13102020
                            PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE 0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END, PCK_NOMINA.GL_RBASE1990), 0);--  ' 12/09/06 casa
                      ELSE
                            PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1)) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END, PCK_NOMINA.GL_RBASE1990), 0);--   ' 12/09/06 casa
                      END IF;
                  END IF;
               END IF;
               IF PCK_NOMINA.GL_DEVENGOSIBC < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990) AND PCK_PARENTR.PARAMETRO31 = '890701933-4' THEN  --'alcaldIa melgar oct 21/2003
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                     PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990);
                  END IF;
                  MI_MENOSMINIMO :=  TRUE;
               END IF;
               PCK_NOMINA.GL_DEVENGOS := PCK_NOMINA.FC_CN(112);
                  IF (PCK_NOMINA.FC_CN(1) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE  0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END) <= PCK_PARENTR.PARAMETRO20 THEN --'140209
                     PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DEVENGOSIBC, PCK_NOMINA.GL_RBASE1990);--'19092011
                     PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DEVENGOSIBC, 0);-- '21102013
                     IF PCK_NOMINA.GL_DEVENGOSIBC < PCK_PARENTR.PARAMETRO20 THEN
                        IF PCK_NOMINA.GL_DEVENGOSIBC  MOD  1000 > 500 THEN --'011102013
                           PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * (PCK_NOMINA.FC_CN(9)) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END  + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE  0 END, PCK_NOMINA.GL_RBASE1990), 0);--  '21102013 -3
                           PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DEVENGOSIBC, 0);-- '21102013
                        END IF;
                     END IF;
                  ELSE
                     PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DEVENGOSIBC, CASE WHEN (PCK_NOMINA.FC_CN(1) + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,84) <> 0 THEN  PCK_NOMINA.FC_CN(84) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,506) <> 0 THEN  PCK_NOMINA.FC_CN(506) ELSE 0 END + CASE WHEN PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA,531) <> 0 THEN  PCK_NOMINA.FC_CN(531) ELSE 0 END) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END);--  '19092011
                  END IF;
                  PCK_NOMINA.GL_IBL := PCK_NOMINA.FC_CN(112);
            ELSE
               PCK_NOMINA.CN(112) := PCK_NOMINA.FC_CN(2);
               PCK_NOMINA.GL_IBL  := PCK_NOMINA.FC_CN(112);
            END IF;
            --'Ajustado en JulIo 2003
            IF PCK_PARST.FC_PAR('MANEJAN AUTOLIQUIDACION POR DEBAJO DEL MINIMO',' ') = 'NO' THEN
              --IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOBENEFICIARIOPENSIONADO IN (1, 6) THEN  --
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_PENSIONADO = 'P' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_PENSIONADO = 'I' THEN
               IF PCK_NOMINA.GL_IBL < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990) AND CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN  PCK_NOMINA.GL_FECHAFIN1 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END  >= PCK_NOMINA.GL_FECHAFIN1 THEN
                  IF PCK_NOMINA.GL_SPER <> 4 THEN
                    --Alerta "OJO!, El ValOR de los ApORtes de SEGURIDAD SOCIAL seran ajustados al mInImo legal mensual, " & personal!APELLIDO1 || ' " & personal!NOMBRES || ' favOR revIsar bIen la novedad." || ', CÃ©dula No. " & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                    MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                    MI_MSG(2).CLAVE := 'CEDULA';
                    MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                    MI_MSG(3).CLAVE := 'TIPO';
                    MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
                    PCK_NOMINA_COM7.PR_ALERTA
                                            (  UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                                              ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERAPORSEGSOCMINNOV
                                              ,UN_REEMPLAZOS   => MI_MSG
                                              ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                                              ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                                              ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                                              ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                                              ,UN_USER         => PCK_CONEXION.FC_GETUSER
                                            );

                     PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990);  --' Ingreso base de cotIzacIÃ³n para apORtes
                  END IF;
                END IF;
              END IF;
            END IF;
            IF PCK_NOMINA.GL_IBL > TO_NUMBER(PCK_PARST.FC_PAR('LIMITE DE SMLMV PARA DESCUENTOS DE SEGURIDAD', '25')) * PCK_NOMINA.FC_CN(201) THEN
               PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(((TO_NUMBER(PCK_PARST.FC_PAR('LIMITE DE SMLMV PARA DESCUENTOS DE SEGURIDAD', '25')) * PCK_NOMINA.FC_CN(201))), 0);
               PCK_NOMINA.GL_IBL := PCK_NOMINA.FC_CN(112);
            ELSE
               PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL, 0);-- ' Ingreso base de cotIzacIÃ³n para apORtes
               PCK_NOMINA.GL_IBL := PCK_NOMINA.FC_CN(112);
            END IF;
            PCK_NOMINA.GL_IBLR := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL, 0);
            PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL, 0);
            PCK_NOMINA.GL_IBL := PCK_NOMINA.FC_CN(112);
            MI_PORCENTAJESALUDDECRETO1122 := TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE PCK_NOMINA.GL_SALUDDECRETO 1122','0'));
            
            MI_IBCDOBLEPENSION := 0; --' 15072020
            --MI_IBCDOBLEPENSION := PCK_NOMINA_COM5.FC_BUSCARSUMAIBCDOBLEPENSION(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, PCK_NOMINA.GL_FECHAINI);-- ' 15072020
            MI_IBCDOBLEPENSION := PCK_NOMINA_COM5.FC_BUSCARSUMAIBCDOBLEPENSION(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION );-- ' 15072020
            

            --JM INI 7743925  18/04/2024 
            BEGIN
                SELECT PORCENTAJE_MAX
                INTO MI_PORCENTAJEMAX
                FROM SALUD_PENSIONADOS
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = PCK_NOMINA.GL_SANO 
                AND LIMITE_INFERIOR <= CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 IS NOT NULL )THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL END
                AND LIMITE_SUPERIOR >= CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 IS NOT NULL )THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL END;
    
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_PORCENTAJEMAX :=0;
            END;  
            
            PCK_PARENTR.PARAMETRO43 := MI_PORCENTAJEMAX;
            MI_PORCENTAJESALUDDECRETO1122 := MI_PORCENTAJEMAX;
            
            IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION) <> (MI_PORCENTAJEMAX) THEN
    
                        MI_MSG(1).CLAVE := 'RIESGO_PENSION';
                        MI_MSG(1).VALOR :=  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION;    
                        MI_MSG(2).CLAVE := 'PORSALUDDECRETO1122';
                        MI_MSG(2).VALOR := MI_PORCENTAJEMAX;
                        MI_MSG(3).CLAVE := 'NOMEMPLEADO';
                        MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                        MI_MSG(4).CLAVE := 'CEDULA';
                        MI_MSG(4).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                        MI_MSG(5).CLAVE := 'TIPO';
                        MI_MSG(5).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
                        MI_MSG(6).CLAVE := 'MESADA';
                        MI_MSG(6).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421;
                        PCK_NOMINA_COM7.PR_ALERTA
                              (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                              ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PORDESCUENTOPERSMESADA
                              ,UN_REEMPLAZOS   => MI_MSG
                              ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                              ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                              ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                              ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                              ,UN_USER         => PCK_CONEXION.FC_GETUSER
                              );    
              END IF;
            --JM FIN 7743925  18/04/2024
            
            --'PCK_NOMINA.FC_CN(113) = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL * 0.03666, 0)  ' ApORtes PCK_NOMINA.GL_SALUDempleado
            PCK_NOMINA.GL_SALUD:= PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * 
            CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 
            THEN  12 ELSE  PCK_PARENTR.PARAMETRO43 END / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + 
            CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0
            THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  0 END  , 
            CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 
            THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END);--mod jm cc 2872 tiene 2 veces el redond 50 (50+50 =100)


            PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * 
            CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 
            THEN  14.5 ELSE  PCK_PARENTR.PARAMETRO39 END / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + 
            CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0
            THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  0 END , 
            CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0
            THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );

               IF PCK_NOMINA.GL_SALUD MOD  100 > 50 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD, PCK_NOMINA.GL_RAPORTES1990);
               ELSE
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD, 0);
               END IF;
            IF PCK_NOMINA.GL_DEVENGOSIBC < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990) AND PCK_PARENTR.PARAMETRO31 = '890701933-4' THEN  --'alcaldIa melgar oct 21/2003
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RBASE1990 END );
               END IF;
               MI_MENOSMINIMO := TRUE;
            END IF;
            IF PCK_NOMINA.GL_IBL <= PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN  --'CASO FALLECIDO  24/0102007
               IF PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END ); -- '13102020 tar 100927gnar
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );-- '13102020 tar 100927gnar
                  PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END ); -- '13102020 tar 100927gnar
                  PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END); -- '13102020 tar 100927gnar
               ELSE
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), PCK_NOMINA.GL_RBASE1990); --'13102020 tar 100927gnar
                  PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END ) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END); --'13102020 tar 100927gnar
               END IF;
               IF PCK_NOMINA.GL_DEVENGOSIBC = 0 OR PCK_NOMINA.GL_IBL = 0 THEN
                  PCK_NOMINA.GL_SUMARSS1990 := 0;
               END IF;
               PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_DEVENGOSIBC * 
               CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL 
                         OR PCK_PARENTR.PARAMETRO43 = 0 
                        THEN  12 ELSE  PCK_PARENTR.PARAMETRO43 
                END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + 
                CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 
                   AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0
                  THEN  0 ELSE 0 END  + 
                  CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 
                  THEN  0.5 
                  ELSE  0 
                  END , 
                  CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 
                  THEN  PCK_NOMINA.GL_RMINIMO1990 
                  ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_DEVENGOSIBC * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  13.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN  0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               IF PCK_NOMINA.GL_SALUD  MOD  100 > 50 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_SALUD, MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
               ELSE
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD+ CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE  0 END, 0);
               END IF;
               PCK_NOMINA.CN(113)  := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0);-- ' ApORtes salud
               
               --PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE PCK_PARENTR.PARAMETRO43 END  / 100 + PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0 THEN  0 ELSE  0 END + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE 0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END ); --' 19092011
               -- TICKET 7729863 ECABRERA: EN CASO QUE LA SALUD SEA MULTIPLO DE 100 NO SE REDONDEA PARA PENSIONADOS CON PENSION COMPARTIDA
               PCK_NOMINA.GL_SALUD := PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE PCK_PARENTR.PARAMETRO43 END  / 100;
               IF ( MOD(PCK_NOMINA.GL_SALUD,100) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0) THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD,CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               ELSE
               	  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE PCK_PARENTR.PARAMETRO43 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0 THEN  0 ELSE  0 END + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE 0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END ); --' 19092011
               END IF;
               -- TICKET 7729863 FIN -- 
               
               PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  13.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN  0.5 ELSE 0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               IF PCK_NOMINA.GL_SALUD  MOD  100 > 50 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_SALUD, MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0) ; --' ApORtes salud
               ELSE
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD, 0);
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0);-- ' ApORtes salud
               END IF;
            ELSIF PCK_NOMINA.GL_IBL < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990  ELSE  PCK_NOMINA.GL_RBASE1990 END) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <= PCK_NOMINA.GL_FECHAFIN1 THEN  --'CASO DE FALLCECIDOS Y SE LES PAGA 1 DIA
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC < PCK_PARENTR.PARAMETRO20 THEN
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0) / 30 * PCK_NOMINA.FC_CN(9), 0);
                  PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0) / 30 * PCK_NOMINA.FC_CN(9), 0);
               ELSE
                  PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0) / 30 * PCK_NOMINA.FC_CN(9), PCK_NOMINA.GL_RBASE1990); --' 121108
                  PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0) / 30 * PCK_NOMINA.FC_CN(9), PCK_NOMINA.GL_RBASE1990);
               END IF;
               --'121108PCK_NOMINA.GL_IBL = PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0) / 30 * PCK_NOMINA.FC_CN(9), 0)
               PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_DEVENGOSIBC * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE  PCK_PARENTR.PARAMETRO43 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA  <> 0 THEN  0 ELSE  0 END  + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_DEVENGOSIBC * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  13.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE  0 END  + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0); --' ApORtes salud
               PCK_NOMINA.GL_SALUD   := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE  PCK_PARENTR.PARAMETRO43 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE PCK_NOMINA.GL_RMINIMO1990 END , CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  13.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE 0 END , CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               PCK_NOMINA.GL_DEVENGOSIBC := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END ) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END ); -- '13102020 tar 100927gnar
               PCK_NOMINA.GL_IBL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END) / 30 * PCK_NOMINA.FC_CN(9), CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END ); --'13102020 tar 100927gnar
               PCK_NOMINA.CN(112) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL, CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE  PCK_NOMINA.GL_RBASE1990 END) ;
               PCK_NOMINA.GL_IBLR := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBL, CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RBASE1990 ELSE PCK_NOMINA.GL_RBASE1990 END);
               --'se adIcIono 131208 casa
               IF PCK_NOMINA.GL_IBLR <= PCK_PARENTR.PARAMETRO20 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE PCK_PARENTR.PARAMETRO43 END / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE 0 END, CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE PCK_NOMINA.GL_RAPORTES1990 END);
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0); -- ' ApORtes salud
               END IF;
            ELSIF PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20, PCK_NOMINA.GL_RBASE1990) = PCK_NOMINA.GL_IBLR AND (PCK_NOMINA.GL_SALUD< PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, 0)) THEN --'17022009
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), PCK_NOMINA.GL_RBASE1990) = PCK_PARENTR.PARAMETRO20 THEN  0 ELSE PCK_NOMINA.GL_RAPORTES1990 END );-- '19092011
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- ( PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0);--   ' ApORtes PCK_NOMINA.GL_SALUDPatrono
                  PCK_NOMINA.CN(130) := PCK_NOMINA.FC_CN(113);
            ELSE
               --PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD- PCK_NOMINA.FC_CN(114)) * 1 / 3, 0);-- ' ApORtes PCK_NOMINA.GL_SALUD'melgar ajuste 50% autORIzado pOR pensIondo, Ticket#7729286 16/03/2023 - se comenta linea debido a que ya no se calcula la tercera parte.
               PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD- PCK_NOMINA.FC_CN(114)), 0); -- Ticket#7729286 16/03/2023 - Se deja la linea con el calculo de la salud - el concepto 114. 
            END IF;
            IF PCK_PARENTR.PARAMETRO31 = '899999465-0' THEN --'PARA CAJICA, A LOS PENSIONADOS SE DESCUENTA DEPENDIENDO EL TIPO DE RIESGO
               PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.FC_CN(112) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
            ELSIF (NOT PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION <> '') AND PCK_NOMINA.GL_DEVENGOSIBC >= PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(201), PCK_NOMINA.GL_RBASE1990) THEN
               IF PCK_PARST.FC_PAR('CALCULAR PCK_NOMINA.GL_SALUDCON DECRETO 1122',' ') = 'SI' THEN
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE 0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
                  
                  IF PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20, PCK_NOMINA.GL_RBASE1990) = PCK_NOMINA.GL_IBLR AND (PCK_NOMINA.GL_SALUD< PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, 0)) THEN --'17022009
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), PCK_NOMINA.GL_RBASE1990) = PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );-- '08032011
                     PCK_NOMINA.CN(113)  := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- ( PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0);   --' ApORtes PCK_NOMINA.GL_SALUDPatrono
                     PCK_NOMINA.CN(130)  := PCK_NOMINA.FC_CN(113);
                  END IF;
               ELSE
                  PCK_NOMINA.CN(113)  := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).RIESGO_PENSION / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN PCK_PARENTR.PARAMETRO43 IS NULL OR PCK_PARENTR.PARAMETRO43 = 0 THEN  12 ELSE  PCK_PARENTR.PARAMETRO43 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END );
               END IF;
            END IF;
            PCK_NOMINA.CN(130) := PCK_NOMINA.FC_CN(113); --' ApORtes PCK_NOMINA.GL_SALUDempleado PORCENTAJE PCK_NOMINA.GL_SALUDDECRETO 1122
            --'FONDO DE SOLIDARIDAD PENSIONAL
            IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CLASE_PENSIONADO  = 'A' THEN
                IF PCK_PARST.FC_PAR('MANEJAN F.S.P. NORMAL',' ') = 'SI' THEN
                   IF PCK_NOMINA.FC_CN(132) = 0 THEN --'RESPETA EL VALOR MANUAL
                         IF PCK_NOMINA.GL_IBLR >= CASE WHEN PCK_PARENTR.PARAMETRO38 IS NULL OR PCK_PARENTR.PARAMETRO38 = 0 THEN  4 ELSE  PCK_PARENTR.PARAMETRO38 END  * PCK_NOMINA.FC_CN(201) THEN
                            PCK_NOMINA.CN(132) := CASE WHEN PCK_NOMINA.FC_CN(132) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_IBLR) * 0.01 + PCK_NOMINA.GL_SUMARSS1990 ,  PCK_NOMINA.GL_RAPORTES1990 ) ELSE  PCK_NOMINA.FC_CN(132) END ; -- ' 270516 PCK_NOMINA.GL_IBLD
                            PCK_NOMINA.CN(115) := PCK_NOMINA.FC_CN(132);
                         ELSE
                            PCK_NOMINA.CN(132) := 0;
                            PCK_NOMINA.CN(115) := 0;
                         END IF;
                   END IF;
                   --'ADICIONAL DEL FONDO DE SOLIDARIDAD PENSIONAL
                    IF PCK_NOMINA.GL_IBL > (10 * PCK_NOMINA.FC_CN(201)) THEN --'LEY 798/2003
                        IF PCK_NOMINA.GL_IBL >= (10 * PCK_NOMINA.FC_CN(201)) AND PCK_NOMINA.GL_IBL < (20 * PCK_NOMINA.FC_CN(201)) THEN
                           PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * 1 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                        ELSIF PCK_NOMINA.GL_IBL >= (20 * PCK_NOMINA.FC_CN(201)) THEN
                           PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * 2 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                        END IF;
                     END IF;
                ELSE
                     IF PCK_NOMINA.GL_IBLR > (10 * PCK_NOMINA.FC_CN(201)) THEN --'LEY 798/2003
                        IF (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(531)) >= (10 * PCK_NOMINA.FC_CN(201)) AND (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(531)) < (20 * PCK_NOMINA.FC_CN(201)) THEN
                           PCK_NOMINA.CN(115) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * 1 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);   --'PCK_NOMINA.FC_CN(2)
                           PCK_NOMINA.CN(132) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * 1 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);   --'PCK_NOMINA.FC_CN(2)
                        ELSIF (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(531)) >= (20 * PCK_NOMINA.FC_CN(201)) THEN
                           PCK_NOMINA.CN(115) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * 2 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990); --   'PCK_NOMINA.FC_CN(2)
                           PCK_NOMINA.CN(132) := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * 2 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                        END IF;
                     END IF;
               END IF;
            END IF;
            --'APORTE PATRONAL
            PCK_NOMINA.CN(116) := PCK_NOMINA.GL_SALUD- PCK_NOMINA.FC_CN(113);
    --'PARA DECRETO LEY 1122 DE 09 DE ENERO DE 2006
    IF PCK_PARST.FC_PAR('CALCULAR PCK_NOMINA.GL_SALUDCON DECRETO 1122',' ') = 'SI' THEN
       IF ((PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER  = 3 OR PCK_NOMINA.GL_SPER  = 5) OR (PCK_NOMINA.GL_SPER  = 12 OR PCK_NOMINA.GL_SPER  = 13)) OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <= PCK_NOMINA.GL_FECHAFIN1) THEN
          IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC <= PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20, 0) THEN --'140209
            --'ValIdANDo que no sea menOR al mInImo y es segunda quIncena PCK_NOMINA.GL_SALUD= PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * PCK_PARENTR.PARAMETRO43 / 100+ PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA, 0.5, 0), CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC = PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA, 0, PCK_NOMINA.GL_RAPORTES1990))
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC <= PCK_PARENTR.PARAMETRO20 THEN --'04022008
               --'PCK_NOMINA.GL_IBLR = PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR, PCK_NOMINA.GL_RBASE1990) --'19092011
               PCK_NOMINA.GL_IBLR := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR, PCK_NOMINA.GL_RBASE1990);   --'19092011
            ELSE
               PCK_NOMINA.GL_IBLR := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR, PCK_NOMINA.GL_RBASE1990);--
            END IF;
            PCK_NOMINA.GL_IBCCAUSANTE := PCK_NOMINA_PROC01.FC_BUSCAR_SUMA_IBCCAUSANTE(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTOCAUSANTE, PCK_NOMINA.GL_FECHAINI);
            IF PCK_NOMINA.GL_IBCCAUSANTE < PCK_PARENTR.PARAMETRO20 THEN
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR >= 1 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  0 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END ));
               ELSE
                  --'031208                  PCK_NOMINA.GL_SALUD= PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA, 0, 0) + 0.01, CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0, 0, PCK_NOMINA.GL_RAPORTES1990))
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE  0 END  + 0 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN  0 ELSE PCK_NOMINA.GL_RAPORTES1990 END );
               END IF;
               IF PCK_NOMINA.GL_IBLR = PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20, PCK_NOMINA.GL_RBASE1990) THEN --'26092011
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + 0.01, CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE PCK_NOMINA.GL_RAPORTES1990 END ); -- '19092011 PCK_NOMINA.GL_RAPORTES1990 centro '12012012
               END IF;
               IF PCK_NOMINA.GL_SALUD MOD  100 > 50 THEN
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_SALUD, MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0); --' ApORtes salud
               ELSE
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD, 0);
                  PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_SALUD), 0); --' ApORtes salud
               END IF;
            ELSE --' cuANDo la suma de los Ibc causante es mayOR al mInImo se redondea el apORte
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR >= 1 THEN
                  IF PCK_NOMINA.CN(9) < 30 THEN --' 17022012
                        PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, 0.01 + PCK_NOMINA.GL_SUMARSS1990, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  0 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END));
                        IF PCK_NOMINA.GL_SALUD MOD  100 > 50 THEN
                           PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, 0.01 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                        ELSE
                           PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, 0.01 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RMINIMO1990);
                        END IF;
                  ELSE
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, 0 + PCK_NOMINA.GL_SUMARSS1990, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END ));
                  END IF;
               ELSE
                  --'' se cambIo PCK_NOMINA.GL_SALUD= PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA, 0, 0) + 0.01+ PCK_NOMINA.GL_SUMARSS1990, CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC < PCK_PARENTR.PARAMETRO20 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0, PCK_NOMINA.GL_RAPORTES1990, PCK_NOMINA.GL_RAPORTES1990)) '19092011 PCK_NOMINA.GL_RAPORTES1990 centro
                  PCK_NOMINA.GL_SALUD   := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, 0.01 + PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 THEN  0.5 ELSE  0 END, PCK_NOMINA.GL_RMINIMO1990); -- '19092011 PCK_NOMINA.GL_RAPORTES1990 centro
                  IF PCK_NOMINA.GL_IBLR = PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20, PCK_NOMINA.GL_RBASE1990) THEN
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + 0.01, CASE WHEN PCK_NOMINA.FC_CN(1) <= PCK_PARENTR.PARAMETRO20 THEN  PCK_NOMINA.GL_RMINIMO1990 ELSE  PCK_NOMINA.GL_RAPORTES1990 END ); --  '19092011 PCK_NOMINA.GL_RAPORTES1990 centro
                  END IF;
                  IF PCK_NOMINA.GL_SALUD MOD  100 > 50 THEN
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_SALUD, MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);
                  ELSE
                     PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD, 0);
                  END IF;
               END IF;
            END IF;
            IF PCK_NOMINA.FC_CN(116) > 0 THEN
               PCK_NOMINA.CN(116) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- (PCK_NOMINA.FC_CN(113) +  PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0);  --' ApORtes PCK_NOMINA.GL_SALUDPatrono
               IF PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116) < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  0 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END)) THEN --'140209
                  PCK_NOMINA.GL_SALUD := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  0 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END));
                  PCK_NOMINA.CN(113)  := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- ( PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0);--   ' ApORtes PCK_NOMINA.GL_SALUDPatrono
                  PCK_NOMINA.CN(130) := PCK_NOMINA.FC_CN(113);
               END IF;
            ELSE
               PCK_NOMINA.CN(113) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- ( PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0); --  ' ApORtes PCK_NOMINA.GL_SALUDPatrono
               PCK_NOMINA.CN(130) := PCK_NOMINA.FC_CN(113);
               IF PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116) < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100, (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR = 1 THEN  0 ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REDONDEAR * -1 END)) THEN --'PCK_NOMINA.GL_IBLr Igual o superIOR al 1 SMLMV
                  PCK_NOMINA.GL_SALUD   := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBLR * MI_PORCENTAJESALUDDECRETO1122 / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990);-- '17022009
                  PCK_NOMINA.CN(113)    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SALUD- ( PCK_NOMINA.FC_CNP(113) + PCK_NOMINA.FC_CN(114) +  PCK_NOMINA.FC_CNP(114) + PCK_NOMINA.FC_CN(119) +  PCK_NOMINA.FC_CNP(119) +  PCK_NOMINA.FC_CNP(116)), 0);   --' ApORtes PCK_NOMINA.GL_SALUDPatrono
                  PCK_NOMINA.CN(130)    := PCK_NOMINA.FC_CN(113);
               END IF;
            END IF;
         END IF;
       END IF;
    END IF;
            --'
            IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CLASE_PENSIONADO  = 'J' THEN
                  PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_DEVENGOSIBC * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  13.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 + CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0.5 ELSE  0 END , CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE PCK_NOMINA.GL_RAPORTES1990 END );
                  PCK_NOMINA.CN(118) := PCK_NOMINA.GL_PENSION; --'CASE WHEN PCK_NOMINA.FC_CN(118) = 0, PCK_SYSMAN_UTL.FC_ROUND(PensIon * 1 / 4, 0), PCK_NOMINA.FC_CN(118)) ' ApORtes pensIÃ³n empleado
                  PCK_NOMINA.CN(131) := PCK_NOMINA.FC_CN(118);
                  PCK_NOMINA.GL_PENSION := PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.GL_IBL * CASE WHEN  PCK_PARENTR.PARAMETRO39 IS NULL OR  PCK_PARENTR.PARAMETRO39 = 0 THEN  14.5 ELSE   PCK_PARENTR.PARAMETRO39 END  / 100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990, CASE WHEN PCK_NOMINA.FC_CN(1) < PCK_PARENTR.PARAMETRO20 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PENSIONCOMPARTIDA <> 0 THEN   0 ELSE  PCK_NOMINA.GL_RAPORTES1990 END);
                  PCK_NOMINA.CN(117) := PCK_NOMINA.GL_PENSION - PCK_NOMINA.FC_CN(131);
            END IF;
            -- ini jm 7730819 28/04/2023
            IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0  OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO2 <> 0  OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3 <> 0 ) AND PCK_NOMINA.FC_CN(9) > 0 THEN
                   
                   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO > 0 THEN
                      PCK_NOMINA.CN(129) := CASE WHEN PCK_NOMINA.FC_CN(129) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100) * (1 + PCK_NOMINA.GL_PERVACS), 0) ELSE  PCK_NOMINA.FC_CN(129) END ;
                   ELSE
                      IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0 THEN
                        PCK_NOMINA.CN(129) := CASE WHEN PCK_NOMINA.FC_CN(129) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1)) * 1 / 100, 0) ELSE  PCK_NOMINA.FC_CN(129) END;
                      END IF;
                   END IF;

                   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO2 <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO2 > 0 THEN
                    PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2',' ')) := CASE WHEN PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2',' '))  = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO2 / 100) * (1 + PCK_NOMINA.GL_PERVACS), 0) ELSE  PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2',' ')) END ;
                   END IF;

                   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3 <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO3 > 0 THEN
                    PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO SINDICATO SINTRAESTATALES',' ')) := CASE WHEN PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO SINDICATO SINTRAESTATALES',' '))  = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO3 / 100) * (1 + PCK_NOMINA.GL_PERVACS), 0) ELSE  PCK_NOMINA.CN(PCK_PARST.FC_PAR('CODIGO CONCEPTO SINDICATO SINTRAESTATALES',' ')) END ;
                   END IF; 

            END IF;
            -- fin 7730819  28/04/2023
            --'TOTAL AJUSTES
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO = '02' THEN
               PCK_NOMINA.CN(629) := PCK_NOMINA.FC_CN(129);
               PCK_NOMINA.CN(129) := 0;
            END IF;
          --SUMA_NO_CONSTITUTIVO_RENTA = 0 --'18062020
          --'VALOR_APORTE_FONDO_SOLIDARIO_CODID19 = 0 '20042020
          --VALOR_APORTE_FONDO_SOLIDARIO_CODID19_OBLIGATORIO := 0;
          --VALOR_APORTE_FONDO_SOLIDARIO_CODID19_VOLUNTARIO = 0
          --''''13102020 tar 100927
        FOR I IN 1 .. PCK_NOMINA.MAXI LOOP
                IF PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1 THEN
                   IF I <> 303 AND I <> 109 THEN
                      PCK_NOMINA.CN(I) := 0;
                   END IF;
                END IF;
        END LOOP;

           
           
--'    ********  T O T A L   D E V E N G O S  **********
          --'guarda Ibc redondeado a mIl
          PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_SUMACON(47, 60) + PCK_NOMINA.FC_CN(61) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(72) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(74) + PCK_NOMINA.FC_CN(75) + PCK_NOMINA.FC_CN(76);
          PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(97) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(95) + PCK_NOMINA.FC_SUMACON(150, 160) + PCK_NOMINA.FC_SUMACON(169, 200) + PCK_NOMINA.FC_SUMACON(370, 378) + PCK_NOMINA.FC_SUMACON(500, 599) + PCK_NOMINA.FC_SUMACON(800, 899);
          PCK_NOMINA.CN(97) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97), 0);
        --IF PCK_PARST.FC_PAR('APLICA DESCUENTO FONDO SOLIDARIO COVID 19',' ') = 'SI' THEN --' 20042020MPV
        --    IF  PCK_NOMINA.GL_SANO = 2020 AND (PCK_NOMINA.GL_SMES  = 5 OR PCK_NOMINA.GL_SMES  = 6 OR PCK_NOMINA.GL_SMES  = 7) THEN
              --CALCULAR_APORTE_FONDO_SOLIDARIO_COVID19
        --     END IF;
        --END IF;
        --'DEVENGOS MENOS DESCUENTOS DE LEY
        PCK_NOMINA.CN(143) := PCK_NOMINA.FC_CN(97) - (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(114) + PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131));
        IF PCK_PARST.FC_PAR('DESCONTAR RETEFUENTE PENSIONADOS',' ') = 'SI' THEN  --' 18062020 SE CAMBIO DE POSICION
              PCK_NOMINA_PROC01.PR_CALCULARRETENCION;
        END IF;
        --Embargos:                                         '851563
IF PCK_NOMINA.GL_SPER <> '04' THEN --'25072012
   PCK_NOMINA.CN(143) := (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(186) + PCK_NOMINA.FC_CN(188) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(160)) - (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(114) + PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131));
ELSE
   --'PCK_NOMINA.FC_CN(143) = 0
   IF PCK_NOMINA.GL_SPER = '04' THEN --'NO SE DEBE SUMAR AL CONCEPTO 143
      PCK_NOMINA.CN(143) := (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(186) + PCK_NOMINA.FC_CN(188)) - (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(114) + PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131));
   ELSE
      PCK_NOMINA.CN(143) := (PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(186) + PCK_NOMINA.FC_CN(188) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(160)) - (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(114) + PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(131));
   END IF;
END IF;
           --' CalculANDo descuento para Embargos
           PCK_NOMINA.PR_DESCUENTAEMBARGOS(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER);
           IF PCK_NOMINA.FC_SUMACON(700, 798) = 0 AND (PCK_NOMINA.FC_CN(609) + PCK_NOMINA.FC_CN(611)) > 0 THEN --'20062012
              PCK_NOMINA.CN(609) := 0;
              PCK_NOMINA.CN(611) := 0;
           END IF;
           
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 BETWEEN (PCK_NOMINA.FC_CN(201) * 10) AND (PCK_NOMINA.FC_CN(201) * 20) THEN
            	-- TICKET 7729863 ECABRERA: EL CONCEPTO FSP(132) SE REDONDEA CON EL 0.5% MULTIPLICADO POR DOS, YA QUE SE REPORTA EN DOS COLUMNA REDONDEADAS INDIVIDUALMENTE
            	-- TICKET 7729863 ECABRERA 17/05/2023: SE CAMBIA EL REDONDEO YA QUE EL CONCEPTO 132 SE REPORTA EN UNA COLUMNA, ADICIONAL SE REDONDEA 
            	--			AL MULTIPLO SUPERIOR DE 100, TENIENDO EN CUENTA QUE SI EL VALOR ES MULTIPLO DE 100 NO DEBE REDONDEARSE
            	PCK_NOMINA.CN(132) := CASE WHEN MOD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL * 1/100,100) = 0 
                                        THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL * 1/100
                                        ELSE PCK_SYSMAN_UTL.FC_ROUND_100(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL * 1/100 , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990 ,-2)
                                      END;  
                --PCK_NOMINA.CN(132) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL * 0.5/100,-2) * 2;
                -- TICKET 7729863 FIN --
                PCK_NOMINA.CN(115) := PCK_NOMINA.FC_CN(132);
            ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).VR_MESPENSION2421 > ((PCK_NOMINA.FC_CN(201) * 20) + 1) THEN 
                PCK_NOMINA.CN(132) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).MESADA_PENSIONAL * 2/100,-2);
                PCK_NOMINA.CN(115) := PCK_NOMINA.FC_CN(132);
            ELSE
                PCK_NOMINA.CN(132) := 0;
                PCK_NOMINA.CN(115) := PCK_NOMINA.FC_CN(132);
            END IF;
            
            PCK_NOMINA.CN(362) := PCK_NOMINA.FC_SUMACON(500, 599) + PCK_NOMINA.FC_CN(169);
            PCK_NOMINA.CN(699) := PCK_NOMINA.FC_SUMACON(600, 698);
            PCK_NOMINA.CN(799) := PCK_NOMINA.FC_SUMACON(700, 798);

            PCK_NOMINA.CN(140) := PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(114) + PCK_NOMINA.FC_CN(131) + PCK_NOMINA.FC_CN(132) + PCK_NOMINA.FC_CN(120) + PCK_NOMINA.FC_CN(125) + PCK_NOMINA.FC_CN(129) + PCK_NOMINA.FC_CN(699) + PCK_NOMINA.FC_CN(799) + PCK_NOMINA.FC_CN(122) + PCK_NOMINA.FC_CN(123) + PCK_NOMINA.FC_CN(124) - PCK_NOMINA.FC_CN(613);
            

            --'Guarda la novedad de los fondos actuales
            --'Ingreso Base ParafIscales
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CAJA_COMPENSACION <> 'CCF99' AND PCK_NOMINA.GL_SPER  = 3 THEN --'AGOSTO/24/05
               PCK_NOMINA.CN(108) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(188) + PCK_NOMINA.FC_SUMACON(50, 60) + PCK_NOMINA.FC_CN(73) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(248) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(153) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(243) + PCK_NOMINA.FC_CN(244) + PCK_NOMINA.FC_CN(174) + PCK_NOMINA.FC_CN(175) + PCK_NOMINA.FC_SUMACON(370, 378) + PCK_NOMINA.FC_SUMACON(500, 599) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(170);
               PCK_NOMINA.CN(101) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(108) * 2 / 100, 0); --' ApORtes Para Caja de CompensacIon FamIlIar - CASE WHEN PCK_NOMINA.GL_SMES  = '06" AND PCK_NOMINA.GL_SPER = '04', AUXT / 12 * doceavas, 0)
               PCK_NOMINA.CN(601) := PCK_NOMINA.FC_CN(101);
               PCK_NOMINA.CN(140) := PCK_NOMINA.FC_CN(140) + PCK_NOMINA.FC_CN(601);
            --'PCK_NOMINA.FC_CN(102) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(108) - PCK_NOMINA.FC_CN(80)) * 3 / 100, 0)  ' ApORtes Para I.C.B.F.
            --'PCK_NOMINA.FC_CN(103) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(108) - PCK_NOMINA.FC_CN(80)) * 0.5 / 100, 0)  ' ApORtes Para Sena
            --'PCK_NOMINA.FC_CN(104) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(108) - PCK_NOMINA.FC_CN(80)) * 0.5 / 100, 0)  ' E.S.A.P.
            --'PCK_NOMINA.FC_CN(105) = PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(108) - PCK_NOMINA.FC_CN(80)) * 1 / 100, 0)  ' INSTITUTOS TECNICOS
            END IF;
            IF PCK_NOMINA.FC_CN(306) <> 0 THEN --'INDICADOR DE RETROACTIVO
               IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 2 AND PCK_NOMINA.GL_SPER  = 5 THEN
                   FOR I IN 1 .. PCK_NOMINA.MAXI LOOP
                      PCK_NOMINA.CN(I) := 0;
                   END LOOP;
               END IF;
            END IF;
            PCK_NOMINA.CN(144) := PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(140);
             IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SALUD= 'EPS99' AND PCK_NOMINA.FC_CN(130) > 0) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SALUD= 'EPS222' THEN  --'2804/2005
               PCK_NOMINA.CN(613) := PCK_NOMINA.FC_CN(130);
               PCK_NOMINA.CN(130) := 0;
               PCK_NOMINA.CN(116) := 0;
            END IF;
            IF PCK_NOMINA.FC_CN(144) <= 0 THEN
                --El Pensionado --NOMEMPLEADO-- se encuentra con saldo menor o igual a ceros, Revise la novedad, CÃ©dula No. --CEDULA--, Tipo: --TIPO--.
                  MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                  MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                  MI_MSG(2).CLAVE := 'CEDULA';
                  MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                  MI_MSG(3).CLAVE := 'TIPO';
                  MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

                  PCK_NOMINA_COM7.PR_ALERTA
                      (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                      ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERCN144MENORACERO
                      ,UN_REEMPLAZOS   => MI_MSG
                      ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                      ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                      ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                      ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                      ,UN_USER         => PCK_CONEXION.FC_GETUSER
                      );

            END IF;
            IF PCK_NOMINA.FC_CN(144) < PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(201) - PCK_NOMINA.FC_CN(113)) / 2, 0) THEN
               IF PCK_NOMINA.FC_CN(1) > PCK_NOMINA.FC_CN(201) THEN
                  --'18122013 Alerta "El PensIonado " & personal!APELLIDO1 || ' " & personal!APELLIDO2 || ' " & personal!NOMBRES || ', EL NETO NO ALCANZA A SER UN 1/2 SMLMV. REVICE. " || ' cÃ³dIgo: " & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ' neto =" & PCK_NOMINA.FC_CN(144) || ', 1/2 SMLMV=" & PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(201)) / 2, 0) || ', CÃ©dula No. " & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                  IF PCK_NOMINA.FC_CN(144) < PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) - PCK_NOMINA.FC_CN(113)) / 2, 0) THEN
                      --El Pensionado --NOMEMPLEADO-- El neto no alcanza a ser un smlmv, CÃ³digo: --CODIGO--, CÃ©dula No. --CEDULA--, Tipo: --TIPO--.
                      MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                      MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                      MI_MSG(2).CLAVE := 'CEDULA';
                      MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                      MI_MSG(3).CLAVE := 'TIPO';
                      MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

                      PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_NETOSMLMV
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );
                  END IF;
               ELSIF PCK_NOMINA.FC_CN(1) < PCK_NOMINA.FC_CN(201) THEN
                  --'18122013 IF PCK_NOMINA.FC_CN(144) < PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1)) / 2 - PCK_NOMINA.FC_CN(130), 0) THEN
                  IF PCK_NOMINA.FC_CN(144) < PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) - PCK_NOMINA.FC_CN(113)) / 2, 0) THEN
                     --El Pensionado --NOMEMPLEADO-- El neto no alcanza a ser un smlmv, CÃ³digo: --CODIGO--, CÃ©dula No. --CEDULA--, Tipo: --TIPO--.
                      MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                      MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                      MI_MSG(2).CLAVE := 'CEDULA';
                      MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                      MI_MSG(3).CLAVE := 'TIPO';
                      MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

                      PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_NETOSMLMV
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );
                  END IF;
               END IF;
            END IF;
            IF PCK_NOMINA.FC_CN(144) < ((PCK_NOMINA.FC_CN(1) - PCK_NOMINA.FC_CN(130) - PCK_NOMINA.FC_CN(613)) / 2) THEN
              -- ALER_TOPOENDEUDAMIENTO         CONSTANT PLS_INTEGER := 61000324;
              --El Pensionado --NOMEMPLEADO--,REVISAR DESCUENTOS.... TOPE ENDEUDAMIENTO --ENDEUDAMIENTO-- SS= --ENDEUDAMIENTOSS--  = --ENDEUDAMIENTOTRES-- neto = --NETO-- id del empleado --ID_DE_EMPLEADO--, CÃ©dula No. --CEDULA--
                      MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                      MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                      
                      MI_MSG(2).CLAVE := 'ENDEUDAMIENTO';
                      MI_MSG(2).VALOR := PCK_NOMINA.FC_CN(1);

                      MI_MSG(3).CLAVE := 'ENDEUDAMIENTOSS';
                      MI_MSG(3).VALOR := (PCK_NOMINA.FC_CN(130) + PCK_NOMINA.FC_CN(613));

                      MI_MSG(4).CLAVE := 'ENDEUDAMIENTOTRES';
                      MI_MSG(4).VALOR := (PCK_NOMINA.FC_CN(1) - PCK_NOMINA.FC_CN(130) - PCK_NOMINA.FC_CN(613)) / 2;
                      
                      MI_MSG(5).CLAVE := 'NETO';
                      MI_MSG(5).VALOR := PCK_NOMINA.FC_CN(144);

                      MI_MSG(6).CLAVE := 'ID_DE_EMPLEADO';
                      MI_MSG(6).VALOR :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;

                      MI_MSG(7).CLAVE := 'CEDULA';
                      MI_MSG(7).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                     
                      PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_TOPOENDEUDAMIENTO
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );

            END IF;
            --'23072012 IF personal!ID_De_Cargo  = '99002' THEN
            IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_PENSIONADO <> 'V' THEN
               IF SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 1, 2)  = '18' AND SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 4, 2)  = '00' THEN
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_VEN_CONSTANCIA >= PCK_NOMINA.GL_FECHAINI THEN
                    -- ALER_SUSTITUTOCUMPLIO18ANOS         CONSTANT PLS_INTEGER := 61000325;
                    --El Pensionado SUSTITUTO, --NOMEMPLEADO-- ID PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', CÃ©dula No. --CEDULA-- cumpliÃ³ 18 aÃ±os, pero tiene constancia con fecha de vencimiento mayor fecha inicial del periodo, se paga, pero deben revisar.
                    MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                    MI_MSG(2).CLAVE := 'ID_DE_EMPLEADO';
                    MI_MSG(2).VALOR :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                    MI_MSG(3).CLAVE := 'CEDULA';
                    MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                    PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_SUSTITUTOCUMPLIO18ANOS
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );
                  ELSIF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_VEN_CONSTANCIA IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_VEN_CONSTANCIA < PCK_NOMINA.GL_FECHAINI THEN
                    -- ALER_SUSTITUTOCUMPLIO18ANOS         CONSTANT PLS_INTEGER := 61000325;
                    --El Pensionado SUSTITUTO, --NOMEMPLEADO-- ID PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', CÃ©dula No. --CEDULA-- cumpliÃ³ 18 aÃ±os, pero tiene constancia con fecha de vencimiento mayor fecha inicial del periodo, se paga, pero deben revisar.
                    MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                    MI_MSG(2).CLAVE := 'ID_DE_EMPLEADO';
                    MI_MSG(2).VALOR :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                    MI_MSG(3).CLAVE := 'CEDULA';
                    MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                    PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_SUSTITUTOCUMPLIO18ANOS
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );


                    FOR I IN 1 .. PCK_NOMINA.MAXI LOOP
                         PCK_NOMINA.CN(I) := 0;
                    END LOOP;
                  END IF;
               END IF;
            END IF;
            --'23072012 GOBERNAR IF personal!ID_De_Cargo  = '99002' THEN
            IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPO_PENSIONADO <> 'V' THEN
               IF SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 1, 2)  = '26' AND (SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 4, 2)  = '00') THEN
                    --ALER_SUSTITUTOCUMPLIO26ANOS         CONSTANT PLS_INTEGER := 61000326;
                    --El Pensionado SUSTITUTO, --NOMEMPLEADO-- ID PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', CÃ©dula No. --CEDULA-- cumpliÃ³ 26 aÃ±os este mes, en el prÃ³ximo se anulan los pagos... revisar.
                    MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                    MI_MSG(2).CLAVE := 'ID_DE_EMPLEADO';
                    MI_MSG(2).VALOR :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                    MI_MSG(3).CLAVE := 'CEDULA';
                    MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                    PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_SUSTITUTOCUMPLIO26ANOS
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );             
               END IF;
               IF SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 1, 2)  = '26' AND (SUBSTR(PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHANCTO, PCK_NOMINA.GL_FECHAFIN1, 1), 4, 2) <> '00') THEN
                    --ALER_SUSTITUCUMPLO26ANOANULA         CONSTANT PLS_INTEGER := 61000327;
                    --El Pensionado SUSTITUTO, --NOMEMPLEADO-- ID PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', CÃ©dula No. --CEDULA-- cumpliÃ³ 26 aÃ±os, y no tiene discapacidad, se anulan los pagos... revisar
                    MI_MSG(1).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                    MI_MSG(2).CLAVE := 'ID_DE_EMPLEADO';
                    MI_MSG(2).VALOR :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                    MI_MSG(3).CLAVE := 'CEDULA';
                    MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                    PCK_NOMINA_COM7.PR_ALERTA
                          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                          ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_SUSTITUCUMPLO26ANOANULA
                          ,UN_REEMPLAZOS   => MI_MSG
                          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                          ,UN_USER         => PCK_CONEXION.FC_GETUSER
                          );  
                    FOR I IN 1 .. PCK_NOMINA.MAXI LOOP
                         PCK_NOMINA.CN(I) := 0;
                    END LOOP;
               END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER  = '03' OR PCK_NOMINA.GL_SPER  = '14' THEN
               FOR I IN 2 .. 799 LOOP
                  --''''IF ((I <> 125) AND I <> 2 AND I <> 303 AND I <> 172 AND I <> 109 AND I <> 300 AND I <> 301 AND (I < 599) OR (I >= 600 AND I <= 698)) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                  IF (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) AND I <> 303 AND I <> 109 THEN
                     PCK_NOMINA.CN(I) := 0;
                  END IF;
                END LOOP;
            END IF;
                      
PCK_NOMINA.CN(30) := TO_NUMBER(PCK_NOMINA.GL_SPER);
PCK_NOMINA.CN(4) := PCK_NOMINA.FC_DIASPERIODO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER);
PCK_NOMINA_COM2.PR_SINDICATO_TODOS;--TERMINAR1:
END PR_CALCULARPENSIONADOS_GNAR;

PROCEDURE PR_CALCULARCESANTIASGNAR(
    /*
    NAME              : PR_CALCULARCESANTIASGNAR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS 
    DATE MIGRADOR     : 11/05/2021
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS, En access = calcularcesantiasALCTOCANCIPA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  PR_CALCULARCESANTIASGNAR
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DIASCES_ENC          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROM_ENC             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTIC_ENC            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASPROMEDIO         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FECHA    DATE;
BEGIN

    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
    --(MZANGUNA:25/10/2018)-Se cambia GL_FECHAFIN a GL_FECHAINI
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
            END IF;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
            PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
        ELSE
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := 0;


        IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        ELSE
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > PCK_NOMINA.GL_FECHAIC AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE PCK_NOMINA.GL_FECHAIC END;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155) ;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        --(EAMAYA:09/09/2019)-Se cambia suma de concepto 70 por la sumatoria del rango de conceptos entre 49 y 60
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(49,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END;
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            ELSE
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(169) END;
            END IF;
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END;
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
            END IF;
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
            END IF;
        END IF;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

END PR_CALCULARCESANTIASGNAR;

PROCEDURE PR_ACTINDCUNEEMPLEADO(
    /*
    NAME              : PR_CALCULARCESANTIASGNAR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 06/09/2021
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Actualiza los indicadores de nómina eléctronica en personal
    @NAME:  PR_ACTINDCUNEEMPLEADO
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_OPCION     IN PCK_SUBTIPOS.TI_LOGICO
)AS
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_UPDATE              PCK_SUBTIPOS.TI_CAMPOS;

BEGIN   

    IF UN_OPCION NOT IN (0) THEN  
        MI_CAMPOS := 'GENERACUNE = -1 '; 
        MI_CONDICION := 'COMPANIA= '''|| UN_COMPANIA ||'''
                   AND   GENERACUNE IN (0)      ';
                                 
    ELSE
        MI_CAMPOS := 'GENERACUNE = 0 '; 
        MI_CONDICION := 'COMPANIA= '''|| UN_COMPANIA ||'''
                         AND   GENERACUNE NOT IN (0)      ';
    END IF;    

    BEGIN
        MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL'
        ,UN_ACCION    => 'M'
        ,UN_CAMPOS    => MI_CAMPOS
        ,UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

END PR_ACTINDCUNEEMPLEADO;

FUNCTION FC_PARLIMRET
(
/*
    NAME              : FC_PARLIMRET
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : EDWIN FERNANDO CABRERA MARTINEX
    DATE MIGRADOR     : 09/02/2023
    TIME              : 05:35 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : TICKET 7726449: FUNCION QUE RETORNA PARAMETRO DE LIMITES EN UVT POR AÑO PARA RENTA EXTERNA (25) Y 
                        Y PARA DEDUCCIONES Y RENTAS EXTERNAS (40)

    */
UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
UN_ANO          IN  NUMBER,
UN_PARAMETRO    IN  NUMBER
) RETURN NUMBER IS
    MI_LIM  ANO.LIM_25_RE%TYPE;
BEGIN 
    SELECT  DECODE(UN_PARAMETRO,40,LIM_40_DRE,25,LIM_25_RE,NULL)
    INTO    MI_LIM  
    FROM    ANO
    WHERE   COMPANIA = UN_COMPANIA 
            AND NUMERO = UN_ANO;
    
    RETURN (MI_LIM);
EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN MI_LIM;
END FC_PARLIMRET;


FUNCTION FC_MANTE_ELIM_CPTO_NOMINA
/*
    NAME              : FC_CN
    AUTHORS           : SYSMAN  SAS / JESUS MILLAN
    DATE              : 24/05/2023
    TIME              : 4:15 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  ejecuta un ajuste de saldo en los conceptos 97,140,144    
    @NAME:  updateCptoPeriodoNomina                   
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO              IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                  IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO              IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
  RETURN VARCHAR2
  AS
    MI_CAMPOS       VARCHAR(100);
    MI_CONDICION    VARCHAR(500);
    MI_RESPUESTA       VARCHAR2(32000);

BEGIN
     
    <<PARTEI>>
      FOR RS IN(SELECT HISTORICOS.COMPANIA,
                        HISTORICOS.ID_DE_PROCESO,
                        HISTORICOS.ANO,
                        HISTORICOS.MES,
                        HISTORICOS.PERIODO,
                        HISTORICOS.ID_DE_EMPLEADO,
                        SUM( CASE WHEN CONCEPTOS.CLASE IN(5) THEN HISTORICOS.VALOR ELSE 0 END) DESCUENTOS,
                        SUM(CASE WHEN CONCEPTOS.CLASE IN(6) THEN HISTORICOS.VALOR ELSE 0 END ) CON140 
                        FROM HISTORICOS 
                        INNER JOIN CONCEPTOS 
                        ON CONCEPTOS.COMPANIA = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO 
                        WHERE HISTORICOS.COMPANIA= UN_COMPANIA 
                        AND HISTORICOS.ANO= UN_ANIO 
                        AND HISTORICOS.MES= UN_MES 
                        AND HISTORICOS.PERIODO= UN_PERIODO 
                        AND HISTORICOS.ID_DE_PROCESO= UN_PROCESO 
                        AND CONCEPTOS.CLASE IN(5,6)  
                        GROUP BY HISTORICOS.COMPANIA,
                        HISTORICOS.ID_DE_PROCESO,
                        HISTORICOS.ANO,
                        HISTORICOS.MES,
                        HISTORICOS.PERIODO,
                        HISTORICOS.ID_DE_EMPLEADO)
        LOOP


            MI_CAMPOS := 'VALOR =  '|| RS.DESCUENTOS ||'';

            MI_CONDICION := 'HISTORICOS.COMPANIA       = '''         || UN_COMPANIA ||
                     ''' AND HISTORICOS.ID_DE_PROCESO = ' || RS.ID_DE_PROCESO || 
                     ' AND HISTORICOS.ANO = '             || UN_ANIO || 
                     ' AND HISTORICOS.MES = '             || UN_MES || 
                     ' AND HISTORICOS.PERIODO = '         || UN_PERIODO || 
                     ' AND HISTORICOS.ID_DE_EMPLEADO = '  || RS.ID_DE_EMPLEADO || 
                     ' AND HISTORICOS.ID_DE_CONCEPTO = 140 ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('HISTORICOS', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION ); 
            --MI_RESPUESTA := MI_RESPUESTA || RS.DESCUENTOS ||'--'|| RS.ID_DE_EMPLEADO||'|';
        END LOOP PARTEI;


         <<PARTEII>>
        FOR RS IN (SELECT HISTORICOS.COMPANIA,
                    HISTORICOS.ID_DE_PROCESO,
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    HISTORICOS.PERIODO,
                    HISTORICOS.ID_DE_EMPLEADO,
                    SUM( CASE WHEN CONCEPTOS.CLASE IN(3)THEN HISTORICOS.VALOR ELSE 0 END) DESCUENTOS,
                    SUM(CASE WHEN CONCEPTOS.CLASE IN(4) THEN HISTORICOS.VALOR ELSE 0 END )CON97  FROM HISTORICOS INNER JOIN CONCEPTOS  ON  CONCEPTOS.COMPANIA = HISTORICOS.COMPANIA  
                    AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO WHERE HISTORICOS.COMPANIA=UN_COMPANIA  
                    AND HISTORICOS.ANO=UN_ANIO  
                    AND HISTORICOS.MES=UN_MES  
                    AND HISTORICOS.PERIODO=UN_PERIODO  
                    AND HISTORICOS.ID_DE_PROCESO= UN_PROCESO 
                    AND CONCEPTOS.CLASE IN(3,4) 
                    GROUP BY HISTORICOS.COMPANIA,
                    HISTORICOS.ID_DE_PROCESO,
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    HISTORICOS.PERIODO,
                    HISTORICOS.ID_DE_EMPLEADO)
        LOOP


            MI_CAMPOS := 'VALOR =  '|| RS.DESCUENTOS ||'';

            MI_CONDICION := 'HISTORICOS.COMPANIA       = '''         || UN_COMPANIA ||
                     ''' AND HISTORICOS.ID_DE_PROCESO = ' || RS.ID_DE_PROCESO || 
                     ' AND HISTORICOS.ANO = '             || UN_ANIO || 
                     ' AND HISTORICOS.MES = '             || UN_MES || 
                     ' AND HISTORICOS.PERIODO = '         || UN_PERIODO || 
                     ' AND HISTORICOS.ID_DE_EMPLEADO = '  || RS.ID_DE_EMPLEADO || 
                     ' AND HISTORICOS.ID_DE_CONCEPTO = 97 ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('HISTORICOS', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION ); 
            --MI_RESPUESTA := MI_RESPUESTA || RS.DESCUENTOS ||'--'|| RS.ID_DE_EMPLEADO||'|';
        END LOOP PARTEII;

        <<PARTETRES>>
        FOR RS IN(SELECT COMPANIA,
                    ID_DE_PROCESO,
                    ANO,
                    MES,
                    PERIODO,
                    ID_DE_EMPLEADO,
                    NVL(DEVENGADO,0)-NVL(DESCUENTOS,0) NETO,
                    N144 FROM (SELECT HISTORICOS.COMPANIA,
                    HISTORICOS.ID_DE_PROCESO,
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    HISTORICOS.PERIODO,
                    HISTORICOS.ID_DE_EMPLEADO,
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(97)THEN HISTORICOS.VALOR END) DEVENGADO,
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(140) THEN HISTORICOS.VALOR END) DESCUENTOS,
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(144) THEN HISTORICOS.VALOR END) N144 
                    FROM HISTORICOS 
                    INNER JOIN CONCEPTOS ON  CONCEPTOS.COMPANIA = HISTORICOS.COMPANIA
                    AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    WHERE HISTORICOS.COMPANIA=UN_COMPANIA 
                    AND HISTORICOS.ANO=UN_ANIO 
                    AND HISTORICOS.MES=UN_MES 
                    AND HISTORICOS.PERIODO=UN_PERIODO
                    AND HISTORICOS.ID_DE_PROCESO= UN_PROCESO 
                    AND HISTORICOS.ID_DE_CONCEPTO IN(144,97,140)
                    GROUP BY HISTORICOS.COMPANIA,
                    HISTORICOS.ID_DE_PROCESO,
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    HISTORICOS.PERIODO,
                    HISTORICOS.ID_DE_EMPLEADO))
        LOOP
            MI_CAMPOS := 'VALOR =  '|| RS.NETO ||'';
            MI_CONDICION := 'HISTORICOS.COMPANIA       = '''         || UN_COMPANIA ||
                     ''' AND HISTORICOS.ID_DE_PROCESO = ' || RS.ID_DE_PROCESO || 
                     ' AND HISTORICOS.ANO = '             || UN_ANIO || 
                     ' AND HISTORICOS.MES = '             || UN_MES || 
                     ' AND HISTORICOS.PERIODO = '         || UN_PERIODO || 
                     ' AND HISTORICOS.ID_DE_EMPLEADO = '  || RS.ID_DE_EMPLEADO || 
                     ' AND HISTORICOS.ID_DE_CONCEPTO = 144 ';
                     
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('HISTORICOS', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION ); 
            --MI_RESPUESTA := MI_RESPUESTA || RS.NETO ||'--'|| RS.ID_DE_EMPLEADO||'|';
        END LOOP PARTETRES;


    RETURN MI_RESPUESTA;

END FC_MANTE_ELIM_CPTO_NOMINA;


FUNCTION FC_ELIMININAR_CPTO_NOMINA
/*
    NAME              : FC_CN
    AUTHORS           : SYSMAN  SAS / JESUS MILLAN
    DATE              : 24/05/2023
    TIME              : 4:15 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Recibe el codigo del concepto a eliminar en los historicos
    @NAME:  deleteCptoPeriodoNomina                     
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO              IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES                  IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO              IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDCONCEPTO           IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_ID_DE_EMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
  RETURN VARCHAR2
  AS
    MI_CAMPOS       VARCHAR(100);
    MI_RESPUESTA       VARCHAR2(32000);
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
     
    MI_CAMPOS := '';
     
    MI_RESPUESTA:= 'COMPANIA =            '''|| UN_COMPANIA || ''' 
                      AND ANO =             '|| UN_ANIO || '
                      AND ID_DE_PROCESO=    '|| UN_PROCESO || '
                      AND MES=              '|| UN_MES || '
                      AND PERIODO=              '|| UN_PERIODO || '
                      AND ID_DE_CONCEPTO=   '|| UN_IDCONCEPTO || '';
                      
    IF UN_ID_DE_EMPLEADO <> 0 THEN 
        MI_CAMPOS := 'AND ID_DE_EMPLEADO=   '|| UN_ID_DE_EMPLEADO || '';
    END IF;
   
            
    MI_CONDICION:=   'COMPANIA =            '''|| UN_COMPANIA || ''' 
                      AND ANO =             '|| UN_ANIO || '
                      AND ID_DE_PROCESO=    '|| UN_PROCESO || '
                      AND MES=              '|| UN_MES || '
                      AND PERIODO=              '|| UN_PERIODO || '
                      AND ID_DE_CONCEPTO=   '|| UN_IDCONCEPTO || MI_CAMPOS;
                      
                      
   BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'HISTORICOS', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
       MI_REEMPLAZOS(1).CLAVE := 'ANIO';
       MI_REEMPLAZOS(1).VALOR := UN_ANIO;
       MI_REEMPLAZOS(2).CLAVE := 'MES';
       MI_REEMPLAZOS(2).VALOR := UN_MES;
       RAISE PCK_EXCEPCIONES.EXC_NOMINA;
     END;



    RETURN MI_RESPUESTA;

END FC_ELIMININAR_CPTO_NOMINA;

PROCEDURE PR_DOS_IDEMPLEADO
/*
    NAME              : PR_DOS_IDEMPLEADO
    AUTHORS           : SYSMAN  SAS / CRISTIAN SUESCUN
    DATE              : 30/08/2024
    TIME              : 12:42 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  verifica si el empleado tiene mas de un ID, si tiene  verifica que el salario si SUPERA LOS 4 S.M.L.V para los conceptos 132-115
    @NAME:  deleteCptoPeriodoNomina                     
  */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,     -- Parámetro de entrada: Compañía
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,        -- Parámetro de entrada: Año
    UN_MES      IN PCK_SUBTIPOS.TI_MES,        -- Parámetro de entrada: Mes
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI, -- Parámetro de entrada: Período de nómina
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO 
) AS
    MI_VALOR                NUMBER := 0;         -- Variable para almacenar un valor numérico
    MI_VALOR2               NUMBER := 0;         -- Variable adicional para valores numéricos
    MI_VALOR_TOTAL          NUMBER := 0;         -- Variable para el valor total
    MI_DOCUMENTO            VARCHAR(32000);         -- Variable para el número de documento
    MI_ID                   NUMBER := 0;         -- Variable para el ID del empleado
    MI_ID2                  NUMBER := 0;         -- Variable para el ID del empleado n2
    MI_CAMPOS               VARCHAR2(32000);    -- Variable para campos de base de datos
    MI_VALORES              VARCHAR2(32000);    -- Variable para valores a insertar
    MI_CONDICION            VARCHAR2(32000);    -- Variable para condiciones de búsqueda
    MI_CN112                NUMBER := 0;         -- Variable para el valor del concepto 112
    MI_CN115                NUMBER := 0;         -- Variable para el valor del concepto 115
    MI_CN132                NUMBER := 0;         -- Variable para el valor del concepto 132
    MI_CN1_SEGID            NUMBER := 0;         -- Variable adicional para valores numéricos
    MI_PARAMETRO1990APLICA  PARAMETRO.VALOR%TYPE; -- Variable para parámetro de aplicación de redondeo
    MI_OBS                  VARCHAR(32000);      -- Variable para observaciones
    MI_BASESNOV             NUMBER := 0;         -- Variable para contar novedades en base
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR; -- Variable para mensajes clave-valor
    MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHAINI_PER DATE;
    MI_FECHAFIN_PER DATE;
    
    MI_CN112_1                NUMBER := 0;  
    MI_CN112_2                NUMBER := 0;
    MI_CN112_T                NUMBER := 0;
    MI_CN201_X4               NUMBER := 0;
BEGIN
    -- Obtener el parámetro de redondeo desde la tabla de parámetros
    MI_PARAMETRO1990APLICA := 'SI'; -- mod JM CC 2326 siempre debe ir redondeado --UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
    PCK_NOMINA.GL_SUMARSS1990 := TO_NUMBER(PCK_PARST.FC_PAR('SUMAR A SS DECRETO 1990', '0'));
    PCK_NOMINA.GL_RAPORTES1990 := TO_NUMBER(PCK_PARST.FC_PAR('DECIMALES REDONDEO APORTES DECRETO 1990','0'));
    --JM AQUI 
    MI_FECHAINI_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 1);
    MI_FECHAFIN_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 2);    
   
    PCK_NOMINA.GL_COMPANIA := UN_COMPANIA;
    PCK_NOMINA.GL_SANO := UN_ANO;
    PCK_NOMINA.GL_ANOACTUAL := UN_ANO;
    PCK_NOMINA.GL_SMES := UN_MES;
    PCK_NOMINA.GL_PROCESOACTUAL := UN_PROCESO;
    PCK_NOMINA.GL_PERIODOACTUAL := UN_PERIODO;
    
    PCK_NOMINA_COM7.PR_CARGAR_PARENTRADA(UN_COMPANIA => UN_COMPANIA);

    --BEGIN             
    FOR MI_RS IN ( SELECT ID_EMPLEADO1, ID_EMPLEADO2, NUMERO_DCTO
        --INTO MI_ID, MI_ID2,MI_DOCUMENTO
        FROM (
          SELECT e1.ID_DE_EMPLEADO AS ID_EMPLEADO1,
                 e2.ID_DE_EMPLEADO AS ID_EMPLEADO2,
                 e1.NUMERO_DCTO,
                 ROW_NUMBER() OVER (PARTITION BY e1.NUMERO_DCTO ORDER BY e1.ID_DE_EMPLEADO) AS rn
              FROM PERSONAL e1
              JOIN PERSONAL e2 ON e1.NUMERO_DCTO = e2.NUMERO_DCTO
              WHERE e1.ID_DE_EMPLEADO <> e2.ID_DE_EMPLEADO
              AND e1.COMPANIA = UN_COMPANIA
              AND e2.COMPANIA = UN_COMPANIA
              AND e1.ID_DE_EMPLEADO NOT IN(0)
              AND ((e1.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e1.ESTADO_ACTUAL     <> 3)
              OR (e1.FECHA_DE_RETIRO   >=TO_DATE(MI_FECHAINI_PER,'DD/MM/YYYY HH24:MI:SS')
              AND e1.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e1.ESTADO_ACTUAL      = 3)) 
              AND e1.ID_DE_EMPLEADO       BETWEEN TO_NUMBER('00000')  AND TO_NUMBER('99999')
              AND e2.ID_DE_EMPLEADO NOT IN(0)
              AND ((e2.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e2.ESTADO_ACTUAL     <> 3)
              OR (e2.FECHA_DE_RETIRO   >=TO_DATE(MI_FECHAINI_PER,'DD/MM/YYYY HH24:MI:SS')
              AND e2.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e2.ESTADO_ACTUAL      = 3)) 
              AND e2.ID_DE_EMPLEADO       BETWEEN TO_NUMBER('00000')  AND TO_NUMBER('99999') 
        )
        WHERE rn = 1
        ORDER BY NUMERO_DCTO
    )LOOP
     /*EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- Manejar el caso cuando no se encuentran datos
            MI_ID := 0;
            MI_ID2 :=0;
    END; */

    --JM FIN 
    MI_ID  := MI_RS.ID_EMPLEADO1;
    MI_ID2 := MI_RS.ID_EMPLEADO2;
    -- Verificar si el ID del empleado fue encontrado
    IF MI_ID <> 0 AND MI_ID2 <> 0 THEN 
        -- Calcular el acumulado del concepto 112 para el empleado
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
            PCK_NOMINA.GL_COMPANIA, 112, 
            PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
            1, PCK_NOMINA.GL_SANO, ---Periodo 1 
            PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
            MI_ID
        );
        MI_CN112_1 := PCK_NOMINA.FC_CNP(112);
        -- Calcular el acumulado del concepto 112 para el empleado n2
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
            PCK_NOMINA.GL_COMPANIA, 112, 
            PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
            1, PCK_NOMINA.GL_SANO, ---Periodo 1 
            PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
            MI_ID2
        );
        MI_CN112_2 := PCK_NOMINA.FC_CNP(112);
        
        -- Verificar salario minimo CN201
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
            PCK_NOMINA.GL_COMPANIA, 201, 
            PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
            3, PCK_NOMINA.GL_SANO, ---Periodo3 
            PCK_NOMINA.GL_SMES, 3, ---Periodo3 
            MI_ID
        );
        
        MI_CN201_X4 := (4 * PCK_NOMINA.FC_CNP(201));
        -- Verificar si el acumulado del empleado supera 4 veces el concepto 201
        IF (MI_CN112_1+MI_CN112_2) >  MI_CN201_X4 THEN
             -- Construir la condición de búsqueda para las novedades
            MI_CONDICION := ''||PCK_NOMINA.GL_COMPANIA||'--'||MI_ID||'--'||PCK_NOMINA.GL_SANO||'--'||PCK_NOMINA.GL_SMES||'';
        
            -- Asignar el valor del concepto 112 a la variable MI_CN112
            MI_CN112 := MI_CN112_1;
            -- Calcular el acumulado del concepto 115 para el empleado
            MI_CN132 := PCK_SYSMAN_UTL.FC_ROUND_100(
                    MI_CN112 * PCK_NOMINA.CPARENTRADA(1).PORC_FSP_AFP / 100,
                    MI_PARAMETRO1990APLICA,
                    PCK_NOMINA.GL_SUMARSS1990,
                    PCK_NOMINA.GL_RAPORTES1990
                );
            
           
            
                SELECT COUNT(*) INTO MI_BASESNOV
                FROM BASESNOVEDADES 
                WHERE LLAVENOVEDAD = MI_CONDICION;
                
                MI_CONDICION  :=' LLAVENOVEDAD= '''|| MI_CONDICION||'''';
                 IF MI_BASESNOV <> 0 THEN
                    MI_CAMPOS := 'FSP ='|| (MI_CN132/2) ||', FSPSUBSISTENCIA='|| (MI_CN132/2) ||'';
                    
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  'BASESNOVEDADES',
                                                               UN_ACCION    =>  'M',
                                                               UN_CAMPOS    =>  MI_CAMPOS,
                                                               UN_CONDICION =>  MI_CONDICION
                                                              
                                                               );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
                        END;
                    --BORRAR HISTORICO CON EL CON LOS CONCEPTOS 
                   -- COMPANIA = '003' AND ID_DE_EMPLEADO = 2386 AND MES = 4 AND ANO = 2024 AND PERIODO = 3 AND ID_DE_PROCESO = 1 AND ID_DE_CONCEPTO = 132
                    BEGIN 
                            MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                             AND ID_DE_EMPLEADO = '||MI_ID||'
                                             AND MES = '||PCK_NOMINA.GL_SMES||'
                                             AND ANO = '||PCK_NOMINA.GL_SANO||'
                                             AND PERIODO = '||UN_PERIODO||'
                                             AND ID_DE_PROCESO = '||UN_PROCESO||'
                                             AND ID_DE_CONCEPTO IN(115,132) ';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'HISTORICOS',
                                                        UN_ACCION    => 'E',
                                                        UN_CONDICION => MI_CONDICION);                
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                             RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                    END;
                    
                    
                    -- Crear una observación sobre la inserción de HISTORICOS    
                    MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID || ' PORQUE EL SALARIO BASE SUPERA LOS 4 S.M.L.V';
                    -- Asignar los nombres de las columnas para la tabla HISTORICOS
                    MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                    
                    MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID                        -- ID del empleado
                                    || ' , '
                                    || 132                          -- ID del concepto
                                    || ' , '
                                    || MI_CN132                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                    
                BEGIN
                    -- Intentar insertar los valores en la tabla HISTORICOS
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                         UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                         UN_ACCION  => 'I',            -- Acción de inserción
                                         UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                         UN_VALORES => MI_VALORES      -- Valores a insertar
                                        );
                EXCEPTION
                    -- Manejar excepción si ocurre un error de inserción
                    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                END;
                
                
                 -- Crear una observación sobre la inserción de HISTORICOS    
                    MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID || ' PORQUE EL SALARIO BASE SUPERA LOS 4 S.M.L.V';
                    -- Asignar los nombres de las columnas para la tabla HISTORICOS
                    MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';

                    MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_ANOACTUAL      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID                        -- ID del empleado
                                    || ' , '
                                    || 115                          -- ID del concepto
                                    || ' , '
                                    || MI_CN132                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                    
                BEGIN
                    -- Intentar insertar los valores en la tabla HISTORICOS
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                         UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                         UN_ACCION  => 'I',            -- Acción de inserción
                                         UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                         UN_VALORES => MI_VALORES      -- Valores a insertar
                                        );
                EXCEPTION
                    -- Manejar excepción si ocurre un error de inserción
                    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                END;
         END IF;  
                ---PARTE 2
                
            MI_CONDICION := ''||PCK_NOMINA.GL_COMPANIA||'--'||MI_ID2||'--'||PCK_NOMINA.GL_SANO||'--'||PCK_NOMINA.GL_SMES||'';
        
            -- Asignar el valor del concepto 112 a la variable MI_CN112
            MI_CN112 := MI_CN112_2;
            ---PARTE  primer id 
            -- Calcular el acumulado del concepto 115 para el empleado
            MI_CN132 := PCK_SYSMAN_UTL.FC_ROUND_100(
                    MI_CN112 * PCK_NOMINA.CPARENTRADA(1).PORC_FSP_AFP / 100,
                    MI_PARAMETRO1990APLICA,
                    PCK_NOMINA.GL_SUMARSS1990,
                    PCK_NOMINA.GL_RAPORTES1990
                );
            
           
            
                SELECT COUNT(*) INTO MI_BASESNOV
                FROM BASESNOVEDADES 
                WHERE LLAVENOVEDAD = MI_CONDICION;
                
                MI_CONDICION  :=' LLAVENOVEDAD= '''|| MI_CONDICION||'''';
                 IF MI_BASESNOV <> 0 THEN
                    MI_CAMPOS := 'FSP ='|| (MI_CN132/2) ||', FSPSUBSISTENCIA='|| (MI_CN132/2) ||'';
                    
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  'BASESNOVEDADES',
                                                               UN_ACCION    =>  'M',
                                                               UN_CAMPOS    =>  MI_CAMPOS,
                                                               UN_CONDICION =>  MI_CONDICION
                                                              
                                                               );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
                        END;
                    --BORRAR HISTORICO CON EL CON LOS CONCEPTOS 
                    BEGIN 
                            MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                             AND ID_DE_EMPLEADO = '||MI_ID2||'
                                             AND MES = '||PCK_NOMINA.GL_SMES||'
                                             AND ANO = '||PCK_NOMINA.GL_SANO||'
                                             AND PERIODO = '||UN_PERIODO||'
                                             AND ID_DE_PROCESO = '||UN_PROCESO||'
                                             AND ID_DE_CONCEPTO IN(115,132) ';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'HISTORICOS',
                                                        UN_ACCION    => 'E',
                                                        UN_CONDICION => MI_CONDICION);                
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                             RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                    END;
                    
                    
                    -- Crear una observación sobre la inserción de HISTORICOS    
                    MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID2 || ' PORQUE EL SALARIO BASE SUPERA LOS 4 S.M.L.V';
                    -- Asignar los nombres de las columnas para la tabla HISTORICOS
                    MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
    
                    MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_ANOACTUAL      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID2                        -- ID del empleado
                                    || ' , '
                                    || 132                          -- ID del concepto
                                    || ' , '
                                    || MI_CN132                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                    
                BEGIN
                    -- Intentar insertar los valores en la tabla HISTORICOS
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                         UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                         UN_ACCION  => 'I',            -- Acción de inserción
                                         UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                         UN_VALORES => MI_VALORES      -- Valores a insertar
                                        );
                EXCEPTION
                    -- Manejar excepción si ocurre un error de inserción
                    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                END;
                
                
                 -- Crear una observación sobre la inserción de HISTORICOS    
                    MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID2 || ' PORQUE EL SALARIO BASE SUPERA LOS 4 S.M.L.V';
                    -- Asignar los nombres de las columnas para la tabla HISTORICOS
                    MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
           
                    MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_ANOACTUAL      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID2                        -- ID del empleado
                                    || ' , '
                                    || 115                          -- ID del concepto
                                    || ' , '
                                    || MI_CN132                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                    
                BEGIN
                    -- Intentar insertar los valores en la tabla HISTORICOS
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                         UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                         UN_ACCION  => 'I',            -- Acción de inserción
                                         UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                         UN_VALORES => MI_VALORES      -- Valores a insertar
                                        );
                EXCEPTION
                    -- Manejar excepción si ocurre un error de inserción
                    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                END;
                                    
        END IF;    
    END IF; 
END IF;  
END LOOP;
  --CC 2326 para que tambien se tomen en cuenta los CN 97,140,144 despues de modificar los CN 115,132
 PCK_DATOS.GL_RTA := PCK_NOMINA_COM9.FC_MANTE_ELIM_CPTO_NOMINA(
    UN_COMPANIA             =>UN_COMPANIA,
    UN_PROCESO              =>UN_PROCESO,
    UN_ANIO                 =>UN_ANO,
    UN_MES                  =>UN_MES,
    UN_PERIODO              =>UN_PERIODO); 
        
                                
END PR_DOS_IDEMPLEADO;

PROCEDURE PR_CALPRIMAVACACINTERRMELGAR(
      /*
      NAME              : PR_CALPRIMAVACACINTERRMELGAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR 
      DATE MIGRADOR     : 08/08/2025
      TIME              :
      SOURCE MODULE     : NOMINA
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALPRIMAVACACIONESINTERMELGAR
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
BEGIN
   IF NOT (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
      
      PCK_NOMINA.GL_DIASVAC := 0;
      PCK_NOMINA.GL_DIASPENDIENTES := 0;
      PCK_NOMINA.GL_PENDIENTES := 0;
      PCK_NOMINA.GL_LICENCIAS := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
      --' Para personal que se retira
      IF NOT PCK_NOMINA.FC_CN(404) <> 0 THEN
      --ELSE
         --'Vacaciones NORmales
         --'acumulado para dias pensientes de vacaicones
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.CNA(91);
         PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);    --' dias de prima pactados para Prima de Vacaciones
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulado del ultimo aÃ±o
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --EMPLEADOS OFICIALES
            PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.FC_CN(1); --' factORes prima de vacaciones
         ELSE
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.CNA(160) / 12) + CASE WHEN PCK_NOMINA.GL_SMES <> 9 THEN  (PCK_NOMINA.CNA(150) / 12) + (PCK_NOMINA.FC_CN(150) / 12) ELSE  CASE WHEN PCK_NOMINA.CNA(150) = 0 THEN  PCK_NOMINA.FC_CN(150) ELSE PCK_NOMINA.CNA(150) END / 12 END , 0); -- ' factORes prima de vacaciones
            PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA, 0), 0); --' factORes prima de vacaciones
         END IF;
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN --'dIFerente de salario integral
            IF PCK_NOMINA.FC_CN(99) <> 0 THEN      --' Salario de Vacaciones y prima de vaciones
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                         --' Vacaciones en Tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN      --' Vacaciones en dinero
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;

         ELSE --' salario Integral no tiene prima de vacaciones
            IF PCK_NOMINA.FC_CN(403) <> 0 THEN
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                        --' Vacaciones en tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;
         END IF;
      END IF;
   END IF;
END PR_CALPRIMAVACACINTERRMELGAR;

PROCEDURE PR_ACT_ENC_PERS_HIST
/*
    NAME              : PR_ACT_ENC_PERS_HIST
    AUTHORS           : SYSMAN  SAS, JM CC 
    TIME              :
    DESCRIPTION       : ACTUALIZA EL PERSONAL HISTORICOS CON LOS ENCARGOS ENCONTRADOS 
    @NAME             : 
    @METHOD           : verificarcarencargospersonalhistorico
  */
  (
    UN_MES         IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIO IN DATE DEFAULT SYSDATE,
    UN_FECHAFIN    IN DATE DEFAULT SYSDATE,
    UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO DEFAULT 0
  )
  AS
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_FECHAFIN       DATE;
    MI_FECHAINI       DATE;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_SUBCONDICION_1 PCK_SUBTIPOS.TI_CONDICION;
    MI_SUBCONDICION_2 PCK_SUBTIPOS.TI_CONDICION;
BEGIN

    IF UN_ID_EMPLEADO <> 0 THEN
        MI_SUBCONDICION_1 := UN_ID_EMPLEADO;
        MI_SUBCONDICION_2 := UN_ID_EMPLEADO;
        MI_FECHAFIN := LAST_DAY(UN_FECHAFIN);
        MI_FECHAINI := TO_DATE('01/' || TO_CHAR(UN_FECHAFIN,'MM/YYYY'),'DD/MM/YYYY');
    ELSE
        MI_SUBCONDICION_1 := '0';
        MI_SUBCONDICION_2 := '99999';
        MI_FECHAFIN := UN_FECHAFIN;
        MI_FECHAINI := UN_FECHAINICIO;
    END IF;

    IF UN_MES IS NULL THEN 
        MI_FECHAFIN := TO_DATE('31/12' || UN_ANO,'DD/MM/YYYY');
        MI_FECHAINI := TO_DATE('01/01' || UN_ANO,'DD/MM/YYYY');
    END IF;
    
    FOR RS IN (SELECT PERSONAL_HISTORICO.ID_DE_EMPLEADO,PERSONAL_HISTORICO.ANO,
                        PERSONAL_HISTORICO.MES,PERSONAL_HISTORICO.PERIODO,
                     PERSONAL_HISTORICO.ID_DE_PROCESO, MAX(ENCARGOMES.ID_DE_CARGO) AS ID_ENCARGO
                     FROM PERSONAL_HISTORICO
                     LEFT JOIN (SELECT * FROM
                     ENCARGOS
                     WHERE ENCARGOS.COMPANIA       = UN_COMPANIA
                     AND ENCARGOS.FECHAINICIO      <= MI_FECHAFIN
                     AND ENCARGOS.FECHAFINAL       >= MI_FECHAINI) ENCARGOMES
                     ON  ENCARGOMES.COMPANIA       = PERSONAL_HISTORICO.COMPANIA
                     AND ENCARGOMES.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
                     AND PERSONAL_HISTORICO.ANO||LPAD(PERSONAL_HISTORICO.MES,2,0) BETWEEN TO_CHAR(ENCARGOMES.FECHAINICIO ,'YYYYMM') AND  TO_CHAR(ENCARGOMES.FECHAFINAL ,'YYYYMM')
                     WHERE PERSONAL_HISTORICO.COMPANIA = UN_COMPANIA
                     AND PERSONAL_HISTORICO.ID_DE_EMPLEADO BETWEEN MI_SUBCONDICION_1 AND MI_SUBCONDICION_2
                     AND PERSONAL_HISTORICO.ANO = UN_ANO
                     AND ENCARGOMES.ID_DE_CARGO IS NOT NULL
                     AND (PERSONAL_HISTORICO.MES = UN_MES OR UN_MES IS NULL)
                     AND (PERSONAL_HISTORICO.PERIODO = UN_PERIODO OR UN_PERIODO IS NULL)
                     AND (PERSONAL_HISTORICO.ID_DE_PROCESO = UN_PROCESO OR UN_PROCESO IS NULL)
                     AND ENCARGOMES.ID_DE_CARGO IS NOT NULL
                     GROUP BY PERSONAL_HISTORICO.ID_DE_EMPLEADO, PERSONAL_HISTORICO.ANO, PERSONAL_HISTORICO.MES, PERSONAL_HISTORICO.PERIODO, PERSONAL_HISTORICO.ID_DE_PROCESO)
    LOOP <<ACTUALIZA_PH>>
    MI_CAMPOS    :='CARGO_ENCARGO = ''' || RS.ID_ENCARGO || '''';
    MI_CONDICION :='COMPANIA       =''' || UN_COMPANIA || ''' 
                    AND ANO =' || RS.ANO  || '
                    AND MES =' || RS.MES  || '
                    AND PERIODO =' || RS.PERIODO  || '
                    AND ID_DE_PROCESO  =' || RS.ID_DE_PROCESO || '
                    AND ID_DE_EMPLEADO  ='   || RS.ID_DE_EMPLEADO;
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => 'PERSONAL_HISTORICO'
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION );
   END LOOP ACTUALIZA_PH;

END PR_ACT_ENC_PERS_HIST;

PROCEDURE PR_REVISAR_PARAFISCALES
/*
    NAME              : PR_REVISAR_PARAFISCALES
    AUTHORS           : SYSMAN  SAS / MARIA ALEJANDRA PEREZ SALAZAR
    DATE              : 16/03/2026
    TIME              : 02:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : verifica si el empleado tiene mas de un ID, si tiene  verifica que el salario si SUPERA LOS 4 S.M.L.V para el concepto 116
    @NAME             : PR_REVISAR_PARAFISCALES                      
  */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,     -- Parámetro de entrada: Compañía
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,        -- Parámetro de entrada: Año
    UN_MES      IN PCK_SUBTIPOS.TI_MES,        -- Parámetro de entrada: Mes
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI, -- Parámetro de entrada: Período de nómina
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO 
) AS    
    MI_ID                   NUMBER := 0;         -- Variable para el ID del empleado
    MI_ID2                  NUMBER := 0;         -- Variable para el ID del empleado n2
    MI_CAMPOS               VARCHAR2(32000);    -- Variable para campos de base de datos
    MI_VALORES              VARCHAR2(32000);    -- Variable para valores a insertar
    MI_CONDICION            VARCHAR2(32000);    -- Variable para condiciones de búsqueda
    MI_CN112                NUMBER := 0;         -- Variable para el valor del concepto 112
    MI_CN116                NUMBER := 0;         -- Variable para el valor del concepto 116
    MI_PARAMETRO1990APLICA  PARAMETRO.VALOR%TYPE; -- Variable para parámetro de aplicación de redondeo
    MI_OBS                  VARCHAR(32000);      -- Variable para observaciones
    MI_BASESNOV             NUMBER := 0;         -- Variable para contar novedades en base
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR; -- Variable para mensajes clave-valor
    MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHAINI_PER DATE;
    MI_FECHAFIN_PER DATE;
    
    MI_CN97_1                NUMBER := 0;  
    MI_CN97_2                NUMBER := 0;
    MI_CN102_1               NUMBER := 0;  
    MI_CN102_2               NUMBER := 0;
    MI_CNP102_1              NUMBER := 0;  
    MI_CNP102_2              NUMBER := 0;
    MI_CN103_1               NUMBER := 0;  
    MI_CN103_2               NUMBER := 0;
    MI_CNP103_1              NUMBER := 0;  
    MI_CNP103_2              NUMBER := 0;
    MI_CN108_1               NUMBER := 0;  
    MI_CN108_2               NUMBER := 0;
    MI_CN80_1                NUMBER := 0;  
    MI_CN80_2                NUMBER := 0;
    MI_CN525_1               NUMBER := 0;  
    MI_CN525_2               NUMBER := 0;
    MI_CN533_1               NUMBER := 0;  
    MI_CN533_2               NUMBER := 0;
    MI_CN112_1               NUMBER := 0;  
    MI_CN112_2               NUMBER := 0;
    MI_CN201_X10             NUMBER := 0;
BEGIN
    -- Obtener el parámetro de redondeo desde la tabla de parámetros
    MI_PARAMETRO1990APLICA := 'SI'; 
    PCK_NOMINA.GL_SUMARSS1990 := TO_NUMBER(PCK_PARST.FC_PAR('SUMAR A SS DECRETO 1990', '0'));
    PCK_NOMINA.GL_RAPORTES1990 := TO_NUMBER(PCK_PARST.FC_PAR('DECIMALES REDONDEO APORTES DECRETO 1990','0'));
     
    MI_FECHAINI_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 1);
    MI_FECHAFIN_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 2);    
   
    PCK_NOMINA.GL_COMPANIA := UN_COMPANIA;
    PCK_NOMINA.GL_SANO := UN_ANO;
    PCK_NOMINA.GL_ANOACTUAL := UN_ANO;
    PCK_NOMINA.GL_SMES := UN_MES;
    PCK_NOMINA.GL_PROCESOACTUAL := UN_PROCESO;
    PCK_NOMINA.GL_PERIODOACTUAL := UN_PERIODO;
    
    PCK_NOMINA_COM7.PR_CARGAR_PARENTRADA(UN_COMPANIA => UN_COMPANIA);
    PCK_PARENTR.PR_CARGAR_PARAMETROSENTRADA(UN_COMPANIA => UN_COMPANIA);
    PCK_PARST.PR_INICIALIZAR_PARSISTEMA (UN_COMPANIA => UN_COMPANIA, UN_MODULO => PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR=> SYSDATE);
    
    IF PCK_PARST.FC_PAR('APLICA EXONERACION EN BASE TOTAL INGRESOS 097', '0') = 'SI'  AND PCK_PARENTR.PARAMETRO70 = 'S' THEN
            FOR MI_RS IN ( SELECT ID_EMPLEADO1, ID_EMPLEADO2, NUMERO_DCTO
                FROM (
                  SELECT e1.ID_DE_EMPLEADO AS ID_EMPLEADO1,
                         e2.ID_DE_EMPLEADO AS ID_EMPLEADO2,
                         e1.NUMERO_DCTO,
                         ROW_NUMBER() OVER (PARTITION BY e1.NUMERO_DCTO ORDER BY e1.ID_DE_EMPLEADO) AS rn
                      FROM PERSONAL e1
                      JOIN PERSONAL e2 ON e1.NUMERO_DCTO = e2.NUMERO_DCTO
                      WHERE e1.ID_DE_EMPLEADO <> e2.ID_DE_EMPLEADO
                      AND e1.COMPANIA = UN_COMPANIA
                      AND e2.COMPANIA = UN_COMPANIA
                      AND e1.ID_DE_EMPLEADO NOT IN(0)
                      AND ((e1.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e1.ESTADO_ACTUAL     <> 3)
                      OR (e1.FECHA_DE_RETIRO   >=TO_DATE(MI_FECHAINI_PER,'DD/MM/YYYY HH24:MI:SS')
                      AND e1.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e1.ESTADO_ACTUAL      = 3)) 
                      AND e1.ID_DE_EMPLEADO       BETWEEN TO_NUMBER('00000')  AND TO_NUMBER('99999')
                      AND e2.ID_DE_EMPLEADO NOT IN(0)
                      AND ((e2.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e2.ESTADO_ACTUAL     <> 3)
                      OR (e2.FECHA_DE_RETIRO   >=TO_DATE(MI_FECHAINI_PER,'DD/MM/YYYY HH24:MI:SS')
                      AND e2.FECHA_DE_INGRESO  <=TO_DATE(MI_FECHAFIN_PER,'DD/MM/YYYY HH24:MI:SS')    AND e2.ESTADO_ACTUAL      = 3)) 
                      AND e2.ID_DE_EMPLEADO       BETWEEN TO_NUMBER('00000')  AND TO_NUMBER('99999') 
                )
                WHERE rn = 1
                AND NUMERO_DCTO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                ORDER BY NUMERO_DCTO
            )LOOP
            
            MI_ID  := MI_RS.ID_EMPLEADO1;
            MI_ID2 := MI_RS.ID_EMPLEADO2;
            -- Verificar si el ID del empleado fue encontrado
            IF MI_ID <> 0 AND MI_ID2 <> 0 THEN 
                -- Calcular el acumulado del concepto 97 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 97, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN97_1 := PCK_NOMINA.FC_CNP(97);
                -- Calcular el acumulado del concepto 97 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 97, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN97_2 := PCK_NOMINA.FC_CNP(97);

                -- Calcular el acumulado del concepto 102 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 102, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CNP102_1 := PCK_NOMINA.FC_CNP(102);
                -- Calcular el acumulado del concepto 102 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 102, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CNP102_2 := PCK_NOMINA.FC_CNP(102); 

                -- Calcular el acumulado del concepto 103 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 103, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CNP103_1 := PCK_NOMINA.FC_CNP(103);
                -- Calcular el acumulado del concepto 103 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 103, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CNP103_2 := PCK_NOMINA.FC_CNP(103); 

                -- Calcular el acumulado del concepto 108 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 108, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN108_1 := PCK_NOMINA.FC_CNP(108);
                -- Calcular el acumulado del concepto 108 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 108, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN108_2 := PCK_NOMINA.FC_CNP(108);

                -- Calcular el acumulado del concepto 80 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 80, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN80_1 := PCK_NOMINA.FC_CNP(80);
                -- Calcular el acumulado del concepto 80 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 80, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN80_2 := PCK_NOMINA.FC_CNP(80); 

                -- Calcular el acumulado del concepto 525 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 525, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN525_1 := PCK_NOMINA.FC_CNP(525);
                -- Calcular el acumulado del concepto 525 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 525, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN525_2 := PCK_NOMINA.FC_CNP(525);

                -- Calcular el acumulado del concepto 533 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 533, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN533_1 := PCK_NOMINA.FC_CNP(533);
                -- Calcular el acumulado del concepto 533 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 533, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN533_2 := PCK_NOMINA.FC_CNP(533);

                -- Calcular el acumulado del concepto 112 para el empleado
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 112, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID
                );
                MI_CN112_1 := PCK_NOMINA.FC_CNP(112);
                -- Calcular el acumulado del concepto 112 para el empleado n2
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 112, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    1, PCK_NOMINA.GL_SANO, ---Periodo 1 
                    PCK_NOMINA.GL_SMES, 99, --- Hasta el Periodo 99
                    MI_ID2
                );
                MI_CN112_2 := PCK_NOMINA.FC_CNP(112); 

                -- Verificar salario minimo CN201
                PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(
                    PCK_NOMINA.GL_COMPANIA, 201, 
                    PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 
                    3, PCK_NOMINA.GL_SANO, ---Periodo3 
                    PCK_NOMINA.GL_SMES, 3, ---Periodo3 
                    MI_ID
                );
                
                MI_CN201_X10 := (10 * PCK_NOMINA.FC_CNP(201));
                -- Verificar si el acumulado del empleado supera 10 veces el concepto 201
                IF (MI_CN97_1+MI_CN97_2) >  MI_CN201_X10 THEN
                     -- Construir la condición de búsqueda para las novedades
                    MI_CONDICION := ''||PCK_NOMINA.GL_COMPANIA||'--'||MI_ID||'--'||PCK_NOMINA.GL_SANO||'--'||PCK_NOMINA.GL_SMES||'';
                
                    -- Asignar el valor del concepto 112 a la variable MI_CN112
                    MI_CN112 := MI_CN112_1;
                    -- Calcular el acumulado del concepto 115 para el empleado
                    MI_CN116 := PCK_SYSMAN_UTL.FC_ROUND_100(
                            MI_CN112 * PCK_NOMINA.CPARENTRADA(1).PORC_PATRON_EPS / 100,
                            MI_PARAMETRO1990APLICA,
                            PCK_NOMINA.GL_SUMARSS1990,
                            PCK_NOMINA.GL_RAPORTES1990
                        );                     
                    
                    SELECT COUNT(*) INTO MI_BASESNOV
                    FROM BASESNOVEDADES 
                    WHERE LLAVENOVEDAD = MI_CONDICION;
                        
                    MI_CONDICION  :=' LLAVENOVEDAD= '''|| MI_CONDICION||'''';
                    IF MI_BASESNOV <> 0 THEN
                        MI_CAMPOS := 'APORTEPATRONALSALUD ='|| MI_CN116 ||'';
                            
                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  'BASESNOVEDADES',
                                                                       UN_ACCION    =>  'M',
                                                                       UN_CAMPOS    =>  MI_CAMPOS,
                                                                       UN_CONDICION =>  MI_CONDICION
                                                                      
                                                                       );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
                        END;
                        --BORRAR HISTORICO DE LOS CONCEPTOS 102,103 Y 116                    
                        BEGIN 
                            MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                                 AND ID_DE_EMPLEADO = '||MI_ID||'
                                                 AND MES = '||PCK_NOMINA.GL_SMES||'
                                                 AND ANO = '||PCK_NOMINA.GL_SANO||'
                                                 AND PERIODO = 7
                                                 AND ID_DE_PROCESO = '||UN_PROCESO||'
                                                 AND ID_DE_CONCEPTO IN(102,103,116) ';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'HISTORICOS',
                                                                UN_ACCION    => 'E',
                                                                UN_CONDICION => MI_CONDICION);                
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END;
                                                
                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || 7                            -- Período liquidacion
                                    || ','
                                    || MI_ID                        -- ID del empleado
                                    || ' , '
                                    || 116                          -- ID del concepto
                                    || ' , '
                                    || MI_CN116                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                            
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                     UN_ACCION  => 'I',            -- Acción de inserción
                                                     UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                     UN_VALORES => MI_VALORES      -- Valores a insertar
                                                    );
                        EXCEPTION
                            -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END; 

                        IF MI_CNP102_1 > 0 THEN
                            MI_CN102_1 := MI_CNP102_1;
                        ELSE 
                            MI_CN102_1 := PCK_SYSMAN_UTL.FC_ROUND_100(
                                    MI_CN108_1 * PCK_NOMINA.CPARENTRADA(1).PORC_ICBF / 100  , 
                                    MI_PARAMETRO1990APLICA, 
                                    PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.CPARENTRADA(1).PORC_ICBF <> 0, 
                                    PCK_NOMINA.GL_SUMARSS1990, 0), PCK_NOMINA.GL_RAPORTES1990);
                        END IF;

                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA       -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO           -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || 7                            -- Período liquidacion
                                    || ','
                                    || MI_ID                        -- ID del empleado
                                    || ' , '
                                    || 102                          -- ID del concepto
                                    || ' , '
                                    || MI_CN102_1                  -- Valor del concepto
                                    || ' ,'''|| MI_OBS              -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                            
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                     UN_ACCION  => 'I',            -- Acción de inserción
                                                     UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                     UN_VALORES => MI_VALORES      -- Valores a insertar
                                                    );
                        EXCEPTION
                            -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END; 

                        IF MI_CNP103_1 > 0 THEN
                            MI_CN103_1 := MI_CNP103_1;
                        ELSE                            
                            MI_CN103_1 := PCK_SYSMAN_UTL.FC_ROUND_100(
                                    MI_CN108_1 * PCK_NOMINA.CPARENTRADA(1).PORC_SENA / 100  , 
                                    MI_PARAMETRO1990APLICA, 
                                    PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.CPARENTRADA(1).PORC_SENA <> 0, 
                                    PCK_NOMINA.GL_SUMARSS1990, 0), PCK_NOMINA.GL_RAPORTES1990);
                        END IF;

                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA       -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO           -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || 7                            -- Período liquidacion
                                    || ','
                                    || MI_ID                        -- ID del empleado
                                    || ' , '
                                    || 103                          -- ID del concepto
                                    || ' , '
                                    || MI_CN103_1                  -- Valor del concepto
                                    || ' ,'''|| MI_OBS              -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                            
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                     UN_ACCION  => 'I',            -- Acción de inserción
                                                     UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                     UN_VALORES => MI_VALORES      -- Valores a insertar
                                                    );
                        EXCEPTION
                            -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END;
                    END IF;  
                    ---PARTE 2                
                    MI_CONDICION := ''||PCK_NOMINA.GL_COMPANIA||'--'||MI_ID2||'--'||PCK_NOMINA.GL_SANO||'--'||PCK_NOMINA.GL_SMES||'';
                
                    -- Asignar el valor del concepto 112 a la variable MI_CN112
                    MI_CN112 := MI_CN112_2;
                    ---PARTE  primer id 
                    -- Calcular el acumulado del concepto 116 para el empleado
                    MI_CN116 := PCK_SYSMAN_UTL.FC_ROUND_100(
                            MI_CN112 * PCK_NOMINA.CPARENTRADA(1).PORC_PATRON_EPS / 100,
                            MI_PARAMETRO1990APLICA,
                            PCK_NOMINA.GL_SUMARSS1990,
                            PCK_NOMINA.GL_RAPORTES1990
                        );
                    
                    SELECT COUNT(*) INTO MI_BASESNOV
                    FROM BASESNOVEDADES 
                    WHERE LLAVENOVEDAD = MI_CONDICION;
                        
                    MI_CONDICION  :=' LLAVENOVEDAD= '''|| MI_CONDICION||'''';
                    IF MI_BASESNOV <> 0 THEN
                        MI_CAMPOS := 'APORTEPATRONALSALUD ='|| MI_CN116 ||'';
                        
                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  'BASESNOVEDADES',
                                                            UN_ACCION    =>  'M',
                                                            UN_CAMPOS    =>  MI_CAMPOS,
                                                            UN_CONDICION =>  MI_CONDICION
                                                        );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
                        END;
                        --BORRAR HISTORICO CON EL CON LOS CONCEPTOS 
                        BEGIN 
                            MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                            AND ID_DE_EMPLEADO = '||MI_ID2||'
                                            AND MES = '||PCK_NOMINA.GL_SMES||'
                                            AND ANO = '||PCK_NOMINA.GL_SANO||'
                                            AND PERIODO = '||UN_PERIODO||'
                                            AND ID_DE_PROCESO = '||UN_PROCESO||'
                                            AND ID_DE_CONCEPTO IN(102,103,116) ';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'HISTORICOS',
                                                        UN_ACCION    => 'E',
                                                        UN_CONDICION => MI_CONDICION);                
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END;

                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID2 || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA      -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_ANOACTUAL      -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID2                        -- ID del empleado
                                    || ' , '
                                    || 116                          -- ID del concepto
                                    || ' , '
                                    || MI_CN116                     -- Valor del concepto
                                    || ' ,'''|| MI_OBS             -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';                                    
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                 UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                 UN_ACCION  => 'I',            -- Acción de inserción
                                                 UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                 UN_VALORES => MI_VALORES      -- Valores a insertar
                                                );
                        EXCEPTION
                        -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END; 

                        IF MI_CNP102_2 > 0 THEN
                            MI_CN102_2 := MI_CNP102_2;
                        ELSE
                            MI_CN102_2 := PCK_SYSMAN_UTL.FC_ROUND_100(
                                    MI_CN108_2 * PCK_NOMINA.CPARENTRADA(1).PORC_ICBF / 100  , 
                                    MI_PARAMETRO1990APLICA, 
                                    PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.CPARENTRADA(1).PORC_ICBF <> 0, 
                                    PCK_NOMINA.GL_SUMARSS1990, 0), PCK_NOMINA.GL_RAPORTES1990);
                        END IF;

                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID2 || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA       -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO           -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID2                       -- ID del empleado
                                    || ' , '
                                    || 102                          -- ID del concepto
                                    || ' , '
                                    || MI_CN102_2                   -- Valor del concepto
                                    || ' ,'''|| MI_OBS              -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                            
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                     UN_ACCION  => 'I',            -- Acción de inserción
                                                     UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                     UN_VALORES => MI_VALORES      -- Valores a insertar
                                                    );
                        EXCEPTION
                            -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END;
                        IF MI_CNP103_2 > 0 THEN
                            MI_CN103_2 := MI_CNP103_2;
                        ELSE                            
                            MI_CN103_2 := PCK_SYSMAN_UTL.FC_ROUND_100(
                                    MI_CN108_2 * PCK_NOMINA.CPARENTRADA(1).PORC_SENA / 100  , 
                                    MI_PARAMETRO1990APLICA, 
                                    PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.CPARENTRADA(1).PORC_SENA <> 0, 
                                    PCK_NOMINA.GL_SUMARSS1990, 0), PCK_NOMINA.GL_RAPORTES1990);
                        END IF;

                        -- Crear una observación sobre la inserción de HISTORICOS    
                        MI_OBS := 'SE ACTUALIZARON HISTORICOS PARA EL ID ' || MI_ID2 || ' PORQUE EL SALARIO BASE SUPERA LOS 10 S.M.L.V';
                        -- Asignar los nombres de las columnas para la tabla HISTORICOS
                        MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, OBSERVACIONES, DATE_CREATED, DATE_MODIFIED, FECHA, MANUAL';
                            
                        MI_VALORES := '
                                    '''
                                    || PCK_NOMINA.GL_COMPANIA       -- Compañía
                                    || ''', '
                                    || PCK_NOMINA.GL_PROCESOACTUAL  -- ID del proceso actual
                                    || ' ,'
                                    || PCK_NOMINA.GL_SANO           -- Año actual
                                    || ','
                                    || PCK_NOMINA.GL_SMES           -- Mes actual
                                    || ','
                                    || PCK_NOMINA.GL_PERIODOACTUAL  -- Período actual
                                    || ','
                                    || MI_ID2                        -- ID del empleado
                                    || ' , '
                                    || 103                          -- ID del concepto
                                    || ' , '
                                    || MI_CN103_2                  -- Valor del concepto
                                    || ' ,'''|| MI_OBS              -- Observaciones
                                    || ''', SYSDATE, SYSDATE, SYSDATE, 0';
                                            
                        BEGIN
                            -- Intentar insertar los valores en la tabla HISTORICOS
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA   => 'HISTORICOS',  -- Tabla de destino
                                                     UN_ACCION  => 'I',            -- Acción de inserción
                                                     UN_CAMPOS  => MI_CAMPOS,      -- Columnas
                                                     UN_VALORES => MI_VALORES      -- Valores a insertar
                                                    );
                        EXCEPTION
                            -- Manejar excepción si ocurre un error de inserción
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;  -- Levantar excepción personalizada
                        END;                
                    END IF;    
                END IF; 
                MI_MSG(1).CLAVE := 'EMPLEADO';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                MI_MSG(2).CLAVE := 'CEDULA';
                MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                PCK_NOMINA_COM7.PR_ALERTA
                (   UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_REVISION_PARAFISCALES
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
            END IF;  
        END LOOP;
    END IF;                             
END PR_REVISAR_PARAFISCALES;

END PCK_NOMINA_COM9;
