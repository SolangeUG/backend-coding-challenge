package com.engagetech.expenses.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Expense entity class
 */
@Entity
@Table(name = "expenses")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdOn", "updatedOn"}, allowGetters = true)
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
public class Expense implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expid")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rateid", nullable = false)
    private ValueAddedTaxRate rate;

    @Column(name = "expdate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "expvalue", nullable = false)
    private Double value;

    @Column(name = "expreason", nullable = false)
    private String reason;

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
    public Expense() {
        super();
    }

    /**
     * Constructor
     * @param date the expense date
     * @param value the expense value
     * @param reason the reason for the expense
     */
    public Expense(Date date, Double value, String reason) {
        super();
        this.date = date;
        this.value = value;
        this.reason = reason;
    }

    /**
     * Return this entity's ID
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Return this expense VAT rate
     * @return VAT rate
     */
    public ValueAddedTaxRate getRate() {
        return rate;
    }

    /**
     * Set current VAT rate
     * @param current VAT rate
     */
    public void setRate(ValueAddedTaxRate current) {
        this.rate = current;
    }

    /**
     * Return the date of the expense
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set date for expense
     * @param date expense date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the value of the expense
     * @return value
     */
    public Double getValue() {
        return value;
    }

    /**
     * Set expense value
     * @param value expense value
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Return the reason of the expense
     * @return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Set reason for this expense
     * @param reason expense reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Return this entity's creation date
     * @return creation date
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * Return this entity's update date
     * @return update date
     */
    public Date getUpdatedOn() {
        return updatedOn;
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
        return "{" +
                " id: " + id +
                " rate: " + rate +
                " date: " + date +
                " value: " + value +
                " reason: " + reason +
                "}";
    }
}
