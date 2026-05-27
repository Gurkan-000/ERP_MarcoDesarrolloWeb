package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestCategoria;
import com.example.erp.DTOs.response.ResponseCategoria;
import com.example.erp.entities.Categoria;

public class MapperCategoria {

    // Si no se usara @Builder en ResponseCategoria lo que se haria seria
    // public static ResponseCategoria toDTO(Categoria categoria) {
    //     
    //     ResponseCategoria responseCategoria = new ResponseCategoria(categoria.getIdCategoria(), categoria.getNombre()); 
    //     return responseCategoria        
    //             
    // }

    public static ResponseCategoria toDTO(Categoria categoria) {
        return ResponseCategoria.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .productos(categoria.getProductos().size())
                .build();
    }

    public static Categoria toEntity(RequestCategoria requestCategoria) {
        return Categoria.builder()
                .nombre(requestCategoria.getNombre())
                .build();
    }

}
