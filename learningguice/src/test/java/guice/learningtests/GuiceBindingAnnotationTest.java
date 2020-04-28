package guice.learningtests;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class GuiceBindingAnnotationTest {
	
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	private static @interface FilePath {}
	
	@BindingAnnotation
	@Target({FIELD, PARAMETER, METHOD})
	@Retention(RUNTIME)
	private static @interface FileName{}
	
	private static class MyFileWrapper2{
		File file;
		
		@Inject
		public MyFileWrapper2(@FilePath String path, @FileName String name) {
			file = new File(path, name);
		}
	}
	
	private static class MyFileWrapper{
		File file;
		
		@Inject
		public MyFileWrapper(@Named("PATH") String path, @Named("NAME") String name) {
			file = new File(path,name);
		}
	}
	
	@Test
	public void binding_annotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(Names.named("PATH")).toInstance("src/test/resources");
				bind(String.class).annotatedWith(Names.named("NAME")).toInstance("aFile.txt");
			}
		};
		
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertTrue(fileWrapper.file.exists());
	}
	
	@Test
	public void custom_binding_annotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(FilePath.class).toInstance("src/test/resources");
				bind(String.class).annotatedWith(FileName.class).toInstance("aFile.txt");
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper2 fileWrapper = injector.getInstance(MyFileWrapper2.class);
		assertTrue(fileWrapper.file.exists());
	}

}
