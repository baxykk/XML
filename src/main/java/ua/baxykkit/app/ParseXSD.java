package ua.baxykkit.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroup.Compositor;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.impl.EmptyImpl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.impl.SimpleTypeImpl;
import com.sun.xml.xsom.parser.XSOMParser;

public class ParseXSD {
	private static List<XSType> ltypes = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String filename = "src/test/resources/family.xsd";
		String path = new File(filename).getAbsolutePath();
		System.out.println(path);

		XSOMParser parser = new XSOMParser();
		parser.setErrorHandler(new MyErrorHandler());
		parser.parse(path);

		XSSchemaSet schemaSet = parser.getResult();
		Iterator<XSSchema> iterSchema = schemaSet.iterateSchema();

		while (iterSchema.hasNext()) {
			XSSchema schema = iterSchema.next();

			if (schema.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
				continue;
			}

			Iterator<XSElementDecl> iterElem = schema.iterateElementDecls();
		
			System.out.println("-----------Elements------");
			while (iterElem.hasNext()) {
				XSElementDecl element = iterElem.next();
				System.out.println(describeElement(element));

			}
			
			Map<String, XSElementDecl> map = schema.getElementDecls();
			Set<String> keys = map.keySet();
			for (String string : keys) {
				System.out.println("Key -> "+string);
			}

			Iterator<XSAttributeDecl> iterAttr = schema.iterateAttributeDecls();

			System.out.println("-----------Attributes------");
			while (iterAttr.hasNext()) {
				XSAttributeDecl attr = iterAttr.next();

				System.out.println(describeAttributes(attr));
			}

			Iterator<XSType> iterType = schema.iterateTypes();

			while (iterType.hasNext()) {
				XSType type = iterType.next();				
				ltypes.add(type);
			}
			List<XSComplexType> complex = new ArrayList<>();
			List<XSSimpleType> simple = new ArrayList<>();
			
			System.out.println("-----------Types------");
			for (XSType type : ltypes) {
				if (type.isComplexType()) complex.add(type.asComplexType());
				else if (type.isSimpleType()) simple.add(type.asSimpleType());
					
				System.out.println(describeTypes(type));
			}
			
			System.out.println("-----------Complex Type -----------");
			
			for (XSComplexType xsct : complex) {
				XSContentType con = xsct.getContentType();
				if (con instanceof EmptyImpl) {
					XSContentType xsContent = con.asEmpty();
					System.out.println(xsContent.toString());
				}
				else if (con instanceof ParticleImpl) {
					XSTerm term = con.asParticle().getTerm();
					System.out.println(term.toString());
					
					if (term.isElementDecl()) {
						XSElementDecl te = term.asElementDecl();
						System.out.println(te.getName());
					}
					else if (term.isModelGroupDecl()) {
						XSModelGroup md = term.asModelGroup();
						System.out.println(md.toString());
						Compositor comp = md.getCompositor();
						if(comp.equals(Compositor.SEQUENCE)) System.out.println("\t++sequence++");
						else if (comp.equals(Compositor.CHOICE)) System.out.println("\t++choice++");
						else if (comp.equals(Compositor.ALL)) System.out.println("\t++all++");
					
						List<XSParticle> xsp = Arrays.asList(md.getChildren());
						xsp.forEach(el -> System.out.println(el));
					}
					else if (term.isWildcard()) {
						XSWildcard xsw = term.asWildcard();
						System.out.println(xsw);
					}
					
				}
				else if (con instanceof SimpleTypeImpl) {
					XSSimpleType st = con.asParticle().asSimpleType();
					System.out.println(st.getName());
				}
				
				XSContentType act = xsct.getContentType();
				XSParticle ap = act.asParticle();
				XSTerm t = ap.getTerm();
				XSModelGroupDecl ag = t.asModelGroupDecl();
				
				//System.out.println(ag.asElementDecl());
			}

		}

	}

	private static String describeTypes(XSType type) {
		String txt = "";

		if (type.isAnonymous())
			txt += ("(anonymous)");
		else
			txt += type.getName();

		if (type.isGlobal())
			txt += ("\t(global)");
		else if (type.isLocal())
			txt += ("\t(local)");

		if (type.isComplexType())
			txt += ("\t(complex)");
		else if (type.isSimpleType())
			txt += ("\t(simple)");

		int deriv = type.getDerivationMethod();
		switch (deriv) {
		case XSType.EXTENSION:
			txt += "(EXTENSION)";
			break;
		case XSType.RESTRICTION:
			txt += "(RESTRICTION)";
			break;
		case XSType.SUBSTITUTION:
			txt += "(SUBSTITUTION)";
			break;
		}

		return txt;
	}

	private static String describeAttributes(XSAttributeDecl attr) {
		String txt = attr.getName() + " \t " + attr.getType().getName();
		XmlString temp;

		temp = attr.getDefaultValue();
		if (temp != null) {
			txt += ("\t((default='" + temp + "')");
		}

		temp = attr.getFixedValue();
		if (temp != null) {
			txt += ("\t((fixed='" + temp + "')");
		}

		return txt;
	}

	private static String describeElement(XSElementDecl element) {
		String txt = element.getName();
		XSType type = element.getType();

		if (!ltypes.contains(type)) {
			if (type.isSimpleType()) {
				if (!type.asSimpleType().isPrimitive()) {
					ltypes.add(type);
				}
			} else
				ltypes.add(type);
		}

		txt += ("\t(" + type.toString() + ")");

		if (element.isGlobal()) {
			txt += ("\t (global)");
		}

		if (element.isLocal()) {
			txt += ("\t (local)");
		}

		if (element.isAbstract()) {
			txt += ("\t (absctract)");
		}

		XmlString str = element.getDefaultValue();
		if (str != null) {
			txt += ("\t (default = '" + str + "')");
		}

		XmlString str1 = element.getFixedValue();
		if (str1 != null) {
			txt += ("\t (fixed = '" + str1 + "')");
		}

		return txt;
	}

}
