folder('folderB')

freeStyleJob('folderB/complex job [특수문자] {#2}') {
  description('Complex job with external script files and many special characters.')

  parameters {
    stringParam('NAME', 'tester', 'Name to greet')
  }

  steps {
    shell(readFileFromWorkspace('scripts/build.sh'))
    shell('''
      echo "Inline shell: quotes ' " backticks ` date $(date)"
      echo "Brackets [] Braces {} Parens () Dollar $ DblBackslash \\"
    '''.stripIndent())
  }

  publishers {
    def postScript = readFileFromWorkspace('scripts/postbuild.groovy')
    configure { project ->
      def pubs = project / 'publishers'
      pubs / 'org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildRecorder' {
        sandbox(true)
        behavior(0)
        script(postScript)
        runForMatrixParent(true)
      }
    }
  }
}
