package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author sdaza
 * @version 1, 11/07/2016
 * 
 * @author ybecerra
 * @version 2, 29/06/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class FacturarAbAcuerdosControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    private boolean dismCuotas;
    private boolean dismValor;
    private String vlrAbono;
    private String vlrCuota;
    private StreamedContent archivoDescarga;
    private boolean dialogoVisible;
    private String textoDialog;

    /**
     * Creates a new instance of FacturarAbAcuerdosControlador
     */
    public FacturarAbAcuerdosControlador() {
        super();

        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURAR_AB_ACUERDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                vlrCuota = (String) parametrosEntrada.get("vlrCuota");
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FacturarAbAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdFacturar en la vista
     *
     *
     */
    public void oprimirCmdFacturar() {
        // <CODIGO_DESARROLLADO>
        if (validarDatos()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3265"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdCancelar en la vista
     *
     *
     */
    public void oprimirCmdCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdProyectar en la vista
     *
     */
    public void oprimirCmdProyectar() {
        // <CODIGO_DESARROLLADO>
        if (validarDatos()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3265"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarDatos() {
        // finalidad del abono
        if (!dismCuotas && !dismValor) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB352"));
            return false;
        }

        if (Double.parseDouble(vlrAbono) <= Double
                        .parseDouble(vlrCuota)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB353"));
            return false;
        }

        Double saldoAcuerdo = 0.0;
        // calcular el saldo del acuerdo de pago

        if (Double.parseDouble(vlrAbono) >= saldoAcuerdo) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB355"));
            return false;
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control disminucionCuotas
     * 
     * 
     */
    public void cambiardisminucionCuotas() {
        // <CODIGO_DESARROLLADO>
        if (dismCuotas) {
            dismValor = false;
        }
        else {
            dismValor = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control disminucionValor
     * 
     * 
     */
    public void cambiardisminucionValor() {
        // <CODIGO_DESARROLLADO>
        if (dismValor) {
            dismCuotas = false;
        }
        else {
            dismCuotas = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * ejecutarFuncion en la vista
     *
     *
     */
    public void aceptarejecutarFuncion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable dismValor
     * 
     * @return dismValor
     */
    public boolean isDismValor() {
        return dismValor;
    }

    /**
     * Asigna la variable dismValor
     * 
     * @param dismValor
     * Variable a asignar en dismValor
     */
    public void setDismValor(boolean dismValor) {
        this.dismValor = dismValor;
    }

    /**
     * Retorna la variable dismCuotas
     * 
     * @return dismCuotas
     */
    public void setDismCuotas(boolean dismCuotas) {
        this.dismCuotas = dismCuotas;
    }

    /**
     * Asigna la variable dismCuotas
     * 
     * @param dismCuotas
     * Variable a asignar en dismCuotas
     */
    public boolean isDismCuotas() {
        return dismCuotas;
    }

    /**
     * Retorna la variable vlrAbono
     * 
     * @return vlrAbono
     */
    public void setVlrAbono(String vlrAbono) {
        this.vlrAbono = vlrAbono;
    }

    /**
     * Asigna la variable vlrAbono
     * 
     * @param vlrAbono
     * Variable a asignar en vlrAbono
     */
    public String getVlrAbono() {
        return vlrAbono;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
