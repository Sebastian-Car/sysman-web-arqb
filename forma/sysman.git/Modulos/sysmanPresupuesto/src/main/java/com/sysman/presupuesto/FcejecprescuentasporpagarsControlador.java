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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FcejecprescuentasporpagarsControladorEnum;
import com.sysman.presupuesto.enums.FcejecprescuentasporpagarsControladorUrlEnum;
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
 * @author ybecerra
 * @version 1, 16/06/2016
 * 
 * @version 2, 18/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class FcejecprescuentasporpagarsControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    private final String strCodigo;

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

    private String condicion = "";
    private String centroCosto = "";
    private String auxiliar = "";
    private String agrupacionC = "";
    private String agrupacionA = "";
    private String condicionFinal = "";
    private String agrupacionCa = "";

    private int ano;
    private int nivel;
    private String observacion;
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
     * Creates a new instance of FcejecprescuentasporpagarsControlador
     */
    public FcejecprescuentasporpagarsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strCodigo = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.FCEJECPRESCUENTASPORPAGARS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FcejecprescuentasporpagarsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        // <CARGAR_LISTA>

        cargarListaAno();
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(new Date());
        nivel = 60;

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcejecprescuentasporpagarsControladorUrlEnum.URL4939
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcejecprescuentasporpagarsControladorUrlEnum.URL5476
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcejecprescuentasporpagarsControladorUrlEnum.URL6096
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL6515
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL7465
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL8703
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL9366
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL10104
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcejecprescuentasporpagarsControladorUrlEnum.URL10863
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FcejecprescuentasporpagarsControladorEnum.PARAM1.getValue(),
                        fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            gestionaDatos();
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("miles", enMiles ? "1" : "0");
            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("nivel", nivel);
            reemplazar.put("condicion", condicion);
            reemplazar.put("centroCosto",
                            centroCosto);
            reemplazar.put("auxiliar", auxiliar);
            reemplazar.put("agrupacionC", agrupacionC);
            reemplazar.put("agrupacionA", agrupacionA);
            reemplazar.put("agrupacionCa", agrupacionCa);
            reemplazar.put("condicionFinal", condicionFinal);

            Map<String, Object> parametros = new HashMap<>();
            cargarParametros(parametros);

            Reporteador.resuelveConsulta("000914FCEJECPRESCUENTASPORPAGAR",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000914FCEJECPRESCUENTASPORPAGAR", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (IOException | JRException | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FcejecprescuentasporpagarsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarParametros(Map<String, Object> parametros) {
        try {
            String parametro = ejbSysmanUtilRemote.consultarParametro(compania,
                            "NOMBRE REPRESENTANTE LEGAL", modulo, new Date(),
                            false);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBREREPRESENTANTE", parametro);
            parametros.put("PR_PERIODO",
                            "  PERIODO:  DE "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .valueOf(mesInicial)]
                                                                .toUpperCase()
                                + " A "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .valueOf(mesFinal)]
                                                                .toUpperCase()
                                + " DE " + ano);
            parametros.put("PR_VISIBLE", enMiles ? 1 : 0);
            parametros.put("PR_ENMILES", "VALOR EN MILES DE PESOS");
            parametros.put("PR_OBSERVACIONES", observacion);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public String buscarObjeto(String opcion) {
        return "1".equals(opcion)
            ? listacentrocostoInicial.getDatasource().get(1).getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString()
            : listaFuenteInicial.getDatasource().get(1).getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString();
    }

    private void gestionaDatos() {
        condicion = " ";
        if (!(buscarObjeto("1").equals(centroInicial)
            && centroFinal.equals(SysmanConstantes.CONS_CENTRO))) {
            centroCosto = "   PLAN_PRESUPUESTAL.CENTRO_COSTO,";
            agrupacionC = "   PLAN_PRESUPUESTAL.CENTRO_COSTO,";
            condicionFinal = "  AND APROPIACIONESACTUALES.CENTRO_COSTO BETWEEN '"
                + centroInicial + "'  AND '" + centroFinal
                + "' \n ";
        }

        if ((fuenteInicial.isEmpty()) || (fuenteFinal.isEmpty())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB791"));
            return;
        }
        else {
            if (!(buscarObjeto("2").equals(fuenteInicial) && fuenteFinal
                            .equals(SysmanConstantes.CONS_AUXILIAR))) {
                auxiliar = " PLAN_PRESUPUESTAL.AUXILIAR ,";
                agrupacionA = " PLAN_PRESUPUESTAL.AUXILIAR, ";
                condicionFinal = condicionFinal.isEmpty()
                    ? "  AND APROPIACIONESACTUALES.AUXILIAR BETWEEN '"
                        + fuenteInicial + "' AND '"
                        + fuenteFinal + "'"
                    : condicionFinal
                        + "  AND APROPIACIONESACTUALES.AUXILIAR BETWEEN '"
                        + fuenteInicial + "' AND '"
                        + fuenteFinal + "'";
            }
        }

    }

    public void cambiarAno() {

        mesInicial = null;
        mesFinal = null;
        cuentaFinal = null;
        cuentaInicial = null;

        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();

    }

    public void cambiarMesInicial() {
        mesFinal = null;
        cargarListaMesFinal();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos().get(strCodigo).toString();
        centroFinal = null;
        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos().get(strCodigo).toString();

    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos().get(strCodigo).toString();
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos().get(strCodigo).toString();
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
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

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
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
