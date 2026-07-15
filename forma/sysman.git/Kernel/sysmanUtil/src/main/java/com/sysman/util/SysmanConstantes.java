/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.util;

/**
 *
 * @author cmanrique
 */
public class SysmanConstantes {

    public static final String CONS_DEPENDENCIA = "999999999999";
    public static final String CONS_MAX_ID = "99999999999999999999999999999999";
    public static final String CONS_CENTRO = "99999999999999999999";
    public static final String CONS_TERCERO = "999999999999999999";
    public static final String CONS_SUCURSAL = "999";
    public static final String CONS_AUXILIAR = "99999999999999999999";
    public static final String CONS_REFERENCIA = "99999999999999999999";
    public static final String CONS_FUENTE = "99999999999999999999";
    public static final String CONS_CLASE_BODEGA_ALM = "20";
    public static final String RETORNO_MENU = "../sysmanWeb/menu.sysman";
    public static final String RETORNO_IDENTIFICACION = "../sysmanWeb/";

    public static final int CODIGO_APLICACION_GENERAL = -1;
    public static final int MODULO_NOMINA = 6;
    public static final int MODULO_HOJAS_DE_VIDA = 21;
    public static final int MODULO_WORKFLOW = 35;
    public static final int MODULO_BANCOPROY = 52;
    public static final int MODULO_FACTURACION_GENERAL = 69;
    public static final int MODULO_CONTRATOS = 9;
    public static final int MODULO_ALMACEN = 10;
    public static final int MODULO_PRECONTRACTUAL = 19;
    public static final int MODULO_CONTABILIDAD = 1;
    public static final int MODULO_PREDIAL = 60;
    public static final int MODULO_PLAN_DE_DESARROLLO = 67;
    public static final int MODULO_PRESUPUESTO = 3;
    public static final int MODULO_ENTES_DE_CONTROL = 99;
    public static final int MODULO_GENERAL = 999;
    public static final int MODULO_PLUSVALIA = 61;
    public static final int MODULO_CONTROL_Y_REGISTRO = 500;

    public static final int MODULO_FACTURACION_SERVICIOS_PUBLICOS = 74;

    public static final String[] NOMBRE_MESES_CONTABILIDAD = { "Inicial",
                                                               "Enero",
                                                               "Febrero",
                                                               "Marzo", "Abril",
                                                               "Mayo",
                                                               "Junio", "Julio",
                                                               "Agosto",
                                                               "Septiembre",
                                                               "Octubre",
                                                               "Noviembre",
                                                               "Diciembre",
                                                               "FINAL" };
    public static final String NUMERO_ORDEN_PREDIAL = "001";

    public static final String RUTA_IDIOMA = "com/sysman/properties/esp";
    public static final String RUTA_PARAMETROS = "com/sysman/properties/parametros";

    public static final String LLAVE_MENSAJE_ERROR = "mensajeError";
    public static final String LLAVE_FORMULARIO_ABRIR = "formularioAbrirMenu";
    public static final String LLAVE_FORMULARIO_MODAL = "formularioAbrirModal";
    public static final String LLAVE_POLITICA_CONTRASENA = "llavePoliticaContrasena";
    public static final String LLAVE_DIAS_NOTIFICACION = "llaveDiasNotificacion";
    public static final String LLAVE_VIGENCIA_CONTRASENA = "formularioCambiarContrasena";
    public static final String MICROSOFT = "MICROSOFT";
    public static final String ORACLE = "ORACLE";

    /**
     * Constante que contiene la clave utilizada para identificar el
     * formulario al cual se accede (formulario destino).
     */
    public static final String LLAVE_FORMULARIO_RETORNO_IN = "formularioRetornoIn";

    /**
     * Constante que contiene la clave utilizada para identificar el
     * formulario (formulario origen) desde el cual se accede al
     * formulario destino.
     */
    public static final String LLAVE_FORMULARIO_RETORNO_OUT = "formularioRetornoOut";

    public static final String LLAVE_MENSAJE_ABRIR = "mensajeAbrir";
    public static final String MSJ_ERROR = "mensajeError";
    public static final String MSJ_ALERTA = "mensajeAlerta";
    public static final String MSJ_INFORMATIVO = "mensajeInformativo";
    public static final String MSJ_FATAL = "mensajeFatal";
    /**
     * Se utiliza para separar registros en CLOB enviados a los Bean
     * por efectos de utilizar tablas temporales
     */
    public static final String SEPARADOR_REG = ",.REG.,";
    /**
     * Se utiliza para separar columnas en CLOB enviados a los Bean
     * por efectos de utilizar tablas temporales
     */
    public static final String SEPARADOR_COL = ",.COL.,";
    /**
     * Se utiliza para separar hojas en CLOB enviados a los Bean por
     * efectos de utilizar tablas temporales
     */
    public static final String SEPARADOR_HOJ = ",.HOJ.,";

    /**
     * Constantes para calcular los valores por defecto para los
     * combos de informes al cargar los mismos
     */
    public static final String DEFECTOINICIAL_STRING = String
                    .valueOf((char) 32);
    public static final String DEFECTOFINAL_STRING = String.valueOf((char) 255);
    public static final String DEFECTOINICIAL_DATE = "01/01/1900";
    public static final String DEFECTOFINAL_DATE = "31/12/2100";
    public static final String DEFECTOINICIAL_NUMBER = "-9";
    public static final String DEFECTOFINAL_NUMBER = "9";
    /**
     * Clase CSS para los eventos que programa el solicitante
     * (Solicitud de Préstamo).
     */
    public static final String CLASE_EVENTO_PROGRAMADO = "evento-progamado";
    /**
     * Clase CSS para los eventos que se cargan para representar los
     * horarios excluidos (Solicitud de Préstamo).
     */
    public static final String CLASE_EVENTO_EXCLUSION = "evento-exclusion";
    /**
     * Tipo de transacción (devolutivos) para dar inicio a la
     * solicitud de préstamo.
     */
    public static final String TIPO_TRANS_SOLICITUD = "SOL";
    /**
     * Tipo de transacción (devolutivos) para realizar la
     * pre-aprobación del préstamo.
     */
    public static final String TIPO_TRANS_PREAPROBACION = "PRE";
    /**
     * Tipo de transacción (devolutivos) para cambiar de estado a En
     * Espera de Requisitos.
     */
    public static final String TIPO_TRANS_REQUISITOS = "REQ";
    /**
     * Tipo de transacción (devolutivos) para indicar que el préstamo
     * está en trámite.
     */
    public static final String TIPO_TRANS_ENTRAMITE = "ENT";
    /**
     * Tipo de transacción (devolutivos) para aprobar el préstamo.
     */
    public static final String TIPO_TRANS_APROBACION = "APR";
    /**
     * Tipo de transacción (devolutivos) para rechazar el préstamo.
     */
    public static final String TIPO_TRANS_RECHAZO = "REC";
    /**
     * Tipo de transacción (devolutivos) para subsanar.
     */
    public static final String TIPO_TRANS_SUBSANAR = "SUB";
    /**
     * Usuario en la Web para inserción en el campo CREATED_BY.
     */
    public static final String USUARIO_WEB = "WEB";
    /**
     * Usuario para consumo de servicos de INDRA
     */
    public static final String USUARIO_INDRA = "tunja";
    /**
     * Contraseña para consumo de servicios de INDRA
     */
    public static final String PASSWORD_INDRA = "tunja";
    /**
     * Código de procedimiento en INDRA para Prestamos de bienes.
     */
    public static final String COD_PROC_PRESTAMOS = "PRBI";
    /**
     * Código de procedimiento en INDRA para Carpeta Ciudadana.
     */
    public static final String COD_PROC_LEY232 = "PLEY232";
    /**
     * Código de procedimiento en INDRA para Programación de Visitas.
     */
    public static final String COD_PROC_VISITAS = "PCONDISAN";
    /**
     * Código de procedimiento en INDRA para Certificación de
     * Contratos.
     */
    public static final String COD_PROC_CERT_CONTRATOS = "CERCONT";
    /**
     * Código de procedimiento en INDRA para Estado de Proyectos.
     */
    public static final String COD_PROC_EST_PROYECTOS = "ESTPROY";
    /**
     * Código de procedimiento en INDRA para Radicación de Proyectos.
     */
    public static final String COD_PROC_RAD_PROYECTOS = "RADPROY";
    /**
     * Código de procedimiento en INDRA para Radicación de Contratos.
     */
    public static final String COD_PROC_RAD_CONTRATOS = "RADCONT";
    /**
     * Código para documento de requerimientos para INDRA
     */
    public static final String DOC_REQ_INDRA = "DOC_REQ";
    /**
     * Código para documento de rechazo para INDRA
     */
    public static final String DOC_REC_INDRA = "DOC_RECHAZO";
    /**
     * Código para documento de aprobación para INDRA
     */
    public static final String DOC_APR_INDRA = "DOC_APROBADO";
    /**
     * Código para documento de subsanar para INDRA
     */
    public static final String DOC_SUB_INDRA = "DOC_SUBSANAR";

    public static final String CONS_COMPANIA_DEFAULT = "001";
    public static final String CONS_SUCURSAL_DEFAULT = "001";

    /**
     * Código del acta de Programación de Visitas en INDRA.
     */
    public static final String DOC_PROG_VISITAS_INDRA = "DOC_PROG_VISITA";
    /**
     * Código de la tarea que se maneja para Programación de Visitas
     * (Condiciones Sanitarias).
     */
    public static final String COD_TAREA_PROG_VISITAS = "CS03";
    /**
     * Código de la compañía para generar los certificados del lado de
     * INDRA.
     */
    public static final String COD_COMPANIA_INDRA = "001";
    /**
     * Procedimiento de Anuncio y Apertura de Establecimientos.
     */
    public static final String COD_PROC_PAA = "PAA";

    /**
     * RUTAS BASE DE DOCUMENTOS
     */
    public static final String RUTA_DOCUMENTOS_PERSONALES = "/HV/DatosPersonales/";
    public static final String RUTA_DOCUMENTOS_FAMILIARES = "/HV/DatosFamiliares/";
    public static final String RUTA_DOCUMENTOS_TERCERO = "/HV/DatosTercero/";

    public static final String RUTA_SOLICITUDES_PERSONALES = "/Solicitudes/DatosPersonales/";
    public static final String RUTA_SOLICITUDES_FAMILIARES = "/Solicitudes/DatosFamiliares/";
    public static final String RUTA_SOLICITUDES_TERCERO = "/Solicitudes/DatosTercero/";

    public static final String RUTA_DOCUMENTOS_TRANSACCIONES = "/Transacciones/";
    public static final String RUTA_DOCUMENTOS_PLANEACION = "/Planeacion/";
    public static final String RUTA_DOCUMENTOS_INSCRITOS = "/Inscritos/";
    public static final String RUTA_DOCUMENTOS_BIENESTARYCAPACITACION = "/BienestaryCapacitacion/";

    public static final String RUTA_DOCUMENTOS_LISTADEELEGIBLES = "/ListaElegibles/";

    // <TIPOS_ARCHIVO>
    public static final String TIPO_PDF = "PDF";
    public static final String TIPO_IMAGEN = "IMAGEN";
    // </TIPOS_ARCHIVO>

    /**
     * Formato fecha/hora ISO 8601 definido en el RFC-3339.
     */
    public static final String ISO_8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * C&oacute;digo del servicio que consulta las url desde la tabla
     * de URL, para consumir servicios
     */
    public static final String SERVICIO_API = "1710001";

}
