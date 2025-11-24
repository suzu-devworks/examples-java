package com.github.suzu_devworks.examples.persistence;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.ninja_squad.dbsetup.Operations.sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.sql.PooledConnection;

import org.assertj.db.type.Changes;
import org.assertj.db.type.DateValue;
import org.assertj.db.type.Source;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.suzu_devworks.examples.localization.DateTimeConvertor;
import com.github.suzu_devworks.examples.persistence.entities.WorkItem;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;

public class JdbcTests {
    private static Callable<Connection> connectionSupplier;

    // use JDBC connection pool
    private static final boolean useConnectionPooling = true;
    private static PooledConnection connectionPool;

    // use dbsetup
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    private static DriverManagerDestination destination;

    // use assertj
    private static Source dbSource;

    @BeforeAll
    static void setup() throws IOException, SQLException {
        final var properties = getProperties();
        final var url = properties.getProperty("jdbc.connection.url");
        final var user = properties.getProperty("jdbc.connection.username");
        final var pass = properties.getProperty("jdbc.connection.password");

        var dataSource = new JdbcDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        connectionPool = dataSource.getPooledConnection();

        connectionSupplier = useConnectionPooling
                ? () -> connectionPool.getConnection()
                : () -> DriverManager.getConnection(url, user, pass);

        destination = new DriverManagerDestination(url, user, pass);

        dbSource = new Source(url, user, pass);
    }

    private static Properties getProperties() throws IOException {
        // configure from jdbc.properties
        var properties = new Properties();

        final ClassLoader classLoader = JdbcTests.class.getClassLoader();
        final Path path = Paths.get(classLoader.getResource("jdbc.properties").getFile());

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        return properties;
    }

    @AfterAll
    static void tearDown() {
    }

    @BeforeEach
    void preparation() {

        var operation = sequenceOf(
                JdbcTests.INITIALIZE_WORK_ITEMS);
        var dbSetup = new DbSetup(destination, operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    private static final Operation INITIALIZE_WORK_ITEMS = sequenceOf(
            sql("TRUNCATE TABLE work_items RESTART IDENTITY"),
            insertInto("work_items")
                    .withGeneratedValue("code",
                            ValueGenerators.stringSequence("")
                                    .startingAt(1000L)
                                    .incrementingBy(10)
                                    .withLeftPadding(5))
                    .withGeneratedValue("name",
                            ValueGenerators.stringSequence("ITEM-")
                                    .startingAt(1000L)
                                    .incrementingBy(10)
                                    .withLeftPadding(5))
                    .withGeneratedValue("price",
                            ValueGenerators.sequence()
                                    .startingAt(1000L)
                                    .incrementingBy(10))
                    .withGeneratedValue("quantity",
                            ValueGenerators.sequence()
                                    .startingAt(10L)
                                    .incrementingBy(1))
                    .withGeneratedValue("purchase_date",
                            ValueGenerators.dateSequence()
                                    .startingAt(LocalDate.parse("2020-09-01"))
                                    .incrementingBy(1, ChronoUnit.DAYS))
                    .columns("last_updated_at")
                    .repeatingValues(DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")))
                    .times(10)
                    .build());

    @AfterEach
    void cleanUp() throws SQLException {
    }

    @Test
    public void testInsert() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);

            final var statmentText = ""
                    + "insert into work_items (code, name, price, quantity, purchase_date, last_updated_at) "
                    + "values (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {

                statement.setString(1, "10001");
                statement.setString(2, "ITEM-ADDITONAL-10001");
                statement.setBigDecimal(3, BigDecimal.valueOf(10020L, 2));
                statement.setInt(4, 1024);
                statement.setObject(5, LocalDate.parse("2020-09-17"));
                statement.setObject(6, DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")));

                var effectiveCount = statement.executeUpdate();
                System.out.println(effectiveCount + " rows insert.");

                connection.commit();

                statement.setString(1, "10002");
                statement.setString(2, "ITEM-ADDITONAL-10002");
                statement.setBigDecimal(3, BigDecimal.valueOf(10021L, 2));
                statement.setInt(4, 2048);
                statement.setObject(5, LocalDate.parse("2020-09-18"));
                statement.setObject(6, DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")));

                var effectiveCount2nd = statement.executeUpdate();
                System.out.println(effectiveCount2nd + " rows insert..");

                connection.rollback();
            }
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("work_items")
                .isCreation()
                .hasPksValues(11)
                .rowAtStartPoint()
                    .doesNotExist()
                .rowAtEndPoint()
                    .exists()
                    .value("code").isEqualTo("10001")
                    .value("name").isEqualTo("ITEM-ADDITONAL-10001")
                    .value("price").isEqualTo(BigDecimal.valueOf(10020L, 2))
                    .value("quantity").isEqualTo(1024)
                    .value("purchase_date").isEqualTo(DateValue.from(LocalDate.parse("2020-09-17")))
                    .value("last_updated_at").isNotNull()
                    ;
        // @formatter:on
    }

    @Test
    public void testUpdate() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);

            final var statmentText = ""
                    + "update work_items set name = ?, price = ?, quantity = ?, purchase_date = ?, last_updated_at = ? "
                    + "where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {

                statement.setString(1, "ITEM-MODIFY-1090#");
                statement.setBigDecimal(2, BigDecimal.valueOf(10030L, 2));
                statement.setInt(3, 2048);
                statement.setObject(4, LocalDate.parse("2020-09-01"));
                statement.setObject(5, DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")));

                statement.setInt(6, 10);

                var rowsUpdated = statement.executeUpdate();
                System.out.println(rowsUpdated + " rows updated");
            }

            connection.commit();
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("work_items")
                .isModification()
                .hasPksValues(10)
                .column("id").isNotModified()
                .column("code").isNotModified()
                .column("name")
                    .valueAtStartPoint()
                        .isEqualTo("ITEM-01090")
                    .valueAtEndPoint()
                        .isEqualTo("ITEM-MODIFY-1090#")
                .column("price")
                    .valueAtStartPoint()
                        .isEqualTo(BigDecimal.valueOf(109000L, 2))
                    .valueAtEndPoint()
                        .isEqualTo(BigDecimal.valueOf(10030L, 2))
                .column("purchase_date")
                    .valueAtStartPoint()
                        .isEqualTo(DateValue.from(LocalDate.parse("2020-09-10")))
                    .valueAtEndPoint()
                        .isEqualTo(DateValue.from(LocalDate.parse("2020-09-01")))
                .column("last_updated_at").isModified()
                ;
        // @formatter:on
    }

    @Test
    public void testFind() throws Exception {
        dbSetupTracker.skipNextLaunch();

        WorkItem item = null;
        try (var connection = connectionSupplier.call()) {

            final var statmentText = ""
                    + "select id, code, name, price, quantity, purchase_date, last_updated_at"
                    + " from work_items"
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {
                statement.setInt(1, 10);

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {

                        item = WorkItem.builder()
                                .id(result.getInt("id"))
                                .code(result.getString("code"))
                                .name(result.getString("name"))
                                .price(result.getBigDecimal("price").setScale(2))
                                .quantity(result.getInt("quantity"))
                                .purchaseDate(result.getObject("purchase_date", LocalDate.class))
                                .lastUpdatedAt(DateTimeConvertor.atZoneSameInstant(
                                        result.getObject("last_updated_at", OffsetDateTime.class),
                                        ZoneId.of("Asia/Tokyo")))
                                .build();

                        System.out.println(item.toString());
                    }
                }
            }
        }

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(10);
        assertThat(item.getCode()).isEqualTo("01090");
        assertThat(item.getName()).isEqualTo("ITEM-01090");
        assertThat(item.getPrice()).isEqualTo(BigDecimal.valueOf(109000L, 2));
        assertThat(item.getQuantity()).isEqualTo(19);
        assertThat(item.getPurchaseDate()).isEqualTo(LocalDate.parse("2020-09-10"));
    }

    @Test
    public void testFindAll() throws Exception {
        dbSetupTracker.skipNextLaunch();

        List<WorkItem> resultList = new ArrayList<>();

        try (var connection = connectionSupplier.call()) {

            final var statmentText = ""
                    + "select code, name, price, quantity, purchase_date, last_updated_at"
                    + " from work_items"
                    + " order by quantity desc";

            try (PreparedStatement statement = connection.prepareStatement(statmentText);
                    ResultSet result = statement.executeQuery();) {

                while (result.next()) {
                    WorkItem item = WorkItem.builder()
                            .code(result.getString("code"))
                            .name(result.getString("name"))
                            .price(result.getBigDecimal("price").setScale(2))
                            .quantity(result.getInt("quantity"))
                            .purchaseDate(result.getObject("purchase_date", LocalDate.class))
                            .lastUpdatedAt(DateTimeConvertor.atZoneSameInstant(
                                    result.getObject("last_updated_at", OffsetDateTime.class),
                                    ZoneId.of("Asia/Tokyo")))
                            .build();

                    resultList.add(item);
                    System.out.println(item.toString());
                }

            }
        }

        assertThat(resultList).isNotEmpty();
        assertThat(resultList.size()).isEqualTo(10);
    }

    @Test
    public void testDelete() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);

            final var statmentText = ""
                    + "delete from work_items"
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {
                statement.setInt(1, 10);

                var rowsDeleted = statement.executeUpdate();
                System.out.println(rowsDeleted + " rows deleted");
            }

            connection.commit();
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("work_items")
                .isDeletion()
                .hasPksValues(10)
                .rowAtStartPoint()
                    .exists()
                .rowAtEndPoint()
                    .doesNotExist()
                ;
        // @formatter:on
    }

    @Test
    public void testBatch() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            // @formatter:off
            // | Isolation Level  | lost update | Dirty reads | Non-repeatable reads | Phantom reads |
            // |------------------|-------------|-------------|----------------------|---------------|
            // | READ UNCOMMITTED | x           | o           | o                    | o             |
            // | READ COMMITTED   | x           | x           | o                    | o             |
            // | REPEATABLE READ  | x           | x           | x                    | o             |
            // | SERIALIZABLE     | x           | x           | x                    | x             |
            // @formatter:on

            final var statmentText = ""
                    + "insert into work_items (code, name, price, quantity, purchase_date, last_updated_at) "
                    + "values (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {

                statement.setString(1, "10101");
                statement.setString(2, "ITEM-ADDITONAL-10101");
                statement.setBigDecimal(3, BigDecimal.valueOf(10101L, 2));
                statement.setInt(4, 1024);
                statement.setObject(5, LocalDate.parse("2020-09-20"));
                statement.setObject(6, DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")));

                statement.addBatch();

                statement.setString(1, "10102");
                statement.setString(2, "ITEM-ADDITONAL-10102");
                statement.setBigDecimal(3, BigDecimal.valueOf(10102L, 2));
                statement.setInt(4, 2048);
                statement.setObject(5, LocalDate.parse("2020-09-21"));
                statement.setObject(6, DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")));
                statement.addBatch();

                var rowsChanged = statement.executeBatch();
                System.out.println(rowsChanged + " rows Changed.");
            }

            connection.commit();
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(2)
            .changeOfCreation(1)
                .isOnTable("work_items")
                .hasPksValues(12)
                .rowAtStartPoint()
                    .doesNotExist()
                .rowAtEndPoint()
                    .exists()
            .changeOfCreation(0)
                .isOnTable("work_items")
                .hasPksValues(11)
                .rowAtStartPoint()
                    .doesNotExist()
                .rowAtEndPoint()
                    .exists()
                    ;
        // @formatter:on
    }

    @Test
    public void testCLOB() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        final var classLoader = getClass().getClassLoader();
        final var filePath = Paths.get(classLoader.getResource("two-column.csv").getFile());
        final var fileSize = Files.size(filePath);
        System.out.println("> size = " + Long.toString(fileSize));
        final List<String> expecteds = Files.readAllLines(filePath);

        List<String> actuals = new ArrayList<>();

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);

            final var statmentText = ""
                    + "update work_items set clob_data = ? "
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {

                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    statement.setCharacterStream(1, reader);
                }
                statement.setInt(2, 2);

                var rowsChanged = statement.executeUpdate();
                System.out.println(rowsChanged + " rows Changed.");
            }

            connection.commit();

            final var queryText = ""
                    + "select clob_data from work_items"
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(queryText)) {
                statement.setInt(1, 2);

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {

                        try (var reader = new BufferedReader(result.getCharacterStream(1))) {
                            reader.lines().forEach(text -> {
                                actuals.add(text);
                            });
                        }
                    }
                }
            }
        }

        changes.setEndPointNow();

        assertThat(actuals)
                .hasSize(expecteds.size())
                .containsSequence(expecteds);

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .changeOfModification()
                .isOnTable("work_items")
                .hasPksValues(2)
                .column("code").isNotModified()
                .column("name").isNotModified()
                .column("price").isNotModified()
                .column("quantity").isNotModified()
                .column("purchase_date").isNotModified()
                .column("last_updated_at").isNotModified()
                .column("clob_data").isModified()
                .column("blob_data").isNotModified()
            ;
        // @formatter:on
    }

    @Test
    public void testBLOB() throws Exception {
        var changes = new Changes(dbSource).setStartPointNow();

        final var classLoader = getClass().getClassLoader();
        final var filePath = Paths.get(classLoader.getResource("user-trash.png").getFile());
        final var fileSize = Files.size(filePath);
        System.out.println("> size = " + Long.toString(fileSize));

        final var sha512 = MessageDigest.getInstance("SHA-512");
        final var expecteds = sha512.digest(Files.readAllBytes(filePath));
        System.out.println("> SHA-512 :" + Base64.getEncoder().encodeToString(expecteds));

        byte[] actuals = null;

        try (var connection = connectionSupplier.call()) {
            connection.setAutoCommit(false);

            final var statmentText = ""
                    + "update work_items set blob_data = ? "
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(statmentText)) {

                try (InputStream stream = Files.newInputStream(filePath)) {
                    statement.setBinaryStream(1, stream);
                }
                statement.setInt(2, 3);

                var rowsChanged = statement.executeUpdate();
                System.out.println(rowsChanged + " rows Changed.");

            }

            connection.commit();

            final var queryText = ""
                    + "select blob_data from work_items"
                    + " where id = ?";

            try (PreparedStatement statement = connection.prepareStatement(queryText);) {
                statement.setInt(1, 3);

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {

                        try (InputStream stream = result.getBinaryStream(1)) {
                            actuals = sha512.digest(stream.readAllBytes());
                        }
                    }
                }
            }
        }

        changes.setEndPointNow();

        assertThat(actuals)
                .hasSize(expecteds.length)
                .containsSequence(expecteds);

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .changeOfModification()
                .isOnTable("work_items")
                .hasPksValues(3)
                .column("code").isNotModified()
                .column("name").isNotModified()
                .column("price").isNotModified()
                .column("quantity").isNotModified()
                .column("purchase_date").isNotModified()
                .column("last_updated_at").isNotModified()
                .column("clob_data").isNotModified()
                .column("blob_data").isModified()
            ;
        // @formatter:on
    }

    @Test
    public void testCallingProcedure() throws Exception {
        dbSetupTracker.skipNextLaunch();

        String upperCased = null;

        try (var connection = connectionSupplier.call()) {

            final var statmentText = "{? = call upper( ? ) }";

            try (CallableStatement statement = connection.prepareCall(statmentText)) {
                statement.registerOutParameter(1, Types.VARCHAR);
                statement.setString(2, "lowercase to uppercase");

                statement.execute();
                upperCased = statement.getString(1);
            }
        }

        assertThat(upperCased).isEqualTo("LOWERCASE TO UPPERCASE");
    }
}
