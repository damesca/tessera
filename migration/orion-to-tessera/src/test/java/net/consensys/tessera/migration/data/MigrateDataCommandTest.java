package net.consensys.tessera.migration.data;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.quorum.tessera.io.IOCallback;
import net.consensys.tessera.migration.OrionKeyHelper;
import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fusesource.leveldbjni.JniDBFactory.factory;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class MigrateDataCommandTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrateDataCommandTest.class);

    private MigrateDataCommand migrateDataCommand;

    private InboundDbHelper inboundDbHelper;

    private TesseraJdbcOptions tesseraJdbcOptions;

    private Path orionConfigDir;

    private String tesseraJdbcUrl;

    @Rule
    public TemporaryFolder outputDir = new TemporaryFolder();

    private OrionKeyHelper orionKeyHelper;

    private MigrationInfo migrationInfo;

    public MigrateDataCommandTest(Path orionConfigDir) {
        this.orionConfigDir = orionConfigDir;
    }

    @Before
    public void beforeTest() throws Exception {

        FileUtils.copyDirectory(orionConfigDir.toFile(),outputDir.getRoot());

        Path workfingOrionConfigDir = outputDir.getRoot().toPath();

        tesseraJdbcUrl = "jdbc:h2:" + workfingOrionConfigDir.toAbsolutePath() + "/" + UUID.randomUUID().toString() + ".db";
        final Path orionConfigFile = Paths.get(workfingOrionConfigDir.toString(),"orion.conf");
        assertThat(orionConfigFile).exists();

        Toml toml = new Toml().read(orionConfigFile.toFile());

        Map orionConfig = new HashMap(toml.toMap());
        orionConfig.put("workdir",workfingOrionConfigDir.toString());
        TomlWriter tomlWriter = new TomlWriter();

        Path adjustedOrionConfigFile = workfingOrionConfigDir.resolve("orion-adjusted.conf");
        tomlWriter.write(orionConfig,Files.newOutputStream(adjustedOrionConfigFile));

        Options options = new Options();
        //options.logger(s -> System.out.println(s));
        options.createIfMissing(true);
        String dbname = "routerdb";
        final DB leveldb = IOCallback.execute(
            () -> factory.open(workfingOrionConfigDir.resolve(dbname).toAbsolutePath().toFile(), options)
        );

        inboundDbHelper = mock(InboundDbHelper.class);
        when(inboundDbHelper.getLevelDb()).thenReturn(Optional.of(leveldb));
        when(inboundDbHelper.getInputType()).thenReturn(InputType.LEVELDB);

        tesseraJdbcOptions = mock(TesseraJdbcOptions.class);
        when(tesseraJdbcOptions.getAction()).thenReturn("drop-and-create");
        when(tesseraJdbcOptions.getUrl()).thenReturn(tesseraJdbcUrl);
        when(tesseraJdbcOptions.getUsername()).thenReturn("junit");
        when(tesseraJdbcOptions.getPassword()).thenReturn("junit");

        orionKeyHelper = OrionKeyHelper.from(adjustedOrionConfigFile);

        migrateDataCommand = new MigrateDataCommand(inboundDbHelper, tesseraJdbcOptions, orionKeyHelper);


        this.migrationInfo = MigrationInfoFactory.create(inboundDbHelper);

    }

    @After
    public void afterTest() {
        MigrationInfo.clear();
    }


    @Test
    public void migrate() throws Exception {


        Map<PayloadType,Long> result = migrateDataCommand.call();
        assertThat(result)
            .containsOnlyKeys(PayloadType.ENCRYPTED_PAYLOAD,PayloadType.PRIVACY_GROUP_PAYLOAD);

        MigrationInfo migrationInfo = MigrationInfo.getInstance();
        assertThat(result.get(PayloadType.ENCRYPTED_PAYLOAD)).isEqualTo(migrationInfo.getTransactionCount());
        assertThat(result.get(PayloadType.PRIVACY_GROUP_PAYLOAD)).isEqualTo(migrationInfo.getPrivacyGroupCount());


    }


    @Parameterized.Parameters(name = "{0}")
    public static List<Path> configs() throws IOException {
        return Files.list(Paths.get("samples"))
            .filter(Files::isDirectory)
            .flatMap(d -> {
                try {
                    return Files.list(d).filter(Files::isDirectory);
                } catch (IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
            })
            .collect(Collectors.toUnmodifiableList());
    }







}
