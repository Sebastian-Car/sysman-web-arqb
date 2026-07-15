/*-
 * CalculofacturacionControlador.java
 *
 * 1.0
 *
 * 30/03/2017
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.CalculofacturacionControladorUrlEnum;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosSieteGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Bean para el cálculo de facturación en lote de servicios publicos.
 *
 * @version 1.0, 30/03/2017
 * @author mzanguna
 * @version 2.0, 11/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuracion del controaldor *Creacion de dss y llamados
 * de ejb
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class CalculofacturacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * variable que almacen el modulo
     */
    private String modulo;
    /**
     * Variables para validar los parámetros de facturación.
     */
    private String ciclo;
    /**
     * variable que almacen al codigo ruta inicial
     */
    private String codRutaInicial;
    /**
     * variable que almacen al codigo ruta final
     */
    private String codRutaFinal;
    /**
     * variable que almacen el ciclo prefacturado
     */
    private boolean cicloPrefacturando;
    /**
     * variable que almacena el estado
     */
    private boolean ckFinal;
    /**
     * variable que almacena el estado
     */
    private boolean parManPrefactu;
    /**
     * variable que almacena el estado
     */
    private boolean visibleCkPrefactura;
    /**
     * variable que almacena el estado
     */
    private boolean parNovedadesExternas;
    /**
     * Atributo que hace visible el dialogo de ver informe de errores
     * cálculo.
     */
    private boolean muestraMensajeCalc;
    /**
     * Atributo que hace visible el dialogo que confirma el proceso de
     * cálculo.
     */
    private boolean muestraMensajeConfirm;
    /**
     * Combo que muestra el listado de ciclos.
     */
    private RegistroDataModelImpl listaCiclo;
    private boolean existeUsuarioCiclo;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosSieteGeneralRemote ejbServPublicosSieteGeneral;

    /**
     * variable de tipo cadena
     */
    private static final String CODIGOINICIAL = "CODIGOINICIAL";
    private static final String CODIGOFINAL = "CODIGOFINAL";
    private static final String PREFACTURANDO = "PREFACTURANDO";
    private static final String PR_GETUSER = "PR_GETUSER";
    private static final String NO = "NO";
    private static final String SI = "SI";
    private static final String PR_MANPREFACTURACION = "PR_MANPREFACTURACION";
    private static final String PR_NOVEDADES_EXTERNAS = "PR_NOVEDADES_EXTERNAS";

    /**
     * Crea una nueva instancia de CalculofacturacionControlador
     */
    public CalculofacturacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CALCULOFACTURACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            ckFinal = true;
            visibleCkPrefactura = false;
            muestraMensajeCalc = false;
            cicloPrefacturando = false;
            muestraMensajeConfirm = false;
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
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
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1392-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name If
         * TraerParametro(
         * "PERMITE CARGAR Y RECAUDAR NOVEDADES EXTERNAS",
         * Getcompany()) = "SI" Then Me!CmdNovedades.visible = True
         * Me!CmdNovedades.Width = Twips(2) Me!CmdNovedades.Left =
         * Twips(0.397) Me!Cancelar.Left = Twips(4.397)
         * Me!Aceptar.Left = Twips(2.407) Else Me!CmdNovedades.visible
         * = False Me!Cancelar.Left = Twips(3.508) Me!Aceptar.Left =
         * Twips(1.402) End If Me!Final.visible = False
         * Me!cuadroFinal.visible = False Me!Final = -1 End Sub
         */
        cargarParametros();
        try {
            ejbServPublicosSieteGeneral.actualizarRangos(compania);

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     * metodo que carga los parametros
     */
    public void cargarParametros() {
        try {
            parManPrefactu = parSiNo(PR_MANPREFACTURACION);
            parNovedadesExternas = parSiNo(PR_NOVEDADES_EXTERNAS);
        }
        catch (NamingException | SQLException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean parSiNo(String parametro)
                    throws NamingException, SQLException {

        String parFinal = parStrDefault(parametro, NO);

        return SI.equals(parFinal) ? true : false;
    }

    private String parStrDefault(String parametro, String defecto)
                    throws NamingException, SQLException {
        return SysmanFunciones.nvlStr(parStr(parametro, false), defecto);
    }

    private String parStr(String parametro, boolean isMayMin)

    {
        try {
            return ejbSysmanUtil.consultarParametro(compania,
                            parametros.getString(parametro), modulo, new Date(),
                            isMayMin);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalculofacturacionControladorUrlEnum.URL6464
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     * Boton en el que se llama la función de calculo de facturación.
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (!existeUsuarioCiclo) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3172"));
            return;
        }

        if (!SysmanFunciones.validarVariableVacio(ciclo)
            || !SysmanFunciones.validarVariableVacio(codRutaInicial)
            || !SysmanFunciones.validarVariableVacio(codRutaFinal)) {

            muestraMensajeConfirm = true;
        }
        else {
            muestraMensajeConfirm = false;
            JsfUtil.agregarMensajeAlerta(
                            "Faltan datos para comenzar el proceso");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * CONFIRMACALC en la vista
     *
     * Confirmación del proceso de cálculo de facturación.
     *
     */
    public void aceptarCONFIRMACALC() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String respuesta;

            respuesta = ejbServPublicosSieteGeneral.calcularFacturacion(
                            compania, Integer.parseInt(ciclo), codRutaInicial,
                            codRutaFinal, true, ckFinal,
                            SessionUtil.getUser().getCodigo());

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            Map<String, Object> rs;

            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CalculofacturacionControladorUrlEnum.URL6565
                                                            .getValue());
            rs = requestManager
                            .get(urlReg.getUrl(), parametros).getFields();

            if ((int) rs.get("CUENTA") > 0) {
                muestraMensajeCalc = true;
            }
            else {
                JsfUtil.agregarMensajeInformativo(respuesta);
            }

            muestraMensajeConfirm = false;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * MENSAJECALC en la vista
     *
     * Mensaje Aceptar si se desea ver el informe de erorres.
     *
     */
    public void aceptarMENSAJECALC() {
        // <CODIGO_DESARROLLADO>

        String reporte = "001445ErroresEnElCalculo";

        archivoDescarga = null;

        // Informe Errores cálculo.

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName()
                            .toLowerCase(), compania);
            reemplazar.put(GeneralParameterEnum.CICLO.getName().toLowerCase(),
                            ciclo);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(PR_GETUSER, SessionUtil.getUser());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            muestraMensajeCalc = false;

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     *
     * Boton que cierra el formulario modal
     *
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdNovedades en la vista
     *
     * Boton novedades de aseo.
     *
     */
    public void oprimirCmdNovedades() {
        // <CODIGO_DESARROLLADO>
        String[] campos = {};
        Object[] valores = {};

        SessionUtil.redireccionar("/serviciosPublicos/frmnovedadesplano.sysman",
                        campos, valores);

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * Se obtiene el ciclo y los codigos de ruta para llamar a la
     * función de cálculo.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();
        codRutaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGOINICIAL), "")
                        .toString();
        codRutaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGOFINAL), "")
                        .toString();
        cicloPrefacturando = (Boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get(PREFACTURANDO), false);

        if (parManPrefactu && cicloPrefacturando) {
            visibleCkPrefactura = true;
            ckFinal = false;
        }
        else {
            visibleCkPrefactura = false;
            ckFinal = true;
        }

        if (codRutaInicial.isEmpty() && codRutaFinal.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3172"));
            existeUsuarioCiclo = false;

        }
        else {
            existeUsuarioCiclo = true;
        }

    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public String getCodRutaInicial() {
        return codRutaInicial;
    }

    public void setCodRutaInicial(String codRutaInicial) {
        this.codRutaInicial = codRutaInicial;
    }

    public String getCodRutaFinal() {
        return codRutaFinal;
    }

    public void setCodRutaFinal(String codRutaFinal) {
        this.codRutaFinal = codRutaFinal;
    }

    public Boolean getCkFinal() {
        return ckFinal;
    }

    public void setCkFinal(Boolean ckFinal) {
        this.ckFinal = ckFinal;
    }

    public Boolean getVisibleCkPrefactura() {
        return visibleCkPrefactura;
    }

    public void setVisibleCkPrefactura(Boolean visibleCkPrefactura) {
        this.visibleCkPrefactura = visibleCkPrefactura;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Boolean getMuestraMensajeCalc() {
        return muestraMensajeCalc;
    }

    public void setMuestraMensajeCalc(Boolean muestraMensajeCalc) {
        this.muestraMensajeCalc = muestraMensajeCalc;
    }

    public Boolean getMuestraMensajeConfirm() {
        return muestraMensajeConfirm;
    }

    public void setMuestraMensajeConfirm(Boolean muestraMensajeConfirm) {
        this.muestraMensajeConfirm = muestraMensajeConfirm;
    }

    public Boolean getParNovedadesExternas() {
        return parNovedadesExternas;
    }

    public void setParNovedadesExternas(Boolean parNovedadesExternas) {
        this.parNovedadesExternas = parNovedadesExternas;
    }
}
