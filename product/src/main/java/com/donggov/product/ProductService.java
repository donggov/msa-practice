package com.donggov.product;

import com.donggov.domain.Product;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(long id) {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Optional<Product> product = productRepository.findById(id);
        product.orElseThrow(() -> new NoResultException("Not Found product : " + id));
        return product.get();
    }

}
