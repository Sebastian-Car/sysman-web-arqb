package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.ContabilizarapropiacioninicialControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 16/06/2016
 *
 * -- Modificado por lcortes 17/04/2017 17:10. Ajustes Refactoring.
 * 
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 * 
 */
@ManagedBean
@ViewScoped
public class ContabilizarapropiacioninicialControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private boolean bloqueado;
    private boolean reescApropiacion;
    private boolean finalizado;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * ContabilizarapropiacioninicialControlador
     */
    public ContabilizarapropiacioninicialControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONTABILIZARAPROPIACIONINICIAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ContabilizarapropiacioninicialControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        anio = Integer.toString(SysmanFunciones
                        .ano(new Date()));

        // <CODIGO_DESARROLLADO>
        /*
         * FR908-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ContabilizarapropiacioninicialControladorUrlEnum.URL3140
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(ContabilizarapropiacioninicialControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        archivoDescarga = null;
        finalizado = false;
        if ((anio == null) || anio.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB628"));
            return;
        }

        try {
            String manejaProcedimientos = SysmanFunciones.nvl(ejbSysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA PROCEDIMIENTOS ALMACENADOS EN PRESUPUESTO",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), false)," ").toString();

            if ("SI".equals(manejaProcedimientos)) {
                Acciones.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                                "PCK_PRESUPUESTO.PR_MAYORIZARCUENTASHPTO",
                                "'" + compania + "', " + anio + ", 1, 13");
            }
            else {

                /*
                 * '********* Verifica si el balance ya fue
                 * contabilizado
                 **************/
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), anio);
                Registro apropiacion = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ContabilizarapropiacioninicialControladorUrlEnum.URL159
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                String apropiaciones = ((Integer) apropiacion.getCampos()
                                .get("CODIGO")).toString();

                if (!"0".equals(apropiaciones)) {
                    reescApropiacion = true;
                }
                else {
                    reescApropiacion = false;
                    contabilizarApropInicial();
                }
            }
        }
        catch (NamingException | SQLException | SystemException e) {
            Logger.getLogger(ContabilizarapropiacioninicialControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSalir() {
        RequestContext.getCurrentInstance().closeDialog(null);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarMensaje() {
        if (finalizado) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1023") + anio + ".");
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarreescribirAprop() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarreescribirAprop() {
        archivoDescarga = null;
        contabilizarApropInicial();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarreescribirAprop() {
        reescApropiacion = false;
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void contabilizarApropInicial() {
        String nombreArchivo = "";
        Clob respuesta = null;
        reescApropiacion = false;
        try {
            
            // se reemplaza llamado a Acciones por AccionesImp
            respuesta = (Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO.FC_CONTABILIZARAPROPINICIAL",
                            "'" + compania + "', " + anio, Types.CLOB);

            String cadena = Acciones.clobToStringSalto(respuesta);
            if ("1".equals(cadena)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
                bloqueado = true;
                reescApropiacion = false;
                return;
            }
            if ("0".equals(cadena)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1027") + " "
                    + anio + ".");
            }
            else {
                nombreArchivo = "Cuentas sin movimiento en apropiacion incial.txt";
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(cadena), nombreArchivo);

            }
        }
        catch (SQLException | IOException | JRException | SystemException e) {

            Logger.getLogger(ContabilizarapropiacioninicialControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public boolean isReescApropiacion() {
        return reescApropiacion;
    }

    public void setReescApropiacion(boolean reescApropiacion) {
        this.reescApropiacion = reescApropiacion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
