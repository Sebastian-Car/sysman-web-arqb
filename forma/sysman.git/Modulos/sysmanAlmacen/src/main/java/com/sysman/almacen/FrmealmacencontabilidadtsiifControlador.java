/*-
 * FrmealmacencontabilidadtsiifControlador.java
 *
 * 1.0
 * 
 * 21/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.enums.FrmealmacencontabilidadtsiifControladorUrlEnum;
import com.sysman.almacen.enums.FrmealmacensiifControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/10/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class  FrmealmacencontabilidadtsiifControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Almacena la fecha
     */
    private Date fecha;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmealmacencontabilidadtsiifControlador
     */
    public FrmealmacencontabilidadtsiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario=2115;
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * en la vista
     *
     */
    public void oprimirAceptar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;   

        String[] informes = {"002053AlmacenContabilidadSIIF","002054AlmacenContabilidadTSIIF2","002055AlmacenContabilidadTSIIF3"};
        String[] consultas = new String[3];

        ByteArrayInputStream[] salidaComprimido = new ByteArrayInputStream[4];
        String[] nombres = {"Informe SIIF diario.xlsx", "CABECERA.TXT", "ARCHIVO 1.TXT", "ARCHIVO 2.TXT"};

        Map<String, Object> reemplazos = new TreeMap<>();


        try {

            parametrosEntrada = SessionUtil.getFlash();


            reemplazos.put("consecutivo", parametrosEntrada.get("consecutivo"));
            reemplazos.put("fechaContable", SysmanFunciones.convertirAFechaCadena( (Date) parametrosEntrada.get("fechaContable")) );
            reemplazos.put("tipoDocumento", parametrosEntrada.get("tipoDocumento"));
            reemplazos.put("numeroDocumento", parametrosEntrada.get("numeroDocumento"));
            reemplazos.put("descripcion", parametrosEntrada.get("descripcion"));
            reemplazos.put("fechaAlmacenContabilidadTSiif", SysmanFunciones.convertirAFechaCadena(fecha));
            reemplazos.put("compania", compania);
            reemplazos.put("identificacionAuxiliar", SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania, "PCI DE CONEXION SIIF", SessionUtil.getModulo(),new Date(), true), ""));
            
            for (int i = 0; i < informes.length; i++) {

                consultas[i] =  Reporteador.resuelveConsulta(informes[i],
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos);
            }


            String[] hojas = {"CABECERA", "ARCHIVO 1", "ARCHIVO 2"};

            salidaComprimido[0] = new ByteArrayInputStream(IOUtils.toByteArray(JsfUtil.exportarHojaDatosStreamed(consultas, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL, hojas).getStream()));


            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.CONSECUTIVO.getName(), parametrosEntrada.get("consecutivo"));
            param.put(GeneralParameterEnum.FECHA_CONTABLE.getName(),SysmanFunciones.convertirAFechaCadena( (Date) parametrosEntrada.get("fechaContable")) );
            param.put(GeneralParameterEnum.TIPO_DOCUMENTO.getName(), parametrosEntrada.get("tipoDocumento"));
            param.put(GeneralParameterEnum.NUMERO_DOCUMENTO.getName(), parametrosEntrada.get("numeroDocumento"));
            param.put(GeneralParameterEnum.DESCRIPCION.getName(), parametrosEntrada.get("descripcion"));
            param.put(GeneralParameterEnum.FECHAALMACENCONTABILIDADTSIIF.getName(), SysmanFunciones.convertirAFechaCadena(fecha));
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.IDENTIFICACION_AUXILIAR.getName(), ejbSysmanUtilRemote.consultarParametro(compania, "PCI DE CONEXION SIIF", SessionUtil.getModulo(),new Date(), true));

            List<Registro> archivoUno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmealmacensiifControladorUrlEnum.URL5050.getValue())
                                            .getUrl(),
                                            param));

            String[] columnasUno = {GeneralParameterEnum.CONSECUTIVO.getName(),
                                    GeneralParameterEnum.FECHA_CONTABLE.getName(),
                                    GeneralParameterEnum.TIPO_DOCUMENTO.getName(),
                                    GeneralParameterEnum.NUMERO_DOCUMENTO.getName(),
                                    GeneralParameterEnum.DESCRIPCION.getName()};

            String texto = generarCadenaArchivoPlano(columnasUno, archivoUno );
            salidaComprimido[1] = JsfUtil.serializarPlano(texto);

            List<Registro> ArchivoDos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmealmacencontabilidadtsiifControladorUrlEnum.URL6968.getValue())
                                            .getUrl(),
                                            param));

            String[] columnasDos = {GeneralParameterEnum.CONSECUTIVO.getName(),
                                    GeneralParameterEnum.CONSECUTIVO_O.getName(),
                                    GeneralParameterEnum.CODIGO_CONTABLE.getName(),
                                    GeneralParameterEnum.VALOR_DEBE.getName(),
                                    GeneralParameterEnum.VALOR_HABER.getName()};

            texto = generarCadenaArchivoPlano(columnasDos, ArchivoDos );
            salidaComprimido[2] = JsfUtil.serializarPlano(texto);

            List<Registro> ArchivoTres = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmealmacencontabilidadtsiifControladorUrlEnum.URL6969.getValue())
                                            .getUrl(),
                                            param));

            String[] columnasTres = {GeneralParameterEnum.CONSECUTIVO_O.getName(),
                                     GeneralParameterEnum.TIPO_AUXILIAR.getName(),
                                     GeneralParameterEnum.AUXILIAR_CONTABLE.getName(),
                                     GeneralParameterEnum.IDENTIFICACION_AUXILIAR.getName(),
                                     GeneralParameterEnum.VALOR_AUXILIAR.getName()};
            
            texto = generarCadenaArchivoPlano(columnasTres, ArchivoTres );
            salidaComprimido[3] = JsfUtil.serializarPlano(texto);

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidaComprimido, nombres, "Informe SIIF diario");
        }
        catch (JRException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (DRException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        catch (SystemException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }




        //</CODIGO_DESARROLLADO>
    }

    /**
     * Genera el texto plano que compone un archivo separado por |
     * @param columnas Nombre de las columnas de la consulta
     * @param registros Lista de registros resultantes de una consulta
     * @return
     */
    private String generarCadenaArchivoPlano(String[] columnas, List<Registro> registros ) {
        String texto ="";

        for (Registro valor : registros) {

            for (int i = 0; i < columnas.length; i++) {

                texto=SysmanFunciones.concatenar(texto,SysmanFunciones.nvl(valor.getCampos().get(columnas[i]),"").toString());

                if(i+1<columnas.length)
                    texto=SysmanFunciones.concatenar(texto,"|");
                else
                    texto=SysmanFunciones.concatenar(texto,"\n");
            }
        }
        return texto;
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
     * Retorna la variable fecha
     * 
     * @return  fecha
     */
    public Date getFecha() {
        return fecha;
    }
    /**
     * Asigna la variable  fecha
     * 
     * @param  fecha
     * Variable a asignar en  fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
