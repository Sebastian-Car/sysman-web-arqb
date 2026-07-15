package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.CertificadoEstratoControladorEnum;
import com.sysman.predial.enums.CertificadoEstratoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author jrodriguezr
 * @version 1, 31/05/2016
 * @author jcrodriguez
 * @version 2, 27/06/2017
 * @descripcion Depuracion del controaldor y Refactoring
 */
@ManagedBean
@ViewScoped
public class CertificadoEstratoControlador extends BeanBaseDatosAcme {
    private final String compania;
    private String codigoPredio;
    private String nitPropietario;
    private String nombrePropietario;
    private RegistroDataModelImpl listaTCodigo;
    private String formatoEstrato;
    private String direccionPredio;
    private String estratoPredio;
    private String sucursalPropietario;
    private String permiteExpCert;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    public CertificadoEstratoControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.CERTIFICADO_ESTRATO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CertificadoEstratoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {

        cargarListaTCodigo();
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
        reasignarOrigenGrilla();
        cargarListaTCodigo();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    /**
     * metodo que carga lal ista de codigos
     */
    public void cargarListaTCodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoEstratoControladorUrlEnum.URL3339
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaTCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo utilizado para seleccionar un registro de un combo
     * grande
     * 
     * @param event
     */
    public void seleccionarFilaTCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoPredio = SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName()) ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        nombrePropietario = SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(), "NOMBRE") ? ""
                            : registroAux.getCampos().get("NOMBRE").toString();
        nitPropietario = SysmanFunciones
                        .validarCampoVacio(registroAux.getCampos(), "NIT") ? ""
                            : registroAux.getCampos().get("NIT").toString();
        sucursalPropietario = SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(), "SUCURSAL") ? ""
                            : registroAux.getCampos().get("SUCURSAL")
                                            .toString();
        direccionPredio = SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(), "DIRECCION") ? ""
                            : registroAux.getCampos().get("DIRECCION")
                                            .toString();
        estratoPredio = SysmanFunciones.validarCampoVacio(
                        registroAux.getCampos(), "ESTRATO_SOCIOECONOMICO") ? ""
                            : registroAux.getCampos()
                                            .get("ESTRATO_SOCIOECONOMICO")
                                            .toString();
        permiteExpCert = registroAux.getCampos().get("PERMITE_EXP_CERT") == null
            ? ""
            : registroAux.getCampos().get("PERMITE_EXP_CERT").toString();
        formatoEstrato = registroAux.getCampos().get("FORMATO_ESTRATO") == null
            ? ""
            : registroAux.getCampos().get("FORMATO_ESTRATO").toString();

    }

    /**
     * metodo que se llama al oprimir en el boton generar
     */
    public void oprimirCmdGenerar() {
        try {
            if (codigoPredio == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB733"));
            }
            if ((permiteExpCert == null) || "".equals(permiteExpCert)
                || "0".equals(permiteExpCert)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB734"));
            }

            Map<String, Object> paramFecha = new TreeMap<>();
            paramFecha.put("TIPO", "19");
            paramFecha.put(GeneralParameterEnum.CODIGO.getName(), "CE1");
            paramFecha.put("FECHAGENERACION", new Date());
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoEstratoControladorUrlEnum.URL3341
                                                                            .getValue())
                                            .getUrl(), paramFecha));

            Date fecha;
            if (!SysmanFunciones.validarCampoVacio(reg.getCampos(),
                            GeneralParameterEnum.FECHA.getName())) {
                fecha = (Date) reg.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName());
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2794"));
                return;
            }

            if (SysmanFunciones.validarVariableVacio(formatoEstrato)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB735"));
            }
            else {
                // <CODIGO_DESARROLLADO>

                String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                                compania,
                                "'' AND  TIPO = ''E'' AND ACTIVO NOT IN (0) ");

                long consecutivo = ejbSysmanUtilRemote
                                .generarSiguienteConsecutivo(
                                                "IP_NUMEROSDEFACTURA",
                                                criterio,
                                                "CONSECUTIVOREAL");

                UrlBean url = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                CertificadoEstratoControladorUrlEnum.URL3322
                                                                .getValue());
                HashMap<String, Object> param = new HashMap<>();
                param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivo);
                param.put(GeneralParameterEnum.FORMATO.getName(),
                                formatoEstrato);
                param.put(CertificadoEstratoControladorEnum.CODIGOPREDIO
                                .getValue(),
                                codigoPredio);
                param.put(CertificadoEstratoControladorEnum.NITPROPIETARIO
                                .getValue(), nitPropietario);
                param.put(CertificadoEstratoControladorEnum.SUCURSALPROPIETARIO
                                .getValue(), sucursalPropietario);
                param.put(CertificadoEstratoControladorEnum.DIRECCIONPREDIO
                                .getValue(), direccionPredio);
                param.put(CertificadoEstratoControladorEnum.ESTRATOPREDIO
                                .getValue(), estratoPredio);
                param.put(CertificadoEstratoControladorEnum.FECHA_IMPRESO
                                .getValue(), new Date());
                param.put(CertificadoEstratoControladorEnum.HORA_IMPRESO
                                .getValue(),
                                new Date());
                param.put(GeneralParameterEnum.USUARIO.getName(),
                                SessionUtil.getUser().getCodigo());
                param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                param.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                requestManager.save(url.getUrl(), url.getMetodo(), param);

                Map<String, Object> paramNumFactura = new HashMap<>();
                paramNumFactura.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramNumFactura.put("NUMCER", consecutivo);
                paramNumFactura.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                paramNumFactura.put("TIPOCERTIFICADO", "E");

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                CertificadoEstratoControladorUrlEnum.URL3340
                                                                .getValue());
                Parameter parameter = new Parameter();
                parameter.setFields(paramNumFactura);

                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);

                StringBuilder strNombreDocumento = new StringBuilder();
                strNombreDocumento.append(idioma.getString("TB_TB3306"));
                strNombreDocumento.append(consecutivo);

                String[] campos = new String[3];
                String[] valores = new String[3];
                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";

                valores[0] = "CE1";
                valores[1] = SysmanFunciones.formatearFecha(fecha);
                valores[2] = strNombreDocumento.toString();

                HashMap<String, String> variablesConsultaW = new HashMap<>();
                variablesConsultaW.put("s$compania$s", "'" + compania + "'");
                variablesConsultaW.put("s$codigoPredio$s",
                                "'" + codigoPredio + "'");
                variablesConsultaW.put("s$numeroOrden$s",
                                "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL
                                    + "'");
                variablesConsultaW.put("s$consecutivo$s",
                                "'" + consecutivo + "'");
                // variables por parametro para documento word
                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);
                SessionUtil.cargarModalDatosFlash(
                                String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(),
                                campos, valores);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
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
    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getNitPropietario() {
        return nitPropietario;
    }

    public void setNitPropietario(String nitPropietario) {
        this.nitPropietario = nitPropietario;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public RegistroDataModelImpl getListaTCodigo() {
        return listaTCodigo;
    }

    public void setListaTCodigo(RegistroDataModelImpl listaTCodigo) {
        this.listaTCodigo = listaTCodigo;
    }
}
