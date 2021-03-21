# Component Test

A library of helper functions to help with component tests for spring boot applications.

## Highlights

### Test Container Support, through the use of:
- @WithVaultContainer 
- @WithMongoDBContainer
- @WithPostgresContainer
    - This annotation has two parameters:   
        - poolSize - the number of connections in conn pool (default 2)
        - command - the command for container (default  postgres -c max_connections=500 -c shared_buffers=256MB)
- @WithElasticSearchContainer
- @WithKafkaContainer  
- @WithEmbeddedKafka
    
There are two ways to use these annotations:

1. Extend BaseComponentTest (to have the whole application context)
```kotlin
@WithPostgresContainer
class SomeTest : BaseComponetTest() {
}
```    
    
2. Use @ExtendWith(TestContainersExtension::class) and @DataXXTest if you want to load application context
for repository tests

```kotlin
@ExtendWith(TestContainersExtension::class)
@WithPostgresContainer
@DataJpaTest
class SomeTest {
}
```    

### Wiremock support
Wiremock is enabled through the boostrap context whenever the profile "wiremock" is active.

Two ways to do this:
```
@ActiveProfiles("wiremock")
```
or
```
class TestClass : BaseComponentTest()
```
BaseComponentTest has wiremock profile active by default. 
A random port will be allocated for wiremock use, and is available to the application at ```wiremock.server.port```


### Utilities to load in mongo data in BSON format - see TestDataRunner

## Build

Built with gradle on the `master` branch.
