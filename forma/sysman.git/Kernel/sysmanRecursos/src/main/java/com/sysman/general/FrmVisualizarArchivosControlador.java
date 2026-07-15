/*-
 * FrmVisualizarArchivosControlador.java
 *
 * 1.0
 * 
 * 15/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.general.enums.FrmVisualizarArchivosControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.Base64;

/**
 * Controlador de la forma <code>frmvisualizararchivo</code>. Permite
 * visualizar archivos de tipo IMAGEN y PDF.
 * <li>No se debe generar la forma desde el migrador.
 *
 * @version 1.0, 15/06/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmVisualizarArchivosControlador extends BeanBaseModal {

    /** Ruta absoluta donde se ubica el archivo a visualizar. */
    private String ruta;

    /** Tipo de archivo a visualizar. */
    private String tipoArchivo;

    /** Codificacion del archivo en Base 64. */
    private String codeBase;

    /**
     * Titulo que se debe mostrar en el encabezado del formulario
     * modal.
     */
    private String titulo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmVisualizarArchivosControlador
     */
    public FrmVisualizarArchivosControlador() {
        super();

        try {
            // 1824
            numFormulario = GeneralCodigoFormaEnum.FRM_VISUALIZAR_ARCHIVOS_CONTROLADOR
                            .getCodigo();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                tipoArchivo = recuperarValor(paramIn,
                                FrmVisualizarArchivosControladorEnum.PR_TIPO_ARCHIVO);

                ruta = recuperarValor(paramIn,
                                FrmVisualizarArchivosControladorEnum.PR_RUTA);

                titulo = recuperarValor(paramIn,
                                FrmVisualizarArchivosControladorEnum.PR_TITULO);

                codificarArchivo();
            }
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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

    /**
     * Codifica el archivo a visualizar en base 64.
     */
    private void codificarArchivo() {
        String mimeType = SysmanConstantes.TIPO_PDF.equals(tipoArchivo)
            ? "data:application/pdf;base64,"
            : "data:image/png;base64,";

        File adjunto = new File(ruta);

        try (InputStream inputStream = new FileInputStream(adjunto)) {
            byte[] vec = new byte[(int) adjunto.length()];

            inputStream.read(vec, 0, vec.length);

            codeBase = mimeType
                            .concat(new String(Base64.encodeBase64(vec, true)));
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Permite recuperar el valor de un campo en una coleccion,
     * mediante un enumerado que contiene la clave.
     * 
     * @param miMap
     * -> Coleccion.
     * @param miEnum
     * -> Enmerado que contiene la clave.
     * @return El valor del campo en la coleccion.
     */
    private String recuperarValor(Map<String, Object> miMap,
        FrmVisualizarArchivosControladorEnum miEnum) {
        return SysmanFunciones.nvl(miMap.get(miEnum.getValue()), "").toString();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getCodeBase() {
        return codeBase;
    }

    public void setCodeBase(String codeBase) {
        this.codeBase = codeBase;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
