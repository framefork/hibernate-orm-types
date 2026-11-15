package org.framefork.hibernate.orm.types.v64;

import org.framefork.hibernate.orm.types.ParameterizedTypeUtils;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.usertype.DynamicParameterizedType;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;

/**
 * Maps any {@link StringCode} subclass to a string; the subclass is resolved from the parameters Hibernate passes for the mapped attribute.
 */
@SuppressWarnings({"removal", "unchecked"}) // DynamicParameterizedType is deprecated for removal since Hibernate ORM 7.0
public class StringCodeJavaType implements BasicJavaType<StringCode>, DynamicParameterizedType
{

    private @Nullable Class<StringCode> codeClass;
    private @Nullable Constructor<StringCode> constructor;

    @Override
    public void setParameterValues(final Properties parameters)
    {
        Class<StringCode> resolved = ParameterizedTypeUtils.getReturnedClass(parameters, StringCode.class);
        try {
            this.constructor = resolved.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        this.codeClass = resolved;
    }

    @Override
    public Type getJavaType()
    {
        return getJavaTypeClass();
    }

    @Override
    public Class<StringCode> getJavaTypeClass()
    {
        return Objects.requireNonNull(codeClass, "codeClass was not yet resolved");
    }

    @Override
    public JdbcType getRecommendedJdbcType(final JdbcTypeIndicators indicators)
    {
        return indicators.getTypeConfiguration().getJdbcTypeRegistry().getDescriptor(SqlTypes.VARCHAR);
    }

    @Override
    public MutabilityPlan<StringCode> getMutabilityPlan()
    {
        return ImmutableMutabilityPlan.instance();
    }

    @Override
    public Comparator<StringCode> getComparator()
    {
        return Comparator.naturalOrder();
    }

    @Nullable
    @Override
    public <X> X unwrap(@Nullable final StringCode value, final Class<X> type, @Nullable final WrapperOptions options)
    {
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(String.class)) {
            return (X) value.value();
        }
        throw new IllegalArgumentException("Cannot unwrap %s to %s".formatted(value, type));
    }

    @Nullable
    @Override
    public <X> StringCode wrap(@Nullable final X value, @Nullable final WrapperOptions options)
    {
        if (value == null) {
            return null;
        }
        if (value instanceof String string) {
            return create(string);
        }
        throw new IllegalArgumentException("Cannot wrap %s".formatted(value.getClass()));
    }

    @Nullable
    @Override
    public StringCode fromString(@Nullable final CharSequence string)
    {
        return (string == null) ? null : create(string.toString());
    }

    private StringCode create(final String value)
    {
        try {
            return Objects.requireNonNull(constructor, "constructor was not yet resolved").newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
