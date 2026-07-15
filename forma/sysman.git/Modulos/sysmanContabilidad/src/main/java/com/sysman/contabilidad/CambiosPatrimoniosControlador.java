package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.CambiosPatrimoniosControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
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
 * @author sdaza
 * @version 1, 05/05/2016
 * @version 2, 07/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos,y las correcciones sonar.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 *
 */
@ManagedBean
@ViewScoped
public class CambiosPatrimoniosControlador extends BeanBaseModal {
    private final String compania;
    private final String consCodigo;
    private String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private String informe;
    private String anoTrabajo;
    private String mesTrabajo;
    private String anoComparar;
    private String mesComparar;
    private String digitos = "6";
    private String nomCtaIni;
    private String nomCtaFin;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private List<Registro> listaAnoComparar;
    private List<Registro> listaMesComparar;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    private ContenedorArchivo contArchivoplantilla;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of CambiosPatrimoniosControlador
     */
    public CambiosPatrimoniosControlador() {
        super();
        contArchivoplantilla = new ContenedorArchivo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOS_PATRIMONIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CambiosPatrimoniosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        anoTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        mesTrabajo = String.valueOf(SysmanFunciones.mes(new Date()) - 1);
        anoComparar = String.valueOf(SysmanFunciones.ano(new Date()));
        mesComparar = String.valueOf(SysmanFunciones.mes(new Date()));
        informe = "1";

        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();
        cargarListaAnoComparar();
        cargarListaMesComparar();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        digitos = "6";

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosPatrimoniosControladorUrlEnum.URL4134
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosPatrimoniosControladorUrlEnum.URL4471
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoComparar() {
        listaAnoComparar = listaAnoTrabajo;
    }

    public void cargarListaMesComparar() {
        listaMesComparar = listaMesTrabajo;
    }

    public void cargarListaCodigoInicial() {
        if ("1".equals(informe)) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiosPatrimoniosControladorUrlEnum.URL5904
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

            listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, consCodigo);
        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiosPatrimoniosControladorUrlEnum.URL6715
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

            listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, consCodigo);
        }
    }

    public void cargarListaCodigoFinal() {

        if ("1".equals(informe)) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiosPatrimoniosControladorUrlEnum.URL7518
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

            listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, consCodigo);
        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiosPatrimoniosControladorUrlEnum.URL8327
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

            listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, consCodigo);
        }
    }

    public void oprimirComando0() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL);
    }

    public void oprimirImprimirvista() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        mesTrabajo = null;
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoComparar() {
        // <CODIGO_DESARROLLADO>
        mesComparar = null;
        cargarListaMesTrabajo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarInforme() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        nomCtaIni = null;
        codigoFinal = null;
        nomCtaFin = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (Integer.parseInt(anoComparar) < Integer.parseInt(anoTrabajo)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1742"));
            anoComparar = null;
            mesComparar = null;
            return;

        }

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("anoTrabajo", anoTrabajo);
            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("anoComparar", anoComparar);
            reemplazar.put("mesComparar", mesComparar);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("digitos", digitos);
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            asignarParametros(parametros);

            if ("1".equals(informe)) {
                String patromonioFecIni = SysmanFunciones.concatenar(
                                idioma.getString("TB_TB485"), " ",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)],
                                "/", anoTrabajo);
                parametros.put("PR_PATRIMONIO_FECHAINICIAL", patromonioFecIni);
                String patromonioVariacion = SysmanFunciones.concatenar(
                                idioma.getString("TB_TB486"),
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)],
                                "/", anoTrabajo);
                parametros.put("PR_PATRIMONIO_VARIACION", patromonioVariacion);
                String patromonioFecFin = SysmanFunciones.concatenar(
                                idioma.getString("TB_TB487"),
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesComparar)],
                                "/", anoComparar);
                parametros.put("PR_PATRIMONIO_FECHAFINAL", patromonioFecFin);
                String colSaldoAnterior = SysmanFunciones.concatenar(
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)],
                                "/", anoTrabajo);
                parametros.put("PR_COL_SALDOANTERIOR", colSaldoAnterior);
                String colSaldoNuevo = SysmanFunciones.concatenar(
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesComparar)],
                                "/", anoComparar);
                parametros.put("PR_COL_SALDONUEVO", colSaldoNuevo);
                String tituloReporte = SysmanFunciones.concatenar(
                                idioma.getString("TB_TB488"), " ",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)]
                                                                .toUpperCase(),
                                " DE ", anoTrabajo, " Y ",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesComparar)]
                                                                .toUpperCase(),
                                " DE ", anoComparar);
                parametros.put("PR_TITULO_REPORTE", tituloReporte);
                Reporteador.resuelveConsulta("000753CambiosPatrimonio",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000753CambiosPatrimonio", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }
            else {
                String tituloReporte = idioma.getString("TB_TB489")
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                    .parseInt(mesTrabajo)].toUpperCase()
                    + " DE "
                    + anoTrabajo + " Y "
                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                    .parseInt(mesComparar)].toUpperCase()
                    + " DE "
                    + anoComparar;
                parametros.put("PR_TITULO_REPORTE", tituloReporte);
                parametros.put("PR_COLANTERIOR",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)]
                                    + "/" + anoTrabajo);
                parametros.put("PR_COLNUEVO",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesComparar)]
                                    + "/" + anoComparar);
                Reporteador.resuelveConsulta("000757FlujoCaja",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed("000757FlujoCaja",
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void asignarParametros(Map<String, Object> parametros) {
        try {
            parametros.put("PR_FIRMA_CONTABLE_1",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FIRMA CONTABLE 1", modulo,
                                            new Date(), false));

            parametros.put("PR_CARGO_CONTABLE_1",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO CONTABLE 1", modulo,
                                            new Date(), false));

            parametros.put("PR_DOCUMENTO_CONTABLE_1",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 1", modulo,
                                            new Date(), false));

            parametros.put("PR_FIRMA_CONTABLE_2",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FIRMA CONTABLE 2", modulo,
                                            new Date(), false));

            parametros.put("PR_CARGO_CONTABLE_2",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO CONTABLE 2", modulo,
                                            new Date(), false));

            parametros.put("PR_DOCUMENTO_CONTABLE_2",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 2", modulo,
                                            new Date(), false));

            parametros.put("PR_FIRMA_CONTABLE_3",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FIRMA CONTABLE 3", modulo,
                                            new Date(), false));

            parametros.put("PR_CARGO_CONTABLE_3",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CARGO CONTABLE 3", modulo,
                                            new Date(), false));

            parametros.put("PR_DOCUMENTO_CONTABLE_3",
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "DOCUMENTO CONTABLE 3", modulo,
                                            new Date(), false));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), " ")
                        .toString();
        nomCtaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();

    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), " ")
                        .toString();
        nomCtaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public String getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public String getAnoComparar() {
        return anoComparar;
    }

    public void setAnoComparar(String anoComparar) {
        this.anoComparar = anoComparar;
    }

    public String getMesComparar() {
        return mesComparar;
    }

    public void setMesComparar(String mesComparar) {
        this.mesComparar = mesComparar;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public String getNomCtaIni() {
        return nomCtaIni;
    }

    public void setNomCtaIni(String nomCtaIni) {
        this.nomCtaIni = nomCtaIni;
    }

    public String getNomCtaFin() {
        return nomCtaFin;
    }

    public void setNomCtaFin(String nomCtaFin) {
        this.nomCtaFin = nomCtaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public List<Registro> getListaAnoComparar() {
        return listaAnoComparar;
    }

    public void setListaAnoComparar(List<Registro> listaAnoComparar) {
        this.listaAnoComparar = listaAnoComparar;
    }

    public List<Registro> getListaMesComparar() {
        return listaMesComparar;
    }

    public void setListaMesComparar(List<Registro> listaMesComparar) {
        this.listaMesComparar = listaMesComparar;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public ContenedorArchivo getContArchivoplantilla() {
        return contArchivoplantilla;
    }

    public void setContArchivoplantilla(
        ContenedorArchivo contArchivoplantilla) {
        this.contArchivoplantilla = contArchivoplantilla;
    }

}
