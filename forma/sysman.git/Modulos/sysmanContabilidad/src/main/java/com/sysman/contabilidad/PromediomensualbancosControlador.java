package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.PromediomensualbancosControladorEnum;
import com.sysman.contabilidad.enums.PromediomensualbancosControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 20/05/2016
 * @version 2, 12/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacion de servicio
 * para combo sencillo
 * @version 3, 21/04/2017--mzanguna, cambio Ejb.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class PromediomensualbancosControlador extends BeanBaseModal {
    /**
     * variable que alamcena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el estado en miles
     */
    private boolean enMiles;
    /**
     * variable que almacena el mes
     */
    private int mes;
    /**
     * variable que almacena el año
     */
    private int ano;
    /**
     * variable que almacena el archivo de desacarga reporte
     */
    private StreamedContent archivoDescarga;
    /**
     * lista los años
     */
    private List<Registro> listaAno;

    /**
     * Creates a new instance of PromediomensualbancosControlador
     */
    public PromediomensualbancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROMEDIOMENSUALBANCOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PromediomensualbancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que inicializa el formulario
     */
    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        abrirFormulario();
    }

    /**
     * metodo que se ejecuta al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
    }

    /**
     * metodo que carga la lista de años
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PromediomensualbancosControladorUrlEnum.URL3373
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirPresentar() {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
    }

    /**
     * metodo que contiene la logica para generar los reportes en pdf
     * o excel
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();

            String milesR;
            if (enMiles) {
                milesR = "1";
            }
            else {
                milesR = "0";
            }

            int ultimoDia = SysmanFunciones.ultimoDiaInt(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/" + ano + ""));
            reemplazar.put(PromediomensualbancosControladorEnum.MES.getValue(),
                            mes);
            reemplazar.put(PromediomensualbancosControladorEnum.ANO.getValue(),
                            ano);
            reemplazar.put(PromediomensualbancosControladorEnum.MILES
                            .getValue(), milesR);
            reemplazar.put(PromediomensualbancosControladorEnum.ULTIMODIA
                            .getValue(), ultimoDia);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(PromediomensualbancosControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            idioma.getString(
                                            PromediomensualbancosControladorEnum.TG_NOMBRE_DE_LA_ENTIDAD
                                                            .getValue())
                                + SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(PromediomensualbancosControladorEnum.PR_NITCOMPANIA
                            .getValue(),
                            idioma.getString(
                                            PromediomensualbancosControladorEnum.IDIOMA1
                                                            .getValue())
                                + SessionUtil.getCompaniaIngreso().getNit());
            parametros.put(PromediomensualbancosControladorEnum.PR_MESINFORMADO
                            .getValue(), "Mes informado: "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                + " de "
                                + ano);
            parametros.put(PromediomensualbancosControladorEnum.PR_ENMILES
                            .getValue(),
                            enMiles
                                ? idioma.getString(
                                                PromediomensualbancosControladorEnum.IDIOMA2
                                                                .getValue())
                                : " ");

            Reporteador.resuelveConsulta(
                            PromediomensualbancosControladorEnum.NOMBREINFORME
                                            .getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            PromediomensualbancosControladorEnum.NOMBREINFORME
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodos get y set
     *
     * @param enMiles
     */
    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public boolean isEnMiles() {
        return enMiles;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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
}
