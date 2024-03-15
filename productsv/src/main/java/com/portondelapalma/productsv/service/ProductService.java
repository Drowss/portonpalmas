package com.portondelapalma.productsv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Category;
import com.portondelapalma.productsv.model.Product;
import com.portondelapalma.productsv.repository.IProductRepository;
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

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository iProductRepository;

    @Autowired
    private S3Service s3;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Override
    public ProductDto createProduct(MultipartFile multipartFile, String productJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
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
                .map(Product::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getAllByCategory(String category) {
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        ModelMapper modelMapper = new ModelMapper();
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
            ObjectMapper mapper = new ObjectMapper();
            ProductDto productDto = mapper.readValue(productJson, ProductDto.class);
            if (productDto.getNameProduct() != null) {
                product.setNameProduct(productDto.getNameProduct());
            }
            if (productDto.getPrice() != null) {
                product.setPrice(productDto.getPrice());
            }
            if (productDto.getDescription() != null) {
                product.setDescription(productDto.getDescription());
            }
            if (productDto.getStock() != null) {
                product.setStock(productDto.getStock());
            }
            if (productDto.getCategory() != null) {
                product.setCategory(productDto.getCategory());
            }
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
