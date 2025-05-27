package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RepositorioReview extends JpaRepository<Review, Long> {

        List<Review> findReviewsByPatronId(Long id);
}
