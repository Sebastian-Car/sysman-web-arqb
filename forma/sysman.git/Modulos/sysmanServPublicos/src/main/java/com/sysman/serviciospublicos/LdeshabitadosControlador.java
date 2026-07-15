package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LdeshabitadosControladorEnum;
import com.sysman.serviciospublicos.enums.LdeshabitadosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author cperez
 * @version 1, 01/01/2011
 * @author jcrodriguez
 * @version 2, 05/06/2017=>Refactoring, creacion de dss y depuracion del controlador
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class LdeshabitadosControlador extends BeanBaseModal {
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el ciclo
     */
    private String ciclo;
    /**
     * variable que almacena el consumo
     */
    private String consumo;
    /**
     * variable que almacena el estado
     */
    private boolean consumoVisible;
    /**
     * variable que almacena el archivo de descarga reporte
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que lista los ciclos
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * EJB
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of LdeshabitadosControlador
     */
    public LdeshabitadosControlador() {

        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.LDESHABITADOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LdeshabitadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * heredado del bean padre
     */
    @PostConstruct
    public void inicializar() {
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * heredado del bean padre
     */
    @Override
    public void abrirFormulario() {
        consumo = "0";
        if ("SI".equals(SysmanFunciones.nvl(getParametro(LdeshabitadosControladorEnum.FORMATO_CALIDAD.getValue(),
                        true),
                        "NO"))) {
            consumoVisible = true;
        }
        else {
            consumoVisible = false;
        }
    }

    /**
     * metodo que se llama para consultar un parametro del sistema
     * 
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtilRemote.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e) {
            Logger.getLogger(LdeshabitadosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * metodo que carga la lista de ciclos
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LdeshabitadosControladorUrlEnum.URL4233.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirExcel() {
        seleccionarReporte(FORMATOS.EXCEL);
    }

    /**
     * metodo que tiene la logica pra seleccionar un formato de reporte
     * 
     * @param formato
     */
    private void seleccionarReporte(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        if ("SI".equals(SysmanFunciones.nvl(getParametro(LdeshabitadosControladorEnum.FORMATO_CALIDAD.getValue(),
                        true),
                        "NO"))) {
            if ((consumo == null) || consumo.isEmpty()) {
                consumo = "0";
            }
            genInforme(formato, LdeshabitadosControladorEnum.REPORTE001062.getValue());
        }
        else {
            genInforme(formato, LdeshabitadosControladorEnum.REPORTE001064.getValue());
        }
    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirPdf() {
        seleccionarReporte(FORMATOS.PDF);
    }

    /**
     * metodo que es llamado al seleccionar un registro fila del combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
    }

    /**
     * metodo que contiene la logica para generar un reporte en formato pdf y excel
     * 
     * @param formato
     * @param reporte
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("consumo", consumo);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LDESHABITADOS_CICLO", ciclo);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
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
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getConsumo() {
        return consumo;
    }

    public void setConsumo(String consumo) {
        this.consumo = consumo;
    }

    public boolean isConsumoVisible() {
        return consumoVisible;
    }

    public void setConsumoVisible(boolean consumoVisible) {
        this.consumoVisible = consumoVisible;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

}
