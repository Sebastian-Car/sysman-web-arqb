/*-
 * FrmplanocriticasysmanControlador.java
 *
 * 1.0
 * 
 * 23/01/2017
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoRemote;
import com.sysman.serviciospublicos.enums.FrmplanocriticasysmanControladorEnum;
import com.sysman.serviciospublicos.enums.FrmplanocriticasysmanControladorUrlEnum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma FrmPlanoCriticaSysman asociado al
 * formulario "Archivo de Crítica Lectores Digitales Sysman".
 *
 * @version 1.0, 23/01/2017
 * @author yrojas
 * 
 * @version 2, 31/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 */

@ManagedBean
@ViewScoped
public class FrmplanocriticasysmanControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el campo CODIGORUTA de
     * las listas.
     */
    private final String codigorutaCons;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo asociado al check Suspendidos del formulario y que es
     * usado para agregar condiciones a la consulta que determina la
     * forma en la que se generan los archivos planos.
     */
    private boolean chkSuspendidos;

    /**
     * Atributo asociado al check Deshabitados del formulario y que es
     * usado para agregar condiciones a la consulta que determina la
     * forma en la que se generan los archivos planos.
     */
    private boolean chkDeshabitados;

    /**
     * Atributo asociado al check Chapetas del formulario y que es
     * usado para agregar condiciones a la consulta que determina la
     * forma en la que se generan los archivos planos.
     */
    private boolean chkChapetas;

    /**
     * Atributo asociado al valor del ciclo que se va a manejar en la
     * consulta del combo.
     */
    private String ciclo;

    /**
     * Atributo asociado al código del aforador.
     */
    private String aforador;

    /**
     * Atributo asociado al codigo inicial del formulario y que es
     * usado para la generacion del reporte correspondiente.
     */
    private String codigoInicial;

    /**
     * Atributo asociado al codigo final del formulario y que es usado
     * para la generacion del reporte correspondiente.
     */
    private String codigoFinal;

    /**
     * Atributo asociado al limite superior que se va a manejar en el
     * archivo plano.
     */
    private String limiteSuperior;

    /**
     * Atributo asociado al limite inferior que se va a manejar en el
     * archivo plano.
     */
    private String limiteInferior;

    /**
     * Atributo asociado a la cadena de observaciones ingresada en el
     * formulario.
     */
    private String observaciones;

    /**
     * Atributo asociado a la cadena de nombre de banco ingresada en
     * el formulario.
     */
    private String banco;

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
     * Lista que contiene la información de los detalles del combo
     * ciclo.
     */
    private List<Registro> listaCiclo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que contiene la información de los detalles del combo de
     * aforadores.
     */
    private RegistroDataModelImpl listaAforador;

    /**
     * Lista que contiene la información de los detalles del combo
     * código inicial.
     */
    private RegistroDataModelImpl listaCodInicial;

    /**
     * Lista que contiene la información de los detalles del combo
     * código final.
     */
    private RegistroDataModelImpl listaCodFinal;
    /**
     * 
     */
    private String tituloPaginaEmpresaParametrizada;
    /**
     * 
     */
    private String labelCodigoFinalEmpresaParametrizada;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbServiciosPublicosCincoRemote ejbServiciosPublicosCincoRemote;

    /**
     * Crea una nueva instancia de FrmplanocriticasysmanControlador
     */
    public FrmplanocriticasysmanControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigorutaCons = "CODIGORUTA";
        // codigo para implementacion de marca blanca 
        tituloPaginaEmpresaParametrizada = idioma.getString("EM_FR1266");
        tituloPaginaEmpresaParametrizada = tituloPaginaEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        
        labelCodigoFinalEmpresaParametrizada = idioma.getString("TT_LB33787");
        labelCodigoFinalEmpresaParametrizada = labelCodigoFinalEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        try {
            // 1266
            numFormulario = GeneralCodigoFormaEnum.FRMPLANOCRITICASYSMAN_CONTROLADOR
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
        cargarListaCiclo();
        cargarListaCodInicial();
        cargarListaCodFinal();
        cargarListaAforador();

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
        banco = "CUALQUIER BANCO AUTORIZADO";
        limiteSuperior = "100";
        limiteInferior = "0";
        chkChapetas = true;
        chkDeshabitados = true;
        chkSuspendidos = true;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmplanocriticasysmanControladorUrlEnum.URL7622
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAforador
     */
    public void cargarListaAforador() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmplanocriticasysmanControladorUrlEnum.URL5841
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }

    /**
     * Carga la lista listaCodInicial
     */
    public void cargarListaCodInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmplanocriticasysmanControladorUrlEnum.URL4783
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigorutaCons);

    }

    /**
     * Carga la lista listaCodFinal
     */
    public void cargarListaCodFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmplanocriticasysmanControladorUrlEnum.URL4721
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(FrmplanocriticasysmanControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigorutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo que genera el archivo comprimido que contiene los
     * archivos planos de parametros y de detalles. Además,
     * dependiendo de los valores de los check, concatena condiciones
     * para la consulta de los archivos planos.
     */
    public void generarReporte() {

        String parametros;
        String detalles;
        ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
        String[] nombres = new String[2];
        StringBuilder condicionAdicional = new StringBuilder();

        if (!chkSuspendidos) {
            condicionAdicional.append(
                            " AND SP_USUARIO.ESTADO NOT IN(''S'',''C'')");
        }

        if (!chkDeshabitados) {
            condicionAdicional.append(" AND SP_USUARIO.INDDESHABITADO = 0 ");
        }

        if (!chkChapetas) {
            condicionAdicional.append(" AND SP_USUARIO.CHAPETAS = 0");
        }

        try {
            parametros = ejbServiciosPublicosCincoRemote
                            .armarParametrosPlanoFacSitio(compania,
                                            codigoInicial, codigoFinal,
                                            Integer.parseInt(ciclo),
                                            observaciones, banco);

            detalles = ejbServiciosPublicosCincoRemote
                            .armarDetallesPlanoFacSitio(compania, codigoInicial,
                                            codigoFinal,
                                            Integer.parseInt(ciclo), new Date(),
                                            aforador, 1, " ",
                                            condicionAdicional.toString(),
                                            Integer.parseInt(limiteInferior),
                                            Integer.parseInt(limiteSuperior),
                                            SessionUtil.getUser().getCodigo());

            salidas[0] = JsfUtil.serializarPlano(parametros);
            nombres[0] = "PLANOPARAMETROS.DAT";
            salidas[1] = JsfUtil.serializarPlano(detalles);
            nombres[1] = "PLANO.DAT";
            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombres, "ARCHIVOSPLANOS");
        }
        catch (JRException | IOException
                        | DRException | NumberFormatException | SystemException
                        | SQLException e) {
            Logger.getLogger(FrmplanocriticasysmanControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton GenerarArchivo en la
     * vista.
     */
    public void oprimirGenerarArchivo() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";

        cargarListaCodInicial();
        cargarListaCodFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAforador
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAforador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        aforador = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigorutaCons).toString();
        codigoFinal = "";
        cargarListaCodFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigorutaCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable chkSuspendidos
     * 
     * @return chkSuspendidos
     */
    public boolean getChkSuspendidos() {
        return chkSuspendidos;
    }

    /**
     * Asigna la variable chkSuspendidos
     * 
     * @param chkSuspendidos
     * Variable a asignar en chkSuspendidos
     */
    public void setChkSuspendidos(boolean chkSuspendidos) {
        this.chkSuspendidos = chkSuspendidos;
    }

    /**
     * Retorna la variable chkDeshabitados
     * 
     * @return chkDeshabitados
     */
    public boolean getChkDeshabitados() {
        return chkDeshabitados;
    }

    /**
     * Asigna la variable chkDeshabitados
     * 
     * @param chkDeshabitados
     * Variable a asignar en chkDeshabitados
     */
    public void setChkDeshabitados(boolean chkDeshabitados) {
        this.chkDeshabitados = chkDeshabitados;
    }

    /**
     * Retorna la variable chkChapetas
     * 
     * @return chkChapetas
     */
    public boolean getChkChapetas() {
        return chkChapetas;
    }

    /**
     * Asigna la variable chkChapetas
     * 
     * @param chkChapetas
     * Variable a asignar en chkChapetas
     */
    public void setChkChapetas(boolean chkChapetas) {
        this.chkChapetas = chkChapetas;
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
     * Retorna la variable aforador
     * 
     * @return aforador
     */
    public String getAforador() {
        return aforador;
    }

    /**
     * Asigna la variable aforador
     * 
     * @param aforador
     * Variable a asignar en aforador
     */
    public void setAforador(String aforador) {
        this.aforador = aforador;
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
     * Retorna la variable limiteSuperior
     * 
     * @return limiteSuperior
     */
    public String getLimiteSuperior() {
        return limiteSuperior;
    }

    /**
     * Asigna la variable limiteSuperior
     * 
     * @param limiteSuperior
     * Variable a asignar en limiteSuperior
     */
    public void setLimiteSuperior(String limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Retorna la variable limiteInferior
     * 
     * @return limiteInferior
     */
    public String getLimiteInferior() {
        return limiteInferior;
    }

    /**
     * Asigna la variable limiteInferior
     * 
     * @param limiteInferior
     * Variable a asignar en limiteInferior
     */
    public void setLimiteInferior(String limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    /**
     * Retorna la variable observaciones
     * 
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     * 
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable banco
     * 
     * @return banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     * 
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaAforador() {
        return listaAforador;
    }

    public void setListaAforador(RegistroDataModelImpl listaAforador) {
        this.listaAforador = listaAforador;
    }

    public RegistroDataModelImpl getListaCodInicial() {
        return listaCodInicial;
    }

    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial) {
        this.listaCodInicial = listaCodInicial;
    }

    public RegistroDataModelImpl getListaCodFinal() {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal) {
        this.listaCodFinal = listaCodFinal;
    }

	public String getTituloPaginaEmpresaParametrizada() {
		return tituloPaginaEmpresaParametrizada;
	}

	public void setTituloPaginaEmpresaParametrizada(String tituloPaginaEmpresaParametrizada) {
		this.tituloPaginaEmpresaParametrizada = tituloPaginaEmpresaParametrizada;
	}

	public String getLabelCodigoFinalEmpresaParametrizada() {
		return labelCodigoFinalEmpresaParametrizada;
	}

	public void setLabelCodigoFinalEmpresaParametrizada(String labelCodigoFinalEmpresaParametrizada) {
		this.labelCodigoFinalEmpresaParametrizada = labelCodigoFinalEmpresaParametrizada;
	}

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
