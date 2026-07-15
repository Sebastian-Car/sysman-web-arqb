package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmListadoCorteControladorUrlEnum;
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
 * @author dmaldonado
 * @version 1, 16/09/2016
 * @modified jguerrero
 * @version 2. 30/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 1/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class FrmListadoCorteControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    /**
     * Constante que identifica el nombre del campo CODIGORUTA
     */
    private final String campoCodigoRuta;
    // <DECLARAR_ATRIBUTOS>
    private boolean abonos;
    private String cbAbono;
    private String pqr;
    private String chapetas;
    private String estado;
    private String codigoFinal;
    private String codigoInicial;
    private String ciclo;
    private int atrasoSuperior;
    private double deudaFinal;
    private double deudaInicial;
    private int atrasoInferior;
    private Date fechaCorte;
    private boolean visibleCPA;
    private boolean visibleFechaCorte;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCodigoInicial;
    private List<Registro> listaTxtCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private boolean alertaPermiteFechas;
    private boolean alertaFiltros;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmListadoCorteControlador
     */
    public FrmListadoCorteControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        campoCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_LISTADO_CORTE_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmListadoCorteControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        String parametro;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE DISTINTAS FECHAS LIMITE PARA UN CICLO",
                            modulo,
                            new Date(), true);

            if (parametro == null) {
                alertaPermiteFechas = true;
            }

            parametro = SysmanFunciones.nvlStr(parametro, "NO");

            if (("SI").equals(parametro)) {
                visibleFechaCorte = true;
            }

            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "FILTROS CHAPETAS, PQR, ABONOS LISTADO USUARIOS COR",
                            modulo,
                            new Date(), true);

            if (parametro == null) {
                alertaFiltros = true;
            }
            parametro = SysmanFunciones.nvlStr(parametro, "NO");
            if (("SI").equals(parametro)) {
                visibleCPA = true;
                chapetas = "3";
                pqr = "3";
                cbAbono = "3";
            }
            atrasoInferior = 1;
            atrasoSuperior = 9999;
            deudaFinal = 999999999;
        }
        catch (SystemException e) {
            Logger.getLogger(FrmListadoCorteControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CARGAR_LISTA>
        cargarListaTxtCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void mensajesInicioModal() {
        if (alertaPermiteFechas) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1579"));
            alertaPermiteFechas = false;
        }

        if (alertaFiltros) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1580"));
            alertaFiltros = false;
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoCorteControladorUrlEnum.URL6823
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put("CODIGOINICIAL", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigoRuta);

        // 366006 CICLO CODIGOINICIAL
    }

    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoCorteControladorUrlEnum.URL7671
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigoRuta);

        // 366004 CICLO

    }

    public void cargarListaTxtCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTxtCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmListadoCorteControladorUrlEnum.URL8124
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 214053
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generaInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {

            String condicion = " ";

            reemplazar = reemplazarValAbono();

            condicion = asignarCondicion(condicion);

            if (visibleCPA) {
                if (SysmanFunciones.nvl(chapetas, "").toString().isEmpty()
                    || SysmanFunciones.nvl(pqr, "").toString().isEmpty()
                    || SysmanFunciones.nvl(cbAbono, "").toString().isEmpty()) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1582"));
                    return;
                }
                else {
                    condicion = asignarCondicion(condicion);
                }
            }
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("condicionAdicional", condicion);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("atrasoInferior", atrasoInferior);
            reemplazar.put("atrasoSuperior", atrasoSuperior);
            reemplazar.put("deudaInicial", deudaInicial);
            reemplazar.put("deudaFinal", deudaFinal);

            String titulo = idioma.getString("TB_TB1584");
            titulo = titulo.replace("s$atrasoInferior$s",
                            Integer.toString(atrasoInferior) + "");
            titulo = titulo.replace("s$atrasoSuperior$s",
                            Integer.toString(atrasoSuperior) + "");

            parametros.put("PR_TITULOATRASO", titulo);

            titulo = idioma.getString("TB_TB1587");
            titulo = titulo.replace("s$codigoInicial$s", codigoInicial + "");
            titulo = titulo.replace("s$codigoFinal$s", codigoFinal + "");
            parametros.put("PR_ENTRECODIGOS", titulo);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("001082INFLISTADOCORTE2",
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001082INFLISTADOCORTE2",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private HashMap<String, Object> reemplazarValAbono() {
        HashMap<String, Object> reemplazar = new HashMap<>();

        if (!abonos) {
            String withConsulta = Reporteador.resuelveConsulta(
                            "800116FrmListadoCorte",
                            Integer.valueOf(modulo), reemplazar);

            String joinAdicional = Reporteador.resuelveConsulta(
                            "800116FrmListadoCorteII",
                            Integer.valueOf(modulo), reemplazar);

            reemplazar.put("withSinAbonos", withConsulta);

            reemplazar.put("joinAdicional", joinAdicional);
        }
        else {
            reemplazar.put("withSinAbonos", "");
            reemplazar.put("joinAdicional", "");
        }
        return reemplazar;
    }

    private String asignarCondicion(String condicion) {
        String condAux = condicion;
        if (!("3").equals(chapetas)) {
            condAux = condAux + " AND SP_USUARIO.CHAPETAS = "
                + chapetas;
        }
        if (!("3").equals(pqr)) {
            condAux = condAux
                + " AND SP_USUARIO.EXCLUIRCARTERA = "
                + (("0").equals(pqr) ? "-1" : "0");
        }
        if (!("3").equals(cbAbono)) {
            condAux = condAux
                + " AND SP_USUARIO.NOTACREDITO = 0";
        }
        if (!("T").equals(estado)) {
            condAux = condAux + " AND SP_USUARIO.ESTADO = '" + estado
                + "'";
        }
        if (visibleFechaCorte) {
            condAux = condAux + " AND SP_USUARIO.FECHALIMITE2 = "
                + SysmanFunciones.formatearFecha(fechaCorte);
        }
        return condAux;
    }

    // <METODOS_CAMBIAR>

    public void cambiarTxtCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtAtrasoSuperior() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.nvlDbl(atrasoInferior, 0) > SysmanFunciones
                        .nvlDbl(atrasoSuperior, 0)) {
            atrasoSuperior = 9999;
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1577"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtDeudaFinal() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.nvlDbl(deudaInicial, 0) > SysmanFunciones
                        .nvlDbl(deudaFinal, 0)) {
            deudaFinal = deudaInicial;
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1578"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtDeuda() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.nvlDbl(deudaInicial, 0) > SysmanFunciones
                        .nvlDbl(deudaFinal, 0)) {
            deudaFinal = deudaInicial;
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1578"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtAtraso() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.nvlDbl(atrasoInferior, 0) > SysmanFunciones
                        .nvlDbl(atrasoSuperior, 0)) {
            atrasoSuperior = 9999;
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1577"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaCorte() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void onRowSelectCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(campoCodigoRuta).toString();
    }

    public void onRowSelectCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(campoCodigoRuta).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getAbonos() {
        return abonos;
    }

    public void setAbonos(boolean abonos) {
        this.abonos = abonos;
    }

    public String getCbAbono() {
        return cbAbono;
    }

    public void setCbAbono(String cbAbono) {
        this.cbAbono = cbAbono;
    }

    public String getPqr() {
        return pqr;
    }

    public void setPqr(String pqr) {
        this.pqr = pqr;
    }

    public String getChapetas() {
        return chapetas;
    }

    public void setChapetas(String chapetas) {
        this.chapetas = chapetas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public int getAtrasoSuperior() {
        return atrasoSuperior;
    }

    public void setAtrasoSuperior(int atrasoSuperior) {
        this.atrasoSuperior = atrasoSuperior;
    }

    public double getDeudaFinal() {
        return deudaFinal;
    }

    public void setDeudaFinal(double deudaFinal) {
        this.deudaFinal = deudaFinal;
    }

    public double getDeudaInicial() {
        return deudaInicial;
    }

    public void setDeudaInicial(double deudaInicial) {
        this.deudaInicial = deudaInicial;
    }

    public int getAtrasoInferior() {
        return atrasoInferior;
    }

    public void setAtrasoInferior(int atrasoInferior) {
        this.atrasoInferior = atrasoInferior;
    }

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public boolean isVisibleCPA() {
        return visibleCPA;
    }

    public void setVisibleCPA(boolean visibleCPA) {
        this.visibleCPA = visibleCPA;
    }

    public boolean isVisibleFechaCorte() {
        return visibleFechaCorte;
    }

    public void setVisibleFechaCorte(boolean visibleFechaCorte) {
        this.visibleFechaCorte = visibleFechaCorte;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public List<Registro> getListaTxtCiclo() {
        return listaTxtCiclo;
    }

    public void setListaTxtCiclo(List<Registro> listaTxtCiclo) {
        this.listaTxtCiclo = listaTxtCiclo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
