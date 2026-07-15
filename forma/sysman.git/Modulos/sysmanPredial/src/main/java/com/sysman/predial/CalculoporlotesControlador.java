package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.CalculoporlotesControladorEnum;
import com.sysman.predial.enums.CalculoporlotesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APICalculoPredial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 21/05/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 * @version 3.0, 22/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Aplicar recomendaciones de SonarLint.
 * 
 */
@ManagedBean
@ViewScoped
public class CalculoporlotesControlador extends BeanBaseModal {
    private final String compania;

    private final String nitCompania;

    /**
     * Constante a nivel de clase que guarda el numero del orden
     * predial <code>SysmanConstantes.NUMERO_ORDEN_PREDIAL</code>
     */
    private final String numeroOrden;

    /**
     * Constante a nivel de clase que guarda el nombre del enumerado
     * <code>GeneralParameterEnum.NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que guarda el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que guarda el nombre del enumerado
     * <code>CalculoporlotesControladorEnum.NIT</code>
     */
    private final String cNit;

    /**
     * Constante que identifica el servicio que busca la URL y tipo de
     * conexión
     */
    private static final String SERVICIO_API = "1710001";

    // <DECLARAR_ATRIBUTOS>

    private boolean aplica1066;
    private boolean aplica1175;
    private String codigoInicial;
    private String codigoFinal;
    private String cedulaInicial;
    private String cedulaFinal;
    private String nombreIncial;
    private String nombreFinal;
    private String tipo;
    private Date fecha;
    private String textoUno;
    private String textoDos;
    private String usuario;
    private boolean visibleCodigo;
    private boolean visibleCedula;
    private boolean visibleNombre;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCmbCedulaInicial;
    private RegistroDataModelImpl listaCmbCedulaFinal;
    private RegistroDataModelImpl listaCmbNombreInicial;
    private RegistroDataModelImpl listaCmbNombreFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Object archivoDescarga;

    /**
     * Creates a new instance of CalculoporlotesControlador
     */
    public CalculoporlotesControlador() {
        super();

        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cNit = CalculoporlotesControladorEnum.NIT.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.CALCULOPORLOTES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            initAdicional();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CalculoporlotesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void initAdicional() {
        tipo = "1";
        visibleCodigo = true;
        fecha = new Date();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbNombreInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL5707
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaCmbNombreInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNombre);
    }

    public void cargarListaCmbNombreFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL6542
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        param.put(CalculoporlotesControladorEnum.MINOMBRE.getValue(),
                        nombreIncial);

        listaCmbNombreFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNombre);
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL7506
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL8844
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        param.put(CalculoporlotesControladorEnum.MICODIGO.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCmbCedulaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL10361
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        listaCmbCedulaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    public void cargarListaCmbCedulaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculoporlotesControladorUrlEnum.URL11332
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);

        param.put(CalculoporlotesControladorEnum.MINIT.getValue(),
                        cedulaInicial);

        listaCmbCedulaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        archivoDescarga = null;
        StringBuilder salida = null;
        ByteArrayInputStream streamTexto;
        try {

            String url = armarUrl();

            APICalculoPredial api = new APICalculoPredial();

            salida = api.calcular(compania, nitCompania,
                            SysmanFunciones.convertirAFechaCadena(fecha,
                                            "yyyy-MM-dd'T'HH:mm:ss-0500"),
                            0, aplica1175, false,
                            codigoInicial, codigoFinal, "001", "999",
                            usuario, "B", url);

            if (!SysmanFunciones.validarVariableVacio(salida.toString())) {

                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB4329"));

                streamTexto = JsfUtil.serializarPlano(salida.toString());

                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                                "Alertas.txt");

            }
            else {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
        }
        catch (IOException | SysmanException | ParseException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String armarUrl() throws SysmanException {

        String url = "";

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), "100");

        Registro rs = new Registro();
        RequestManager requestManager = new RequestManager();
        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getUrlBeanById(SERVICIO_API)
                                            .getUrl(),
                            parametros));
        }
        catch (NullPointerException | SystemException e) {
            throw new SysmanException(idioma.getString("TB_TB4230"));

        }
        if (rs == null) {
            throw new SysmanException(idioma.getString("TB_TB4232"));
        }
        else if (rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString() == null) {
            throw new SysmanException(idioma.getString("TB_TB4231"));
        }
        url = rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString();
        return url;
    }

    public void getInforme(FORMATOS formato) {
        try {
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("numeroOrden", numeroOrden);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();

            String reporte = "000807PREDIALERRORYAQUE";

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiaropttipo() {
        // <CODIGO_DESARROLLADO>
        validarTipoCalculo();

        textoUno = textoDos = "";

        /* Gestiona visibilidad de componentes */
        visibleCodigo = "1".equals(tipo);
        visibleCedula = "2".equals(tipo);
        visibleNombre = "3".equals(tipo);

        switch (tipo) {
        case "2":
            cedulaInicial = cedulaFinal = "";
            listaCmbCedulaInicial = listaCmbCedulaFinal = null;

            cargarListaCmbCedulaInicial();
            break;
        case "3":
            listaCmbNombreInicial = listaCmbNombreFinal = null;

            cargarListaCmbNombreInicial();
            break;
        default:
            codigoInicial = codigoFinal = "";
            listaCodigoInicial = listaCodigoFinal = null;

            cargarListaCodigoInicial();
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Valida que el valor seleccionado en el combo:
     * <code>Calcular Entre</code> sea diferente de nulo.
     */
    private void validarTipoCalculo() {
        if (SysmanFunciones.validarVariableVacio(tipo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3252"));
            tipo = "1";
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        textoUno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        codigoFinal = null;
        textoDos = null;

        listaCodigoFinal = null;

        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        textoDos = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    public void seleccionarFilaCmbCedulaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cedulaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNit), "").toString();

        textoUno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        cedulaFinal = null;
        textoDos = null;

        cargarListaCmbCedulaFinal();
    }

    public void seleccionarFilaCmbCedulaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cedulaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(cNit), "")
                        .toString();

        textoDos = SysmanFunciones.nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    public void seleccionarFilaCmbNombreInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        nombreIncial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        nombreFinal = null;

        cargarListaCmbNombreFinal();
    }

    public void seleccionarFilaCmbNombreFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public boolean isAplica1066() {
        return aplica1066;
    }

    public void setAplica1066(boolean aplica1066) {
        this.aplica1066 = aplica1066;
    }

    public boolean isAplica1175() {
        return aplica1175;
    }

    public void setAplica1175(boolean aplica1175) {
        this.aplica1175 = aplica1175;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCedulaInicial() {
        return cedulaInicial;
    }

    public void setCedulaInicial(String cedulaInicial) {
        this.cedulaInicial = cedulaInicial;
    }

    public String getCedulaFinal() {
        return cedulaFinal;
    }

    public void setCedulaFinal(String cedulaFinal) {
        this.cedulaFinal = cedulaFinal;
    }

    public String getNombreIncial() {
        return nombreIncial;
    }

    public void setNombreIncial(String nombreIncial) {
        this.nombreIncial = nombreIncial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTextoUno() {
        return textoUno;
    }

    public void setTextoUno(String textoUno) {
        this.textoUno = textoUno;
    }

    public String getTextoDos() {
        return textoDos;
    }

    public void setTextoDos(String textoDos) {
        this.textoDos = textoDos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isVisibleCodigo() {
        return visibleCodigo;
    }

    public void setVisibleCodigo(boolean visibleCodigo) {
        this.visibleCodigo = visibleCodigo;
    }

    public boolean isVisibleCedula() {
        return visibleCedula;
    }

    public void setVisibleCedula(boolean visibleCedula) {
        this.visibleCedula = visibleCedula;
    }

    public boolean isVisibleNombre() {
        return visibleNombre;
    }

    public void setVisibleNombre(boolean visibleNombre) {
        this.visibleNombre = visibleNombre;
    }

    public Object getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(Object archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public RegistroDataModelImpl getListaCmbCedulaInicial() {
        return listaCmbCedulaInicial;
    }

    public void setListaCmbCedulaInicial(
        RegistroDataModelImpl listaCmbCedulaInicial) {
        this.listaCmbCedulaInicial = listaCmbCedulaInicial;
    }

    public RegistroDataModelImpl getListaCmbCedulaFinal() {
        return listaCmbCedulaFinal;
    }

    public void setListaCmbCedulaFinal(
        RegistroDataModelImpl listaCmbCedulaFinal) {
        this.listaCmbCedulaFinal = listaCmbCedulaFinal;
    }

    public RegistroDataModelImpl getListaCmbNombreInicial() {
        return listaCmbNombreInicial;
    }

    public void setListaCmbNombreInicial(
        RegistroDataModelImpl listaCmbNombreInicial) {
        this.listaCmbNombreInicial = listaCmbNombreInicial;
    }

    public RegistroDataModelImpl getListaCmbNombreFinal() {
        return listaCmbNombreFinal;
    }

    public void setListaCmbNombreFinal(
        RegistroDataModelImpl listaCmbNombreFinal) {
        this.listaCmbNombreFinal = listaCmbNombreFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
