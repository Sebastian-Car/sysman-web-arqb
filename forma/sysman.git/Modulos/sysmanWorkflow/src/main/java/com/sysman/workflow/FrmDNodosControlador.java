/*-
 * FrmDNodosControlador.java
 *
 * 1.0
 * 
 * 12/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.FrmDNodosControladorEnum;
import com.sysman.workflow.enums.FrmDNodosControladorUrlEnum;
import com.sysman.workflow.enums.FrmNodosControladorEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma: <code>frmdnodos</code>, encargada de
 * conectar los nodos asociados a un proceso.
 *
 * @version 1.0, 12/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmDNodosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual se accede al formulario.
     */
    private final String modulo = SessionUtil.getModulo();

    /** Constante a nivel de clase que aloja la cadena: COMPANIA. */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /** Constante a nivel de clase que aloja la cadena: ESTADO. */
    private final String cEstado = GeneralParameterEnum.ESTADO.getName();

    /** Constante a nivel de clase que aloja la cadena: PROCESO. */
    private final String cProceso = FrmDNodosControladorEnum.PROCESO.getValue();

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene el codigo del proceso. */
    private String codigoProceso;

    /**
     * Coleccion que permite identificar la llave del proceso desde el
     * cual se accede a este formulario.
     */
    private Map<String, Object> ridForm;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo Estado (CB5856). */
    private List<Registro> listaEstado;

    /**
     * Lista que contiene los detalles del combo Nodo Destino
     * (CB5857).
     */
    private List<Registro> listaNodoDestino;

    /**
     * Lista que contiene los detalles del combo Nodo Origen (CB5858).
     */
    private List<Registro> listaNodoOrigen;

    /** Lista que contiene los detalles del combo Proceso (CB5859). */
    private List<Registro> listaProceso;

    /**
     * Lista que contiene los detalles del combo Operacion (CB5996).
     */
    private List<Registro> listaOperacion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmDNodosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmDNodosControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1759
            numFormulario = GeneralCodigoFormaEnum.FRM_D_NODOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                codigoProceso = paramIn.get(
                                FrmDNodosControladorEnum.PR_PROCESO.getValue())
                                .toString();

                ridForm = (Map<String, Object>) paramIn.get(
                                FrmDNodosControladorEnum.PR_RID.getValue());
            }
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
        enumBase = GenericUrlEnum.D_NODOS;
        registro = new Registro();

        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        cargarListaEstado();
        cargarListaProceso();
        cargarListaOperacion();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put("PROCESO", codigoProceso);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista:<code>listaEstado</code> asociada al combo
     * Estado (CB5856).
     */
    public void cargarListaEstado() {
        Map<String, Object> param = new TreeMap<>();
        param.put("CATEGORIA", 4);

        try {
            listaEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDNodosControladorUrlEnum.URL3984
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaNodoDestino</code> asociada al combo
     * Etapa Destino (CB5857).
     */
    public void cargarListaNodoDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cProceso, codigoProceso);
        param.put(cEstado, 4); // Estado Activo

        try {
            listaNodoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDNodosControladorUrlEnum.URL4334
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaNodoOrigen</code> asociada al combo
     * Etapa Origen (CB5858).
     */
    public void cargarListaNodoOrigen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cProceso, codigoProceso);
        param.put(cEstado, 4); // Estado Activo

        registro.getCampos();

        try {
            listaNodoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDNodosControladorUrlEnum.URL4334
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaProceso</code> asociada al combo
     * Proceso (CB5859).
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDNodosControladorUrlEnum.URL5169
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaOperacion</code> asociada al combo
     * Operacion (CB5996).
     */
    public void cargarListaOperacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put("CATEGORIA", 3); // Tipos de operacion

        try {
            listaOperacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDNodosControladorUrlEnum.URL3984
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
     * Metodo ejecutado al oprimir el boton nodoDispara
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirnodoDispara(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
    	 Map<String, Object> param = new HashMap<>();
         param.put("proceso",codigoProceso);
         param.put("nodoOrigen", reg.getCampos().get("NODO_ORIGEN"));
         param.put("nodoDestino", reg.getCampos().get("NODO_DESTINO"));
         param.put("nombreproceso", reg.getCampos().get("PROCESO_NOM"));
         param.put("PR_RID", ridForm);


         Direccionador direccionador = new Direccionador();
         direccionador.setParametros(param);

         direccionador.setNumForm(
                         String.valueOf(GeneralCodigoFormaEnum.FRM_NODO_DISPARA_CONTROLADOR
                                         .getCodigo()));
         SessionUtil.redireccionarForma(direccionador, modulo);
         


        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del combo Nodo Origen
     * (CB5858). Actualiza el combo Nodo Destino (CB5857).
     */
    public void cambiarNodoOrigen() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("NODO_DESTINO", "");

        cargarListaNodoDestino();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el combo Proceso (CB5859).
     * Actualiza el combo nodo origen (CB5858) y nodo destino
     * (CB5857).
     */
    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        codigoProceso = registro.getCampos().get(cProceso).toString();

        registro.getCampos().put("NODO_ORIGEN", "");
        registro.getCampos().put("NODO_DESTINO", "");

        cargarListaNodoOrigen();
        cargarListaNodoDestino();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el combo Nodo Origen (CB5858) en la
     * fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNodoOrigenC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el combo Proceso (CB5859) en la
     * fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProcesoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        codigoProceso = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cProceso).toString();

        cargarListaNodoOrigen();
        cargarListaNodoDestino();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cEstado, 4);
        registro.getCampos().put("PROCESO", codigoProceso);

        cargarListaNodoOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true -> Permite insertar.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * Guarda el codigo del proceso para sugerirlo al crear un nuevo
     * enlace entre nodos.
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        codigoProceso = registro.getCampos().get(cProceso).toString();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion o actualizacion
     * del registro.
     * 
     * @return true -> Permite insertar o actualizar.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion o
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true -> Permite eliminar.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true.
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cEstado, 4);
        registro.getCampos().put(cProceso, codigoProceso);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove("NODO_O_NOM");
        registro.getCampos().remove("NODO_D_NOM");
        registro.getCampos().remove("PROCESO_NOM");
        registro.getCampos().remove("ESTADO_NOM");
        registro.getCampos().remove("OPERACION_NOM");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario.
     *
     * @param registro
     * registro del cual se activo la edicion.
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        /*- Se asigna el codigo del proceso a la variable de clase. */
        codigoProceso = registro.getCampos().get(cProceso).toString();

        cargarListaNodoOrigen();
        cargarListaNodoDestino();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cEstado, 4);
        registro.getCampos().put(cProceso, codigoProceso);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario. Redirecciona al formulario de procesos.
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmNodosControladorEnum.PR_RID.getValue(), ridForm);

        Direccionador dir = new Direccionador();
        dir.setParametros(param);

        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_WF_PROCESOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(dir, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaEstado
     * 
     * @return listaEstado
     */
    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    /**
     * Asigna la lista listaEstado
     * 
     * @param listaEstado
     * Variable a asignar en listaEstado
     */
    public void setListaEstado(List<Registro> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * Retorna la lista listaNodoDestino
     * 
     * @return listaNodoDestino
     */
    public List<Registro> getListaNodoDestino() {
        return listaNodoDestino;
    }

    /**
     * Asigna la lista listaNodoDestino
     * 
     * @param listaNodoDestino
     * Variable a asignar en listaNodoDestino
     */
    public void setListaNodoDestino(List<Registro> listaNodoDestino) {
        this.listaNodoDestino = listaNodoDestino;
    }

    /**
     * Retorna la lista listaNodoOrigen
     * 
     * @return listaNodoOrigen
     */
    public List<Registro> getListaNodoOrigen() {
        return listaNodoOrigen;
    }

    /**
     * Asigna la lista listaNodoOrigen
     * 
     * @param listaNodoOrigen
     * Variable a asignar en listaNodoOrigen
     */
    public void setListaNodoOrigen(List<Registro> listaNodoOrigen) {
        this.listaNodoOrigen = listaNodoOrigen;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaOperacion() {
        return listaOperacion;
    }

    public void setListaOperacion(List<Registro> listaOperacion) {
        this.listaOperacion = listaOperacion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
