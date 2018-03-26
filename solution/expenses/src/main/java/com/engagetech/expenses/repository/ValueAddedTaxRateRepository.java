package com.engagetech.expenses.repository;

import com.engagetech.expenses.model.ValueAddedTaxRate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

/**
 * Repository interface for CRUD operations on VAT rates data
 */
@Repository
public interface ValueAddedTaxRateRepository extends CrudRepository<ValueAddedTaxRate, Long> {

    @Query("SELECT rate FROM ValueAddedTaxRate rate WHERE rate.enabled = TRUE")
    Iterable<ValueAddedTaxRate> findAllEnabled();

    /**
     * Return the current VAT rate
     * @return "enabled" VAT rate
     */
    default ValueAddedTaxRate findEnabled() {
        // TODO #1 : create exception to handle cases when no ENABLED VAT rate is set
        // TODO #2 : create exception to handle cases when more than one VAT rate is ENABLED
        ValueAddedTaxRate rate = null;

        Iterable<ValueAddedTaxRate> rates = findAllEnabled();
        Iterator<ValueAddedTaxRate> iterator = rates.iterator();

        if (iterator.hasNext()) {
            rate = iterator.next();
        }
        return rate;
    }
}
