# Hibernate ORM types

Scaffolding for writing custom Hibernate ORM types, for Hibernate ORM 6.1 through 8.0.

It currently contains two classes, in the package `org.framefork.hibernate.orm.types`:

- `ImmutableType<T, JavaTypeType>` is a base class for a custom type of immutable values. It implements `UserType` together with the type contracts Hibernate uses internally, so one instance works as a registered user type, as the type of a mapped attribute and as the type of a query parameter. Values are bound and extracted by a `JdbcType` through the `JavaType` you pass to the constructor. Hibernate never copies the values, so the dirty check compares them with `equals`, and caching stores the value itself.
- `ParameterizedTypeUtils` builds and reads the parameters Hibernate passes to `DynamicParameterizedType.setParameterValues`. A type that maps a whole family of classes can resolve the concrete class of the mapped attribute with `getReturnedClass(parameters, expectedType)`, and code that creates such a type itself can build the same parameters with `forClass(implClass)`.

## Installation

Pick the module matching the Hibernate ORM line your code runs on.

| Hibernate ORM      | Tested on                    | Artifact                                                                                                         |
|--------------------|------------------------------|------------------------------------------------------------------------------------------------------------------|
| 8.0                | 8.0.0.CR1                    | [org.framefork:hibernate-orm-types-80](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-80) |
| 7.2, 7.3, 7.4      | 7.2.25, 7.3.13, 7.4.16       | [org.framefork:hibernate-orm-types-72](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-72) |
| 7.0, 7.1           | 7.0.10, 7.1.36               | [org.framefork:hibernate-orm-types-70](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-70) |
| 6.3, 6.4, 6.5, 6.6 | 6.3.2, 6.4.10, 6.5.3, 6.6.59 | [org.framefork:hibernate-orm-types-63](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-63) |
| 6.2                | 6.2.52                       | [org.framefork:hibernate-orm-types-62](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-62) |
| 6.1                | 6.1.7                        | [org.framefork:hibernate-orm-types-61](https://central.sonatype.com/artifact/org.framefork/hibernate-orm-types-61) |

The modules do not bring `hibernate-core`: they compile against the first release of their line and use whichever `hibernate-core` version your application has.
Every Hibernate ORM minor a module covers is tested at its newest patch, listed in the table, on PostgreSQL and MySQL.
`hibernate-orm-types-80` is built and tested against Hibernate ORM `8.0.0.CR1`; Hibernate ORM 8.0 is not GA yet.

Minimum supported Java is 17.

## Usage

A type for a family of string-backed value classes, each mapped to a `VARCHAR` column:

```java
import org.framefork.hibernate.orm.types.ImmutableType;
import org.framefork.hibernate.orm.types.ParameterizedTypeUtils;
import org.hibernate.type.SqlTypes;

public class StringCodeType extends ImmutableType<StringCode, StringCodeJavaType>
{

    public StringCodeType()
    {
        // null: the JdbcType is looked up by getSqlType() once Hibernate passes its TypeConfiguration
        super(new StringCodeJavaType(), null);
    }

    public StringCodeType(Class<? extends StringCode> codeClass)
    {
        this();
        setParameterValues(ParameterizedTypeUtils.forClass(codeClass));
    }

    @Override
    public String getName()
    {
        return "string-code";
    }

    @Override
    public int getSqlType()
    {
        return SqlTypes.VARCHAR;
    }

}
```

`ImmutableType.setParameterValues` passes the parameters on to the `JavaType` when it implements `ParameterizedType`, so `StringCodeJavaType` (a `BasicJavaType<StringCode>` that also implements `DynamicParameterizedType`) resolves the concrete class there:

```java
@Override
public void setParameterValues(Properties parameters)
{
    this.codeClass = ParameterizedTypeUtils.getReturnedClass(parameters, StringCode.class);
}
```

Map an attribute with `@Type(StringCodeType.class)`, or register `new StringCodeType(CountryCode.class)` for a class in a `TypeContributor`.

Since `deepCopy`, `disassemble`, `assemble` and `replace` return the value itself, the values must be immutable, and must be `Serializable` to be stored in the second-level cache.

## License

Apache License 2.0, see [LICENSE](LICENSE).
