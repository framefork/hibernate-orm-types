package org.framefork.hibernate.orm.types;

import org.hibernate.usertype.DynamicParameterizedType;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("removal") // DynamicParameterizedType is deprecated for removal since Hibernate ORM 7.0
final class ParameterizedTypeUtilsTest
{

    @Test
    void returnedClassIsReadFromTheParameterTypeBuiltByForClass()
    {
        var parameters = ParameterizedTypeUtils.forClass(CountryCode.class);

        Class<StringCode> returnedClass = ParameterizedTypeUtils.getReturnedClass(parameters, StringCode.class);
        assertThat(returnedClass).isEqualTo(CountryCode.class);
    }

    @Test
    void parameterTypeOfAnotherClassIsRejected()
    {
        var parameters = ParameterizedTypeUtils.forClass(String.class);

        assertThatThrownBy(() -> ParameterizedTypeUtils.getReturnedClass(parameters, StringCode.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(String.class.getName());
    }

    @Test
    void withoutParameterTypeTheReturnedClassIsTheDeclaredTypeOfTheEntityField()
    {
        Class<StringCode> returnedClass = ParameterizedTypeUtils.getReturnedClass(entityProperty(Shipment.class.getName(), "currency"), StringCode.class);
        assertThat(returnedClass).isEqualTo(CurrencyCode.class);
    }

    @Test
    void entityFieldOfAnotherTypeIsRejected()
    {
        assertThatThrownBy(() -> ParameterizedTypeUtils.getReturnedClass(entityProperty(Shipment.class.getName(), "id"), StringCode.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id");
    }

    @Test
    void missingEntityClassOrFieldIsRejected()
    {
        assertThatThrownBy(() -> ParameterizedTypeUtils.getReturnedClass(entityProperty(Shipment.class.getName(), "missing"), StringCode.class))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ParameterizedTypeUtils.getReturnedClass(entityProperty("com.example.Missing", "country"), StringCode.class))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parametersWithoutParameterTypeOrEntityAreRejected()
    {
        assertThatThrownBy(() -> ParameterizedTypeUtils.getReturnedClass(new Properties(), StringCode.class))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void parameterTypeKnowsOnlyItsReturnedClass()
    {
        var parameterType = new ParameterizedTypeUtils.ParameterizedParameterType(CountryCode.class);

        assertThat(parameterType.getReturnedClass()).isEqualTo(CountryCode.class);
        assertThat(parameterType.getAnnotationsMethod()).isEmpty();
        assertThatThrownBy(parameterType::getTable).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(parameterType::getColumns).isInstanceOf(UnsupportedOperationException.class);
    }

    private static Properties entityProperty(final String entityClass, final String propertyName)
    {
        var parameters = new Properties();
        parameters.setProperty(DynamicParameterizedType.ENTITY, entityClass);
        parameters.setProperty(DynamicParameterizedType.PROPERTY, propertyName);
        return parameters;
    }

}
