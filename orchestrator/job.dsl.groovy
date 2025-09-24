// Job DSL to create the orchestrator pipeline job

pipelineJob('tools/orchestrator') {
  description('Batch runner to execute selected jobs sequentially or in parallel. Uses logical IDs from orchestrator/registry.groovy to tolerate renames.')

  parameters {
    choiceParam('MODE', ['sequential', 'parallel'], 'Execution mode for selected jobs')
    stringParam('JOBS', '', 'Comma-separated list of logical IDs (from registry.groovy) or full job names. Empty = all registered jobs.')
    booleanParam('PROPAGATE', true, 'Fail orchestrator if any job fails')
    booleanParam('DRY_RUN', false, 'Show what would run without triggering')
  }

  definition {
    cps {
      sandbox(true)
      script(readFileFromWorkspace('orchestrator/Jenkinsfile'))
    }
  }
}

