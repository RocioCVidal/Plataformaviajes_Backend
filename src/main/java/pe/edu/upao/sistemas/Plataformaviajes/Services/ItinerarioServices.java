package pe.edu.upao.sistemas.Plataformaviajes.Services;

import org.springframework.stereotype.Service;
import pe.edu.upao.sistemas.Plataformaviajes.DTO.PublicacionDTO;
import pe.edu.upao.sistemas.Plataformaviajes.Models.*;
import pe.edu.upao.sistemas.Plataformaviajes.Repository.ItinerarioRepository;
import pe.edu.upao.sistemas.Plataformaviajes.Repository.LugarRepository;
import pe.edu.upao.sistemas.Plataformaviajes.Repository.PaisRepository;
import pe.edu.upao.sistemas.Plataformaviajes.Repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItinerarioServices {

    private final ItinerarioRepository itinerarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final LugarRepository lugarRepository;

    private final PaisRepository paisRepository;

    public ItinerarioServices(ItinerarioRepository itinerarioRepository, UsuarioRepository usuarioRepository, LugarRepository lugarRepository, PaisRepository paisRepository){
        this.itinerarioRepository = itinerarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.lugarRepository = lugarRepository;
        this.paisRepository = paisRepository;
    }

    public Itinerario crearDesdeDTO(PublicacionDTO publicacionDTO){
        Itinerario itinerario = new Itinerario();
        itinerario.setTitulo(publicacionDTO.getTitulo());
        itinerario.setDescripcion(publicacionDTO.getDescripcion());

        Usuario usuario = usuarioRepository.findById(publicacionDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        itinerario.setUsuario(usuario);

        Lugar lugar = lugarRepository.findByNombre(publicacionDTO.getLugar())
                .orElseGet(() -> {
                    Lugar nuevoLugar = new Lugar();
                    nuevoLugar.setNombre(publicacionDTO.getLugar());
                    return lugarRepository.save(nuevoLugar);
                });
        itinerario.setLugar(lugar);

        Pais pais = paisRepository.findByNombreIgnoreCase(publicacionDTO.getNombrePais())
                .orElseThrow(() -> new RuntimeException("Pais no encontrado"));
        itinerario.setPais(pais);

        itinerario.setDuracion(publicacionDTO.getDuracion());
        itinerario.setFechainicio(publicacionDTO.getFechaInicio());
        itinerario.setFechaFin(publicacionDTO.getFechaFin());

        List<Actividad> actividades = publicacionDTO.getActividades().stream()
                .map(descripcion -> new Actividad(descripcion, itinerario))
                .collect(Collectors.toList());
        itinerario.setActividades(actividades);

        return itinerarioRepository.save(itinerario);
    }
}
