package com.engagetech.expenses.repository;

import com.engagetech.expenses.model.ValueAddedTaxRate;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * ValueAddedTaxRate Repository Unit Tests
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class ValueAddedTaxRateRepositoryTests {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private ValueAddedTaxRateRepository repository;

    @Test
    @DisplayName("Should add a VAT rate with field enabled set to FALSE")
    public void shouldCreateVATRate() {
        ValueAddedTaxRate rate = new ValueAddedTaxRate(4.4, false);
        manager.persist(rate);
        manager.flush();

        ValueAddedTaxRate entity = repository.findEnabled();
        assertNotNull(entity);
        assertTrue(entity.getEnabled());
        assertNotEquals(4.4, entity.getRate());
    }

    @Test
    @DisplayName("Should retrieve only one enabled VAT rate")
    public void shouldReturnOneEnabledVATRate() {
        // by default, there is one "enabled" entry in the vat_rate table
        // this test should be able to retrieve that entity
        ValueAddedTaxRate entity = repository.findEnabled();
        assertNotNull(entity);
        assertTrue(entity.getEnabled());
        assertNotNull(entity.getRate());
    }
}