package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
import com.sysman.predial.enums.CartacobrosogControladorEnum;
import com.sysman.predial.enums.CartacobrosogControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author acaceres
 * @version 1, 31/05/2016
 *
 * @modifier sdaza
 * @version 2, 28/03/2016<br>
 * - inicializar chkResol en true, bloquear el objeto grafico de esta
 * variable <br>
 * - Eliminar codigo migrado de access que no se requiere
 * 
 * @modifier amonroy
 * @version 3, 23/06/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones que son llamadas en el
 * controlador. <br>
 * - Se eliminan los atributos lblResol y txtResol que se utilizaban
 * en campos que no se encontraban visibles en el formulario. <br>
 * - Se elimina el llamado al parametro
 * "NUMERO RESOLUCION CARTA DE COBRO" que ya no estaba siendo
 * utilizado
 */
@ManagedBean
@ViewScoped
public class CartacobrosogControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    private Boolean chkResol;
    private String codigoInicial;
    private String codigoFinal;
    private String anoInicial;
    private String anoFinal;
    private String anosDeuda;
    private String deudas;
    private String plantilla;
    private String nombrePlantilla;
    private Date fechaPlantilla;
    private Boolean chkResolVisible;
    private Boolean conResolucionVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoInicial;
    private List<Registro> listaAnoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaplantilla;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_PAR
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbPredialSieteRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL_COM7
     */
    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;

    // </DECLARAR_ADICIONALES>
    public CartacobrosogControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CARTACOBROSOG_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CartacobrosogControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CartacobrosogControladorUrlEnum.URL4517
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CartacobrosogControladorUrlEnum.URL5032
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CartacobrosogControladorUrlEnum.URL5550
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CartacobrosogControladorUrlEnum.URL6819
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(CartacobrosogControladorEnum.MICODIGO.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaplantilla() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CartacobrosogControladorUrlEnum.URL8228
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CartacobrosogControladorEnum.TIPO.getValue(), 20);
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "FECHA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     * 
     * Recarga el listado de registros para el anio final
     * 
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarChkResol() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(cod).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(cod).toString();
    }

    public void seleccionarFilaplantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantilla = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "")
                        .toString();
        nombrePlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        try {
            fechaPlantilla = SysmanFunciones.convertirAFecha(
                            registroAux.getCampos().get("FECHA").toString());
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>

        try {
            String[] campos = new String[3];
            String[] valores = new String[3];

            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = plantilla;
            valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
            valores[2] = nombrePlantilla;

            String nombreTesorero = getParametro("NOMBRE TESORERO", "");
            String cargoTesorero = getParametro("CARGO TESORERO", "");

            HashMap<String, String> variablesConsultaW = new HashMap<>();

            variablesConsultaW.put("s$ciudadCompania$s", "'"
                + SessionUtil.getCompaniaIngreso().getCiudad() + "'");
            variablesConsultaW.put("s$ahora$s",
                            "'" + SysmanFunciones.convertirAFechaCadena(
                                            new Date(),
                                            "EEEEE, dd ' de ' MMMMM yyyy")
                                + "'");
            variablesConsultaW.put("s$nombreTesorero$s",
                            "'" + nombreTesorero + "'");
            variablesConsultaW.put("s$cargoTesorero$s",
                            "'" + cargoTesorero + "' ");
            variablesConsultaW.put("s$codigoInicial$s",
                            "'" + codigoInicial + "'");
            variablesConsultaW.put("s$codigoFinal$s", "'" + codigoFinal + "'");
            variablesConsultaW.put("s$numeroOrden$s",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            variablesConsultaW.put("s$anosDeuda$s", "'" + anosDeuda + "'");
            variablesConsultaW.put("s$deudas$s", "'" + deudas + "'");

            String consecutivos = ejbPredialSiete.actualizarNotificaPredial(
                            compania,
                            codigoInicial,
                            codigoFinal,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            SessionUtil.getUser().getCodigo(),
                            anosDeuda,
                            Integer.parseInt(deudas));

            if (consecutivos.length() >= 18) {
                variablesConsultaW.put("s$resolucionInicial$s",
                                "'" + consecutivos.substring(0, 9) + "'");
                variablesConsultaW.put("s$resolucionFinal$s",
                                "'" + consecutivos.substring(10, 19) + "'");
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                return;
            }

            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos, valores);

        }

        catch (ParseException | NumberFormatException | SystemException e) {
            Logger.getLogger(CartacobrosogControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        deudas = "0";
        anosDeuda = "0";
        chkResol = true;
        String parametro = getParametro("IMPRIMIR RESOLUCION CARTA DE COBRO",
                        null);

        if (parametro == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3260"));
            return;
        }

        chkResolVisible = "SI".equals(parametro) ? true : false;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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

    // <SET_GET_ATRIBUTOS>

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public Boolean getChkResol() {
        return chkResol;
    }

    public void setChkResol(Boolean chkResol) {
        this.chkResol = chkResol;
    }

    public Boolean getChkResolVisible() {
        return chkResolVisible;
    }

    public void setChkResolVisible(Boolean chkResolVisible) {
        this.chkResolVisible = chkResolVisible;
    }

    public Boolean getConResolucionVisible() {
        return conResolucionVisible;
    }

    public void setConResolucionVisible(Boolean conResolucionVisible) {
        this.conResolucionVisible = conResolucionVisible;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public String getAnosDeuda() {
        return anosDeuda;
    }

    public void setAnosDeuda(String anosDeuda) {
        this.anosDeuda = anosDeuda;
    }

    public String getDeudas() {
        return deudas;
    }

    public void setDeudas(String deudas) {
        this.deudas = deudas;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

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

    /**
     * @return the listaplantilla
     */
    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    /**
     * @param listaplantilla
     * the listaplantilla to set
     */
    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
