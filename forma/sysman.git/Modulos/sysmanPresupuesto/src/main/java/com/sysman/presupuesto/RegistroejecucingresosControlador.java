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
import com.sysman.presupuesto.enums.RegistroejecucingresosControladorEnum;
import com.sysman.presupuesto.enums.RegistroejecucingresosControladorUrlEnum;
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
 * @version 1, 21/07/2016
 * 
 * @author eamaya
 * @version 2, 20/04/2017
 * 
 * @author asana
 * @version 3, 13/06/2017 Se implementa enum en formulario y se ajusta Conexi�n
 * 
 */
@ManagedBean
@ViewScoped

public class RegistroejecucingresosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean miles;
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private int mes;
    private int nivel;
    private String cargoUno;
    private String cargoDos;
    private String firmaUno;
    private String firmaDos;
    private String cedulaUno;
    private String cedulaDos;
    private String seccion;
    private String unidad;
    private String seccionResolucion;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of RegistroejecucingresosControlador
     */
    public RegistroejecucingresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        nivel = 60;
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROEJECUCINGRESOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegistroejecucingresosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        /*
         * FR1010-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroejecucingresosControladorUrlEnum.URL4229
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroejecucingresosControladorUrlEnum.URL4799
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
                                        RegistroejecucingresosControladorUrlEnum.URL5509
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(RegistroejecucingresosControladorEnum.PARAM0.getValue(),
                        "C");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroejecucingresosControladorUrlEnum.URL6552
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(RegistroejecucingresosControladorEnum.PARAM0.getValue(),
                        "C");
        param.put(RegistroejecucingresosControladorEnum.PARAM4.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String parReporte;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("miles", miles ? 1 : 0);
            reemplazar.put("ano", ano);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("mes", mes);
            reemplazar.put("nivel", nivel);

            if (!cargarParametros()) {
                return;
            }
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CARGOUNO", cargoUno);
            parametros.put("PR_CARGODOS", cargoDos);
            parametros.put("PR_FIRMAUNO", firmaUno);
            parametros.put("PR_FIRMADOS", firmaDos);
            parametros.put("PR_CEDULAUNO", cedulaUno);
            parametros.put("PR_CEDULADOS", cedulaDos);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
            if (("SI").equals(seccionResolucion)) {
                if (!seccion.isEmpty()) {
                    parametros.put("PR_TITULOSECCION",
                                    idioma.getString("TG_SECCION2"));
                    parametros.put("PR_SECCION", seccion);
                }
                else {
                    parametros.put("PR_TITULOSECCION", " ");
                    parametros.put("PR_SECCION", " ");
                }
                if (!unidad.isEmpty()) {
                    parametros.put("PR_TITULOUNIDAD", "UNIDAD EJECUTORA: ");
                    parametros.put("PR_UNIDAD", unidad);
                }
                else {
                    parametros.put("PR_TITULOUNIDAD", " ");
                    parametros.put("PR_UNIDAD", " ");
                }
            }
            parReporte = seleccionarReporte();

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, 
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, 
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }




    }

    public boolean cargarParametros() {

        try {
            cargoUno = ejbParametro.consultarParametro(compania,
                            "CARGO1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            if (cargoUno == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB905"));
                return false;
            }
            cargoDos = ejbParametro.consultarParametro(compania,
                            "CARGO2 EN RESOLUCION 036", modulo, new Date(),
                            false);

            if (cargoDos == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB906"));
                return false;
            }

            if (!cargarFirmas()) {
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(RegistroejecucingresosControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    public boolean cargarFirmas() {

        try {

            firmaUno = ejbParametro.consultarParametro(compania,
                            "FIRMA1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            if (firmaUno == null) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB907"));
                return false;
            }

            firmaDos = ejbParametro.consultarParametro(compania,
                            "FIRMA2 EN RESOLUCION 036", modulo, new Date(),
                            false);
            if (firmaDos == null) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB908"));
                return false;
            }

            if (!cargarCedulas()) {
                return false;
            }

        }
        catch (SystemException e) {
            Logger.getLogger(RegistroejecucingresosControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    public boolean cargarCedulas() {
        try {
            cedulaUno = ejbParametro.consultarParametro(compania,
                            "CEDULA1 EN RESOLUCION 036", modulo, new Date(),
                            false);
            if (cedulaUno == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB909"));
                return false;
            }
            cedulaDos = ejbParametro.consultarParametro(compania,
                            "CEDULA2 EN RESOLUCION 036", modulo, new Date(),
                            false);

            if (cedulaDos == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB910"));
                return false;
            }

            if (!cargarSecciones()) {
                return false;
            }

        }
        catch (SystemException e) {
            Logger.getLogger(RegistroejecucingresosControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    public boolean cargarSecciones() {
        try {
            seccionResolucion = ejbParametro.consultarParametro(compania,
                            "SECCION EN INFORMES RESOLUCION 036", modulo,
                            new Date(), false);
            if (seccionResolucion == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB911"));
                return false;
            }
            seccion = ejbParametro.consultarParametro(compania, "SECCION 036",
                            modulo, new Date(), false);
            if (seccion == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB912"));
                return false;
            }
            unidad = ejbParametro.consultarParametro(compania,
                            "UNIDAD EJECUTORA 036", modulo, new Date(), false);
            if (unidad == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB913"));
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(RegistroejecucingresosControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    public String seleccionarReporte() {
        String reporte;
        if (indicador) {
            reporte = "001035REGISTROEJECUCINGRESOS036";
        }
        else {
            reporte = "001037REGISTROEJECUCINGRESOS036SO";
        }
        return reporte;
    }

    // <METODOS_CAMBIAR>

    public void cambiarAno() {

        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaMes();
        cargarListaCuentaInicial();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean getMiles() {
        return miles;
    }

    public void setMiles(boolean miles) {
        this.miles = miles;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
