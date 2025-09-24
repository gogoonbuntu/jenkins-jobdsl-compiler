package dev.local.jobdsl.compiler;

import javaposse.jobdsl.dsl.MemoryJobManagement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalJobManagement extends MemoryJobManagement {
    private final Path baseDir;

    public LocalJobManagement(Path baseDir) {
        this.baseDir = baseDir;
    }

    // Job DSL calls JobManagement.readFileInWorkspace from readFileFromWorkspace()
    public String readFileInWorkspace(String filePath) {
        Path p = baseDir.resolve(filePath).normalize();
        try {
            return Files.readString(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
