package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EpitemsestproysControladorEnum;
import com.sysman.precontractual.enums.EpitemsestproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 17/03/2016
 * @modified jguerrero
 * @version 2. 24/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class EpitemsestproysControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private Registro registroSub;
    private final String modulo;
    private HashMap<String, Object> llaveRid;
    private String cantidad;
    private String subTotal;
    private String total;
    private List<Registro> listaEpditemestproy;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private String auxiliar;
    private RegistroDataModelImpl listatxtDependencia;
    private RegistroDataModelImpl listatxtResponsable;
    private String codEstudio;
    /*
     * Variable que recibe por parametro del formulario
     * frmestprevioproy
     */
    private String nombreDependencia;
    /*
     * Variable que almacenara el nombre de la dependencia
     */
    private String nombreResponsable;
    /*
     * Variable que almacenara el nombre del responsable
     */

    private String especificacion;
    private String elementoValor;

    /**
     * Atributo que gestiona la visibilidad de los controles de
     * insercion, actualizacion y eliminacion
     */
    private boolean esCreador;

    private String vigencia;

    private final String nombreCons;
    private final String dependenciaCons;
    private final String responsableCons;
    private final String porcIvaCons;
    private final String porcDescuentCons;
    private final String administracionCons;
    private final String imprevistosCons;
    private final String utilidadesCons;
    private final String esdItemeCons;
    private final String codItemCons;
    private final String codigoElementoCons;
    private final String codEstudioCons;
    private final String elementoCons;
    private final String vlrTotalCons;
    private final String subTotalCons;
    private final String cantidadCons;
    private final String valorUnitarioCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @SuppressWarnings("unchecked")
    public EpitemsestproysControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();
        responsableCons = GeneralParameterEnum.RESPONSABLE.getName();
        porcIvaCons = GeneralParameterEnum.PORCIVA.getName();
        porcDescuentCons = GeneralParameterEnum.PORCDESCUENTO.getName();
        administracionCons = EpitemsestproysControladorEnum.ADMINISTRACION
                        .getValue();
        imprevistosCons = EpitemsestproysControladorEnum.IMPREVISTOS.getValue();
        utilidadesCons = EpitemsestproysControladorEnum.UTILIDADES.getValue();
        esdItemeCons = EpitemsestproysControladorEnum.ES_DITEM_E.getValue();
        codItemCons = EpitemsestproysControladorEnum.COD_ITEM.getValue();
        codigoElementoCons = GeneralParameterEnum.CODIGOELEMENTO.getName();
        codEstudioCons = GeneralParameterEnum.COD_ESTUDIO.getName();
        elementoCons = GeneralParameterEnum.ELEMENTO.getName();
        vlrTotalCons = EpitemsestproysControladorEnum.VLRTOTAL.getValue();
        subTotalCons = EpitemsestproysControladorEnum.SUBTOTAL.getValue();
        cantidadCons = GeneralParameterEnum.CANTIDAD.getName();
        valorUnitarioCons = GeneralParameterEnum.VALORUNITARIO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.EPITEMSESTPROYS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            llaveRid = (HashMap<String, Object>) parametrosEntrada.get(
                            EpitemsestproysControladorEnum.RIDLOWER.getValue());
            codEstudio = (String) parametrosEntrada
                            .get(EpitemsestproysControladorEnum.TXT_COD_ESTUDIOLOWER
                                            .getValue());
            esCreador = Boolean.parseBoolean(
                            parametrosEntrada
                                            .get(EpitemsestproysControladorEnum.ES_CREADORLOWER
                                                            .getValue())
                                            .toString());

            vigencia = (String) parametrosEntrada
                            .get(EpitemsestproysControladorEnum.VIGENCIA_PERIODOLOWER
                                            .getValue());
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {
        cargarlistaElemento();
        cargarlistaElementoE();
        cargarlistatxtDependencia();

    }

    @Override
    public void iniciarListasSub() {
        cargarlistatxtResponsable();

        registroSub.getCampos().put(porcIvaCons, 0);
        registroSub.getCampos().put(porcDescuentCons, 0);
        registroSub.getCampos().put(administracionCons, 0);
        registroSub.getCampos().put(imprevistosCons, 0);
        registroSub.getCampos().put(utilidadesCons, 0);
        cargarlistaEpditemestproy();
        sumaTotal();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaEpditemestproy = null;
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.ES_ITEMS_E;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        EpitemsestproysControladorEnum.CODESTUDIO.getValue(),
                        codEstudio);

    }

    public void cargarlistaEpditemestproy() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(EpitemsestproysControladorEnum.CODIGOESTUDIO.getValue(),
                            codEstudio);
            param.put(EpitemsestproysControladorEnum.CODIGOITEM.getValue(),
                            registro.getCampos().get(codItemCons));

            listaEpditemestproy = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.ES_DITEM_E
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            esdItemeCons));

        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarlistaElemento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EpitemsestproysControladorUrlEnum.URL14511
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoCons);

    }

    public void cargarlistaElementoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EpitemsestproysControladorUrlEnum.URL14511
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoCons);
    }

    public void cargarlistatxtDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EpitemsestproysControladorUrlEnum.URL16321
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatxtDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarlistatxtResponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EpitemsestproysControladorUrlEnum.URL17206
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos().get(dependenciaCons));

        listatxtResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        responsableCons);

    }

    public void agregarRegistroSubEpditemestproy() {
        try {

            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "'' AND COD_ESTUDIO = ''", codEstudio,
                            "''  AND COD_ITEM = ''",
                            retornarString(registro, codItemCons), "''");
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            esdItemeCons, criterio,
                            EpitemsestproysControladorEnum.COD_DITEM.getValue(),
                            "1");

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos()
                            .put(EpitemsestproysControladorEnum.CREATED_BY
                                            .getValue(),
                                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos()
                            .put(EpitemsestproysControladorEnum.DATE_CREATED
                                            .getValue(), new Date());

            registroSub.getCampos().put(codItemCons,
                            registro.getCampos().get(codItemCons));
            registroSub.getCampos().put(codEstudioCons,
                            registro.getCampos().get(codEstudioCons));
            registroSub.getCampos().put(
                            EpitemsestproysControladorEnum.COD_DITEM.getValue(),
                            consecutivo);            
            registroSub.getCampos().put(
                            GeneralParameterEnum.DEPENDENCIA.getName(),
                            registro.getCampos().get( GeneralParameterEnum.DEPENDENCIA.getName())
                             );
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_DITEM_E
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarlistaEpditemestproy();

            sumaTotal();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            registroSub.getCampos().put(porcIvaCons, 0);
            registroSub.getCampos().put(porcDescuentCons, 0);
            registroSub.getCampos().put(administracionCons, 0);
            registroSub.getCampos().put(imprevistosCons, 0);
            registroSub.getCampos().put(utilidadesCons, 0);

        }
    }

    public void editarRegSubEpditemestproy(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().remove(
                        EpitemsestproysControladorEnum.CREATED_BY.getValue());
        reg.getCampos().remove(
                        EpitemsestproysControladorEnum.DATE_CREATED.getValue());

        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_DITEM_E
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

            sumaTotal();

        }
        catch (SystemException ex) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarlistaEpditemestproy();

        }
    }

    public void eliminarRegSubEpditemestproy(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_DITEM_E
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarlistaEpditemestproy();

            sumaTotal();
        }
        catch (SystemException ex) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cancelarEdicionEpditemestproy() {
        cargarlistaEpditemestproy();
    }

    public void oprimirGeneraCodigo(ActionEvent ac) {
        // Metodo generado del beanBase

    }

    public void oprimirCOTIZACION(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String codItem = registro.getCampos()
                        .get(EpitemsestproysControladorEnum.COD_ITEM.getValue())
                        .toString();
        String elemento = reg.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName())
                        .toString();
        String[] campos = { "rid", EpitemsestproysControladorEnum.CODESTUDIO
                        .getValue().toLowerCase(),
                            EpitemsestproysControladorEnum.COD_ITEM.getValue()
                                            .toLowerCase(),
                            GeneralParameterEnum.ELEMENTO.getName()
                                            .toLowerCase() };
        Object[] valores = { css, codEstudio, codItem, elemento };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.FRMCOTIZACIONESPROYS_CONTROLADOR
                                        .getCodigo()),
                        String.valueOf(modulo), campos,
                        valores);

    }

    public void cambiarCANTIDAD() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        // Trae la multiplicaciï¿½n de los registros Cantidad y Valor
        // Unitario y lo muestra en el campo de Subtotal
        registroSub.getCampos().get(subTotalCons);
        registroSub.getCampos().get(vlrTotalCons);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVALORUNITARIO() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        // Trae la multiplicaciï¿½n de los registros Cantidad y Valor
        // Unitario y lo muestra en el campo de Subtotal
        registroSub.getCampos().get(subTotalCons);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarSUBTOTAL() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPORCIVA() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPORCDESCUENTO() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtA() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtI() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtU() {
        // <CODIGO_DESARROLLADO>
        actualizarTotales(registroSub);
        registroSub.getCampos().get(vlrTotalCons);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarElementoC(int rowNum) {
        listaEpditemestproy.get(rowNum).getCampos().put(
                        GeneralParameterEnum.ESPECIFICACION.getName(),
                        especificacion);
        listaEpditemestproy.get(rowNum).getCampos().put(valorUnitarioCons,
                        elementoValor);
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
    }

    public void cambiarCANTIDADC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(subTotalCons);
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void cambiarVALORUNITARIOC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(subTotalCons);
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);

    }

    public void cambiarSUBTOTALC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void cambiarPORCIVAC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);

    }

    public void cambiarPORCDESCUENTOC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void cambiarTxtAC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void cambiarTxtIC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void cambiarTxtUC(int rowNum) {
        actualizarTotales(listaEpditemestproy.get(rowNum % 10));
        listaEpditemestproy.get(rowNum).getCampos().get(vlrTotalCons);
    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(elementoCons,
                        registroAux.getCampos().get(codigoElementoCons));
        registroSub.getCampos().put(
                        GeneralParameterEnum.ESPECIFICACION.getName(),
                        registroAux.getCampos()
                                        .get(EpitemsestproysControladorEnum.NOMBRELARGO
                                                        .getValue()));
        registroSub.getCampos().put(valorUnitarioCons,
                        registroAux.getCampos()
                                        .get(EpitemsestproysControladorEnum.VLRUNITARIOPROM
                                                        .getValue()));

        actualizarTotales(registroSub);
    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, codigoElementoCons);
        especificacion = retornarString(registroAux,
                        EpitemsestproysControladorEnum.NOMBRELARGO.getValue());
        elementoValor = retornarString(registroAux,
                        EpitemsestproysControladorEnum.VLRUNITARIOPROM
                                        .getValue());

    }

    public void seleccionarFilatxtDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(dependenciaCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        nombreDependencia = retornarString(registroAux, nombreCons);
        nombreResponsable = null;
        registro.getCampos().put(responsableCons, null);

        cargarlistatxtResponsable();

    }

    public void seleccionarFilatxtResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(responsableCons,
                        registroAux.getCampos().get(responsableCons));
        // nombreResponsable: Recibe el nombre del responsable
        // seleccionado
        nombreResponsable = retornarString(registroAux, nombreCons);

        registro.getCampos()
                        .put(EpitemsestproysControladorEnum.SUCURSAL_RESPONSABLE
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.SUCURSAL
                                                                        .getName()));

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {

        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if ("i".equals(accion)) {
            registro.getCampos()
                            .put(EpitemsestproysControladorEnum.PORCIVAGLOBAL
                                            .getValue(), 0);
            registro.getCampos()
                            .put(EpitemsestproysControladorEnum.PORCDESCGLOBAL
                                            .getValue(), 0);
            registro.getCampos().put(
                            EpitemsestproysControladorEnum.A_GLOBAL.getValue(),
                            0);
            registro.getCampos().put(
                            EpitemsestproysControladorEnum.I_GLOBAL.getValue(),
                            0);
            registro.getCampos().put(
                            EpitemsestproysControladorEnum.U_GLOBAL.getValue(),
                            0);
            nombreDependencia = null;
            nombreResponsable = null;
            cantidad = null;
            subTotal = "0";
            total = null;
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            new Date());

        }
        // </CODIGO_DESARROLLADO>

    }

    public void actualizarTotales(Registro reg) {

        double cantidadE = retornarDouble(reg, cantidadCons);

        double valorUnitarioE = retornarDouble(reg, valorUnitarioCons);
        double subTot = cantidadE * valorUnitarioE;
        reg.getCampos().put(subTotalCons, subTot);
        reg.getCampos().put(vlrTotalCons, subTot);

        //

        double ivaE = retornarDouble(reg, porcIvaCons);

        double porcE = retornarDouble(reg, porcDescuentCons);

        double adminE = retornarDouble(reg, administracionCons);

        double impreE = retornarDouble(reg, imprevistosCons);

        double utiE = retornarDouble(reg, utilidadesCons);

        double valorTotal = ((subTot + ((subTot * ivaE) / 100))
            - ((subTot * porcE) / 100)) + ((subTot * adminE) / 100)
            + ((subTot * impreE) / 100) + ((subTot * utiE) / 100);

        reg.getCampos().put(vlrTotalCons, valorTotal);

    }

    public void sumaTotal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(EpitemsestproysControladorEnum.COD_ITEM.getValue(),
                            registro.getCampos().get(codItemCons));
            param.put(GeneralParameterEnum.COD_ESTUDIO.getName(), codEstudio);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EpitemsestproysControladorUrlEnum.URL25393
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg != null) {

                cantidad = retornarStringFormatDouble(reg, cantidadCons);

                subTotal = new java.text.DecimalFormat("$ #,##0.00")
                                .format(retornarDouble(reg, subTotalCons));

                total = new java.text.DecimalFormat("$ #,##0.00")
                                .format(retornarDouble(reg, vlrTotalCons));

            }
            else {

                cantidad = "0";
                subTotal = "0";
                total = "0";
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        long codItem = 0;
        try {
            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "'' AND COD_ESTUDIO = ''", codEstudio,
                            "''");

            codItem = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.ES_ITEMS_E.getTable(), criterio,
                            codItemCons, "1");

        }
        catch (SystemException ex) {
            Logger.getLogger(EpitemsestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        registro.getCampos().put(codEstudioCons, codEstudio);
        registro.getCampos().put(codItemCons, codItem);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getlistaEpditemestproy() {
        return listaEpditemestproy;
    }

    public void setlistaEpditemestproy(
        List<Registro> listaEpditemestproy) {
        this.listaEpditemestproy = listaEpditemestproy;
    }

    public RegistroDataModelImpl getlistaElemento() {
        return listaElemento;
    }

    public void setlistaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getlistaElementoE() {
        return listaElementoE;
    }

    public void setlistaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getlistatxtDependencia() {
        return listatxtDependencia;
    }

    public void setlistatxtDependencia(
        RegistroDataModelImpl listatxtDependencia) {
        this.listatxtDependencia = listatxtDependencia;
    }

    public RegistroDataModelImpl getlistatxtResponsable() {
        return listatxtResponsable;
    }

    public void setlistatxtResponsable(
        RegistroDataModelImpl listatxtResponsable) {
        this.listatxtResponsable = listatxtResponsable;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getCodEstudio() {
        return codEstudio;
    }

    public void setCodEstudio(String codEstudio) {
        this.codEstudio = codEstudio;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridEstPrevios",
                        llaveRid);
        parametros.put(EpitemsestproysControladorEnum.TXT_COD_ESTUDIOLOWER
                        .getValue(), codEstudio);
        parametros.put(EpitemsestproysControladorEnum.VIGENCIA_PERIODOLOWER
                        .getValue(), vigencia);
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private double retornarDouble(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? 0
            : Double.parseDouble(reg.getCampos().get(campo).toString());
    }

    private String retornarStringFormatDouble(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "0"
            : String.valueOf(Double.parseDouble(
                            reg.getCampos().get(campo).toString()));
    }

}
