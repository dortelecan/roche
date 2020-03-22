package org.dortelecan.roche.web;

import org.dortelecan.roche.RocheApplication;
import org.dortelecan.roche.model.Product;
import org.dortelecan.roche.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RocheApplication.class)
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void before() {
        productRepository.deleteAll();
    }

    Product createValidProduct() {
        Product toReturn = new Product();
        toReturn.setName("prod1");
        toReturn.setPriceInPence(10L);
        productRepository.save(toReturn);
        return toReturn;
    }

    @Test
    void createProduct() throws Exception {
        ResultActions toReturn = mvc.perform(
                MockMvcRequestBuilders.post("/product")
                        .content("{  \"name\": \"prod1\",\"priceInPence\": 10}")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("prod1"));
    }

    @Test
    void createProductWithNoProductName() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            mvc.perform(
                    MockMvcRequestBuilders.post("/product")
                            .content("{  \"priceInPence\": 10}")
                            .contentType("application/json"))
                    .andExpect(status().is4xxClientError());
        });
    }

    @Test
    void getProducts() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/product").param("pageSize", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value("true"));
        Product created = createValidProduct();
        mvc.perform(MockMvcRequestBuilders.get("/product").param("pageSize", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value("false"))
                .andExpect(jsonPath("$.content[0].name").value("prod1"));
    }

    @Test
    void updateProduct() throws Exception {
        Product created = createValidProduct();
        mvc.perform(
                MockMvcRequestBuilders.put("/product")
                        .content("{ \"sku\":" + created.getSku() + ", \"name\": \"prod2\",\"priceInPence\": 11,  \"creationDateTime\": \"2020-03-22T11:20:32.329\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("prod2"));
        mvc.perform(MockMvcRequestBuilders.get("/product").param("pageSize", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value("false"))
                .andExpect(jsonPath("$.content[0].name").value("prod2"));
    }

    @Test
    void deleteProduct() throws Exception {
        Product created = createValidProduct();
        mvc.perform(
                MockMvcRequestBuilders.delete("/product/" + created.getSku())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("prod1"))
                .andExpect(jsonPath("$.deleted").value("true"));
        mvc.perform(MockMvcRequestBuilders.get("/product").param("pageSize", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value("true"));
    }
}