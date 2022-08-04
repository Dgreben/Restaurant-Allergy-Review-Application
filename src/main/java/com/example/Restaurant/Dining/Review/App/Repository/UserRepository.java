package com.example.Restaurant.Dining.Review.App.Repository;

import com.example.Restaurant.Dining.Review.App.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findByUsername(String username);
}
