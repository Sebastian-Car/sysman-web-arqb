package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.general.enums.ArmarDireccionesControladorEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dmaldonado
 * @version 1, 22/02/2016
 * 
 * @version 1.1, 03/04/2017, pespitia :<br>
 * Refactoring y buenas practicas SonarLint.
 */
@ManagedBean
@ViewScoped
public class ArmarDireccionesControlador extends BeanBaseModal {

    private final String compania;
    private String tipo;
    private String letra1;
    private String orientacion1;
    private String letra2;
    private String orientacion2;
    private String otros;
    private String numero1;
    private String direccion;
    private String letrasOtros;
    private String numero2;
    private String numero3;

    /**
     * Creates a new instance of ArmarDireccionesControlador
     */
    public ArmarDireccionesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                letrasOtros = (String) parametrosEntrada
                                .get("direccionInicial");
            }

        }
        catch (Exception ex) {
            Logger.getLogger(ArmarDireccionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    public void oprimirCmdBorrar() {
        // <CODIGO_DESARROLLADO>
        direccion = "";
        tipo = "";
        numero1 = "";
        numero2 = "";
        numero3 = "";
        letra1 = "";
        letra2 = "";
        letrasOtros = "";
        orientacion1 = "";
        orientacion2 = "";
        otros = "";
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdAceptar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametrosSalida = new HashMap<>();

        parametrosSalida.put(
                        ArmarDireccionesControladorEnum.PR_DIRECCION.getValue(),
                        direccion);

        SessionUtil.setFlash(parametrosSalida);
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbTipo() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbLetra1() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbOrientacion1() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbLetra2() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbOrientacion2() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbOtros() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtNumero() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtLetras() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtNum2() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTxtNum3() {
        // <CODIGO_DESARROLLADO>
        armarDireccion();
        // </CODIGO_DESARROLLADO>
    }

    public void armarDireccion() {
        String[] parametros = { verificarVariable(tipo),
                                verificarVariable(numero1),
                                verificarVariable(letra1),
                                verificarVariable(orientacion1),
                                verificarVariable(numero2).trim().isEmpty() ? ""
                                    : "No. " + verificarVariable(numero2),
                                verificarVariable(letra2),
                                verificarVariable(orientacion2),
                                verificarVariable(numero3).trim().isEmpty() ? ""
                                    : "- " + verificarVariable(numero3),
                                verificarVariable(otros),
                                verificarVariable(letrasOtros).trim() };

        direccion = SysmanFunciones.concatenar(parametros);

    }

    public String verificarVariable(String var) {
        return SysmanFunciones.validarVariableVacio(var) ? "" : var + " ";
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLetra1() {
        return letra1;
    }

    public void setLetra1(String letra1) {
        this.letra1 = letra1;
    }

    public String getOrientacion1() {
        return orientacion1;
    }

    public void setOrientacion1(String orientacion1) {
        this.orientacion1 = orientacion1;
    }

    public String getLetra2() {
        return letra2;
    }

    public void setLetra2(String letra2) {
        this.letra2 = letra2;
    }

    public String getOrientacion2() {
        return orientacion2;
    }

    public void setOrientacion2(String orientacion2) {
        this.orientacion2 = orientacion2;
    }

    public String getOtros() {
        return otros;
    }

    public void setOtros(String otros) {
        this.otros = otros;
    }

    public String getNumero1() {
        return numero1;
    }

    public void setNumero1(String numero1) {
        this.numero1 = numero1;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLetrasOtros() {
        return letrasOtros;
    }

    public void setLetrasOtros(String letrasOtros) {
        this.letrasOtros = letrasOtros;
    }

    public String getNumero2() {
        return numero2;
    }

    public void setNumero2(String numero2) {
        this.numero2 = numero2;
    }

    public String getNumero3() {
        return numero3;
    }

    public void setNumero3(String numero3) {
        this.numero3 = numero3;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }
}
