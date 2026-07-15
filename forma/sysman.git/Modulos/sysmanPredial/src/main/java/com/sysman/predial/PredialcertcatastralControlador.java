package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PredialcertcatastralControladorEnum;
import com.sysman.predial.enums.PredialcertcatastralControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 09/06/2016
 *
 * -- Modificado por lcortes 02/03/2017 04:40 pm. Se incluyen
 * validaciones para los campos que no son obligatorios en la tabla
 * IP_USUARIOS_PREDIAL y que estan nulos pero que son necesarios para
 * generar la pantilla Word.
 * 
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 * 
 * @author asana
 * @version 4.0, 11/07/2017 Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class PredialcertcatastralControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;

    /** Constante a nivel de clase que aloja el valor CODIGO */
    private final String codigo;

    /**
     * Constante a nivel de clase que aloja el valor
     * MSM_TRANS_INTERRUMPIDA
     */
    private final String mensajeA;

    // <DECLARAR_ATRIBUTOS>
    private boolean verProp;
    private boolean chkVenta;
    private String codigoPredio;
    private String cedula;
    private String modeloPlantilla;
    private String nombreUsuario;
    private String expedida;
    private String numCer;
    private Date fechaExpedida;
    private String destino;
    private Double valor;
    private String direccion;
    private String areaHa;
    private String areaM;
    private String avaluoAnio;
    private String pagoAnio;
    private String sucursal;
    private String orden;
    private StreamedContent archivoDescarga;
    private boolean generarCertificadoANoRegistrados;
    private boolean visibleChkValor;
    private boolean visibleCopropietarios;
    private double porcentajeCobroCertificado;
    private double tarifaCertificadoCatastral;
    private Date fechaPlantilla;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigopredio;
    private RegistroDataModelImpl listacedula;
    private RegistroDataModelImpl listaplantilla;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public PredialcertcatastralControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigo = GeneralParameterEnum.CODIGO.getName();
        mensajeA = "MSM_TRANS_INTERRUMPIDA";

        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALCERTCATASTRAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            visibleChkValor = true;
            fechaExpedida = new Date();
            valor = tarifaCertificadoCatastral;
            visibleCopropietarios = true;
            chkVenta = false;
            orden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        }
        catch (Exception ex) {
            Logger.getLogger(PredialcertcatastralControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigopredio();
        cargarListacedula();
        cargarListaplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    public void initAdicional() {
        try {
            generarCertificadoANoRegistrados = "SI"
                            .equalsIgnoreCase(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "GENERAR CERTIFICADO A NO REGISTRADOS",
                                                            modulo,
                                                            new Date(), false),
                                            "NO").toString());
            tarifaCertificadoCatastral = Double.parseDouble(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "TARIFA CERTIFICADO CATASTRAL",
                                            modulo, new Date(),
                                            false),
                            "0").toString());
            porcentajeCobroCertificado = Double.parseDouble(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "PORCENTAJE COBRO CERTIFICADO",
                                            modulo, new Date(),
                                            false),
                            "0.0").toString());
            valor = tarifaCertificadoCatastral;

        }
        catch (SystemException e) {
            Logger.getLogger(PredialcertcatastralControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
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
        cargarListacodigopredio();
        cargarListacedula();
        cargarListaplantilla();
        initAdicional();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigopredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertcatastralControladorUrlEnum.URL7969
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(PredialcertcatastralControladorEnum.PARAM8.getValue(),
                        orden);

        listacodigopredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

    }

    public void cargarListacedula() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertcatastralControladorUrlEnum.URL8986
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialcertcatastralControladorEnum.PARAM8.getValue(), orden);
        param.put(PredialcertcatastralControladorEnum.PARAM12.getValue(),
                        codigoPredio);

        listacedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaplantilla() {
        String tipo = "12";
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertcatastralControladorUrlEnum.URL9817
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(PredialcertcatastralControladorEnum.PARAM0.getValue(), tipo);
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarChkVenta() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        try {
            if (codigoPredio != null) {

                if (chkVenta) {

                    valor = Double.valueOf(
                                    listacodigopredio
                                                    .getRegistroUnico(param)
                                                    .getCampos()
                                                    .get("AVALUO_ANO")
                                                    .toString())
                        * porcentajeCobroCertificado;

                    destino = "VENTA";
                    visibleCopropietarios = false;
                }
                else {
                    valor = tarifaCertificadoCatastral;
                    destino = "";
                    visibleCopropietarios = true;

                }

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB744"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigopredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoPredio = registroAux.getCampos().get(codigo).toString();
        direccion = registroAux.getCampos().get("DIRECCION") == null ? " "
            : registroAux.getCampos().get("DIRECCION").toString();
        areaHa = registroAux.getCampos().get("AREA_HA").toString();
        areaM = registroAux.getCampos().get("AREA_M2").toString();
        pagoAnio = registroAux.getCampos().get("PAGO_ANO") == null ? "0"
            : registroAux.getCampos().get("PAGO_ANO").toString();
        avaluoAnio = registroAux.getCampos().get("AVALUO_ANO").toString();
        cedula = nombreUsuario = null;
        orden = registroAux.getCampos().get("NUMERO_ORDEN").toString();
        cargarListacedula();
    }

    public void seleccionarFilacedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedula = registroAux.getCampos().get("NIT").toString();
        nombreUsuario = registroAux.getCampos().get("NOMBRE") == null ? " "
            : registroAux.getCampos().get("NOMBRE").toString();
        sucursal = registroAux.getCampos().get("SUCURSAL").toString();
    }

    public void seleccionarFilaplantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modeloPlantilla = registroAux.getCampos().get(codigo).toString();
        try {
            fechaPlantilla = SysmanFunciones.convertirAFecha(
                            registroAux.getCampos().get("FECHA").toString());
        }
        catch (ParseException e) {
            Logger.getLogger(PredialcertcatastralControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirCmdNoReg() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new TreeMap<>();
        parametros.put("FORMULARIO", "Cert");
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCERTNOREGISTRADOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPREVIA() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (chkVenta) {
            getInformeCertVenta(true);
        }
        else {
            getPlantillaWord(true);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirIMPRIMIR() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        consultarConsecutivo();
        registrarCertificado();
        if (chkVenta) {
            getInformeCertVenta(false);
        }
        else {
            getPlantillaWord(false);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void consultarConsecutivo() {

        try {
            String tipoCertificado = "C";

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialcertcatastralControladorEnum.PARAM15.getValue(),
                            tipoCertificado);

            Registro reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertcatastralControladorUrlEnum.URL14665
                                                                            .getValue())
                                            .getUrl(),
                            param));

            Map<String, Object> campos = new HashMap<>();
            if (reg != null) {
                campos = reg.getCampos();
            }
            if (!campos.isEmpty()) {
                numCer = String.valueOf(campos.get("CONSECUTIVOREAL"));
                if (!SysmanFunciones.validarVariableVacio(numCer)) {
                    String aux = "%0" + campos.get("DIGITOS") + "d";
                    numCer = String.format(aux, Integer.parseInt(numCer) + 1);

                    Map<String, Object> params = new TreeMap<>();
                    params.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    params.put(PredialcertcatastralControladorEnum.PARAM1
                                    .getValue(), numCer);
                    params.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());
                    params.put(PredialcertcatastralControladorEnum.PARAM15
                                    .getValue(), tipoCertificado);
                    Parameter parametros = new Parameter();
                    parametros.setFields(params);
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    PredialcertcatastralControladorUrlEnum.URL14667
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), parametros);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB967"));
                    return;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB968"));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeA) + e.getMessage());
        }

    }

    public void registrarCertificado() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialcertcatastralControladorEnum.PARAM1.getValue(),
                            numCer);
            param.put(PredialcertcatastralControladorEnum.PARAM14.getValue(),
                            codigoPredio);
            param.put(PredialcertcatastralControladorEnum.PARAM2.getValue(),
                            direccion);
            param.put(PredialcertcatastralControladorEnum.PARAM3.getValue(),
                            "0");
            param.put(PredialcertcatastralControladorEnum.PARAM4.getValue(),
                            areaHa);
            param.put(PredialcertcatastralControladorEnum.PARAM5.getValue(),
                            areaM);
            param.put(PredialcertcatastralControladorEnum.PARAM6.getValue(),
                            avaluoAnio);
            param.put(PredialcertcatastralControladorEnum.PARAM7.getValue(),
                            pagoAnio);
            param.put(GeneralParameterEnum.NOMBRE.getName(),
                            nombreUsuario);
            param.put(PredialcertcatastralControladorEnum.PARAM9.getValue(),
                            cedula);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            orden);
            param.put(PredialcertcatastralControladorEnum.PARAM11.getValue(),
                            fechaExpedida);
            param.put(GeneralParameterEnum.VALOR.getName(),
                            valor);
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);
            param.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PredialcertcatastralControladorUrlEnum.URL14666
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            param);
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeA)
                                + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    public void getInformeCertVenta(boolean esInformativo) {
        String reporte = "000895PREDIALCERTVENTA";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put("numeroOrden", orden);
            reemplazar.put("codigo", codigoPredio);
            reemplazar.put("nit", cedula);
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_NUMCER", esInformativo ? "" : numCer);
            parametros.put("PR_DESTINO", destino);
            parametros.put("PR_NOMBRE_TESORERO",
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "NOMBRE TESORERO",
                                                            modulo,
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_CARGO_TESORERO",
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "CARGO TESORERO",
                                                            modulo,
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_VALOR", valor);
            parametros.put("PR_EXPEDIDA", expedida);
            parametros.put("PR_ESINFORMATIVO", esInformativo);

            parametros.put("PR_FECHAVENCIMIENTO", SysmanFunciones
                            .sumarRestarMesesFecha(fechaExpedida, 1));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void getPlantillaWord(boolean esInformativo) {
        listaplantilla.load();
        if (listaplantilla.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB748"));
        }
        else {
            String strNombreDocumento = "certificadoCatastral";
            String[] campos = new String[3];
            String[] valores = new String[3];
            String firma = "";
            String cargo = "";
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = modeloPlantilla;
            valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
            valores[2] = strNombreDocumento;

            try {
                firma = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE TESORERO", modulo,
                                new Date(), false);
                cargo = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO TESORERO", modulo,
                                new Date(), false);
            }
            catch (SystemException e) {
                Logger.getLogger(
                                PredialcertcatastralControlador.class.getName())
                                .log(Level.SEVERE, null, e);

                JsfUtil.agregarMensajeError(e.getMessage());
            }

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s",
                            SysmanFunciones.concatenar("'", compania, "'"));
            variablesConsultaW.put("s$numeroOrden$s",
                            SysmanFunciones.concatenar("'", orden, "'"));
            variablesConsultaW.put("s$codigo$s",
                            SysmanFunciones.concatenar("'", codigoPredio, "'"));
            variablesConsultaW.put("s$nit$s",
                            SysmanFunciones.concatenar("'", cedula, "'"));
            variablesConsultaW.put("s$expedida$s",
                            SysmanFunciones.concatenar("'", expedida, "'"));
            variablesConsultaW.put("s$destino$s",
                            SysmanFunciones.concatenar("'", destino, "'"));
            variablesConsultaW.put("s$numCer$s", esInformativo ? "' '"
                : "'Certificado Nro: " + numCer + "'");
            variablesConsultaW.put("s$esInformativo$s", esInformativo
                ? "'" + idioma.getString("TB_TB2864") + "'" : "' '");
            variablesConsultaW.put("s$copropietarios$s",
                            verProp ? SysmanFunciones.concatenar("'",
                                            idioma.getString("TB_TB2865"), "'")
                                : "''");
            variablesConsultaW.put("s$companiaProp$s", verProp
                ? SysmanFunciones.concatenar("'", compania, "'") : "'-1'");
            variablesConsultaW.put("s$firma$s",
                            SysmanFunciones.concatenar("'", firma, "'"));
            variablesConsultaW.put("s$cargo$s",
                            SysmanFunciones.concatenar("'", cargo, "'"));

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                            .getCodigo()),
                            modulo,
                            campos, valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        numCer = "0"; // se pone con el fin de realizar pruebas de los
                      // reportes, se debe asignar como esta la
                      // codificación en acces
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    public boolean getChkVenta() {
        return chkVenta;
    }

    public void setChkVenta(boolean chkVenta) {
        this.chkVenta = chkVenta;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getExpedida() {
        return expedida;
    }

    public void setExpedida(String expedida) {
        this.expedida = expedida;
    }

    public String getNumCer() {
        return numCer;
    }

    public void setNumCer(String numCer) {
        this.numCer = numCer;
    }

    public Date getFechaExpedida() {
        return fechaExpedida;
    }

    public void setFechaExpedida(Date fechaExpedida) {
        this.fechaExpedida = fechaExpedida;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isGenerarCertificadoANoRegistrados() {
        return generarCertificadoANoRegistrados;
    }

    public void setGenerarCertificadoANoRegistrados(
        boolean generarCertificadoANoRegistrados) {
        this.generarCertificadoANoRegistrados = generarCertificadoANoRegistrados;
    }

    public boolean isVisibleChkValor() {
        return visibleChkValor;
    }

    public void setVisibleChkValor(boolean visibleChkValor) {
        this.visibleChkValor = visibleChkValor;
    }

    public double getPorcentajeCobroCertificado() {
        return porcentajeCobroCertificado;
    }

    public void setPorcentajeCobroCertificado(
        double porcentajeCobroCertificado) {
        this.porcentajeCobroCertificado = porcentajeCobroCertificado;
    }

    public double getTarifaCertificadoCatastral() {
        return tarifaCertificadoCatastral;
    }

    public void setTarifaCertificadoCatastral(
        double tarifaCertificadoCatastral) {
        this.tarifaCertificadoCatastral = tarifaCertificadoCatastral;
    }

    public Date getFechaPlantilla() {
        return fechaPlantilla;
    }

    public void setFechaPlantilla(Date fechaPlantilla) {
        this.fechaPlantilla = fechaPlantilla;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListacodigopredio() {
        return listacodigopredio;
    }

    public void setListacodigopredio(RegistroDataModelImpl listacodigopredio) {
        this.listacodigopredio = listacodigopredio;
    }

    public RegistroDataModelImpl getListacedula() {
        return listacedula;
    }

    public void setListacedula(RegistroDataModelImpl listacedula) {
        this.listacedula = listacedula;
    }

    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }

    public boolean isVerProp() {
        return verProp;
    }

    public void setVerProp(boolean verProp) {
        this.verProp = verProp;
    }

    public boolean isVisibleCopropietarios() {
        return visibleCopropietarios;
    }

    public void setVisibleCopropietarios(boolean visibleCopropietarios) {
        this.visibleCopropietarios = visibleCopropietarios;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
