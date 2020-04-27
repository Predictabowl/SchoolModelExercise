package guice.learningtests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

public class GuiceProvidersTest {

	private static interface IMyService{}
	private static class MyService implements IMyService{}
	
	private static class MyClientWithProvider{
		@Inject
		Provider<IMyService> serviceProvider;
		
		IMyService getService() {
			return serviceProvider.get();
		}
	}
	
	private static class MyFileWrapper{
		@Inject
		File file;
	}
	
	@Test
	public void inject_provider_example() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		
		Injector injector = Guice.createInjector(module);
		MyClientWithProvider client = injector.getInstance(MyClientWithProvider.class);
		assertNotNull(client.getService());
	}
	
	@Test
	public void provider_binding() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(File.class).toProvider(() -> new File("src/test/resources/aFile.txt"));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertTrue(fileWrapper.file.exists());
	}

}
