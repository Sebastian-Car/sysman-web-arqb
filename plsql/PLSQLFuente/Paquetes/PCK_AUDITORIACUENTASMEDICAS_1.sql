create or replace PACKAGE BODY PCK_AUDITORIACUENTASMEDICAS AS

PROCEDURE PR_CARGAR_RIPS
/*
    NAME              : PR_CARGAR_RIPS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 18/10/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :JPULIDO
    DATE MODIFIED     :15/11/2019
    TIME              :
    MODIFICATIONS     : ADICIONAR PARAMETRO CONSECUTIVO
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE LOS ARCHIVOS RIP A SU RESPECTIVA TABLA

    @NAME:    cargarRips
    @METHOD:  POST
*/
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONSECUTIVO IN PCK_SUBTIPOS.TI_CONSECUTIVO,
  UN_CADENARIP   IN CLOB,
  UN_TIPORIP     IN VARCHAR2,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_CODIGO_CUM           PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_LINEAARCHIVO         PCK_SUBTIPOS.TI_ENTERO;  
MI_PARAMETROCOMPRO      VARCHAR2(100);
MI_BUSCAR_CONSECUTIVO   PCK_SUBTIPOS.TI_CONSECUTIVO; 
MI_MSG_EXCEPTION        NUMBER;
MI_CONSECUTIVO          NUMBER(5);

BEGIN
    
  MI_LINEAARCHIVO := 1;
  
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENARIP,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
                                               
  MI_PARAMETROCOMPRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'COMPROBANTE PARA CAUSACION DE CUENTAS MEDICAS',
                                              UN_MODULO    => 84,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);                                                

  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
    
    --INSERCION RIP CT
    IF UN_TIPORIP = 'CT' THEN
       
      MI_CAMPOS := 'COMPANIA
                   ,COD_PREST_SERV_SALUD
                   ,FECHA_REMISION
                   ,COD_ARCHIVO
                   ,TOTAL_REGISTRO
                   ,CONSECUTIVO_RIPS
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'|| MI_DATOS_COLUMNAS(4)||' 
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                  
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_CONTROL'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARCT,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;   
    
    ELSIF UN_TIPORIP = 'AF' THEN
	--INSERCION RIP AF
    
     
      MI_CAMPOS := 'COMPANIA
                    ,COD_PREST_SERV_SALUD
                    ,NOMBRE_RAZON_SOCIAL
                    ,TIPO_IDENTI_PREST_SERV_SALUD
                    ,NUM_IDENTIF_PRESTADOR
                    ,NUM_FACTURA
                    ,FECHA_EXP_FACTURA
                    ,FECHA_INICIAL
                    ,FECHA_FINAL
                    ,COD_ENTIDAD_ADMINISTRADORA
                    ,NOMBRE_ENTIDAD_ADMINISTRADORA
                    ,NUMERO_CONTRATO
                    ,PLAN_BENEFICIOS
                    ,NUMERO_POLIZA
                    ,VALOR_TOT_PAGO_COMPARTIDO
                    ,VALOR_COMISION
                    ,VALOR_TOTAL_DESCUENTOS
                    ,VALOR_NETO_A_PAGAR_ENTI_CONTR
                    ,CONSECUTIVO_RIPS
                    ,CREATED_BY
                    ,DATE_CREATED
                    ,TIPO_COMPROBANTE
                    ,APROBADO
                    ,APROBADO_POR
                    ,APROBADO_FECHA';

      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'||MI_DATOS_COLUMNAS(14)||'
                    ,'||MI_DATOS_COLUMNAS(15)||'
                    ,'||MI_DATOS_COLUMNAS(16)||'
                    ,'||MI_DATOS_COLUMNAS(17)||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE
                    ,'''||MI_PARAMETROCOMPRO||''' 
                    ,-1
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE'; 
                    
            BEGIN
              BEGIN
                SELECT DISTINCT CONSECUTIVO_RIPS
                INTO MI_BUSCAR_CONSECUTIVO
                FROM CM_ARCHIVO_TRANSACCIONES
                WHERE COMPANIA=UN_COMPANIA
                AND   COD_PREST_SERV_SALUD = MI_DATOS_COLUMNAS(1)
                AND   NUM_FACTURA = MI_DATOS_COLUMNAS(5);
                
                EXCEPTION  WHEN NO_DATA_FOUND THEN
                  MI_BUSCAR_CONSECUTIVO := 0;
              END;
              
              IF MI_BUSCAR_CONSECUTIVO NOT IN(0) THEN
                MI_MSG_EXCEPTION:=PCK_ERRORES.ERR_CM_EXISTEFACT;
                RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END IF;
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_TRANSACCIONES'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   MI_MSG_EXCEPTION:=PCK_ERRORES.ERR_CM_ISNERTARAF;
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(1).CLAVE := 'FACTURA';
                      MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(5);
                      MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                      MI_MSGERROR(2).VALOR := MI_BUSCAR_CONSECUTIVO;
                      
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => MI_MSG_EXCEPTION,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );

            END; 
            
    ELSIF UN_TIPORIP = 'US' THEN
	--INSERCION RIP US
    
      /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_USUARIOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||''' AND TIPO_IDENTIFICACION_USUARIO='''||MI_DATOS_COLUMNAS(1)||'''  AND NUM_IDENTI_USUARIO_SISTEMA ='''||MI_DATOS_COLUMNAS(2)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO'); -- CC4492 -MPEREZ
      MI_CAMPOS := 'COMPANIA
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,COD_ENTIDAD_ADMIN
                    ,TIPO_USUARIO
                    ,PRIMER_APELLIDO_USUA
                    ,SEGUNDO_APELLIDO_USUA
                    ,PRIMER_NOMBRE_USUA
                    ,SEGUNDO_NOMBRE_USUA
                    ,EDAD
                    ,UNIDAD_DE_MEDIDA_EDAD
                    ,SEXO
                    ,COD_DEPART_RESI_HABITUAL
                    ,COD_MUNI_RESIDENCIA_HABITUAL
                    ,ZONA_RESIDENCIA_HABITUAL
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO 
                    ,CREATED_BY
                    ,DATE_CREATED';
                    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'||MI_DATOS_COLUMNAS(9)||'
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||' 
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';          
                    
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_USUARIOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARUS,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;                     
             
    ELSIF UN_TIPORIP = 'AC' THEN
	--INSERCION RIP AC
        
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_CONSULTA',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
    
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,FECHA_CONSULTA
                    ,NUMERO_AUTORIZACION
                    ,CODIGO_CONSULTA
                    ,FINALIDAD_CONSULTA
                    ,CAUSA_EXTERNA
                    ,COD_DIAGNOSTICO_PRINCIPAL
                    ,COD_DIAGNOSTICO_RELACIONADO_1
                    ,COD_DIAGNOSTICO_RELACIONADO_2
                    ,COD_DIAGNOSTICO_RELACIONADO_3
                    ,TIPO_DIAGNOSTICO_PRINCIPAL
                    ,VALOR_CONSULTA
                    ,VALOR_CUOTA_MODERADORA
                    ,VALOR_NETO_A_PAGAR
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
                    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''
                    ,'''||MI_DATOS_COLUMNAS(17)||'''
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                    
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_CONSULTA'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAC,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
            
    ELSIF UN_TIPORIP = 'AP' THEN
	--INSERCION RIP AP
            
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_PROCEDIMIENTOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');  
    
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,FECHA_PROCEDIMIENTO
                    ,NUMERO_AUTORIZACION
                    ,CODIGO_PROCEDIMIENTO
                    ,AMBITO_REALI_PROCEDIMIENTO
                    ,FINALIDAD_PROCEDIMIENTO
                    ,PERSONA_ATIENDE
                    ,DIAGNOSTICO_PRINCIPAL
                    ,DIAGNOSTICO_RELACIONADO
                    ,COMPLICACION
                    ,FORMA_REALIZA_ACTO_QUIRURGICO
                    ,VALOR_PROCEDIMIENTO
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';      
                    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'||MI_DATOS_COLUMNAS(15)||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                    
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_PROCEDIMIENTOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAP,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;                    
           
    ELSIF UN_TIPORIP = 'AU' THEN
	--INSERCION RIP AU
          
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_URGE_OBSERVACION',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');   
    
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,FECHA_INGRESO_USUARIO_SISTEMA
                    ,HORA_INGRESO_USUARIO
                    ,NUMERO_AUTORIZACION
                    ,CAUSA_EXTERNA
                    ,DIAGNOSTICO_SALIDA
                    ,DIAGNOSTICO_RELACIONADO_1
                    ,DIAGNOSTICO_RELACIONADO_2
                    ,DIAGNOSTICO_RELACIONADO_3
                    ,DESTINO_USUARIO_SALIDA_OBSER
                    ,ESTADO_SALIDA
                    ,CAUSA_BASICA_MUERTE_URGENCIAS
                    ,FECHA_SALIDA_USUARIO_OBSERVA
                    ,HORA_SALIDA_USUARIO_OBSERVA
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';      
                    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                     ,TO_DATE('''||MI_DATOS_COLUMNAS(5)||' '||MI_DATOS_COLUMNAS(6)|| ''', ''DD/MM/YYYY HH24:MI:SS'')                       
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''   
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(16)||' '||MI_DATOS_COLUMNAS(17)|| ''', ''DD/MM/YYYY HH24:MI:SS'')
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';   
                    
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_URGE_OBSERVACION'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAU,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;                      
    
    ELSIF UN_TIPORIP = 'AH' THEN
	--INSERCION RIP AH
          
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_HOSPITALIZACION',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');    
    
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,VIA_INGRESO_INSTITUCION
                    ,FECHA_INGRESO_USUARIO_SISTEMA
                    ,HORA_INGRESO_USUARIO
                    ,NUMERO_AUTORIZACION
                    ,CAUSA_EXTERNA
                    ,DIAGNOSTICO_PRINCIPAL_INGRESO
                    ,DIAGNOSTICO_PRINCIPAL_EGRESO
                    ,DIAGNOSTICO_RELACIONADO_1
                    ,DIAGNOSTICO_RELACIONADO_2
                    ,DIAGNOSTICO_RELACIONADO_3
                    ,DIAGNOSTICO_COMPILACION
                    ,ESTADO_SALIDA
                    ,DIAG_CAUSA_BASIC_MUERTE
                    ,FECHA_EGRESO_USUARIO
                    ,HORA_EGRESO_USUARIO
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';      
                    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(6)||' '||MI_DATOS_COLUMNAS(7)|| ''', ''DD/MM/YYYY HH24:MI:SS'')                       
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''   
                    ,'''||MI_DATOS_COLUMNAS(17)|| '''
                    ,'''||MI_DATOS_COLUMNAS(18)|| '''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(18)||' '||MI_DATOS_COLUMNAS(19)|| ''', ''DD/MM/YYYY HH24:MI:SS'')
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';  
                    
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_HOSPITALIZACION'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAH,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;                     
              
    ELSIF UN_TIPORIP = 'AN' THEN
	--INSERCION RIP AN 
          
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_RECIEN_NACIDOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');    
    
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTI_MADRE
                    ,NUMERO_IDENTI_MADRE
                    ,FECHA_RECIEN_NACIDO
                    ,HORA_NACIMIENTO
                    ,EDAD_GESTACIONAL
                    ,CONTROL_PRENATAL
                    ,SEXO
                    ,PESO
                    ,DIAGNOSTICO_RECIEN_NACIDO
                    ,CAUSA_BASICA_MUERTE
                    ,FECHA_MUERTE_NACIDO
                    ,HORA_MUERTE_NACIDO
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';   

      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(5)||''||MI_DATOS_COLUMNAS(6)|| ''', ''DD/MM/YYYY HH24:MI:SS'')                                           
                    ,'||MI_DATOS_COLUMNAS(7)||'
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'||MI_DATOS_COLUMNAS(10)||'
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(13)||''||MI_DATOS_COLUMNAS(14)|| ''', ''DD/MM/YYYY HH24:MI:SS'')
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE'; 
                                 
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_RECIEN_NACIDOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAN,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;   
            
    ELSIF UN_TIPORIP = 'AM' THEN
	--INSERCION RIP AM
    
      MI_CODIGO_CUM := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => MI_DATOS_COLUMNAS(6),
                                               UN_DELIMITADOR  => '-');
                                               
    IF NOT MI_CODIGO_CUM.EXISTS(2) THEN
        MI_CODIGO_CUM(1) := MI_DATOS_COLUMNAS(6);
        MI_CODIGO_CUM(2) := 999;
    END IF ;
          
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_MEDICAMENTOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO'); 
            
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,NUMERO_AUTORIZACION
                    ,CODIGO_MEDICAMENTO
                    ,TIPO_MEDICAMENTO
                    ,NOMBRE_GENERICO_MEDICAMENTO
                    ,FORMA_FARMACEUTICA
                    ,CONCENTRACION_MEDICAMENTO
                    ,UNIDAD_MEDIDA_MEDICAMENTO
                    ,NUMERO_UNIDADES
                    ,VALOR_UNITARIO_MEDICAMENTO
                    ,VALOR_TOTAL_MEDICAMENTO
                    ,MEDICAMENTO
                    ,CONSECUTIVOCUM
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
    
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'||MI_DATOS_COLUMNAS(12)||'
                    ,'||MI_DATOS_COLUMNAS(13)||'
                    ,'||MI_DATOS_COLUMNAS(14)||'
                    ,'''||MI_CODIGO_CUM(1)||'''
                    ,'|| MI_CODIGO_CUM(2)||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';     
                    
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_MEDICAMENTOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAM,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;     
                
    ELSIF UN_TIPORIP = 'AT' THEN
	--INSERCION RIP AT 
          
          /* Se adiciona el campo autoincremental CONSECUTIVO*/
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_OTROS_SERVICIOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(1)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO'); 
          
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,NUMERO_AUTORIZACION
                    ,TIPO_SERVICIO
                    ,CODIGO_SERVICIO
                    ,NOMBRE_SERVICIO
                    ,CANTIDAD
                    ,VALOR_UNI_MATERIAL_INSUMO
                    ,VALOR_TOTAL_MATERIAL_INSUMO
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';   

      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'||MI_DATOS_COLUMNAS(10)||'
                    ,'||MI_DATOS_COLUMNAS(11)||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';      
                    
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_OTROS_SERVICIOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_ISNERTARAT,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;                         
                    
    END IF;
   
   MI_LINEAARCHIVO := MI_LINEAARCHIVO + 1; 
  END LOOP;
    
END PR_CARGAR_RIPS ;



PROCEDURE PR_ELIMINAR_RIPS
/*
    NAME              : PR_ELIMNAR_RIPS
    AUTHOR MIGRACION  : JULIAN CAMILO PULIDO DELGADO
    DATE MIGRADOR     : 19/11/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO QUE ELIMINA LOS DATOS DE LOS ARCHIVOS RIP

    @NAME:    eliminarRips
    @METHOD:  POST
*/
( 
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONSECUTIVO IN PCK_SUBTIPOS.TI_CONSECUTIVO
)  
AS 
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;

BEGIN
   
    --  ELIMINAR RIP CT
     MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND CONSECUTIVO_ARCHIVO       = '||UN_CONSECUTIVO||'';
       
          BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_AUDITORIA_GLOSAS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
       MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND CONSECUTIVO_RIPS       = '||UN_CONSECUTIVO||'';
                   
       BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_CONTROL',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_TRANSACCIONES',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;  
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_USUARIOS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;  
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_CONSULTA',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END; 
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_PROCEDIMIENTOS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
       BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_URGE_OBSERVACION',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_HOSPITALIZACION',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_RECIEN_NACIDOS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_MEDICAMENTOS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
      
      BEGIN
            BEGIN 
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'CM_ARCHIVO_OTROS_SERVICIOS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );
       
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'COMPANIA';
                        MI_MSGERROR(2).VALOR := UN_COMPANIA;
                         
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
           BEGIN
     BEGIN
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'RESULTADOS_VALIDACION_RIPS',
                                           UN_ACCION      =>'E',
                                           UN_CONDICION   =>MI_CONDICION
                                           );
     EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PLANDES;
     END;
     EXCEPTION
          WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
             MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
             MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
             MI_MSGERROR(2).CLAVE := 'COMPANIA';
             MI_MSGERROR(2).VALOR := UN_COMPANIA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD =>SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                ,UN_REEMPLAZOS   => MI_MSGERROR
                                );
     END;
      BEGIN
         BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       =>'VALIDACION_RIPS',
                                               UN_ACCION      =>'E',
                                               UN_CONDICION   =>MI_CONDICION
                                               );
         EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PLANDES;
         END;
     EXCEPTION
          WHEN  PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
             MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
             MI_MSGERROR(1).VALOR := UN_CONSECUTIVO;
             MI_MSGERROR(2).CLAVE := 'COMPANIA';
             MI_MSGERROR(2).VALOR := UN_COMPANIA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD =>SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_CM_ISNERTARAT
                                ,UN_REEMPLAZOS   => MI_MSGERROR
                                );
     END;

END PR_ELIMINAR_RIPS ;


FUNCTION FC_CAUSACION_CUENTAS_MEDICAS
/*
    NAME              : PR_CAUSACION_CUENTAS_MEDICAS
    AUTHOR MIGRACION  : JULIAN CAMILO PULIDO DELGADO
    DATE MIGRADOR     : 04/12/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 03/02/2020
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO QUE SE ENCARGA DE CAUSAR UNA FACTURA

    @NAME:    causacionCuentasMedicas
    @METHOD:  POST
*/
( 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FACTURA          IN VARCHAR2,
    UN_CODIGO_PRESTADOR IN VARCHAR2,
    UN_TIPO_COMPROBANTE IN VARCHAR2,
    UN_ANO              IN NUMBER,
    UN_FECHA            IN DATE,
    UN_CODIGO_INTERFASE IN VARCHAR2,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
    UN_RADICADO         IN VARCHAR2,
    UN_CONSECUTIVORIP   IN PCK_SUBTIPOS.TI_CONSECUTIVO,
    UN_AGRUPADO         IN NUMBER
)  RETURN VARCHAR2
IS 
    MI_NUMERO             NUMBER:=0;
    MI_REGISTROS          NUMBER:=0;
    MI_RS                 SYS_REFCURSOR;
    MI_RS1                SYS_REFCURSOR;
    MI_CONSECUTIVO        NUMBER:=0;
    MI_CONSECUTIVOFACTURA NUMBER(5,0);
    MI_DESCRIPCION        VARCHAR2(255);
    MI_VALOR_CUENTA       NUMBER:=0;
    MI_RTA                CLOB;
    MI_VALORPAGARENTIDAD  NUMBER(20,2);
    MI_NITPRESTADOR       VARCHAR2(20 CHAR); 
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;      
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    
    MI_CONSECUTIVORIP     PCK_SUBTIPOS.TI_CONSECUTIVO;
   
BEGIN

     MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                  ,UN_ANIO         => UN_ANO
                                                                  ,UN_TIPO         => UN_TIPO_COMPROBANTE
                                                                  ,UN_NUMERO       => 0);                                                                   
    IF (UN_AGRUPADO = 0) THEN
        MI_DESCRIPCION:='CAUSACION CUENTAS MEDICAS DEL PRESTADOR '||UN_CODIGO_PRESTADOR||' FACTURA NRO '||UN_FACTURA||' RADICADO No '||UN_RADICADO||' CONSECUTIVO RIP '||UN_CONSECUTIVORIP;
    /* Cambie el lugar de esta consulta, estaba despues del llamado a la función FC_CONTABILIZAR 7715707 mperez*/
        BEGIN
            SELECT NVL(VALOR_NETO_A_PAGAR_ENTI_CONTR,0) ,
                   NUM_IDENTIF_PRESTADOR
            INTO   MI_VALORPAGARENTIDAD,
                   MI_NITPRESTADOR            
            FROM CM_ARCHIVO_TRANSACCIONES 
            WHERE COMPANIA             = UN_COMPANIA
              AND NUM_FACTURA          = UN_FACTURA
              AND COD_PREST_SERV_SALUD = UN_CODIGO_PRESTADOR;
          END;                                                                  
        
        FOR MI_RS IN (
        SELECT
            CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA,
            NVL(VALOR_TOT_PAGO_COMPARTIDO,0)            VALOR_TOT_PAGO_COMPARTIDO,
            NVL(VALOR_COMISION,0)                       VALOR_COMISION,
            NVL(VALOR_TOTAL_DESCUENTOS,0)               VALOR_TOTAL_DESCUENTOS,
          --  NVL(VALOR_NETO_A_PAGAR_ENTI_CONTR,0)        VALOR_NETO_A_PAGAR_ENTI_CONTR,
            NVL(P.PVALOR_PROCEDIMIENTO,0)               PVALOR_PROCEDIMIENTO,
            NVL(C.CVALOR_CONSULTA,0)                    CVALOR_CONSULTA,
            NVL(C.CVALOR_CUOTA_MODERADORA,0)            CVALOR_CUOTA_MODERADORA,
          --  NVL(C.CVALOR_NETO_A_PAGAR,0)                CVALOR_NETO_A_PAGAR,
            NVL(MVALOR_TOTAL_MEDICAMENTO,0)             MVALOR_TOTAL_MEDICAMENTO,
            NVL(OVALOR_TOTAL_MATERIAL_INSUMO,0)         OVALOR_TOTAL_MATERIAL_INSUMO
        FROM
            CM_ARCHIVO_TRANSACCIONES 
        LEFT JOIN (
            SELECT
                COMPANIA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_CONSULTA)         CVALOR_CONSULTA,
                SUM(VALOR_CUOTA_MODERADORA) CVALOR_CUOTA_MODERADORA,
                SUM(VALOR_NETO_A_PAGAR)     CVALOR_NETO_A_PAGAR,
                NUM_FACTURA
            FROM
                CM_ARCHIVO_CONSULTA
            GROUP BY
                NUM_FACTURA,
                COMPANIA,
                CONSECUTIVO_RIPS

            ) C ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = C.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = C.NUM_FACTURA
        AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= C.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_TOTAL_MEDICAMENTO) MVALOR_TOTAL_MEDICAMENTO,
                NUM_FACTURA
            FROM
                CM_ARCHIVO_MEDICAMENTOS
            GROUP BY
                NUM_FACTURA,
                COMPANIA,
                CONSECUTIVO_RIPS
            ) M ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = M.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = M.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= M.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_TOTAL_MATERIAL_INSUMO) OVALOR_TOTAL_MATERIAL_INSUMO
            FROM
                CM_ARCHIVO_OTROS_SERVICIOS
            GROUP BY
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS
            ) O ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = O.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = O.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= O.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_PROCEDIMIENTO) PVALOR_PROCEDIMIENTO
            FROM
                CM_ARCHIVO_PROCEDIMIENTOS
            GROUP BY
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS
            ) P ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = P.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = P.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= P.CONSECUTIVO_RIPS
         WHERE
        CM_ARCHIVO_TRANSACCIONES.COMPANIA                    = UN_COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA             = UN_FACTURA
        AND CM_ARCHIVO_TRANSACCIONES.COD_PREST_SERV_SALUD    = UN_CODIGO_PRESTADOR        
        AND CM_ARCHIVO_TRANSACCIONES.APROBADO NOT IN (0)
        AND CM_ARCHIVO_TRANSACCIONES.CAUSADO  IN (0)
    ) LOOP    
            FOR MI_RS1 IN (
                SELECT 
                        ANO,
                        AUXILIAR,
                        CENTRO_COSTO,
                        CODIGO_TRANSACCION,
                        CUENTA_CREDITO,
                        CUENTA_DEBITO,
                        FUENTE_RECURSO,
                        REFERENCIA,
                        SUCURSAL,
                        TERCERO,
                        VARIABLE
                FROM   CM_DET_CAUSACION_AUTOMATICA
                WHERE   COMPANIA            =UN_COMPANIA
                AND     ANO                 =UN_ANO
                AND     CODIGO_TRANSACCION  =UN_CODIGO_INTERFASE
            ) LOOP
    
               IF MI_RS1.CUENTA_DEBITO IS NOT NULL THEN
               
                    BEGIN
                         SELECT CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                                   --  WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                                   --  WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO
                                                     
                                                ELSE
                                                    0
                                                END  
                        INTO   MI_VALOR_CUENTA
                        FROM   DUAL;
                     EXCEPTION WHEN NO_DATA_FOUND THEN
                         MI_VALOR_CUENTA := 0;
                     END;
                     
                    IF MI_VALOR_CUENTA > 0 THEN
                        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
                        MI_REGISTROS := MI_REGISTROS + FC_ENVIAR_PLAN_AJUSTES(
                                                UN_COMPANIA,
                                                UN_ANO,
                                                UN_TIPO_COMPROBANTE,
                                                MI_NUMERO,
                                                MI_CONSECUTIVO,
                                                UN_FECHA,
                                                MI_RS1.CUENTA_DEBITO,
                                                CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                                   --  WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                                   --  WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO
                                                     
                                                ELSE
                                                    0
                                                END,
                                                0,
                                                CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN MI_NITPRESTADOR ELSE  MI_RS1.TERCERO END, /*7715707 mperez*/
                                                CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN '001' ELSE  MI_RS1.SUCURSAL END,
                                                MI_RS1.CENTRO_COSTO,
                                                MI_RS1.AUXILIAR,
                                                MI_RS1.REFERENCIA,
                                                MI_RS1.FUENTE_RECURSO,
                                                UN_FACTURA);
                        END IF;
                    END IF;
                    IF MI_RS1.CUENTA_CREDITO IS NOT NULL THEN
                    
                        BEGIN
                            SELECT CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                                --     WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                                 --    WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO
                                                         
                                                    ELSE
                                                        0
                                                    END  
                            INTO   MI_VALOR_CUENTA
                            FROM   DUAL;
                         EXCEPTION WHEN NO_DATA_FOUND THEN
                             MI_VALOR_CUENTA := 0;
                         END;
                         
                        IF MI_VALOR_CUENTA > 0 THEN
                        
                            MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
                            MI_REGISTROS := MI_REGISTROS + FC_ENVIAR_PLAN_AJUSTES(    
                                                    UN_COMPANIA,
                                                    UN_ANO,
                                                    UN_TIPO_COMPROBANTE,
                                                    MI_NUMERO,
                                                    MI_CONSECUTIVO,
                                                    UN_FECHA,
                                                    MI_RS1.CUENTA_CREDITO,
                                                    0,
                                                    CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                                  --   WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                                  --   WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO
                                                         
                                                    ELSE
                                                        0
                                                    END,
                                                    CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN MI_NITPRESTADOR ELSE  MI_RS1.TERCERO END, /*7715707 mperez*/
                                                    CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN '001' ELSE  MI_RS1.SUCURSAL END,
                                                    MI_RS1.CENTRO_COSTO,
                                                    MI_RS1.AUXILIAR,
                                                    MI_RS1.REFERENCIA,
                                                    MI_RS1.FUENTE_RECURSO,
                                                    UN_FACTURA);
                        END IF;
                    END IF;
    
            END LOOP;
        END LOOP;    
        
        MI_RTA:=PCK_CONTABILIZAR.FC_CONTABILIZAR(
                                UN_COMPANIA  => UN_COMPANIA,
                                UN_TIPOCOMPROBANTE => UN_TIPO_COMPROBANTE,
                                UN_NUMERO => MI_NUMERO,
                                UN_ANO => UN_ANO,
                                UN_FECHA => UN_FECHA,
                                UN_TERCERO => MI_NITPRESTADOR, /*Modifique aquí, cambie UN_CODIGO_PRESTADOR por MI_NITPRESTADOR 7715707 mperez */
                                UN_SUCURSAL => '001',
                              
                                UN_DESCRIPCION => MI_DESCRIPCION,
                                UN_SIMPLE => 0,
                                UN_INDIMPRESION => 0,
                               
                                UN_TEXTO =>  MI_DESCRIPCION,
                                
                                 UN_NRO_DOCUMENTO => UN_FACTURA,
                              
                                UN_USUARIO =>  UN_USUARIO);                      
                               
        MI_CONSECUTIVOFACTURA :=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_AUDITORIA_GLOSAS',
                                                                 UN_CRITERIO => 'COMPANIA            = '''||UN_COMPANIA||''' 
                                                                             AND CONSECUTIVO_ARCHIVO = '||UN_CONSECUTIVORIP||' 
                                                                             AND NUM_FACTURA         = '''||UN_FACTURA||'''',
                                                                 UN_CAMPO    =>'CONSECUTIVO_FACTURA') ;                        
                                                                 
        MI_CAMPOS := 'COMPANIA,
                     CONSECUTIVO_ARCHIVO,
                     CONSECUTIVO_FACTURA,
                     NUM_FACTURA,
                     COD_PREST_SERV_SALUD,
                     NUM_IDENTIF_PRESTADOR,
                     VALOR_NETO_A_PAGAR_ENTI_CONTR';
        
        MI_VALORES := ''''||UN_COMPANIA||''',
                       '||UN_CONSECUTIVORIP||',
                       '||MI_CONSECUTIVOFACTURA||',
                       '''||UN_FACTURA||''',
                       '''||UN_CODIGO_PRESTADOR||''',                  
                       '''||MI_NITPRESTADOR||''',
                       '||MI_VALORPAGARENTIDAD||'';     
        
        BEGIN        
            BEGIN    
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_AUDITORIA_GLOSAS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
            END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN                
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE        
                           );
        END; 
    ELSE
        MI_DESCRIPCION:='CAUSACION CUENTAS MEDICAS DEL PRESTADOR '||UN_CODIGO_PRESTADOR|| ' RADICADO No '||UN_RADICADO||' CONSECUTIVO RIP '||UN_CONSECUTIVORIP;
        
        BEGIN
            SELECT DISTINCT
                   NUM_IDENTIF_PRESTADOR
            INTO   MI_NITPRESTADOR            
            FROM CM_ARCHIVO_TRANSACCIONES 
            WHERE COMPANIA             = UN_COMPANIA
              AND CONSECUTIVO_RIPS     = UN_CONSECUTIVORIP
              AND COD_PREST_SERV_SALUD = UN_CODIGO_PRESTADOR;
          END;  
          
          FOR MI_RS IN (
        SELECT
                CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS,
                SUM(NVL(VALOR_TOT_PAGO_COMPARTIDO,0))       VALOR_TOT_PAGO_COMPARTIDO,
                SUM(NVL(VALOR_COMISION,0))                  VALOR_COMISION,
                SUM(NVL(VALOR_TOTAL_DESCUENTOS,0))          VALOR_TOTAL_DESCUENTOS,
              --  SUM(NVL(VALOR_NETO_A_PAGAR_ENTI_CONTR,0))   VALOR_NETO_A_PAGAR_ENTI_CONTR,
                SUM(NVL(P.PVALOR_PROCEDIMIENTO,0))         PVALOR_PROCEDIMIENTO,
                SUM(NVL(C.CVALOR_CONSULTA,0))               CVALOR_CONSULTA,
                SUM(NVL(C.CVALOR_CUOTA_MODERADORA,0))       CVALOR_CUOTA_MODERADORA,
              --  SUM(NVL(C.CVALOR_NETO_A_PAGAR,0))           CVALOR_NETO_A_PAGAR,
                SUM(NVL(MVALOR_TOTAL_MEDICAMENTO,0))        MVALOR_TOTAL_MEDICAMENTO,
                SUM(NVL(OVALOR_TOTAL_MATERIAL_INSUMO,0))    OVALOR_TOTAL_MATERIAL_INSUMO
        FROM
            CM_ARCHIVO_TRANSACCIONES 
        LEFT JOIN (
            SELECT
                COMPANIA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_CONSULTA)         CVALOR_CONSULTA,
                SUM(VALOR_CUOTA_MODERADORA) CVALOR_CUOTA_MODERADORA,
                SUM(VALOR_NETO_A_PAGAR)     CVALOR_NETO_A_PAGAR,
                NUM_FACTURA
            FROM
                CM_ARCHIVO_CONSULTA
            GROUP BY
                NUM_FACTURA,
                COMPANIA,
                CONSECUTIVO_RIPS

            ) C ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = C.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = C.NUM_FACTURA
        AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= C.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_TOTAL_MEDICAMENTO) MVALOR_TOTAL_MEDICAMENTO,
                NUM_FACTURA
            FROM
                CM_ARCHIVO_MEDICAMENTOS
            GROUP BY
                NUM_FACTURA,
                COMPANIA,
                CONSECUTIVO_RIPS
            ) M ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = M.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = M.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= M.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_TOTAL_MATERIAL_INSUMO) OVALOR_TOTAL_MATERIAL_INSUMO
            FROM
                CM_ARCHIVO_OTROS_SERVICIOS
            GROUP BY
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS
            ) O ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = O.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = O.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= O.CONSECUTIVO_RIPS
        LEFT JOIN (
            SELECT
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS,
                SUM(VALOR_PROCEDIMIENTO) PVALOR_PROCEDIMIENTO
            FROM
                CM_ARCHIVO_PROCEDIMIENTOS
            GROUP BY
                COMPANIA,
                NUM_FACTURA,
                CONSECUTIVO_RIPS
            ) P ON CM_ARCHIVO_TRANSACCIONES.COMPANIA = P.COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA = P.NUM_FACTURA
         AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS= P.CONSECUTIVO_RIPS
         WHERE
        CM_ARCHIVO_TRANSACCIONES.COMPANIA                    = UN_COMPANIA
        AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS        = UN_CONSECUTIVORIP
        AND CM_ARCHIVO_TRANSACCIONES.COD_PREST_SERV_SALUD    = UN_CODIGO_PRESTADOR        
        AND CM_ARCHIVO_TRANSACCIONES.APROBADO NOT IN (0)
        AND CM_ARCHIVO_TRANSACCIONES.CAUSADO  IN (0)
        GROUP BY CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS
    ) LOOP    
            FOR MI_RS1 IN (
                SELECT 
                        ANO,
                        AUXILIAR,
                        CENTRO_COSTO,
                        CODIGO_TRANSACCION,
                        CUENTA_CREDITO,
                        CUENTA_DEBITO,
                        FUENTE_RECURSO,
                        REFERENCIA,
                        SUCURSAL,
                        TERCERO,
                        VARIABLE
                FROM   CM_DET_CAUSACION_AUTOMATICA
                WHERE   COMPANIA            =UN_COMPANIA
                AND     ANO                 =UN_ANO
                AND     CODIGO_TRANSACCION  =UN_CODIGO_INTERFASE
            ) LOOP
    
               IF MI_RS1.CUENTA_DEBITO IS NOT NULL THEN               
                    BEGIN
                         SELECT CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                               --      WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                               --      WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO                                                     
                                                ELSE
                                                    0
                                                END  
                        INTO   MI_VALOR_CUENTA
                        FROM   DUAL;
                     EXCEPTION WHEN NO_DATA_FOUND THEN
                         MI_VALOR_CUENTA := 0;
                     END;
                     
                    IF MI_VALOR_CUENTA > 0 THEN
                        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
                        MI_REGISTROS := MI_REGISTROS + FC_ENVIAR_PLAN_AJUSTES2(
                                                UN_COMPANIA,
                                                UN_ANO,
                                                UN_TIPO_COMPROBANTE,
                                                MI_NUMERO,
                                                MI_CONSECUTIVO,
                                                UN_FECHA,
                                                MI_RS1.CUENTA_DEBITO,
                                                MI_VALOR_CUENTA,
                                                0,
                                                CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN MI_NITPRESTADOR ELSE  MI_RS1.TERCERO END, /*7715707 mperez*/
                                                CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN '001' ELSE  MI_RS1.SUCURSAL END,
                                                MI_RS1.CENTRO_COSTO,
                                                MI_RS1.AUXILIAR,
                                                MI_RS1.REFERENCIA,
                                                MI_RS1.FUENTE_RECURSO,
                                                UN_CONSECUTIVORIP);
                        END IF;
                    END IF;
                    IF MI_RS1.CUENTA_CREDITO IS NOT NULL THEN                    
                        BEGIN
                            SELECT CASE WHEN MI_RS1.VARIABLE='VALOR_TOT_PAGO_COMPARTIDO' THEN  MI_RS.VALOR_TOT_PAGO_COMPARTIDO
                                                     WHEN MI_RS1.VARIABLE='VALOR_COMISION' THEN  MI_RS.VALOR_COMISION
                                                     WHEN MI_RS1.VARIABLE='VALOR_TOTAL_DESCUENTOS' THEN  MI_RS.VALOR_TOTAL_DESCUENTOS
                                                 --    WHEN MI_RS1.VARIABLE='VALOR_NETO_A_PAGAR_ENTI_CONTR' THEN  MI_RS.VALOR_NETO_A_PAGAR_ENTI_CONTR
                                                     WHEN MI_RS1.VARIABLE='PVALOR_PROCEDIMIENTO' THEN  MI_RS.PVALOR_PROCEDIMIENTO
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CONSULTA' THEN  MI_RS.CVALOR_CONSULTA
                                                     WHEN MI_RS1.VARIABLE='CVALOR_CUOTA_MODERADORA' THEN  MI_RS.CVALOR_CUOTA_MODERADORA
                                                 --    WHEN MI_RS1.VARIABLE='CVALOR_NETO_A_PAGAR' THEN  MI_RS.CVALOR_NETO_A_PAGAR
                                                     WHEN MI_RS1.VARIABLE='MVALOR_TOTAL_MEDICAMENTO' THEN  MI_RS.MVALOR_TOTAL_MEDICAMENTO
                                                     WHEN MI_RS1.VARIABLE='OVALOR_TOTAL_MATERIAL_INSUMO' THEN  MI_RS.OVALOR_TOTAL_MATERIAL_INSUMO                                                         
                                                    ELSE
                                                        0
                                                    END  
                            INTO   MI_VALOR_CUENTA
                            FROM   DUAL;
                         EXCEPTION WHEN NO_DATA_FOUND THEN
                             MI_VALOR_CUENTA := 0;
                         END;
                         
                        IF MI_VALOR_CUENTA > 0 THEN                        
                            MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
                            MI_REGISTROS := MI_REGISTROS + FC_ENVIAR_PLAN_AJUSTES2(    
                                                    UN_COMPANIA,
                                                    UN_ANO,
                                                    UN_TIPO_COMPROBANTE,
                                                    MI_NUMERO,
                                                    MI_CONSECUTIVO,
                                                    UN_FECHA,
                                                    MI_RS1.CUENTA_CREDITO,
                                                    0,
                                                    MI_VALOR_CUENTA,
                                                    CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN MI_NITPRESTADOR ELSE  MI_RS1.TERCERO END, /*7715707 mperez*/
                                                    CASE WHEN MI_RS1.TERCERO=RPAD('9',LENGTH(MI_RS1.TERCERO),'9') THEN '001' ELSE  MI_RS1.SUCURSAL END,
                                                    MI_RS1.CENTRO_COSTO,
                                                    MI_RS1.AUXILIAR,
                                                    MI_RS1.REFERENCIA,
                                                    MI_RS1.FUENTE_RECURSO,
                                                    UN_CONSECUTIVORIP);
                        END IF;
                    END IF;    
            END LOOP;
        END LOOP;  
        
        MI_REGISTROS := FC_ENVIAR_PLAN_AJ_CAUSACION();
        
        MI_RTA:=PCK_CONTABILIZAR.FC_CONTABILIZAR(
                                UN_COMPANIA  => UN_COMPANIA,
                                UN_TIPOCOMPROBANTE => UN_TIPO_COMPROBANTE,
                                UN_NUMERO => MI_NUMERO,
                                UN_ANO => UN_ANO,
                                UN_FECHA => UN_FECHA,
                                UN_TERCERO => MI_NITPRESTADOR, /*Modifique aquí, cambie UN_CODIGO_PRESTADOR por MI_NITPRESTADOR 7715707 mperez */
                                UN_SUCURSAL => '001',                              
                                UN_DESCRIPCION => MI_DESCRIPCION,
                                UN_SIMPLE => 0,
                                UN_INDIMPRESION => 0,                               
                                UN_TEXTO =>  MI_DESCRIPCION,                                
                                UN_NRO_DOCUMENTO => UN_FACTURA,                              
                                UN_USUARIO =>  UN_USUARIO);         
    
    
        FOR MI_RS1 IN (
            SELECT CM_ARCHIVO_TRANSACCIONES.NUM_FACTURA, 
                   NVL(CM_ARCHIVO_TRANSACCIONES.VALOR_NETO_A_PAGAR_ENTI_CONTR,0) VALORPAGARENTIDAD
              FROM CM_ARCHIVO_TRANSACCIONES 
             WHERE CM_ARCHIVO_TRANSACCIONES.COMPANIA                = UN_COMPANIA
               AND CM_ARCHIVO_TRANSACCIONES.CONSECUTIVO_RIPS        = UN_CONSECUTIVORIP
               AND CM_ARCHIVO_TRANSACCIONES.COD_PREST_SERV_SALUD    = UN_CODIGO_PRESTADOR
        ) LOOP
            MI_CONSECUTIVOFACTURA :=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_AUDITORIA_GLOSAS',
                                                                 UN_CRITERIO => 'COMPANIA            = '''||UN_COMPANIA||''' 
                                                                             AND CONSECUTIVO_ARCHIVO = '||UN_CONSECUTIVORIP||' 
                                                                             AND NUM_FACTURA         = '''||MI_RS1.NUM_FACTURA||'''',
                                                                 UN_CAMPO    =>'CONSECUTIVO_FACTURA') ;                        
                                                                 
        MI_CAMPOS := 'COMPANIA,
                     CONSECUTIVO_ARCHIVO,
                     CONSECUTIVO_FACTURA,
                     NUM_FACTURA,
                     COD_PREST_SERV_SALUD,
                     NUM_IDENTIF_PRESTADOR,
                     VALOR_NETO_A_PAGAR_ENTI_CONTR';
        
        MI_VALORES := ''''||UN_COMPANIA||''',
                       '||UN_CONSECUTIVORIP||',
                       '||MI_CONSECUTIVOFACTURA||',
                       '''||MI_RS1.NUM_FACTURA||''',
                       '''||UN_CODIGO_PRESTADOR||''',                  
                       '''||MI_NITPRESTADOR||''',
                       '||MI_RS1.VALORPAGARENTIDAD||'';     
        
        BEGIN        
            BEGIN    
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_AUDITORIA_GLOSAS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
        
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
            END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN                
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE        
                           );
        END;     
        END LOOP;
    END IF;
    RETURN MI_RTA;
END FC_CAUSACION_CUENTAS_MEDICAS;

FUNCTION FC_ENVIAR_PLAN_AJUSTES
(
/*
    NAME              : PR_CAUSACION_CUENTAS_MEDICAS
    AUTHOR MIGRACION  : JULIAN CAMILO PULIDO DELGADO
    DATE MIGRADOR     : 04/12/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : INSERTA UN NUEVO REGISTRO EN LA TABLA TEMPORAL PLAN AJUSTES

    @NAME:    enviarPlanAjustes
    */
	UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO            IN NUMBER,
	UN_TIPO           IN VARCHAR2,
	UN_NUMERO         IN NUMBER,
	UN_CONSECUTIVO    IN NUMBER,
	UN_FECHA          IN DATE,
	UN_CUENTA         IN VARCHAR2,
	UN_VALOR_DEBITO   IN NUMBER,
	UN_VALOR_CREDITO  IN NUMBER,
	UN_TERCERO        IN VARCHAR2,
	UN_SUCURSAL       IN VARCHAR2,
	UN_CENTRO_COSTO   IN VARCHAR2,
	UN_AUXILIAR       IN VARCHAR2,
	UN_REFERENCIA     IN VARCHAR2,
	UN_FUENTE_RECURSO IN VARCHAR2,
	UN_FACTURA        IN VARCHAR2
) RETURN NUMBER
IS
	MI_VAL NUMBER(1) := 0;
    MI_NATURALEZA VARCHAR2(1);
BEGIN
      BEGIN
        SELECT NATURALEZA 
        INTO MI_NATURALEZA
        FROM   PLAN_CONTABLE
        WHERE  COMPANIA=UN_COMPANIA
          AND  ANO=UN_ANO
          AND  CODIGO=UN_CUENTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NATURALEZA:='D';
      END;

	INSERT INTO TEMP_PLANA_AJUSTES (
		COMPANIA,
		ANO,
		TIPO_CPTE,
        COMPROBANTE,
        CONSECUTIVO,
		FECHA,
		CUENTA,
        NATURALEZA,
		VALOR_DEBITO,
		VALOR_CREDITO,
		TERCERO,
		SUCURSAL,
		CENTRO_COSTO,
		AUXILIAR,
		REFERENCIA,
		FUENTE_RECURSOS,
		NRO_DOCUMENTO
		)
	VALUES
	(
		UN_COMPANIA,
		UN_ANO,
		UN_TIPO,
        UN_NUMERO,
        UN_CONSECUTIVO,
		UN_FECHA,
		UN_CUENTA,
        MI_NATURALEZA,
		UN_VALOR_DEBITO,
		UN_VALOR_CREDITO,
		UN_TERCERO,
		UN_SUCURSAL,
		UN_CENTRO_COSTO,
		UN_AUXILIAR,
		UN_REFERENCIA,
		UN_FUENTE_RECURSO,
		UN_FACTURA
	);

	RETURN MI_VAL+1;

	EXCEPTION WHEN OTHERS THEN
--        DBMS_OUTPUT.PUT_LINE (SQLERRM);
		RETURN MI_VAL;
END;

FUNCTION FC_ENVIAR_PLAN_AJUSTES2
/*
    NAME              : FC_ENVIAR_PLAN_AJUSTES
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 23/06/2022                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : INSERTA UN NUEVO REGISTRO EN LA TABLA TEMPORAL PLAN AJUSTES CAUSACION CUANDO EL PROCESO DE CAUSACIÓN DE CUENTAS SE REALIZA AGRUPADO

    @NAME:    enviarPlanAjustes
*/
(	UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO            IN NUMBER,
	UN_TIPO           IN VARCHAR2,
	UN_NUMERO         IN NUMBER,
	UN_CONSECUTIVO    IN NUMBER,
	UN_FECHA          IN DATE,
	UN_CUENTA         IN VARCHAR2,
	UN_VALOR_DEBITO   IN NUMBER,
	UN_VALOR_CREDITO  IN NUMBER,
	UN_TERCERO        IN VARCHAR2,
	UN_SUCURSAL       IN VARCHAR2,
	UN_CENTRO_COSTO   IN VARCHAR2,
	UN_AUXILIAR       IN VARCHAR2,
	UN_REFERENCIA     IN VARCHAR2,
	UN_FUENTE_RECURSO IN VARCHAR2,
	UN_FACTURA        IN VARCHAR2
) RETURN NUMBER
IS
	MI_VAL NUMBER(1) := 0;
    MI_NATURALEZA VARCHAR2(1);
    
BEGIN
      BEGIN
        SELECT NATURALEZA 
        INTO MI_NATURALEZA
        FROM   PLAN_CONTABLE
        WHERE  COMPANIA=UN_COMPANIA
          AND  ANO=UN_ANO
          AND  CODIGO=UN_CUENTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NATURALEZA:='D';
      END;
  
	INSERT INTO TEMP_PLANA_AJUSTES_CAUSACION (
		COMPANIA,
		ANO,
		TIPO_CPTE,
        COMPROBANTE,
        CONSECUTIVO,
		FECHA,
		CUENTA,
        NATURALEZA,
		VALOR_DEBITO,
		VALOR_CREDITO,
		TERCERO,
		SUCURSAL,
		CENTRO_COSTO,
		AUXILIAR,
		REFERENCIA,
		FUENTE_RECURSOS,
		NRO_DOCUMENTO
		)
	VALUES
	(
		UN_COMPANIA,
		UN_ANO,
		UN_TIPO,
        UN_NUMERO,
        UN_CONSECUTIVO,
		UN_FECHA,
		UN_CUENTA,
        MI_NATURALEZA,
		UN_VALOR_DEBITO,
		UN_VALOR_CREDITO,
		UN_TERCERO,
		UN_SUCURSAL,
		UN_CENTRO_COSTO,
		UN_AUXILIAR,
		UN_REFERENCIA,
		UN_FUENTE_RECURSO,
		UN_FACTURA
	);  
	RETURN MI_VAL+1;

	EXCEPTION WHEN OTHERS THEN
--        DBMS_OUTPUT.PUT_LINE (SQLERRM);
		RETURN MI_VAL;
END;

FUNCTION FC_ENVIAR_PLAN_AJ_CAUSACION RETURN NUMBER
/*
    NAME              : FC_ENVIAR_PLAN_AJ_CAUSACION
    AUTHOR MIGRACION  : JULIAN CAMILO PULIDO DELGADO
    DATE MIGRADOR     : 04/12/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 23/06/2022
    TIME              :
    MODIFICATIONS     : Se modifica
    DESCRIPTION       : INSERTA UN NUEVO REGISTRO EN LA TABLA TEMPORAL PLAN AJUSTES

    @NAME:    enviarPlanAjustes
    */
IS
	MI_VAL NUMBER(1) := 0;
BEGIN
    BEGIN
        INSERT INTO TEMP_PLANA_AJUSTES(
                COMPANIA,
                ANO,
                TIPO_CPTE,
                COMPROBANTE,
                CONSECUTIVO,
                FECHA,
                CUENTA,
                NATURALEZA,
                VALOR_DEBITO,
                VALOR_CREDITO,
                TERCERO,
                SUCURSAL,
                CENTRO_COSTO,
                AUXILIAR,
                REFERENCIA,
                FUENTE_RECURSOS,
                NRO_DOCUMENTO
		)        
        SELECT  COMPANIA,
                ANO,
                TIPO_CPTE,
                COMPROBANTE,
                ROW_NUMBER() OVER(ORDER BY CUENTA ASC) AS CONSECUTIVO , 
                SYSDATE,
                CUENTA,
                NATURALEZA,
                TOTAL_VALOR_DEBITO,
                TOTAL_VALOR_CREDITO,
                TERCERO,
                SUCURSAL,
                CENTRO_COSTO,
                AUXILIAR,
                REFERENCIA,
                FUENTE_RECURSOS,
                NRO_DOCUMENTO 
                FROM (
                    SELECT  COMPANIA ,
                            ANO ,
                            TIPO_CPTE ,
                            COMPROBANTE,
                            CUENTA ,
                            NATURALEZA,
                            SUM(VALOR_DEBITO) TOTAL_VALOR_DEBITO ,
                            SUM(VALOR_CREDITO) TOTAL_VALOR_CREDITO,
                            CENTRO_COSTO ,
                            TERCERO ,
                            SUCURSAL ,
                            AUXILIAR ,
                            REFERENCIA,
                            FUENTE_RECURSOS,
                            NRO_DOCUMENTO
                    FROM TEMP_PLANA_AJUSTES_CAUSACION
                    GROUP BY COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CUENTA,NATURALEZA, CENTRO_COSTO, TERCERO, 
                            SUCURSAL, AUXILIAR, REFERENCIA,FUENTE_RECURSOS, NRO_DOCUMENTO);
    END;
    RETURN MI_VAL+1;

	EXCEPTION WHEN OTHERS THEN
--        DBMS_OUTPUT.PUT_LINE (SQLERRM);
		RETURN MI_VAL;
END;

PROCEDURE PR_GENERAR_PROXIMO_ANIO
/*
    NAME              : PR_GENERAR_PROXIMO_ANIO
    AUTHOR MIGRACION  : JULIAN CAMILO PULIDO DELGADO
    DATE MIGRADOR     : 06/12/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO QUE DUPLICA LAS VARIABLES DE LA INTERFAZ PARA UN AÑO ESPECIFICO

    @NAME:    generarProximoAnio
    @METHOD:  POST
    */















( 
    UN_COD_TRASACCION   IN VARCHAR2,
    UN_ANO_DESDE        IN NUMBER,
    UN_ANO              IN NUMBER,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
   MI_RS                SYS_REFCURSOR;
   MI_EXISTE_ANIO       NUMBER;
   MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_VARIABLE          VARCHAR2(255);
   
BEGIN

    BEGIN
       FOR MI_RS IN (
          SELECT  
              COMPANIA,
              CODIGO_TRANSACCION,
              VARIABLE,
              CUENTA_DEBITO,
              CUENTA_CREDITO,
              CENTRO_COSTO,
              TERCERO,
              SUCURSAL,
              AUXILIAR,
              REFERENCIA,
              FUENTE_RECURSO,
              DESCRIPCION
          FROM 
              CM_DET_CAUSACION_AUTOMATICA
          WHERE
              ANO=UN_ANO_DESDE
          AND CODIGO_TRANSACCION=UN_COD_TRASACCION
        ) LOOP


        BEGIN
          INSERT INTO CM_DET_CAUSACION_AUTOMATICA (
              COMPANIA,
              ANO,
              CODIGO_TRANSACCION,
              VARIABLE,
              CUENTA_DEBITO,
              CUENTA_CREDITO,
              CENTRO_COSTO,
              TERCERO,
              SUCURSAL,
              AUXILIAR,
              REFERENCIA,
              FUENTE_RECURSO,
              DESCRIPCION,
              CREATED_BY,
              DATE_CREATED
            )
          VALUES
          (
              MI_RS.COMPANIA,
              UN_ANO,
              MI_RS.CODIGO_TRANSACCION,
              MI_RS.VARIABLE,
              MI_RS.CUENTA_DEBITO,
              MI_RS.CUENTA_CREDITO,
              MI_RS.CENTRO_COSTO,
              MI_RS.TERCERO,
              MI_RS.SUCURSAL,
              MI_RS.AUXILIAR,
              MI_RS.REFERENCIA,
              MI_RS.FUENTE_RECURSO,
              MI_RS.DESCRIPCION,
              UN_USUARIO,
              SYSDATE
          );
          
           EXCEPTION WHEN OTHERS THEN
            MI_VARIABLE:=MI_RS.DESCRIPCION;
            RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
        END;
        
        END LOOP;
        
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(2).CLAVE := 'VARIABLE';
                      MI_MSGERROR(2).VALOR := MI_VARIABLE;
                      MI_MSGERROR(1).CLAVE := 'ANO';
                      MI_MSGERROR(1).VALOR := UN_ANO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_NOEXISTEANO,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
    END;
    
END PR_GENERAR_PROXIMO_ANIO ;

FUNCTION FC_GETCMCODIGOSCUMNUMERO
/*
    NAME              : FC_GETCMCODIGOSCUMNUMERO
    AUTHOR MIGRACION  : JM
    DATE MIGRADOR     : 06/08/2024                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PARA QUE NO TARDE TANTO CONTANDO
*/
( UN_EXPEDIENTE IN VARCHAR2,
    UN_PRODUCTO IN VARCHAR2,
    UN_CONSECUTIVOCUM IN VARCHAR2,
    UN_DESCRIPCIONCOMERCIAL IN VARCHAR2,
    UN_CANTIDADCUM IN VARCHAR2,
    UN_TITULAR IN VARCHAR2,
    UN_NOMBRE_ROL IN VARCHAR2
) RETURN number
IS
  MI_RTA number;
BEGIN

    MI_RTA := 0;
    
    BEGIN 
        
    WITH DataFiltrada AS (SELECT CUMS.EXPEDIENTE, CUMS.PRODUCTO, CUMS.CONSECUTIVOCUM, CUMS.DESCRIPCIONCOMERCIAL, CUMS.CANTIDADCUM, CUMS_FABR.TITULAR, FABR.NOMBRE_ROL FROM CM_CUMS_FABR CUMS_FABR INNER JOIN CM_CODIGOS_CUMS CUMS ON CUMS.EXPEDIENTE = CUMS_FABR.EXPEDIENTE AND CUMS.CONSECUTIVOCUM = CUMS_FABR.CONSECUTIVOCUM INNER JOIN CM_FABRICANTES_CUMS FABR ON FABR.ID = CUMS_FABR.ID_FABR_CUMS WHERE (CUMS.EXPEDIENTE LIKE UPPER(CONCAT(CONCAT('%', UN_EXPEDIENTE ),'%')) OR UN_EXPEDIENTE IS NULL) AND (CUMS.PRODUCTO LIKE UPPER(CONCAT(CONCAT('%', UN_PRODUCTO ),'%')) OR UN_PRODUCTO IS NULL) AND (CUMS.CONSECUTIVOCUM LIKE UPPER(CONCAT(CONCAT('%', UN_CONSECUTIVOCUM ),'%')) OR UN_CONSECUTIVOCUM IS NULL) AND (CUMS.DESCRIPCIONCOMERCIAL LIKE UPPER(CONCAT(CONCAT('%', UN_DESCRIPCIONCOMERCIAL ),'%')) OR UN_DESCRIPCIONCOMERCIAL IS NULL) AND (CUMS.CANTIDADCUM LIKE UPPER(CONCAT(CONCAT('%', UN_CANTIDADCUM ),'%')) OR UN_CANTIDADCUM IS NULL) AND (CUMS_FABR.TITULAR LIKE UPPER(CONCAT(CONCAT('%', UN_TITULAR ),'%')) OR UN_TITULAR IS NULL) AND (FABR.NOMBRE_ROL LIKE UPPER(CONCAT(CONCAT('%', UN_NOMBRE_ROL),'%')) OR UN_NOMBRE_ROL IS NULL) ) SELECT COUNT(1) AS TOTAL INTO MI_RTA FROM DataFiltrada;
    
    EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN MI_RTA;
    END; 
 

    RETURN MI_RTA;



END FC_GETCMCODIGOSCUMNUMERO;


FUNCTION FC_GETCMCODIGOSCUMPAGINA
/*
    NAME              : FC_GETCMCODIGOSCUMNUMERO
    AUTHOR MIGRACION  : JM
    DATE MIGRADOR     : 06/08/2024                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PARA QUE NO TARDE TANTO CONTANDO
*/
( UN_EXPEDIENTE IN VARCHAR2,
    UN_PRODUCTO IN VARCHAR2,
    UN_CONSECUTIVOCUM IN VARCHAR2,
    UN_DESCRIPCIONCOMERCIAL IN VARCHAR2,
    UN_CANTIDADCUM IN VARCHAR2,
    UN_TITULAR IN VARCHAR2,
    UN_NOMBRE_ROL IN VARCHAR2,
    UN_PAGTAMANIO IN NUMBER,
    UN_PAGINICIO IN NUMBER
) RETURN CLOB
IS
  MI_RTA CLOB;
BEGIN

    MI_RTA := '';
   
    BEGIN
    FOR MI_RS IN ( WITH DataFiltrada AS (
                SELECT 
                    CUMS.EXPEDIENTE, 
                    CUMS.PRODUCTO, 
                    CUMS.CONSECUTIVOCUM, 
                    CUMS.DESCRIPCIONCOMERCIAL, 
                    CUMS.CANTIDADCUM, 
                    CUMS_FABR.TITULAR, 
                    FABR.NOMBRE_ROL,
                    ROWNUM AS RNUM
                FROM 
                    CM_CUMS_FABR CUMS_FABR 
                INNER JOIN 
                    CM_CODIGOS_CUMS CUMS 
                    ON CUMS.EXPEDIENTE = CUMS_FABR.EXPEDIENTE 
                    AND CUMS.CONSECUTIVOCUM = CUMS_FABR.CONSECUTIVOCUM 
                INNER JOIN 
                    CM_FABRICANTES_CUMS FABR 
                    ON FABR.ID = CUMS_FABR.ID_FABR_CUMS 
                WHERE 
                    (CUMS.EXPEDIENTE LIKE UPPER(CONCAT(CONCAT('%', UN_EXPEDIENTE ),'%')) OR UN_EXPEDIENTE IS NULL) AND
                    (CUMS.PRODUCTO LIKE UPPER(CONCAT(CONCAT('%', UN_PRODUCTO ),'%')) OR UN_PRODUCTO IS NULL) AND
                    (CUMS.CONSECUTIVOCUM LIKE UPPER(CONCAT(CONCAT('%', UN_CONSECUTIVOCUM ),'%')) OR UN_CONSECUTIVOCUM IS NULL) AND
                    (CUMS.DESCRIPCIONCOMERCIAL LIKE UPPER(CONCAT(CONCAT('%', UN_DESCRIPCIONCOMERCIAL ),'%')) OR UN_DESCRIPCIONCOMERCIAL IS NULL) AND
                    (CUMS.CANTIDADCUM LIKE UPPER(CONCAT(CONCAT('%', UN_CANTIDADCUM ),'%')) OR UN_CANTIDADCUM IS NULL) AND
                    (CUMS_FABR.TITULAR LIKE UPPER(CONCAT(CONCAT('%', UN_TITULAR ),'%')) OR UN_TITULAR IS NULL) AND
                    (FABR.NOMBRE_ROL LIKE UPPER(CONCAT(CONCAT('%', UN_NOMBRE_ROL ),'%')) OR UN_NOMBRE_ROL IS NULL)
                ORDER BY 
                    CUMS.EXPEDIENTE, 
                    CUMS.CONSECUTIVOCUM
            ) SELECT                     
                    JSON_OBJECT(
                        'EXPEDIENTE' VALUE EXPEDIENTE,
                        'PRODUCTO' VALUE PRODUCTO,
                        'CONSECUTIVOCUM' VALUE CONSECUTIVOCUM,
                        'DESCRIPCIONCOMERCIAL' VALUE DESCRIPCIONCOMERCIAL,
                        'CANTIDADCUM' VALUE CANTIDADCUM,
                        'TITULAR' VALUE TITULAR,
                        'NOMBRE_ROL' VALUE NOMBRE_ROL
                    ) AS JSONOBJ, RNUM
                FROM DataFiltrada 
                WHERE RNUM <= (UN_PAGINICIO + UN_PAGTAMANIO) 
                AND RNUM > UN_PAGINICIO)
        LOOP
        
        MI_RTA := MI_RTA||MI_RS.JSONOBJ||',';
        --MI_RTA := REPLACE(MI_RTA, '"', '\"');
        --MI_RTA := REPLACE(MI_RTA, '\', '\\');
      
  END LOOP;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN MI_RTA;
    END; 
    
  
    
    IF LENGTH(MI_RTA) > 0 THEN
        MI_RTA := SUBSTR(MI_RTA, 1, LENGTH(MI_RTA) - 1);
    END IF;

    --RETURN JSON_ARRAY(MI_RTA);
    RETURN MI_RTA;


END FC_GETCMCODIGOSCUMPAGINA;

PROCEDURE PR_CARGAR_RIPS_JSON 
/*
    NAME              : PR_CARGAR_RIPS_JSON
    AUTHOR            : LVEGA
    DATE MIGRADOR     : 13/06/2025       
    MODIFICATIONS     : 
    DESCRIPTION       : RECIBE UNA CADENA DESDE EL JAVA DE LA LECTURA DE ARCHIVOS RIPS (JSON Y XML) Y LLEVA LOS DATOS A LAS TABLAS CORRESPONDIENTES
*/
(
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONSECUTIVO             IN PCK_SUBTIPOS.TI_CONSECUTIVO,
    UN_CLOB_USUARIOS           IN CLOB,
    UN_CLOB_CONSULTAS          IN CLOB,
    UN_CLOB_PROCEDIMIENTOS     IN CLOB,
    UN_CLOB_MEDICAMENTOS       IN CLOB,
    UN_CLOB_URGENCIAS          IN CLOB,
    UN_CLOB_RECIEN_NACIDOS     IN CLOB,
    UN_CLOB_OTROS_SERVICIOS    IN CLOB,
    UN_CLOB_HOSPITALIZACION    IN CLOB,
    UN_CLOB_VAL_FACTURA        IN CLOB,
    UN_CLOB_VAL_FACTDETALLE    IN CLOB,
    UN_CLOB_ARCHIVOS_CONTROL   IN CLOB,
    UN_CLOB_TRANSACCION        IN CLOB,
    UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO
)

AS
    MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
    MI_CODIGO_CUM           PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSECUTIVO          NUMBER(5);
    MI_LINEAARCHIVO         NUMBER := 1;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_COD_PREST_SERV_SALUD VARCHAR2(255);
    MI_COD_ARCHIVO          VARCHAR2(50);
    MI_NOMBRE_ARCHIVO       VARCHAR2(250);
    MI_TIPO_ARCHIVO         VARCHAR2(50);
    MI_CC_USUARIO           VARCHAR2(2);
    MI_NUM_USUARIO          VARCHAR2(20);
    MI_FACT_VALIDACION      VARCHAR2(50);
    MI_FECHA_EXP            VARCHAR2(50);
    MI_NOMBRE_MEDICAMENTO   VARCHAR2(50);
    MI_BUSCAR_CONSECUTIVO   PCK_SUBTIPOS.TI_CONSECUTIVO;
    MI_MSG_EXCEPTION        NUMBER;
    MI_PARAMETROCOMPRO      VARCHAR2(100);
    MI_COUNT_DUPLICADO      NUMBER := 0;
    MI_NUM_ID_MADRE         VARCHAR2(50);
    MI_TIPO_ID_MADRE        VARCHAR2(100);
    MI_FACTURA_DUPLICADA    VARCHAR2(50);

BEGIN

	MI_PARAMETROCOMPRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'COMPROBANTE PARA CAUSACION DE CUENTAS MEDICAS',
                                                  UN_MODULO    => 84,
                                                  UN_FECHA_PAR => SYSDATE,
                                                  UN_IND_MAYUS => 0);
  IF UN_CLOB_USUARIOS IS NOT NULL AND LENGTH(UN_CLOB_USUARIOS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_USUARIOS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
                                                            
               MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_USUARIOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||''''  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                     MI_CAMPOS := 'COMPANIA
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,TIPO_USUARIO
                    ,COD_ENTIDAD_ADMIN
                    ,PRIMER_APELLIDO_USUA
                    ,PRIMER_NOMBRE_USUA
                    ,EDAD
                    ,UNIDAD_DE_MEDIDA_EDAD
                    ,COD_DEPART_RESI_HABITUAL
                    ,FECHA_NACIMIENTO
                    ,SEXO
                    ,COD_PAIS_RESIDENCIA
                    ,COD_MUNI_RESIDENCIA_HABITUAL
                    ,ZONA_RESIDENCIA_HABITUAL
                    ,INCAPACIDAD
                    ,CONSECUTIVO
                    ,CONSECUTIVO_RIPS
                    ,NUM_FACTURA
                    ,COD_PAIS_ORIGEN
                    ,CREATED_BY
                    ,DATE_CREATED';
                    
                    MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,''N/A''
                    ,''N/A''
                    ,''N/A''
                    ,0
                    ,''0''
                    ,''N/A''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'|| MI_CONSECUTIVO||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| MI_DATOS_COLUMNAS(12) ||'''
                    ,'||MI_DATOS_COLUMNAS(11)||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                    
                    MI_CC_USUARIO := MI_DATOS_COLUMNAS(1);
                    MI_NUM_USUARIO :=MI_DATOS_COLUMNAS(2);
                    
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_USUARIOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                                                       
                MI_NUM_ID_MADRE := MI_DATOS_COLUMNAS(2); MI_TIPO_ID_MADRE := MI_DATOS_COLUMNAS(1);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'USUARIOS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;

    IF UN_CLOB_CONSULTAS IS NOT NULL AND LENGTH(UN_CLOB_CONSULTAS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_CONSULTAS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            
                MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_CONSULTA',
                                                                   UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(22)||''' '  ,
                                                                   UN_CAMPO    => 'CONSECUTIVO');
                                                                   
                 BEGIN                                                   
                   SELECT NUM_FACTURA
                    INTO MI_FACTURA_DUPLICADA
                    FROM (
                        SELECT NUM_FACTURA
                        FROM CM_ARCHIVO_CONSULTA
                        WHERE COMPANIA                    = UN_COMPANIA
                          AND COD_PREST_SERV_SALUD        = MI_DATOS_COLUMNAS(1)
                          AND TIPO_IDENTIFICACION_USUARIO = MI_DATOS_COLUMNAS(2)
                          AND NUM_IDENTI_USUARIO_SISTEMA  = MI_DATOS_COLUMNAS(3)
                          AND COD_SERVICIO                = MI_DATOS_COLUMNAS(9)
                          AND TRUNC(FECHA_CONSULTA)       = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(4), 'YYYY-MM-DD HH24:MI:SS'))
                        ORDER BY DATE_CREATED DESC
                    )
                    WHERE ROWNUM = 1;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_FACTURA_DUPLICADA := NULL;
                END;
                    
                    IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
                        MI_MSGERROR(1).CLAVE := 'MENSAJE';
                        MI_MSGERROR(1).VALOR := 'Error: Servicio '||MI_DATOS_COLUMNAS(9)||
                                                ' ya radicado para el usuario '||MI_DATOS_COLUMNAS(2)||
                                                '-'||MI_DATOS_COLUMNAS(3)||
                                                ' en la fecha '||MI_DATOS_COLUMNAS(4)||
                                                '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
                        MI_MSGERROR(2).CLAVE := 'TABLA';
                        MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_CONSULTA';
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => -20002,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                            UN_REEMPLAZOS => MI_MSGERROR
                        );
                    END IF;

                     MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,FECHA_CONSULTA
                    ,NUMERO_AUTORIZACION
                    ,CODIGO_CONSULTA
                    ,MODALIDAD_GRUPO_SERVICIO_TECSAL
                    ,GRUPO_SERVICIOS
                    ,COD_SERVICIO
                    ,FINALIDAD_CONSULTA
                    ,CAUSA_EXTERNA
                    ,COD_DIAGNOSTICO_PRINCIPAL
                    ,COD_DIAGNOSTICO_RELACIONADO_1
                    ,COD_DIAGNOSTICO_RELACIONADO_2
                    ,COD_DIAGNOSTICO_RELACIONADO_3
                    ,TIPO_DIAGNOSTICO_PRINCIPAL
                    ,VALOR_CONSULTA
                    ,VALOR_NETO_A_PAGAR
                    ,CONCEPTO_RECAUDO
                    ,VALOR_CUOTA_MODERADORA
                    ,NUM_FEV_PAGO_MODERADOR
                    ,CONSECUTIVO
                    ,CONSECUTIVO_RIPS
                    ,CREATED_BY
                    ,DATE_CREATED';
                    
                    MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(22)||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''
                    ,'''||MI_DATOS_COLUMNAS(17)||'''
                    ,0
                    ,'''||MI_DATOS_COLUMNAS(18)||'''
                    ,'''||MI_DATOS_COLUMNAS(19)||'''
                    ,'''||MI_DATOS_COLUMNAS(20)||'''
                    ,'|| MI_CONSECUTIVO||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                    
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_CONSULTA'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'CONSULTA';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;

IF UN_CLOB_MEDICAMENTOS IS NOT NULL AND LENGTH(UN_CLOB_MEDICAMENTOS) > 0 THEN
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_MEDICAMENTOS,
                                                UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP--(CC:3246_INI_CFBARRERA: Corrige procesamiento de medicamentos, ajusta indices y agrega campo CONSECUTIVO en PK)
        --  Procesa la fila actual
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                        UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
        MI_CODIGO_CUM := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_COLUMNAS(8),  
                                             UN_DELIMITADOR => '-');
        IF NOT MI_CODIGO_CUM.EXISTS(2) THEN
            MI_CODIGO_CUM(1) := MI_DATOS_COLUMNAS(8);
            MI_CODIGO_CUM(2) := 999;
        END IF;
        
        -- Generar consecutivo
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
            UN_TABLA    => 'CM_ARCHIVO_MEDICAMENTOS',
            UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(24)||''' ',
            UN_CAMPO    => 'CONSECUTIVO');
            

        BEGIN
            SELECT NUM_FACTURA
            INTO MI_FACTURA_DUPLICADA
            FROM (
                SELECT NUM_FACTURA
                FROM CM_ARCHIVO_MEDICAMENTOS
                WHERE COMPANIA                    = UN_COMPANIA
                  AND COD_PREST_SERV_SALUD        = MI_DATOS_COLUMNAS(1)
                  AND TIPO_IDENTIFICACION_USUARIO = MI_CC_USUARIO
                  AND NUM_IDENTI_USUARIO_SISTEMA  = MI_NUM_USUARIO
                  AND CODIGO_MEDICAMENTO          = MI_DATOS_COLUMNAS(8)
                  AND TRUNC(FECHA_DISPENS_ADMON)  = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(4), 'YYYY-MM-DD HH24:MI:SS'))
                ORDER BY DATE_CREATED DESC
            )
            WHERE ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FACTURA_DUPLICADA := NULL;
        END;
        
        IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
            MI_MSGERROR(1).CLAVE := 'MENSAJE';
            MI_MSGERROR(1).VALOR := 'Error: Servicio '||MI_DATOS_COLUMNAS(8)||
                                    ' ya radicado para el usuario '||MI_CC_USUARIO||
                                    '-'||MI_NUM_USUARIO||
                                    ' en la fecha '||MI_DATOS_COLUMNAS(4)||
                                    '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
            MI_MSGERROR(2).CLAVE := 'TABLA';
            MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_MEDICAMENTOS';
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => -20002,
                UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                UN_REEMPLAZOS => MI_MSGERROR
            );
        END IF;


        MI_NOMBRE_MEDICAMENTO := MI_DATOS_COLUMNAS(9);
        BEGIN
            SELECT PRODUCTO
            INTO MI_NOMBRE_MEDICAMENTO
            FROM CM_CODIGOS_CUMS
            WHERE (EXPEDIENTE || '-' || CONSECUTIVOCUM) = MI_DATOS_COLUMNAS(8)
              AND ROWNUM = 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                MI_NOMBRE_MEDICAMENTO := '.';
            WHEN OTHERS THEN
                MI_NOMBRE_MEDICAMENTO := '.';
        END;
       
        MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,NUMERO_AUTORIZACION
                    ,ID_MIPRES
                    ,FECHA_DISPENS_ADMON
                    ,COD_DIAGNOSTICO_PRINCIPAL
                    ,COD_DIAGNOSTICO_RELACIONADO
                    ,TIPO_MEDICAMENTO
                    ,CODIGO_MEDICAMENTO
                    ,MEDICAMENTO
                    ,NOMBRE_GENERICO_MEDICAMENTO
                    ,CONCENTRACION_MEDICAMENTO
                    ,UNIDAD_MEDIDA_MEDICAMENTO
                    ,FORMA_FARMACEUTICA
                    ,NUMERO_UNIDADES
                    ,CANTIDAD_MEDICAMENTO
                    ,DIAS_TRATAMIENTO
                    ,TIPO_ID_PRESCRIPTOR
                    ,NUM_ID_PRESCRIPTOR
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,VALOR_UNITARIO_MEDICAMENTO
                    ,VALOR_TOTAL_MEDICAMENTO
                    ,CONCEPTO_RECAUDO
                    ,VALOR_PAGO_MODERADOR
                    ,NUM_FEV_PAGO_MODERADOR
                    ,CONSECUTIVOCUM
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
     
        MI_VALORES := '''' || UN_COMPANIA || '''
                  ,'''||MI_DATOS_COLUMNAS(24)||'''
                  ,''' || MI_DATOS_COLUMNAS(1) || '''
                  ,''' || MI_DATOS_COLUMNAS(2) || '''
                  ,''' || MI_DATOS_COLUMNAS(3) || '''
                  ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                  ,''' || MI_DATOS_COLUMNAS(5) || '''
                  ,''' || MI_DATOS_COLUMNAS(6) || '''
                  ,''' || MI_DATOS_COLUMNAS(7) || '''
                  ,''' || MI_DATOS_COLUMNAS(8) || '''
                  ,''' || MI_DATOS_COLUMNAS(8) || '''
                  ,''' || NVL(NULLIF(MI_DATOS_COLUMNAS(9), ''''), MI_NOMBRE_MEDICAMENTO) || '''
                  ,''' || MI_DATOS_COLUMNAS(10) || '''
                  ,''' || MI_DATOS_COLUMNAS(11) || '''
                  ,''' || MI_DATOS_COLUMNAS(12) || '''
                  ,'   || MI_DATOS_COLUMNAS(13) || ' 
                  ,'   || MI_DATOS_COLUMNAS(14) || '
                  ,'   || MI_DATOS_COLUMNAS(15) || '
                  ,''' || MI_DATOS_COLUMNAS(16) || '''
                  ,''' || MI_DATOS_COLUMNAS(17) || '''
                  ,''' || MI_CC_USUARIO || '''
                  ,''' || MI_NUM_USUARIO || '''
                  ,'   || MI_DATOS_COLUMNAS(18) || '
                  ,'   || MI_DATOS_COLUMNAS(19) || '
                  ,''' || MI_DATOS_COLUMNAS(20) || '''
                  ,'   || MI_DATOS_COLUMNAS(21) || '
                  ,''' || MI_DATOS_COLUMNAS(22) || '''
                  ,''' || MI_DATOS_COLUMNAS(23) || '''
                  ,'   || UN_CONSECUTIVO || '
                  ,'   || MI_CONSECUTIVO || '
                  ,''' || UN_USUARIO || '''
                  ,SYSDATE';
                  
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                    UN_TABLA    => 'CM_ARCHIVO_MEDICAMENTOS',
                    UN_ACCION   => 'I',
                    UN_CAMPOS   => MI_CAMPOS,
                    UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
            MI_MSGERROR(1).CLAVE := 'LINEA';
            MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
            MI_MSGERROR(2).CLAVE := 'TABLA';
            MI_MSGERROR(2).VALOR := 'MEDICAMENTOS';
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
        
    END LOOP;
END IF;--(CC:3246_FIN_CFBARRERA)

    IF UN_CLOB_PROCEDIMIENTOS IS NOT NULL AND LENGTH(UN_CLOB_PROCEDIMIENTOS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_PROCEDIMIENTOS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            
                 MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_PROCEDIMIENTOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(21)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                                                     
          BEGIN
                SELECT NUM_FACTURA
                INTO MI_FACTURA_DUPLICADA
                FROM (
                    SELECT NUM_FACTURA
                    FROM CM_ARCHIVO_PROCEDIMIENTOS
                    WHERE COMPANIA                    = UN_COMPANIA
                      AND COD_PREST_SERV_SALUD        = MI_DATOS_COLUMNAS(1)
                      AND TIPO_IDENTIFICACION_USUARIO = MI_CC_USUARIO
                      AND NUM_IDENTI_USUARIO_SISTEMA  = MI_NUM_USUARIO
                      AND CODIGO_PROCEDIMIENTO        = MI_DATOS_COLUMNAS(5)
                      AND TRUNC(FECHA_PROCEDIMIENTO)  = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(2), 'YYYY-MM-DD HH24:MI:SS'))
                    ORDER BY DATE_CREATED DESC
                )
                WHERE ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_FACTURA_DUPLICADA := NULL;
            END;
            
            IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := 'Error: Procedimiento '||MI_DATOS_COLUMNAS(5)||
                                        ' ya radicado para el usuario '||MI_CC_USUARIO||
                                        '-'||MI_NUM_USUARIO||
                                        ' en la fecha '||MI_DATOS_COLUMNAS(2)||
                                        '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
                MI_MSGERROR(2).CLAVE := 'TABLA';
                MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_PROCEDIMIENTOS';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => -20002,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                    UN_REEMPLAZOS => MI_MSGERROR
                );
            END IF;

             MI_CAMPOS := 'COMPANIA
                            ,NUM_FACTURA
                            ,AMBITO_REALI_PROCEDIMIENTO
                            ,COD_PREST_SERV_SALUD
                            ,FECHA_PROCEDIMIENTO
                            ,ID_MIPRES
                            ,NUMERO_AUTORIZACION
                            ,CODIGO_PROCEDIMIENTO
                            ,VIA_INGRESO_SERVICIO_SALUD
                            ,MODALIDAD_GRUPO_SERVICIO_TECSAL
                            ,GRUPO_SERVICIOS
                            ,COD_SERVICIO
                            ,FINALIDAD_PROCEDIMIENTO
                            ,TIPO_ID_REALIZA_PROCEDIMIENTO
                            ,NUM_ID_REALIZA_PROCEDIMIENTO
                            ,TIPO_IDENTIFICACION_USUARIO
                            ,NUM_IDENTI_USUARIO_SISTEMA
                            ,DIAGNOSTICO_PRINCIPAL
                            ,DIAGNOSTICO_RELACIONADO
                            ,COMPLICACION
                            ,VALOR_PROCEDIMIENTO
                            ,CONCEPTO_RECAUDO
                            ,VALOR_PAGO_MODERADOR
                            ,NUM_FEV_PAGO_MODERADOR
                            ,CONSECUTIVO
                            ,CONSECUTIVO_RIPS
                            ,CREATED_BY
                            ,DATE_CREATED';

          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||MI_DATOS_COLUMNAS(21)||'''
                        ,''0''
                        ,'''||MI_DATOS_COLUMNAS(1)||'''
                        ,TO_DATE('''||MI_DATOS_COLUMNAS(2)||''', ''YYYY-MM-DD HH24:MI:SS'')
                        ,'''||MI_DATOS_COLUMNAS(3)||'''
                        ,'''||MI_DATOS_COLUMNAS(4)||'''
                        ,'''||MI_DATOS_COLUMNAS(5)||'''
                        ,'''||MI_DATOS_COLUMNAS(6)||'''
                        ,'''||MI_DATOS_COLUMNAS(7)||'''
                        ,'''||MI_DATOS_COLUMNAS(8)||'''
                        ,'''||MI_DATOS_COLUMNAS(9)||'''
                        ,'''||MI_DATOS_COLUMNAS(10)||'''
                        ,'''||MI_DATOS_COLUMNAS(11)||'''
                        ,'''||MI_DATOS_COLUMNAS(12)||'''
                        ,'''||MI_CC_USUARIO||'''
                        ,'''||MI_NUM_USUARIO||'''
                        ,'''||MI_DATOS_COLUMNAS(13)||'''
                        ,'''||MI_DATOS_COLUMNAS(14)||'''
                        ,'''||MI_DATOS_COLUMNAS(15)||'''
                        ,'''||MI_DATOS_COLUMNAS(16)||'''
                        ,'''||MI_DATOS_COLUMNAS(17)||'''
                        ,'''||MI_DATOS_COLUMNAS(18)||'''
                        ,'''||MI_DATOS_COLUMNAS(19)||'''
                        ,'||MI_CONSECUTIVO||'
                        ,'|| UN_CONSECUTIVO||'
                        ,'''|| UN_USUARIO ||'''
                        ,SYSDATE';
                    
                BEGIN
                 BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_PROCEDIMIENTOS'
                                                           ,UN_ACCION  => 'I'
                                                           ,UN_CAMPOS  => MI_CAMPOS
                                                           ,UN_VALORES => MI_VALORES);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
                  END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'PROCEDIMIENTOS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
                END;
        END LOOP;
    END IF;

    IF UN_CLOB_URGENCIAS IS NOT NULL AND LENGTH(UN_CLOB_URGENCIAS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_URGENCIAS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_URGE_OBSERVACION',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(13)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                                                     

            BEGIN
                SELECT NUM_FACTURA
                INTO MI_FACTURA_DUPLICADA
                FROM (
                    SELECT NUM_FACTURA
                    FROM CM_ARCHIVO_URGE_OBSERVACION
                    WHERE COMPANIA                         = UN_COMPANIA
                      AND COD_PREST_SERV_SALUD             = MI_DATOS_COLUMNAS(1)
                      AND TIPO_IDENTIFICACION_USUARIO      = MI_CC_USUARIO
                      AND NUM_IDENTI_USUARIO_SISTEMA       = MI_NUM_USUARIO
                      AND CAUSA_EXTERNA                    = MI_DATOS_COLUMNAS(3)
                      AND COD_DIAGNOSTICO_PRINCIPAL        = MI_DATOS_COLUMNAS(4)
                      AND TRUNC(FECHA_INGRESO_USUARIO_SISTEMA) = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(2), 'YYYY-MM-DD HH24:MI:SS'))
                    ORDER BY DATE_CREATED DESC
                )
                WHERE ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_FACTURA_DUPLICADA := NULL;
            END;
            
            IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := 'Error: Servicio '||MI_DATOS_COLUMNAS(3)|| '-'|| MI_DATOS_COLUMNAS(4) ||
                                        ' ya radicado para el usuario '||MI_CC_USUARIO||
                                        '-'||MI_NUM_USUARIO||
                                        ' en la fecha '||MI_DATOS_COLUMNAS(2)||
                                        '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
                MI_MSGERROR(2).CLAVE := 'TABLA';
                MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_URGE_OBSERVACION';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => -20002,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                    UN_REEMPLAZOS => MI_MSGERROR
                );
            END IF;

          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,ESTADO_SALIDA
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,FECHA_INGRESO_USUARIO_SISTEMA
                    ,HORA_INGRESO_USUARIO
                    ,CAUSA_EXTERNA
                    ,COD_DIAGNOSTICO_PRINCIPAL
                    ,DIAGNOSTICO_SALIDA
                    ,DIAGNOSTICO_RELACIONADO_1
                    ,DIAGNOSTICO_RELACIONADO_2
                    ,DIAGNOSTICO_RELACIONADO_3
                    ,DESTINO_USUARIO_SALIDA_OBSER
                    ,CAUSA_BASICA_MUERTE_URGENCIAS
                    ,FECHA_SALIDA_USUARIO_OBSERVA
                    ,HORA_SALIDA_USUARIO_OBSERVA
                    ,CONSECUTIVO
                    ,CONSECUTIVO_RIPS
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,''0''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_CC_USUARIO||'''
                    ,'''||MI_NUM_USUARIO||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(2)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(2)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(11)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(11)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'||MI_CONSECUTIVO||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_URGE_OBSERVACION'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'URGENCIAS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;

    IF UN_CLOB_HOSPITALIZACION IS NOT NULL AND LENGTH(UN_CLOB_HOSPITALIZACION) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_HOSPITALIZACION,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_HOSPITALIZACION',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(16)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                                                     
                                                     
            BEGIN
                SELECT NUM_FACTURA
                INTO MI_FACTURA_DUPLICADA
                FROM (
                    SELECT NUM_FACTURA
                    FROM CM_ARCHIVO_HOSPITALIZACION
                    WHERE COMPANIA                           = UN_COMPANIA
                      AND COD_PREST_SERV_SALUD               = MI_DATOS_COLUMNAS(1)
                      AND TIPO_IDENTIFICACION_USUARIO        = MI_CC_USUARIO
                      AND NUM_IDENTI_USUARIO_SISTEMA         = MI_NUM_USUARIO
                      AND CAUSA_EXTERNA                      = MI_DATOS_COLUMNAS(5)
                      AND DIAGNOSTICO_PRINCIPAL_INGRESO      = MI_DATOS_COLUMNAS(6)
                      AND TRUNC(FECHA_INGRESO_USUARIO_SISTEMA) = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(3), 'YYYY-MM-DD HH24:MI:SS'))
                    ORDER BY DATE_CREATED DESC
                )
                WHERE ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_FACTURA_DUPLICADA := NULL;
            END;
            
            IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := 'Error: Servicio '||MI_DATOS_COLUMNAS(5)|| '-'|| MI_DATOS_COLUMNAS(6) ||
                                        ' ya radicado para el usuario '||MI_CC_USUARIO||
                                        '-'||MI_NUM_USUARIO||
                                        ' en la fecha '||MI_DATOS_COLUMNAS(3)||
                                        '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
                MI_MSGERROR(2).CLAVE := 'TABLA';
                MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_HOSPITALIZACION';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => -20002,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                    UN_REEMPLAZOS => MI_MSGERROR
                );
            END IF;


          MI_CAMPOS := 'COMPANIA
                     ,NUM_FACTURA
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,COD_PREST_SERV_SALUD
                    ,VIA_INGRESO_INSTITUCION
                    ,FECHA_INGRESO_USUARIO_SISTEMA
                    ,HORA_INGRESO_USUARIO
                    ,NUMERO_AUTORIZACION
                    ,CAUSA_EXTERNA
                    ,DIAGNOSTICO_PRINCIPAL_INGRESO
                    ,DIAGNOSTICO_PRINCIPAL_EGRESO
                    ,DIAGNOSTICO_RELACIONADO_1
                    ,DIAGNOSTICO_RELACIONADO_2
                    ,DIAGNOSTICO_RELACIONADO_3
                    ,DIAGNOSTICO_COMPILACION
                    ,ESTADO_SALIDA
                    ,DIAG_CAUSA_BASIC_MUERTE
                    ,FECHA_EGRESO_USUARIO
                    ,HORA_EGRESO_USUARIO
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''
                    ,'''||MI_CC_USUARIO||'''
                    ,'''||MI_NUM_USUARIO||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(3)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(3)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(14)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(14)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_HOSPITALIZACION'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'HOSPITALIZACION';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;

    IF UN_CLOB_RECIEN_NACIDOS IS NOT NULL AND LENGTH(UN_CLOB_RECIEN_NACIDOS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_RECIEN_NACIDOS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_RECIEN_NACIDOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(14)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                                                     
        BEGIN
            SELECT NUM_FACTURA
            INTO MI_FACTURA_DUPLICADA
            FROM (
                SELECT NUM_FACTURA
                FROM CM_ARCHIVO_RECIEN_NACIDOS
                WHERE COMPANIA               = UN_COMPANIA
                  AND COD_PREST_SERV_SALUD   = MI_DATOS_COLUMNAS(1)
                  AND TIPO_IDENTI_MADRE      = MI_TIPO_ID_MADRE
                  AND NUMERO_IDENTI_MADRE    = MI_NUM_ID_MADRE
                  AND TIPO_ID_RECIEN_NACIDO  = MI_DATOS_COLUMNAS(2)
                  AND NUM_ID_RECIEN_NACIDO   = MI_DATOS_COLUMNAS(3)
                  AND TRUNC(FECHA_RECIEN_NACIDO) = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(4), 'YYYY-MM-DD HH24:MI:SS'))
                ORDER BY DATE_CREATED DESC
            )
            WHERE ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FACTURA_DUPLICADA := NULL;
        END;
        
        IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
            MI_MSGERROR(1).CLAVE := 'MENSAJE';
            MI_MSGERROR(1).VALOR := 'Error: Atención neonato '||MI_DATOS_COLUMNAS(2)||
                                    '-'||MI_DATOS_COLUMNAS(3)||
                                    ' de la madre '||MI_TIPO_ID_MADRE||
                                    '-'||MI_NUM_ID_MADRE||
                                    ' ya radicada en la fecha '||MI_DATOS_COLUMNAS(4)||
                                    '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
            MI_MSGERROR(2).CLAVE := 'TABLA';
            MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_RECIEN_NACIDOS';
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => -20002,
                UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                UN_REEMPLAZOS => MI_MSGERROR
            );
        END IF;

          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,TIPO_IDENTI_MADRE
                    ,NUMERO_IDENTI_MADRE
                    ,COD_PREST_SERV_SALUD
                    ,TIPO_ID_RECIEN_NACIDO
                    ,NUM_ID_RECIEN_NACIDO
                    ,FECHA_RECIEN_NACIDO
                    ,HORA_NACIMIENTO
                    ,EDAD_GESTACIONAL
                    ,CONTROL_PRENATAL
                    ,SEXO
                    ,PESO
                    ,DIAGNOSTICO_RECIEN_NACIDO
                    ,CONDICION_DESTINO_USUARIO_EGRESO
                    ,CAUSA_BASICA_MUERTE
                    ,FECHA_EGRESO
                    ,CONSECUTIVO
                    ,CONSECUTIVO_RIPS
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_TIPO_ID_MADRE||'''
                    ,'''||MI_NUM_ID_MADRE||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(12)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'||MI_CONSECUTIVO||'
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_RECIEN_NACIDOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'RECIEN NACIDOS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;

    IF UN_CLOB_OTROS_SERVICIOS IS NOT NULL AND LENGTH(UN_CLOB_OTROS_SERVICIOS) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_OTROS_SERVICIOS,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
        
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CM_ARCHIVO_OTROS_SERVICIOS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(17)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
                                                     
            BEGIN
                SELECT NUM_FACTURA
                INTO MI_FACTURA_DUPLICADA
                FROM (
                    SELECT NUM_FACTURA
                    FROM CM_ARCHIVO_OTROS_SERVICIOS
                    WHERE COMPANIA                         = UN_COMPANIA
                      AND COD_PREST_SERV_SALUD             = MI_DATOS_COLUMNAS(1)
                      AND TIPO_IDENTIFICACION_USUARIO      = MI_CC_USUARIO
                      AND NUM_IDENTI_USUARIO_SISTEMA       = MI_NUM_USUARIO
                      AND CODIGO_SERVICIO                  = MI_DATOS_COLUMNAS(6)
                      AND TRUNC(FECHA_SUMINISTRO_TECNOLOGIA) = TRUNC(TO_DATE(MI_DATOS_COLUMNAS(4), 'YYYY-MM-DD HH24:MI:SS'))
                    ORDER BY DATE_CREATED DESC
                )
                WHERE ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_FACTURA_DUPLICADA := NULL;
            END;
            
            IF MI_FACTURA_DUPLICADA IS NOT NULL THEN
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := 'Error: Servicio '||MI_DATOS_COLUMNAS(6)||
                                        ' ya radicado para el usuario '||MI_CC_USUARIO||
                                        '-'||MI_NUM_USUARIO||
                                        ' en la fecha '||MI_DATOS_COLUMNAS(4)||
                                        '. Identificado en Factura: '||MI_FACTURA_DUPLICADA;
                MI_MSGERROR(2).CLAVE := 'TABLA';
                MI_MSGERROR(2).VALOR := 'CM_ARCHIVO_OTROS_SERVICIOS';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => -20002,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CM_DUPLICADO_RIPS,
                    UN_REEMPLAZOS => MI_MSGERROR
                );
            END IF;


          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,COD_PREST_SERV_SALUD
                    ,NUMERO_AUTORIZACION
                    ,ID_MIPRES
                    ,FECHA_SUMINISTRO_TECNOLOGIA
                    ,TIPO_SERVICIO
                    ,CODIGO_SERVICIO
                    ,NOMBRE_SERVICIO
                    ,CANTIDAD
                    ,TIPO_ID_ORDENA_SERVICIO
                    ,NUM_ID_ORDENA_SERVICIO
                    ,TIPO_IDENTIFICACION_USUARIO
                    ,NUM_IDENTI_USUARIO_SISTEMA
                    ,VALOR_UNI_MATERIAL_INSUMO
                    ,VALOR_TOTAL_MATERIAL_INSUMO
                    ,CONCEPTO_RECAUDO
                    ,VALOR_PAGO_MODERADOR
                    ,NUM_FEV_PAGO_MODERADOR
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(17)||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'''||MI_DATOS_COLUMNAS(9)||'''
                    ,'||MI_DATOS_COLUMNAS(10)||'
                     ,'''||MI_CC_USUARIO||'''
                    ,'''||MI_NUM_USUARIO||'''
                    ,'||MI_DATOS_COLUMNAS(11)||'
                    ,'||MI_DATOS_COLUMNAS(12)||'
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'|| UN_CONSECUTIVO||'
                    ,'|| MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_OTROS_SERVICIOS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'OTROS SERVICIOS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;
    
        IF UN_CLOB_TRANSACCION IS NOT NULL AND LENGTH(UN_CLOB_TRANSACCION) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_TRANSACCION,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);

    BEGIN
         BEGIN
            BEGIN
             SELECT DISTINCT I.RADICADO
             INTO MI_BUSCAR_CONSECUTIVO
             FROM CM_ARCHIVO_TRANSACCIONES T
             INNER JOIN CM_IMPORTARRIPS I
             ON T.COMPANIA = I.COMPANIA AND
             T.CONSECUTIVO_RIPS = I.CONSECUTIVO
             WHERE T.COMPANIA=UN_COMPANIA
             AND T.COD_PREST_SERV_SALUD =  MI_DATOS_COLUMNAS(10)
             AND T.NUM_FACTURA = MI_DATOS_COLUMNAS(1)
             AND ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_BUSCAR_CONSECUTIVO := 0;
            END;
    
            IF MI_BUSCAR_CONSECUTIVO NOT IN(0) THEN
             MI_MSG_EXCEPTION := PCK_ERRORES.ERR_CM_EXISTEFACT_JSON;
             RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
            END IF;
    
          MI_CAMPOS := 'COMPANIA,
                        NUM_FACTURA,
                        APROBADO,
                        FECHA_EXP_FACTURA,
                        FECHA_INICIAL,
                        FECHA_FINAL,
                        NUM_IDENTIF_PRESTADOR,
                        NOMBRE_RAZON_SOCIAL,
                        TIPO_IDENTI_PREST_SERV_SALUD,
                        NOMBRE_ENTIDAD_ADMINISTRADORA,
                        VALOR_NETO_A_PAGAR_ENTI_CONTR,
                        COD_PREST_SERV_SALUD,
                        PLAN_BENEFICIOS,
                        NUMERO_CONTRATO,
                        NUMERO_POLIZA,
                        CUFE,
                        TIPO_NOTA,
                        NUM_NOTA,
                        CONSECUTIVO_RIPS,
                        CREATED_BY,
                        DATE_CREATED,
                        TIPO_COMPROBANTE';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,-1
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(2)||''', ''YYYY-MM-DD'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(3)||''', ''YYYY-MM-DD'')
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(4)||''', ''YYYY-MM-DD'')
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(8)||'''
                    ,'||MI_DATOS_COLUMNAS(9)||'
                    ,'''||MI_DATOS_COLUMNAS(10)||'''
                    ,'''||MI_DATOS_COLUMNAS(11)||'''
                    ,'''||MI_DATOS_COLUMNAS(12)||'''
                    ,'''||MI_DATOS_COLUMNAS(13)||'''
                    ,'''||MI_DATOS_COLUMNAS(14)||'''
                    ,'''||MI_DATOS_COLUMNAS(15)||'''
                    ,'''||MI_DATOS_COLUMNAS(16)||'''
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE
                    ,'''||MI_PARAMETROCOMPRO||''' ';
                    
                    MI_FECHA_EXP := MI_DATOS_COLUMNAS(2);
                    MI_COD_PREST_SERV_SALUD := MI_DATOS_COLUMNAS(10);
              
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CM_ARCHIVO_TRANSACCIONES'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
    
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
               MI_MSGERROR(1).CLAVE := 'LINEA';
               MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
               MI_MSGERROR(1).CLAVE := 'FACTURA';
               MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(1);
               MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
               MI_MSGERROR(2).VALOR := MI_BUSCAR_CONSECUTIVO;
    
               PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD  => SQLCODE,
               UN_ERROR_COD => MI_MSG_EXCEPTION,
               UN_REEMPLAZOS => MI_MSGERROR
               );

                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'TRANSACCIONES';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
          END;
        END LOOP;
      END IF;
    IF UN_CLOB_ARCHIVOS_CONTROL IS NOT NULL AND LENGTH(UN_CLOB_ARCHIVOS_CONTROL) > 0 THEN

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                        UN_LISTA        => UN_CLOB_ARCHIVOS_CONTROL,
                        UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP

        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                                UN_LISTA        => MI_DATOS_FILA(RS),
                                UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_COL);

        -- Columnas esperadas: 1 = COD_ARCHIVO, 2 = NOMBRE_ARCHIVO, 3 = TIPO_ARCHIVO
        MI_COD_ARCHIVO    := MI_DATOS_COLUMNAS(1) || '_' || TO_CHAR(RS);
        MI_NOMBRE_ARCHIVO := MI_DATOS_COLUMNAS(2);
        MI_TIPO_ARCHIVO   := MI_DATOS_COLUMNAS(3);

        MI_CAMPOS := 'COMPANIA
                     ,FECHA_REMISION
                     ,COD_PREST_SERV_SALUD
                     ,COD_ARCHIVO
                     ,TOTAL_REGISTRO
                     ,VALIDADO
                     ,CONSECUTIVO_RIPS
                     ,CREATED_BY
                     ,DATE_CREATED
                     ,BORRADO_LOGICO
                     ,NOMBRE_ARCHIVO
                     ,TIPO_ARCHIVO';

        MI_VALORES := ''''||UN_COMPANIA||'''
                      ,TO_DATE('''||MI_FECHA_EXP||''', ''YYYY-MM-DD'')
                      ,'''||MI_COD_PREST_SERV_SALUD||'''
                      ,'''||MI_COD_ARCHIVO||'''
                      ,1
                      ,0
                      ,'''||UN_CONSECUTIVO||'''
                      ,'''||UN_USUARIO||'''
                      ,SYSDATE
                      ,0
                      ,'''||MI_NOMBRE_ARCHIVO||'''
                      ,'''||MI_TIPO_ARCHIVO||'''';

        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                  UN_TABLA    => 'CM_ARCHIVO_CONTROL',
                                  UN_ACCION   => 'I',
                                  UN_CAMPOS   => MI_CAMPOS,
                                  UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'ARCHIVO CONTROL';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
         END;

    END LOOP;
END IF;

 IF UN_CLOB_VAL_FACTURA IS NOT NULL AND LENGTH(UN_CLOB_VAL_FACTURA) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_VAL_FACTURA,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);

          MI_CAMPOS := 'COMPANIA
                    ,ESTADO_RESULTADO
                    ,PROCESS_OLD
                    ,NUM_FACTURA
                    ,CODIGO_UNICO_VALIDACION
                    ,FECHA_VALIDACION
                    ,CONSECUTIVO_RIPS
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,TO_DATE('''||MI_DATOS_COLUMNAS(5)||''', ''YYYY-MM-DD HH24:MI:SS'')
                    ,'|| UN_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
                    
                    MI_FACT_VALIDACION := MI_DATOS_COLUMNAS(3);
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'VALIDACION_RIPS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'VALIDACION RIPS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;
    
    IF UN_CLOB_VAL_FACTDETALLE IS NOT NULL AND LENGTH(UN_CLOB_VAL_FACTDETALLE) > 0 THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => UN_CLOB_VAL_FACTDETALLE,
                                                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);
            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'RESULTADOS_VALIDACION_RIPS',
                                                     UN_CRITERIO => 'CONSECUTIVO_RIPS ='||UN_CONSECUTIVO||' AND COMPANIA='''||UN_COMPANIA||'''  AND NUM_FACTURA='''||MI_DATOS_COLUMNAS(7)||''' '  ,
                                                     UN_CAMPO    => 'CONSECUTIVO');
          MI_CAMPOS := 'COMPANIA
                    ,NUM_FACTURA
                    ,CLASE
                    ,CODIGO
                    ,DESCRIPCION
                    ,OBSERVACIONES_NOTIFICACIONES
                    ,PATH_FUENTE
                    ,FUENTE
                    ,CONSECUTIVO_RIPS
                    ,CONSECUTIVO
                    ,CREATED_BY
                    ,DATE_CREATED';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_DATOS_COLUMNAS(7)||'''
                    ,'''||MI_DATOS_COLUMNAS(1)||'''
                    ,'''||MI_DATOS_COLUMNAS(2)||'''
                    ,'''||MI_DATOS_COLUMNAS(3)||'''
                    ,'''||MI_DATOS_COLUMNAS(4)||'''
                    ,'''||MI_DATOS_COLUMNAS(5)||'''
                    ,'''||MI_DATOS_COLUMNAS(6)||'''
                    ,'|| UN_CONSECUTIVO||'
                    ,'||MI_CONSECUTIVO||'
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RESULTADOS_VALIDACION_RIPS'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_AUDITORIACUENTASMEDICAS THEN
                      MI_MSGERROR(1).CLAVE := 'LINEA';
                      MI_MSGERROR(1).VALOR := MI_LINEAARCHIVO;
                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'DETALLE VALIDACION RIPS';
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CM_RIPS_JSON,
                      UN_REEMPLAZOS => MI_MSGERROR
                       );
            END;
        END LOOP;
    END IF;      
    
END PR_CARGAR_RIPS_JSON;

FUNCTION FC_INFFAC120_PLANO
/*
    NAME              : FC_INFFAC120_PLANO
    AUTHORS           : LVEGA
    DATE              : 08/04/2026
    DESCRIPTION       : GENERA EL REGISTRO TIPO 1 (CONTROL) DEL ARCHIVO PLANO
                        SEGUN LA RESOLUCION 220 DE 2024 DEL MINISTERIO DE SALUD
                        Y PROTECCION SOCIAL. REPORTE DE FACTURACION POR SERVICIOS
                        DE SALUD PRESTADOS A POBLACION MIGRANTE NO AFILIADA.
    PARAMETROS DE ENTRADA:
      UN_COMPANIA        : CODIGO DE LA COMPANIA
      UN_FECHAINICIAL    : FECHA INICIAL DEL PERIODO A REPORTAR
      UN_FECHAFINAL      : FECHA FINAL DEL PERIODO A REPORTAR
      UN_TOTALREGISTROS  : CANTIDAD TOTAL DE REGISTROS TIPO 2 DEL ARCHIVO
      
      InformeFAC120_plano
*/
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHAINICIAL          IN DATE,
  UN_FECHAFINAL            IN DATE,
  UN_CLASECUENTAINICIAL    IN NUMBER,
  UN_CLASECUENTAFINAL      IN NUMBER
) RETURN CLOB
AS
  MI_SALIDA             CLOB := '';
  MI_DETALLE            CLOB := '';
  MI_TIPOENTIDAD        COMPANIA.TIPOENTIDAD%TYPE;
  MI_CODIGODIVIPOLA     VARCHAR2(5  CHAR);
  MI_TIPOIDENTIDAD      VARCHAR2(2  CHAR);
  MI_TOTALREGISTROS     NUMBER       := 0;
  MI_PARAMETROAGRUPADO  VARCHAR2(2  CHAR);
  MI_RS                 SYS_REFCURSOR;

  MI_TOTAL_EGRESO_COMPRO  NUMBER := 0;
  MI_TOTAL_APROBADO_RADI  NUMBER := 0;
  MI_VALOR_PAGADO         NUMBER := 0;
  MI_VALOR        VARCHAR2(255);
BEGIN


  SELECT TIPOENTIDAD,
         CASE TIPOENTIDAD
           WHEN 1 THEN TO_CHAR(DEPARTAMENTO)
           WHEN 2 THEN TO_CHAR(CIUDAD)
         END
    INTO MI_TIPOENTIDAD,
         MI_CODIGODIVIPOLA
    FROM COMPANIA
   WHERE CODIGO = UN_COMPANIA;

  MI_TIPOIDENTIDAD := CASE MI_TIPOENTIDAD
                        WHEN 1 THEN 'DE'
                        WHEN 2 THEN 'DI'
                      END;


  MI_PARAMETROAGRUPADO := PCK_SYSMAN_UTL.FC_PAR(
                            UN_COMPANIA  => UN_COMPANIA,
                            UN_NOMBRE    => 'CAUSACION DE CUENTAS MEDICAS AGRUPADO',
                            UN_MODULO    => 84,
                            UN_FECHA_PAR => SYSDATE,
                            UN_IND_MAYUS => 0);


  FOR MI_RS IN (
    SELECT
      CASE T.TIPOENTIDAD
        WHEN 'Publica'  THEN 1
        WHEN 'Privada'  THEN 2
        WHEN 'Mixta'    THEN 3
      END                                          AS TIPO_IPS,
      CAT.NUM_IDENTIF_PRESTADOR                    AS NIT_IPS,
      T.NOMBRE                                     AS NOMBRE_IPS,
      CAT.NUM_IDENTIF_PRESTADOR                    AS PREFIJO_FACTURA,
      CAT.NUM_FACTURA                              AS NUM_FACTURA,
      CAT.FECHA_EXP_FACTURA                        AS FECHA_EMISION,
      CIR.FECHA                                    AS FECHA_RADICACION,
      NVL(CAG.VALOR_NETO_A_PAGAR_ENTI_CONTR, 0)    AS VALOR_TOTAL_FACTURA,
      NVL(CAG.VALOR_OBJECION,                0)    AS VALOR_AUDITADO,
      NVL(CAG.VALOR_OBJECION - 
          CAG.GLOSA_ACEPTADA_IPS,            0)    AS VALOR_GLOSA_CONCILIAR,
      NVL(CAG.GLOSA_ACEPTADA_IPS,            0)    AS VALOR_GLOSA_DEFINITIVA,
      NVL(CAG.TOTAL_APROBADO_PAGAR,          0)    AS VALOR_RECONOCIDO,
      CAT.TIPO_COMPROBANTE                         AS TIPO_COMPROBANTE,
      CAT.NUMERO_COMPROBANTE                       AS NUMERO_COMPROBANTE,
      CAT.CONSECUTIVO_RIPS                         AS CONSECUTIVO_RIPS
    FROM CM_ARCHIVO_TRANSACCIONES  CAT
    INNER JOIN TERCERO            T
           ON  T.COMPANIA          = CAT.COMPANIA
           AND T.NIT               = CAT.NUM_IDENTIF_PRESTADOR
    INNER JOIN CM_IMPORTARRIPS    CIR
           ON  CIR.COMPANIA        = CAT.COMPANIA
           AND CIR.CONSECUTIVO     = CAT.CONSECUTIVO_RIPS
   INNER JOIN (
        SELECT
            COMPANIA,
            CONSECUTIVO_ARCHIVO,
            COD_PREST_SERV_SALUD,
            NUM_FACTURA,
            SUM(VALOR_NETO_A_PAGAR_ENTI_CONTR) AS VALOR_NETO_A_PAGAR_ENTI_CONTR,
            SUM(VALOR_OBJECION)                AS VALOR_OBJECION,
            SUM(GLOSA_ACEPTADA_IPS)            AS GLOSA_ACEPTADA_IPS,
            SUM(TOTAL_APROBADO_PAGAR)          AS TOTAL_APROBADO_PAGAR
        FROM CM_AUDITORIA_GLOSAS
        GROUP BY
            COMPANIA,
            CONSECUTIVO_ARCHIVO,
            COD_PREST_SERV_SALUD,
            NUM_FACTURA
    ) CAG
           ON CAG.COMPANIA = CAT.COMPANIA
          AND CAG.CONSECUTIVO_ARCHIVO = CAT.CONSECUTIVO_RIPS
          AND CAG.COD_PREST_SERV_SALUD = CAT.COD_PREST_SERV_SALUD
          AND CAG.NUM_FACTURA = CAT.NUM_FACTURA
        WHERE CAT.COMPANIA             = UN_COMPANIA
          AND CAT.CAUSADO              NOT IN (0)
      AND CIR.FECHA BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
      AND CIR.CLASECUENTA BETWEEN UN_CLASECUENTAINICIAL AND UN_CLASECUENTAFINAL
    ORDER BY CAT.CONSECUTIVO_RIPS DESC, CAT.NUM_FACTURA
  )
  LOOP

    MI_TOTALREGISTROS := MI_TOTALREGISTROS + 1;


    IF MI_PARAMETROAGRUPADO = 'SI' THEN

      -- SUMATORIA DE EGRESOS DEL COMPROBANTE
      SELECT NVL(SUM(DCC.VALOR_DEBITO), 0)
        INTO MI_TOTAL_EGRESO_COMPRO
        FROM DETALLE_COMPROBANTE_CNT DCC
       INNER JOIN TIPO_COMPROBANTE   TC
               ON TC.COMPANIA        = DCC.COMPANIA
              AND TC.CODIGO          = DCC.TIPO_CPTE
       WHERE DCC.COMPANIA            = UN_COMPANIA
         AND DCC.TIPO_CPTE_AFECT           = MI_RS.TIPO_COMPROBANTE
         AND DCC.CMPTE_AFECTADO            = MI_RS.NUMERO_COMPROBANTE
         AND DCC.FECHA BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL;

      --  SUMATORIA TOTAL_APROBADO_PAGAR DE TODAS LAS FACTURAS DEL RADICADO
      SELECT NVL(SUM(CAG2.TOTAL_APROBADO_PAGAR), 0)
        INTO MI_TOTAL_APROBADO_RADI
        FROM CM_AUDITORIA_GLOSAS    CAG2
       INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
               ON CAT2.COMPANIA     = CAG2.COMPANIA
              AND CAT2.CONSECUTIVO_RIPS = CAG2.CONSECUTIVO_ARCHIVO
              AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
              AND CAT2.NUM_FACTURA  = CAG2.NUM_FACTURA
       WHERE CAG2.COMPANIA          = UN_COMPANIA
         AND CAT2.CONSECUTIVO_RIPS  = MI_RS.CONSECUTIVO_RIPS;
         
         MI_VALOR :=  MI_RS.CONSECUTIVO_RIPS;

      -- CALCULAR VALOR PAGADO PROPORCIONAL
      -- PROPORCION = VALOR_RECONOCIDO_FACTURA / TOTAL_APROBADO_RADICADO
      -- VALOR_PAGADO = TOTAL_EGRESO_COMPROBANTE * PROPORCION
       IF MI_TOTAL_APROBADO_RADI > 0 THEN
          MI_VALOR_PAGADO := ROUND(
                                   MI_TOTAL_EGRESO_COMPRO *
                                   (MI_RS.VALOR_RECONOCIDO / MI_TOTAL_APROBADO_RADI),
                                0);
        ELSE
           MI_VALOR_PAGADO := 0;
        END IF;

    ELSE

      SELECT NVL(SUM(DCC.VALOR_DEBITO), 0)
        INTO MI_VALOR_PAGADO
        FROM DETALLE_COMPROBANTE_CNT DCC
       INNER JOIN TIPO_COMPROBANTE   TC
               ON TC.COMPANIA        = DCC.COMPANIA
              AND TC.CODIGO          = DCC.TIPO_CPTE
       WHERE DCC.COMPANIA            = UN_COMPANIA
         AND DCC.TIPO_CPTE_AFECT     = MI_RS.TIPO_COMPROBANTE
         AND DCC.CMPTE_AFECTADO      = MI_RS.NUMERO_COMPROBANTE
         AND DCC.FECHA BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL;

    END IF;

    
    MI_DETALLE := MI_DETALLE || TO_CLOB(
      '2'
      || '|' || TO_CHAR(MI_TOTALREGISTROS)
      || '|' || TO_CHAR(MI_RS.TIPO_IPS)
      || '|' || MI_RS.NIT_IPS
      || '|' || MI_RS.NOMBRE_IPS
      || '|' || 'NO'
      || '|' || ''      
      || '|' || MI_RS.NUM_FACTURA
      || '|' || TO_CHAR(MI_RS.FECHA_EMISION,   'YYYY-MM-DD')
      || '|' || TO_CHAR(MI_RS.FECHA_RADICACION,'YYYY-MM-DD')
      || '|' || 'I'
      || '|' || TO_CHAR(MI_RS.VALOR_TOTAL_FACTURA)
      || '|' || TO_CHAR(MI_RS.VALOR_AUDITADO)
      || '|' || TO_CHAR(MI_RS.VALOR_GLOSA_CONCILIAR)
      || '|' || TO_CHAR(MI_RS.VALOR_GLOSA_DEFINITIVA)
      || '|' || TO_CHAR(MI_RS.VALOR_RECONOCIDO)
      || '|' || TO_CHAR(MI_VALOR_PAGADO)
      || '|' || TO_CHAR(MI_RS.VALOR_RECONOCIDO - MI_VALOR_PAGADO)
      || '|' || '0'
      || '|' || 'NO'
      || '|' || '0'
      || '|' || ''
      || CHR(13)||CHR(10)
    );

  END LOOP;


  MI_SALIDA := TO_CLOB(
    '1'
    || '|' || MI_TIPOIDENTIDAD
    || '|' || MI_CODIGODIVIPOLA
    || '|' || TO_CHAR(UN_FECHAINICIAL, 'YYYY-MM-DD')
    || '|' || TO_CHAR(UN_FECHAFINAL,   'YYYY-MM-DD')
    || '|' || TO_CHAR(MI_TOTALREGISTROS)
    || CHR(13)||CHR(10)
  ) || MI_DETALLE;

  RETURN MI_SALIDA;

END FC_INFFAC120_PLANO;

FUNCTION FC_INF_FT033_PLANO
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL       IN DATE,
    UN_FECHAFINAL         IN DATE,
    UN_CLASECUENTAINICIAL IN NUMBER,
    UN_CLASECUENTAFINAL   IN NUMBER
) RETURN CLOB
AS
    MI_SALIDA              CLOB := '';
    MI_PARAMETROAGRUPADO   VARCHAR2(2 CHAR);

    MI_TOTAL_EGRESO_COMPRO NUMBER := 0;
    MI_TOTAL_APROBADO_RADI NUMBER := 0;
    MI_VALOR_PAGADO        NUMBER := 0;
    MI_PENDIENTE_PAGO      NUMBER := 0;

BEGIN

    FOR MI_RS IN (
        SELECT
            CASE T.TIPOID
                WHEN 'N'  THEN 'NI'
                WHEN 'C'  THEN 'CC'
                WHEN 'E'  THEN 'CE'
                WHEN 'I'  THEN 'PT'
                WHEN 'DE' THEN 'DE'
                ELSE 'OT'
            END                                                         AS TIPO_ID_ACREEDOR,
            CAT.NUM_IDENTIF_PRESTADOR                                   AS ID_ACREEDOR,
            NVL(T.DIGITOVERIFICACION, '0')                              AS DV_ACREEDOR,
            T.NOMBRE                                                    AS NOMBRE_ACREEDOR,
            CASE T.TIPOENTIDAD
                WHEN 'Publica' THEN 'PU'
                WHEN 'Privada' THEN 'PR'
                WHEN 'Mixta'   THEN 'MX'
                ELSE 'PR'
            END AS NATURALEZA_ACREEDOR,
            EXTRACT(YEAR FROM CIR.FECHA) AS VIGENCIA_DEUDA,
            NVL(CAG_AGR.VALOR_RADICADO,0)          AS VALOR_RADICADO,
            NVL(CAG_AGR.PENDIENTE_AUDITAR,0)       AS PENDIENTE_AUDITAR,
            NVL(CAG_AGR.VALOR_AUDITADO,0)          AS VALOR_AUDITADO,
            NVL(CAG_AGR.VALOR_GLOSA,0)             AS VALOR_GLOSA,
            NVL(CAG_AGR.PENDIENTE_CONCILIAR,0)     AS PENDIENTE_CONCILIAR,
            NVL(CAG_AGR.VALOR_RECONOCIDO,0)        AS VALOR_RECONOCIDO,
            CAT.TIPO_COMPROBANTE,
            CAT.NUMERO_COMPROBANTE,
            CAT.CONSECUTIVO_RIPS
        FROM CM_ARCHIVO_TRANSACCIONES CAT
        INNER JOIN TERCERO T
            ON T.COMPANIA = CAT.COMPANIA
           AND T.NIT      = CAT.NUM_IDENTIF_PRESTADOR
        INNER JOIN CM_IMPORTARRIPS CIR
            ON CIR.COMPANIA    = CAT.COMPANIA
           AND CIR.CONSECUTIVO = CAT.CONSECUTIVO_RIPS
        INNER JOIN (
            SELECT
                CAT_I.COMPANIA,
                CAT_I.CONSECUTIVO_RIPS,
                CAT_I.TIPO_COMPROBANTE,
                CAT_I.NUMERO_COMPROBANTE,
                NVL(SUM(CAG_I.VALOR_RADICADO),0)          AS VALOR_RADICADO,
                NVL(SUM(CAG_I.PENDIENTE_AUDITAR),0)       AS PENDIENTE_AUDITAR,
                NVL(SUM(CAG_I.VALOR_AUDITADO),0)          AS VALOR_AUDITADO,
                NVL(SUM(CAG_I.VALOR_GLOSA),0)             AS VALOR_GLOSA,
                NVL(SUM(CAG_I.PENDIENTE_CONCILIAR),0)     AS PENDIENTE_CONCILIAR,
                NVL(SUM(CAG_I.VALOR_RECONOCIDO),0)        AS VALOR_RECONOCIDO
            FROM CM_ARCHIVO_TRANSACCIONES CAT_I
            INNER JOIN (
                SELECT
                    COMPANIA,
                    CONSECUTIVO_ARCHIVO,
                    COD_PREST_SERV_SALUD,
                    NUM_FACTURA,
                    SUM(VALOR_NETO_A_PAGAR_ENTI_CONTR) AS VALOR_RADICADO,
                    SUM(
                        CASE
                            WHEN NVL(TOTAL_APROBADO_PAGAR,0) = 0
                            THEN NVL(VALOR_OBJECION,0)
                                 - NVL(GLOSA_ACEPTADA_IPS,0)
                            ELSE 0
                        END
                    ) AS PENDIENTE_AUDITAR,
                    SUM(
                        CASE
                            WHEN NVL(TOTAL_APROBADO_PAGAR,0) > 0
                            THEN NVL(VALOR_OBJECION,0)
                            ELSE 0
                        END
                    ) AS VALOR_AUDITADO,
                    SUM(GLOSA_ACEPTADA_IPS) AS VALOR_GLOSA,
                    SUM(
                        VALOR_OBJECION
                        - GLOSA_ACEPTADA_IPS
                    ) AS PENDIENTE_CONCILIAR,
                    SUM(TOTAL_APROBADO_PAGAR) AS VALOR_RECONOCIDO
                FROM CM_AUDITORIA_GLOSAS
                GROUP BY
                    COMPANIA,
                    CONSECUTIVO_ARCHIVO,
                    COD_PREST_SERV_SALUD,
                    NUM_FACTURA
            ) CAG_I
                    ON CAG_I.COMPANIA             = CAT_I.COMPANIA
                   AND CAG_I.CONSECUTIVO_ARCHIVO  = CAT_I.CONSECUTIVO_RIPS
                   AND CAG_I.COD_PREST_SERV_SALUD = CAT_I.COD_PREST_SERV_SALUD
                   AND CAG_I.NUM_FACTURA          = CAT_I.NUM_FACTURA
            WHERE CAT_I.COMPANIA = UN_COMPANIA
            GROUP BY
                CAT_I.COMPANIA,
                CAT_I.CONSECUTIVO_RIPS,
                CAT_I.TIPO_COMPROBANTE,
                CAT_I.NUMERO_COMPROBANTE
        ) CAG_AGR
                ON CAG_AGR.COMPANIA           = CAT.COMPANIA
               AND CAG_AGR.CONSECUTIVO_RIPS   = CAT.CONSECUTIVO_RIPS
               AND CAG_AGR.TIPO_COMPROBANTE   = CAT.TIPO_COMPROBANTE
               AND CAG_AGR.NUMERO_COMPROBANTE = CAT.NUMERO_COMPROBANTE
        WHERE CAT.COMPANIA = UN_COMPANIA
          AND CAT.CAUSADO <> 0
          AND CIR.FECHA BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
          AND CIR.CLASECUENTA BETWEEN UN_CLASECUENTAINICIAL
                                  AND UN_CLASECUENTAFINAL
        GROUP BY
        T.TIPOID,
        CAT.NUM_IDENTIF_PRESTADOR,
        T.DIGITOVERIFICACION,
        T.NOMBRE,
        T.TIPOENTIDAD,
        EXTRACT(YEAR FROM CIR.FECHA),
        CAG_AGR.VALOR_RADICADO,
        CAG_AGR.PENDIENTE_AUDITAR,
        CAG_AGR.VALOR_AUDITADO,
        CAG_AGR.VALOR_GLOSA,
        CAG_AGR.PENDIENTE_CONCILIAR,
        CAG_AGR.VALOR_RECONOCIDO,
        CAT.COMPANIA,
        CAT.TIPO_COMPROBANTE,
        CAT.NUMERO_COMPROBANTE,
        CAT.CONSECUTIVO_RIPS
        ORDER BY
            CAT.NUM_IDENTIF_PRESTADOR,
            EXTRACT(YEAR FROM CIR.FECHA)
    )
    LOOP

            SELECT NVL(SUM(DCC.VALOR_DEBITO), 0)
      		  INTO MI_VALOR_PAGADO
              FROM DETALLE_COMPROBANTE_CNT DCC
             INNER JOIN TIPO_COMPROBANTE   TC
                     ON TC.COMPANIA        = DCC.COMPANIA
                    AND TC.CODIGO          = DCC.TIPO_CPTE
             WHERE DCC.COMPANIA            = UN_COMPANIA
               AND DCC.TIPO_CPTE_AFECT     = MI_RS.TIPO_COMPROBANTE
               AND DCC.CMPTE_AFECTADO      = MI_RS.NUMERO_COMPROBANTE
               AND DCC.FECHA BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL;

            SELECT NVL(SUM(CAG2.TOTAL_APROBADO_PAGAR), 0)
              INTO MI_TOTAL_APROBADO_RADI
              FROM CM_AUDITORIA_GLOSAS       CAG2
             INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
                     ON CAT2.COMPANIA           = CAG2.COMPANIA
                    AND CAT2.CONSECUTIVO_RIPS   = CAG2.CONSECUTIVO_ARCHIVO
                    AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
                    AND CAT2.NUM_FACTURA        = CAG2.NUM_FACTURA
             WHERE CAG2.COMPANIA               = UN_COMPANIA
               AND CAT2.CONSECUTIVO_RIPS       = MI_RS.CONSECUTIVO_RIPS;
    
    
        MI_PENDIENTE_PAGO := MI_RS.VALOR_RECONOCIDO - MI_VALOR_PAGADO;

        MI_SALIDA := MI_SALIDA || TO_CLOB(
            MI_RS.TIPO_ID_ACREEDOR                          || '|' ||
            MI_RS.ID_ACREEDOR                               || '|' ||
            MI_RS.DV_ACREEDOR                               || '|' ||
            MI_RS.NOMBRE_ACREEDOR                           || '|' ||
            MI_RS.NATURALEZA_ACREEDOR                       || '|' ||
            'MIGR'                                          || '|' ||
            TO_CHAR(MI_RS.VIGENCIA_DEUDA)                   || '|' ||
            TO_CHAR(MI_RS.VALOR_RADICADO)                   || '|' ||
            TO_CHAR(MI_RS.PENDIENTE_AUDITAR)                || '|' ||
            TO_CHAR(MI_RS.VALOR_AUDITADO)                   || '|' ||
            TO_CHAR(MI_RS.VALOR_GLOSA)                      || '|' ||
            TO_CHAR(MI_RS.PENDIENTE_CONCILIAR)              || '|' ||
            TO_CHAR(MI_RS.VALOR_RECONOCIDO)                 || '|' ||
            '0'                                             || '|' ||  -- anticipoPendiente
            TO_CHAR(MI_VALOR_PAGADO)                        || '|' ||  -- pagadoReconocido
            TO_CHAR(MI_VALOR_PAGADO)                        || '|' ||  -- valorPagado
            TO_CHAR(MI_PENDIENTE_PAGO)                      || '|' ||  -- pendientePago
            '0'                                             || '|' ||  -- sgpPrestacion
            '0'                                             || '|' ||  -- excedenteSGP
            '0'                                             || '|' ||  -- impuestoLicor
            '0'                                             || '|' ||  -- impuestoCerveza
            '0'                                             || '|' ||  -- excedenteRentas
            '0'                                             || '|' ||  -- excedenteMaestra
            '0'                                             || '|' ||  -- transferenciasMSPS
            '0'                                             || '|' ||  -- otrosRecursos
            'NA'                                                     || -- cualesOtros
            CHR(13) || CHR(10)
        );

    END LOOP;

    RETURN MI_SALIDA;

END FC_INF_FT033_PLANO;

END PCK_AUDITORIACUENTASMEDICAS;

