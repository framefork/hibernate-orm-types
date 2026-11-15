package org.framefork.hibernate.orm.types.v64;

import jakarta.persistence.EntityManager;
import org.framefork.testing.hibernate.AbstractMySQLIntegrationTest;
import org.hibernate.SessionFactory;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

final class MySQLImmutableTypeTest extends AbstractMySQLIntegrationTest implements ImmutableTypeContract
{

    @Override
    protected Class<?>[] entities()
    {
        return new Class<?>[]{Shipment.class};
    }

    @Override
    protected void additionalProperties(final Properties properties)
    {
        properties.setProperty(STATISTICS_SETTING, "true");
    }

    @Override
    public <T> T returning(final Function<EntityManager, T> work)
    {
        return doInJPA(work);
    }

    @Override
    public void consuming(final Consumer<EntityManager> work)
    {
        doInJPA(work);
    }

    @Override
    public SessionFactory factory()
    {
        return sessionFactory();
    }

}
