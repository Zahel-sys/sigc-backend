package com.sigc.backend.adapter.out.persistence;

import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import com.sigc.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaUsuarioAdapter implements IUsuarioRepository {

    private final UsuarioRepository usuarioRepository;

    public JpaUsuarioAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return Optional.ofNullable(usuarioRepository.findByEmail(email)).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email) != null;
    }

    @Override
    public Usuario save(Usuario usuario) {
        com.sigc.backend.model.Usuario entity = toEntity(usuario);
        com.sigc.backend.model.Usuario saved = usuarioRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public long count() {
        return usuarioRepository.count();
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // --- Mapping helpers ---
    private Usuario toDomain(com.sigc.backend.model.Usuario e) {
        if (e == null) return null;
        Usuario u = new Usuario();
        if (e.getIdUsuario() != null) {
            u.setId(e.getIdUsuario());
        }
        u.setEmail(e.getEmail());
        u.setPassword(e.getPassword());
        u.setNombre(e.getNombre());
        // entity doesn't have apellido field; leave null
        u.setApellido(null);
        u.setTelefono(e.getTelefono());
        u.setDni(e.getDni());
        u.setRole(e.getRol());
        u.setActivo(e.isActivo());
        // fechaRegistro -> createdAt
        if (e.getFechaRegistro() != null) {
            u.setCreatedAt(e.getFechaRegistro());
            u.setUpdatedAt(e.getFechaRegistro());
        }
        return u;
    }

    private com.sigc.backend.model.Usuario toEntity(Usuario u) {
        com.sigc.backend.model.Usuario e = new com.sigc.backend.model.Usuario();
        if (u.getId() != null) {
            e.setIdUsuario(u.getId());
        }
        e.setNombre(u.getNombre());
        e.setEmail(u.getEmail());
        e.setPassword(u.getPassword());
        e.setDni(u.getDni());
        e.setTelefono(u.getTelefono());
        e.setRol(u.getRole());
        e.setActivo(u.getActivo() != null ? u.getActivo() : true);
        return e;
    }
}
