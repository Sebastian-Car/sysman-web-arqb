/*-
 * FrmCrearProgaProyectosControlador.java
 *
 * 1.0
 * 
 * 05/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.FrmCrearProgaProyectosControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Calse encargada de crear la programacion a proyectos
 *
 * @version 1.0, 05/03/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class FrmCrearProgaProyectosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar el codigo del proyecto
     * seleccionado en la forma.
     */
    private String proyecto;
    /**
     * Variable encargada de almacenar la vigencia selecciada en la
     * forma
     */
    private String ano;

    /**
     * 
     */
    private String nombreProyecto;

    /**
     * 
     */
    private String vigenciaFinal;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista encargada de almacenar los datos de respuesta de la base
     * de datos
     */

    private RegistroDataModelImpl listaproyecto;
    /**
     * Lista encargada de almacenar las vigencias de respuesta a la
     * base de datos
     */
    private List<Registro> listavigencia;
    /**
     * Ejb encargado de ejecutar la funcion que crea la programacion
     */
    @EJB
    private EjbBancoProyectoCincoRemote ejbBanCoProyCin;

    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCrearProgaProyectosControlador
     */
    public FrmCrearProgaProyectosControlador() {
        super();
        compania = SessionUtil.getCompania();

        ano = "TODAS";
        proyecto = "TODOS";

        try {
            // 1733
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            numFormulario = GeneralCodigoFormaEnum.FRMCREARPROGAPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
			if (parametrosEntrada != null) {
				//ano = parametrosEntrada.get("anoIni").toString();
				proyecto = parametrosEntrada.get("numeroProyecto").toString();
				nombreProyecto = parametrosEntrada.get("nombreProyecto").toString();
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
        cargarListaproyecto();
        cargarListavigencia();
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
     * Carga la lista listaproyecto
     *
     */
    public void cargarListaproyecto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCrearProgaProyectosControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), ano);

        listaproyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 32043 VIGENCIA
    }

    /**
     * 
     * Carga la lista listavigencia
     *
     */
    public void cargarListavigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listavigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCrearProgaProyectosControladorUrlEnum.URL17434
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
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        try {
            Long rta = ejbBanCoProyCin.crearMante(compania, ano, proyecto, SessionUtil.getUser().getCodigo());

            if (rta > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4011"));

            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4012"));

                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2421")
                                .replace("#$nombreProyecto#$", proyecto)
                                .replace("#$nombreComponente#$", nombreProyecto));

            }

            // ejbBancoProyectoCero.actualizarProgramado(compania,
            // Integer.parseInt(ano),
            // proyecto, proyecto, 1,
            // SessionUtil.getUser().getCodigo());
            //
            //
            // JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2421")
            // .replace("#$nombreProyecto#$", proyecto)
            // .replace("#$nombreComponente#$", proyecto));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control vigencia
     * 
     * 
     */
    public void cambiarvigencia() {
        // <CODIGO_DESARROLLADO>
        proyecto = null;
        cargarListaproyecto();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaproyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreProyecto = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREPROYECTO"), 0).toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proyecto
     * 
     * @return proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * Asigna la variable proyecto
     * 
     * @param proyecto
     * Variable a asignar en proyecto
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaproyecto
     * 
     * @return listaproyecto
     */

    /**
     * Retorna la lista listavigencia
     * 
     * @return listavigencia
     */
    public List<Registro> getListavigencia() {
        return listavigencia;
    }

    public RegistroDataModelImpl getListaproyecto() {
        return listaproyecto;
    }

    public void setListaproyecto(RegistroDataModelImpl listaproyecto) {
        this.listaproyecto = listaproyecto;
    }

    /**
     * Asigna la lista listavigencia
     * 
     * @param listavigencia
     * Variable a asignar en listavigencia
     */
    public void setListavigencia(List<Registro> listavigencia) {
        this.listavigencia = listavigencia;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    public String getVigenciaInicial() {
        return nombreProyecto;
    }

    public void setVigenciaInicial(String vigenciaInicial) {
        this.nombreProyecto = vigenciaInicial;
    }

    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

}
