/*-
 * EalmacencontabilidadtControlador.java
 *
 * 1.0
 * 
 * 21/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
import com.sysman.contabilizar.enums.EalmacencontabilidadTControladorEnum;
import com.sysman.contabilizar.enums.EalmacencontabilidadTControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Esta clase permite gestionar la interfaz de Almacen a
 * Contabilidad.
 *
 * @version 1.0, 21/06/2018
 * @author dnino
 * 
 * * @version 1.1, 21/06/2018
 * @author jcaceres creacion del metodo selccionDeArchivo
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class EalmacencontabilidadtControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * que se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena valor del check Niif.
     */
    private Boolean pasarNiif;
    /**
     * Variable que almacena el valor del campo Tipo Comprobante.
     */
    private String tipo;
    /**
     * Variable que almacena el valor del campo Número Comprobante.
     */
    private String numero;
    /**
     * Variable que almacena el valor del campo Fecha Comprobante.
     */
    private String fechaInterface;
    /**
     * Variable que almacena la fecha seleccionada.
     */
    private Date fecha;
    /**
     * Variable que consulta el parámetro PROCESO NUMERACION NOMINA
     * CONTABILIDAD.
     * 
     */
    private String proceso;
    /**
     * Variable que consulta el parámetro MANEJA NIIF EN ALMACEN.
     */
    private String niif;
    /**
     * Variable que consulta el parámetro IVA DISCRIMINADO EN INTERFAZ
     * DIARIA DE ALMACEN POR TIPO MOV
     */
    private String iva;
    /**
     * Variable que controla visualización de check y etiqueta de
     * NIIF.
     */
    private String visNiif;
    /**
     * Variable que obtiene el año de la fecha seleccionada.
     */
    private int ano;
    /**
     * Variable que obtiene el mes de la fecha seleccionada.
     */
    private int mes;
    /**
     * Variable que obtiene el día de la fecha seleccionada.
     */
    private int dia;
    /**
     * Variable que obtiene el último día de mes de la fecha
     * seleccionada.
     */
    private int diaU;
    /**
     * Variable que almacena el valor del parámetro TIPO COMPROBANTE
     * INTERFASE DIARIA ALMACEN EN NIIF
     */
    private String tipoComprobanteNiif;
    /**
     * Variable que almacena el valor del parámetro MANEJA INTERFACE
     * ALMACEN MENSUAL POR CENTRO COSTO
     */
    private String centro;
    /**
     * Variable que almacena el valor del parámetro MANEJA INTERFACE
     * ALMACEN MENSUAL POR CENTRO COSTO
     */
    private BigDecimal numeroComprobante;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de ContabilizarAlmacen para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_CONTABILIZAR_ALMACEN
     */
    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacen;

    /**
     * Crea una nueva instancia de EalmacencontabilidadtControlador
     */
    public EalmacencontabilidadtControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.E_ALMACEN_CONTABILIDAD_T_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     * 
     * @return
     * 
     * @return
     */
    public void generarReporte(ReportesBean.FORMATOS formato)

    {

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            // Condicional para evaluar visualización de check y
            // etiqueta "Pasar a NIIF"
            proceso = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "PROCESO NUMERACION NOMINA CONTABILIDAD",
                                            modulo,
                                            new Date(), true), "NO");
            niif = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA NIIF EN ALMACEN",
                                            modulo,
                                            new Date(), true), "NO");
            tipo = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TIPO COMPROBANTE INTERFASE DIARIA ALMACEN",
                                            modulo,
                                            new Date(), true), "ALM");
            tipoComprobanteNiif = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TIPO COMPROBANTE INTERFASE DIARIA ALMACEN EN NIIF",
                                            modulo,
                                            new Date(), true), "IMN");
            iva = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN POR TIPO MOV",
                                            modulo,
                                            new Date(), true), "NO");
            centro = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA INTERFACE ALMACEN MENSUAL POR CENTRO COSTO",
                                            modulo,
                                            new Date(), true), "NO");

            if ("SI".equals(niif)) {
                visNiif = "block";
            } else {
                visNiif = "none";
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    /*
     * metodo donde se valida que funcion se ejecuta deacuerdo a unos
     * parametros consultados, y posteriormente se genera un archivo
     * txt, excel y pdf .
     */
    public void seleccionDeArchivo() throws SystemException {
        numeroComprobante = BigDecimal.valueOf(Integer.parseInt(numero));
        archivoDescarga = null;
        // variables que contiene el nombre de los reportes a generar
        // segun corresponda
        String nombrePDF = "001501INT";
        /**
         * variable que almacena el retorno de la funcion en este caso
         * el texto, que retorna en un Clob
         */
        String cadenaClob = "";
        String nombreSalida;
        // variable que contiene el nombre de la consulta para generar
        // el archivo Excel
        String consulta = "0000000vrfInterfaceDiariaAlmacenIvaDiscriminado";
        /**
         * variable que puede almacenar la consulta que retorna una
         * funcion segun corresponda, o el resuleve consulta del plano
         * Excel si aplica el caso.
         */
        String consultaSQL = "";

        // vector donde se almacena el nombre del reporte o plano y su
        // correspondiente extencion
        String[] nombresInformes = new String[3];

        // se declaran ByteArrayInputStream devido a que se debe
        // implementar un .rar para la descarga de los archivos
        ByteArrayInputStream salidaNombreExcel;
        ByteArrayInputStream salidaNombreClob;
        ByteArrayInputStream salidaNombrePDF;

        Map<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametro = new HashMap<>();
        reemplazos.put("compania", compania);
        reemplazos.put("fecha", SysmanFunciones
                        .formatearFechaCadena(fecha, "dd/MM/yyyy"));

        reemplazos.put("ano", ano);
        reemplazos.put("tipoCpte", tipo);
        reemplazos.put("numeroPptoInicial", numero);
        reemplazos.put("numeroPptoFinal", numero);

        parametro.put("PR_TITULO_CER_REG", "NOMDIS");
        parametro.put("PR_TITULO_RP_RO", "NOMRESERVA");
        parametro.put("PR_DESCRIPCION", "ALMACEN");
        try {
            // se evalua parametro
            if ("SI".equals(iva)) {
                // se almacena la respuesta de la funcion
                // contabilizarAlmcnH
                cadenaClob = ejbContabilizarAlmacen.contabilizarAlmcnH(
                                compania,
                                ano,
                                mes,
                                fecha,
                                tipo,
                                numeroComprobante, SessionUtil.getUser()
                                                .getCodigo());
                consultaSQL = Reporteador.resuelveConsulta(consulta,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos);
                nombreSalida = "documentosDescargadosInterfazI";
            } else {
                // se evalua aparametro
                if ("NO".equals(centro)) {
                    // se evalua el check de NIIF
                    if (pasarNiif == true) {
                        compania = "";
                        String compAlmacen = "";
                        // se almacena el valor del parametro

                        compAlmacen = ejbSysmanUtil.consultarParametro(
                                        SessionUtil.getCompania(),
                                        "COMPANIA PARA INSERTAR COMPROBANTE ALMACEN",
                                        modulo,
                                        new Date(), true);
                        if (compAlmacen == null) {
                            compAlmacen = compania;
                        }
                        // se compara el valor del parametro y se le
                        // asigna un valor a compañia segun
                        // corresponda
                        if (compAlmacen != "100") {
                            compania = SysmanFunciones.nvlStr(compAlmacen,
                                            SessionUtil.getCompania());
                        } else {
                            String equivNiif = "";
                            equivNiif = ejbSysmanUtil.consultarParametro(
                                            SessionUtil.getCompania(),
                                            "COMPAÑIA EQUIVALENTE NIIF",
                                            modulo,
                                            new Date(), true);
                            compania = SysmanFunciones.nvlStr(equivNiif,
                                            SessionUtil.getCompania());
                        }
                    } else {
                        compania = SessionUtil.getCompania();
                    }
                    // se almacena la respuesta de la funcion
                    // contabilizarHNiveles
                    cadenaClob = ejbContabilizarAlmacen
                                    .contabilizarHNiveles(
                                                    compania,
                                                    ano,
                                                    mes,
                                                    fecha,
                                                    tipo,
                                                    numeroComprobante,
                                                    pasarNiif,
                                                    SessionUtil.getUser()
                                                                    .getCodigo());

                    // se almacena la consulat y se serializa para
                    // generar el archivo plano Excel
                    consultaSQL = ejbContabilizarAlmacen
                                    .contabilizarArmConsltHNvles(compania,
                                                    fecha, pasarNiif);
                    nombreSalida = "documentosDescargadosInterfazC";
                } else {
                    // se almacena la respuesta de la funcion
                    // contabilizarHNivelesCC
                    cadenaClob = ejbContabilizarAlmacen.contabilizarHNivelesCC(
                                    compania,
                                    ano,
                                    mes,
                                    fecha,
                                    tipo,
                                    numeroComprobante,
                                    pasarNiif,
                                    SessionUtil.getUser()
                                                    .getCodigo());
                    // se almacena la consulat y se serializa para
                    // generar el archivo plano Excel
                    consultaSQL = ejbContabilizarAlmacen
                                    .contabilizarArmConsltHNvlesCC(compania,
                                                    fecha, pasarNiif);
                    nombreSalida = "documentosDescargadosInterfazNCC";
                }
            }
            Reporteador.resuelveConsulta(nombrePDF,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametro);
            salidaNombrePDF = JsfUtil.serializarReporte(
                            nombrePDF, parametro,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
            salidaNombreClob = JsfUtil.serializarPlano(
                            cadenaClob);
            salidaNombreExcel = JsfUtil.serializarHojaDatos(consultaSQL,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
            ByteArrayInputStream[] salida = new ByteArrayInputStream[3];
            int cantidad = 0;
            if (!(salidaNombreExcel == null)) {
                salida[cantidad] = salidaNombreExcel;
                nombresInformes[cantidad] = "VerificaciónInterfaceDiariaAlmacenIvaDiscriminado.xlsx";
                cantidad++;
            }
            if (!(salidaNombreClob == null)) {
                salida[cantidad] = salidaNombreClob;
                nombresInformes[cantidad] = "InconsistenciaContabilizarAlmacen.txt";
                cantidad++;
            }
            if (!(salidaNombrePDF == null)) {
                salida[cantidad] = salidaNombrePDF;
                nombresInformes[cantidad] = nombrePDF + ".pdf";
                cantidad++;
            }
            if (cantidad > 0) {
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salida,
                                nombresInformes,
                                nombreSalida);
            }
        } catch (JRException | IOException | SystemException
                        | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlerta(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     * 
     * @throws SystemException
     * 
     */

    public void oprimirAceptar() throws SystemException

    {
        archivoDescarga = null;
        seleccionDeArchivo();

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Fecha
     * 
     */
    public void cambiarFecha() {
        try {
            diaU = SysmanFunciones.ultimoDiaInt(fecha);
            dia = SysmanFunciones.dia(fecha);
            mes = SysmanFunciones.mes(fecha);
            ano = SysmanFunciones.ano(fecha);
            fechaInterface = ejbSysmanUtil.generarCerosIzquierda(diaU, 2) + "/"
                + ejbSysmanUtil.generarCerosIzquierda(mes, 2) + "/" + ano;
            if (proceso == "NO") {
                numero = ano + ejbSysmanUtil.generarCerosIzquierda(mes, 2)
                    + ejbSysmanUtil.generarCerosIzquierda(dia, 4);
            } else {
                Registro rs = null;
                //
                Map<String, Object> param = new TreeMap<>();
                try {
                    tipo = SysmanFunciones
                                    .nvlStr(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "TIPO COMPROBANTE INTERFASE DIARIA ALMACEN",
                                                    modulo,
                                                    new Date(), true), "AL1");
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(GeneralParameterEnum.ANO.getName(), ano);
                    param.put(GeneralParameterEnum.MES.getName(), mes);
                    param.put(GeneralParameterEnum.TIPO.getName(), tipo);
                    rs = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    EalmacencontabilidadTControladorUrlEnum.URL4410
                                                                                    .getValue())
                                                    .getUrl(), param));
                    String conteo = "0";
                    if (rs != null) {
                        conteo = SysmanFunciones.toString(rs.getCampos()
                                        .get(EalmacencontabilidadTControladorEnum.ULTIMONUMERO
                                                        .getValue()));
                    }
                    if (!"0".equals(conteo)) {
                        numero = ano
                            + ejbSysmanUtil.generarCerosIzquierda(mes, 2)
                            + ejbSysmanUtil.generarCerosIzquierda(dia, 4);
                    } else {
                        numero = ano
                            + ejbSysmanUtil.generarCerosIzquierda(mes, 2)
                            + "0001";
                    }

                } catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
        } catch (ParseException | SystemException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo ejecutado al cambiar el control NIIF
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarNIIF() {
        if (pasarNiif == true) {
            tipo = tipoComprobanteNiif;
        } else {
            try {
                tipo = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "TIPO COMPROBANTE INTERFASE DIARIA ALMACEN",
                                                modulo,
                                                new Date(), true), "AL1");
            } catch (SystemException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    public Boolean getPasarNiif() {
        return pasarNiif;
    }

    public void setPasarNiif(Boolean pasarNiif) {
        this.pasarNiif = pasarNiif;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable numero
     * 
     * @return numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Asigna la variable numero
     * 
     * @param numero
     * Variable a asignar en numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Retorna la variable fechaInterface
     * 
     * @return fechaInterface
     */
    public String getFechaInterface() {
        return fechaInterface;
    }

    /**
     * Asigna la variable fechaInterface
     * 
     * @param fechaInterface
     * Variable a asignar en fechaInterface
     */
    public void setFechaInterface(String fechaInterface) {
        this.fechaInterface = fechaInterface;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public EjbSysmanUtilRemote getEjbSysmanUtil() {
        return ejbSysmanUtil;
    }

    public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
        this.ejbSysmanUtil = ejbSysmanUtil;
    }

    public String getModulo() {
        return modulo;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getNiif() {
        return niif;
    }

    public void setNiif(String niif) {
        this.niif = niif;
    }

    public String getVisNiif() {
        return visNiif;
    }

    public void setVisNiif(String visNiif) {
        this.visNiif = visNiif;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getDiaU() {
        return diaU;
    }

    public void setDiaU(int diaU) {
        this.diaU = diaU;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getTipoComprobanteNiif() {
        return tipoComprobanteNiif;
    }

    public void setTipoComprobanteNiif(String tipoComprobanteNiif) {
        this.tipoComprobanteNiif = tipoComprobanteNiif;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public BigDecimal getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(BigDecimal numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
