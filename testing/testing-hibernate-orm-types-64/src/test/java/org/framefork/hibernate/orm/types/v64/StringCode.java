package org.framefork.hibernate.orm.types.v64;

import java.io.Serializable;

/**
 * An immutable value wrapping a string; each implementation is a distinct type of code mapped by {@link StringCodeType}.
 */
public interface StringCode extends Serializable, Comparable<StringCode>
{

    String value();

    @Override
    default int compareTo(final StringCode other)
    {
        return value().compareTo(other.value());
    }

}
