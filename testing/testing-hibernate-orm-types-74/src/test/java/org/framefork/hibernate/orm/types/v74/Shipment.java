package org.framefork.hibernate.orm.types.v74;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = Shipment.TABLE_NAME)
public class Shipment
{

    public static final String TABLE_NAME = "test_shipment";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    @Column(name = "country")
    @Type(StringCodeType.class)
    private @Nullable CountryCode country;

    @Column(name = "currency")
    @Type(StringCodeType.class)
    private @Nullable CurrencyCode currency;

    protected Shipment()
    {
    }

    public Shipment(@Nullable final CountryCode country, @Nullable final CurrencyCode currency)
    {
        this.country = country;
        this.currency = currency;
    }

    public @Nullable Long getId()
    {
        return id;
    }

    public @Nullable CountryCode getCountry()
    {
        return country;
    }

    public void setCountry(@Nullable final CountryCode country)
    {
        this.country = country;
    }

    public @Nullable CurrencyCode getCurrency()
    {
        return currency;
    }

}
