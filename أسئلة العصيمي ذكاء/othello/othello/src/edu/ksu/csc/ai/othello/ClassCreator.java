package edu.ksu.csc.ai.othello;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Various utility functions for finding and instantiating classes.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A. Sultanik</a>
 */
class ClassCreator {
	/**
	 * Returns a set of all classes whose name contains the given text.
	 */
	public static <U> HashSet<Class<? extends U>> getClassesContaining(String text, Class<U> type, boolean caseSensitive) {
		HashSet<Class<? extends U>> matches = new HashSet<Class<? extends U>>();
		if(!caseSensitive)
			text = text.toLowerCase();
		for(Class<? extends U> c : getClassesOfType(type))
			if((caseSensitive && c.getName().contains(text)) || (!caseSensitive && c.getName().toLowerCase().contains(text)))
				matches.add(c);
		return matches;
	}

	/**
	 * Returns a set of all classes whose name contains the given text.
	 */
	public static <U> HashSet<Class<? extends U>> getClassesContaining(String text, Class<U> type) {
		return getClassesContaining(text, type, false);
	}
	
	/**
	 * Returns the Class object associated with the name of a class.
	 * This is equivalent to
	 * <code>ClassLoader.getSystemClassLoader().loadClass(className)</code>.
	 *
	 * @throws ClassNotFoundException if the class cannot be found.
	 */
	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return ClassLoader.getSystemClassLoader().loadClass(className);
	}

	/**
	 * Returns all of the classes defined within the given jar file.
	 */
	public static HashSet<Class<?>> getClasses(JarFile jarFile) {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if(name.endsWith(".class")) {
				name = name.substring(0, name.length() - 6);
				name = name.replace('/', '.');
				name = name.replace('\\', '.');
				try {
					classes.add(getClass(name));
				} catch(Exception e) {}
			}
		}
		return classes;
	}

	private static HashSet<Class<?>> getClasses(String classpathDir, String subDir) {
		File file = new File(classpathDir + subDir);
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		if(!file.isDirectory())
			return classes;
		File[] files = file.listFiles();
		int i;
		for(i=0; i<files.length; i++) {
			if(files[i].isDirectory()) {
				classes.addAll(getClasses(classpathDir, subDir + "/" + files[i].getName()));
			}
			else {
				String name = subDir + "/" + files[i].getName();
				if(name.endsWith(".class")) {
					while(name.startsWith("/") || name.startsWith("\\"))
						name = name.substring(1);
					name = name.substring(0, name.length() - 6);
					name = name.replace('/', '.');
					name = name.replace('\\', '.');
					try {
						classes.add(getClass(name));
					} catch(Exception e) {}
				}
			}
		}
		return classes;
	}

	/**
	 * Returns all classes found in the given classpath.  Note that
	 * elements of the classpath are assumed to be delimed by colons
	 * (":").
	 */
	public static HashSet<Class<?>> getClasses(String classpath) {
		String[] elements;
		if(!classpath.contains(":") && classpath.contains(";"))
			elements = classpath.split(";"); /* Windows */
		else
			elements = classpath.split(":"); /*  POSIX  */
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for(int i=0; i<elements.length; i++) {
			File file = new File(elements[i]);
			if(elements[i].endsWith(".jar")) {
				try {
					classes.addAll(getClasses(new JarFile(file)));
				} catch(Exception e) {}
			}
			else if(file.isDirectory()) {
				classes.addAll(getClasses(elements[i], ""));
			}
			else {
				if(file.isFile() && elements[i].endsWith(".class")) {
					elements[i] = elements[i].substring(0, elements[i].length() - 6);
					elements[i] = elements[i].replace('/', '.');
					elements[i] = elements[i].replace('\\', '.');
					try {
						classes.add(getClass(elements[i]));
					} catch(Exception e) {}
				}
			}
		}
		return classes;
	}

	/**
	 * Returns all classes found in the current classpath.  This is
	 * equivalent to
	 * <code>getClasses(System.getProperty("java.class.path"))</code>.
	 */
	public static HashSet<Class<?>> getClasses() {
		return getClasses(System.getProperty("java.class.path"));
	}

	/**
	 * Returns all classes found in the given classpath that are
	 * assignable from the class <code>type</code>.
	 */
	public static <U> HashSet<Class<? extends U>> getClassesOfType(Class<U> type, String classpath) {
		HashSet<Class<? extends U>> classesOfType = new HashSet<Class<? extends U>>();
		for(Iterator<Class<?>> iter = getClasses(classpath).iterator(); iter.hasNext();) {
			Class<?> c = iter.next();
			if(type.isAssignableFrom(c))
				classesOfType.add(c.asSubclass(type));	    
		}
		return classesOfType;
	}

	/**
	 * Returns all classes found in the current classpath that are
	 * assignable from the class <code>type</code>.
	 */
	public static <U> HashSet<Class<? extends U>> getClassesOfType(Class<U> type) {
		return getClassesOfType(type, System.getProperty("java.class.path"));
	}

	/**
	 * Returns the deepest subclass of type <code>type</code>.
	 */
	public static <U> Class<? extends U> getDeepestSubclass(Class<U> type) {
		HashSet<Class<? extends U>> subclasses = getClassesOfType(type);
		Hashtable<Class<? extends U>,Integer> depths = new Hashtable<Class<? extends U>,Integer>();

		int maxDepth = -1;
		Class<? extends U> max = null;

		for(Iterator<Class<? extends U>> iter = subclasses.iterator(); iter.hasNext();) {
			int depth = 0;
			Integer knownDepth = null;
			Class<? extends U> c = iter.next();
			if(c.equals(type))
				continue;
			Hashtable<Class<? extends U>,Integer> chain = new Hashtable<Class<? extends U>,Integer>();
			while((knownDepth = depths.get(c)) == null) {
				chain.put(c,new Integer(++depth));
				c = c.getSuperclass().asSubclass(type);
				if(c.equals(type)) {
					knownDepth = new Integer(0);
					break;
				}
			}
			depth++;
			for(Enumeration<Class<? extends U>> e = chain.keys(); e.hasMoreElements();) {
				c = e.nextElement();
				int actualDepth = knownDepth.intValue() + depth - chain.get(c).intValue();
				if(actualDepth > maxDepth) {
					maxDepth = actualDepth;
					max = c;
				}
				depths.put(c, new Integer(actualDepth));
			}
		}

		if(maxDepth <= 0 || max == null)
			return type;
		else
			return max;
	}

	/**
	 * Returns the <code>main(String[])</code> method of the given
	 * class or <code>null</code> if the class does not implement a
	 * main method.
	 */
	public static Method getMainMethod(Class<?> c) {
		try {
			return c.getMethod("main", String[].class);
		} catch(Exception e) { }
		return null;
	}
}
