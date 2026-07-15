/*-
 * FrmInfPlanIndicativoDnpControlador.java
 *
 * 1.0
 * 
 * 21/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloTresRemote;
import com.sysman.plandesarrollo.enums.FrmInfPlanIndicativoDnpControladorEnum;
import com.sysman.plandesarrollo.enums.FrmInfPlanIndicativoDnpControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Migración del fromulario en access FRM_INF_PLAINDICATIVO_DNP a web
 * con el controlador FrmInfPlanIndicativoDnpControlador, forma
 * frminfplanindicativodnp.xhtml, creacion de properties y la
 * generación del archivo desplegable en excel apartir de un botón.
 * 
 * @version 1.0, 21/03/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped

public class FrmInfPlanIndicativoDnpControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    /**
     * Variable que permite acceder a los procedimientos del paquete:
     * <code>PCK_PLAN_DESARROLLO3</code>.
     */

    @EJB
    private EjbPlanDesarrolloTresRemote ejbPlanDesarrolloTresRemote;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * 
     */

    private final String compania;

    private final String modulo = SessionUtil.getModulo();

    private String vigenciaInicial;

    private String vigenciaFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaVigencia;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmInfPlanIndicativoDnpControlador
     */
    public FrmInfPlanIndicativoDnpControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INF_PLAN_IONDICATIVO_DNP_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaVigencia();
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
     * Carga la lista listaVigencia
     *
     *
     */

    public void cargarListaVigencia() {

        // </METODOS_CARGAR_LISTA>

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfPlanIndicativoDnpControladorUrlEnum.URL5320
                                                        .getValue());

        listaVigencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "LLAVE");

    }

    // <METODOS_BOTONES>
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
        descargarArchivoExcel();
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado al oprimir el boton excel (BT3057) en la
     * vista.
     * 
     */

    private void descargarArchivoExcel() {
        int vigInicial = Integer.parseInt(vigenciaInicial);
        int vigFinal = Integer.parseInt(vigenciaFinal);

        if ((vigFinal - vigInicial) >= 4) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4093"));
            return;

        }
        else if (vigInicial <= vigFinal) {

            String datosExcel;

            try {

                ejbPlanDesarrolloTresRemote.prepararInfPlanIndicador(
                                compania,
                                Integer.parseInt(vigenciaInicial),
                                Integer.parseInt(vigenciaFinal));

                Map<String, Object> param = new TreeMap<>();
                param.put(FrmInfPlanIndicativoDnpControladorEnum.compania
                                .getValue(),
                                SysmanFunciones.concatenar("'", compania,
                                                "'"));
                param.put(FrmInfPlanIndicativoDnpControladorEnum.vigencia
                                .getValue(), vigenciaInicial);

                datosExcel = Reporteador.resuelveConsulta(
                                "800139INF_PLAN_INDICATIVO_DNP",
                                Integer.parseInt(modulo), param);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                datosExcel,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL97);

            }
            catch (SystemException | JRException | IOException
                            | SQLException
                            | DRException | SysmanException e) {

                // Auto-generated catch block
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4094"));
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaVigencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaVigencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        vigenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfPlanIndicativoDnpControladorEnum.VIGENCIA_INICIAL
                                                        .getValue()),
                                        "")
                        .toString();

        vigenciaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfPlanIndicativoDnpControladorEnum.VIGENCIA_FINAL
                                                        .getValue()),
                                        "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigenciaInicial
     * 
     * @return vigenciaInicial
     */
    public String getVigenciaInicial() {
        return vigenciaInicial;
    }

    /**
     * Asigna la variable vigenciaInicial
     * 
     * @param vigenciaInicial
     * Variable a asignar en vigenciaInicial
     */
    public void setVigenciaInicial(String vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    /**
     * Retorna la variable vigenciaFinal
     * 
     * @return vigenciaFinal
     */
    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    /**
     * Asigna la variable vigenciaFinal
     * 
     * @param vigenciaFinal
     * Variable a asignar en vigenciaFinal
     */
    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public RegistroDataModelImpl getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(RegistroDataModelImpl listaVigencia) {
        this.listaVigencia = listaVigencia;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
