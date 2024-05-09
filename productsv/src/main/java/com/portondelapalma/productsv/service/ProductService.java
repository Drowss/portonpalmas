package com.portondelapalma.productsv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Category;
import com.portondelapalma.productsv.model.Product;
import com.portondelapalma.productsv.repository.IProductRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ProductService {

    @Autowired
    private IProductRepository iProductRepository;

    @Autowired
    private S3Service s3;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${stripeSecretKey}")
    private String stripeSecretKey;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductDto createProduct(MultipartFile multipartFile, String productJson) throws JsonProcessingException, StripeException {
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

        this.createProductStripe(product);

        iProductRepository.save(product);
        logger.info(productDto.getNameProduct() + " creado correctamente.");
        return productDto;
    }

    public ResponseEntity<String> createProductStripe(Product product) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(product.getNameProduct())
                        .setDescription(product.getDescription())
                        .addImage(product.getImagePath())
                        .build();
        com.stripe.model.Product productStripe = com.stripe.model.Product.create(params);
        createPrice(productStripe.getId(), product);
        return ResponseEntity.ok(productStripe.getId());
    }

    public ResponseEntity<String> createPrice(String productId, Product product) throws StripeException {

        Stripe.apiKey = stripeSecretKey;
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency("cop")
                        .setUnitAmount(product.getPrice() * 100)
                        .setProduct(productId)
                        .build();
        Price price = Price.create(params);
        return ResponseEntity.ok(price.getId());
    }


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

    public List<ProductDto> getAllProducts() {
        logger.info("Se han encontrado " + iProductRepository.findAll().size() + " productos");
        return iProductRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }


    public List<ProductDto> getAllByCategory(String category) {
        Category categoryEnum = Category.valueOf(category.toUpperCase());
        List<Product> products = iProductRepository.getAllByCategory(categoryEnum);
        logger.info("Se han encontrado " + products.size() + " productos de la categoría " + category);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class)) //Each product is mapped as HorseDto
                .toList();
    }


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

    public void deleteImage(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String path = uri.getPath();
        s3.deleteFile(path.substring(path.lastIndexOf('/') + 1));
        logger.info("Imagen eliminada correctamente");
    }

    public ProductDto getById(Long idProduct) {
        Product product = iProductRepository.findById(idProduct).orElseThrow(() -> {
            logger.error("No se pudo encontrar un producto con el ID: " + idProduct);
            return new NoSuchElementException("No se pudo encontrar un producto con el ID: " + idProduct);
        });
        return product.toDto();
    }

    public ProductDto getByName(String nameProduct) {
        return iProductRepository.getByName(nameProduct).toDto();
    }

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
