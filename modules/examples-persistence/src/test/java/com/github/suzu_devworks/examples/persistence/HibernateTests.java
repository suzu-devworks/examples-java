package com.github.suzu_devworks.examples.persistence;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.ninja_squad.dbsetup.Operations.sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.assertj.db.type.Changes;
import org.assertj.db.type.Source;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.suzu_devworks.examples.localization.DateTimeConvertor;
import com.github.suzu_devworks.examples.persistence.entities.User;
import com.github.suzu_devworks.examples.persistence.entities.UserItem;
import com.github.suzu_devworks.examples.persistence.hibernate.SessionFactoryBuilder;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;

public class HibernateTests {

    // use hibernate
    private static SessionFactory sessionFactory;

    // use dbsetup
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    private static DriverManagerDestination destination;

    // use assertj
    private static Source dbSource;

    @BeforeAll
    static void setup() {
        // configure from hibernate.cfg.xml
        var configure = new Configuration().configure();

        final var url = configure.getProperty(AvailableSettings.URL);
        final var user = configure.getProperty(AvailableSettings.USER);
        final var pass = configure.getProperty(AvailableSettings.PASS);

        sessionFactory = new SessionFactoryBuilder().build();

        destination = new DriverManagerDestination(url, user, pass);

        dbSource = new Source(url, user, pass);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void preparation() {

        var operation = sequenceOf(
                HibernateTests.TRUCATE_ALL,
                HibernateTests.INSERT_REFERENCE_DATA,
                HibernateTests.INSERT_USER_ITEMS_DATA);
        var dbSetup = new DbSetup(destination, operation);

        // new DataSourceDestination(dataSource),
        // or without DataSource:
        // DbSetup dbSetup = new DbSetup(new
        // DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)
        // , operation);

        dbSetupTracker.launchIfNecessary(dbSetup);
        // If you want to skip creating test data,
        // call dbSetupTracker.skipNextLaunch() in test case.

    }

    private static final Operation TRUCATE_ALL = sequenceOf(
            // truncate("users", "user_items","user_status"),
            sql("TRUNCATE TABLE users RESTART IDENTITY"),
            sql("TRUNCATE TABLE user_items RESTART IDENTITY"),
            sql("TRUNCATE TABLE user_status RESTART IDENTITY"));

    private static final Operation INSERT_REFERENCE_DATA = sequenceOf(
            insertInto("users")
                    .columns("user_name", "email", "password")
                    .values("Alice", "alice@example.local", "password for alica.")
                    .values("Bob", "bob@example.local", "password for bob.")
                    .values("Carol", "carol@example.local", "password for carol.")
                    .build());

    private static final Operation INSERT_USER_ITEMS_DATA = sequenceOf(
            insertInto("user_items")
                    .withGeneratedValue("item_code",
                            ValueGenerators.sequence()
                                    .startingAt(1000L)
                                    .incrementingBy(10))
                    .withGeneratedValue("item_name",
                            ValueGenerators.stringSequence("ITEM-")
                                    .startingAt(1000L)
                                    .incrementingBy(10)
                                    .withLeftPadding(6))
                    .withGeneratedValue("purchase_date",
                            ValueGenerators.dateSequence()
                                    .startingAt(LocalDate.now())
                                    .incrementingBy(1, ChronoUnit.DAYS))
                    .withDefaultValue("last_updated_at",
                            DateTimeConvertor.now(ZoneId.of("Asia/Tokyo")))
                    .columns("user_id")
                    .repeatingValues(1)
                    .times(10)
                    .build());

    @AfterEach
    void cleanUp() {
    }

    @Test
    public void testInsert() {
        var changes = new Changes(dbSource).setStartPointNow();

        User user = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            user = new User(
                    null,
                    "Dave",
                    "dave@example.local",
                    "password for dave.");

            session.persist(user);
            session.getTransaction().commit();
        }

        changes.setEndPointNow();

        assertThat(user.getId())
                .isNotNull()
                .isGreaterThan(BigInteger.ZERO);

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("users")
                .isCreation()
                .hasPksValues(4)
                .rowAtStartPoint()
                    .doesNotExist()
                .rowAtEndPoint()
                    .exists()
                    .hasValues(4, "Dave", "dave@example.local",
                    "password for dave.                                              ");
        // @formatter:on

    }

    @Test
    public void testUpdate() {
        var changes = new Changes(dbSource).setStartPointNow();

        var id = BigInteger.valueOf(3);

        User user = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            user = session.find(User.class, id);
            user.setEmail("charlie@example.local");
            user.setPassword("updated password for charlie.");

            // It has been updated even with commit alone.
            // session.update(user);
            // session.merge(user);
            session.getTransaction().commit();
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("users")
                .isModification()
                .hasPksValues(id)
                .column("id").isNotModified()
                .column("user_name").isNotModified()
                .column("email")
                    .valueAtStartPoint()
                        .isEqualTo("carol@example.local")
                    .valueAtEndPoint()
                        .isEqualTo("charlie@example.local")
                .column("password")
                    .valueAtStartPoint()
                        .isEqualTo("password for carol.                                             ")
                    .valueAtEndPoint()
                        .isEqualTo("updated password for charlie.                                   ")
                ;
        // @formatter:on
    }

    @Test
    public void testFind() {
        dbSetupTracker.skipNextLaunch();

        var id = BigInteger.valueOf(1);

        User user = null;
        try (Session session = sessionFactory.openSession()) {

            user = session.find(User.class, id);
        }

        // jupiter
        Assertions.assertEquals("Alice", user.getName());
        Assertions.assertEquals("alice@example.local", user.getEmail());
        Assertions.assertEquals(
                "password for alica.                                             ",
                user.getPassword());

        // hamcrest
        assertThat(user.getName(), is(equalTo("Alice")));
        assertThat(user.getEmail(), is(equalTo("alice@example.local")));
        assertThat(user.getPassword(), is(equalTo(
                "password for alica.                                             ")));

        // asseretj
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.local");
        assertThat(user.getPassword()).isEqualTo(
                "password for alica.                                             ");

    }

    @Test
    public void testFindAll() {
        dbSetupTracker.skipNextLaunch();

        List<UserItem> actuals = null;
        try (Session session = sessionFactory.openSession()) {

            final var queryString = "from UserItem";

            Query<UserItem> query = session.createQuery(queryString, UserItem.class);
            actuals = query.getResultList();
        }

        for (UserItem item : actuals) {
            System.out.println(item.toString());
        }

        assertThat(actuals)
                .isNotEmpty()
                .hasSize(10);

    }

    @Test
    public void testDelete() {
        var changes = new Changes(dbSource).setStartPointNow();

        var id = BigInteger.valueOf(2);

        User user = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            user = session.find(User.class, id);

            // session.delete(user);
            session.remove(user);
            session.getTransaction().commit();
        }

        changes.setEndPointNow();

        // @formatter:off
        assertThat(changes)
            .hasNumberOfChanges(1)
            .change()
                .isOnTable("users")
                .isDeletion()
                .hasPksValues(id)
                .rowAtStartPoint()
                    .exists()
                .rowAtEndPoint()
                    .doesNotExist();
        // @formatter:on
    }

}
