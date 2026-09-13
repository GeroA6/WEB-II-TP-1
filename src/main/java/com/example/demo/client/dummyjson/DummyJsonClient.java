package com.example.demo.client.dummyjson;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.exception.ServicioExternoException;

@Component 
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient restClient){
        this.restClient = restClient;
    }

    public DummyJsonProductosResponse obtenerTodos() {
        try {
            return restClient.get()
                .uri("/products")
                .retrieve()
                .body(DummyJsonProductosResponse.class);     
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al consultar el catalogo en DummyJSON", e);
        }
    }

    public DummyJsonProducto obtenerPorId(Long id) {
        try {
            return restClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(DummyJsonProducto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado con id:" + id);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al consultar producto en DummyJSON", e);
        }
    }

}
