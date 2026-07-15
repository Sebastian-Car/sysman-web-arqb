/*-
 * FrmtipotransaccionsstsControlador.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmtipotransaccionsstsControladorEnum;
import com.sysman.hojasdevida.enums.FrmtipotransaccionsstsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Migracion del formulario access FRM_TIPO_TRANSACCION_SST a web
 * controlador FrmtipotransaccionsstsControlador forma
 * frmtipotransaccionsst.xhtml creacion de ruta menu para abrir el
 * formulario continuo creacion de properties para el formulario
 * continuo llamado de dss ya estaba creado reutilizacion de dss
 * numero 104008
 * 
 * @version 1.0, 28/12/2017
 * @author jcrodriguez
 */
@ManagedBean
@ViewScoped
public class FrmtipotransaccionsstsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * variable cadena que almacena la plantilla
     */
    private String plantilla;

    /**
     * variable que almanna la lista de plantillas
     */
    private RegistroDataModelImpl listaModeloPlantilla;
    /**
     * variable que almanna la lista de plantillas
     */
    private RegistroDataModelImpl listaModeloPlantillaE;

    private List<Registro> listaclaseTransaccion;

    private String codigoPlantilla;

    private final String claseTransaccionCons;

    private final String codigoPlanillaCons;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */

    private String auxiliar;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmtipotransaccionsstsControlador
     */
    public FrmtipotransaccionsstsControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoPlanillaCons = "CODIGO_PLANTILLA";
        claseTransaccionCons = "CLASE_TRANSACCION";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_TIPOTRANSACCIONSSTS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        enumBase = GenericUrlEnum.SST_TIPO_TRANSACCION;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        cargarListaModeloPlantilla();
        cargarListaModeloPlantillaE();
        cargarListaclaseTransaccion();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListaclaseTransaccion() {
        listaclaseTransaccion = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT SST_CLASE_TRANSACCION.CODIGO," +
                            "   SST_CLASE_TRANSACCION.NOMBRE NOMBRE_CLASE,"
                            + " SST_CLASE_TRANSACCION.CODIGO_PLANTILLA"
                            +
                            " FROM SST_CLASE_TRANSACCION");
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaModeloPlantilla
     */
    public void cargarListaModeloPlantilla() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsstsControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmtipotransaccionsstsControladorEnum.TIPO.getValue(),
                        codigoPlantilla);

        listaModeloPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaModeloPlantilla
     */
    public void cargarListaModeloPlantillaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsstsControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmtipotransaccionsstsControladorEnum.TIPO.getValue(),
                        codigoPlantilla);

        listaModeloPlantillaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * Metodo ejecutado al oprimir el boton Anexo
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirAnexo(Registro reg, int indice) {
        String[] campos = { "tipoTransaccion", "nombreTransaccion" };
        Object[] valores = { reg.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString(), reg.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName())
                                        .toString() };
        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.DOCUMENTO_ANEXO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
    }

    /**
     * Metodo ejecutado al oprimir el boton Descargar
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDescargar(Registro reg, int indice) {

        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.CODIGO.getName(), SysmanFunciones
                            .nvl(reg.getCampos()
                                            .get(FrmtipotransaccionsstsControladorEnum.RUTAFORMATO_PLANTILLA
                                                            .getValue()),
                                            "")
                            .toString());

            param.put(FrmtipotransaccionsstsControladorEnum.TIPO.getValue(),
                            obtenerTipoTransaccion(reg));

            param.put(FrmtipotransaccionsstsControladorEnum.FECHAGENERACION
                            .getValue(),
                            new Date());
            rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtipotransaccionsstsControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
            Date fecha = (Date) rs.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());

            String nombreDocumento = idioma.getString("TB_TB3895").replace(
                            "s$fecha$s",
                            SysmanFunciones.convertirAFechaCadena(new Date(),
                                            "dd:MM:yyyy"));

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = SysmanFunciones
                            .nvl(reg.getCampos()
                                            .get(FrmtipotransaccionsstsControladorEnum.RUTAFORMATO_PLANTILLA
                                                            .getValue()),
                                            "")
                            .toString();
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = nombreDocumento;

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("s$compania$s", "'" + compania + "'");
            parametros.put("s$codigo$s", "'" + reg.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString()
                + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            parametros);
            String numForm = String
                            .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo());
            SessionUtil.cargarModalDatosFlash(numForm, SessionUtil.getModulo(),
                            campos, valores);
        }
        catch (SystemException | ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    public void oprimirclaseEvento(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        codigoPlantilla = service.buscarEnLista(
                        retornarString(reg, claseTransaccionCons),
                        GeneralParameterEnum.CODIGO.getName(),
                        codigoPlanillaCons, listaclaseTransaccion);

        String[] campos = { "transaccion", "plantilla" };
        Object[] valores = { reg.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString(), codigoPlantilla };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.TIPO_CLASE_EVENTOSSTS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirtipoActividad(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "transaccion" };
        Object[] valores = { reg.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString() };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_TIPO_ACTIVIDADSS_CONTOLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmtipotransaccionsstsControladorEnum.RUTAFORMATO_PLANTILLA
                                        .getValue(), SysmanFunciones
                                                        .nvl(registroAux.getCampos()
                                                                        .get(GeneralParameterEnum.CODIGO
                                                                                        .getName()),
                                                                        "")
                                                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantillaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // heredado del bean base
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().remove("NOMBRE_CLASE");
        generarCodigo(registro);

        return true;
    }

    private void generarCodigo(Registro registro) {

        StringBuilder criterio = new StringBuilder();
        criterio.append(GeneralParameterEnum.COMPANIA.getName().toLowerCase());
        criterio.append("=");
        criterio.append("''");
        criterio.append(compania);
        criterio.append("''");
        int aux;
        try {
            aux = (int) ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.SST_TIPO_TRANSACCION.getTable(),
                            criterio.toString(),
                            GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            aux);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // heredado del bean base

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     */
    @Override
    public boolean actualizarAntes() {
        // heredado del bean base
        registro.getCampos().remove("NOMBRE_CLASE");

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // heredado del bean base
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // heredado del bean base
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // heredado del bean base
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // heredado del bean base
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        codigoPlantilla = service.buscarEnLista(
                        retornarString(registro, claseTransaccionCons),
                        GeneralParameterEnum.CODIGO.getName(),
                        codigoPlanillaCons, listaclaseTransaccion);

        cargarListaModeloPlantillaE();
    }

    public void cambiarclaseTransaccion() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("RUTAFORMATO_PLANTILLA", null);

        codigoPlantilla = service.buscarEnLista(
                        retornarString(registro, claseTransaccionCons),
                        GeneralParameterEnum.CODIGO.getName(),
                        codigoPlanillaCons, listaclaseTransaccion);

        cargarListaModeloPlantilla();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control claseTransaccion en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarclaseTransaccionC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("RUTAFORMATO_PLANTILLA", null);

        String claseTransaccion = retornarString(
                        listaInicial.getDatasource().get(rowNum % 10),
                        claseTransaccionCons);

        codigoPlantilla = service.buscarEnLista(
                        claseTransaccion,
                        "CODIGO",
                        codigoPlanillaCons, listaclaseTransaccion);

        cargarListaModeloPlantillaE();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
        registro.getCampos().clear();
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable plantilla
     * 
     * @return plantilla
     */
    public String getPlantilla() {

        return plantilla;
    }

    /**
     * Asigna la variable plantilla
     * 
     * @param plantilla
     * Variable a asignar en plantilla
     */
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantilla() {
        return listaModeloPlantilla;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantilla(
        RegistroDataModelImpl listaModeloPlantilla) {
        this.listaModeloPlantilla = listaModeloPlantilla;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantillaE() {
        return listaModeloPlantillaE;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantillaE(
        RegistroDataModelImpl listaModeloPlantillaE) {
        this.listaModeloPlantillaE = listaModeloPlantillaE;
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

    public List<Registro> getListaclaseTransaccion() {
        return listaclaseTransaccion;
    }

    public void setListaclaseTransaccion(List<Registro> listaclaseTransaccion) {
        this.listaclaseTransaccion = listaclaseTransaccion;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private String obtenerTipoTransaccion(Registro reg) {
        return service.buscarEnLista(
                        retornarString(reg, claseTransaccionCons),
                        "CODIGO",
                        codigoPlanillaCons, listaclaseTransaccion);

    }

}
