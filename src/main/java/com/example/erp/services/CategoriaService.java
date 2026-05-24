package com.example.erp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestCategoria;
import com.example.erp.DTOs.response.ResponseCategoria;
import com.example.erp.entities.Categoria;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.mappers.MapperCategoria;
import com.example.erp.repositories.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly=true)
    public List<ResponseCategoria> obtenerCategorias() {

        return categoriaRepository.findAll().stream()
                .map(MapperCategoria::toDTO)
                .toList();

    }

    public Categoria obtenerCategoria(UUID idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                                                .orElseThrow(() -> new EntidadNoEncontradaException("Categoria no encontrado")); 

        return categoria;

    }

    @Transactional
    public ResponseCategoria insertarCategoria(RequestCategoria requestCategoria){

        Categoria categoria = MapperCategoria.toEntity(requestCategoria);

        categoria = categoriaRepository.save(categoria);

        return MapperCategoria.toDTO(categoria);

    }


}
