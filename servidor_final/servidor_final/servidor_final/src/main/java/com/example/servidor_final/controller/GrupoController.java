package com.example.servidor_final.controller;

import com.example.servidor_final.ChatWebSocketHandler;
import com.example.servidor_final.model.Grupo;
import com.example.servidor_final.model.NombreGrupoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GrupoController {

    private Map<String, List<String>> grupos = new HashMap<>();
  //  private final List<Grupo> grupos = new ArrayList<>();

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
    private final AtomicLong idCounter = new AtomicLong();

    @GetMapping("/creados")
    public List<Grupo> obtenerGrupos() {
        // Convertir el Map en una lista de objetos Grupo
        return grupos.entrySet().stream()
                .map(entry -> new Grupo(idCounter.incrementAndGet(), entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @PutMapping("/rename")
    public ResponseEntity<String> renombrarGrupo(@RequestBody NombreGrupoRequest request) {
        String nombreAntiguo = request.getNombreActual();
        String nuevoNombre = request.getNuevoNombre();

        if (!grupos.containsKey(nombreAntiguo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El grupo no existe");
        }

        // Eliminar el grupo con el nombre antiguo y añadirlo con el nuevo nombre
        List<String> miembros = grupos.remove(nombreAntiguo);
        grupos.put(nuevoNombre, miembros);

        // Aquí también podrías querer sincronizar los cambios con cualquier otro sistema de WebSocket si fuera necesario.
        chatWebSocketHandler.agregarGrupoAlMapa(nuevoNombre, miembros);

        return ResponseEntity.ok("Grupo renombrado correctamente");
    }

    @PostMapping("/create")
    public Grupo crearGrupo(@RequestBody Grupo grupo) {
        System.out.println("🛠 Creando grupo: " + grupo.getNombreGrupo() + " con miembros: " + grupo.getMiembros());

        if (!grupos.containsKey(grupo.getNombreGrupo())) {
            grupos.put(grupo.getNombreGrupo(), new ArrayList<>(grupo.getMiembros()));
            System.out.println("✅ Grupo creado y almacenado en memoria: " + grupos);
            System.out.println("🔍 Lista de grupos actuales: " + grupos.keySet());

            // Sincronizar el grupo con ChatWebSocketHandler
            chatWebSocketHandler.agregarGrupoAlMapa(grupo.getNombreGrupo(), grupo.getMiembros());

            System.out.println("✅ Grupo sincronizado con WebSocketHandler.");
        } else {
            System.out.println("⚠️ El grupo " + grupo.getNombreGrupo() + " ya existe.");
        }

        return grupo;  // Puedes modificar el constructor si es necesario
    }

    @DeleteMapping("/{nombreGrupo}")
    public ResponseEntity<String> eliminarGrupo(@PathVariable String nombreGrupo) {
        if (grupos.containsKey(nombreGrupo)) {
            grupos.remove(nombreGrupo);
            return ResponseEntity.ok("Grupo eliminado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Grupo no encontrado.");
        }
    }
}
