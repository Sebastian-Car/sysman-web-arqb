/*-
 * NovedadesPlanoAseoControlador.java
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
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.impl.EjbServiciosPublicosUtl;
import com.sysman.serviciospublicos.enums.NovedadesPlanoAseoControladorEnum;
import com.sysman.serviciospublicos.enums.NovedadesPlanoAseoControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
 * Esta clase es la encqargada de gestionar la importacion de datos
 * con respecto a una plantilla excel
 *
 * @version 1.0, 28/07/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class NovedadesPlanoAseoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temprolamente lo seleccionado
     * en el combo ciclo del formulario
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente el año del ciclo
     * seleccionado en el formulario
     */
    private String ano;
    /**
     * Variable encargada de almacenar temporalmente el periodo del
     * ciclo seleccionado en el formulario
     */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selector y funciona como contenedor del archivo que se
     * debe guardar
     */
    private ContenedorArchivo contArchivoselector;
    /**
     * Variable a nivel de clase encargada de confirmar o no la
     * insercion de datos con respecto a las validaciones
     */

    private boolean insertar;
    /**
     * Variable a nivel de clase encargada de almacenar el mensaje que
     * sera mostrado en el comentario del excel
     */
    private StringBuilder mensaje;
    /**
     * Variable a nivel de clase encargada de almacenar el registro
     * con los datos del excel para que se puedan hacer las
     * respectivas validacioens
     */
    private Registro regValidaciones;
    /**
     * Variable a nivel de clase encargada de almacenar el parametro
     * APLICA RESOLUCION CRA 720
     * 
     */
    private String parAseo720;
    /**
     * Variable a nivel de clase encargada de almacenar el parametro
     * FACTURACION EN SITIO
     */
    private String enSitio;
    /**
     * Variable a nivel de clase encargada de almacenar el parametro
     * CARGA UNIDADES NO RESIDENCIAL EN ASEO CONJUNTO
     */
    private String manejaNoResi;
    /**
     * Variable a nivel de clase encargada de almacenar el codigo Ruta
     * de los datos del excel por medio del registro regValidaciones.
     */
    private String strCodRuta;

    /**
     * Constante encargada de almacenar el String codigo.
     */
    private final String codigoCons;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar temporalmente la respuesta al
     * llamado a la base de datos.
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Ejb Encargado de invocar la funcion que se encarga de hacer el
     * llamdo a la base de datos para consultar los parametros.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosUtl ejbServPubUtl;

    /**
     * Variable encargada de lamacenar temporalmente el id de la
     * empreas.
     */

    private String idEmpresa;
    private final String periodoCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NovedadesPlanoAseoControlador
     */
    public NovedadesPlanoAseoControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = GeneralParameterEnum.CODIGO
                        .getName();
        periodoCons = GeneralParameterEnum.PERIODO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.NOVEDADESPLANOASEO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            contArchivoselector = new ContenedorArchivo();
            mensaje = new StringBuilder();

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
        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>

        // </CREAR_ARBOLES>
        abrirFormulario();

        insertar = true;

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
         * FR1411-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 79, Me.Name ChDrive "C" ChDir "C:\SYSMAN\"
         * End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar la respuesta en la listaCiclko
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadesPlanoAseoControladorUrlEnum.URL4775
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
     * Metodo ejecutado al oprimir el boton Aceptar en la vista.
     * Además, es el encargado de invocar el metodo para cargar los
     * datos
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            if (contArchivoselector.getArchivo() == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3347"));
                return;
            }

            boolean cicloCal;
            cicloCal = ejbServPubUtl.obtenerCicloCalculado(compania,
                            Integer.parseInt(ciclo));
            if (cicloCal) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3342"));
                return;
            }

            traerParametros();
            creacionWorkBook();

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = validarString(registroAux,
                        GeneralParameterEnum.NUMERO.getName());
        ano = validarString(registroAux, GeneralParameterEnum.ANO.getName());
        periodo = validarString(registroAux,
                        GeneralParameterEnum.PERIODO.getName());

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoselector
     * 
     * @return contArchivoselector
     */
    public ContenedorArchivo getContArchivoselector() {
        return contArchivoselector;
    }

    /**
     * Asigna el objeto contArchivoselector
     * 
     * @param contArchivoselector
     * Variable a asignar en contArchivoselector
     */
    public void setContArchivoselector(ContenedorArchivo contArchivoselector) {
        this.contArchivoselector = contArchivoselector;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    private String validarString(Registro reg, String campo) {

        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    /**
     * Metodo encargado de la logica principal de la carga de datos
     * del excel. Este metodo invoca los siguientes metodos.
     * 
     * obtenerRegistro, validarCodigo ,validarEStado
     * ,validarEnsitioFimm validarDatosEmpresa ,validarNovedad
     * ,insertarNovedad ,pintarFila, agregarComentario
     */

    private void cargarDatosExcel(Workbook workbook, Sheet sheet,
        String extension) {
        try {

            String[] campos = validarCampos720();

            CellStyle styleRed = workbook.createCellStyle();
            styleRed.setFillForegroundColor(HSSFColor.RED.index);
            styleRed.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            Map<String, Object> fila = new HashMap<>();
            // Recorre toda la fila y almacena los datos en un
            // HashMap.
            // Se obtiene el nombre del campo iterando el List CAMPO
            // teniendo encuenta la variable COLUMN del for.

            for (int rowNum = 1; rowNum <= (sheet.getLastRowNum()); rowNum++) {
                Row r = sheet.getRow(rowNum);

                insertar = true;

                // En caso del que el parametro esta en "SI". se deja
                // hasta la columna BS (ACCES). En el excel esta
                // columna equivale al numero 70

                // En caso de que el parametro esta en "NO" se deja
                // hasta la columna AT (ACCES). En el excel Esta
                // columna equivale al numero 45. Se dejan numero
                // estaticos ya que el excel se
                // esta recorriendo como una matriz y esta empieza
                // desde (0,0),
                int maximoColumn = "SI".equals(parAseo720)
                    ? 70 : 45;

                for (int column = 0; column <= maximoColumn; column++) {
                    if ((r != null)
                        && (r.getCell(column) != null)) {
                        // Se convierten a String todos los datos de
                        // la fila del String.

                        r.getCell(column)
                                        .setCellType(Cell.CELL_TYPE_STRING);

                        // Se obtiene el nombre del campo del []
                        // Campo y su respectivo valor del Excel
                        // teniendo encuenta la misma Columna.

                        fila.put(campos[column],
                                        r.getCell(column)
                                                        .getStringCellValue());

                    }

                }
                if (obtenerRegistro(fila) && (r != null)) {

                    validarCodigo(fila);
                    validarEStado();
                    validarEnsitioFimm();
                    validarDatosEmpresa(fila);
                    validarNovedad(fila);
                    insertarNovedad(fila);
                    pintarFila(r, 0, Math.max(r.getLastCellNum(),
                                    1), styleRed);

                    agregarComentario(r, workbook, sheet, mensaje.toString());

                }
                fila.clear();
                mensaje.delete(0, mensaje.length());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            SysmanFunciones.concatenar(
                                            idioma.getString("TB_TB3356"),
                                            extension));
        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo es el encargado de almacenar el registro teniendo
     * encuenta los datos del excel para las respectivas validacioens
     * 
     */
    private boolean obtenerRegistro(Map<String, Object> fila) {
        boolean rta = true;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), fila.get(codigoCons));

        try {
            regValidaciones = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NovedadesPlanoAseoControladorUrlEnum.URL4776
                                                                            .getValue())
                                            .getUrl(), param));
            if (regValidaciones == null) {
                mensaje.append(idioma.getString("TB_TB3348"));
                insertar = false;
                rta = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;

    }

    /**
     * Metodo encargado de la validacion del codigo que se enuentra en
     * el excel
     **/

    private void validarCodigo(Map<String, Object> fila) {

        int anoRegVal = Integer.parseInt(
                        regValidaciones.getCampos()
                                        .get(GeneralParameterEnum.ANO.getName())
                                        .toString());

        int anoFila = Integer.parseInt(fila
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        int cicloRegVal = Integer.parseInt(
                        regValidaciones.getCampos().get(
                                        GeneralParameterEnum.CICLO.getName())
                                        .toString());

        if ((!regValidaciones.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).equals(fila
                                        .get(periodoCons)))
            || (anoFila != anoRegVal)
            || (cicloRegVal != Integer.parseInt(ciclo))) {
            insertar = false;
            mensaje.append(idioma.getString("TB_TB3349"));

        }

    }

    /**
     * Metodo encargado de la validacion del Estado que se enuentra en
     * el excel
     **/

    private void validarEStado() {
        if ("R".equals(regValidaciones.getCampos()
                        .get(GeneralParameterEnum.ESTADO.getName())
                        .toString())) {
            mensaje.append(idioma.getString("TB_TB3350"));
            insertar = false;
        }
        else {
            strCodRuta = regValidaciones.getCampos()
                            .get(GeneralParameterEnum.CODIGORUTA.getName())
                            .toString();
        }
    }

    /**
     * Metodo encargado de la validacion de la Fimm que se enuentra en
     * el excel
     **/
    private void validarEnsitioFimm() {
        if ("SI".equals(enSitio)
            && "P".equals(regValidaciones.getCampos()
                            .get(NovedadesPlanoAseoControladorEnum.PARAM0
                                            .getValue()))) {
            mensaje.append(idioma.getString("TB_TB3351"));
            insertar = false;
        }
    }

    /**
     * Metodo encargado de la validacion de los datos de la empresa
     * que se enuentra en el excel
     **/
    private void validarDatosEmpresa(Map<String, Object> fila) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(NovedadesPlanoAseoControladorEnum.PARAM2.getValue(), fila.get(
                        NovedadesPlanoAseoControladorEnum.PARAM2.getValue()));

        try {
            Registro empresa = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NovedadesPlanoAseoControladorUrlEnum.URL4777
                                                                            .getValue())
                                            .getUrl(), param));
            if (empresa == null) {
                mensaje.append(idioma.getString("TB_TB3352"));
                insertar = false;
            }
            else {
                idEmpresa = empresa.getCampos()
                                .get(NovedadesPlanoAseoControladorEnum.PARAM1
                                                .getValue())
                                .toString();
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo encargado de que la novedad no se encuntre en la base de
     * datos.
     **/

    private void validarNovedad(Map<String, Object> fila) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), strCodRuta);
        param.put(GeneralParameterEnum.ANO.getName(),
                        fila.get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        fila.get(GeneralParameterEnum.PERIODO.getName()));
        param.put(NovedadesPlanoAseoControladorEnum.PARAM2.getValue(),
                        idEmpresa);

        try {
            Registro novedad = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NovedadesPlanoAseoControladorUrlEnum.URL4778
                                                                            .getValue())
                                            .getUrl(), param));
            if (novedad != null) {
                mensaje.append(idioma.getString("TB_TB3353"));
                insertar = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo encargado de insertar la novedad
     **/
    private void insertarNovedad(Map<String, Object> fila) {
        if (insertar) {
            UrlBean urlCreate;
            if ("SI".equals(parAseo720)) {
                urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                NovedadesPlanoAseoControladorUrlEnum.URL4780
                                                                .getValue());

            }
            else {

                urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                NovedadesPlanoAseoControladorUrlEnum.URL4779
                                                                .getValue());
            }

            fila.put(NovedadesPlanoAseoControladorEnum.PARAM4.getValue(),
                            idEmpresa);
            fila.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fila.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            fila.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fila.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            fila.put(GeneralParameterEnum.CODIGORUTA.getName(), strCodRuta);

            fila.remove(NovedadesPlanoAseoControladorEnum.PARAM2.getValue());
            fila.remove(NovedadesPlanoAseoControladorEnum.PARAM3.getValue());

            if (!"SI".equals(manejaNoResi)) {

                fila.put(NovedadesPlanoAseoControladorEnum.PARAM5.getValue(),
                                "0");
            }

            Parameter parameter = new Parameter();
            parameter.setFields(fila);
            try {
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                parameter);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Metodo encargado de traer los parametros de la base de datos
     * teniendo encuenta el Ejb de sysmanUtil
     **/
    private void traerParametros() {
        try {
            parAseo720 = ejbSysmanUtil.consultarParametro(compania,
                            NovedadesPlanoAseoControladorEnum.PARAM6.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

            enSitio = ejbSysmanUtil.consultarParametro(compania,
                            NovedadesPlanoAseoControladorEnum.PARAM7.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);
            manejaNoResi = ejbSysmanUtil.consultarParametro(compania,
                            NovedadesPlanoAseoControladorEnum.PARAM8.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo encargado de pintar la fila de rojo si alguna de las
     * validacioes retorna false la variable insertar
     **/

    public void pintarFila(Row row, int startColumn, int endColumn,
        CellStyle style) {
        if (!insertar) {
            for (int column = startColumn; column < endColumn; column++) {
                if (row.getCell(column) != null) {
                    row.getCell(column).setCellStyle(style);

                }
                else {
                    Cell cell = row.createCell(column);
                    cell.setCellStyle(style);
                }
            }
        }
    }

    /**
     * Metodo encargado de agregar el mensaje al mcomentario si alguna
     * de las validacioes retorna false la variable insertar
     **/

    private void agregarComentario(Row row, Workbook workbook, Sheet sheet,
        String mensaje) {
        if (!insertar) {
            CreationHelper factory = workbook.getCreationHelper();

            Drawing drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(row.getCell(5).getColumnIndex());
            anchor.setCol2(row.getCell(5).getColumnIndex() + 2);
            anchor.setRow1(row.getCell(5).getRowIndex());
            anchor.setRow2(row.getCell(5).getRowIndex() + 4);
            // Create the comment and set the text+author

            Comment comment = drawing.createCellComment(anchor);
            //se agrega codigo para implentacion de marca blanca
            String marcaBlanca = idioma.getString("TB_TB3354");
            marcaBlanca = marcaBlanca.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
            RichTextString str = factory
                            .createRichTextString(SysmanFunciones.concatenar(
                                            marcaBlanca,
                                            mensaje));
            comment.setString(str);
            comment.setAuthor(SessionUtil.getUser().getCodigo());
            comment.setVisible(true);

            // Assign the comment to the cell
            row.getCell(3).setCellComment(comment);
        }
    }

    /**
     * Metodo encargado de validar los datos a insertar teniendo
     * encuenta el parametro APLICA RESOLUCION CRA 720
     **/
    private String[] validarCampos720() {

        String[] campos = { "ANO", periodoCons, codigoCons,
                            NovedadesPlanoAseoControladorEnum.PARAM2.getValue(),
                            NovedadesPlanoAseoControladorEnum.PARAM3.getValue(),
                            "FECHA_INICIO", "FECHA_FIN", "FECHA_GENERA",
                            "TARIFA", "ESTRATO", "CATEGORIA", "VALORDEU",
                            "MESESMORA", "INTMORA", "FRECUENCIA_ASEO",
                            "FRECUENCIA_BARRIDO", "VALORASEO_SINSUBOSOBRE",
                            "SUBOSOBRE", "SUBOSOBRE_DES", "VALORASEO",
                            "HISTORICO1", "HISTORICO2", "HISTORICO3",
                            "HISTORICO4", "HISTORICO5", "HISTORICO6",
                            "VALOR_FINAN", "CUOTA_FINAN", "UNIDADES_RESID",
                            "TDI_ASEO", "BARRIDO_TBL", "RECOLECCION_TRT",
                            "TRAMOEXCE_TTE", "DISPOFINAL_TDT",
                            "MANEJOREC_TFR",
                            "REF01", "TEX01", "REF02", "TEX02", "REF03",
                            "TEX03", "REF04",
                            "TEX04", "REF05", "TEX05", "VALOR_COB" };

        String[] campos720 = { "ANO", periodoCons, codigoCons,
                               NovedadesPlanoAseoControladorEnum.PARAM2
                                               .getValue(),
                               NovedadesPlanoAseoControladorEnum.PARAM3
                                               .getValue(),
                               "FECHA_INICIO", "FECHA_FIN", "FECHA_GENERA",
                               "TARIFA", "ESTRATO", "CATEGORIA", "VALORDEU",
                               "MESESMORA", "INTMORA", "FRECUENCIA_ASEO",
                               "FRECUENCIA_BARRIDO", "VALORASEO_SINSUBOSOBRE",
                               "SUBOSOBRE", "SUBOSOBRE_DES", "VALORASEO",
                               "HISTORICO1", "HISTORICO2", "HISTORICO3",
                               "HISTORICO4", "HISTORICO5", "HISTORICO6",
                               "VALOR_FINAN", "CUOTA_FINAN", "UNIDADES_RESID",
                               "TDI_ASEO", "BARRIDO_TBL", "RECOLECCION_TRT",
                               "TRAMOEXCE_TTE", "DISPOFINAL_TDT",
                               "MANEJOREC_TFR",
                               "REF01", "TEX01", "REF02", "TEX02", "REF03",
                               "TEX03", "REF04",
                               "TEX04", "REF05", "TEX05", "VALOR_COB",
                               "COMISION", "FACTURA", "FECHA_PAGO",
                               "CCS_720",
                               "CBLS_720", "CLUS_720", "CRT_720",
                               "CDF_720", "CTL_720", "VBA_720", "COSTO_CCS_720",
                               "COSTO_CBLS_720", "COSTO_CLUS_720",
                               "COSTO_CRT_720", "COSTO_CDF_720",
                               "COSTO_CTL_720", "COSTO_TRBL_720",
                               "COSTO_TRLU_720", "COSTO_TRNA_720",
                               "COSTO_TRRA_720", "COSTO_VBA_720",
                               "COSTO_TRA_720", "COSTO_FCS_720",
                               "COSTO_FCS1_720", "COSTO_FIJO", "COSTO_VARIABLE",
                               "COSTO_TAFNA_720", "PORC_INTERES" };

        if ("SI".equals(parAseo720)) {
            return campos720;
        }
        else {
            return campos;
        }

    }

    /*
     * Metodo encargado de validar el nombre de la hoja, este nombre
     * tiene que corresponder al ciclo ano y peridoo concatenados
     */
    private Sheet validarNombreHoja(Workbook workbook) {
        if (workbook.getSheet(SysmanFunciones.concatenar(ciclo, ano,
                        periodo)) == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3357"));
            return null;

        }
        else {
            return workbook.getSheet(
                            SysmanFunciones.concatenar(ciclo, ano, periodo));
        }

    }

    private void creacionWorkBook() {

        String rutaArchivo = contArchivoselector.getArchivo()
                        .getPath();

        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());

        if (extension == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2761"));
        }

        try (FileInputStream file = new FileInputStream(rutaArchivo)) {
            Workbook workbook;

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(file);

            }
            else if (".xls".equals(extension)) {

                workbook = new HSSFWorkbook(file);

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3359"));
                file.close();
                return;
            }
            file.close();

            // Obtiene la hoja 0 del excel
            Sheet sheet = validarNombreHoja(workbook);
            if (sheet == null) {
                return;
            }

            cargarDatosExcel(workbook, sheet, extension);

        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}