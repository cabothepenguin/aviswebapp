package controller;

import dto.VehiculoDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import model.CategoriaVehiculo;
import service.VehiculoService;
import service.CategoriaService;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Named("vehiculoBean")
@ViewScoped
public class VehiculoController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(VehiculoController.class.getName());

    @Inject
    private VehiculoService service;

    @Inject
    private CategoriaService categoriaService;

    private String selectedPlaca;
    private Part uploadedFile;
    private String fileName;
    private byte[] file;

    // Form de creación
    private VehiculoDto newVehiculo = new VehiculoDto();
    // Form de edición
    private VehiculoDto selectedVehiculo = new VehiculoDto();

    // Para listar en la tabla (SIEMPRE usar DTO)
    private List<VehiculoDto> vehiculos = new ArrayList<>();

    // Lista de categorías para el select
    private List<CategoriaVehiculo> categorias = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadVehiculos();
        loadCategorias();
    }



    /* =================== CREATE =================== */
    public void addVehiculo() {
        if (uploadedFile != null && newVehiculo.getImage() == null) {
            upload(); // copia bytes a newVehiculo.image / imageName
        }

        try {
            service.addVehiculo(newVehiculo);
            SuccessMessage("Vehículo adicionado con éxito!");
            LOG.info("=== CONTROLLER: Vehículo agregado ===");
            loadVehiculos();
        } catch (Exception e) {
            LOG.severe("=== ERROR al agregar vehículo === " + e.getMessage());
            e.printStackTrace();
            ErrorMessage("Error al agregar: " + e.getMessage());
        }
    }

    /* =================== READ =================== */
    public void loadVehiculos() {
        try {
            vehiculos = service.listar()
                    .stream()
                    .map(v -> {
                        VehiculoDto dto = new VehiculoDto();
                        dto.setPlaca(v.getPlaca());
                        dto.setModelo(v.getModelo());
                        dto.setMarca(v.getMarca());
                        dto.setEstado(v.getEstado());
                        dto.setAnio(v.getAnio());
                        dto.setPrecio(v.getPrecio());
                        dto.setCategoria(v.getCategoria());
                        dto.setImage(v.getImage());
                        dto.setImageName(v.getImageName());
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            vehiculos = new ArrayList<>();
            error("No se pudieron cargar los vehículos.");
        }
    }

    public Optional<VehiculoDto> findByPlaca(String placa) {
        return service.buscarPorPlaca(placa);
    }

    /* =================== UPDATE =================== */
    public String update() {
        try {
            service.actualizar(selectedVehiculo);
            success("Vehículo actualizado correctamente.");
            loadVehiculos();
            return "/Vehiculos/list-vehiculo.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al actualizar el vehículo.");
        }
        return null;
    }

    /* =================== DELETE =================== */
    public String delete() {
        try {
            service.eliminar(selectedPlaca);
            loadVehiculos(); // refrescar lista
            success("Vehículo eliminado correctamente.");
            return "/Vehiculos/list-vehiculo.xhtml?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el vehículo"));
            return null;
        }
    }

    /* =================== Helpers =================== */
    private void loadCategorias() {
        try {
            categorias = categoriaService.listarCategorias();
            System.out.println("=== CONTROLLER: Cargadas " + categorias.size() + " categorías ===");
        } catch (Exception e) {
            LOG.severe("Error cargando categorías: " + e.getMessage());
            categorias = new ArrayList<>();
            error("No se pudieron cargar las categorías.");
        }
    }

    public void loadVehiculoByPlaca(String placa) {
        selectedVehiculo = service.buscarPorPlaca(placa)
                .orElseGet(() -> {
                    error("No se encontró vehículo con placa: " + placa);
                    return new VehiculoDto();
                });
    }

    public void upload() {
        if (uploadedFile == null) {
            System.out.println("=== CONTROLLER: No se seleccionó archivo ===");
            return;
        }

        // Normalizar nombre del archivo
        String rawName = uploadedFile.getSubmittedFileName();
        String safeName = Paths.get(rawName)
                .getFileName()
                .toString()
                .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        this.fileName = safeName;

        try {
            // Guardar en carpeta accesible públicamente: /resources/images
            String uploadsPath = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRealPath("/resources/images");

            Path uploadDir = Paths.get(uploadsPath);
            Files.createDirectories(uploadDir);

            try (InputStream in = uploadedFile.getInputStream()) {
                byte[] data = in.readAllBytes();
                Path target = uploadDir.resolve(safeName);

                Files.write(target, data,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );

                // Asignar al DTO
                newVehiculo.setImage(data);       // bytes en DB si lo usas
                newVehiculo.setImageName(safeName); // nombre de archivo para mostrar

                System.out.println("=== CONTROLLER: Archivo subido exitosamente: " + safeName + " ===");
            }
        } catch (IOException e) {
            System.out.println("=== CONTROLLER ERROR: Error al subir archivo ===");
            e.printStackTrace();
            error("Error al subir el archivo: " + e.getMessage());
        }
    }



    // VehiculoController.java
// ...
    private String estadoBuscar;              // <--- NUEVO

    public void buscarPorEstado() {
        try {
            if (estadoBuscar == null || estadoBuscar.isBlank()) {
                loadVehiculos(); // todos
            } else {
                vehiculos = service.listarPorEstado(estadoBuscar);
            }
            success("Vehículos cargados.");
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            error("Ocurrió un error al cargar vehículos por estado.");
            vehiculos = new ArrayList<>();
        }
    }


    // getter/setter
    public String getEstadoBuscar() { return estadoBuscar; }
    public void setEstadoBuscar(String estadoBuscar) { this.estadoBuscar = estadoBuscar; }



    private void error(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void success(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void ErrorMessage(String msg) {
        FacesMessage msgF = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }

    private void SuccessMessage(String msg) {
        FacesMessage msgF = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, msgF);
    }

    /* =================== Getters/Setters =================== */
    public VehiculoDto getNewVehiculo() { return newVehiculo; }
    public void setNewVehiculo(VehiculoDto newVehiculo) { this.newVehiculo = newVehiculo; }

    public VehiculoDto getSelectedVehiculo() { return selectedVehiculo; }
    public void setSelectedVehiculo(VehiculoDto selectedVehiculo) { this.selectedVehiculo = selectedVehiculo; }

    public Part getUploadedFile() { return uploadedFile; }
    public void setUploadedFile(Part uploadedFile) { this.uploadedFile = uploadedFile; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public byte[] getFile() { return file; }
    public void setFile(byte[] file) { this.file = file; }

    public String getSelectedPlaca() { return selectedPlaca; }
    public void setSelectedPlaca(String selectedPlaca) { this.selectedPlaca = selectedPlaca; }

    public List<VehiculoDto> getVehiculos() { return vehiculos; }
    public void setVehiculos(List<VehiculoDto> vehiculos) { this.vehiculos = vehiculos; }

    public List<CategoriaVehiculo> getCategorias() { return categorias; }
}
