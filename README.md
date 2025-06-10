<br/>
<div align="center">
<a href="https://github.com/mquesada02/TP-Cripto">
<img src="https://emojiapi.dev/api/v1/1f510.svg" alt="Logo" width="80" height="80">
</a>
<h2 align="center">Trabajo Práctico: Secreto Compartido en Imágenes con Esteganografía</h2>
<h3 align="center">Criptografía y Seguridad</h3>
</div>
<br/>

## Contribuidores
<div align="center">
  <a href="https://github.com/mquesada02"><img src="https://github.com/mquesada02.png" alt="mquesada02" width="80" height="80"></a>
  <a href="https://github.com/ursath"><img src="https://github.com/ursath.png" alt="ursath" width="80" height="80"></a>
  <a href="https://github.com/micaelaperillo"><img src="https://github.com/micaelaperillo.png" alt="micaelaperillo" width="80" height="80"></a>
  <br/>
</div>

## Tabla de contenidos

- [Tabla de contenidos](#tabla-de-contenidos)
- [Sobre este proyecto](#sobre-este-proyecto)
- [Requerimientos del sistema](#requerimientos-del-sistema)
- [Entorno de desarrollo](#entorno-de-desarrollo)
- [Distribución del secreto](#distribucion-del-secreto)
- [Recuperación del secreto](#recuperacion-del-secreto)

## Sobre este proyecto

Este trabajo práctico implementa el algoritmo de secreto compartido del paper de Kuang-Shyr Wu y Tsung-Ming Lo sobre el algoritmo de Thien-Lin.
Este paper proporciona una forma eficiente de guardar el secreto para imágenes de 8 bits, junto la aplicación de esteganografía para distribuir un secreto en diferentes portadoras.

## Requerimientos del sistema

Para preparar el entorno de desarrollo se requiere lo siguiente:

- [Java 21+](https://openjdk.org/)
- [Maven 3.9.9](https://maven.apache.org/)

También se pueden instalar con los siguientes comandos:
```sh
$> sudo apt install openjdk-21-jdk maven
```

## Entorno de desarrollo

Para poder ejecutar el programa, simplemente se deberá dar permisos de ejecución al script con el siguiente comando:

```sh
$> chmod u+x main.sh
```

## Distribucion del secreto

Si se desea distribuir una imágen en múltiples imágenes, debe ejecutar el siguiente comando:

```sh
$> ./main.sh -d -secret <bmp-image> -k <k-value> [-n <n-value>] [-dir <hosts-directory>]
```
dónde
* **bmp-image** es el path al secreto a distribuir en los distintas imágenes _host_.
* **k-value** es el minimo de imágenes requeridas para recuperar la imágen.
* **n-value** es la cantidad de imágenes a las que se quiere distribuir el secreto. Tiene como valor por defecto el número de imágenes en el <hosts-directory>.
* **hosts-directory** es el directorio de las imágenes _host_. Tiene como valor por defecto el directorio actual.

## Recuperacion del secreto

Si se desea recuperar una imágen secreta a partir de múltiples imágenes, debe ejecutar el siguiente comando:

```sh
$> ./main.sh -r -secret <bmp-image> -k <k-value> [-dir <hosts-directory>]
```
dónde
* **bmp-image** es el path donde se guardará la imágen recuperada.
* **k-value** es el minimo de imágenes requeridas para recuperar la imágen.
* **hosts-directory** es el directorio de las imágenes _host_ con el secreto distribuído. Tiene como valor por defecto el directorio actual.
