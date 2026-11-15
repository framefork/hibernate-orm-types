package org.framefork.hibernate.orm.types.v73;

import java.util.Objects;

public record CurrencyCode(String value) implements StringCode
{

    public CurrencyCode
    {
        Objects.requireNonNull(value, "value must not be null");
    }

    @Override
    public String toString()
    {
        return value;
    }

}
