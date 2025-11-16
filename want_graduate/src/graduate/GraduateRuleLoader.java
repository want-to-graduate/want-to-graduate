package graduate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class GraduateRuleLoader {
	/**
	 * 2022.Json 같은 파일을 읽어 GradeRule 객체로 변환해 주는 로더.
	 */
	private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /** 예: load(Path.of("2022.Json")) */
    public static GraduateRule load(Path jsonPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(jsonPath, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, GraduateRule.class);
        }
    }
}
