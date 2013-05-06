package management;

import java.io.File;

import junit.framework.Assert;
import management.testclasses.DummyManagedClass;
import management.testclasses.DummyManagementExtension;
import management.testclasses.ManagedClassStore;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.LocaleProducer;
import br.gov.frameworkdemoiselle.DemoiselleException;
import br.gov.frameworkdemoiselle.util.Beans;

/**
 * Test case that simulates a management extension and tests if properties and operations on a managed class can be
 * easily accessed and invoked.
 * 
 * @author serpro
 */
@RunWith(Arquillian.class)
public class ManagementTestCase {

	@Deployment
	public static JavaArchive createMultithreadedDeployment() {
		return ShrinkWrap
				.create(JavaArchive.class)
				.addClass(LocaleProducer.class)
				.addPackages(true, "br")
				.addAsResource(new FileAsset(new File("src/test/resources/test/beans.xml")), "beans.xml")
				.addAsManifestResource(
						new File("src/main/resources/META-INF/services/javax.enterprise.inject.spi.Extension"),
						"services/javax.enterprise.inject.spi.Extension")
				.addPackages(false, ManagementTestCase.class.getPackage())
				.addClasses(DummyManagementExtension.class, DummyManagedClass.class, ManagedClassStore.class);
	}

	@Test
	public void testReadProperty() {
		DummyManagedClass managedClass = Beans.getReference(DummyManagedClass.class);
		managedClass.setName("Test Name");

		// store é nossa extensão de gerenciamento falsa, então estamos testando um "cliente" acessando
		// nosso tipo gerenciado DummyManagedClass remotamente.
		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);
		Object name = store.getProperty(DummyManagedClass.class, "name");
		Assert.assertEquals("Test Name", name);
	}

	@Test
	public void testWriteProperty() {
		// store é nossa extensão de gerenciamento falsa, então estamos testando um "cliente" definindo
		// um novo valor em uma propriedade de nosso tipo gerenciado DummyManagedClass remotamente.
		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);
		store.setProperty(DummyManagedClass.class, "name", "Test Name");

		DummyManagedClass managedClass = Beans.getReference(DummyManagedClass.class);
		Assert.assertEquals("Test Name", managedClass.getName());
	}

	@Test
	public void testReadAWriteOnly() {

		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);

		try {
			store.getProperty(DummyManagedClass.class, "writeOnlyProperty");
			Assert.fail();
		} catch (DemoiselleException de) {
			// SUCCESS
		}

	}

	@Test
	public void testWriteAReadOnly() {

		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);

		try {
			store.setProperty(DummyManagedClass.class, "readOnlyProperty", "New Value");
			Assert.fail();
		} catch (DemoiselleException de) {
			// SUCCESS
		}

	}

	@Test
	public void testInvokeOperation() {

		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);

		try {
			store.setProperty(DummyManagedClass.class, "firstFactor", new Integer(10));
			store.setProperty(DummyManagedClass.class, "secondFactor", new Integer(15));
			Integer response = (Integer) store.invoke(DummyManagedClass.class, "sumFactors");
			Assert.assertEquals(new Integer(25), response);
		} catch (DemoiselleException de) {
			Assert.fail(de.getMessage());
		}

	}

	@Test
	public void testInvokeNonAnnotatedOperation() {

		ManagedClassStore store = Beans.getReference(ManagedClassStore.class);

		try {
			// O método "nonOperationAnnotatedMethod" existe na classe DummyManagedClass, mas não está anotado como
			// "@Operation", então
			// ela não pode ser exposta para extensões.
			store.invoke(DummyManagedClass.class, "nonOperationAnnotatedMethod");
			Assert.fail();
		} catch (DemoiselleException de) {
			// SUCCESS
		}

	}
}
