package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.PrepararFinanciablesControladorEnum;
import com.sysman.nomina.enums.PrepararFinanciablesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author esarmiento
 * @version 1, 27/07/2015
 * 
 * @version 2, 19/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class PrepararFinanciablesControlador extends BeanBaseModal {

    private final String compania;
    private String mes;
    private String anio;
    private String mes2;
    private String anio2;
    private String periodo;
    private String periodo2;
    private List<Registro> listaMes;
    private List<Registro> listaAno;
    private List<Registro> listaMes2;
    private List<Registro> listaAno2;
    private List<Registro> listaPeriodo;
    private List<Registro> listaPeriodo2;

    private final String anioSession;
    private final String mesSession;
    private final String periodoSession;
    private final String proceso;
    private static final String CTEMSMINTERR = "MSM_TRANS_INTERRUMPIDA";

    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    /**
     * Creates a new instance of PrepararFinanciablesControlador
     */
    public PrepararFinanciablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        anioSession = (String) SessionUtil.getSessionVar("anioNomina");
        mesSession = (String) SessionUtil.getSessionVar("mesNomina");
        periodoSession = (String) SessionUtil.getSessionVar("periodoNomina");
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        try {
            numFormulario = GeneralCodigoFormaEnum.PREPARAR_FINANCIABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PrepararFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        anio = anioSession;
        mes = mesSession;
        periodo = periodoSession;
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();
    }

    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararFinanciablesControladorUrlEnum.URL2999
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
                                                            PrepararFinanciablesControladorUrlEnum.URL3853
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio2);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararFinanciablesControladorUrlEnum.URL2999
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararFinanciablesControladorUrlEnum.URL3853
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.MES.getName(), mes);
            param.put(PrepararFinanciablesControladorEnum.PARAM0.getValue(),
                            proceso);

            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararFinanciablesControladorUrlEnum.URL4457
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio2);
            param.put(GeneralParameterEnum.MES.getName(), mes2);
            param.put(PrepararFinanciablesControladorEnum.PARAM0.getValue(),
                            proceso);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararFinanciablesControladorUrlEnum.URL4457
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarMes2() {
        periodo2 = null;
        cargarListaPeriodo2();
    }

    public void cambiarAno2() {
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    public void oprimirPreparar() {
        try {
            if (verificarMenor()) {
                ejbNominaSeis.prepararPeriodoFinan(compania,
                                Integer.parseInt(anio), Integer.parseInt(mes),
                                Integer.parseInt(periodo),
                                Integer.parseInt(anio2), Integer.parseInt(mes2),
                                Integer.parseInt(periodo2),
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2609"));
            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(CTEMSMINTERR)
                                + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean verificarMenor() {
        String aux1 = SysmanFunciones.concatenar(anio,
                        SysmanFunciones.padl(mes, 2, "0"),
                        periodo);
        String aux2 = SysmanFunciones.concatenar(anio2,
                        SysmanFunciones.padl(mes2, 2, "0"), periodo2);
        if (aux1.compareTo(aux2) > 0) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2509"));
            return false;
        }
        else {
            return true;
        }
    }

    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = "";
        anio2 = null;
        mes2 = null;
        periodo2 = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        mes = null;
        periodo = null;
        anio2 = null;
        mes2 = null;
        periodo2 = null;
        cargarListaMes();
        cargarListaPeriodo();
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        anio2 = null;
        mes2 = null;
        periodo2 = null;
        perpre();
        // </CODIGO_DESARROLLADO>
    }

    public void limpiarCombo() {
        this.anio2 = null;
        this.mes2 = null;
        this.periodo2 = null;
    }

    public void actualizarComobos(String resultado) {
        anio2 = resultado.substring(0, 4);
        cargarListaMes2();
        mes2 = String.valueOf(Integer.parseInt(resultado.substring(4, 6)));
        cargarListaPeriodo2();
        periodo2 = String.valueOf(Integer.parseInt(resultado.substring(6, 8)));
    }

    public void perpre() {
        try {
            String resultadoAux = ejbNominaDos.siguientePeriodo(compania,
                            Integer.parseInt(anio), Integer.parseInt(periodo),
                            Integer.parseInt(mes), Integer.parseInt(proceso),
                            "");
            if (resultadoAux.length() < 8) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2610"));
                limpiarCombo();
            }
            else {
                actualizarComobos(resultadoAux);
            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(CTEMSMINTERR)
                                + ex.getMessage());
            Logger.getLogger(PrepararFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getAnio2() {
        return anio2;
    }

    public void setAnio2(String anio2) {
        this.anio2 = anio2;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        perpre();
        // </CODIGO_DESARROLLADO>
    }
}
