package hu.ponte.image_resizer.config;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:image_tools.properties", ignoreResourceNotFound = true)
public class ImageMagickConfig {
    @Value("${image_tool.searchPath:}")
    private String searchPath;

    @Value("${image_tool.searchPathDefaultWindows}")
    private String searchPathDefaultWindows;

    @Value("${image_tool.searchPathDefaultLinux}")
    private String searchPathDefaultLinux;

    @Bean
    @Profile("windows")
    public ConvertCmd windowsConvertCmd() {
        ConvertCmd cmd = new ConvertCmd();

        if (searchPath.isEmpty()) {
            cmd.setSearchPath(searchPathDefaultWindows);
        } else {
            cmd.setSearchPath(searchPath);
        }
        return cmd;
    }

    @Bean
    @Profile("linux")
    public ConvertCmd linuxConvertCmd() {
        ConvertCmd cmd = new ConvertCmd();

        if (searchPath.isEmpty()) {
            cmd.setSearchPath(searchPathDefaultLinux);
        } else {
            cmd.setSearchPath(searchPath);
        }
        return cmd;
    }

    @Bean
    public IMOperation defaultOperation() {
        IMOperation operation = new IMOperation();
        // Alapértelmezett műveletek beállítása
        operation.quality(90.0);
        operation.colorspace("RGB");
        return operation;
    }
}
