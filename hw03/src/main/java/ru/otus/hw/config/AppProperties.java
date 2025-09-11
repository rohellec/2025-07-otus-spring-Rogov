package ru.otus.hw.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Locale;
import java.util.Map;

@Setter
@ConfigurationProperties(prefix = "test")
public class AppProperties implements TestConfig, TestFileNameProvider, LocaleConfig {

    @Getter
    private final int rightAnswersCountToPass;

    @Getter
    private final Locale locale;

    private final Map<String, String> fileNameByLocaleTag;

    @ConstructorBinding
    public AppProperties(Locale locale,
                         int rightAnswersCountToPass,
                         Map<String, String> fileNameByLocaleTag) {
        this.locale = locale;
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.fileNameByLocaleTag = fileNameByLocaleTag;
    }


    @Override
    public String getTestFileName() {
        return fileNameByLocaleTag.get(locale.toLanguageTag());
    }
}
