/*-
 * FrmDetallePlanosSqlsControlador.java
 *
 * 1.0
 * 
 * 24/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmDetallePlanosSqlsControladorUrlEnum;
import com.sysman.general.enums.ReportesControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que se encarga de administrar los datos de los
 * detalle_planos_ssql
 *
 * @version 1.0, 24/11/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmDetallePlanosSqlsControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private final String consGrupo;
    
    private final String consSecuencia;
    
    private final String consCodigo;

    // <DECLARAR_ATRIBUTOS>

    private boolean bloqueado;
    
    private int indiceParametros;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFiltro;
    private RegistroDataModelImpl listaFiltroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaParametros;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmDetallePlanosSqlsControlador
     */
    public FrmDetallePlanosSqlsControlador() {
        super();
        compania = SessionUtil.getCompania();
        consGrupo="GRUPO";
        consSecuencia="SECUENCIA";
        consCodigo= "CODIGO_FILTRO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_DETALLE_PLANOS_SQLS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFiltro();
        cargarListaFiltroE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaParametros();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaParametros = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.DETALLE_PLANOS_SQL;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    /**
     * 
     * Carga la lista listaParametros
     *
     */
    public void cargarListaParametros() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_PARAMETROS_PLANOS
                                                        .getGridKey());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(consGrupo, registro.getCampos().get(consGrupo));
        param.put(consSecuencia, registro.getCampos().get(consSecuencia));
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        String[] rowKey;
        try {
            rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.D_PARAMETROS_PLANOS.getTable());
            listaParametros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            rowKey);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    //
    // // <METODOS_CARGAR_LISTA>
    //
    /**
     * 
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltro() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL13311
                                                        .getValue());

        listaFiltro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    /**
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltroE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL13311
                                                        .getValue());

        listaFiltroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    public void cambiarDefecto() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado al cambiar el control Defecto en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDefectoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaParametros.getDatasource().get(rowNum % 10)
                        .getCampos().get("PORDEFECTO")) {
            bloqueado = true;
            listaParametros.getDatasource().get(rowNum % 10)
            .getCampos().put(consCodigo,null);
        }
        else {
            bloqueado = false;
            listaParametros.getDatasource().get(rowNum % 10)
            .getCampos().put("VALOR_DEFECTO",null);
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consCodigo,
                        registroAux.getCampos().get(consCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(consCodigo);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    /**
     * Metodo de insercion del formulario Parametros
     * 
     */
    public void agregarRegistroSubParametros() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario Parametros
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubParametros(RowEditEvent event) {
        Registro regPar = (Registro) event.getObject();
        try {
            regPar.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            regPar.getCampos().remove("NOMBRE_TIPO");
            regPar.getCampos().remove("NOMBRE_PARAMETRO"); 
            regPar.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            regPar.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            regPar.getCampos().remove("NOMBRE_FILTRO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_PARAMETROS_PLANOS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            regPar.getCampos(), regPar.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaParametros();
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Parametros
     *
     */
    public void cancelarEdicionParametros() {
        listaParametros.load();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
    
    public void activarEdicionParametros(Registro r) {
        Registro reg = r;
        if((boolean) reg.getCampos().get("PORDEFECTO")) {
            bloqueado= true;
        }else {
            bloqueado=false;
        }
        
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        insertarCondicion(registro.getCampos().get("SSQL").toString());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private void insertarCondicion(String sql) {
        try {

            String regex = ":([a-z]|[A-Z])+";
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(sql.toUpperCase());

            Map<String, Object> campos = new HashMap<>();
            campos.put("COMPANIA", compania);
            campos.put(consGrupo, registro.getCampos().get(consGrupo));
            campos.put(consSecuencia, registro.getCampos().get(consSecuencia));
            campos.put("CODIGO",
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmDetallePlanosSqlsControladorUrlEnum.URL002
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);

            while (matcher.find()) {

                String cadena = matcher.group();

                Map<String, Object> campos2 = new HashMap<>();

                campos2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                campos2.put(consGrupo, registro.getCampos().get(consGrupo));
                campos2.put(consSecuencia, registro.getCampos().get(consSecuencia));
                campos2.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));
                campos2.put("CODIGO_PARAMETRO",
                                consecutivoDetalleParametro());

                campos2.put("NOMBRE_PARAMETRO", cadena.toLowerCase());

                campos2.put("ETIQUETA_PARAMETRO",
                                cadena.substring(1, cadena.length()));

                campos2.put("TIPO_PARAMETRO", "S");

                campos2.put(GeneralParameterEnum.CREATED_BY
                                .getName(), SessionUtil.getUser().getCodigo());

                campos2.put(GeneralParameterEnum.DATE_CREATED
                                .getName(), new Date());

                Parameter parameterIns = new Parameter();
                parameterIns.setFields(campos2);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.D_PARAMETROS_PLANOS
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                parameterIns);

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private long consecutivoDetalleParametro() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_PARAMETROS_PLANOS", "COMPANIA = ''" + compania +
                                "'' AND GRUPO = ''"
                                + registro.getCampos().get(consGrupo).toString() +
                                "'' AND CODIGO = ''"
                                + registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString()
                                +
                                "'' AND SECUENCIA = ''"
                                + registro.getCampos().get(consSecuencia)
                                                .toString()
                                + "''",
                            "CODIGO_PARAMETRO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaFiltro
     * 
     * @return listaFiltro
     */
    public RegistroDataModelImpl getListaFiltro() {
        return listaFiltro;
    }

    /**
     * Asigna la lista listaFiltro
     * 
     * @param listaFiltro
     * Variable a asignar en listaFiltro
     */
    public void setListaFiltro(RegistroDataModelImpl listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

    /**
     * Retorna la lista listaFiltro
     * 
     * @return listaFiltro
     */
    public RegistroDataModelImpl getListaFiltroE() {
        return listaFiltroE;
    }

    /**
     * Asigna la lista listaFiltro
     * 
     * @param listaFiltro
     * Variable a asignar en listaFiltro
     */
    public void setListaFiltroE(RegistroDataModelImpl listaFiltroE) {
        this.listaFiltroE = listaFiltroE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaParametros
     * 
     * @return listaParametros
     */
    public RegistroDataModelImpl getListaParametros() {
        return listaParametros;
    }

    /**
     * Asigna la lista listaParametros
     * 
     * @param listaParametros
     * Variable a asignar en listaParametros
     */
    public void setListaParametros(RegistroDataModelImpl listaParametros) {
        this.listaParametros = listaParametros;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getIndiceParametros() {
        return indiceParametros;
    }

    public void setIndiceParametros(int indiceParametros) {
        this.indiceParametros = indiceParametros;
    }
    
    

    // </SET_GET_ADICIONALES>
}
