package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrminfexentosControladorEnum;
import com.sysman.predial.enums.FrminfexentosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 10/06/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 * @author eamaya
 * @version 3.0, 4/07/2017 Proceso de Refactoring DSS, cambio de
 * Sysdate por new Date(), correcciones SonarLint y cambio de textos
 * quemados por texto en bean
 * 
 */
@ManagedBean
@ViewScoped

public class FrminfexentosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreCodIncial;
    private String nombreCodFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrminfexentosControlador
     */
    public FrminfexentosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFEXENTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrminfexentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfexentosControladorUrlEnum.URL3877
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfexentosControladorUrlEnum.URL4776
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        param.put(FrminfexentosControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ((fechaInicial == null) || (fechaFinal == null)
            || SysmanFunciones.validarVariableVacio(codigoInicial)
            || SysmanFunciones.validarVariableVacio(codigoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB612"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB613"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ((fechaInicial == null) || (fechaFinal == null)
            || SysmanFunciones.validarVariableVacio(codigoInicial)
            || SysmanFunciones.validarVariableVacio(codigoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB612"));
            return;
        }
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB613"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        String nombreArchivo = "prediosexentossincalcular.txt";
        String texto;
        StringBuilder bld = new StringBuilder();

        String encabezado;
        try {
            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            List<Registro> rs =

            RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfexentosControladorUrlEnum.URL7697
                                                                            .getValue())
                                            .getUrl(), param));

            if (!rs.isEmpty()) {
                bld.append(idioma.getString("TB_TB3278") + "\r\n\r\n");
                for (Registro registro : rs) {
                    bld.append(idioma.getString("TB_TB3279")
                        + registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName())
                        + " "
                        + idioma.getString("TB_TB3280")
                        + registro.getCampos().get("NIT") + " "
                        + idioma.getString("TG_USUARIO3")
                        + registro.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName())
                        + " \r\n");
                }
            }
            HashMap<String, Object> reemplazos = new HashMap<>();
            HashMap<String, Object> parametros = new HashMap<>();

            // Reemplazos valores consulta reporte
            reemplazos.put("codInicial", codigoInicial);
            reemplazos.put("codFinal", codigoFinal);
            reemplazos.put("fechaIni",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFin",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            // Inicio Parï¿½metros Reporte
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CODINICIAL", codigoInicial);
            parametros.put("PR_CODFINAL", codigoFinal);
            //

            parametros.put("PR_FECHAINI", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            for (int i = 1; i <= 20; i++) {
                if ((i < 5) || (i > 12)) {
                    encabezado = encabezadoColumna(i);
                    parametros.put("PR_ENCABEZADOCOLUMNC" + i, encabezado);
                }
            }

            String reporte = "000897INFEXENTOS";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
            texto = bld.toString();
            salidas[0] = JsfUtil.serializarPlano(texto);
            salidas[1] = JsfUtil.serializarReporte(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            String[] nombresArchivos = new String[2];
            nombresArchivos[0] = nombreArchivo;
            if (formato == ReportesBean.FORMATOS.PDF) {
                nombresArchivos[1] = "000897INFEXENTOS.pdf";

            }
            else {
                nombresArchivos[1] = "000897INFEXENTOS.xls";

            }

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,
                            nombresArchivos);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String encabezadoColumna(int concepto) {
        String encabezado = null;

        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), concepto);

        Registro regAux;
        try {
            regAux = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrminfexentosControladorUrlEnum.URL2892
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            if (regAux != null) {

                if ("800095728-2".equals(
                                SessionUtil.getCompaniaIngreso().getNit())) {
                    encabezado = SysmanFunciones.nvl(
                                    regAux.getCampos()
                                                    .get(GeneralParameterEnum.NOMBRE
                                                                    .getName()),
                                    " ").toString();

                }
                else {

                    encabezado = SysmanFunciones.nvl(
                                    regAux.getCampos()
                                                    .get("ENCABEZADO"),
                                    regAux.getCampos()
                                                    .get(GeneralParameterEnum.CODIGO
                                                                    .getName()))
                                    .toString();
                    encabezado = encabezado == null ? " " : encabezado;

                }

                if (".".equals(encabezado) || ",".equals(encabezado)) {
                    encabezado = " ";
                }
            }
            else {
                encabezado = " ";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return encabezado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodInicial(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodIncial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        codigoFinal = null;
        nombreCodFinal = null;
        cargarListaCodFinal();
    }

    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getNombreCodIncial() {
        return nombreCodIncial;
    }

    public void setNombreCodIncial(String nombreCodIncial) {
        this.nombreCodIncial = nombreCodIncial;
    }

    public String getNombreCodFinal() {
        return nombreCodFinal;
    }

    public void setNombreCodFinal(String nombreCodFinal) {
        this.nombreCodFinal = nombreCodFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodInicial() {
        return listaCodInicial;
    }

    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial) {
        this.listaCodInicial = listaCodInicial;
    }

    public RegistroDataModelImpl getListaCodFinal() {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal) {
        this.listaCodFinal = listaCodFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
