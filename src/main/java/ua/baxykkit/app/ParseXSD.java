package ua.baxykkit.app;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;

public class ParseXSD {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String  filename = "src/test/resources/family.xsd";
		System.setProperty(DOMImplementationRegistry.PROPERTY, "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl");
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance(); 
		com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl impl = (XSImplementationImpl) registry.getDOMImplementation("XS-Loader");
		XSLoader schemaLoader = impl.createXSLoader(null);
		String path = new File(filename).getAbsolutePath();
		System.out.println(path);
		XSModel model = schemaLoader.loadURI(path);
		XSNamedMap map = model.getComponents((short) 0);
		System.out.println(map.toString());
	}

}
