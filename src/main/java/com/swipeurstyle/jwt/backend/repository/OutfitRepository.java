package com.swipeurstyle.jwt.backend.repository;

import com.swipeurstyle.jwt.backend.entity.Garment;
import com.swipeurstyle.jwt.backend.entity.Outfit;
import com.swipeurstyle.jwt.backend.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends CrudRepository<Outfit, Long> {
    List<Outfit> findByUser(User user);
    void deleteByTop(Garment top);
    void deleteByBottom(Garment bottom);
    void deleteByShoes(Garment shoes);
}
