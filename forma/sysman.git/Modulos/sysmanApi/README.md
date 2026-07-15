# PROYECTO ASOCIADO A LOS SERVICIOS EXPUESTOS POR EL ERP


[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Dentro de este proyecto encontrara el codigo asociado para realizar las siguientes acciones sobre el ERP de Stefanini-sysman

  - REGISTRAR DATOS PERSONALES (NOMINA)
  - REGISTRAR DATOS DE FAMILIA (NOMINA)
  - GENERAR PDF A TRAVES DE SOLICITUDES
  - CONTABILIZAR UN COMPROBANTE
  - VALIDAR SI EXISTE UN COMPROBANTE CONTABLE
  - IMPRIMIR COMPROBANTE CONTABLE
  - GENERAR JSON ORGANIGRAMA BUCARAMANGA
  - ESTADO MES CONTABLE
  - GENERAR LIQUIDACION FACTURA
  

# 1. PRERREQUISITOS Y ENDPOINT ASOCIADOS 

## 1.1. PRERREQUISITOS
A nivel de la base de datos se debe tener creado el usuario AUTOSERVICIO con la contraseña SYSAUTOSERVICIOMAN

## 1.2. ENDPOINT ASOCIADOS 
Se debe tener en cuenta que la ruta para consumir los servicios API REST es la misma que la de ERP y  deben	estar	precedidas	por	el	contexto	web:
/autoservicio/v1

URL Endpoint
```sh
https://www.sysman.com.co/sysmanApi/autoservicio/v1
```
Donde, “www.sysman.com.co” es el host que aloja la API que se est&aacute; consumiendo.
A continuaci&oacute;n se muestra la URI que se debe adicionar a la URL del endpoint  para consumir el servicio determinado.
Todos los endpoint van precedidos del path servicio:


| SERVICIO | URI | METODO
| ------ | ------ |-----
| Registrar Datos Personales| /datosfamiliares| Post
| Regsitrar Datos de Familiares | /datospersionales | Post
| Generar PDF a traves de Solicitudes| /solicitudes | Post
| Contabilizar Comprobante Contable| /contabilizar | Post
| Validar Existencia de Comprobante Contable| /existecomprobante | Get
| Impresi&oacute;n de Comprobante Contable| /imprimecomprobante | Get
| Validar Pago de Factura Idsn| /validapagoidsn | Get
| Generar JSON Organigrama Bucaramanga|/generaorganigrama|Get
| Generar JSON Dependencia Inventario|/generaInventarioDependencia|Get
| Generara el Estado del mes contable|/estadoMesContable|Get
| Generar Liquidación y Factura de SF |/liquidacionfactura|POST

Para cada petici&oacute;n HTTP se debe enviar como cabeceras HTTP la siguiente informaci&oacute;n, necesaria para la comunicaci&oacute;n, con la sintaxis ‘Cabecera: Valor’

| CLAVE | VALOR
| ------ | ------ 
|Authorization| q4dpueLWGsv9cGtu
|Content-Type | application/json

El valor del Token para consumir el servicio esta dado por cada cliente


# 2. PROCESADORES Y SERVICIOS ASOCIADOS

Para implementar la capa de l&oacute;gica del negocio  y servicio se utilizaron las siguientes clases:


| SERVICIO | SERVICIO | PROCESADOR
| ------ | ------ |-----
| Registrar Datos Personales| datosPersonales|  **ProcesaActualizacionDatosPersonales.java**
| Registrar Datos de Familiares | datosPersonales | **ProcesaActualizacionDatosPersonales.java**
| Generar PDF a traves de Solicitudes| solicitudes | **ProcesaSolicitudesAutoservicio.java**
| Contabilizar Comprobante| contabiliza | **ProcesaContabiliza.java**
| Imprimir Comprobante| imprimecomprobante | **ProcesaImprimeComprobante.java**
| Generar JSON Organigrama| generaorganigrama | **ProcesaGeneraOrganigrama.java**

## 3. CONTABILIZAR UN COMPROBANTE


Por medio de este servicio permite registrar un comprobante contable. En esta secci&oacute;n se especifican los datos necesarios para consumir al servicio.

### 3.1 ESTRUCTURA JSON

```sh
{    "nitEntidad": long
	, "compania":"String"
	, "tipo":"String"
	, "numero": long
	, "ano": int
	, "fecha": "String"
	, "tercero": "String"
	, "sucursal": "String"
	, "centroCosto": "String"
	, "auxiliar": "String"
	, "referencia": "String"
	, "fuenteRecurso": "String"
	, "descripcion": "String"
	, "texto": "String"
	, "simplifica": boolean
	, "omitirPptal": boolean
	, "concilia": boolean
	, "noNetea": boolean
	, "contratista": boolean
	, "tipoContrato": "String"
	, "contrato": long
	, "nroDocumento":"String"
	, "almDep": boolean
	, "respetaTercero": boolean
	, "respetaAuxiliar": boolean
	, "reemplaza": boolean
	, "usuario":"String"
	, "detalle" : [{
						  "cuenta":"String"
						, "tercero": "String"
						, "sucursal": "String"
						, "centroCosto": "String"
						, "auxiliar": "String"
						, "referencia": "String"
						, "fuenteRecurso": "String"
						, "nroDocumento":"String"
						, "valorDebito":"String"
						, "valorCredito": "String"
						, "cuentapptal":"String"						
						},
						{
						  "cuenta":"String"
						, "tercero": "String"
						, "sucursal": "String"
						, "centroCosto": "String"
						, "auxiliar": "String"
						, "referencia": "String"
						, "fuenteRecurso": "String"
						, "nroDocumento": "String"
						, "descripcion": "String"
						, "valorDebito": "String"
						, "valorCredito": "String"
						, "cuentapptal": "String"						
						}
						]

}
```
### 3.2 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).
#### BODY
#### Datos del comprobante

| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipo| String| SI | Tipo de Comprobante que se va a realizar.
| numero| Long | NO | N&uacute;mero o consecutivo que creara, para que el sistema lo cree se puede dejar de enviar.
| ano | int | SI | A&ntilde;o para el cual se desea crear el comprobante.
| fecha| String | SI |Fecha formato dd/mm/yyyy.
| tercero| String| NO | N&uacute;mero de identificaci&oacute;n de la persona a nombre de la cual queda del respectivo comprobante.
| sucursal| String| NO | Sucursal de la persona a nombre de la cual queda del respectivo comprobante(si el tercero es varios sera 999 de lo contrario 001).
| centroCosto | String | NO | C&oacute;digo de centro de costo al cual pertenece el comprobante.
| auxiliar| String | NO | C&oacute;digo del auxiliar al cual pertenece el comprobante.
| referencia| String | NO | C&oacute;digo de la referencia al cual pertenece el comprobante.
| fuenteRecurso| String | NO | C&oacute;digo de la fuente de recurso al cual pertenece el comprobante.
| descripcion| String | SI |Descripci&oacute;n reducida del comprobante contable.
| texto| String | SI |Descripci&oacute;n ampliada del comprobante contable.
| simplifica | boolean | SI | Indica si la contabilizaci&oacute;n se realiza simplificandola, es decir sumar los valores por cada cuenta contable y sus repectivos auxiliares.
| omitirPptal | boolean | SI | Indica si la contabilizaci&oacute;n crea el comprobante presupuestal equivalente de acuerdo a las equivalencia presupuestales de las cuentas contables.
| concilia | boolean | SI | Indica si la contabilizaci&oacute;n al terminar deja conciliadas las cuentas de bancos que el comprobante tenga.
| noNetea | boolean | SI | Indica si la contabilizaci&oacute;n se realiza netando o no; las cuentas en su d&eacute;bito y cr&eacute;dito.
| contratista | boolean | SI | Indica si la contabilizaci&oacute;n se reportan los datos del contratista.
| tipoContrato | String | NO | tipo de contrato cuando el parametro contratista esta en true.
| contrato | Long | NO | N&uacute;mero de contrato cuando el parametro contratista esta en true.
| nroDocumento| String | NO | N&uacute;mero de cheques, referencia de pago o n&uacute;mero de factura del comprobante a crear. 
| almDep | boolean | SI | Indica si la contabilizaci&oacute;n se envia la dependencia desde almacen.
| respetaTercero | boolean | SI | Indica si la contabilizaci&oacute;n respeta el tercero enviado por detalle o se toma el VARIOS para simplificar.
| respetaAuxiliar | boolean | SI | Indica si la contabilizaci&oacute;n respeta el auxiliar enviado por detalle o se toma el VARIOS para simplificar.
| reemplaza | boolean | SI | Indica si la contabilizaci&oacute;n reemplaza elcomprobante si este ya existe en la base de datos.
|detalle| List | SI |Lista de detalles que tiene el comprobante. 
#### Datos del detalle del Comprobante
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| cuenta| String| SI | C&oacute;digo de la cuenta contable.
| tercero| String| NO | N&uacute;mero de identificaci&oacute;n de la persona a nombre de la cual se registra el detalle.
| sucursal| String| NO | Sucursal de la persona a nombre de la cual se registra el detalle(si el tercero es varios sera 999 de lo contrario 001).
| centroCosto | String | NO | C&oacute;digo de centro de costo del cual se registra el detalle.
| auxiliar| String | NO | C&oacute;digo del auxiliar del cual se registra el detalle.
| referencia| String | NO | C&oacute;digo de la referencia de la cual se registra el detalle.
| fuenteRecurso| String | NO | C&oacute;digo de la fuente de recurso de la cual se registra el detalle.
| nroDocumento| String| NO | Referencias de pago, n&uacute;meros de facturas entre otros.
| descripcion| String| NO | Texto explicativo del detalle del comprobante.
| valorCredito | String| SI | Valor por el cual se afecta la cuenta contable al cr&eacute;dito. 
| valorDebito| String| SI | Valor por el cual se afecta la cuenta contable al D&eacute;bito. 
| cuentapptal| String| NO | C&oacute;digo del rubro presupuestal para realizar el comprobante presupuestal debe coincidir con el equivalente presupuestal de la cuenta contable. 


### 3.3 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 3.3.1 ESTRUCTURA JSON 

```sh
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando el comprobante se genera correctamente la salida es 0.
| Mensaje| String| Mensaje asociado al registro del recaudo con sus detalles, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Cuando el c&oacute;digo de salida es 0, en este se genera el n&uacute;mero del comprobante generado, de lo contrario la descripci&oacute;n de la incosistencia presentada.



## 4. VALIDAR SI EXISTE UN COMPROBANTE CONTABLE


Por medio de este servicio se valida si ya existe un comprobante contable en el ERP.

### 4.1 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipo| String| SI | Tipo de Comprobante que se va a validar.
| numero| Long | SI | N&uacute;mero o consecutivo que se validara.
| anio | int | SI | A&ntilde;o para el cual se desea validar el comprobante.

### 4.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 4.2.1 ESTRUCTURA JSON 

```sh
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petición se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validación, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Cuando el c&oacute;digo de salida es 0, un true si el comprobante existe sino un false.


## 5. IMPRIME COMPROBANTE CONTABLE


Por medio de este servicio se imprime un comprobante contable del ERP.

<span style="color:red">**__OJO__**</span>: Validar que en el aplicativo de Contabilidad que el tipo de comprobante a imprimir tenga un formato de impresión valido

### 5.1 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipo| String| SI | Tipo de Comprobante que se va a imprimir.
| numero| Long | SI | N&uacute;mero o consecutivo que se va a imprimir.
| anio | int | SI | A&ntilde;o del comprobante a imprimir.

### 5.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 5.2.1 ESTRUCTURA JSON 

```sh
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petici&oacuten se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validaci&oacuten, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Cuando el c&oacute;digo de salida es 0, regresa el base64 que representa el reporte, para que sea decodificado y/o materilializado.

## 6. VALIDAR PAGO FACTURA IDSN


Por medio de este servicio se envian los datos de pago de una factura dado el n&uacute;mero de factura y tercero 

### 6.1 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipo| String| SI | Tipo de Comprobante que se va a consultar; es decir el tipo con el cual se realiza el pago.
| nroDocumento| String| SI | Tipo de Comprobante que se va a consultar; es decir el tipo con el cual se realiza el pago.
| tercero| Long | SI | Tercero al cual se le realiza el pago de acuerdo a la causaci&oacute;n de la factura


### 6.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 6.2.1 ESTRUCTURA JSON 

```json
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo": [
        {
            "tipo": "String",
            "comprobante": Long,
            "fecha": "String",
            "tipocdp": "String",
            "cdp": Long,
            "tiporp": "String",
            "rp": Long
        },
        {
            "tipo": "EGR",
            "comprobante": Long,
            "fecha": "String",
            "tipocdp": "String",
            "cdp": Long,
            "tiporp": "String",
            "rp": Long
        }
    ]
}
```

ó

```json
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petici&oacuten se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validaci&oacuten, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Cuando el c&oacute;digo de salida es 0, regresa los datos del comprobante de pago de la factura de acuerdo a la estructura siguiente; sino se devuleve 0 en este campo devuelve un String con la descripci&oacute;n de error

##### 6.2.1.1 ESTRUCTURA JSON CUERPO

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| tipo| String| Tipo de comprobante con el que se realiza el pago de la factura.
| comprobante| Long| N&uacute;mero del comprobante con el cual se realiza el pago de la factura.
| fecha| String| Fecha del comprobante con el cual se realiza el pago de la factura, la fecha se devuelve en formato DD/MM/YYYY
| tipocdp| String| Tipo de comprobante de disponibilidad generado y heredado para realizar el pago de la factura
| cdp| Long| N&uacute;mero de comprobante de disponibilidad generado y heredado para realizar el pago de la factura
| tiporp| String| Tipo de comprobante de registro generado y heredado para realizar el pago de la factura
| rp| Long| N&uacute;mero de comprobante de registro generado y heredado para realizar el pago de la factura

## 7. GENERAR JSON ORGANIGRAMA BUCARAMANGA


Por medio de este servicio se genera un archivo de tipo Json que contiene la información del organigrama configurado por la entidad.

### 7.1 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.


### 7.2 RESPUESTA DEL SERVICIO

La salida se da en formato Json de la siguiente forma

#### 7.2.1 ESTRUCTURA JSON 

```json
     {
    "id": 1,
    "title": "ALCALDIA DE BUCARAMANGA",
    "root": {
        {
          "id": "String",                       
          "title": "String",                               
          "type": "String",                  
          "color_nodo":"hexa",                        
          "children" : [    
            {
                "id": "String",                       
                "title": "String",                               
                "type": "String",                  
                "color_nodo":"hexa",                        
                "children" : []                   
            }
          ]                    
        }
    }
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| id| String| Identificador del nodo.
| title| String| Título del nodo.
| type| String| Existen los tipos (subordinate, staff, staffleft, stafftop, collateral).
|color_nodo| Hexa |Color para cada uno de los nodos, si se define desde el json sobre escribe el valor asignado en la configuracion general.
|children| [] |Los hijos del correspondiente nodo con las mismas propiedades anteriormente mencionadas.

## 8. GENERAR JSON DEPENDENCIA INVENTARIO IDEAM


Por medio de este servicio se genera un archivo de tipo Json que contiene la información de los inventarios por dependencia.

### 8.1 PARAMETROS DE ENTRADA
#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| Authorization| String| SI | Credenciales de autorizaci&oacute;n.
| Content-Type | String | SI | El tipo de contenido de la petici&oacute;n en POST(application/json).

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| nitEntidad| Long| SI | C&oacute;digo que identifica al Nit de la Entidad.
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
|codigoEquivalente|String|SI|Código equivalente que diferencia las dependencias

### 8.2 RESPUESTA DEL SERVICIO

La salida se da en formato Json de la siguiente forma

#### 8.2.1 ESTRUCTURA JSON 
```json
{
    "codigo": "int",
    "mensaje": "String",
    "cuerpo": [
        {
            "fields": {
                "SERIEDEVOLUTIVO": "String",
                "ACTA_INSTALACION": "String",
                "FECHA_INSTALACION": "String",
                "PLACA_DE_INVENTARIO": "String",
                "MARCA": "String ",
                "MODELO": "String",
                "DESCRIPCION": "String"
            }
        }
    ]
}
```
| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petición se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validación, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Si no existe información con el código equivalente retornará vacío [], de lo contrario retornará la información correspondiente

##### 8.2.1.1 ESTRUCTURA JSON CUERPO

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| fields| String| Nodo que separa los registros.
| SERIEDEVOLUTIVO| String| Número de serie devolutivo del elemento.
| ACTA_INSTALACION| String| Número de acta de instalación del elemento.
| FECHA_INSTALACION| String| Fecha de instalación del elemento.
| PLACA_DE_INVENTARIO| Long| Número de serie del elemento.
| MARCA| String| Marca del elemento.
| MODELO| String| Modelo del elemento.
|DESCRIPCION|String|Descripción del elemento.


## 9. SERVICIOS PQRS

Por medio de los siguientes recursos se puede radicar un tramite a traves del proceso de PQRS creado a través del modulo de WorkFlow desde la pagina web de la alcaldía.


### 9.1 SERVICIO RADICAR PQRS
#### 9.1.1 RECURSO
| NOMBRE | URL    | METODO | DESCRIPCION
| ------ | ------ |-----|----------
| radicarPQRS | http://localhost:puerto/sysmanApi/autoservicio/pqr | POST | Permite radicar un tramite tipo pqrs y recibir un correo con la confirmación del número de radicado.

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipoTramite | String | SI | Tipo de tramite que puede ser realizado por el cudadano (Petición, Queja, Reclamo, Sugerencia).
| cedula| Long| SI | Número del documento del ciudadano
| nombre| String | SI | Nombre del ciudadano.
| direccion| String | SI | Dirección del ciudadano.
| correo| String | SI | Correo electrónico del ciudadano.
| descripcion | String | SI | Descripcion de la pqrs que se esta radicando.
| anexos | [ {"anexo": "1","nombre": "","valor": ""}, ... ] | NO | Colección de anexos que se suben a la pqrs


### 9.1.2 RESPUESTA DEL SERVICIO

La salida se da en formato Json de la siguiente forma

#### ESTRUCTURA JSON 
```json
{
    "codigo": 0,
    "mensaje": "OK",
    "cuerpo": 202000006
}
```

### 9.2 SERVICIO CONSULTAR PQRS
#### 9.2.1 RECURSO
| NOMBRE | URL    | METODO | DESCRIPCION
| ------ | ------ |-----|----------
| obtenerPQRS | http://localhost:puerto/sysmanApi/autoservicio/pqr? | GET | Permite obtener información de la pqrs sobre como va el tramite.

#### PARAMS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| numRadicado| String| SI | Número del radicado a consultar.
| cedula | String | SI | Identificación de la persona que radica la pqrs.
| compania | String | SI | Código de la compania. 

### 9.1.2 RESPUESTA DEL SERVICIO

La salida se da en formato Json de la siguiente forma

#### ESTRUCTURA JSON 
```json
{
    "codigo": 0,
    "mensaje": "OK",
    "cuerpo": {
        "numRadicado": "202000006",
        "etapa": "Radicar Solicitud",
        "dependencia": "NINGUNA"
    }
}
```

### 9.3 A TENER EN CUENTA PQRS
El proceso de pqrs permite radicar y consultar un tramite dentro del módulo de workflow, para lo cual se vale del uso de las siguientes conexiones:

| ESQUEMA | DESCRIPCION
| ------ | ------
|FACTURACION_WEB | Hace uso de esta conexión para obtener los servicios pasando como parametro el código de la aplicacion (35) y el codigo de compania, almacenados en la tabla TOL_COMPANIA_SERVICIO.
| SYSMANDSUNIST | Hace uso de esta conexión para registrar el trámite a traves del paquete PCK_WORKFLOW.FC_WORKFLOW_PQRS

Para el uso del api de mensajeria para el envio de correos desde el backend, se obtiene la url del servicio configurado en la conexión con el esquema ```SYSMANDSUNIST```, dicha url esta registrada en la TABLA ```URLSERVICIO```

## 10. 

Por medio de este servicio se expone el plan contable 


## 10. CAMPOS PLAN CONTABLE


Por medio de este servicio se expone el plan contable 

<span style="color:red">**__OJO__**</span>: El siguiente DSS debe estar creado
MERGE INTO API_RECURSOS FIN USING (SELECT 16 SERVICIO, '213' ID, '16213' CODIGO, 'planescontables/getcamposplancontable' URL,'GET' METODO_HTTP,NULL METODO, NULL SERVICIO_CONTEO, NULL RECURSO_CONTEO  FROM DUAL ) INI ON (FIN.SERVICIO = INI.SERVICIO AND FIN.ID = INI.ID)  WHEN MATCHED THEN  UPDATE SET FIN.CODIGO =  INI.CODIGO,FIN.URL =  INI.URL,FIN.METODO_HTTP =  INI.METODO_HTTP,FIN.METODO =  INI.METODO,FIN.SERVICIO_CONTEO =  INI.SERVICIO_CONTEO,FIN.RECURSO_CONTEO =  INI.RECURSO_CONTEO  WHEN NOT MATCHED THEN  INSERT (SERVICIO,ID,CODIGO,URL,METODO_HTTP,METODO,SERVICIO_CONTEO,RECURSO_CONTEO)  VALUES (INI.SERVICIO,INI.ID,INI.CODIGO,INI.URL,INI.METODO_HTTP,INI.METODO,INI.SERVICIO_CONTEO,INI.RECURSO_CONTEO);


### 10.1 PARAMETROS DE ENTRADA

#### RECURSO
| NOMBRE | URL    | METODO | DESCRIPCION
| ------ | ------ |-----|----------
| listaPlanContable | http://localhost:puerto/sysmanApi/autoservicio/v1/listaPlanContable?compania=001&ano=2022&codigo=1 | GET | Permite mostrar la información completa del plan contable dando el codigo del mismo 

#### HEADERS
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------

#### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| mes | int | SI | mes del plan contable a generar.
| anio | int | SI | A&ntilde;o del plan contable.
| codigo| String| SI | Codigo del plan contable .


### 10.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 10.2.1 ESTRUCTURA JSON 

```sh
{
    "codigo": 0,
    "mensaje": "OK",
    "cuerpo": [
        {
            "compania": "001",
            "ano": "2022",
            "codigo": "2",
            "nombre": "PASIVO",
            "naturaleza": "C",
            "movimiento": "false",
            "man_cen_cto": "false",
            "man_aux_ter": "false",
            "man_aux_gen": "false",
            "man_aux_ref": "false",
            "man_aux_fue": "false",
            "obliga_tercero": "false",
            "obliga_centro": "false",
            "obliga_auxiliar": "false",
            "obliga_referencia": "false",
            "obliga_fuente": "false",
            "dinamica": "",
            "presupuesto_anual": "0",
            "corriente": "false",
            "formato": "",
            "clasecuenta": "E",
            "bloqueacuenta": "NO",
            "saldoinicial": "0",
            "saldo0": "9276549887.94",
            "saldo1": "9228759681.78",
            "saldo2": "10615698865.89",
            "saldo3": "13774799977.15",
            "saldo4": "10920097342.77",
            "saldo5": "11404651232.44",
            "saldo6": "12440307136.5",
            "saldo7": "12415629700.48",
            "saldo8": "12755198672.88",
            "saldo9": "12313960139.66",
            "saldo10": "13033974132.35",
            "saldo11": "12931225898.22",
            "saldo12": "12987240812.22",
            "saldo13": "12987240812.22",
            "neto0": "9276549887.94",
            "neto1": "-47790206.16",
            "neto2": "1386939184.11",
            "neto3": "3159101111.26",
            "neto4": "-2854702634.38",
            "neto5": "484553889.67",
            "neto6": "1035655904.06",
            "neto7": "-24677436.02",
            "neto8": "339568972.4",
            "neto9": "-441238533.22",
            "neto10": "720013992.69",
            "neto11": "-102748234.13",
            "neto12": "56014914",
            "neto13": "0",
            "debito0": "8874947510.6",
            "debito1": "1453393076.16",
            "debito2": "3318425519.38",
            "debito3": "4948377265.75",
            "debito4": "6998074419.79",
            "debito5": "3093022638.81",
            "debito6": "7063286801.11",
            "debito7": "4966755181.15",
            "debito8": "3141384828.56",
            "debito9": "7195937869.31",
            "debito10": "4607143094.45",
            "debito11": "4605898449.43",
            "debito12": "0",
            "debito13": "0",
            "credito0": "18151497398.54",
            "credito1": "1405602870",
            "credito2": "4705364703.49",
            "credito3": "8107478377.01",
            "credito4": "4143371785.41",
            "credito5": "3577576528.48",
            "credito6": "8098942705.17",
            "credito7": "4942077745.13",
            "credito8": "3480953800.96",
            "credito9": "6754699336.09",
            "credito10": "5327157087.14",
            "credito11": "4503150215.3",
            "credito12": "56014914",
            "credito13": "0",
            "ajuste0": "0",
            "ajuste1": "0",
            "ajuste2": "0",
            "ajuste3": "0",
            "ajuste4": "0",
            "ajuste5": "0",
            "ajuste6": "0",
            "ajuste7": "0",
            "ajuste8": "0",
            "ajuste9": "0",
            "ajuste10": "0",
            "ajuste11": "0",
            "ajuste12": "0",
            "ajuste13": "0",
            "generadesembolso": "false",
            "porcretencion": "0",
            "creditoexterno": "false",
            "pasarsaldo": "false",
            "cod_equiv": "",
            "transaccional5544": "false",
            "destino": "",
            "formatoegreso": "",
            "banco": "",
            "permiteconsolidar": "true",
            "man_fact_arrendamiento": "false",
            "notransaccional5544": "false",
            "noreportarreciprocas": "true",
            "terceroequivalentereciprocas": "",
            "conceptoex": "",
            "cuenta_bancaria": "",
            "terceroex": "",
            "sucursalex": "",
            "tipodescuento_sia": "",
            "codbanco_sia": "",
            "numerocuenta_sia": "",
            "destinocuentabanco": "",
            "codbanco_serec": "",
            "numerocuenta_serec": "",
            "cuenta_pptal": "",
            "esoficial": "false",
            "fuente": "",
            "equivpr_debito": "",
            "equivpr_credito": "",
            "ivaex": "0",
            "retepracticada": "0",
            "reteasumida": "0",
            "ivacomun": "0",
            "ivasimplificado": "0",
            "exdistrital": "false",
            "id_niif": "",
            "codigo_niif": "",
            "man_distri_ccosto": "false",
            "reteica": "0",
            "cree_practicada": "false",
            "cree_asumida": "false",
            "ccbalance": "99999999999999999999",
            "reportasaldoreciprocas": "false",
            "men": "false",
            "verificar_mov": "false",
            "cod_flujocaja": "",
            "created_by": "",
            "modified_by": "ABOLIVAR_SS",
            "aplica_deterioro": "false",
            "deb_reco_det": "",
            "cre_reco_det": "",
            "deb_caus_det": "",
            "cre_caus_det": "",
            "deb_rec_det": "",
            "cre_rec_det": "",
            "date_modified": "Thu Sep 29 10:55:17 COT 2022",
            "date_created": "",
            "cheque": "0",
            "reportar_100": "true",
            "tercero_reciprocas": "",
            "ind_circularunica": "false",
            "cuentas_maestras_salud": "false",
            "fecha_conciliacion": "",
            "saldo_conciliacion": "",
            "observ_conciliacion": "",
            "mostrarf1001": "false",
            "ind_agente_retencion": "false",
            "ind_sujeto_retencion": "false",
            "codigo_fut": "",
            "naturaleza_cgn": "",
            "mostrar_en_flujo": "true",
            "contraprestacion": "false",
            "concepto_flujo_cgn": "",
            "mostrar_en_flujo_cgn": "false",
            "debito_reversion_det_actual": "",
            "credito_reversion_det_actual": "",
            "debito_reversion_det_anterior": "",
            "credito_reversion_det_anterior": "",
            "cod_equi_cartera": ""
        }
    ]
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petici&oacuten se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validaci&oacuten, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Si no existe información con el código equivalente retornará vacío [], de lo contrario retornará la información correspondiente

## 11. GENERAR LIQUIDACION FACTURA

Creación del servicio de liquidacionfactura  en el cual  crea el header el detalle del concepto enviado  y realiza la facturación del mismo  

### 11.1 ESTRUCTURA JSON -  PARAMETROS DE ENTRADA

```sh
{
    "compania":"String",
    "tipoFactura":"String",
    "tercero":"String",
    "concepto":"String",
    "ano":"int",
    "descripcion":"String"
}

| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| tipoFactura| String| SI | Tipo de la factura a crear esta debe estar creada previamente en facturación.
| tercero| String| SI | N&uacute;mero de identificaci&oacute;n de la persona a nombre de la cual quedala respectiva factura.
| concepto| String| SI | Conceptos a facturar pueden ir uno o varios estos deben ir separados por, Ejemplo 001,003,018.
| ano | int | SI | A&ntilde;o para el cual se desea crear la factura.
| descripcion| String | SI |Descripci&oacute;n de la factura a generar.

```

### 11.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 11.2.1 ESTRUCTURA JSON 

```sh
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petición se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validación, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Cuando el c&oacute;digo de salida es 0, en este se genera el n&uacute;mero de factura, de lo contrario la descripci&oacute;n de la incosistencia presentada.

#### 11.2.1.1 EJEMPLO ESTRUCTURA JSON 
```sh
{
    "codigo": 0,
    "mensaje": "OK",
    "cuerpo": {
        "ANO": " 2022",
        "TIPOFACTURA": " SRV",
        "FECHA_VENCIMIENTO": " 13/01/2023 15:45:19",
        "TERCERO": " 999999999999999999",
        "SUCURSAL": " 999",
        "NRO_FACTURA": " 3260",
        "VALOR_TOTAL": " 593800"
    }
}

## 12. GENERARÁ EL ESTADO DEL MES CONTABLE


Por medio de este servicio se generará el Estado del mes contable.

### 12.1 PARAMETROS DE ENTRADA

### BODY
| NOMBRE | TIPO DATO | OBLIGATORIO | DESCRIPCION
| ------ | ------ |-----|----------
| compania| String | SI | C&oacute;digo que identifica a la compa&ntilde;ia o sucursal.
| anio | int | SI | A&ntilde;o para el cual se desea validar el periodo contable.
| mes | int | SI | mes del A&ntilde;o contable a generar.
| dia | int | SI | dia del mes contable a generar.


### 12.2 RESPUESTA DEL SERVICIO

La salida se da en formato json de la siguiente forma

#### 12.2.1 ESTRUCTURA JSON 

```sh
{    "codigo":"int"
	, "mensaje":"String"
	, "cuerpo":"String"
}
```

| NOMBRE | TIPO DATO |DESCRIPCION
| ------ | ------ |-----
| codigo| int| C&oacute;digo de Respueta, cuando la petición se da sin errores la salida es 0.
| Mensaje| String| Mensaje asociado a la validación, cuando el c&oacute;digo de salida es 0 genera un Ok.
| cuerpo| String| Como respuesta genera ABIERTO o CERRADO dependiendo la configuracion del mismo