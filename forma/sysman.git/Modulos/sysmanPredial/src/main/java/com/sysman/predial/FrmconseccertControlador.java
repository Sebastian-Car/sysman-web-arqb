package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

/**
 *
 * @author dsuesca
 * @version 1, 23/05/2016
 * 
 * @author eamaya
 * @version 1.1, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 * 
 */
@ManagedBean
@ViewScoped
public class FrmconseccertControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String actual;
    private String nuevo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmconseccertControlador NOTA: Este
     * formulario es reemplazado por formulario N�meros de factura
     */
    public FrmconseccertControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCONSECCERT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmconseccertControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        initAdicional();
    }

    public void initAdicional() {

        Registro rs = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT MAX(NUMCER) CONSEC " +

                            "FROM IP_CERTIFICADOCATAS");

        if ((rs == null) || (rs.getCampos().get("CONSEC") == null)) {
            actual = "0000000000";
        }
        else {
            actual = SysmanFunciones.strZero(
                            rs.getCampos().get("CONSEC").toString(), 10);
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR761-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim
         * db As DAO.Database Dim Rs As DAO.Recordset Dim StrSql As
         * String Set db = CurrentDb() StrSql =
         * "SELECT MAX(NUMCER) AS ConsActual FROM CERTIFICADOVALORIZACION"
         * Set Rs = db.OpenRecordset(StrSql) If Not Rs.EOF And Not
         * IsNull(Rs!ConsActual) Then Me!TxtConseActual =
         * strzero(Rs!ConsActual, 10) Else Me!TxtConseActual =
         * strzero(0, 10) End If formularioAbrir 60, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAceptar() {
        // <CODIGO_DESARROLLADO>
        int actualizar = 0;

        if ((actual == "0000000000")
            || (Integer.parseInt(actual) < Integer.parseInt(nuevo))) {
            try {
                actualizar = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                                "PARAMETRO", "VALOR = '" + nuevo + "'",
                                "NOMBRE = 'CONSECUTIVO CATASTRAL' AND MODULO = "
                                    + SessionUtil.getModulo());
            }
            catch (IllegalAccessException | InstantiationException
                            | ClassNotFoundException | SQLException
                            | NamingException ex) {
                Logger.getLogger(FrmconseccertControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
            int anio = SysmanFunciones.getParteFecha(
                            Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN),
                            Calendar.YEAR);
            String campos = "COMPANIA, NUMCER, CODIGOPREDIO, NOMBRE, NIT, NUMERO_ORDEN, FECHA_EXP, VALOR, ANOAVALUO";
            String valores = compania + ",'"
                + String.format("%010d", Integer.parseInt(nuevo) - 1)
                + "', 'N/A', 'N/A', 'N/A', 'N/A', SYSDATE, 0," + anio;

            try {
                actualizar = actualizar
                    + Acciones.insertarRegistro(ConectorPool.ESQUEMA_SYSMAN,
                                    "IP_CERTIFICADOCATAS", campos, valores);
            }
            catch (IllegalAccessException | InstantiationException
                            | ClassNotFoundException | SQLException
                            | NamingException ex) {
                Logger.getLogger(FrmconseccertControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }

            if (actualizar >= 2) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB937"));
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB938"));
            }
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB939"));
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getNuevo() {
        return nuevo;
    }

    public void setNuevo(String nuevo) {
        this.nuevo = nuevo;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
