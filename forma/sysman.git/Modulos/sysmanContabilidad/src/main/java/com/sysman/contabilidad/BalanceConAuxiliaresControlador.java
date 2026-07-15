/*-
 * BalanceConAuxiliaresControlador.java
 *
 * 1.0
 *
 * 05/12/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceConAuxiliaresControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * Controlador usado para generar informes contables teniendo en
 * cuenta los auxilaries de tercero, centro de costo, auxiliar
 * general, referencia y fuente de recurso. Es posible realizar
 * diferentes combinaciones de los auxilares y teniendo en cuenta un
 * rango especifico de estos,
 *
 * @version 1.0, 05/12/2016
 * @author sdaza
 * @author yrojas
 * @version 2, 11/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por
 * las invocaciones de los ejb.
 * 
 * @author ybecerra
 * @version 4, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class BalanceConAuxiliaresControlador extends BeanBaseModal {

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo al
     * cual ingresa el usuario
     */
    private final String modulo;

    private final String strMensaje;
    // <DECLARAR_ATRIBUTOS>
    /**
     * atributo que indica si se genera el informe teniendo en cuenta
     * el centro de costo. Si su valor es verdadero se harï¿½n
     * visibles los controles grï¿½ficos para seleccionar el rango de
     * centros de costo a tener en cuenta
     */
    private boolean indCentroCosto;
    /**
     * atributo que indica si se genera el informe teniendo en cuenta
     * el tercero. Si su valor es verdadero se harï¿½n visibles los
     * controles grï¿½ficos para seleccionar el rango de terceros a
     * tener en cuenta
     */
    private boolean indTercero;
    /**
     * atributo que indica si se genera el informe teniendo en cuenta
     * el auxiliar general. Si su valor es verdadero se harï¿½n
     * visibles los controles grï¿½ficos para seleccionar el rango de
     * auxiliares generales a tener en cuenta
     */
    private boolean indAuxiliar;
    /**
     * indica si los valores a comparar en el informe son
     * exclusivamente entre los dos meses seleccionados, de los
     * contrario se tendrï¿½ el cuenta el rango abarcado por los meses
     * seleccionados
     */
    private boolean indMeses;
    /**
     * atributo que indica si se genera el informe teniendo en cuenta
     * la referencia. Si su valor es verdadero se harï¿½n visibles los
     * controles grï¿½ficos para seleccionar el rango de referencias a
     * tener en cuenta
     */
    private boolean indReferencia;
    /**
     * atributo que indica si se genera el informe teniendo en cuenta
     * la fuente de recursos. Si su valor es verdadero se harï¿½n
     * visibles los controles grï¿½ficos para seleccionar el rango de
     * fuentes de recurso a tener en cuenta
     */
    private boolean indFteRecurso;

    /**
     * atributo que indica si se genera el informe mayorizando los
     * auxiliares de acuerdo a la seleccion
     */
    private boolean mayorizar;
    /**
     * atributo que indica si se genera el informe en el saldo inicial
     * y final con el saldo neto y no con su debito y credito
     */
    private boolean especial;
    /**
     * atributo que indica el codigo contable inicial para la
     * generaciï¿½n del informe
     */
    private String codigoInicial;
    /**
     * atributo que indica el codigo contable final para la
     * generaciï¿½n del informe
     */
    private String codigoFinal;
    /**
     * atributo que indica el centro de costo inicial para la
     * generaciï¿½n del informe
     */
    private String centroInicial;
    /**
     * atributo que indica el centro de costo final para la
     * generaciï¿½n del informe
     */
    private String centroFinal;
    /**
     * atributo que indica el tercero inicial para la generaciï¿½n del
     * informe
     */
    private String terceroInicial;
    /**
     * atributo que indica el tercero final para la generaciï¿½n del
     * informe
     */
    private String terceroFinal;
    /**
     * atributo que indica el auxiliar general inicial para la
     * generaciï¿½n del informe
     */
    private String auxiliarInicial;
    /**
     * atributo que indica el auxiliar general final para la
     * generaciï¿½n del informe
     */
    private String auxiliarFinal;
    /**
     * atributo que indica la referencia inicial para la generaciï¿½n
     * del informe
     */
    private String referenciaInicial;
    /**
     * atributo que indica la referencia final para la generaciï¿½n
     * del informe
     */
    private String referenciaFinal;
    /**
     * atributo que indica la fuente de recuerso inicial para la
     * generaciï¿½n del informe
     */
    private String fteRecursoInicial;
    /**
     * atributo que indica la fuente de recuerso final para la
     * generaciï¿½n del informe
     */
    private String fteRecursoFinal;
    /**
     * atributo que indica el aï¿½o seleccionado para la generaciï¿½n
     * del informe
     */
    private String anoTrabajo;
    /**
     * atributo que indica el mes inicial para la generaciï¿½n del
     * informe
     */
    private String mesInicial;
    /**
     * atributo que indica la longitud de las cuentas a visualizar en
     * el informe
     */
    private String digitos;
    /**
     * atributo que indica el mes final para la generaciï¿½n del
     * informe
     */
    private String mesFinal;
    /**
     * atributo que indica si el informe tendrï¿½ o no en cuenta los
     * registros con valores en cero
     */
    private boolean saldoCero;
    
    /**
     * atributo que indica si se genera el informe en excel plano sin celdas combinadas
     */
    private boolean excelPlano;
    
    /**
     * atributo que indica si se genera el informe con referenciado cuando el indicador
     * referencia se encuentra activo
     */
    private boolean referenciado;

    /**
     * atributo usado en la generaciï¿½n del reporte
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de los aï¿½os de la compania
     */
    private List<Registro> listaAnoTrabajo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado del plan contable, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Listado del plan contable, cï¿½digo final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Listado de los centros, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCmbCentroCInicial;
    /**
     * Listado de los centros, cï¿½digo final
     */
    private RegistroDataModelImpl listaCmbCentroCFinal;
    /**
     * Listado de los terceros, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCmbTerceroInicial;
    /**
     * Listado de los terceros, cï¿½digo final
     */
    private RegistroDataModelImpl listaCmbTerceroFinal;
    /**
     * Listado de los auxiliares generales, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCmbAuxiliarInicial;
    /**
     * Listado de los auxiliares generales, cï¿½digo final
     */
    private RegistroDataModelImpl listaCmbAuxiliarFinal;
    /**
     * Listado de las referencias, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCmbReferenciaInicial;
    /**
     * Listado de las referencias, cï¿½digo final
     */
    private RegistroDataModelImpl listaCmbReferenciaFinal;
    /**
     * Listado de las fuentes de recurso, cï¿½digo inicial
     */
    private RegistroDataModelImpl listaCmbFteRecursoInicial;
    /**
     * Listado de las fuentes de recurso, cï¿½digo final
     */
    private RegistroDataModelImpl listaCmbFteRecursoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BalanceConAuxiliaresControlador
     */

    public BalanceConAuxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strMensaje = "TB_TB2697";
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_CON_AUXILIARES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
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
        anoTrabajo = Integer.toString(SysmanFunciones.ano(new Date()));
        mesInicial = Integer.toString(SysmanFunciones.mes(new Date()));
        mesFinal = "12";
        codigoInicial = "1";
        cargarListaCodigoInicial();
        digitos = "6";
        cargarListaCodigoFinal();
        codigoFinal = SysmanConstantes.CONS_MAX_ID;
        cargarListaCmbFteRecursoInicial();
        cargarListaCmbFteRecursoFinal();
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        cargarListaCmbTerceroInicial();
        cargarListaCmbTerceroFinal();
        cargarListaCmbAuxiliarInicial();
        cargarListaCmbAuxiliarFinal();
        cargarListaCmbReferenciaInicial();
        cargarListaCmbReferenciaFinal();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnoTrabajo
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            BalanceConAuxiliaresControladorUrlEnum.URL11002
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL11513
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL12468
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbCentroCInicial
     */
    public void cargarListaCmbCentroCInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL13433
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbCentroCInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbCentroCFinal
     */
    public void cargarListaCmbCentroCFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL14266
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbCentroCFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbTerceroInicial
     */
    public void cargarListaCmbTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL15101
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * Carga la lista listaCmbTerceroFinal
     */
    public void cargarListaCmbTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL16130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     * Carga la lista listaCmbAuxiliarInicial
     */
    public void cargarListaCmbAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL17165
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbAuxiliarFinal
     */
    public void cargarListaCmbAuxiliarFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL18172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbReferenciaInicial
     */
    public void cargarListaCmbReferenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL19185
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbReferenciaFinal
     */
    public void cargarListaCmbReferenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL20207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbFteRecursoInicial
     */
    public void cargarListaCmbFteRecursoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL21231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbFteRecursoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCmbFteRecursoFinal
     */
    public void cargarListaCmbFteRecursoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceConAuxiliaresControladorUrlEnum.URL22291
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCmbFteRecursoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton cmdImprimir en la vista
     *
     */
    public void oprimircmdImprimir() {
        // <CODIGO_DESARROLLADO>
        generarReportes(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton cmdExcel en la vista
     */
    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        generarReportes(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarAuxiliares() {
        if (indTercero && (SysmanFunciones.validarVariableVacio(terceroInicial)
            || SysmanFunciones.validarVariableVacio(terceroFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString(strMensaje));
            return false;

        }
        if (indAuxiliar
            && (SysmanFunciones.validarVariableVacio(auxiliarInicial)
                || SysmanFunciones.validarVariableVacio(auxiliarFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString(strMensaje));
            return false;

        }
        if (indCentroCosto
            && (SysmanFunciones.validarVariableVacio(centroInicial)
                || SysmanFunciones.validarVariableVacio(centroFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString(strMensaje));
            return false;

        }
        if (indReferencia
            && (SysmanFunciones.validarVariableVacio(referenciaInicial)
                || SysmanFunciones.validarVariableVacio(referenciaFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString(strMensaje));
            return false;

        }
        if (indFteRecurso
            && (SysmanFunciones.validarVariableVacio(fteRecursoInicial)
                || SysmanFunciones.validarVariableVacio(fteRecursoFinal))) {
            JsfUtil.agregarMensajeError(idioma.getString(strMensaje));
            return false;

        }
        return true;
    }

    private boolean validarMeses() {
        if (Integer.parseInt(mesInicial) > Integer.parseInt(mesFinal)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2690"));
            return false;
        }
        if (indMeses && (mesFinal == null)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2693"));
            return false;
        }
        return true;
    }

    private void generarReportes(FORMATOS formatos) {
        archivoDescarga = null;
        String reporte = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        if (validarAuxiliares() && validarMeses()) {

            asignarParametros(parametros);

            mesFinal = indMeses ? mesFinal : mesInicial;

            reemplazar.put("mesIni", mesInicial);
            reemplazar.put("mesFin", mesFinal);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mestrabajo", mesFinal);
            reemplazar.put("anio", anoTrabajo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);

            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal", auxiliarFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);
            reemplazar.put("fuenteInicial", fteRecursoInicial);
            reemplazar.put("fuenteFinal", fteRecursoFinal);

            reemplazar.put("digitos", digitos);
            reemplazar.put("manTer", indTercero ? "1" : "0");
            reemplazar.put("manAux", indAuxiliar ? "1" : "0");
            reemplazar.put("manCen", indCentroCosto ? "1" : "0");
            reemplazar.put("manRef", indReferencia ? "1" : "0");
            reemplazar.put("manFue", indFteRecurso ? "1" : "0");
            reemplazar.put("mayoriza", mayorizar ? "1" : "0");
            reemplazar.put("saldoCero", saldoCero ? "1" : "0");

            reemplazar.put("baseAuxiliar", Reporteador.resuelveConsulta(
                            "800046BaseBalancesAuxiliares",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            reporte = definirReporte();
            try {

                Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                                reemplazar, parametros);
                
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);

            } catch (

            FileNotFoundException e) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                                .replace("s$reporte$s", reporte)
                                    + e.getMessage());
                logger.error(e.getMessage(), e);
            }

            catch (JRException | IOException | SysmanException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }

    }

    private boolean validarIndicadores() {
        return indTercero || indAuxiliar || indReferencia || indFteRecurso
            || indCentroCosto;
    }

    private void asignarParametros(Map<String, Object> parametros) {
        parametros.put("PR_TITULO", definirTituloCC3());
        parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                        .getNombre().toUpperCase());
        parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso()
                .getNit());
        String firmaCont1;
        try {
            firmaCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);
            String cargoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(),
                            true);
            String documentoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 1", modulo,
                            new Date(), true);
            String firmaCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(),
                            true);
            String cargoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(),
                            true);
            String documentoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 2", modulo,
                            new Date(), true);
            String firmaCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(),
                            true);
            String cargoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(),
                            true);
            String documentoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 3", modulo,
                            new Date(), true);
            parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
            parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);
            parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
            parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
            parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
            parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);
            parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
            parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
            parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);

            parametros.put("PR_NIT_VISIBLE", indTercero);
            parametros.put("PR_CENTRO_VISIBLE", indCentroCosto);
            parametros.put("PR_AUXILIAR_VISIBLE", indAuxiliar);
            parametros.put("PR_REFERENCIA_VISIBLE", indReferencia);
            parametros.put("PR_FUENTE_VISIBLE", indFteRecurso);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String definirTituloCC3() {
        String tituloCC3;
        if (referenciado) {
        	
            // Obtener el nombre de la compañía
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre().toUpperCase();

            
            // Obtener el NIT de la compañía 
            String nitCompania = SessionUtil.getCompaniaIngreso().getNit().toString();
            //cc1629 mrosero 
         if(indMeses) {
             tituloCC3 = nombreCompania + "\n"  
                     + "NIT: " + nitCompania + "\n" 
                     + "BALANCE DE PRUEBA POR REFERENCIADO DEL MES DE "
                     + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)].toUpperCase()
                     + " " + "A"
                     + " " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                     .parseInt(mesFinal)].toUpperCase()
                     + " DE " + anoTrabajo;
         }else {
        	 tituloCC3 = nombreCompania + "\n"  
                     + "NIT: " + nitCompania + "\n" 
                     + "BALANCE DE PRUEBA POR REFERENCIADO DEL MES DE "
                     + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)].toUpperCase()
                     + " DE " + anoTrabajo;
         }
        }
        else if (indMeses) {
            tituloCC3 = idioma.getString("TB_TB2691")
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mesInicial)].toUpperCase()
                + " " + "A"
                + " " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mesFinal)].toUpperCase()
                + " DE " + anoTrabajo + "";

        } else {
            tituloCC3 = idioma.getString("TB_TB2692") + " "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mesInicial)].toUpperCase()
                + " DE "
                + anoTrabajo + "";
        }
        return tituloCC3;
    }

    private String definirReporte() {
        String reporte;

        if(indReferencia && referenciado)
        { 	if(especial)
	        {
        	reporte = "002684INFAUXILIARPRUEBAREFERENCIADOESP";
	        }
	        else
        	reporte = "002608INFAUXILIARPRUEBAREFERENCIADO";
        }
        else if(excelPlano) {
        	reporte = "002600INFPLANOAUXILIARPRUEBA";
        } else if (especial) {
            reporte = "000588AuxiliarPruebaEspecial";            
        } else if (mayorizar && indCentroCosto) {
            reporte = "002572BalanceCentroCostosMayorizado";
        } else {
            reporte = "000588AuxiliarPrueba";
        }
        return reporte;
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control anoTrabajo
     *
     * Se debe cargar las listas que requieren aï¿½o para refrescar
     * los datos de los combos
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        cargarListaCmbTerceroInicial();
        cargarListaCmbTerceroFinal();
        cargarListaCmbAuxiliarInicial();
        cargarListaCmbAuxiliarFinal();
        cargarListaCmbReferenciaInicial();
        cargarListaCmbReferenciaFinal();
        cargarListaCmbFteRecursoInicial();
        cargarListaCmbFteRecursoFinal();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control indMeses
     *
     * se actualiza el valor del indicador el cual es usado para
     * visualizar o no la etiqueta y campo del mes final
     *
     */
    public void cambiarindMeses() {
        // NO ESTA IMPLEMENTADO
    }

    /**
     * Metodo ejecutado al cambiar el control indCentroCosto
     *
     *
     */
    public void cambiarindCentroCosto() {
    }

    /**
     * Metodo ejecutado al cambiar el control indTercero
     *
     */
    public void cambiarindTercero() {
    }

    /**
     * Metodo ejecutado al cambiar el control indAuxiliar
     *
     */
    public void cambiarindAuxiliar() {
    }

    /**
     * Metodo ejecutado al cambiar el control indReferencia
     */
    public void cambiarindReferencia() {
    }

    /**
     * Metodo ejecutado al cambiar el control indFteRecurso
     */
    public void cambiarindFteRecurso() {
    }

    /**
     * Metodo ejecutado al cambiar el control Digitos
     * 
     * 
     */
    public void cambiarDigitos() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(digitos)) {
            digitos = "6";
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesFinal
     * 
     * 
     */
    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(mesFinal)) {
            mesFinal = "12";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesInicial
     * 
     * 
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(mesInicial)) {
            mesInicial = "1";
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCentroCInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCentroCInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCentroCFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCentroCFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbTerceroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbTerceroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbAuxiliarInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbAuxiliarFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbReferenciaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbReferenciaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbFteRecursoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbFteRecursoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fteRecursoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbFteRecursoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbFteRecursoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fteRecursoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indCentroCosto
     *
     * @return indCentroCosto
     */
    public boolean getIndCentroCosto() {
        return indCentroCosto;
    }

    /**
     * Asigna la variable indCentroCosto
     *
     * @param indCentroCosto
     * Variable a asignar en indCentroCosto
     */
    public void setIndCentroCosto(boolean indCentroCosto) {
        this.indCentroCosto = indCentroCosto;
    }

    /**
     * Retorna la variable indTercero
     *
     * @return indTercero
     */
    public boolean getIndTercero() {
        return indTercero;
    }

    /**
     * Asigna la variable indTercero
     *
     * @param indTercero
     * Variable a asignar en indTercero
     */
    public void setIndTercero(boolean indTercero) {
        this.indTercero = indTercero;
    }

    /**
     * Retorna la variable indAuxiliar
     *
     * @return indAuxiliar
     */
    public boolean getIndAuxiliar() {
        return indAuxiliar;
    }

    /**
     * Asigna la variable indAuxiliar
     *
     * @param indAuxiliar
     * Variable a asignar en indAuxiliar
     */
    public void setIndAuxiliar(boolean indAuxiliar) {
        this.indAuxiliar = indAuxiliar;
    }

    /**
     * Retorna la variable indMeses
     *
     * @return indMeses
     */
    public boolean getIndMeses() {
        return indMeses;
    }

    /**
     * Asigna la variable indMeses
     *
     * @param indMeses
     * Variable a asignar en indMeses
     */
    public void setIndMeses(boolean indMeses) {
        this.indMeses = indMeses;
    }

    /**
     * Retorna la variable indReferencia
     *
     * @return indReferencia
     */
    public boolean getIndReferencia() {
        return indReferencia;
    }

    /**
     * Asigna la variable indReferencia
     *
     * @param indReferencia
     * Variable a asignar en indReferencia
     */
    public void setIndReferencia(boolean indReferencia) {
        this.indReferencia = indReferencia;
    }

    /**
     * Retorna la variable indFteRecurso
     *
     * @return indFteRecurso
     */
    public boolean getIndFteRecurso() {
        return indFteRecurso;
    }

    /**
     * Asigna la variable indFteRecurso
     *
     * @param indFteRecurso
     * Variable a asignar en indFteRecurso
     */
    public void setIndFteRecurso(boolean indFteRecurso) {
        this.indFteRecurso = indFteRecurso;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable centroInicial
     *
     * @return centroInicial
     */
    public String getCentroInicial() {
        return centroInicial;
    }

    /**
     * Asigna la variable centroInicial
     *
     * @param centroInicial
     * Variable a asignar en centroInicial
     */
    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    /**
     * Retorna la variable centroFinal
     *
     * @return centroFinal
     */
    public String getCentroFinal() {
        return centroFinal;
    }

    /**
     * Asigna la variable centroFinal
     *
     * @param centroFinal
     * Variable a asignar en centroFinal
     */
    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    /**
     * Retorna la variable terceroInicial
     *
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     *
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     *
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     *
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable auxiliarInicial
     *
     * @return auxiliarInicial
     */
    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    /**
     * Asigna la variable auxiliarInicial
     *
     * @param auxiliarInicial
     * Variable a asignar en auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    /**
     * Retorna la variable auxiliarFinal
     *
     * @return auxiliarFinal
     */
    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    /**
     * Asigna la variable auxiliarFinal
     *
     * @param auxiliarFinal
     * Variable a asignar en auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     *
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     *
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     *
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     *
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    /**
     * Retorna la variable fteRecursoInicial
     *
     * @return fteRecursoInicial
     */
    public String getFteRecursoInicial() {
        return fteRecursoInicial;
    }

    /**
     * Asigna la variable fteRecursoInicial
     *
     * @param fteRecursoInicial
     * Variable a asignar en fteRecursoInicial
     */
    public void setFteRecursoInicial(String fteRecursoInicial) {
        this.fteRecursoInicial = fteRecursoInicial;
    }

    /**
     * Retorna la variable fteRecursoFinal
     *
     * @return fteRecursoFinal
     */
    public String getFteRecursoFinal() {
        return fteRecursoFinal;
    }

    /**
     * Asigna la variable fteRecursoFinal
     *
     * @param fteRecursoFinal
     * Variable a asignar en fteRecursoFinal
     */
    public void setFteRecursoFinal(String fteRecursoFinal) {
        this.fteRecursoFinal = fteRecursoFinal;
    }

    /**
     * Retorna la variable anoTrabajo
     *
     * @return anoTrabajo
     */
    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    /**
     * Asigna la variable anoTrabajo
     *
     * @param anoTrabajo
     * Variable a asignar en anoTrabajo
     */
    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
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
     * Retorna la variable digitos
     *
     * @return digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Asigna la variable digitos
     *
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
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
     * @return the saldoCero
     */
    public boolean isSaldoCero() {
        return saldoCero;
    }

    /**
     * @param saldoCero
     * the saldoCero to set
     */
    public void setSaldoCero(boolean saldoCero) {
        this.saldoCero = saldoCero;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaCmbCentroCInicial
     *
     * @return listaCmbCentroCInicial
     */
    public RegistroDataModelImpl getListaCmbCentroCInicial() {
        return listaCmbCentroCInicial;
    }

    /**
     * @return the listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * @param listaCodigoInicial
     * the listaCodigoInicial to set
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * @return the listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * @param listaCodigoFinal
     * the listaCodigoFinal to set
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCmbCentroCInicial
     *
     * @param listaCmbCentroCInicial
     * Variable a asignar en listaCmbCentroCInicial
     */
    public void setListaCmbCentroCInicial(
        RegistroDataModelImpl listaCmbCentroCInicial) {
        this.listaCmbCentroCInicial = listaCmbCentroCInicial;
    }

    /**
     * Retorna la lista listaCmbCentroCFinal
     *
     * @return listaCmbCentroCFinal
     */
    public RegistroDataModelImpl getListaCmbCentroCFinal() {
        return listaCmbCentroCFinal;
    }

    /**
     * Asigna la lista listaCmbCentroCFinal
     *
     * @param listaCmbCentroCFinal
     * Variable a asignar en listaCmbCentroCFinal
     */
    public void setListaCmbCentroCFinal(
        RegistroDataModelImpl listaCmbCentroCFinal) {
        this.listaCmbCentroCFinal = listaCmbCentroCFinal;
    }

    /**
     * Retorna la lista listaCmbTerceroInicial
     *
     * @return listaCmbTerceroInicial
     */
    public RegistroDataModelImpl getListaCmbTerceroInicial() {
        return listaCmbTerceroInicial;
    }

    /**
     * Asigna la lista listaCmbTerceroInicial
     *
     * @param listaCmbTerceroInicial
     * Variable a asignar en listaCmbTerceroInicial
     */
    public void setListaCmbTerceroInicial(
        RegistroDataModelImpl listaCmbTerceroInicial) {
        this.listaCmbTerceroInicial = listaCmbTerceroInicial;
    }

    /**
     * Retorna la lista listaCmbTerceroFinal
     *
     * @return listaCmbTerceroFinal
     */
    public RegistroDataModelImpl getListaCmbTerceroFinal() {
        return listaCmbTerceroFinal;
    }

    /**
     * Asigna la lista listaCmbTerceroFinal
     *
     * @param listaCmbTerceroFinal
     * Variable a asignar en listaCmbTerceroFinal
     */
    public void setListaCmbTerceroFinal(
        RegistroDataModelImpl listaCmbTerceroFinal) {
        this.listaCmbTerceroFinal = listaCmbTerceroFinal;
    }

    /**
     * Retorna la lista listaCmbAuxiliarInicial
     *
     * @return listaCmbAuxiliarInicial
     */
    public RegistroDataModelImpl getListaCmbAuxiliarInicial() {
        return listaCmbAuxiliarInicial;
    }

    /**
     * Asigna la lista listaCmbAuxiliarInicial
     *
     * @param listaCmbAuxiliarInicial
     * Variable a asignar en listaCmbAuxiliarInicial
     */
    public void setListaCmbAuxiliarInicial(
        RegistroDataModelImpl listaCmbAuxiliarInicial) {
        this.listaCmbAuxiliarInicial = listaCmbAuxiliarInicial;
    }

    /**
     * Retorna la lista listaCmbAuxiliarFinal
     *
     * @return listaCmbAuxiliarFinal
     */
    public RegistroDataModelImpl getListaCmbAuxiliarFinal() {
        return listaCmbAuxiliarFinal;
    }

    /**
     * Asigna la lista listaCmbAuxiliarFinal
     *
     * @param listaCmbAuxiliarFinal
     * Variable a asignar en listaCmbAuxiliarFinal
     */
    public void setListaCmbAuxiliarFinal(
        RegistroDataModelImpl listaCmbAuxiliarFinal) {
        this.listaCmbAuxiliarFinal = listaCmbAuxiliarFinal;
    }

    /**
     * Retorna la lista listaCmbReferenciaInicial
     *
     * @return listaCmbReferenciaInicial
     */
    public RegistroDataModelImpl getListaCmbReferenciaInicial() {
        return listaCmbReferenciaInicial;
    }

    /**
     * Asigna la lista listaCmbReferenciaInicial
     *
     * @param listaCmbReferenciaInicial
     * Variable a asignar en listaCmbReferenciaInicial
     */
    public void setListaCmbReferenciaInicial(
        RegistroDataModelImpl listaCmbReferenciaInicial) {
        this.listaCmbReferenciaInicial = listaCmbReferenciaInicial;
    }

    /**
     * Retorna la lista listaCmbReferenciaFinal
     *
     * @return listaCmbReferenciaFinal
     */
    public RegistroDataModelImpl getListaCmbReferenciaFinal() {
        return listaCmbReferenciaFinal;
    }

    /**
     * Asigna la lista listaCmbReferenciaFinal
     *
     * @param listaCmbReferenciaFinal
     * Variable a asignar en listaCmbReferenciaFinal
     */
    public void setListaCmbReferenciaFinal(
        RegistroDataModelImpl listaCmbReferenciaFinal) {
        this.listaCmbReferenciaFinal = listaCmbReferenciaFinal;
    }

    /**
     * @return the listaCmbFteRecursoInicial
     */
    public RegistroDataModelImpl getListaCmbFteRecursoInicial() {
        return listaCmbFteRecursoInicial;
    }

    /**
     * @param listaCmbFteRecursoInicial
     * the listaCmbFteRecursoInicial to set
     */
    public void setListaCmbFteRecursoInicial(
        RegistroDataModelImpl listaCmbFteRecursoInicial) {
        this.listaCmbFteRecursoInicial = listaCmbFteRecursoInicial;
    }

    /**
     * @return the listaCmbFteRecursoFinal
     */
    public RegistroDataModelImpl getListaCmbFteRecursoFinal() {
        return listaCmbFteRecursoFinal;
    }

    /**
     * @param listaCmbFteRecursoFinal
     * the listaCmbFteRecursoFinal to set
     */
    public void setListaCmbFteRecursoFinal(
        RegistroDataModelImpl listaCmbFteRecursoFinal) {
        this.listaCmbFteRecursoFinal = listaCmbFteRecursoFinal;
    }

    public boolean isMayorizar() {
        return mayorizar;
    }

    public void setMayorizar(boolean mayorizar) {
        this.mayorizar = mayorizar;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

	public boolean isExcelPlano() {
		return excelPlano;
	}

	public void setExcelPlano(boolean excelPlano) {
		this.excelPlano = excelPlano;
	}

	public boolean isReferenciado() {
		return referenciado;
	}

	public void setReferenciado(boolean referenciado) {
		this.referenciado = referenciado;
	}

}
