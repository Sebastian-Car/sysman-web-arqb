package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.CambiosdenombreatercerosControladorEnum;
import com.sysman.contabilidad.enums.CambiosdenombreatercerosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CambiosdenitsControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 16/05/2016 09:01:23 -- Modificado por dsuesca
 *
 * @author eamaya
 * @version 2, 04/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y el cambio
 * del llamado del metodo getLlave por getLlaveServicio.
 */
@ManagedBean
@ViewScoped

public class CambiosdenombreatercerosControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String cRegistrado;
    private final String cNitNuevo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que valida si el boton de inconsistencias esta activo
     * o inactivo
     */
    private boolean activarInconsistencias = true;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNitAnterior;
    private RegistroDataModelImpl listaNitAnteriorE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaDcambiosdenombres;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    private String nombreAuxiliar;
    private String sucursalAuxiliar;
    private boolean allowAdditionsSub;
    private boolean allowDeletionsSub;
    private boolean allowEditsSub;
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    /**
     * Atributo que permite la lectura de los datos contenidos en el
     * archivo que se carga
     */
    private String extension;
    /**
     * Atributo que almacena los registros del archivo excel que no se
     * registraron en la tabla D_CAMBIOSNIT
     */
    private String cadena;
    /**
     * Atributo que almacena los registros del archivo excel que no se
     * registraron en la tabla D_CAMBIOSNIT
     */
    private String cadenaDos;
    /**
     * Atributo que se activara al ingresar al metodo de importarDatos
     */
    private boolean cadUno = false;
    /**
     * Atributo que se activara al ingresar al metodo de importarDatos
     */
    private boolean cadDos = false;

    @EJB
    private EjbSysmanUtilRemote ejbConsecutivo;

    // </DECLARAR_ADICIONALES>
    public CambiosdenombreatercerosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cRegistrado = "REGISTRADO";
        cNitNuevo = "NITNUEVO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOSDENOMBREATERCEROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(
                            CambiosdenombreatercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNitAnterior();
        cargarListaNitAnteriorE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaDcambiosdenombres();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaDcambiosdenombres = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.CAMBIOSDENIT;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
    }

    public void cargarListaDcambiosdenombres()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_CAMBIOSDENIT
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));

        try
        {
            listaDcambiosdenombres = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            CambiosdenitsControladorEnum.D_CAMBIOSDENIT
                                                            .getValue()));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNitAnterior()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiosdenombreatercerosControladorUrlEnum.URL5063
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNitAnterior = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");

    }

    public void cargarListaNitAnteriorE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiosdenombreatercerosControladorUrlEnum.URL5871
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNitAnteriorE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Numero
     *
     */
    public void cambiarNumero()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNitAnteriorC(int rowNum)
    {

        listaDcambiosdenombres.getDatasource().get(rowNum).getCampos().put(
                        "NITANTERIOR", auxiliar);
        listaDcambiosdenombres.getDatasource().get(rowNum).getCampos().put(
                        cNitNuevo, auxiliar);
        listaDcambiosdenombres.getDatasource().get(rowNum).getCampos().put(
                        "NOMBREANTERIOR", nombreAuxiliar);
        listaDcambiosdenombres.getDatasource().get(rowNum).getCampos().put(
                        "SUCURSALANTERIOR", sucursalAuxiliar);
        listaDcambiosdenombres.getDatasource().get(rowNum).getCampos().put(
                        "SUCURSALNUEVA", sucursalAuxiliar);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaNitAnterior(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("NITANTERIOR",
                        registroAux.getCampos().get("NIT"));
        registroSub.getCampos().put("NOMBREANTERIOR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registroSub.getCampos().put("SUCURSALANTERIOR",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registroSub.getCampos().put("SUCURSALNUEVA",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registroSub.getCampos().put(cNitNuevo,
                        registroAux.getCampos().get("NIT"));
        registroSub.getCampos().put(cNitNuevo,
                        registroAux.getCampos().get("NIT"));
    }

    public void seleccionarFilaNitAnteriorE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT") == null ? ""
            : registroAux.getCampos().get("NIT").toString();
        sucursalAuxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString();
        nombreAuxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirCambio()
    {
        int cambios = 0;
        // <CODIGO_DESARROLLADO>

        if (validarBotonCambio())
        {
            for (Registro regAux : listaDcambiosdenombres)
            {
                try
                {

                    regAux.getLlave().remove("KEY_NUMERO");

                    Map<String, Object> param = new TreeMap<>();
                    param.put("NOMBRENUEVO",
                                    regAux.getCampos().get("NOMBRENUEVO"));
                    param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                    new Date());
                    param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());

                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    CambiosdenombreatercerosControladorUrlEnum.URL8794
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), param,
                                    regAux.getLlave());

                    registro.getCampos().remove(
                                    GeneralParameterEnum.COMPANIA.getName());

                    registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED
                                    .getName(),
                                    new Date());
                    registro.getCampos().put(
                                    GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());

                    registro.getCampos().put(cRegistrado, -1);

                    UrlBean urlUpdate2 = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    GenericUrlEnum.CAMBIOSDENIT
                                                                    .getUpdateKey());
                    requestManager.update(urlUpdate2.getUrl(),
                                    urlUpdate2.getMetodo(),
                                    registro.getCampos(),
                                    registro.getLlave());

                    cambios++;
                }
                catch (SystemException e)
                {
                    Logger.getLogger(CambiosdenombreatercerosControlador.class
                                    .getName()).log(Level.SEVERE, null, e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            JsfUtil.agregarMensajeInformativoDialogo(idioma
                            .getString("TB_TB976")
                            .replace("#numero#", String.valueOf(cambios))
                            .replace("#campo#", idioma.getString("TB_TB3210")));
        }
        if (cambios > 0)
        {
            registro.getCampos().put(cRegistrado, true);
            allowAdditionsSub = allowDeletionsSub = allowEditsSub = false;
            agregarRegistroNuevo(false);

        }
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarBotonCambio()
    {
        if ((Boolean) registro.getCampos().get(cRegistrado))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB960"));
            return false;
        }

        if (listaDcambiosdenombres.isVacio())
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB961").replace("#numero#",
                                            registro.getCampos()
                                                            .get(GeneralParameterEnum.NUMERO
                                                                            .getName())
                                                            .toString()));
            return false;
        }

        return true;
    }

    public void oprimirComando16()
    {
        // <CODIGO_DESARROLLADO>
        guardarNuevo();
        agregarRegistroNuevo(false);
        cargarRegistro(rid, "m");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton inconsistencias en la
     * vista
     *
     *
     */
    public void oprimirinconsistencias()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try
        {
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            SysmanFunciones.concatenar(cadena, cadenaDos)), "Inconsistencias.txt");
        }
        catch (JRException | IOException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubDcambiosdenombres()
    {
        try
        {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CAMBIOSDENIT
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaDcambiosdenombres();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            CambiosdenombreatercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubDcambiosdenombres(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove("CREATED_BY");
            reg.getCampos().remove("DATE_CREATED");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiosdenombreatercerosControladorUrlEnum.URL13574
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            CambiosdenombreatercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaDcambiosdenombres();
        }
    }

    public void eliminarRegSubDcambiosdenombres(Registro reg)
    {
        try
        {
            reg.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CAMBIOSDENIT
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaDcambiosdenombres();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            CambiosdenombreatercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionDcambiosdenombres()
    {
        cargarListaDcambiosdenombres();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoExcel(FileUploadEvent event)
    {
        // <CODIGO_DESARROLLADO>
        workbook = null;
        try
        {
            InputStream is = event.getFile().getInputstream();
            if (is == null)
            {
                return;
            }
            String rutaArchivo = event.getFile().getFileName();
            extension = FilenameUtils.getExtension(rutaArchivo);
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (workbook == null)
            {
                if ("xls".equals(extension))
                {
                    workbook = new HSSFWorkbook(is);
                }
                else
                {
                    workbook = new XSSFWorkbook(is);
                }
            }

            importarDatos(workbook);
        }

        catch (IOException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     * 
     * @param workbook
     * Contenido del archivo seleccionado
     */
    private void importarDatos(Workbook workbook)
    {
        Sheet sheet = workbook.getSheetAt(0);
        HashMap<String, Object> campos = new HashMap<>();
        HashMap<String, Object> nombreCampos = new HashMap<>();
        cadUno = cadDos = true;

        cadena = idioma.getString("TB_TB4183");

        for (int i = 0; i < Math
                        .max(sheet.getRow(0)
                                        .getLastCellNum(), 0); i++)
        {
            Row r = sheet.getRow(0);
            nombreCampos.put(String.valueOf(i),
                            r.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                                            .getStringCellValue()
                                            .toUpperCase());
        }
        for (int rowNum = 1; rowNum <= sheet
                        .getLastRowNum(); rowNum++)
        {

            for (int column = 0; column < Math
                            .max(sheet.getRow(1)
                                            .getLastCellNum(),
                                            0); column++)
            {
                if (validarFilaCeldaVacia(sheet, rowNum, column))
                {
                    Row r = sheet.getRow(rowNum);
                    Cell cell = r.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String valor;
                    if (cell != null)
                    {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        valor = "";
                        if (validarCampos(campos, column))
                        {
                            valor = !SysmanFunciones.validarVariableVacio(
                                            cell.getStringCellValue())
                                                ? cell.getStringCellValue()
                                                : null;
                        }
                    }
                    else
                    {
                        valor = "";
                    }

                    campos.put(nombreCampos.get(String.valueOf(column))
                                    .toString(), valor);
                }
                else
                {
                    return;
                }

            }
            insertarDatos(campos);
            campos = new HashMap<>();

        }

        cargarListaDcambiosdenombres();
        activarInconsistencias = false;
    }

    /**
     * Valida si una celda especifica dentro de la hoja de datos viene
     * nula o esta en blanco
     * 
     * @param sheet
     * Hoja de datos que se va a analizar
     * @param rowNum
     * Numero de fila que se evaluara
     * @param column
     * Numero de la columna dentro de la fila que se evaluara
     * @return Verdadero si la celda posee valor
     */
    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column)
    {
        boolean respuesta = false;
        Row rAux = sheet.getRow(rowNum);
        if (rAux != null)
        {
            Cell cellAux = rAux.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cellAux != null)
            {
                cellAux.setCellType(Cell.CELL_TYPE_STRING);
                if (!SysmanFunciones.validarVariableVacio(
                                cellAux.getStringCellValue()))
                {
                    respuesta = true;

                }
            }
            else if (column == 2 || column == 5)
            {
                return true;
            }

        }
        return respuesta;
    }

    /**
     * Permite validar si un campo que viene vacio cuando se esta
     * importando no sea la primera columna
     * 
     * @param campos
     * estructura que almacena los campo que se van a insertar
     * @param column
     * numero de columna dentro de la fila que se esta leyebdo
     * @return Verdadero si la estructura que almacena los campos no
     * esta vacia
     */
    private boolean validarCampos(Map<String, Object> campos, int column)
    {
        return !campos.isEmpty() || column >= 0;
    }

    /**
     * Realiza el llamado al metodo de Insertar ubicado en la clase
     * acciones, para realizar el registro de la informacion que se
     * envia por parametro
     * 
     * @param archivo
     * nombre de la tabla en la que se realizara la insercion
     * @param campos
     * con sus repectivos valores a insertar
     */
    private void insertarDatos(HashMap<String, Object> campos)
    {
        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_CAMBIOSDENIT.getCreateKey());
        campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        campos.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO
                        .getName()));
        campos.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
        campos.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());

        if (!validarTerceroAnterior(campos.get(CambiosdenombreatercerosControladorEnum.NITANTERIOR.getValue()).toString(),
                        campos.get(CambiosdenombreatercerosControladorEnum.SUCURSALANTERIOR.getValue()).toString()))
        {

            if (cadUno)
            {
                cadena = SysmanFunciones.concatenar(cadena, "\r\n",
                                idioma.getString("TB_TB4184"));
            }
            cadUno = false;

            armarCadena(campos, "1");
        }
        else if (verificarTercero(campos.get(CambiosdenombreatercerosControladorEnum.NITANTERIOR.getValue()).toString(),
                        campos.get(CambiosdenombreatercerosControladorEnum.SUCURSALANTERIOR.getValue()).toString()))
        {
            if (cadDos)
            {
                cadenaDos = idioma.getString("TB_TB4185");
            }
            cadDos = false;
            armarCadena(campos, "2");
        }
        else
        {
            Parameter parameter = new Parameter();
            parameter.setFields(campos);
            try
            {
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                parameter);
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }

    }

    /**
     * Metodo que arma la cadena a generar en el archivo plano
     * 
     * @param campos
     */
    public void armarCadena(HashMap<String, Object> campos, String opcion)
    {
        String cadenaBase = SysmanFunciones.concatenar(opcion.equals("1") ? cadena : cadenaDos, "NIT ANTERIOR: ",
                        SysmanFunciones.padr(campos.get(CambiosdenombreatercerosControladorEnum.NITANTERIOR.getValue()).toString(), 18,
                                        " "),
                        "\t", "SUCURSAL ANTERIOR: ",
                        campos.get(CambiosdenombreatercerosControladorEnum.SUCURSALANTERIOR.getValue()).toString(), "\t",
                        "NOMBRE ANTERIOR: ", campos.get(CambiosdenombreatercerosControladorEnum.NOMBREANTERIOR.getValue()).toString(),
                        "\r\n", SysmanFunciones.concatenar("NIT NUEVO", SysmanFunciones.padl(": ", 5, " ")),
                        SysmanFunciones.padr(campos.get(CambiosdenombreatercerosControladorEnum.NITNUEVO.getValue()).toString(), 18,
                                        " "),
                        "\t",
                        SysmanFunciones.concatenar("SUCURSAL NUEVA", SysmanFunciones.padl(": ", 5, " ")),
                        campos.get(CambiosdenombreatercerosControladorEnum.SUCURSALNUEVA.getValue()).toString(),
                        "\t", SysmanFunciones.concatenar("NOMBRE NUEVO", SysmanFunciones.padl(": ", 5, " ")),
                        campos.get(CambiosdenombreatercerosControladorEnum.NOMBRENUEVO.getValue()).toString(), "\r\n");
        ;

        if ("1".equals(opcion))
        {
            cadena = cadenaBase;
        }
        else
        {
            cadenaDos = cadenaBase;
        }

    }

    /**
     * Metodo que valida si el nit anterior del excel cargado se
     * encuentra registrado en la base de datos en la tabla TERCERO
     * 
     * @param nit
     * @param sucursal
     */
    public boolean validarTerceroAnterior(String nit, String sucursal)
    {

        boolean respuesta = false;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CambiosdenombreatercerosControladorEnum.NIT.getValue(), nit);
        param.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        try
        {
            Registro rsTercero = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosdenombreatercerosControladorUrlEnum.URL15318
                                                                            .getValue())
                                            .getUrl(), param));

            if (!rsTercero.getCampos().get("EXISTE").toString().equals("0"))
            {
                respuesta = true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return respuesta;
    }

    /**
     * Metodo que valida si el nit anterior del excel cargado se
     * encuentra registrado en la base de datos en la tabla
     * D_CAMBIOSNIT
     * 
     * @param nit
     * @param sucursal
     */
    public boolean verificarTercero(String nit, String sucursal)
    {

        boolean respuesta = false;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
        param.put(CambiosdenombreatercerosControladorEnum.NIT.getValue(), nit);
        param.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        try
        {
            Registro rsExiste = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosdenombreatercerosControladorUrlEnum.URL854
                                                                            .getValue())
                                            .getUrl(), param));

            if (!rsExiste.getCampos().get("EXISTE").toString().equals("0"))
            {
                respuesta = true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return respuesta;
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css == null)
        {
            registro.getCampos().put("USUARIO",
                            SessionUtil.getUser().getCodigo());
            registro.getCampos().put("FECHA", new Date());
            allowAdditionsSub = allowDeletionsSub = allowEditsSub = false;
        }
        else
        {
            allowAdditionsSub = allowDeletionsSub = allowEditsSub = !(Boolean) registro
                            .getCampos().get(cRegistrado);
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try
        {
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            ejbConsecutivo.generarSiguienteConsecutivo(
                                            "CAMBIOSDENIT",
                                            "COMPANIA =" + compania,
                                            GeneralParameterEnum.NUMERO
                                                            .getName()));
        }
        catch (SystemException e)
        {
            Logger.getLogger(
                            CambiosdenombreatercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            registro.getCampos().get(GeneralParameterEnum.FECHA
                                            .getName()));
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable activarInconsistencias
     * 
     * @return activarInconsistencias
     */
    public boolean isActivarInconsistencias()
    {
        return activarInconsistencias;
    }

    /**
     * Asigna la variable activarInconsistencias
     * 
     * @param activarInconsistencias
     * Variable a asignar en activarInconsistencias
     */
    public void setActivarInconsistencias(boolean activarInconsistencias)
    {
        this.activarInconsistencias = activarInconsistencias;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaNitAnterior()
    {
        return listaNitAnterior;
    }

    public void setListaNitAnterior(RegistroDataModelImpl listaNitAnterior)
    {
        this.listaNitAnterior = listaNitAnterior;
    }

    public RegistroDataModelImpl getListaNitAnteriorE()
    {
        return listaNitAnteriorE;
    }

    public void setListaNitAnteriorE(RegistroDataModelImpl listaNitAnteriorE)
    {
        this.listaNitAnteriorE = listaNitAnteriorE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    public RegistroDataModelImpl getListaDcambiosdenombres()
    {
        return listaDcambiosdenombres;
    }

    public void setListaDcambiosdenombres(
        RegistroDataModelImpl listaDcambiosdenombres)
    {
        this.listaDcambiosdenombres = listaDcambiosdenombres;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public boolean isAllowAdditionsSub()
    {
        return allowAdditionsSub;
    }

    public void setAllowAdditionsSub(boolean allowAdditionsSub)
    {
        this.allowAdditionsSub = allowAdditionsSub;
    }

    public boolean isAllowDeletionsSub()
    {
        return allowDeletionsSub;
    }

    public void setAllowDeletionsSub(boolean allowDeletionsSub)
    {
        this.allowDeletionsSub = allowDeletionsSub;
    }

    public boolean isAllowEditsSub()
    {
        return allowEditsSub;
    }

    public void setAllowEditsSub(boolean allowEditsSub)
    {
        this.allowEditsSub = allowEditsSub;
    }

    // </SET_GET_ADICIONALES>
}
