const NotificacionEventSourceManager = (function () {
    let eventSourceInstance = null;

    // Inicializa el EventSource si no existe o devuelve la instancia existente
    const initializeEventSource = (url) => {
        eventSourceInstance = new EventSource(url);

        // Event listeners: Se configuran solo una vez
        eventSourceInstance.addEventListener("INIT", (event) => {
            console.log("Conexión establecida (Cliente). ", event.data);
        });

        eventSourceInstance.addEventListener("NOTIFICACION", (event) => {
            notyf.open({
                type: "reabastecimiento",
                position: {
                    x: 'center',
                    y: 'top',
                },
                message: event.data.replaceAll('"', ''),
                duration: 10000,
            })
            console.log("Notificación recibida:", event.data.toString());
        });

        eventSourceInstance.onerror = (error) => {
            console.error("Error en la conexión SSE:", error);
        };

        console.log("Nueva conexión SSE establecida.");

        return eventSourceInstance;
    };

    // Cierra la conexión actual (si es necesario)
    const closeConnection = () => {
        if (eventSourceInstance) {
            eventSourceInstance.close();
            eventSourceInstance = null;
            console.log("Conexión SSE cerrada.");
        }
    };

    return {
        getInstance: function (url) {
            return initializeEventSource(url);
        },
        closeConnection,
    };
})();