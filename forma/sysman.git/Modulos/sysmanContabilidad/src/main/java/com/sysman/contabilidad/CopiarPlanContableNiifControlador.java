/*-
 * CopiarPlanContableNiifControlador.java
 *
 * 1.0
 *
 * 23/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.CopiarPlanContableNiifControladorEnum;
import com.sysman.contabilidad.enums.CopiarPlanContableNiifControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Esta clase es el controlador para el formulario Copiar Plan
 * Contable NIIF en Access "FRM_Copiarplancontableniif", el cual es
 * llamado desde
 * Contabilidad\Mantenimiento\UtilidadesMGC\Archivo\Copiar plan NIIF
 *
 * @version 1.0, 23/11/2016
 * @author amonroy
 * @modified jsforero
 * @version 2. 11/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 */
@ManagedBean
@ViewScoped
public class CopiarPlanContableNiifControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el modulo en el que se esta trabajando
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la compania equivalente niif
     */
    private String companianiif;
    /**
     * Atributo que almacena el anio fuente seleccionado en el
     * comboBox anoFuente
     */
    private String anioFuente;
    /**
     * Atributo que almacena el anio destino seleccionado en el
     * comboBox anoDestino
     */
    private String anioDestino;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de anoFuente
     */
    private List<Registro> listaAnofuente;
    /**
     * Listado de registros para el comboBox de anoDestino
     */
    private List<Registro> listaAnodestino;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadTresRemote contabilidadTres;

    /**
     * Crea una nueva instancia de CopiarPlanContableNiifControlador
     */
    public CopiarPlanContableNiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.COPIAR_PLAN_CONTABLE_NIIF_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            anioFuente = String
                            .valueOf(Calendar.getInstance().get(Calendar.YEAR)
                                - 1);
            anioDestino = String.valueOf(
                            Calendar.getInstance().get(Calendar.YEAR));
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

        try {
            companianiif = ejbSysmanUtil.consultarParametro(
                            compania, idioma.getString("TB_TB2200"), modulo,
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((companianiif == null) || companianiif.isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2201"));
            return;
        }
        cargarListaAnofuente();
        cargarListaAnodestino();
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

    /**
     *
     * Carga la lista listaAnofuente
     */
    public void cargarListaAnofuente() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            listaAnofuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarPlanContableNiifControladorUrlEnum.URL5120
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
     * Carga la lista listaAnodestino
     */
    public void cargarListaAnodestino() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), companianiif);
        param.put(GeneralParameterEnum.ANO.name(), anioFuente);

        try {
            listaAnodestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarPlanContableNiifControladorUrlEnum.URL5470
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
     * Evalua si en la Compania Niif ya ha sido configurado el plan
     * contable para el anio Destino
     * 
     * @return Verdadero si ya tiene cuentas creadas en ese anio
     */
    private boolean validarPlanConfigurado() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), companianiif);
        parametros.put(GeneralParameterEnum.ANO.getName(), anioDestino);
        Registro existe = null;
        boolean planConfigurado = false;

        UrlBean urlReg = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CopiarPlanContableNiifControladorUrlEnum.URL5422
                                                        .getValue());
        try {
            existe = RegistroConverter.toRegistro(
                            requestManager.get(urlReg.getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((existe != null)
            && !"0".equals(existe.getCampos().get("NUMERO").toString())) {
            String msj = idioma.getString("TB_TB2197");
            msj = msj.replace("s$anioDestino$s", anioDestino);
            JsfUtil.agregarMensajeError(msj);
            planConfigurado = true;
        }
        return planConfigurado;
    }

    /**
     * Verifica que el anio destino este configurado para la compania
     * 
     * @return Verdadero si el anio esta creado
     */
    private boolean validarAnio() {
        boolean anoCreado = false;
        Registro existe;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), companianiif);
        parametros.put(CopiarPlanContableNiifControladorEnum.PARAM0
                        .getValue(), anioDestino);

        UrlBean urlano = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CopiarPlanContableNiifControladorUrlEnum.URL5423
                                                        .getValue());
        try {
            existe = RegistroConverter.toRegistro(
                            requestManager.get(urlano.getUrl(), parametros));

            if ((existe != null)
                && !"0".equals(existe.getCampos().get("NUMERO").toString())) {
                anoCreado = true;
            }
            else {
                String msj1 = idioma.getString("TB_TB2198");
                msj1 = msj1.replace("s$anioDestino$s", anioDestino);
                msj1 = msj1.replace("s$companiaNiif$s", companianiif);
                JsfUtil.agregarMensajeError(msj1);
                anoCreado = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return anoCreado;
    }

    /**
     * Evalua si hay cuentas con "Centro de Costo Relacionado"
     * configurado para alguna cuenta en la compania BASE en el anio
     * ORIGEN
     * 
     * @return Verdadero cuando existen cuentas con centro de costo
     * asociado
     */
    private boolean validarCuentasConCentro() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), anioFuente);
        Registro cuentasConCentro;
        boolean conCentro = false;

        UrlBean urlReg = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CopiarPlanContableNiifControladorUrlEnum.URL001
                                                        .getValue());
        try {
            cuentasConCentro = RegistroConverter.toRegistro(
                            requestManager.get(urlReg.getUrl(), parametros));
            if ((cuentasConCentro != null)
                && !"0".equals(cuentasConCentro.getCampos().get("CUENTAS")
                                .toString())) {
                conCentro = true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return conCentro;
    }

    /**
     * Evalua si existen Centros de Costo configurados en la compania
     * NIIF
     */
    private boolean validarCentrosEnNiif() {
        boolean centrosEnNiif = true;
        boolean conCentro = validarCuentasConCentro();
        Map<String, Object> parametros = new HashMap<>();
        if (conCentro) {
            Registro centrosNiif;
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                            companianiif);
            parametros.put(GeneralParameterEnum.ANO.getName(), anioDestino);

            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CopiarPlanContableNiifControladorUrlEnum.URL002
                                                            .getValue());
            try {
                centrosNiif = RegistroConverter.toRegistro(requestManager
                                .get(urlReg.getUrl(), parametros));
                if ((centrosNiif != null)
                    && "0".equals(centrosNiif.getCampos().get("CENTROS")
                                    .toString())) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3240"));
                    centrosEnNiif = false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        return centrosEnNiif;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        try {
            if (!validarPlanConfigurado() && validarAnio()
                && validarCentrosEnNiif()) {

                boolean resultado = contabilidadTres
                                .insertarCuentaContableEnNiif(companianiif,
                                                compania,
                                                Integer.parseInt(anioFuente),
                                                Integer.parseInt(anioDestino),
                                                "",
                                                SessionUtil.getUser()
                                                                .getCodigo());

                if (resultado) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB874"));
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>

    /**
     * Se actualiza el listado del anio destino al cambiar el anio
     * fuente
     */
    public void cambiarAnofuente() {
        anioDestino = null;
        cargarListaAnodestino();
    }

    public void cambiarAnodestino() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna la variable anioFuente
     *
     * @return anioFuente
     */
    public String getAnioFuente() {
        return anioFuente;
    }

    /**
     * Asigna la variable anioFuente
     *
     * @param anioFuente
     * Variable a asignar en anioFuente
     */
    public void setAnioFuente(String anioFuente) {
        this.anioFuente = anioFuente;
    }

    /**
     * Retorna la variable anioDestino
     *
     * @return anioDestino
     */
    public String getAnioDestino() {
        return anioDestino;
    }

    /**
     * Asigna la variable anioDestino
     *
     * @param anioDestino
     * Variable a asignar en anioDestino
     */
    public void setAnioDestino(String anioDestino) {
        this.anioDestino = anioDestino;
    }

    /**
     * Retorna la variable companianiif
     *
     * @return
     */
    public String getCompanianiif() {
        return companianiif;
    }

    /**
     * Asigna la variable companianiif
     *
     * @param companianiif
     * Variable a asignar companianiif
     */
    public void setCompanianiif(String companianiif) {
        this.companianiif = companianiif;
    }

    /**
     * Retorna la lista listaAnofuente
     *
     * @return listaAnofuente
     */
    public List<Registro> getListaAnofuente() {
        return listaAnofuente;
    }

    /**
     * Asigna la lista listaAnofuente
     *
     * @param listaAnofuente
     * Variable a asignar en listaAnofuente
     */
    public void setListaAnofuente(List<Registro> listaAnofuente) {
        this.listaAnofuente = listaAnofuente;
    }

    /**
     * Retorna la lista listaAnodestino
     *
     * @return listaAnodestino
     */
    public List<Registro> getListaAnodestino() {
        return listaAnodestino;
    }

    /**
     * Asigna la lista listaAnodestino
     *
     * @param listaAnodestino
     * Variable a asignar en listaAnodestino
     */
    public void setListaAnodestino(List<Registro> listaAnodestino) {
        this.listaAnodestino = listaAnodestino;
    }
}
