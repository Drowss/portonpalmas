package com.portondelapalma.productsv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Category;
import com.portondelapalma.productsv.model.Product;
import com.portondelapalma.productsv.repository.IProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository iProductRepository;

    @Autowired
    private S3Service s3;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Transactional
    public void saveProductORM(MultipartFile file, String productJson) throws JsonProcessingException {
        ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);
        String imagePath = s3.saveFile(file);
        productDto.setImagePath(imagePath);
        entityManager.createNativeQuery("INSERT INTO Product (name_product, description, price, stock, image_path, category) VALUES (?, ?, ?, ?, ?, ?)")
                .setParameter(1, productDto.getNameProduct())
                .setParameter(2, productDto.getDescription())
                .setParameter(3, productDto.getPrice())
                .setParameter(4, productDto.getStock())
                .setParameter(5, productDto.getImagePath())
                .setParameter(6, productDto.getCategory())
                .executeUpdate();
    }

    @Transactional
    public void updateProductORM(Long idProduct, MultipartFile file, String productJson) throws JsonProcessingException, URISyntaxException {

        Product product = (Product) entityManager.createNativeQuery("SELECT * FROM product WHERE id_product = ?", Product.class)
                .setParameter(1, idProduct)
                .getSingleResult();

        if (productJson != null) {
            Product productDto = objectMapper.readValue(productJson, Product.class);

            Optional.ofNullable(productDto.getNameProduct()).ifPresent(product::setNameProduct);
            Optional.ofNullable(productDto.getPrice()).ifPresent(product::setPrice);
            Optional.ofNullable(productDto.getDescription()).ifPresent(product::setDescription);
            Optional.ofNullable(productDto.getStock()).ifPresent(product::setStock);
            Optional.ofNullable(productDto.getCategory()).ifPresent(product::setCategory);
            entityManager.createNativeQuery("UPDATE product SET name_product = ?, description = ?, price = ?, stock = ?, image_path = ?, category = ? WHERE id_product = ?")
                    .setParameter(1, product.getNameProduct())
                    .setParameter(2, product.getDescription())
                    .setParameter(3, product.getPrice())
                    .setParameter(4, product.getStock())
                    .setParameter(5, product.getImagePath())
                    .setParameter(6, product.getCategory())
                    .setParameter(7, idProduct)
                    .executeUpdate();


        }

        if (file != null) {
            String imagePath = s3.saveFile(file);
            deleteImage(product.getImagePath());
            entityManager.createNativeQuery("UPDATE product SET image_path = ? WHERE id_product = ?")
                    .setParameter(1, imagePath)
                    .setParameter(2, idProduct)
                    .executeUpdate();
        }
    }

    @Override
    public Product findProductByIdProductQuery(Long idProduct) {
        return (Product) entityManager.createNativeQuery("SELECT * FROM product WHERE id_product = ?", Product.class)
                .setParameter(1, idProduct)
                .getSingleResult();
    }

    @Transactional
    public void deleteProductORM(Long idProduct) {
        entityManager.createNativeQuery("DELETE FROM product WHERE id_product = ?")
                .setParameter(1, idProduct)
                .executeUpdate();
    }

    @Transactional
    public List<Product> findAllProductORM() {
        return entityManager.createNativeQuery("SELECT * FROM product", Product.class).getResultList();
    }

    @Override
    public ProductDto createProduct(MultipartFile multipartFile, String productJson) throws JsonProcessingException {
        ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);
        String imagePath = s3.saveFile(multipartFile);
        productDto.setImagePath(imagePath);

        Product product = Product.builder()
                .imagePath(imagePath)
                .nameProduct(productDto.getNameProduct())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .stock(productDto.getStock())
                .category(productDto.getCategory())
                .build();

        iProductRepository.save(product);
        logger.info(productDto.getNameProduct() + " creado correctamente.");
        return productDto;
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long idProduct) throws MalformedURLException, URISyntaxException {
        if (idProduct == null || idProduct < 0) {
            logger.error("ID de producto inválido: " + idProduct);
            throw new IllegalArgumentException("ID de producto debe ser positivo: " + idProduct);
        }

        Product product = iProductRepository.findById(idProduct).orElseThrow(() -> {
            logger.error("No se pudo encontrar un producto con el ID: " + idProduct);
            return new NoSuchElementException("No se pudo encontrar un producto con el ID: " + idProduct);
        });

        deleteImage(product.getImagePath());
        iProductRepository.deleteById(idProduct);
        logger.info("Se ha eliminado la entidad correctamente");
        return ResponseEntity.ok("Se ha eliminado la entidad correctamente");
    }

    @Override
    public List<ProductDto> getAllProducts() {
        logger.info("Se han encontrado " + iProductRepository.findAll().size() + " productos");
        return iProductRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public List<ProductDto> getAllByCategory(String category) {
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        List<Product> products = iProductRepository.getAllByCategory(categoryEnum);
        logger.info("Se han encontrado " + products.size() + " productos de la categoría " + category);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class)) //Each product is mapped as HorseDto
                .toList();
    }

    @Override
    public Product putProduct(Long id, MultipartFile multipartFile, String productJson) throws URISyntaxException, JsonProcessingException {
        Product product = iProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El producto no fue encontrado " + id));

        if (productJson != null) {
            ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);

            Optional.ofNullable(productDto.getNameProduct()).ifPresent(product::setNameProduct);
            Optional.ofNullable(productDto.getPrice()).ifPresent(product::setPrice);
            Optional.ofNullable(productDto.getDescription()).ifPresent(product::setDescription);
            Optional.ofNullable(productDto.getStock()).ifPresent(product::setStock);
            Optional.ofNullable(productDto.getCategory()).ifPresent(product::setCategory);
        }

        if (multipartFile != null) {
            String imagePath = s3.saveFile(multipartFile);
            deleteImage(product.getImagePath());
            product.setImagePath(imagePath);
        }

        logger.info("Producto actualizado correctamente");
        return iProductRepository.save(product);
    }

    @Override
    public void deleteImage(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String path = uri.getPath();
        s3.deleteFile(path.substring(path.lastIndexOf('/') + 1));
        logger.info("Imagen eliminada correctamente");
    }

    @Override
    public ProductDto getById(Long idProduct) {
        Product product = iProductRepository.findById(idProduct).orElseThrow(() -> {
            logger.error("No se pudo encontrar un producto con el ID: " + idProduct);
            return new NoSuchElementException("No se pudo encontrar un producto con el ID: " + idProduct);
        });
        return product.toDto();
    }

    @Override
    public ProductDto getByName(String nameProduct) {
        return iProductRepository.getByName(nameProduct).toDto();
    }

    @Override
    public void modifyStock(Long idProduct, Integer stock) {
        Product product = iProductRepository.findById(idProduct).orElseThrow(() -> {
            logger.error("No se pudo encontrar un producto con el ID: " + idProduct);
            return new NoSuchElementException("No se pudo encontrar un producto con el ID: " + idProduct);
        });

        product.setStock(stock);
        iProductRepository.save(product);
        logger.info("Stock modificado correctamente");
    }
}
