package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.TipoDocumento;
import com.empresa.maestra_dyd_boot.repository.TipoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoService(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public List<TipoDocumento> listarTodos() {
        return tipoDocumentoRepository.findAll();
    }

    public TipoDocumento buscarPorId(Long id) {
        return tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado: " + id));
    }

    public TipoDocumento guardar(TipoDocumento tipoDocumento) {
        return tipoDocumentoRepository.save(tipoDocumento);
    }

    public void eliminar(Long id) {
        tipoDocumentoRepository.deleteById(id);
    }
}