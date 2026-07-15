package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmrptdivisionmunicipalsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmrptdivisionmunicipalsControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 15/10/2015
 * @modified jguerrero
 * @version 2. 21/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class FrmrptdivisionmunicipalsControlador extends BeanBaseModal {

    private String pais;
    private String departamento;
    private String ciudad;
    private String reporte;
    private Boolean asociacion;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of FrmrptdivisionmunicipalsControlador
     */
    public FrmrptdivisionmunicipalsControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMRPTDIVISIONMUNICIPALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(
                            FrmrptdivisionmunicipalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaPais();

        reporte = "1";
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {

        // Metodo que se hereda desde el bean base
    }

    public void cargarListaPais() {

        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrptdivisionmunicipalsControladorUrlEnum.URL2404
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 1001
    }

    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmrptdivisionmunicipalsControladorEnum.PAIS.getValue(),
                        pais);

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrptdivisionmunicipalsControladorUrlEnum.URL2773
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // PAIS 2005
    }

    public void cargarListaCiudad() {

        Map<String, Object> param = new TreeMap<>();
        param.put(FrmrptdivisionmunicipalsControladorEnum.PAIS.getValue(),
                        pais);
        param.put(FrmrptdivisionmunicipalsControladorEnum.DEPARTAMENTO
                        .getValue(), departamento);

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrptdivisionmunicipalsControladorUrlEnum.URL3342
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 5001 DEPARTAMENTO PAIS
    }

    public void oprimirImpresora() {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(FrmrptdivisionmunicipalsControladorEnum.PR_PAIS
                            .getValue(), retornarPais().toUpperCase());

            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put(FrmrptdivisionmunicipalsControladorEnum.PAIS_LOWER
                            .getValue(), pais);
            reemplazos.put(FrmrptdivisionmunicipalsControladorEnum.DEPARTAMENTO_LOWER
                            .getValue(), departamento);
            reemplazos.put(FrmrptdivisionmunicipalsControladorEnum.CIUDAD_LOWER
                            .getValue(), SysmanFunciones.nvlStr(ciudad, ""));

            Reporteador.resuelveConsulta(nombreReporte(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte(),
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarPais() {

        departamento = null;
        ciudad = null;

        cargarListaDepartamento();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento() {

        ciudad = null;
        cargarListaCiudad();
    }

    public void cambiarCmbReporte() {
        // Metodo cambiarReporte
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Boolean getAsociacion() {
        return asociacion;
    }

    public void setAsociacion(Boolean asociacion) {
        this.asociacion = asociacion;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    private String retornarPais() {
        return SysmanFunciones.concatenar(
                        service.buscarEnLista(pais,
                                        FrmrptdivisionmunicipalsControladorEnum.PAIS
                                                        .getValue(),
                                        GeneralParameterEnum.NOMBRE.getName(),
                                        listaPais),
                        " - ",
                        service.buscarEnLista(departamento,
                                        GeneralParameterEnum.CODIGO.getName(),
                                        GeneralParameterEnum.NOMBRE.getName(),
                                        listaDepartamento));
    }

    private String nombreReporte() {
        String nombreReporte;

        if ("1".equals(reporte)) {
            if (asociacion) {
                nombreReporte = FrmrptdivisionmunicipalsControladorEnum.REPORTE302
                                .getValue();

            }
            else {
                nombreReporte = FrmrptdivisionmunicipalsControladorEnum.REPORTE306
                                .getValue();

            }

        }
        else {
            nombreReporte = FrmrptdivisionmunicipalsControladorEnum.REPORTE307
                            .getValue();

        }
        return nombreReporte;
    }

}
