package com.example.demo.repository;


import com.example.demo.model.PropertyTransaction;
import com.example.demo.model.PropertyType;
import com.example.demo.model.UserModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import com.example.demo.model.PropertyModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<PropertyModel, Long>{
    List<PropertyModel> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<PropertyModel> findAllByOwnerId(Long ownerId);
    @Query("SELECT p FROM PropertyModel p " +
            "WHERE (:propertyType IS NULL OR p.propertyType = :propertyType) " +
            "AND (:propertyTransaction IS NULL OR p.propertyTransaction = :propertyTransaction) " +
            "AND (:minSurface IS NULL OR p.surface >= :minSurface) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:minRooms IS NULL OR p.rooms >= :minRooms)" +
            "AND (:city IS NULL OR LOWER(p.city) = LOWER(:city))")
    List<PropertyModel> searchProperties(@Param("propertyType") PropertyType propertyType,
                                         @Param("propertyTransaction") PropertyTransaction propertyTransaction,
                                         @Param("minSurface") Long minSurface,
                                         @Param("maxPrice") Long maxPrice,
                                         @Param("minRooms") Long minRooms,
                                         @Param("city") String city);

    List<PropertyModel> findByOwner(UserModel owner);
}
