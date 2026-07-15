package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FechasNoCoincidentesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 2, 16/05/2016 12:26:44 -- Modificado por sdaza
 * @modified jguerrero
 * @version 2. 10/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @version 3.0, 12/06/2017,<strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class FechasNoCoincidentesControlador extends BeanBaseModal {
    private final String compania;
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaCaja;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreCuenta;
    private int anoFecIni;
    private boolean indCaja;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaCaja;
    private final String codigoCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FechasNoCoincidentesControlador
     */
    public FechasNoCoincidentesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";

        try {
            // 706
            numFormulario = GeneralCodigoFormaEnum.FECHAS_NO_COINCIDENTES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FechasNoCoincidentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = new Date();

        anoFecIni = SysmanFunciones.ano(fechaInicial);

        indCaja = true;

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaCaja();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaCaja() {

        if (indCaja) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FechasNoCoincidentesControladorUrlEnum.URL3154
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoFecIni);

            listaCuentaCaja = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoCons);

        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FechasNoCoincidentesControladorUrlEnum.URL3665
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoFecIni);

            listaCuentaCaja = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoCons);

        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarFechas()) {
            return;
        }

        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarFechas()) {
            return;
        }
        generaInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaCaja = null;
        nombreCuenta = null;
        anoFecIni = SysmanFunciones.ano(fechaInicial);
        cargarListaCuentaCaja();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarindCaja() {
        // <CODIGO_DESARROLLADO>
        cuentaCaja = null;
        nombreCuenta = null;

        cargarListaCuentaCaja();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaCaja(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaCaja = registroAux.getCampos().get(codigoCons).toString();
        nombreCuenta = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        // validar fechas

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();

        try {
            reemplazar.put("compania", compania);

            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            reemplazar.put("fechaFinal", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));

            reemplazar.put("cuentaCaja", cuentaCaja);
            parametros.put("PR_TITULO_REPORTE", "Entre fechas "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial)
                + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_COMPANIA", compania);

            Reporteador.resuelveConsulta("000773FechasNoCoincidentes",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000773FechasNoCoincidentes", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCuentaCaja() {
        return cuentaCaja;
    }

    public void setCuentaCaja(String cuentaCaja) {
        this.cuentaCaja = cuentaCaja;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public int getAnoFecIni() {
        return anoFecIni;
    }

    public void setAnoFecIni(int anoFecIni) {
        this.anoFecIni = anoFecIni;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaCaja() {
        return listaCuentaCaja;
    }

    public void setListaCuentaCaja(RegistroDataModelImpl listaCuentaCaja) {
        this.listaCuentaCaja = listaCuentaCaja;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean isIndCaja() {
        return indCaja;
    }

    public void setIndCaja(boolean indCaja) {
        this.indCaja = indCaja;
    }

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB528"));
            rta = false;
        }

        return rta;

    }

}
