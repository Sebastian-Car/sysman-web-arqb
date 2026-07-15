package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.SaldoPlanContableControladorEnum;
import com.sysman.contabilidad.enums.SaldoPlanContableControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @modified jguerrero
 * @version 2. 10/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio codigo formulario y actualizacion
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class SaldoPlanContableControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private final String modulo = SessionUtil.getModulo();

    private String anio;
    private String cuenta;
    private StreamedContent archivoDescarga;

    public SaldoPlanContableControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.SALDO_PLAN_CONTABLE_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();

        try {
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                anio = (String) parametros.get("anio");
                cuenta = (String) parametros
                                .get(GeneralParameterEnum.CUENTA.getName());

                rid = (Map<String, Object>) parametros.get("rid");

            }
            SessionUtil.cleanFlash();
        }
        catch (Exception ex) {
            Logger.getLogger(SaldoPlanContableControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        tabla = SaldoPlanContableControladorEnum.PARAM0.getValue();

        asignarOrigenDatos();
        abrirFormulario();

    }

    @Override
    public void asignarOrigenDatos() {

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoPlanContableControladorUrlEnum.URL2789
                                                        .getValue());

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), anio);
        parametros.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        try {
            registro = RegistroConverter.toRegistro(
                            requestManager.get(urlLectura.getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtImprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSQL;
            String informe;
            reemplazar.put("anio", anio);
            reemplazar.put("cuenta", cuenta);

            informe = "000553infSaldoPlanCont";

            strSQL = Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSQL);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        parametros.put("anio", anio);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLAN_CONTABLE_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void cargarRegistro() {
        // Metodo heredado de la clase BeanBase

    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListas() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

}
