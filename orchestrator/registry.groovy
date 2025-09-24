// Logical job registry: stable IDs mapped to actual Jenkins full names.
// Update the 'name' values when jobs are renamed.
// Example structure:
// [
//   build_api: [name: 'folderA/build-api'],
//   test_web: [name: 'folderB/test-web'],
// ]

def REGISTRY = [
  sample1: [name: 'folderA/example job (v1.0) [특수문자]'],
  sample2: [name: 'folderB/complex job [특수문자] {#2}'],
]

return REGISTRY

