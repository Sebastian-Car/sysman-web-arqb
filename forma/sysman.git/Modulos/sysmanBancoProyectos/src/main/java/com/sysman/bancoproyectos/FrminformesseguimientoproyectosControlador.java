/*-
 * FrminformesseguimientoproyectosControlador.java
 *
 * 1.0
 * 
 * 19/11/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.sysman.bancoproyectos.enums.FrmInfSeguimientosProyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrminformesProyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
//import com.sysman.presupuesto.enums.LisanualpacpagosControladorEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/11/2020
 * @author mavargas
 */
@ManagedBean
@ViewScoped
public class  FrminformesseguimientoproyectosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
private String OPCION;
private String proyectoInicial;
private String proyectoFinal;

private StreamedContent archivoDescarga;
 private RegistroDataModelImpl listaProyectoInicial;
 private RegistroDataModelImpl listaProyectoFinal;
 private static final String CODIGO = "CODIGO";
 private static final String NUMERO = "NUMERO";

 
    public FrminformesseguimientoproyectosControlador() {
  super();
            compania = SessionUtil.getCompania();
        try {
        	//2165
        	numFormulario = GeneralCodigoFormaEnum.FRMINFORMES_PROYECTOS_CONTROLADOR.getCodigo();
        	validarPermisos();
            }
            catch (Exception ex) {
                Logger.getLogger(FrminformesseguimientoproyectosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                SessionUtil.redireccionarMenuPermisos();
        }
    }
        
        
    @PostConstruct
    public void inicializar(){
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		 cargarListaProyectoInicial(); cargarListaProyectoFinal();
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

  
public void cargarListaProyectoInicial(){
    UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		frminformeseguimientoproyectosenumsControladorUrlEnum.URL9541
                                            .getValue());
	
	Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    
    listaProyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
            urlBean.getUrlConteo().getUrl(), param,
            true, CODIGO);
    
}

public void cargarListaProyectoFinal() {
    UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		frminformeseguimientoproyectosenumsControladorUrlEnum.URL10118
                                                    .getValue());
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.NUMERO.getName(), proyectoInicial);

    listaProyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, CODIGO);
}


public void oprimirpdf() throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
    //<CODIGO_DESARROLLADO>
    archivoDescarga=null;    
    generarInforme(FORMATOS.PDF);
   //</CODIGO_DESARROLLADO>
    }

public void oprimirExcel() throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
    //<CODIGO_DESARROLLADO>
    archivoDescarga=null; 
    generarInforme(FORMATOS.EXCEL97);
   //</CODIGO_DESARROLLADO>
}

public void cambiarOP70() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }


public void generarInforme(ReportesBean.FORMATOS formato) throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
	
	try
    {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("proyectoInicial",proyectoInicial);
        reemplazar.put("proyectoFinal",proyectoFinal);
        reemplazar.put("compania",compania);
        
        
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "002164SEGUIMIENTOPROYECTOS";
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_PROYECTOINICIAL", (proyectoInicial));
        parametros.put("PR_PROYECTOFINAL",(proyectoFinal));
        parametros.put("PR_CIUDADCOMPANIA",SessionUtil.getCompaniaIngreso().getCiudad());
       
        
        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                        ConectorPool.ESQUEMA_SYSMAN, formato);
    }
    catch (JRException | IOException ex)
    {
        JsfUtil.agregarMensajeError(
                        idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
        Logger.getLogger(FrminformesseguimientoproyectosControlador.class.getName())
                        .log(Level.SEVERE, null, ex);
    }

    
}


public void seleccionarFilaProyectoInicial(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	proyectoInicial = registroAux.getCampos()
                    .get(FrmInfSeguimientosProyectosControladorEnum.CODIGO
                                    .getValue()).toString();
	proyectoFinal = null;
	cargarListaProyectoFinal();
}



public void seleccionarFilaProyectoFinal(SelectEvent event) {

	Registro registroAux = (Registro) event.getObject();
	proyectoFinal = registroAux.getCampos()
                    .get(FrmInfSeguimientosProyectosControladorEnum.CODIGO
                                    .getValue()).toString();
}

public String getOPCION() {
        return OPCION;
    }
  
    public void setOPCION(String OPCION) {
        this.OPCION = OPCION;
    }
    
public String getProyectoInicial() {
        return proyectoInicial;
    }
    
    public void setProyectoInicial(String proyectoInicial) {
        this.proyectoInicial = proyectoInicial;
    }

public String getProyectoFinal() {
        return proyectoFinal;
    }

    public void setProyectoFinal(String proyectoFinal) {
        this.proyectoFinal = proyectoFinal;
    }
  
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaProyectoInicial() {
        return listaProyectoInicial;
    }
    
    public void setListaProyectoInicial(RegistroDataModelImpl listaProyectoInicial) {
        this.listaProyectoInicial = listaProyectoInicial;
    }
    
    public RegistroDataModelImpl getListaProyectoFinal() {
        return listaProyectoFinal;
    }
    
    public void setListaProyectoFinal(RegistroDataModelImpl listaProyectoFinal) {
        this.listaProyectoFinal = listaProyectoFinal;
    }
}
