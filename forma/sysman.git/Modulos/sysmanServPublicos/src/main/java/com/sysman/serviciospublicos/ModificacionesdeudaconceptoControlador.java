/*-
 * ModificacionesdeudaconceptoControlador.java
 *
 * 1.0
 *
 * 18/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ModificacionesdeudaconceptoControladorEnum;
import com.sysman.serviciospublicos.enums.ModificacionesdeudaconceptoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
 * Controlador del formulario ModificacionesdeudaconceptoControlador
 *
 * @version 1.0, 18/11/2016
 * @author cperez
 *
 * -- Modificado por lcortes 09/06/2017. Refactorizacion de codigo de
 * las listas para utilizar dss. Reemplazo de llamados a la clase
 * Acciones.
 *
 * @modified lcortes.
 * @version 2, 26/09/2017. Se ajusta el metodo para generar el reporte
 * para que valide si esta seleccionado el indicador por usuario y
 * determinar el informe a generar.
 *
 * -- Modificado por lcortes 02/10/2017. Se quitan los reemplazos que
 * se enviaban a la consulta 001266LModificacionesDeudaConcepto y que
 * no se tienen en cuenta en la consulta unificada
 * 001265LModificacionesDeudaConceptoCOS.
 */
@ManagedBean
@ViewScoped
public class ModificacionesdeudaconceptoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa a la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado del checkPorUsuario si es verdadero o falso
     * para poder mostar los combos de codigo inicial y el codigo
     * final
     */
    private boolean checkPorUsuario;
    /*
     * Cambia el estado de visible a none los combos usuario inicial y
     * usuario Final
     */
    private boolean checkPorUsuarioVisible;
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el ano Inicial para mandarlo a la consulta
     */
    private String anoInicial;
    /**
     * Obtiene el periodo Inicial para mandarlo a la consulta
     */
    private String periodoInicial;
    /**
     * Obtiene el ano Final para mandarlo a la consulta
     */
    private String anoFinal;
    /**
     * Obtiene el periodo Final para mandarlo a la consulta
     */
    private String periodoFinal;
    /**
     * Obtiene el codigo Inicial para mandarlo a la consulta
     */
    private String codigoInicial;
    /**
     * Obtiene el codigo Final para mandarlo a la consulta
     */
    private String codigoFinal;
    /**
     * Constante para el campo CODIGORUTA
     */
    private final String cCodigoRuta;
    /**
     * Constante para el campo "NUMERO"
     */
    private final String cNumero;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener mandar la lista del ciclo
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener mandar la lista del codigo Inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Necesario para obtener mandar la lista del codigo Final
     */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * ModificacionesdeudaconceptoControlador
     */
    public ModificacionesdeudaconceptoControlador() {
        super();
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.MODIFICACIONESDEUDACONCEPTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
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
        mensajesInicioModal();
        checkPorUsuario = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesdeudaconceptoControladorUrlEnum.URL6355
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    /**
     *
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesdeudaconceptoControladorUrlEnum.URL6964
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesdeudaconceptoControladorUrlEnum.URL7877
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ModificacionesdeudaconceptoControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * Para saber si el parametro FORMATO CALIDAD es igual a si o a no
     */
    private String consultarValorParametro() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String valorParametro = null;
        try {
            valorParametro = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CALIDAD", SessionUtil.getModulo(),
                            new Date(), true);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return valorParametro;
    }

    /*
     * muesta el mensaje de error del campo Banco
     */
    private boolean validarCamposVacios() {
        if (SysmanFunciones.validarVariableVacio(codigoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1975"));
            return false;
        }
        else if (SysmanFunciones.validarVariableVacio(codigoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1976"));
            return false;
        }
        return true;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    private void generarInforme(ReportesBean.FORMATOS formato) {
        if (!validarCamposVacios()) {
            return;
        }
        String reporte = "";
        try {

            if (checkPorUsuario) {
                reporte = "001266LModificacionesDeudaConcepto";

            }
            else {
                reporte = "001265LModificacionesDeudaConceptoCOS";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_VISIBLE",
                            "SI".equals(consultarValorParametro()) ? true
                                : false);

            Reporteador.resuelveConsulta(
                            "001265LModificacionesDeudaConceptoCOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (SysmanException | JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado al abrir el formulario, visualiza una alerta o
     * no
     */
    public void mensajesInicioModal() {

        try {
            if (ejbSysmanUtil.consultarParametro(compania, "FORMATO CALIDAD",
                            modulo, new Date(), true) == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2736"));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "0";
        codigoFinal = "99999999999999";
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control checkPorUsuario
     *
     */
    public void cambiarcheckPorUsuario() {
        // <CODIGO_DESARROLLADO>
        if (!checkPorUsuario) {
            checkPorUsuarioVisible = false;
        }
        else {
            checkPorUsuarioVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get(cNumero).toString();
        anoInicial = registroAux.getCampos().get("ANO").toString();
        anoFinal = registroAux.getCampos().get("ANO").toString();
        periodoInicial = registroAux.getCampos().get("PERIODO").toString();
        periodoFinal = registroAux.getCampos().get("PERIODO").toString();
        codigoInicial = "0";
        codigoFinal = "99999999999999";

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
    }

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
        codigoInicial = registroAux.getCampos().get(cCodigoRuta).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
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
        codigoFinal = registroAux.getCampos().get(cCodigoRuta).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkPorUsuario
     *
     * @return checkPorUsuario
     */
    public boolean getCheckPorUsuario() {
        return checkPorUsuario;
    }

    /**
     * Retorna la variable CheckPorUsuarioVisible
     *
     * @return CheckPorUsuarioVisible
     */
    public boolean isCheckPorUsuarioVisible() {
        return checkPorUsuarioVisible;
    }

    /**
     * Asigna la variable CheckPorUsuarioVisible
     *
     * @param CheckPorUsuarioVisible
     * Variable a asignar en CheckPorUsuarioVisible
     */
    public void setCheckPorUsuarioVisible(boolean checkPorUsuarioVisible) {
        this.checkPorUsuarioVisible = checkPorUsuarioVisible;
    }

    /**
     * Asigna la variable checkPorUsuario
     *
     * @param checkPorUsuario
     * Variable a asignar en checkPorUsuario
     */
    public void setCheckPorUsuario(boolean checkPorUsuario) {
        this.checkPorUsuario = checkPorUsuario;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

}
