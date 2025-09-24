// Groovy Postbuild script with special characters and quotes
manager.listener.logger.println("[postbuild.groovy] Start <&> \" ' $")

def env = manager.build.getEnvironment(manager.listener)
def name = env['NAME'] ?: 'world'
manager.listener.logger.println("Env NAME=${name}")

// JSON-like and regex content
def json = '{"msg":"hello \\"' + name + '\\" & <world> (test)"}'
manager.listener.logger.println("JSON: ${json}")

// Backticks-like content (not executed here, just text)
manager.listener.logger.println('`echo not-executed` ${DOLLAR} $(subshell)')

return true

