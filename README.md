# spring-load-entity

Auto load entities from database with `EntityManager`, by simply annotating fiels with `@LoadEntity(longId)`

## Configuration

```xml
<repositories>
    <repository>
        <id>myMavenRepo</id>
        <url>https://mymavenrepo.com/repo/H7y9TxxC8tlHnK4oS5RL/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.romajs.spring</groupId>
        <artifactId>spring-load-entity</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

At your `@SpringBootApplication`, add the following:

```java
@ComponentScan(basePackages = {"com.mypackage", "com.romajs.spring.annotation"})
```

## Usage

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadEntityTest {

    @LoadEntity(1L)
    private DemoEntity demoEntity;

    @Test
    public void shouldFoundEntity() {
        Assert.assertNotNull(demoEntity);
    }
    
}

@Entity
public class DemoEntity {

    @Id
    private Long id;

    @Column
    private String name;
    
    // getters & setters
}
```

For spring boot tests:
* Works with flyway script loading
* Does not work with `data.sql` *(possibly by some bug with spring boot loading `data.sql` and `BeanPostProcessor`)*