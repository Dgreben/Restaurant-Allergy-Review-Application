package com.example.Restaurant.Allergy.Review.Application.Repository;

import com.example.Restaurant.Allergy.Review.Application.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
