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

    List<Outfit> findByTop(Garment garment);
    List<Outfit> findByBottom(Garment garment);
    List<Outfit> findByShoes(Garment garment);
}
