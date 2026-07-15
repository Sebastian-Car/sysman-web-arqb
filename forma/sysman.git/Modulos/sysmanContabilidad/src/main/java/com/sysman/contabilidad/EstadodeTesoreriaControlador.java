/*-
 * EstadodeTesoreriaControlador.java
 *
 * 1.0
 * 
 * 13/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EstadodeTesoreriaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 13/02/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class EstadodeTesoreriaControlador extends BeanBaseModal {
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private String conFirmas;
    // <DECLARAR_ATRIBUTOS>

    private String codigoInicial;
    private String codigoFinal;
    private int anio;
    private String mesInicial;
    private String mesFinal;
    private String numeroDigitos;
    private boolean manejaCheckParaFirma;
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
    
     */
    private List<Registro> listaAnoInicial;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
    
     */
    private RegistroDataModelImpl listaCodigoInicial;

    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EstadodeTesoreriaControlador
     */
    public EstadodeTesoreriaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        anio = SysmanFunciones.ano(new Date());

        numeroDigitos = "16";
        mesFinal = "12";
        mesInicial = "1";
        codigoInicial = "11";
        codigoFinal = "13";
        manejaCheckParaFirma = false;
        try {
            numFormulario = 2033;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        cargarListaAnoInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
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
    	try {
			manejaCheckParaFirma = "SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,
							"MANEJA CHECK PARA FIRMAS",
							SessionUtil.getModulo(), new Date(),true), "NO"));
    	}catch (SystemException e) {
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoInicial
     *
     * 
     */
    public void cargarListaAnoInicial() {

        try {

            Map<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadodeTesoreriaControladorUrlEnum.URL4013
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     * 
     */
    public void cargarListaCodigoInicial() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodeTesoreriaControladorUrlEnum.URL16016
                                                        .getValue());

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     * 
     */
    public void cargarListaCodigoFinal() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CUENTAINICIAL", codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadodeTesoreriaControladorUrlEnum.URL16018
                                                        .getValue());

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     * 
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     * 
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        int mesI = Integer.parseInt(mesInicial) - 1;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("mesinicial", mesInicial);
            reemplazar.put("mesfinal", mesFinal);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroDigitos", numeroDigitos);
            reemplazar.put("mesI", mesI);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_ESTADODETESORERIA_MESINICIAL",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesInicial)]);
            parametros.put("PR_FORMS_ESTADODETESORERIA_MESFINAL",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesFinal)]);

            parametros.put("PR_FORMS_ESTADODETESORERIA_ANOINICIAL", anio);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_DIRECCIONCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_TELEFONOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getTelefono());
            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDepartamento());
            parametros.put("PR_FIRMAS",
                    "true".equals(conFirmas) ? true : false);
            parametros.put("PR_CARGO_TESORERO", SysmanFunciones
                    .nvl(ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO TESORERO",
                                    SessionUtil.getModulo(), new Date(),
                                    false), "")
                    .toString());

            parametros.put("PR_NOMBRE_TESORERO", SysmanFunciones
                    .nvl(ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE TESORERO",
                                    SessionUtil.getModulo(), new Date(),
                                    false), "")
                    .toString());

            String reporte = "001992InfdeContraloriaRes109I7";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     * 
     * 
     * 
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        mesInicial = null;
        mesFinal = null;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get("CODIGO").toString();

        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get("CODIGO").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable conFirmas
     *
     * @return  conFirmas
     */
public String getConFirmas() {
        return conFirmas;
    }
    
    /**
    // </METODOS_COMBOS_GRANDES>
     * Asigna la variable  conFirmas
    // <METODOS_ARBOL>
     *
    // </METODOS_ARBOL>
     * @param  conFirmas
    // <SET_GET_ATRIBUTOS>
     * Variable a asignar en  conFirmas
     */
    public void setConFirmas(String conFirmas) {
        this.conFirmas = conFirmas;
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
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
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
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    /**
     * @return the numeroDigitos
     */
    public String getNumeroDigitos() {
        return numeroDigitos;
    }

    /**
     * @param numeroDigitos
     * the numeroDigitos to set
     */
    public void setNumeroDigitos(String numeroDigitos) {
        this.numeroDigitos = numeroDigitos;
    }

	public boolean isManejaCheckParaFirma() {
		return manejaCheckParaFirma;
	}

	public void setManejaCheckParaFirma(boolean manejaCheckParaFirma) {
		this.manejaCheckParaFirma = manejaCheckParaFirma;
	}

    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
