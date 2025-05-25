package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesDAO.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioReview extends JpaRepository<Review, Long> {

        List<Review> findReviewsByPatronId(Long id);
}
