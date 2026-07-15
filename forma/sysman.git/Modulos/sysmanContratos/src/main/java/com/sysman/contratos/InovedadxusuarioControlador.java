
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InovedadxusuarioControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 24/11/2015
 * 
 * @version 2, 09/08/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Se reemplazo el número del formulario por un enumerado.
 */
@ManagedBean
@ViewScoped
public class InovedadxusuarioControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String tipoContrato;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContratoInicial;

    // <MANEJO_EJBs>
    /**
     * Atributo que permite utilizar las funciones y procedimientos
     * del paquete <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </MANEJO_EJBs>

    /**
     * Creates a new instance of InovedadxusuarioControlador
     */
    public InovedadxusuarioControlador() {
        super();

        // 376
        numFormulario = GeneralCodigoFormaEnum.INOVEDADXUSUARIO_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InovedadxusuarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InovedadxusuarioControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdPdf() {
        archivoDescarga = null;

        generarReporteNovedadxUsuario(FORMATOS.PDF);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;

        generarReporteNovedadxUsuario(FORMATOS.EXCEL97);
    }

    private void generarReporteNovedadxUsuario(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "000397INovedadxUsuario";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("nombreTipo",
                        SysmanFunciones.colocarComillas(tipoContrato));

        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        // </REEMPLAZAR VARIABLES EN CONSULTA>

        String nomTipoContrato = service.buscarEnLista(tipoContrato, "CODIGO",
                        "NOMBRE", listaTipoContratoInicial);

        try {
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            long conteo = service.getConteoConsulta(
                            parametros.get("PR_STRSQL").toString());

            if (conteo == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
                return;
            }

            String firmaOrdenServicio = recuperarValorPar(
                            "FIRMA ORDENES DE SERVICIO");

            firmaOrdenServicio = validarParametro("FIRMA ORDENES DE SERVICIO",
                            firmaOrdenServicio) ? firmaOrdenServicio : "";

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_TIPOCONTRATOINICIAL", nomTipoContrato);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));

            parametros.put("PR_FIRMA_ORDENES_DE_SERVICIO", firmaOrdenServicio);
            // </ENVIAR PARAMETROS AL REPORTE>

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3441")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
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

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }
}