package com.philips.shoppingcart.repositories;

import com.philips.shoppingcart.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
