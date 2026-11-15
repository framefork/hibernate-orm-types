package org.framefork.hibernate.orm.types;

import org.hibernate.usertype.DynamicParameterizedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

/**
 * Helpers for the {@link Properties} that Hibernate passes to {@link DynamicParameterizedType#setParameterValues(Properties)}.
 * <p>
 * Hibernate fills these parameters itself when it resolves a type for a mapped attribute. Code that creates a dynamically parameterized type on its own,
 * for example when registering one type instance per Java class in a {@code TypeContributor}, can use {@link #forClass(Class)} to build the parameters
 * Hibernate would have passed, and the type can read its target class back with {@link #getReturnedClass(Properties, Class)} either way.
 */
public final class ParameterizedTypeUtils
{

    private ParameterizedTypeUtils()
    {
    }

    /**
     * Builds parameters for {@link DynamicParameterizedType#setParameterValues(Properties)} whose {@link DynamicParameterizedType#PARAMETER_TYPE}
     * names {@code implClass} as the returned class.
     */
    public static Properties forClass(final Class<?> implClass)
    {
        var parameters = new Properties();
        parameters.put(DynamicParameterizedType.PARAMETER_TYPE, new ParameterizedParameterType(implClass));
        return parameters;
    }

    /**
     * Resolves the Java class a dynamically parameterized type is bound to.
     * <p>
     * Takes the returned class of {@link DynamicParameterizedType#PARAMETER_TYPE} when the parameters have one. Otherwise reads the declared type of the field
     * named by {@link DynamicParameterizedType#PROPERTY} on the class named by {@link DynamicParameterizedType#ENTITY}.
     *
     * @param expectedType the class the resolved class must be assignable to
     * @throws IllegalArgumentException when the resolved class is not assignable to {@code expectedType}, or the entity class or its field does not exist
     * @throws NullPointerException when the parameters have neither a parameter type nor an entity and property name
     */
    @SuppressWarnings("unchecked")
    public static <ExpectedType> Class<ExpectedType> getReturnedClass(final Properties parameters, final Class<?> expectedType)
    {
        var parameterType = (DynamicParameterizedType.ParameterType) parameters.get(DynamicParameterizedType.PARAMETER_TYPE);
        if (parameterType != null) {
            Class<?> actualType = parameterType.getReturnedClass();
            if (!expectedType.isAssignableFrom(actualType)) {
                throw new IllegalArgumentException("Expected %s but found %s".formatted(expectedType, actualType));
            }
            return (Class<ExpectedType>) actualType;

        } else {
            String entityClass = Objects.requireNonNull(parameters.get(DynamicParameterizedType.ENTITY), "parameters.get(ENTITY) must not be null").toString();
            String propertyName = Objects.requireNonNull(parameters.get(DynamicParameterizedType.PROPERTY), "parameters.get(PROPERTY) must not be null").toString();

            return getFieldTypeChecked(entityClass, propertyName, expectedType);
        }
    }

    @SuppressWarnings("unchecked")
    private static <ExpectedType> Class<ExpectedType> getFieldTypeChecked(final String className, final String propertyName, final Class<?> expectedType)
    {
        Class<?> declaredFieldType;
        try {
            Class<?> aClass = Class.forName(className);
            Field declaredField = aClass.getDeclaredField(propertyName);
            declaredFieldType = declaredField.getType();

        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (!expectedType.isAssignableFrom(declaredFieldType)) {
            throw new IllegalArgumentException(String.format("Property %s$%s is expected to be an instance of %s", className, propertyName, expectedType));
        }

        return (Class<ExpectedType>) declaredFieldType;
    }

    /**
     * A {@link DynamicParameterizedType.ParameterType} that knows only its returned class; it has no annotations, and the methods describing the mapped
     * column (catalog, schema, table, columns, lengths, primary key) throw {@link UnsupportedOperationException}.
     */
    public static final class ParameterizedParameterType implements DynamicParameterizedType.ParameterType
    {

        private final Class<?> clazz;

        public ParameterizedParameterType(final Class<?> clazz)
        {
            this.clazz = clazz;
        }

        @Override
        public Class<?> getReturnedClass()
        {
            return clazz;
        }

        @Override
        public Annotation[] getAnnotationsMethod()
        {
            return new Annotation[0];
        }

        @Override
        public String getCatalog()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSchema()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getTable()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPrimaryKey()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getColumns()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long[] getColumnLengths()
        {
            throw new UnsupportedOperationException();
        }

    }

}
