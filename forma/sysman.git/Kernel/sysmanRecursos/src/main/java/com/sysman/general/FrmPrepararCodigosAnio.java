/*-
 * FrmPrepararCodigosAnio.java
 *
 * 1.0
 * 
 * 03/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.circularunica.ejb.EjbCircularUnicaCeroGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmPrepararCodigosAnioControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/07/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class FrmPrepararCodigosAnio extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;

    /**
     * Atributo que almacena el año seleccionado del combo Año inicial
     */
    private String anoIni;

    private String opcion;

    /**
     * Atributo que almacena el año seleccionado del combo Año final
     */
    private String anoFin;

    private boolean bloqueado;

    private String tituloLabel;

    private String tituloModal;

    private String tituloBtn;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbCircularUnicaCeroGeneralRemote ejbCircularUnicaCeroGeneralRemote;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * variable que almacena la lista listaAnoIni
     */
    private List<Registro> listaAnoIni;
    /**
     * variable que almacena la lista listaAnoFin
     */
    private List<Registro> listaAnoFin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPrepararCodigosAnio
     */
    public FrmPrepararCodigosAnio() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        bloqueado = false;
        parametrosEntrada = SessionUtil.getFlash();

        try {
            // 1843
            numFormulario = GeneralCodigoFormaEnum.FRM_PREPARAR_CODIGOS_ANIO
                            .getCodigo();

            validarPermisos();

            if (parametrosEntrada != null) {
                if ("99040101".equals(SessionUtil.getMenuActual())
                    || "99030103".equals(SessionUtil.getMenuActual())) {
                    opcion = parametrosEntrada.get("opcion").toString();
                }

                anoIni = parametrosEntrada.get("anioInicial")
                                .toString();

            }
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
        cargarListaAnoIni();
        cargarListaAnoFin();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoIni
     */
    public void cargarListaAnoIni() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));

            if ("99040101".equals(SessionUtil.getMenuActual())
                || "99030103".equals(SessionUtil.getMenuActual())) {
                tituloModal = idioma.getString("TB_TB4151");
                tituloLabel = idioma.getString("TB_TB4151").toUpperCase();
                tituloBtn = idioma.getString("TG_PREPARAR");
                listaAnoIni = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmPrepararCodigosAnioControladorUrlEnum.URL001
                                                                                .getValue())
                                                .getUrl(), param));
            }
            else {
                tituloModal = idioma.getString("TB_TB4152");
                tituloLabel = idioma.getString("TB_TB4152").toUpperCase();
                tituloBtn = idioma.getString("TG_ACTUALIZAR");
                listaAnoIni = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmPrepararCodigosAnioControladorUrlEnum.URL003
                                                                                .getValue())
                                                .getUrl(), param));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnoFin
     */
    public void cargarListaAnoFin() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));
            param.put(GeneralParameterEnum.ANO.getName(), anoIni);

            listaAnoFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPrepararCodigosAnioControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPreparar en la vista
     *
     */
    public void oprimirBtnPreparar() {
        archivoDescarga = null;
        try {

            if ("99040101".equals(SessionUtil.getMenuActual())
                || "99030103".equals(SessionUtil.getMenuActual())) {
                ejbCircularUnicaCeroGeneralRemote.prepararCodigos(compania,
                                Integer.parseInt(anoIni),
                                Integer.parseInt(anoFin),
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(opcion));
                ejecutarRcCircularUnica();
            }

            else {
                archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                ejbCircularUnicaCeroGeneralRemote
                                                                .actualizarVigenciaPlanPptal(
                                                                                compania,
                                                                                Integer.parseInt(
                                                                                                anoIni),
                                                                                Integer.parseInt(
                                                                                                anoFin),
                                                                                SessionUtil.getUser()
                                                                                                .getCodigo()
                                                                                                .toString())),
                                                "Inconsistencia Configuraci�n Presupuestal.txt");
            }
        }

        catch (NumberFormatException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnCancelar en la vista
     *
     */
    public void oprimirBtnCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto RcCircularUnica
     * en la vista
     *
     */
    public void ejecutarRcCircularUnica() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4144"));
        return;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    // <SET_GET_ATRIBUTOS>

    /**
     * @return the bloqueado
     */
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * @param bloqueado
     * the bloqueado to set
     */
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    /**
     * @return the anoIni
     */
    public String getAnoIni() {
        return anoIni;
    }

    /**
     * @param anoIni
     * the anoIni to set
     */
    public void setAnoIni(String anoIni) {
        this.anoIni = anoIni;
    }

    /**
     * @return the anoFin
     */
    public String getAnoFin() {
        return anoFin;
    }

    /**
     * @param anoFin
     * the anoFin to set
     */
    public void setAnoFin(String anoFin) {
        this.anoFin = anoFin;
    }

    /**
     * @return the titulo
     */
    public String getTituloLabel() {
        return tituloLabel;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTituloLabel(String titulo) {
        this.tituloLabel = titulo;
    }

    /**
     * @return the tituloModal
     */
    public String getTituloModal() {
        return tituloModal;
    }

    /**
     * @param tituloModal
     * the tituloModal to set
     */
    public void setTituloModal(String tituloModal) {
        this.tituloModal = tituloModal;
    }

    /**
     * @return the tituloBtn
     */
    public String getTituloBtn() {
        return tituloBtn;
    }

    /**
     * @param tituloBtn
     * the tituloBtn to set
     */
    public void setTituloBtn(String tituloBtn) {
        this.tituloBtn = tituloBtn;
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
     * Retorna la lista listaAnoIni
     * 
     * @return listaAnoIni
     */
    public List<Registro> getListaAnoIni() {
        return listaAnoIni;
    }

    /**
     * Asigna la lista listaAnoIni
     * 
     * @param listaAnoIni
     * Variable a asignar en listaAnoIni
     */
    public void setListaAnoIni(List<Registro> listaAnoIni) {
        this.listaAnoIni = listaAnoIni;
    }

    /**
     * Retorna la lista listaAnoFin
     * 
     * @return listaAnoFin
     */
    public List<Registro> getListaAnoFin() {
        return listaAnoFin;
    }

    /**
     * Asigna la lista listaAnoFin
     * 
     * @param listaAnoFin
     * Variable a asignar en listaAnoFin
     */
    public void setListaAnoFin(List<Registro> listaAnoFin) {
        this.listaAnoFin = listaAnoFin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
