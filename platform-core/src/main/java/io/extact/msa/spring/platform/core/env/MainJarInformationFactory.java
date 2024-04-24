package io.extact.msa.spring.platform.core.env;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.core.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainJarInformationFactory {

    private String jarFindName;

    public MainJarInformationFactory(Environment environment) {
        jarFindName = environment.getProperty("rms.env.main.jar");
    }

    public MainJarInformation create() {
        URL manifestUrl = findTargetManifestUrl();
        if (manifestUrl == null) {
            return MainJarInformation.UNKNOWN;
        }
        return buidFromManifestUrl(manifestUrl);
    }

    private URL findTargetManifestUrl() {

        // check prop value
        if (jarFindName == null) {
            return null;
        }

        // find jar metainf
        Enumeration<URL> metainfResources;
        try {
            metainfResources = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // filter by jar protocol and filename
        Predicate<Object> findJarMatcher = new FindJarMatcher(jarFindName);
        List<URL> metainfUrls = Collections.list(metainfResources).stream()
                .filter(url -> url.getProtocol().equals("jar"))
                .filter(url -> findJarMatcher.test(ResourceUtils.extractFilePathStringOfUrl(url)))
                .toList();

        // resolve metainf resource url
        if (metainfUrls.isEmpty()) {
            log.warn("no matched jar or META-INF [{}]", jarFindName);
            return null;
        }
        if (metainfUrls.size() > 1) {
            String urlPaths = metainfUrls.stream().map(URL::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            log.warn("to many match jar or META-INF [{}]" + System.lineSeparator() + urlPaths, jarFindName);
            return null;
        }
        return metainfUrls.get(0);
    }

    private MainJarInformation buidFromManifestUrl(URL manifestUrl) {

        // resolve jarName from jarfile path
        String jarFilePath = manifestUrl.toString().substring(0, manifestUrl.toString().indexOf('!'));
        String jarName = jarFilePath.substring(jarFilePath.lastIndexOf("/") + 1, jarFilePath.length());

        // resolve manifest
        Manifest manifest;
        try (InputStream is = manifestUrl.openStream()) {
            manifest = new Manifest(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // resolve metainf prop
        Optional<String> applicationName = getManifestValue(manifest, "Application-Name");
        Optional<String> version = getManifestValue(manifest, "Application-Version");
        Optional<String> mainClassName = getManifestValue(manifest, "Main-Class");
        Optional<String> buildtime = getManifestValue(manifest, "Build-Time");

        // build MainJarInfo
        MainJarInformation.MainJarInformationBuilder builder = MainJarInformation.builder();
        applicationName.ifPresent(v -> builder.applicationName(v));
        builder.jarName(jarName);
        version.ifPresent(v -> builder.version(v));
        mainClassName.ifPresent(v -> builder.mainClassName(v));
        buildtime.ifPresent(v -> builder.buildTimeInfo(v));

        return builder.build();
    }

    private Optional<String> getManifestValue(Manifest manifest, String key) {
        String value = manifest.getMainAttributes().getValue(key);
        return Optional.ofNullable(value);
    }


    static class FindJarMatcher implements Predicate<Object> {

        private Pattern pattern;

        private FindJarMatcher(String findJarName) {
            this.pattern = Pattern.compile(findJarName);
        }

        @Override
        public boolean test(Object input) {
            return pattern.matcher(input.toString()).find();
        }
    }
}
