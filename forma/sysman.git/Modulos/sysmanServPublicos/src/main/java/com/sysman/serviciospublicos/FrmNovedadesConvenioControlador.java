/*-
 * FrmNovedadesConvenioControlador.java
 *
 * 1.0
 * 
 * 28/07/2017
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUtlRemote;
import com.sysman.serviciospublicos.enums.FrmNovedadesConvenioControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * Clase que permite cargar de un archivo excel las novedades de convenio,
 * de igual forma, la generacion de un excel con comentarios en los registros donde se presento
 * algun tipo de error. 
 *
 * @version 1.0, 28/07/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class  FrmNovedadesConvenioControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al ciclo en la forma
     * del formulario.
     */
    private String ciclo;
    /**
     * Atributo que contiene el valor asignado al año en la forma
     * del formulario.
     */
    private String anio;
    /**
     * Atributo que contiene el valor asignado al periodo en la forma
     * del formulario.
     */
    private String periodo;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selectorArchivo y funciona como contenedor del archivo que se
     * debe guardar
     */
    private ContenedorArchivo contArchivoselectorArchivo;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbServiciosPublicosUtlRemote ejbServiciosPublicosUtl;

    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCiclo;
    //</DECLARAR_LISTAS_COMBO_GRANDE>


    /**
     * Crea una nueva instancia de FrmNovedadesConvenioControlador
     */ 
    public FrmNovedadesConvenioControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario=GeneralCodigoFormaEnum.FRMNOVEDADESCONVENIO_CONTROLADOR.getCodigo();
            validarPermisos();
            contArchivoselectorArchivo = new ContenedorArchivo();
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
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
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
    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo(){
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmNovedadesConvenioControladorUrlEnum.URL4996.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,true,"NUMERO");
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAceptar
     * en la vista
     *
     */
    public void oprimirCmdAceptar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;
        if(contArchivoselectorArchivo.getArchivo()==null){
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3347"));
        }else{
            try (FileInputStream fileIs = new FileInputStream(
                            contArchivoselectorArchivo.getArchivo())) {  
               
                Workbook workbook = null;
                String rutaArchivo = contArchivoselectorArchivo.getArchivo().getPath();
                String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length());

                if (".xlsx".equals(extension)) {
                    workbook = new XSSFWorkbook(fileIs);
                }
                else {
                    workbook = new HSSFWorkbook(fileIs);  
                }
                Sheet sheet = workbook.getSheetAt(0);
                
                CreationHelper factory = workbook.getCreationHelper();
                Drawing drawing = sheet.createDrawingPatriarch();

                boolean aux = ejbServiciosPublicosUtl.obtenerCicloCalculado(
                                compania, Integer.parseInt(ciclo));
                
                if (!aux){
                    cargarArchivo(sheet, factory, drawing, workbook);
                }else{
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3489"));
                }
                workbook.close();
            }
            catch (NumberFormatException | SystemException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        //</CODIGO_DESARROLLADO>
    }
    
    public void cargarArchivo(Sheet sheet, CreationHelper factory, Drawing drawing, Workbook workbook){
        int contador = 0;
        String mensaje;
        boolean estado=true;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row   = sheet.getRow(i);
            Cell cell = row.getCell(0);
            if (cell != null && validarCelda(cell)) {
                mensaje=iniciarFuncion(row);
                contador++;
                if(mensaje!=null){
                    ClientAnchor anchor = factory.createClientAnchor();
                    anchor.setCol1(cell.getColumnIndex());
                    anchor.setCol2(cell.getColumnIndex()+5);
                    anchor.setRow1(row.getRowNum());
                    anchor.setRow2(row.getRowNum()+10);

                    Comment comment = drawing.createCellComment(anchor);
                    RichTextString str = factory.createRichTextString(mensaje);
                    comment.setString(str);
                    comment.setAuthor("SYSMAN");
                    
                    cell.setCellComment(comment);
                }
            }
            else {
                estado=false;
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3492"));
                break;
            }
        }
        if(estado){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                workbook.write(out);
                out.close();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO")
                                + " "
                                + idioma.getString("TB_TB2993").replace(
                                                "#contador#",
                                                Integer.toString(contador)));
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                "Novedades de Convenios.xls");
            }
            catch (IOException | JRException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public String iniciarFuncion(Row row) {
        try {
            ejbServiciosPublicosUtl.cargarNovedadesConvenio(compania,
                            recuperarValorCelda(3, row),
                            Integer.parseInt(ciclo),
                            Integer.parseInt(recuperarValorCelda(1, row)),
                            recuperarValorCelda(2, row),
                            recuperarValorCelda(0, row),
                            recuperarValorCelda(9, row),
                            recuperarValorCelda(4, row),
                            recuperarValorCelda(4, row),
                            recuperarValorCelda(6, row),
                            recuperarValorCelda(7, row),
                            recuperarValorCelda(8, row),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e);
            return obtenerMensaje(e.getMessage());
        }
        return null;
    }

    public String obtenerMensaje(String cadena){
        String [] aux=cadena.split("\n");

        StringBuilder mensaje= new StringBuilder();
        for (int i = 1; i < aux.length; i++) {
            if(aux[i].charAt(0)!='@' && aux[i].charAt(0)!='O'){
                mensaje.append(aux[i]).append("\n");
            }
        } 
        return mensaje.toString();
    }

    /**
     * Util para obtener el valor de una celda en una fila por su
     * posicion.
     * 
     * @param pos
     * Posicion de la celda.
     * @param row
     * Fila
     * @return El valor de la celda.
     */
    private String recuperarValorCelda(int pos, Row row) {
        Cell cell1 = row.getCell(pos);

        if (cell1 != null) {
            cell1.setCellType(Cell.CELL_TYPE_STRING);
            return cell1.getStringCellValue();
        }

        return "";
    }


    /**
     * Verifica el valor de la celda y retorna false si esta vacia.
     * 
     * @param celda
     * Objeto de tipo <code>Cell</code>
     * @return false si la celda esta vacia.
     */
    private boolean validarCelda(Cell celda) {
        celda.setCellType(Cell.CELL_TYPE_STRING);

        return !celda.getStringCellValue().isEmpty();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdCancelar
     * en la vista
     *
     */
    public void oprimirCmdCancelar() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo= SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "").toString();
        anio= SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "").toString();
        periodo= SysmanFunciones.nvl(registroAux.getCampos().get("PERIODO"), "").toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return  ciclo
     */
    public String getCiclo() {
        return ciclo;
    }
    /**
     * Asigna la variable  ciclo
     * 
     * @param  ciclo
     * Variable a asignar en  ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public String getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
    /**
     * Retorna la variable periodo
     * 
     * @return  periodo
     */
    public String getPeriodo() {
        return periodo;
    }
    /**
     * Asigna la variable  periodo
     * 
     * @param  periodo
     * Variable a asignar en  periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna el objeto contArchivoselectorArchivo
     * 
     * @return contArchivoselectorArchivo
     */
    public ContenedorArchivo getContArchivoselectorArchivo() {
        return contArchivoselectorArchivo;
    }
    /**
     * Asigna el objeto contArchivoselectorArchivo
     * 
     * @param contArchivoselectorArchivo
     * Variable a asignar en contArchivoselectorArchivo
     */
    public void setContArchivoselectorArchivo(ContenedorArchivo contArchivoselectorArchivo) {
        this.contArchivoselectorArchivo = contArchivoselectorArchivo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
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
     * Variable a asignar en  listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
