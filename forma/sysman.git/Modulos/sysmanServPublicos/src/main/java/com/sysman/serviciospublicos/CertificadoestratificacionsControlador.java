/*-
 * CertificadoestratificacionsControlador.java
 *
 * 1.0
 *
 * 03/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.CertificadoestratificacionsControladorEnum;
import com.sysman.serviciospublicos.enums.CertificadoestratificacionsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Controlador del formulario 1287
 *
 * @version 1.0, 03/02/2017
 * @author spina
 * @version 2, 16/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class CertificadoestratificacionsControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * lista para mostrar en el combo de destino
     */
    private List<Registro> listaDestino;
    /**
     * lista para mostrar los ciclos en la interaz grafica
     */
    private List<Registro> listaCiclo;
    /**
     * lista para mostrar las plantillas de impresion
     */
    private List<Registro> listaCmbPlantillaImpresion;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista para mostrar los codigos de ruta
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /*
     * Nombre a mostrar en el formulario despues de seleccionar un
     * codigo de ruta
     */
    private String nombre;
    /*
     * Nombre a mostrar en el formulario despues de seleccionar un
     * formato de impresion
     */
    private String nombreformatoImpresion;
    /*
     * codigo de formato para consultar en la base de datos
     */
    private String codigoformatoImpresion;

    private static final String CTECODIGORUTA = GeneralParameterEnum.CODIGORUTA
                    .getName();
    private static final String CTENOMBRE = GeneralParameterEnum.NOMBRE
                    .getName();
    private static final String CTEIDACTA = CertificadoestratificacionsControladorEnum.IDACTA
                    .getValue();

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * CertificadoestratificacionsControlador
     */
    public CertificadoestratificacionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CERTIFICADOESTRATIFICACIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaCmbPlantillaImpresion();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.SP_CERTIFICADOSESTRATIFICACION;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        buscarUrls();
    }
    // <METODOS_CARGAR_LISTA>

    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoestratificacionsControladorUrlEnum.URL8825
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarCiclo() {
        nombre = "";
        registro.getCampos().put(CTECODIGORUTA, "");
        cargarListaCodigoRuta();
    }

    public void cambiarCmbPlantillaImpresion() {
        nombreformatoImpresion = service.buscarEnLista(codigoformatoImpresion,
                        GeneralParameterEnum.CODIGO.getName(), CTENOMBRE,
                        listaCmbPlantillaImpresion);
    }

    public void cargarListaCmbPlantillaImpresion() {
        try {
            listaCmbPlantillaImpresion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoestratificacionsControladorUrlEnum.URL260
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCodigoRuta
     *
     */
    public void cargarListaCodigoRuta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadoestratificacionsControladorUrlEnum.URL9613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CICLO.getName()));
        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CTECODIGORUTA);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTECODIGORUTA,
                        registroAux.getCampos().get(CTECODIGORUTA));
        nombre = registroAux.getCampos().get(CTENOMBRE).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>

        if (codigoformatoImpresion == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2794"));
            return;
        }
        if (registro.getCampos().get(CTEIDACTA) == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2935"));
            return;
        }

        // Selecciona el formato mas reciente
        Date fechaGeneracion = (Date) registro.getCampos()
                        .get(CertificadoestratificacionsControladorEnum.FECHAACTA
                                        .getValue());
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoformatoImpresion);
        try {
            param.put(CertificadoestratificacionsControladorEnum.FECHAACTA
                            .getValue(), SysmanFunciones.convertirAFechaCadena(
                                            fechaGeneracion,
                                            "dd/MM/yyyy HH:mm:ss"));
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadoestratificacionsControladorUrlEnum.URL261
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2810"));
            return;
        }
        Date fecha = (Date) rs.getCampos().get("FECHA");

        String[] campos = new String[3];
        String[] valores = new String[3];
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";

        valores[0] = codigoformatoImpresion;
        valores[1] = SysmanFunciones.formatearFecha(fecha);
        valores[2] = nombreformatoImpresion;

        String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre()
                        .toUpperCase();
        String direccion = SessionUtil.getCompaniaIngreso().getDireccion();
        String companiaTelefono = SessionUtil.getCompaniaIngreso()
                        .getTelefono();
        String nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        String nuir = SessionUtil.getCompaniaIngreso().getNuir();
        nuir = nuir == null ? "" : nuir;

        String[] meses = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD;
        String nombreMes = meses[SysmanFunciones.mes(fechaGeneracion)];
        int dia = SysmanFunciones
                        .dia(fechaGeneracion);
        int ano = SysmanFunciones
                        .ano(fechaGeneracion);

        String cargoFirma = "";
        String nombreFirma = "";
        String modulo = SessionUtil.getModulo();
        try {
            cargoFirma = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO FIRMA CERTIFICADO ESTRATIFICACION",
                            modulo,
                            new Date(), true);

            nombreFirma = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE FIRMA CERTIFICADO ESTRATIFICACION",
                            modulo,
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String nombreciudad = SessionUtil.getCompaniaIngreso().getCiudad();
        // variables por parametro para documento word
        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$companiaNombre$s",
                        "'" + nombreCompania + "'");
        variablesConsultaW.put("s$companiaDireccion$s",
                        "'" + direccion + "'");
        variablesConsultaW.put("s$companiaTelefono$s",
                        "'" + companiaTelefono + "'");
        variablesConsultaW.put("s$companiaNit$s",
                        "'" + nitCompania + "'");
        variablesConsultaW.put("s$nuir$s",
                        "'" + nuir + "'");
        variablesConsultaW.put("s$idacta$s",
                        "'" + registro.getCampos().get(CTEIDACTA) + "'");
        variablesConsultaW.put("s$cargoFirma$s",
                        "'" + cargoFirma + "'");
        variablesConsultaW.put("s$nombreciudad$s",
                        "'" + nombreciudad + "'");
        variablesConsultaW.put("s$nombreMes$s",
                        "'" + nombreMes + "'");
        variablesConsultaW.put("s$dia$s",
                        "'" + dia + "'");
        variablesConsultaW.put("s$ano$s",
                        "'" + ano + "'");
        variablesConsultaW.put("s$nombreFirma$s",
                        "'" + nombreFirma + "'");

        variablesConsultaW.put("s$ciclo$s",
                        "'" + registro.getCampos().get(
                                        GeneralParameterEnum.CICLO.getName())
                            + "'");
        variablesConsultaW.put("s$codigoRuta$s",
                        "'" + registro.getCampos().get(CTECODIGORUTA) + "'");

        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);

        SessionUtil.cargarModalDatosFlash("281", SessionUtil.getModulo(),
                        campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioImprimir() {
        registro.getCampos().put(GeneralParameterEnum.IMPRESO.getName(), true);
        agregarRegistroNuevo(false);
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarListaCodigoRuta();
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos()
                            .put(CertificadoestratificacionsControladorEnum.FECHAACTA
                                            .getValue(), new Date());
        }
        if (registro.getCampos().get(CTECODIGORUTA) != null) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(CTECODIGORUTA));
            Registro rs = null;
            try {
                rs = listaCodigoRuta.getRegistroUnico(param);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            if (rs != null) {
                nombre = SysmanFunciones.nvl(rs.getCampos().get(CTENOMBRE), "")
                                .toString();
            }
            else {
                nombre = "";
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            registro.getCampos().put(CTEIDACTA,
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            "SP_CERTIFICADOSESTRATIFICACION",
                                            "SP_CERTIFICADOSESTRATIFICACION.COMPANIA = ''"
                                                + compania + "''"
                                                + "AND SP_CERTIFICADOSESTRATIFICACION.CLASE = ''CDES''",
                                            "SP_CERTIFICADOSESTRATIFICACION.IDACTA",
                                            "1"));
            registro.getCampos().put(GeneralParameterEnum.CLASE.getName(),
                            "CDES");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.IMPRESO.getName(), false);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     *
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CLASE.getName());
            registro.getCampos()
                            .remove(CertificadoestratificacionsControladorEnum.IDACTA
                                            .getValue());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     * @return
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     *
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaDestino
     *
     * @return listaDestino
     */
    public List<Registro> getListaDestino() {
        return listaDestino;
    }

    /**
     * Asigna la lista listaCmbDestino
     *
     * @param listaDestino
     * Variable a asignar en listaDestino
     */
    public void setListaDestino(List<Registro> listaDestino) {
        this.listaDestino = listaDestino;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaCmbPlantillaImpresion
     *
     * @return listaCmbPlantillaImpresion
     */
    public List<Registro> getlistaCmbPlantillaImpresion() {
        return listaCmbPlantillaImpresion;
    }

    /**
     * Asigna la lista listaCmbPlantillaImpresion
     *
     * @param listaCmbPlantillaImpresion
     * Variable a asignar en listaCmbPlantillaImpresion
     */
    public void setlistaCmbPlantillaImpresion(
        List<Registro> listaCmbPlantillaImpresion) {
        this.listaCmbPlantillaImpresion = listaCmbPlantillaImpresion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoRuta
     *
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     *
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormatoImpresion() {
        return nombreformatoImpresion;
    }

    public void setFormatoImpresion(String formatoImpresion) {
        this.nombreformatoImpresion = formatoImpresion;
    }

    public String getCodigoformatoImpresion() {
        return codigoformatoImpresion;
    }

    public void setCodigoformatoImpresion(String codigoformatoImpresion) {
        this.codigoformatoImpresion = codigoformatoImpresion;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
