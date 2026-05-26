package com.example.erp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.entities.Mesa;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.mappers.MapperMesa;
import com.example.erp.repositories.MesaRepository;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public List<ResponseMesa> obtenerMesas() {

        return mesaRepository.findAll().stream()
                .map(MapperMesa::toDTO)
                .toList();

    }

    public ResponseMesa obtenerMesa(UUID idMesa) {

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));
        
        return MapperMesa.toDTO(mesa);

    }

}
