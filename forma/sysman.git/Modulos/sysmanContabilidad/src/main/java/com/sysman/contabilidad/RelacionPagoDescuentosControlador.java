package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionPagoDescuentosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/05/2016
 * @modified jguerrero
 * @version 2. 12/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class RelacionPagoDescuentosControlador extends BeanBaseModal {
    private final String compania;
    private String mes;
    private String terceroInicial;
    private String terceroFinal;
    private String anio;
    private String nombreMes;
    private String nombreInicial;
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listames;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaterceroinicial;
    private RegistroDataModelImpl listatercerofinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RelacionPagoDescuentosControlador
     */
    public RelacionPagoDescuentosControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));

        try {
            numFormulario = GeneralCodigoFormaEnum.RELACION_PAGO_DESCUENTOS_CONTROLADOR.getCodigo();
            validarPermisos();       
        }
        catch (Exception ex) {
            Logger.getLogger(RelacionPagoDescuentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() { 

        cargarListaAno();
        cargarListames();
        nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mes)];
        cargarListaterceroinicial();    
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListames() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacionPagoDescuentosControladorUrlEnum.URL3960
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 70013
    }

    public void cargarListaAno() {
        listaAno = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT NUMERO" +
                            "   FROM ANO" +
                            "  WHERE COMPANIA = '" + compania + "'" +
                            "    AND NUMERO NOT IN 0");
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacionPagoDescuentosControladorUrlEnum.URL4391
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 4001
    }

    public void cargarListaterceroinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPagoDescuentosControladorUrlEnum.URL4748
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaterceroinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListatercerofinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPagoDescuentosControladorUrlEnum.URL5344
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TECEROINICIAL", String.valueOf(terceroInicial));

        listatercerofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato) {
        String reporte = "000811INFRELPAGDESCUENTOS";
     
        try {
     
            HashMap<String, Object> reemplazar = new HashMap<>();      

            String cargoTesorero;

            cargoTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE TESORERO", SessionUtil.getModulo(),
                            new Date(), true);

            String nombreTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO TESORERO", SessionUtil.getModulo(),
                            new Date(), true);

            String cargoAuxiliar = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE AUXILIAR TESORERO", SessionUtil.getModulo(),
                            new Date(), true);

            String nombreAuxiliar = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO AUXILIAR TESORERO", SessionUtil.getModulo(),
                            new Date(), true);

            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            parametros.put("PR_ANO", anio);
            parametros.put("PR_MES", nombreMes.toUpperCase());
            parametros.put("PR_NOMBRE_TESORERO", nombreTesorero);
            parametros.put("PR_CARGO_TESORERO", cargoTesorero);
            parametros.put("PR_NOMBRE_AUXILIAR_TESORERO", nombreAuxiliar);
            parametros.put("PR_CARGO_AUXILIAR_TESORERO", cargoAuxiliar);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(RelacionPagoDescuentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        } 
        catch (SystemException | OutOfMemoryError | JRException
                        | IOException  e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SysmanException e) {      
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>
        nombreMes = null;
        if (!SysmanFunciones.validarVariableVacio(mes)) {
            nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes)];
            // </CODIGO_DESARROLLADO>

        }

    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListames();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaterceroinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        nombreInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListatercerofinal();
        terceroFinal = null;
    }

    public void seleccionarFilatercerofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
        nombreFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNombreMes() {
        return nombreMes;
    }

    public void setNombreMes(String nombreMes) {
        this.nombreMes = nombreMes;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaterceroinicial() {
        return listaterceroinicial;
    }

    public void setListaterceroinicial(
        RegistroDataModelImpl listaterceroinicial) {
        this.listaterceroinicial = listaterceroinicial;
    }

    public RegistroDataModelImpl getListatercerofinal() {
        return listatercerofinal;
    }

    public void setListatercerofinal(RegistroDataModelImpl listatercerofinal) {
        this.listatercerofinal = listatercerofinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
