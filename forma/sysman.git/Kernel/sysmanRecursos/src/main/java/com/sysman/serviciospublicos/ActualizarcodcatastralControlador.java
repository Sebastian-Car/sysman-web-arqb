package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.reportes.Reporteador;

import java.io.IOException;
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

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 22/08/2016
 * @version 2, 10/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuracion del controlador :*Refactoring
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class ActualizarcodcatastralControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena la opcion
     */
    private String opcion;
    /**
     * variable que almacena el ciclo
     */
    private String ciclo;
    /**
     * variable que almacen el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que lista los ciclos
     */
    private List<Registro> listaCiclo;
    /**
     * variable que almacena la url para cargar la lista ciclos
     */
    private static final String URL = "214007";
    /**
     * variable que almacena el ciclo
     */
    private static final String CICLO_E = "CICLO";
    /**
     * variable que almacena el numero del formulario a redireccionar
     */
    private static final String FORMULARIO1051 = "1051";
    /**
     * variable que almacena el numero del formulario a redireccionar
     */
    private static final String FORMULARIO1056 = "1056";
    /**
     * variable que almacena el prametro ciclo
     */
    private static final String PR_CICLO = "PR_CICLO";
    /**
     * variable que almacena la url de actualizacion
     */
    private static final String URLUPDATE = "213003";
    /**
     * variable que alamcena el nombre del reporte
     */
    private static final String REPORTE001044 = "001044Usuariosconcatastrorepetido";

    /**
     * Creates a new instance of ActualizarcodcatastralControlador
     */
    public ActualizarcodcatastralControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARCODCATASTRAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            ciclo = "1";
            // <INI_ADICIONAL>
            opcion = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarcodcatastralControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que es llamado para inicializar las listaas y abrir el
     * formulario
     */
    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * meotod que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que carga la lista de ciclos
     */
    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        URL);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que es llamado cuando se oprime el boton aceptar
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(CICLO_E, ciclo);
        switch (opcion) {
        case "1":
            redireccionar(FORMULARIO1051, parametros);
            break;
        case "2":
            actualizarSPUSUARIO();
            break;
        case "3":
            generarinformeRepetidos();
            break;
        case "4":
            redireccionar(FORMULARIO1056, parametros);
            break;
        default:
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo es llamado cuando se quiere actualizar el
     * CODIGOCATASTRAL
     */
    private void actualizarSPUSUARIO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(URLUPDATE);

        HashMap<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        params.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        params.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        Parameter parameter = new Parameter();
        parameter.setFields(params);
        try {
            int miRpt = requestManager.update(urlBean.getUrl(),
                            urlBean.getMetodo(), parameter);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3140")
                            .replace("s$cantidad$s", String.valueOf(miRpt)));
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    /**
     * metodo que redirecciona a un formulario
     * 
     * @param numFormulario
     * @param parametros
     */
    private void redireccionar(String numFormulario,
        Map<String, Object> parametros) {
        Direccionador dir = new Direccionador();
        dir.setNumForm(numFormulario);
        dir.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(dir);
    }

    /**
     * metodo que contiene la logica para generar el reporte en
     * formato pdf
     */
    public void generarinformeRepetidos() {

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(CICLO_E.toLowerCase(), ciclo);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(REPORTE001044,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put(PR_CICLO, ciclo);

            archivoDescarga = JsfUtil.exportarStreamed(REPORTE001044,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
}
