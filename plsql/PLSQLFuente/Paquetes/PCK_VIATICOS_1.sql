create or replace PACKAGE BODY PCK_VIATICOS AS

PROCEDURE PR_CREARDETALLELEGVIATICOS
(

    /*
    NAME                  : Evento boton detalle del formulario Legalizacion viaticos
    AUTHOR                : STEFANINI SYSMAN
    AUTHOR MIGRATION      : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRATION        : 18/01/2018 
    TIME                  : 03:11 PM
    SOURCE MODULE         : SysmanLV2018.01.01
    DESCRIPTION           : Procedimiento que inserta a la tabla VI_DETALLE_LEGALIZA_VIATICOS y actualiza a las tablas VI_DETALLE_VIATICOS y VI_VIATICOS
                            Ruta: Principal Viaticos\Procesos\Legalización de viáticos
    PARAMETERS            : UN_COMPANIA: Codigo de la compania en la cual se ingresa en la aplicacion.
                            UN_ANO: Ano del registro seleccionado 
                            UN_TERCERO = Tercero del registro seleccionado 
                            UN_CODSOLICITUD = Código seleccionado en el combo Código de solicitud del formulario 
                            UN_NUMERO = Numero del registro seleccionado
                            UN_USUARIO: Codigo del usuario que inicio sesion.

    @NAME: crearDetalleLegalizaViaticos
    @METHOD: POST
  */
	UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO      	  IN PCK_SUBTIPOS.TI_ANIO,
	UN_TERCERO  	  IN PCK_SUBTIPOS.TI_TERCERO,
	UN_CODSOLICITUD IN VARCHAR2,
	UN_NUMERO 		  IN VARCHAR2,
	UN_USUARIO		  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
	MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS 		    PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
	MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGEEXISTE;   
	MI_RETORNO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_EXISTE         PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN

  MI_TABLA := 'VI_DETALLE_LEGALIZA_VIATICOS';
  MI_MERGEUSING := 'SELECT  
                      VI_DETALLE_VIATICOS.COMPANIA,
                      VI_DETALLE_VIATICOS.ANO ,
                      VI_DETALLE_VIATICOS.TIPO_VIATICO,
                      VI_DETALLE_VIATICOS.NUMERO NUMERO_AFECTADO,
                      VI_DETALLE_VIATICOS.TERCERO,
                      VI_DETALLE_VIATICOS.SUCURSAL,
                      VI_DETALLE_VIATICOS.CONCEPTO,
                      '||UN_NUMERO||' NUMERO,
                      VI_DETALLE_VIATICOS.DESCRIPCION,
                      VI_DETALLE_VIATICOS.TOTAL - VI_DETALLE_VIATICOS.VALOR_ABONADO VALOR
                    FROM VI_VIATICOS
                      INNER JOIN VI_DETALLE_VIATICOS
                        ON VI_VIATICOS.COMPANIA        = VI_DETALLE_VIATICOS.COMPANIA 
                        AND VI_VIATICOS.ANO            = VI_DETALLE_VIATICOS.ANO
                        AND VI_VIATICOS.TIPO_VIATICO   = VI_DETALLE_VIATICOS.TIPO_VIATICO
                        AND VI_VIATICOS.CODSOLICITUD   = VI_DETALLE_VIATICOS.NUMERO
                    WHERE VI_VIATICOS.COMPANIA 					  ='''||UN_COMPANIA||'''
                      AND VI_VIATICOS.ANO  					      = '||UN_ANO||'
                      AND VI_DETALLE_VIATICOS.TERCERO 		= '''||UN_TERCERO||'''
                      AND VI_VIATICOS.CODSOLICITUD 			  = '||UN_CODSOLICITUD||'
                      AND VI_DETALLE_VIATICOS.TOTAL-VI_DETALLE_VIATICOS.VALOR_ABONADO >0';
  MI_MERGEENLACE := '	TABLA.COMPANIA 			    = VISTA.COMPANIA
                  AND TABLA.ANO 				      = VISTA.ANO
                  AND TABLA.TIPO_VIATICO 		  = VISTA.TIPO_VIATICO
                  AND TABLA.NUMERO_AFECTADO 	= VISTA.NUMERO_AFECTADO
                  AND TABLA.TERCERO 			    = VISTA.TERCERO
                  AND TABLA.SUCURSAL	 		    = VISTA.SUCURSAL
                  AND TABLA.CODIGO_CONCEPTO 	= VISTA.CONCEPTO';
  MI_MERGEEXISTE := ' UPDATE SET 
                        TABLA.VALOR 			    = VISTA.VALOR,
                        TABLA.VALOR_AFECTADO 	= VISTA.VALOR,
                        TABLA.SALDO 			    = VISTA.VALOR,
                        TABLA.DATE_MODIFIED		= SYSDATE,
                        TABLA.MODIFIED_BY		  = '''||UN_USUARIO||'''';
  MI_MERGENOEXIS := 'INSERT (
                        TABLA.COMPANIA,
                        TABLA.ANO,
                        TABLA.NUMERO,
                        TABLA.TIPO_VIATICO,
                        TABLA.TERCERO,
                        TABLA.SUCURSAL,
                        TABLA.CODIGO_CONCEPTO,
                        TABLA.NUMERO_AFECTADO,
                        TABLA.DESCRIPCION,
                        TABLA.VALOR,
                        TABLA.VALOR_AFECTADO,
                        TABLA.SALDO,
                        TABLA.CREATED_BY,
                        TABLA.DATE_CREATED)
                    VALUES (
                			VISTA.COMPANIA,
                			VISTA.ANO,
                			VISTA.NUMERO,
                			VISTA.TIPO_VIATICO,
                			VISTA.TERCERO,
                			VISTA.SUCURSAL,
                			VISTA.CONCEPTO,
                			VISTA.NUMERO_AFECTADO,
                			VISTA.DESCRIPCION,
                			VISTA.VALOR,
                			VISTA.VALOR,
                			VISTA.VALOR,
                			'''||UN_USUARIO||''',
                			SYSDATE
                			)';		
	BEGIN 
		BEGIN
				MI_RETORNO	 := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                        UN_ACCION      => 'IM',
                                        UN_MERGEUSING  => MI_MERGEUSING,
                                        UN_MERGEENLACE => MI_MERGEENLACE,
                                        UN_MERGEEXISTE => MI_MERGEEXISTE,
                                        UN_MERGENOEXIS => MI_MERGENOEXIS);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERRR_VIATICOS_LEGALIZAVIATICOS
                              );
    END;	

	IF MI_RETORNO > 0 THEN 

		MI_TABLA := 'VI_DETALLE_VIATICOS';
		MI_MERGEUSING := '	SELECT  
								VI_VIATICOS.COMPANIA,
								VI_VIATICOS.ANO,
								VI_VIATICOS.TIPO_VIATICO,
								VI_DETALLE_VIATICOS.VALOR_ABONADO,
								VI_DETALLE_VIATICOS.TOTAL,
                VI_DETALLE_VIATICOS.CONCEPTO
							FROM  VI_VIATICOS
								INNER JOIN VI_DETALLE_VIATICOS
									ON  VI_VIATICOS.COMPANIA      = VI_DETALLE_VIATICOS.COMPANIA 
									AND VI_VIATICOS.ANO           = VI_DETALLE_VIATICOS.ANO 
									AND VI_VIATICOS.TIPO_VIATICO  = VI_DETALLE_VIATICOS.TIPO_VIATICO
                  AND VI_VIATICOS.CODSOLICITUD  = VI_DETALLE_VIATICOS.NUMERO
							WHERE VI_VIATICOS.COMPANIA 			= '''||UN_COMPANIA||'''
								AND VI_VIATICOS.ANO 			= '||UN_ANO||'
								AND VI_DETALLE_VIATICOS.TERCERO = '''||UN_TERCERO||'''
								AND VI_VIATICOS.CODSOLICITUD 	= '||UN_CODSOLICITUD||'';
		MI_MERGEENLACE := '	VISTA.COMPANIA      = TABLA.COMPANIA 
                    AND VISTA.ANO           = TABLA.ANO 
                    AND VISTA.TIPO_VIATICO  = TABLA.TIPO_VIATICO
                    AND VISTA.CONCEPTO      = TABLA.CONCEPTO';
		MI_MERGEEXISTE := 'UPDATE SET 
							TABLA.VALOR_ABONADO = VISTA.TOTAL,
							TABLA.SALDO 		= 0,
							TABLA.MODIFIED_BY 	= '''||UN_USUARIO||''',
							TABLA.DATE_MODIFIED = SYSDATE';
		BEGIN 
			BEGIN
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	
													UN_TABLA   		=>  MI_TABLA, 
                                                    UN_ACCION  		=>  'MM', 
                                                    UN_MERGEUSING  	=> 	MI_MERGEUSING, 
                                                    UN_MERGEENLACE	=>  MI_MERGEENLACE,
													UN_MERGEEXISTE 	=> 	MI_MERGEEXISTE);
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
				RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
			END;
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN   
        MI_MSGERROR(1).CLAVE := 'TABLA';
        MI_MSGERROR(1).VALOR := MI_TABLA;
        MI_MSGERROR(2).CLAVE := 'CODSOLICITUD';
        MI_MSGERROR(2).VALOR := UN_CODSOLICITUD;
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD   => SQLCODE,
                                                    UN_ERROR_COD => PCK_ERRORES.ERRR_VIATICOS_ACTLEGVIATICOS,
                                                    UN_REEMPLAZOS  => MI_MSGERROR
                                                  );
  	END;	
		<<VALORDETLEGALIZAVIATICOS>>
		FOR RS IN (
			SELECT  VALOR,
					CODIGO_CONCEPTO 
			FROM VI_DETALLE_LEGALIZA_VIATICOS
			WHERE COMPANIA     		= UN_COMPANIA
				AND ANO          	= UN_ANO
				AND TIPO_VIATICO  = 1
				AND NUMERO_AFECTADO = UN_CODSOLICITUD
				)
		LOOP
			MI_TABLA := 'VI_VIATICOS';
			MI_MERGEUSING := '	SELECT  
									VI_VIATICOS.COMPANIA,
									VI_VIATICOS.ANO,
									VI_VIATICOS.TIPO_VIATICO,
                  VI_VIATICOS.CODSOLICITUD,
									VI_VIATICOS.VALORABONADO,
									VI_VIATICOS.SALDOTOTAL
								FROM VI_VIATICOS    	
									INNER JOIN VI_DETALLE_VIATICOS 
										ON VI_VIATICOS.COMPANIA       = VI_DETALLE_VIATICOS.COMPANIA
										AND VI_VIATICOS.ANO           = VI_DETALLE_VIATICOS.ANO
										AND VI_VIATICOS.TIPO_VIATICO  = VI_DETALLE_VIATICOS.TIPO_VIATICO
                    AND VI_VIATICOS.CODSOLICITUD  = VI_DETALLE_VIATICOS.NUMERO
								WHERE VI_VIATICOS.COMPANIA  			= '''||UN_COMPANIA||'''
									AND VI_VIATICOS.ANO 				= '||UN_ANO||'
									AND VI_DETALLE_VIATICOS.TERCERO 	= '''||UN_TERCERO||'''
									AND VI_DETALLE_VIATICOS.CONCEPTO 	= '''||RS.CODIGO_CONCEPTO||'''
									AND VI_VIATICOS.CODSOLICITUD 		= '||UN_CODSOLICITUD||'';

			MI_MERGEENLACE := '	VISTA.COMPANIA      = TABLA.COMPANIA 
                      AND VISTA.ANO           = TABLA.ANO 
                      AND VISTA.TIPO_VIATICO  = TABLA.TIPO_VIATICO
                      AND VISTA.CODSOLICITUD  = TABLA.CODSOLICITUD';			
			MI_MERGEEXISTE := 'UPDATE SET 
                            TABLA.VALORABONADO 	= VISTA.VALORABONADO + '||RS.VALOR||',
                            TABLA.SALDOTOTAL 	  = VISTA.SALDOTOTAL - '||RS.VALOR||',
                            TABLA.MODIFIED_BY 	= '''||UN_USUARIO||''',
                            TABLA.DATE_MODIFIED = SYSDATE';		
			BEGIN 
				BEGIN
					PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	
                                                    UN_TABLA   		  =>  MI_TABLA, 
                                                    UN_ACCION  		  =>  'MM', 
                                                    UN_MERGEUSING  	=> 	MI_MERGEUSING, 
                                                    UN_MERGEENLACE	=>  MI_MERGEENLACE,
													UN_MERGEEXISTE 	=> 	MI_MERGEEXISTE);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
					RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
				END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN    
        MI_MSGERROR(1).CLAVE := 'TABLA';
        MI_MSGERROR(1).VALOR := MI_TABLA;
        MI_MSGERROR(2).CLAVE := 'CODSOLICITUD';
        MI_MSGERROR(2).VALOR := UN_CODSOLICITUD;
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_VIATICOS_ACTLEGVIATICOS,
                                                    UN_REEMPLAZOS => MI_MSGERROR
                                                  );
			END;					
		END LOOP VALORDETLEGALIZAVIATICOS;	
	END IF;
END PR_CREARDETALLELEGVIATICOS;

--2

PROCEDURE PR_ACTUALIZARDETALLE
(

  	 /*
    NAME                  : Evento al actualiza el registro del formulario detalle Legalizacion viaticos
    AUTHOR                : STEFANINI SYSMAN
    AUTHOR MIGRATION      : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRATION        : 20/01/2018 
    TIME                  : 10:16 AM
    SOURCE MODULE         : SysmanLV2018.01.01
    DESCRIPTION           : Procedimiento que acualiza las tablas VI_DETALLE_VIATICOS,VI_VIATICOS,VI_LEGALIZACION_VIATICOS
                            Ruta: Principal Viaticos\Procesos\Legalización de viáticos\boton detalle
    PARAMETERS            : UN_COMPANIA: Codigo de la compania en la cual se ingresa en la aplicacion.
                            UN_ANO: Ano del registro seleccionado 
                            UN_NUMERO         : Numero de la legalizacion de viatico
                            UN_NUMEROAFECTADO : Numero afectado del registro seleccionad
                            UN_CONCEPTO : Concepto del registro seleccionado 
                            UN_USUARIO: Codigo del usuario que inicio sesion.
                            UN_VALOR : Valor cambiado del registro seleccionado
                            UN_VALOR_ANTERIOR: valor antes de ser actualizado el registro

    @NAME: actualizarDetalleLegalizaViaticos
    @METHOD: PUT
  */
	UN_COMPANIA 		  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO      		  IN PCK_SUBTIPOS.TI_ANIO,
	UN_NUMERO 			  IN VARCHAR2,
	UN_NUMEROAFECTADO	IN VARCHAR2,
	UN_CONCEPTO       IN VARCHAR2,
	UN_USUARIO			  IN PCK_SUBTIPOS.TI_USUARIO,
	UN_VALOR          IN PCK_SUBTIPOS.TI_DOBLE,
	UN_VALOR_ANTERIOR	IN PCK_SUBTIPOS.TI_DOBLE
)

	AS 
		MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS 			PCK_SUBTIPOS.TI_CAMPOS;
		MI_VALORES 			PCK_SUBTIPOS.TI_VALORES;
		MI_CONDICION 		PCK_SUBTIPOS.TI_CONDICION;
		MI_TOTAL			  PCK_SUBTIPOS.TI_DOBLE;
    MI_MERGEUSING	  PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE	PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE	PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TERCERO      PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSAL     PCK_SUBTIPOS.TI_SUCURSAL;

	BEGIN 

    SELECT TERCERO, SUCURSAL 
    INTO MI_TERCERO, MI_SUCURSAL
    FROM VI_LEGALIZACION_VIATICOS
    WHERE COMPANIA      = UN_COMPANIA 
      AND ANO           = UN_ANO
      AND NUMERO        = UN_NUMERO
      AND TIPO_VIATICO  = 1;

		MI_TABLA := 'VI_DETALLE_VIATICOS';
		MI_CAMPOS := 'VALOR_ABONADO 	= VALOR_ABONADO + ('||UN_VALOR||' - '||UN_VALOR_ANTERIOR||'), 
                  SALDO 			    = ABS(SALDO - ('||UN_VALOR||' - '||UN_VALOR_ANTERIOR||')),
                  MODIFIED_BY 	  = '''||UN_USUARIO||''',
                  DATE_MODIFIED 	= SYSDATE';
		MI_CONDICION := ' COMPANIA 		  = '''||UN_COMPANIA||'''
                  AND ANO 			    = '||UN_ANO||'
                  AND TIPO_VIATICO 	= 1
                  AND NUMERO 			  = '||UN_NUMEROAFECTADO||'
                  AND TERCERO 		  = '''||MI_TERCERO||'''
                  AND SUCURSAL 		  = '''||MI_SUCURSAL||'''
                  AND CONCEPTO   		= '''||UN_CONCEPTO||'''';
		BEGIN 
			BEGIN 
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	
                            UN_TABLA    =>MI_TABLA, 
														UN_ACCION   =>'M',
														UN_CAMPOS   =>MI_CAMPOS,
														UN_CONDICION=>MI_CONDICION
                                                     ); 
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_VIATICOS;                                                     
			END;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN
     MI_MSGERROR(1).CLAVE := 'TABLA';
     MI_MSGERROR(1).VALOR := MI_TABLA;
     MI_MSGERROR(2).CLAVE := 'CODSOLICITUD';
     MI_MSGERROR(2).VALOR := UN_NUMEROAFECTADO;
     PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERRR_VIATICOS_ACTLEGVIATICOS,
                                UN_REEMPLAZOS  => MI_MSGERROR
                                );
    END; 											 


		MI_TABLA := 'VI_VIATICOS';
		MI_MERGEUSING := '	SELECT  
								VI_VIATICOS.COMPANIA,
								VI_VIATICOS.ANO,
								VI_VIATICOS.TIPO_VIATICO,
								VI_VIATICOS.CODSOLICITUD,
								VI_VIATICOS.VALORABONADO,
								VI_VIATICOS.SALDOTOTAL
							FROM VI_VIATICOS    	
								INNER JOIN VI_DETALLE_VIATICOS 
									ON VI_VIATICOS.COMPANIA       = VI_DETALLE_VIATICOS.COMPANIA
									AND VI_VIATICOS.ANO           = VI_DETALLE_VIATICOS.ANO
									AND VI_VIATICOS.TIPO_VIATICO  = VI_DETALLE_VIATICOS.TIPO_VIATICO
                  AND VI_VIATICOS.CODSOLICITUD  = VI_DETALLE_VIATICOS.NUMERO
							WHERE VI_VIATICOS.COMPANIA  			  = '''||UN_COMPANIA||'''
								AND VI_VIATICOS.ANO 				      = '||UN_ANO||'
								AND VI_DETALLE_VIATICOS.TERCERO 	= '''||MI_TERCERO||'''
								AND VI_DETALLE_VIATICOS.CONCEPTO 	= '''||UN_CONCEPTO||'''
								AND VI_VIATICOS.CODSOLICITUD 		  = '||UN_NUMEROAFECTADO||'';
		MI_MERGEENLACE := '	VISTA.COMPANIA      = TABLA.COMPANIA 
                    AND VISTA.ANO           = TABLA.ANO 
                    AND VISTA.TIPO_VIATICO  = TABLA.TIPO_VIATICO
                    AND VISTA.CODSOLICITUD  = TABLA.CODSOLICITUD';			
		MI_MERGEEXISTE := 'UPDATE SET 
						TABLA.VALORABONADO 	= VISTA.VALORABONADO + ('||UN_VALOR||' - '||UN_VALOR_ANTERIOR||'),
						TABLA.SALDOTOTAL 	  = VISTA.SALDOTOTAL - ('||UN_VALOR||' - '||UN_VALOR_ANTERIOR||'),
						TABLA.MODIFIED_BY 	= '''||UN_USUARIO||''',
						TABLA.DATE_MODIFIED = SYSDATE';		
		BEGIN 
			BEGIN
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	
												UN_TABLA   		  =>  MI_TABLA, 
                        UN_ACCION  		  =>  'MM', 
                        UN_MERGEUSING  	=> 	MI_MERGEUSING, 
                        UN_MERGEENLACE	=>  MI_MERGEENLACE,
												UN_MERGEEXISTE 	=> 	MI_MERGEEXISTE);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
					RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
				END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN    
        MI_MSGERROR(1).CLAVE := 'TABLA';
        MI_MSGERROR(1).VALOR := MI_TABLA;
        MI_MSGERROR(2).CLAVE := 'CODSOLICITUD';
        MI_MSGERROR(2).VALOR := UN_NUMEROAFECTADO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERRR_VIATICOS_ACTLEGVIATICOS,
                                UN_REEMPLAZOS  => MI_MSGERROR
                                );
			END;	

		BEGIN 
			BEGIN 

				SELECT SUM(NVL(VALOR,0)) TOTAL
				INTO MI_TOTAL
				FROM VI_DETALLE_LEGALIZA_VIATICOS 
				WHERE COMPANIA = UN_COMPANIA
					AND ANO = UN_ANO
					AND TIPO_VIATICO = 1
					AND NUMERO_AFECTADO= UN_NUMEROAFECTADO;

				MI_TABLA := 'VI_LEGALIZACION_VIATICOS';
				MI_CAMPOS := '	VALOR_LEGALIZADO 	= '||MI_TOTAL||',
                        MODIFIED_BY 		  = '''||UN_USUARIO||''',
                        DATE_MODIFIED 		= SYSDATE';
				MI_CONDICION := ' COMPANIA 			  = '''||UN_COMPANIA||'''
                      AND ANO 			      = '||UN_ANO||'
                      AND NUMERO 			    = '||UN_NUMERO||'
                      AND TIPO_VIATICO 	  = 1';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	
                            UN_TABLA    =>MI_TABLA, 
														UN_ACCION   =>'M',
														UN_CAMPOS   =>MI_CAMPOS,
														UN_CONDICION=>MI_CONDICION
                                                     ); 
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_VIATICOS;                                                     
			END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE,
                             UN_ERROR_COD=>PCK_ERRORES.ERRR_UPDTVIATICOS );
        END; 
END PR_ACTUALIZARDETALLE	;

--3

PROCEDURE PR_INSDETALLEVIATICOS
/*
    NAME              : PR_INSDETALLEVIATICOS => En Access CalcularViaticos()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 25/01/2018
    TIME              : 09:15 AM
    SOURCE MODULE     : SysmanLV2018.01.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Funcion que inserta detalles de viatico por defecto a partir de la solicitud
                        de viatico siempre y cuando este no tenga detalles de viaticos

    PARAMETERS        :                         

    @NAME:  insertarDetalleViaticos
    @METHOD:  POST                         
  */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO             IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOVIATICO     IN VI_VIATICOS.TIPO_VIATICO%TYPE, 
	UN_CODIGOSOLICITUD IN VI_VIATICOS.CODSOLICITUD%TYPE, 
  UN_TERCERO         IN VI_VIATICOS.TERCERO%TYPE, 	
  UN_SUCURSAL        IN VI_VIATICOS.SUCURSAL%TYPE, 
  UN_PAISORIGEN      IN VI_VIATICOS.PAIS_ORIGEN%TYPE,  
  UN_DEPTOORIGEN     IN VI_VIATICOS.DEPARTAMENTO_ORIGEN%TYPE, 
  UN_CIUDADORIGEN    IN VI_VIATICOS.CIUDAD_ORIGEN%TYPE, 
  UN_PAISDESTINO     IN VI_VIATICOS.PAIS_DESTINO%TYPE, 
  UN_DEPTODESTINO    IN VI_VIATICOS.DEPARTAMENTO_DESTINO%TYPE, 
  UN_CIUDADDESTINO   IN VI_VIATICOS.CIUDAD_DESTINO%TYPE, 
  UN_DESCRIPCION     IN VI_VIATICOS.OBSERVACION%TYPE,  
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_CANTIDAD             PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RSSOLICITUD          SYS_REFCURSOR;

BEGIN
--CONSULTAR SI LA SOLICITUD CONTIENE DETALLES DE VIATICO

    SELECT COUNT(*)
      INTO MI_CANTIDAD
      FROM VI_DETALLE_VIATICOS
    WHERE COMPANIA     = UN_COMPANIA
      AND ANO          = UN_ANO
      AND TIPO_VIATICO = UN_TIPOVIATICO
      AND NUMERO       = UN_CODIGOSOLICITUD
      AND TERCERO      = UN_TERCERO
      AND SUCURSAL     = UN_SUCURSAL;



 --INSERTA DETALLES POR DEFECTO     
    IF MI_CANTIDAD = 0 THEN

       <<RECORRER_DETALLE_CAT_CONCEPTO>> 
        FOR MI_RSSOLICITUD IN (
              SELECT DISTINCT VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ANO,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ESCALAFON,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ID_CATEGORIA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO,
                    VI_DETALLE_CATEGORIA_CONCEPTO.TARIFA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.CONSECUTIVO
              FROM VI_DETALLE_CATEGORIA_CONCEPTO
              INNER JOIN VI_CONCEPTO_VIATICOS
                ON VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA            = VI_CONCEPTO_VIATICOS.COMPANIA
                AND VI_DETALLE_CATEGORIA_CONCEPTO.ANO                = VI_CONCEPTO_VIATICOS.ANO
                AND VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO    = VI_CONCEPTO_VIATICOS.CODIGO_CONCEPTO
               INNER JOIN PERSONAL
                    ON VI_DETALLE_CATEGORIA_CONCEPTO.ID_CATEGORIA        = PERSONAL.ID_DE_CATEGORIA
                   AND VI_DETALLE_CATEGORIA_CONCEPTO.ESCALAFON           = PERSONAL.ESCALAFON
                   AND VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA            = PERSONAL.COMPANIA
              WHERE PERSONAL.COMPANIA                                 = UN_COMPANIA
                AND PERSONAL.NUMERO_DCTO                              = UN_TERCERO
                AND VI_CONCEPTO_VIATICOS.IND_ORIGEN_DESTINO NOT IN(0)
                AND VI_DETALLE_CATEGORIA_CONCEPTO.ANO                 = UN_ANO
                AND VI_DETALLE_CATEGORIA_CONCEPTO.PAISORIGEN          = UN_PAISORIGEN
                AND VI_DETALLE_CATEGORIA_CONCEPTO.DEPARTAMENTOORIGEN  = UN_DEPTOORIGEN
                AND VI_DETALLE_CATEGORIA_CONCEPTO.CIUDADORIGEN        = UN_CIUDADORIGEN
                AND VI_DETALLE_CATEGORIA_CONCEPTO.PAISDESTINO         = UN_PAISDESTINO
                AND VI_DETALLE_CATEGORIA_CONCEPTO.DEPARTAMENTODESTINO = UN_DEPTODESTINO
                AND VI_DETALLE_CATEGORIA_CONCEPTO.CIUDADDESTINO       = UN_CIUDADDESTINO              
              UNION
              SELECT VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ANO,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ESCALAFON,
                    VI_DETALLE_CATEGORIA_CONCEPTO.ID_CATEGORIA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO,
                    VI_DETALLE_CATEGORIA_CONCEPTO.TARIFA,
                    VI_DETALLE_CATEGORIA_CONCEPTO.CONSECUTIVO
              FROM VI_DETALLE_CATEGORIA_CONCEPTO
               INNER JOIN VI_CONCEPTO_VIATICOS
                ON VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA            = VI_CONCEPTO_VIATICOS.COMPANIA
                AND VI_DETALLE_CATEGORIA_CONCEPTO.ANO                = VI_CONCEPTO_VIATICOS.ANO
                AND VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO    = VI_CONCEPTO_VIATICOS.CODIGO_CONCEPTO
               INNER JOIN PERSONAL
                    ON VI_DETALLE_CATEGORIA_CONCEPTO.ID_CATEGORIA    = PERSONAL.ID_DE_CATEGORIA
                   AND VI_DETALLE_CATEGORIA_CONCEPTO.ESCALAFON       = PERSONAL.ESCALAFON
                   AND VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA        = PERSONAL.COMPANIA
              WHERE PERSONAL.COMPANIA                                = UN_COMPANIA
                AND PERSONAL.NUMERO_DCTO                             = UN_TERCERO
                AND VI_CONCEPTO_VIATICOS.IND_ORIGEN_DESTINO IN(0)
        )LOOP 




         MI_CAMPOS := ' COMPANIA
                    , ANO
                    , TIPO_VIATICO
                    , NUMERO
                    , TERCERO
                    , SUCURSAL
                    , CONCEPTO
                    , DESCRIPCION
                    , CONSECUTIVO
                    , NUMDIASPER
                    , NUMDIASSINPER
                    , VALDIASPER
                    , VALDIASSINPER
                    , TOTAL                             
                    , SALDO_PRESUPUESTAL                  
                    , VALOR_ABONADO
                    , SALDO                   
                    , CREATED_BY
                    , DATE_CREATED';

          MI_VALORES := ' '''||UN_COMPANIA||'''
                         ,'||UN_ANO||'
                         ,'||UN_TIPOVIATICO||'
                         ,'''||UN_CODIGOSOLICITUD||''' 
                         ,'''||UN_TERCERO||'''
                         ,'''||UN_SUCURSAL||'''
                         ,'''||MI_RSSOLICITUD.CODIGO_CONCEPTO||'''
                         ,'''||UN_DESCRIPCION||'''
                         ,'||MI_RSSOLICITUD.CONSECUTIVO||'
                         ,0
                         ,0
                         ,0
                         ,0
                         ,0                                    
                         ,0                  
                         ,0
                         ,0                
                         ,'''||UN_USUARIO||'''
                         ,SYSDATE';

                BEGIN
                 BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'VI_DETALLE_VIATICOS', 
                                        UN_ACCION  =>  'I', 
                                        UN_CAMPOS  =>  MI_CAMPOS, 
                                        UN_VALORES =>  MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
                 END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN  
                      MI_MSGERROR(1).CLAVE := 'CODIGOSOLICITUD';
                      MI_MSGERROR(1).VALOR := UN_CODIGOSOLICITUD;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD   => SQLCODE,
                                                UN_ERROR_COD => PCK_ERRORES.ERRR_VIATICOS_DETALLE,
                                                UN_REEMPLAZOS  => MI_MSGERROR
                                              );
                END;	     

        END LOOP RECORRER_DETALLE_CAT_CONCEPTO;    

    END IF;              

END PR_INSDETALLEVIATICOS;


--4

PROCEDURE PR_ACTTOTALDETALLEVIATICOS
/*
    NAME              : PR_ACTTOTALDETALLEVIATICOS => En Access CalcularViaticos()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 29/01/2018
    TIME              : 08:15 AM
    SOURCE MODULE     : SysmanLV2018.01.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Funcion que actualiza el total del viatico a partir de los detalles de la solicitud de viatico

    PARAMETERS        :                         

    @NAME:  actualizarTotalDetalleViaticos
    @METHOD:  POST                         
  */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO             IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOVIATICO     IN VI_VIATICOS.TIPO_VIATICO%TYPE, 
	UN_CODIGOSOLICITUD IN VI_VIATICOS.CODSOLICITUD%TYPE,
  UN_VALOR           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_CANTIDAD             PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TOTAL                VI_DETALLE_VIATICOS.TOTAL%TYPE;
  MI_RSVIATICO            SYS_REFCURSOR;

BEGIN     
     MI_CAMPOS := 'VALORTOTALVIATICO = VALORTOTALVIATICO + '|| UN_VALOR||'
                  ,MODIFIED_BY     = '''||UN_USUARIO||'''
                  ,DATE_MODIFIED   = SYSDATE';
     MI_CONDICION := 'COMPANIA     = ''' || UN_COMPANIA  ||'''
                  AND ANO          = '   || UN_ANO       ||'
                  AND TIPO_VIATICO = '   || UN_TIPOVIATICO || '
                  AND CODSOLICITUD = '''|| UN_CODIGOSOLICITUD||''' ';  

        BEGIN
           BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'VI_VIATICOS', 
                                      UN_ACCION  =>  'M', 
                                      UN_CAMPOS  =>  MI_CAMPOS, 
                                      UN_CONDICION =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_VIATICOS;
           END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_VIATICOS THEN  
                    MI_MSGERROR(1).CLAVE := 'CODIGOSOLICITUD';
                    MI_MSGERROR(1).VALOR := UN_CODIGOSOLICITUD;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_UPDTOTVIATICOS,
                                              UN_REEMPLAZOS  => MI_MSGERROR
                                            );
        END;	 


END PR_ACTTOTALDETALLEVIATICOS;


--5

FUNCTION FC_DIASPERNOCTANDOVIATICO
/*
    NAME              : FC_DIASPERNOCTANDOVIATICO 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 19/01/2018
    TIME              : 12:50 PM
    SOURCE MODULE     : SysmanLV2018.01.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Calcula los dias que pernocta a partir de la solicitud de viatico
    PARAMETERS        : UN_COMPANIA --> Codigo de la compania 
                        UN_PERNOCTANDO --> si es -1 la funcion devuelve los dias pernoctando y si es 0 devuelve los dias sin pernoctar
                        UN_CODIGOSOLICITUD --> Codigo de solicitud de viatico para traer la fecha inicial y final de la solicitud                  




    @NAME:  retornarDiasPernoctandoViaticos
    @METHOD:  GET                         
  */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PERNOCTANDO     IN PCK_SUBTIPOS.TI_LOGICO,
  UN_CODIGOSOLICITUD IN VI_VIATICOS.CODSOLICITUD%TYPE

  )
RETURN NUMBER

AS
  MI_TOTALDIAS        NUMBER;
  MI_DIASHABILES      NUMBER;
  MI_DIA              DATE;
  MI_DIASIG           DATE;
  MI_DIACTUALFESTIVO  NUMBER;
  MI_DIASIGFESTIVO    NUMBER;
  MI_DIASPER          NUMBER;
  MI_DIASINPER        NUMBER;
  MI_DIASPERNOCTANDO  NUMBER;
  MI_FECHAINICIO      DATE;
  MI_FECHAFIN         DATE;

BEGIN

 MI_DIASPERNOCTANDO := 0;

  <<RECORRER_SOLICITUD>>             
  FOR MI_RSSOLICITUD IN (SELECT 
                          FECHAINICIO,
                          FECHAFIN,                              
                          SABADO,
                          DOMINGO,
                          FESTIVO
                        FROM VI_VIATICOS
                        WHERE COMPANIA   = UN_COMPANIA
                        AND CODSOLICITUD = UN_CODIGOSOLICITUD)LOOP 

      MI_FECHAINICIO := MI_RSSOLICITUD.FECHAINICIO;
      MI_FECHAFIN := MI_RSSOLICITUD.FECHAFIN;

      MI_TOTALDIAS := MI_FECHAFIN  - MI_FECHAINICIO;   

      MI_DIACTUALFESTIVO := 0;
      MI_DIASIGFESTIVO := 0; 
      MI_DIASPER := 0;
      MI_DIASINPER := 0;


      --CUANDO SE TOMAN SOLO LOS DIAS HABILES

      IF MI_RSSOLICITUD.SABADO IN (0) AND MI_RSSOLICITUD.DOMINGO IN (0) AND MI_RSSOLICITUD.FESTIVO IN (0)   THEN                          

      MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => 0,
                                                            UN_DOMINGOS => 0,
                                                            UN_FESTIVOS => 0) ;

        FOR I IN 0.. MI_TOTALDIAS  LOOP

          MI_DIA := MI_FECHAINICIO + I;  
          MI_DIASIG := MI_FECHAINICIO + (I+1);

            <<FESTIVOS_ENTRE_FECHAS>>
              FOR MI_RS IN (SELECT ID_DE_FESTIVO
                            FROM FESTIVOS
                            WHERE ID_DE_FESTIVO BETWEEN MI_FECHAINICIO AND MI_FECHAFIN) LOOP

                IF MI_RS.ID_DE_FESTIVO =  MI_DIA THEN
                  MI_DIACTUALFESTIVO := 1; 
                ELSE  
                  MI_DIACTUALFESTIVO := 0; 
                END IF;

                IF MI_RS.ID_DE_FESTIVO =  MI_DIASIG AND MI_DIACTUALFESTIVO NOT IN (1)AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 5 AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 7 THEN
                  MI_DIASINPER := MI_DIASINPER + 1;
                  MI_DIASIGFESTIVO := 1;

                END IF;

              END LOOP FESTIVOS_ENTRE_FECHAS;  

          IF I = MI_TOTALDIAS AND MI_DIACTUALFESTIVO <> 1 AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 6 AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 7 THEN
            MI_DIASINPER := MI_DIASINPER + 1 ;
            MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
            CONTINUE;

          ELSIF I = MI_TOTALDIAS THEN
            MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
            CONTINUE;        
          END IF;    

          IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 5 AND MI_TOTALDIAS > I AND MI_DIASIGFESTIVO <> 1 AND MI_DIACTUALFESTIVO <> (1)THEN
            MI_DIASINPER := MI_DIASINPER + 1 ;
          END IF;
        END LOOP;



      --CUANDO SE TOMAN COMO HABILIES LOS SABADOS
      ELSIF MI_RSSOLICITUD.SABADO NOT IN (0) AND MI_RSSOLICITUD.DOMINGO IN (0) AND MI_RSSOLICITUD.FESTIVO IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => -1,
                                                            UN_DOMINGOS => 0,
                                                            UN_FESTIVOS => 0) ; 


         FOR I IN 0.. MI_TOTALDIAS  LOOP

           MI_DIA := MI_FECHAINICIO + I;  
           MI_DIASIG := MI_FECHAINICIO + (I+1);

            <<FESTIVOS_ENTRE_FECHAS>>
              FOR MI_RS IN (SELECT ID_DE_FESTIVO
                            FROM FESTIVOS
                            WHERE ID_DE_FESTIVO BETWEEN MI_FECHAINICIO AND MI_FECHAFIN) LOOP

                IF MI_RS.ID_DE_FESTIVO =  MI_DIA THEN
                  MI_DIACTUALFESTIVO := 1;
                ELSE  
                  MI_DIACTUALFESTIVO := 0;   
                END IF;

                IF MI_RS.ID_DE_FESTIVO =  MI_DIASIG AND MI_DIACTUALFESTIVO NOT IN (1)AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 5 AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 7 THEN
                  MI_DIASINPER := MI_DIASINPER + 1;
                  MI_DIASIGFESTIVO := 1;

                END IF;        

              END LOOP FESTIVOS_ENTRE_FECHAS;  

            IF I = MI_TOTALDIAS AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 7 AND MI_DIACTUALFESTIVO <> 1 THEN
              MI_DIASINPER := MI_DIASINPER + 1;
              MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
              CONTINUE;

            ELSIF I = MI_TOTALDIAS THEN
              MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
              CONTINUE;          
            END IF;

            IF  PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 6 AND MI_TOTALDIAS > I AND MI_DIASIGFESTIVO <> 1 AND MI_DIACTUALFESTIVO <>1 THEN
             MI_DIASINPER := MI_DIASINPER + 1;
            END IF;

         END LOOP;

       --CUANDO SE TOMAN COMO HABILIES LOS DOMINGOS
      ELSIF MI_RSSOLICITUD.SABADO IN (0) AND MI_RSSOLICITUD.DOMINGO NOT IN (0) AND MI_RSSOLICITUD.FESTIVO IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => 0,
                                                            UN_DOMINGOS => -1,
                                                            UN_FESTIVOS => 0);   


            FOR I IN 0.. MI_TOTALDIAS  LOOP
               MI_DIA := MI_FECHAINICIO + I;  
               MI_DIASIG := MI_FECHAINICIO + (I+1);

                <<FESTIVOS_ENTRE_FECHAS>>
                  FOR MI_RS IN (SELECT ID_DE_FESTIVO
                                FROM FESTIVOS
                                WHERE ID_DE_FESTIVO BETWEEN MI_FECHAINICIO AND MI_FECHAFIN) LOOP

                    IF MI_RS.ID_DE_FESTIVO =  MI_DIA THEN
                      MI_DIACTUALFESTIVO := 1;
                    ELSE  
                      MI_DIACTUALFESTIVO := 0;   
                    END IF;

                    IF MI_RS.ID_DE_FESTIVO =  MI_DIASIG AND MI_DIACTUALFESTIVO NOT IN (1)AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 5 THEN
                     IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 7 THEN
                      MI_DIASINPER := MI_DIASINPER + 1;
                     END IF; 
                      MI_DIASIGFESTIVO := 1;

                    END IF;        

                  END LOOP FESTIVOS_ENTRE_FECHAS;            

               IF I = MI_TOTALDIAS AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<> 6 AND MI_DIACTUALFESTIVO <> 1 THEN
                  MI_DIASINPER := MI_DIASINPER + 1;
                  MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                  CONTINUE;

               ELSIF   I = MI_TOTALDIAS THEN
                  MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                  CONTINUE;
               END IF;

               IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) = 5 AND MI_TOTALDIAS > I AND MI_DIASIGFESTIVO <> 1 AND MI_DIACTUALFESTIVO <> 1 THEN
                MI_DIASINPER := MI_DIASINPER + 1;      
               END IF;

               IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) = 7 AND MI_TOTALDIAS > I AND MI_DIASIGFESTIVO = 1  THEN
                MI_DIASINPER := MI_DIASINPER + 1;
               END IF;

          END LOOP;
       --CUANDO SE TOMAN COMO HABILIES LOS FESTIVOS
      ELSIF MI_RSSOLICITUD.SABADO IN (0) AND MI_RSSOLICITUD.DOMINGO IN (0) AND MI_RSSOLICITUD.FESTIVO NOT IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => 0,
                                                            UN_DOMINGOS => 0,
                                                            UN_FESTIVOS => -1);   


            FOR I IN 0.. MI_TOTALDIAS  LOOP     
              IF I = MI_TOTALDIAS AND  PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) = 6 AND  PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) = 7 THEN
                 MI_DIASINPER := MI_DIASINPER + 1;
                 MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                 CONTINUE;

              ELSIF I = MI_TOTALDIAS THEN
                 MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                 CONTINUE;
              END IF;          

              IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) = 5 AND MI_TOTALDIAS > I THEN
                 MI_DIASINPER := MI_DIASINPER + 1;
              END IF;

            END LOOP;

     --CUANDO SE TOMAN COMO HABILES SABADOS Y DOMINGOS
       ELSIF MI_RSSOLICITUD.SABADO NOT IN (0) AND MI_RSSOLICITUD.DOMINGO NOT IN (0) AND MI_RSSOLICITUD.FESTIVO IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => -1,
                                                            UN_DOMINGOS => -1,
                                                            UN_FESTIVOS => 0);  


           FOR I IN 0.. MI_TOTALDIAS  LOOP  
             MI_DIA := MI_FECHAINICIO + I;  
             MI_DIASIG := MI_FECHAINICIO + (I+1);

              <<FESTIVOS_ENTRE_FECHAS>>
                  FOR MI_RS IN (SELECT ID_DE_FESTIVO
                                FROM FESTIVOS
                                WHERE ID_DE_FESTIVO BETWEEN MI_FECHAINICIO AND MI_FECHAFIN) LOOP

                    IF MI_RS.ID_DE_FESTIVO =  MI_DIA THEN
                      MI_DIACTUALFESTIVO := 1;          
                    ELSE  
                      MI_DIACTUALFESTIVO := 0; 
                    END IF;


                    IF MI_RS.ID_DE_FESTIVO =  MI_DIASIG AND MI_DIACTUALFESTIVO <> 1 THEN                 
                      MI_DIASINPER := MI_DIASINPER + 1;                 
                      MI_DIASIGFESTIVO := 1;                
                    END IF;        

                  END LOOP FESTIVOS_ENTRE_FECHAS; 

             IF I= MI_TOTALDIAS AND MI_DIACTUALFESTIVO <> 1   THEN
                MI_DIASINPER := MI_DIASINPER +1 ;
                MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                CONTINUE;
            ELSIF I= MI_TOTALDIAS THEN
                 MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                 CONTINUE;
            END IF;
           END LOOP;

     --CUANDO SE TOMAN COMO HABILES SABADOS Y FESTIVOS
      ELSIF MI_RSSOLICITUD.SABADO NOT IN (0) AND MI_RSSOLICITUD.DOMINGO IN (0) AND MI_RSSOLICITUD.FESTIVO NOT IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => -1,
                                                            UN_DOMINGOS => 0,
                                                            UN_FESTIVOS => -1);  


           FOR I IN 0.. MI_TOTALDIAS  LOOP    
              MI_DIA := MI_FECHAINICIO + I;  
              MI_DIASIG := MI_FECHAINICIO + (I+1);

             IF I = MI_TOTALDIAS AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) <> 7 THEN
                MI_DIASINPER := MI_DIASINPER + 1;
                MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                CONTINUE;         
             ELSIF   I = MI_TOTALDIAS THEN
                MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;
                CONTINUE;
             END IF;  

             IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO) <> 6 AND MI_TOTALDIAS > I THEN
                MI_DIASINPER := MI_DIASINPER + 1;
             END IF;  
           END LOOP;


     --CUANDO SE TOMAN COMO HABILES DOMINGOS Y FESTIVOS
      ELSIF MI_RSSOLICITUD.SABADO IN (0) AND MI_RSSOLICITUD.DOMINGO NOT IN (0) AND MI_RSSOLICITUD.FESTIVO NOT IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => 0,
                                                            UN_DOMINGOS => -1,
                                                            UN_FESTIVOS => -1);  


           FOR I IN 0.. MI_TOTALDIAS  LOOP    
             MI_DIA := MI_FECHAINICIO + I;  
              IF I = MI_TOTALDIAS  AND PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)<>6 THEN
                MI_DIASINPER := MI_DIASINPER + 1;
                MI_DIASPER := MI_DIASHABILES - MI_DIASINPER; 
                CONTINUE;
              ELSIF I = MI_TOTALDIAS THEN
                MI_DIASPER := MI_DIASHABILES - MI_DIASINPER; 
                CONTINUE;
              END IF;

              IF PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINICIO)= 5 THEN
                MI_DIASINPER := MI_DIASINPER + 1;
              END IF;
           END LOOP;



     --CUANDO SE TOMAN COMO HABILES SABADOS, DOMINGOS Y FESTIVOS
      ELSIF MI_RSSOLICITUD.SABADO NOT IN (0) AND MI_RSSOLICITUD.DOMINGO NOT IN (0) AND MI_RSSOLICITUD.FESTIVO NOT IN (0)   THEN   

         MI_DIASHABILES := PCK_SYSMAN_UTL.FC_DIASHABILVIATICOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHAINI => MI_FECHAINICIO, 
                                                            UN_FECHAFIN => MI_FECHAFIN, 
                                                            UN_SABADOS  => -1,
                                                            UN_DOMINGOS => -1,
                                                            UN_FESTIVOS => -1);   


          MI_DIASINPER :=  MI_DIASINPER + 1;
          MI_DIASPER := MI_DIASHABILES - MI_DIASINPER;


        END IF;                                                           

  END LOOP RECORRER_SOLICITUD;

  IF UN_PERNOCTANDO NOT IN (0) THEN  
    MI_DIASPERNOCTANDO := ABS(MI_DIASPER);
  ELSE
    MI_DIASPERNOCTANDO := ABS(MI_DIASINPER);  
  END IF;

  RETURN MI_DIASPERNOCTANDO;

END FC_DIASPERNOCTANDOVIATICO;

END PCK_VIATICOS;