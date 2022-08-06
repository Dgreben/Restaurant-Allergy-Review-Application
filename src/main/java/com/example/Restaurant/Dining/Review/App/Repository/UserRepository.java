package com.example.Restaurant.Dining.Review.App.Repository;

import com.example.Restaurant.Dining.Review.App.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
