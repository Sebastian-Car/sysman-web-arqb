create or replace PACKAGE BODY PCK_PREDIAL_COM7 AS


FUNCTION FC_IMPORTAR_IGAC_MESES
/*
      NAME              : FC_IMPORTAR_IGAC_MESES  --> EN ACCESS Importar_Igac_Meses
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
      DATE MIGRADOR     : 02/03/2017
      TIME              : 10:00 AM
      SOURCE MODULE     : PredialP2017.01.06VB
      MODIFIER          : AURA LILIANA MONROY - JAVIER RODRIGUEZ
      DATE MODIFIED     : 16/08/2017
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION QUE REALIZA EL PROCESO DE CARGAR LOS DATOS DE LAS NUEVAS RESOLUCIONES.
      PARAMETERS        :     
                          UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_ANIO         => AÑO SELECCIONADO POR EL USUARIO EN EL FORMULARIO   		
                          UN_USUARIO			=> USUARIO QUE REALIZA EL PROCESO      
                          UN_NUMEROORDEN	=> NUMERO DEL PROPIETARIO PRINCIPAL POR DEFECTO    

      @NAME: importarIgacMeses 
      @METHOD:  GET
    */ 
(
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO,
    UN_NUMEROORDEN IN PCK_SUBTIPOS.TI_NUMORDEN
)
RETURN NUMBER
AS   
    MI_NOVEDAD                   VARCHAR2(200 CHAR);
    MI_DBLAREATERRENO            PCK_SUBTIPOS.TI_DOBLE;
    MI_DBLAREAMETROS             PCK_SUBTIPOS.TI_DOBLE;
    MI_DBLAREAHA                 PCK_SUBTIPOS.TI_DOBLE;
    MI_INDPROCESADO              PCK_SUBTIPOS.TI_LOGICO;
    MI_NOMBREUS                  VARCHAR2(300 CHAR);
    MI_RESDEPARTAMENTO           DEPARTAMENTO.CODIGO%TYPE;
    MI_RESMUNICIPIO              CIUDAD.CODIGO%TYPE;
    MI_RESRESOLUCION             IP_IGAC_RESOLUCIONES.RESOLUCION%TYPE;
    MI_RESFECHAINGRESOSISTEMA    DATE;
    MI_RESREGISTRADO             PCK_SUBTIPOS.TI_LOGICO;
    MI_RESANO                    PCK_SUBTIPOS.TI_ANIO;
    MI_TABLA                     PCK_SUBTIPOS.TI_TABLA;  
    MI_TABLAUSUARIO              PCK_SUBTIPOS.TI_TABLA;    
    MI_CAMPOS                    PCK_SUBTIPOS.TI_CAMPOS;
    MI_STRUSUARIO                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOSUSUARIO             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                   PCK_SUBTIPOS.TI_VALORES;
    MI_VALORESUSUARIO            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;
    MI_STRCONDUSUARIO            PCK_SUBTIPOS.TI_CONDICION;  
    MI_IDREPORTE                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSRES                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_EXISTEN                   PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_RESOLUCIONESDET           PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_STRCODPADRE               PCK_SUBTIPOS.TI_CODPREDIO;
    MI_REEMPLAZOS                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RS_USU_DEPARTAMENTO       DEPARTAMENTO.CODIGO%TYPE;
    MI_RS_USU_MUNICIPIO          CIUDAD.CODIGO%TYPE;
    MI_RS_USU_RESOLUCION         IP_USUARIOS_PREDIAL.RESOLUCION%TYPE;
    MI_RS_USU_RADICACION         IP_USUARIOS_PREDIAL.RADICACION%TYPE;
    MI_RS_USU_MUTACION           IP_USUARIOS_PREDIAL.MUTACION%TYPE;
    MI_RS_USU_CODIGO             PCK_SUBTIPOS.TI_CODPREDIO;   
    MI_RS_USU_NUMERO_ORDEN       PCK_SUBTIPOS.TI_NUMORDEN;
    MI_RS_USU_NUMERO_REGISTROS   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RS_USU_INDBORRADO         PCK_SUBTIPOS.TI_LOGICO;
    MI_RS_USU_FECHABORRADO       DATE;
    MI_RS_USU_BORRADOPOR         IP_USUARIOS_PREDIAL.RESOLUCION%TYPE;
    MI_RS_USU_TIPO               IP_USUARIOS_PREDIAL.TIPO%TYPE;
    MI_RS_USU_NOMBRE             IP_USUARIOS_PREDIAL.NOMBRE%TYPE; 
    MI_RS_USU_ESTADO_CIVIL       IP_USUARIOS_PREDIAL.ESTADO_CIVIL%TYPE;
    MI_RS_USU_TIPO_NIT           IP_USUARIOS_PREDIAL.TIPO_NIT%TYPE;
    MI_RS_USU_NIT                IP_USUARIOS_PREDIAL.NIT%TYPE;
    MI_RS_USU_DIRECCION          IP_USUARIOS_PREDIAL.DIRECCION%TYPE;
    MI_RS_USU_COMUNA             IP_USUARIOS_PREDIAL.COMUNA%TYPE;
    MI_RS_USU_DESTINO_ECONOMICO  IP_USUARIOS_PREDIAL.DESTINO_ECONOMICO%TYPE;
    MI_RS_USU_AREA_M2            IP_USUARIOS_PREDIAL.AREA_M2%TYPE;
    MI_RS_USU_AREA_CONSTRUIDA    IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA%TYPE;
    MI_RS_USU_AREA_CONST_ANT     IP_USUARIOS_PREDIAL.AREA_CONST_ANT%TYPE;
    MI_RS_USU_AREA_HA            IP_USUARIOS_PREDIAL.AREA_HA%TYPE;
    MI_RS_USU_AVALUO_ANO         IP_USUARIOS_PREDIAL.AVALUO_ANO%TYPE;
    MI_RS_USU_VIGENCIA           IP_USUARIOS_PREDIAL.VIGENCIA%TYPE;
    MI_RS_USU_MAT_INMOBILIARIA   IP_USUARIOS_PREDIAL.MATRICULA_INMOBILIARIA%TYPE;
    MI_RS_USU_PAGO_ANO           IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE;
    MI_RS_USU_INDPAGOAUTOMATICO  IP_USUARIOS_PREDIAL.INDPAGOAUTOMATICO%TYPE;
    MI_RS_USU_OBSERVACIO         IP_USUARIOS_PREDIAL.OBSERVACIO%TYPE;
    MI_RS_USU_ANO_CONSTRUCCION   IP_USUARIOS_PREDIAL.ANO_CONSTRUCCION%TYPE;
    MI_RTIPO2MATRI_INMOB         IP_RESOLUCIONES_TIPO_2.MATRICULA_INMOBILIARIA%TYPE;
    MI_RESDETANTRESOLUCION       IP_IGAC_RESOLUCIONESDET.ANTRESOLUCION%TYPE;
    MI_RESDETANTNOMBRE           IP_IGAC_RESOLUCIONESDET.ANTNOMBRE%TYPE;
    MI_RESDETANTTIPODOCUMENTO    IP_IGAC_RESOLUCIONESDET.ANTTIPODOCUMENTO%TYPE;
    MI_RESDETANTNUMERODOCUMENTO  IP_IGAC_RESOLUCIONESDET.ANTNUMERODOCUMENTO%TYPE;
    MI_RESDETANTDIRECCION        IP_IGAC_RESOLUCIONESDET.ANTDIRECCION%TYPE;
    MI_RESDETANTDESTINOECONOMICO IP_IGAC_RESOLUCIONESDET.ANTDESTINOECONOMICO%TYPE;
    MI_RESDETANTAREATERRENO      IP_IGAC_RESOLUCIONESDET.ANTAREATERRENO%TYPE;
    MI_RESDETANTAREAHECTARES     IP_IGAC_RESOLUCIONESDET.ANTAREAHECTARES%TYPE;
    MI_RESDETANTCONSTRUIDA       IP_IGAC_RESOLUCIONESDET.ANTCONSTRUIDA%TYPE;
    MI_RESDETANTAVALUO           IP_IGAC_RESOLUCIONESDET.ANTAVALUO%TYPE;
    MI_RESDETAREAHECTARES        IP_IGAC_RESOLUCIONESDET.AREAHECTARES%TYPE;
    MI_RSANOPAGO_ANO             IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE;
    MI_RSANOCODIGO               IP_USUARIOS_PREDIAL.CODIGO%TYPE;
    MI_TARIFATRPCOD              IP_TARIFAS.TRPCOD%TYPE;
    MI_TARIFATRPRAN              IP_TARIFAS.TRPRAN%TYPE;
    MI_USUAUXRESOLUCION          IP_USUARIOS_PREDIAL.RESOLUCION%TYPE; 
    MI_USUAUXCODIGO              IP_USUARIOS_PREDIAL.CODIGO%TYPE; 
    MI_USUAUXNOMBRE              IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
    MI_USUAUXTIPO_NIT            IP_USUARIOS_PREDIAL.TIPO_NIT%TYPE;
    MI_USUAUXNIT                 IP_USUARIOS_PREDIAL.NIT%TYPE;
    MI_USUAUXDIRECCION           IP_USUARIOS_PREDIAL.DIRECCION%TYPE;
    MI_USUAUXDESTINO_ECONOMICO   IP_USUARIOS_PREDIAL.DESTINO_ECONOMICO%TYPE;
    MI_USUAUXAREA_M2             IP_USUARIOS_PREDIAL.AREA_M2%TYPE;
    MI_USUAUXAREA_HA             IP_USUARIOS_PREDIAL.AREA_HA%TYPE;
    MI_USUAUXAVALUO_ANO          IP_USUARIOS_PREDIAL.AVALUO_ANO%TYPE;
    MI_USUAUXNUMERO_REGISTRO     IP_USUARIOS_PREDIAL.NUMERO_REGISTROS%TYPE; 
    MI_USUAUXAREA_CONSTRUIDA     IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA%TYPE;
    MI_CODIGOTOTALREG            PCK_SUBTIPOS.TI_CODPREDIO;
    MI_RTA                       PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN     
    -- Etapa = 1
    --'crear un conjunto de los datos cargados de los predios que se registran en esta resolución
    --strEtapa = "8"
    <<RESOLUCIONES>>
    FOR MI_RSTIPO1 IN (
        SELECT   NUMERO_RES LINEA
               , DEPARTAMENTO
               , PAIS 
               , MUNICIPIO
               , RESOLUCION
               , RADICACION
               , CLASE_MUTACION
               , NUMERO_DEL_PREDIO
               , CANCELA_INSCRIBE
               , TIPO_REGISTRO
               , NUMERO_ORDEN
               , TOTAL_REGISTROS
               , NOMBRE
               , ESTADO_CIVIL
               , NUMERO_DOCUMENTO
               , DIRECCION
               , COMUNA
               , DESTINO_ECONOMICO
               , TIPO_DOCUMENTO 
               , AREA_TERRENO
               , AREA_CONSTRUIDA
               , AVALUO
               , VIGENCIA
               , PROCESADO 
          FROM IP_RESOLUCIONES_TIPO_1 
         WHERE COMPANIA   = UN_COMPANIA 
           AND PROCESADO IN (0) 
         ORDER BY NUMERO_RES)
    LOOP  
        --'Inicializar la variable que almacenara el código catastral padre
        --MI_STRCODPADRE = ""
        --strEtapa = "9"
        --'Recorrer los datos de los predios que conforman la resolución
        --SysCmd acSysCmdSetStatus, "Procesando el registro& MI_RSTIPO1.AbsolutePosition + 1 &de& MI_RSTIPO1.RecordCount &--> "
        MI_INDPROCESADO:= -1;
        BEGIN 
            SELECT  
                 DEPARTAMENTO
               , MUNICIPIO
               , RESOLUCION
               , FECHAINGRESOSISTEMA
               , REGISTRADO
               , ANO 
            INTO  
                 MI_RESDEPARTAMENTO
               , MI_RESMUNICIPIO
               , MI_RESRESOLUCION
               , MI_RESFECHAINGRESOSISTEMA
               , MI_RESREGISTRADO
               , MI_RESANO 
            FROM  IP_IGAC_RESOLUCIONES 
            WHERE COMPANIA     = UN_COMPANIA 
              AND PAIS         = MI_RSTIPO1.PAIS
              AND DEPARTAMENTO = MI_RSTIPO1.DEPARTAMENTO
              AND MUNICIPIO    = MI_RSTIPO1.MUNICIPIO
              AND RESOLUCION   = CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                      THEN TO_CHAR(EXTRACT (YEAR FROM SYSDATE))
                                      ELSE TO_CHAR(TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)))
                                 END||MI_RSTIPO1.RESOLUCION
              AND ANO = UN_ANIO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_RESDEPARTAMENTO:=NULL;
            MI_RESMUNICIPIO:=NULL;
            MI_RESRESOLUCION:=NULL;
            MI_RESFECHAINGRESOSISTEMA:=NULL;
            MI_RESREGISTRADO:=NULL;
            MI_RESANO:=NULL;
        END;
        --strEtapa = "10"
        --'Verificar si la resolución existe.
        IF MI_RESRESOLUCION IS NULL THEN
            --'Crear registro de la resolución
            --strEtapa = "11"
            MI_TABLA:='IP_IGAC_RESOLUCIONES';
            MI_CAMPOS:=' COMPANIA
                        ,PAIS
                        ,DEPARTAMENTO
                        ,MUNICIPIO
                        ,RESOLUCION
                        ,FECHAINGRESOSISTEMA
                        ,REGISTRADO
                        ,ANO
                        ,CREATED_BY 
                        ,DATE_CREATED';
            MI_VALORES:=' '''||UN_COMPANIA||''','''
                             ||MI_RSTIPO1.PAIS||''', '''
                             ||MI_RSTIPO1.DEPARTAMENTO||''', '''
                             ||MI_RSTIPO1.MUNICIPIO||''','''
                             ||CASE WHEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9) < 2004
                                    THEN EXTRACT (YEAR FROM SYSDATE)
                                    ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                               END||MI_RSTIPO1.RESOLUCION||''',TO_DATE('''
                             ||CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                    THEN TO_CHAR(SYSDATE,'DD/MM/YYYY')
                                    ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 3, 2)||'/'||SUBSTR(MI_RSTIPO1.VIGENCIA, 1, 2)||'/'||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                               END||''',''DD/MM/YYYY''),-1 ,'
                             ||UN_ANIO||','''
                             ||UN_USUARIO||''',SYSDATE ';
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
                                                  UN_TABLA   => MI_TABLA
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='RESOLUCION';
                    MI_REEMPLAZOS(0).VALOR:=CASE WHEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9) < 2004
                                                 THEN EXTRACT (YEAR FROM SYSDATE)
                                                 ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                                            END||MI_RSTIPO1.RESOLUCION;
                    MI_REEMPLAZOS(1).CLAVE:='ANIO';
                    MI_REEMPLAZOS(1).VALOR:=UN_ANIO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES1
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;
            IF MI_RTA = 0 THEN
                ---'si no se puede crear el registro de la resolución se debe ingresar al registro de novedades y pasar al siguiente registro
                --strEtapa = "12"
                MI_NOVEDAD:= 'Se presentaron inconvenientes en la creación de la resolución '
                              ||MI_RSTIPO1.RESOLUCION
                              ||' la cual tiene relacionado el predio'
                              ||MI_RSTIPO1.NUMERO_DEL_PREDIO;
                MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                              UN_TABLA    => 'IP_IGAC_REPORTE'
                                             ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                               AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                               AND ANO        = '||UN_ANIO||''
                                             ,UN_CAMPO    => 'ID_REP'
                                             ,UN_INICIAL  => '1');
                /*'(TAR:1000065680; FECHA:24/08/2016; AUTOR:AA)
                'Se cambia el campo MI_RS_USU_CODIGO por MI_RSTIPO1.NUMERO_DEL_PREDIO ya que hasta el momento no se ha cargado el recordset
                'de usuarios**/

                MI_TABLA:= 'IP_IGAC_REPORTE';
                MI_CAMPOS:= ' COMPANIA
                             ,NUMERO_ORDEN 
                             ,ID_REP 
                             ,RESOLUCION
                             ,ANO
                             ,FECHA_REGISTRO
                             ,CODIGO
                             ,DESCRIPCION
                             ,TIPO_NOVEDAD
                             ,CREATED_BY 
                             ,DATE_CREATED'; 
                MI_VALORES:=''''||UN_COMPANIA||''','''
                                ||MI_RSTIPO1.NUMERO_ORDEN||''', '''
                                ||MI_IDREPORTE||''', '''
                                ||MI_RSTIPO1.RESOLUCION||''','
                                ||UN_ANIO||', 
                                 SYSDATE, '''
                                || MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                                ||MI_NOVEDAD||''', 4,'''
                                ||UN_USUARIO||''',SYSDATE';
                MI_INDPROCESADO:= 0;
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                      UN_TABLA   => MI_TABLA
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                        MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                        MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                        MI_REEMPLAZOS(1).VALOR:=MI_NOVEDAD;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                --GoTo SigRegistro
                MI_TABLA:='IP_RESOLUCIONES_TIPO_1'; 
                MI_CAMPOS:=' PROCESADO = '||MI_INDPROCESADO;
                MI_CONDICION:='    COMPANIA   = '''||UN_COMPANIA||'''
                               AND NUMERO_RES = '''||MI_RSTIPO1.LINEA||''' 
                               AND PROCESADO IN (0) ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                     UN_TABLA     => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        MI_REEMPLAZOS(0).CLAVE := 'NUMERO_RES';
                        MI_REEMPLAZOS(0).VALOR := MI_RSTIPO1.LINEA;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES3
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                CONTINUE;
            END IF;
        END IF; 

        --'Si el tipo de registro es de cancelación se almacena el código catastral ya que este será el padre de los predios que se inscriben hasta encontrar otro de cancelación
        --strEtapa = "13"
        IF MI_RSTIPO1.CANCELA_INSCRIBE = 'C' THEN
           MI_STRCODPADRE:= MI_RSTIPO1.NUMERO_DEL_PREDIO;
        END IF;
        BEGIN 
             SELECT  
                    MATRICULA_INMOBILIARIA
               INTO MI_RTIPO2MATRI_INMOB  
               FROM IP_RESOLUCIONES_TIPO_2 
              WHERE COMPANIA          = UN_COMPANIA 
                AND NUMERO_DEL_PREDIO = MI_RSTIPO1.NUMERO_DEL_PREDIO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RTIPO2MATRI_INMOB:=NULL;
        END;
        --strEtapa = "14"
        --'cargar los datos correspondientes la tipo de registro 2 de la resolución
        BEGIN 
            SELECT 
                DEPARTAMENTO
               ,MUNICIPIO
               ,RESOLUCION
               ,RADICACION
               ,MUTACION
               ,CODIGO
               ,NUMERO_ORDEN
               ,NUMERO_REGISTROS
               ,INDBORRADO
               ,FECHABORRADO
               ,BORRADOPOR
               ,TIPO
               ,NOMBRE
               ,ESTADO_CIVIL
               ,TIPO_NIT
               ,NIT
               ,DIRECCION
               ,COMUNA
               ,DESTINO_ECONOMICO
               ,AREA_M2
               ,AREA_CONSTRUIDA
               ,AREA_CONST_ANT
               ,AREA_HA
               ,AVALUO_ANO
               ,MATRICULA_INMOBILIARIA
               ,VIGENCIA
               ,PAGO_ANO
               ,INDPAGOAUTOMATICO
               ,OBSERVACIO
               ,ANO_CONSTRUCCION 
          INTO  MI_RS_USU_DEPARTAMENTO
               ,MI_RS_USU_MUNICIPIO
               ,MI_RS_USU_RESOLUCION
               ,MI_RS_USU_RADICACION
               ,MI_RS_USU_MUTACION
               ,MI_RS_USU_CODIGO
               ,MI_RS_USU_NUMERO_ORDEN
               ,MI_RS_USU_NUMERO_REGISTROS
               ,MI_RS_USU_INDBORRADO
               ,MI_RS_USU_FECHABORRADO
               ,MI_RS_USU_BORRADOPOR
               ,MI_RS_USU_TIPO
               ,MI_RS_USU_NOMBRE
               ,MI_RS_USU_ESTADO_CIVIL
               ,MI_RS_USU_TIPO_NIT
               ,MI_RS_USU_NIT
               ,MI_RS_USU_DIRECCION
               ,MI_RS_USU_COMUNA
               ,MI_RS_USU_DESTINO_ECONOMICO
               ,MI_RS_USU_AREA_M2
               ,MI_RS_USU_AREA_CONSTRUIDA
               ,MI_RS_USU_AREA_CONST_ANT
               ,MI_RS_USU_AREA_HA
               ,MI_RS_USU_AVALUO_ANO
               ,MI_RS_USU_MAT_INMOBILIARIA
               ,MI_RS_USU_VIGENCIA
               ,MI_RS_USU_PAGO_ANO
               ,MI_RS_USU_INDPAGOAUTOMATICO
               ,MI_RS_USU_OBSERVACIO
               ,MI_RS_USU_ANO_CONSTRUCCION 
          FROM IP_USUARIOS_PREDIAL 
         WHERE COMPANIA     = UN_COMPANIA 
           AND CODIGO       = MI_RSTIPO1.NUMERO_DEL_PREDIO 
           AND NUMERO_ORDEN = MI_RSTIPO1.NUMERO_ORDEN;
         EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_RS_USU_DEPARTAMENTO:=NULL;
            MI_RS_USU_MUNICIPIO:=NULL;
            MI_RS_USU_RESOLUCION:=NULL;
            MI_RS_USU_RADICACION:=NULL;
            MI_RS_USU_MUTACION:=NULL;
            MI_RS_USU_CODIGO:=NULL;
            MI_RS_USU_NUMERO_ORDEN:=NULL;
            MI_RS_USU_NUMERO_REGISTROS:=NULL;
            MI_RS_USU_INDBORRADO:=NULL;
            MI_RS_USU_FECHABORRADO:=NULL;
            MI_RS_USU_BORRADOPOR:=NULL;
            MI_RS_USU_TIPO:=NULL;
            MI_RS_USU_NOMBRE:=NULL;
            MI_RS_USU_ESTADO_CIVIL:=NULL;
            MI_RS_USU_TIPO_NIT:=NULL;
            MI_RS_USU_NIT:=NULL;
            MI_RS_USU_DIRECCION:=NULL;
            MI_RS_USU_COMUNA:=NULL;
            MI_RS_USU_DESTINO_ECONOMICO:=NULL;
            MI_RS_USU_AREA_M2:=NULL;
            MI_RS_USU_AREA_CONSTRUIDA:=NULL;
            MI_RS_USU_AREA_CONST_ANT:=NULL;
            MI_RS_USU_AREA_HA:=NULL;
            MI_RS_USU_AVALUO_ANO:=NULL;
            MI_RS_USU_VIGENCIA:=NULL;
            MI_RS_USU_MAT_INMOBILIARIA:=NULL;
            MI_RS_USU_PAGO_ANO:=NULL;
            MI_RS_USU_INDPAGOAUTOMATICO:=NULL;
            MI_RS_USU_OBSERVACIO:=NULL;
            MI_RS_USU_ANO_CONSTRUCCION :=NULL;
        END;
        --strEtapa = "15"
        --'Verificar si la cédula catastral existe en el sistema actualmente
        -- Set MI_RS_USU_= db.OpenRecordset(MI_STRSQL)

        --'si existe el predio
        IF MI_RS_USU_CODIGO IS NOT NULL THEN            
            BEGIN 
                SELECT COUNT(1) EXISTEN
                  INTO MI_EXISTEN
                  FROM IP_IGAC_RESOLUCIONESDET 
                 WHERE COMPANIA   = UN_COMPANIA
                   AND RESOLUCION = CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                         THEN EXTRACT( YEAR FROM SYSDATE)
                                         ELSE TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9))
                                    END||MI_RSTIPO1.RESOLUCION;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_EXISTEN:=0;
            END;

            --  strEtapa = "16"

            MI_CONSRES:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                      UN_TABLA    => 'IP_IGAC_RESOLUCIONESDET'
                                     ,UN_CRITERIO => '     COMPANIA     = '''||UN_COMPANIA||'''
                                                       AND PAIS         = '''||MI_RSTIPO1.PAIS||'''
                                                       AND DEPARTAMENTO = '''||MI_RSTIPO1.DEPARTAMENTO||'''
                                                       AND MUNICIPIO    = '''||MI_RSTIPO1.MUNICIPIO||'''
                                                       AND RESOLUCION   = '''||CASE WHEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9) < 2004
                                                                                    THEN TO_CHAR(EXTRACT(YEAR FROM SYSDATE))
                                                                                    ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                                                                               END||MI_RSTIPO1.RESOLUCION||'''
                                                       AND ANO          = '||UN_ANIO
                                     ,UN_CAMPO    => 'CONSECUTIVO'
                                     ,UN_INICIAL  => '1');

            --strEtapa = "17"
            MI_NOMBREUS:= REPLACE(MI_RS_USU_NOMBRE, CHR(39),' ');
            MI_DBLAREATERRENO:= MI_RSTIPO1.AREA_TERRENO;
            /*'17/02/2015
            'Tar-1000052047
            'Gustavo Andrés Figueredo Avila
            'Se asigna valor por omisión de MI_DBLAREAMETROS*/
            MI_DBLAREAMETROS:= MI_RSTIPO1.AREA_TERRENO;

            IF MI_DBLAREATERRENO > 0 THEN
                MI_DBLAREAHA:= TRUNC(MI_DBLAREATERRENO / 10000); --INT
                MI_DBLAREAMETROS:= MI_DBLAREATERRENO - (MI_DBLAREAHA * 10000);
            END IF;
            MI_TABLA:= 'IP_IGAC_RESOLUCIONESDET';
            MI_CAMPOS:=' COMPANIA
                        ,PAIS
                        ,DEPARTAMENTO
                        ,MUNICIPIO
                        ,RESOLUCION
                        ,CONSECUTIVO
                        ,ANO
                        ,VIGENCIA 
                        ,TARIFA
                        ,ULTIMO_ANIO
                        ,TRPRAN
                        ,RADICACION
                        ,MUTACION
                        ,CODIGO
                        ,CANCELAINSCRIBE
                        ,TIPOREGISTRO
                        ,NUMEROORDEN
                        ,TOTALREGISTROS
                        ,NOMBRE
                        ,DIRECCION
                        ,AREATERRENO
                        ,AREACONSTRUIDA
                        ,AVALUO
                        ,DESTINOECONOMICO
                        ,TIPODOCUMENTO
                        ,NUMERODOCUMENTO
                        ,ESTADOCIVIL
                        ,ANTRESOLUCION
                        ,ANTNOMBRE
                        ,ANTTIPODOCUMENTO
                        ,ANTNUMERODOCUMENTO
                        ,ANTDIRECCION
                        ,ANTDESTINOECONOMICO
                        ,ANTAREATERRENO
                        ,ANTCONSTRUIDA
                        ,ANTAVALUO
                        ,ANTAREAHECTARES
                        ,USUARIOCREADOR
                        ,REGISTRADO
                        ,AREATERENOM2
                        ,AREAHECTARES
                        ,OBSERVACION
                        ,ANO_CONSTRUCCION
                        ,FECHAINGRESOSISTEMA
                        ,CREATED_BY
                        ,DATE_CREATED';

            MI_VALORES:=' '''||UN_COMPANIA||''','''
                             ||MI_RSTIPO1.PAIS||''','''
                             ||MI_RSTIPO1.DEPARTAMENTO||''','''
                             ||MI_RSTIPO1.MUNICIPIO||''','''
                             ||CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                    THEN TO_CHAR(EXTRACT(YEAR FROM SYSDATE))
                                    ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                               END || MI_RSTIPO1.RESOLUCION||''','''
                             ||MI_CONSRES||''','''
                             ||UN_ANIO||''','''
                             ||UN_ANIO||''',''CA'','''
                             ||UN_ANIO||''',''01'','''
                             ||MI_RSTIPO1.RADICACION||''','''
                             ||MI_RSTIPO1.CLASE_MUTACION||''','''
                             ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                             ||MI_RSTIPO1.CANCELA_INSCRIBE||''',1,'''
                             ||MI_RSTIPO1.NUMERO_ORDEN||''','
                             ||MI_RSTIPO1.TOTAL_REGISTROS||','''
                             ||MI_RSTIPO1.NOMBRE||''','''
                             ||MI_RSTIPO1.DIRECCION||''','
                             ||MI_RSTIPO1.AREA_TERRENO||','
                             ||MI_RSTIPO1.AREA_CONSTRUIDA||','
                             ||MI_RSTIPO1.AVALUO||','''
                             ||MI_RSTIPO1.DESTINO_ECONOMICO||''','''
                             ||MI_RSTIPO1.TIPO_DOCUMENTO||''','''
                             ||MI_RSTIPO1.NUMERO_DOCUMENTO||''','''
                             ||CASE WHEN MI_RSTIPO1.ESTADO_CIVIL IS NULL 
                                         OR MI_RSTIPO1.ESTADO_CIVIL = ' '
                                    THEN '.' 
                                    ELSE MI_RSTIPO1.ESTADO_CIVIL
                               END||''','''
                             ||NVL(MI_RS_USU_RESOLUCION,'')||''','''
                             ||MI_NOMBREUS||''','''
                             ||MI_RS_USU_TIPO_NIT||''','''
                             ||MI_RS_USU_NIT||''','''
                             ||MI_RS_USU_DIRECCION||''','''
                             ||MI_RS_USU_DESTINO_ECONOMICO||''','
                             ||NVL(MI_RS_USU_AREA_M2,0)||','
                             ||NVL(MI_RS_USU_AREA_CONSTRUIDA,0)||','''
                             ||NVL(MI_RS_USU_AVALUO_ANO, 0)||''','
                             ||TO_NUMBER(NVL(MI_RS_USU_AREA_HA, 0))||','''
                             ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION||''',-1,'
                             ||NVL(MI_RSTIPO1.AREA_TERRENO,0)||','
                             ||CASE WHEN NVL(MI_RSTIPO1.AREA_TERRENO,0) > 0
                                    THEN MI_DBLAREAHA
                                    ELSE 0
                               END||','''
                             ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION||''','
                             ||CASE WHEN NVL(MI_RS_USU_AREA_CONSTRUIDA, 0) = 0 
                                         AND MI_RSTIPO1.AREA_CONSTRUIDA > 0
                                    THEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                                    ELSE NVL(MI_RS_USU_ANO_CONSTRUCCION, 0)
                               END
                             ||',SYSDATE , '''
                             ||UN_USUARIO||''',SYSDATE';
            BEGIN
                BEGIN
                    MI_RESOLUCIONESDET :=PCK_DATOS.FC_ACME(
                                                  UN_TABLA   => MI_TABLA
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:= 'CONSECUTIVO';
                    MI_REEMPLAZOS(0).VALOR:= MI_CONSRES;
                    MI_REEMPLAZOS(1).CLAVE:= 'RESOLUCION';
                    MI_REEMPLAZOS(1).VALOR:= CASE WHEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9) < 2004
                                                  THEN EXTRACT(YEAR FROM SYSDATE)
                                                  ELSE SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                                             END || MI_RSTIPO1.RESOLUCION;
                    MI_REEMPLAZOS(2).CLAVE:= 'ANIO';
                    MI_REEMPLAZOS(2).VALOR:= UN_ANIO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES4
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;
            /*'(TAR:1000065608; FECHA:22/08/2016; AUTOR:AA)
            'Se ajusta para incluir el numero de resgistros en la actualización del número de resgitros.
             */
            MI_TABLAUSUARIO:= 'IP_USUARIOS_PREDIAL'; 
            MI_STRUSUARIO:= '  DEPARTAMENTO     = '''||MI_RSTIPO1.DEPARTAMENTO||'''
                              ,MUNICIPIO        = '''||MI_RSTIPO1.MUNICIPIO||''' 
                              ,RESOLUCION       = '''||MI_RSTIPO1.RESOLUCION||''' 
                              ,OBSERVACIO       = ''Resolucion No'||MI_RSTIPO1.RESOLUCION||'''  
                              ,RADICACION       = '''||MI_RSTIPO1.RADICACION||''' 
                              ,MUTACION         = '''||MI_RSTIPO1.CLASE_MUTACION||''' 
                              ,NUMERO_REGISTROS = '''||MI_RSTIPO1.TOTAL_REGISTROS||''' 
                              ,DATE_MODIFIED    = SYSDATE
                              ,MODIFIED_BY      = '''||UN_USUARIO||''' ';

            IF MI_RSTIPO1.CANCELA_INSCRIBE = 'C' THEN
                --' se verifica si el predio que se va a cancelar tiene deuda, de ser asi lo ingresa al reporte de novedades y no lo cancela para su verificación
                IF MI_RS_USU_PAGO_ANO <> EXTRACT(YEAR FROM SYSDATE) THEN  --' Si se cumple se debe registrar un novedad de tipo 1.
                    -- strEtapa = "18"
                    MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                     UN_TABLA    => 'IP_IGAC_REPORTE'
                                                    ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                                      AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                                      AND ANO        = '||UN_ANIO||''
                                                    ,UN_CAMPO    => 'ID_REP'
                                                    ,UN_INICIAL  => '1');
                    MI_TABLA:='IP_IGAC_REPORTE';
                    MI_CAMPOS:=' COMPANIA 
                                ,ID_REP
                                ,NUMERO_ORDEN
                                ,RESOLUCION
                                ,ANO
                                ,FECHA_REGISTRO
                                ,CODIGO
                                ,DESCRIPCION
                                ,TIPO_NOVEDAD
                                ,DATE_CREATED
                                ,CREATED_BY'; 
                    MI_VALORES:=' '''||UN_COMPANIA||''','''
                                     ||MI_IDREPORTE||''','''
                                     ||MI_RSTIPO1.NUMERO_ORDEN||''','''
                                     ||MI_RSTIPO1.RESOLUCION||''','''
                                     ||UN_ANIO||''',SYSDATE, '''
                                     ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''',''El Propietario no se canceló porque presenta deuda desde '
                                     ||MI_RS_USU_PAGO_ANO||''', 1,SYSDATE,'''
                                     ||UN_USUARIO||''' ';
                    BEGIN
                        BEGIN
                            MI_RTA :=PCK_DATOS.FC_ACME(
                                                          UN_TABLA   => MI_TABLA
                                                         ,UN_ACCION  => 'I'
                                                         ,UN_CAMPOS  => MI_CAMPOS
                                                         ,UN_VALORES => MI_VALORES );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                            MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                            MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                            MI_REEMPLAZOS(1).VALOR:='El Propietario no se canceló porque presenta deuda desde '
                                                     ||MI_RS_USU_PAGO_ANO;
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                                   ,UN_TABLAERROR => MI_TABLA
                                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                   );
                    END;

                    MI_STRUSUARIO:= MI_STRUSUARIO
                                    ||', INDBORRADO = 0' ;
                ELSE
                  --strEtapa = "19"
                  MI_STRUSUARIO:= MI_STRUSUARIO
                                  ||', INDBORRADO   = -1
                                     , FECHABORRADO = SYSDATE
                                     , BORRADOPOR   = ''RES '|| MI_RSTIPO1.RESOLUCION||''' ';
                  MI_STRCODPADRE:= MI_RSTIPO1.NUMERO_DEL_PREDIO;
               END IF;
            ELSE
               MI_STRUSUARIO:= MI_STRUSUARIO||', INDBORRADO = 0 ';
            END IF;
            --strEtapa = "20"
            MI_STRUSUARIO:= MI_STRUSUARIO
                            ||',   TIPO         = '''|| MI_RSTIPO1.TIPO_REGISTRO
                            ||''', NOMBRE       = '''|| MI_RSTIPO1.NOMBRE
                            ||''', ESTADO_CIVIL = '''|| CASE WHEN MI_RSTIPO1.ESTADO_CIVIL IS NULL
                                                             THEN '.'
                                                             ELSE MI_RSTIPO1.ESTADO_CIVIL
                                                        END
                            ||''', TIPO_NIT  = '''|| MI_RSTIPO1.TIPO_DOCUMENTO
                            ||''', NIT       = '''|| MI_RSTIPO1.NUMERO_DOCUMENTO
                            ||''', DIRECCION = '''|| MI_RSTIPO1.DIRECCION
                            ||''', COMUNA    = '''|| MI_RSTIPO1.COMUNA
                            ||''', DESTINO_ECONOMICO = '''|| MI_RSTIPO1.DESTINO_ECONOMICO
                            ||''', AREA_M2 ='||MI_DBLAREAMETROS
                            ||', AREA_CONST_ANT ='|| NVL(MI_RS_USU_AREA_CONSTRUIDA, 0)
                            ||', AREA_CONSTRUIDA ='||MI_RSTIPO1.AREA_CONSTRUIDA
                            ||', AREA_HA ='|| CASE WHEN MI_RSTIPO1.AREA_TERRENO > 0
                                                   THEN MI_DBLAREAHA
                                                   ELSE 0
                                              END
                            ||', ANO_CONSTRUCCION ='|| CASE WHEN MI_RS_USU_AREA_CONSTRUIDA = 0 
                                                                AND MI_RSTIPO1.AREA_CONSTRUIDA > 0
                                                           THEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)
                                                           ELSE NVL(MI_RS_USU_ANO_CONSTRUCCION, 0)
                                                      END
                            ||', AVALUO_ANO ='||MI_RSTIPO1.AVALUO;

            IF MI_RTIPO2MATRI_INMOB IS NOT NULL THEN
               MI_STRUSUARIO:= MI_STRUSUARIO 
                               ||', MATRICULA_INMOBILIARIA = '''|| MI_RTIPO2MATRI_INMOB||''' ';
            END IF;

            --strEtapa = "21"
            --' ejecuta la consulta que inserta en el detalle de la resolución el registro de predio modificado
            IF MI_RESOLUCIONESDET = 0 THEN
               -- strEtapa = "22"
                MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                     UN_TABLA    => 'IP_IGAC_REPORTE'
                                                    ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                                      AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                                      AND ANO        = '||UN_ANIO||''
                                                    ,UN_CAMPO    => 'ID_REP'
                                                    ,UN_INICIAL  => '1');
                MI_NOVEDAD:= 'El detalle de la Resolución en la cual se relacionada el predio'
                              || MI_RSTIPO1.NUMERO_DEL_PREDIO
                              ||' no se creó';
                MI_TABLA:='IP_IGAC_REPORTE';
                MI_CAMPOS:='  COMPANIA
                            , NUMERO_ORDEN
                            , ID_REP
                            , RESOLUCION
                            , ANO
                            , FECHA_REGISTRO
                            , CODIGO
                            , DESCRIPCION
                            , TIPO_NOVEDAD
                            , DATE_CREATED
                            , CREATED_BY';
                MI_VALORES:=' '''||UN_COMPANIA||''','''
                                 ||MI_RSTIPO1.NUMERO_ORDEN||''', '''
                                 ||MI_IDREPORTE||''', '''
                                 ||MI_RSTIPO1.RESOLUCION||''','
                                 ||UN_ANIO||',SYSDATE,'''
                                 ||MI_RS_USU_CODIGO||''','''
                                 ||MI_NOVEDAD||''', 4, SYSDATE, '''
                                 ||UN_USUARIO||''' ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                      UN_TABLA   => MI_TABLA
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                        MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                        MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                        MI_REEMPLAZOS(1).VALOR:=MI_NOVEDAD;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;                 
                MI_INDPROCESADO:= -2;
                MI_TABLA:='IP_RESOLUCIONES_TIPO_1'; 
                MI_CAMPOS:=' PROCESADO = '||MI_INDPROCESADO;
                MI_CONDICION:='    COMPANIA   = '''||UN_COMPANIA||'''
                           AND NUMERO_RES = '''||MI_RSTIPO1.LINEA||''' 
                           AND PROCESADO IN (0) ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                     UN_TABLA     => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='NUMERO_RES';
                        MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.LINEA;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES3
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                CONTINUE;
                --GoTo SigRegistro
            END IF;
            --strEtapa = "23"
            MI_STRCONDUSUARIO:='      COMPANIA     ='''||UN_COMPANIA||'''
                                  AND CODIGO       ='''|| MI_RSTIPO1.NUMERO_DEL_PREDIO||''' 
                                  AND NUMERO_ORDEN ='''|| MI_RSTIPO1.NUMERO_ORDEN||''' ' ;
            --' actualiza el estado o modificaciones del predio
            BEGIN
                MI_TABLA:='IP_USUARIOS_PREDIAL';
                BEGIN
                    MI_RTA :=PCK_DATOS.FC_ACME(
                                                  UN_TABLA     => MI_TABLA
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_STRUSUARIO
                                                 ,UN_CONDICION => MI_STRCONDUSUARIO );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                    MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.NUMERO_DEL_PREDIO;
                    MI_REEMPLAZOS(1).CLAVE:='NROORDEN';
                    MI_REEMPLAZOS(1).VALOR:=MI_RSTIPO1.NUMERO_ORDEN;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES5
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;            

            IF MI_RTA = 0 THEN
                --strEtapa = "24"
                MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                             UN_TABLA    => 'IP_IGAC_REPORTE'
                                            ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                              AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                              AND ANO        = '||UN_ANIO||''
                                            ,UN_CAMPO    => 'ID_REP'
                                            ,UN_INICIAL  => '1');
                MI_NOVEDAD:= 'El estado del predio e información de la resolución no se actualizó correctamente en el sistema';
                MI_TABLA:='IP_IGAC_REPORTE';
                MI_CAMPOS:=' COMPANIA
                            ,NUMERO_ORDEN
                            ,ID_REP
                            ,RESOLUCION
                            ,ANO
                            ,FECHA_REGISTRO
                            ,CODIGO
                            ,DESCRIPCION
                            ,TIPO_NOVEDAD
                            ,DATE_CREATED
                            ,CREATED_BY';
                MI_VALORES:=' '''||UN_COMPANIA||''','''
                                 ||MI_RSTIPO1.NUMERO_ORDEN||''', '''
                                 ||MI_IDREPORTE||''', '''
                                 ||MI_RSTIPO1.RESOLUCION||''','
                                 ||UN_ANIO||',SYSDATE, '''
                                 ||MI_RS_USU_CODIGO||''','''
                                 ||MI_NOVEDAD||''', 4, SYSDATE,'''
                                 ||UN_USUARIO||''' ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                      UN_TABLA   => MI_TABLA
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                        MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                        MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                        MI_REEMPLAZOS(1).VALOR:=MI_NOVEDAD;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                MI_INDPROCESADO:= 0;
                MI_TABLA:='IP_RESOLUCIONES_TIPO_1'; 
                MI_CAMPOS:=' PROCESADO = '||MI_INDPROCESADO;
                MI_CONDICION:='    COMPANIA   = '''||UN_COMPANIA||'''
                               AND NUMERO_RES = '''||MI_RSTIPO1.LINEA||''' 
                               AND PROCESADO IN (0) ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                     UN_TABLA     => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='NUMERO_RES';
                        MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.LINEA;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES3
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                CONTINUE;
                --GoTo SigRegistro
            END IF;
            --RsResolucionDet.Requery
            BEGIN
                SELECT  
                        ANTRESOLUCION
                       ,ANTNOMBRE
                       ,ANTTIPODOCUMENTO
                       ,ANTNUMERODOCUMENTO
                       ,ANTDIRECCION
                       ,ANTDESTINOECONOMICO
                       ,ANTAREATERRENO
                       ,ANTAREAHECTARES
                       ,ANTCONSTRUIDA
                       ,ANTAVALUO
                 INTO   MI_RESDETANTRESOLUCION
                       ,MI_RESDETANTNOMBRE
                       ,MI_RESDETANTTIPODOCUMENTO
                       ,MI_RESDETANTNUMERODOCUMENTO
                       ,MI_RESDETANTDIRECCION
                       ,MI_RESDETANTDESTINOECONOMICO
                       ,MI_RESDETANTAREATERRENO
                       ,MI_RESDETANTAREAHECTARES
                       ,MI_RESDETANTCONSTRUIDA
                       ,MI_RESDETANTAVALUO
                FROM   IP_IGAC_RESOLUCIONESDET 
                WHERE COMPANIA     = UN_COMPANIA   
                  AND PAIS         = MI_RSTIPO1.PAIS
                  AND DEPARTAMENTO = MI_RSTIPO1.DEPARTAMENTO
                  AND MUNICIPIO    = MI_RSTIPO1.MUNICIPIO
                  AND ANO          = UN_ANIO
                  AND CONSECUTIVO  = MI_CONSRES
                  AND RESOLUCION   = TO_CHAR(CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                                 THEN TO_CHAR(EXTRACT(YEAR FROM SYSDATE))
                                                 ELSE TO_CHAR(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9))
                                             END||MI_RSTIPO1.RESOLUCION);      
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_RESDETANTRESOLUCION:=NULL;
                MI_RESDETANTNOMBRE:=NULL;
                MI_RESDETANTTIPODOCUMENTO:=NULL;
                MI_RESDETANTNUMERODOCUMENTO:=NULL;
                MI_RESDETANTDIRECCION:=NULL;
                MI_RESDETANTDESTINOECONOMICO:=NULL;
                MI_RESDETANTAREATERRENO:=NULL;
                MI_RESDETANTAREAHECTARES:=NULL;
                MI_RESDETANTCONSTRUIDA:=NULL;
                MI_RESDETANTAVALUO:=NULL;
            END;
            --strEtapa = "25"
            IF MI_RSTIPO1.CANCELA_INSCRIBE = 'I' THEN
                IF NVL(MI_RESDETANTRESOLUCION, 0) <> NVL(MI_RS_USU_RESOLUCION, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '112'
                               ,UN_VANMOD      => MI_RESDETANTRESOLUCION
                               ,UN_VNUMOD      => MI_RS_USU_RESOLUCION
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'
                                                   ||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF MI_RESDETANTNOMBRE <> NVL(MI_RS_USU_NOMBRE,'') THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '007'
                               ,UN_VANMOD      => MI_RESDETANTNOMBRE
                               ,UN_VNUMOD      => MI_RS_USU_NOMBRE
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF MI_RESDETANTTIPODOCUMENTO <> NVL(MI_RS_USU_TIPO_NIT,'') THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '008'
                               ,UN_VANMOD      => MI_RESDETANTTIPODOCUMENTO
                               ,UN_VNUMOD      => MI_RS_USU_TIPO_NIT
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF NVL(MI_RESDETANTNUMERODOCUMENTO, 0) <> NVL(MI_RS_USU_NIT, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '009'
                               ,UN_VANMOD      => MI_RESDETANTNUMERODOCUMENTO
                               ,UN_VNUMOD      => MI_RS_USU_NIT
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF MI_RESDETANTDIRECCION <> NVL(MI_RS_USU_DIRECCION,'') THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '011'
                               ,UN_VANMOD      => MI_RESDETANTDIRECCION
                               ,UN_VNUMOD      => MI_RS_USU_DIRECCION
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF MI_RESDETANTDESTINOECONOMICO <> NVL(MI_RS_USU_DESTINO_ECONOMICO,'') THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '017'
                               ,UN_VANMOD      => MI_RESDETANTDESTINOECONOMICO
                               ,UN_VNUMOD      => MI_RS_USU_DESTINO_ECONOMICO
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF NVL(MI_RESDETANTAREATERRENO, 0) <> NVL(MI_RS_USU_AREA_M2, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '014'
                               ,UN_VANMOD      => MI_RESDETANTAREATERRENO
                               ,UN_VNUMOD      => MI_RS_USU_AREA_M2
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF NVL(MI_RESDETANTAREAHECTARES, 0) <> NVL(MI_RS_USU_AREA_HA, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '013'
                               ,UN_VANMOD      => MI_RESDETANTAREAHECTARES
                               ,UN_VNUMOD      => MI_RS_USU_AREA_HA
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF NVL(MI_RESDETANTCONSTRUIDA, 0) <> NVL(MI_RS_USU_AREA_CONSTRUIDA, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '015'
                               ,UN_VANMOD      => MI_RESDETANTCONSTRUIDA
                               ,UN_VNUMOD      => MI_RS_USU_AREA_CONSTRUIDA
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF NVL(MI_RESDETANTAVALUO, 0) <> NVL(MI_RS_USU_AVALUO_ANO, 0) THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '016'
                               ,UN_VANMOD      => MI_RESDETANTAVALUO
                               ,UN_VNUMOD      => MI_RS_USU_AVALUO_ANO
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                END IF;
                IF TO_NUMBER(NVL(MI_RSTIPO1.TOTAL_REGISTROS,0)) <> NVL(MI_RS_USU_NUMERO_REGISTROS, 0) 
                    AND (MI_CODIGOTOTALREG <> MI_RS_USU_CODIGO OR MI_CODIGOTOTALREG IS NULL) THEN
                    /*'(TAR:1000065608; FECHA:22/08/2016; AUTOR:AA)
                    'Se ajusta para que lleve el total de registros de la tabla de resoluciones igac
                    */
                    MI_CODIGOTOTALREG:=MI_RS_USU_CODIGO;
                    PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA    => UN_COMPANIA
                               ,UN_NUMERO_ORDEN => MI_RS_USU_NUMERO_ORDEN
                               ,UN_CODMOD      => MI_RS_USU_CODIGO
                               ,UN_OPEMOD      => UN_USUARIO
                               ,UN_CCOMOD      => '006'
                               ,UN_VANMOD      => TO_NUMBER(NVL(MI_RSTIPO1.TOTAL_REGISTROS,0))
                               ,UN_VNUMOD      => PCK_SYSMAN_UTL.FC_STRZERO(MI_RSTIPO1.TOTAL_REGISTROS+1, 3)
                               ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)'); 
                END IF;
            END IF;    
        --'si no existe el predio
        ELSE
            --strEtapa = "26"
            MI_CONSRES:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                      UN_TABLA    => 'IP_IGAC_RESOLUCIONESDET'
                                     ,UN_CRITERIO => '     COMPANIA     = '''||UN_COMPANIA||'''
                                                       AND PAIS         = '''||MI_RSTIPO1.PAIS||'''
                                                       AND DEPARTAMENTO = '''||MI_RSTIPO1.DEPARTAMENTO||'''
                                                       AND RESOLUCION   = '''||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION||'''
                                                       AND ANO          = '||UN_ANIO
                                     ,UN_CAMPO    => 'CONSECUTIVO'
                                     ,UN_INICIAL  => '1');

            --strEtapa = "27"
            --strEtapa = "28"
            MI_CAMPOSUSUARIO:='  COMPANIA
                               , CODIGO 
                               , PAIS
                               , DATE_CREATED
                               , CREATED_BY ';
            MI_VALORESUSUARIO:= ' '''||UN_COMPANIA||''','''
                                     ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                                     ||MI_RSTIPO1.PAIS||''',SYSDATE, '''
                                     ||UN_USUARIO||''' '
                                      ;
            MI_CAMPOSUSUARIO:=MI_CAMPOSUSUARIO||',VIGENCIA';
            MI_VALORESUSUARIO:= MI_VALORESUSUARIO||','||EXTRACT(YEAR FROM SYSDATE);
            BEGIN
                SELECT  PAGO_ANO
                       ,CODIGO 
                  INTO  MI_RSANOPAGO_ANO
                       ,MI_RSANOCODIGO 
                  FROM IP_USUARIOS_PREDIAL 
                 WHERE COMPANIA     = UN_COMPANIA
                   AND NUMERO_ORDEN = MI_RSTIPO1.NUMERO_ORDEN
                   AND CODIGO       = MI_RSTIPO1.NUMERO_DEL_PREDIO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_RSANOPAGO_ANO:=NULL;
                MI_RSANOCODIGO:=NULL;
            END;
            IF MI_RSANOCODIGO IS NOT NULL THEN
                IF NVL(MI_RSANOPAGO_ANO, 0) NOT IN (0) THEN
                    MI_CAMPOSUSUARIO:= MI_CAMPOSUSUARIO
                                       ||',PAGO_ANO';
                    MI_VALORESUSUARIO:= MI_VALORESUSUARIO||','||MI_RSANOPAGO_ANO;
                    BEGIN  
                        SELECT  TRPCOD
                               ,TRPRAN 
                          INTO  MI_TARIFATRPCOD
                               ,MI_TARIFATRPRAN 
                          FROM IP_TARIFAS 
                         WHERE COMPANIA = UN_COMPANIA
                           AND TRPANO = MI_RSANOPAGO_ANO
                           AND ROWNUM = 1;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_TARIFATRPCOD:=NULL;
                        MI_TARIFATRPRAN:=NULL;
                    END;
                    IF MI_TARIFATRPCOD IS NOT NULL THEN
                         --strEtapa = "29"
                        MI_TABLA:='IP_FACTURADOS';
                        MI_CAMPOS:='  COMPANIA
                                    , CODIGO
                                    , NUMERO_ORDEN
                                    , PREANO
                                    , AVALUO
                                    , TRPCOD
                                    , TRPRAN
                                    , PAGADO
                                    , DATE_CREATED
                                    , CREATED_BY';
                        MI_VALORES:= ' '''||UN_COMPANIA||''','''
                                          ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''', '''
                                          ||MI_RSTIPO1.NUMERO_ORDEN||''','
                                          ||MI_RSANOPAGO_ANO||', 0, '''
                                          ||MI_TARIFATRPCOD||''','''
                                          ||MI_TARIFATRPRAN||''', -1,SYSDATE,'''
                                          ||UN_USUARIO||''' ';
                        BEGIN
                            BEGIN
                                MI_RTA :=PCK_DATOS.FC_ACME(
                                                              UN_TABLA   => MI_TABLA
                                                             ,UN_ACCION  => 'I'
                                                             ,UN_CAMPOS  => MI_CAMPOS
                                                             ,UN_VALORES => MI_VALORES );
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                                MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.NUMERO_DEL_PREDIO;
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES6
                                       ,UN_TABLAERROR => MI_TABLA
                                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                       );
                        END;
                    END IF;
                ELSE
                    --strEtapa = "30"
                    MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                 UN_TABLA    => 'IP_IGAC_REPORTE'
                                                ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                                  AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                                  AND ANO        = '||UN_ANIO||''
                                                ,UN_CAMPO    => 'ID_REP'
                                                ,UN_INICIAL  => '1');
                    MI_TABLA:='IP_IGAC_REPORTE'; 
                    MI_CAMPOS:='   COMPANIA
                                 , NUMERO_ORDEN 
                                 , ID_REP
                                 , RESOLUCION
                                 , ANO
                                 , FECHA_REGISTRO
                                 , CODIGO
                                 , DESCRIPCION
                                 , TIPO_NOVEDAD
                                 , DATE_CREATED
                                 , CREATED_BY ';
                    MI_VALORES:=' '''||UN_COMPANIA||''','''
                                     ||MI_RSTIPO1.NUMERO_ORDEN||''','''
                                     ||MI_IDREPORTE||''','''
                                     ||MI_RSTIPO1.RESOLUCION||''','
                                     ||UN_ANIO||',TO_DATE(SYSDATE, ''DD/MM/YYYY''),'''
                                     ||MI_RSTIPO1.NUMERO_DEL_PREDIO
                                     ||''',''No se logro establecer tarifa para el ultimo año de pago, generando inconvenientes en el registro del facturado'', 3, SYSDATE,'''
                                     ||UN_USUARIO||''' ';
                    BEGIN
                        BEGIN
                            MI_RTA :=PCK_DATOS.FC_ACME(
                                                          UN_TABLA   => MI_TABLA
                                                         ,UN_ACCION  => 'I'
                                                         ,UN_CAMPOS  => MI_CAMPOS
                                                         ,UN_VALORES => MI_VALORES );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                            MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                            MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                            MI_REEMPLAZOS(1).VALOR:='No se logro establecer tarifa para el ultimo año de pago, generando inconvenientes en el registro del facturado';
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                                   ,UN_TABLAERROR => MI_TABLA
                                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                   );
                    END;

                    MI_IDREPORTE:= MI_IDREPORTE + 1;
                    MI_CAMPOSUSUARIO:=MI_CAMPOSUSUARIO||',PAGO_ANO';
                    MI_VALORESUSUARIO:= MI_VALORESUSUARIO||',0';
                END IF;
            ELSE
                --strEtapa = "31"
                MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                             UN_TABLA    => 'IP_IGAC_REPORTE'
                                            ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                              AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                              AND ANO        = '||UN_ANIO||''
                                            ,UN_CAMPO    => 'ID_REP'
                                            ,UN_INICIAL  => '1');
                MI_TABLA:='IP_IGAC_REPORTE';
                MI_CAMPOS:='  COMPANIA
                            , NUMERO_ORDEN 
                            , ID_REP
                            , RESOLUCION
                            , ANO
                            , FECHA_REGISTRO
                            , CODIGO
                            , DESCRIPCION
                            , DATE_CREATED
                            , CREATED_BY'; 
                MI_VALORES:=' '''||UN_COMPANIA||''','''
                                 ||MI_RSTIPO1.NUMERO_ORDEN||''','''
                                 ||MI_IDREPORTE||''','''
                                 ||MI_RSTIPO1.RESOLUCION||''','
                                 ||UN_ANIO
                                 ||',TO_DATE(SYSDATE,''DD/MM/YYYY''),'''
                                 ||MI_RSTIPO1.NUMERO_DEL_PREDIO
                                 ||''',''No se establece el último año de pago porque no se encontro el código padre'', SYSDATE, '''
                                 ||UN_USUARIO||''' ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                      UN_TABLA   => MI_TABLA
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                        MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                        MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                        MI_REEMPLAZOS(1).VALOR:='No se establece el último año de pago porque no se encontro el código padre';
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
                MI_IDREPORTE := MI_IDREPORTE + 1;
                MI_RS_USU_PAGO_ANO:= 0;
            END IF;

            MI_CAMPOSUSUARIO:= MI_CAMPOSUSUARIO
                               ||',INDPAGOAUTOMATICO';
            MI_VALORESUSUARIO:= MI_VALORESUSUARIO||',-1';

            --'para predial nuevo los campos de periodos

            --strEtapa = "32"
            MI_CAMPOSUSUARIO:=MI_CAMPOSUSUARIO||'
                              ,NUMERO_ORDEN
                              ,NUMERO_REGISTROS
                              ,DEPARTAMENTO
                              ,MUNICIPIO
                              ,RESOLUCION
                              ,RADICACION
                              ,MUTACION ';
            MI_VALORESUSUARIO:= MI_VALORESUSUARIO||','''
                                ||MI_RSTIPO1.NUMERO_ORDEN||''','
                                ||MI_RSTIPO1.TOTAL_REGISTROS||','''
                                ||MI_RSTIPO1.DEPARTAMENTO||''','''
                                ||MI_RSTIPO1.MUNICIPIO||''','''
                                ||MI_RSTIPO1.RESOLUCION||''','''
                                ||MI_RSTIPO1.RADICACION||''','''
                                ||MI_RSTIPO1.CLASE_MUTACION||''' ';
            --strEtapa = "33"
            IF MI_RSTIPO1.CANCELA_INSCRIBE = 'C' THEN
                MI_CAMPOSUSUARIO:=MI_CAMPOSUSUARIO
                                  ||',INDBORRADO
                                     ,FECHABORRADO
                                     ,BORRADOPOR';
                MI_VALORESUSUARIO:= MI_VALORESUSUARIO
                                    ||',-1
                                       ,SYSDATE
                                       ,''RES'||MI_RSTIPO1.RESOLUCION||''' ';

            ELSE
                MI_CAMPOSUSUARIO:=MI_CAMPOSUSUARIO
                                  ||',INDBORRADO';
                MI_VALORESUSUARIO:= MI_VALORESUSUARIO
                                    ||',0';
            END IF;
           -- strEtapa = "34"
            MI_CAMPOSUSUARIO:= MI_CAMPOSUSUARIO
                               ||',TIPO
                                  ,NOMBRE
                                  ,ESTADO_CIVIL
                                  ,TIPO_NIT
                                  ,NIT
                                  ,DIRECCION
                                  ,COMUNA
                                  ,DESTINO_ECONOMICO
                                  ,AREA_M2
                                  ,AREA_CONST_ANT
                                  ,AREA_CONSTRUIDA
                                  ,AREA_HA
                                  ,AVALUO_ANO';

            MI_VALORESUSUARIO:= MI_VALORESUSUARIO||','''
                              ||MI_RSTIPO1.TIPO_REGISTRO||''','''
                              ||MI_RSTIPO1.NOMBRE||''','''
                              ||CASE WHEN MI_RSTIPO1.ESTADO_CIVIL IS NULL 
                                     THEN '.'
                                     ELSE MI_RSTIPO1.ESTADO_CIVIL
                                END||''','''
                              ||MI_RSTIPO1.TIPO_DOCUMENTO||''','''
                              ||MI_RSTIPO1.NUMERO_DOCUMENTO||''','''
                              ||MI_RSTIPO1.DIRECCION||''','''
                              ||MI_RSTIPO1.COMUNA||''','''
                              ||MI_RSTIPO1.DESTINO_ECONOMICO||''','
                              ||NVL(MI_DBLAREAMETROS,0)||','
                              ||NVL(MI_RS_USU_AREA_CONSTRUIDA,0)||','
                              ||NVL(MI_RSTIPO1.AREA_CONSTRUIDA,0)||','
                              ||NVL(CASE WHEN MI_RSTIPO1.AREA_TERRENO > 0
                                     THEN MI_DBLAREAHA
                                     ELSE 0
                                END,0)||','
                              ||MI_RSTIPO1.AVALUO;


            IF MI_RTIPO2MATRI_INMOB IS NOT NULL THEN
                MI_CAMPOSUSUARIO:= MI_CAMPOSUSUARIO
                                   ||',MATRICULA_INMOBILIARIA';
                MI_VALORESUSUARIO:= MI_VALORESUSUARIO||''','
                                    ||MI_RTIPO2MATRI_INMOB;
            END IF;    
            BEGIN
                MI_TABLA:='IP_USUARIOS_PREDIAL';
                BEGIN
                    MI_RTA :=PCK_DATOS.FC_ACME(
                                                  UN_TABLA   => MI_TABLA
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOSUSUARIO
                                                 ,UN_VALORES => MI_VALORESUSUARIO );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                    MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.NUMERO_DEL_PREDIO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES7
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;

            --strEtapa = "35"
            --Crea el registro en el detalle de la resolución para el predio evaluado db.Execute strsql 
            MI_TABLA:='IP_IGAC_RESOLUCIONESDET';
            MI_CAMPOS:=' COMPANIA
                        ,PAIS
                        ,DEPARTAMENTO
                        ,MUNICIPIO
                        ,RESOLUCION
                        ,CONSECUTIVO
                        ,ANO
                        ,VIGENCIA
                        ,ULTIMO_ANIO
                        ,TRPRAN
                        ,TARIFA 
                        ,RADICACION
                        ,MUTACION
                        ,CODIGO
                        ,CANCELAINSCRIBE
                        ,TIPOREGISTRO
                        ,NUMEROORDEN
                        ,TOTALREGISTROS
                        ,NOMBRE
                        ,DIRECCION
                        ,AREATERRENO
                        ,AREACONSTRUIDA
                        ,AVALUO
                        ,DESTINOECONOMICO
                        ,TIPODOCUMENTO
                        ,OBSERVACION
                        ,ANO_CONSTRUCCION
                        ,FECHAINGRESOSISTEMA
                        ,DATE_CREATED
                        ,CREATED_BY';
            MI_VALORES:=''''||UN_COMPANIA||''','''
                            ||MI_RSTIPO1.PAIS||''','''
                            ||MI_RSTIPO1.DEPARTAMENTO||''','''
                            ||MI_RSTIPO1.MUNICIPIO||''','''
                            ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION||''','''
                            ||MI_CONSRES||''','
                            ||UN_ANIO||','
                            ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||','
                            ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||',''01'',''CA'','''
                            ||MI_RSTIPO1.RADICACION||''','''
                            ||MI_RSTIPO1.CLASE_MUTACION||''','''
                            ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                            ||MI_RSTIPO1.CANCELA_INSCRIBE||''',1,'''
                            ||MI_RSTIPO1.NUMERO_ORDEN||''','
                            ||MI_RSTIPO1.TOTAL_REGISTROS||','''
                            ||MI_RSTIPO1.NOMBRE||''','''
                            ||MI_RSTIPO1.DIRECCION||''','
                            ||MI_RSTIPO1.AREA_TERRENO||','
                            ||MI_RSTIPO1.AREA_CONSTRUIDA||','
                            ||MI_RSTIPO1.AVALUO||','''
                            ||MI_RSTIPO1.DESTINO_ECONOMICO||''','''
                            ||MI_RSTIPO1.TIPO_DOCUMENTO||''','''
                            ||SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION||''','
                            ||CASE WHEN MI_RSTIPO1.AREA_CONSTRUIDA > 0
                                   THEN SUBSTR(MI_RSTIPO1.VIGENCIA, 5,9)
                                   ELSE 0
                              END
                            ||',SYSDATE,SYSDATE,'''
                            ||UN_USUARIO||''' ';
            BEGIN
                BEGIN
                    MI_RTA :=PCK_DATOS.FC_ACME(
                                                  UN_TABLA   => MI_TABLA
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:= 'CONSECUTIVO';
                    MI_REEMPLAZOS(0).VALOR:= MI_CONSRES;
                    MI_REEMPLAZOS(1).CLAVE:= 'RESOLUCION';
                    MI_REEMPLAZOS(1).VALOR:= SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)||MI_RSTIPO1.RESOLUCION;
                    MI_REEMPLAZOS(2).CLAVE:= 'ANIO';
                    MI_REEMPLAZOS(2).VALOR:= UN_ANIO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES4
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;

            IF MI_RTA = 0 THEN
                MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                             UN_TABLA    => 'IP_IGAC_REPORTE'
                                            ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                              AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                              AND ANO        = '||UN_ANIO||''
                                            ,UN_CAMPO    => 'ID_REP'
                                            ,UN_INICIAL  => '1');
                MI_NOVEDAD:= 'El detalle de la Resolución en la cual se relacionada el predio '||MI_RSTIPO1.NUMERO_DEL_PREDIO||' no se creó';
                MI_TABLA:='IP_IGAC_REPORTE';
                MI_CAMPOS:='  COMPANIA
                             ,ID_REP
                             ,RESOLUCION
                             ,ANO
                             ,FECHA_REGISTRO
                             ,CODIGO
                             ,DESCRIPCION
                             ,TIPO_NOVEDAD
                             ,DATE_CREATED
                             ,CREATED_BY';
                MI_VALORES:=' '''||UN_COMPANIA||''','''
                                 ||MI_IDREPORTE||''','''
                                 ||MI_RSTIPO1.RESOLUCION||''','
                                 ||UN_ANIO
                                 ||',TO_DATE(SYSDATE,''DD/MM/YYYY''), '''
                                 ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                                 ||MI_NOVEDAD||', 4, SYSDATE, '''
                                 ||UN_USUARIO||''' ';
                BEGIN
                    BEGIN
                        MI_RTA :=PCK_DATOS.FC_ACME(
                                                      UN_TABLA   => MI_TABLA
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                        MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                        MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                        MI_REEMPLAZOS(1).VALOR:=MI_NOVEDAD;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
            END IF;

            BEGIN
                SELECT  
                        ANTRESOLUCION
                       ,ANTNOMBRE
                       ,ANTTIPODOCUMENTO
                       ,ANTNUMERODOCUMENTO
                       ,ANTDIRECCION
                       ,ANTDESTINOECONOMICO
                       ,ANTAREATERRENO
                       ,ANTAREAHECTARES
                       ,ANTCONSTRUIDA
                       ,ANTAVALUO
                       ,AREAHECTARES
                 INTO   MI_RESDETANTRESOLUCION
                       ,MI_RESDETANTNOMBRE
                       ,MI_RESDETANTTIPODOCUMENTO
                       ,MI_RESDETANTNUMERODOCUMENTO
                       ,MI_RESDETANTDIRECCION
                       ,MI_RESDETANTDESTINOECONOMICO
                       ,MI_RESDETANTAREATERRENO
                       ,MI_RESDETANTAREAHECTARES
                       ,MI_RESDETANTCONSTRUIDA
                       ,MI_RESDETANTAVALUO
                       ,MI_RESDETAREAHECTARES
                 FROM IP_IGAC_RESOLUCIONESDET 
                WHERE COMPANIA    = UN_COMPANIA   
                  AND CONSECUTIVO = MI_CONSRES
                  AND RESOLUCION  = CASE WHEN TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9)) < 2004
                                        THEN EXTRACT(YEAR FROM SYSDATE)
                                        ELSE TO_NUMBER(SUBSTR(MI_RSTIPO1.VIGENCIA, 5, 9))
                                   END||MI_RSTIPO1.RESOLUCION;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_RESDETANTRESOLUCION:=NULL;
                MI_RESDETANTNOMBRE:=NULL;
                MI_RESDETANTTIPODOCUMENTO:=NULL;
                MI_RESDETANTNUMERODOCUMENTO:=NULL;
                MI_RESDETANTDIRECCION:=NULL;
                MI_RESDETANTDESTINOECONOMICO:=NULL;
                MI_RESDETANTAREATERRENO:=NULL;
                MI_RESDETANTAREAHECTARES:=NULL;
                MI_RESDETANTCONSTRUIDA:=NULL;
                MI_RESDETANTAVALUO:=NULL;
            END;
            --strEtapa = "36"
            BEGIN
                 SELECT 
                         RESOLUCION 
                        ,CODIGO 
                        ,NOMBRE
                        ,TIPO_NIT
                        ,NIT
                        ,DIRECCION
                        ,DESTINO_ECONOMICO
                        ,AREA_M2
                        ,AREA_HA
                        ,AVALUO_ANO
                        ,NUMERO_REGISTROS
                        ,AREA_CONSTRUIDA
                   INTO  MI_USUAUXRESOLUCION 
                        ,MI_USUAUXCODIGO 
                        ,MI_USUAUXNOMBRE
                        ,MI_USUAUXTIPO_NIT
                        ,MI_USUAUXNIT
                        ,MI_USUAUXDIRECCION
                        ,MI_USUAUXDESTINO_ECONOMICO
                        ,MI_USUAUXAREA_M2
                        ,MI_USUAUXAREA_HA
                        ,MI_USUAUXAVALUO_ANO
                        ,MI_USUAUXNUMERO_REGISTRO
                        ,MI_USUAUXAREA_CONSTRUIDA
                   FROM IP_USUARIOS_PREDIAL 
                  WHERE COMPANIA     = UN_COMPANIA
                    AND CODIGO       = MI_RSTIPO1.NUMERO_DEL_PREDIO
                    AND NUMERO_ORDEN = MI_RSTIPO1.NUMERO_ORDEN;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_USUAUXRESOLUCION:=NULL; 
                MI_USUAUXCODIGO:=NULL; 
                MI_USUAUXNOMBRE:=NULL;
                MI_USUAUXTIPO_NIT:=NULL;
                MI_USUAUXNIT:=NULL;
                MI_USUAUXDIRECCION:=NULL;
                MI_USUAUXDESTINO_ECONOMICO:=NULL;
                MI_USUAUXAREA_M2:=NULL;
                MI_USUAUXAREA_HA:=NULL;
                MI_USUAUXAVALUO_ANO:=NULL;
                MI_USUAUXNUMERO_REGISTRO:=NULL;
            END;
            IF MI_USUAUXCODIGO IS NOT NULL THEN
                IF MI_RSTIPO1.CANCELA_INSCRIBE = 'I' THEN
                    IF MI_RESDETANTRESOLUCION IS NOT NULL THEN
                        IF NVL(MI_RESDETANTRESOLUCION, 0) <> MI_USUAUXRESOLUCION THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '112'
                                       ,UN_VANMOD   => MI_RESDETANTRESOLUCION
                                       ,UN_VNUMOD   => MI_USUAUXRESOLUCION
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF MI_RESDETANTNOMBRE <> MI_USUAUXNOMBRE THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '007'
                                       ,UN_VANMOD   => MI_RESDETANTNOMBRE
                                       ,UN_VNUMOD   => MI_USUAUXNombre
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF MI_RESDETANTTIPODOCUMENTO <> MI_USUAUXTIPO_NIT THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '008'
                                       ,UN_VANMOD   => MI_RESDETANTTIPODOCUMENTO
                                       ,UN_VNUMOD   => MI_USUAUXTIPO_NIT
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF NVL(MI_RESDETANTNUMERODOCUMENTO, 0) <> MI_USUAUXNIT THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '009'
                                       ,UN_VANMOD   => MI_RESDETANTNUMERODOCUMENTO
                                       ,UN_VNUMOD   => MI_USUAUXNIT
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF MI_RESDETANTDIRECCION <> MI_USUAUXDIRECCION THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA    => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN  
                                       ,UN_CODMOD      => MI_USUAUXCODIGO
                                       ,UN_OPEMOD      => UN_USUARIO
                                       ,UN_CCOMOD      => '011'
                                       ,UN_VANMOD      => MI_RESDETANTDIRECCION
                                       ,UN_VNUMOD      => MI_USUAUXDIRECCION
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF MI_RESDETANTDESTINOECONOMICO <> MI_USUAUXDESTINO_ECONOMICO THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '017'
                                       ,UN_VANMOD   => MI_RESDETANTDESTINOECONOMICO
                                       ,UN_VNUMOD   => MI_USUAUXDESTINO_ECONOMICO
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF NVL(MI_RESDETANTAREATERRENO, 0) <> MI_USUAUXAREA_M2 THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '014'
                                       ,UN_VANMOD   => MI_RESDETANTAREATERRENO
                                       ,UN_VNUMOD   => MI_USUAUXAREA_M2
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF NVL(MI_RESDETANTAREAHECTARES, 0) <> MI_USUAUXAREA_HA THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD   => MI_USUAUXCODIGO
                                       ,UN_OPEMOD   => UN_USUARIO
                                       ,UN_CCOMOD   => '013'
                                       ,UN_VANMOD   => MI_RESDETANTAREAHECTARES
                                       ,UN_VNUMOD   => MI_USUAUXAREA_HA
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF NVL(MI_RESDETANTCONSTRUIDA, 0) <> MI_USUAUXAREA_CONSTRUIDA THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA    => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN  
                                       ,UN_CODMOD      => MI_USUAUXCODIGO
                                       ,UN_OPEMOD      => UN_USUARIO
                                       ,UN_CCOMOD      => '015'
                                       ,UN_VANMOD      => MI_RESDETANTCONSTRUIDA
                                       ,UN_VNUMOD      => MI_USUAUXAREA_CONSTRUIDA
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF NVL(MI_RESDETANTAVALUO, 0) <> MI_USUAUXAVALUO_ANO THEN
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA    => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD      => MI_USUAUXCODIGO
                                       ,UN_OPEMOD      => UN_USUARIO
                                       ,UN_CCOMOD      => '016'
                                       ,UN_VANMOD      => MI_RESDETANTAVALUO
                                       ,UN_VNUMOD      => MI_USUAUXAVALUO_ANO
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        END IF;
                        IF MI_RSTIPO1.TOTAL_REGISTROS <> MI_USUAUXNUMERO_REGISTRO 
                           AND (MI_CODIGOTOTALREG <> MI_RS_USU_CODIGO OR MI_CODIGOTOTALREG IS NULL) THEN
                            MI_CODIGOTOTALREG:=MI_RS_USU_CODIGO;
                            PCK_PREDIAL.PR_AUDITORIA( 
                                        UN_COMPANIA    => UN_COMPANIA
                                       ,UN_NUMERO_ORDEN => MI_RSTIPO1.NUMERO_ORDEN 
                                       ,UN_CODMOD      => MI_USUAUXCODIGO
                                       ,UN_OPEMOD      => UN_USUARIO
                                       ,UN_CCOMOD      => '006'
                                       ,UN_VANMOD      => MI_USUAUXNUMERO_REGISTRO
                                       ,UN_VNUMOD      => PCK_SYSMAN_UTL.FC_STRZERO(MI_USUAUXNUMERO_REGISTRO+ 1, 3)
                                       ,UN_DESCRIPCION => 'Resolución tipo Mes No'||MI_RSTIPO1.RESOLUCION||'(Plano)');
                        ELSE 
                            MI_TABLA:='IP_USUARIOS_PREDIAL';
                            MI_CAMPOS:='  NUMERO_REGISTROS = '||MI_RSTIPO1.TOTAL_REGISTROS||'
                                        , AREA_HA          = '||NVL(MI_RESDETAREAHECTARES, 0)||'
                                        , DATE_MODIFIED    = SYSDATE 
                                        , MODIFIED_BY      = '''||UN_USUARIO||''' ';
                            MI_CONDICION:= '     COMPANIA = '''||UN_COMPANIA||''' 
                                             AND CODIGO   = '''||MI_USUAUXCODIGO||''' ';
                            BEGIN
                                BEGIN
                                    MI_RTA :=PCK_DATOS.FC_ACME(
                                                                  UN_TABLA     => MI_TABLA
                                                                 ,UN_ACCION    => 'M'
                                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                                 ,UN_CONDICION => MI_CONDICION );
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                    MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                                    MI_REEMPLAZOS(0).VALOR:=MI_USUAUXCODIGO;
                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(    
                                            UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES8
                                           ,UN_TABLAERROR => MI_TABLA
                                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                           );
                            END;
                       END IF;
                   ELSE
                        MI_IDREPORTE:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                     UN_TABLA    => 'IP_IGAC_REPORTE'
                                                    ,UN_CRITERIO => '     COMPANIA   = '''||UN_COMPANIA||'''
                                                                      AND RESOLUCION = '''||MI_RSTIPO1.RESOLUCION||'''
                                                                      AND ANO        = '||UN_ANIO||''
                                                    ,UN_CAMPO    => 'ID_REP'
                                                    ,UN_INICIAL  => '1');
                        MI_NOVEDAD:= 'La auditoria de cambios para el predio '
                                     ||MI_RSTIPO1.NUMERO_DEL_PREDIO
                                     ||' no se registro correctamente';
                        MI_TABLA:='IP_IGAC_REPORTE';
                        MI_CAMPOS:='  COMPANIA
                                    , NUMERO_ORDEN
                                    , ID_REP
                                    , RESOLUCION
                                    , ANO
                                    , FECHA_REGISTRO
                                    , CODIGO
                                    , DESCRIPCION
                                    , TIPO_NOVEDAD
                                    , DATE_CREATED
                                    , CREATED_BY '; 
                        MI_VALORES:=' '''||UN_COMPANIA||''','''
                                         ||MI_RSTIPO1.NUMERO_ORDEN||''','''   
                                         ||MI_IDREPORTE||''','''
                                         ||MI_RSTIPO1.RESOLUCION||''','
                                         || UN_ANIO
                                         ||', TO_DATE(SYSDATE,''DD/MM/YYYY''),'''
                                         ||MI_RSTIPO1.NUMERO_DEL_PREDIO||''','''
                                         ||MI_NOVEDAD||''', 4, SYSDATE,'''
                                         ||UN_USUARIO||''' ';
                        BEGIN
                            BEGIN
                                MI_RTA :=PCK_DATOS.FC_ACME(
                                                             UN_TABLA   => MI_TABLA
                                                            ,UN_ACCION  => 'I'
                                                            ,UN_CAMPOS  => MI_CAMPOS
                                                            ,UN_VALORES => MI_VALORES );
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                MI_REEMPLAZOS(0).CLAVE:='REPORTE';
                                MI_REEMPLAZOS(0).VALOR:=MI_IDREPORTE;
                                MI_REEMPLAZOS(1).CLAVE:='NOVEDAD';
                                MI_REEMPLAZOS(1).VALOR:=MI_NOVEDAD;
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES2
                                       ,UN_TABLAERROR => MI_TABLA
                                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                       );
                        END;
                   END IF;
                END IF;
            END IF;
        --strEtapa = "37"  
        END IF;
        MI_TABLA:='IP_RESOLUCIONES_TIPO_1'; 
        MI_CAMPOS:=' PROCESADO = '||MI_INDPROCESADO;
        MI_CONDICION:='    COMPANIA   = '''||UN_COMPANIA||'''
                       AND NUMERO_RES = '''||MI_RSTIPO1.LINEA||''' 
                       AND PROCESADO IN (0) ';
        BEGIN
            BEGIN
                MI_RTA :=PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='NUMERO_RES';
                MI_REEMPLAZOS(0).VALOR:=MI_RSTIPO1.LINEA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_IMPORTIGAC_MESES3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
       END;
    END LOOP RESOLUCIONES;

  RETURN -1; 
END FC_IMPORTAR_IGAC_MESES;
--2
FUNCTION FC_IMPORTAR_IGAC_TIPO_DOS
/*
      NAME              : FC_IMPORTAR_IGAC_TIPO_DOS  --> EN ACCESS Importar_IGAC_Tipo_Dos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
      DATE MIGRADOR     : 03/03/2017
      TIME              : 12:00 M
      SOURCE MODULE     : PredialP2017.01.06VB
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION QUE REALIZA EL PROCESO DE CARGAR LOS DATOS DE LAS NUEVAS RESOLUCIONES.
      PARAMETERS        :     
                          UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO  		
                          UN_USUARIO			=> USUARIO QUE REALIZA EL PROCESO        

      @NAME: importarIgacTipoDos 
      @METHOD:  GET
    */ 
(
   UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)

RETURN VARCHAR2 
AS 
    MI_NUMREGISTROS  NUMBER := 0;
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS; 
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES; 

BEGIN
    MI_TABLA:= 'IP_INFORMACION_CATASTRAL';
    FOR MI_RS IN (
        SELECT * 
          FROM IP_IGAC_TIPO_DOS 
         WHERE COMPANIA = UN_COMPANIA)
    LOOP
        MI_CAMPOS:= ' CODIGO, 
                      NUMERO_ORDEN, 
                      MATRICULA_INMOBILIARIA, 
                      ZONA_HOM_FISICA_UNO, 
                      ZONA_HOM_ECON_UNO, 
                      ZONA_HOM_AREA1, 
                      ZONA_HOM_FISICA_DOS, 
                      ZONA_HOM_ECON_DOS, 
                      ZONA_HOM_AREA2, 
                      CON_UND_HAB_UNO, 
                      CON_UND_WC_UNO, 
                      CON_UND_LOCAL_UNO, 
                      CON_UND_PISOS_UNO, 
                      CON_UND_ESTRATO_UNO, 
                      CON_UND_DESTINO_UNO, 
                      CON_UND_PUNTOS_UNO, 
                      CON_UND_AREAC_UNO, 
                      CON_UND_HAB_DOS, 
                      CON_UND_WC_DOS, 
                      CON_UND_LOCAL_DOS, 
                      CON_UND_PISOS_DOS, 
                      CON_UND_ESTRATO_DOS, 
                      CON_UND_DESTINO_DOS, 
                      CON_UND_PUNTOS_DOS, 
                      CON_UND_AREAC_DOS, 
                      CON_UND_HAB_TRES, 
                      CON_UND_WC_TRES, 
                      CON_UND_LOCAL_TRES, 
                      CON_UND_PISOS_TRES, 
                      CON_UND_ESTRATO_TRES, 
                      CON_UND_DESTINO_TRES, 
                      CON_UND_PUNTOS_TRES, 
                      CON_UND_AREAC_TRES, 
                      VIGENCIA, 
                      COMPANIA, 
                      CREATED_BY, 
                      DATE_CREATED ';
         MI_VALORES:= ' '''||MI_RS.CODIGO||''','''||
                             MI_RS.NUMERO_ORDEN||''',''' ||
                             MI_RS.MATRICULA_INMOBILIARIA||''','''||
                             MI_RS.ZONA_HOM_FISICA_UNO||''','''||
                             MI_RS.ZONA_HOM_ECON_UNO||''','''||
                             MI_RS.ZONA_HOM_AREA1||''','''||
                             MI_RS.ZONA_HOM_FISICA_DOS||''','''||
                             MI_RS.ZONA_HOM_ECON_DOS||''','''||
                             MI_RS.ZONA_HOM_AREA2||''','''||
                             MI_RS.CON_UND_HAB_UNO||''','''||
                             MI_RS.CON_UND_WC_UNO||''','''||
                             MI_RS.CON_UND_LOCAL_UNO||''','''||
                             MI_RS.CON_UND_PISOS_UNO||''','''||
                             MI_RS.CON_UND_ESTRATO_UNO||''','''||
                             MI_RS.CON_UND_DESTINO_UNO||''','''||
                             MI_RS.CON_UND_PUNTOS_UNO||''','''||
                             MI_RS.CON_UND_M2_UNO||''','''||
                             MI_RS.CON_UND_HAB_DOS||''','''||
                             MI_RS.CON_UND_WC_DOS||''','''||
                             MI_RS.CON_UND_LOCAL_DOS||''','''||
                             MI_RS.CON_UND_PISOS_DOS||''','''||
                             MI_RS.CON_UND_ESTRATO_DOS||''','''||
                             MI_RS.CON_UND_DESTINO_DOS||''','''||
                             MI_RS.CON_UND_PUNTOS_DOS||''','''||
                             MI_RS.CON_UND_M2_DOS||''','''||
                             MI_RS.CON_UND_HAB_TRES||''','''||
                             MI_RS.CON_UND_WC_TRES||''','''||
                             MI_RS.CON_UND_LOCAL_TRES||''','''||
                             MI_RS.CON_UND_PISOS_TRES||''','''||
                             MI_RS.CON_UND_ESTRATO_TRES||''','''||
                             MI_RS.CON_UND_DESTINO_TRES||''','''||
                             MI_RS.CON_UND_PUNTOS_TRES||''','''||
                             MI_RS.CON_UND_M2_TRES||''','''||
                             MI_RS.VIGENCIA||''','''||
                             UN_COMPANIA||''','''|| 
                             UN_USUARIO||''',
                             SYSDATE';
        MI_NUMREGISTROS:=  MI_NUMREGISTROS
                           +PCK_DATOS.FC_ACME(
                                      UN_TABLA   => MI_TABLA
                                     ,UN_ACCION  => 'I'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES
                                     );
    END LOOP; 
    RETURN MI_NUMREGISTROS;
END FC_IMPORTAR_IGAC_TIPO_DOS;   

--3
PROCEDURE PR_PREPARARTIPOMES 
/*
      NAME              : PR_PREPARARTIPOMES  --> EN ACCESS parte de Importar_IGAC_Tipo_Mes
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
      DATE MIGRADOR     : 03/03/2017
      TIME              : 04:00 PM
      SOURCE MODULE     : PredialP2017.01.06VB
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : PROCEDIMIENTO QUE BORRA LAS TABLAS PARA EL PROCESO DE CARGAR LOS DATOS DE LAS NUEVAS RESOLUCIONES.
      PARAMETERS        :     
                          UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO  		   

      @NAME: prepararTipoMes 
      @METHOD:  PUT
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
    MI_TABLA PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;

BEGIN
    MI_TABLA:= 'IP_RESOLUCIONES_TIPO_1';
    MI_CONDICION:= 'COMPANIA = '''||UN_COMPANIA||''' ';
    BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA     => MI_TABLA
                                    ,UN_ACCION    => 'E' 
                                    ,UN_CONDICION => MI_CONDICION
                                    );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    MI_TABLA:= 'IP_RESOLUCIONES_TIPO_2';
    BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA     => MI_TABLA
                                    ,UN_ACCION    => 'E' 
                                    ,UN_CONDICION => MI_CONDICION
                                    );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    MI_TABLA:= 'IP_RESOLUCIONES_TIPO_3';
    BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA     => MI_TABLA
                                    ,UN_ACCION    => 'E' 
                                    ,UN_CONDICION => MI_CONDICION
                                    );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_TIPMESLIMINARTABL
                   ,UN_TABLAERROR => MI_TABLA
                    ); 
END PR_PREPARARTIPOMES;

--4
PROCEDURE PR_PREPARARTIPODOS
/*
      NAME              : PR_PREPARARTIPODOS  --> EN ACCESS parte de Importar_IGAC_Tipo_Dos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
      DATE MIGRADOR     : 03/03/2017
      TIME              : 05:00 PM
      SOURCE MODULE     : PredialP2017.01.06VB
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : PROCEDIMIENTO QUE BORRA LAS TABLAS PARA EL PROCESO DE CARGAR LOS DATOS DE LAS NUEVAS RESOLUCIONES.
      PARAMETERS        :     
                          UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO  		   

      @NAME: prepararTipoDos 
      @METHOD:  PUT
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
)
AS 
    MI_TABLA PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;

BEGIN
    MI_TABLA:= 'IP_INFORMACION_CATASTRAL';
    MI_CONDICION:= 'COMPANIA = '''||UN_COMPANIA||''' ';
    BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA     => MI_TABLA
                                    ,UN_ACCION    => 'E' 
                                    ,UN_CONDICION => MI_CONDICION
                                    );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    MI_TABLA:= 'IP_IGAC_TIPO_DOS';
    BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA     => MI_TABLA
                                    ,UN_ACCION    => 'E' 
                                    ,UN_CONDICION => MI_CONDICION
                                    );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_TIPMESLIMINARTABL
                   ,UN_TABLAERROR => MI_TABLA
                    ); 
END PR_PREPARARTIPODOS;

FUNCTION FC_ACTUALIZARNOTIFICAPREDIAL
/*
    NAME              : FC_ACTUALIZARNOTIFICAPREDIAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 06/03/2017
    TIME              : 10:08 AM  
    SOURCE MODULE     : SysmanAl2015.10.01.accdbto
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : SE RETORNA UNA CADENA CONCATENADA CON EL NOMBRE DE LA DEPENDENCIA
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_CODIGOINICIAL  => CODIGO INICIAL POR EL CUAL SE VA A FILTRAR LA DEPENDENCIA.
                        UN_CODIGOFINAL    => CODIGO FINAL POR EL CUAL SE VA A FILTRAR LA DEPENDENCIA.
                        UN_ORDEN          => NUMERO DE ORDEN POR EL CUAL SE VA A FILTRAR LA TABLA IP_USUARIOS_PREDIAL
                        UN_USUARIO        => USUARIO CON EL QUE SE ESTA TRABAJANDO
                        UN_ANIOSDEUDA     => AÑOS DE DEUDA
                        UN_DEUDAS         => DEUDAS POR EL CUAL SE VA A FILTRAR LA TABLA IP_FACTURADOS
      @NAME:    actualizarNotificaPredial 
      @METHOD:  GET
  */
(
  UN_COMPANIA       IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOINICIAL  IN  IP_USUARIOS_PREDIAL.CODIGO%TYPE,
  UN_CODIGOFINAL    IN  IP_USUARIOS_PREDIAL.CODIGO%TYPE,
  UN_ORDEN          IN  IP_USUARIOS_PREDIAL.NUMERO_ORDEN%TYPE,
  UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO,
  UN_ANIOSDEUDA     IN  IP_FACTURADOS.TOTAL%TYPE,
  UN_DEUDAS         IN  IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE
)
RETURN VARCHAR2
AS
MI_CONSECUTIVOREAL      IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_RETORNO              VARCHAR2(30 CHAR);
BEGIN

    WITH NUMEROSDEFACTURA AS (
      SELECT    CONSECUTIVOREAL
      FROM      IP_NUMEROSDEFACTURA 
      WHERE     TIPO = 'D'
      ORDER BY  SECUENCIA DESC)
    SELECT CONSECUTIVOREAL
    INTO MI_CONSECUTIVOREAL
    FROM NUMEROSDEFACTURA
    WHERE ROWNUM=1;

   MI_CAMPOS :='COMPANIA
              , CONSECUTIVO
              , CODIGO
              , NUMERO_ORDEN
              , USUARIO_CREADOR
              , NOMBRE
              , NIT
              , SUCURSAL
              , ANOINI
              , ANOFIN
              , TOTAL
              , FECHA_REGISTRO
              , CREATED_BY
              , DATE_CREATED ';

   FOR MI_RS IN ( SELECT IP_USUARIOS_PREDIAL.CODIGO,
                        IP_USUARIOS_PREDIAL.NUMERO_ORDEN,
                        IP_USUARIOS_PREDIAL.NOMBRE,
                        IP_USUARIOS_PREDIAL.NIT,
                        IP_USUARIOS_PREDIAL.SUCURSAL,
                        MIN(IP_FACTURADOS.PREANO) MENORANO,
                        MAX(IP_FACTURADOS.PREANO) MAYORANO,
                        SUM(IP_FACTURADOS.TOTAL) TOTALDEUDA
                  FROM IP_USUARIOS_PREDIAL
                  INNER JOIN IP_FACTURADOS
                  ON IP_USUARIOS_PREDIAL.COMPANIA      = IP_FACTURADOS.COMPANIA
                  AND IP_USUARIOS_PREDIAL.CODIGO       = IP_FACTURADOS.CODIGO
                  AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_FACTURADOS.NUMERO_ORDEN
                  WHERE IP_USUARIOS_PREDIAL.COMPANIA                          = UN_COMPANIA
                    AND IP_USUARIOS_PREDIAL.CODIGO                            BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
                    AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN                      = UN_ORDEN
                    AND IP_USUARIOS_PREDIAL.INDBORRADO                        IN (0)
                    AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO                  IN (0)
                    AND TO_CHAR(SYSDATE, 'YYYY')-IP_USUARIOS_PREDIAL.PAGO_ANO > UN_ANIOSDEUDA
                    AND IP_FACTURADOS.PAGADO  = 0
                    AND IP_FACTURADOS.NOCOBRADO = 0
                    AND IP_FACTURADOS.INDPAGO_ACPAG  = 0
                  GROUP BY 
                    IP_USUARIOS_PREDIAL.CODIGO,
                    IP_USUARIOS_PREDIAL.NUMERO_ORDEN,
                    IP_USUARIOS_PREDIAL.NOMBRE,
                    IP_USUARIOS_PREDIAL.NIT,
                    IP_USUARIOS_PREDIAL.SUCURSAL
                  HAVING SUM(IP_FACTURADOS.TOTAL) > UN_DEUDAS)
  LOOP
    MI_CONSECUTIVOREAL:=MI_CONSECUTIVOREAL+1;
    IF MI_RETORNO IS NULL THEN
      MI_RETORNO:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  MI_CONSECUTIVOREAL,
                                                UN_LONGITUD  =>  9);
    END IF;
    MI_VALORES :=''''||UN_COMPANIA||''', 
                 '''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  MI_CONSECUTIVOREAL,
                                                UN_LONGITUD  =>  9)||''', 
                 '''||MI_RS.CODIGO||''', 
                 '''||MI_RS.NUMERO_ORDEN||''', 
                 '''||UN_USUARIO||''', 
                 '''||MI_RS.NOMBRE||''', 
                 '''||MI_RS.NIT||''',
                 '''||MI_RS.SUCURSAL||''',
                 '''||MI_RS.MENORANO||''',
                 '''||MI_RS.MAYORANO||''',
                 ' || MI_RS.TOTALDEUDA || ',
                 TO_DATE('''||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                 '''||UN_USUARIO||''', 
                 TO_DATE('''||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')';
      BEGIN 
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'IP_NOTIFICAPREDIAL', 
                                                 UN_ACCION  =>  'I', 
                                                 UN_CAMPOS  =>  MI_CAMPOS, 
                                                 UN_VALORES =>  MI_VALORES );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_INS_CARTACOBRO
                    );
      END;

  END LOOP;
  MI_RETORNO:=MI_RETORNO||','||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  MI_CONSECUTIVOREAL,
                                                         UN_LONGITUD  =>  9);
  MI_CAMPOS := 'CONSECUTIVOREAL = ''' ||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  MI_CONSECUTIVOREAL,
                                                                  UN_LONGITUD  =>  9) || '''';
  MI_CONDICION := 'TIPO = ''D''';
        BEGIN 
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_NUMEROSDEFACTURA', 
                                                   UN_ACCION    =>  'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS, 
                                                   UN_CONDICION =>  MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ACT_CONSCARTACOBRO
                        );
        END; 
        RETURN MI_RETORNO; 
END FC_ACTUALIZARNOTIFICAPREDIAL;

--6

  FUNCTION FC_ELIMINARACUERDO 
    /*
    NAME              : FC_ELIMINARACUERDO En Access --> CmEliminarAcuerdo_Click() en Form_Usuarios_Predial
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
    DATE MIGRADOR     : 08,09,10,27,28/02/2017, 01,02,03,06/03/2017
    TIME              : 04:07 PM
    SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
    DESCRIPTION       : Proceso que permite eliminar los acuerdos de pago que no están anulados ni pagos.
    PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.  
                        UN_CODIGOACUERDO    => Código del acuerdo que se va a eliminar.
                        UN_USUARIO          => Usuario que realiza la eliminación del acuerdo.
                        UN_CODPREDIO        => Código del predio al cual está registrado el acuerdo.
                        UN_PREANIOI         => Año inicial de las vigencias facturadas del acuerdo de pago.
                        UN_PREANIO          => Año final de las vigencias facturadas del acuerdo.
                        UN_NUMEROORDEN      => Número de orden de los propietarios del predio.
                        UN_CONCUOTASCANC    => Valor que permite identificar si el acuerdo tiene cuotas canceladas, 
                                               su valor puede ser SI o NO.
                        UN_ELIMINARCUOTCANC => Valor que permite identificar si el acuerdo tiene cuotas canceladas y aún así se va
                                               a eliminar, su valor puede ser SI o NO.
                        UN_APLICADSCESP     => Permite identificar si se va a cobrar un excedente por el valor de los descuentos si
                                               el acuerdo tenía descuentos especiales, su valor puede ser SI o NO.
                        UN_LEYDESCESP       => Identifica la ley que se está incumpliendo para eliminar el acuerdo de pago.
                        UN_VIGENCIASALDO    => Vigencia (año) a la cual se le va a crear el excedente por cobrar.

    @NAME:  eliminarAcuerdoDePago
    @METHOD:  GET
    */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOACUERDO        IN IP_ACUERDOS.CODIGOACUERDO%TYPE,
    UN_USUARIO              IN VARCHAR2,
    UN_CODPREDIO            IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_PREANIOI             IN PCK_SUBTIPOS.TI_ANIO,
    UN_PREANIO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_NUMEROORDEN          IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_CONCUOTASCANC        IN VARCHAR2,
    UN_ELIMINARCUOTCANC     IN VARCHAR2,
    UN_APLICADSCESP         IN VARCHAR2,
    UN_LEYDESCESP           IN VARCHAR2 DEFAULT NULL,
    UN_VIGENCIASALDO        IN PCK_SUBTIPOS.TI_ANIO DEFAULT NULL
  )
  RETURN VARCHAR2 AS 
    MI_RTAACME              PCK_SUBTIPOS.TI_RTA_ACME;
    MI_NOFACTURA            IP_FACTURADOSACUERDOS.DOCNUM%TYPE;
    MI_INTPREANO            PCK_SUBTIPOS.TI_ANIO;
    MI_DOCNUM               IP_FACTURADOSACUERDOS.DOCNUM%TYPE;
    MI_BANCOPAGO            IP_FACTURADOSACUERDOS.PAG_BAN%TYPE;
    MI_FECHACUOTA           TIMESTAMP;
    MI_FECHAPAGO            TIMESTAMP;
    MI_TOTALABONO           PCK_SUBTIPOS.TI_DOBLE;
    MI_VIGENCIAABONO        PCK_SUBTIPOS.TI_DOBLE;
    MI_INTACUERDO           PCK_SUBTIPOS.TI_DOBLE;
    MI_INTRECARGO           PCK_SUBTIPOS.TI_DOBLE;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_PREDIO               PCK_SUBTIPOS.TI_CODPREDIO;
    MI_PAG_BAN              IP_FACTURADOSACUERDOS.PAG_BAN%TYPE;
    MI_TOTAL                PCK_SUBTIPOS.TI_DOBLE; 
    MI_PREANOI              PCK_SUBTIPOS.TI_ANIO; 
    MI_PREANO               PCK_SUBTIPOS.TI_ANIO;
    MI_CONTADOR             PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_RESPUESTA            VARCHAR2(15 CHAR);
    MI_VLRDESCESP           PCK_SUBTIPOS.TI_DOBLE;
    MI_OBSDESCESP           VARCHAR2(120 CHAR);
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_AYUDAMSG             VARCHAR2(20 CHAR);
    MI_VIGENCIASALDO        PCK_SUBTIPOS.TI_ANIO;
  BEGIN
  --INICIALIZAR PARAMETROS
  PCK_PARST.PR_INICIALIZAR_PARSISTEMA(UN_COMPANIA=>UN_COMPANIA, 
                                      UN_MODULO  =>GL_MODULO
                                      );
    IF UN_CONCUOTASCANC = 'SI'
    THEN
      IF UN_ELIMINARCUOTCANC = 'SI'
      THEN
        --//Etapa 4: Valida facturas activas para anularlas
        <<ANULA_FACTURAS>>
        FOR RS_FACT_ACUERDOS IN (SELECT DOCNUM 
                                   FROM IP_FACTURADOSACUERDOS 
                                  WHERE COMPANIA      = UN_COMPANIA
                                    AND CODIGOACUERDO = UN_CODIGOACUERDO
                                    AND DOCNUM IS NOT NULL 
                                    AND PAGADO        = 0 
                                    AND PREDIO        = UN_CODPREDIO
                                    AND NUMERO_ORDEN  = UN_NUMEROORDEN)
        LOOP
          BEGIN
            BEGIN
              --//Etapa 4.1: Anula las facturas activas
              MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => ' ANULADO = -1, FECHAANULACION = SYSDATE ',
                                               UN_CONDICION => ' COMPANIA          = ''' ||  UN_COMPANIA ||
                                                              ''' AND DOCNUM       = ''' || RS_FACT_ACUERDOS.DOCNUM || 
                                                              ''' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
              MI_DOCNUM := RS_FACT_ACUERDOS.DOCNUM;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'DOCNUM';
            MI_MSGERROR(1).VALOR := MI_DOCNUM;
           PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ANULAFACTACT,
               UN_REEMPLAZOS => MI_MSGERROR,
               UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
             );
          END;
        END LOOP ANULA_FACTURAS;

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                 UN_NOMBRE    => 'TRABAJA CUOTAS ACUERDO SEGUN VIGENCIAS ADEUDADAS',
                                 UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                 UN_FECHA_PAR => SYSDATE) <> 'SI'
        THEN
        --//Etapa 5: Carga información de última cuota cancelada
          BEGIN 
            SELECT PREANO,
                   DOCNUM,
                   FECHAFACTURADO,
                   PAG_BAN,
                   FECHAPAGO
              INTO MI_INTPREANO,
                   MI_NOFACTURA,
                   MI_FECHACUOTA,
                   MI_BANCOPAGO, 
                   MI_FECHAPAGO     
              FROM IP_FACTURADOSACUERDOS
             WHERE COMPANIA      = UN_COMPANIA 
               AND CODIGOACUERDO = UN_CODIGOACUERDO 
               AND PREDIO        = UN_CODPREDIO 
               AND PAGADO      NOT IN (0)
               AND TOTAL       NOT IN (0)
               AND ROWNUM = 1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NOFACTURA := NVL(MI_NOFACTURA, 'SinDocNum');
            MI_INTPREANO := NVL(MI_INTPREANO, 0);
          END;
          --//Etapa 6: Carga valor abonado hasta el momento
          --'Se actualiza el indicador de es abono en la factura para la cual se va a crear el abono
          <<DOCNUM_ESABONO>>
          FOR RS_FACTACUERDOS IN (SELECT DOCNUM 
                                    FROM IP_FACTURADOSACUERDOS 
                                   WHERE COMPANIA = UN_COMPANIA 
                                     AND CODIGOACUERDO = UN_CODIGOACUERDO
                                     AND PAGADO NOT IN (0) 
                                     AND PREDIO        = UN_CODPREDIO
                                     AND NUMERO_ORDEN  = UN_NUMEROORDEN)
          LOOP
            BEGIN 
              BEGIN
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => ' ESABONO = -1',
                                                 UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                ''' AND DOCNUM       = ''' || RS_FACTACUERDOS.DOCNUM || 
                                                                ''' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                MI_DOCNUM := RS_FACTACUERDOS.DOCNUM;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'DOCNUM';
              MI_MSGERROR(1).VALOR := MI_DOCNUM;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_A_ABONO,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
             );
          END;
          END LOOP DOCNUM_ESABONO;

          --'Obtener el valor abonado hasta el momento a la deuda del predio con las cuotas canceladas.
          BEGIN
            BEGIN 
              SELECT SUM(TOTAL) AS TOTALACUERDO, 
                     SUM(INTERES_ACUERDO) AS INTACUERDO, 
                     SUM(INTERES_RECARGO) AS INTRECARGO 
                INTO MI_TOTALABONO,
                     MI_INTACUERDO,
                     MI_INTRECARGO
                FROM IP_FACTURADOSACUERDOS
               WHERE COMPANIA      = UN_COMPANIA 
                 AND CODIGOACUERDO = UN_CODIGOACUERDO 
                 AND PAGADO NOT IN (0) 
                 AND PREDIO        = UN_CODPREDIO 
                 AND NUMERO_ORDEN  = UN_NUMEROORDEN
               GROUP BY CODIGOACUERDO, PAGADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_TOTALABONO := NULL;
            END; 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'PREDIO';
            MI_MSGERROR(1).VALOR := UN_CODPREDIO;
            MI_MSGERROR(2).CLAVE := 'ACUERDO';
            MI_MSGERROR(2).VALOR := UN_CODIGOACUERDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VALORABONADO,
               UN_REEMPLAZOS => MI_MSGERROR,
               UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
           );
          END;

          IF MI_TOTALABONO IS NOT NULL
          THEN
            --//Etapa 6.1IF: Total abonado
            MI_TOTALABONO := NVL(MI_TOTALABONO, 0) - NVL(MI_INTACUERDO, 0) - NVL(MI_INTRECARGO, 0);
            --//Etapa 6.2IF: Vigencia abonado
            BEGIN
              MI_VIGENCIAABONO := PCK_PREDIAL_COM3.FC_REPARTIRDEUDA(UN_COMPANIA   => UN_COMPANIA,             
                                                                    UN_CODPREDIO  => UN_CODPREDIO,             
                                                                    UN_ANOABONO   => 0,            
                                                                    UN_ABONO      => MI_TOTALABONO,            
                                                                    UN_NUMRECIBO  => MI_NOFACTURA,   
                                                                    UN_INDACUERDO => -1,              
                                                                    UN_USUARIO    => UN_USUARIO);
              IF MI_VIGENCIAABONO IS NULL THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END IF; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'PREDIO';
              MI_MSGERROR(1).VALOR := UN_CODPREDIO;
              MI_MSGERROR(2).CLAVE := 'ACUERDO';
              MI_MSGERROR(2).VALOR := UN_CODIGOACUERDO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VIGABONO,
                 UN_REEMPLAZOS => MI_MSGERROR 
             );
          END;
            --//Etapa 6.3IF: Actualiza pagado y observaciones
            BEGIN 
              BEGIN
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOSABONOS',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => 'PAGADO = -1 , OBSERVACIONES = ''Se originó de la eliminación del acuerdo de pago ' || UN_CODIGOACUERDO 
                                                                || ''', PAG_BAN = ''' || MI_BANCOPAGO 
                                                                || ''', FECHAPAGO = TO_DATE(''' || TO_CHAR(MI_FECHAPAGO, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')',
                                                 UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                ''' AND PAGADO       =  0 ' ||
                                                                  ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'ACUERDO';
              MI_MSGERROR(1).VALOR := UN_CODIGOACUERDO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGYOBS,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOSABONOS'
             );
            END;
            --//Etapa 6.4: Valida vigencia y preano
            BEGIN
              IF MI_VIGENCIAABONO >= UN_PREANIOI
              AND MI_INTPREANO <= UN_PREANIO
              THEN
                BEGIN
                  --//Etapa 6.5IF: Actualiza indicador de pagado
                  MI_AYUDAMSG := UN_PREANIOI || ' y ' || MI_VIGENCIAABONO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'PAGADO = -1, INDPAGO_ACPAG = -1, OBSERVACIONES = ''AP ' || UN_CODIGOACUERDO || '''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || MI_VIGENCIAABONO ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                  --//Etapa 6.6IF: Actualiza observaciones  
                  MI_AYUDAMSG := MI_VIGENCIAABONO || ' y ' || UN_PREANIO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = '' ''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || MI_VIGENCIAABONO || '+ 1 AND ' || UN_PREANIO ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || ''''); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

              ELSIF MI_VIGENCIAABONO = 0
              THEN 
                BEGIN
                  --//Etapa 6.5...: Actualiza observaciones 
                  MI_AYUDAMSG := UN_PREANIOI || ' y ' || UN_PREANIO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = '' ''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || UN_PREANIO ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'PREDIO';
              MI_MSGERROR(1).VALOR := UN_CODPREDIO;
              MI_MSGERROR(2).CLAVE := 'ANIOSMSG';
              MI_MSGERROR(2).VALOR := MI_AYUDAMSG;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGYOBSF,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOS'
             );
            END;
          ELSE --' Actualizacion de tablas si las cuotas se registraron con CNT
            --//Etapa 6ELSE: cuotas registradas CNT
            --//Etapa 6.1ELSE: Inserta Registro en FA 
            BEGIN
              MI_CAMPOS := 'COMPANIA,
                            CODIGO, 
                            NUMERO_ORDEN, 
                            DOCNUM,
                            PREANO, 
                            PAGADO, 
                            C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, 
                            OBSERVACIONES, 
                            PAG_BAN, 
                            FECHAPAGO';
              MI_VALORES := '''' || UN_COMPANIA || ''', '''
                                 || UN_CODPREDIO || ''', ''' 
                                 || UN_NUMEROORDEN || ''', '''
                                 || MI_NOFACTURA || ''', ' 
                                 || MI_INTPREANO || 
                                 ', -1,' ||  
                                 '0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, ''' ||
                                 'Se originó de la eliminacion del acuerdo de pago ' || UN_CODIGOACUERDO || ''', '''
                                 || MI_BANCOPAGO || 
                                 ''', TO_DATE(''' || MI_FECHAPAGO || ''', ''DD/MM/YYYY'')';    
              BEGIN                   
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_FACTURADOSABONOS',
                                                 UN_ACCION  => 'I',
                                                 UN_CAMPOS  => MI_CAMPOS,
                                                 UN_VALORES => MI_VALORES);  
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_FACTABONO,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOSABONOS'
             );
            END;
            BEGIN
              IF MI_INTPREANO = UN_PREANIOI
              THEN
                BEGIN
                  --//Etapa 6.2ELSE: Actualiza observaciones  
                  MI_AYUDAMSG := UN_PREANIOI || ' y ' || UN_PREANIO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = '' ''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || UN_PREANIO ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              ELSIF MI_INTPREANO > UN_PREANIOI
              AND MI_INTPREANO <= UN_PREANIO
              THEN
                BEGIN
                  --//Etapa 6.2ELSE...: Actualiza pagado 
                  MI_AYUDAMSG := UN_PREANIOI || ' y ' || UN_PREANIO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'PAGADO = -1, INDPAGO_ACPAG = -1, OBSERVACIONES = ''AP' || UN_CODIGOACUERDO || '''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || UN_PREANIO || '- 1' ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                  --//Etapa 6.3ELSE...: Actualiza observaciones
                  MI_AYUDAMSG := MI_INTPREANO || ' y ' || UN_PREANIO;
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = '' ''',
                                                   UN_CONDICION => ' COMPANIA          = ''' || UN_COMPANIA ||
                                                                  ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                  ''' AND PREANO  BETWEEN ' || MI_INTPREANO || ' AND ' || UN_PREANIO ||
                                                                    ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              END IF;-- MI_INTPREANO 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'PREDIO';
              MI_MSGERROR(1).VALOR := UN_CODPREDIO;
              MI_MSGERROR(2).CLAVE := 'ANIOSMSG';
              MI_MSGERROR(2).VALOR := MI_AYUDAMSG;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGYOBSF,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOS'
             ); 
             END;
          END IF;-- TOTALACUERDO
        ELSE --**--'Eliminación acuerdo si este se creó a partir del valor de las vigencias adeudadas. Cada cuota corresponde a un año adeudado
          MI_CONTADOR := 0;
          --//Etapa 7: Carga datos de acuerdos si se creó con vigencias adeudadas  
          <<VIG_ADEUDADAS>>
          FOR RS_FACT_ACUERDOS IN (SELECT PREDIO, 
                                          DOCNUM, 
                                          PAG_BAN,
                                          TOTAL, 
                                          FECHAPAGO, 
                                          PREANOI, 
                                          PREANO 
                                     FROM IP_FACTURADOSACUERDOS
                                    WHERE COMPANIA      = UN_COMPANIA 
                                      AND CODIGOACUERDO = UN_CODIGOACUERDO 
                                      AND PREDIO        = UN_CODPREDIO 
                                      AND PAGADO NOT IN (0)
                                      AND NUMERO_ORDEN  = UN_NUMEROORDEN
                                      AND ROWNUM = 1)
          LOOP 
            BEGIN
              BEGIN
                --//Etapa 7.1: Actualiza datos de acuerdo con vigencias adeudadas  
                MI_PREDIO    :=  RS_FACT_ACUERDOS.PREDIO;
                MI_DOCNUM    :=  RS_FACT_ACUERDOS.DOCNUM; 
                MI_PAG_BAN   :=  RS_FACT_ACUERDOS.PAG_BAN;
                MI_TOTAL     :=  RS_FACT_ACUERDOS.TOTAL;
                MI_FECHAPAGO :=  RS_FACT_ACUERDOS.FECHAPAGO; 
                MI_PREANOI   :=  RS_FACT_ACUERDOS.PREANOI; 
                MI_PREANO    :=  RS_FACT_ACUERDOS.PREANO;
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => 'PAGADO = -1, INDPAGO_ACPAG = -1, OBSERVACIONES = ''AP ' || UN_CODIGOACUERDO || ''', '
                                                                || 'DOCNUM = ''' || MI_DOCNUM || ''', PAG_BAN = ' || MI_PAG_BAN || 
                                                                 ', PREVAL = ' || MI_TOTAL || ', FECHAPAGO = TO_DATE('''|| TO_CHAR(MI_FECHAPAGO, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')',
                                                 UN_CONDICION => ' COMPANIA          = '   || UN_COMPANIA ||
                                                                  ' AND CODIGO       = ''' || MI_PREDIO || 
                                                                ''' AND PREANO  BETWEEN ' || MI_PREANOI || ' AND ' || MI_PREANO ||
                                                                  ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                MI_CONTADOR :=+1;  
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              SELECT TO_CHAR(TRUNC(TO_DATE(MI_FECHAPAGO, 'DD/MM/YYYY HH24:mi:ss')), 'DD/MM/YYYY')
                INTO MI_AYUDAMSG
                FROM DUAL;
              MI_MSGERROR(1).CLAVE := 'DOCNUM';
              MI_MSGERROR(1).VALOR := MI_DOCNUM;
              MI_MSGERROR(2).CLAVE := 'FECHA';
              MI_MSGERROR(2).VALOR := MI_AYUDAMSG;
              MI_MSGERROR(3).CLAVE := 'ACUERDO';
              MI_MSGERROR(3).VALOR := UN_CODIGOACUERDO;
              MI_MSGERROR(4).CLAVE := 'PREDIO';
              MI_MSGERROR(4).VALOR := UN_CODPREDIO;      
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_FACTVIGADEU,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOS'
             ); 
             END;
          END LOOP VIG_ADEUDADAS;

          IF MI_CONTADOR NOT IN (0)
          THEN
            BEGIN
              BEGIN
                --//Etapa 7.2: Actualiza datos de acuerdo con vigencias adeudadas  
                MI_AYUDAMSG := UN_PREANIOI || ' y ' || UN_PREANIO;
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = '' ''',
                                                 UN_CONDICION => ' COMPANIA          = '   || UN_COMPANIA ||
                                                                  ' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                                ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || UN_PREANIO ||
                                                                  ' AND PAGADO       = 0 ' ||
                                                                  ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
                PCK_PREDIAL.PR_ACT_ULTVIGCANCELADA_ANT(UN_COMPANIA => UN_COMPANIA, 
                                                       UN_CODIGO   => UN_CODPREDIO);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'PREDIO';
              MI_MSGERROR(1).VALOR := UN_CODPREDIO;
              MI_MSGERROR(2).CLAVE := 'ANIOSMSG';
              MI_MSGERROR(2).VALOR := MI_AYUDAMSG;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGYOBSF,
                 UN_REEMPLAZOS => MI_MSGERROR,
                 UN_TABLAERROR => 'IP_FACTURADOS'
             ); 
             END;    
          END IF;
        END IF; -- PARAMETRO
      END IF; -- Fin confirmación de acuerdo con cuotas.
    ELSE --' Acuerdo sin cuotas canceladas
      --//Recorre el cursor anulando las facturas pendientes del acuerdo 
      <<FACT_PENDIENTES>>
      FOR RS_FACT IN (SELECT DOCNUM 
                        FROM IP_FACTURADOSACUERDOS 
                       WHERE COMPANIA      = UN_COMPANIA 
                         AND CODIGOACUERDO = UN_CODIGOACUERDO 
                         AND DOCNUM IS NOT NULL 
                         AND PAGADO        = 0 
                         AND PREDIO        = UN_CODPREDIO
                         AND NUMERO_ORDEN  = UN_NUMEROORDEN)
      LOOP
        BEGIN
          BEGIN
          --//Etapa: 8. Anula facturas pendientes de acuerdos  
            MI_DOCNUM := RS_FACT.DOCNUM;
            MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => 'ANULADO = -1, FECHAANULACION = SYSDATE ' ,
                                             UN_CONDICION => ' COMPANIA          = '   || UN_COMPANIA ||
                                                              ' AND DOCNUM       = ''' || RS_FACT.DOCNUM ||
                                                            ''' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'DOCNUM';
            MI_MSGERROR(1).VALOR := MI_DOCNUM;
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTFACTPEND,
               UN_REEMPLAZOS => MI_MSGERROR,
               UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
           ); 
        END;
      END LOOP FACT_PENDIENTES;

      BEGIN
      --//8.1. Actualizar el indicador que permite identificar las vigencias que se estan contempladas por el acuerdo a cero
        BEGIN
          MI_AYUDAMSG := UN_PREANIOI || ' y ' || UN_PREANIO;
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => 'INDPAGO_ACPAG = 0, OBSERVACIONES = NULL',
                                           UN_CONDICION => ' COMPANIA          = ' ||  UN_COMPANIA ||
                                                            ' AND CODIGO       = ''' || UN_CODPREDIO || 
                                                          ''' AND PREANO  BETWEEN ' || UN_PREANIOI || ' AND ' || UN_PREANIO ||
                                                            ' AND PAGADO       = 0 ' ||
                                                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'ANIOSMSG';
        MI_MSGERROR(1).VALOR := MI_AYUDAMSG;
        MI_MSGERROR(2).CLAVE := 'ACUERDO';
        MI_MSGERROR(2).VALOR := UN_CODIGOACUERDO;
        MI_MSGERROR(3).CLAVE := 'PREDIO';
        MI_MSGERROR(3).VALOR := UN_CODPREDIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTINDVIGACU,
           UN_REEMPLAZOS => MI_MSGERROR,
           UN_TABLAERROR => 'IP_FACTURADOS'
       ); 
      END; 
    END IF;-- CUOTAS CANCELADAS
    --strEtapa = "9. Registrar el ultimo abono a la vigencia que corresponde"
    <<FACT_ABONOS>>
    FOR RS_FACT_ABONOS IN(SELECT PREANO, 
                                 DOCNUM, 
                                 TOTAL
                            FROM IP_FACTURADOSABONOS 
                           WHERE COMPANIA      = UN_COMPANIA
                             AND CODIGO        = UN_CODPREDIO 
                             AND NUMERO_ORDEN  = UN_NUMEROORDEN 
                             AND PAGADO NOT IN (0)
                             AND OBSERVACIONES LIKE '''%' || UN_CODIGOACUERDO || '''')
    LOOP
      BEGIN
        BEGIN
          --//9.1IF Valor abonado 2
          MI_AYUDAMSG := '2';
          MI_PREANO := RS_FACT_ABONOS.PREANO;
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => 'TOTALABONADO2 = TOTALABONADO1, VALORULTIMOABONO2 = VALORULTIMOABONO1, 
                                                            FECHAULTIMOABONO2 = FECHAULTIMOABONO1, DOCNUMABONO2 = DOCNUMABONO1',
                                           UN_CONDICION =>  '   COMPANIA = ''' ||  UN_COMPANIA ||
                                                          ''' AND CODIGO = ''' || UN_CODPREDIO || 
                                                          ''' AND PREANO = ' || RS_FACT_ABONOS.PREANO ||
                                                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
          --//9.2IF Valor abonado 1
          MI_AYUDAMSG := '1' ;
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => 'TOTALABONADO1 = TOTALABONADO, VALORULTIMOABONO1 = VALORULTIMOABONO, 
                                                            FECHAULTIMOABONO1 = FECHAULTIMOABONO, DOCNUMABONO1 = DOCNUMABONO  ',
                                           UN_CONDICION =>  '   COMPANIA = ''' ||  UN_COMPANIA ||
                                                          ''' AND CODIGO = ''' || UN_CODPREDIO || 
                                                          ''' AND PREANO = ' || RS_FACT_ABONOS.PREANO ||
                                                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
          --//9.3IF Valor abonado
          MI_AYUDAMSG := ' ' ;
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => 'TOTALABONADO = TOTALABONADO + ' || NVL(RS_FACT_ABONOS.TOTAL, 0) || 
                                                           ', VALORULTIMOABONO = ' || NVL(RS_FACT_ABONOS.TOTAL, 0) || 
                                                           ', FECHAULTIMOABONO = SYSDATE ' || 
                                                           ', DOCNUMABONO = ''' || RS_FACT_ABONOS.DOCNUM || '''',
                                           UN_CONDICION => ' COMPANIA = ' ||  UN_COMPANIA ||
                                                            ' AND CODIGO = ''' || UN_CODPREDIO || 
                                                          ''' AND PREANO = ' || RS_FACT_ABONOS.PREANO ||
                                                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'ABONADO';
        MI_MSGERROR(1).VALOR := MI_AYUDAMSG;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := MI_PREANO;
        MI_MSGERROR(3).CLAVE := 'PREDIO';
        MI_MSGERROR(3).VALOR := UN_CODPREDIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTVALABONADO,
           UN_REEMPLAZOS => MI_MSGERROR,
           UN_TABLAERROR => 'IP_FACTURADOS'
       ); 
      END; 
      BEGIN
        BEGIN
          --//9.4IF Pago año abono
          MI_PREANO := RS_FACT_ABONOS.PREANO;
          MI_DOCNUM := RS_FACT_ABONOS.DOCNUM;
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => 'PAGO_ANO_ABONO = ' || RS_FACT_ABONOS.PREANO ||
                                                           ', VALORULTIMOABONO = ' || NVL(RS_FACT_ABONOS.TOTAL, 0) || 
                                                           ', FECHAULTIMOABONO = SYSDATE ' ||
                                                           ', FACTURAULTIMOABONO = ''' || RS_FACT_ABONOS.DOCNUM || '''',
                                           UN_CONDICION => '   COMPANIA = ''' || UN_COMPANIA ||
                                                         ''' AND CODIGO = ''' || UN_CODPREDIO ||
                                                         ''' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'ANIO';
        MI_MSGERROR(1).VALOR := MI_PREANO;
        MI_MSGERROR(2).CLAVE := 'DOCNUM';
        MI_MSGERROR(2).VALOR := MI_DOCNUM;
        MI_MSGERROR(3).CLAVE := 'PREDIO';
        MI_MSGERROR(3).VALOR := UN_CODPREDIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTPAGOANIOABON,
           UN_REEMPLAZOS => MI_MSGERROR,
           UN_TABLAERROR => 'IP_USUARIOS_PREDIAL'
       ); 
      END;
    END LOOP FACT_ABONOS; 

    --10. Cambia el estado del acuerdo de activo a anulado y asigna la fecha de anulación   
    BEGIN
      MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_ACUERDOS',
                                       UN_ACCION    => 'M',
                                       UN_CAMPOS    => 'ANULADO = -1, FECHAANULADO =  SYSDATE ' ||
                                                       ', ELIMINADO_POR = ''' || UN_USUARIO || '''',
                                       UN_CONDICION => '     COMPANIA      = ''' ||  UN_COMPANIA ||
                                                     ''' AND CODIGOACUERDO = ''' || UN_CODIGOACUERDO || 
                                                     ''' AND PREDIO        = ''' || UN_CODPREDIO ||
                                                     ''' AND NUMERO_ORDEN  = ''' || UN_NUMEROORDEN || '''');
      IF MI_RTAACME > 0 
      THEN
        PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA,     
                                 UN_CODMOD      => UN_CODPREDIO,
                                 UN_OPEMOD      => UN_USUARIO,
                                 UN_CCOMOD      => '248',
                                 UN_VANMOD      => '0',
                                 UN_VNUMOD      => '-1',
                                 UN_DESCRIPCION => 'Predio ' || UN_CODPREDIO || ' Acuerdo: ' || UN_CODIGOACUERDO,
                                 UN_FECMOD      => SYSDATE,
                                 UN_HORMOD      => SYSDATE);
      END IF;

      --11. Actualizar el indicador del predio que permite activar el mensaje de si el predio está o no en acuerdo de pago  
      MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                       UN_ACCION    => 'M',
                                       UN_CAMPOS    => 'PAGO_ACUERDO = 0 ',
                                       UN_CONDICION => ' COMPANIA = ' ||  UN_COMPANIA ||
                                                       ' AND CODIGO = ''' || UN_CODPREDIO ||
                                                     ''' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''');
      MI_RESPUESTA := 'OK';
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;  

    IF UN_APLICADSCESP = 'SI'
    THEN
      BEGIN
        SELECT SUM(DESESPC2 + DESESPC4 + DESESPC13 + DESESPC14 + DESESPC15 + 
                   DESESPC16 + DESESPC17 + DESESPC18 + DESESPC19 + DESESPC20) AS DESCUENTO
          INTO MI_VLRDESCESP
          FROM IP_FACTURADOS
         WHERE COMPANIA      = UN_COMPANIA
           AND CODIGO        = UN_CODPREDIO
           AND NUMERO_ORDEN  = UN_NUMEROORDEN
           AND OBSERVACIONES = 'AP ' || UN_CODIGOACUERDO
           AND PAGADO        NOT IN(0)
           AND INDPAGO_ACPAG NOT IN (0);
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VLRDESCESP := 0;
        END;

        IF UN_VIGENCIASALDO = 0 THEN
        MI_VIGENCIASALDO := NULL;

        ELSE
        MI_VIGENCIASALDO := UN_VIGENCIASALDO;
        END IF;

        MI_OBSDESCESP := 'Excedente creado por la eliminación del acuerdo de pago ' || UN_CODIGOACUERDO ||
                         ', incumplimiento ley ' || UN_LEYDESCESP || '';
        MI_CAMPOS := 'COMPANIA, 
                      DOCNUM, 
                      PRECOD, 
                      NUMERO_ORDEN, 
                      PREANO,                    
                      ANO_EXCEDENTE, 
                      VALOR, 
                      FECHA, 
                      OBSERVACIONES, 
                      TIPO, 
                      USUARIO';
        MI_VALORES := '''' || UN_COMPANIA || ''', '''
                      || UN_CODIGOACUERDO || ''', '''                         
                      || UN_CODPREDIO || ''', '''
                      || UN_NUMEROORDEN || ''', '
                      || MI_VIGENCIAABONO || ', ' 
                      || MI_VIGENCIASALDO || ', ''' 
                      || MI_VLRDESCESP || 
                      ''', SYSDATE, '''  
                      || MI_OBSDESCESP || ''', 
                      ''D'', '''
                      || UN_USUARIO || '''';    
      BEGIN           
        BEGIN 
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_PAGOSDOBLES',
                                           UN_ACCION  => 'I',
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'VIGABONO';
        MI_MSGERROR(1).VALOR := MI_VIGENCIAABONO;
        MI_MSGERROR(2).CLAVE := 'LEY';
        MI_MSGERROR(2).VALOR := UN_LEYDESCESP;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REGPAGDOBLEAC,
           UN_REEMPLAZOS => MI_MSGERROR,
           UN_TABLAERROR => 'IP_PAGOSDOBLES'
       ); 
      END;
    END IF;

    RETURN MI_RESPUESTA;

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'ACUERDO';
        MI_MSGERROR(1).VALOR := UN_CODIGOACUERDO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_DEL_ACUERDO
         );
    RETURN '-1';
  END FC_ELIMINARACUERDO;

  PROCEDURE PR_ANULARCERT_CATASTRAL(
  /*
      NAME              : PR_ANULARCERT_CATASTRAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIAN ESTEVEN GUERRERO GUERRERO
      DATE MIGRADOR     : 22/06/2017
      TIME              : 12:01 PM
      SOURCE MODULE     : PredialP2017.01.06VB
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : PROCEDIMIENTO QUE PERMITE ANULAR LOS CERTIFICADOS CATASTRALES.
      PARAMETERS        :     
                          UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_CERTIFICADO  => CERTIFICADO QUE SE VA A ANULAR
                          UN_USUARIO			=> USUARIO QUE REALIZA EL PROCESO                              

      @NAME: anularCertificadoCatastral 
      @METHOD: PUT
    */ 

    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CERTIFICADO IN IP_CERTIFICADOCATAS.NUMCER%TYPE,        
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
    ) 
    AS    
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;  

    BEGIN  
      MI_TABLA:='IP_CERTIFICADOCATAS';

	    MI_CAMPOS:= 'ANULADO = -1,
                   VALOR = 0,
                   ANULADO_POR = ''' || UN_USUARIO || ''',
                   FECHA_ANULADO = SYSDATE,
                   DATE_MODIFIED = SYSDATE,
			             MODIFIED_BY = ''' || UN_USUARIO || '''';

	    MI_CONDICION:= 'COMPANIA = ''' || UN_COMPANIA ||
                      ''' AND NUMCER = '''|| UN_CERTIFICADO ||''' ' ;

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                     ,UN_ACCION    => 'M' 
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
              THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CERTIFICADO';
          MI_MSGERROR(1).VALOR := UN_CERTIFICADO;
          MI_MSGERROR(2).CLAVE := 'ENTE';
          MI_MSGERROR(2).VALOR := 'certificadoCatastral';      
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ANULAR_CERTCATAS
                                    ,UN_TABLAERROR => MI_TABLA
                                    ,UN_REEMPLAZOS => MI_MSGERROR
                                    );    
    END;	    
END PR_ANULARCERT_CATASTRAL;

 --8
  PROCEDURE PR_RESOLUCIONES123
      /*
        NAME              : PR_RESOLUCIONES123 --> EN ACCESS resoluciones123
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
        DATE MIGRADOR     : 22/06/2017
        TIME              : 11:00 AM
        SOURCE MODULE     : PredialP2016.05.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Realiza la inserción de la información contenida en el parámetro UN_LINEAARCHIVO dependiendo el tipo de resolución
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA               => Compañia de ingreso a la aplicación
                            UN_USUARIO   		          => Usuario que accede al sistema y ejecuta el procedimiento
                            UN_FACTORDEMULTIPLICACION => Es un indicador para realizar la lectura del parámetro UN_LINEAARCHIVO
                            UN_LINEAS 				        => Línea dentro del archivo en la que se encuentra el texto UN_LINEAARCHIVO
                            UN_LINEAARCHIVO 			    => Contiene la información que se va a registrar 
                            UN_CODIGOPAIS 			      => El código que identifica el país al que pertenece la compañia de ingreso
        @NAME  :  insertarResolucionesIgacMes
        @METHOD:  POST    
      */ 
  (
    UN_COMPANIA   		          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO   		            IN PCK_SUBTIPOS.TI_USUARIO,
    UN_FACTORDEMULTIPLICACION  	IN PCK_SUBTIPOS.TI_ENTERO,
    UN_LINEAS 					        IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_LINEAARCHIVO 			      IN PCK_SUBTIPOS.TI_STRSQL,
    UN_CODIGOPAIS 				      IN PCK_SUBTIPOS.TI_ENTERO
  )
  AS 
    MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_RTA                      PCK_SUBTIPOS.TI_RTA_ACME;

  BEGIN 

    IF SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION , 1) = '1' THEN 
      MI_TABLA   := 'IP_RESOLUCIONES_TIPO_1';
      MI_CAMPOS  := ' COMPANIA '||
                    ', PAIS '||
                    ', DEPARTAMENTO '||
                    ', MUNICIPIO '||
                    ', RESOLUCION '||
                    ', RADICACION '||
                    ', CLASE_MUTACION '||
                    ', NUMERO_DEL_PREDIO '||
                    ', CANCELA_INSCRIBE '||
                    ', TIPO_REGISTRO '||
                    ', NUMERO_ORDEN '||
                    ', TOTAL_REGISTROS '||
                    ', NOMBRE '||
                    ', ESTADO_CIVIL '||
                    ', TIPO_DOCUMENTO '||
                    ', NUMERO_DOCUMENTO '||
                    ', DIRECCION '||
                    ', COMUNA '||
                    ', DESTINO_ECONOMICO '||
                    ', AREA_TERRENO '||
                    ', AREA_CONSTRUIDA '||
                    ', AVALUO '||
                    ', VIGENCIA '||
                    ', NUMERO_RES '||
                    ', DATE_CREATED '||
                    ', CREATED_BY ';

      MI_VALORES := '  '''|| UN_COMPANIA ||''' '||
                    ', '''|| UN_CODIGOPAIS ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 1 + UN_FACTORDEMULTIPLICACION, 2) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 3 + UN_FACTORDEMULTIPLICACION, 3) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 6 + UN_FACTORDEMULTIPLICACION, 4) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 10 + UN_FACTORDEMULTIPLICACION, 5) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 15 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 16 + UN_FACTORDEMULTIPLICACION, 15) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 31 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 33 + UN_FACTORDEMULTIPLICACION, 3) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 36 + UN_FACTORDEMULTIPLICACION, 3) ||''' '||
                    ', '''|| REPLACE(SUBSTR(UN_LINEAARCHIVO, 39 + UN_FACTORDEMULTIPLICACION, 33), CHR(39), '`') ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 72 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 73 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 74 + UN_FACTORDEMULTIPLICACION, 12) ||''' '||
                    ', '''|| REPLACE(SUBSTR(UN_LINEAARCHIVO, 86 + UN_FACTORDEMULTIPLICACION, 34), CHR(39), '`') ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 120 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 121 + UN_FACTORDEMULTIPLICACION, 1) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 122 + UN_FACTORDEMULTIPLICACION, 12) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 134 + UN_FACTORDEMULTIPLICACION, 6) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 140 + UN_FACTORDEMULTIPLICACION, 12) ||''' '||
                    ', '''|| SUBSTR(UN_LINEAARCHIVO, 152 + UN_FACTORDEMULTIPLICACION, 8) ||''' '||
                    ', ' || UN_LINEAS || ' '||
                    ', SYSDATE '||
                    ', '''||UN_USUARIO||''' ';

        BEGIN
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                        ,UN_ACCION  => 'I' 
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_INS_RES1
                     ,UN_TABLAERROR => MI_TABLA
                    );  
        END;  		               

    ELSIF SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION , 1) = '2' THEN
      MI_TABLA   := 'IP_RESOLUCIONES_TIPO_2';
      MI_CAMPOS  := '  COMPANIA '||
                    ', PAIS '||
                    ', DEPARTAMENTO ' ||
                    ', MUNICIPIO ' ||
                    ', RESOLUCION ' ||
                    ', RADICACION ' ||
                    ', CLASE_MUTACION ' ||
                    ', NUMERO_DEL_PREDIO ' ||
                    ', CANCELA_INSCRIBE ' ||
                    ', TIPO_REGISTRO ' ||
                    ', NUMERO_ORDEN ' ||
                    ', TOTAL_REGISTROS ' ||
                    ', MATRICULA_INMOBILIARIA ' ||
                    ', NUMERO_RES '||
                    ', DATE_CREATED '||
                    ', CREATED_BY ';

      MI_VALORES :=   '  ''' || UN_COMPANIA ||''' '||
                      ', ''' || UN_CODIGOPAIS ||''' '||	
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 1 + UN_FACTORDEMULTIPLICACION, 2) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 3 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 6 + UN_FACTORDEMULTIPLICACION, 4) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 10 + UN_FACTORDEMULTIPLICACION, 5) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 15 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 16 + UN_FACTORDEMULTIPLICACION, 15) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 31 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 33 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 36 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 43 + UN_FACTORDEMULTIPLICACION, 18) || ''' '||
                      ', ' || UN_LINEAS || ' '||
                      ', SYSDATE '||
                      ', '''||UN_USUARIO||''' ';

        BEGIN
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                        ,UN_ACCION  => 'I' 
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                             ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_INS_RES2
                             ,UN_TABLAERROR => MI_TABLA);  
        END; 		                

    ELSIF SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION , 1) = '3' THEN
      MI_TABLA   :=   'IP_RESOLUCIONES_TIPO_3';
      MI_CAMPOS  :=   ' COMPANIA '||
                      ', PAIS '||
                      ', DEPARTAMENTO ' ||
                      ', MUNICIPIO ' ||
                      ', RESOLUCION ' ||
                      ', RADICACION ' ||
                      ', CLASE_MUTACION ' ||
                      ', NUMERO_DEL_PREDIO ' ||
                      ', CANCELA_INSCRIBE ' ||
                      ', TIPO_REGISTRO ' ||
                      ', NUMERO_ORDEN ' ||
                      ', TOTAL_REGISTROS ' ||
                      ', DECRETOS ' ||
                      ', NUMERO_RES ' ||
                      ', DATE_CREATED '||
                      ', CREATED_BY ';

      MI_VALORES :=   '  ''' || UN_COMPANIA ||''' '||
                      ', ''' || UN_CODIGOPAIS ||''' '||	
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 1 + UN_FACTORDEMULTIPLICACION, 2) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 3 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 6 + UN_FACTORDEMULTIPLICACION, 4) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 10 + UN_FACTORDEMULTIPLICACION, 5) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 15 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 16 + UN_FACTORDEMULTIPLICACION, 15) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 31 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 32 + UN_FACTORDEMULTIPLICACION, 1) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 33 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 36 + UN_FACTORDEMULTIPLICACION, 3) || ''' '||
                      ', ''' || SUBSTR(UN_LINEAARCHIVO, 39 + UN_FACTORDEMULTIPLICACION, 70) || ''' '||
                      ', ' || UN_LINEAS || ' '||
                      ', SYSDATE '||
                      ', '''||UN_USUARIO||''' ';

        BEGIN
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                        ,UN_ACCION  => 'I' 
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                             ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_INS_RES3
                             ,UN_TABLAERROR => MI_TABLA);  
        END; 	
    END IF;

  END PR_RESOLUCIONES123; 

 --9
  FUNCTION FC_ACTUALIZARIGACMES
      /*
        NAME              : FC_ACTUALIZARIGACMES --> EN ACCESS Importar_Igac_Meses
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
        DATE MIGRADOR     : 22/06/2017
        TIME              : 03:36 PM
        SOURCE MODULE     : PredialP2016.05.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Realiza el proceso de actualización de la información IGAC por mes 
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_PLANO            => Concatenación del archivo plano que fue cargado, se utiliza el simbolo @ como separador de línea
                            UN_LONGITUDARCHIVO  => Total de caracteres en el archivo plano
                            UN_ANIO             => Año de la resolución
                            UN_USUARIO          => Usuario que accede al sistema y ejecuta el procedimiento
                            UN_CODIGOPAIS       => El código que identifica el país al que pertenece la compañia de ingreso

        @NAME  :  actualizarIgacMes
        @METHOD:  POST    
      */ 
  (
       UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
       UN_PLANO             IN CLOB, 
       UN_LONGITUDARCHIVO   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,  
       UN_ANIO              IN PCK_SUBTIPOS.TI_ANIO, 
       UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO,
       UN_CODIGOPAIS        IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS 
     MI_CADENA                    CLOB;
     MI_LINEAARCHIVO              PCK_SUBTIPOS.TI_STRSQL;
     MI_TAMANIO                   PCK_SUBTIPOS.TI_LONG;
     MI_LINEAS                    PCK_SUBTIPOS.TI_ENTERO := 0;
     MI_NUMEROREG                 PCK_SUBTIPOS.TI_ENTERO;
     MI_FACTORDEMULTIPLICACION    PCK_SUBTIPOS.TI_ENTERO := 0;
     MI_RESULTADO                 PCK_SUBTIPOS.TI_LOGICO;

  BEGIN
      PCK_PREDIAL_COM7.PR_PREPARARTIPOMES (UN_COMPANIA => UN_COMPANIA);
      -- 159 es el tamanio definido para la linea del archivo
      MI_TAMANIO   := UN_LONGITUDARCHIVO / 159;
      MI_CADENA    := UN_PLANO;
      MI_NUMEROREG := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);--HASTA LA PRIMERA @

     WHILE MI_LINEAS <> MI_TAMANIO
      LOOP 
          MI_LINEAARCHIVO := REGEXP_SUBSTR(MI_CADENA,'[^@]+', 1);
          MI_CADENA       := REPLACE(MI_CADENA,MI_LINEAARCHIVO||'@',''); 
          MI_NUMEROREG    := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);

          IF MI_TAMANIO * 159 = LENGTH(MI_LINEAARCHIVO) THEN 
            MI_FACTORDEMULTIPLICACION := CASE WHEN MI_LINEAS <> 0 THEN 159 * MI_LINEAS ELSE MI_LINEAS END;    
            MI_LINEAS := MI_LINEAS + 1;        
            PCK_PREDIAL_COM7.PR_RESOLUCIONES123( UN_COMPANIA                => UN_COMPANIA
                                                ,UN_USUARIO                 => UN_USUARIO
                                                ,UN_FACTORDEMULTIPLICACION  => MI_FACTORDEMULTIPLICACION
                                                ,UN_LINEAS                  => MI_LINEAS
                                                ,UN_LINEAARCHIVO            => MI_LINEAARCHIVO
                                                ,UN_CODIGOPAIS              => UN_CODIGOPAIS);
          ELSE 
            MI_FACTORDEMULTIPLICACION := 0;
            MI_LINEAS := MI_LINEAS + 1;
            PCK_PREDIAL_COM7.PR_RESOLUCIONES123( UN_COMPANIA                => UN_COMPANIA
                                                ,UN_USUARIO                 => UN_USUARIO
                                                ,UN_FACTORDEMULTIPLICACION  => MI_FACTORDEMULTIPLICACION
                                                ,UN_LINEAS                  => MI_LINEAS
                                                ,UN_LINEAARCHIVO            => MI_LINEAARCHIVO
                                                ,UN_CODIGOPAIS              => UN_CODIGOPAIS);
          END IF;

      END LOOP; 

      MI_RESULTADO := PCK_PREDIAL_COM7.FC_IMPORTAR_IGAC_MESES( UN_COMPANIA    => UN_COMPANIA
                                                              ,UN_ANIO        => UN_ANIO
                                                              ,UN_USUARIO     => UN_USUARIO
                                                              ,UN_NUMEROORDEN => PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL);

    RETURN MI_RESULTADO;
  END FC_ACTUALIZARIGACMES; 

--10 
FUNCTION FC_PREPARAR_CONCEPTO_ANIO
      /*
        NAME              : FC_PREPARAR_CONCEPTO_ANIO
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
        DATE MIGRADOR     : 02/08/2017
        TIME              : 03:00 PM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : REALIZA EL PROCESO DE PREPARAR LOS CONCEPTOS PÀRA UN AÑO DETERMINADO TENIENDO EN CUENTA UN AÑO BASE 
        MODIFICATIONS     : 
        PARAMETERS        : 
                             UN_COMPANIA        => COMPANIA DE INGRESO A LA APLICACION
                             UN_ANIOFINAL       => AÑO PARA EL CUAL SE VAN A PREPARAR LOS CONCEPTOS
                             UN_ANIOINICIAL     => AÑO BASE PARA PREPARA LOS CONCEPTOS
                             UN_USUARIO         => USUARIO QUE ESTA REALIZANDO EL PROCESO 

        @NAME  :  prepararConceptoAnio
        @METHOD:  GET    
      */ 
(
  UN_COMPANIA                  IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIOFINAL                 IN   IP_CONCEPTOS.ANO%TYPE,
  UN_ANIOINICIAL               IN   IP_CONCEPTOS.ANO%TYPE,
  UN_USUARIO                   IN  PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_ANIO                 IP_CONCEPTOS.ANO%TYPE;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES; 
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RETORNO              PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  MI_MSGERROR(1).CLAVE := 'ANIO';
  MI_MSGERROR(1).VALOR := UN_ANIOFINAL ;
  BEGIN
    BEGIN
      SELECT NUMERO 
      INTO MI_ANIO
      FROM ANO 
      WHERE ANO.COMPANIA = UN_COMPANIA
      AND NUMERO = UN_ANIOFINAL;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD   => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ANIONOCREADO, /*El año #anoFinal# no ha sido creado. No se puede ejecutar el proceso*/
      UN_REEMPLAZOS   =>  MI_MSGERROR
    );                      
  END;

  BEGIN
    SELECT DISTINCT ANO 
    INTO MI_ANIO
    FROM IP_CONCEPTOS  
    WHERE IP_CONCEPTOS.COMPANIA = UN_COMPANIA
    AND IP_CONCEPTOS.ANO = UN_ANIOFINAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_ANIO:=0;
  END;

  IF MI_ANIO > 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_CONCEPTOANIO,/*Los conceptos para el año #anoFinal# ya fueron creados.*/
                              UN_REEMPLAZOS   =>  MI_MSGERROR
                            );
    END;
  ELSE
      MI_CAMPOS:= ' COMPANIA,
                    ANO,
                    CODIGO,
                    NOMBRE,
                    VALOR,
                    SUMAORESTA,
                    PORCENTAJE,
                    CAUSACIONDB,
                    CAUSACIONCR,
                    RECAUDODB,
                    TABLAAMODIFICAR,
                    CAMPOAMODIFICAR,
                    RECAUDOCR,
                    FORMULA_COPIA,
                    MINIMO,
                    MINIMO1,
                    APLICAINTERES,
                    PRIORIDAD,
                    INFORMATIVO,
                    ESCAPITALVIGENCIA,
                    ESINTERESVIGENCIA,
                    PRORATIAR,
                    ENCABEZADO,
                    FORMULA,
                    VIGANT,
                    VIGDFR,
                    VERINF_TOTALCERO,
                    CAUSACIONDBNULA,
                    CAUSACIONCRNULA,
                    VER_RESOL,
                    FUENTE,
                    CTA_CRD_SUPERNUM,
                    CTA_DBT_SUPERNUM,
                    PRIORIDAD_ABON_EN_ACU,
                    COD_CENTRO_COSTO,
                    ESCAPITALABACU,
                    ESINTERESABACU,
                    NIVEL,
                    FORMULA1,
                    DATE_CREATED,
                    CREATED_BY';

      MI_VALORES:= 'SELECT IP_CONCEPTOS.COMPANIA,
                    '||UN_ANIOFINAL||',
                    IP_CONCEPTOS.CODIGO,
                    IP_CONCEPTOS.NOMBRE,
                    VALOR,
                    SUMAORESTA,
                    PORCENTAJE,
                    CAUSACIONDB,
                    CAUSACIONCR,
                    PLAN_CONTABLE.CODIGO,
                    TABLAAMODIFICAR,
                    CAMPOAMODIFICAR,
                    P1.CODIGO,
                    FORMULA_COPIA,
                    MINIMO,
                    MINIMO1,
                    APLICAINTERES,
                    PRIORIDAD,
                    INFORMATIVO,
                    ESCAPITALVIGENCIA,
                    ESINTERESVIGENCIA,
                    PRORATIAR,
                    ENCABEZADO,
                    FORMULA,
                    VIGANT,
                    VIGDFR,
                    VERINF_TOTALCERO,
                    CAUSACIONDBNULA,
                    CAUSACIONCRNULA,
                    VER_RESOL,
                    IP_CONCEPTOS.FUENTE,
                    CTA_CRD_SUPERNUM,
                    CTA_DBT_SUPERNUM,
                    PRIORIDAD_ABON_EN_ACU,
                    COD_CENTRO_COSTO,
                    ESCAPITALABACU,
                    ESINTERESABACU,
                    NIVEL,
                    FORMULA1,
                    SYSDATE,
                    '''||UN_USUARIO||''' 
                  FROM IP_CONCEPTOS
                  LEFT JOIN PLAN_CONTABLE
                    ON IP_CONCEPTOS.COMPANIA   = PLAN_CONTABLE.COMPANIA
                    AND IP_CONCEPTOS.ANO       = '||UN_ANIOFINAL||'
                    AND IP_CONCEPTOS.RECAUDOCR = PLAN_CONTABLE.CODIGO
                  LEFT JOIN PLAN_CONTABLE P1
                    ON IP_CONCEPTOS.COMPANIA   = P1.COMPANIA
                    AND IP_CONCEPTOS.ANO       = '||UN_ANIOFINAL||'
                    AND IP_CONCEPTOS.RECAUDODB = P1.CODIGO
                  WHERE IP_CONCEPTOS.ANO       = '||UN_ANIOINICIAL||'
                    AND IP_CONCEPTOS.COMPANIA  = '''||UN_COMPANIA||'''';
   BEGIN
     BEGIN
          MI_RETORNO := PCK_DATOS.FC_ACME (UN_TABLA   =>  'IP_CONCEPTOS', 
                                                 UN_ACCION  =>  'IS',
                                                 UN_CAMPOS  =>  MI_CAMPOS, 
                                                 UN_VALORES =>  MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
     END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_INSCONCEPTOANIO,
                            UN_REEMPLAZOS   =>  MI_MSGERROR
                          );
      END;
    END IF;
    RETURN MI_RETORNO;
END FC_PREPARAR_CONCEPTO_ANIO;

--11
FUNCTION FC_ACTUALIZARIGACDOS
/*
        NAME              : FC_ACTUALIZARIGACDOS 
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
        DATE MIGRADOR     : 18/08/2017
        TIME              : 12:36 PM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Realiza el proceso de recorrido e insercion de datos en la tabla IP_IGAC_TIPO_DOS
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_PLANO            => Concatenación del archivo plano que fue cargado, se utiliza el simbolo @ como separador de línea
                            UN_LONGITUDARCHIVO  => Total de caracteres en el archivo plano.
                            UN_DEPARTAMENTO     => El código que identifica el departamento al que pertenece la compañia de ingreso
                            UN_PAIS             => El código que identifica el país al que pertenece la compañia de ingreso
                            UN_MUNICIPIO        => El código que identifica el municipio al que pertenece la compañia de ingreso
                            UN_USUARIO          => Usuario que accede al sistema y ejecuta el procedimiento
        @NAME  :  actualizarIgacDos
        @METHOD:  GET    
*/ 
(
 UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_PLANO             IN CLOB, 
 UN_LONGITUDARCHIVO   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
 UN_DEPARTAMENTO      IN IP_IGAC_TIPO_DOS.DEPARTAMENTO%TYPE,
 UN_PAIS              IN IP_IGAC_TIPO_DOS.PAIS%TYPE,
 UN_MUNICIPIO         IN IP_IGAC_TIPO_DOS.MUNICIPIO%TYPE,
 UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
  MI_LINEAS                    PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_TAMANIO                   PCK_SUBTIPOS.TI_LONG;
  MI_LINEAARCHIVO              PCK_SUBTIPOS.TI_STRSQL;
  MI_CADENA                    CLOB;
  MI_NUMEROREG                 PCK_SUBTIPOS.TI_ENTERO;
  MI_PREDIO                    PCK_SUBTIPOS.TI_ENTERO;
  MI_TABLA                     PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                    PCK_SUBTIPOS.TI_CAMPOS;  
  MI_VALORES                   PCK_SUBTIPOS.TI_VALORES;
  MI_RTA                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_INCONSISTENCIAS           CLOB;
  MI_TOTAL                     PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  PCK_PREDIAL_COM7.PR_PREPARARTIPODOS(UN_COMPANIA => UN_COMPANIA);
  MI_TAMANIO   := UN_LONGITUDARCHIVO / 151;
  MI_CADENA    := UN_PLANO;
  MI_NUMEROREG := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);
  MI_INCONSISTENCIAS := '';

  WHILE MI_LINEAS <> MI_TAMANIO
  LOOP 
    MI_LINEAARCHIVO := REGEXP_SUBSTR(MI_CADENA,'[^@]+', 1);
    MI_CADENA       := REPLACE(MI_CADENA,MI_LINEAARCHIVO||'@',''); 
    MI_NUMEROREG    := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);

    MI_MSGERROR (1).CLAVE := 'LUGAR';
    IF UN_DEPARTAMENTO <> SUBSTR(MI_LINEAARCHIVO,1,2) THEN
      MI_MSGERROR (1).VALOR := 'departamento';
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DEPARTMUNICIPIO,
                          UN_REEMPLAZOS => MI_MSGERROR
                        );
      END;
    ELSIF UN_MUNICIPIO <> SUBSTR(MI_LINEAARCHIVO,3,3) THEN
      MI_MSGERROR (1).VALOR := 'municipio';
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 

        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD =>SQLCODE,
                          UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_DEPARTMUNICIPIO,
                          UN_REEMPLAZOS => MI_MSGERROR
                        );
      END;
    END IF;

    IF SUBSTR(MI_LINEAARCHIVO,21,1) = '2' THEN
      SELECT  COUNT(1) 
      INTO    MI_PREDIO
      FROM IP_USUARIOS_PREDIAL  
      WHERE COMPANIA      = UN_COMPANIA 
        AND NUMERO_ORDEN  = SUBSTR(MI_LINEAARCHIVO,22,3)
        AND CODIGO        = SUBSTR(MI_LINEAARCHIVO,6,15);

      IF MI_PREDIO <> 0 THEN 
        MI_TABLA := 'IP_IGAC_TIPO_DOS';
        MI_CAMPOS := '  DEPARTAMENTO,
                        MUNICIPIO,
                        CODIGO,
                        TIPO,
                        NUMERO_ORDEN,
                        NUMERO_REGISTROS,
                        MATRICULA_INMOBILIARIA,
                        ZONA_HOM_FISICA_UNO,
                        ZONA_HOM_ECON_UNO,
                        ZONA_HOM_AREA1,
                        ZONA_HOM_FISICA_DOS,
                        ZONA_HOM_ECON_DOS,
                        ZONA_HOM_AREA2,
                        CON_UND_HAB_UNO,
                        CON_UND_WC_UNO,
                        CON_UND_LOCAL_UNO,
                        CON_UND_PISOS_UNO,
                        CON_UND_ESTRATO_UNO,
                        CON_UND_DESTINO_UNO,
                        CON_UND_PUNTOS_UNO,
                        CON_UND_M2_UNO,
                        CON_UND_HAB_DOS,
                        CON_UND_WC_DOS,
                        CON_UND_LOCAL_DOS,
                        CON_UND_PISOS_DOS,
                        CON_UND_ESTRATO_DOS,
                        CON_UND_DESTINO_DOS,
                        CON_UND_PUNTOS_DOS,
                        CON_UND_M2_DOS,
                        CON_UND_HAB_TRES,
                        CON_UND_WC_TRES,
                        CON_UND_LOCAL_TRES,
                        CON_UND_PISOS_TRES,
                        CON_UND_ESTRATO_TRES,
                        CON_UND_DESTINO_TRES,
                        CON_UND_PUNTOS_TRES,
                        CON_UND_M2_TRES,
                        VIGENCIA,
                        COMPANIA,
                        CREATED_BY,
                        DATE_CREATED,
                        PAIS';
        MI_VALORES :=  ''''||SUBSTR(MI_LINEAARCHIVO,1,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,3,3)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,6,15)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,21,1)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,22,3)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,25,3)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,30,18)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,48,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,50,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,52,10)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,62,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,64,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,66,10)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,76,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,78,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,80,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,82,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,84,1)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,85,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,87,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,89,6)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,104,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,106,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,108,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,110,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,112,1)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,113,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,115,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,117,6)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,123,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,125,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,127,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,129,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,131,1)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,132,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,134,2)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,136,6)||''',
                        '''||SUBSTR(MI_LINEAARCHIVO,142,8)||''',
                        '''|| UN_COMPANIA ||''',
                        '''|| UN_USUARIO  ||''',
                        SYSDATE,
                        '''|| UN_PAIS  ||'''';
        BEGIN
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                        ,UN_ACCION  => 'I' 
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         MI_MSGERROR(1).CLAVE := 'PREDIO';
         MI_MSGERROR(1).VALOR := SUBSTR(MI_LINEAARCHIVO,6,15);
         MI_MSGERROR(2).CLAVE := 'ORDEN';
         MI_MSGERROR(2).VALOR := SUBSTR(MI_LINEAARCHIVO,22,3);
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                             ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSIGACTIPODOS
                             ,UN_TABLAERROR => MI_TABLA); 
        END;
      ELSE
       MI_INCONSISTENCIAS:= MI_INCONSISTENCIAS|| CHR(10);
       MI_INCONSISTENCIAS:= MI_INCONSISTENCIAS || 'El predio ' ||
                            SUBSTR(MI_LINEAARCHIVO,6,15)|| ' con número de orden: ' ||
                            SUBSTR(MI_LINEAARCHIVO,22,3)|| ' no existe.';
      END IF;   
    END IF;
    MI_LINEAS := MI_LINEAS + 1; 
  END LOOP;
  MI_TOTAL:=PCK_PREDIAL_COM7.FC_IMPORTAR_IGAC_TIPO_DOS( UN_COMPANIA => UN_COMPANIA, 
                                                        UN_USUARIO  => UN_USUARIO );

  RETURN MI_INCONSISTENCIAS; 
END FC_ACTUALIZARIGACDOS;


END PCK_PREDIAL_COM7;