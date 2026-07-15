package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisboletinmensualcajaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 12/05/2016
 * 
 * @version 2, 10/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class LisboletinmensualcajaControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaCaja;
    private String mes;
    private String anio;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaCaja;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LisboletinmensualcajaControlador
     */
    public LisboletinmensualcajaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISBOLETINMENSUALCAJA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisboletinmensualcajaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaCaja();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        cargarListaCuentaCaja();
        mes = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.MONTH)
            + 1);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisboletinmensualcajaControladorUrlEnum.URL3518
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaCaja() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisboletinmensualcajaControladorUrlEnum.URL3817
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaCaja = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("mesAnterior", Integer.parseInt(mes) - 1);
            reemplazar.put("cuentaCaja", cuentaCaja);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LISBOLETINMENSUALCAJA_CUENTACAJA",
                            cuentaCaja);
            parametros.put("PR_FORMS_LISBOLETINMENSUALCAJA_MES_COLUMN(1)",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)]
                                + " de " + anio);

            String reporte = "000763LisBoletinMensualCaja";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaCaja = null;
        cargarListaCuentaCaja();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectCuentaCaja(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaCaja = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCuentaCaja() {
        return cuentaCaja;
    }

    public void setCuentaCaja(String cuentaCaja) {
        this.cuentaCaja = cuentaCaja;
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

    public RegistroDataModelImpl getListaCuentaCaja() {
        return listaCuentaCaja;
    }

    public void setListaCuentaCaja(RegistroDataModelImpl listaCuentaCaja) {
        this.listaCuentaCaja = listaCuentaCaja;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
