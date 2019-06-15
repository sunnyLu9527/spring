/**
 *    Copyright 2010-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

/**
 *
 * spring bean实例化之前
 * @MapperScan主要做了三个事情
 * 1.扫描出所有的mapper所对应的BeanDefinition
 * 2.把mapper变成FactoryBean，MapperFactoryBean的BeanDefinition
 * 3.为BeanDefinition添加一个构造方法的值，因为mybatis的MapperFactoryBean有一个有参数构造方法，
 * spring在实例化这个对象的时候需要一个构造方法的值，这个值是一个class，后面spring在实例化过程中根据这个class返回我们的代理对象
 *
 * spring bean实例化之中和之后的工作
 * mybatis主要利用spring的初始方法扩展点来完成对mapper信息的初始化，比如sql语句的初始化，这里的spring扩展点主要就是afterPropertiesSet，
 * 就是利用MapperFactoryBean去实现InitializingBean，由于MapperFactoryBean是一个factoryBean，我们理解为就是一个mapper，所以可以理解为他
 * 就是自己获得自己的信息，然后把信息缓存起来，缓存到map中
 * @see org.apache.ibatis.session.Configuration#mappedStatements
 *
 * Use this annotation to register MyBatis mapper interfaces when using Java
 * Config. It performs when same work as {@link MapperScannerConfigurer} via
 * {@link MapperScannerRegistrar}.
 *
 * <p>Configuration example:</p>
 * <pre class="code">
 * &#064;Configuration
 * &#064;MapperScan("org.mybatis.spring.sample.mapper")
 * public class AppConfig {
 *
 *   &#064;Bean
 *   public DataSource dataSource() {
 *     return new EmbeddedDatabaseBuilder()
 *              .addScript("schema.sql")
 *              .build();
 *   }
 *
 *   &#064;Bean
 *   public DataSourceTransactionManager transactionManager() {
 *     return new DataSourceTransactionManager(dataSource());
 *   }
 *
 *   &#064;Bean
 *   public SqlSessionFactory sqlSessionFactory() throws Exception {
 *     SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
 *     sessionFactory.setDataSource(dataSource());
 *     return sessionFactory.getObject();
 *   }
 * }
 * </pre>
 *
 * @author Michael Lanyon
 * @author Eduardo Macarron
 *
 * @since 1.2.0
 * @see MapperScannerRegistrar
 * @see MapperFactoryBean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MapperScannerRegistrar.class)
@Repeatable(MapperScans.class)
public @interface MapperScan {

  /**
   * Alias for the {@link #basePackages()} attribute. Allows for more concise
   * annotation declarations e.g.:
   * {@code @MapperScan("org.my.pkg")} instead of {@code @MapperScan(basePackages = "org.my.pkg"})}.
   *
   * @return base package names
   */
  String[] value() default {};

  /**
   * Base packages to scan for MyBatis interfaces. Note that only interfaces
   * with at least one method will be registered; concrete classes will be
   * ignored.
   *
   * @return base package names for scanning mapper interface
   */
  String[] basePackages() default {};

  /**
   * Type-safe alternative to {@link #basePackages()} for specifying the packages
   * to scan for annotated components. The package of each class specified will be scanned.
   * <p>Consider creating a special no-op marker class or interface in each package
   * that serves no purpose other than being referenced by this attribute.
   *
   * @return classes that indicate base package for scanning mapper interface
   */
  Class<?>[] basePackageClasses() default {};

  /**
   * The {@link BeanNameGenerator} class to be used for naming detected components
   * within the Spring container.
   *
   * @return the class of {@link BeanNameGenerator}
   */
  Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

  /**
   * 有指定注解的接口将会被扫描
   * This property specifies the annotation that the scanner will search for.
   * <p>
   * The scanner will register all interfaces in the base package that also have
   * the specified annotation.
   * <p>
   * Note this can be combined with markerInterface.
   *
   * @return the annotation that the scanner will search for
   */
  Class<? extends Annotation> annotationClass() default Annotation.class;

  /**
   * 继承了指定的父类接口的接口将会被扫描
   * This property specifies the parent that the scanner will search for.
   * <p>
   * The scanner will register all interfaces in the base package that also have
   * the specified interface class as a parent.
   * <p>
   * Note this can be combined with annotationClass.
   *
   * @return the parent that the scanner will search for
   */
  Class<?> markerInterface() default Class.class;

  /**
   * Specifies which {@code SqlSessionTemplate} to use in the case that there is
   * more than one in the spring context. Usually this is only needed when you
   * have more than one datasource.
   *
   * @return the bean name of {@code SqlSessionTemplate}
   */
  String sqlSessionTemplateRef() default "";

  /**
   * Specifies which {@code SqlSessionFactory} to use in the case that there is
   * more than one in the spring context. Usually this is only needed when you
   * have more than one datasource.
   *
   * @return the bean name of {@code SqlSessionFactory}
   */
  String sqlSessionFactoryRef() default "";

  /**
   * Specifies a custom MapperFactoryBean to return a mybatis proxy as spring bean.
   *
   * @return the class of {@code MapperFactoryBean}
   */
  Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;

}
