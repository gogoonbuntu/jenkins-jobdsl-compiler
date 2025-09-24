package dev.local.jobdsl.compiler;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> opts = parseArgs(args);
        Path baseDir = Paths.get(opts.getOrDefault("base", "."));
        String targets = opts.getOrDefault("targets", "jobs/**/*.groovy");
        Path outDir = Paths.get(opts.getOrDefault("out", "out"));
        boolean clean = Boolean.parseBoolean(opts.getOrDefault("clean", "false"));

        if (!Files.isDirectory(baseDir)) {
            System.err.println("Base directory does not exist: " + baseDir);
            System.exit(2);
        }

        if (clean && Files.exists(outDir)) {
            try (var s = Files.walk(outDir)) {
                s.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
            }
        }
        Files.createDirectories(outDir);

        List<Path> files = collectTargets(baseDir, targets);
        if (files.isEmpty()) {
            // Fallback: search under jobs/ for .groovy files
            Path jobsDir = baseDir.resolve("jobs");
            if (Files.isDirectory(jobsDir)) {
                try (var s = Files.walk(jobsDir)) {
                    files = s.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".groovy")).toList();
                }
            }
            if (files.isEmpty()) {
                System.err.println("No DSL files matched for: " + targets);
                System.exit(1);
            }
        }

        LocalJobManagement jm = new LocalJobManagement(baseDir);
        DslScriptLoader loader = new DslScriptLoader(jm);

        for (Path p : files) {
            try {
                String script = Files.readString(p);
                loader.runScript(script);
                System.out.println("OK: " + baseDir.relativize(p));
            } catch (Throwable t) {
                System.err.println("ERROR in " + p + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
                t.printStackTrace(System.err);
                System.exit(3);
            }
        }

        writeXmlMap(outDir.resolve("jobs"), jm.getSavedConfigs());
        writeXmlMap(outDir.resolve("views"), jm.getSavedViews());

        System.out.println("Generated jobs: " + jm.getSavedConfigs().keySet());
        System.out.println("Generated views: " + jm.getSavedViews().keySet());
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "-h": case "--help":
                    System.out.println("Usage: --targets <glob[,glob...]> --out <dir> [--clean] [--base <dir>]");
                    System.exit(0);
                    break;
                case "-t": case "--targets": m.put("targets", nextArg(args, ++i, a)); break;
                case "-o": case "--out": m.put("out", nextArg(args, ++i, a)); break;
                case "-b": case "--base": m.put("base", nextArg(args, ++i, a)); break;
                case "-c": case "--clean": m.put("clean", "true"); break;
                default: System.err.println("Unknown option: " + a); System.exit(2);
            }
        }
        return m;
    }

    private static String nextArg(String[] args, int i, String opt) {
        if (i >= args.length) {
            System.err.println("Missing value for " + opt);
            System.exit(2);
        }
        return args[i];
    }

    private static List<Path> collectTargets(Path baseDir, String targetArg) throws IOException {
        Set<Path> out = new LinkedHashSet<>();
        String[] patterns = Arrays.stream(targetArg.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        List<String> regexes = new ArrayList<>();
        for (String p : patterns) regexes.add(globToRegex(p));

        try (var s = Files.walk(baseDir)) {
            for (Path p : s.filter(Files::isRegularFile).toList()) {
                String rel = baseDir.relativize(p).toString().replace('\\', '/');
                for (String r : regexes) {
                    if (rel.matches(r)) { out.add(p); break; }
                }
            }
        }
        return new ArrayList<>(out);
    }

    private static String globToRegex(String glob) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        char[] cs = glob.replace('\\', '/').toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            if (c == '*') {
                boolean doublestar = (i + 1 < cs.length && cs[i + 1] == '*');
                if (doublestar) { sb.append(".*"); i++; }
                else sb.append("[^/]*");
            } else if (c == '?') {
                sb.append("[^/]");
            } else if (".[]{}()+-^$|".indexOf(c) >= 0) {
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        sb.append("$");
        return sb.toString();
    }

    private static void writeXmlMap(Path base, Map<String, String> map) throws IOException {
        if (map == null || map.isEmpty()) return;
        for (Map.Entry<String, String> e : map.entrySet()) {
            Path path = base.resolve(e.getKey() + ".xml");
            Files.createDirectories(path.getParent());
            Files.writeString(path, e.getValue(), StandardCharsets.UTF_8);
        }
    }
}
