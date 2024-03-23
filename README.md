## Description

This is a microservices-based web application that allows for the registration of horses and products. However, only products can be sold through the application. The application is divided into several services:

- `product_service`: Handles all operations related to products.
- `horse_service`: Handles all operations related to horse registration.
- `cart_service`: Manages the shopping cart functionality.
- `user_service`: Manages user registration and authentication.
- `sale_service`: Handles all operations related to sales.

The application also includes the following infrastructure services:

- `API Gateway`: Serves as the single entry point for all client requests. It routes requests to the appropriate microservice
- `Eureka Server`: Provides service discovery to help microservices find each other without hardcoding hostname and port.
- `Config Server`: Provides centralized configuration management for the microservices.

**IMPORTANT**: The application uses Amazon S3 for cloud storage. Images related to products and horses are stored in S3 buckets.

# Environment Variables <a name="environment-variables"></a>

The application uses environment variables for configuration. These are stored in a `.env` file at the root of the project. You should create your own `.env` file and set the following variables:

```properties
DATABASE_PASSWORD=your_database_password
access=your_access_key
secret=your_secret_key
HORSES_BUCKETNAME=your_horses_bucket_name
PRODUCTS_BUCKETNAME=your_products_bucket_name
region=your_region
gitpassword=your_git_token
expiration=your_expiration_time
secretKey=your_secret_key
```

# How to Run


This application is containerized using Docker, and can be easily run on any system that has Docker installed.

Follow these steps to run the application:

1. **Environment Variables**: Create a `.env` file at the root of the project and set the [environment variables](#environment-variables) as described above.

2. **Install Docker**: If you haven't already, first install Docker on your machine. You can download Docker from [here](https://www.docker.com/products/docker-desktop).

3. **Run the Docker script**: Run `docker-init.sh` using bash to build the Docker images and start the containers.

# Testing
I recommend using `Postman` to test the endpoints.

The gateway is exposed on port `443`

#### These endpoints are the essencial ones, there are more endpoints that are not listed here, but you can find them in the code.

# User Service
| Endpoint              | HTTP Method | Description                                                      | Request Body                                                                                                                                                                                      | Access                 |
|-----------------------|-------------|------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------|
| `/user/auth/register` | POST        | Endpoint for signing up to the application                       | `{`<br/>`"email": "myEmail@git.com",`<br/>` "password": "1234",` <br/>`"address": "Antioquia, Medellín Calle X #00-00",`<br/>`"cellphone": "30029121",` <br/> `"dni": "your_identification_#"`<br/>`}` | Endpoint for all users          |
| `/user/auth/login`    | POST        | Endpoint for logging in, it returns a token as cookie            | `{}`                                                                                                                                                                                              | Endpoint for all users |
| `/user/auth/logout`   | POST        | Endpoint for logging out, it removes token from the cookie given | `{}`                                                                                                                                                                                              | Endpoint for all users |

# Horse Service
| Endpoint           | HTTP Method                    | Description                                      | Request Body                                                                                                                                                                                        | Access                         |
|--------------------|--------------------------------|--------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------|
| `/horse/v1/upload` | POST<br/>`Multipart/form-data` | Endpoint for admins to upload horse information. | `{`<br/>`"breed": "Arabian",`<br/>` "description": "Arabian horse,` <br/>`"price": 5000,`<br/>`"bornOn": "2015-01-01,"` <br/> `"imagePath": Its generated when it receives a MultipartFile`<br/>`}` | Admin (verified from a cookie) |
| `/horse/v1`        | GET                            | Endpoint for getting all horses available        | `{}`                                                                                                                                                                                                  | Endpoint for all users         |

# Product Service
| Endpoint             | HTTP Method                    | Description                                        | Request Body                                                                                                                                                                                                                                                           | Access                         |
|----------------------|--------------------------------|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------|
| `/product/v1/upload` | POST<br/>`Multipart/form-data` | Endpoint for admins to upload product information. | `{`<br/>`"nameProduct": "Leash",`<br/>` "description": "Leash for horses,` <br/>`"price": 2300,`<br/>`"stock": 20,` <br/> `"imagePath": Its generated when it receives a MultipartFile`<br/>`"category": This is an ENUM class (UTILIDADES, ALIMENTACION, CUIDADOS)`<br/>`}` | Admin (verified from a cookie) |
| `/product/v1`        | GET                            | Endpoint for getting all products available        | `{}`                                                                                                                                                                                                                                                                   | Endpoint for all users         |

# Cart Service
| Endpoint                                     | HTTP Method | Description                                                              | Request Body                                                                                                                                                                                                                                                          |
|----------------------------------------------|-------------|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/cart/v1/add-product?idProduct=(id)`        | PUT         | Endpoint for getting cart from the cookie, it adds item to the cart      | `{}` | Admin (verified from a cookie) |
| `/cart/v1/delete-product?nameProduct=(name)` | PUT         | Endpoint for getting cart from the cookie, it removes item from the cart | `{}`                                                                                                                                                                                                                                                                  |

# Sale Service
| Endpoint              | HTTP Method | Description                                                                                                       | Request Body | Response Body                                                                                                   |
|-----------------------|-------------|-------------------------------------------------------------------------------------------------------------------|-------------|-----------------------------------------------------------------------------------------------------------------|
| `/sale/v1/successful` | POST        | Endpoint for buying products taking the cart from the cookie and update stock on the products that have been sold | `{}`        | `{`<br/>`date: date,`<br/>`total: total`<br/>`email: email,`<br/>`dni: dni,`<br/>`products: [products]`<br/>`}` |

