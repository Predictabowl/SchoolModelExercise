package guice.learningtests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceCyclicDependenciesTest {

	private static interface IMyView {}
	private static class MyView implements IMyView {
		IMyController controller;
		
		public void setController(IMyController controller) {
			this.controller = controller;
		}
	}

	private static interface IMyRepository {}
	private static class MyRepository implements IMyRepository {}

	private static interface IMyController {}
	private static class MyController implements IMyController{
		IMyView view;
		IMyRepository repository;
		
		@Inject
		public MyController(@Assisted IMyView view, IMyRepository repository) {
			this.view = view;
			this.repository = repository;
		}
	}
	
	private static interface MyControllerFactory{
		IMyController create(IMyView view);
	}
	
	private static class MyViewProvider implements Provider<MyView>{
		@Inject
		private MyControllerFactory controllerFactory;

		@Override
		public MyView get() {
			// the cycle must be solved manually
			MyView view = new MyView();
			view.setController(controllerFactory.create(view));
			return view;
		}
	}

	
	@Test
	public void cyclic_dependencies() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class);
//				bind(IMyView.class).to(MyView.class);
				bind(MyView.class).toProvider(MyViewProvider.class);
				install(new FactoryModuleBuilder().implement(IMyController.class, MyController.class).build(MyControllerFactory.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyView view = injector.getInstance(MyView.class);
		MyController controller = (MyController) view.controller;
		assertNotNull(controller.repository);
		assertSame(view, controller.view);
	}

}
