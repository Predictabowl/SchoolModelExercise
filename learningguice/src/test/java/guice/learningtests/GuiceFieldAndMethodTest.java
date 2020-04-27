package guice.learningtests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceFieldAndMethodTest {
	
	private static interface IMyService{}
	
	private static class MyService implements IMyService{}

	private static class MyClientWithInjectedField{
		@Inject
		IMyService service;
	}
	
	private static class MyClientWithInjectedMethods{
		IMyService service;
		
		@Inject
		public void init(IMyService service) {
			this.service = service;
		}
	}
	
	@Test
	public void filed_and_method_injection() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = injector.getInstance(MyClientWithInjectedField.class);
		MyClientWithInjectedMethods client2 = injector.getInstance(MyClientWithInjectedMethods.class);
		assertNotNull(client1.service);
		assertNotNull(client2.service);
	}
	
	@Test
	public void inject_members() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = new MyClientWithInjectedField();
		injector.injectMembers(client1);
		MyClientWithInjectedMethods client2 = new MyClientWithInjectedMethods();
		injector.injectMembers(client2);
		assertNotNull(client1.service);
		assertNotNull(client2.service);
	}

}
