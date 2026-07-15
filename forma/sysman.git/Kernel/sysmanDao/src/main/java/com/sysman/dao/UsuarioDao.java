/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.dao;

import com.sysman.enums.ParametrosConstantes;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Aplicacion;
import com.sysman.logica.Dependencia;
import com.sysman.logica.ResponsableAso;
import com.sysman.logica.Usuario;
import com.sysman.util.SysmanFunciones;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Operaciones para trabajar con datos del usuario.
 * 
 * @author cmanrique
 * @author jrodrigueza
 */
public class UsuarioDao {

    private static final String CODIGO_URL_DATOS_AUTENTICACION = "47004";
    private static final String CODIGO_URL_DEPENDENCIA_USUARIO = "52002";
    private static final String CODIGO_URL_RESPONSABLE_ASOCIADO_USUARIO = "52003";
    private static final String CODIGO_URL_APLICACION_GENERAL = "58002";
    /**
     * 47026 getUsuariosCargarDatosUsuarioQuery
     */
    private static final String CODIGO_URL_CARGA_DATOS_USUARIO = "47026";

    /**
     * Contenedor de datos de usuario
     */
    private Usuario usuario;

    /**
     * Maneja las peticiones a la base de datos.
     */
    private RequestManager requestManager;

    /**
     * Constructor
     */
    public UsuarioDao() {
        requestManager = new RequestManager();
    }

    /**
     * Validaci&oacute;n de usuario y contrase&ntilde;a para una
     * compa&ntilde;&iacute;a dada.
     * 
     * @param nombreUsuario
     * nombre de usuario
     * @param password
     * contrase&ntilde;a
     * @param compania
     * c&oacute;digo de la compa&ntilde;&iacute;a
     * @throws SysmanException
     * en caso de se presenten problemas al capturar los datos del
     * usuario o al codificar la contrase&ntilde;a
     */
    public void validarUsuario(String nombreUsuario, String password,
        String compania) throws SysmanException {
        Parameter parUsuario;
        try {
            Map<String, Object> par = new HashMap<>();
            par.put(ParametrosConstantes.CODIGO.getValue(), nombreUsuario);
            par.put(ParametrosConstantes.PASSWORD.getValue(),
                            SysmanFunciones.getMD5Code(password));

            parUsuario = requestManager.get(UrlServiceUtil
                            .getUrlBeanById(CODIGO_URL_DATOS_AUTENTICACION)
                            .getUrl(),
                            par);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            throw new SysmanException("Error al codificar la contraseña");
        }
        cargarDatosUsuario(parUsuario, compania);
    }

    /**
     * Validaci&oacute;n de usuario para una compa&ntilde;&iacute;a
     * dada. Uso exclusivo para autenticación por LDAP.
     * 
     * @param usuario
     * c&oacute;digo de usuario
     * @param password
     * contrase&ntilde;a
     * @param compania
     * c&oacute;digo de la compa&ntilde;&iacute;a
     * @throws SystemException
     * @throws SysmanException
     * en caso de se presenten problemas al capturar los datos del
     * usuario.
     */
    public void validarUsuario(String usuario, String compania)
                    throws SysmanException {
        Map<String, Object> par = new HashMap<>();
        par.put(ParametrosConstantes.CODIGO.getValue(), usuario);
        try {
            Parameter parUsuario = requestManager.get(UrlServiceUtil
                            .getUrlBeanById(CODIGO_URL_CARGA_DATOS_USUARIO)
                            .getUrl(),
                            par);
            cargarDatosUsuario(parUsuario, compania);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    /**
     * Carga los datos de usuario registrados en la base de datos.
     * 
     * @param parUsuario
     * datos de usuario
     * @param compania
     * @throws SysmanException
     * en caso de que el usuario este inactivo o se presenten
     * problemas al traer datos de la dependencia, responsable
     * asociado y aplicaci&oacute;n general.
     */
    private void cargarDatosUsuario(Parameter parUsuario, String compania)
                    throws SysmanException {
        try {
            usuario = null;

            if (parUsuario != null) {
                if (!"A".equals(SysmanFunciones.toString(
                                parUsuario.getFields().get("ESTADO")))) {
                    throw new SysmanException("El usuario no esta activo");
                }
                String codigoUsuario = SysmanFunciones.toString(parUsuario
                                .getFields()
                                .get(ParametrosConstantes.CODIGO.getValue()));
                usuario = new Usuario();
                usuario.setCodigo(codigoUsuario);
                usuario.setEstilo(SysmanFunciones.toString(
                                parUsuario.getFields().get("ESTILO")));
                usuario.setIdioma(SysmanFunciones.toString(
                                parUsuario.getFields().get("IDIOMA")));
                usuario.setRutaImagen(SysmanFunciones.toString(
                                parUsuario.getFields().get("RUTA_IMAGEN")));
                usuario.setFraseDelDia(SysmanFunciones.toString(
                                parUsuario.getFields().get("FRASEDELDIA")));
                usuario.setGenero(SysmanFunciones.toString(
                                parUsuario.getFields().get("GENERO")));
                usuario.setCedula(SysmanFunciones.toString(
                                parUsuario.getFields().get("CEDULA")));
                usuario.setSucursal(SysmanFunciones.toString(
                                parUsuario.getFields().get("SUCURSAL")));
                usuario.setIdentificador(SysmanFunciones.toString(
                                parUsuario.getFields().get("IDENTIFICADOR")));

                usuario.setNombre1(SysmanFunciones.toString(
                                parUsuario.getFields().get("NOMBRE1")));
                usuario.setNombre2(SysmanFunciones.toString(
                                parUsuario.getFields().get("NOMBRE2")));
                usuario.setApellido1(SysmanFunciones.toString(
                                parUsuario.getFields().get("APELLIDO1")));
                usuario.setApellido2(SysmanFunciones.toString(
                                parUsuario.getFields().get("APELLIDO2")));
                usuario.setPais(SysmanFunciones.toString(
                                parUsuario.getFields().get("PAIS")));
                usuario.setRegion(SysmanFunciones.toString(
                                parUsuario.getFields().get("REGION")));
                usuario.setCiudad(SysmanFunciones.toString(
                                parUsuario.getFields().get("CIUDAD")));
                usuario.setCorreoElectronico(SysmanFunciones.toString(
                                parUsuario.getFields()
                                                .get("CORREOELECTRONICO")));
                usuario.setTituloProfesional(SysmanFunciones.toString(
                                parUsuario.getFields()
                                                .get("TITULO_PROFESIONAL")));
                usuario.setEstado(SysmanFunciones.toString(
                                parUsuario.getFields()
                                                .get("ESTADO")));
                usuario.setDireccion(SysmanFunciones.toString(
                                parUsuario.getFields()
                                                .get("DIRECCION")));
                usuario.setCelular(SysmanFunciones.toString(
                                parUsuario.getFields()
                                                .get("CELULAR")));
                usuario.setFechaNacimiento(parUsuario.getFields()
                                .get("FECHANACIMIENTO") != null
                                    ? (Date) parUsuario.getFields()
                                                    .get("FECHANACIMIENTO")
                                    : null);

                usuario.setDependencia(getDependencia(codigoUsuario, compania));
                usuario.setResponsableAso(
                                getResponsableAsoc(codigoUsuario, compania));
                usuario.setAplicacionGeneral(getAplicacionGeneral());

                usuario.setMinutosBloqueo((Integer) parUsuario.getFields()
                                .get("MINUTOSBLOQUEO"));

                usuario.setPassword(
                                SysmanFunciones.toString(parUsuario.getFields()
                                                .get("PASSWORD")));
            }
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    /**
     * Trae los datos de la dependencia asociada al usuario.
     * 
     * @param usuario
     * @param compania
     * @return
     * @throws SystemException
     */
    private Dependencia getDependencia(String usuario, String compania)
                    throws SystemException {
        Dependencia dependencia = null;

        Map<String, Object> par = new HashMap<>();
        par.put(ParametrosConstantes.CODIGO.getValue(), usuario);
        par.put(ParametrosConstantes.COMPANIA.getValue(), compania);

        Parameter parDependencia = requestManager.get(UrlServiceUtil
                        .getUrlBeanById(CODIGO_URL_DEPENDENCIA_USUARIO)
                        .getUrl(),
                        par);

        if (parDependencia != null) {
            dependencia = new Dependencia(SysmanFunciones
                            .toString(parDependencia.getFields()
                                            .get(ParametrosConstantes.CODIGO
                                                            .getValue())),
                            SysmanFunciones.toString(parDependencia.getFields()
                                            .get(ParametrosConstantes.NOMBRE
                                                            .getValue())),
                            (boolean) parDependencia.getFields()
                                            .get("MOVIMIENTO"),
                            SysmanFunciones.toString(parDependencia.getFields()
                                            .get("SIGLA")),
                            SysmanFunciones.toString(parDependencia.getFields()
                                            .get("CENTRODECOSTO")),
                            (boolean) parDependencia.getFields()
                                            .get("COMODATO"),
                            (boolean) parDependencia.getFields().get("ACTIVO"),
                            (boolean) parDependencia.getFields()
                                            .get("VERBANCO"));
        }

        return dependencia;

    }

    /**
     * Trae los datos del responsable asociado.
     * 
     * @param usuario
     * @param compania
     * @return
     * @throws SystemException
     */
    private ResponsableAso getResponsableAsoc(String usuario, String compania)
                    throws SystemException {
        ResponsableAso respAsociado = null;

        Map<String, Object> par = new HashMap<>();
        par.put(ParametrosConstantes.CODIGO.getValue(), usuario);
        par.put(ParametrosConstantes.COMPANIA.getValue(), compania);

        Parameter parResponsable = requestManager.get(UrlServiceUtil
                        .getUrlBeanById(CODIGO_URL_RESPONSABLE_ASOCIADO_USUARIO)
                        .getUrl(),
                        par);

        if (parResponsable != null) {
            respAsociado = new ResponsableAso(
                            SysmanFunciones.toString(parResponsable.getFields()
                                            .get("RESPONSABLE")),
                            SysmanFunciones.toString(parResponsable.getFields()
                                            .get("SUCURSAL")),
                            SysmanFunciones.toString(parResponsable.getFields()
                                            .get("NOMBRE")),
                            SysmanFunciones.toString(parResponsable.getFields()
                                            .get("CARGO")),
                            (boolean) parResponsable.getFields()
                                            .get("JEFEUNIDAD"),
                            (boolean) parResponsable.getFields()
                                            .get("ACTIVO_RECEP"));
        }

        return respAsociado;
    }

    /**
     * Trae los datos de la aplicaci&oacute;n general.
     * 
     * @return
     * @throws SystemException
     */
    private Aplicacion getAplicacionGeneral() throws SystemException {
        Aplicacion aplicacion = null;
        Parameter parAplicacion = requestManager.get(UrlServiceUtil
                        .getUrlBeanById(CODIGO_URL_APLICACION_GENERAL)
                        .getUrl(),
                        null);
        if (parAplicacion != null) {
            aplicacion = new Aplicacion(
                            (int) parAplicacion.getFields().get("APLICACION"),
                            SysmanFunciones.toString(parAplicacion.getFields()
                                            .get("NOMBRE")),
                            SysmanFunciones.toString(parAplicacion.getFields()
                                            .get("AREA")),
                            (int) parAplicacion.getFields().get("DIASHABILES"),
                            SysmanFunciones.toString(parAplicacion.getFields()
                                            .get("RUTA_ARCHIVOS")),
                            SysmanFunciones.toString(parAplicacion.getFields()
                                            .get("RUTA_DOCUMENTOS")));
        }
        return aplicacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}