package com.github.suzu_devworks.examples.persistence.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CamelToSnakePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return convertToSnakeCase(logicalName);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return convertToSnakeCase(logicalName);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return convertToSnakeCase(logicalName);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return convertToSnakeCase(logicalName);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return convertToSnakeCase(logicalName);
    }

    private Identifier convertToSnakeCase(final Identifier identifier) {

        if (identifier == null) {
            return null;
        }

        final var regex = "([a-z])([A-Z])";
        final var replacement = "$1_$2";

        var newName = identifier.getText()
                .replaceAll(regex, replacement)
                .toLowerCase();

        return Identifier.toIdentifier(newName);
    }

}
