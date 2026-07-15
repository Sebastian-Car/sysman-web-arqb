package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.FrminfautoavaluosControladorEnum;
import com.sysman.predial.enums.FrminfdescuentosacasControladorUrlEnum;
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
 * @author ybecerra
 * @version 1, 02/06/2016
 * 
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario ademas se
 * ajusta conexion.
 * 
 * @modifier amonroy
 * @version 2, 04/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class FrminfdescuentosacasControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String strCodigo;
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;
    /**
     * Implementacion del EJB de EjbPredialCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL
     */
    @EJB
    private EjbPredialCeroRemote ejbPredialCeroRemote;

    /**
     * Creates a new instance of FrminfdescuentosacasControlador
     */
    public FrminfdescuentosacasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigo = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFDESCUENTOSACAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrminfdescuentosacasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Integer ano = SysmanFunciones.ano(new Date());

        try {
            fechaInicial = SysmanFunciones.convertirAFecha("01/01/" + ano);
            fechaFinal = new Date();
        }
        catch (ParseException e) {
            Logger.getLogger(FrminfdescuentosacasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfdescuentosacasControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);

    }

    public void cargarListaCodFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfdescuentosacasControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrminfautoavaluosControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        String reporte = "000857INFDESCUENTOSACA";

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");

            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CODIGOS", "ENTRE LOS CODIGOS " + codigoInicial
                + " Y " + codigoFinal);
            parametros.put("PR_FECHAS", "Y ENTRE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_CONCEPTO1", obtenerNombre(1));
            parametros.put("PR_CONCEPTO2", obtenerNombre(2));
            parametros.put("PR_CONCEPTO3", obtenerNombre(3));
            parametros.put("PR_CONCEPTO4", obtenerNombre(4));
            parametros.put("PR_CONCEPTO13", obtenerNombre(13));
            parametros.put("PR_CONCEPTO14", obtenerNombre(14));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } //

    }

    private String obtenerNombre(int numero) {
        String nombreEncabezado = null;
        try {
            nombreEncabezado = ejbPredialCeroRemote
                            .consultarEncabezadoDeColumna(compania, numero);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return nombreEncabezado;
    }

    public void seleccionarFilaCodInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodFinal();
    }

    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), "")
                        .toString();
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

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
}
