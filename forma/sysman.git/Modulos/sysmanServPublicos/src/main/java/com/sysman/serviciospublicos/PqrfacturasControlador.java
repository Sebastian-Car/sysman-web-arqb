/*-
 * PqrfacturasControlador.java
 *
 * 1.0
 *
 * 20/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.PqrfacturasControladorEnum;
import com.sysman.serviciospublicos.enums.PqrfacturasControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Clase que contiene la migracion de la pestana PQR (Peticiones
 * Quejas y Reclamos) del formulario Factura. El formulario contiene
 * dos subformularios: SeguimientoSubFactura y
 * UsuarioProblemaConsulta.
 *
 * @version 1.0, 20/09/2016
 * @author acaceresS
 * 
 * @version 2.0 14/06/2017
 * @author asana
 * Se realiza refactoring, se modifican en 
 */
@ManagedBean
@ViewScoped
public class PqrfacturasControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Almacenara el codigo de la compania con la que se va a trabajar
     */
    private final String compania;

    /**
     * Almacenara el numero del modulo con el que se esta trabajando
     */
    private final String modulo;

    
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que almacena los datos de la columna Descripcion del
     * problema en el subformulario UsuarioProblemaConsulta (Problemas
     * de aforo)
     */
    private List<Registro> listaProblemaC;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista que almacenara los datos del subformulario
     * SeguimientosubFactura
     */
    private List<Registro> listaSeguimientosubfactura;

    /**
     * Lista que almacenara los datos del subformulario
     * UsuarioProblemaConsulta
     */
    private List<Registro> listaUsuarioproblemaconsulta;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    /**
     * Almacenara el ciclo que se trae por parametro desde el
     * formulario Factura.
     */
    private String ciclo;

    /**
     * Almacenara el codigo de la ruta que se trae por parametro desde
     * el formulario Factura.
     */
    private String codigoRuta;

    /**
     * Almacenara el ano que se trae por parametro desde el formulario
     * Factura.
     */
    private String ano;

    /**
     * Almacenara el periodo que se trae por parametro desde el
     * formulario Factura.
     */
    private String periodo;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public PqrfacturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.PQRFACTURAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                /* Parametros que se traen del formulario Factura */
                ciclo = (String) parametrosEntrada.get("ciclo");
                codigoRuta = (String) parametrosEntrada.get("codigoRuta");
                ano = parametrosEntrada.get("ano").toString();
                periodo = (String) parametrosEntrada.get("periodo");

            }

            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSeguimientosubfactura();
        cargarListaUsuarioproblemaconsulta();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSeguimientosubfactura = null;
        listaUsuarioproblemaconsulta = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_USUARIO;
        buscarLlave();
        iniciarListasSub();
        iniciarListas();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    /**
     * Metodo usado para cargar la lista del subformulario
     * SeguimientoSubFactura.
     */
    public void cargarListaSeguimientosubfactura() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

        try {
            listaSeguimientosubfactura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PqrfacturasControladorUrlEnum.URL21585
                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            PqrfacturasControladorEnum.TABLASEGUIMIENTOPQR
                                            .getValue()));
        }
        catch (SystemException | SysmanException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo usado para cargar la lista del subformulario
     * UsuarioProblemaConsulta
     */
    public void cargarListaUsuarioproblemaconsulta() {
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania, "MANEJA HISTORICOS DE PROBLEMAS AFORO", modulo, new Date(), false);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            String url;

            if ("NO".equals(parametro)) {

               url  = PqrfacturasControladorUrlEnum.URL18410.getValue();
            } else {

                url = PqrfacturasControladorUrlEnum.URL22556.getValue();
            }
            
            listaUsuarioproblemaconsulta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            url)
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            PqrfacturasControladorEnum.TABLASEGUIMIENTOPQR
                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

// <METODOS_CARGAR_LISTA>
// </METODOS_CARGAR_LISTA>
// <METODOS_CAMBIAR>
// </METODOS_CAMBIAR>
// <METODOS_COMBOS_GRANDES>
// </METODOS_COMBOS_GRANDES>
// <METODOS_BOTONES>
// </METODOS_BOTONES>
// <METODOS_SUBFORM>

public void onCancelSeguimientosubfactura() {
    cargarListaSeguimientosubfactura();
    cargarListaUsuarioproblemaconsulta();
}


public void onCancelUsuarioproblemaconsulta() {
    cargarListaUsuarioproblemaconsulta();
}

/**
 * Metodo usado para asegurar que al cerrar el formulario, retorne
 * al formulario facturaintegrado.
 */
public void ejecutarrcCerrar() {
    // <CODIGO_DESARROLLADO>
    
    HashMap<String, Object> param = new HashMap<>(); 
    param.put("rid", rid);
    Direccionador direccionar = new Direccionador();
    direccionar.setNumForm(Integer.toString(GeneralCodigoFormaEnum.FACTURAINTEGRADOS_CONTROLADOR.getCodigo()));
    direccionar.setParametros(param);
   }

// </METODOS_SUBFORM>
// <METODOS_ADICIONALES>
// </METODOS_ADICIONALES>
@Override
public void abrirFormulario() {
    // <CODIGO_DESARROLLADO>

    // </CODIGO_DESARROLLADO>
}


@Override
public void cargarRegistro() {
    // <CODIGO_DESARROLLADO>
    precargarRegistro();
    // </CODIGO_DESARROLLADO>
}

@Override
public boolean insertarAntes() {
    // <CODIGO_DESARROLLADO>
    // </CODIGO_DESARROLLADO>
    return true;
}

@Override
public boolean insertarDespues() {
    // <CODIGO_DESARROLLADO>
    /*
     * FR1110-DESPUES_INSERTAR Private Sub Form_AfterInsert()
     * AuditarModif Me, 1, Me!Compania & "^" & Me!Ciclo & "^" &
     * Me!CodigoRuta End Sub
     */
    // </CODIGO_DESARROLLADO>
    return true;
}

@Override
public boolean actualizarAntes() {
    // <CODIGO_DESARROLLADO>
    // </CODIGO_DESARROLLADO>
    return true;
}

@Override
public boolean actualizarDespues() {
    // <CODIGO_DESARROLLADO>
    /*
     * FR1110-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
     * Dim str As String If Not Me.NewRecord Then
     * AudRegistroComparar Me, Me!Compania & "^" & Me!Ciclo & "^"
     * & Me!CodigoRuta, Me!Compania, Me!Ciclo, Me!CodigoRuta End
     * If End Sub
     */
    // </CODIGO_DESARROLLADO>
    return true;
}

@Override
public boolean eliminarAntes() {
    // <CODIGO_DESARROLLADO>
    /*
     * FR1110-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As
     * Integer) AuditarModif Me, 2, Me!Compania & "^" & Me!Ciclo &
     * "^" & Me!CodigoRuta End Sub
     */
    // </CODIGO_DESARROLLADO>
    return true;
}

@Override
public boolean eliminarDespues() {
    // <CODIGO_DESARROLLADO>
    // </CODIGO_DESARROLLADO>
    return true;
}

// <SET_GET_ATRIBUTOS>
// </SET_GET_ATRIBUTOS>
// <SET_GET_LISTAS>

public List<Registro> getListaProblemaC() {
    return listaProblemaC;
}

public List<Registro> getListaSeguimientosubfactura() {
    return listaSeguimientosubfactura;
}

public void setListaSeguimientosubfactura(
    List<Registro> listaSeguimientosubfactura) {
    this.listaSeguimientosubfactura = listaSeguimientosubfactura;
}

public List<Registro> getListaUsuarioproblemaconsulta() {
    return listaUsuarioproblemaconsulta;
}

public void setListaUsuarioproblemaconsulta(
    List<Registro> listaUsuarioproblemaconsulta) {
    this.listaUsuarioproblemaconsulta = listaUsuarioproblemaconsulta;
}


}

