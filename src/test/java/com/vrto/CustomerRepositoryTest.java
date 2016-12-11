package com.vrto;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repository;

    @Test
    public void timestampsAreAutomaticallySet_WhenSavingSingleEntity() {
        // not setting timestamps manually
        val saved = repository.save(new Customer("Test Customer"));

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    public void timestampsAreAutomaticallySet_WhenSavingMultiplEntities() {
        val saved = repository.save(Arrays.asList(new Customer("Customer 1"), new Customer("Customer 2")));

        saved.forEach(cus -> {
            assertThat(cus.getCreatedAt()).isNotNull();
            assertThat(cus.getUpdatedAt()).isNotNull();
        });
    }
}