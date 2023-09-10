package per.duyd.interview.pts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
  @Autowired
  protected MockMvc mockMvc;

  public static final String TEST_RESOURCES_FOLDER = "src/test/resources";

  public static String readFileToJsonString(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(TEST_RESOURCES_FOLDER + filePath)));
  }
}
