[1mdiff --git a/productsv/src/main/java/com/portondelapalma/productsv/service/ProductService.java b/productsv/src/main/java/com/portondelapalma/productsv/service/ProductService.java[m
[1mindex a529dc5..813b98c 100644[m
[1m--- a/productsv/src/main/java/com/portondelapalma/productsv/service/ProductService.java[m
[1m+++ b/productsv/src/main/java/com/portondelapalma/productsv/service/ProductService.java[m
[36m@@ -44,7 +44,7 @@[m [mpublic class ProductService implements IProductService {[m
     private final Logger logger = LoggerFactory.getLogger(ProductService.class);[m
 [m
     @Transactional[m
[31m-    public void saveProductORM(MultipartFile file, String productJson) throws JsonProcessingException {[m
[32m+[m[32m    public void saveProductSQL(MultipartFile file, String productJson) throws JsonProcessingException {[m
         ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);[m
         String imagePath = s3.saveFile(file);[m
         productDto.setImagePath(imagePath);[m
[36m@@ -59,7 +59,7 @@[m [mpublic class ProductService implements IProductService {[m
     }[m
 [m
     @Transactional[m
[31m-    public void updateProductORM(Long idProduct, MultipartFile file, String productJson) throws JsonProcessingException, URISyntaxException {[m
[32m+[m[32m    public void updateProductSQL(Long idProduct, MultipartFile file, String productJson) throws JsonProcessingException, URISyntaxException {[m
 [m
         Product product = (Product) entityManager.createNativeQuery("SELECT * FROM product WHERE id_product = ?", Product.class)[m
                 .setParameter(1, idProduct)[m
[36m@@ -73,7 +73,8 @@[m [mpublic class ProductService implements IProductService {[m
             Optional.ofNullable(productDto.getDescription()).ifPresent(product::setDescription);[m
             Optional.ofNullable(productDto.getStock()).ifPresent(product::setStock);[m
             Optional.ofNullable(productDto.getCategory()).ifPresent(product::setCategory);[m
[31m-            entityManager.createNativeQuery("UPDATE product SET name_product = ?, description = ?, price = ?, stock = ?, image_path = ?, category = ? WHERE id_product = ?")[m
[32m+[m[32m            entityManager.createNativeQuery("UPDATE product" +[m
[32m+[m[32m                            " SET name_product = ?, description = ?, price = ?, stock = ?, image_path = ?, category = ? WHERE id_product = ?")[m
                     .setParameter(1, product.getNameProduct())[m
                     .setParameter(2, product.getDescription())[m
                     .setParameter(3, product.getPrice())[m
[36m@@ -104,14 +105,14 @@[m [mpublic class ProductService implements IProductService {[m
     }[m
 [m
     @Transactional[m
[31m-    public void deleteProductORM(Long idProduct) {[m
[32m+[m[32m    public void deleteProductSQL(Long idProduct) {[m
         entityManager.createNativeQuery("DELETE FROM product WHERE id_product = ?")[m
                 .setParameter(1, idProduct)[m
                 .executeUpdate();[m
     }[m
 [m
     @Transactional[m
[31m-    public List<Product> findAllProductORM() {[m
[32m+[m[32m    public List<Product> findAllProductSQL() {[m
         return entityManager.createNativeQuery("SELECT * FROM product", Product.class).getResultList();[m
     }[m
 [m
[1mdiff --git a/user-sv/src/main/java/com/example/usersv/service/UserdataService.java b/user-sv/src/main/java/com/example/usersv/service/UserdataService.java[m
[1mindex cb58384..da0bb85 100644[m
[1m--- a/user-sv/src/main/java/com/example/usersv/service/UserdataService.java[m
[1m+++ b/user-sv/src/main/java/com/example/usersv/service/UserdataService.java[m
[36m@@ -64,6 +64,7 @@[m [mpublic class UserdataService {[m
     @Transactional[m
     public void insertWithQuery(Userdata userdata) {[m
         userdata.setIdCart(iCartAPI.createCart(CartDto.builder().items(new HashMap<>()).total(0L).build()));[m
[32m+[m
         entityManager.createNativeQuery("INSERT INTO userdata (email, name, password, city, region, street_type, street_number, local_apto_number, postal_code, cellphone, dni, role, id_cart) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")[m
                 .setParameter(1, userdata.getEmail())[m
                 .setParameter(2, userdata.getName())[m
