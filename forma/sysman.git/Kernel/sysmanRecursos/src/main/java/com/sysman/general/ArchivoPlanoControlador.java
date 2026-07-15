/*-
 * ArchivoPlanoControlador.java
 *
 * 1.0
 *
 * 01/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.ejb.impl.EjbContabilizarCeroGeneral;
import com.sysman.controladores.SessionUtil;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario para cargar archivo plano de interfaz.
 *
 * @version 1.0, 01/02/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ArchivoPlanoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos SubirPlano y funciona como contenedor del archivo que
     * se debe guardar
     */

    private boolean ckResumido = false;
    private boolean ckSinPpto = false;
    private boolean ckTerceroDet = false;
    private boolean ckConciliar = false;
    private boolean ckPlanoSIOT = false;
    private UploadedFile archivoCargaplano;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbContabilizarCeroGeneral ejbContabilizar;

    /**
     * Crea una nueva instancia de ArchivoPlanoControlador
     */
    public ArchivoPlanoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1680;
            validarPermisos();

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
         * FR1680-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore 'formularioAbrir 1, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     * Inicio de cargue de plano
     *
     */
    public void oprimirAceptar() {
        BufferedReader br = null;
        try {
            archivoDescarga = null;
            String plano = archivoCargaplano.getFileName();
            StringBuilder textoArchivo = new StringBuilder("");
            if (SysmanFunciones.validarVariableVacio(plano)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
                return;
            }

            InputStream is;
            is = archivoCargaplano.getInputstream();

            InputStreamReader r = new InputStreamReader(is);

            br = new BufferedReader(r);
            String linea;
            while ((linea = br.readLine()) != null) {
            	linea = linea.replaceAll("\\'", "");
            	linea = linea.replaceAll("\\&", "");
            	textoArchivo.append(linea)
                                .append(SysmanConstantes.SEPARADOR_REG)
                                .append("\r\n");
            }

            String contenidoPlano = Acciones
                            .getClobConcatenado(textoArchivo.toString());
            if (",".equals(contenidoPlano
                            .substring(contenidoPlano.length() - 1))) {
                contenidoPlano = contenidoPlano.substring(0,
                                contenidoPlano.length() - 1);
            }

            if ("960201".equals(SessionUtil.getMenuActual())) {

                if(ckPlanoSIOT)
                {
                	archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbContabilizar
                                            .contabilizarPlanoSIOT(compania,
                                                            ckConciliar,
                                                            contenidoPlano,
                                                            SessionUtil.getUser()
                                                                            .getCodigo())),
                            "Novedades.txt");
                	
                }
                else
                {
	            	archivoDescarga = JsfUtil.getArchivoDescarga(
	                                JsfUtil.serializarPlano(ejbContabilizar
	                                                .contabilizarPorPlano(compania,
	                                                                ckResumido,
	                                                                ckSinPpto,
	                                                                ckTerceroDet,
	                                                                ckConciliar,
	                                                                contenidoPlano,
	                                                                SessionUtil.getUser()
	                                                                                .getCodigo())),
	                                "Novedades.txt");
                }
            }
            else {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(ejbContabilizar
                                                .contabilizarPorPlanoEsp(
                                                                compania,
                                                                ckResumido,
                                                                ckSinPpto,
                                                                ckTerceroDet,
                                                                ckConciliar,
                                                                contenidoPlano,
                                                                SessionUtil.getUser()
                                                                                .getCodigo())),
                                "Novedades.txt");
            }

            archivoCargaplano = null;
        }
        catch (IOException | JRException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            archivoCargaplano = null;
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    logger.error("Error al cerrar el buffer"
                                    .concat(e.getMessage()), e);
                }
            }
        }

    }

    public void oprimirCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isCkResumido() {
        return ckResumido;
    }

    public void setCkResumido(boolean ckResumido) {
        this.ckResumido = ckResumido;
    }

    public boolean isCkSinPpto() {
        return ckSinPpto;
    }

    public void setCkSinPpto(boolean ckSinPpto) {
        this.ckSinPpto = ckSinPpto;
    }

    public UploadedFile getArchivoCargaplano() {
        return archivoCargaplano;
    }

    public void setArchivoCargaplano(UploadedFile archivoCargaplano) {
        this.archivoCargaplano = archivoCargaplano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCkConciliar() {
        return ckConciliar;
    }

    public void setCkConciliar(boolean ckConciliar) {
        this.ckConciliar = ckConciliar;
    }

    public boolean isCkTerceroDet() {
        return ckTerceroDet;
    }

    public void setCkTerceroDet(boolean ckTerceroDet) {
        this.ckTerceroDet = ckTerceroDet;
    }

	public boolean isCkPlanoSIOT() {
		return ckPlanoSIOT;
	}

	public void setCkPlanoSIOT(boolean ckPlanoSIOT) {
		this.ckPlanoSIOT = ckPlanoSIOT;
	}

}
