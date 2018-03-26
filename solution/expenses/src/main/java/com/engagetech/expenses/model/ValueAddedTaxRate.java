package com.engagetech.expenses.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * VAT Rate entity class
 */
@Entity
@Table(name = "vat_rate")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdOn", "updatedOn"}, allowGetters = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class ValueAddedTaxRate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rateid")
    private Long id;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(
            mappedBy = "rate",
            targetEntity = Expense.class,
            fetch = FetchType.EAGER)
    private List<Expense> expenses;

    @Column(name ="createdOn", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdOn;

    @Column(name ="updatedOn", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedOn;

    /**
     * Default constructor
     */
    public ValueAddedTaxRate() {
        super();
    }

    /**
     * Constructor
     * @param rate VAT rate value
     * @param enabled is it current?
     */
    public ValueAddedTaxRate(Double rate, Boolean enabled) {
        super();
        this.rate = rate;
        this.enabled = enabled;
    }

    /**
     * Return this entity's ID
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Return this VAT rate
     * @return the rate
     */
    public Double getRate() {
        return rate;
    }

    /**
     * Return this VAT rate status
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Return a list of expenses with this rate
     * @return expenses list
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.valueOf(rate);
    }
}
