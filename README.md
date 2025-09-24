# jobdsl-compiler

Small CLI to compile Jenkins Job DSL (Groovy) into XML locally without Jenkins.

## Quick start

Requirements: JDK 11+.

Build and run:

```
./gradlew run --args "--targets jobs/**/*.groovy --out out --clean"
```

Outputs:
- Generated job XML under `out/jobs/<name>.xml` (folder names preserved)
- Generated view XML under `out/views/<name>.xml`

## CLI options

- `--targets`: Comma-separated Ant globs. Default: `jobs/**/*.groovy`
- `--out`: Output directory. Default: `out`
- `--clean`: Clean output directory before write
- `--base`: Base directory to resolve globs and `readFileFromWorkspace`

## Notes

- Uses `job-dsl-core` with `MemoryJobManagement` and preloads workspace files so calls like `readFileFromWorkspace('path')` work.
- This validates DSL structure and produces the XML Jenkins would save, but does not execute jobs.

