/*-
 * InformeMedidoresNuevos.java
 *
 * 1.0
 *
 * 07/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.InformeMedidoresNuevosEnum;
import com.sysman.serviciospublicos.enums.InformeMedidoresNuevosUrlEnum;
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
 * Controlador del formulario InformeMedidoresNuevos
 *
 * @version 1.0, 07/10/2016
 * @author cperez
 * 
 * @version 2, 05/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 */

@ManagedBean
@ViewScoped
public class InformeMedidoresNuevos extends BeanBaseModal {
    /**
     * Obtiene el código de la compañía
     */
    private final String compania;

    /**
     * Variable tipo string para Obtener el CODIGORUTA
     */
    private final String codigoRutaConstante;

    /**
     * Variable tipo string para la obtener el "NOMBREDEPERIODO"
     */
    private final String periodoCompleto;
    /**
     * Generada automaticamente
     */
    private final String modulo;

    /**
     * Variable tipo string validar que parametro es.
     */
    private final String si;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;

    /**
     * Obtiene el codigoInicial de la consulta
     */
    private String codigoIncial;

    /**
     * Obtiene el codigoFinal de la consulta
     */
    private String codigoFinal;

    /**
     * Obtiene el pediodoInicial de la consulta
     */
    private String pediodoInicial;

    /**
     * Obtiene el periodoFinal de la consulta
     */
    private String periodoFinal;

    /**
     * Obtiene el periodoInicialPer de la consulta el campo numero uno
     * para porder realizar validaciones
     */
    private int periodoInicialPer;

    /**
     * Obtiene el periodoFinalPer de la consulta el campo numero uno
     * para porder realizar validaciones
     */
    private int periodoFinalPer;

    /**
     * Para poder realizar el reporte
     */
    private StreamedContent archivoDescarga;

    /**
     * Cambia el estado de visible a false del codigo final y el
     * inicial
     */
    private boolean bloquearCodigo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacmbCodigoInicial;
    private RegistroDataModelImpl listaCmbCodigoFinal;
    private RegistroDataModelImpl listacmbPeriodoInicial;
    private RegistroDataModelImpl listacmbPeriodoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeMedidoresNuevos
     */
    public InformeMedidoresNuevos() {
        super();
        codigoRutaConstante = "CODIGORUTA";
        periodoCompleto = "NOMBREDEPERIODO";
        si = "SI";
        periodoInicialPer = 0;
        periodoFinalPer = 0;
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {

            numFormulario = GeneralCodigoFormaEnum.INFORME_MEDIDORES_NUEVOS
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbPeriodoInicial();
        cargarListacmbPeriodoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        periodoInicialPer = 0;
        periodoFinalPer = 0;
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeMedidoresNuevosUrlEnum.URL6690
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeMedidoresNuevosUrlEnum.URL7241
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);
    }

    public void cargarListaCmbCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeMedidoresNuevosUrlEnum.URL7835
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(InformeMedidoresNuevosEnum.PARAM0.getValue(), codigoIncial);

        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);
    }

    public void cargarListacmbPeriodoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeMedidoresNuevosUrlEnum.URL8428
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbPeriodoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, periodoCompleto);
    }

    public void cargarListacmbPeriodoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeMedidoresNuevosUrlEnum.URL8430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoInicialPer);

        listacmbPeriodoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, periodoCompleto);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Para poder imprimir el pdf de los reportes
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (periodoInicialPer <= periodoFinalPer) {
            try {
                if (SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(
                                compania, "FORMATO CALIDAD", modulo, new Date(),
                                false),
                                "NO").equals(si)) {
                    genInforme(FORMATOS.PDF, "001149rptMedidoresNuevosCOS");
                }
                else {
                    genInforme(FORMATOS.PDF, "001148rptMedidoresNuevos");

                }
            }
            catch (SystemException e) {
                Logger.getLogger(InformeMedidoresNuevos.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1734"));
        }

    }

    /**
     * Para poder imprimir el Excel de los reportes
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (periodoInicialPer <= periodoFinalPer) {
            try {
                if (SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(
                                compania, "FORMATO CALIDAD", modulo, new Date(),
                                false),
                                "NO").equals(si)) {
                    genInforme(FORMATOS.EXCEL, "001149rptMedidoresNuevosCOS");
                }
                else {
                    genInforme(FORMATOS.EXCEL, "001148rptMedidoresNuevos");

                }
            }
            catch (SystemException e) {
                Logger.getLogger(InformeMedidoresNuevos.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1734"));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     *
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoIncial = codigoFinal = "";
        cargarListacmbCodigoInicial();
        cargarListaCmbCodigoFinal();
    }

    // </CODIGO_DESARROLLADO>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoIncial = registroAux.getCampos()
                        .get(codigoRutaConstante).toString();
        codigoFinal = "";
        cargarListaCmbCodigoFinal();

    }

    public void seleccionarFilaCmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRutaConstante)
                        .toString();
    }

    public void seleccionarFilacmbPeriodoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        pediodoInicial = registroAux.getCampos().get(periodoCompleto)
                        .toString();
        periodoInicialPer = Integer.parseInt(
                        registroAux.getCampos().get("PERCOMPLETO").toString());
        cargarListacmbPeriodoFinal();
    }

    public void seleccionarFilacmbPeriodoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        periodoFinal = registroAux.getCampos().get(periodoCompleto).toString();
        periodoFinalPer = Integer.parseInt(
                        registroAux.getCampos().get("PERCOMPLETO").toString());
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>

    public String getCiclo() {
        return ciclo;
    }

    public int getPeriodoFinalPer() {
        return periodoFinalPer;
    }

    public void setPeriodoFinalPer(int periodoFinalPer) {
        this.periodoFinalPer = periodoFinalPer;
    }

    public int getPeriodoInicialPer() {
        return periodoInicialPer;
    }

    public void setPeriodoInicialPer(int periodoInicialPer) {
        this.periodoInicialPer = periodoInicialPer;
    }

    public boolean isBloquearCodigo() {
        return bloquearCodigo;
    }

    public void setBloquearCodigo(boolean bloquearCodigo) {
        this.bloquearCodigo = bloquearCodigo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoIncial() {
        return codigoIncial;
    }

    public void setCodigoIncial(String codigoIncial) {
        this.codigoIncial = codigoIncial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getPediodoInicial() {
        return pediodoInicial;
    }

    public void setPediodoInicial(String pediodoInicial) {
        this.pediodoInicial = pediodoInicial;
    }

    public String getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }

    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    public RegistroDataModelImpl getListaCmbCodigoFinal() {
        return listaCmbCodigoFinal;
    }

    public void setListaCmbCodigoFinal(
        RegistroDataModelImpl listaCmbCodigoFinal) {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }

    public RegistroDataModelImpl getListacmbPeriodoInicial() {
        return listacmbPeriodoInicial;
    }

    public void setListacmbPeriodoInicial(
        RegistroDataModelImpl listacmbPeriodoInicial) {
        this.listacmbPeriodoInicial = listacmbPeriodoInicial;
    }

    public RegistroDataModelImpl getListacmbPeriodoFinal() {
        return listacmbPeriodoFinal;
    }

    public void setListacmbPeriodoFinal(
        RegistroDataModelImpl listacmbPeriodoFinal) {
        this.listacmbPeriodoFinal = listacmbPeriodoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", codigoIncial);
            reemplazar.put("cicloFinal", codigoFinal);
            reemplazar.put("periodoInicialPer", periodoInicialPer);
            reemplazar.put("periodoFinalPer", periodoFinalPer);
            reemplazar.put("compania", compania);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_INFORMEMEDIDORESNUEVOS_CICLO", ciclo);
            parametros.put("PR_FORMS_INFORMEMEDIDORESNUEVOS_CMBCODIGOINICIAL",
                            codigoIncial);
            parametros.put("PR_FORMS_INFORMEMEDIDORESNUEVOS_CMBCODIGOFINAL",
                            codigoFinal);
            parametros.put("PR_FORMS_INFORMEMEDIDORESNUEVOS_CMBPERIODOINICIAL_NOMBRE",
                            pediodoInicial);
            parametros.put("PR_FORMS_INFORMEMEDIDORESNUEVOS_CMBPERIODOFINAL_NOMBRE",
                            periodoFinal);
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
}
