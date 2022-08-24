package com.example.Restaurant.Allergy.Review.Application.Repository;

import com.example.Restaurant.Allergy.Review.Application.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
