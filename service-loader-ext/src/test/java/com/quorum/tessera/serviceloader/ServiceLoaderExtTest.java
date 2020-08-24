package com.quorum.tessera.serviceloader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceLoaderExtTest {

    private ServiceLoaderExt<SampleService> sampleServiceServiceLoader;

    @Before
    public void beforeTest() throws Exception {
        sampleServiceServiceLoader = ServiceLoaderExt.load(SampleService.class);
    }

    @After
    public void afterTest() {

    }

    @Test
    public void findFirst() {
        assertThat(sampleServiceServiceLoader.findFirst())
            .containsInstanceOf(SampleServiceImpl.class);
    }

    @Test
    public void stream() {
        assertThat(sampleServiceServiceLoader.stream()).hasSize(2);
    }

    @Test
    public void iterator() {
        assertThat(sampleServiceServiceLoader.iterator())
            .hasSize(2);
    }

    @Test
    public void spliterator() {
        assertThat(sampleServiceServiceLoader.spliterator()).isNotNull();
    }

    @Test
    public void foreach() {
        Consumer<? super SampleService> c = mock(Consumer.class);
        sampleServiceServiceLoader.forEach(c);
        verify(c, times(2)).accept(any(SampleService.class));

    }

    @Test
    public void reload() {
        sampleServiceServiceLoader.reload();
    }

    @Test
    public void singletonAnnotatedClassIsAlwaysTheSame() {

        Predicate<java.util.ServiceLoader.Provider> findSingletonProviders = p -> p.type() == SingletonSampleService.class;

        SampleService firstResult = sampleServiceServiceLoader.stream()
            .filter(findSingletonProviders)
            .map(java.util.ServiceLoader.Provider::get)
            .findAny().get();

        SampleService secondResult = sampleServiceServiceLoader.stream()
            .filter(findSingletonProviders)
            .map(java.util.ServiceLoader.Provider::get)
            .findAny().get();

        assertThat(firstResult).isSameAs(secondResult);
    }

    @Test
    public void nonSingletonClasses() {

        Predicate<java.util.ServiceLoader.Provider> findSingletonProviders = p -> p.type() == SampleServiceImpl.class;

        SampleService firstResult = sampleServiceServiceLoader.stream()
            .filter(findSingletonProviders)
            .map(java.util.ServiceLoader.Provider::get)
            .findAny().get();

        SampleService secondResult = sampleServiceServiceLoader.stream()
            .filter(findSingletonProviders)
            .map(java.util.ServiceLoader.Provider::get)
            .findAny().get();

        assertThat(firstResult).isNotSameAs(secondResult);

    }

    @Test
    public void loadWithClassloader() throws Exception {
        ClassLoader classLoader = mock(ClassLoader.class);

        ServiceLoaderExt<SampleService> result = ServiceLoaderExt.load(SampleService.class, classLoader);
        assertThat(result).isNotNull().isExactlyInstanceOf(ServiceLoaderExt.class);
    }

    @Test
    public void loadWithModuleLayer() throws Exception {
        ServiceLoaderExt<SampleService> result = ServiceLoaderExt.load(ModuleLayer.empty(),SampleService.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void loadInstalled() {
        ServiceLoaderExt<SampleService> result = ServiceLoaderExt.loadInstalled(SampleService.class);
        assertThat(result).isNotNull().isExactlyInstanceOf(ServiceLoaderExt.class);
    }
}
