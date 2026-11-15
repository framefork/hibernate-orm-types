package org.framefork.hibernate.orm.types.v66;

import java.util.Objects;

public record CountryCode(String value) implements StringCode
{

    public CountryCode
    {
        Objects.requireNonNull(value, "value must not be null");
    }

    @Override
    public String toString()
    {
        return value;
    }

}
