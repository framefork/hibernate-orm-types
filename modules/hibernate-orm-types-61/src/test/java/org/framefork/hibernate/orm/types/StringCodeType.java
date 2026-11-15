package org.framefork.hibernate.orm.types;

import org.hibernate.type.SqlTypes;

public class StringCodeType extends ImmutableType<StringCode, StringCodeJavaType>
{

    public static final String NAME = "string-code";

    public StringCodeType()
    {
        super(new StringCodeJavaType(), null);
    }

    @SuppressWarnings("this-escape")
    public StringCodeType(final Class<? extends StringCode> codeClass)
    {
        this();
        setParameterValues(ParameterizedTypeUtils.forClass(codeClass));
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public int getSqlType()
    {
        return SqlTypes.VARCHAR;
    }

}
