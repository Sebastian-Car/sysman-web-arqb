package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.FrmplanodiansControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;

/**
 *
 * @author ybecerra
 * @version 1, 11/12/2015
 * 
 * @author eamaya
 * @version 2.0, 08/08/2017 Proceso de Refactoring DSS y cambio de
 * numero de forulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class FrmplanodiansControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private String ano;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listaAno;

    /**
     * Creates a new instance of FrmplanodiansControlador
     */
    public FrmplanodiansControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMPLANODIANS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmplanodiansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        cargarListaAno();
    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmplanodiansControladorUrlEnum.URL3214
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void generarExcel() {

        try {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.NATURALEZA.getName(), "N");
            parametros.put(GeneralParameterEnum.CLASE.getName(), "N");
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmplanodiansControladorUrlEnum.URL3968
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("ano", "'" + ano + "'");

            String dian = Reporteador.resuelveConsulta("900005DianTotal",
                            Integer.parseInt(modulo), remplazar);
            String datos = Reporteador.resuelveConsulta("900006DatosCompania",
                            Integer.parseInt(modulo), remplazar);

            String[] consultas = { dian, datos };

            String[] hojas = { "hoja2", "hoja1" };

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consultas,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            hojas);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1766"));
            Logger.getLogger(FrmplanodiansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (DRException | IOException
                        | SysmanException | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmplanodiansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

}
