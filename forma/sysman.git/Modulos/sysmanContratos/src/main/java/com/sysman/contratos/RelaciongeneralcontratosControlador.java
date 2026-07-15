package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.RelaciongeneralcontratosControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @version 1, 14/10/2015
 * 
 * @version 2, 10/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazó el número del formulario por enumerado.<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class RelaciongeneralcontratosControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo = SessionUtil.getModulo();

    private int ano;
    private String mes;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private List<Registro> listaMes;

    // <DECLARAR_EJBs>
    /**
     * Atributo que gestiona el acceso a alas funciones y
     * procedimientos del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of RelaciongeneralcontratosControlador
     */
    public RelaciongeneralcontratosControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 279
            numFormulario = GeneralCodigoFormaEnum.RELACIONGENERALCONTRATOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            RelaciongeneralcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        ano = SysmanFunciones.ano(new Date());

        cargarListaAno();
        cargarListaMes();
        abrirFormulario();
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelaciongeneralcontratosControladorUrlEnum.URL2741
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelaciongeneralcontratosControladorUrlEnum.URL3138
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirImprimir() {
        archivoDescarga = null;

        generarReporteRelacionGeneralContratos(FORMATOS.PDF);
    }

    public void oprimircmbExcel() {
        archivoDescarga = null;

        generarReporteRelacionGeneralContratos(FORMATOS.EXCEL97);
    }

    public void cambiarAno() {
        mes = "";

        cargarListaMes();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    private void generarReporteRelacionGeneralContratos(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000282I2RelacionGeneralContratos";

        String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mes)].toUpperCase();

        try {
            String parametro = recuperarValorPar("FIRMA ORDENES DE SERVICIO");

            parametro = validarParametro("FIRMA ORDENES DE SERVICIO", parametro)
                ? parametro : "";

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("mes", mes);
            reemplazar.put("ano", ano);
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_ANO", ano);
            parametros.put("PR_MES", nombreMes);
            parametros.put("PR_FIRMA", parametro);
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            long conteo = service.getConteoConsulta(
                            parametros.get("PR_STRSQL").toString());

            if (conteo == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
                return;
            }

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
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
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3443")
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
}
