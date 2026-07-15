package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.enums.MedidoresControladorEnum;
import com.sysman.serviciospublicos.enums.MedidoresControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author amonroy
 * @version 1, 05/08/2016
 * 
 * @version 2, 12/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class MedidoresControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /**
     * Constante que almacenara la cadena "PERIODO"
     */
    private final String periodoC;

    /**
     * Constante que almacenara la cadena "MARCA"
     */
    private final String marcaC;

    /**
     * Constante que almacenara la cadena "DIGITOS"
     */
    private final String digitosC;

    /**
     * Constante que almacenara la cadena "CONSECUTIVO"
     */
    private final String consecutivoC;

    /**
     * Constante que almacenara la cadena "CODIGORUTA"
     */
    private final String codigoRutaC;

    /**
     * Constante que almacenara la cadena "CODIGOINTERNO"
     */
    private final String codigoInternoC;

    /**
     * Constante que almacenara la cadena "CODIGO"
     */
    private final String codigoC;
    
    private final String tb1276;

    private int indice;
    // <DECLARAR_ATRIBUTOS>
    private String codigoRuta;
    private String codigoInterno;
    private String ciclo;
    private String ano;
    private String periodo;
    private Date fechaPreparacion;
    private String codigoInternoGrilla;
    private boolean editando;
    private int lecturaAnterior;
    private int lecturaFinalViejo;
    private boolean registroMedidor;
    private boolean cambioMedidor;
    
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMarca;
    private List<Registro> listaCodigo1;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigoruta1;
    private RegistroDataModelImpl listacodigoruta1E;
    private RegistroDataModelImpl listaCODIGORUTA;
    private RegistroDataModelImpl listaCODIGORUTAE;
    private RegistroDataModelImpl listaAno;
    private RegistroDataModelImpl listaAnoE;
    private RegistroDataModelImpl listaPeriodo;
    private RegistroDataModelImpl listaPeriodoE;
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    
    @EJB
    private EjbServiciosPublicosSieteRemote ejbServiciosPublicosSiete;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of MedidoresControlador
     */
    public MedidoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        periodoC = "PERIODO";
        marcaC = "MARCA";
        digitosC = "DIGITOS";
        consecutivoC = "CONSECUTIVO";
        codigoRutaC = "CODIGORUTA";
        codigoInternoC = "CODIGOINTERNO";
        codigoC = "CODIGO";
        tb1276=idioma.getString("TB_TB1276");
        try {
            numFormulario = GeneralCodigoFormaEnum.MEDIDORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = (String) parametrosEntrada.get("CICLO");
                ano = parametrosEntrada.get(GeneralParameterEnum.ANO.getName())
                                .toString();
                periodo = (String) parametrosEntrada.get(periodoC);
                fechaPreparacion = (Date) parametrosEntrada
                                .get("FECHAPREPARACION");

            }

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(MedidoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_MEDIDOR;
        buscarLlave();

        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaMarca();
        cargarListaCodigo1();
        cargarListaAno();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoruta1();
        cargarListacodigoruta1E();
        cargarListaCODIGORUTA();
        cargarListaCODIGORUTAE();
        cargarListaAno();
        cargarListaPeriodo();
        cargarListaAnoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(MedidoresControladorEnum.PARAM2.getValue(),
                        codigoRuta != null ? codigoRuta == "" ? "-1": codigoRuta : "-1");
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMarca() {
        try {
            listaMarca = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL14021
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigo1() {
        try {
            listaCodigo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL9799
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
 
    
    

    public void cargarListaAno() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL12473
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        if ((registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()) != null)
            && !"".equals(registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()))) {
            param.put(MedidoresControladorEnum.PARAM4.getValue(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidoresControladorUrlEnum.URL9209
                                                            .getValue());
        }

        listaAno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ANO.getName());
    }

    public void cargarListaAnoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL12473
                                                        .getValue());

        listaAnoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.ANO.getName());
    }

    public void cargarListaPeriodo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL10399
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        if ((registro.getCampos().get(periodoC) != null)
            && !"".equals(registro.getCampos().get(periodoC))) {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MedidoresControladorUrlEnum.URL11759
                                                            .getValue());
            param.put(MedidoresControladorEnum.PARAM5.getValue(),
                            registro.getCampos().get(periodoC));
        }

        listaPeriodo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.MES.getName());
    }

    public void cargarListaPeriodoE(int rowNum) {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL10399
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(),
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos()
                                        .get(GeneralParameterEnum.ANO.getName())
                                        .toString());

        listaPeriodoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.MES.getName());
    }

    public void cargarListacodigoruta1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL13412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacodigoruta1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    public void cargarListacodigoruta1E() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL13412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacodigoruta1E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    public void cargarListaCODIGORUTA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL13412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCODIGORUTA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    public void cargarListaCODIGORUTAE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MedidoresControladorUrlEnum.URL13412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCODIGORUTAE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirmarcaMedidor() {
        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.MARCASMEDIDORS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo());
    }

    public void retornarFormulariomarcaMedidor() {
        cargarListaMarca();
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCODIGORUTAC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(codigoInternoC, codigoInternoGrilla);
        cambioMedidor=true;
        
        // Para el cambio en una fila selecciona (PARA
        // SUBFORMULARIOS) se realiza como lo muestra
        // la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public boolean cambiarCodigo1() {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(codigoC).toString());

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL11001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((rs != null)
            && (Integer.parseInt(rs.getCampos().get("TOTAL").toString()) > 0)) {
            JsfUtil.agregarMensajeAlerta(tb1276);
            registroMedidor=false;
            return false;
            
        }
        else {
            registroMedidor=true;
            return true;
        }
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Codigo1 en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigo1C(int rowNum) {
        
        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            listaInicial.getDatasource().get(rowNum % 10).getCampos().get(codigoC));

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL11001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((rs != null)
            && (Integer.parseInt(rs.getCampos().get("TOTAL").toString()) > 0)) {
            JsfUtil.agregarMensajeAlerta(tb1276);
            registroMedidor=false;
            
        }
        else {
            registroMedidor=true;
        }
    }

    public void cambiarMarca() {
        String strMarca = registro.getCampos().get(marcaC).toString();
        registro.getCampos().put(digitosC, service.buscarEnLista(strMarca,
                        marcaC, digitosC, listaMarca));

    }

    public void cambiarMarcaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(digitosC,
                        service.buscarEnLista(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(marcaC).toString(), marcaC,
                                        digitosC,
                                        listaMarca));
    }

    public void cambiarAnoC(int rowNum) {
        // </CODIGO_DESARROLLADO>
        cargarListaPeriodoE(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control estado en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarestadoC(int rowNum) {
        //<CODIGO_DESARROLLADO>
        Registro reg= listaInicial.getDatasource().get(rowNum % 10);
        if("B".equals(reg.getCampos().get("ESTADO"))){
            asignarMedidor();
        }
        //</CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    
    
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigoruta1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = SysmanFunciones.nvl(registroAux.getCampos().get(codigoRutaC), "").toString();
        codigoInterno = SysmanFunciones.nvl(registroAux.getCampos().get(codigoInternoC), "").toString();
        reasignarOrigen();
    }

    public void onRowSelectcodigoruta1E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoRutaC).toString();
    }

    public void seleccionarFilaCODIGORUTA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoRutaC,
                        registroAux.getCampos().get(codigoRutaC));
        registro.getCampos().put(codigoInternoC,
                        registroAux.getCampos().get(codigoInternoC));

        codigoRuta = SysmanFunciones.nvl(registroAux.getCampos().get(codigoRutaC),"").toString();
        codigoInterno = SysmanFunciones.nvl(registroAux.getCampos().get(codigoInternoC), "").toString();
        cargarListaAno();
        cargarListaPeriodo();
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),codigoRuta);
            
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL76235
                                                                            .getValue())
                                            .getUrl(), param));
            if(rs != null){
                registro.getCampos().put("LECTMEDANT",SysmanFunciones.nvl(rs.getCampos().get("LECTURA"), "0").toString());
            }
            
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaCODIGORUTAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoRutaC).toString();
        codigoInternoGrilla = registroAux.getCampos()
                        .get(codigoInternoC).toString();

    }

    public void seleccionarFilaAno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), registroAux
                        .getCampos().get(GeneralParameterEnum.ANO.getName()));
        registro.getCampos().remove(periodoC);
        cargarListaPeriodo();

    }

    public void seleccionarFilaAnoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString();

    }

    public void seleccionarFilaPeriodo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(periodoC, registroAux.getCampos()
                        .get(GeneralParameterEnum.MES.getName()));
    }

    public void seleccionarFilaPeriodoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.MES.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1030-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Maximize End Sub
         */
        registro.getCampos().put("FECHA_REG", new Date());
        registroMedidor=true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
        editando = false;
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASE.getName(), 0);
        registro.getCampos().put("LOCALIZACION", 99999);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        
        if(!registroMedidor){
            registro.getCampos().clear();
            registroMedidor=true;
            JsfUtil.agregarMensajeAlerta(tb1276);
            return false;
        }
        
        Registro auxR = null;
        try {
            auxR = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL5427
                                                                            .getValue())
                                            .getUrl(), param));

            int maxConsecutivo = Integer.parseInt(
                            auxR.getCampos().get(consecutivoC).toString());
            registro.getCampos().put(consecutivoC, maxConsecutivo);
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        asignarMedidor();
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        // Se eliminan estos campos del registro debido a que no
        // existen en la tabla medidores
        registro.getCampos().remove(codigoInternoC);
        registro.getCampos().remove("ESTADOLABEL");
        registro.getCampos().remove("TIPOLABEL");

        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos().put(GeneralParameterEnum.CICLO.getName(), ciclo);
        boolean estado=true;

        if(!registroMedidor){
            registroMedidor=true;
            JsfUtil.agregarMensajeAlerta(tb1276);
            return false;
        }
        
        String sCodigo1 = registro.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
        codigoRuta = registro.getCampos().get(codigoRutaC).toString();

        if ((SysmanFunciones.nvl(registro.getCampos().get(codigoRutaC),
                        "") != "")
            && (SysmanFunciones.nvl(registro.getCampos().get(codigoC),
                            "") != "")) {
            estado=validaSql(sCodigo1)?validarCodigoRuta(sCodigo1):false;
        }
        
        // </CODIGO_DESARROLLADO>
        return estado;
    }

    public boolean validarCodigoRuta(String sCodigo1) {
        boolean estado=true;
        try {
            String msj;
            codigoRuta = registro.getCampos().get(codigoRutaC).toString();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGO.getName(), sCodigo1);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            Registro auxR;

            auxR = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL1154
                                                                            .getValue())
                                            .getUrl(), param));

            if (auxR != null) {
                msj = idioma.getString("TB_TB1271")
                                .replace("s$codigo$s", sCodigo1)
                                .replace("s$codigoRuta$s", auxR.getCampos()
                                                .get(codigoRutaC).toString());
                JsfUtil.agregarMensajeInformativo(msj);
                estado=false;
            }
            if(estado){
                Registro auxR1 = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                MedidoresControladorUrlEnum.URL7249
                                                                                .getValue())
                                                .getUrl(), param));

                if (auxR1 != null) {
                    Registro aux = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    MedidoresControladorUrlEnum.URL8521
                                                                                    .getValue())
                                                    .getUrl(), param));

                    msj = idioma.getString("TB_TB1272")
                                    .replace("s$codigoRuta$s", codigoRuta)
                                    .replace("s$cod$s",
                                                    aux.getCampos().get("MEDIDOR")
                                                                    .toString());
                    JsfUtil.agregarMensajeInformativo(msj);

                    
                } 
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;
    }

    public boolean validaSql(String sCodigo1) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        boolean estado=true;
        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MedidoresControladorUrlEnum.URL4578
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (codigoRuta != null) {
            if (rs != null) {
                // Me!LECTMEDANT = rs!LECTURA
                lecturaAnterior = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("LECTURA"), 0)
                                .toString());

                String elemCambio = SysmanFunciones
                                .nvl(registro.getCampos().get("ELEMCAMBIO"),
                                                "")
                                .toString();
                if(!SysmanFunciones.validarVariableVacio(registro.getCampos().get(periodoC).toString())){
                    String periodoParam = SysmanFunciones.padl(registro.getCampos().get(periodoC).toString(), 2, "0");
                    String periodoRegActual = SysmanFunciones.padl(
                                    rs.getCampos().get(periodoC).toString(), 2,
                                    "0");
                    String anoConsulta = rs.getCampos()
                                    .get(GeneralParameterEnum.ANO.getName())
                                    .toString();
                    String fechaMe = registro.getCampos().get("ANO").toString() + periodoParam;
                    String fechaRegistro = anoConsulta + periodoRegActual;

                    int comparaFechas = fechaMe.compareTo(fechaRegistro);

                    if ("Medidor".equalsIgnoreCase(elemCambio)
                        && (comparaFechas <= 0) || (comparaFechas <= 0)) {
                        JsfUtil.agregarMensajeAlerta(
                                        idioma.getString("TB_TB1273"));
                        estado=false;
                    }
                }       
                estado=estado?validarParametroConsumo(elemCambio):false;
            } // cierre de IF grande
        }
        else if ("".equals(SysmanFunciones.nvlStr(codigoRuta, ""))
            && "".equals(SysmanFunciones.nvlStr(sCodigo1, ""))) {
            estado = false;
        }
        return estado;
    }

    public boolean validarParametroConsumo(String elemCambio) {
        boolean estado=true;
        boolean valorParam;
        try {
            valorParam = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania, "MANEJA CONSUMOS MIXTOS",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            "NO")
                            .toString());

            lecturaAnterior = Integer.parseInt(SysmanFunciones.nvl(
                            registro.getCampos().get("LECTMEDANT"),
                            0).toString());
            lecturaFinalViejo = Integer.parseInt(SysmanFunciones.nvl(
                            registro.getCampos()
                                            .get("LECTURAFINALVIEJO"),
                            0).toString());

            if ("Medidor".equalsIgnoreCase(elemCambio)
                && (valorParam) && lecturaAnterior > lecturaFinalViejo) {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB1274"));
                    estado=false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;
    }



    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        
        if(("B".equals(registro.getCampos().get("ESTADO")) || cambioMedidor) && editando){
            asignarMedidor();
            cambioMedidor=false;
        }
        editando = false;
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    
    public void asignarMedidor(){
        try {
            ejbServiciosPublicosSiete.actualizarEstadoMedidores(compania, Integer.parseInt(ciclo),
                            codigoRuta,
                            Long.parseLong(registro.getCampos().get(consecutivoC).toString()),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }
    
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if(registro.getCampos().get(codigoRutaC)!= null){
            String anoAct = registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()).toString();

            if (registro.getCampos().get(periodoC) == null
                || !registro.getCampos().get(periodoC).toString().equals(periodo)
                || !anoAct.equals(ano)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1275"));
                return false;
            }
        }
        
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        editando = true;

    }

    @Override
    public void removerCombos() {
        // Metodo que se hereda desde el bean base
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo que se hereda desde el bean base
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Date getFechaPreparacion() {
        return fechaPreparacion;
    }

    public void setFechaPreparacion(Date fechaPreparacion) {
        this.fechaPreparacion = fechaPreparacion;
    }

    public boolean isEditando() {
        return editando;
    }

    public void setEditando(boolean editando) {
        this.editando = editando;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getCodigoInternoGrilla() {
        return codigoInternoGrilla;
    }

    public void setCodigoInternoGrilla(String codigoInternoGrilla) {
        this.codigoInternoGrilla = codigoInternoGrilla;
    }

    // </SET_GET_ATRIBUTOS>
    public int getLecturaAnterior() {
        return lecturaAnterior;
    }

    public void setLecturaAnterior(int lecturaAnterior) {
        this.lecturaAnterior = lecturaAnterior;
    }

    public int getLecturaFinalViejo() {
        return lecturaFinalViejo;
    }

    public void setLecturaFinalViejo(int lecturaFinalViejo) {
        this.lecturaFinalViejo = lecturaFinalViejo;
    }

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMarca() {
        return listaMarca;
    }

    public void setListaMarca(List<Registro> listaMarca) {
        this.listaMarca = listaMarca;
    }

    public List<Registro> getListaCodigo1() {
        return listaCodigo1;
    }

    public void setListaCodigo1(List<Registro> listaCodigo1) {
        this.listaCodigo1 = listaCodigo1;
    }

    public RegistroDataModelImpl getListaAno() {
        return listaAno;
    }

    public void setListaAno(RegistroDataModelImpl listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaAnoE() {
        return listaAnoE;
    }

    public void setListaAnoE(RegistroDataModelImpl listaAnoE) {
        this.listaAnoE = listaAnoE;
    }

    public RegistroDataModelImpl getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(RegistroDataModelImpl listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public RegistroDataModelImpl getListaPeriodoE() {
        return listaPeriodoE;
    }

    public void setListaPeriodoE(RegistroDataModelImpl listaPeriodoE) {
        this.listaPeriodoE = listaPeriodoE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListacodigoruta1() {
        return listacodigoruta1;
    }

    public RegistroDataModelImpl getListacodigoruta1E() {
        return listacodigoruta1E;
    }

    public void setListacodigoruta1E(RegistroDataModelImpl listacodigoruta1e) {
        listacodigoruta1E = listacodigoruta1e;
    }

    public void setListacodigoruta1(RegistroDataModelImpl listacodigoruta1) {
        this.listacodigoruta1 = listacodigoruta1;
    }

    public RegistroDataModelImpl getListaCODIGORUTA() {
        return listaCODIGORUTA;
    }

    public void setListaCODIGORUTA(RegistroDataModelImpl listaCODIGORUTA) {
        this.listaCODIGORUTA = listaCODIGORUTA;
    }

    public RegistroDataModelImpl getListaCODIGORUTAE() {
        return listaCODIGORUTAE;
    }

    public void setListaCODIGORUTAE(RegistroDataModelImpl listaCODIGORUTAE) {
        this.listaCODIGORUTAE = listaCODIGORUTAE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
