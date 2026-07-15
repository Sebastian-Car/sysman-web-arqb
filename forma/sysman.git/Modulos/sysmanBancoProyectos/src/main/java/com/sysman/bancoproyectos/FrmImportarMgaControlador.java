/*-
 * FrmImportarMgaControlador.java
 *
 * 1.0
 * 
 * 01/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.FrmImportarMgaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Clase que permite subir un archivo xml de los proyectos registrados
 * en la MGA
 *
 * @version 1.0, 01/11/2017
 * @author jreina
 * 
 * @version 1.1, 13/10/2021
 * @author gfigueredo
 * Se aÃ¯Â¿Â½ade validaciÃ¯Â¿Â½n en la asiganciÃ¯Â¿Â½n de las variables "Proyecto", "Producto" y "Actividades",
 * para que reemplace el caracter "'" por un vacio.
 * @see #oprimirbtnSubir()
 */

@ManagedBean
@ViewScoped
public class FrmImportarMgaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private boolean ckBpim;
    private boolean ckCodigoBpim;
    private String dependencia;
    private String vigenciaInicial;
    private String vigenciaFinal;
    private String txtBpim;
    private String txtCodigoBpim;
    private String txtCodigoBpin;
    private ContenedorArchivo contArchivoSelectorArchivo;
    private InputStream archivo;
    private boolean consecutivoInd;
    private boolean visibleBpin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaVigenciaInicial;
    private List<Registro> listaVigenciaFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaDependencia;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    /* JM Ticket#7700208*/ 
    private HashSet<String> product_years = new HashSet<String>();
    private HashSet<String> activity_years = new HashSet<String>();
    /*Fin JM Ticket#7700208*/
    
    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmImportarMgaControlador
     */
    public FrmImportarMgaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_IMPORTAR_MGA_CONTROLADOR.getCodigo();
            contArchivoSelectorArchivo = new ContenedorArchivo();
            validarPermisos();
            consecutivoInd = false;
            // <INI_ADICIONAL>
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
        cargarListaVigenciaInicial();
        cargarListaVigenciaFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependencia();
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
        ckBpim = false;
        ckCodigoBpim = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigenciaInicial
     *
     */
    public void cargarListaVigenciaInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaVigenciaInicial = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmImportarMgaControladorUrlEnum.URL7704
                                            .getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaVigenciaFinal
     *
     */
    public void cargarListaVigenciaFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), vigenciaInicial);

            listaVigenciaFinal = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmImportarMgaControladorUrlEnum.URL9660
                                            .getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarMgaControladorUrlEnum.URL4576
                                                        .getValue());

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public boolean validarCampo() {

        if (ckBpim && (SysmanFunciones.validarVariableVacio(txtBpim) || txtBpim.length() < 8)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3771"));
            return false;
        }

        if (ckCodigoBpim && (SysmanFunciones.validarVariableVacio(txtCodigoBpim) || txtCodigoBpim.length() < 12)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4224"));
            return false;
        }
        return true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnSubir en la vista
     *
     */
    public void oprimirbtnSubir() {
        // <CODIGO_DESARROLLADO>
        if (contArchivoSelectorArchivo.getArchivo() == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1901"));
            return;
        }
        if (validarCampo()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory
                                .newInstance();
                DocumentBuilder docBuilder;
                docBuilder = docFactory.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(leerArchivo()));

                Document doc = docBuilder.parse(is);
                doc.getDocumentElement().normalize();
                StringBuilder proyectos = new StringBuilder();
                StringBuilder productos = new StringBuilder();
                StringBuilder actividades = new StringBuilder();
                NodeList nodeList = doc.getElementsByTagName("Project");
                proyectos.append("TO_CLOB('");
                productos.append("TO_CLOB('");
                actividades.append("TO_CLOB('");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    asignarValorTag(element, proyectos, "Id");
                    String id = consultarValorTag(element, "Id");
                    asignarValorTag(element, proyectos, "Name");
                    asignarValorTag(element, proyectos, "ParticipantsCoordination");
                    asignarValorTag(element, proyectos, "AffectedPeople");
                    asignarValorTag(element, proyectos, "ObjectivePeople");

                    consultarParteEspecifica(doc, proyectos, "GeneralObjective");

                    consultarParte("Product", doc, productos, id);

                    consultarParte("Activity", doc, actividades, id);

                    proyectos.delete(proyectos.length() - 7, proyectos.length());
                    proyectos.append(SysmanConstantes.SEPARADOR_REG);
                }
                proyectos.append("')");
                productos.append("')");
                actividades.append("')");

                //Inicio gfigueredo - ValidaciÃ¯Â¿Â½n ticket 7701257 Ã¯Â¿Â½ 1000104219 Banco de Proyecto Cajica
                String proyectosXml = proyectos.toString()/*.replace("'", "")*/;
                String productossXml = productos.toString()/*.replace("'", "")*/;
                String actividadesXml = actividades.toString()/*.replace("'", "")*/;
               //Inicio gfigueredo - ValidaciÃ¯Â¿Â½n ticket 7701257 Ã¯Â¿Â½ 1000104219 Banco de Proyecto Cajica
                if (ckBpim == true) {

                    ejbBancoProyectoCinco.importarXml(compania, proyectosXml,
                            productossXml, actividadesXml,
                                    ckBpim, txtBpim, dependencia,
                                    Integer.parseInt(vigenciaInicial),
                                    Integer.parseInt(vigenciaFinal), SessionUtil.getUser().getCodigo(),"");
                    JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3368"));
                }
                else if (!consecutivoInd) {
                    ejbBancoProyectoCinco.importarXml(compania, proyectosXml,
                            productossXml, actividadesXml,
                                    ckCodigoBpim, txtCodigoBpim, dependencia,
                                    Integer.parseInt(vigenciaInicial),
                                    Integer.parseInt(vigenciaFinal), SessionUtil.getUser().getCodigo(),"");
                    JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3368"));
                } else {
                	ejbBancoProyectoCinco.importarXml(compania, proyectosXml,
                            productossXml, actividadesXml,
                                    ckCodigoBpim, txtCodigoBpim, dependencia,
                                    Integer.parseInt(vigenciaInicial),
                                    Integer.parseInt(vigenciaFinal), SessionUtil.getUser().getCodigo(),txtCodigoBpin);
                    JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3368"));
                	
                }
            }
            catch (ParserConfigurationException | SAXException | IOException
                            | NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }



    public void consultarParte(String tag, Document doc, StringBuilder aux, String id) {
        NodeList nodeList = doc.getElementsByTagName(tag);
        int num = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            num = num +1;
            if ("Product".equals(tag)) {
                 /*JM Ticket#7700208*/
                 productPeriod(element, aux, doc,id);
                 /*Fin JM Ticket#7700208*/
            }
            else {
           
                /*JM Ticket#7700208*/
                 activityPeriod(element, aux,id); 
                 /*Fin JM Ticket#7700208*/
                
                
            }
            
            if (num >= 2) {
             	aux.append("') || TO_CLOB('");
             	num = 0;
             	}
             
        }
    }
    
    
    /*JM Ticket#7700208*/
    public void productPeriod(Element element, StringBuilder aux, Document doc, String id) {
        product_years.clear();
        NodeList nodeList = element.getElementsByTagName("Activity");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element activities = (Element) nodeList.item(i);
            if (activities != null) {
                //Actividades dentro del Producto
                NodeList nodeSubListInputs = activities.getElementsByTagName("Inputs");
                for (int m = 0; m < nodeSubListInputs.getLength(); m++) {
                    Element inputs = (Element) nodeSubListInputs.item(m);
                        //Iputs dentro de Actividades dentro del Producto
                        NodeList nodeSubListInputChilds = inputs.getElementsByTagName("Input");
                        for (int n = 0; n < nodeSubListInputChilds.getLength(); n++) {
                            Element input = (Element) nodeSubListInputChilds.item(n);
                            if (input != null) {
                                   NodeList nodeSubListPeriod = input.getElementsByTagName("Period");
                                   for (int j = 0; j < nodeSubListPeriod.getLength(); j++) {
                                    Element periodActivities = (Element) nodeSubListPeriod.item(0);
                                        if (periodActivities != null) {
                                            //Periodos de los inputs en las Actividades dentro del Producto
                                            NodeList nodoNamef = periodActivities.getChildNodes();
                                            product_years.add(nodoNamef.item(0).getNodeValue());   
                                        }
                                    }
                                }
                            }   
                        }
                    }
                }
      
        

        
       for(String period: product_years) {
           aux.append(id).append(SysmanConstantes.SEPARADOR_COL);
           asignarValorTag(element, aux, "Id");
           asignarValorTag(element, aux, "ProductName");
           asignarValorTag(element, aux, "Amount");
           asignarValorTag(element, aux, "SpecificObjectiveId");
           consultarParteEspecifica(doc, aux, "SpecificObjective", consultarValorTag(element, "SpecificObjectiveId"));
           asignarValorTag(element, aux, "ProductName");
           String periodValue;
           periodValue = Integer.toString((Integer.parseInt(vigenciaInicial) + Integer.parseInt(period)));
           aux.append(periodValue);
           aux.append(SysmanConstantes.SEPARADOR_COL);
           String sumPeriodAmmount = sumPeriodAmount(element,period);
           aux.append(sumPeriodAmmount);
           aux.append(SysmanConstantes.SEPARADOR_COL);
           aux.append(periodAmountPerUnit(element, sumPeriodAmmount));
           aux.append(SysmanConstantes.SEPARADOR_COL);
           
           aux.delete(aux.length() - 7, aux.length());
           aux.append(SysmanConstantes.SEPARADOR_REG);
           
       } 
        
    }
    
    
    public void activityPeriod(Element element, StringBuilder aux, String id) {
        activity_years.clear();
        NodeList nodeList = element.getElementsByTagName("Period");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element elementoName = (Element) nodeList.item(i);
            if (elementoName != null) {
                NodeList nodoNamef = elementoName.getChildNodes();
                activity_years.add(nodoNamef.item(0).getNodeValue());

            }
        }
        
        for(String period: activity_years) {
                 aux.append(id).append(SysmanConstantes.SEPARADOR_COL);
                 asignarValorTag(element, aux, "Id");
                 asignarValorTag(element, aux, "ProductId");
                 asignarValorTag(element, aux, "Name");
                 asignarValorTag(element, aux, "Cost");
                 
                 String periodValue;
                 periodValue = Integer.toString((Integer.parseInt(vigenciaInicial) + Integer.parseInt(period)));   
                 aux.append(periodValue);
                 aux.append(SysmanConstantes.SEPARADOR_COL);
                 aux.append(sumPeriodAmount(element,period));
                 aux.append(SysmanConstantes.SEPARADOR_COL);
                 
                 aux.delete(aux.length() - 7, aux.length());
                 aux.append(SysmanConstantes.SEPARADOR_REG);
        }
    }
    
    

    
    
    public String sumPeriodAmount(Element element, String year) {
        String sum = "0"; 
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00",otherSymbols);
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        NodeList nodeListPeriod = element.getElementsByTagName("Period");
        NodeList nodeListAmount = element.getElementsByTagName("WeightValue");
          for (int i = 0; i < nodeListPeriod.getLength(); i++) {
              Element activityPeriod = (Element) nodeListPeriod.item(i);
              Element activityAmount = (Element) nodeListAmount.item(i);
              if (activityPeriod != null && activityAmount != null) {
                  NodeList period = activityPeriod.getChildNodes();
                  NodeList amount = activityAmount.getChildNodes();
                  if(period.item(0).getNodeValue().equals(year))
                     sum =  df.format(Double.parseDouble(sum) + Double.parseDouble(amount.item(0).getNodeValue()));
                    }
  
              }
        
        return sum;
    }
    
    
    public String periodAmountPerUnit(Element element, String amount) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00",otherSymbols);
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        NodeList nodoName = element.getElementsByTagName("Amount");
        String qty = "1";
        Element elementoName = (Element) nodoName.item(0);
        if (elementoName != null) {
            NodeList nodoNamef = elementoName.getChildNodes();
            //aux.append(nodoNamef.item(0).getNodeValue());
            qty = nodoNamef.item(0).getNodeValue();
        }
        return df.format(Double.parseDouble(amount) / Double.parseDouble(qty));     
    }

    /* FIN JM Ticket#7700208*/
    
    public void consultarParteEspecifica(Document doc, StringBuilder aux, String tag) {
        NodeList nodeList = doc.getElementsByTagName(tag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            asignarValorTag(element, aux, tag);
        }
    }

    public void consultarParteEspecifica(Document doc, StringBuilder aux, String tag, Object objProducto) {
        NodeList nodeList = doc.getElementsByTagName(tag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (objProducto.equals(consultarValorTag(element, "Id"))) {
                asignarValorTag(element, aux, tag);
            }
        }
    }

    public void asignarValorTag(Element element, StringBuilder aux, String tag) {
        NodeList nodoName = element.getElementsByTagName(tag);
        Element elementoName = (Element) nodoName.item(0);
        if (elementoName != null) {
            NodeList nodoNamef = elementoName.getChildNodes();
            aux.append(nodoNamef.item(0).getNodeValue());
            aux.append(SysmanConstantes.SEPARADOR_COL);
        }
    }
    

    
  
    
    public String consultarValorTag(Element element, String tag) {
        NodeList nodoName = element.getElementsByTagName(tag);
        Element elementoName = (Element) nodoName.item(0);
        if (elementoName != null) {
            NodeList nodoNamef = elementoName.getChildNodes();
            return nodoNamef.item(0).getNodeValue();
        }
        else {
            return null;
        }
    }

    public String leerArchivo() {
        String rutaArchivo = contArchivoSelectorArchivo.getArchivo().getPath();
        StringBuilder textoArchivo = new StringBuilder("");
        try (FileInputStream file = new FileInputStream(new File(rutaArchivo))) {
            InputStreamReader r = new InputStreamReader(file, StandardCharsets.UTF_8);//
            BufferedReader br = new BufferedReader(r);
            String linea;
            while ((linea = br.readLine()) != null) {
                textoArchivo.append(linea);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return textoArchivo.toString().substring(1, textoArchivo.length());

    }

    public void cargarArchivoSelector(FileUploadEvent event) {
        try {
            archivo = event.getFile().getInputstream();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3767"));
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarVigenciaInicial() {
        cargarListaVigenciaFinal();
    }

    public void cambiarAsignarBPIM() {
        // METODO IMPLEMENTADO

        if (ckBpim = true) {
            txtCodigoBpim = "";
            txtCodigoBpin = "";

            ckCodigoBpim = false;
            visibleBpin = false;

        }

    }

    public void cambiarcodigoBPIM() {
        // <CODIGO_DESARROLLADO>
    	
    	try {
    	consecutivoInd = ("SI").equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA CONSECUTIVO PROYECTO INDEPENDIENTE DE CODIGO BPIN EN PROYECTOS",
						SessionUtil.getModulo(), new Date(), false), "NO"));
    	
        if (ckCodigoBpim = true && !consecutivoInd) {

            ckBpim = false;
            visibleBpin = false;
            txtBpim = "";

        } else {
        	
        	ckBpim = false;
        	visibleBpin = true;
        	ckCodigoBpim = true;
            txtBpim = "";
        }
        
    	} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);

		}

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos().get("CODIGO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckBpim
     * 
     * @return ckBpim
     */
    public boolean isCkBpim() {
        return ckBpim;
    }

    /**
     * Asigna la variable ckBpim
     * 
     * @param ckBpim
     * Variable a asignar en ckBpim
     */
    public void setCkBpim(boolean ckBpim) {
        this.ckBpim = ckBpim;
    }

    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * Retorna la variable vigenciaInicial
     * 
     * @return vigenciaInicial
     */
    public String getVigenciaInicial() {
        return vigenciaInicial;
    }

    /**
     * Asigna la variable vigenciaInicial
     * 
     * @param vigenciaInicial
     * Variable a asignar en vigenciaInicial
     */
    public void setVigenciaInicial(String vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    /**
     * Retorna la variable vigenciaFinal
     * 
     * @return vigenciaFinal
     */
    public String getVigenciaFinal() {
        return vigenciaFinal;
    }

    /**
     * Asigna la variable vigenciaFinal
     * 
     * @param vigenciaFinal
     * Variable a asignar en vigenciaFinal
     */
    public void setVigenciaFinal(String vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    /**
     * Retorna la variable txtBpim
     * 
     * @return txtBpim
     */
    public String getTxtBpim() {
        return txtBpim;
    }

    /**
     * Asigna la variable txtBpim
     * 
     * @param txtBpim
     * Variable a asignar en txtBpim
     */
    public void setTxtBpim(String txtBpim) {
        this.txtBpim = txtBpim;
    }

    /**
     * Retorna la variable txtCodigoBpim
     * 
     * @return txtCodigoBpim
     */
    public String getTxtCodigoBpim() {
        return txtCodigoBpim;
    }

    /**
     * Asigna la variable txtCodigoBpim
     * 
     * @param txtCodigoBpim
     * Variable a asignar en txtCodigoBpim
     */
    public void setTxtCodigoBpim(String txtCodigoBpim) {
        this.txtCodigoBpim = txtCodigoBpim;
    }
    
    /**
     * Retorna la variable txtCodigoBpin
     * 
     * @return txtCodigoBpin
     */
    public String getTxtCodigoBpin() {
        return txtCodigoBpin;
    }

    /**
     * Asigna la variable txtCodigoBpin
     * 
     * @param txtCodigoBpin
     * Variable a asignar en txtCodigoBpin
     */
    public void setTxtCodigoBpin(String txtCodigoBpin) {
        this.txtCodigoBpin = txtCodigoBpin;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigenciaInicial
     * 
     * @return listaVigenciaInicial
     */
    public List<Registro> getListaVigenciaInicial() {
        return listaVigenciaInicial;
    }

    /**
     * Asigna la lista listaVigenciaInicial
     * 
     * @param listaVigenciaInicial
     * Variable a asignar en listaVigenciaInicial
     */
    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    /**
     * Retorna la lista listaVigenciaFinal
     * 
     * @return listaVigenciaFinal
     */
    public List<Registro> getListaVigenciaFinal() {
        return listaVigenciaFinal;
    }

    /**
     * Asigna la lista listaVigenciaFinal
     * 
     * @param listaVigenciaFinal
     * Variable a asignar en listaVigenciaFinal
     */
    public void setListaVigenciaFinal(List<Registro> listaVigenciaFinal) {
        this.listaVigenciaFinal = listaVigenciaFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public ContenedorArchivo getContArchivoSelectorArchivo() {
        return contArchivoSelectorArchivo;
    }

    public void setContArchivoSelectorArchivo(
        ContenedorArchivo contArchivoSelectorArchivo) {
        this.contArchivoSelectorArchivo = contArchivoSelectorArchivo;
    }

    public InputStream getArchivo() {
        return archivo;
    }

    public void setArchivo(InputStream archivo) {
        this.archivo = archivo;
    }

    public boolean isCkCodigoBpim() {
        return ckCodigoBpim;
    }

    public void setCkCodigoBpim(boolean ckCodigoBpim) {
        this.ckCodigoBpim = ckCodigoBpim;
    }
    
    public boolean isVisibleBpin() {
        return visibleBpin;
    }

    public void setVisibleBpin(boolean visibleBpin) {
        this.visibleBpin = visibleBpin;
    }

}
