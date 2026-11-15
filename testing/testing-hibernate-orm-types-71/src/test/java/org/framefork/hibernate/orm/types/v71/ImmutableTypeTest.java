package org.framefork.hibernate.orm.types.v71;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

final class ImmutableTypeTest
{

    private final StringCodeType type = new StringCodeType(CountryCode.class);

    @Test
    void returnedClassComesFromTheParametersPassedToTheJavaType()
    {
        assertThat(type.returnedClass()).isEqualTo(CountryCode.class);
        assertThat(type.getReturnedClass()).isEqualTo(CountryCode.class);
        assertThat(new StringCodeType(CurrencyCode.class).returnedClass()).isEqualTo(CurrencyCode.class);
    }

    @Test
    void equalityAndHashCodeDelegateToTheValues()
    {
        var czech = new CountryCode("CZ");

        assertThat(type.equals(czech, new CountryCode("CZ"))).isTrue();
        assertThat(type.equals(czech, new CountryCode("SK"))).isFalse();
        assertThat(type.equals(czech, new CurrencyCode("CZ"))).isFalse();
        assertThat(type.equals(null, null)).isTrue();
        assertThat(type.equals(czech, null)).isFalse();
        assertThat(type.isSame(czech, new CountryCode("CZ"))).isTrue();
        assertThat(type.isEqual(czech, new CountryCode("SK"))).isFalse();
        assertThat(type.hashCode(czech)).isEqualTo(czech.hashCode());
        assertThat(type.getHashCode(czech)).isEqualTo(czech.hashCode());
    }

    @Test
    void valuesAreNeverCopied()
    {
        var czech = new CountryCode("CZ");

        assertThat(type.isMutable()).isFalse();
        assertThat(type.deepCopy(czech)).isSameAs(czech);
        assertThat(type.replace(czech, new CountryCode("SK"), new Object())).isSameAs(czech);
    }

    @Test
    void disassembledValueSurvivesSerializationAndIsAssembledBack() throws IOException, ClassNotFoundException
    {
        var czech = new CountryCode("CZ");

        Serializable cached = type.disassemble(czech);
        assertThat(cached).isSameAs(czech);

        Serializable deserialized = deserialize(serialize(cached));
        var assembled = type.assemble(deserialized, new Object());
        assertThat(assembled).isSameAs(deserialized).isEqualTo(czech);
    }

    @Test
    void comparisonUsesTheComparatorOfTheJavaType()
    {
        assertThat(type.compare(new CountryCode("CZ"), new CountryCode("SK"))).isNegative();
        assertThat(type.compare(new CountryCode("SK"), new CountryCode("CZ"))).isPositive();
        assertThat(type.compare(new CountryCode("CZ"), new CountryCode("CZ"))).isZero();
    }

    @Test
    void stringFormsComeFromTheJavaTypeAndTheValue()
    {
        assertThat(type.fromStringValue("CZ")).isEqualTo(new CountryCode("CZ"));
        assertThat(type.toSqlLiteral(new CountryCode("CZ"))).isEqualTo("'CZ'");
        assertThat(type.toSqlLiteral(null)).isNull();
        assertThat(type.toString(new CountryCode("CZ"))).isEqualTo("CZ");
        assertThat(type.getName()).isEqualTo(StringCodeType.NAME);
    }

    private static byte[] serialize(final Serializable value) throws IOException
    {
        var bytes = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        return bytes.toByteArray();
    }

    private static Serializable deserialize(final byte[] bytes) throws IOException, ClassNotFoundException
    {
        try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (Serializable) in.readObject();
        }
    }

}
