package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import com.example.demo.dto.producto.ProductoDTO;

@Service 
public class ProductoService {

    private final DummyJsonClient dummyJsonClient;

    public ProductoService(DummyJsonClient dummyJsonClient){
        this.dummyJsonClient = dummyJsonClient;
    }

    //metodos principales
    public ProductoDTO obtenerPorId(Long id) {
        DummyJsonProducto respuesta = dummyJsonClient.obtenerPorId(id);
        return mapearADto(respuesta);
    }

    public List<ProductoDTO> obtenerTodos(){
        DummyJsonProductosResponse respuesta = dummyJsonClient.obtenerTodos();
    
        List<ProductoDTO> productosDto = new ArrayList<>();

        for (DummyJsonProducto p: respuesta.products()) {
            ProductoDTO dto = mapearADto(p);
            productosDto.add(dto);
        }

        return productosDto;
    }

    //metodo auxiliar
    private ProductoDTO mapearADto(DummyJsonProducto p) {
        return new ProductoDTO(
            p.id(),
            p.title(),
            p.description(),
            p.category(),
            p.stock(),
            p.price()
        );
    }
}
