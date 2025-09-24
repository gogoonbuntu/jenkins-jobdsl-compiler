// Complex sample: freestyle job with shell step and Groovy Postbuild using raw XML

freeStyleJob('folderA/example job (v1.0) [특수문자]') {
  description('''
    Example job with special chars <>&"' and env ${PATH}
  '''.stripIndent())

  parameters {
    stringParam('NAME', 'world', 'Target name')
  }

  steps {
    shell('''#!/bin/bash -xe
set -o pipefail
echo "Hello, $NAME" | sed -E 's/(H.*o)/[\\1]/'
echo "Specials: ; | & < > $ ( ) ` ' \" \\"  \\ \$HOME"
cat <<'EOS'
HEREDOC test with specials: ;|&<>'"`\
Groovy DSL ${NOT_EXPANDED} $(echo hi)
EOS
''')
  }

  publishers {
    // Configure Groovy Postbuild via raw XML to avoid DSL plugin dependency
    configure { project ->
      def pubs = project / 'publishers'
      pubs / 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder' {
        sandbox(true)
        behavior(0) // 0: Do nothing on failure (plugin-specific)
        script('''
          // Groovy Postbuild script body with specials
          manager.listener.logger.println("Postbuild: <&> \" ' $")
          return true
        '''.stripIndent())
        runForMatrixParent(true)
      }
    }
  }
}
