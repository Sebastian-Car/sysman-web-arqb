/*-
 * RegiscuentasporpagarcgrControlador.java
 *
 * 1.0
 *
 * 08/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.RegiscuentasporpagarcgrControladorEnum;
import com.sysman.cgr.enums.RegiscuentasporpagarcgrControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar el informe de registro de cuentas
 * por pagar. Se accede desde la ruta Panel Principal\Entes de
 * Control\SChip-CGR\Configuracion/Informes\Resolucion 6224\Libro de
 * registro de cuentas por pagar.
 *
 * @version 1.0, 08/03/2017
 * @author lcortes
 * @version 2.0 16/08/2017
 * @modifiedby jrodriguezr Se elimina la conexion y se ajusta el
 * manejo de excepciones
 * @version 3.0, 29/08/2017
 * @modifiedby <strong>jrodriguezr </strong>Se refactoriza el código
 * SQL de las listas para utilizar DSS. También los llamados a
 * funciones, procedimientos y métodos de la clase Acciones a llamados
 * a EJB. Textos al archivo properties. Cambio el numero del
 * formulario al enumerado.
 */
@ManagedBean
@ViewScoped
public class RegiscuentasporpagarcgrControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar si la casilla de verificacion
     * Mostrar Codigo Interno ha sido seleccionada o no.
     */
    private boolean indCodInterno;
    /**
     * Atributo que permite identificar si la casilla de verificacion
     * Con Saldos ha sido seleccionada o no.
     */
    private boolean conSaldos;
    /**
     * Atributo que almacena el ID seleccionado de la lista Cuenta
     * Inicial.
     */
    private String cuentaInicial;
    /**
     * Atributo que almacena el ID seleccionado de la lista Cuenta
     * Final.
     */
    private String cuentaFinal;
    /**
     * Atributo que almacena el codigo del mes inicial seleccionado.
     */
    private String mesInicial;
    /**
     * Atributo que almacena el codigo del mes inicial seleccionado.
     */
    private String mesFinal;
    /**
     * Atributo que almacena el anio seleccionado.
     */
    private String anio;
    /**
     * Atributo que almacena el valor del campo Nivel.
     */
    private String nivel;
    /**
     * Atributo que almacena el nombre del mes inicial seleccionado de
     * la lista mes inicial.
     */
    private String nombreMesIni;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de los meses que se pueden seleccionar en la lista mes
     * incial.
     */
    private List<Registro> listaMesInicial;
    /**
     * Lista de los meses que se pueden seleccionar en la lista mes
     * final.
     */
    private List<Registro> listaMesFinal;
    /**
     * Lista de los anios que se pueden seleccionar en la lista ano.
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de cuentas de las cuentas existentes para el anio
     * seleccionado.
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista de cuentas de las cuentas existentes para el anio
     * seleccionado.
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RegiscuentasporpagarcgrControlador
     */
    public RegiscuentasporpagarcgrControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISCUENTASPORPAGARCGR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
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
        cargarListaAno();
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
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        cargarListaMesInicial();
        mesInicial = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaMesFinal();
        mesFinal = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaCuentaInicial();
        nivel = "99";
        nombreMesIni = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegiscuentasporpagarcgrControladorUrlEnum.URL7573
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaMesFinal
     */
    public void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegiscuentasporpagarcgrControladorUrlEnum.URL8199
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegiscuentasporpagarcgrControladorUrlEnum.URL8912
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCuentaInicial
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegiscuentasporpagarcgrControladorUrlEnum.URL9587
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCuentaFinal
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegiscuentasporpagarcgrControladorUrlEnum.URL10394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(RegiscuentasporpagarcgrControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista.
     * Permite generar el informe en formato PDF.
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios()) {
            return;
        }

        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEnviarexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios()) {
            return;
        }
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();
        String tituloSeccion = "";
        String tituloUnidad = "";
        String tituloRegional = "";
        String conSeccion = "";
        String conUnidad = "";
        String conRegional = "";
        String conCodInterno = "NO";
        String sqlUnion = "";
        try {
            // Reemplazos consulta reporte
            reemplazar.put("nivel", nivel);
            reemplazar.put("anio", anio);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            if (indCodInterno) {
                conCodInterno = "SI";
            }
            if (conSaldos) {
                sqlUnion = Reporteador.resuelveConsulta(
                                "800091REGISTROCUENTASxPAGAR036UNION",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar);

            }
            reemplazar.put("sqlUnion", sqlUnion);
            if (("SI").equals(SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SECCION EN INFORMES RESOLUCION 036",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "NO"))) {
                conSeccion = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(
                                                compania, "SECCION 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "");
                tituloSeccion = conSeccion != "" ? "SECCION: " : "";
                conUnidad = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "UNIDAD EJECUTORA 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "");
                tituloUnidad = conUnidad != "" ? "UNIDAD EJECUTORA: " : "";
                conRegional = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(
                                                compania, "REGIONAL 036",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "");
                tituloRegional = conRegional != "" ? "REGIONAL: " : "";
            }

            // Parametros del reporte
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CONSECCION", conSeccion);
            parametros.put("PR_CONUNIDAD", conUnidad);
            parametros.put("PR_CONREGIONAL", conRegional);
            parametros.put("PR_TITULOSECCION", tituloSeccion);
            parametros.put("PR_TITULOUNIDAD", tituloUnidad);
            parametros.put("PR_TITULOREGIONAL", tituloRegional);
            parametros.put("PR_NMES", nombreMesIni);
            parametros.put("PR_VISIBLE", conCodInterno);

            Reporteador.resuelveConsulta("001438REGISTROCUENTASxPAGAR036",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001438REGISTROCUENTASxPAGAR036", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite verificar que los campos obligatorios para
     * generar el informe no esten vacios.
     *
     * @return true Si los campos obligatorios se han diligenciado
     * completamente.
     */
    private boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mesInicial)
            || SysmanFunciones.validarVariableVacio(mesFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3496"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)
            || SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(
                            "Debe seleccionar las cuentas inicial y final.");
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(nivel)) {
            JsfUtil.agregarMensajeAlerta(
                            "Debe seleccionar ingresar el valor del nivel.");
            return false;
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     *
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = null;
        mesFinal = null;
        cuentaInicial = null;
        cuentaFinal = null;
        listaMesFinal = null;
        listaCuentaFinal = null;
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesInicial
     *
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        nombreMesIni = listaMesInicial.get(Integer.parseInt(mesInicial) - 1)
                        .getCampos().get("NOMBRE").toString();
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indCodInterno
     *
     * @return indCodInterno
     */
    public boolean isIndCodInterno() {
        return indCodInterno;
    }

    /**
     * Asigna la variable indCodInterno
     *
     * @param indCodInterno
     * Variable a asignar en indCodInterno
     */
    public void setIndCodInterno(boolean indCodInterno) {
        this.indCodInterno = indCodInterno;
    }

    /**
     * Retorna la variable conSaldos
     *
     * @return conSaldos
     */
    public boolean isConSaldos() {
        return conSaldos;
    }

    /**
     * Asigna la variable conSaldos
     *
     * @param conSaldos
     * Variable a asignar en conSaldos
     */
    public void setConSaldos(boolean conSaldos) {
        this.conSaldos = conSaldos;
    }

    /**
     * Retorna la variable cuentaInicial
     *
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable mesInicial
     *
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     *
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     *
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     *
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable nivel
     *
     * @return nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     *
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaMesInicial
     *
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     *
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     *
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     *
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
