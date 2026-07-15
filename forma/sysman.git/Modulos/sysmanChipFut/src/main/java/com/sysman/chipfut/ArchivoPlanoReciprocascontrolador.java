/*-
 * ArchivoPlanoReciprocascontrolador.java
 *
 * 1.0
 * 
 * 21/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
/**
 * Clase que permite leer un archivo plano y subirlo a la base de datos como registros
 *
 * @version 1.0, 21/11/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class  ArchivoPlanoReciprocascontrolador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos SubirArchivo y funciona como contenedor del archivo que se
     * desea cargar
     */
 private UploadedFile archivoCargasubirArchivo;
 private StreamedContent archivoDescarga;               
    /**             
     * Crea una nueva instancia de ArchivoPlanoReciprocascontrolador
     */
    public ArchivoPlanoReciprocascontrolador() {
  super();
            compania = SessionUtil.getCompania();
        try {
numFormulario=1990;
            validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
		abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
  @Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
	  
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT2942
     * en la vista
     *
     *
     */
  public void oprimirAceptar() {
      //<CODIGO_DESARROLLADO>
	  try {
	  archivoDescarga = null;
	  String subirArchivo = archivoCargasubirArchivo.getFileName();
	  StringBuilder textoArchivo = new StringBuilder("");
	  if (SysmanFunciones.validarVariableVacio(subirArchivo)) {
          JsfUtil.agregarMensajeError(idioma.getString("TB_TB5258"));
          return;
      }
	  
	  InputStream is;
		is = archivoCargasubirArchivo.getInputstream();
		
		 InputStreamReader r = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(r);
         
         String linea;
         while ((linea = br.readLine()) != null) {
        	 linea = linea.replace("\t", SysmanConstantes.SEPARADOR_COL);
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
//         archivoDescarga = JsfUtil.getArchivoDescarga(
//                 JsfUtil.serializarPlano(ejbContabilizar
//                                 .contabilizarPorPlano(compania,
//                                                 ckResumido,
//                                                 ckSinPpto,
//                                                 ckTerceroDet,
//                                                 ckConciliar,
//                                                 contenidoPlano,
//                                                 SessionUtil.getUser()
//                                                                 .getCodigo())),
//                 "Novedades.txt");
//
// archivoCargaplano = null;

         
         
	} catch (IOException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
     //</CODIGO_DESARROLLADO>
 }
 /**
  * 
  * Metodo ejecutado al oprimir el boton Cancelar
  * en la vista
  *
  *
  */
public void oprimirCancelar() {
      //<CODIGO_DESARROLLADO>
	RequestContext.getCurrentInstance().closeDialog(null);
     //</CODIGO_DESARROLLADO>
 }


//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto contArchivoSubirArchivo
     * 
     * @return contArchivoSubirArchivo
     */
public UploadedFile getArchivoCargasubirArchivo() {
        return archivoCargasubirArchivo;
    }
    /**
     * Asigna el objeto contArchivoSubirArchivo
     * 
     * @param contArchivoSubirArchivo
     * Variable a asignar en contArchivoSubirArchivo
     */
    public void setArchivoCargasubirArchivo(UploadedFile archivoCargasubirArchivo) {
        this.archivoCargasubirArchivo = archivoCargasubirArchivo;
    }
   
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
