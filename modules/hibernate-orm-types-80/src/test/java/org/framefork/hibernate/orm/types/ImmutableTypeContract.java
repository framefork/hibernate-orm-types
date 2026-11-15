package org.framefork.hibernate.orm.types;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The checks every per-database test class runs against {@link StringCodeType}, a custom type built on {@code ImmutableType}.
 */
interface ImmutableTypeContract
{

    String STATISTICS_SETTING = "hibernate.generate_statistics";

    <T> T returning(Function<EntityManager, T> work);

    void consuming(Consumer<EntityManager> work);

    SessionFactory factory();

    @Test
    default void valueIsReadBackAsTheMappedClass()
    {
        long id = persist(new Shipment(new CountryCode("CZ"), new CurrencyCode("CZK")));

        consuming(em -> {
            var shipment = em.find(Shipment.class, id);
            assertThat(shipment.getCountry()).isEqualTo(new CountryCode("CZ"));
            assertThat(shipment.getCurrency()).isEqualTo(new CurrencyCode("CZK"));
        });
    }

    @Test
    default void valueIsStoredAsItsString()
    {
        persist(new Shipment(new CountryCode("CZ"), new CurrencyCode("CZK")));

        assertThat(queryColumn("SELECT country FROM " + Shipment.TABLE_NAME)).containsExactly("CZ");
    }

    @Test
    default void nullIsStoredAsSqlNull()
    {
        long id = persist(new Shipment(null, null));

        consuming(em -> {
            var shipment = em.find(Shipment.class, id);
            assertThat(shipment.getCountry()).isNull();
            assertThat(shipment.getCurrency()).isNull();
        });
        assertThat(queryColumn("SELECT COUNT(*) FROM " + Shipment.TABLE_NAME + " WHERE country IS NULL")).containsExactly("1");
    }

    @Test
    default void valueIsBoundAsQueryParameter()
    {
        persist(new Shipment(new CountryCode("CZ"), new CurrencyCode("CZK")));
        long slovakId = persist(new Shipment(new CountryCode("SK"), new CurrencyCode("EUR")));

        List<Long> found = returning(em -> em.createQuery("SELECT s.id FROM Shipment s WHERE s.country = :country", Long.class)
            .setParameter("country", new CountryCode("SK"))
            .getResultList());
        assertThat(found).containsExactly(slovakId);
    }

    @Test
    default void equalValueDoesNotMakeTheEntityDirty()
    {
        long id = persist(new Shipment(new CountryCode("CZ"), new CurrencyCode("CZK")));
        var statistics = factory().getStatistics();
        statistics.clear();

        consuming(em -> em.find(Shipment.class, id).setCountry(new CountryCode("CZ")));
        assertThat(statistics.getEntityUpdateCount()).isZero();

        consuming(em -> em.find(Shipment.class, id).setCountry(new CountryCode("SK")));
        assertThat(statistics.getEntityUpdateCount()).isEqualTo(1L);
        consuming(em -> assertThat(em.find(Shipment.class, id).getCountry()).isEqualTo(new CountryCode("SK")));
    }

    @Test
    default void jdbcTypeIsResolvedFromTheTypeConfiguration()
    {
        var type = new StringCodeType(CountryCode.class);
        type.setTypeConfiguration(factory().unwrap(SessionFactoryImplementor.class).getTypeConfiguration());

        assertThat(type.getJdbcType().getDefaultSqlTypeCode()).isEqualTo(SqlTypes.VARCHAR);
    }

    /**
     * Reads the first column of every row as a string, through plain JDBC so that no Hibernate type is involved.
     */
    private List<@Nullable String> queryColumn(final String sql)
    {
        return returning(em -> em.unwrap(Session.class).doReturningWork(connection -> {
            var values = new ArrayList<@Nullable String>();
            try (var statement = connection.createStatement(); var resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    values.add(resultSet.getString(1));
                }
            }
            return values;
        }));
    }

    private long persist(final Shipment shipment)
    {
        return returning(em -> {
            em.persist(shipment);
            return Objects.requireNonNull(shipment.getId());
        });
    }

}
