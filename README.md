Chat_Server – Servidor para la app Chat_Android

Servidor ligero desarrollado en Java para permitir la comunicación entre varios dispositivos dentro de una red local. Este servidor actúa como backend para la aplicación Android Chat_Android, gestionando mensajes, conexiones y grupos de chat.

¿Qué es Chat_Server? Es un servidor TCP simple que permite que varios clientes (la app Android) se conecten simultáneamente a través de la misma red WiFi. Gestiona:
Conexiones entrantes

Envío y recepción de mensajes

Difusión de mensajes a todos los clientes conectados

Identificación básica de usuarios

Relación con Chat_Android Este servidor es necesario para que la app Chat_Android funcione en modo multicliente dentro de una red local. La app se conecta a la IP del servidor y envía/recibe mensajes en tiempo real.

Tecnologías utilizadas Java Sockets TCP Multithreading I/O Streams

Estructura del proyecto Server.java — Punto de entrada del servidor

ClientHandler.java — Maneja cada cliente conectado

Cómo ejecutar el servidor Clonar el repositorio:
bash git clone https://github.com/Danikartt/Chat_Server.git Abrir el proyecto en IntelliJ IDEA o cualquier IDE Java

Ejecutar la clase principal Server

El servidor quedará escuchando en un puerto (por defecto 5000)

Conexión desde la app Android En la app Chat_Android, configura:
IP del servidor → la IP local del PC donde corre Chat_Server

Puerto → el mismo que usa el servidor (ej. 5000)

Ejemplo de conexión desde Android:

java Socket socket = new Socket("192.168.1.100", 5000); (Reemplazar por la IP real de tu PC en la red local)

Requisitos Todos los dispositivos deben estar en la misma red WiFi
El firewall debe permitir conexiones entrantes al puerto del servidor

Estado del proyecto Proyecto funcional y en mejora continua. Pensado como práctica personal para aprender comunicación cliente-servidor.

Licencia Uso libre para fines educativos.
