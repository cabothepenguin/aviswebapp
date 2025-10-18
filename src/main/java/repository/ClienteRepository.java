package repository;

import dto.ClienteDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import model.Cliente;

import java.util.List;

/**
 * Repositorio de clientes (JPA/JPQL) alineado al modelo Cliente.
 * - PK: cedula (String)
 * - Campos: nombre (String), telefono (Integer), correo (String)
 * - Entity name: "administracion_clientes" (definido en @Entity(name=...))
 */
@Named
@ApplicationScoped
public class ClienteRepository {

    @PersistenceContext(unitName = "avisrent-pu")
    private EntityManager em;

    // ---------- CREATE ----------

    /** Inserta un cliente a partir del DTO. */
    @Transactional
    public void addClient(ClienteDto dto) {
        em.persist(toEntity(dto));
    }

    // ---------- READ ----------

    /** Lista todos los clientes (entidades). */
    public List<Cliente> getClients() {
        // OJO: usar el nombre de entidad "administracion_clientes" en JPQL
        return em.createQuery("SELECT c FROM Cliente c", Cliente.class)
                .getResultList();
    }

    /** Busca por cédula (PK). */
    public Cliente findByCedula(String cedula) {
        if (cedula == null) return null;
        return em.find(Cliente.class, cedula.trim());
    }

    /** ¿Existe un cliente por cédula? */
    public boolean existsByCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return false;
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Cliente c WHERE c.cedula = :cedula", Long.class)
                .setParameter("cedula", cedula.trim())
                .getSingleResult();
        return count != null && count > 0;
    }

    /** Alias para cubrir el posible uso 'exisByCedula' desde el service. */
    public boolean exisByCedula(String cedula) {
        return existsByCedula(cedula);
    }

    // ---------- UPDATE ----------

    /** Actualiza un cliente a partir del DTO (requiere existencia). */
    @Transactional
    public void updateClient(ClienteDto dto) {
        if (dto == null || dto.getCedula() == null || dto.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria para actualizar.");
        }
        Cliente actual = em.find(Cliente.class, dto.getCedula().trim());
        if (actual == null) {
            throw new IllegalStateException("No existe cliente con cédula: " + dto.getCedula());
        }
        // Actualiza campos editables
        if (dto.getNombre() != null)   actual.setNombre(safeTrim(dto.getNombre()));
        if (dto.getTelefono() != null) actual.setTelefono(dto.getTelefono()); // Integer
        if (dto.getCorreo() != null)   actual.setCorreo(safeTrim(dto.getCorreo()));

        em.merge(actual);
    }

    // ---------- DELETE ----------

    /** Elimina un cliente por cédula (idempotente). */
    @Transactional
    public void deleteClient(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return;
        Cliente ref = em.find(Cliente.class, cedula.trim());
        if (ref != null) {
            em.remove(ref);
        }
    }

    // ---------- MAPEOS ----------

    /** Convierte un DTO a entidad Cliente. */
    private Cliente toEntity(ClienteDto dto) {
        Cliente c = new Cliente();
        c.setCedula(safeTrim(dto.getCedula()));
        c.setNombre(safeTrim(dto.getNombre()));
        c.setTelefono(dto.getTelefono()); // Integer
        c.setCorreo(safeTrim(dto.getCorreo()));
        return c;
    }

    private String safeTrim(String s) { return s == null ? null : s.trim(); }
}
