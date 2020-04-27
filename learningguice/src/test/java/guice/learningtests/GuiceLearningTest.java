package guice.learningtests;

import static org.junit.Assert.*;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;

import org.junit.Test;

public class GuiceLearningTest {

	private static interface IMyService {
	}

	private static class MyService implements IMyService {
	}
	
	@Singleton
	private static class MySingletonService implements IMyService {
	}

	private static class MyClient {
		MyService service;

		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}

	private static class MyGenericClient {
		IMyService service;

		@Inject
		public MyGenericClient(IMyService service) {
			this.service = service;
		}

	}

	@Test
	public void can_instantiate_concrete_classes_without_configuration() {
		Module module = new AbstractModule() {
		};
		Injector injector = Guice.createInjector(module);
		MyClient client = injector.getInstance(MyClient.class);
		assertNotNull(client.service);
	}

	@Test
	public void can_instantiate_abstract_type() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client = injector.getInstance(MyGenericClient.class);
		assertNotNull(client.service);
	}

	@Test
	public void bind_to_instance() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).toInstance(new MyService());
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client1 = injector.getInstance(MyGenericClient.class);
		MyGenericClient client2 = injector.getInstance(MyGenericClient.class);
		assertNotNull(client1.service);
		assertSame(client1.service, client2.service);
	}

	@Test
	public void bind_to_singleton_test() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
				bind(MyService.class).in(Singleton.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client1 = injector.getInstance(MyGenericClient.class);
		MyGenericClient client2 = injector.getInstance(MyGenericClient.class);
		assertNotNull(client1.service);
		assertSame(client1.service, client2.service);
	}

	@Test
	public void singleton_per_injector_test() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class).in(Singleton.class);
			}
		};
		assertNotSame(Guice.createInjector(module).getInstance(MyGenericClient.class).service,
				Guice.createInjector(module).getInstance(MyGenericClient.class).service);
	}
	
	@Test
	public void singleton_annotation_test() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MySingletonService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client1 = injector.getInstance(MyGenericClient.class);
		MyGenericClient client2 = injector.getInstance(MyGenericClient.class);
		assertSame(client1.service, client2.service);
		assertNotSame(Guice.createInjector(module).getInstance(MyGenericClient.class).service,
				Guice.createInjector(module).getInstance(MyGenericClient.class).service);
	}

}
