# JWT-Authentication
Complete Configuration of JWT


### Secret key Generator website
https://asecuritysite.com/encryption/plain


# Overview of JWT Authentication Flow
### 1. Client Requests Access:
    The client sends a login request to the server with user credentials.
### 2. Server Validates Credentials:
    The server verifies the credentials and generates a JWT if valid.
### 3. Client Receives and Stores JWT:
    The client receives the JWT and stores it (usually in local storage or a cookie).
### 4. Client Makes Authenticated Requests:
    The client includes the JWT in the Authorization header for subsequent API requests.
### 5. Server Verifies JWT:
    The server checks the JWT's validity and extracts the user's information.
### 6. Server Processes the Request:
    If the JWT is valid, the server processes the request and sends back a response.



# JWT Structure

## A JSON Web Token (JWT) typically consists of three parts separated by dots (.):

- Header: Contains metadata about the token, such as the type of token and the algorithm used for signing.
- Payload: Contains the claims, which are the actual data being transmitted (e.g., user information).
- Signature: Ensures that the token hasn't been altered.

## Here's an example of a JWT:

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

## Parts of a JWT
- Header: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
- Payload: eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
- Signature: SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

## How the Signature is Created
### Combine the Header and Payload: The header and payload are base64-encoded and combined with a dot:

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ

- Hashing with Secret Key: This combined string is then hashed using the specified algorithm (HS256 in this case) along with the secret key. This generates a unique signature.

- Signature: The signature ensures that the header and payload have not been altered. If any part of the token changes, the signature will no longer match.

## Verification Process

### When the system receives a token, it performs the following steps:

- Separate the Token: Split the token into its three parts (header, payload, and signature).

- Recreate the Signature: Using the header and payload, the system creates a new signature using the same algorithm and secret key.

- Compare Signatures: The newly created signature is compared to the signature in the token. If they match, the token is valid and has not been tampered with. If they don't match, the token is invalid.

## In Simple Terms

### Generating a Token:

Combine user information (payload) and metadata (header).
Use a secret key and a specific method to create a signature.
The token is the combination of the header, payload, and signature.
Verifying a Token:

Use the same secret key and method to recreate the signature from the received token's header and payload.
Check if this recreated signature matches the one in the token.
If they match, the token is valid; otherwise, it's not.
What Does the Signature Look Like?
The signature is a base64-encoded string of the hashed value created from the header, payload, and secret key. In our example JWT:


SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
This string is the signature. It ensures the integrity and authenticity of the token.

## Why It’s Important
- Integrity: Ensures the token's data hasn't been altered.
- Authenticity: Verifies that the token was issued by a trusted source (using the secret key).

### By signing the token with a secret key and verifying it on receipt, we ensure that the token is both secure and reliable.








### Detailed Summary and Resolution of Dependency Cycle

#### Issue

A dependency cycle exists among the following classes:
1. **JwtFiIter**: This class is a filter that processes JWT tokens.
2. **UserServiceInfoImpl**: This class implements user-related services.
3. **SecurityConfig**: This class configures the security settings of the application.

#### Cause

Circular dependencies create a loop in the dependency graph, preventing Spring from initializing beans. Here’s how the cycle looks:

1. `JwtFiIter` depends on `UserServiceInfoImpl`.
2. `UserServiceInfoImpl` depends on `PasswordEncoder`.
3. `SecurityConfig` depends on `JwtFiIter` and `UserServiceInfoImpl`.

This creates a situation where Spring cannot figure out the order to instantiate these beans, leading to a deadlock.

#### Resolution

##### Option 1: Use `@Lazy` Annotation

Mark one of the dependencies as lazy, so Spring will delay its initialization until it is actually needed.

**Step-by-Step:**

1. **Add `@Lazy` Annotation to `JwtFiIter` in `SecurityConfig`:**
   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {

       @Autowired
       @Lazy
       private JwtFiIter jwtFiIter;

       @Autowired
       public UserServiceInfoImpl userDetailsService;

       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
           return httpSecurity.csrf(csrf -> csrf.disable())
                   .authorizeHttpRequests(auth -> auth
                           .requestMatchers("/auth/welcome", "/auth/addUser", "/auth/login")
                           .permitAll()
                           .anyRequest().authenticated())
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .authenticationProvider(authenticationProvider())
                   .addFilterBefore(jwtFiIter, UsernamePasswordAuthenticationFilter.class)
                   .build();
       }

       @Bean
       public AuthenticationProvider authenticationProvider() {
           DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
           daoAuthenticationProvider.setUserDetailsService(userDetailsService);
           daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
           return daoAuthenticationProvider;
       }

       @Bean
       public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
           return config.getAuthenticationManager();
       }

       @Bean
       public PasswordEncoder getPasswordEncoder() {
           return new BCryptPasswordEncoder();
       }

   }
   ```

2. **Explanation:**
   - `@Lazy` delays the initialization of `JwtFiIter` until it is needed. This breaks the cycle because `JwtFiIter` is not immediately required when `SecurityConfig` and `UserServiceInfoImpl` are being initialized.

##### Option 2: Use Setter Injection

Change the `JwtFiIter` to use setter injection for `UserServiceInfoImpl`.

**Step-by-Step:**

1. **Modify `JwtFiIter` to Use Setter Injection:**
   ```java
   @Component
   public class JwtFiIter extends OncePerRequestFilter {

       @Autowired
       private JwtService jwtService;

       private UserServiceInfoImpl userServiceInfo;

       @Autowired
       public void setUserServiceInfo(UserServiceInfoImpl userServiceInfo) {
           this.userServiceInfo = userServiceInfo;
       }

       @Override
       protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           String authHeader = request.getHeader("Authorization");

           String token = null;
           String username = null;

           if (authHeader != null && authHeader.startsWith("Bearer")) {
               token = authHeader.substring(7);
               username = jwtService.extractUsername(token);
           }

           if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userServiceInfo.loadUserByUsername(username);

               if (jwtService.validateToken(token, userDetails)) {
                   UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }

           filterChain.doFilter(request, response);
       }
   }
   ```

2. **Explanation:**
   - By using setter injection, `UserServiceInfoImpl` is not immediately required when `JwtFiIter` is being initialized. This breaks the cycle by allowing Spring to initialize `JwtFiIter` without needing `UserServiceInfoImpl` right away.

### What to Keep in Mind

1. **Avoid Circular Dependencies:**
   - Design your application to minimize or eliminate circular dependencies.
   - Ensure that your classes and components have clear, unidirectional dependencies.
   - For example, instead of class A depending on class B and vice versa, try to refactor so that both depend on a third class C, or restructure the responsibilities to avoid the dependency loop.

2. **Use Setter Injection:**
   - For components with circular dependencies, consider using setter injection instead of constructor injection.
   - Setter injection can help to delay the injection of dependencies, breaking the initialization cycle.
   - This approach is particularly useful when a bean requires a circular dependency but doesn’t need it immediately upon creation.

3. **Consider Lazy Initialization:**
   - Use `@Lazy` annotation for beans that can be initialized later.
   - Lazy initialization delays the creation of a bean until it is actually needed, which can help break dependency cycles.
   - Be cautious with lazy initialization, as it can introduce performance overhead or unexpected behavior if not used judiciously.

### Example of Dependency Cycle and Resolution

#### Example of a Dependency Cycle

- **Class A** depends on **Class B**.
- **Class B** depends on **Class C**.
- **Class C** depends on **Class A**.

```java
@Component
public class ClassA {
    @Autowired
    private ClassB classB;
}

@Component
public class ClassB {
    @Autowired
    private ClassC classC;
}

@Component
public class ClassC {
    @Autowired
    private ClassA classA;
}
```

#### Resolving the Dependency Cycle with Setter Injection

```java
@Component
public class ClassA {
    private ClassB classB;

    @Autowired
    public void setClassB(ClassB classB) {
        this.classB = classB;
    }
}

@Component
public class ClassB {
    @Autowired
    private ClassC classC;
}

@Component
public class ClassC {
    private ClassA classA;

    @Autowired
    public void setClassA(ClassA classA) {
        this.classA = classA;
    }
}
```

#### Resolving the Dependency Cycle with `@Lazy` Annotation

```java
@Component
public class ClassA {
    @Autowired
    @Lazy
    private ClassB classB;
}

@Component
public class ClassB {
    @Autowired
    private ClassC classC;
}

@Component
public class ClassC {
    @Autowired
    @Lazy
    private ClassA classA;
}
```

### Summary

Understanding and resolving dependency cycles is crucial for building robust and maintainable Spring applications. By avoiding circular dependencies, using setter injection, and considering lazy initialization, you can prevent dependency-related issues and ensure smooth bean initialization.






### Spring IoC Container and Dependency Injection Explained

The Spring IoC (Inversion of Control) container is the core of the Spring Framework. It manages the lifecycle and dependencies of the beans (objects) in a Spring application. Here's a detailed step-by-step explanation of how the IoC container works and how it handles dependency injection.

#### Step-by-Step Process of Spring IoC Container and Dependency Injection

1. **Spring Application Initialization**:
   - When a Spring Boot application starts, it triggers the initialization process of the Spring IoC container. This is typically done by running the `SpringApplication.run(Application.class, args)` method.

2. **Configuration Class Parsing**:
   - The IoC container scans the application for configuration classes annotated with `@Configuration`, `@ComponentScan`, `@EnableAutoConfiguration`, etc. These configurations provide metadata about the beans that need to be created.

3. **Bean Definition Loading**:
   - The container reads the metadata and loads the definitions of the beans from various sources like `@Component`, `@Service`, `@Repository`, and XML configuration files.

4. **Bean Instantiation**:
   - The container starts instantiating the beans. For each bean definition, it checks the bean's scope (singleton by default) and creates an instance if necessary.
   - If a bean has dependencies, the container ensures that these dependencies are resolved and injected.

5. **Dependency Injection**:
   - **Constructor Injection**: Dependencies are injected through the bean's constructor.
   - **Setter Injection**: Dependencies are injected through setter methods.
   - **Field Injection**: Dependencies are injected directly into fields marked with `@Autowired`.

6. **Post-Processing**:
   - The container processes any `BeanPostProcessor` implementations, allowing custom modification of new bean instances.

7. **Bean Initialization**:
   - The container calls the `@PostConstruct` annotated methods or any custom initialization methods specified by the `init-method` attribute in XML configuration.

8. **Application Context Ready**:
   - Once all beans are instantiated, dependencies are injected, and initialization methods are called, the application context is fully ready, and the application starts running.

#### Example

Let's look at a simple example with constructor injection:

**Configuration Class:**
```java
@Configuration
public class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        return new ServiceB(serviceA());
    }
}
```

**Service Classes:**
```java
public class ServiceA {
    public void performAction() {
        System.out.println("ServiceA action performed");
    }
}

public class ServiceB {
    private final ServiceA serviceA;

    @Autowired
    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }

    public void execute() {
        serviceA.performAction();
    }
}
```

**Main Application:**
```java
@SpringBootApplication
public class SpringIoCDemoApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringIoCDemoApplication.class, args);
        ServiceB serviceB = context.getBean(ServiceB.class);
        serviceB.execute();
    }
}
```

### Lazy Initialization

When we add the `@Lazy` annotation to an `@Autowired` field or a bean definition, it changes the timing of bean initialization.

**What Happens with `@Lazy`:**

- **Standard Behavior**: Without `@Lazy`, beans are created and their dependencies are injected immediately during the application context startup.
- **With `@Lazy`**: The bean is not instantiated and its dependencies are not injected until it is actually needed (i.e., when it is first accessed).

**Example with `@Lazy`:**
```java
@Component
public class ServiceC {
    private final ServiceA serviceA;

    @Autowired
    @Lazy
    public ServiceC(ServiceA serviceA) {
        this.serviceA = serviceA;
    }

    public void execute() {
        serviceA.performAction();
    }
}
```

In this example, `ServiceA` will only be instantiated when `ServiceC.execute()` is called for the first time.

### Setter Injection over Field Injection

**Setter Injection**:
- Dependencies are provided via public setter methods.
- It allows for optional dependencies and can be used for better testability and clarity.

**Field Injection**:
- Dependencies are injected directly into the fields.
- It is concise but can make testing and maintenance harder due to hidden dependencies.

**Example of Setter Injection:**
```java
@Component
public class ServiceD {
    private ServiceA serviceA;

    @Autowired
    public void setServiceA(ServiceA serviceA) {
        this.serviceA = serviceA;
    }

    public void execute() {
        serviceA.performAction();
    }
}
```

**Why Prefer Setter Injection:**
- **Visibility**: Dependencies are explicit in the setter methods, making the code easier to understand.
- **Optional Dependencies**: Setter injection allows for optional dependencies that can be set or changed later.
- **Testing**: It's easier to create mock objects and set them via setter methods during testing.

### Summary

- **Spring IoC Container** manages the lifecycle and dependencies of beans.
- **Dependency Injection** can be done via constructors, setters, or fields.
- **Lazy Initialization** delays the creation of a bean until it's needed.
- **Setter Injection** is often preferred over field injection for better code visibility, optional dependencies, and testability.

By understanding these concepts, you can design and develop Spring applications that are easier to maintain, test, and extend.


