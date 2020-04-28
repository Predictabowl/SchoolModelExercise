package guice.learningtests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceAssistedInjectTest {

	private static interface IMyView {}
	private static class MyView implements IMyView {}

	private static interface IMyRepository {}
	private static class MyRepository implements IMyRepository {}

	private static interface IMyController {}
	private static class MyController implements IMyController {
		IMyView view;
		IMyRepository repository;

		@Inject
		public MyController(@Assisted IMyView view, // from the instance creator
				IMyRepository repository // from the injector
		) {
			this.view = view;
			this.repository = repository;
		}
	}

	private static interface MyControllerFactory {
		IMyController create(IMyView view);
	}

	@Test
	public void assistend_inject() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class);
				install(new FactoryModuleBuilder().implement(IMyController.class, MyController.class)
						.build(MyControllerFactory.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyControllerFactory controllerFactory = injector.getInstance(MyControllerFactory.class);
		MyController controller = (MyController) controllerFactory.create(new MyView());
		assertNotNull(controller.repository);
		assertNotNull(controller.view);
	}

}
