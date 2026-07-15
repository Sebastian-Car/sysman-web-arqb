/*-
 * ReclasificacionSiifControlador.java
 *
 * 1.0
 *
 * 24/10/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
import com.sysman.nomina.enums.ReclasificacionSiifControladorEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.model.StreamedContent;

/**
 * Reclasificación Siif
 *
 * @version 1.0, 24/10/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ReclasificacionSiifControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private int procesoNomina;
    private int anioNomina;
    private int mesNomina;
    private int periodoNomina;

    private int columna;
    private int fila;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos SeleccionarArchivo y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoSeleccionarArchivo;
    private StreamedContent archivoDescarga;
    private int consecutivo = 1;
    private Date fechaContable = new Date();
    private int tipoDocSoporte = 32;
    private String documentoSoporte;
    private String descripcion = "";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaTresRemote ejbNominaTres;

    /**
     * Crea una nueva instancia de ReclasificacionSiifControlador
     */
    public ReclasificacionSiifControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 1976;
            validarPermisos();
            contArchivoSeleccionarArchivo = new ContenedorArchivo();
            procesoNomina = Integer.parseInt(SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), 0).toString());
            anioNomina = Integer.parseInt(SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), 0).toString());
            mesNomina = Integer.parseInt(SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), 0).toString());
            periodoNomina = Integer.parseInt(SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), 0).toString());

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = "";
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la consulta correspondiente de la grilla del formulario, se hace la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     *
     *
     *
     */

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton BtIncapacidades en la vista
     *
     *
     *
     */
    public void oprimirBtIncapacidades()
    {
        archivoDescarga = null;
        generarPlano(1);

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtRetenciones en la vista
     *
     *
     *
     */
    public void oprimirBtRetenciones()
    {
        archivoDescarga = null;
        generarPlano(2);

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtBeneficios en la vista
     *
     *
     *
     */
    public void oprimirBtBeneficios()
    {
        archivoDescarga = null;
        generarPlano(3);
    }

    private boolean isNumeric2(String cadena)
    {
        return NumberUtils.isNumber(cadena);
    }

    private void generarPlano(int opcion)
    {
        if (contArchivoSeleccionarArchivo.getArchivo() == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            return;
        }

        String datos = "";
        try
        {
            String rutaArchivo = contArchivoSeleccionarArchivo.getArchivo()
                            .getPath();

            FileInputStream file;

            file = new FileInputStream(new File(rutaArchivo));

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            ByteArrayInputStream salidaPlano;
            file.close();

            Row row;

            for (int hoja = 1; hoja <= 2; hoja++)
            {
                // Hoja de excel.

                Cell nCell;
                if (hoja == 1)
                {
                    encabezadoPlanoExcel(workbook, 0);
                }

                HSSFSheet sheet = workbook.getSheetAt(hoja);
                if (opcion == 1)
                {
                    datos = ejbNominaTres.generarPlanoIncapacidadSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina,
                                    hoja,
                                    ReclasificacionSiifControladorEnum.EXCEL.getValue());
                }
                else if (opcion == 2)
                {
                    datos = ejbNominaTres.generarPlanoRetefuenteSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina,
                                    hoja,
                                    ReclasificacionSiifControladorEnum.EXCEL.getValue());
                }
                else if (opcion == 3)
                {
                    datos = ejbNominaTres.generarPlanoBeneficiosSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina,
                                    hoja,
                                    ReclasificacionSiifControladorEnum.EXCEL.getValue());
                }

                String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
                String[] colum;

                if ((registro.length == 0) || datos.isEmpty())
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3346"));
                    workbook.close();
                    return;
                }

                for (int i = 0; i < registro.length; i++)
                {
                    colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
                    fila = Integer.parseInt(
                                    colum[0]);
                    columna = Integer.parseInt(colum[1]);

                    row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
                    String valor = colum[2];

                    nCell = row.getCell(columna) == null ? row.createCell(columna)
                        : row.getCell(columna);

                    if (isNumeric2(valor))
                    {
                        double valorNum = Double.parseDouble(valor);
                        nCell.setCellValue(valorNum);
                        nCell.setCellType(0);
                    }
                    else
                    {
                        nCell.setCellValue(valor);
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();

            ByteArrayInputStream[] salida = new ByteArrayInputStream[4];
            String[] nombresInformes = new String[4];

            salida[0] = new ByteArrayInputStream(out.toByteArray());
            nombresInformes[0] = opcion == 1 ? "Reclasif NIT incapacidades.xls"
                : opcion == 2 ? "Retefuente_Rentas.xls" : "Reclasificación Beneficios a Corto Plazo.xls";

            StringBuilder datoEncabezado = new StringBuilder();
            datoEncabezado.append(consecutivo).append("|").append(SysmanFunciones.convertirAFechaCadena(fechaContable, "yyyy-MM-dd"))
                            .append("|")
                            .append(tipoDocSoporte).append("|").append(documentoSoporte).append("|").append(descripcion);
            salidaPlano = JsfUtil.serializarPlano(datoEncabezado.toString());
            salida[1] = salidaPlano;
            nombresInformes[1] = "Encabezado.Txt";

            if (opcion == 1)
            {
                datos = ejbNominaTres.generarPlanoIncapacidadSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 1,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
            }
            else if (opcion == 2)
            {
                datos = ejbNominaTres.generarPlanoRetefuenteSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 1,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
            }
            else if (opcion == 3)
            {
                datos = ejbNominaTres.generarPlanoBeneficiosSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 1,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
            }
            salidaPlano = JsfUtil.serializarPlano(
                            datos);
            salida[2] = salidaPlano;
            nombresInformes[2] = "Archivo1.Txt";

            String nombreArchivo = "";
            if (opcion == 1)
            {
                datos = ejbNominaTres.generarPlanoIncapacidadSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 2,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
                nombreArchivo = "Reclasificaciones-Incapacidades";
            }
            else if (opcion == 2)
            {
                datos = ejbNominaTres.generarPlanoRetefuenteSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 2,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
                nombreArchivo = "Reclasificaciones-Rentas";
            }
            else if (opcion == 3)
            {
                datos = ejbNominaTres.generarPlanoBeneficiosSiif(compania, procesoNomina, anioNomina, mesNomina, periodoNomina, 2,
                                ReclasificacionSiifControladorEnum.PLANO.getValue());
                nombreArchivo = "Reclasificaciones-B.E.C.P";
            }
            salidaPlano = JsfUtil.serializarPlano(
                            datos);
            salida[3] = salidaPlano;
            nombresInformes[3] = "Archivo2.Txt";

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salida,
                            nombresInformes,
                            nombreArchivo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage() + " En Fila:" + fila + " Columna" + columna, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void encabezadoPlanoExcel(HSSFWorkbook workbook, int hoja) throws SysmanException
    {
        try
        {
            HSSFSheet sheet = workbook.getSheetAt(hoja);
            Cell nCell;

            fila = 1;
            columna = 0;
            Row row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
            nCell = row.getCell(columna) == null ? row.createCell(columna)
                : row.getCell(columna);
            nCell.setCellValue(consecutivo);

            fila = 1;
            columna = 1;
            row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
            nCell = row.getCell(columna) == null ? row.createCell(columna)
                : row.getCell(columna);

            nCell.setCellValue(SysmanFunciones.convertirAFechaCadena(fechaContable, "yyyy-MM-dd"));

            fila = 1;
            columna = 2;
            row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
            nCell = row.getCell(columna) == null ? row.createCell(columna)
                : row.getCell(columna);
            nCell.setCellValue(tipoDocSoporte);

            fila = 1;
            columna = 3;
            row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
            nCell = row.getCell(columna) == null ? row.createCell(columna)
                : row.getCell(columna);
            nCell.setCellValue(documentoSoporte);

            fila = 1;
            columna = 4;
            row = sheet.getRow(fila) == null ? sheet.createRow(fila) : sheet.getRow(fila);
            nCell = row.getCell(columna) == null ? row.createCell(columna)
                : row.getCell(columna);
            nCell.setCellValue(descripcion);
        }
        catch (Exception e)
        {
            throw new SysmanException(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1976-AL_ABRIR Private Sub Form_Open(Cancel As Integer) 'formularioAbrir 1, Me.Name 'Me.Caption = NombreEmpresa(0) End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto contArchivoSeleccionarArchivo
     *
     * @return contArchivoSeleccionarArchivo
     */
    public ContenedorArchivo getContArchivoSeleccionarArchivo()
    {
        return contArchivoSeleccionarArchivo;
    }

    /**
     * Asigna el objeto contArchivoSeleccionarArchivo
     *
     * @param contArchivoSeleccionarArchivo
     * Variable a asignar en contArchivoSeleccionarArchivo
     */
    public void setContArchivoSeleccionarArchivo(ContenedorArchivo contArchivoSeleccionarArchivo)
    {
        this.contArchivoSeleccionarArchivo = contArchivoSeleccionarArchivo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public int getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(int consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public Date getFechaContable()
    {
        return fechaContable;
    }

    public void setFechaContable(Date fechaContable)
    {
        this.fechaContable = fechaContable;
    }

    public int getTipoDocSoporte()
    {
        return tipoDocSoporte;
    }

    public void setTipoDocSoporte(int tipoDocSoporte)
    {
        this.tipoDocSoporte = tipoDocSoporte;
    }

    public String getDocumentoSoporte()
    {
        return documentoSoporte;
    }

    public void setDocumentoSoporte(String documentoSoporte)
    {
        this.documentoSoporte = documentoSoporte;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public int getColumna()
    {
        return columna;
    }

    public void setColumna(int columna)
    {
        this.columna = columna;
    }

    public int getFila()
    {
        return fila;
    }

    public void setFila(int fila)
    {
        this.fila = fila;
    }

}
