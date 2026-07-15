package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.presupuesto.enums.EjecPptalReservasApropiacionControladorEnum;
import com.sysman.presupuesto.enums.EjecPptalReservasApropiacionControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/06/2016
 * 
 * @version 2, 18/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author asana
 * @version 3, 13/06/2017 se implementa enum y modifica conexi�n.
 */
@ManagedBean
@ViewScoped
public class EjecPptalReservasApropiacionControlador extends BeanBaseModal {
    private final String compania;
    private final String codigoCons;

    // <DECLARAR_ATRIBUTOS>
    private boolean enMiles;
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial;
    private String centroFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String nmes1;
    private String nmes2;
    private String nivel;
    private String observaciones;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of
     * EjecPptalReservasApropiacionControlador
     */
    public EjecPptalReservasApropiacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        try {
            numFormulario = GeneralCodigoFormaEnum.EJEC_PPTAL_RESERVAS_APROPIACION_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(EjecPptalReservasApropiacionControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListaMesFinal();
        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                           .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                           .parseInt(mesInicial)
                                                           + 1];
        enMiles = true;
        nivel = "60";
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // cargarListaCuentaFinal()
        cargarListacentrocostoInicial();
        // cargarListacentrocostoFinal()
        cargarListaFuenteInicial();
        // cargarListaFuenteFinal()
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),anio);
            listaMesInicial = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL4975.getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal() {
        listaMesFinal = listaMesInicial;
    }

    public void cargarListaAno() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL5852.getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL6191.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL7595.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        param.put(EjecPptalReservasApropiacionControladorEnum.PARAM0.getValue(),cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);

    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL9128.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL9982.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),centroInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL10701.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(EjecPptalReservasApropiacionControladorUrlEnum.URL11317.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        param.put(EjecPptalReservasApropiacionControladorEnum.PARAM0.getValue(),fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
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

    private boolean auxGenerarReporte() {
        boolean rta = true;
        if (agregarMensaje(cuentaInicial, idioma.getString("TB_TB280"))
                        || agregarMensaje(cuentaFinal, idioma.getString("TB_TB281"))
                        || agregarMensaje(centroInicial,
                                        idioma.getString("TB_TB278"))) {
            rta = false;
        }
        if (agregarMensaje(centroFinal, idioma.getString("TB_TB279"))
                        || agregarMensaje(fuenteInicial, idioma.getString("TB_TB282"))
                        || agregarMensaje(fuenteFinal,
                                        idioma.getString("TB_TB283"))) {
            rta = false;
        }
        if (agregarMensaje(mesInicial, idioma.getString("TB_TB284"))
                        || agregarMensaje(mesFinal, idioma.getString("TB_TB285"))
                        || agregarMensaje(anio,
                                        idioma.getString("TB_TB286"))) {
            rta = false;
        }
        return rta;

    }

    private void generaReporte(FORMATOS formato) {
        try {
            if (!auxGenerarReporte()) {
                return;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000943FCCBEJECUCIONRAIDAGR";
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anio);
            reemplazar.put("miles", enMiles ? "-1" : "0");
            reemplazar.put("nivel", nivel);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            cargarParametros(parametros);



            archivoDescarga = JsfUtil.exportarStreamed(reporte, 
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, 
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }




    }

    public void cargarParametros(Map<String, Object> parametros){
        String nombreRpteLegal = "";
        String firmaRpteLegal = "";
        try {
            nombreRpteLegal = ejbSysmanUtilRemote.consultarParametro(
                            compania, "NOMBRE REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), false);

            firmaRpteLegal = ejbSysmanUtilRemote.consultarParametro(
                            compania, "FIRMA REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), false);
            parametros.put("PR_FORMATO",
                            enMiles ? "#,#00;(#,#00)" : "#,#00.00;(#,#00.00)");
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES2", nmes2.toUpperCase());
            parametros.put("PR_NMES1", nmes1.toUpperCase());
            parametros.put("PR_MESINICIAL", mesInicial);
            parametros.put("PR_MESFINAL", mesFinal);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", nombreRpteLegal);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", firmaRpteLegal);
            parametros.put("PR_OBSERVACIONES", observaciones);
            parametros.put("PR_MILES",
                            enMiles ? "VALOR EN MILES DE PESOS" : "");
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } 
    }

    private boolean agregarMensaje(String campo, String mensaje) {
        if (SysmanFunciones.validarVariableVacio(campo)) {
            JsfUtil.agregarMensajeAlerta(mensaje);
            return true;
        }
        return false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                           .parseInt(mesInicial)];
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesFinal() {
        // <CODIGO_DESARROLLADO>
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                           .parseInt(mesFinal)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = mesFinal = nmes1 = nmes2 = cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = null;
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListaFuenteInicial();
        cargarListacentrocostoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListacentrocostoFinal();
        centroFinal = null;
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListaFuenteFinal();
        fuenteFinal = null;
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getEnMiles() {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getCentroInicial() {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal() {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNmes1() {
        return nmes1;
    }

    public void setNmes1(String nmes1) {
        this.nmes1 = nmes1;
    }

    public String getNmes2() {
        return nmes2;
    }

    public void setNmes2(String nmes2) {
        this.nmes2 = nmes2;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>




    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }



    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListacentrocostoInicial() {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial) {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal() {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal) {
        this.listacentrocostoFinal = listacentrocostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
