/*-
 * FrmpersuasivosControlador.java
 *
 * 1.0
 *
 * 15 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyac�.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmpersuasivosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmpersuasivosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario FrmpersuasivosControlador.
 *
 * @author cperez
 * @version 1, 15/09/2016
 * @modified jguerrero
 * @version 2. 31/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class FrmpersuasivosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String estadoConstante;
    private final String codigoConstante;
    private final String codigoRutaConstante;
    private final String codigoRutaConstanteS;
    private final String administraCobroCoactivoPersuasivoContante;
    private final String codigoInternoConstante;
    private final String periodoCierreCons;
    private final String idCobroActCons;
    private final String periodoCons;
    private String ciclo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable para bloquear o desbloquear el boton de impresion de
     * word.
     */
    private boolean bloqueaWord;
    private int indice;
    /**
     * Util para conocer el codigo interno seleccionado del suscriptor
     * seleccionado.
     */
    private String codigoInterno;
    private String idBoton;
    /**
     * Util para la lista del combo formateado.
     */
    private String formateado;

    /**
     * Util para obtener el estado del filtro seleccionado.
     */
    private String filtroEstado;

    /**
     * Util obtener la consulta sql.
     */
    private String strsql;

    /**
     * Util obtener la Fila del Filtro del CODIGO INTERNO.
     */
    private String auxiliar;

    private StreamedContent archivoDescarga;
    private final String modulo;
    private boolean bolEstado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private boolean filtrosVisible;
    private boolean idCobroCoact;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /** Lista del Codigo Interno. */
    private RegistroDataModelImpl listaFiltroCodInterno;
    private RegistroDataModelImpl listaFiltroCodInternoE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista del Codigo RUTA1. */
    private RegistroDataModelImpl listaCODIGORUTA1;

    /** Lista del Codigo RUTA1E. */
    private RegistroDataModelImpl listaCODIGORUTA1E;

    /** Lista del registro Formateado. */
    private RegistroDataModelImpl listaFormateado;

    /** Lista del registro FormateadoE. */
    private RegistroDataModelImpl listaFormateadoE;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmpersuasivosControlador
     */
    public FrmpersuasivosControlador() {
        super();
        formateado = "";
        estadoConstante = GeneralParameterEnum.ESTADO.getName();
        codigoConstante = GeneralParameterEnum.CODIGO.getName();
        codigoRutaConstante = GeneralParameterEnum.CODIGORUTA.getName();
        codigoRutaConstanteS = "s$codigoRuta$s";
        administraCobroCoactivoPersuasivoContante = "ADMINISTRA COBRO COACTIVO EN PERSUASIVO";
        periodoCierreCons = "PERIODOCIERRE";
        periodoCons = "PERIODO";
        idCobroActCons = "IDCOBRO_COACT";

        codigoInternoConstante = "CODIGOINTERNO";
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        bloqueaWord = true;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMPERSUASIVOS_CONTROLADOR
                            .getCodigo();
            /* Por omision tome el servicio de acueducto. */
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                ciclo = parametros.get("ciclo").toString();
            }
            else {
                SessionUtil.redireccionarMenu();
            }
            // <INI_ADICIONAL>
            filtrosVisible = false;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmpersuasivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = FrmpersuasivosControladorEnum.PARAM6.getValue();
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        cargarListaFiltroCodInterno();
        cargarListaFiltroCodInternoE();
        cargarListaFormateado();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCODIGORUTA1();
        cargarListaCODIGORUTA1E();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        parametrosListado.put("CODIGOINTERNO", codigoInterno);
        parametrosListado.put("ESTADO", filtroEstado);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL14079
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL14078
                                                        .getValue());

    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaFiltroCodInterno() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL10443
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaFiltroCodInterno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoInternoConstante);
    }

    public void cargarListaFiltroCodInternoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL10443
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaFiltroCodInternoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoInternoConstante);

    }

    public void cargarListaFormateado() {

        Map<String, Object> param = new TreeMap<>();
        param.put("TIPO", "27");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL12076
                                                        .getValue());
        listaFormateado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "LLAVE");

    }

    public void cargarListaFormateadoE() {

        Map<String, Object> param = new TreeMap<>();
        param.put("TIPO", "27");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL12076
                                                        .getValue());
        listaFormateadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "LLAVE");
    }

    public void cargarListaCODIGORUTA1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL13248
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCODIGORUTA1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);

    }

    public void cargarListaCODIGORUTA1E() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpersuasivosControladorUrlEnum.URL13248
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCODIGORUTA1E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaConstante);
    }

    public void oprimirWord(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(formateado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1634"));
        }
        else {

            Date fechaActual = new Date();
            try {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.CODIGO.getName(), formateado);
                Registro rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmpersuasivosControladorUrlEnum.URL33363
                                                                                .getValue())
                                                .getUrl(), param));

                Date fecha = (Date) rs.getCampos().get("FECHA");

                String strNombreDocumento = idioma.getString("TB_TB1635");
                strNombreDocumento = strNombreDocumento.replace(
                                codigoRutaConstanteS,
                                reg.getCampos()
                                                .get(codigoRutaConstante)
                                                .toString());

                strNombreDocumento = strNombreDocumento + " " + SysmanFunciones
                                .convertirAFechaCadena(fechaActual)
                                .replace("/", "")
                    + "_"
                    + SysmanFunciones.convertirAHoraCadena(fechaActual).replace(
                                    ":",
                                    "");

                String[] campos = new String[3];
                String[] valores = new String[3];
                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";

                valores[0] = formateado;
                valores[1] = SysmanFunciones.formatearFecha(fecha);
                valores[2] = strNombreDocumento;

                HashMap<String, String> variablesConsultaW = new HashMap<>();

                variablesConsultaW.put(codigoRutaConstanteS,
                                "'" + reg.getCampos().get(codigoRutaConstante)
                                    + "'");
                variablesConsultaW.put("s$idCobro$s",
                                "'" + reg.getCampos().get("IDCOBRO") + "'");

                // variables por parametro para documento word
                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);

                SessionUtil.cargarModalDatosFlash(Integer
                                .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(),
                                campos,
                                valores);

                // </CODIGO_DESARROLLADO>
            }
            catch (ParseException | SystemException ex) {

                Logger.getLogger(FrmpersuasivosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);

                JsfUtil.agregarMensajeError(ex.getMessage());

            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFiltrar() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String condicion = "";
        String z = "Z";
        String consulta;
        if ((codigoInterno != null) && !"".equals(codigoInterno)
            && !codigoInterno.equals(z)) {
            condicion = condicion + " AND SP_USUARIO.CODIGOINTERNO = '"
                + codigoInterno + "'";
        }
        else {
            condicion = condicion + " ";
        }
        if ((filtroEstado != null) && !"".equals(filtroEstado)
            && !filtroEstado.equals(z)) {
            condicion = condicion + " AND SP_COBROSPERSUASIVOS.ESTADO = '"
                + filtroEstado + "'";
        }
        else {
            condicion = condicion + " ";
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("ciclo", ciclo);
        reemplazar.put("condicion",
                        SysmanFunciones.validarVariableVacio(condicion) ? ""
                            : condicion);

        consulta = Reporteador.resuelveConsulta("800117CobroPersuasivo",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </CODIGO_DESARROLLADO>
    // </CODIGO_DESARROLLADO>

    /**
     * Cambia el estado del suscriptor .
     *
     * @param Cambiar
     * Cambiar Estado.
     */
    public void cambiarESTADO() {

        //

    }

    public void cambiarESTADOC(int rowNum) {
        // </CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarredireccionarWord() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void retornarFormularioWord(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFormateadoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaFiltroCodInterno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInterno = retornarVariable(registroAux, codigoInternoConstante);

    }

    public void seleccionarFilaFiltroCodInternoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarVariable(registroAux, codigoInternoConstante);
    }

    public void seleccionarFilaCODIGORUTA1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoRutaConstante,
                        registroAux.getCampos().get(codigoRutaConstante));
    }

    public void seleccionarFilaCODIGORUTA1E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoRutaConstante).toString();
    }

    public void seleccionarFilaFormateado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formateado = registroAux.getCampos().get(codigoConstante).toString();

    }

    public void seleccionarFilaFormateadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoConstante).toString();
        formateado = auxiliar;
    }

    // </METODOS_COMBOS_GRANDES>
    @Override

    public void abrirFormulario() {
        String si = "SI";
        try {
            if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "FILTROS ADMINISTRA COBROS PERSUASIVOS", modulo,
                            new Date(), true),
                            "NO").equals(si)) {
                filtrosVisible = true;
            }
            else {
                filtrosVisible = false;
            }
            if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            administraCobroCoactivoPersuasivoContante, modulo,
                            new Date(), true),
                            "NO").equals(si)) {
                idCobroCoact = true;
            }
            else {
                idCobroCoact = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(FrmpersuasivosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        procesoAntesActua();
        registro.getCampos().remove("PERCIERRE");
        registro.getCampos().remove("NOMBRECODIGORUTA");
        registro.getCampos().remove(codigoInternoConstante);
        registro.getCampos().remove("formateado");
        registro.getCampos().remove("PER");
        registro.getCampos().remove("ESTADOAUXILIAR");
        registro.getCampos().remove("FECHAACTA");

        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove("ANOCIERRE");
        registro.getCampos().remove("PERIODOSATRASO");
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(periodoCierreCons);
        registro.getCampos().remove(periodoCons);
        registro.getCampos().remove("IDCOBRO");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove("FECHACIERRE");
        registro.getLlave();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo para actualizar despues de
     */
    @Override
    public boolean actualizarDespues() {

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // Sirve para remover los campos

    }

    @Override
    public void asignarValoresRegistro() {
        // este metodo no se usa
    }

    // <SET_GET_ATRIBUTOS>
    public String getIdBoton() {
        return idBoton;
    }

    public void setIdBoton(String idBoton) {
        this.idBoton = idBoton;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getFormateado() {
        return formateado;
    }

    public void setFormateado(String formateado) {
        this.formateado = formateado;
    }

    public String getStrsql() {
        return strsql;
    }

    public void setStrsql(String strsql) {
        this.strsql = strsql;
    }

    public String getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isIdCobroCoact() {
        return idCobroCoact;
    }

    public void setIdCobroCoact(boolean idCobroCoact) {
        this.idCobroCoact = idCobroCoact;
    }

    public boolean isFiltrosVisible() {
        return filtrosVisible;
    }

    public void setFiltrosVisible(boolean filtrosVisible) {
        this.filtrosVisible = filtrosVisible;
    }

    public boolean isBloqueaWord() {
        return bloqueaWord;
    }

    public void setBloqueaWord(boolean bloqueaWord) {
        this.bloqueaWord = bloqueaWord;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaFiltroCodInterno() {
        return listaFiltroCodInterno;
    }

    public void setListaFiltroCodInterno(
        RegistroDataModelImpl listaFiltroCodInterno) {
        this.listaFiltroCodInterno = listaFiltroCodInterno;
    }

    public RegistroDataModelImpl getListaFiltroCodInternoE() {
        return listaFiltroCodInternoE;
    }

    public void setListaFiltroCodInternoE(
        RegistroDataModelImpl listaFiltroCodInternoE) {
        this.listaFiltroCodInternoE = listaFiltroCodInternoE;
    }

    public RegistroDataModelImpl getListaFormateado() {
        return listaFormateado;
    }

    public void setListaFormateado(RegistroDataModelImpl listaFormateado) {
        this.listaFormateado = listaFormateado;
    }

    public RegistroDataModelImpl getListaFormateadoE() {
        return listaFormateadoE;
    }

    public void setListaFormateadoE(RegistroDataModelImpl listaFormateadoE) {
        this.listaFormateadoE = listaFormateadoE;
    }

    public RegistroDataModelImpl getListaCODIGORUTA1() {
        return listaCODIGORUTA1;
    }

    public boolean isBolEstado() {
        return bolEstado;
    }

    public void setBolEstado(boolean bolEstado) {
        this.bolEstado = bolEstado;
    }

    public void setListaCODIGORUTA1(RegistroDataModelImpl listaCODIGORUTA1) {
        this.listaCODIGORUTA1 = listaCODIGORUTA1;
    }

    public RegistroDataModelImpl getListaCODIGORUTA1E() {
        return listaCODIGORUTA1E;
    }

    public void setListaCODIGORUTA1E(RegistroDataModelImpl listaCODIGORUTA1E) {
        this.listaCODIGORUTA1E = listaCODIGORUTA1E;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        idBoton = "FRFR1095:TBFR1095:" + indice + ":BT2101";
        formateado = null;

        String c = "C";
        String t = "T";
        String si = "SI";
        try {
            if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            administraCobroCoactivoPersuasivoContante, modulo,
                            new Date(), true),
                            "NO").equals(si)) {
                if (registro.getCampos().get(estadoConstante).equals(c)) {
                    bolEstado = true;
                }
                else {
                    bolEstado = false;
                }
            }
            else {
                if (registro.getCampos().get(estadoConstante).equals(c)
                    || registro.getCampos().get(estadoConstante).equals(t)) {
                    bolEstado = true;
                }
            }

        }
        catch (SystemException e) {
            Logger.getLogger(FrmpersuasivosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    private String retornarVariable(Registro reg, String variable) {

        return SysmanFunciones.validarCampoVacio(reg.getCampos(), variable)
            ? null : reg.getCampos().get(variable).toString();

    }

    private void procesoAntesActua() {
        try {
            String c = "C";
            String t = "T";
            String si = "SI";
            // <CODIGO_DESARROLLADO>

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaConstante));

            Registro regAno = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmpersuasivosControladorUrlEnum.URL11262
                                                                            .getValue())
                                            .getUrl(), param));

            if (registro.getCampos().get(estadoConstante).equals(c)
                || registro.getCampos().get(estadoConstante).equals(t)) {
                registro.getCampos().put("ANOCIERRE",
                                regAno.getCampos().get(GeneralParameterEnum.ANO
                                                .getName()));
                registro.getCampos().put(periodoCierreCons,
                                regAno.getCampos().get(periodoCons));
                registro.getCampos().put(periodoCierreCons, new Date());

                Registro reg = new Registro();

                Map<String, Object> paramReg = new TreeMap<>();
                paramReg.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                if (registro.getCampos().get(estadoConstante).equals(c)) {

                    reg = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmpersuasivosControladorUrlEnum.URL12674
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    paramReg));

                }
                if (registro.getCampos().get(estadoConstante).equals(t)) {

                    reg = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmpersuasivosControladorUrlEnum.URL12675
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    paramReg));

                }

                if (reg.getCampos().get(codigoConstante) != null)

                {
                    String strCodTipo = (reg.getCampos()
                                    .get(codigoConstante))
                                                    .toString();

                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    FrmpersuasivosControladorUrlEnum.URL14081
                                                                    .getValue());
                    Map<String, Object> fields = new TreeMap<>();
                    fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    fields.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                    fields.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                    registro.getCampos()
                                                    .get(codigoRutaConstante));
                    fields.put("TITPOCOBRO", strCodTipo);

                    Parameter parameter = new Parameter();
                    parameter.setFields(fields);

                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(),
                                    parameter);

                }

            }

            if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            administraCobroCoactivoPersuasivoContante, modulo,
                            new Date(), true),
                            "NO").equals(si)) {

                String criterio = " SP_COBROSPERSUASIVOS.COMPANIA = ''"
                    + compania + "'' " +
                    " AND SP_COBROSPERSUASIVOS.CICLO = " + ciclo +
                    " AND SP_COBROSPERSUASIVOS.ANO = "
                    + registro.getCampos()
                                    .get(GeneralParameterEnum.ANO.getName())
                    +
                    " AND SP_COBROSPERSUASIVOS.PERIODO= "
                    + registro.getCampos().get(periodoCons) +
                    " AND SP_COBROSPERSUASIVOS.ESTADO NOT IN (''T'')";

                Long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                                "SP_COBROSPERSUASIVOS", criterio,
                                idCobroActCons);

                registro.getCampos().put(idCobroActCons,
                                consecutivo);
            }
            else {
                String consecutivoInicial = (String) SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CONSECUTIVO INICIAL COBRO COACTIVO",
                                                modulo,
                                                new Date(), true),
                                                "NO");

                registro.getCampos().put(idCobroActCons,
                                consecutivoInicial);
            }

        }
        catch (SystemException e)

        {
            Logger.getLogger(FrmpersuasivosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}
