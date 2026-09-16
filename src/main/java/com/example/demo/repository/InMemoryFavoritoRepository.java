package com.example.demo.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import com.example.demo.domain.Favorito;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFavoritoRepository implements FavoritoRepository {

    private final Map<Long, Favorito> datos = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Favorito> findAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Favorito save(Favorito favorito) {
        if (favorito.id() == null) {
            // es una creacion, se le asigna un nuevo ID
            Long nuevoId = nextId.getAndIncrement();
            Favorito nuevoFavorito = new Favorito(
                    nuevoId,
                    favorito.productoId(),
                    favorito.nota(),
                    favorito.fechaAgregado());
            datos.put(nuevoId, nuevoFavorito);
            return nuevoFavorito;
        } else {
            // es una actualizacion, se reemplaza el valor en el mapa
            datos.put(favorito.id(), favorito);
            return favorito;
        }
    }

    @Override
    public void deleteById(Long id) {
        datos.remove(id);
    }

}
