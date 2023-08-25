package com.apiconsumerservices;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController("/")
@Slf4j
public class ProductController {
//    @GetMapping("slow-service-products")
//    private List<Product> getProducts() throws InterruptedException {
//        Thread.sleep(2000L);
//        return Arrays.asList(
//                new Product("yam", "arita"),
//                new Product("To the galos", "gal"),
//                new Product("North London forever", "martin")
//        );
//    }


//    @GetMapping("product-blocking")
//    public ResponseEntity<List<Product>> getProductBlocking() {
//        log.info("entering blocking controller");
//        final String uri = "https://localhost:2302/slow-service-products";
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Product> response = restTemplate.exchange(uri, HttpMethod.GET,
//                null, Product.class);
//        Product product = response.getBody();
//        log.info(" "+product);
//        assert product != null;
//        List<Product> result = product.getProduct();
//        log.info("exiting blocking controller");
//        log.info("The result " + result);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

//    @GetMapping("product-blocking")
//    public ResponseEntity<List<Product>> getProductBlocking() {
//        log.info("entering blocking controller");
//        final String url = "https://dummyjson.com/todos";
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Product> response = restTemplate.exchange(
//                url, HttpMethod.GET, null, Product.class);
//        Product product = response.getBody();
//        assert product != null;
//        log.info(" "+product);
//        List<Product> products = product.getProducts();
//        log.info("exiting blocking controller");
//        log.info("The result " + products);
//        return new ResponseEntity<>(products, HttpStatus.OK);
//    }


    @GetMapping("product-unblocking")
    public Flux<Product> getUnblockingProduct() {
        log.info("entering unblocking controller");
        final String uri = "https://dummyjson.com";
        Flux<Product> productFlux = WebClient.create()
                .get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class);

        productFlux.subscribe(product -> log.info(product.toString()));
        log.info("leaving unblocking controller");
        return productFlux;
    }
    // Extracts a paginated list of products based on the requested page -Rest Template
    @GetMapping("get-products/{id}")
    public List<Product> products(@PathVariable Long id) throws org.apache.tomcat.util.json.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity(httpHeaders);
        ResponseEntity<String> product = restTemplate.exchange("https://dummyjson.com/products", HttpMethod.GET,entity, String.class);
        List<Product> productList = new ObjectMapper().convertValue(new JSONParser(product.getBody())
                .object().get("products"), new TypeReference<List<Product>>() {});
        //For Rest Template, return the product here and map to the object class

        //Pagination starts here
        PagedListHolder<Product> pagedListHolder = new PagedListHolder<>(productList);
        pagedListHolder.setPageSize(5);
        pagedListHolder.setPage(Math.toIntExact(id));
        pagedListHolder.setMaxLinkedPages(productList.size());
        return pagedListHolder.getPageList();
    }

    @GetMapping("get-one-product/{id}")
    public Mono<Product> productMono(@PathVariable Long id) {
        return WebClient.create("https://dummyjson.com/products/" +id).get().accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Product.class);
    }
}
